package com.blackducksoftware.tools.connector.protex.common;

import java.util.List;

import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.common.ComponentPojo;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class ProtexComponentPojo extends ComponentPojo {

    private final ComponentNameVersionIds nameVersionIds;
    private final List<ProtexLicensePojo> licenses;
    private final ProtexComponentType type;
    private final String description;
    private final String primaryLicenseId;
    private final String primaryLicenseName;

    public ProtexComponentPojo(String name, String version,
	    ApprovalStatus approvalStatus, String homepage, boolean deprecated,
	    ComponentNameVersionIds nameVersionIds,
	    List<ProtexLicensePojo> licenses, ProtexComponentType type,
	    String description, String primaryLicenseId,
	    String primaryLicenseName) {
	super(name, version, approvalStatus, homepage, deprecated);
	this.nameVersionIds = nameVersionIds;
	this.licenses = licenses;
	this.type = type;
	this.description = description;
	this.primaryLicenseId = primaryLicenseId;
	this.primaryLicenseName = primaryLicenseName;
    }

    public ComponentNameVersionIds getNameVersionIds() {
	return nameVersionIds;
    }

    public List<ProtexLicensePojo> getLicenses() {
	return licenses;
    }

    public ProtexComponentType getType() {
	return type;
    }

    public String getDescription() {
	return description;
    }

    public String getPrimaryLicenseId() {
	return primaryLicenseId;
    }

    public String getPrimaryLicenseName() {
	return primaryLicenseName;
    }

    @Override
    public String toString() {
	return "ProtexComponentPojo [type=" + type + ", getName()=" + getName()
		+ ", getVersion()=" + getVersion() + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((nameVersionIds == null) ? 0 : nameVersionIds.hashCode());
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
	ProtexComponentPojo other = (ProtexComponentPojo) obj;
	if (nameVersionIds == null) {
	    if (other.nameVersionIds != null) {
		return false;
	    }
	} else if (!nameVersionIds.equals(other.nameVersionIds)) {
	    return false;
	}
	return true;
    }

}
