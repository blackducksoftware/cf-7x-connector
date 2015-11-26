package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

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
     * Get a subset (or all) of the applications that the user can access.
     *
     * @param firstRow
     *            get rows starting at this index (first = 0)
     * @param lastRow
     *            get rows ending at this index (use Integer.MAX_VALUE for all)
     * @return
     * @throws CommonFrameworkException
     */
    List<ApplicationPojo> getApplications(int firstRow, int lastRow)
            throws CommonFrameworkException;

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
     * Get an application's components, optionally recursively, by application
     * ID, limited to components with an approval status that is in the provided
     * list.
     *
     * If recursive: For each component (at any level in the tree) that is an
     * application published as a component, returns the approved components
     * pointed to the published application's requests instead of the published
     * application itself.
     *
     * @param appId
     * @param limitToApprovalStatusValues
     *            If not null and not empty, components included in the return
     *            value are limited to those with an ApprovalStatus that appears
     *            in the given list of ApprovalStatus values.
     * @param recursive
     * @return
     * @throws CommonFrameworkException
     */
    <T extends CodeCenterComponentPojo> List<T> getComponentsByAppId(
            Class<T> pojoClass, String appId,
            List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
            throws CommonFrameworkException;

    /**
     * Gets all User's that are assigned to the specified Application.
     *
     * @param appId
     *            String
     * @return List<(ApplicationUserPojo)>
     * @throws CommonFrameworkException
     */
    List<ApplicationUserPojo> getAllUsersAssignedToApplication(String appId)
            throws CommonFrameworkException;
}
