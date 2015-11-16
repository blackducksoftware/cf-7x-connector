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
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;
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

    private final ICodeCenterServerWrapper ccsw;
    private final ConfigurationManager config;
    private final Map<String, NamedProtexServer> protexServerCache = new HashMap<>();

    /**
     *
     * @param ccsw
     *            a ConfigurationManager
     * @param config
     */
    public ProtexServerManager(ICodeCenterServerWrapper ccsw,
	    ConfigurationManager config) {
	this.ccsw = ccsw;
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
    public void validateServers() throws CommonFrameworkException {
	if (!(config instanceof CcConfigMgrWithPtxServers)) {
	    throw new CommonFrameworkException(
		    "To use the ProtexServerManager, you must pass a configuration manager object that implements CcConfigMgrWithPtxServers");
	}
	CcConfigMgrWithPtxServers configWithPtxServers = (CcConfigMgrWithPtxServers) config;
	List<String> pxServerValidationList = getPxServerValidationList(configWithPtxServers);
	for (String protexServerName : pxServerValidationList) {
	    connectToServerAndCacheIt(protexServerName);
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
    public ProtexServerWrapper<ProtexProjectPojo> getProtexServerWrapper(
	    String serverName) throws CommonFrameworkException {
	NamedProtexServer namedServer;
	if (protexServerCache.containsKey(serverName)) {
	    namedServer = protexServerCache.get(serverName);
	} else {
	    namedServer = connectToServerAndCacheIt(serverName);
	}

	return namedServer.getProtexServerWrapper();
    }

    // Private methods

    /**
     * Connect to a given Protex server.
     *
     * @param protexServerName
     * @return
     * @throws CommonFrameworkException
     */
    private NamedProtexServer connectToServerAndCacheIt(String protexServerName)
	    throws CommonFrameworkException {
	ServerNameToken serverNameToken = new ServerNameToken();
	serverNameToken.setName(protexServerName);
	ProtexServer protexServer;
	try {
	    protexServer = ccsw.getInternalApiWrapper().getSettingsApi()
		    .getServerDetails(serverNameToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error looking up in Code Center settings the Protex server named "
			    + protexServerName + ": " + e.getMessage());
	}
	String protexUrl = protexServer.getHostAddress();
	ProtexServerWrapper<ProtexProjectPojo> psw;

	ConfigurationManager protexConfig = createProtexConfig(config,
		protexUrl);
	try {
	    psw = new ProtexServerWrapper<ProtexProjectPojo>(
		    protexConfig.getServerBean(), protexConfig, true);
	} catch (Exception e) {
	    throw new CommonFrameworkException(
		    "Error connecting to the Protex server named (in Code Center settings) "
			    + protexServerName + " with URL " + protexUrl
			    + ": " + e.getMessage());
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
}
