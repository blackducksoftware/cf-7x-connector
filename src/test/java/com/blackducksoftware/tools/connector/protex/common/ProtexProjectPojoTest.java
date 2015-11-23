package com.blackducksoftware.tools.connector.protex.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo.ApprovalState;

public class ProtexProjectPojoTest {

    private static final String TEST_LICENSE_NAME = "test license name";
    private static final String VERSION_ID = "versionId";
    private static final String NAME_ID = "nameId";
    private static final String TEST_PRIMARY_LICENSE_ID = "testPrimaryLicenseId";
    private static final String TEST_PRIMARY_LICENSE_NAME = "Test Primary License Name";
    private static final String TEST_COMPONENT_DESCRIPTION = "test component description";
    private static final String TEST_HOMEPAGE = "testHomepage";
    private static final String TEST_COMPONENT_VERSION = "testComponentVersion";
    private static final String TEST_COMPONENT_NAME = "testComponentName";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	ComponentNameVersionIds nameVersionIds = new ComponentNameVersionIds(
		NAME_ID, VERSION_ID);
	List<ProtexLicensePojo> licenses = new ArrayList<>();
	ProtexLicensePojo license = new ProtexLicensePojo("testLicenseId",
		TEST_LICENSE_NAME, "test comment", "", "",
		ApprovalState.APPROVED, "test license text");
	licenses.add(license);

	ProtexComponentPojo comp = new ProtexComponentPojo();

	comp.setName(TEST_COMPONENT_NAME);
	comp.setVersion(TEST_COMPONENT_VERSION);
	comp.setApprovalStatus(ApprovalStatus.APPROVED);
	comp.setHomepage(TEST_HOMEPAGE);
	comp.setDeprecated(true);
	comp.setNameVersionIds(nameVersionIds);
	comp.setLicenses(licenses);
	comp.setType(ProtexComponentType.STANDARD);
	comp.setDescription(TEST_COMPONENT_DESCRIPTION);
	comp.setPrimaryLicenseId(TEST_PRIMARY_LICENSE_ID);
	comp.setPrimaryLicenseName(TEST_PRIMARY_LICENSE_NAME);

	assertEquals(TEST_COMPONENT_NAME, comp.getName());
	assertEquals(TEST_COMPONENT_VERSION, comp.getVersion());
	assertEquals(ApprovalStatus.APPROVED, comp.getApprovalStatus());
	assertEquals(TEST_HOMEPAGE, comp.getHomepage());
	assertEquals(true, comp.isDeprecated());
	assertEquals(NAME_ID, comp.getNameVersionIds().getNameId());
	assertEquals(VERSION_ID, comp.getNameVersionIds().getVersionId());
	assertEquals(TEST_LICENSE_NAME, comp.getLicenses().get(0).getName());
	assertEquals(ProtexComponentType.STANDARD, comp.getType());
	assertEquals(TEST_COMPONENT_DESCRIPTION, comp.getDescription());
	assertEquals(TEST_PRIMARY_LICENSE_ID, comp.getPrimaryLicenseId());
	assertEquals(TEST_PRIMARY_LICENSE_NAME, comp.getPrimaryLicenseName());
    }
}
