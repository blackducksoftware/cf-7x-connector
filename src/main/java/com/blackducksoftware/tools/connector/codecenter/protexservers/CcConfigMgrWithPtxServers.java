package com.blackducksoftware.tools.connector.codecenter.protexservers;

import com.blackducksoftware.tools.commonframework.core.config.IConfigurationManager;

public interface CcConfigMgrWithPtxServers extends
	IConfigurationManager {

    String getPxServerValidationList();
}
