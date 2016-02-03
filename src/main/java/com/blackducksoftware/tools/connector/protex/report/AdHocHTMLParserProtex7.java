/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.standard.protex.report.AdHocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;
import com.blackducksoftware.tools.commonframework.standard.protex.report.IAdHocParser;
import com.blackducksoftware.tools.commonframework.standard.protex.report.ParserUtils;

/**
 * The main parsing class for the AdHoc SDK Report sections.
 *
 * @author sbillings
 * @param <T>
 *            the generic type
 */
public class AdHocHTMLParserProtex7<T extends HocElement> extends
	AdHocParser<T> implements IAdHocParser<T> {

    /** The log. */
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private static final String NEW_LINE = System.getProperty("line.separator");

    /** The raw columns. */
    // parent > child: child elements that descend directly from parent, e.g.
    // div.content > p finds p elements; and body > * finds the direct children
    // of the body tag
    // http://jsoup.org/cookbook/extracting-data/selector-syntax
    private static final String SELECTOR_RAW_COLUMNS_REPORTTABLE_THEAD = "table[class=reportTable] > thead";
    private static final String SELECTOR_RAW_COLUMNS_REPORTTABLE_BOM_THEAD = "table[class=bomTable] > thead";
    private static final String SELECTOR_RAW_COLUMNS_LABELEDTABLE_THEAD = "table[class=labeledTable] > thead";

    /** The raw rows. */
    private static final String SELECTOR_RAW_ROWS_REPORTTABLE_TBODY = "table[class=reportTable] > tbody";
    private static final String SELECTOR_RAW_ROWS_REPORTTABLE_BOM_TBODY = "table[class=bomTable] > tbody";
    private static final String SELECTOR_RAW_ROWS_LABELEDTABLE_TBODY = "table[class=labeledTable] > tbody";

    // This keeps track of which style of HTML we are parsing.
    private boolean parsingLabeledTable = false; // false = parsing reportTable

    /** The table header. */
    private static final String SELECTOR_TAG_TABLE_TH = "th";
    private static final String SELECTOR_TAG__TABLE_SUBLABEL = "td[id=labeledTableSubLabel]";

    /** The table data. */
    private static final String SELECTOR_TAG_TABLE_TD = "td";

    /** The table row. */
    private static final String SELECTOR_TAG_TABLE_TR = "tr";

    private boolean nonConforming = false;
    private boolean bomTable = false;
    private String selectorHeader = "";
    private String selectorRow = "";

    private static final int BOGUS_COUNTER = 7;

    /**
     * Instantiates a new ad hoc parser.
     */
    public AdHocHTMLParserProtex7() {
    }

    /**
     * Parses the headers from doc.
     *
     * @param doc
     *            the doc
     * @param targetSectionIndex
     * @return the hoc element
     * @throws Exception
     *             the exception
     */
    @Override
    public HocElement parseHeadersFromDoc(Document doc, int targetSectionIndex)
	    throws Exception {
	try {
	    HocElement adHocHeader = parseHeaders(doc, targetSectionIndex);
	    adHocHeader.setDoc(doc, selectorHeader);
	    return adHocHeader;
	} catch (Exception e) {
	    log.error("Error while parsing HTML: " + e.getMessage());
	    throw e;
	}
    }

    /**
     * Parse report section table rows from the Protex report, returning the
     * data in a list of HocElements. nonConforming = report sections of the
     * form: Label: value. Normal (conforming) = report sections that contain
     * columns of data, each with a header (label) at the top (first row).
     *
     * @param doc
     *            the doc
     * @param adHocHeader
     *            the header
     * @param returnRawHtml
     *            the raw html indicator
     * @param hocElementClass
     *            the hoc element class
     * @param targetSectionIndex
     *            the section index
     * @return the array list of the parsed rows
     * @throws Exception
     */
    @Override
    public ArrayList<T> parseRows(Document doc, HocElement adHocHeader,
	    boolean returnRawHtml, Class<T> hocElementClass,
	    int targetSectionIndex) throws Exception {
	try {
	    // Use the counter to make every item unique, this is important for
	    // when parsing happens on the RGT side as some items can be
	    // otherwise identical!
	    Integer counter = 0;
	    ArrayList<T> rowElements = new ArrayList<T>();
	    Elements allReportSections = doc.body().select(selectorRow);
	    if (!nonConforming) {
		parseConformingSection(adHocHeader, returnRawHtml,
			hocElementClass, targetSectionIndex, rowElements,
			counter, allReportSections);
	    } else {
		parseNonConformingSection(returnRawHtml, hocElementClass,
			targetSectionIndex, rowElements, counter,
			allReportSections);
	    }

	    log.debug("Number of rows parsed: " + rowElements.size());
	    return rowElements;

	} catch (Exception e) {
	    log.error("Error parsing rows: " + e.getMessage());
	    throw e;
	}
    }

    /**
     * Parse a "non-conforming" report section. Non-conforming = The data label
     * for each data value is in the cell to the left of the value.
     *
     * @param returnRawHtml
     * @param hocElementClass
     * @param targetSectionIndex
     * @param rowElements
     * @param counter
     * @param allReportSections
     * @throws Exception
     */
    private void parseNonConformingSection(boolean returnRawHtml,
	    Class<T> hocElementClass, int targetSectionIndex,
	    ArrayList<T> rowElements, Integer counter,
	    Elements allReportSections) throws Exception {

	T adHocRow = generateNewInstance(hocElementClass);
	Element reportSection = allReportSections.get(targetSectionIndex);
	Elements rows = reportSection.select(SELECTOR_TAG_TABLE_TR);

	log.info("Parsing NonConforming Section. row count = " + rows.size());

	for (Element row : rows) {
	    Elements headerElements = row.select(SELECTOR_TAG_TABLE_TH);
	    Elements headerElementsSub = row
		    .select(SELECTOR_TAG__TABLE_SUBLABEL);
	    Elements valueElements = row.select(SELECTOR_TAG_TABLE_TD);

	    String elementsText = headerElements.text();
	    String elementsTextSub = headerElementsSub.text();
	    String elementsValue = valueElements.text();

	    if (!elementsText.trim().isEmpty()) {
		if (!returnRawHtml) {
		    elementsValue = ParserUtils.decode(elementsValue);
		}

		adHocRow.setPair(elementsText, elementsValue);
		log.debug("Set Pair: " + elementsText + " : " + elementsValue);
	    } else if (!elementsTextSub.trim().isEmpty()) {
		// When parsing sub-elements on analysis summary: values are in
		// the 2nd <td> element on the row
		if (valueElements.size() > 1) {
		    elementsValue = valueElements.get(1).text(); // get the
								 // value in the
								 // 2nd <td>
								 // element
		}
		if (!returnRawHtml) {
		    elementsValue = ParserUtils.decode(elementsValue);
		}
		adHocRow.setPair(elementsTextSub.trim(), elementsValue);
		log.debug("Set Pair: " + elementsText + " : " + elementsValue);
	    }
	} // row

	counter++;
	rowElements.add(adHocRow);
    }

    /**
     * Parse a "conforming" report section. "Conforming" = columns of data with
     * column headers in first row.
     *
     * @param adHocHeader
     * @param returnRawHtml
     * @param hocElementClass
     * @param targetSectionIndex
     * @param rowElements
     * @param counter
     * @param allReportSections
     * @throws Exception
     */
    private void parseConformingSection(HocElement adHocHeader,
	    boolean returnRawHtml, Class<T> hocElementClass,
	    int targetSectionIndex, ArrayList<T> rowElements, Integer counter,
	    Elements allReportSections) throws Exception {

	int adHocHeaderSize = adHocHeader.getSize();
	Element reportSection = allReportSections.get(targetSectionIndex);
	Elements rows = reportSection.select(SELECTOR_TAG_TABLE_TR);
	log.info("Parsing Conforming Section. adHocHeader size = "
		+ adHocHeaderSize + " ; row count = " + rows.size());

	for (Element row : rows) {
	    T adHocRow = generateNewInstance(hocElementClass);
	    adHocRow.setCounter(counter);
	    Elements rowCells = row.select(SELECTOR_TAG_TABLE_TD);

	    int position = 1;
	    for (Element rowCell : rowCells) {
		if (adHocHeaderSize > 0) {
		    String headerKey = adHocHeader.getCoordinate(position);
		    String rowValue = rowCell.html().toString();
		    if (returnRawHtml) {
			// This grabs the inner HTML (thus stripping out table
			// data blocks) and stuffs it raw.
			rowValue = ParserUtils.decode(rowValue);
		    } else {
			rowValue = ParserUtils.decode(rowCell.text());
		    }
		    adHocRow.setPair(headerKey, rowValue);
		    log.debug("Set Pair : " + "  -  " + headerKey + " : "
			    + rowCell.text());
		}
		position++;
	    } // row cell

	    if (rowContainsData(adHocRow, adHocHeader)) {
		rowElements.add(adHocRow);
	    }
	    counter++;
	} // row
    }

    /**
     * This checks the integrity of the row and makes sure there is data instead
     * of blank values. TODO: This has the desired (according to auditors)
     * effect of weeding out occasional bogus data rows that protex puts into
     * the Identified Files section (a row with all columns empty except
     * File/Folder), but has the undesired effect of omitting data for sections
     * with more than BOGUS_COUNTER+1 columns when the only contain a message
     * saying there is not data. For example: If the Identification Audit Trail
     * section has no data, it contains only the message
     * "The project does not have any audited identifications." and a bunch of
     * empty columns. This method returns false for that, causing the template
     * sheet to be included in the report as-is, which seems less than ideal.
     *
     * @param adHocRow
     *            the ad hoc row
     * @param adHocHeader
     *            the ad hoc header
     * @return true, if successful
     */
    private boolean rowContainsData(HocElement adHocRow, HocElement adHocHeader) {
	Collection<String> headerValues = adHocHeader.getInternalValues();
	Iterator<String> it = headerValues.iterator();
	int bogusCounter = 0;

	while (it.hasNext()) {
	    String key = it.next();
	    String value = adHocRow.getValue(key);
	    if (value == null || value.length() == 0) {
		bogusCounter++;
	    }
	}

	if (bogusCounter > BOGUS_COUNTER) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Parses the header row of the HTML document. If a header is missing then a
     * fatal exception is generated.
     *
     * @param doc
     *            the doc
     * @param targetSectionIndex
     * @return the parsed header element
     * @throws Exception
     *             the exception
     */
    private AdHocElement parseHeaders(Document doc, int targetSectionIndex)
	    throws Exception {
	AdHocElement adHocHeader = new AdHocElement();
	try {
	    /*
	     * Protex 7 reports: Summary section (perhaps all non-conforming
	     * sections): Headers are found in a table with class=labeledTable
	     * Other sections: Headers are found in a table with
	     * class=reportTable.
	     * 
	     * Try reportTable first. If the section header isn't found, then
	     * try labeledTable Keep track of which we're currently parsing.
	     */
	    Elements reportSections = doc.body().select(
		    SELECTOR_RAW_COLUMNS_REPORTTABLE_THEAD);
	    Elements sectionsReportTableHead = doc.body().select(
		    SELECTOR_RAW_COLUMNS_REPORTTABLE_THEAD);
	    Elements sectionsColumsLabeledTable = doc.body().select(
		    SELECTOR_RAW_COLUMNS_LABELEDTABLE_THEAD);
	    Elements sectionsRowsLabeledTable = doc.body().select(
		    SELECTOR_RAW_ROWS_LABELEDTABLE_TBODY);
	    Elements sectionsBOMTable = doc.body().select(
		    SELECTOR_RAW_ROWS_REPORTTABLE_BOM_TBODY);

	    // Check if BOM or ReportTable sections (not applicable to 'Summary'
	    // and 'Analysis Summary' sections
	    if (sectionsBOMTable.size() != 0
		    || sectionsReportTableHead.size() != 0) {
		parsingLabeledTable = false;
		nonConforming = false;
		String selectorTag = SELECTOR_RAW_COLUMNS_REPORTTABLE_BOM_THEAD;
		// Check for BOM section
		bomTable = (sectionsBOMTable.size() != 0);
		if (bomTable) {
		    reportSections = sectionsBOMTable;
		    // Set the selector tag for header and row
		    selectorHeader = SELECTOR_RAW_COLUMNS_REPORTTABLE_BOM_THEAD;
		    selectorRow = SELECTOR_RAW_ROWS_REPORTTABLE_BOM_TBODY;
		} else {
		    reportSections = sectionsReportTableHead;
		    // Set the selector tag for header and row
		    selectorHeader = SELECTOR_RAW_COLUMNS_REPORTTABLE_THEAD;
		    selectorRow = SELECTOR_RAW_ROWS_REPORTTABLE_TBODY;
		    selectorTag = SELECTOR_RAW_COLUMNS_REPORTTABLE_THEAD;
		}

		Element tableThead = doc.select(selectorTag).first();
		Elements tableTheadRows = tableThead
			.select(SELECTOR_TAG_TABLE_TR);
		int k = 1;
		for (Element th : tableTheadRows.get(1).children()) {
		    // System.out.println(th.text());
		    String header = th.text();
		    adHocHeader.setCoordinate(k++, header);
		    log.debug("Header column: " + header);
		}
	    }
	    // Check for 'Summary' and 'Analysis Summary' sections
	    else if (sectionsColumsLabeledTable.size() != 0
		    || sectionsRowsLabeledTable.size() != 0) {
		parsingLabeledTable = true;
		nonConforming = true;
		reportSections = sectionsRowsLabeledTable;
		selectorHeader = SELECTOR_RAW_ROWS_LABELEDTABLE_TBODY;
		selectorRow = SELECTOR_RAW_ROWS_LABELEDTABLE_TBODY;

		Element reportSection = reportSections.get(targetSectionIndex);
		Elements verticalColumns = reportSection
			.select(SELECTOR_TAG_TABLE_TH);

		// Start with position 0 as it is a bogus header
		// When we do the matching of the rows, we will start at 1.
		int position = 0;
		for (Element vColumn : verticalColumns) {
		    adHocHeader.setCoordinate(position, vColumn.text());
		    log.debug("Header column: " + vColumn.text());
		    position++;
		}
	    } else {
		log.error("ERROR parsing headers !!!");
		throw new Exception();
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append(NEW_LINE);
	    sb.append("Parsing Headers:");
	    sb.append(NEW_LINE);
	    sb.append("LabeledTable - ");
	    sb.append(parsingLabeledTable);
	    sb.append(NEW_LINE);
	    sb.append("NON-Conforming - ");
	    sb.append(nonConforming);
	    sb.append(NEW_LINE);
	    sb.append("BOM Table - ");
	    sb.append(bomTable);
	    sb.append(NEW_LINE);
	    sb.append("SELECTOR Header -  ");
	    sb.append(selectorHeader);
	    sb.append(NEW_LINE);
	    sb.append("SELECTOR Row: ");
	    sb.append(selectorRow);
	    sb.append(NEW_LINE);
	    log.debug(sb.toString());
	} catch (Exception e) {
	    log.error("Error parsing headers " + e.getMessage());
	    throw e;
	}
	return adHocHeader;
    }

}
