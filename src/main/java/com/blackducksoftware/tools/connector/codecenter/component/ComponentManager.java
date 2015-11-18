package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.AttributeValues;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.application.RequestPojo;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;
import com.blackducksoftware.tools.connector.common.Licenses;

public class ComponentManager implements IComponentManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private final CodeCenterAPIWrapper codeCenterApiWrapper;
    private final IAttributeDefinitionManager attrDefMgr;
    private final ILicenseManager licenseManager;
    private final Map<NameVersion, Component> componentsByNameVersionCache = new HashMap<>();
    private final Map<String, Component> componentsByIdCache = new HashMap<>();

    public ComponentManager(CodeCenterAPIWrapper codeCenterApiWrapper,
	    IAttributeDefinitionManager attrDefMgr,
	    ILicenseManager licenseManager) {
	this.codeCenterApiWrapper = codeCenterApiWrapper;
	this.attrDefMgr = attrDefMgr;
	this.licenseManager = licenseManager;
    }

    /**
     * Get a component by its ID.
     *
     * Components fetched are cached.
     */
    @Override
    public ComponentPojo getComponentById(String componentId)
	    throws CommonFrameworkException {

	// Check cache first
	if (componentsByIdCache.containsKey(componentId)) {
	    return createPojo(componentsByIdCache.get(componentId));
	}

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

	// Add to caches
	NameVersion nameVersion = new NameVersion(sdkComp.getName(),
		sdkComp.getVersion());
	addToCache(nameVersion, sdkComp);

	return createPojo(sdkComp);
    }

    /**
     * Get a component by its name/version.
     *
     * Components fetched are cached.
     */
    @Override
    public ComponentPojo getComponentByNameVersion(String componentName,
	    String componentVersion) throws CommonFrameworkException {
	NameVersion nameVersion = new NameVersion(componentName,
		componentVersion);

	// Check cache first
	if (componentsByNameVersionCache.containsKey(nameVersion)) {
	    return createPojo(componentsByNameVersionCache.get(nameVersion));
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

	return createPojo(sdkComp);
    }

    private void addToCache(NameVersion nameVersion, Component sdkComp) {
	componentsByIdCache.put(sdkComp.getId().getId(), sdkComp);
	componentsByNameVersionCache.put(nameVersion, sdkComp);
    }

    /**
     * Get a list of components that correspond to the given list of requests.
     *
     */
    @Override
    public List<ComponentPojo> getComponentsForRequests(
	    List<RequestPojo> requests) throws CommonFrameworkException {

	List<ComponentPojo> components = new ArrayList<>(requests.size());
	for (RequestPojo request : requests) {
	    ComponentPojo comp = getComponentById(request.getComponentId());
	    components.add(comp);
	}

	return components;
    }

    private ComponentPojo createPojo(Component sdkComp)
	    throws CommonFrameworkException {
	List<AttributeValue> sdkAttrValues = sdkComp.getAttributeValues();
	List<AttributeValuePojo> attrValues = AttributeValues.valueOf(
		attrDefMgr, sdkAttrValues);

	ApplicationIdToken appIdToken = sdkComp.getApplicationId();
	String appId;
	if (appIdToken == null) {
	    appId = null;
	} else {
	    appId = appIdToken.getId();
	}

	List<LicensePojo> licenses = Licenses.valueOf(licenseManager,
		sdkComp.getDeclaredLicenses());

	ComponentPojo comp = new ComponentPojo(sdkComp.getId().getId(),
		sdkComp.getName(), sdkComp.getVersion(),
		ApprovalStatus.valueOf(sdkComp.getApprovalStatus()),
		sdkComp.getHomepage(), sdkComp.getIntendedAudiences(), sdkComp
			.getKbComponentId().getId(), sdkComp.getKbReleaseId()
			.getId(), sdkComp.isApplicationComponent(), appId,
		sdkComp.isDeprecated(), attrValues, licenses);
	return comp;
    }

}
