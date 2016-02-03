/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/
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
