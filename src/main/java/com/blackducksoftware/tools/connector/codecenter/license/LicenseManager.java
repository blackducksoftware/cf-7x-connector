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
package com.blackducksoftware.tools.connector.codecenter.license;

import com.blackducksoftware.sdk.codecenter.cola.data.License;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;

public class LicenseManager implements ILicenseManager<LicensePojo> {
    private final CodeCenterAPIWrapper ccApiWrapper;

    public LicenseManager(CodeCenterAPIWrapper ccApiWrapper) {
	this.ccApiWrapper = ccApiWrapper;
    }

    @Override
    public LicensePojo getLicenseByName(String licenseName)
	    throws CommonFrameworkException {
	LicenseNameToken licToken = new LicenseNameToken();
	licToken.setName(licenseName);
	License lic;
	try {
	    lic = ccApiWrapper.getColaApi().getLicense(licToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error getting license "
		    + licenseName + " from Code Center: " + e.getMessage());
	}
	LicensePojo licPojo = createPojo(lic);
	return licPojo;
    }

    @Override
    public LicensePojo getLicenseById(String licenseId)
	    throws CommonFrameworkException {
	LicenseIdToken licToken = new LicenseIdToken();
	licToken.setId(licenseId);
	License lic;
	try {
	    lic = ccApiWrapper.getColaApi().getLicense(licToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting license for License ID " + licenseId
			    + " from Code Center: " + e.getMessage());
	}
	LicensePojo licPojo = createPojo(lic);
	return licPojo;
    }

    // private methods

    private LicensePojo createPojo(License lic) throws CommonFrameworkException {
	String licenseText;

	licenseText = lic.getText();

	LicensePojo licPojo = new LicensePojo(lic.getId().getId(), lic
		.getNameToken().getName(), licenseText);
	return licPojo;
    }
}
