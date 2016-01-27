package com.blackducksoftware.tools.connector.codecenter.application;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

public class ApplicationPojoTest {

    private static final String APP_VERSION = "TestAppVersion";

    private static final String APP_NAME = "Test Application";

    private static final String APP_ID = "testAppId";

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

        ApplicationPojo app = new ApplicationPojo(APP_ID, APP_NAME,
                APP_VERSION, attrValues, ApprovalStatus.PENDING, false);

        assertEquals(APP_ID, app.getId());
        assertEquals(APP_NAME, app.getName());
        assertEquals(APP_VERSION, app.getVersion());
        assertEquals(ApprovalStatus.PENDING, app.getApprovalStatus());
        assertEquals(ATTR_VALUE, app.getAttributeByName(ATTR_NAME));
    }

    @Test
    public void fieldNameCheck() {
        try {
            // The field name in ApplicationPojo should be attributeValuesByName
            // Other utilities rely on this field name staying the same
            Field field = ApplicationPojo.class.getDeclaredField("attributeValuesByName");
            Assert.assertNotNull(field);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
