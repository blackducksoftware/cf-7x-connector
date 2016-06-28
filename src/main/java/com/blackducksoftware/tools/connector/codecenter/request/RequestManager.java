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
package com.blackducksoftware.tools.connector.codecenter.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.VulnerabilityStatusNameToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseIdToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestApplicationComponentToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestCreate;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityUpdate;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.RequestVulnerabilitySummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityIdToken;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySeverityEnum;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.common.RequestVulnerabilityPojo;
import com.blackducksoftware.tools.connector.codecenter.common.VulnerabilitySeverity;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class RequestManager implements IRequestManager {
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	private final CodeCenterAPIWrapper ccApiWrapper;

	private final ApplicationCache applicationCache;

	private final LoadingCache<String, List<RequestVulnerabilityPojo>> vulnsByRequestIdCache;

	public RequestManager(final CodeCenterAPIWrapper ccApiWrapper, final ApplicationCache applicationCache) {
		this.ccApiWrapper = ccApiWrapper;
		this.applicationCache = applicationCache;
		this.vulnsByRequestIdCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(60, TimeUnit.MINUTES)
				.build(new CacheLoader<String, List<RequestVulnerabilityPojo>>() {
					@Override
					public List<RequestVulnerabilityPojo> load(final String requestId) throws CommonFrameworkException {
						return fetchVulnerabilities(requestId);
					}
				});
	}

	private List<RequestVulnerabilityPojo> fetchVulnerabilities(final String requestId) throws CommonFrameworkException {
		log.debug("Fetching request with ID " + requestId + " from CodeCenter");
		final RequestIdToken requestIdToken = new RequestIdToken();
		requestIdToken.setId(requestId);
		final RequestVulnerabilityPageFilter filter = new RequestVulnerabilityPageFilter();
		filter.setFirstRowIndex(0);
		filter.setLastRowIndex(Integer.MAX_VALUE);
		final List<RequestVulnerabilityPojo> vulns = getVulnerabilitiesByRequestIdTokenPageFilter(requestIdToken,
				filter);

		return vulns;
	}

	@Override
	public List<RequestVulnerabilityPojo> getVulnerabilitiesByRequestId(final String requestId)
			throws CommonFrameworkException {
		try {
			return vulnsByRequestIdCache.get(requestId);
		} catch (final ExecutionException e) {
			throw new CommonFrameworkException("Error getting vulnerabilities for request ID '" + requestId
					+ "' from cache: " + e.getMessage());
		}

	}

	private List<RequestVulnerabilityPojo> getVulnerabilitiesByRequestIdTokenPageFilter(
			final RequestIdToken requestIdToken, final RequestVulnerabilityPageFilter filter) throws CommonFrameworkException {
		List<RequestVulnerabilitySummary> requestVulnerabilitySummaries;
		try {
			requestVulnerabilitySummaries = ccApiWrapper.getRequestApi()
					.searchVulnerabilities(requestIdToken, filter);
		} catch (final SdkFault e) {
			final String msg = "Error getting vulnerabilities for request ID " + requestIdToken.getId() +
					": " + e.getMessage();
			log.error(msg);
			throw new CommonFrameworkException(msg);
		}

		final List<RequestVulnerabilityPojo> vulns = new ArrayList<>(requestVulnerabilitySummaries.size());

		for (final RequestVulnerabilitySummary sdkVuln : requestVulnerabilitySummaries) {
			sdkVuln.setRequestId(requestIdToken); // Workaround for Protex 7.3 bug PROTEX-21157
			final RequestVulnerabilityPojo requestVulnerabilityPojo = toPojo(sdkVuln);
			vulns.add(requestVulnerabilityPojo);
		}
		return vulns;
	}

	@Override
	public void updateRequestVulnerability(final RequestVulnerabilityPojo updatedRequestVulnerability) throws CommonFrameworkException {
		updateRequestVulnerability(updatedRequestVulnerability,
				false);
	}

	@Override
	public void updateRequestVulnerability(final RequestVulnerabilityPojo updatedRequestVulnerability,
			final boolean setUnreviewedAsNull) throws CommonFrameworkException {
		log.debug("updatedRequestVulnerability(): " + updatedRequestVulnerability);

		final RequestVulnerabilityUpdate requestVulnerabilityUpdate = new RequestVulnerabilityUpdate();

		final RequestIdToken requestIdToken = new RequestIdToken();
		requestIdToken.setId(updatedRequestVulnerability.getRequestId());
		requestVulnerabilityUpdate.setRequestId(requestIdToken);

		final VulnerabilityIdToken vulnerabilityIdToken = new VulnerabilityIdToken();
		vulnerabilityIdToken.setId(updatedRequestVulnerability.getVulnerabilityId());
		requestVulnerabilityUpdate.setVulnerability(vulnerabilityIdToken);

		requestVulnerabilityUpdate.setActualRemediateDate(updatedRequestVulnerability.getActualRemediationDate());
		requestVulnerabilityUpdate.setTargetRemediateDate(updatedRequestVulnerability.getTargetRemediationDate());
		requestVulnerabilityUpdate.setComment(updatedRequestVulnerability.getComments());

		VulnerabilityStatusNameToken vulnerabilityStatusNameToken;
		if (setUnreviewedAsNull && "Unreviewed".equals(updatedRequestVulnerability.getReviewStatusName())) {
			log.debug("Setting vuln status to Unreviewed by setting it to null (the pre-CC 7.1.1 way)");
			vulnerabilityStatusNameToken = null; // Workaround for CC versions before 7.1.1
		} else {
			vulnerabilityStatusNameToken = new VulnerabilityStatusNameToken();
			vulnerabilityStatusNameToken.setName(updatedRequestVulnerability.getReviewStatusName());
		}
		requestVulnerabilityUpdate
		.setVulnerabilityStatus(vulnerabilityStatusNameToken);

		try {
			ccApiWrapper.getRequestApi().setVulnerabilityStatus(requestVulnerabilityUpdate);
		} catch (final SdkFault e) {
			final String msg = "requestApi.setVulnerabilityStatus(requestVulnerabilityUpdate) failed: "
					+ e.getMessage();
			log.error(msg);
			throw new CommonFrameworkException(msg);
		}

		updateCache(updatedRequestVulnerability);
	}

	private void updateCache(final RequestVulnerabilityPojo updatedRequestVulnerability)
			throws CommonFrameworkException {
		final String requestId = updatedRequestVulnerability.getRequestId();
		final List<RequestVulnerabilityPojo> cachedVulns = getCachedVulns(requestId);
		log.debug("cachedVulns: " + cachedVulns);

		final RequestVulnerabilityPojo oldVuln = removeOldVulnFromList(cachedVulns,
				updatedRequestVulnerability.getVulnerabilityId());
		log.debug("Removed old vuln from cached list: " + oldVuln);
		cachedVulns.add(updatedRequestVulnerability);
		log.debug("Added updated vuln to cached list: " + updatedRequestVulnerability);
		vulnsByRequestIdCache.put(requestId, cachedVulns);
	}

	private List<RequestVulnerabilityPojo> getCachedVulns(final String requestId) throws CommonFrameworkException {
		List<RequestVulnerabilityPojo> cachedVulns;
		try {
			cachedVulns = vulnsByRequestIdCache.get(requestId);
		} catch (final ExecutionException e) {
			throw new CommonFrameworkException("Error getting vulnerabilities for request ID '" + requestId
					+ "' from cache: " + e.getMessage());
		}
		return cachedVulns;
	}

	private RequestVulnerabilityPojo removeOldVulnFromList(final List<RequestVulnerabilityPojo> cachedVulns,
			final String vulnId) throws CommonFrameworkException {

		RequestVulnerabilityPojo oldVuln = null;
		for (final RequestVulnerabilityPojo cachedVuln : cachedVulns) {
			if (cachedVuln.getVulnerabilityId().equals(vulnId)) {
				oldVuln = cachedVuln;
				break;
			}
		}
		if (oldVuln == null) {
			throw new CommonFrameworkException("Failed to find vulnerability with ID " + vulnId + " in list");
		}
		cachedVulns.remove(oldVuln);
		return oldVuln;
	}

	private RequestVulnerabilityPojo toPojo(final RequestVulnerabilitySummary sdkVuln) throws CommonFrameworkException {
		final RequestVulnerabilityPojo vuln = new RequestVulnerabilityPojo(sdkVuln.getId().getId(), sdkVuln.getName().getName(),
				sdkVuln.getDescription(), convertSeverity(sdkVuln.getSeverity()),
				sdkVuln.getBasescore(), sdkVuln.getExploitabilityscore(),
				sdkVuln.getImpactscore(), sdkVuln.getCreated(), sdkVuln.getModified(), sdkVuln.getPublished(), sdkVuln.getRequestId().getId(),
				sdkVuln.getComments(), sdkVuln.getReviewStatusName().getName(),
				sdkVuln.getTargetRemediateDate(), sdkVuln.getActualRemediateDate());
		return vuln;
	}

	private VulnerabilitySeverity convertSeverity(final VulnerabilitySeverityEnum sdkSeverity) throws CommonFrameworkException {
		switch (sdkSeverity) {
		case HIGH:
			return VulnerabilitySeverity.HIGH;
		case LOW:
			return VulnerabilitySeverity.LOW;
		case MEDIUM:
			return VulnerabilitySeverity.MEDIUM;
		default:
			throw new CommonFrameworkException("Unsupported vulnerability severity: " + sdkSeverity);
		}
	}

	@Override
	public String createRequest(final String appId, final String compId, final String licenseId, final boolean submit) throws CommonFrameworkException {
		final RequestCreate request = new RequestCreate();

		// Should this be requested
		request.setSubmit(submit);

		final RequestApplicationComponentToken token = new RequestApplicationComponentToken();
		final ApplicationIdToken appToken = new ApplicationIdToken();
		appToken.setId(appId);
		token.setApplicationId(appToken);
		final ComponentIdToken componentIdToken = new ComponentIdToken();
		componentIdToken.setId(compId);
		token.setComponentId(componentIdToken);

		request.setApplicationComponentToken(token);
		final LicenseIdToken licenseIdToken = new LicenseIdToken();
		licenseIdToken.setId(licenseId);
		request.setLicenseId(licenseIdToken);

		RequestIdToken requestIdToken;
		try {
			requestIdToken = ccApiWrapper.getRequestApi().createRequest(request);
		} catch (final SdkFault e) {
			final String msg = "requestApi.createRequest() failed for appId / compId: " + appId + " / " + compId + ": "
					+ e.getMessage();
			log.error(msg);
			throw new CommonFrameworkException(msg);
		}
		applicationCache.removeRequestsFromCache(appId); // we've just invalidated the cache
		return requestIdToken.getId();
	}

	@Override
	public void deleteRequest(final String appId, final String requestId) throws CommonFrameworkException {
		final RequestIdToken token = new RequestIdToken();
		token.setId(requestId);
		try {
			ccApiWrapper.getRequestApi().deleteRequest(token);
		} catch (final SdkFault e) {
			final String msg = "requestApi.deleteRequest() failed for requestId " + requestId + ": "
					+ e.getMessage();
			log.error(msg);
			throw new CommonFrameworkException(msg);
		}
		applicationCache.removeRequestsFromCache(appId); // we've just invalidated the cache
	}
}
