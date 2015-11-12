package com.blackducksoftware.tools.commonframework.standard.codecenter.application;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public interface IApplicationManager {
    ApplicationPojo getApplicationByNameVersion(String name, String version)
	    throws CommonFrameworkException;

    ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException;
}
