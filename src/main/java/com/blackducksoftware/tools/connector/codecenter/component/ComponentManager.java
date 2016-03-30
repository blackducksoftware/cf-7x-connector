/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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

    private final Map<String, Component> componentsByIdCache = new HashMap<>();

    public ComponentManager(CodeCenterAPIWrapper codeCenterApiWrapper,
            IAttributeDefinitionManager attrDefMgr,
            ILicenseManager<LicensePojo> licenseManager) {
        this.codeCenterApiWrapper = codeCenterApiWrapper;
        this.attrDefMgr = attrDefMgr;
        this.licenseManager = licenseManager;
    }

    @Override
    public <T extends CodeCenterComponentPojo> T getComponentById(Class<T> pojoClass, String componentId) throws CommonFrameworkException {
        return getComponentById(pojoClass, componentId, null);
    }

    @Override
    public <T extends CodeCenterComponentPojo> T getComponentById(
            Class<T> pojoClass, String componentId, String licenseRequestId)
            throws CommonFrameworkException {

        Component sdkComp = getSdkComponentByIdCached(componentId);

        return createPojo(pojoClass, sdkComp, licenseRequestId);
    }

    /**
     * Get a component by its name/version.
     *
     * Components fetched are cached.
     */
    @Override
    public <T extends CodeCenterComponentPojo> T getComponentByNameVersion(
            Class<T> pojoClass, String componentName, String componentVersion)
            throws CommonFrameworkException {
        NameVersion nameVersion = new NameVersion(componentName,
                componentVersion);

        // Check cache first
        if (componentsByNameVersionCache.containsKey(nameVersion)) {
            return createPojo(pojoClass,
                    componentsByNameVersionCache.get(nameVersion), null);
        }

        ComponentNameVersionToken componentNameVersionToken = new ComponentNameVersionToken();
        componentNameVersionToken.setName(componentName);
        componentNameVersionToken.setVersion(componentVersion);
        ColaApi colaApi = codeCenterApiWrapper.getColaApi();
        Component sdkComp;
        try {
            sdkComp = colaApi.getCatalogComponent(componentNameVersionToken);
        } catch (SdkFault e) {
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
            Class<T> pojoClass, int firstRowIndex, int lastRowIndex)
            throws CommonFrameworkException {

        ComponentPageFilter filter = new ComponentPageFilter();
        filter.setFirstRowIndex(firstRowIndex);
        filter.setLastRowIndex(lastRowIndex);
        List<Component> ccComps;
        try {
            ccComps = codeCenterApiWrapper.getColaApi()
                    .searchCatalogComponents("", filter);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting components "
                    + firstRowIndex + " to " + lastRowIndex + ": "
                    + e.getMessage());
        }
        List<T> comps = new ArrayList<>(ccComps.size());
        for (Component ccComp : ccComps) {
            T comp = createPojo(pojoClass, ccComp, null);
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
            Class<T> pojoClass, List<RequestPojo> requests)
            throws CommonFrameworkException {

        List<T> components = new ArrayList<>(requests.size());
        for (RequestPojo request : requests) {
            T comp = getComponentById(pojoClass, request.getComponentId(), request.getLicenseId());
            components.add(comp);
        }

        return components;
    }

    @Override
    public <T extends CodeCenterComponentPojo> List<T> getComponentsForRequests(
            Class<T> pojoClass, List<RequestPojo> requests,
            List<ApprovalStatus> limitToApprovalStatusValues)
            throws CommonFrameworkException {

        List<T> components = new ArrayList<>(requests.size());
        for (RequestPojo request : requests) {
            T comp = getComponentById(pojoClass, request.getComponentId(), request.getLicenseId());
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
    public List<AttachmentDetails> searchAttachments(String componentId,
            String searchString) throws CommonFrameworkException {

        Component comp = getSdkComponentByIdCached(componentId);

        AttachmentPageFilter pageFilter = new AttachmentPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        List<ComponentAttachment> sdkAttachments;
        try {
            sdkAttachments = codeCenterApiWrapper.getColaApi()
                    .searchComponentAttachments("", pageFilter, comp.getId());
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error searching attachments for component ID "
                            + componentId + ": " + e.getMessage());
        }

        List<AttachmentDetails> attachmentDetailsList = new ArrayList<>(
                sdkAttachments.size());
        for (ComponentAttachment sdkAttachment : sdkAttachments) {
            AttachmentDetails attachmentDetails = createAttachmentDetails(sdkAttachment);
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
    public File downloadAttachment(String componentId, String filename,
            String targetDirPath) throws CommonFrameworkException {
        ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
        ComponentIdToken compToken = new ComponentIdToken();
        compToken.setId(componentId);
        attachmentToken.setComponentId(compToken);
        attachmentToken.setFileName(filename);
        AttachmentContent content;
        try {
            content = codeCenterApiWrapper.getColaApi()
                    .getComponentAttachmentContent(attachmentToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting data handler for component ID "
                            + componentId + " attachment " + filename + ": "
                            + e.getMessage());
        }
        DataHandler dataHandler = content.getAttachmentContent();
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
    public void attachFile(String componentId, String sourceFilePath,
            String description) throws CommonFrameworkException {

        File file = new File(sourceFilePath);

        Component comp = getSdkComponentByIdCached(componentId);
        ComponentAttachmentCreate attachmentCreateBean = new ComponentAttachmentCreate();
        attachmentCreateBean.setComponentId(comp.getId());
        attachmentCreateBean.setFileName(file.getName());
        attachmentCreateBean.setDescription(description);
        attachmentCreateBean.setName(file.getName());

        URL url = null;
        try {
            url = new File(sourceFilePath).toURI().toURL();
        } catch (MalformedURLException mue) {
            throw new CommonFrameworkException(mue.getMessage());
        }

        DataHandler dataHandler = new DataHandler(url);
        attachmentCreateBean.setAttachmentContent(dataHandler);
        try {

            codeCenterApiWrapper.getColaApi().createComponentAttachment(
                    attachmentCreateBean);
        } catch (SdkFault e) {
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
    public void deleteAttachment(String componentId, String filename)
            throws CommonFrameworkException {
        ComponentIdToken componentIdToken = new ComponentIdToken();
        componentIdToken.setId(componentId);

        ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
        attachmentToken.setComponentId(componentIdToken);
        attachmentToken.setFileName(filename);
        try {
            codeCenterApiWrapper.getColaApi().deleteComponentAttachment(
                    attachmentToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error deleting file "
                    + filename + " from component ID " + componentId + ": "
                    + e.getMessage());
        }

    }

    // Private methods

    private Component getSdkComponentByIdCached(String componentId)
            throws CommonFrameworkException {
        // Check cache first
        if (componentsByIdCache.containsKey(componentId)) {
            return componentsByIdCache.get(componentId);
        }

        Component sdkComp = getSdkComponentById(componentId);

        // Add to caches
        NameVersion nameVersion = new NameVersion(sdkComp.getName(),
                sdkComp.getVersion());
        addToCache(nameVersion, sdkComp);

        return sdkComp;
    }

    private Component getSdkComponentById(String componentId)
            throws CommonFrameworkException {
        ComponentIdToken componentIdToken = new ComponentIdToken();
        componentIdToken.setId(componentId);
        ColaApi colaApi = codeCenterApiWrapper.getColaApi();
        Component sdkComp;
        try {
            sdkComp = colaApi.getCatalogComponent(componentIdToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting component for ID " + componentId + ": "
                            + e.getMessage());
        }
        return sdkComp;
    }

    private <T extends CodeCenterComponentPojo> T createPojo(
            Class<T> pojoClass, Component sdkComp, String licenseRequestId)
            throws CommonFrameworkException {
        List<AttributeValue> sdkAttrValues = sdkComp.getAttributeValues();
        List<AttributeValuePojo> attrValues = AttributeValues.valueOf(
                attrDefMgr, sdkAttrValues);

        String appId = getAppId(sdkComp);
        String kbComponentId = getKbComponentId(sdkComp);
        String kbComponentReleaseId = getKbComponentReleaseId(sdkComp);

        // Declared licenses
        List<LicensePojo> licenses = Licenses.valueOf(licenseManager,
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
        boolean applicationComponent = sdkComp.getApplicationId() != null;
        T comp;
        try {
            comp = instantiatePojo(pojoClass);
        } catch (Exception e) {
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

    private String getKbComponentReleaseId(Component sdkComp) {
        KbComponentReleaseIdToken kbCompIdToken = sdkComp.getKbReleaseId();
        String kbComponentReleaseId;
        if (kbCompIdToken == null) {
            kbComponentReleaseId = null;
        } else {
            kbComponentReleaseId = kbCompIdToken.getId();
        }
        return kbComponentReleaseId;
    }

    private String getKbComponentId(Component sdkComp) {
        KbComponentIdToken kbCompIdToken = sdkComp.getKbComponentId();
        String kbComponentId;
        if (kbCompIdToken == null) {
            kbComponentId = null;
        } else {
            kbComponentId = kbCompIdToken.getId();
        }
        return kbComponentId;
    }

    private String getAppId(Component sdkComp) {
        ApplicationIdToken appIdToken = sdkComp.getApplicationId();
        String appId;
        if (appIdToken == null) {
            appId = null;
        } else {
            appId = appIdToken.getId();
        }
        return appId;
    }

    private AttachmentDetails createAttachmentDetails(
            ComponentAttachment sdkAttachment) {
        AttachmentDetails attachmentDetails = new AttachmentDetails(
                sdkAttachment.getId(), sdkAttachment.getFileName(),
                sdkAttachment.getDescription(),
                sdkAttachment.getTimeUploaded(), sdkAttachment
                        .getUserUploaded().getId(),
                sdkAttachment.getContentType(),
                sdkAttachment.getFilesizeBytes());
        return attachmentDetails;
    }

    private void addToCache(NameVersion nameVersion, Component sdkComp) {
        componentsByIdCache.put(sdkComp.getId().getId(), sdkComp);
        componentsByNameVersionCache.put(nameVersion, sdkComp);
    }

    private <T extends CodeCenterComponentPojo> void removeFromCache(T comp) {
        componentsByIdCache.remove(comp.getId());
        NameVersion nameVersion = new NameVersion(comp.getName(), comp.getVersion());
        componentsByNameVersionCache.remove(nameVersion);
    }

    @Override
    public <T extends CodeCenterComponentPojo> T instantiatePojo(
            Class<T> pojoClass) throws CommonFrameworkException {
        T componentPojo = null;
        Constructor<?> constructor = null;
        ;
        try {
            constructor = pojoClass.getConstructor();
        } catch (SecurityException e) {
            throw new CommonFrameworkException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new CommonFrameworkException(e.getMessage());
        }

        try {
            componentPojo = (T) constructor.newInstance();
        } catch (IllegalArgumentException e) {
            throw new CommonFrameworkException(e.getMessage());
        } catch (InstantiationException e) {
            throw new CommonFrameworkException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new CommonFrameworkException(e.getMessage());
        } catch (InvocationTargetException e) {
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
    public <T extends CodeCenterComponentPojo> void updateAttributeValues(Class<T> pojoClass, String compId, Set<AttributeValuePojo> changedAttrValues)
            throws CommonFrameworkException {
        log.info("updateAttributeValues() called with component ID: " + compId);
        T comp = getComponentById(pojoClass, compId, null);

        ComponentUpdate componentUpdate = new ComponentUpdate();

        ComponentIdToken componentIdToken = new ComponentIdToken();
        componentIdToken.setId(comp.getId());
        componentUpdate.setId(componentIdToken);

        for (AttributeValuePojo attrValue : changedAttrValues) {

            String attrName = attrValue.getName();

            AttributeValue attrValueObject = new AttributeValue();
            AttributeIdToken attrIdToken = new AttributeIdToken();
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
        } catch (SdkFault e) {
            log.error("SDK: Error updating custom attribute values on component");
            throw new CommonFrameworkException("Error updating attribute values on component " + comp.getName() + ": "
                    + e.getMessage());
        }
        removeFromCache(comp); // remove stale cache entry
    }
}
