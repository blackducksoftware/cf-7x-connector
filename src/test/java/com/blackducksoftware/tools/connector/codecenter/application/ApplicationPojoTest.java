/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    private static final String TEST_OWNER_ID = "testOwnerId";

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
                APP_VERSION, attrValues, ApprovalStatus.PENDING, false, TEST_OWNER_ID);

        assertEquals(APP_ID, app.getId());
        assertEquals(APP_NAME, app.getName());
        assertEquals(APP_VERSION, app.getVersion());
        assertEquals(ApprovalStatus.PENDING, app.getApprovalStatus());
        assertEquals(ATTR_VALUE, app.getAttributeByName(ATTR_NAME));
        assertEquals(TEST_OWNER_ID, app.getOwnerId());
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
