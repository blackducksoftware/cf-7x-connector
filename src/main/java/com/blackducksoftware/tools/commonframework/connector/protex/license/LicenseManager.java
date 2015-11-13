package com.blackducksoftware.tools.commonframework.connector.protex.license;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.tools.commonframework.connector.protex.IProtexServerWrapper;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;

public class LicenseManager implements ILicenseManager {
    private final IProtexServerWrapper<ProtexProjectPojo> psw;
    private final Map<String, GlobalLicense> licenseByNameCache = new HashMap<>();
    private final Map<String, GlobalLicense> licenseByIdCache = new HashMap<>();

    public LicenseManager(IProtexServerWrapper<ProtexProjectPojo> psw) {
	this.psw = psw;
    }

    // TODO comments
    @Override
    public LicensePojo getLicenseByName(String licenseName)
	    throws CommonFrameworkException {

	if (licenseByNameCache.containsKey(licenseName)) {
	    return createPojo(licenseByNameCache.get(licenseName));
	}

	GlobalLicense globalLicense;
	try {
	    globalLicense = psw.getInternalApiWrapper().getLicenseApi()
		    .getLicenseByName(licenseName);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting license for license name " + licenseName
			    + ": " + e.getMessage());
	}

	addToCache(globalLicense);
	LicensePojo licPojo = createPojo(globalLicense);
	return licPojo;
    }

    @Override
    public LicensePojo getLicenseById(String licenseId)
	    throws CommonFrameworkException {

	if (licenseByIdCache.containsKey(licenseId)) {
	    return createPojo(licenseByIdCache.get(licenseId));
	}

	GlobalLicense globalLicense;
	try {
	    globalLicense = psw.getInternalApiWrapper().getLicenseApi()
		    .getLicenseById(licenseId);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting license for license ID " + licenseId + ": "
			    + e.getMessage());
	}

	addToCache(globalLicense);
	return createPojo(globalLicense);
    }

    private void addToCache(GlobalLicense lic) {
	licenseByIdCache.put(lic.getLicenseId(), lic);
	licenseByNameCache.put(lic.getName(), lic);
    }

    private LicensePojo createPojo(GlobalLicense lic)
	    throws CommonFrameworkException {
	LicensePojo licPojo = new LicensePojo(lic.getLicenseId(),
		lic.getName(), lic.getComment(), lic.getExplanation(),
		lic.getSuffix(), LicensePojo.toApprovalState(lic
			.getApprovalState()));
	return licPojo;
    }

}
