package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.application.RequestPojo;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionPojo;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;

public class ComponentManager implements IComponentManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    // TODO cache, check cache, add to cache
    private final CodeCenterAPIWrapper codeCenterApiWrapper;
    private final IAttributeDefinitionManager attrDefMgr;
    private final Map<NameVersion, Component> componentsByNameVersionCache = new HashMap<>();
    private final Map<String, Component> componentsByIdCache = new HashMap<>();

    public ComponentManager(CodeCenterAPIWrapper codeCenterApiWrapper,
	    IAttributeDefinitionManager attrDefMgr) {
	this.codeCenterApiWrapper = codeCenterApiWrapper;
	this.attrDefMgr = attrDefMgr;
    }

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
	List<AttributeValue> sdkAttrValues = sdkComp.getAttributeValues();
	List<AttributeValuePojo> attrValues = toPojos(sdkAttrValues);

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

    /**
     * Convert a list of attribute values (SDK objects) to POJOs.
     *
     * TODO: This method exists here and in ApplicationManager. Centralize.
     *
     * @param attrValues
     * @return
     * @throws CommonFrameworkException
     */
    private List<AttributeValuePojo> toPojos(List<AttributeValue> attrValues)
	    throws CommonFrameworkException {
	List<AttributeValuePojo> pojos = new ArrayList<>();
	for (AttributeValue attrValue : attrValues) {
	    String attrId = getAttributeId(attrValue);
	    AttributeDefinitionPojo attrDefPojo = attrDefMgr
		    .getAttributeDefinitionById(attrId);
	    String attrName = attrDefPojo.getName();

	    String value = null;
	    List<String> valueList = attrValue.getValues();
	    if (valueList.size() > 1) {
		log.warn("Attribute "
			+ attrName
			+ " has multiple values, which is not supported; using the first value");
	    }
	    if ((valueList != null) && (valueList.size() > 0)) {
		value = attrValue.getValues().get(0);
	    }
	    log.info("Processing attr id " + attrId + ", name " + attrName
		    + ", value " + value);

	    AttributeValuePojo pojo = new AttributeValuePojo(attrId, attrName,
		    value);
	    pojos.add(pojo);
	}
	return pojos;
    }

    // TODO: This method exists here and in ApplicationManager. Centralize.
    private String getAttributeId(AttributeValue attrValue) {
	AttributeIdToken attrIdToken = (AttributeIdToken) attrValue
		.getAttributeId();
	String attrId = attrIdToken.getId();
	return attrId;
    }
}
