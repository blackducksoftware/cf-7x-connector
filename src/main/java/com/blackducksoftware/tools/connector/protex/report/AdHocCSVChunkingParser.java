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

import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.google.common.base.Preconditions;

/**
 * Extension of the main parser for chunking purposes only.
 * 
 * @author akamen
 * 
 * @param <T>
 */
public class AdHocCSVChunkingParser<T extends HocElement> extends
        AdHocCSVParser<T> {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    /**
     * 
     * @param hocElementClass
     * @param sectionName
     * @param report
     * @param rowChunk
     * @throws Exception
     */
    public AdHocCSVChunkingParser(Class<T> hocElementClass, String sectionName,
            ReportPojo report, long rowChunk) throws Exception {

        super(hocElementClass, sectionName);
        super.rowChunk = rowChunk;
        this.report = report;

        Preconditions.checkNotNull(report);

        // Instantiate the reader
        try {
            headerForChunking = parseHeader(AdHocCSVParser.CHUNKING);

        } catch (Exception e) {
            log.error("Unable to created a limited parser: " + e.getMessage());
            throw new Exception(e);
        }

    }

    /**
     * Limited rows, up until it hits rowChunk
     * 
     * @param report
     * @return
     * @throws Exception
     */
    public List<T> getLimitedRowsFromReport(ReportPojo report) throws Exception {
        this.report = report;

        List<T> rows = new ArrayList<T>();
        try {
            rows = parseRows(headerForChunking, AdHocCSVParser.CHUNKING);
        } catch (Exception e) {
            log.error("General error in parsing CSV.");
            throw new Exception(e);
        }

        return rows;
    }

}
