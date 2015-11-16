package com.blackducksoftware.tools.connector.codecenter.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeGroupTypeEnum;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeGroupType;

public class AttributeGroupTypeTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertEquals("APPLICATION", AttributeGroupType.APPLICATION.toString());
	assertEquals(AttributeGroupTypeEnum.APPLICATION,
		AttributeGroupType.APPLICATION.getCcType());

	assertEquals("COMPONENT", AttributeGroupType.COMPONENT.toString());
	assertEquals(AttributeGroupTypeEnum.COMPONENT,
		AttributeGroupType.COMPONENT.getCcType());
    }

}
