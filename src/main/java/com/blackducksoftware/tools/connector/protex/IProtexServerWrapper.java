/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.protex.component.IProtexComponentManager;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
import com.blackducksoftware.tools.connector.protex.obligation.IObligationManager;
import com.blackducksoftware.tools.connector.protex.project.IProjectManager;
import com.blackducksoftware.tools.connector.protex.report.IReportManager;

public interface IProtexServerWrapper<T extends ProtexProjectPojo> extends
	IServerWrapper {

    CodeTreeHelper getCodeTreeHelper();

    /**
     * Returns a pojo based on name. Throws exception if name does not produce
     * anything
     *
     * @param projectName
     *            the project name
     * @return the project by name
     * @throws Exception
     *             the exception
     */
    @Override
    ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException;

    /**
     * Returns project POJO based on ID.
     *
     * @param projectID
     *            the project id
     * @return the project by id
     * @throws Exception
     *             the exception
     */
    @Override
    ProjectPojo getProjectByID(String projectID)
	    throws CommonFrameworkException;

    String getProjectURL(ProjectPojo pojo);

    /**
     * Returns a list of ProtexProjectPojos populated with necessary date.
     *
     * @param <T>
     *            Your pojo (can be a default ProtexProjectPojo).
     *
     * @return the projects
     * @throws Exception
     *             the exception
     */
    @Override
    <T> List<T> getProjects(Class<T> theProjectClass) throws Exception;

    /**
     * Creates the project.
     *
     * @param projectName
     *            the project name
     * @param description
     *            the description
     * @return the string
     * @throws Exception
     *             the exception
     */
    String createProject(String projectName, String description)
	    throws Exception;

    @Override
    ProtexAPIWrapper getInternalApiWrapper();

    @Override
    ConfigurationManager getConfigManager();

    /**
     * Get the license manager.
     *
     * @return
     */
    ILicenseManager<ProtexLicensePojo> getLicenseManager();

    /**
     * Get the report manager.
     *
     * @return
     */
    IReportManager getReportManager();

    /**
     * Get the Project manager.
     *
     * @return
     */
    IProjectManager getProjectManager();

    /**
     * Get the component manager.
     *
     * @return
     */
    IProtexComponentManager getComponentManager();

    /**
     * Get the obligation manager.
     *
     * @return
     */
    IObligationManager getObligationManager();

}