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
package com.blackducksoftware.tools.commonframework.connector.protex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeApi;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNode;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeRequest;
import com.blackducksoftware.sdk.protex.project.codetree.CodeTreeNodeType;
import com.blackducksoftware.sdk.protex.project.codetree.NodeCountType;
import com.blackducksoftware.sdk.protex.util.CodeTreeUtilities;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;

/**
 * Code Tree Assister Only to be retrieved from the Protex Wrapper
 *
 * @author akamen
 *
 */
public class CodeTreeHelper extends ApiHelper {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private static CodeTreeApi codeTreeApi;

    public CodeTreeHelper(ProtexAPIWrapper apiWrapper) {
	super(apiWrapper);
	init();
    }

    private void init() {
	codeTreeApi = apiWrapper.getCodeTreeApi();

    }

    /**
     * Based on a project, return the total pending id count. Where total
     * includes all types: Code, Search, Dependency.
     *
     * @param project
     * @return
     */
    public Long getTotalPendingIDCount(ProjectPojo project) {
	long totalCount = -1;
	log.debug("Getting Total Pending Count for Project: "
		+ project.getProjectName());

	try {
	    totalCount = checkCountForPending(project.getProjectKey(), "/",
		    NodeCountType.PENDING_ID_ALL);
	    log.debug("Retrieved Total Pending Count for Project: "
		    + totalCount);
	} catch (Exception e) {
	    log.error("Could not get total pending count: " + e.getMessage());
	}

	return totalCount;
    }

    /**
     * Returns all the file paths associated with this component
     *
     * @param componentId
     * @return
     */
    public List<String> getAllFilePathsForComponent(String componentId) {
	List<String> filePaths = new ArrayList<String>();

	try {

	} catch (Exception e) {
	    log.error("Unable to determine file paths for component: "
		    + componentId, e);
	}

	return filePaths;
    }

    // Check the count for specific types
    private static long checkCountForPending(String projectKey, String path,
	    NodeCountType... types) throws Exception {
	CodeTreeNodeRequest ctrRequest = new CodeTreeNodeRequest();
	ctrRequest.setDepth(CodeTreeUtilities.INFINITE_DEPTH);
	ctrRequest.setIncludeParentNode(true);
	List<CodeTreeNodeType> nodeTypes = ctrRequest.getIncludedNodeTypes();
	nodeTypes.add(CodeTreeNodeType.FILE);

	List<NodeCountType> countsForCheck = ctrRequest.getCounts();
	for (NodeCountType type : types) {
	    countsForCheck.add(type);
	}

	long totalCount = 0;
	try {
	    List<CodeTreeNode> nodes = codeTreeApi.getCodeTreeNodes(projectKey,
		    path, ctrRequest);
	    for (CodeTreeNode node : nodes) {
		Map<NodeCountType, Long> map = CodeTreeUtilities
			.getNodeCountMap(node);
		long pendingIdCount = map.get(NodeCountType.PENDING_ID_ALL);
		if (pendingIdCount > 0) {
		    totalCount = pendingIdCount;
		}
	    }

	} catch (SdkFault e) {
	    throw new Exception("Fatal, count not determine count: "
		    + e.getMessage());
	}
	return totalCount;
    }
}
