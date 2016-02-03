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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.SSOBean;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;

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
     */
    public APIWrapper(ConfigurationManager configManager, ServerBean bean) {
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
     */
    protected void determineServerURL(ServerBean bean) {
        // AK: This allows a bit more flexibility on the server URLs. If the
        // user manually specifies an HTTP(S) address, then we allow it.
        // Otherwise, everything else is prepended with an HTTPS
        String serverURL = null;

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
        String proxyServer = configManager.getProxyServer();
        String proxyPort = configManager.getProxyPort();

        String httpsProxyServer = configManager.getProxyServerHttps();
        String httpsProxyPort = configManager.getProxyPortHttps();

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
