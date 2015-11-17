package com.blackducksoftware.tools.connector.codecenter.application;

import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;

public class RequestPojo {
    private final String requestId;
    private final String applicationId;
    private final String componentId;
    private final ApprovalStatus requestApprovalStatus;
    private final String licenseId;

    public RequestPojo(String requestId, String applicationId,
	    String componentId, ApprovalStatus requestApprovalStatus,
	    String licenseId) {
	this.requestId = requestId;
	this.applicationId = applicationId;
	this.componentId = componentId;
	this.requestApprovalStatus = requestApprovalStatus;
	this.licenseId = licenseId;
    }

    public String getRequestId() {
	return requestId;
    }

    public String getApplicationId() {
	return applicationId;
    }

    public String getComponentId() {
	return componentId;
    }

    public ApprovalStatus getRequestApprovalStatus() {
	return requestApprovalStatus;
    }

    public String getLicenseId() {
	return licenseId;
    }

    @Override
    public String toString() {
	return "RequestPojo [applicationId=" + applicationId + ", componentId="
		+ componentId + "]";
    }

}
