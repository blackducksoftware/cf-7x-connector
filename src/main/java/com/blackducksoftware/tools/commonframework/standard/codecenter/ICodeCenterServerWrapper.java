package com.blackducksoftware.tools.commonframework.standard.codecenter;

import com.blackducksoftware.tools.commonframework.connector.protex.IServerWrapper;
import com.blackducksoftware.tools.commonframework.standard.codecenter.application.IApplicationManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.license.ILicenseManager;

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
    ILicenseManager getLicenseManager();
}