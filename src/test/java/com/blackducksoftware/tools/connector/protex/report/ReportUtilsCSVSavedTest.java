/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;
import com.blackducksoftware.tools.connector.protex.report.ProtexReportCSVProcessor;

/**
 * This tests the parser for CSV, but does so against an existing set of saved
 * CSV files.
 *
 * Any changes in the parser will be picked up here. This will not spot changes
 * in the underlying CSV/Server changes.
 *
 * @author akamen
 *
 */
public class ReportUtilsCSVSavedTest extends SavedTest {

    private static ProtexReportCSVProcessor<AdHocElement> csvProcessor;

    // Section names for the new CSV report
    private static final String SECTION_IDENTIFIED_FILES = "identifiedFiles";
    private static final String SECTION_SUMMARY = "summary";
    private static final String SECTION_ANALYSIS_SUMMARY = "analysisSummary";
    private static final String SECTION_BOM = "billOfMaterials";
    private static final String SECTION_OBLIGATIONS = "obligations";

    // Individual Sections
    private static final String ID_FILES_CSV = "src/test/resources/savedreports/csv/csv_id_files.csv";
    private static final String ID_FILES_LONG_CSV = "src/test/resources/savedreports/csv/long_id_files.csv";
    private static final String SUMMARY_CSV = "src/test/resources/savedreports/csv/csv_summary.csv";
    private static final String BOM_CSV = "src/test/resources/savedreports/csv/csv_bom.csv";
    private static final String OBLIGATIONS_CSV = "src/test/resources/savedreports/csv/obligations.csv";

    
    private static final int EXPECTED_COUNT_OBLIGATIONS = 164;
    
    // All the sections
    private static final String COMBINED_CSV = "src/test/resources/savedreports/csv/CSV_IT_TESTReport.csv";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

 
    /**
     * Summary section does not have a header.
     * @throws Exception
     */
    @Test
    public void testBasicSummaryCount() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(
		SECTION_SUMMARY);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(SUMMARY_CSV);

	List<AdHocElement> rows = csvProcessor.getRows(report,
		AdHocElement.class);

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(8, rows.size());
    }

    @Test
    public void testBasicIdentifiedFileCount() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(
		SECTION_IDENTIFIED_FILES);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(ID_FILES_CSV);

	List<AdHocElement> rows = csvProcessor.getRows(report,
		AdHocElement.class);

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(263, rows.size());
    }

    @Test
    public void testLongIdentifiedFileCount() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(
		SECTION_IDENTIFIED_FILES);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(ID_FILES_LONG_CSV);

	List<AdHocElement> rows = csvProcessor.getRows(report,
		AdHocElement.class);

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(745, rows.size());
    }

    @Test
    public void testBasicBOMCount() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(SECTION_BOM);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(BOM_CSV);

	List<AdHocElement> rows = csvProcessor.getRows(report,
		AdHocElement.class);

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(8, rows.size());
    }

    /**
     * Tests the chunking mechanism by getting 3 rows at a time. Total rows is
     * 8, so expecting 3 returns.
     *
     * @throws Exception
     */
    @Test
    public void testBOMCountInChunks() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(SECTION_BOM);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(BOM_CSV);

	int totalCount = 0;

	while (!csvProcessor.isFinished()) {
	    List<AdHocElement> rows = csvProcessor.getRowChunk(report,
		    AdHocElement.class, 3);
	    totalCount += rows.size();
	}

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(8, totalCount);
    }

    /**
     * This pulls the analysis summary report, but from the combined report
     * TODO:  Disabling this report because these sections have no headers
     * No headers causes failure, come back to this.
     * @throws Exception
     */
    @Test
    public void testAnalysisummaryCountFromCombined() throws Exception {
	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(
		SECTION_ANALYSIS_SUMMARY);
	// Mock the report object for the parser
	Report report = mockTheReportBySection(COMBINED_CSV);

	List<AdHocElement> rows = csvProcessor.getRows(report,
		AdHocElement.class);

	// Number within the CSV (excluding those rows without a proper section
	// key)
	assertEquals(33, rows.size());
    }


    @Test
    public void testObligationsCount() throws Exception {
    	csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(SECTION_OBLIGATIONS);
    	// Mock the report object for the parser
    	Report report = mockTheReportBySection(OBLIGATIONS_CSV);

    	List<AdHocElement> rows = csvProcessor.getRows(report, AdHocElement.class);

    	assertEquals(EXPECTED_COUNT_OBLIGATIONS, rows.size());
    }
    
    /**
     * The mock here is the actual data source, naming is not relevant.
     *
     * @param section_file
     * @return
     */
    private Report mockTheReportBySection(String section_file) {
	Report report = new Report();
	DataSource dataSource = new FileDataSource(section_file);
	DataHandler dataHandler = new DataHandler(dataSource);

	report.setFileContent(dataHandler);
	report.setFileName(getFullPathOfLocalFile(section_file));

	return report;
    }

    private String getFullPathOfLocalFile(String sectionIdentifiedFiles) {
	File f = new File(sectionIdentifiedFiles);
	return f.getAbsolutePath();
    }

}
