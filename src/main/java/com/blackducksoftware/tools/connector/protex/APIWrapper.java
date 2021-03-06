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
package com.blackducksoftware.tools.connector.protex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.ProxyBean;
import com.blackducksoftware.tools.commonframework.core.config.SSOBean;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Common APIWrapper class
 * 
 * @author akamen
 */
public abstract class APIWrapper implements IAPIWrapper {

    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    /** The https string. */
    private static final String HTTPS_STRING = "https://";

    /** The http string. */
    private static final String HTTP_STRING = "http";

    private TLSClientParameters tlsClientParameters;

    /**
     * Instantiates a new API wrapper.
     * 
     * @param configManager
     *            the config manager
     * @param bean
     * @throws CommonFrameworkException
     */
    public APIWrapper(ConfigurationManager configManager, ServerBean bean) throws CommonFrameworkException {
        setProxyInformation(configManager);
        determineServerURL(bean);
        setSSOInformation(configManager.getSsoBean());

        // For cert bypass
        tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
    }

    /**
     * This wraps the api in a HTTPconduit and sets the switch to disable the
     * certificate check Useful for those clients where the javacerts are out of
     * date/wrong/etc.
     * 
     * @param api
     * @param theClass
     * @return
     */
    public <T> T disableCertificateCheck(Object api, Class<T> theClass) {
        org.apache.cxf.endpoint.Client client = org.apache.cxf.frontend.ClientProxy
                .getClient(api);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        http.setTlsClientParameters(tlsClientParameters);

        return (T) api;
    }

    /**
     * Determine server url.
     * 
     * @param bean
     *            the config manager
     * @throws CommonFrameworkException
     */
    protected void determineServerURL(ServerBean bean) throws CommonFrameworkException {
        // AK: This allows a bit more flexibility on the server URLs. If the
        // user manually specifies an HTTP(S) address, then we allow it.
        // Otherwise, everything else is prepended with an HTTPS
        String serverURL = null;

        if (bean == null) {
            throw new CommonFrameworkException("Server bean null");
        }

        String serverName = bean.getServerName();

        if (serverName.contains(HTTP_STRING)) {
            serverURL = serverName;
        } else {
            serverURL = HTTPS_STRING + serverName;
        }

        bean.setServerName(serverURL);

    }

    /**
     * Sets the cookies to a map and returns the map
     * 
     * @param ssoBean
     */
    public Map<String, List<String>> getAuthCookies(SSOBean ssoBean) {

        Map<String, List<String>> cookies = new HashMap<String, List<String>>();
        cookies.put(SSOBean.SSO_COOKIE_AUTH_METHODS, ssoBean.getAuthenticationMethods());
        log.info("Setting AUTH_METHODS for Proxy");

        return cookies;
    }

    @Override
    public void setProxyInformation(ConfigurationManager configManager) {
        ProxyBean pb = configManager.getProxyBean();
        String proxyServer = pb.getProxyServer();
        String proxyPort = pb.getProxyPort();

        String httpsProxyServer = pb.getProxyServerHttps();
        String httpsProxyPort = pb.getProxyPortHttps();

        // TODO: Intelligently determine whether http or https.
        // Only do something if user provided legitimate data
        if (proxyServer != null && proxyPort != null) {
            System.setProperty("http.proxyHost", proxyServer.trim());
            System.setProperty("http.proxyPort", proxyPort);
        } else if (httpsProxyServer != null && httpsProxyPort != null) {

            System.setProperty("https.proxyHost", httpsProxyServer.trim());
            System.setProperty("https.proxyPort", httpsProxyPort);
        }

    }

    /**
     * * Sets up SYSTEM properties to handle all the SSL information
     * This is optional and can be done by -D switches instead.
     */
    @Override
    public void setSSOInformation(SSOBean ssoBean) {
        if (ssoBean.isInitialized())
        {
            if (ssoBean.getKeyStorePath() != null) {
                System.setProperty(SSOBean.SSO_KEY_STORE_PATH, ssoBean.getKeyStorePath());
            }
            if (ssoBean.getKeyStorePassword() != null) {
                System.setProperty(SSOBean.SSO_KEY_STORE_PASSWORD, ssoBean.getKeyStorePassword());
            }
            if (ssoBean.getKeyStoreType() != null) {
                System.setProperty(SSOBean.SSO_KEY_STORE_TYPE, ssoBean.getKeyStoreType());
            }
            if (ssoBean.getTrustStorePath() != null) {
                System.setProperty(SSOBean.SSO_TRUST_STORE_PATH, ssoBean.getTrustStorePath());
            }
            if (ssoBean.getTrustStorePassword() != null) {
                System.setProperty(SSOBean.SSO_TRUST_STORE_PASSWORD, ssoBean.getTrustStorePassword());
            }
            if (ssoBean.getTrustStoreType() != null) {
                System.setProperty(SSOBean.SSO_TRUST_STORE_TYPE, ssoBean.getTrustStoreType());
            }

            log.info("System properties now have the following SSL Properties");

            log.info(System.getProperty(SSOBean.SSO_KEY_STORE_PATH));
            log.info(System.getProperty(SSOBean.SSO_KEY_STORE_TYPE));
            log.info(System.getProperty(SSOBean.SSO_TRUST_STORE_PATH));
            log.info(System.getProperty(SSOBean.SSO_TRUST_STORE_TYPE));
        }
        else
        {
            log.info("No SSO information set by user, skipping");
        }

    }

}
