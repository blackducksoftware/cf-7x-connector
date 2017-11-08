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
package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * A non-SDK-specific class representing an application.
 *
 * TODO: The POJOs in this package and it's sister packages should be merged
 * with (into) the POJOs in commonframework in
 * com.blackducksoftware.tools.connector.codecenter.pojo. This application POJO
 * is functionally almost identical to the one in commonframework. The rest of
 * them are complementary (non-overlapping).
 *
 * @author sbillings
 *
 */
public class ApplicationPojo {
    private final String id;

    private final String name;

    private final String version;

    private final Map<String, AttributeValuePojo> attributeValuesByName = new HashMap<>();

    private final ApprovalStatus approvalStatus;

    private final boolean locked;

    private final String ownerId;

    public ApplicationPojo(String id, String name, String version,
            List<AttributeValuePojo> attributeValues,
            ApprovalStatus approvalStatus, boolean locked, String ownerId) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.locked = locked;
        this.ownerId = ownerId;

        if (attributeValues != null) {
            AttributeValues.addAttributeValuesToMap(attributeValuesByName,
                    attributeValues);
        }

        this.approvalStatus = approvalStatus;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public Map<String, AttributeValuePojo> getAttributeValuesByName() {
        return attributeValuesByName;
    }

    public String getAttributeByName(String name) {
        AttributeValuePojo attribute = attributeValuesByName.get(name);
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public String toString() {
        return "ApplicationPojo [name=" + name + ", version=" + version + "]";
    }

}
