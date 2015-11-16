package com.blackducksoftware.tools.connector.protex.license;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * License details.
 *
 * @author sbillings
 *
 */
public class LicensePojo {

    private final String id;
    private final String name;
    private final String comment;
    private final String explanation;
    private final String suffix;
    private final ApprovalState approvalState;
    private final String licenseText;

    public LicensePojo(String id, String name, String comment,
	    String explanation, String suffix, ApprovalState approvalState,
	    String licenseText) {
	this.id = id;
	this.name = name;
	this.comment = comment;
	this.explanation = explanation;
	this.suffix = suffix;
	this.approvalState = approvalState;
	this.licenseText = licenseText;
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

    public String getLicenseText() {
	return licenseText;
    }

    @Override
    public String toString() {
	return "LicensePojo [name=" + name + ", approvalState=" + approvalState
		+ "]";
    }

    /**
     * Convert an SDK license approval value to an ApprovalState (the generic
     * equivalent).
     *
     * @param sdkApprovalState
     * @return
     * @throws CommonFrameworkException
     */
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

    /**
     * Approval state enum (generic, to keep client code free from SDK
     * references).
     *
     * @author sbillings
     *
     */
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
