package com.blackducksoftware.tools.connector.protex.report;

import com.blackducksoftware.sdk.protex.report.ReportFormat;

public enum Format {
    CSV(ReportFormat.CSV), HTML(ReportFormat.HTML), MS_WORD(
	    ReportFormat.MS_WORD), ODF_SPREADSHEET(ReportFormat.ODF_SPREADSHEET), ODF_TEXT(
	    ReportFormat.ODF_TEXT), XLS(ReportFormat.XLS);

    private final ReportFormat ccFormat;

    private Format(ReportFormat ccFormat) {
	this.ccFormat = ccFormat;
    }

    public boolean isEquivalent(ReportFormat otherCcFormat) {
	return ccFormat.equals(otherCcFormat);
    }

    ReportFormat getCcFormat() {
	return ccFormat;
    }

}
