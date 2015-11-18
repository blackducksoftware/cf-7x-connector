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
}
