/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.tools.connector.protex;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;

/**
 * The Interface for a generic Server Wrapper where a server wrapper is a thin
 * layer around the SDK "Proxy" Objects.
 */
public interface IServerWrapper {

    /**
     * Gets the project by name.
     * 
     * @param projectName
     *            the project name
     * @return the project by name
     * @throws Exception
     *             the exception
     */
    ProjectPojo getProjectByName(String projectName) throws Exception;

    /**
     * Gets the project by id.
     * 
     * @param projectID
     *            the project id
     * @return the project by id
     * @throws Exception
     *             the exception
     */
    ProjectPojo getProjectByID(String projectID) throws Exception;

    /**
     * Gets the projects.
     * 
     * @param <T>
     * 
     * @return the projects
     * @throws Exception
     *             the exception
     */
    <T> List<T> getProjects(Class<T> classType) throws Exception;

    // Helper methods
    /**
     * Gets the implementation of the specific API Wrapper.
     * 
     * @return the internal api wrapper
     */
    APIWrapper getInternalApiWrapper();

    /**
     * Gets the implementation of the specific Configuration Manager.
     * 
     * @return the config manager
     */
    ConfigurationManager getConfigManager();
}
