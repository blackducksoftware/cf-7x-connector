/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.connector.codecenter.application;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationUserPojoTest {

    private static final String APP_VERSION = "TestAppVersion";

    private static final String APP_NAME = "Test Application";

    private static final String APP_ID = "testAppId";

    private static final String USER_NAME = "testUserName";

    private static final String USER_ID = "testUserId";

    private static final String ROLE_NAME = "testRoleName";

    private static final String ROLE_ID = "testRoleId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {

        ApplicationUserPojo appUser = new ApplicationUserPojo(APP_NAME, APP_VERSION,
                APP_ID, USER_NAME, USER_ID, ROLE_NAME, ROLE_ID);

        assertEquals(APP_VERSION, appUser.getApplicationVersion());
        assertEquals(APP_NAME, appUser.getApplicationName());
        assertEquals(APP_ID, appUser.getApplicationId());
        assertEquals(USER_NAME, appUser.getUserName());
        assertEquals(USER_ID, appUser.getUserId());
        assertEquals(ROLE_NAME, appUser.getRoleName());
        assertEquals(ROLE_ID, appUser.getRoleId());
    }

}
