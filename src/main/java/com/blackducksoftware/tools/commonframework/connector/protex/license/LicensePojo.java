package com.blackducksoftware.tools.commonframework.connector.protex.license;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public class LicensePojo {

    private final String id;
    private final String name;
    private final String comment;
    private final String explanation;
    private final String suffix;
    private final ApprovalState approvalState;

    public LicensePojo(String id, String name, String comment,
	    String explanation, String suffix, ApprovalState approvalState) {
	this.id = id;
	this.name = name;
	this.comment = comment;
	this.explanation = explanation;
	this.suffix = suffix;
	this.approvalState = approvalState;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getComment() {
	return comment;
    }

    public String getExplanation() {
	return explanation;
    }

    public String getSuffix() {
	return suffix;
    }

    public ApprovalState getApprovalState() {
	return approvalState;
    }

    @Override
    public String toString() {
	return "LicensePojo [name=" + name + ", approvalState=" + approvalState
		+ "]";
    }

    // TODO Move this to ApprovalState enum?
    static ApprovalState toApprovalState(LicenseApprovalState sdkApprovalState)
	    throws CommonFrameworkException {
	switch (sdkApprovalState) {
	case APPROVED:
	    return ApprovalState.APPROVED;
	case BLANKET_APPROVED:
	    return ApprovalState.BLANKET_APPROVED;
	case DIS_APPROVED:
	    return ApprovalState.DISAPPROVED;
	case NOT_REVIEWED:
	    return ApprovalState.NOT_REVIEWED;
	default:
	    throw new CommonFrameworkException("Unsupported approval state: "
		    + sdkApprovalState.toString());
	}
    }

    public enum ApprovalState {
	APPROVED(LicenseApprovalState.APPROVED), BLANKET_APPROVED(
		LicenseApprovalState.BLANKET_APPROVED), DISAPPROVED(
		LicenseApprovalState.DIS_APPROVED), NOT_REVIEWED(
		LicenseApprovalState.NOT_REVIEWED);

	private LicenseApprovalState ccState;

	private ApprovalState(LicenseApprovalState ccState) {
	    this.ccState = ccState;
	}

	public boolean isEquivalent(LicenseApprovalState otherCcType) {
	    return ccState.equals(otherCcType);
	}

	LicenseApprovalState getCcState() {
	    return ccState;
	}
    }
}
