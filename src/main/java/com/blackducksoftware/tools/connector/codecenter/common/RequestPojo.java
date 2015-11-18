package com.blackducksoftware.tools.connector.codecenter.common;


/**
 * A request (a component use by an application).
 *
 * @author sbillings
 *
 */
public class RequestPojo {
    private final String requestId;
    private final String applicationId;
    private final String componentId;
    private final ApprovalStatus requestApprovalStatus;
    private final String licenseId;

    // TODO: Add request attribute values

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
