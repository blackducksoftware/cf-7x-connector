package com.blackducksoftware.tools.connector.codecenter.application;

import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;

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

    ApprovalStatusEnum getCcValue() {
	return ccValue;
    }
}
