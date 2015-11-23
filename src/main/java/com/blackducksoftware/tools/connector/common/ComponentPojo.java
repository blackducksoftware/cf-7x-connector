package com.blackducksoftware.tools.connector.common;

public abstract class ComponentPojo {
    private String name;
    private String version;
    private ApprovalStatus approvalStatus;

    private String homepage;
    private boolean deprecated;

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

    public void setName(String name) {
	this.name = name;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
	this.approvalStatus = approvalStatus;
    }

    public void setHomepage(String homepage) {
	this.homepage = homepage;
    }

    public void setDeprecated(boolean deprecated) {
	this.deprecated = deprecated;
    }

}
