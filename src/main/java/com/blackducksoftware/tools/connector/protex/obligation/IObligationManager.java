package com.blackducksoftware.tools.connector.protex.obligation;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public interface IObligationManager {

    List<ObligationPojo> getObligationsByLicenseId(String licenseId)
	    throws CommonFrameworkException;
}
