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

import com.blackducksoftware.tools.connector.codecenter.application.IApplicationManager;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.component.ICodeCenterComponentManager;
import com.blackducksoftware.tools.connector.codecenter.externalId.IExternalIdManager;
import com.blackducksoftware.tools.connector.codecenter.protexservers.IProtexServerManager;
import com.blackducksoftware.tools.connector.codecenter.request.IRequestManager;
import com.blackducksoftware.tools.connector.codecenter.user.ICodeCenterUserManager;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;
import com.blackducksoftware.tools.connector.protex.IServerWrapper;

/**
 * A connection to a Code Center server.
 *
 * Provides access to "Managers" (which provide a higher level of abstraction
 * for accessing Code Center objects) and Apis (which provide low-level access
 * directly to Code Center SDK objects).
 *
 * Managers return POJOs. Apis return SDK objects.
 *
 * @author sbillings
 *
 */
public interface ICodeCenterServerWrapper extends IServerWrapper {

    @Override
    CodeCenterAPIWrapper getInternalApiWrapper();

    /**
     * Get the ApplicationManager for this Code Center connection.
     *
     *
     * @return
     */
    IApplicationManager getApplicationManager();

    /**
     * Get the ExternalIdManager for this Code Center connection.
     *
     *
     * @return
     */
    IExternalIdManager getExternalIdManager();

    /**
     * Get the AttributeDefinitionManager for this Code Center connection.
     *
     * @return
     */
    IAttributeDefinitionManager getAttributeDefinitionManager();

    /**
     * Get the LicenseManager for this Code Center connection.
     *
     * @return
     */
    ILicenseManager<LicensePojo> getLicenseManager();

    /**
     * Get the ProtexServerManager for this Code Center connection.
     *
     * @return
     */
    IProtexServerManager getProtexServerManager();

    /**
     * Get the ComponentManager for this Code Center connection.
     *
     * @return
     */
    ICodeCenterComponentManager getComponentManager();

    /**
     * Get the CodeCenterUserManager for this Code Center connection.
     *
     * @return
     */
    ICodeCenterUserManager getUserManager();

    /**
     * Get the RequestManager for this Code Center connection.
     * 
     * @return
     */
    IRequestManager getRequestManager();
}
