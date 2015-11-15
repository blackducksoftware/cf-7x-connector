package com.blackducksoftware.tools.commonframework.connector.protex.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.commonframework.connector.protex.license.LicensePojo;

public class ApprovalStateTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertTrue(LicensePojo.ApprovalState.APPROVED
		.isEquivalent(LicenseApprovalState.APPROVED));
	assertTrue(LicensePojo.ApprovalState.BLANKET_APPROVED
		.isEquivalent(LicenseApprovalState.BLANKET_APPROVED));
	assertTrue(LicensePojo.ApprovalState.DISAPPROVED
		.isEquivalent(LicenseApprovalState.DIS_APPROVED));
	assertTrue(LicensePojo.ApprovalState.NOT_REVIEWED
		.isEquivalent(LicenseApprovalState.NOT_REVIEWED));

	assertEquals("APPROVED", LicensePojo.ApprovalState.APPROVED.toString());
    }

}
