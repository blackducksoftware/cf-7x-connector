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
package com.blackducksoftware.tools.connector.codecenter;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.attribute.AttributeApi;
import com.blackducksoftware.sdk.codecenter.client.util.CodeCenterServerProxyV7_0;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.externalid.ExternalIdApi;
import com.blackducksoftware.sdk.codecenter.request.RequestApi;
import com.blackducksoftware.sdk.codecenter.settings.SettingsApi;
import com.blackducksoftware.sdk.codecenter.user.UserApi;
import com.blackducksoftware.sdk.codecenter.vulnerability.VulnerabilityApi;
import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.core.config.SSOBean;
import com.blackducksoftware.tools.commonframework.core.config.server.ServerBean;
import com.blackducksoftware.tools.connector.protex.APIWrapper;

/**
 * The Class CodeCenterAPIWrapper.
 */
public class CodeCenterAPIWrapper extends APIWrapper {

    /** The cc proxy. */
    private CodeCenterServerProxyV7_0 ccProxy;

    /** The vulnerability api. */
    private VulnerabilityApi vulnerabilityApi;

    /** The application api. */
    private ApplicationApi applicationApi;

    /** The externalId api. */
    private ExternalIdApi externalIdApi;

    /** The user api. */
    private UserApi userApi;

    /** The cola api. */
    private ColaApi colaApi;

    /** The attribute api. */
    private AttributeApi attributeApi;

    private RequestApi requestApi;

    private SettingsApi settingsApi;

    public CodeCenterAPIWrapper(ServerBean bean, ConfigurationManager manager)
            throws Exception {
        super(manager, bean);
        getAllApis(bean, manager);
    }

    private void getAllApis(ServerBean bean, ConfigurationManager manager)
            throws Exception {
        String errorMessage = "";
        try {
            ccProxy = new CodeCenterServerProxyV7_0(bean.getServerName(), bean.getUserName(), bean.getPassword());

            // Optional SSO -- sets the auth methods
            SSOBean ssoBean = manager.getSsoBean();
            Map<String, List<String>> cookies = getAuthCookies(ssoBean);
            ccProxy.setRequestHeaders(cookies);

            // Get all the APIs
            vulnerabilityApi = ccProxy.getVulnerabilityApi();
            applicationApi = ccProxy.getApplicationApi();
            externalIdApi = ccProxy.getExternalIdApi();
            userApi = ccProxy.getUserApi();
            colaApi = ccProxy.getColaApi();
            attributeApi = ccProxy.getAttributeApi();
            requestApi = ccProxy.getRequestApi();
            settingsApi = ccProxy.getSettingsApi();

        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (e.getCause() != null) {
                errorMessage += ": " + e.getCause().getMessage();
            }
            throw new Exception(errorMessage);
        }
    }

    public CodeCenterServerProxyV7_0 getProxy() {
        return ccProxy;
    }

    public VulnerabilityApi getVulnerabilityApi() {
        return vulnerabilityApi;
    }

    public ApplicationApi getApplicationApi() {
        return applicationApi;
    }

    public ExternalIdApi getExternalIdApi() {
        return externalIdApi;
    }

    public UserApi getUserApi() {
        return userApi;
    }

    public ColaApi getColaApi() {
        return colaApi;
    }

    public AttributeApi getAttributeApi() {
        return attributeApi;
    }

    public RequestApi getRequestApi() {
        return requestApi;
    }

    public SettingsApi getSettingsApi() {
        return settingsApi;
    }

}
