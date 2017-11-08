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
package com.blackducksoftware.tools.connector.protex.report;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.blackducksoftware.sdk.protex.policy.PolicyApi;
import com.blackducksoftware.sdk.protex.policy.ProtexSystemInformation;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.ReportApi;
import com.blackducksoftware.sdk.protex.report.ReportFormat;
import com.blackducksoftware.sdk.protex.report.ReportSection;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.sdk.protex.report.ReportTemplateRequest;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.commonframework.test.TestUtils;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;
import com.blackducksoftware.tools.connector.protex.ProtexServerWrapper;

/**
 * These tests make sure ReportUtils correctly generates excel reports from
 * saved reports (HTML files previously generated/saved).
 *
 * @author Steve Billings
 * @date Oct 7, 2014
 *
 */
public class ReportUtilsHTMLSavedTest extends SavedTest {
    private static final String SERVER_NAME_PROTEX7 = "https://se-px01.dc1.lan";
    private static final String MOCK_PROTEX7_VERSION_STRING = "7.0";
    private static final String PROTEX7_HTML_DIR = "protex7";
    private static final String EXPECTED_REPORT_PROTEX7 = "src/test/resources/expected_report_comprehensive_protex7_savedHtml.xlsx";

    private static final String SAVED_REPORT_DIR = "src/test/resources/savedreports";

    // There are a few changes in Protex 7 that require template changes: some
    // headers changed, some fields were added.
    private static final String TEMPLATE_FILE_PROTEX7 = "src/test/resources/real_excel_template_comprehensive_protex7_v02.xlsx";

    private static final String PROTEX_PROJECT_ID = "reporttest_id";
    private static final String PROJECT_NAME_PROTEX7 = "JUnit_CF_ReportIT2";

    private static ProtexServerWrapper<ProtexProjectPojo> mockProtexServerWrapper;
    private static ProtexAPIWrapper mockApiWrapper;
    private static ReportApi mockReportApi;
    private static PolicyApi mockPolicyApi;
    private static ProjectApi mockProjectApi;

    /**
     * Create mocks for Protex
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

	// Mock wrappers and APIs
	mockProtexServerWrapper = mock(ProtexServerWrapper.class);
	mockApiWrapper = mock(ProtexAPIWrapper.class);
	mockReportApi = mock(ReportApi.class);
	mockPolicyApi = mock(PolicyApi.class);
	mockProjectApi = mock(ProjectApi.class);
	IReportManager reportManager = new ReportManager(mockApiWrapper);

	when(mockProtexServerWrapper.getInternalApiWrapper()).thenReturn(
		mockApiWrapper);
	when(mockProtexServerWrapper.getReportManager()).thenReturn(
		reportManager);
	when(mockApiWrapper.getReportApi()).thenReturn(mockReportApi);
	when(mockApiWrapper.getPolicyApi()).thenReturn(mockPolicyApi);
	when(mockApiWrapper.getProjectApi()).thenReturn(mockProjectApi);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * Test report generation from Protex 7 report HTML.
     *
     * @throws Exception
     */
    @Test
    public void testProtex7() throws Exception {
	testHTMLReport(MOCK_PROTEX7_VERSION_STRING, TEMPLATE_FILE_PROTEX7,
		PROTEX7_HTML_DIR, EXPECTED_REPORT_PROTEX7, PROJECT_NAME_PROTEX7);
    }

    private void testHTMLReport(String protexVersion, String templateFilename,
	    String htmlDir, String expectedReportFilename, String projectName)
	    throws Exception {

	// Mock the Protex server version
	ProtexSystemInformation protexInfo = new ProtexSystemInformation();
	protexInfo.setBdsServerLibraryVersion(protexVersion);
	when(mockPolicyApi.getSystemInformation()).thenReturn(protexInfo);

	// Create a mock project and tell the mock server wrapper to return it
	ProjectPojo expectedPojo = new ProtexProjectPojo(PROTEX_PROJECT_ID,
		projectName);
	when(mockProtexServerWrapper.getProjectByName(projectName)).thenReturn(
		expectedPojo);

	// Set up mocks so the Protex report HTML is read from files, not pulled
	// from Protex
	prepMockForSection(ReportSectionType.SUMMARY, htmlDir
		+ "/01_summary.html");
	prepMockForSection(
		ReportSectionType.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION,
		htmlDir + "/02_codeMatchesPendingId.html");
	prepMockForSection(ReportSectionType.STRING_SEARCH_PATTERNS, htmlDir
		+ "/03_stringSearchPatterns.html");
	prepMockForSection(ReportSectionType.CODE_MATCHES_ALL, htmlDir
		+ "/04_codeMatchesAll_reduced.html");

	prepMockForSection(ReportSectionType.ANALYSIS_SUMMARY, htmlDir
		+ "/05_analysisSummary.html");
	prepMockForSection(ReportSectionType.BILL_OF_MATERIALS, htmlDir
		+ "/06_bom.html");
	prepMockForSection(ReportSectionType.POTENTIAL_BILL_OF_MATERIALS,
		htmlDir + "/07_potentialBom.html");
	prepMockForSection(ReportSectionType.IP_ARCHITECTURE, htmlDir
		+ "/08_ipArchitecture.html");
	prepMockForSection(ReportSectionType.OBLIGATIONS, htmlDir
		+ "/09_obligations.html");
	prepMockForSection(ReportSectionType.FILE_INVENTORY, htmlDir
		+ "/10_fileInventory.html");
	prepMockForSection(ReportSectionType.IDENTIFIED_FILES, htmlDir
		+ "/11_identifiedFiles.html");
	prepMockForSection(ReportSectionType.EXCLUDED_COMPONENTS, htmlDir
		+ "/12_excludedComponents.html");
	prepMockForSection(ReportSectionType.STRING_SEARCHES, htmlDir
		+ "/13_stringSearches.html");
	prepMockForSection(ReportSectionType.STRING_SEARCH_HITS_PENDING_ID,
		htmlDir + "/14_stringSearchHitsPendingId.html");
	// 15 not used
	prepMockForSection(ReportSectionType.DEPENDENCIES_ALL, htmlDir
		+ "/16_dependenciesAll.html");
	prepMockForSection(
		ReportSectionType.DEPENDENCIES_JAVA_IMPORT_STATEMENTS, htmlDir
			+ "/17_dependenciesJavaImportStmts.html");
	prepMockForSection(
		ReportSectionType.DEPENDENCIES_JAVA_PACKAGE_STATEMENTS, htmlDir
			+ "/18_dependenciesJavaPackageStmts.html");
	prepMockForSection(ReportSectionType.DEPENDENCIES_NON_JAVA, htmlDir
		+ "/19_dependenciesNonJava.html");
	prepMockForSection(ReportSectionType.FILE_DISCOVERY_PATTERNS, htmlDir
		+ "/20_fileDiscoveryPatterns.html");
	prepMockForSection(ReportSectionType.RAPID_ID_CONFIGURATIONS, htmlDir
		+ "/21_rapidIdConfigurations.html");
	prepMockForSection(ReportSectionType.WORK_HISTORY_BILL_OF_MATERIALS,
		htmlDir + "/22_workHistoryBom.html");
	prepMockForSection(ReportSectionType.WORK_HISTORY_FILE_INVENTORY,
		htmlDir + "/23_workHistoryFileInventory.html");
	prepMockForSection(
		ReportSectionType.FILE_DISCOVERY_PATTERN_MATCHES_PENDING_IDENTIFICATION,
		htmlDir + "/23_fileDiscoveryPatternMatchesPendingId.html");
	prepMockForSection(ReportSectionType.CODE_MATCHES_PRECISION, htmlDir
		+ "/24_codeMatchesPrecision.html");
	// 25 not used
	prepMockForSection(ReportSectionType.LICENSE_TEXTS, htmlDir
		+ "/26_licenseText.html");
	prepMockForSection(ReportSectionType.CODE_LABEL, htmlDir
		+ "/27_codeLabel.html");
	prepMockForSection(ReportSectionType.COMPARE_CODE_MATCHES_PRECISION,
		htmlDir + "/28_compareCodeMatchesPrecision.html");
	prepMockForSection(ReportSectionType.COMPARE_CODE_MATCHES_ALL, htmlDir
		+ "/29_compareCodeMatchesAll.html");
	prepMockForSection(ReportSectionType.ANALYSIS_WARNINGS_AND_ERRORS,
		htmlDir + "/30_analysisWarningsAndErrors.html");
	prepMockForSection(ReportSectionType.IDENTIFICATION_AUDIT_TRAIL,
		htmlDir + "/31_identificationAuditTrail.html");
	prepMockForSection(ReportSectionType.LINK_TO_EXTERNAL_DOCUMENTS,
		htmlDir + "/32_linkToExternalDocuments.html");
	prepMockForSection(ReportSectionType.LICENSE_CONFLICTS, htmlDir
		+ "/33_licenseConflicts.html");
	prepMockForSection(ReportSectionType.LICENSES_IN_EFFECT, htmlDir
		+ "/34_licensesInEffect.html");
	prepMockForSection(
		ReportSectionType.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION,
		htmlDir + "/35_codeMatchesPendingIdPrecision.html");

	// The test: Use ReportUtils to generate report
	File templateFile = new File(templateFilename);
	ConfigurationManager config = initConfig(SERVER_NAME_PROTEX7);
	Workbook wb = (new ReportUtils()).getReportSectionBySection(
		mockProtexServerWrapper, projectName, templateFile, config,
		Format.HTML);

	// Write the generated report to a file
	String reportFilename = TestUtils.getTempReportFilePath();
	OutputStream os = new FileOutputStream(reportFilename);
	wb.write(os);
	os.close();

	// Compare the generated file to the expected file (generated/saved
	// earlier)
	TestUtils.checkReport(expectedReportFilename, reportFilename, false,
		false);
    }

    private void prepMockForSection(ReportSectionType sectionType,
	    String htmlFilename) throws Exception {
	// Mock up a report section (HTML) by loading it from a file
	DataSource dataSource = new FileDataSource(SAVED_REPORT_DIR + "/"
		+ htmlFilename);
	DataHandler dataHandler = new DataHandler(dataSource);
	Report report = new Report();
	report.setFileContent(dataHandler);

	// Tell the report API to return the mock report section
	when(
		mockReportApi.generateAdHocProjectReport(eq(PROTEX_PROJECT_ID),
			argThat(new IsRequestForThisSection(sectionType)),
			eq(ReportFormat.HTML), eq(false))).thenReturn(report);

    }

    /**
     * A class for creating section-specific Mockito custom argument matchers.
     * Used to detect which report section is being processed, which is used to
     * decide which saved report HTML file should be used.
     *
     * @author Steve Billings
     * @date Oct 7, 2014
     *
     */
    class IsRequestForThisSection extends
	    ArgumentMatcher<ReportTemplateRequest> {
	private ReportSectionType expectedSectionType;

	public IsRequestForThisSection(ReportSectionType expectedSectionType) {
	    this.expectedSectionType = expectedSectionType;
	}

	@Override
	public boolean matches(Object request) {
	    ReportSection argSection = ((ReportTemplateRequest) request)
		    .getSections().get(0);

	    ReportSectionType argSectionType = argSection.getSectionType();

	    boolean result = argSectionType.name().equalsIgnoreCase(
		    expectedSectionType.name());

	    return result;
	}
    }

}
