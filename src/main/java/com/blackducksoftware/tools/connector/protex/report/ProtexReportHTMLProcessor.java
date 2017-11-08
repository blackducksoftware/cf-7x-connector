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

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;

/**
 * Parser for Protex HTML reports, used to extract report data from them.
 *
 * @author sbillings
 *
 */
public class ProtexReportHTMLProcessor {
    private static HocElement header;
    private static Document doc;

    /**
     * Gets the rows from buffer.
     *
     * @param <T>
     *            the generic type
     * @param returnRawHtml
     *            the return raw html
     * @param lnr
     *            the lnr
     * @param hocElementClass
     *            the hoc element class
     * @return the rows from buffer
     * @throws Exception
     *             the exception
     */
    public static <T extends HocElement> List<T> getRowsFromBuffer(
	    IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
	    boolean returnRawHtml, LineNumberReader lnr,
	    Class<T> hocElementClass) throws Exception {
	List<T> parsedRows = new ArrayList<T>();

	try {
	    StringBuffer buf = new StringBuffer();
	    String line = "";
	    String htmlBody = buf.toString();

	    while ((line = lnr.readLine()) != null) {
		buf.append(line);
	    }

	    htmlBody = buf.toString();

	    /**
	     * HACK TIME. Unfortunately while this looks messy, it is relatively
	     * harmless. Because JSoup insists on stripping out HTML tags for
	     * chunks of html it invariably breaks our entire parsing scheme. By
	     * wrapping each block in a known table tag we ensure that nothing
	     * gets stripped out. --AK
	     */
	    htmlBody = "<tbody><table class='reportTable'>" + htmlBody
		    + "</table></tbody>";

	    // Parse the number of HTML lines that we were able to get.
	    Document doc = Jsoup.parseBodyFragment(htmlBody);

	    buf = null;

	    AdHocHTMLParser<T> adHocParser = new AdHocHTMLParser<T>(
		    protexServerWrapper);

	    // To enable this method to be called multiple times for different
	    // report sections, the header
	    // is initialized each time.
	    // TODO: If we develop the ability to generate multiple sections in
	    // one shot, might be able to go
	    // back to only initializing header if it's null
	    header = adHocParser.parseHeadersFromDoc(doc, 0);

	    /**
	     * Sometimes we want to keep the HTML intact, so lets stuff the
	     * datablock with the raw html and send it.
	     */
	    if (returnRawHtml) {
		parsedRows = adHocParser.parseRows(doc, header, returnRawHtml,
			hocElementClass, 0);
		return parsedRows;
	    }

	    parsedRows = adHocParser.parseRows(doc, header, false,
		    hocElementClass, 0);

	} catch (Exception e) {
	    throw new Exception("Error processing file chunk!", e);
	} finally {

	}
	return parsedRows;
    }

    /**
     * Initialize the parser for parsing multi-section reports.
     *
     * @param lnr
     * @param hocElementClass
     * @throws Exception
     */
    public static <T extends HocElement> void init(LineNumberReader lnr,
	    Class<T> hocElementClass) throws Exception {
	try {
	    String htmlBody = getHtmlBody(lnr);

	    /**
	     * HACK TIME. Unfortunately while this looks messy, it is relatively
	     * harmless. Because JSoup insists on stripping out HTML tags for
	     * chunks of html it invariably breaks our entire parsing scheme. By
	     * wrapping each block in a known table tag we ensure that nothing
	     * gets stripped out. --AK
	     */
	    htmlBody = "<tbody><table class='reportTable'>" + htmlBody
		    + "</table></tbody>";

	    // Parse the number of HTML lines that we were able to get.
	    doc = Jsoup.parseBodyFragment(htmlBody);

	} catch (Exception e) {
	    throw new Exception("Error processing file chunk! "
		    + e.getMessage(), e);
	} finally {

	}

    }

    /**
     * Get a given report section's worth of data (while parsing multi-section
     * reports).
     *
     * @param hocElementClass
     * @param targetSectionIndex
     * @param returnRawHtml
     * @return
     * @throws Exception
     */
    public static <T extends HocElement> List<T> getReportSectionData(
	    IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
	    Class<T> hocElementClass, int targetSectionIndex,
	    boolean returnRawHtml) throws Exception {
	AdHocHTMLParser<T> parser = new AdHocHTMLParser<T>(protexServerWrapper);

	// To enable this method to be called multiple times for different
	// report sections, the header
	// is initialized each time.
	header = parser.parseHeadersFromDoc(doc, targetSectionIndex);

	List<T> parsedRows = new ArrayList<T>();

	/**
	 * Sometimes we want to keep the HTML intact, so lets stuff the
	 * datablock with the raw html and send it.
	 */
	if (returnRawHtml) {
	    parsedRows = parser.parseRows(doc, header, returnRawHtml,
		    hocElementClass, targetSectionIndex);
	    return parsedRows;
	}

	parsedRows = parser.parseRows(doc, header, false, hocElementClass,
		targetSectionIndex);
	return parsedRows;
    }

    private static String getHtmlBody(LineNumberReader lnr) throws IOException {
	StringBuffer buf = new StringBuffer();
	String line = "";
	String htmlBody = buf.toString();

	while ((line = lnr.readLine()) != null) {
	    buf.append(line);
	}

	htmlBody = buf.toString();
	return htmlBody;
    }

}
