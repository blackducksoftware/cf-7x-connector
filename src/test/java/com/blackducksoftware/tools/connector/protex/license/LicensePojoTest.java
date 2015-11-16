package com.blackducksoftware.tools.connector.protex.license;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class LicensePojoTest {

    private static final String TEST_TEXT = "Test License Text";
    private static final String TEST_SUFFIX = "Test Suffix";
    private static final String TEST_EXPLANATION = "Test Explanation";
    private static final String TEST_COMMENT = "Test Comment";
    private static final String TEST_NAME = "Test Name";
    private static final String TEST_ID = "testId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	ProtexLicensePojo lic = new ProtexLicensePojo(TEST_ID, TEST_NAME, TEST_COMMENT,
		TEST_EXPLANATION, TEST_SUFFIX,
		ProtexLicensePojo.ApprovalState.DISAPPROVED, TEST_TEXT);

	assertEquals(TEST_ID, lic.getId());
	assertEquals(TEST_NAME, lic.getName());
	assertEquals(TEST_COMMENT, lic.getComment());
	assertEquals(TEST_EXPLANATION, lic.getExplanation());
	assertEquals(TEST_SUFFIX, lic.getSuffix());
	assertEquals(ProtexLicensePojo.ApprovalState.DISAPPROVED,
		lic.getApprovalState());
	assertEquals(TEST_TEXT, lic.getLicenseText());
    }
}
