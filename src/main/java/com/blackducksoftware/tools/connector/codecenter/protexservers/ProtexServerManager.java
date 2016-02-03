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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.administration.data.ProtexServer;
import com.blackducksoftware.sdk.codecenter.administration.data.ServerNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.IConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;
import com.blackducksoftware.tools.connector.protex.ProtexServerWrapper;

/**
 * Manages a set of Protex servers.
 * 
 * These are a subset of the Protex servers configured into the Code Center
 * server's settings.
 * 
 * Each server that has been connected to (either during validation, or during a
 * getProtexServerWrapper() call) is cached, so those connections can be re-used
 * if they are requested again.
 * 
 * If you want to call the validate method, the ConfigurationManager object you
 * provide to the constructor must implement CcConfigMgrWithPtxServers.
 * 
 * @author sbillings
 * 
 */
public class ProtexServerManager implements IProtexServerManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterAPIWrapper ccApiWrapper;

    private final ConfigurationManager config;

    private final Map<String, NamedProtexServer> protexServerCache = new HashMap<>();

    public ProtexServerManager(CodeCenterAPIWrapper ccApiWrapper,
            ConfigurationManager config) {
        this.ccApiWrapper = ccApiWrapper;
        this.config = config;
    }

    /**
     * Validate the Protex servers named in the configuration, caching each one.
     * 
     * To use this method, the ConfigurationManager provided to the constructor
     * must implement CcConfigMgrWithPtxServers.
     * 
     * @throws CommonFrameworkException
     */
    @Override
    public void validateServers(boolean cacheFailedConnections) throws CommonFrameworkException {
        if (!(config instanceof CcConfigMgrWithPtxServers)) {
            throw new CommonFrameworkException(
                    "To use the ProtexServerManager, you must pass a configuration manager object that implements CcConfigMgrWithPtxServers");
        }
        CcConfigMgrWithPtxServers configWithPtxServers = (CcConfigMgrWithPtxServers) config;
        List<String> pxServerValidationList = getPxServerValidationList(configWithPtxServers);
        for (String protexServerName : pxServerValidationList) {
            connectToServerAndCacheIt(protexServerName, cacheFailedConnections);
            log.info("Connected to Protex server: " + protexServerName);
        }
    }

    private List<String> getPxServerValidationList(
            CcConfigMgrWithPtxServers config) {
        List<String> ccProtexServerNames;

        String nameListString = config.getPxServerValidationList();
        if ((nameListString == null) || (nameListString.length() == 0)) {
            ccProtexServerNames = new ArrayList<String>(0);
        } else {
            String[] nameArray = nameListString.split(",");
            ccProtexServerNames = Arrays.asList(nameArray);
        }
        return ccProtexServerNames;
    }

    /**
     * Get the ProtexServerWrapper for the given Protex Server.
     * 
     * If it's in the cache, use that. If not, connect to this new Protex server
     * and cache it.
     * 
     * @param serverName
     * @return
     * @throws CommonFrameworkException
     */
    @Override
    public IProtexServerWrapper<ProtexProjectPojo> getProtexServerWrapper(
            String serverName) throws CommonFrameworkException {
        NamedProtexServer namedServer;
        if (protexServerCache.containsKey(serverName)) {
            namedServer = protexServerCache.get(serverName);
        } else {
            namedServer = connectToServerAndCacheIt(serverName, IProtexServerManager.CACHE_SUCCESS);
        }

        return namedServer.getProtexServerWrapper();
    }

    /**
     * Gets the names of cached Protex Servers
     */
    @Override
    public List<String> getAllProtexNames() throws CommonFrameworkException {
        List<String> protexNames = new ArrayList<String>();
        if (protexServerCache.size() == 0) {
            throw new CommonFrameworkException(
                    "No Protex servers cached, did you initialize?");
        }

        for (NamedProtexServer namedProtex : protexServerCache.values()) {
            protexNames.add(namedProtex.getName());

        }

        return protexNames;
    }

    // Private methods

    /**
     * Connect to a given Protex server.
     * 
     * @param protexServerName
     * @param cacheFailedConnections
     * @return
     * @throws CommonFrameworkException
     */
    private NamedProtexServer connectToServerAndCacheIt(String protexServerName, boolean cacheFailedConnections)
            throws CommonFrameworkException {
        // First we attempt to look up the cache, just in case previous failed connections reside here.
        NamedProtexServer namedProtexServer = protexServerCache.get(protexServerName);
        String protexUrl = null;
        if (namedProtexServer == null)
        {
            ServerNameToken serverNameToken = new ServerNameToken();
            serverNameToken.setName(protexServerName);
            ProtexServer protexServer;
            try {
                protexServer = ccApiWrapper.getSettingsApi().getServerDetails(
                        serverNameToken);
            } catch (SdkFault e) {
                throw new CommonFrameworkException(
                        "Error looking up in Code Center settings the Protex server named "
                                + protexServerName + ": " + e.getMessage());
            }
            protexUrl = protexServer.getHostAddress();
            log.info("Derived URL from Code Center for Protex instance: " + protexUrl);
        }
        // If exists, then use the existing one.
        else
        {
            protexUrl = namedProtexServer.getUrl();
            log.info("Using cached URL for Protex instance: " + protexUrl);
        }

        ProtexServerWrapper<ProtexProjectPojo> psw = null;

        ConfigurationManager protexConfig = createProtexConfig(config,
                protexUrl);
        try {
            psw = new ProtexServerWrapper<ProtexProjectPojo>(
                    protexConfig.getServerBean(), protexConfig, true);
        } catch (Exception e) {
            if (!cacheFailedConnections)
            {
                throw new CommonFrameworkException(
                        "Error connecting to the Protex server named (in Code Center settings) "
                                + protexServerName + " with URL " + protexUrl
                                + ": " + e.getMessage());
            }
            else
            {
                log.warn("Connection failed, but proceeding with caching this Protex instance");
            }
        }
        NamedProtexServer namedServer = new NamedProtexServer(protexServerName,
                protexUrl, psw);
        protexServerCache.put(protexServerName, namedServer);
        return namedServer;
    }

    /**
     * We have a Code Center config, but we need a Protex config to connect to
     * protex.
     * 
     * @param ccConfig
     * @param protexUrl
     * @return
     */
    private ConfigurationManager createProtexConfig(
            IConfigurationManager ccConfig, String protexUrl) {
        Properties props = new Properties();
        props.setProperty("protex.server.name", protexUrl);
        props.setProperty("protex.user.name", ccConfig.getServerBean()
                .getUserName());
        props.setProperty("protex.password", ccConfig.getServerBean()
                .getPassword());

        ConfigurationManager protexConfig = new ProtexConfigurationManager(
                props);
        return protexConfig;
    }

    /*
     * (non-JSDoc)
     * 
     * @see
     * com.blackducksoftware.tools.connector.codecenter.protexservers.IProtexServerManager#getNamedProtexServer(java
     * .lang.String)
     */
    @Override
    public NamedProtexServer getNamedProtexServer(String key) throws CommonFrameworkException {
        NamedProtexServer namedProtex = protexServerCache.get(key);
        if (namedProtex == null) {
            throw new CommonFrameworkException("No Protex instance found with name: " + key);
        }

        return namedProtex;
    }

    /**
     * Sets the NamedProtexServer (overriding existing values)
     * Connects and caches it.
     */
    @Override
    public void setNamedProtexServer(NamedProtexServer server, String key) throws CommonFrameworkException {
        NamedProtexServer protexServer = protexServerCache.remove(key);
        if (protexServer == null) {
            throw new CommonFrameworkException("Attempted to update non existing Protex instance with key: " + key);
        }
        protexServerCache.put(key, server);
        connectToServerAndCacheIt(key, IProtexServerManager.CACHE_SUCCESS);

    }

}
