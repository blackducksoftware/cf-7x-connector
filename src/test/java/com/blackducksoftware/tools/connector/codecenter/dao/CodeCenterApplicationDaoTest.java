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
package com.blackducksoftware.tools.connector.codecenter.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class CodeCenterApplicationDaoTest {
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

    private static CodeCenterServerWrapper mockCcServerWrapper;
    private static ApplicationApi mockApplicationApi;
    private static RequestApi mockRequestApi;
    private static ColaApi mockColaApi;

    private static final Date REMEDIATION_DATE = new Date(1431019378621L); // May
									   // 7,
									   // 2015
    private static final String REMEDIATION_DATE_STRING = "2015-05-07";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
	    "yyyy-MM-dd");

    private class ComponentDetails {
	private final RequestIdToken requestIdToken;
	private final String componentId;
	private final String kbComponentId;
	private final String componentName;
	private final String componentVersion;

	ComponentDetails(RequestIdToken requestIdToken, String componentId,
		String kbComponentId, String componentName,
		String componentVersion) {

	    this.requestIdToken = requestIdToken;
	    this.componentId = componentId;
	    this.kbComponentId = kbComponentId;
	    this.componentName = componentName;
	    this.componentVersion = componentVersion;
	}

	RequestIdToken getRequestIdToken() {
	    return requestIdToken;
	}

	String getComponentId() {
	    return componentId;
	}

	String getKbComponentId() {
	    return kbComponentId;
	}

	String getComponentName() {
	    return componentName;
	}

	String getComponentVersion() {
	    return componentVersion;
	}
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	mockCcServerWrapper = setupApis();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {

	// Setup

	Application mockApp = setupApp(APP_ID, APP_NAME, APP_VERSION,
		APP_DESCRIPTION);
	RequestIdToken requestIdToken1 = setupRequestIdToken(REQUEST_ID1);
	RequestIdToken requestIdToken2 = setupRequestIdToken(REQUEST_ID2);
	// setupVulnerabilities();
	setupVulnerability(REQUEST_ID1, VULNERABILITY_NAME1, VULNERABILITY_ID1,
		VULNERABILITY_DESCRIPTION, VULNERABILITY_STATUS1,
		VULN_COMMENT1, REMEDIATION_DATE, REMEDIATION_DATE);
	setupVulnerability(REQUEST_ID2, VULNERABILITY_NAME2, VULNERABILITY_ID2,
		VULNERABILITY_DESCRIPTION, VULNERABILITY_STATUS2,
		VULN_COMMENT2, REMEDIATION_DATE, REMEDIATION_DATE);

	List<ComponentDetails> componentDetailsList = new ArrayList<>();
	ComponentDetails componentDetails1 = new ComponentDetails(
		requestIdToken1, COMPONENT_ID1, KB_COMPONENT_ID1,
		COMPONENT_NAME1, COMPONENT_VERSION1);
	ComponentDetails componentDetails2 = new ComponentDetails(
		requestIdToken2, COMPONENT_ID2, KB_COMPONENT_ID2,
		COMPONENT_NAME2, COMPONENT_VERSION2);
	componentDetailsList.add(componentDetails1);
	componentDetailsList.add(componentDetails2);
	setupComponents(mockApp, componentDetailsList);

	// Test1
	CodeCenterApplicationDao dao = new CodeCenterApplicationDao(
		mockCcServerWrapper, false, mockApp);
	ApplicationPojo appPojo = dao.getApplication();

	// Verify results from Test1
	assertEquals(APP_NAME, appPojo.getName());
	assertEquals(APP_VERSION, appPojo.getVersion());
	checkCompUses(dao);
    }

    // @Test
    // public void testTableDriven() throws Exception {
    //
    // }

    private void checkCompUses(CodeCenterApplicationDao dao) throws SdkFault,
	    Exception {
	List<ComponentUsePojo> compUses = dao.getComponentUses();
	assertEquals(2, compUses.size());

	for (ComponentUsePojo compUse : compUses) {
	    ComponentPojo comp = dao.getComponent(compUse);

	    List<VulnerabilityPojo> vulns = dao.getVulnerabilities(comp,
		    compUse);
	    assertEquals(1, vulns.size());

	    for (VulnerabilityPojo vuln : vulns) {
		System.out.println("Component " + comp.getName() + ": vuln: "
			+ vuln.getName());

		if (COMPONENT_NAME1.equals(comp.getName())) {
		    assertEquals(VULNERABILITY_NAME1, vuln.getName());
		    String formattedActualRemediationDate = DATE_FORMAT
			    .format(vuln.getActualRemediationDate());
		    assertEquals(REMEDIATION_DATE_STRING,
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

    private void setupComponents(Application app,
	    List<ComponentDetails> componentDetails) throws SdkFault {

	List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>();

	// Build requestSummaries for this app
	for (int i = 0; i < componentDetails.size(); i++) {
	    ComponentIdToken componentIdToken1 = mock(ComponentIdToken.class);
	    when(componentIdToken1.getId()).thenReturn(
		    componentDetails.get(i).getComponentId());

	    RequestSummary requestSummary1 = mock(RequestSummary.class);
	    when(requestSummary1.getId()).thenReturn(
		    componentDetails.get(i).getRequestIdToken());
	    when(requestSummary1.getComponentId())
		    .thenReturn(componentIdToken1);

	    requestSummaries.add(requestSummary1);

	    Component component1 = mock(Component.class);
	    when(mockColaApi.getCatalogComponent(componentIdToken1))
		    .thenReturn(component1);

	    when(component1.getId()).thenReturn(componentIdToken1);
	    when(component1.getName()).thenReturn(
		    componentDetails.get(i).getComponentName());
	    when(component1.getVersion()).thenReturn(
		    componentDetails.get(i).getComponentVersion());
	    KbComponentIdToken kbComponentIdToken1 = mock(KbComponentIdToken.class);
	    when(kbComponentIdToken1.getId()).thenReturn(
		    componentDetails.get(i).getKbComponentId());
	    when(component1.getKbComponentId()).thenReturn(kbComponentIdToken1);
	}

	// ApplicationIdToken applicationIdToken = new ApplicationIdToken();
	// applicationIdToken.setId(appId);
	when(
		mockApplicationApi.searchApplicationRequests(eq(app.getId()),
			anyString(), (RequestPageFilter) anyObject()))
		.thenReturn(requestSummaries);
    }

    private static CodeCenterServerWrapper setupApis() {
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

    private void setupVulnerability(String requestId, String vulnName,
	    String vulnId, String vulnDescription, String vulnStatus,
	    String vulnComment, Date targetRemDate, Date actualRemDate)
	    throws SdkFault {
	List<RequestVulnerabilitySummary> requestVulnerabilitySummaries1 = new ArrayList<RequestVulnerabilitySummary>();
	RequestVulnerabilitySummary requestVulnerabilitySummary1 = new RequestVulnerabilitySummary();
	VulnerabilityNameToken vulnerabilityNameToken1 = new VulnerabilityNameToken();
	vulnerabilityNameToken1.setName(vulnName);

	VulnerabilityIdToken vulnerabilityIdToken1 = mock(VulnerabilityIdToken.class);
	when(vulnerabilityIdToken1.getId()).thenReturn(vulnId);
	requestVulnerabilitySummary1.setId(vulnerabilityIdToken1);
	requestVulnerabilitySummary1.setName(vulnerabilityNameToken1);
	requestVulnerabilitySummary1.setDescription(vulnDescription);
	requestVulnerabilitySummary1
		.setSeverity(VulnerabilitySeverityEnum.HIGH);
	requestVulnerabilitySummary1.setPublished(new Date());

	VulnerabilityStatusNameToken vulnerabilityStatusNameToken1 = mock(VulnerabilityStatusNameToken.class);
	when(vulnerabilityStatusNameToken1.getName()).thenReturn(vulnStatus);
	requestVulnerabilitySummary1
		.setReviewStatusName(vulnerabilityStatusNameToken1);
	requestVulnerabilitySummary1.setComments(vulnComment);
	requestVulnerabilitySummary1.setTargetRemediateDate(targetRemDate);
	requestVulnerabilitySummary1.setActualRemediateDate(actualRemDate);
	requestVulnerabilitySummaries1.add(requestVulnerabilitySummary1);

	RequestIdTokenMatcher returnsRequestId1 = new RequestIdTokenMatcher(
		requestId);

	when(
		mockRequestApi.searchVulnerabilities(
			argThat(returnsRequestId1),
			(RequestVulnerabilityPageFilter) anyObject()))
		.thenReturn(requestVulnerabilitySummaries1);
    }

    private RequestIdToken setupRequestIdToken(String requestId) {
	RequestIdToken requestIdToken = mock(RequestIdToken.class);
	when(requestIdToken.getId()).thenReturn(requestId);
	return requestIdToken;
    }

    private Application setupApp(String appId, String appName,
	    String appVersion, String appDescription) throws SdkFault {
	ApplicationNameVersionToken applicationNameVersionToken = new ApplicationNameVersionToken();
	applicationNameVersionToken.setName(appName);
	applicationNameVersionToken.setVersion(appVersion);
	ApplicationIdToken applicationIdToken = new ApplicationIdToken();
	applicationIdToken.setId(appId);
	Application mockApp = mock(Application.class);

	when(mockApp.getId()).thenReturn(applicationIdToken);
	when(mockApp.getName()).thenReturn(appName);
	when(mockApp.getVersion()).thenReturn(appVersion);
	when(mockApp.getDescription()).thenReturn(appDescription);

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
