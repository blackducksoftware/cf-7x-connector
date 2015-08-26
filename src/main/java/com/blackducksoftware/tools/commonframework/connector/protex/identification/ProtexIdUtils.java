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

package com.blackducksoftware.tools.commonframework.connector.protex.identification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.ComponentKey;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeRequest;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.NodeCount;
import com.blackducksoftware.sdk.protex.project.codetree.NodeCountType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchType;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;
import com.blackducksoftware.tools.commonframework.connector.protex.ProtexServerWrapper;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.identification.IdentificationMade;

public class ProtexIdUtils {
    private static final Logger log = LoggerFactory
	    .getLogger(ProtexIdUtils.class.getName());

    private final Identifier identifier;
    private static ProtexServerWrapper<ProtexProjectPojo> protexServerWrapper;
    private final String projectId;

    private final Collection<IdentificationMade> identificationsMade = new ArrayList<IdentificationMade>();
    private boolean doRefresh;

    /**
     * Constructor
     *
     * @param config
     *            a ConfigurationManager with Protex server/username/password
     *            set.
     * @throws Exception
     *             upon error connecting to or using Protex
     */
    public ProtexIdUtils(ConfigurationManager config, Identifier identifier,
	    String protexProjectName, boolean doRefresh) throws Exception {
	this.doRefresh = doRefresh;

	log.debug("Creating ProtexServerWrapper");
	protexServerWrapper = new ProtexServerWrapper<>(config.getServerBean(),
		config, true);
	log.debug("Created ProtexServerWrapper");

	log.debug("Loading project " + protexProjectName);
	Project project = loadProject(protexProjectName);
	log.debug("Loaded project");
	projectId = project.getProjectId();

	this.identifier = identifier;
    }

    /**
     * Returns the list of identifications made so far
     *
     * @return the list of identifications made so far
     */
    public Collection<IdentificationMade> getIdentificationsMade() {
	return identificationsMade;
    }

    public String getProjectId() {
	return projectId;
    }

    /**
     * Performs a Code Match identification Code Match attempts to do a code
     * match identification, as opposed to declare file.
     *
     * @param path
     *            the path to the file
     * @param target
     *            the discovery to make the identification with
     * @throws SdkFault
     */
    public void makeId(String path, Discovery discoveryTarget) throws SdkFault {
	CodeMatchDiscovery target = (CodeMatchDiscovery) discoveryTarget;
	log.debug("Making match for: " + target.getFilePath() + ": "
		+ target.getDiscoveredComponentKey().getComponentId()
		+ ", type: " + target.getDiscoveryType());

	identifier.makeIdentificationOnFile(path, target);

	IdentificationMade idMade = new IdentificationMade(path, target
		.getMatchingSourceInfo().getFirstLine(), target
		.getMatchingSourceInfo().getLineCount(), target
		.getDiscoveredComponentKey().getComponentId(), target
		.getDiscoveredComponentKey().getVersionId(),
		getComponentVersionString(target),
		target.getMatchRatioAsPercent());

	identificationsMade.add(idMade);
	log.debug("Added Identification for " + idMade);
    }

    /**
     * Load the project of the given name from Protex
     *
     * @param projectName
     * @return
     * @throws SdkFault
     *             if project doesn't exist
     */
    private Project loadProject(String projectName) throws SdkFault {
	Project project = protexServerWrapper.getInternalApiWrapper()
		.getProjectApi().getProjectByName(projectName);
	return project;
    }

    public ProtexServerWrapper<ProtexProjectPojo> getProtexServerWrapper() {
	return protexServerWrapper;
    }

    /**
     * Get the "best" match out of the given list of code match discoveries.
     * Best = most (highest %) of code coverage.
     *
     * @param codeMatchDiscoveries
     * @return
     * @throws SdkFault
     */
    public static CodeMatchDiscovery bestMatch(
	    List<CodeMatchDiscovery> codeMatchDiscoveries) throws SdkFault {
	int maxScore = 0;
	CodeMatchDiscovery bestCodeMatchDiscovery = null;
	for (CodeMatchDiscovery match : codeMatchDiscoveries) {
	    int thisScore = match.getMatchRatioAsPercent();
	    String versionString = getComponentVersionString(match);

	    ComponentKey key = match.getDiscoveredComponentKey();

	    log.debug("Code Match Discovery: " + key.getComponentId() + "/"
		    + versionString + "; score: " + thisScore + "; ID status: "
		    + match.getIdentificationStatus().toString());

	    if (match.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION) {

		if (thisScore > maxScore) {
		    log.debug("\tThis one is the best so far");
		    bestCodeMatchDiscovery = match;
		    maxScore = thisScore;
		} else {
		    log.debug("\tThis one is NOT the best so far; ignoring it");
		}
	    } else {
		log.debug("\tThis match identification status was not pending; ignoring it");
	    }
	}
	return bestCodeMatchDiscovery;
    }

    /**
     * Get the version string for a match. This is SLOW.
     *
     * @param match
     * @return
     */
    public static String getComponentVersionString(CodeMatchDiscovery match) {
	String versionString = "unknown";
	if (protexServerWrapper != null) {
	    try {

		versionString = protexServerWrapper.getInternalApiWrapper()
			.getComponentApi()
			.getComponentByKey(match.getDiscoveredComponentKey())
			.getVersionName();

	    } catch (Exception e) {
	    }
	}
	return versionString;
    }

    /**
     * Get the code match discoveries for a file.
     *
     * @param tree
     * @return
     * @throws SdkFault
     */
    public List<CodeMatchDiscovery> getCodeMatchDiscoveries(
	    List<CodeTreeNode> nodes) throws SdkFault {
	List<CodeMatchType> codeMatchTypes = new ArrayList<CodeMatchType>();
	codeMatchTypes.add(CodeMatchType.PRECISION);
	// codeMatchTypes.add(CodeMatchType.GENERIC); // Precision matches are
	// better;
	List<CodeMatchDiscovery> codeMatchDiscoveries = protexServerWrapper
		.getInternalApiWrapper().getDiscoveryApi()
		.getCodeMatchDiscoveries(projectId, nodes, codeMatchTypes);
	return codeMatchDiscoveries;
    }

    /**
     * Get the code match discoveries for a list of files.
     *
     * @param tree
     * @return
     * @throws SdkFault
     */
    public List<CodeMatchDiscovery> getCodeMatchDiscoveries(String path,
	    List<CodeTreeNode> files) throws SdkFault {
	return getCodeMatchDiscoveries(files);
    }

    /**
     * Get all code tree nodes in project.
     */
    public List<CodeTreeNode> getAllCodeTreeNodes() throws SdkFault {
	CodeTreeNodeRequest codeTreeNodeRequest = new CodeTreeNodeRequest();
	codeTreeNodeRequest.setDepth(-1);
	codeTreeNodeRequest.setIncludeParentNode(true);
	codeTreeNodeRequest.getIncludedNodeTypes().add(
		CodeTreeNodeType.EXPANDED_ARCHIVE);
	codeTreeNodeRequest.getIncludedNodeTypes().add(CodeTreeNodeType.FILE);
	codeTreeNodeRequest.getIncludedNodeTypes().add(CodeTreeNodeType.FOLDER);
	List<CodeTreeNode> nodes = protexServerWrapper.getInternalApiWrapper()
		.getCodeTreeApi()
		.getCodeTreeNodes(projectId, "/", codeTreeNodeRequest);
	return nodes;
    }

    /**
     * Are there pending IDs in the tree?
     */
    public boolean hasPendingIds() throws SdkFault {
	CodeTreeNodeRequest codeTreeNodeRequest = new CodeTreeNodeRequest();
	codeTreeNodeRequest.setDepth(-1);
	codeTreeNodeRequest.setIncludeParentNode(true);
	codeTreeNodeRequest.getIncludedNodeTypes().add(
		CodeTreeNodeType.EXPANDED_ARCHIVE);
	codeTreeNodeRequest.getIncludedNodeTypes().add(CodeTreeNodeType.FILE);
	codeTreeNodeRequest.getIncludedNodeTypes().add(CodeTreeNodeType.FOLDER);

	codeTreeNodeRequest.getCounts()
		.add(NodeCountType.PENDING_ID_CODE_MATCH);
	List<CodeTreeNode> nodes = protexServerWrapper.getInternalApiWrapper()
		.getCodeTreeApi()
		.getCodeTreeNodes(projectId, "/", codeTreeNodeRequest);

	for (CodeTreeNode node : nodes) {
	    List<NodeCount> nodeCounts = node.getNodeCounts();
	    for (NodeCount nodeCount : nodeCounts) {
		NodeCountType nodeCountType = nodeCount.getCountType();
		if (nodeCountType == NodeCountType.PENDING_ID_CODE_MATCH) {
		    if (nodeCount.getCount() > 0) {
			return true;
		    } else {
			return false;
		    }
		}
	    }
	}
	return false;
    }

    /**
     * Refresh the BOM
     *
     * @throws SdkFault
     */
    public void refreshBom() throws SdkFault {
	if (!doRefresh) {
	    log.info("Skipping BOM refresh as requested");
	    return;
	}
	if (identifier.isFinalBomRefreshRequired()) {
	    log.info("Refreshing BOM.");
	    protexServerWrapper.getInternalApiWrapper().getBomApi()
		    .refreshBom(projectId, true, false);
	}
    }

    /**
     * Find out if the identifier being used requires multiple passes on the
     * file tree to make all identifications
     *
     * @return true if multiple passes are required to process all pending IDs
     */
    public boolean isMultiPassIdStrategy() {
	return identifier.isMultiPassIdStrategy();
    }

}
