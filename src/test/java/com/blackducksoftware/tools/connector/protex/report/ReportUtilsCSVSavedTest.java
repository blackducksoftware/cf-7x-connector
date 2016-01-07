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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
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

import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;

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

    // Individual Sections
    private static final String ID_FILES_CSV = "src/test/resources/savedreports/csv/csv_id_files.csv";

    private static final String ID_FILES_LONG_CSV = "src/test/resources/savedreports/csv/long_id_files.csv";

    private static final String SUMMARY_CSV = "src/test/resources/savedreports/csv/csv_summary.csv";

    private static final String BOM_CSV = "src/test/resources/savedreports/csv/csv_bom.csv";

    private static final String OBLIGATIONS_CSV = "src/test/resources/savedreports/csv/obligations.csv";

    private static final int EXPECTED_COUNT_OBLIGATIONS = 164;

    private static final int EXPECTED_COUNT_LICENSE_TEXTS = 13;

    private static final int EXPECTED_COUNT_ANALYSIS_SUMMARY = 33;

    private static final int EXPECTED_COUNT_SUMMARY = 8;

    private static final int EXPECTED_COUNT_BOM = 8;

    private static final int EXPECTED_COUNT_ID_FILES = 263;

    private static final int EXPECTED_COUNT_ID_FILES_LONG_CSV = 745;

    // All the sections
    private static final String COMBINED_CSV = "src/test/resources/savedreports/csv/CSV_IT_TESTReport.csv";

    private static final String LICENSE_TEXTS_CSV = "src/test/resources/savedreports/csv/csv_license_texts.csv";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Summary section does not have a header.
     * 
     * @throws Exception
     */
    @Test
    public void testBasicSummaryCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.SUMMARY.toString().toLowerCase());
        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(SUMMARY_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_SUMMARY, rows.size());
    }

    @Test
    public void testBasicIdentifiedFileCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.IDENTIFIED_FILES.toString().toLowerCase());
        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(ID_FILES_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_ID_FILES, rows.size());
    }

    @Test
    public void testLongIdentifiedFileCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.IDENTIFIED_FILES.toString().toLowerCase());

        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(ID_FILES_LONG_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_ID_FILES_LONG_CSV, rows.size());
    }

    @Test
    public void testBasicBOMCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.BILL_OF_MATERIALS.toString().toLowerCase());
        ;
        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(BOM_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_BOM, rows.size());
    }

    /**
     * Tests the chunking mechanism by getting 3 rows at a time. Total rows is
     * 8, so expecting 3 returns.
     * 
     * @throws Exception
     */
    @Test
    public void testBOMCountInChunks() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.BILL_OF_MATERIALS.toString().toLowerCase());
        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(BOM_CSV);

        int totalCount = 0;
        while (!csvProcessor.isFinished()) {
            List<AdHocElement> rows = csvProcessor.getRowChunk(report,
                    AdHocElement.class, 3);
            totalCount += rows.size();
        }

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_BOM, totalCount);
    }

    /**
     * This pulls the analysis summary report, but from the combined report
     * TODO: Disabling this report because these sections have no headers No
     * headers causes failure, come back to this.
     * 
     * @throws Exception
     */
    @Test
    public void testAnalysisummaryCountFromCombined() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.ANALYSIS_SUMMARY.toString().toLowerCase());

        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(COMBINED_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        // Number within the CSV (excluding those rows without a proper section
        // key)
        assertEquals(EXPECTED_COUNT_ANALYSIS_SUMMARY, rows.size());
    }

    @Test
    public void testObligationsCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.OBLIGATIONS.toString().toLowerCase());

        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(OBLIGATIONS_CSV);

        List<AdHocElement> rows = csvProcessor.getRows(report,
                AdHocElement.class);

        assertEquals(EXPECTED_COUNT_OBLIGATIONS, rows.size());
    }

    @Test
    public void testLicenseTextsCount() throws Exception {
        csvProcessor = new ProtexReportCSVProcessor<AdHocElement>(ReportSectionType.LICENSE_TEXTS.toString().toLowerCase());

        // Mock the report object for the parser
        ReportPojo report = mockTheReportBySection(LICENSE_TEXTS_CSV);
        List<AdHocElement> rows = csvProcessor.getRows(report, AdHocElement.class);

        assertEquals(EXPECTED_COUNT_LICENSE_TEXTS, rows.size());
    }

    /**
     * The mock here is the actual data source, naming is not relevant.
     * 
     * @param sectionFilePathName
     *            the path name of the file
     * @return the report pojo
     */
    private ReportPojo mockTheReportBySection(String sectionFilePathName) {

        DataSource dataSource = new FileDataSource(sectionFilePathName);
        DataHandler dataHandler = new DataHandler(dataSource);
        ReportPojo report = new ReportPojo(dataHandler,
                getFullPathOfLocalFile(sectionFilePathName));
        return report;
    }

    /**
     * Returns the absolute pathname string of this abstract pathname
     * 
     * @param pathName
     *            the path name of the file
     * @return the absolute pathname string of this abstract pathname
     */
    private String getFullPathOfLocalFile(String pathName) {
        File f = new File(pathName);
        return f.getAbsolutePath();
    }
}
