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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.common.NameVersion;

public class ApplicationCacheTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testBasic() {
	ApplicationCache appCache = new ApplicationCache();
	NameVersion nameVersion = new NameVersion("testAppName",
		"testAppVersion");
	assertFalse(appCache.containsApplication(nameVersion));

	Application app = new Application();
	app.setName("testAppName");
	app.setVersion("testAppVersion");
	ApplicationIdToken appIdToken = new ApplicationIdToken();
	appIdToken.setId("testAppId");
	app.setId(appIdToken);
	appCache.putApplication(app);
	List<RequestSummary> requestSummaries = new ArrayList<>(1);
	RequestSummary request = new RequestSummary();
	requestSummaries.add(request);
	appCache.putRequestList("testAppId", requestSummaries);
	assertTrue(appCache.containsApplication(nameVersion));
	assertTrue(appCache.containsApplication("testAppId"));
	assertTrue(appCache.containsRequestList("testAppId"));

	appCache.remove("testAppName", "testAppVersion");

	assertFalse(appCache.containsApplication(nameVersion));
	assertFalse(appCache.containsApplication("testAppId"));
	assertFalse(appCache.containsRequestList("testAppId"));
    }

    @Test
    public void testRemoveByNameVersion() {
	ApplicationCache appCache = new ApplicationCache();
	NameVersion nameVersion = new NameVersion("testAppName",
		"testAppVersion");
	assertFalse(appCache.containsApplication(nameVersion));

	Application app = new Application();
	app.setName("testAppName");
	app.setVersion("testAppVersion");
	ApplicationIdToken appIdToken = new ApplicationIdToken();
	appIdToken.setId("testAppId");
	app.setId(appIdToken);
	appCache.putApplication(app);
	List<RequestSummary> requestSummaries = new ArrayList<>(1);
	RequestSummary request = new RequestSummary();
	requestSummaries.add(request);
	appCache.putRequestList("testAppId", requestSummaries);
	assertTrue(appCache.containsApplication(nameVersion));
	assertTrue(appCache.containsApplication("testAppId"));
	assertTrue(appCache.containsRequestList("testAppId"));

	appCache.remove("testAppId");

	assertFalse(appCache.containsApplication(nameVersion));
	assertFalse(appCache.containsApplication("testAppId"));
	assertFalse(appCache.containsRequestList("testAppId"));
    }

}
