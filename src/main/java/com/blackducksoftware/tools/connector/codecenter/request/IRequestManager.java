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
	 * Get those vulnerabilities associated with a request that have the given
	 * remediation status.
	 * 
	 * @param requestId
	 * @param remediationStatusName
	 * @return
	 * @throws CommonFrameworkException
	 */
	List<RequestVulnerabilityPojo> getVulnerabilitiesByRequestIdRemediationStatus(String requestId,
			String remediationStatusName) throws CommonFrameworkException;

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

	/**
	 * Update the given request vulnerability with values in the given POJO.
	 * The request ID and vulnerability ID must be the original values. These identify the request vulnerability
	 * to change. This method updates the following request vulnerability values: Remediation status,
	 * target/actual remediation dates, and comments.
	 *
	 * @param updatedRequestVulnerability
	 * @throws CommonFrameworkException
	 */
	void updateRequestVulnerability(RequestVulnerabilityPojo updatedRequestVulnerability, boolean setUnreviewedAsNull) throws CommonFrameworkException;

	/**
	 * Create a new component request on an application.
	 *
	 * @param appId
	 * @param compId
	 * @param licenseId
	 * @param submit
	 * @return
	 * @throws CommonFrameworkException
	 */
	String createRequest(String appId, String compId, String licenseId, boolean submit) throws CommonFrameworkException;

	/**
	 * Delete a component request from an application.
	 *
	 * @param requestId
	 * @throws CommonFrameworkException
	 */
	void deleteRequest(String appId, String requestId) throws CommonFrameworkException;
}
