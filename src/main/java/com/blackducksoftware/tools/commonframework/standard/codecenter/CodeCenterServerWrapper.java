/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
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
package com.blackducksoftware.tools.commonframework.standard.codecenter;

import java.util.List;

import com.blackducksoftware.tools.commonframework.connector.protex.IServerWrapper;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;

/**
 * The Class CodeCenterServerWrapper.
 */
public class CodeCenterServerWrapper implements IServerWrapper {

    /** The api wrapper. */
    private final CodeCenterAPIWrapper apiWrapper;

    /** The config manager. */
    private final ConfigurationManager configManager;

    public CodeCenterServerWrapper(ServerBean bean, ConfigurationManager manager)
	    throws Exception {
	configManager = manager;
	apiWrapper = new CodeCenterAPIWrapper(bean, manager);
    }

    @Override
    public ProjectPojo getProjectByName(String projectName) throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    public ProjectPojo getProjectByID(String projectID) throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> getProjects(Class<T> classType) throws Exception {
	throw new UnsupportedOperationException();
    }

    @Override
    public CodeCenterAPIWrapper getInternalApiWrapper() {
	return apiWrapper;
    }

    @Override
    public ConfigurationManager getConfigManager() {
	return configManager;
    }

}
