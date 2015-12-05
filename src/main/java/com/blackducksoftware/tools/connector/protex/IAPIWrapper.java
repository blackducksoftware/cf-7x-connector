/*
 * Copyright (C) 2015 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.tools.connector.protex;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.SSOBean;

/**
 * Inteface for the API Wrapper to ensure that certain elements are
 * set for both CC and Protex.
 * 
 * @author akamen
 * 
 */
public interface IAPIWrapper {

    void setProxyInformation(ConfigurationManager configManager);

    void setSSOInformation(SSOBean ssoBean);

}
