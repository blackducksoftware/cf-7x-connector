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
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.common.RequestVulnerabilityPojo;

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
            RequestVulnerabilityPojo requestVulnerabilityPojo = toPojo(sdkVuln);
            vulns.add(requestVulnerabilityPojo);
        }

        return vulns;
    }

    @Override
    public void updateRequestVulnerability(RequestVulnerabilityPojo updatedRequestVulnerability) throws CommonFrameworkException {
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

        VulnerabilityStatusNameToken vulnerabilityStatusNameToken = new VulnerabilityStatusNameToken();
        vulnerabilityStatusNameToken.setName(updatedRequestVulnerability.getReviewStatusName());
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

    private RequestVulnerabilityPojo toPojo(RequestVulnerabilitySummary sdkVuln) {
        RequestVulnerabilityPojo vuln = new RequestVulnerabilityPojo(sdkVuln.getId().getId(), sdkVuln.getName().getName(),
                sdkVuln.getDescription(), sdkVuln.getBasescore(), sdkVuln.getExploitabilityscore(),
                sdkVuln.getImpactscore(), sdkVuln.getCreated(), sdkVuln.getModified(), sdkVuln.getPublished(), sdkVuln.getRequestId().getId(),
                sdkVuln.getComments(), sdkVuln.getReviewStatusName().getName(),
                sdkVuln.getTargetRemediateDate(), sdkVuln.getActualRemediateDate());
        return vuln;
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
