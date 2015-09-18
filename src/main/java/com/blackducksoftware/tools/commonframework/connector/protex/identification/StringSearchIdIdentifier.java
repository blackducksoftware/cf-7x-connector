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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.ComponentKey;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.StringSearchIdentificationRequest;

public class StringSearchIdIdentifier implements Identifier {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private String programName;
    private ProtexIdUtils protexUtils;

    public StringSearchIdIdentifier(String programName) {
	this.programName = programName;
    }

    @Override
    public void setProtexUtils(ProtexIdUtils protexUtils) {
	this.protexUtils = protexUtils;
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
    public void makeStringSearchIdentificationOnFile(String path,
	    StringSearchDiscovery stringSearchDiscoveryTarget,
	    String componentId, String componentVersionId) throws SdkFault {

	StringSearchIdentificationRequest idRequest = new StringSearchIdentificationRequest();
	idRequest.setFolderLevelIdentification(false);
	idRequest.setComment("Code Match Id-ed by " + programName + " at "
		+ new Date());
	ComponentKey componentKey = new ComponentKey();
	componentKey.setComponentId(componentId);
	componentKey.setVersionId(componentVersionId);
	idRequest.setIdentifiedComponentKey(componentKey);

	idRequest.setIdentifiedUsageLevel(UsageLevel.COMPONENT);
	idRequest.setStringSearchId(stringSearchDiscoveryTarget
		.getStringSearchId());
	idRequest.getMatchLocations().addAll(
		stringSearchDiscoveryTarget.getMatchLocations());
	log.info("Adding String Search Identification for " + path + ": "
		+ componentId + " version " + componentVersionId
		+ " match type "
		+ stringSearchDiscoveryTarget.getDiscoveryType());
	protexUtils
		.getProtexServerWrapper()
		.getInternalApiWrapper()
		.getIdentificationApi()
		.addStringSearchIdentification(protexUtils.getProjectId(),
			path, idRequest, BomRefreshMode.SYNCHRONOUS);
    }

    @Override
    public boolean isFinalBomRefreshRequired() {
	// Using this strategy, no need to refresh BOM at the end
	return false;
    }

    @Override
    public boolean isMultiPassIdStrategy() {
	return false;
    }

    @Override
    public void makeIdentificationOnFile(String path, Discovery target)
	    throws SdkFault {
	throw new UnsupportedOperationException(
		"makeIdentificationOnFile method not supported");

    }
}
