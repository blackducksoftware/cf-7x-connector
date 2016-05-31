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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;

public class ApplicationCache {
    private final Map<NameVersion, Application> appsByNameVersionCache = new HashMap<>();

    private final Map<String, Application> appsByIdCache = new HashMap<>();

    private final Map<String, List<RequestSummary>> requestListsByAppIdCache = new HashMap<>();

    public void removeRequestsFromCache(String appId) {
	if (requestListsByAppIdCache.containsKey(appId)) {
	    requestListsByAppIdCache.remove(appId);
	}
    }

    public boolean containsApplication(NameVersion nameVersion) {
	return appsByNameVersionCache.containsKey(nameVersion);
    }

    public boolean containsApplication(String appId) {
	return appsByIdCache.containsKey(appId);
    }

    public Application getApplication(NameVersion nameVersion) {
	return appsByNameVersionCache.get(nameVersion);
    }

    public Application getApplication(String appId) {
	return appsByIdCache.get(appId);
    }

    public void putApplication(Application app) {
	NameVersion nameVersion = new NameVersion(app.getName(),
		app.getVersion());
	appsByNameVersionCache.put(nameVersion, app);
	appsByIdCache.put(app.getId().getId(), app);
    }

    public void removeApplication(Application app) {
	NameVersion nameVersion = new NameVersion(app.getName(),
		app.getVersion());
	appsByNameVersionCache.remove(nameVersion);
	appsByIdCache.remove(app.getId().getId());
    }

    public boolean containsRequestList(String appId) {
	return requestListsByAppIdCache.containsKey(appId);
    }

    public List<RequestSummary> getRequestList(String appId) {
	return requestListsByAppIdCache.get(appId);
    }

    public void putRequestList(String appId,
	    List<RequestSummary> requestSummaries) {
	requestListsByAppIdCache.put(appId, requestSummaries);
    }

    // Call only one of these remove methods; no need to cal both

    public void remove(String appName, String appVersion) {
	NameVersion nameVersion = new NameVersion(appName, appVersion);
	if (!appsByNameVersionCache.containsKey(nameVersion)) {
	    return;
	}
	Application app = appsByNameVersionCache.get(nameVersion);
	if (appsByIdCache.containsKey(app.getId().getId())) {
	    appsByIdCache.remove(app.getId().getId());
	}
	appsByNameVersionCache.remove(nameVersion);

	if (requestListsByAppIdCache.containsKey(app.getId().getId())) {
	    requestListsByAppIdCache.remove(app.getId().getId());
	}
    }

    public void remove(String appId) {
	if (!appsByIdCache.containsKey(appId)) {
	    return;
	}
	Application app = appsByIdCache.get(appId);
	appsByIdCache.remove(appId);
	NameVersion nameVersion = new NameVersion(app.getName(),
		app.getVersion());
	appsByNameVersionCache.remove(nameVersion);

	if (requestListsByAppIdCache.containsKey(appId)) {
	    requestListsByAppIdCache.remove(appId);
	}
    }
}
