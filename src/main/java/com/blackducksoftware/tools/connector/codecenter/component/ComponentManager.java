package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.application.RequestPojo;

public class ComponentManager implements IComponentManager {

    // TODO cache, check cache, add to cache
    private final CodeCenterAPIWrapper codeCenterApiWrapper;
    private final Map<NameVersion, Component> componentsByNameValueCache = new HashMap<>();
    private final Map<String, Component> componentsById = new HashMap<>();

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
	// TODO Auto-generated function stub
	return null;
    }

    @Override
    public List<ComponentPojo> getComponentsForRequests(
	    List<RequestPojo> requests) throws CommonFrameworkException {
	// TODO Auto-generated function stub
	return null;
    }

    private ComponentPojo createPojo(Component sdkComp)
	    throws CommonFrameworkException {
	List<AttributeValuePojo> attrValues = new ArrayList<>(); // TODO

	ComponentPojo comp = new ComponentPojo(sdkComp.getId().getId(),
		sdkComp.getName(), sdkComp.getVersion(),
		ApprovalStatus.toPojo(sdkComp.getApprovalStatus()),
		sdkComp.getHomepage(), sdkComp.getIntendedAudiences(), sdkComp
			.getKbComponentId().getId(), sdkComp.getKbReleaseId()
			.getId(), sdkComp.isApplicationComponent(), sdkComp
			.getApplicationId().getId(), sdkComp.isDeprecated(),
		attrValues);
	return comp;
    }
}
