package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.common.ComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;

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
    /**
     * Get an application by name/version.
     *
     * @param name
     * @param version
     * @return
     * @throws CommonFrameworkException
     */
    ApplicationPojo getApplicationByNameVersion(String name, String version)
	    throws CommonFrameworkException;

    /**
     * Get an application by ID.
     *
     * @param id
     * @return
     * @throws CommonFrameworkException
     */
    ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException;

    /**
     * Get an application's requests, by application ID.
     *
     * @param appId
     * @return
     * @throws CommonFrameworkException
     */
    List<RequestPojo> getRequestsByAppId(String appId)
	    throws CommonFrameworkException;

    /**
     * Get an application's components recursively, by application ID.
     *
     * For each component (at any level in the tree) that is an application
     * published as a component, returns the components pointed to the published
     * application's requests instead of the published application itself.
     *
     * @param appId
     * @return
     * @throws CommonFrameworkException
     */
    List<ComponentPojo> getComponentsRecursivelyByAppId(String appId)
	    throws CommonFrameworkException;

    /**
     * Get an application's components recursively, by application ID, limited
     * to components with an approval status that is in the provided list.
     *
     * For each component (at any level in the tree) that is an application
     * published as a component, returns the approved components pointed to the
     * published application's requests instead of the published application
     * itself.
     *
     * @param appId
     * @param limitToApprovalStatusValues
     *            If not null and not empty, components included in the return
     *            value are limited to those with an ApprovalStatus that appears
     *            in the given list of ApprovalStatus values.
     * @return
     * @throws CommonFrameworkException
     */
    List<ComponentPojo> getComponentsRecursivelyByAppId(String appId,
	    List<ApprovalStatus> limitToApprovalStatusValues)
	    throws CommonFrameworkException;
}
