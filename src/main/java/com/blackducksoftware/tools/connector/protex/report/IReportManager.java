package com.blackducksoftware.tools.connector.protex.report;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Generate a Protex report.
 *
 * @author sbillings
 *
 */
public interface IReportManager {
    ReportPojo generateAdHocProjectReportSingleSection(String projectId,
	    ReportSectionSelection section, String name, String sectionTitle,
	    Format format, boolean includeTableOfContents)
	    throws CommonFrameworkException;
}
