package com.blackducksoftware.tools.commonframework.connector.protex.license;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public interface ILicenseManager {
    LicensePojo getLicenseByName(String licenseName)
	    throws CommonFrameworkException;

    LicensePojo getLicenseById(String licenseId)
	    throws CommonFrameworkException;
}
