/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
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
import com.blackducksoftware.tools.connector.codecenter.component.ComponentManager;
import com.blackducksoftware.tools.connector.codecenter.component.ICodeCenterComponentManager;
import com.blackducksoftware.tools.connector.codecenter.externalId.ExternalIdManager;
import com.blackducksoftware.tools.connector.codecenter.externalId.IExternalIdManager;
import com.blackducksoftware.tools.connector.codecenter.license.LicenseManager;
import com.blackducksoftware.tools.connector.codecenter.protexservers.IProtexServerManager;
import com.blackducksoftware.tools.connector.codecenter.protexservers.ProtexServerManager;
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

        // Higher-level managers with dependencies on other managers
        componentManager = new ComponentManager(apiWrapper,
                attributeDefinitionManager, licenseManager);
        applicationManager = new ApplicationManager(apiWrapper,
                attributeDefinitionManager, componentManager, userManager);

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
}
