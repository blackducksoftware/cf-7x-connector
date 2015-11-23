package com.blackducksoftware.tools.connector.protex.project;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentPojo;

public interface IProjectManager {
    ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException;

    ProjectPojo getProjectById(String projectID)
	    throws CommonFrameworkException;

    <T extends ProtexComponentPojo> List<T> getComponentsByProjectId(
	    Class<T> pojoClass, String projectId)
	    throws CommonFrameworkException;
}
