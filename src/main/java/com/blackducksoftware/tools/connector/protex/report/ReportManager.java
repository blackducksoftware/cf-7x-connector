package com.blackducksoftware.tools.connector.protex.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.ReportSection;
import com.blackducksoftware.sdk.protex.report.ReportTemplateRequest;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;

public class ReportManager implements IReportManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final ProtexAPIWrapper ccApiWrapper;

    public ReportManager(ProtexAPIWrapper ccApiWrapper) {
	this.ccApiWrapper = ccApiWrapper;
    }

    @Override
    public ReportPojo generateAdHocProjectReportSingleSection(String projectId,
	    ReportSectionSelection pojoSection, String name,
	    String sectionTitle, Format format, boolean includeTableOfContents)
	    throws CommonFrameworkException {

	log.info("Generating " + sectionTitle + " report");
	ReportTemplateRequest reportReq = new ReportTemplateRequest();

	ReportSection ccSection = new ReportSection();
	ccSection.setSectionType(pojoSection.getCcType());
	ccSection.setLabel(sectionTitle);
	reportReq.getSections().add(ccSection);

	reportReq.setTitle(name);
	reportReq.setName(name);

	Report ccReport;
	try {
	    ccReport = ccApiWrapper.getReportApi().generateAdHocProjectReport(
		    projectId, reportReq, format.getCcFormat(), false);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error generating Protex report " + name
			    + " for Project ID " + projectId + ": "
			    + e.getMessage());
	}

	return toPojo(ccReport);
    }

    private ReportPojo toPojo(Report ccReport) {
	ReportPojo reportPojo = new ReportPojo(ccReport.getFileContent(),
		ccReport.getFileName());

	return reportPojo;
    }
}
