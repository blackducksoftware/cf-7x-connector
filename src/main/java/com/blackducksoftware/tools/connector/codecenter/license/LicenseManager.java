package com.blackducksoftware.tools.connector.codecenter.license;

import com.blackducksoftware.sdk.codecenter.cola.data.License;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.LicenseNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;

public class LicenseManager implements ILicenseManager<LicensePojo> {
    private final ICodeCenterServerWrapper ccsw;

    public LicenseManager(ICodeCenterServerWrapper codeCenterServerWrapper) {
	ccsw = codeCenterServerWrapper;
    }

    @Override
    public LicensePojo getLicenseByName(String licenseName)
	    throws CommonFrameworkException {
	LicenseNameToken licToken = new LicenseNameToken();
	licToken.setName(licenseName);
	License lic;
	try {
	    lic = ccsw.getInternalApiWrapper().getColaApi()
		    .getLicense(licToken);
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
	    lic = ccsw.getInternalApiWrapper().getColaApi()
		    .getLicense(licToken);
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
