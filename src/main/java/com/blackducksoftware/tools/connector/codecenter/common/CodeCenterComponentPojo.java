package com.blackducksoftware.tools.connector.codecenter.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.common.ComponentPojo;
import com.blackducksoftware.tools.connector.common.LicensePojo;

/**
 * A component.
 *
 * @author sbillings
 *
 */
public class CodeCenterComponentPojo extends ComponentPojo {
    private final String id;
    private final String intendedAudiences;
    private final List<LicensePojo> licenses;
    private final String kbComponentId;
    private final String kbReleaseId;
    private final boolean applicationComponent;
    private final String applicationId;
    private final Map<String, AttributeValuePojo> attributeValuesByName;
    private final List<CodeCenterComponentPojo> subComponents;

    /**
     * This constructor accepts a list of attribute values.
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
    public CodeCenterComponentPojo(String id, String name, String version,
	    ApprovalStatus approvalStatus, String homepage,
	    String intendedAudiences, String kbComponentId, String kbReleaseId,
	    boolean applicationComponent, String applicationId,
	    boolean deprecated, List<AttributeValuePojo> attributeValues,
	    List<LicensePojo> licenses,
	    List<CodeCenterComponentPojo> subComponents) {
	super(name, version, approvalStatus, homepage, deprecated);
	this.id = id;
	this.intendedAudiences = intendedAudiences;
	this.kbComponentId = kbComponentId;
	this.kbReleaseId = kbReleaseId;
	this.applicationComponent = applicationComponent;
	this.applicationId = applicationId;
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
     * creating one CodeCenterComponentPojo from another.
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
    public CodeCenterComponentPojo(String id, String name, String version,
	    ApprovalStatus approvalStatus, String homepage,
	    String intendedAudiences, String kbComponentId, String kbReleaseId,
	    boolean applicationComponent, String applicationId,
	    boolean deprecated,
	    Map<String, AttributeValuePojo> attributeValues,
	    List<LicensePojo> licenses,
	    List<CodeCenterComponentPojo> subComponents) {
	super(name, version, approvalStatus, homepage, deprecated);
	this.id = id;
	this.intendedAudiences = intendedAudiences;
	this.kbComponentId = kbComponentId;
	this.kbReleaseId = kbReleaseId;
	this.applicationComponent = applicationComponent;
	this.applicationId = applicationId;
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

    public String getIntendedAudiences() {
	return intendedAudiences;
    }

    public List<LicensePojo> getLicenses() {
	return licenses;
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

    public List<CodeCenterComponentPojo> getSubComponents() {
	if (subComponents == null) {
	    return null;
	}
	return new ArrayList<CodeCenterComponentPojo>(subComponents);
    }

    @Override
    public String toString() {
	return "CodeCenterComponentPojo [name=" + getName() + ", version="
		+ getVersion() + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((getName() == null) ? 0 : getName().hashCode());
	result = prime * result
		+ ((getVersion() == null) ? 0 : getVersion().hashCode());
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
	CodeCenterComponentPojo other = (CodeCenterComponentPojo) obj;
	if (getName() == null) {
	    if (other.getName() != null) {
		return false;
	    }
	} else if (!getName().equals(other.getName())) {
	    return false;
	}
	if (getVersion() == null) {
	    if (other.getVersion() != null) {
		return false;
	    }
	} else if (!getVersion().equals(other.getVersion())) {
	    return false;
	}
	return true;
    }

}
