package com.blackducksoftware.tools.connector.codecenter.application;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Provides a higher level of abstraction for accessing Code Center
 * applications.
 *
 * The objects returned are POJOs, not SDK objects.
 *
 * @author sbillings
 *
 */
public interface IApplicationManager {
    ApplicationPojo getApplicationByNameVersion(String name, String version)
	    throws CommonFrameworkException;

    ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException;
}
