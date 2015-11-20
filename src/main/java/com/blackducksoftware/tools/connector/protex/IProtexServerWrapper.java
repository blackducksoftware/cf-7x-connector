package com.blackducksoftware.tools.connector.protex;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
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

    ILicenseManager<ProtexLicensePojo> getLicenseManager();

    IReportManager getReportManager();

}