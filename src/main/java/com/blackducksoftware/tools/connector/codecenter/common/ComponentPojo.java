package com.blackducksoftware.tools.connector.codecenter.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final List<LicensePojo> licenses;
    private final String homepage;
    private final String intendedAudiences;
    private final String kbComponentId;
    private final String kbReleaseId;
    private final boolean applicationComponent;
    private final String applicationId;
    private final boolean deprecated;
    private final Map<String, AttributeValuePojo> attributeValuesByName;
    private final List<ComponentPojo> subComponents;

    /**
     * This constructor acceps a list of attribute values.
     *
     * @param id
     * @param name
     * @param version
     * @param approvalStatus
     * @param homepage
     * @param intendedAudiences
     * @param kbComponentId
     * @param kbReleaseId
     * @param applicationComponent
     * @param applicationId
     * @param deprecated
     * @param attributeValues
     * @param licenses
     * @param subComponents
     */
    public ComponentPojo(String id, String name, String version,
	    ApprovalStatus approvalStatus, String homepage,
	    String intendedAudiences, String kbComponentId, String kbReleaseId,
	    boolean applicationComponent, String applicationId,
	    boolean deprecated, List<AttributeValuePojo> attributeValues,
	    List<LicensePojo> licenses, List<ComponentPojo> subComponents) {
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
	attributeValuesByName = new HashMap<>(attributeValues.size());
	AttributeValues.addAttributeValuesToMap(attributeValuesByName,
		attributeValues);
	this.licenses = licenses;
	if ((subComponents != null) && (subComponents.size() > 0)) {
	    this.subComponents = new ArrayList<>(subComponents.size());
	    this.subComponents.addAll(subComponents);
	} else {
	    this.subComponents = null;
	}
    }

    /**
     * This constructor accepts a map of attribute values, which is useful for
     * creating one ComponentPojo from another.
     *
     * @param id
     * @param name
     * @param version
     * @param approvalStatus
     * @param homepage
     * @param intendedAudiences
     * @param kbComponentId
     * @param kbReleaseId
     * @param applicationComponent
     * @param applicationId
     * @param deprecated
     * @param attributeValues
     * @param licenses
     * @param subComponents
     */
    public ComponentPojo(String id, String name, String version,
	    ApprovalStatus approvalStatus, String homepage,
	    String intendedAudiences, String kbComponentId, String kbReleaseId,
	    boolean applicationComponent, String applicationId,
	    boolean deprecated,
	    Map<String, AttributeValuePojo> attributeValues,
	    List<LicensePojo> licenses, List<ComponentPojo> subComponents) {
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
	attributeValuesByName = new HashMap<>(attributeValues);
	this.licenses = licenses;
	if ((subComponents != null) && (subComponents.size() > 0)) {
	    this.subComponents = new ArrayList<>(subComponents.size());
	    this.subComponents.addAll(subComponents);
	} else {
	    this.subComponents = null;
	}
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
	if (!attributeValuesByName.containsKey(name)) {
	    return null;
	}
	AttributeValuePojo valuePojo = attributeValuesByName.get(name);
	if (valuePojo == null) {
	    return null;
	}
	String value = valuePojo.getValue();
	return value;
    }

    public Map<String, AttributeValuePojo> getAttributeValuesByName() {
	return new HashMap<String, AttributeValuePojo>(attributeValuesByName);
    }

    public List<ComponentPojo> getSubComponents() {
	if (subComponents == null) {
	    return null;
	}
	return new ArrayList<ComponentPojo>(subComponents);
    }

    @Override
    public String toString() {
	return "ComponentPojo [name=" + name + ", version=" + version + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((version == null) ? 0 : version.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	ComponentPojo other = (ComponentPojo) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (version == null) {
	    if (other.version != null) {
		return false;
	    }
	} else if (!version.equals(other.version)) {
	    return false;
	}
	return true;
    }

}
