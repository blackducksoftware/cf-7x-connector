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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.google.common.base.Preconditions;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * Current version does not collect headers because our CSV files do not contain
 * any. Awaiting 7.1 Protex.
 * 
 * @author akamen
 * 
 * @param <T>
 */
public class AdHocCSVParser<T extends HocElement> extends AdHocParser<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    // This is the value in the report that we are looking for combined with
    // sectionName
    private final String HEADER_PREFIX = "header";

    // Overwrite the default maximum number of characters defined in the parser
    // settings (4096)
    private final int SETTINGS_MAX_CHARS_PER_COLUMN = 10000;// Integer.MAX_VALUE;
    public static Boolean CHUNKING = true;
    public static Boolean NOT_CHUNKING = false;

    protected Report report;
    protected CsvParser reader;
    protected BufferedReader inputReader;
    private final Class<T> hocElementClass;
    private String sectionName;

    // // For chunking
    // Create a default size of max value
    protected Long rowChunk = new Long(Long.MAX_VALUE);
    protected AdHocElement headerForChunking;

    private Boolean isParserFinished = false;

    public AdHocCSVParser(Class<T> hocElementClass, String sectionName) {
	this.hocElementClass = hocElementClass;
	this.sectionName = sectionName;
	Preconditions.checkNotNull(sectionName);
    }

    /**
     * Returns ALL the rows for a report filtered for that report section. The
     * section is specified within the Report object.
     * 
     * @param report
     * @param tempHeader
     * @return
     * @throws Exception
     */
    public List<T> getAllRowsFromReport(Report report) throws Exception {
	this.report = report;

	List<T> rows = new ArrayList<T>();
	try {
	    AdHocElement adHocHeader = parseHeader(AdHocCSVParser.NOT_CHUNKING);
	    rows = parseRows(adHocHeader, false);

	} catch (Exception e) {
	    log.error("General error in parsing CSV: " + e.getMessage());
	    throw new Exception(e);
	}

	return rows;
    }

    /**
     * Generates the header coordinates Requires at least Protex 7.1 to work
     * (https://jira/browse/PROTEX-18812)
     * 
     * @param chunking
     *            - not supported
     * @return
     * @throws Exception
     */
    protected AdHocElement parseHeader(boolean chunking) throws Exception {
	AdHocElement adHocHeader;
	try {
	    Preconditions.checkNotNull(report.getFileContent());

	    InputStream is = report.getFileContent().getInputStream();
	    inputReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	    // Initialize the reader (this will be used later by the main row
	    // processing
	    CsvParserSettings settings = new CsvParserSettings();
	    settings.setMaxCharsPerColumn(SETTINGS_MAX_CHARS_PER_COLUMN);
	    reader = new CsvParser(settings);
	    reader.beginParsing(inputReader);
	    adHocHeader = findHeader();

	    // If standard header...
	    if (!adHocHeader.isVertical()) {
		// Should not be empty...
		if (adHocHeader.isEmpty())
		    throw new CommonFrameworkException(
			    "No headers found for CSV sectio: " +  this.sectionName);
	    } else {
		/// Otherwise we are dealing with a vertical sheet, means the reader
		// has been spun through to the end.
		// Reset the reader.
		reader = null;
		is = report.getFileContent().getInputStream();
		inputReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		reader = new CsvParser(settings);
		reader.beginParsing(inputReader);
	    }
	} catch (Exception e) {
	    log.error("Unable to parse headers: " + e.getMessage());
	    throw new Exception(e);
	}

	return adHocHeader;
    }

    /**
     * Find the header in the CSV The expectation is that a row exists with the
     * first column value set to: <sectionName+header> Will iterate through all
     * rows until it finds something.
     * 
     * @param reader
     * @return
     * @throws Exception
     */
    private AdHocElement findHeader() throws Exception {
	AdHocElement headerRow = new AdHocElement();
	// Validate we have a good section name
	if (sectionName == null || sectionName.length() == 0)
	    throw new CommonFrameworkException(
		    "Header processing encountered an error, section name is missing!");

	String[] potentialRow = reader.parseNext();

	if (potentialRow == null)
	{
	    // This means we just looked through the entire sheet and did not find a header
	    // for our section!
	    headerRow.setIsVertical(true);
	    return headerRow;
	}

	String firstColumnValue = potentialRow[0];
	// Only interested in our section
	if (firstColumnValue.startsWith(sectionName)) {
	    // Only interested in the header
	    if (firstColumnValue.equalsIgnoreCase(sectionName + HEADER_PREFIX)) {

		// We want to start with index of 1 not 0, as the
		for (int column = 1; column < potentialRow.length; column++) {
		    String headerName = potentialRow[column];
		    headerRow.setCoordinate(column, headerName);
		}

		return headerRow;
	    }
	}

	return findHeader();
    }

    /**
     * Removes null cells which
     * 
     * @param potentialRow
     * @return
     */
    private String[] stripNulls(String[] potentialRow) {

	// We can create a newly resized row based on length because the nulls do not register
	String[] resizedRows = new String[potentialRow.length];
	String lastCell = potentialRow[potentialRow.length - 1];

	if (lastCell == null)
	{
	    resizedRows = Arrays.copyOf(potentialRow, resizedRows.length-1);
	    return resizedRows;
	}
	
	return potentialRow;
    }

    /**
     * Parses our weird CSV The notable things that need to be done 1) Find only
     * those that rows that have the first column match the name of the section
     * 2) Start the data on Column 1. 3) The name of the header is just the
     * position
     * 
     * @param header
     * @return
     * @throws Exception
     */
    protected List<T> parseRows(AdHocElement header, boolean chunk)
	    throws Exception {
	List<String[]> allRows = new ArrayList<String[]>();
	Long internalCounter = new Long(0);
	List<T> rows = new ArrayList<T>();
	convertSectionName();

	if (chunk) {
	    while (internalCounter < this.rowChunk) {
		String[] rec = reader.parseNext();

		// This is the equivalent of iterator.next() == null
		if (rec == null) {
		    log.debug("No more rows, closing CSV reader.");
		    setIsParserFinished(true);
		    reader.stopParsing();
		    break;
		}

		allRows.add(rec);
		internalCounter++;
	    }
	} else {
	    // allRows = reader.parseAll(inputReader);
	    String[] record;
	    while ((record = reader.parseNext()) != null) {
		allRows.add(record);
	    }
	    log.info("Parsed all rows, count: " + allRows.size());

	}

	try {
	    boolean isHeaderVertical = checkHeaderOrientation(header);
	    T adHocRow = generateNewInstance(hocElementClass);
	    // At this point, due to previous header processing we should be
	    // jumping directly into the content.

	    for (String[] record : allRows) {
		log.debug("Parsing row: " + internalCounter);

		// The first column is our section, it must match for us to
		// proceed

		String sectionKey = record[0];
		if (sectionKey != null) {
		    if (sectionKey.equalsIgnoreCase(this.sectionName)) {
			if (isHeaderVertical) {
			    // This assumes that there are two columns of data.
			    // First column with headers, second with values.
			    // If there is no coordinate, then this is a
			    // vertical sheet
			    String columnName = record[1];
			    String value = record[2];
			    log.debug("Set Vertical Pair : " + "  -  "
				    + columnName + " : " + value);
			    adHocRow.setPair(columnName, value);

			} else {
			    // For non-vertical sheets, create a fresh new
			    // row.
			    adHocRow = generateNewInstance(hocElementClass);

			    // i is column number
			    for (int i = 1; i < record.length; i++) {
				String columnName = header.getCoordinate(i);
				if (columnName != null) {
				    String value = record[i];

				    if (value == null) {
					value = "";
				    }

				    log.debug("Set Pair : " + "  -  "
					    + columnName + " : " + value);
				    adHocRow.setPair(columnName, value);
				} else {
				    log.debug("Coordinate number: " + i
					    + " does not contain any mappings.");
				}
			    }
			}
			adHocRow.setCounter(internalCounter.intValue());
			rows.add(adHocRow);
			internalCounter++;
		    } else {
			log.debug("Skipping row, with section key: "
				+ sectionKey);
			continue;
		    }
		}
	    } // All the rows

	} catch (Exception e) {
	    log.error("Error parsing rows: " + e.getMessage());
	    throw new Exception(e);
	}

	return rows;
    }

    /**
     * Determines what kind of header we have. Vertical header is unconventional
     * and would only happen if no coordinates are assigned.
     * 
     * @param header
     * @return
     */
    private boolean checkHeaderOrientation(AdHocElement header) {
	boolean vertical = true;

	int elementCount = header.getInternalValues().size();
	for (int i = 0; i < elementCount; i++) {
	    String value = header.getCoordinate(i);
	    if (value != null) {
		log.debug("Section name: " + sectionName + " is non vertical");
		vertical = false;
		break;
	    }
	}

	return vertical;
    }

    /**
     * Prepares the section name to match that which is used in the CSV report
     * Which means that we simply have to remove all underscores
     */
    private void convertSectionName() {
	this.sectionName = sectionName.replace("_", "");
	log.info("CSV parser using section name for matching: " + sectionName);

    }

    public Boolean isParserFinished() {
	return isParserFinished;
    }

    private void setIsParserFinished(Boolean isParserFinished) {
	this.isParserFinished = isParserFinished;
    }

}
