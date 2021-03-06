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
