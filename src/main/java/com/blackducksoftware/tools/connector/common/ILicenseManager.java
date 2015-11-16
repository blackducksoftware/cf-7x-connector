package com.blackducksoftware.tools.connector.common;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Provide license data retrieved from Protex.
 *
 * @author sbillings
 *
 */
public interface ILicenseManager<T extends LicensePojo> {
    /**
     * Get license by name.
     *
     * @param licenseName
     * @return
     * @throws CommonFrameworkException
     */
    T getLicenseByName(String licenseName) throws CommonFrameworkException;

    /**
     * Get license by ID.
     *
     * @param licenseId
     * @return
     * @throws CommonFrameworkException
     */
    T getLicenseById(String licenseId) throws CommonFrameworkException;
}
