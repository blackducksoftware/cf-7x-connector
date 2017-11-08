/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.tools.connector.protex.identification;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationDirective;
import com.blackducksoftware.sdk.protex.project.codetree.identification.CodeMatchIdentificationRequest;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.ProtexServerWrapper;

public class CodeMatchIdIdentifier implements Identifier {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private String programName;
    private ProtexServerWrapper<ProtexProjectPojo> protexServerWrapper;
    private Project project;

    public CodeMatchIdIdentifier(
	    ProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
	    Project project, String programName) {
	this.protexServerWrapper = protexServerWrapper;
	this.project = project;
	this.programName = programName;
    }

    /**
     * Adds the given discovery as a code match identification. This will, in
     * general, leave no more pending code matches for only a subset of the
     * file.
     *
     * @param path
     * @param target
     * @throws SdkFault
     */
    @Override
    public void makeIdentificationOnFile(String path, Discovery target)
	    throws SdkFault {

	CodeMatchDiscovery codeMatchDiscoveryTarget = (CodeMatchDiscovery) target;

	CodeMatchIdentificationRequest idRequest = new CodeMatchIdentificationRequest();
	idRequest
		.setCodeMatchIdentificationDirective(CodeMatchIdentificationDirective.SNIPPET_AND_FILE);

	idRequest.setDiscoveredComponentKey(codeMatchDiscoveryTarget
		.getDiscoveredComponentKey());
	idRequest.setIdentifiedComponentKey(codeMatchDiscoveryTarget
		.getDiscoveredComponentKey());
	idRequest.setIdentifiedUsageLevel(UsageLevel.COMPONENT);

	idRequest.setComment("Code Match Id-ed by " + programName + " at "
		+ new Date());

	log.info("Adding Code Match Identification for "
		+ path
		+ ": "
		+ codeMatchDiscoveryTarget.getDiscoveredComponentKey()
			.getComponentId()
		+ " version "
		+ codeMatchDiscoveryTarget.getDiscoveredComponentKey()
			.getVersionId() + " match type "
		+ codeMatchDiscoveryTarget.getCodeMatchType());
	protexServerWrapper
		.getInternalApiWrapper()
		.getIdentificationApi()
		.addCodeMatchIdentification(project.getProjectId(), path,
			idRequest, BomRefreshMode.SYNCHRONOUS);
    }

    @Override
    public boolean isFinalBomRefreshRequired() {
	// Using this strategy, no need to refresh BOM at the end
	return false;
    }

    @Override
    public boolean isMultiPassIdStrategy() {
	return true;
    }

    @Override
    public void makeStringSearchIdentificationOnFile(String path,
	    StringSearchDiscovery target, String componentId,
	    String componentVersionId) throws SdkFault {
	throw new UnsupportedOperationException(
		"makeStringSearchIdentificationOnFile method not supported");
    }
}
