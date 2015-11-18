package com.blackducksoftware.tools.connector.codecenter.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;

public class ApprovalStatusTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertTrue(ApprovalStatus.ALL.isEquivalent(ApprovalStatusEnum.ALL));
	assertTrue(ApprovalStatus.APPEALED
		.isEquivalent(ApprovalStatusEnum.APPEALED));
	assertTrue(ApprovalStatus.APPROVED
		.isEquivalent(ApprovalStatusEnum.APPROVED));
	assertTrue(ApprovalStatus.CANCELLED
		.isEquivalent(ApprovalStatusEnum.CANCELED));
	assertTrue(ApprovalStatus.DEFERRED
		.isEquivalent(ApprovalStatusEnum.DEFERRED));
	assertTrue(ApprovalStatus.MORE_INFO
		.isEquivalent(ApprovalStatusEnum.MOREINFO));
	assertTrue(ApprovalStatus.NOT_SUBMITTED
		.isEquivalent(ApprovalStatusEnum.NOTSUBMITTED));
	assertTrue(ApprovalStatus.PENDING
		.isEquivalent(ApprovalStatusEnum.PENDING));
	assertTrue(ApprovalStatus.REJECTED
		.isEquivalent(ApprovalStatusEnum.REJECTED));
	assertFalse(ApprovalStatus.REJECTED
		.isEquivalent(ApprovalStatusEnum.APPROVED));
    }

}
