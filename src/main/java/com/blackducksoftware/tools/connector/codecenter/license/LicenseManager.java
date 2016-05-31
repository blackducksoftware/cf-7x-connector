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
