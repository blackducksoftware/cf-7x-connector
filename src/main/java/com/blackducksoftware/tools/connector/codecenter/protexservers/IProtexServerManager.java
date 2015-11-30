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

    /**
     * Validate the Protex servers named in the configuration.
     * 
     * @throws CommonFrameworkException
     */
    void validateServers() throws CommonFrameworkException;

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
}
