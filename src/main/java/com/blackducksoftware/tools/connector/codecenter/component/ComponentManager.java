/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.connector.codecenter.component;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachment;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachmentCreate;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachmentToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentPageFilter;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentSearchTypeEnum;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentUpdate;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentReleaseIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentContent;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentPageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentUtils;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;
import com.blackducksoftware.tools.connector.common.Licenses;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Code Center Component Manager.
 *
 * Limited to single-value attribute types.
 *
 * @author sbillings
 *
 */
public class ComponentManager implements ICodeCenterComponentManager {
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	private final CodeCenterAPIWrapper codeCenterApiWrapper;

	private final IAttributeDefinitionManager attrDefMgr;

	private final ILicenseManager<LicensePojo> licenseManager;

	private final Map<NameVersion, Component> componentsByNameVersionCache = new HashMap<>();

	private final LoadingCache<String, Component> componentsByIdCache;

	public ComponentManager(final CodeCenterAPIWrapper codeCenterApiWrapper,
			final IAttributeDefinitionManager attrDefMgr,
			final ILicenseManager<LicensePojo> licenseManager) {
		this.codeCenterApiWrapper = codeCenterApiWrapper;
		this.attrDefMgr = attrDefMgr;
		this.licenseManager = licenseManager;
		final int componentCacheSize = 50000; // TODO cache params:
		// configurable?
		this.componentsByIdCache = CacheBuilder.newBuilder().maximumSize(componentCacheSize)
				.expireAfterWrite(60, TimeUnit.MINUTES).build(new CacheLoader<String, Component>() {
					@Override
					public Component load(final String componentId) throws CommonFrameworkException {
						return getSdkComponentById(componentId);
					}
				});
	}

	@Override
	public <T extends CodeCenterComponentPojo> T getComponentById(final Class<T> pojoClass, final String componentId) throws CommonFrameworkException {
		return getComponentById(pojoClass, componentId, null);
	}

	@Override
	public <T extends CodeCenterComponentPojo> T getComponentById(
			final Class<T> pojoClass, final String componentId, final String licenseRequestId)
					throws CommonFrameworkException {

		final Component sdkComp = getSdkComponentByIdCached(componentId);

		return createPojo(pojoClass, sdkComp, licenseRequestId);
	}

	/**
	 * Get a component by its name/version.
	 *
	 * Components fetched are cached.
	 */
	@Override
	public <T extends CodeCenterComponentPojo> T getComponentByNameVersion(
			final Class<T> pojoClass, final String componentName, final String componentVersion)
					throws CommonFrameworkException {
		final NameVersion nameVersion = new NameVersion(componentName,
				componentVersion);

		// Check cache first
		if (componentsByNameVersionCache.containsKey(nameVersion)) {
			return createPojo(pojoClass,
					componentsByNameVersionCache.get(nameVersion), null);
		}

		final ComponentNameVersionToken componentNameVersionToken = new ComponentNameVersionToken();
		componentNameVersionToken.setName(componentName);
		componentNameVersionToken.setVersion(componentVersion);
		final ColaApi colaApi = codeCenterApiWrapper.getColaApi();
		Component sdkComp;
		try {
			sdkComp = colaApi.getCatalogComponent(componentNameVersionToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error getting component "
					+ componentName + " / " + componentVersion + ": "
					+ e.getMessage());
		}

		// Add to caches
		addToCache(nameVersion, sdkComp);

		return createPojo(pojoClass, sdkComp, null);
	}

	/**
	 * Get a subset of the entire catalog.
	 *
	 * The cache is not used, nor updated (due to concerns about how large it
	 * could get).
	 *
	 */
	@Override
	public <T extends CodeCenterComponentPojo> List<T> getComponents(
			final Class<T> pojoClass, final int firstRowIndex, final int lastRowIndex)
					throws CommonFrameworkException {

		final ComponentPageFilter filter = new ComponentPageFilter();
		filter.setFirstRowIndex(firstRowIndex);
		filter.setLastRowIndex(lastRowIndex);
		List<Component> ccComps;
		try {
			ccComps = codeCenterApiWrapper.getColaApi()
					.searchCatalogComponents("", filter);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error getting components "
					+ firstRowIndex + " to " + lastRowIndex + ": "
					+ e.getMessage());
		}
		final List<T> comps = new ArrayList<>(ccComps.size());
		for (final Component ccComp : ccComps) {
			final T comp = createPojo(pojoClass, ccComp, null);
			comps.add(comp);
		}
		return comps;
	}

	/**
	 * Get a list of components that correspond to the given list of requests.
	 *
	 */
	@Override
	public <T extends CodeCenterComponentPojo> List<T> getComponentsForRequests(
			final Class<T> pojoClass, final List<RequestPojo> requests)
					throws CommonFrameworkException {

		final List<T> components = new ArrayList<>(requests.size());
		for (final RequestPojo request : requests) {
			final T comp = getComponentById(pojoClass, request.getComponentId(), request.getLicenseId());
			components.add(comp);
		}

		return components;
	}

	@Override
	public <T extends CodeCenterComponentPojo> List<T> getComponentsForRequests(
			final Class<T> pojoClass, final List<RequestPojo> requests,
			final List<ApprovalStatus> limitToApprovalStatusValues)
					throws CommonFrameworkException {

		final List<T> components = new ArrayList<>(requests.size());
		for (final RequestPojo request : requests) {
			final T comp = getComponentById(pojoClass, request.getComponentId(), request.getLicenseId());
			if ((limitToApprovalStatusValues == null)
					|| (limitToApprovalStatusValues.size() == 0)
					|| (limitToApprovalStatusValues.contains(comp
							.getApprovalStatus()))) {
				components.add(comp);
			}
		}

		return components;
	}

	/**
	 * Get attachment details for a given set of attachments from the given
	 * Component.
	 *
	 * @param componentId
	 * @param searchString
	 * @return
	 * @throws CommonFrameworkException
	 */
	@Override
	public List<AttachmentDetails> searchAttachments(final String componentId,
			final String searchString) throws CommonFrameworkException {

		final Component comp = getSdkComponentByIdCached(componentId);

		final AttachmentPageFilter pageFilter = new AttachmentPageFilter();
		pageFilter.setFirstRowIndex(0);
		pageFilter.setLastRowIndex(Integer.MAX_VALUE);
		List<ComponentAttachment> sdkAttachments;
		try {
			sdkAttachments = codeCenterApiWrapper.getColaApi()
					.searchComponentAttachments("", pageFilter, comp.getId());
		} catch (final SdkFault e) {
			throw new CommonFrameworkException(
					"Error searching attachments for component ID "
							+ componentId + ": " + e.getMessage());
		}

		final List<AttachmentDetails> attachmentDetailsList = new ArrayList<>(
				sdkAttachments.size());
		for (final ComponentAttachment sdkAttachment : sdkAttachments) {
			final AttachmentDetails attachmentDetails = createAttachmentDetails(sdkAttachment);
			attachmentDetailsList.add(attachmentDetails);
		}

		return attachmentDetailsList;
	}

	/**
	 * Download a Component attachment to the given directory.
	 *
	 * @param attachmentId
	 * @param targetDirPath
	 * @return
	 * @throws CommonFrameworkException
	 */
	@Override
	public File downloadAttachment(final String componentId, final String filename,
			final String targetDirPath) throws CommonFrameworkException {
		final ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
		final ComponentIdToken compToken = new ComponentIdToken();
		compToken.setId(componentId);
		attachmentToken.setComponentId(compToken);
		attachmentToken.setFileName(filename);
		AttachmentContent content;
		try {
			content = codeCenterApiWrapper.getColaApi()
					.getComponentAttachmentContent(attachmentToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException(
					"Error getting data handler for component ID "
							+ componentId + " attachment " + filename + ": "
							+ e.getMessage());
		}
		final DataHandler dataHandler = content.getAttachmentContent();
		return AttachmentUtils.downloadAttachment("component", componentId,
				filename, targetDirPath, dataHandler);
	}

	/**
	 * Attach the named file to the given Component.
	 *
	 * @param componentId
	 * @param sourceFilePath
	 * @param description
	 * @throws CommonFrameworkException
	 */
	@Override
	public void attachFile(final String componentId, final String sourceFilePath,
			final String description) throws CommonFrameworkException {

		final File file = new File(sourceFilePath);

		final Component comp = getSdkComponentByIdCached(componentId);
		final ComponentAttachmentCreate attachmentCreateBean = new ComponentAttachmentCreate();
		attachmentCreateBean.setComponentId(comp.getId());
		attachmentCreateBean.setFileName(file.getName());
		attachmentCreateBean.setDescription(description);
		attachmentCreateBean.setName(file.getName());

		URL url = null;
		try {
			url = new File(sourceFilePath).toURI().toURL();
		} catch (final MalformedURLException mue) {
			throw new CommonFrameworkException(mue.getMessage());
		}

		final DataHandler dataHandler = new DataHandler(url);
		attachmentCreateBean.setAttachmentContent(dataHandler);
		try {

			codeCenterApiWrapper.getColaApi().createComponentAttachment(
					attachmentCreateBean);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException(
					"Error creating component attachment on component ID "
							+ componentId + " from file " + url.toString()
							+ ": " + e.getMessage());
		}

	}

	/**
	 * Delete the given attachment from the given component.
	 *
	 * @param attachmentId
	 */
	@Override
	public void deleteAttachment(final String componentId, final String filename)
			throws CommonFrameworkException {
		final ComponentIdToken componentIdToken = new ComponentIdToken();
		componentIdToken.setId(componentId);

		final ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
		attachmentToken.setComponentId(componentIdToken);
		attachmentToken.setFileName(filename);
		try {
			codeCenterApiWrapper.getColaApi().deleteComponentAttachment(
					attachmentToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error deleting file "
					+ filename + " from component ID " + componentId + ": "
					+ e.getMessage());
		}

	}

	// Private methods

	private Component getSdkComponentByIdCached(final String componentId)
			throws CommonFrameworkException {
		// Check cache first

		Component sdkComp;
		try {
			sdkComp = componentsByIdCache.get(componentId);
		} catch (final ExecutionException e) {
			throw new CommonFrameworkException("Error getting component with ID " + componentId
					+ " from componentsByIdCache: " + e.getMessage());
		}

		// Add to caches
		final NameVersion nameVersion = new NameVersion(sdkComp.getName(),
				sdkComp.getVersion());
		addToCache(nameVersion, sdkComp);

		return sdkComp;
	}

	private Component getSdkComponentById(final String componentId)
			throws CommonFrameworkException {

		final ComponentIdToken componentIdToken = new ComponentIdToken();
		componentIdToken.setId(componentId);
		final ColaApi colaApi = codeCenterApiWrapper.getColaApi();
		Component sdkComp;
		try {
			log.debug("SDK: Getting from Code Center component by ID: " + componentId);
			sdkComp = colaApi.getCatalogComponent(componentIdToken);
			log.debug("SDK: Done getting from Code Center component by ID: " + componentId);
		} catch (final SdkFault e) {
			log.debug("SDK: Error getting from Code Center component by ID: " + componentId);
			throw new CommonFrameworkException(
					"Error getting component for ID " + componentId + ": "
							+ e.getMessage());
		}
		return sdkComp;
	}

	private <T extends CodeCenterComponentPojo> T createPojo(
			final Class<T> pojoClass, final Component sdkComp, final String licenseRequestId)
					throws CommonFrameworkException {
		final List<AttributeValue> sdkAttrValues = sdkComp.getAttributeValues();
		final List<AttributeValuePojo> attrValues = AttributeValues.valueOf(
				attrDefMgr, sdkAttrValues);

		final String appId = getAppId(sdkComp);
		final String kbComponentId = getKbComponentId(sdkComp);
		final String kbComponentReleaseId = getKbComponentReleaseId(sdkComp);

		// Declared licenses
		final List<LicensePojo> licenses = Licenses.valueOf(licenseManager,
				sdkComp.getDeclaredLicenses());
		// Requested License (Where applicable)
		LicensePojo requestedLicense = null;
		if (licenseRequestId != null)
		{
			requestedLicense = Licenses.valueOf(licenseManager, licenseRequestId);
			log.info("Found requested license, name: " + requestedLicense.getName());

		}

		log.info("Component: " + sdkComp.getName() + " / "
				+ sdkComp.getVersion() + "; Approval: "
				+ sdkComp.getApprovalStatus());

		// sdkComp.isApplicationComponent() always returns false,
		// so set flag in pojo based on whether or not
		// applicationId has a value
		final boolean applicationComponent = sdkComp.getApplicationId() != null;
		T comp;
		try {
			comp = instantiatePojo(pojoClass);
		} catch (final Exception e) {
			throw new CommonFrameworkException(
					"Error instantiating component POJO: " + e.getMessage());
		}
		comp.setId(sdkComp.getId().getId());
		comp.setName(sdkComp.getName());
		comp.setDescription(sdkComp.getDescription());
		comp.setVersion(sdkComp.getVersion());
		comp.setApprovalStatus(ApprovalStatus.valueOf(sdkComp
				.getApprovalStatus()));
		comp.setHomepage(sdkComp.getHomepage());
		comp.setIntendedAudiences(sdkComp.getIntendedAudiences());
		comp.setKbComponentId(kbComponentId);
		comp.setKbReleaseId(kbComponentReleaseId);
		comp.setApplicationComponent(applicationComponent);
		comp.setApplicationId(appId);
		comp.setDeprecated(sdkComp.isDeprecated());
		comp.setAttributeValues(attrValues);
		// Declared Licenses
		comp.setLicenses(licenses);
		// Requested Licenses
		if (requestedLicense != null) {
			comp.setRequestedLicense(requestedLicense);
		}
		comp.setSubComponents(null);
		return comp;
	}

	private String getKbComponentReleaseId(final Component sdkComp) {
		final KbComponentReleaseIdToken kbCompIdToken = sdkComp.getKbReleaseId();
		String kbComponentReleaseId;
		if (kbCompIdToken == null) {
			kbComponentReleaseId = null;
		} else {
			kbComponentReleaseId = kbCompIdToken.getId();
		}
		return kbComponentReleaseId;
	}

	private String getKbComponentId(final Component sdkComp) {
		final KbComponentIdToken kbCompIdToken = sdkComp.getKbComponentId();
		String kbComponentId;
		if (kbCompIdToken == null) {
			kbComponentId = null;
		} else {
			kbComponentId = kbCompIdToken.getId();
		}
		return kbComponentId;
	}

	private String getAppId(final Component sdkComp) {
		final ApplicationIdToken appIdToken = sdkComp.getApplicationId();
		String appId;
		if (appIdToken == null) {
			appId = null;
		} else {
			appId = appIdToken.getId();
		}
		return appId;
	}

	private AttachmentDetails createAttachmentDetails(
			final ComponentAttachment sdkAttachment) {
		final AttachmentDetails attachmentDetails = new AttachmentDetails(
				sdkAttachment.getId(), sdkAttachment.getFileName(),
				sdkAttachment.getDescription(),
				sdkAttachment.getTimeUploaded(), sdkAttachment
				.getUserUploaded().getId(),
				sdkAttachment.getContentType(),
				sdkAttachment.getFilesizeBytes());
		return attachmentDetails;
	}

	private void addToCache(final NameVersion nameVersion, final Component sdkComp) {
		componentsByNameVersionCache.put(nameVersion, sdkComp);
	}

	private <T extends CodeCenterComponentPojo> void removeFromCache(final T comp) {
		componentsByIdCache.invalidate(comp.getId());
		final NameVersion nameVersion = new NameVersion(comp.getName(), comp.getVersion());
		componentsByNameVersionCache.remove(nameVersion);
	}

	@Override
	public <T extends CodeCenterComponentPojo> T instantiatePojo(
			final Class<T> pojoClass) throws CommonFrameworkException {
		T componentPojo = null;
		Constructor<?> constructor = null;
		;
		try {
			constructor = pojoClass.getConstructor();
		} catch (final SecurityException e) {
			throw new CommonFrameworkException(e.getMessage());
		} catch (final NoSuchMethodException e) {
			throw new CommonFrameworkException(e.getMessage());
		}

		try {
			componentPojo = (T) constructor.newInstance();
		} catch (final IllegalArgumentException e) {
			throw new CommonFrameworkException(e.getMessage());
		} catch (final InstantiationException e) {
			throw new CommonFrameworkException(e.getMessage());
		} catch (final IllegalAccessException e) {
			throw new CommonFrameworkException(e.getMessage());
		} catch (final InvocationTargetException e) {
			throw new CommonFrameworkException(e.getMessage());
		}

		return componentPojo;
	}

	/**
	 * Update some custom attribute values on the given component.
	 *
	 * Limited to single-value attribute types.
	 *
	 * @param pojoClass
	 * @param compId
	 * @param changedAttrValues
	 * @throws CommonFrameworkException
	 */
	@Override
	public <T extends CodeCenterComponentPojo> void updateAttributeValues(final Class<T> pojoClass, final String compId, final Set<AttributeValuePojo> changedAttrValues)
			throws CommonFrameworkException {
		log.info("updateAttributeValues() called with component ID: " + compId);
		final T comp = getComponentById(pojoClass, compId, null);

		final ComponentUpdate componentUpdate = new ComponentUpdate();

		final ComponentIdToken componentIdToken = new ComponentIdToken();
		componentIdToken.setId(comp.getId());
		componentUpdate.setId(componentIdToken);

		for (final AttributeValuePojo attrValue : changedAttrValues) {

			final String attrName = attrValue.getName();

			final AttributeValue attrValueObject = new AttributeValue();
			final AttributeIdToken attrIdToken = new AttributeIdToken();
			attrIdToken.setId(attrValue.getAttrId());
			attrValueObject.setAttributeId(attrIdToken);
			attrValueObject.getValues().add(attrValue.getValue());

			log.info("Setting attribute " + attrName + " to "
					+ attrValue.getValue());
			componentUpdate.getAttributeValues().add(attrValueObject);
		}

		try {
			log.debug("SDK: Updating custom attribute values on component");
			codeCenterApiWrapper.getColaApi().updateCatalogComponent(componentUpdate);
			log.debug("SDK: Done updating custom attribute values on component");
		} catch (final SdkFault e) {
			log.error("SDK: Error updating custom attribute values on component");
			throw new CommonFrameworkException("Error updating attribute values on component " + comp.getName() + ": "
					+ e.getMessage());
		}
		removeFromCache(comp); // remove stale cache entry
	}

	@Override
	public void populateComponentCacheFromCatalog(final int batchSize) throws CommonFrameworkException {
		log.info("Preloading components into component cache");

		final ComponentPageFilter pageFilter = new ComponentPageFilter();
		pageFilter.setComponentType(ComponentSearchTypeEnum.ALL);
		pageFilter.setIncludeDeprecated(false);

		int firstRow = 0;
		int lastRow = batchSize - 1;
		int totalLoaded = 0;
		while (true) {

			log.debug("Preloading components into component cache: index " + firstRow + "-" + lastRow);

			pageFilter.setFirstRowIndex(firstRow);
			pageFilter.setLastRowIndex(lastRow);
			List<Component> partialSdkCompList;
			try {
				log.debug("SDK: Getting catalog components");
				partialSdkCompList = codeCenterApiWrapper.getColaApi().searchCatalogComponents("", pageFilter);
				// TODO:
				// The above SDK method is deprecated, but the replacement
				// (searchAllIndexedCatalogComponents)
				// returns diff type; might have all the fields we need though.
				log.debug("SDK: Done getting catalog components");
			} catch (final SdkFault e) {
				log.debug("SDK: Error getting catalog components");
				throw new CommonFrameworkException("Error preloading components into component cache; firstRowIndex: "
						+ firstRow + "; lastRow: " + lastRow + ": " + e.getMessage());
			}
			log.debug("Actually loaded " + partialSdkCompList.size() + " components; adding them to cache");
			for (final Component sdkComp : partialSdkCompList) {
				// Add to caches
				componentsByIdCache.put(sdkComp.getId().getId(), sdkComp);
				final NameVersion nameVersion = new NameVersion(sdkComp.getName(), sdkComp.getVersion());
				addToCache(nameVersion, sdkComp);
			}
			log.debug("Done adding this batch of components to cache");
			if (partialSdkCompList.size() == 0) {
				break; // there are no more
			}
			totalLoaded += partialSdkCompList.size();

			if (partialSdkCompList.size() < batchSize) {
				break; // we've reached the end of the catalog
			}
			firstRow += batchSize;
			lastRow += batchSize;

		}
		log.info("Done preloading components into component cache; Total components preloaded: " + totalLoaded
				+ "; # components currently in cache: " + componentsByIdCache.size());
	}
}
