package com.blackducksoftware.tools.connector.protex.project;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;

public interface IProjectManager {
    ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException;

    ProjectPojo getProjectByID(String projectID)
	    throws CommonFrameworkException;

    List<CodeCenterComponentPojo> getComponentsByProjectId(String projectId)
	    throws CommonFrameworkException;
}
