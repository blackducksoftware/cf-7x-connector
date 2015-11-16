package com.blackducksoftware.tools.connector.codecenter.protexservers;

import java.util.Properties;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;

public class ProtexConfigurationManager extends ConfigurationManager {
    public ProtexConfigurationManager(Properties props) {
	super(props, APPLICATION.PROTEX);
    }
}
