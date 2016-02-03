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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.sdk.codecenter.cola.data.LicenseSummary;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Utility methods for licenses.
 * 
 * @author sbillings
 * 
 */
public class Licenses {
    public static List<LicensePojo> valueOf(
            ILicenseManager<LicensePojo> licMgr,
            List<LicenseSummary> sdkLicenses) throws CommonFrameworkException {
        List<LicensePojo> licenses = new ArrayList<>(sdkLicenses.size());
        for (LicenseSummary sdkLicense : sdkLicenses) {
            LicensePojo license = licMgr.getLicenseById(sdkLicense.getId()
                    .getId());
            licenses.add(license);
        }
        return licenses;
    }

    public static LicensePojo valueOf(ILicenseManager<LicensePojo> licMgr, String licenseId) throws CommonFrameworkException
    {
        LicensePojo license = licMgr.getLicenseById(licenseId);
        return license;
    }
}
