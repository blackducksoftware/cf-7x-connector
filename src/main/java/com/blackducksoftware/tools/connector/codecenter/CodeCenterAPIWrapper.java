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

import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.attribute.AttributeApi;
import com.blackducksoftware.sdk.codecenter.client.util.CodeCenterServerProxyV7_0;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.externalid.ExternalIdApi;
import com.blackducksoftware.sdk.codecenter.report.ReportApi;
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

    private ReportApi reportApi;

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
            reportApi = ccProxy.getReportApi();

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

    public ReportApi getReportApi() {
        return reportApi;
    }

    public void setReportApi(ReportApi reportApi) {
        this.reportApi = reportApi;
    }
}
