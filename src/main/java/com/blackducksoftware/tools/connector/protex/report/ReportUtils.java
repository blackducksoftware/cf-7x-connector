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
package com.blackducksoftware.tools.connector.protex.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.model.TemplateColumn;
import com.blackducksoftware.tools.commonframework.standard.protex.report.model.TemplateSheet;
import com.blackducksoftware.tools.commonframework.standard.protex.report.template.TemplateReader;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;

/**
 * Higher-level abstraction for getting Protex reports using the
 * TemplateReader/ProtexServerWrapper code. If you want a protex report, based
 * on a template, in the form of a Workbook, call getReport(). This is easer
 * than calling TemplateReader.generateWorkbookFromFile() and
 * ProtexServerWrapper.getReport().
 * 
 * @author sbillings
 * 
 */
public class ReportUtils {
    private static final int MAX_CELL_STRING_LEN = 32767;

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private Integer chunkingSize = 0;

    public Integer getChunkingSize() {
        return chunkingSize;
    }

    public void setChunkingSize(Integer chunkingSize) {
        this.chunkingSize = chunkingSize;
    }

    /**
     * Get a Protex report in the form of a workbook, fetching sections one at a
     * time.
     * 
     * @param protexServerWrapper
     * @param protexProjectName
     * @param templateFile
     * @param config
     * @return
     * @throws Exception
     */
    public Workbook getReportSectionBySection(
            IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
            String protexProjectName, File templateFile,
            ConfigurationManager config, Format reportFormat) throws Exception {

        // Generate a workbook from a template file, and populate the template
        // map.
        TemplateReader templateReader = new TemplateReader(config);
        Workbook wb = TemplateReader.generateWorkBookFromFile(templateFile);
        templateReader.setTemplateBook(wb);
        templateReader.populateTemplateMap();

        ProjectPojo project = protexServerWrapper
                .getProjectByName(protexProjectName);

        Map<String, TemplateSheet> sheetMap = templateReader.getSheetMap();
        for (String sheetKey : sheetMap.keySet()) {
            TemplateSheet sheet = sheetMap.get(sheetKey);
            String reportSection = sheet.getSheetName();
            // System.out.println("Section: " + reportSection);

            List<AdHocElement> rowsToParse = getReportSection(
                    protexServerWrapper, project, reportSection, reportFormat,
                    AdHocElement.class);
            if (rowsToParse != null) {
                populateSectionData(templateReader, sheet, wb, rowsToParse);
            }
        }

        return wb;
    }

    private void populateSectionData(TemplateReader templateReader,
            TemplateSheet templateSheet, Workbook wb,
            List<AdHocElement> reportRows) throws Exception {
        Sheet sheet = wb.getSheet(templateSheet.getSheetName());
        Map<String, TemplateColumn> columnMap = templateSheet.getColumnMap();

        /**
         * Grab the keys once, since we are writing out rows, the keys must
         * logically be identical. Verify the keys against the template, by
         * removing all keys that do not exist in the template.
         */
        if ((reportRows == null) || (reportRows.size() == 0)) {
            log.info("There is no data in this section");
            return;
        }
        Set<String> elementValueKeys = reportRows.get(0).getPairKeys();
        elementValueKeys = verifyKeysAgainstTemplate(elementValueKeys,
                columnMap);

        /**
         * We are starting at one, because the sheet already has a header.
         */
        int rownum = 1;
        for (AdHocElement element : reportRows) {
            Row row = sheet.createRow(rownum);

            for (String elementValueKey : elementValueKeys) {
                // Grab the column from the map based on name
                TemplateColumn templateColumn = columnMap.get(elementValueKey);

                int columnPos = templateColumn.getColumnPos();

                // Place the cell in the same position as the template and fill
                // the style
                Cell cell = row.createCell(columnPos);
                cell.setCellStyle(templateColumn.getCellStyle());

                String elementValue = element.getValue(elementValueKey);
                elementValue = limitToMaxCellLen(elementValue);
                cell.setCellValue(elementValue);
            }
            rownum++;
        }
    }

    private static String limitToMaxCellLen(String orig) {
        if (orig.length() > MAX_CELL_STRING_LEN) {
            return orig.substring(0, (MAX_CELL_STRING_LEN - 1));
        }
        return orig;
    }

    /**
     * Removes the keys that do not exist in the template
     * 
     * @param elementValueKeys
     * @param columnMap
     * @return
     */
    private Set<String> verifyKeysAgainstTemplate(Set<String> elementValueKeys,
            Map<String, TemplateColumn> columnMap) {

        Set<String> verifiedKeys = new HashSet<String>();

        for (String key : elementValueKeys) {
            TemplateColumn column = columnMap.get(key);
            if (column == null) {
                log.warn("The following column does not exist in our template: "
                        + key);
            } else {
                verifiedKeys.add(key);
            }
        }

        return verifiedKeys;
    }

    /**
     * Fetches back a specific report section. This will be a list of objects
     * that extend HocElement.
     * 
     * @param <T>
     *            the generic type
     * @param project
     *            the project
     * @param reportSection
     *            the report section
     * @param adHocClass
     *            This must be the class (either AdHocElement or your own custom
     *            derivation)
     * @param tempHeader
     *            - For CSV, an optional header to specify header information.
     * @return the report section
     * @throws Exception
     *             the exception
     */
    public <T extends HocElement> List<T> getReportSection(
            IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
            ProjectPojo project, String reportSection, Format reportFormat,
            Class<T> adHocClass) throws Exception {

        List<T> returnRows = null;

        // TODO: Figure out translation mechanism from sheet to enum
        ReportSectionSelection sectionType = null;

        try {
            sectionType = ReportSectionSelection.valueOf(reportSection
                    .toUpperCase());
        } catch (Exception e) {
            String reportSectionNameUpper = reportSection.toUpperCase();
            if (reportSectionNameUpper.startsWith("FILE_DISCOVERY_PATTERN_MAT")) {
                sectionType = ReportSectionSelection.FILE_DISCOVERY_PATTERN_MATCHES_PENDING_IDENTIFICATION;
            } else if (reportSectionNameUpper
                    .startsWith("DEPENDENCIES_JAVA_IMPORT")) {
                sectionType = ReportSectionSelection.DEPENDENCIES_JAVA_IMPORT_STATEMENTS;
            } else if (reportSectionNameUpper
                    .startsWith("DEPENDENCIES_JAVA_PACKAGE")) {
                sectionType = ReportSectionSelection.DEPENDENCIES_JAVA_PACKAGE_STATEMENTS;
            } else if (reportSectionNameUpper
                    .startsWith("CODE_MATCHES_PENDING")) {
                sectionType = ReportSectionSelection.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION;
            } else {
                throw new Exception(
                        "Unable to determine section type for section key: "
                                + reportSection + "; " + e.getMessage());
            }
        }

        switch (sectionType) {
        case CODE_LABEL:
            log.warn("Code Label report section is not supported.");
            return null;
        }

        if (reportFormat == Format.HTML) {
            LineNumberReader lnr = getLineNumberReader(protexServerWrapper,
                    project, sectionType, reportSection);
            returnRows = ProtexReportHTMLProcessor.getRowsFromBuffer(
                    protexServerWrapper, false, lnr, adHocClass);
        } else if (reportFormat == Format.CSV) {
            ProtexReportCSVProcessor<T> csvProcessor = new ProtexReportCSVProcessor<T>(
                    sectionType.name());

            try {
                ReportPojo report = protexServerWrapper.getReportManager()
                        .generateAdHocProjectReportSingleSection(
                                project.getProjectKey(), sectionType,
                                sectionType.name(), reportSection,
                                reportFormat, false);

                returnRows = csvProcessor.getRows(report, adHocClass, getChunkingSize());

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return returnRows;
    }

    private static LineNumberReader getLineNumberReader(
            IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
            ProjectPojo project, ReportSectionSelection section,
            String sectionTitle) throws Exception {

        InputStream is = null;
        BufferedReader br = null;
        LineNumberReader lnr = null;
        Boolean includeTableOfContents = false;

        try {
            if (lnr == null) {
                IReportManager reportManager = protexServerWrapper
                        .getReportManager();
                String projectId = project.getProjectKey();
                String sectionName = section.name();
                ReportPojo report = reportManager
                        .generateAdHocProjectReportSingleSection(projectId,
                                section, sectionName, sectionTitle,
                                Format.HTML, includeTableOfContents);

                is = report.getFileContent().getInputStream();

                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                lnr = new LineNumberReader(br);
                lnr.readLine();
            }
        } catch (Exception e) {
            throw new Exception("Error reading Protex report: "
                    + e.getMessage());
        }
        return lnr;
    }
}
