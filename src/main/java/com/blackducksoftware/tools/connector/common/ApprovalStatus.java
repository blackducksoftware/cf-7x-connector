package com.blackducksoftware.tools.connector.common;

import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.sdk.protex.common.ApprovalState;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * The approval status of an object (application or component).
 *
 * @author sbillings
 *
 */
public enum ApprovalStatus {
    ALL(ApprovalStatusEnum.ALL), APPEALED(ApprovalStatusEnum.APPEALED), APPROVED(
	    ApprovalStatusEnum.APPROVED), CANCELLED(ApprovalStatusEnum.CANCELED), DEFERRED(
	    ApprovalStatusEnum.DEFERRED), MORE_INFO(ApprovalStatusEnum.MOREINFO), NOT_SUBMITTED(
	    ApprovalStatusEnum.NOTSUBMITTED), PENDING(
	    ApprovalStatusEnum.PENDING), REJECTED(ApprovalStatusEnum.REJECTED);

    private ApprovalStatusEnum ccValue;

    private ApprovalStatus(ApprovalStatusEnum ccValue) {
	this.ccValue = ccValue;
    }

    public boolean isEquivalent(ApprovalStatusEnum otherCcValue) {
	return ccValue.equals(otherCcValue);
    }

    /**
     * Convert a Code Center SDK approval status to the POJO equivalent.
     * Intended for use by cf-7x-connector classes.
     *
     * @param ccApprovalStatus
     * @return
     * @throws CommonFrameworkException
     */
    public static ApprovalStatus valueOf(ApprovalStatusEnum ccApprovalStatus)
	    throws CommonFrameworkException {
	switch (ccApprovalStatus) {
	case ALL:
	    return ApprovalStatus.ALL;
	case APPEALED:
	    return ApprovalStatus.APPEALED;
	case APPROVED:
	    return ApprovalStatus.APPROVED;
	case CANCELED:
	    return ApprovalStatus.CANCELLED;
	case DEFERRED:
	    return ApprovalStatus.DEFERRED;
	case MOREINFO:
	    return ApprovalStatus.MORE_INFO;
	case NOTSUBMITTED:
	    return ApprovalStatus.NOT_SUBMITTED;
	case PENDING:
	    return ApprovalStatus.PENDING;
	case REJECTED:
	    return ApprovalStatus.REJECTED;
	default:
	    throw new CommonFrameworkException("Unsupported ApprovalStatus: "
		    + ccApprovalStatus);
	}
    }

    /**
     * Convert a Protex SDK approval status to the POJO equivalent. Intended for
     * use by cf-7x-connector classes.
     *
     * @param protexApprovalState
     * @return
     * @throws CommonFrameworkException
     */
    public static ApprovalStatus valueOf(ApprovalState protexApprovalState)
	    throws CommonFrameworkException {
	switch (protexApprovalState) {

	case APPROVED:
	    return ApprovalStatus.APPROVED;

	case DIS_APPROVED:
	    return ApprovalStatus.REJECTED;

	case NOT_REVIEWED:
	    return ApprovalStatus.PENDING;

	default:
	    throw new CommonFrameworkException(
		    "Unsupported Protex ApprovalState: " + protexApprovalState);
	}
    }

    ApprovalStatusEnum getCcValue() {
	return ccValue;
    }
}
