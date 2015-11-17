package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.AttributeValues;
import com.blackducksoftware.tools.connector.common.LicensePojo;

/**
 * A component.
 *
 * @author sbillings
 *
 */
public class ComponentPojo {
    private final String id;
    private final String name;
    private final String version;
    private final ApprovalStatus approvalStatus;
    private final List<LicensePojo> licenses = new ArrayList<>();
    private final String homepage;
    private final String intendedAudiences;
    private final String kbComponentId;
    private final String kbReleaseId;
    private final boolean applicationComponent;
    private final String applicationId;
    private final boolean deprecated;
    private final Map<String, String> attributeValuesByName = new HashMap<>();

    public ComponentPojo(String id, String name, String version,
	    ApprovalStatus approvalStatus, String homepage,
	    String intendedAudiences, String kbComponentId, String kbReleaseId,
	    boolean applicationComponent, String applicationId,
	    boolean deprecated, List<AttributeValuePojo> attributeValues) {
	this.id = id;
	this.name = name;
	this.version = version;
	this.approvalStatus = approvalStatus;
	this.homepage = homepage;
	this.intendedAudiences = intendedAudiences;
	this.kbComponentId = kbComponentId;
	this.kbReleaseId = kbReleaseId;
	this.applicationComponent = applicationComponent;
	this.applicationId = applicationId;
	this.deprecated = deprecated;
	AttributeValues.addAttributeValuesToMap(attributeValuesByName,
		attributeValues);
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getVersion() {
	return version;
    }

    public ApprovalStatus getApprovalStatus() {
	return approvalStatus;
    }

    public List<LicensePojo> getLicenses() {
	return licenses;
    }

    public String getHomepage() {
	return homepage;
    }

    public String getIntendedAudiences() {
	return intendedAudiences;
    }

    public String getKbComponentId() {
	return kbComponentId;
    }

    public String getKbReleaseId() {
	return kbReleaseId;
    }

    public boolean isApplicationComponent() {
	return applicationComponent;
    }

    public String getApplicationId() {
	return applicationId;
    }

    public boolean isDeprecated() {
	return deprecated;
    }

    public String getAttributeByName(String name) {
	String value = attributeValuesByName.get(name);
	return value;
    }

    @Override
    public String toString() {
	return "ComponentPojo [name=" + name + ", version=" + version
		+ ", applicationComponent=" + applicationComponent
		+ ", applicationId=" + applicationId + "]";
    }
}
