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
