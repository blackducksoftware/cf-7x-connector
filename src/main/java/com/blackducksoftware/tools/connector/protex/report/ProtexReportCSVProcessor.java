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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.google.common.base.Preconditions;

/**
 * Parser for the CSV format introduced in the new 7.x SDK
 * 
 * @author akamen
 * @param <T>
 * 
 */
public class ProtexReportCSVProcessor<T extends HocElement> {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    // Internal required members
    private final String sectionName;

    private AdHocElement tempHeader;

    // For chunking
    private AdHocCSVChunkingParser<T> limitedParser;

    private Boolean isFinished = false;

    /**
     * Section name is required.
     * 
     * @param sectionName
     */
    public ProtexReportCSVProcessor(String sectionName) {
        this.sectionName = sectionName;
        Preconditions.checkNotNull(sectionName);
    }

    /**
     * Basic entry point for the user to get all the rows for a particular
     * report. Invokes the CSV adhoc parser.
     * 
     * @param report
     *            the report
     * @param hocElementClass
     *            the element class
     * @return all the rows for a particular report
     * @throws Exception
     */
    public List<T> getRows(ReportPojo report, Class<T> hocElementClass)
            throws Exception {
        List<T> parsedRows = new ArrayList<T>();
        try {
            log.info("Getting CSV report for section: " + sectionName);
            AdHocCSVParser<T> parser = new AdHocCSVParser<T>(hocElementClass,
                    sectionName);
            parsedRows = parser.getAllRowsFromReport(report);
            log.info("CSV parsed, found following number of rows: "
                    + parsedRows.size());
        } catch (Exception e) {
            throw e;
        }

        return parsedRows;
    }

    /**
     * Basic entry point for the user to get all the rows for a particular
     * report. The chunkingSize parameter is pre-defined in the configuration
     * and indicates the chunk size for row processing;
     * No 'chunking' will be applied if chunkingSize is set to 0.
     * Invokes the CSV adhoc parser.
     * 
     * @param report
     *            the report
     * @param hocElementClass
     *            the element class
     * @param chunkingSize
     *            The number of SDK lines you want processed at any given time
     * @return the rows for a particular report
     * @throws Exception
     */
    public List<T> getRows(ReportPojo report, Class<T> hocElementClass, Integer chunkingSize)
            throws Exception {

        if (chunkingSize == 0) {
            return getRows(report, hocElementClass);
        }
        else {
            int totalCount = 0;
            List<T> allRows = new ArrayList<T>();
            while (!isFinished()) {
                List<T> rows = getRowChunk(report, hocElementClass, chunkingSize);
                totalCount += rows.size();
                allRows.addAll(rows);
                log.info("CSV chunk parsed: " + rows.size() + "; Total rows parsed: " + totalCount);
            }
            return allRows;
        }
    }

    /**
     * Returns a specific amount of rows
     * 
     * @param report
     * @param hocElementClass
     * @param rowChunk
     * @return
     * @throws Exception
     */
    public List<T> getRowChunk(ReportPojo report, Class<T> hocElementClass,
            Integer rowChunk) throws Exception {

        List<T> parsedRows = new ArrayList<T>();

        try {
            log.info("Getting limited CSV report for section: " + sectionName);
            if (limitedParser == null) {
                limitedParser = new AdHocCSVChunkingParser<T>(hocElementClass,
                        sectionName, report, rowChunk);
            }
            parsedRows = limitedParser.getLimitedRowsFromReport(report);

            if (limitedParser.isParserFinished()) {
                setIsFinished(true);
            }

            log.info("CSV partially parsed, found following number of rows: "
                    + parsedRows.size());
        } catch (Exception e) {
            throw e;
        }

        return parsedRows;

    }

    public Boolean isFinished() {
        return isFinished;
    }

    private void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

}
