package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
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

}
