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
package com.blackducksoftware.tools.connector.codecenter.protexservers;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;

/**
 * Manages a set of Protex servers.
 * 
 * These are a subset of the Protex servers configured into the Code Center
 * server's settings.
 * 
 * @author sbillings
 * 
 */
public interface IProtexServerManager {

    public static boolean CACHE_FAILED = true;

    public static boolean CACHE_SUCCESS = false;

    /**
     * Validate the Protex servers named in the configuration.
     * 
     * @param cacheFailedConnections
     *            - Caches those connections that failed (useful in case of reinjection)
     * @throws CommonFrameworkException
     */
    void validateServers(boolean cacheFailedConnections) throws CommonFrameworkException;

    /**
     * Get the ProtexServerWrapper for the given Protex Server.
     * 
     * @param serverName
     * @return
     * @throws CommonFrameworkException
     */
    IProtexServerWrapper<ProtexProjectPojo> getProtexServerWrapper(
            String serverName) throws CommonFrameworkException;

    /**
     * Returns the list of cached Protex server names
     * 
     * @return
     * @throws CommonFrameworkException
     */
    List<String> getAllProtexNames() throws CommonFrameworkException;

    /**
     * Gets a specific NamedProtexServer based on a 'key'
     * 
     * @param key
     * @return
     * @throws CommonFrameworkException
     */
    NamedProtexServer getNamedProtexServer(String key) throws CommonFrameworkException;

    /**
     * Sets a Named Protex Server back into the cache
     * 
     * @param server
     * @parem key - Named of Protex configured instance
     * @throws CommonFrameworkException
     */
    void setNamedProtexServer(NamedProtexServer server, String key) throws CommonFrameworkException;

}
