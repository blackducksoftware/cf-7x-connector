package com.blackducksoftware.tools.connector.codecenter.request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.attribute.data.VulnerabilityStatusNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.RequestApi;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.RequestVulnerabilitySummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityIdToken;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityNameToken;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySeverityEnum;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.common.RequestVulnerabilityPojo;
import com.blackducksoftware.tools.connector.codecenter.common.VulnerabilitySeverity;

public class RequestManagerTest {

	private static final String REM_STATUS_NAME = "Not an Issue (NAI)";
	private static final String CHANGED_REM_STATUS_NAME = "Rejected by Auditor";
	private static final String TEST_VULN_NAME = "testVulnName";
	private static final String TEST_VULN_ID = "testVulnId";
	private static final String REQUEST_ID = "testRequestId";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testVulnerabilityCaching() throws CommonFrameworkException, SdkFault {

		final CodeCenterAPIWrapper mockCcApiWrapper = mock(CodeCenterAPIWrapper.class);
		final ApplicationCache mockApplicationCache = mock(ApplicationCache.class);
		final RequestManager requestManager = new RequestManager(mockCcApiWrapper, mockApplicationCache);

		final RequestIdToken requestIdToken = new RequestIdToken();
		requestIdToken.setId(REQUEST_ID);

		final RequestApi mockRequestApi = mock(RequestApi.class);
		when(mockCcApiWrapper.getRequestApi()).thenReturn(mockRequestApi);
		final List<RequestVulnerabilitySummary> vulnSummaries = new ArrayList<>();
		final RequestVulnerabilitySummary vulnSummary = new RequestVulnerabilitySummary();
		final VulnerabilityIdToken vulnIdToken = new VulnerabilityIdToken();
		vulnIdToken.setId(TEST_VULN_ID);
		vulnSummary.setId(vulnIdToken);
		final VulnerabilityNameToken vulnNameToken = new VulnerabilityNameToken();
		vulnNameToken.setName(TEST_VULN_NAME);
		vulnSummary.setName(vulnNameToken);
		vulnSummary.setRequestId(requestIdToken);
		final VulnerabilityStatusNameToken vulnStatusNameToken = new VulnerabilityStatusNameToken();
		vulnStatusNameToken.setName(REM_STATUS_NAME);
		vulnSummary.setReviewStatusName(vulnStatusNameToken);
		vulnSummary.setSeverity(VulnerabilitySeverityEnum.HIGH);
		vulnSummaries.add(vulnSummary);
		when(mockRequestApi.searchVulnerabilities(any(RequestIdToken.class), any(RequestVulnerabilityPageFilter.class)))
		.thenReturn(vulnSummaries);

		List<RequestVulnerabilityPojo> vulns = requestManager.getVulnerabilitiesByRequestId(REQUEST_ID);

		// Verify that Code Center SDK was called to populate cache
		verify(mockRequestApi, times(1)).searchVulnerabilities(any(RequestIdToken.class),
				any(RequestVulnerabilityPageFilter.class));

		final Date now = new Date();
		final RequestVulnerabilityPojo updatedRequestVulnerability = new RequestVulnerabilityPojo(TEST_VULN_ID,
				TEST_VULN_NAME, "", VulnerabilitySeverity.HIGH, "0", "0", "0", now, now, now, REQUEST_ID, "",
				CHANGED_REM_STATUS_NAME, now, now);
		requestManager.updateRequestVulnerability(updatedRequestVulnerability);

		// Re-get the vuln and verify the change
		vulns = requestManager.getVulnerabilitiesByRequestId(REQUEST_ID);
		assertEquals(CHANGED_REM_STATUS_NAME, vulns.get(0).getReviewStatusName());

		// Verify that Code Center SDK was NOT called this time
		verify(mockRequestApi, times(1)).searchVulnerabilities(any(RequestIdToken.class),
				any(RequestVulnerabilityPageFilter.class));
	}

}
