package com.blackducksoftware.tools.connector.protex.project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;

public class ProjectManager implements IProjectManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final ProtexAPIWrapper apiWrapper;
    private final Map<String, Project> projectsByNameCache = new HashMap<>();
    private final Map<String, Project> projectsByIdCache = new HashMap<>();

    public ProjectManager(ProtexAPIWrapper apiWrapper) {
	this.apiWrapper = apiWrapper;
    }

    @Override
    public ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException {

	// Check cache first
	if (projectsByNameCache.containsKey(projectName)) {
	    return populateProjectBean(projectsByNameCache.get(projectName));
	}
	Project proj;
	try {
	    ProjectApi projectAPI = apiWrapper.getProjectApi();
	    proj = projectAPI.getProjectByName(projectName.trim());

	    if (proj == null) {
		throw new Exception(
			"Project name specified, resulted in empty project object:"
				+ projectName);
	    }

	} catch (Exception e) {
	    String details = e.getMessage();
	    throw new CommonFrameworkException(
		    "Unable to find project by the name of: " + projectName
			    + ". Reason: " + details);
	}

	addToCache(proj);
	ProtexProjectPojo pojo = populateProjectBean(proj);

	return pojo;
    }

    @Override
    public ProjectPojo getProjectByID(String projectID)
	    throws CommonFrameworkException {

	// Check cache first
	if (projectsByIdCache.containsKey(projectID)) {
	    return populateProjectBean(projectsByNameCache.get(projectID));
	}
	Project proj;
	try {
	    ProjectApi projectAPI = apiWrapper.getProjectApi();
	    proj = projectAPI.getProjectById(projectID);

	    if (proj == null) {
		throw new Exception(
			"Project ID specified, resulted in empty project object:"
				+ projectID);
	    }

	} catch (Exception e) {
	    throw new CommonFrameworkException(
		    "Unable to find project by the ID of: " + projectID);
	}

	addToCache(proj);
	ProjectPojo pojo = populateProjectBean(proj);
	return pojo;
    }

    @Override
    public List<CodeCenterComponentPojo> getComponentsByProjectId(
	    String projectId) throws CommonFrameworkException {
	// TODO Auto-generated function stub
	return null;
    }

    // Private methods

    private void addToCache(Project project) {
	projectsByNameCache.put(project.getName(), project);
	projectsByIdCache.put(project.getProjectId(), project);
    }

    private ProtexProjectPojo populateProjectBean(Project proj) {
	ProtexProjectPojo pojo = new ProtexProjectPojo(proj.getProjectId(),
		proj.getName());

	try {
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	    String prettyAnalyzedDate = df.format(proj.getLastAnalyzedDate());
	    pojo.setAnalyzedDate(prettyAnalyzedDate);
	    log.debug("Set project last analyzed date: " + prettyAnalyzedDate);
	} catch (Exception e) {
	}

	return pojo;
    }
}
