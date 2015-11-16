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
package com.blackducksoftware.tools.connector.protex;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.protex.client.util.ProtexServerProxy;
import com.blackducksoftware.sdk.protex.client.util.ServerAuthenticationException;
import com.blackducksoftware.sdk.protex.component.ComponentApi;
import com.blackducksoftware.sdk.protex.license.LicenseApi;
import com.blackducksoftware.sdk.protex.policy.PolicyApi;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.DiscoveryApi;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationApi;
import com.blackducksoftware.sdk.protex.project.localcomponent.LocalComponentApi;
import com.blackducksoftware.sdk.protex.report.ReportApi;
import com.blackducksoftware.sdk.protex.role.RoleApi;
import com.blackducksoftware.sdk.protex.user.UserApi;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;

/**
 * Primary authenticator and validator for the Protex SDKs.
 *
 * @author Ari Kamen
 */
public class ProtexAPIWrapper extends APIWrapper {

    /** The log. */
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    /** The bom api. */
    private BomApi bomApi;

    /** The code tree api. */
    private CodeTreeApi codeTreeApi;
    private ComponentApi componentApi;
    /** The identification api. */
    private IdentificationApi identificationApi;

    /** The project api. */
    private ProjectApi projectApi;

    /** The discovery api. */
    private DiscoveryApi discoveryApi;

    /** The report api. */
    private ReportApi reportApi;

    /** The user api. */
    private UserApi userApi;

    /** The license api. */
    private LicenseApi licenseApi;

    /** The standard component api. */
    /** The role api. */
    private RoleApi roleApi;
    private LocalComponentApi localComponentApi;
    private PolicyApi policyApi;

    /** The protex server. */
    private ProtexServerProxy protexServer;

    /** The error message. */
    private String errorMessage = "";

    /**
     * Creates a proxy object with the proper credentials Credentials keyed off
     * the server bean.
     *
     * @param bean
     * @param configManager
     * @param validate
     * @throws Exception
     */
    public ProtexAPIWrapper(ServerBean bean,
	    ConfigurationManager configManager, boolean validate)
	    throws Exception {
	super(configManager, bean);
	getAllApisAndValidate(bean.getServerName(), bean.getUserName(),
		bean.getPassword(), validate, configManager);

    }

    /**
     * Handles initializing all APIs and performing user credential validation
     *
     * @param server
     *            the connection server
     * @param user
     *            the user information
     * @param password
     *            the user password
     * @param validate
     *            the validate flag
     * @param configManager
     *            the configuration manager
     * @throws Exception
     *             if error occurs during authentication or API calls
     */
    private void getAllApisAndValidate(String server, String user,
	    String password, boolean validate,
	    ConfigurationManager configManager) throws Exception {
	try {
	    protexServer = new ProtexServerProxy(server, user, password);
	    log.info("User Info: " + user);
	    if (validate) {
		validateCredentials();
	    }

	    // Potential CXF limit override option
	    protexServer.setMaximumChildElements(configManager
		    .getChildElementCount());

	    bomApi = protexServer.getBomApi();
	    codeTreeApi = protexServer.getCodeTreeApi();
	    componentApi = protexServer.getComponentApi();
	    identificationApi = protexServer.getIdentificationApi();
	    projectApi = protexServer.getProjectApi();
	    discoveryApi = protexServer.getDiscoveryApi();
	    reportApi = protexServer.getReportApi();
	    userApi = protexServer.getUserApi();
	    licenseApi = protexServer.getLicenseApi();
	    policyApi = protexServer.getPolicyApi();
	    localComponentApi = protexServer.getLocalComponentApi();

	} catch (ServerAuthenticationException sae) {
	    errorMessage = "Unable to log in: " + sae.getMessage();
	    throw new Exception(errorMessage);
	} catch (Exception e) {
	    errorMessage = e.getMessage();
	    if (e.getCause() != null) {
		errorMessage += ": " + e.getCause().getMessage();
	    }
	    throw new Exception(errorMessage);
	}

    }

    /**
     * Grabs one project to check if authentication worked.
     *
     * @param protex
     *            the protex
     * @param manager
     *            the manager
     * @throws Exception
     *             the exception
     */
    private void validateCredentials() throws Exception {
	try {
	    protexServer.validateCredentials();
	} catch (Exception e) {
	    String msg = e.getMessage();
	    if (e.getCause() != null) {
		msg += "; Caused by: " + e.getCause().getMessage();
	    }
	    // This is a hack; In 7.0 the SDK should throw a more helpful
	    // message when the SDK license is absent,
	    // but for now, this seems to be about the best we can do in terms
	    // of getting a helpful msg to the user. -- Steve Billings
	    if (e instanceof SOAPFaultException) {
		msg += " Please check that the Protex server has the SDK license enabled.";
	    }
	    throw new Exception(msg);
	}
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    /**
     * Retrieves the interal proxy object
     *
     * @return
     */
    public ProtexServerProxy getProxy() {
	return protexServer;
    }

    public BomApi getBomApi() {
	return bomApi;
    }

    public CodeTreeApi getCodeTreeApi() {
	return codeTreeApi;
    }

    public IdentificationApi getIdentificationApi() {
	return identificationApi;
    }

    public ProjectApi getProjectApi() {
	return projectApi;
    }

    public DiscoveryApi getDiscoveryApi() {
	return discoveryApi;
    }

    public ReportApi getReportApi() {
	return reportApi;
    }

    public UserApi getUserApi() {
	return userApi;
    }

    public LicenseApi getLicenseApi() {
	return licenseApi;
    }

    public RoleApi getRoleApi() {
	return roleApi;
    }

    public LocalComponentApi getLocalComponentApi() {
	return localComponentApi;
    }

    public PolicyApi getPolicyApi() {
	return policyApi;
    }

    public ComponentApi getComponentApi() {
	return componentApi;
    }

}
