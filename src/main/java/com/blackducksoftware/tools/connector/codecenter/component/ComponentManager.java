package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.application.RequestPojo;

public class ComponentManager implements IComponentManager {

    // TODO cache, check cache, add to cache
    private final CodeCenterAPIWrapper codeCenterApiWrapper;
    private final Map<NameVersion, Component> componentsByNameVersionCache = new HashMap<>();
    private final Map<String, Component> componentsByIdCache = new HashMap<>();

    public ComponentManager(CodeCenterAPIWrapper codeCenterApiWrapper) {
	this.codeCenterApiWrapper = codeCenterApiWrapper;
    }

    @Override
    public ComponentPojo getComponentById(String componentId)
	    throws CommonFrameworkException {
	// TODO Auto-generated function stub
	return null;
    }

    @Override
    public ComponentPojo getComponentByNameVersion(String componentName,
	    String componentVersion) throws CommonFrameworkException {
	NameVersion nameVersion = new NameVersion(componentName,
		componentVersion);

	// Check cache first
	if (componentsByNameVersionCache.containsKey(nameVersion)) {
	    return createPojo(componentsByNameVersionCache.get(nameVersion));
	}
	List<String> valuesGotten = new ArrayList<String>(); // TODO
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

	List<AttributeValue> attrValues = sdkComp.getAttributeValues();

	// Add to caches
	addToCache(nameVersion, sdkComp);

	return createPojo(sdkComp);
    }

    private void addToCache(NameVersion nameVersion, Component sdkComp) {
	componentsByIdCache.put(sdkComp.getId().getId(), sdkComp);
	componentsByNameVersionCache.put(nameVersion, sdkComp);
    }

    @Override
    public List<ComponentPojo> getComponentsForRequests(
	    List<RequestPojo> requests) throws CommonFrameworkException {
	// TODO check cache
	// TODO Auto-generated function stub
	// TODO put in cache
	return null;
    }

    private ComponentPojo createPojo(Component sdkComp)
	    throws CommonFrameworkException {
	List<AttributeValuePojo> attrValues = new ArrayList<>(); // TODO

	ApplicationIdToken appIdToken = sdkComp.getApplicationId();
	String appId;
	if (appIdToken == null) {
	    appId = null;
	} else {
	    appId = appIdToken.getId();
	}

	ComponentPojo comp = new ComponentPojo(sdkComp.getId().getId(),
		sdkComp.getName(), sdkComp.getVersion(),
		ApprovalStatus.toPojo(sdkComp.getApprovalStatus()),
		sdkComp.getHomepage(), sdkComp.getIntendedAudiences(), sdkComp
			.getKbComponentId().getId(), sdkComp.getKbReleaseId()
			.getId(), sdkComp.isApplicationComponent(), appId,
		sdkComp.isDeprecated(), attrValues);
	return comp;
    }
}
