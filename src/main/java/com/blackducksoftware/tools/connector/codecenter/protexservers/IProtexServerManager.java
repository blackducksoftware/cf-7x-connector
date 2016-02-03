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
