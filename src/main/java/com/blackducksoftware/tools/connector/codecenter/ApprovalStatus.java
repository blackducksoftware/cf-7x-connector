package com.blackducksoftware.tools.connector.codecenter;

import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

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

    // TODO this is public, but takes an SDK class arg, which isn't ideal
    // Only used by a test?
    public boolean isEquivalent(ApprovalStatusEnum otherCcValue) {
	return ccValue.equals(otherCcValue);
    }

    /**
     * Convert an SDK approval status to the POJO equivalent. Should not be
     * called from classes outside cf-7x-connector.
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

    ApprovalStatusEnum getCcValue() {
	return ccValue;
    }
}
