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

package com.blackducksoftware.tools.connector.protex.identification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.BomRefreshMode;
import com.blackducksoftware.sdk.protex.common.UsageLevel;
import com.blackducksoftware.sdk.protex.license.License;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.sdk.protex.project.Project;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.CodeMatchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;
import com.blackducksoftware.sdk.protex.project.codetree.identification.IdentificationRequest;
import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;

public class DeclareIdentifier implements Identifier {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private String programName;

    private IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper;

    private Project project;

    public DeclareIdentifier(
            IProtexServerWrapper<ProtexProjectPojo> protexServerWrapper,
            Project project, String programName) {
        this.protexServerWrapper = protexServerWrapper;
        this.project = project;
        this.programName = programName;
    }

    @Override
    public void makeIdentificationOnFile(String path, Discovery target)
            throws SdkFault {
        CodeMatchDiscovery codeMatchDiscoveryTarget = (CodeMatchDiscovery) target;

        IdentificationRequest declRequest = new IdentificationRequest();

        LicenseInfo lic = new LicenseInfo();

        String licenseId = codeMatchDiscoveryTarget.getMatchingLicenseInfo()
                .getLicenseId();
        License thisLicense = protexServerWrapper.getInternalApiWrapper()
                .getLicenseApi().getLicenseById(licenseId);
        if (thisLicense != null) {
            log.debug(codeMatchDiscoveryTarget.getDiscoveredComponentKey()
                    .getComponentId() + ": License: " + thisLicense.getName());
            lic.setLicenseId(thisLicense.getLicenseId());
            lic.setName(thisLicense.getName());
        }

        declRequest.setIdentifiedComponentKey(codeMatchDiscoveryTarget
                .getDiscoveredComponentKey());

        if (thisLicense != null) {
            declRequest.setIdentifiedLicenseInfo(lic);
        } else {
            declRequest.setIdentifiedLicenseInfo(null);
        }

        declRequest.setIdentifiedUsageLevel(UsageLevel.COMPONENT);

        declRequest.setComment("Declare Id-ed by " + programName
                + " at \" + new Date()");

        log.info("Adding Declaration for "
                + path
                + ": "
                + codeMatchDiscoveryTarget.getDiscoveredComponentKey()
                        .getComponentId());
        protexServerWrapper
                .getInternalApiWrapper()
                .getIdentificationApi()
                .addDeclaredIdentification(project.getProjectId(), path,
                        declRequest, BomRefreshMode.SKIP);
    }

    @Override
    public boolean isFinalBomRefreshRequired() {
        // Using this strategy, BOM must be refreshed at the end
        return true;
    }

    @Override
    public boolean isMultiPassIdStrategy() {
        return false;
    }

    @Override
    public void makeStringSearchIdentificationOnFile(String path,
            StringSearchDiscovery target, String componentId,
            String componentVersionId) throws SdkFault {
        throw new UnsupportedOperationException(
                "makeStringSearchIdentificationOnFile method not supported");
    }
}
