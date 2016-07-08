/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex;

import java.util.HashMap;
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
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;

/**
 * Code Tree Assister Only to be retrieved from the Protex Wrapper
 *
 * @author akamen
 *
 */
public class CodeTreeHelper extends ApiHelper {

	private static final Logger log = LoggerFactory.getLogger(CodeTreeHelper.class.getName());

	private static CodeTreeApi codeTreeApi;

	private static final String ROOT = "/";

	public CodeTreeHelper(final ProtexAPIWrapper apiWrapper) {
		super(apiWrapper);
		init();
	}

	private void init() {
		codeTreeApi = apiWrapper.getCodeTreeApi();

	}

	public Long getTotalCountByType(final ProjectPojo project, final NodeCountType type)
			throws CommonFrameworkException {
		try {
			return checkCountForPending(project.getProjectKey(), ROOT, type);
		} catch (final CommonFrameworkException e) {
			throw new CommonFrameworkException("Error while retrieving count by type: " + e.getMessage());
		}
	}

	/**
	 * Based on a project, return the total pending id count. Where total
	 * includes all types: Code, Search, Dependency.
	 *
	 * @param project
	 * @return
	 */
	public Long getTotalPendingIDCount(final ProjectPojo project) {
		return getTotalPendingIDCount(project, ROOT);
	}

	/**
	 * Based on a project, return the total pending id count for a given subset
	 * of the tree, specified by a path. Where total includes all types: Code,
	 * Search, Dependency.
	 *
	 * @param project
	 * @return
	 */
	public Long getTotalPendingIDCount(final ProjectPojo project, final String path) {
		long totalCount = -1;
		log.debug("Getting Total Pending Count for Project: " + project.getProjectName());

		try {
			totalCount = checkCountForPending(project.getProjectKey(), path, NodeCountType.PENDING_ID_ALL);
			log.debug("Retrieved Total Pending Count for Project: " + totalCount);
		} catch (final Exception e) {
			log.error("Could not get total pending count: " + e.getMessage());
		}

		return totalCount;
	}

	/**
	 * Returns a map of counts, keys of type NodeCountType
	 *
	 * @param project
	 * @return
	 */
	public Map<NodeCountType, Long> getAllCountsForProjects(final ProjectPojo project) {
		Map<NodeCountType, Long> mappedCounts = new HashMap<NodeCountType, Long>();
		log.debug("Getting Total File Count for Project: " + project.getProjectName());

		try {
			mappedCounts = checkCountForTypes(project.getProjectKey(), ROOT, NodeCountType.values());
			log.debug("Retrieved all mapped counts: " + mappedCounts);
		} catch (final Exception e) {
			log.error("Could not get all mapped counts:  " + e.getMessage());
		}

		return mappedCounts;
	}

	// Check the count for specific types
	private long checkCountForPending(final String projectKey, final String path, final NodeCountType... types)
			throws CommonFrameworkException {
		final CodeTreeNodeRequest ctrRequest = new CodeTreeNodeRequest();

		long totalCount = 0;
		try {

			final Map<NodeCountType, Long> map = checkCountForTypes(projectKey, path, types);

			final long pendingIdCount = map.get(NodeCountType.PENDING_ID_ALL);
			if (pendingIdCount > 0) {
				totalCount = pendingIdCount;

			}

		} catch (final Exception e) {
			throw new CommonFrameworkException("Fatal, count not determine count: " + e.getMessage());
		}
		return totalCount;
	}

	// Check the count for specific types
	private static Map<NodeCountType, Long> checkCountForTypes(final String projectKey, final String path,
			final NodeCountType... types) throws CommonFrameworkException {

		final CodeTreeNodeRequest ctrRequest = new CodeTreeNodeRequest();
		Map<NodeCountType, Long> map = new HashMap<NodeCountType, Long>();

		// github issue #63: Steve B: Changed from SINGLE_NODE to INFINITE_DEPTH
		ctrRequest.setDepth(CodeTreeUtilities.INFINITE_DEPTH);

		ctrRequest.setIncludeParentNode(true);
		final List<CodeTreeNodeType> nodeTypes = ctrRequest.getIncludedNodeTypes();
		nodeTypes.add(CodeTreeNodeType.FILE);

		final List<NodeCountType> countsForCheck = ctrRequest.getCounts();
		for (final NodeCountType type : types) {
			countsForCheck.add(type);
		}

		try {
			final List<CodeTreeNode> nodesWithCounts = codeTreeApi.getCodeTreeNodes(projectKey, path, ctrRequest);
			for (final CodeTreeNode node : nodesWithCounts) {
				map = CodeTreeUtilities.getNodeCountMap(node);
			}

		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Fatal, count not get map with counts: " + e.getMessage());
		}
		return map;
	}

}
