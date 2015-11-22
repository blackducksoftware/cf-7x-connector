package com.blackducksoftware.tools.connector.protex.component;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.protex.common.ComponentNameVersionIds;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentPojo;

public interface IProtexComponentManager {
    ProtexComponentPojo getComponentByNameVersionIds(
	    ComponentNameVersionIds nameVersionIds)
	    throws CommonFrameworkException;

    List<ProtexComponentPojo> getComponentsByNameVersionIds(
	    List<ComponentNameVersionIds> nameVersionIdsList)
	    throws CommonFrameworkException;
}
