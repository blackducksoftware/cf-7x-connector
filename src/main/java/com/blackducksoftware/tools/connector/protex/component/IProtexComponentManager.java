package com.blackducksoftware.tools.connector.protex.component;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.protex.common.ComponentNameVersionIds;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentPojo;

public interface IProtexComponentManager {
    <T extends ProtexComponentPojo> T getComponentByNameVersionIds(
	    Class<T> pojoClass, ComponentNameVersionIds nameVersionIds)
	    throws CommonFrameworkException;

    <T extends ProtexComponentPojo> T getComponentByNameVersion(
	    Class<T> pojoClass, String componentName, String componentVersion)
	    throws CommonFrameworkException;

    <T extends ProtexComponentPojo> List<T> getComponentsByNameVersionIds(
	    Class<T> pojoClass, List<ComponentNameVersionIds> nameVersionIdsList)
	    throws CommonFrameworkException;

    <T extends ProtexComponentPojo> T instantiatePojo(Class<T> pojoClass)
	    throws CommonFrameworkException;
}
