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
