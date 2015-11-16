package com.blackducksoftware.tools.connector.codecenter.license;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.common.LicensePojo;

public class LicensePojoTest {

    private static final String TEST_TEXT = "Test License Text";
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
	LicensePojo lic = new LicensePojo(TEST_ID, TEST_NAME, TEST_TEXT);

	assertEquals(TEST_ID, lic.getId());
	assertEquals(TEST_NAME, lic.getName());
	assertEquals(TEST_TEXT, lic.getLicenseText());
    }
}
