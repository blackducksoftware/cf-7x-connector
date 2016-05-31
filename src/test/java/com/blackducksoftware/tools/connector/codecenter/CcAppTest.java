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
package com.blackducksoftware.tools.connector.codecenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.VulnerabilityStatusNameToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.RequestApi;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.RequestVulnerabilitySummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityIdToken;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityNameToken;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySeverityEnum;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ApplicationPojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentPojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentUsePojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.VulnerabilityPojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.dao.CodeCenterApplicationDao;

public class CcAppTest {
    private static final String APP_DESCRIPTION = "test app";
    private static final String APP_ID = "appId";
    private static final String REQUEST_ID2 = "requestId2";
    private static final String REQUEST_ID1 = "requestId1";
    private static final String VULNERABILITY_STATUS2 = "vulnerabilityStatus2";
    private static final String VULNERABILITY_DESCRIPTION = "test";
    private static final String VULNERABILITY_ID2 = "vulnerabilityId2";
    private static final String VULNERABILITY_STATUS1 = "vulnerabilityStatus1";
    private static final String VULNERABILITY_ID1 = "vulnerabilityId1";
    private static final String KB_COMPONENT_ID2 = "kbComponentId2";
    private static final String COMPONENT_VERSION2 = "componentVersion2";
    private static final String COMPONENT_NAME2 = "componentName2";
    private static final String KB_COMPONENT_ID1 = "kbComponentId1";
    private static final String COMPONENT_VERSION1 = "componentVersion1";
    private static final String COMPONENT_ID2 = "componentId2";
    private static final String COMPONENT_ID1 = "componentId1";
    private static final String VULN_COMMENT2 = "test comment2";
    private static final String VULNERABILITY_NAME2 = "vulnerabilityName2";
    private static final String VULN_COMMENT1 = "test comment1";
    private static final String VULNERABILITY_NAME1 = "vulnerabilityName1";
    private static final String COMPONENT_NAME1 = "componentName1";
    private static final String APP_VERSION = "Unspecified";
    private static final String APP_NAME = "origApp";
    private ApplicationApi mockApplicationApi;
    private RequestApi mockRequestApi;
    private ColaApi mockColaApi;

    private ApplicationNameVersionToken applicationNameVersionToken;
    private ApplicationIdToken applicationIdToken;
    private RequestIdToken requestIdToken1;
    private RequestIdToken requestIdToken2;

    private static final Date remediationDate = new Date(1431019378621L); // May
									  // 7,
									  // 2015
    private static final String remediationDateString = "2015-05-07";
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {

	// Setup
	CodeCenterServerWrapper mockCcServerWrapper = setupApis();
	Application mockApp = setupApp();
	setupRequestIdTokens();
	setupVulnerabilities();
	setupComponents();

	// Test1
	CodeCenterApplicationDao dao = new CodeCenterApplicationDao(
		mockCcServerWrapper, false, mockApp);
	ApplicationPojo appPojo = dao.getApplication();

	// Verify results from Test1
	assertEquals(APP_NAME, appPojo.getName());
	assertEquals(APP_VERSION, appPojo.getVersion());
	checkCompUses(dao);
    }

    private void checkCompUses(CodeCenterApplicationDao dao) throws SdkFault,
	    Exception {
	List<ComponentUsePojo> compUses = dao.getComponentUses();
	for (ComponentUsePojo compUse : compUses) {
	    ComponentPojo comp = dao.getComponent(compUse);

	    List<VulnerabilityPojo> vulns = dao.getVulnerabilities(comp,
		    compUse);
	    for (VulnerabilityPojo vuln : vulns) {
		System.out.println("Component " + comp.getName() + ": vuln: "
			+ vuln.getName());

		if (COMPONENT_NAME1.equals(comp.getName())) {
		    assertEquals(VULNERABILITY_NAME1, vuln.getName());
		    String formattedActualRemediationDate = dateFormat
			    .format(vuln.getActualRemediationDate());
		    assertEquals(remediationDateString,
			    formattedActualRemediationDate);
		    assertEquals(VULN_COMMENT1, vuln.getStatusComment());
		} else if (COMPONENT_NAME2.equals(comp.getName())) {
		    assertEquals(VULNERABILITY_NAME2, vuln.getName());
		    assertEquals(VULN_COMMENT2, vuln.getStatusComment());
		} else {
		    fail("Unexpected component: " + comp.getName());
		}
	    }
	}
    }

    private void setupComponents() throws SdkFault {
	ComponentIdToken componentIdToken1 = mock(ComponentIdToken.class);
	when(componentIdToken1.getId()).thenReturn(COMPONENT_ID1);

	ComponentIdToken componentIdToken2 = mock(ComponentIdToken.class);
	when(componentIdToken2.getId()).thenReturn(COMPONENT_ID2);

	RequestSummary requestSummary1 = mock(RequestSummary.class);
	when(requestSummary1.getId()).thenReturn(requestIdToken1);
	when(requestSummary1.getComponentId()).thenReturn(componentIdToken1);

	RequestSummary requestSummary2 = mock(RequestSummary.class);
	when(requestSummary2.getId()).thenReturn(requestIdToken2);
	when(requestSummary2.getComponentId()).thenReturn(componentIdToken2);

	List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>();
	requestSummaries.add(requestSummary1);
	requestSummaries.add(requestSummary2);
	when(
		mockApplicationApi.searchApplicationRequests(
			eq(applicationIdToken), anyString(),
			(RequestPageFilter) anyObject())).thenReturn(
		requestSummaries);

	Component component1 = mock(Component.class);
	Component component2 = mock(Component.class);
	when(mockColaApi.getCatalogComponent(componentIdToken1)).thenReturn(
		component1);
	when(mockColaApi.getCatalogComponent(componentIdToken2)).thenReturn(
		component2);

	when(component1.getId()).thenReturn(componentIdToken1);
	when(component1.getName()).thenReturn(COMPONENT_NAME1);
	when(component1.getVersion()).thenReturn(COMPONENT_VERSION1);
	KbComponentIdToken kbComponentIdToken1 = mock(KbComponentIdToken.class);
	when(kbComponentIdToken1.getId()).thenReturn(KB_COMPONENT_ID1);
	when(component1.getKbComponentId()).thenReturn(kbComponentIdToken1);

	when(component2.getId()).thenReturn(componentIdToken2);
	when(component2.getName()).thenReturn(COMPONENT_NAME2);
	when(component2.getVersion()).thenReturn(COMPONENT_VERSION2);
	KbComponentIdToken kbComponentIdToken2 = mock(KbComponentIdToken.class);
	when(kbComponentIdToken2.getId()).thenReturn(KB_COMPONENT_ID2);
	when(component2.getKbComponentId()).thenReturn(kbComponentIdToken2);
    }

    private CodeCenterServerWrapper setupApis() {
	mockApplicationApi = mock(ApplicationApi.class);
	mockRequestApi = mock(RequestApi.class);
	mockColaApi = mock(ColaApi.class);
	CodeCenterAPIWrapper mockCodeCenterApiWrapper = mock(CodeCenterAPIWrapper.class);
	when(mockCodeCenterApiWrapper.getApplicationApi()).thenReturn(
		mockApplicationApi);
	CodeCenterServerWrapper mockCcServerWrapper = mock(CodeCenterServerWrapper.class);
	when(mockCcServerWrapper.getInternalApiWrapper()).thenReturn(
		mockCodeCenterApiWrapper);
	when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);
	when(mockCodeCenterApiWrapper.getRequestApi()).thenReturn(
		mockRequestApi);
	return mockCcServerWrapper;
    }

    private void setupVulnerabilities() throws SdkFault {
	List<RequestVulnerabilitySummary> requestVulnerabilitySummaries1 = new ArrayList<RequestVulnerabilitySummary>();
	RequestVulnerabilitySummary requestVulnerabilitySummary1 = new RequestVulnerabilitySummary();
	VulnerabilityNameToken vulnerabilityNameToken1 = new VulnerabilityNameToken();
	vulnerabilityNameToken1.setName(VULNERABILITY_NAME1);

	VulnerabilityIdToken vulnerabilityIdToken1 = mock(VulnerabilityIdToken.class);
	when(vulnerabilityIdToken1.getId()).thenReturn(VULNERABILITY_ID1);
	requestVulnerabilitySummary1.setId(vulnerabilityIdToken1);
	requestVulnerabilitySummary1.setName(vulnerabilityNameToken1);
	requestVulnerabilitySummary1.setDescription(VULNERABILITY_DESCRIPTION);
	requestVulnerabilitySummary1
		.setSeverity(VulnerabilitySeverityEnum.HIGH);
	requestVulnerabilitySummary1.setPublished(new Date());

	VulnerabilityStatusNameToken vulnerabilityStatusNameToken1 = mock(VulnerabilityStatusNameToken.class);
	when(vulnerabilityStatusNameToken1.getName()).thenReturn(
		VULNERABILITY_STATUS1);
	requestVulnerabilitySummary1
		.setReviewStatusName(vulnerabilityStatusNameToken1);
	requestVulnerabilitySummary1.setComments(VULN_COMMENT1);
	requestVulnerabilitySummary1.setTargetRemediateDate(remediationDate);
	requestVulnerabilitySummary1.setActualRemediateDate(remediationDate);

	requestVulnerabilitySummaries1.add(requestVulnerabilitySummary1);
	List<RequestVulnerabilitySummary> requestVulnerabilitySummaries2 = new ArrayList<RequestVulnerabilitySummary>();
	VulnerabilityNameToken vulnerabilityNameToken2 = new VulnerabilityNameToken();
	vulnerabilityNameToken2.setName(VULNERABILITY_NAME2);
	RequestVulnerabilitySummary requestVulnerabilitySummary2 = new RequestVulnerabilitySummary();

	VulnerabilityIdToken vulnerabilityIdToken2 = mock(VulnerabilityIdToken.class);
	when(vulnerabilityIdToken2.getId()).thenReturn(VULNERABILITY_ID2);
	requestVulnerabilitySummary2.setId(vulnerabilityIdToken2);
	requestVulnerabilitySummary2.setName(vulnerabilityNameToken2);
	requestVulnerabilitySummary2.setDescription(VULNERABILITY_DESCRIPTION);
	requestVulnerabilitySummary2
		.setSeverity(VulnerabilitySeverityEnum.HIGH);
	requestVulnerabilitySummary2.setPublished(new Date());

	VulnerabilityStatusNameToken vulnerabilityStatusNameToken2 = mock(VulnerabilityStatusNameToken.class);
	when(vulnerabilityStatusNameToken2.getName()).thenReturn(
		VULNERABILITY_STATUS2);
	requestVulnerabilitySummary2
		.setReviewStatusName(vulnerabilityStatusNameToken2);
	requestVulnerabilitySummary2.setComments(VULN_COMMENT2);
	requestVulnerabilitySummary2.setTargetRemediateDate(new Date());
	requestVulnerabilitySummary2.setActualRemediateDate(new Date());

	requestVulnerabilitySummaries2.add(requestVulnerabilitySummary2);
	RequestIdTokenMatcher returnsRequestId1 = new RequestIdTokenMatcher(
		REQUEST_ID1);
	RequestIdTokenMatcher returnsRequestId2 = new RequestIdTokenMatcher(
		REQUEST_ID2);

	when(
		mockRequestApi.searchVulnerabilities(
			argThat(returnsRequestId1),
			(RequestVulnerabilityPageFilter) anyObject()))
		.thenReturn(requestVulnerabilitySummaries1);
	when(
		mockRequestApi.searchVulnerabilities(
			argThat(returnsRequestId2),
			(RequestVulnerabilityPageFilter) anyObject()))
		.thenReturn(requestVulnerabilitySummaries2);
    }

    private void setupRequestIdTokens() {
	requestIdToken1 = mock(RequestIdToken.class);
	when(requestIdToken1.getId()).thenReturn(REQUEST_ID1);
	requestIdToken2 = mock(RequestIdToken.class);
	when(requestIdToken2.getId()).thenReturn(REQUEST_ID2);
    }

    private Application setupApp() throws SdkFault {
	applicationNameVersionToken = new ApplicationNameVersionToken();
	applicationNameVersionToken.setName(APP_NAME);
	applicationNameVersionToken.setVersion(APP_VERSION);
	applicationIdToken = new ApplicationIdToken();
	applicationIdToken.setId(APP_ID);
	Application mockApp = mock(Application.class);

	when(mockApp.getId()).thenReturn(applicationIdToken);
	when(mockApp.getName()).thenReturn(APP_NAME);
	when(mockApp.getVersion()).thenReturn(APP_VERSION);
	when(mockApp.getDescription()).thenReturn(APP_DESCRIPTION);

	when(mockApplicationApi.getApplication(applicationNameVersionToken))
		.thenReturn(mockApp);

	return mockApp;
    }

    private class RequestIdTokenMatcher extends BaseMatcher<RequestIdToken> {
	private String expectedId;

	public RequestIdTokenMatcher(String expectedId) {
	    this.expectedId = expectedId;
	}

	@Override
	public boolean matches(Object arg0) {
	    if (!(arg0 instanceof RequestIdToken)) {
		return false;
	    }
	    RequestIdToken requestIdToken = (RequestIdToken) arg0;
	    return expectedId.equals(requestIdToken.getId());
	}

	@Override
	public void describeTo(Description description) {
	    description.appendText("RequestIdTokenMatcher");
	}
    }

}
