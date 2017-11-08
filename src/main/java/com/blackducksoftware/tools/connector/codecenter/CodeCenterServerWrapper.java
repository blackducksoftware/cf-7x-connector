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
package com.blackducksoftware.tools.connector.codecenter;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.config.ConfigConstants.APPLICATION;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.standard.common.ProjectPojo;
import com.blackducksoftware.tools.connector.codecenter.application.ApplicationManager;
import com.blackducksoftware.tools.connector.codecenter.application.IApplicationManager;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.component.ComponentManager;
import com.blackducksoftware.tools.connector.codecenter.component.ICodeCenterComponentManager;
import com.blackducksoftware.tools.connector.codecenter.externalId.ExternalIdManager;
import com.blackducksoftware.tools.connector.codecenter.externalId.IExternalIdManager;
import com.blackducksoftware.tools.connector.codecenter.license.LicenseManager;
import com.blackducksoftware.tools.connector.codecenter.protexservers.IProtexServerManager;
import com.blackducksoftware.tools.connector.codecenter.protexservers.ProtexServerManager;
import com.blackducksoftware.tools.connector.codecenter.request.IRequestManager;
import com.blackducksoftware.tools.connector.codecenter.request.RequestManager;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserManager;
import com.blackducksoftware.tools.connector.codecenter.user.ICodeCenterUserManager;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;

/**
 * The Class CodeCenterServerWrapper.
 */
public class CodeCenterServerWrapper implements ICodeCenterServerWrapper {

    /** The api wrapper. */
    private CodeCenterAPIWrapper apiWrapper;

    private IAttributeDefinitionManager attributeDefinitionManager;

    private IApplicationManager applicationManager;

    private IExternalIdManager externalIdManager;

    private ILicenseManager<LicensePojo> licenseManager;

    private IProtexServerManager protexServerManager;

    private ICodeCenterComponentManager componentManager;

    private ICodeCenterUserManager userManager;

    private IRequestManager requestManager;

    /** The config manager. */
    private ConfigurationManager configManager;

    /**
     * Deprecated as of 1.6.5 use the CodeCenterServerWrapper(ConfigurationManager manager) instead
     *
     * @param bean
     * @param manager
     * @throws Exception
     */
    @Deprecated
    public CodeCenterServerWrapper(ServerBean bean, ConfigurationManager manager) throws Exception {
        initialize(bean, manager);
    }

    public CodeCenterServerWrapper(ConfigurationManager manager)
            throws Exception {

        ServerBean ccBean = manager.getServerBean(APPLICATION.CODECENTER);
        if (ccBean == null) {
            throw new Exception("No Code Center connection available");
        }
        initialize(ccBean, manager);
    }

    /**
     * This replaces the old deprecated constructor
     *
     * @param bean
     * @param manager
     */
    private void initialize(ServerBean bean, ConfigurationManager manager) throws Exception {
        configManager = manager;
        apiWrapper = new CodeCenterAPIWrapper(bean, manager);

        // Low-level managers, no dependencies on other managers
        licenseManager = new LicenseManager(apiWrapper);
        attributeDefinitionManager = new AttributeDefinitionManager(apiWrapper);
        userManager = new CodeCenterUserManager(apiWrapper);
        ApplicationCache applicationCache = new ApplicationCache();
        requestManager = new RequestManager(apiWrapper, applicationCache);

        // Higher-level managers with dependencies on other managers
        componentManager = new ComponentManager(apiWrapper,
                attributeDefinitionManager, licenseManager);

        applicationManager = new ApplicationManager(apiWrapper,
                attributeDefinitionManager, componentManager, userManager,
                applicationCache);

        externalIdManager = new ExternalIdManager(apiWrapper);

        protexServerManager = new ProtexServerManager(apiWrapper, manager);
    }

    @Override
    public ProjectPojo getProjectByName(String projectName) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProjectPojo getProjectByID(String projectID) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> getProjects(Class<T> classType) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public CodeCenterAPIWrapper getInternalApiWrapper() {
        return apiWrapper;
    }

    @Override
    public ConfigurationManager getConfigManager() {
        return configManager;
    }

    @Override
    public IApplicationManager getApplicationManager() {
        return applicationManager;
    }

    @Override
    public IExternalIdManager getExternalIdManager() {
        return externalIdManager;
    }

    @Override
    public IAttributeDefinitionManager getAttributeDefinitionManager() {
        return attributeDefinitionManager;
    }

    @Override
    public ILicenseManager<LicensePojo> getLicenseManager() {
        return licenseManager;
    }

    @Override
    public IProtexServerManager getProtexServerManager() {
        return protexServerManager;
    }

    @Override
    public ICodeCenterComponentManager getComponentManager() {
        return componentManager;
    }

    @Override
    public ICodeCenterUserManager getUserManager() {
        return userManager;
    }

    @Override
    public IRequestManager getRequestManager() {
        return requestManager;
    }
}
