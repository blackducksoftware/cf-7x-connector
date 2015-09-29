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
import com.blackducksoftware.sdk.protex.common.StringSearchPatternOriginType;
import com.blackducksoftware.sdk.protex.component.Component;
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
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.tools.commonframework.connector.protex.ProtexServerWrapper;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.commonframework.standard.protex.identification.IdentificationMade;

public class ProtexIdUtils {
    private static final Logger log = LoggerFactory
	    .getLogger(ProtexIdUtils.class.getName());

    private final Identifier identifier;
    private ProtexServerWrapper<ProtexProjectPojo> protexServerWrapper;
    private final Project project;

    private final Collection<IdentificationMade> identificationsMade = new ArrayList<IdentificationMade>();
    private final boolean doRefresh;

    /**
     * Constructor
     *
     * @throws Exception
     *             upon error connecting to or using Protex
     */
    public ProtexIdUtils(
	    ProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
	    Identifier identifier, Project project, boolean doRefresh)
	    throws Exception {
	this.doRefresh = doRefresh;

	this.protexServerWrapper = protexServerWrapper;
	this.project = project;

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
	return project.getProjectId();
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
     * Performs a Code Match identification Code Match attempts to do a code
     * match identification, as opposed to declare file.
     *
     * @param path
     *            the path to the file
     * @param target
     *            the discovery to make the identification with
     * @throws SdkFault
     */
    public void makeStringSearchId(String path, Discovery discoveryTarget,
	    String componentName, String componentVersion) throws SdkFault {
	StringSearchDiscovery target = (StringSearchDiscovery) discoveryTarget;

	Component component = getComponentByName(componentName,
		componentVersion);
	String componentNameId = component.getComponentKey().getComponentId();
	String componentVersionId = component.getComponentKey().getVersionId();

	identifier.makeStringSearchIdentificationOnFile(path, target,
		componentNameId, componentVersionId);

	IdentificationMade idMade = new IdentificationMade(path, target
		.getMatchLocations().get(0).getFirstLine(), 0, componentName,
		componentVersion, "<unknown>", 0); // TODO ugly

	identificationsMade.add(idMade);
	log.debug("Added Identification for " + idMade);
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
    public CodeMatchDiscovery bestMatch(List<Discovery> codeMatchDiscoveries)
	    throws SdkFault {
	int maxScore = 0;
	CodeMatchDiscovery bestCodeMatchDiscovery = null;
	for (Discovery match : codeMatchDiscoveries) {
	    CodeMatchDiscovery codeMatch = (CodeMatchDiscovery) match;
	    int thisScore = codeMatch.getMatchRatioAsPercent();
	    String versionString = getComponentVersionString(codeMatch);

	    ComponentKey key = codeMatch.getDiscoveredComponentKey();

	    log.debug("Code Match Discovery: " + key.getComponentId() + "/"
		    + versionString + "; score: " + thisScore + "; ID status: "
		    + codeMatch.getIdentificationStatus().toString());

	    if (codeMatch.getIdentificationStatus() == IdentificationStatus.PENDING_IDENTIFICATION) {

		if (thisScore > maxScore) {
		    log.debug("\tThis one is the best so far");
		    bestCodeMatchDiscovery = codeMatch;
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
    public String getComponentVersionString(CodeMatchDiscovery match) {
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

    public Component getComponentByName(String componentName,
	    String componentVersion) throws SdkFault {
	log.info("Looking up: " + componentName + " / " + componentVersion);
	List<Component> components = protexServerWrapper
		.getInternalApiWrapper().getComponentApi()
		.getComponentsByName(componentName, componentVersion);
	switch (components.size()) {
	case 0:
	    return null;
	case 1:
	    return components.get(0);
	default:
	    log.warn("There are more than one component with name "
		    + componentName + " / version " + componentVersion);
	    return components.get(0);
	}
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
		.getInternalApiWrapper()
		.getDiscoveryApi()
		.getCodeMatchDiscoveries(project.getProjectId(), nodes,
			codeMatchTypes);
	return codeMatchDiscoveries;
    }

    /**
     * Get the string search discoveries for a file.
     *
     * @param tree
     * @return
     * @throws SdkFault
     */
    public List<StringSearchDiscovery> getStringSearchDiscoveries(
	    List<CodeTreeNode> nodes) throws SdkFault {

	List<StringSearchPatternOriginType> patternTypes = new ArrayList<>();
	patternTypes.add(StringSearchPatternOriginType.CUSTOM);
	patternTypes.add(StringSearchPatternOriginType.PROJECT_LOCAL);
	patternTypes.add(StringSearchPatternOriginType.STANDARD);

	List<StringSearchDiscovery> codeMatchDiscoveries = protexServerWrapper
		.getInternalApiWrapper()
		.getDiscoveryApi()
		.getStringSearchDiscoveries(project.getProjectId(), nodes,
			patternTypes);
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
	List<CodeTreeNode> nodes = protexServerWrapper
		.getInternalApiWrapper()
		.getCodeTreeApi()
		.getCodeTreeNodes(project.getProjectId(), "/",
			codeTreeNodeRequest);
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
	List<CodeTreeNode> nodes = protexServerWrapper
		.getInternalApiWrapper()
		.getCodeTreeApi()
		.getCodeTreeNodes(project.getProjectId(), "/",
			codeTreeNodeRequest);

	for (CodeTreeNode node : nodes) {
	    List<NodeCount> nodeCounts = node.getNodeCounts();
	    for (NodeCount nodeCount : nodeCounts) {
		NodeCountType nodeCountType = nodeCount.getCountType();
		if (nodeCountType == NodeCountType.PENDING_ID_CODE_MATCH) {
		    if (nodeCount.getCount() > 0) {
			return true;
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
		    .refreshBom(project.getProjectId(), true, false);
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
