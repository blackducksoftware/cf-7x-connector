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
