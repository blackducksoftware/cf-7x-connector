package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.common.ComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;

public interface IComponentManager {

    /**
     * Get a component by its component ID (not kbComponentId).
     *
     * @param componentId
     * @return
     * @throws CommonFrameworkException
     */
    ComponentPojo getComponentById(String componentId)
	    throws CommonFrameworkException;

    /**
     * Get a component by its name/version.
     *
     * @param componentName
     * @param componentVersion
     * @return
     * @throws CommonFrameworkException
     */
    ComponentPojo getComponentByNameVersion(String componentName,
	    String componentVersion) throws CommonFrameworkException;

    /**
     * Get the list of components named in a list of requests.
     *
     * @param requests
     * @return
     * @throws CommonFrameworkException
     */
    List<ComponentPojo> getComponentsForRequests(List<RequestPojo> requests)
	    throws CommonFrameworkException;

    /**
     * Get the list of components named in a list of requests, limited to those
     * with one of the ApprovalStatus values in the provided list.
     *
     * @param requests
     * @param limitToApprovalStatusValues
     *            If not null and not empty, components included in the return
     *            value are limited to those with an ApprovalStatus that appears
     *            in the given list of ApprovalStatus values.
     * @return
     * @throws CommonFrameworkException
     */
    List<ComponentPojo> getComponentsForRequests(List<RequestPojo> requests,
	    List<ApprovalStatus> limitToApprovalStatusValues)
	    throws CommonFrameworkException;

}
