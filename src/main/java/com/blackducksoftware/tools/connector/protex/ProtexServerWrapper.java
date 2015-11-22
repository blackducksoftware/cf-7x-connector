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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.LicenseCategory;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.ProjectInfo;
import com.blackducksoftware.sdk.protex.project.ProjectRequest;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.protex.component.IProtexComponentManager;
import com.blackducksoftware.tools.connector.protex.component.ProtexComponentManager;
import com.blackducksoftware.tools.connector.protex.license.LicenseManager;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
import com.blackducksoftware.tools.connector.protex.project.IProjectManager;
import com.blackducksoftware.tools.connector.protex.project.ProjectManager;
import com.blackducksoftware.tools.connector.protex.report.IReportManager;
import com.blackducksoftware.tools.connector.protex.report.ReportManager;

/**
 * Wrapper class around the Protex Server that provides common methods. This is
 * the primary class for SDK access.
 *
 * @author akamen
 *
 */
public class ProtexServerWrapper<T extends ProtexProjectPojo> implements
	IProtexServerWrapper<T> {

    /** The log. */
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private ILicenseManager<ProtexLicensePojo> licenseManager;

    private IReportManager reportManager;

    private IProjectManager projectManager;

    private IProtexComponentManager componentManager;

    /** The api wrapper. */
    private ProtexAPIWrapper apiWrapper;

    /** The config manager. */
    private ConfigurationManager configManager;
    private ServerBean serverBean;

    /**
     * Assistors These are the static helpers that contain code that pertain to
     * a specific type of operation.
     */
    private CodeTreeHelper codeTreeHelper;

    public ProtexServerWrapper() {
    }

    public ProtexServerWrapper(ServerBean bean, ConfigurationManager manager,
	    boolean validate) throws Exception {
	serverBean = bean;
	configManager = manager;
	apiWrapper = new ProtexAPIWrapper(bean, manager, validate);
	codeTreeHelper = new CodeTreeHelper(apiWrapper);
	licenseManager = new LicenseManager(apiWrapper);
	reportManager = new ReportManager(apiWrapper);
	componentManager = new ProtexComponentManager(apiWrapper,
		licenseManager);
	projectManager = new ProjectManager(apiWrapper, componentManager);
    }

    @Override
    public CodeTreeHelper getCodeTreeHelper() {
	return codeTreeHelper;
    }

    @Override
    public ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException {
	return projectManager.getProjectByName(projectName);
    }

    @Override
    public ProjectPojo getProjectByID(String projectID)
	    throws CommonFrameworkException {
	return projectManager.getProjectById(projectID);
    }

    @Override
    public String getProjectURL(ProjectPojo pojo) {
	String bomUrl = serverBean.getServerName()
		+ "/protex/ProtexIPIdentifyFolderBillOfMaterialsContainer?isAtTop=true&ProtexIPProjectId="
		+ pojo.getProjectKey()
		+ "&ProtexIPIdentifyFileViewLevel=folder&ProtexIPIdentifyFileId=-1";

	log.debug("Built URL for project: " + bomUrl);

	return bomUrl;
    }

    @Override
    public <T> List<T> getProjects(Class<T> theProjectClass) throws Exception {
	ArrayList<T> projectList = new ArrayList<T>();

	try {
	    ProjectApi projectAPI = apiWrapper.getProjectApi();

	    String userName = configManager.getServerBean().getUserName();
	    if (userName == null || userName.length() == 0) {
		userName = serverBean.getUserName();
	    }

	    List<ProjectInfo> project_list_info = projectAPI
		    .getProjectsByUser(userName);

	    for (ProjectInfo project : project_list_info) {
		if (project != null) {
		    String projName = project.getName();
		    String projID = project.getProjectId();
		    T projPojo = (T) generateNewInstance(theProjectClass);

		    // Set the basic
		    ((ProtexProjectPojo) projPojo).setProjectKey(projID);
		    ((ProtexProjectPojo) projPojo).setProjectName(projName);

		    projectList.add(projPojo);
		}
	    }
	} catch (SdkFault sf) {
	    // Try to explain why this has failed...messy, but could save time
	    // and aggravation

	    String message = sf.getMessage();
	    if (message != null) {
		if (message.contains("role")) {
		    throw new Exception(
			    "You do not have enough permission to list projects, you must be at least a 'Manager' to perform this task");
		}
	    } else {
		throw new Exception("Error getting project list", sf);
	    }
	} catch (Throwable t) {

	    if (t instanceof javax.xml.ws.soap.SOAPFaultException) {
		throw new Exception("There *may* be problem with SDK", t);
	    } else if (t instanceof javax.xml.ws.WebServiceException) {
		throw new Exception(
			"There *may* be problem with the connection.  The URL specified cannot be reached!",
			t);
	    } else {
		throw new Exception("General error, cannot continue! Error: ",
			t);
	    }
	}

	return projectList;
    }

    @Override
    public String createProject(String projectName, String description)
	    throws Exception {
	String projectID = "";
	ProjectRequest projectRequest = new ProjectRequest();
	projectRequest.setName(projectName);

	if (description != null) {
	    projectRequest.setDescription(description);
	}

	try {
	    ProjectApi projectAPI = apiWrapper.getProjectApi();
	    projectID = projectAPI.createProject(projectRequest,
		    LicenseCategory.PROPRIETARY);
	} catch (SdkFault e) {
	    throw new Exception(e.getMessage());
	}

	return projectID;
    }

    @Override
    public ProtexAPIWrapper getInternalApiWrapper() {
	return apiWrapper;
    }

    @Override
    public ConfigurationManager getConfigManager() {
	return this.configManager;
    }

    private T generateNewInstance(Class<?> theProjectClass) throws Exception {
	T pojo = null;
	Constructor<?> constructor = null;
	;
	try {
	    constructor = theProjectClass.getConstructor();
	} catch (SecurityException e) {
	    throw new Exception(e.getMessage());
	} catch (NoSuchMethodException e) {
	    throw new Exception(e.getMessage());
	}

	try {
	    pojo = (T) constructor.newInstance();
	} catch (IllegalArgumentException e) {
	    throw new Exception(e.getMessage());
	} catch (InstantiationException e) {
	    throw new Exception(e.getMessage());
	} catch (IllegalAccessException e) {
	    throw new Exception(e.getMessage());
	} catch (InvocationTargetException e) {
	    throw new Exception(e.getMessage());
	}

	return pojo;
    }

    @Override
    public ILicenseManager<ProtexLicensePojo> getLicenseManager() {
	return licenseManager;
    }

    @Override
    public IReportManager getReportManager() {
	return reportManager;
    }

    @Override
    public IProjectManager getProjectManager() {
	return projectManager;
    }

    @Override
    public IProtexComponentManager getComponentManager() {
	return componentManager;
    }
}
