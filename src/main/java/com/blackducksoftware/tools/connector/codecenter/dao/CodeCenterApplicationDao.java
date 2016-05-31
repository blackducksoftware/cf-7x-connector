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
package com.blackducksoftware.tools.connector.codecenter.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.attribute.AttributeApi;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.VulnerabilityStatusNameToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentReleaseIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.RequestApi;
import com.blackducksoftware.sdk.codecenter.request.data.RequestIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestVulnerabilityUpdate;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.RequestVulnerabilitySummary;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityIdToken;
import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.ApplicationDao;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.CodeCenterDaoConfigManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ApplicationPojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ApplicationPojoImpl;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentPojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentPojoImpl;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentUsePojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.ComponentUsePojoImpl;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.VulnerabilityPojo;
import com.blackducksoftware.tools.commonframework.standard.codecenter.pojo.VulnerabilityPojoImpl;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;
import com.blackducksoftware.tools.connector.codecenter.ICodeCenterServerWrapper;

/**
 * Read / write Code Center applications / components / vulnerabilities /
 * vulnerability metadata. This class does the Code Center SDK-specific work,
 * interacting with it's clients via POJOs. If/when they are accessed, it
 * lazy-loads component_uses+components, and vulnerabilities for the app.
 * updateCompUseVulnData() writes vulnerability metadata (specific to this use)
 * to Code Center.
 *
 * @author sbillings
 *
 */
public class CodeCenterApplicationDao implements ApplicationDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    static final int EST_NUM_VULNS_PER_COMPONENT = 40; // a rough approx for

    // initial hashmap sizing
    static final String STATUS_NOT_SET_VALUE = "Unreviewed";

    // Application details
    private ApplicationPojo applicationPojo;

    private Application app;

    // Mapping of ComponentUse to Component
    private Map<ComponentUsePojo, ComponentPojo> compUseToCompMap;

    // Caches for the Code Center-specific (vs. POJO) representations of the
    // data
    // Each is fully populated the first time that "level" (component, vuln)
    // gets accessed
    private Map<ComponentUsePojo, RequestSummary> compUseCache; // compUse

    // and
    // comp
    // are...
    private Map<ComponentPojo, Component> compCache; // ... populated

    // together
    private Map<VulnerabilityPojo, RequestVulnerabilitySummary> vulnCache;

    // Attribute IDs for the attributes we'll need to collect values for
    private Map<String, String> appAttrNameIdMap = new HashMap<String, String>(
            8);

    // references to each API we'll use, just for convenience
    private ApplicationApi applicationApi;

    private RequestApi requestApi;

    private ColaApi colaApi;

    private AttributeApi attrApi;

    // ccimporter does not want to skip non KB components
    // snapshot does want to skip them. So both modes are supported.
    private boolean skipNonKbComponents = true;

    /**
     * Use this constructor when you need this class to construct its own
     * CodeCenterServerWrapper. If your config object returns a list of custom
     * attribute names via the .getApplicationAttributeNames() method, and you
     * use this constructor, the ApplicationPojo's this class generates will
     * include the values of those custom attributes.
     *
     * @param ccServerWrapper
     * @throws Exception
     */
    public CodeCenterApplicationDao(CodeCenterDaoConfigManager config,
            String appName, String appVersion) throws Exception {
        skipNonKbComponents = config.isSkipNonKbComponents();
        ServerBean serverBean = config.getServerBean(APPLICATION.CODECENTER);
        initCodeCenterApis(serverBean, (ConfigurationManager) config);

        // Collect the attr IDs for the custom attributes we will need
        for (String attrName : config.getApplicationAttributeNames()) {
            AttributeNameToken attrNameObj = new AttributeNameToken();
            attrNameObj.setName(attrName);
            String attrId = attrApi.getAttribute(attrNameObj).getId().getId();
            appAttrNameIdMap.put(attrName, attrId);
        }

        loadApplication(appName, appVersion);
        initSupportingData();
    }

    /**
     * Use this constructor when you already have a CodeCenterServerWrapper. If
     * you use this constructor, the ApplicationPojo's this class generates will
     * NOT include the values of those custom attributes.
     *
     * @param ccServerWrapper
     * @throws Exception
     */
    public CodeCenterApplicationDao(ICodeCenterServerWrapper ccServerWrapper,
            boolean skipNonKbComponents, Application app) throws Exception {
        this.skipNonKbComponents = skipNonKbComponents;
        initCodeCenterApis(ccServerWrapper);
        this.app = app;
        initSupportingData();
    }

    /**
     * Get the POJO for the application this class manages.
     */
    @Override
    public ApplicationPojo getApplication() throws Exception {
        return applicationPojo;
    }

    /**
     * Get all of the componentUses for this application.
     */
    @Override
    public List<ComponentUsePojo> getComponentUses() throws SdkFault {
        log.debug("Getting requests for app " + app.getName());
        populateCompCaches();

        List<ComponentUsePojo> compUsePojos = new ArrayList<ComponentUsePojo>(
                compUseCache.size());
        compUsePojos.addAll(compUseCache.keySet());
        return compUsePojos;
    }

    /**
     * Get all of the components for this application in a sorted list.
     */
    @Override
    public SortedSet<ComponentPojo> getComponentsSorted() throws SdkFault {
        List<ComponentPojo> pojoList = getComponents();
        SortedSet<ComponentPojo> pojoSortedSet = new TreeSet<ComponentPojo>();
        pojoSortedSet.addAll(pojoList);
        return pojoSortedSet;
    }

    /**
     * Get all of the components for this app.
     */
    @Override
    public List<ComponentPojo> getComponents() throws SdkFault {
        populateCompCaches();

        List<ComponentPojo> componentPojos = new ArrayList<ComponentPojo>(
                compCache.size());
        componentPojos.addAll(compCache.keySet());
        return componentPojos;
    }

    /**
     * Get the component for the given componentUse.
     */
    @Override
    public ComponentPojo getComponent(ComponentUsePojo componentUse)
            throws SdkFault {
        populateCompCaches();
        return compUseToCompMap.get(componentUse);
    }

    /**
     * Get all of the vulnerabilities for the given component / componentUse.
     */
    @Override
    public List<VulnerabilityPojo> getVulnerabilities(ComponentPojo compPojo,
            ComponentUsePojo compUsePojo) throws Exception {
        populateVulnCache();
        List<VulnerabilityPojo> vulnPojos = new ArrayList<VulnerabilityPojo>(
                vulnCache.size());

        // add to list only those vulnerabilities from cache with matching
        // compUse ID
        for (VulnerabilityPojo vulnerabilityPojo : vulnCache.keySet()) {
            if (vulnerabilityPojo.getCompUseId().equals(compUsePojo.getId())) {
                vulnPojos.add(vulnerabilityPojo);
            }
        }
        return vulnPojos;
    }

    /**
     * Get all of the components for this application in a sorted list.
     */
    @Override
    public SortedSet<VulnerabilityPojo> getVulnerabilitiesSorted(
            ComponentPojo compPojo, ComponentUsePojo compUsePojo)
            throws Exception {
        List<VulnerabilityPojo> pojoList = getVulnerabilities(compPojo,
                compUsePojo);
        SortedSet<VulnerabilityPojo> pojoSortedSet = new TreeSet<VulnerabilityPojo>();
        pojoSortedSet.addAll(pojoList);
        return pojoSortedSet;
    }

    /**
     * Update the componentUse-specific vulnerability metadata in Code Center
     * for the given compUse/vuln.
     */
    @Override
    public void updateCompUseVulnData(ComponentUsePojo compUse,
            VulnerabilityPojo vuln) throws Exception {
        populateVulnCache();

        RequestVulnerabilitySummary requestVulnerabilitySummary = vulnCache
                .get(vuln);
        if (requestVulnerabilitySummary == null) {
            String msg = "Vulnerability " + vuln + " not found in cache";
            log.error(msg);
            throw new Exception(msg);
        }

        RequestVulnerabilityUpdate requestVulnerabilityUpdate = new RequestVulnerabilityUpdate();

        RequestIdToken requestIdToken = new RequestIdToken();
        requestIdToken.setId(compUse.getId());
        requestVulnerabilityUpdate.setRequestId(requestIdToken);

        VulnerabilityIdToken vulnerabilityIdToken = new VulnerabilityIdToken();
        vulnerabilityIdToken.setId(vuln.getId());
        requestVulnerabilityUpdate.setVulnerability(vulnerabilityIdToken);

        requestVulnerabilityUpdate.setActualRemediateDate(vuln
                .getActualRemediationDate());
        requestVulnerabilityUpdate.setTargetRemediateDate(vuln
                .getTargetRemediationDate());
        requestVulnerabilityUpdate.setComment(vuln.getStatusComment());

        if (!CodeCenterApplicationDao.STATUS_NOT_SET_VALUE.equals(vuln
                .getStatus())) {
            VulnerabilityStatusNameToken vulnerabilityStatusNameToken = new VulnerabilityStatusNameToken();
            vulnerabilityStatusNameToken.setName(vuln.getStatus());
            requestVulnerabilityUpdate
                    .setVulnerabilityStatus(vulnerabilityStatusNameToken);
        }

        try {
            requestApi.setVulnerabilityStatus(requestVulnerabilityUpdate);
        } catch (SdkFault e) {
            String msg = "requestApi.setVulnerabilityStatus(requestVulnerabilityUpdate) failed: "
                    + e.getMessage();
            log.error(msg);
            throw e;
        }
    }

    // private methods

    private void loadApplication(String appName, String version)
            throws SdkFault {

        ApplicationNameVersionToken token = new ApplicationNameVersionToken();
        token.setName(appName);
        token.setVersion(version);
        app = applicationApi.getApplication(token);

        log.debug("Application ID: " + app.getId().getId());
        log.debug("Application Component ID: "
                + app.getApplicationComponentId().getId());
    }

    private void initSupportingData() {
        Map<String, String> appAttrNameValueMap = new HashMap<String, String>(8);
        collectCustomAttrs(appAttrNameValueMap, app);

        applicationPojo = new ApplicationPojoImpl(app.getId().getId(),
                app.getName(), app.getVersion(), app.getDescription(),
                appAttrNameValueMap);
    }

    private void populateVulnCache() throws Exception {
        if (vulnCache != null) {
            return;
        }

        populateCompCaches();

        vulnCache = new HashMap<VulnerabilityPojo, RequestVulnerabilitySummary>(
                compUseCache.size()
                        * CodeCenterApplicationDao.EST_NUM_VULNS_PER_COMPONENT);

        for (ComponentUsePojo compUsePojo : compUseCache.keySet()) {
            String compUseId = compUsePojo.getId();

            RequestIdToken requestIdToken = new RequestIdToken();
            requestIdToken.setId(compUseId);
            RequestVulnerabilityPageFilter filter = new RequestVulnerabilityPageFilter();
            filter.setFirstRowIndex(0);
            filter.setLastRowIndex(Integer.MAX_VALUE);
            List<RequestVulnerabilitySummary> requestVulnerabilitySummaries = requestApi
                    .searchVulnerabilities(requestIdToken, filter);

            for (RequestVulnerabilitySummary requestVulnerabilitySummary : requestVulnerabilitySummaries) {
                VulnerabilityPojo vulnerabilityPojo = new VulnerabilityPojoImpl(
                        requestVulnerabilitySummary.getId().getId(),
                        compUsePojo.getId(), requestVulnerabilitySummary
                                .getName().getName(),
                        requestVulnerabilitySummary.getDescription(),
                        requestVulnerabilitySummary.getSeverity().toString(),
                        requestVulnerabilitySummary.getPublished(), null, 0L,
                        requestVulnerabilitySummary.getReviewStatusName()
                                .getName(),
                        requestVulnerabilitySummary.getComments(),
                        requestVulnerabilitySummary.getTargetRemediateDate(),
                        requestVulnerabilitySummary.getActualRemediateDate());

                vulnCache.put(vulnerabilityPojo, requestVulnerabilitySummary);
            }
        }
    }

    private void populateCompCaches() throws SdkFault {
        if (compUseCache != null) {
            return;
        }

        String applicationName = app.getName();

        RequestPageFilter pageFilter = new RequestPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        log.debug("Fetching requests for app " + applicationName);
        List<RequestSummary> requests = applicationApi
                .searchApplicationRequests(app.getId(), null, pageFilter);

        compUseCache = new HashMap<ComponentUsePojo, RequestSummary>(
                requests.size());
        compUseToCompMap = new HashMap<ComponentUsePojo, ComponentPojo>(
                requests.size());
        compCache = new HashMap<ComponentPojo, Component>(requests.size());

        for (RequestSummary request : requests) {

            if (skipNonKbComponents) {
                // Peek ahead at the component: Is comp in KB? If not, skip
                log.debug("Looking up catalog component for app");
                ComponentIdToken componentIdToken = request.getComponentId();
                Component component = colaApi
                        .getCatalogComponent(componentIdToken);
                KbComponentReleaseIdToken kbComponentReleaseIdToken1 = component
                        .getKbReleaseId();
                if (kbComponentReleaseIdToken1 == null) {
                    log.debug("\tSkipping " + applicationName + ": "
                            + component.getName() + " "
                            + component.getVersion()
                            + "; The KB Component Release ID token is null");
                    continue;
                }
                log.debug("Found catalog component for app");
            }

            // Generate and cache ComponentUsePojo
            ComponentUsePojo compUsePojo = new ComponentUsePojoImpl(request
                    .getId().getId());
            compUseCache.put(compUsePojo, request);

            // Get the component
            ComponentIdToken compIdToken = request.getComponentId();
            Component component = colaApi.getCatalogComponent(compIdToken);

            // Generate the CodeCenterComponentPojo
            String kbComponentId = null;
            KbComponentIdToken kbComponentIdToken = component
                    .getKbComponentId();
            if (kbComponentIdToken != null) {
                kbComponentId = kbComponentIdToken.getId();
            }

            ComponentPojo componentPojo = new ComponentPojoImpl(component
                    .getId().getId(), component.getName(),
                    component.getVersion(), kbComponentId);
            compCache.put(componentPojo, component);
            compUseToCompMap.put(compUsePojo, componentPojo);
        }
    }

    private void collectCustomAttrs(Map<String, String> appAttrNameValueMap,
            Application app) {
        List<AttributeValue> attrValues = app.getAttributeValues();
        for (AttributeValue attrValue : attrValues) {
            AttributeIdToken attrId = (AttributeIdToken) (attrValue
                    .getAttributeId());
            if ((attrValue.getValues() != null)
                    && (attrValue.getValues().size() > 0)) {
                for (String appAttrName : appAttrNameIdMap.keySet()) {
                    if (appAttrNameIdMap.get(appAttrName)
                            .equals(attrId.getId())) {
                        if (attrValue.getValues().size() > 1) {
                            log.warn("*** WARNING: app attr " + appAttrName
                                    + " has " + attrValue.getValues().size()
                                    + " values; only using the first one");
                        }
                        String attrValueString = attrValue.getValues().get(0);
                        log.debug("Putting app attr " + appAttrName + " value "
                                + attrValueString);
                        appAttrNameValueMap.put(appAttrName, attrValueString);
                    }
                }
            }
        }
    }

    private void initCodeCenterApis(ServerBean serverBean,
            ConfigurationManager config) throws Exception {
        CodeCenterServerWrapper ccServerWrapper = new CodeCenterServerWrapper(
                serverBean, config);
        initCodeCenterApis(ccServerWrapper);
    }

    private void initCodeCenterApis(ICodeCenterServerWrapper ccServerWrapper) {
        applicationApi = ccServerWrapper.getInternalApiWrapper()
                .getApplicationApi();
        colaApi = ccServerWrapper.getInternalApiWrapper().getColaApi();
        attrApi = ccServerWrapper.getInternalApiWrapper().getAttributeApi();
        requestApi = ccServerWrapper.getInternalApiWrapper().getRequestApi();
    }

    @Override
    public String toString() {
        return "CodeCenterDao [app=" + app.getName() + " / " + app.getVersion()
                + "]";
    }

}
