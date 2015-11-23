package com.blackducksoftware.tools.connector.protex.project;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.ProjectApi;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;
import com.blackducksoftware.tools.connector.protex.common.ComponentNameVersionIds;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentPojo;
import com.blackducksoftware.tools.connector.protex.component.IProtexComponentManager;

public class ProjectManager implements IProjectManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final ProtexAPIWrapper apiWrapper;
    private final IProtexComponentManager compMgr;
    private final Map<String, Project> projectsByNameCache = new HashMap<>();
    private final Map<String, Project> projectsByIdCache = new HashMap<>();

    public ProjectManager(ProtexAPIWrapper apiWrapper,
	    IProtexComponentManager compMgr) {
	this.apiWrapper = apiWrapper;
	this.compMgr = compMgr;
    }

    @Override
    public ProjectPojo getProjectByName(String projectName)
	    throws CommonFrameworkException {

	Project proj = getProtexProjectByName(projectName);
	ProtexProjectPojo pojo = toPojo(proj);

	return pojo;
    }

    @Override
    public ProjectPojo getProjectById(String projectId)
	    throws CommonFrameworkException {

	Project proj = getProtexProjectById(projectId);
	ProjectPojo pojo = toPojo(proj);
	return pojo;
    }

    @Override
    public <T extends ProtexComponentPojo> List<T> getComponentsByProjectId(
	    Class<T> pojoClass, String projectId)
	    throws CommonFrameworkException {
	Project protexProject = getProtexProjectById(projectId);
	log.info("Getting components for project " + protexProject.getName());
	List<BomComponent> bomComponents;
	try {
	    bomComponents = apiWrapper.getBomApi().getBomComponents(
		    protexProject.getProjectId());
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting BOM Components for Protex Project ID "
			    + projectId + ": " + e.getMessage());
	}

	List<ComponentNameVersionIds> nameVersionIdsList = toNameVersionIdsList(bomComponents);

	List<T> componentPojos = compMgr.getComponentsByNameVersionIds(
		pojoClass, nameVersionIdsList);
	return componentPojos;
    }

    // Private methods

    private Project getProtexProjectById(String projectId)
	    throws CommonFrameworkException {
	// Check cache first
	if (projectsByIdCache.containsKey(projectId)) {
	    return projectsByIdCache.get(projectId);
	}
	Project proj;
	try {
	    ProjectApi projectAPI = apiWrapper.getProjectApi();
	    proj = projectAPI.getProjectById(projectId);

	    if (proj == null) {
		throw new Exception(
			"Project ID specified, resulted in empty project object:"
				+ projectId);
	    }

	} catch (Exception e) {
	    throw new CommonFrameworkException(
		    "Unable to find project by the ID of: " + projectId);
	}

	addToCache(proj);
	return proj;
    }

    private Project getProtexProjectByName(String projectName)
	    throws CommonFrameworkException {
	// Check cache first
	if (projectsByNameCache.containsKey(projectName)) {
	    return projectsByNameCache.get(projectName);
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
	return proj;
    }

    private void addToCache(Project project) {
	projectsByNameCache.put(project.getName(), project);
	projectsByIdCache.put(project.getProjectId(), project);
    }

    private ProtexProjectPojo toPojo(Project proj) {
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

    private List<ComponentNameVersionIds> toNameVersionIdsList(
	    List<BomComponent> bomComponents) {
	List<ComponentNameVersionIds> nameVersionIdsList = new ArrayList<>(
		bomComponents.size());
	for (BomComponent bomComponent : bomComponents) {
	    log.info("Processing component " + bomComponent.getComponentName());

	    switch (bomComponent.getType()) {
	    case LOCAL:
		log.warn("Skipping local component: "
			+ bomComponent.getComponentName() + " / "
			+ bomComponent.getVersionName());
		continue;
	    case PROJECT:
		log.info("Skipping parent project component: "
			+ bomComponent.getComponentName() + " / "
			+ bomComponent.getVersionName());
		continue;
	    default:
		// Proceed with processing it
	    }
	    ComponentNameVersionIds nameVersionIds = ComponentNameVersionIds
		    .valueOf(bomComponent);
	    nameVersionIdsList.add(nameVersionIds);
	}
	return nameVersionIdsList;
    }
}
