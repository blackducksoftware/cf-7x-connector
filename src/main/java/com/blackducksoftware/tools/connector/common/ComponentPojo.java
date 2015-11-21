package com.blackducksoftware.tools.connector.common;

import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;

public abstract class ComponentPojo {
    private final String name;
    private final String version;
    private final ApprovalStatus approvalStatus;

    private final String homepage;
    private final boolean deprecated;

    public ComponentPojo(String name, String version,
	    ApprovalStatus approvalStatus, String homepage, boolean deprecated) {

	this.name = name;
	this.version = version;
	this.approvalStatus = approvalStatus;
	this.homepage = homepage;
	this.deprecated = deprecated;
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

    public String getHomepage() {
	return homepage;
    }

    public boolean isDeprecated() {
	return deprecated;
    }

}
