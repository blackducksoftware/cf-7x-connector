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
