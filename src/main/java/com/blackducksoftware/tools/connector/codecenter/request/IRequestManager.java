package com.blackducksoftware.tools.connector.codecenter.request;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.RequestVulnerabilityPojo;

public interface IRequestManager {
    /**
     * Get all vulnerabilities associated with a request.
     *
     * @param requestId
     * @return
     * @throws CommonFrameworkException
     */
    List<RequestVulnerabilityPojo> getVulnerabilitiesByRequestId(String requestId) throws CommonFrameworkException;

    /**
     * Update the given request vulnerability with values in the given POJO.
     * The request ID and vulnerability ID must be the original values. These identify the request vulnerability
     * to change. This method updates the following request vulnerability values: Remediation status,
     * target/actual remediation dates, and comments.
     *
     * @param updatedRequestVulnerability
     * @throws CommonFrameworkException
     */
    void updateRequestVulnerability(RequestVulnerabilityPojo updatedRequestVulnerability) throws CommonFrameworkException;
}