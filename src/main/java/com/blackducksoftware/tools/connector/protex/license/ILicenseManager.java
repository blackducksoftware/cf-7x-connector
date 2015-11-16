package com.blackducksoftware.tools.connector.protex.license;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Provide license data retrieved from Protex.
 *
 * @author sbillings
 *
 */
public interface ILicenseManager {
    /**
     * Get license by name.
     *
     * @param licenseName
     * @return
     * @throws CommonFrameworkException
     */
    LicensePojo getLicenseByName(String licenseName)
	    throws CommonFrameworkException;

    /**
     * Get license by ID.
     *
     * @param licenseId
     * @return
     * @throws CommonFrameworkException
     */
    LicensePojo getLicenseById(String licenseId)
	    throws CommonFrameworkException;
}
