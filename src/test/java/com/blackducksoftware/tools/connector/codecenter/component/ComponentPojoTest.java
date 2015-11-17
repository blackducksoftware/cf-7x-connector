package com.blackducksoftware.tools.connector.codecenter.component;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;

public class ComponentPojoTest {
    private static final String TEST_KB_RELEASE_ID = "testKbReleaseId";
    private static final String TEST_KB_COMP_ID = "testKbCompId";
    private static final String TEST_AUDIENCES = "test audiences";
    private static final String COMP_HOMEPAGE = "www.google.com";
    private static final String COMP_VERSION = "TestCompVersion";
    private static final String COMP_NAME = "Test Component";
    private static final String COMP_ID = "testCompId";
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
    public void test() {
	List<AttributeValuePojo> attrValues = new ArrayList<>();
	attrValues.add(new AttributeValuePojo(ATTR_ID, ATTR_NAME, ATTR_VALUE));

	ComponentPojo comp = new ComponentPojo(COMP_ID, COMP_NAME,
		COMP_VERSION, ApprovalStatus.PENDING, COMP_HOMEPAGE,
		TEST_AUDIENCES, TEST_KB_COMP_ID, TEST_KB_RELEASE_ID, false,
		null, false, attrValues);

	assertEquals(COMP_ID, comp.getId());
	assertEquals(COMP_NAME, comp.getName());
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
    }

}
