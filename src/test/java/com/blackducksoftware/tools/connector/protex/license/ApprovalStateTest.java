package com.blackducksoftware.tools.connector.protex.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class ApprovalStateTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertTrue(ProtexLicensePojo.ApprovalState.APPROVED
		.isEquivalent(LicenseApprovalState.APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.BLANKET_APPROVED
		.isEquivalent(LicenseApprovalState.BLANKET_APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.DISAPPROVED
		.isEquivalent(LicenseApprovalState.DIS_APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.NOT_REVIEWED
		.isEquivalent(LicenseApprovalState.NOT_REVIEWED));

	assertEquals("APPROVED", ProtexLicensePojo.ApprovalState.APPROVED.toString());
    }

}
