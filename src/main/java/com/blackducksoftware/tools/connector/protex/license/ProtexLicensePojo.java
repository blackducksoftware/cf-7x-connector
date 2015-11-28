package com.blackducksoftware.tools.connector.protex.license;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.common.LicensePojo;

/**
 * License details.
 *
 * @author sbillings
 *
 */
public class ProtexLicensePojo extends LicensePojo {

    private final String comment;
    private final String explanation;
    private final String suffix;
    private final ApprovalState approvalState;
    private final List<String> obligationIds;

    public ProtexLicensePojo(String id, String name, String comment,
	    String explanation, String suffix, ApprovalState approvalState,
	    String licenseText, List<String> obligationIds) {
	super(id, name, licenseText);

	this.comment = comment;
	this.explanation = explanation;
	this.suffix = suffix;
	this.approvalState = approvalState;
	if (obligationIds == null) {
	    this.obligationIds = new ArrayList<>(0);
	} else {
	    this.obligationIds = new ArrayList<>(obligationIds);
	}
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

    public List<String> getObligationIds() {
	List<String> returnedObligationIds = new ArrayList<>(obligationIds);
	return returnedObligationIds;
    }

    @Override
    public String toString() {
	return "ProtexLicensePojo [name=" + getName() + ", approvalState="
		+ approvalState + "]";
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
