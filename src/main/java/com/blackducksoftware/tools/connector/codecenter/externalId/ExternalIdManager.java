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
package com.blackducksoftware.tools.connector.codecenter.externalId;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.externalid.data.CatalogComponentVersionObjectKey;
import com.blackducksoftware.sdk.codecenter.externalid.data.ExternalIdInfo;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;

public class ExternalIdManager implements IExternalIdManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterAPIWrapper ccApiWrapper;

    public ExternalIdManager(CodeCenterAPIWrapper ccApiWrapper) {
        this.ccApiWrapper = ccApiWrapper;
    }

    @Override
    public List<ExternalIdInfo> getExternalIdObjectById(String source, String objectId) throws CommonFrameworkException {
        CatalogComponentVersionObjectKey key = new CatalogComponentVersionObjectKey();
        ComponentIdToken idToken = new ComponentIdToken();
        idToken.setId(objectId);
        key.setObjectId(idToken);

        List<ExternalIdInfo> externalIdInfo = null;
        try {
            externalIdInfo = ccApiWrapper.getExternalIdApi().getExternalIdsByObjectId(source, key);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting the external Id information : " + e.getMessage());
        }
        return externalIdInfo;
    }

}
