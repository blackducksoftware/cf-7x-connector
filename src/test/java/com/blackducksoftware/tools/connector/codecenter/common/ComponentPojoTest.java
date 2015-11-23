package com.blackducksoftware.tools.connector.codecenter.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.common.LicensePojo;

public class ComponentPojoTest {
    private static final String TEST_LICENSE_TEXT2 = "Test License Text2";
    private static final String TEST_LICENSE_NAME2 = "Test License Name2";
    private static final String TEST_LICENSE_ID2 = "testLicenseId2";
    private static final String TEST_LICENSE_TEXT1 = "Test License Text1";
    private static final String TEST_LICENSE_NAME1 = "Test License Name1";
    private static final String TEST_LICENSE_ID1 = "testLicenseId1";
    private static final String TEST_KB_RELEASE_ID = "testKbReleaseId";
    private static final String TEST_KB_COMP_ID = "testKbCompId";
    private static final String TEST_AUDIENCES = "test audiences";
    private static final String COMP_HOMEPAGE = "www.google.com";
    private static final String COMP_VERSION = "TestCompVersion";
    private static final String COMP_NAME1 = "Test Component1";
    private static final String COMP_ID1 = "testCompId1";
    private static final String COMP_NAME2 = "Test Component2";
    private static final String COMP_ID2 = "testCompId2";
    private static final String ATTR_VALUE = "testAttrValue";
    private static final String ATTR_NAME = "testAttrName";
    private static final String ATTR_ID = "testAttrId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testWithoutSubComponents() {
	List<AttributeValuePojo> attrValues = new ArrayList<>();
	attrValues.add(new AttributeValuePojo(ATTR_ID, ATTR_NAME, ATTR_VALUE));

	List<LicensePojo> licenses = new ArrayList<>(2);
	LicensePojo license = new LicensePojo(TEST_LICENSE_ID1,
		TEST_LICENSE_NAME1, TEST_LICENSE_TEXT1);
	licenses.add(license);
	license = new LicensePojo(TEST_LICENSE_ID2, TEST_LICENSE_NAME2,
		TEST_LICENSE_TEXT2);
	licenses.add(license);

	CodeCenterComponentPojo comp = new CodeCenterComponentPojo();
	comp.setId(COMP_ID1);
	comp.setName(COMP_NAME1);
	comp.setVersion(COMP_VERSION);
	comp.setApprovalStatus(ApprovalStatus.PENDING);
	comp.setHomepage(COMP_HOMEPAGE);
	comp.setIntendedAudiences(TEST_AUDIENCES);
	comp.setKbComponentId(TEST_KB_COMP_ID);
	comp.setKbReleaseId(TEST_KB_RELEASE_ID);
	comp.setApplicationComponent(false);
	comp.setApplicationId(null);
	comp.setDeprecated(false);
	comp.setAttributeValues(attrValues);
	comp.setLicenses(licenses);
	comp.setSubComponents(null);

	assertEquals(COMP_ID1, comp.getId());
	assertEquals(COMP_NAME1, comp.getName());
	assertEquals(COMP_VERSION, comp.getVersion());

	assertEquals(COMP_HOMEPAGE, comp.getHomepage());
	assertEquals(TEST_AUDIENCES, comp.getIntendedAudiences());
	assertEquals(TEST_KB_COMP_ID, comp.getKbComponentId());
	assertEquals(TEST_KB_RELEASE_ID, comp.getKbReleaseId());
	assertEquals(false, comp.isApplicationComponent());
	assertEquals(null, comp.getApplicationId());
	assertEquals(false, comp.isDeprecated());

	assertEquals(ApprovalStatus.PENDING, comp.getApprovalStatus());
	assertEquals(ATTR_VALUE, comp.getAttributeByName(ATTR_NAME));

	assertEquals(2, comp.getLicenses().size());
	assertEquals(TEST_LICENSE_ID1, comp.getLicenses().get(0).getId());
	assertEquals(TEST_LICENSE_ID2, comp.getLicenses().get(1).getId());
	assertEquals(TEST_LICENSE_NAME1, comp.getLicenses().get(0).getName());
	assertEquals(TEST_LICENSE_NAME2, comp.getLicenses().get(1).getName());
	assertEquals(TEST_LICENSE_TEXT1, comp.getLicenses().get(0)
		.getLicenseText());
	assertEquals(TEST_LICENSE_TEXT2, comp.getLicenses().get(1)
		.getLicenseText());

    }

    @Test
    public void testWithSubComponents() {
	List<AttributeValuePojo> attrValues = new ArrayList<>();
	attrValues.add(new AttributeValuePojo(ATTR_ID, ATTR_NAME, ATTR_VALUE));

	List<LicensePojo> licenses = new ArrayList<>(2);
	LicensePojo license = new LicensePojo(TEST_LICENSE_ID1,
		TEST_LICENSE_NAME1, TEST_LICENSE_TEXT1);
	licenses.add(license);
	license = new LicensePojo(TEST_LICENSE_ID2, TEST_LICENSE_NAME2,
		TEST_LICENSE_TEXT2);
	licenses.add(license);

	CodeCenterComponentPojo subComp = new CodeCenterComponentPojo();
	subComp.setId(COMP_ID1);
	subComp.setName(COMP_NAME1);
	subComp.setVersion(COMP_VERSION);
	subComp.setApprovalStatus(ApprovalStatus.PENDING);
	subComp.setHomepage(COMP_HOMEPAGE);
	subComp.setIntendedAudiences(TEST_AUDIENCES);
	subComp.setKbComponentId(TEST_KB_COMP_ID);
	subComp.setKbReleaseId(TEST_KB_RELEASE_ID);
	subComp.setApplicationComponent(false);
	subComp.setApplicationId(null);
	subComp.setDeprecated(false);
	subComp.setAttributeValues(attrValues);
	subComp.setLicenses(licenses);
	subComp.setSubComponents(null);

	List<CodeCenterComponentPojo> subComponents = new ArrayList<>();
	subComponents.add(subComp);

	CodeCenterComponentPojo comp = new CodeCenterComponentPojo();
	comp.setId(COMP_ID2);
	comp.setName(COMP_NAME2);
	comp.setVersion(COMP_VERSION);
	comp.setApprovalStatus(ApprovalStatus.PENDING);
	comp.setHomepage(COMP_HOMEPAGE);
	comp.setIntendedAudiences(TEST_AUDIENCES);
	comp.setKbComponentId(TEST_KB_COMP_ID);
	comp.setKbReleaseId(TEST_KB_RELEASE_ID);
	comp.setApplicationComponent(false);
	comp.setApplicationId(null);
	comp.setDeprecated(false);
	comp.setAttributeValues(attrValues);
	comp.setLicenses(licenses);
	comp.setSubComponents(subComponents);

	assertEquals(1, comp.getSubComponents().size());
	assertEquals(COMP_NAME2, comp.getName());
	assertEquals(COMP_NAME1, comp.getSubComponents().get(0).getName());
    }

}
