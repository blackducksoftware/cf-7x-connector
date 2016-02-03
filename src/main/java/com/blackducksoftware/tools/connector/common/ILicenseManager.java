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
