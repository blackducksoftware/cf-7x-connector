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
package com.blackducksoftware.tools.connector.codecenter.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;

public class ApplicationCacheTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testApplication() {
        ApplicationCache appCache = new ApplicationCache();

        // Put an application in the cache
        Application app = new Application();
        app.setName("testAppName");
        app.setVersion("testAppVersion");
        ApplicationIdToken appIdToken = new ApplicationIdToken();
        appIdToken.setId("testAppId");
        app.setId(appIdToken);
        appCache.putApplication(app);

        // Verify that app is found in cache
        assertTrue(appCache.containsApplication("testAppId"));
        assertEquals("testAppName", appCache.getApplication("testAppId").getName());
        NameVersion nameVersion = new NameVersion("testAppName", "testAppVersion");
        assertTrue(appCache.containsApplication(nameVersion));
        assertEquals("testAppVersion", appCache.getApplication(nameVersion).getVersion());
    }

    @Test
    public void testRequestList() {
        ApplicationCache appCache = new ApplicationCache();

        // Add a request summary list to the cache
        List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>(1);
        requestSummaries.add(null);
        appCache.putRequestList("testAppId", requestSummaries);

        // Verify that the request summary list can be found
        assertTrue(appCache.containsRequestList("testAppId"));
        assertEquals(null, appCache.getRequestList("testAppId").get(0));
    }

}
