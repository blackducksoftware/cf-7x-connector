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

public class RequestManager implements IRequestManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterAPIWrapper ccApiWrapper;

    private final ApplicationCache applicationCache;

    public RequestManager(CodeCenterAPIWrapper ccApiWrapper, ApplicationCache applicationCache) {
        this.ccApiWrapper = ccApiWrapper;
        this.applicationCache = applicationCache;
    }

    @Override
    public List<RequestVulnerabilityPojo> getVulnerabilitiesByRequestId(String requestId) throws CommonFrameworkException {

        RequestIdToken requestIdToken = new RequestIdToken();
        requestIdToken.setId(requestId);
        RequestVulnerabilityPageFilter filter = new RequestVulnerabilityPageFilter();
        filter.setFirstRowIndex(0);
        filter.setLastRowIndex(Integer.MAX_VALUE);
        List<RequestVulnerabilitySummary> requestVulnerabilitySummaries;
        try {
            requestVulnerabilitySummaries = ccApiWrapper.getRequestApi()
                    .searchVulnerabilities(requestIdToken, filter);
        } catch (SdkFault e) {
            String msg = "Error getting vulnerabilities for request ID " + requestId +
                    ": " + e.getMessage();
            log.error(msg);
            throw new CommonFrameworkException(msg);
        }

        List<RequestVulnerabilityPojo> vulns = new ArrayList<>(requestVulnerabilitySummaries.size());

        for (RequestVulnerabilitySummary sdkVuln : requestVulnerabilitySummaries) {
            sdkVuln.setRequestId(requestIdToken); // Workaround for Protex 7.3 bug PROTEX-21157
            RequestVulnerabilityPojo requestVulnerabilityPojo = toPojo(sdkVuln);
            vulns.add(requestVulnerabilityPojo);
        }

        return vulns;
    }

    @Override
    public void updateRequestVulnerability(RequestVulnerabilityPojo updatedRequestVulnerability) throws CommonFrameworkException {
        updateRequestVulnerability(updatedRequestVulnerability,
                false);
    }

    @Override
    public void updateRequestVulnerability(RequestVulnerabilityPojo updatedRequestVulnerability,
            boolean setUnreviewedAsNull) throws CommonFrameworkException {
        log.debug("updatedRequestVulnerability(): " + updatedRequestVulnerability);
        RequestVulnerabilityUpdate requestVulnerabilityUpdate = new RequestVulnerabilityUpdate();

        RequestIdToken requestIdToken = new RequestIdToken();
        requestIdToken.setId(updatedRequestVulnerability.getRequestId());
        requestVulnerabilityUpdate.setRequestId(requestIdToken);

        VulnerabilityIdToken vulnerabilityIdToken = new VulnerabilityIdToken();
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
        } catch (SdkFault e) {
            String msg = "requestApi.setVulnerabilityStatus(requestVulnerabilityUpdate) failed: "
                    + e.getMessage();
            log.error(msg);
            throw new CommonFrameworkException(msg);
        }
    }

    private RequestVulnerabilityPojo toPojo(RequestVulnerabilitySummary sdkVuln) throws CommonFrameworkException {
        RequestVulnerabilityPojo vuln = new RequestVulnerabilityPojo(sdkVuln.getId().getId(), sdkVuln.getName().getName(),
                sdkVuln.getDescription(), convertSeverity(sdkVuln.getSeverity()),
                sdkVuln.getBasescore(), sdkVuln.getExploitabilityscore(),
                sdkVuln.getImpactscore(), sdkVuln.getCreated(), sdkVuln.getModified(), sdkVuln.getPublished(), sdkVuln.getRequestId().getId(),
                sdkVuln.getComments(), sdkVuln.getReviewStatusName().getName(),
                sdkVuln.getTargetRemediateDate(), sdkVuln.getActualRemediateDate());
        return vuln;
    }

    private VulnerabilitySeverity convertSeverity(VulnerabilitySeverityEnum sdkSeverity) throws CommonFrameworkException {
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
    public String createRequest(String appId, String compId, String licenseId, boolean submit) throws CommonFrameworkException {
        RequestCreate request = new RequestCreate();

        // Should this be requested
        request.setSubmit(submit);

        RequestApplicationComponentToken token = new RequestApplicationComponentToken();
        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(appId);
        token.setApplicationId(appToken);
        ComponentIdToken componentIdToken = new ComponentIdToken();
        componentIdToken.setId(compId);
        token.setComponentId(componentIdToken);

        request.setApplicationComponentToken(token);
        LicenseIdToken licenseIdToken = new LicenseIdToken();
        licenseIdToken.setId(licenseId);
        request.setLicenseId(licenseIdToken);

        RequestIdToken requestIdToken;
        try {
            requestIdToken = ccApiWrapper.getRequestApi().createRequest(request);
        } catch (SdkFault e) {
            String msg = "requestApi.createRequest() failed for appId / compId: " + appId + " / " + compId + ": "
                    + e.getMessage();
            log.error(msg);
            throw new CommonFrameworkException(msg);
        }
        applicationCache.removeRequestsFromCache(appId); // we've just invalidated the cache
        return requestIdToken.getId();
    }

    @Override
    public void deleteRequest(String appId, String requestId) throws CommonFrameworkException {
        RequestIdToken token = new RequestIdToken();
        token.setId(requestId);
        try {
            ccApiWrapper.getRequestApi().deleteRequest(token);
        } catch (SdkFault e) {
            String msg = "requestApi.deleteRequest() failed for requestId " + requestId + ": "
                    + e.getMessage();
            log.error(msg);
            throw new CommonFrameworkException(msg);
        }
        applicationCache.removeRequestsFromCache(appId); // we've just invalidated the cache
    }
}
