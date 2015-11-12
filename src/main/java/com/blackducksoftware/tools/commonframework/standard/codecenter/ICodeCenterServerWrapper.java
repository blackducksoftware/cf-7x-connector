package com.blackducksoftware.tools.commonframework.standard.codecenter;

import com.blackducksoftware.tools.commonframework.connector.protex.IServerWrapper;
import com.blackducksoftware.tools.commonframework.standard.codecenter.application.IApplicationManager;
import com.blackducksoftware.tools.commonframework.standard.codecenter.attribute.IAttributeDefinitionManager;

public interface ICodeCenterServerWrapper extends IServerWrapper {

    @Override
    CodeCenterAPIWrapper getInternalApiWrapper();

    IApplicationManager getApplicationManager();

    IAttributeDefinitionManager getAttributeDefinitionManager();
}