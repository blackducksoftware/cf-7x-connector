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
package com.blackducksoftware.tools.connector.protex.license;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.license.GlobalLicense;
import com.blackducksoftware.sdk.protex.obligation.AssignedObligation;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;

/**
 * Provide cached license data retrieved from Protex.
 *
 * @author sbillings
 *
 */
public class LicenseManager implements ILicenseManager<ProtexLicensePojo> {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private static final String LICENSE_TEXT_CHAR_ENCODING = "UTF-8";

    private final ProtexAPIWrapper apiWrapper;

    private final Map<String, GlobalLicense> licenseByNameCache = new HashMap<>();

    private final Map<String, GlobalLicense> licenseByIdCache = new HashMap<>();

    public LicenseManager(ProtexAPIWrapper apiWrapper) {
        this.apiWrapper = apiWrapper;
    }

    /**
     * Get license by name.
     *
     * If it's in the cache, return that. Else get it from Protex.
     *
     * @param licenseName
     * @return
     * @throws CommonFrameworkException
     */
    @Override
    public ProtexLicensePojo getLicenseByName(String licenseName)
            throws CommonFrameworkException {

        if (licenseByNameCache.containsKey(licenseName)) {
            return createPojo(licenseByNameCache.get(licenseName));
        }

        GlobalLicense globalLicense;
        try {
            globalLicense = apiWrapper.getLicenseApi().getLicenseByName(
                    licenseName);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting license for license name " + licenseName
                            + ": " + e.getMessage());
        }

        addToCache(globalLicense);
        ProtexLicensePojo licPojo = createPojo(globalLicense);
        return licPojo;
    }

    /**
     * Get license by ID.
     *
     * If it's in the cache, return that. Else get it from Protex.
     *
     * @param licenseId
     * @return
     * @throws CommonFrameworkException
     */
    @Override
    public ProtexLicensePojo getLicenseById(String licenseId)
            throws CommonFrameworkException {

        if (licenseByIdCache.containsKey(licenseId)) {
            return createPojo(licenseByIdCache.get(licenseId));
        }

        GlobalLicense globalLicense;
        try {
            log.info("Fetching license for license ID: " + licenseId);
            globalLicense = apiWrapper.getLicenseApi()
                    .getLicenseById(licenseId);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting license for license ID " + licenseId + ": "
                            + e.getMessage());
        }

        addToCache(globalLicense);
        return createPojo(globalLicense);
    }

    // Private methods

    private void addToCache(GlobalLicense lic) {
        licenseByIdCache.put(lic.getLicenseId(), lic);
        licenseByNameCache.put(lic.getName(), lic);
    }

    private ProtexLicensePojo createPojo(GlobalLicense lic)
            throws CommonFrameworkException {
        String licenseText = "";

        if (lic.getText() != null) {
            try {
                licenseText = new String(lic.getText(), LICENSE_TEXT_CHAR_ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new CommonFrameworkException(
                        "Error converting license text bytes to a String interpreting them using character encoding "
                                + LICENSE_TEXT_CHAR_ENCODING
                                + ": "
                                + e.getMessage());
            }
        }

        List<AssignedObligation> obligations;
        try {
            obligations = apiWrapper.getLicenseApi().getLicenseObligations(
                    lic.getLicenseId());
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting obligations for license " + lic.getName()
                            + ": " + e.getMessage());
        }
        List<String> obligationIds = new ArrayList<>(obligations.size());
        for (AssignedObligation obligation : obligations) {
            obligationIds.add(obligation.getObligationId());
        }

        ProtexLicensePojo licPojo = new ProtexLicensePojo(lic.getLicenseId(),
                lic.getName(), lic.getComment(), lic.getExplanation(),
                lic.getSuffix(), ProtexLicensePojo.toApprovalState(lic
                        .getApprovalState()), licenseText, obligationIds);
        return licPojo;
    }

}
