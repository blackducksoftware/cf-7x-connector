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
package com.blackducksoftware.tools.connector.codecenter.common;

import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * A request (a component use by an application).
 *
 * @author sbillings
 *
 */
public class RequestPojo {
    private final String requestId;

    private final String applicationId;

    private final String componentId;

    private final ApprovalStatus requestApprovalStatus;

    private final String licenseId;

    // TODO: Add request attribute values

    public RequestPojo(String requestId, String applicationId,
            String componentId, ApprovalStatus requestApprovalStatus,
            String licenseId) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.componentId = componentId;
        this.requestApprovalStatus = requestApprovalStatus;
        this.licenseId = licenseId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getComponentId() {
        return componentId;
    }

    public ApprovalStatus getRequestApprovalStatus() {
        return requestApprovalStatus;
    }

    public String getLicenseId() {
        return licenseId;
    }

    @Override
    public String toString() {
        return "RequestPojo [requestId=" + requestId + ", applicationId=" + applicationId + ", componentId=" + componentId + ", requestApprovalStatus="
                + requestApprovalStatus + ", licenseId=" + licenseId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = prime * result + ((componentId == null) ? 0 : componentId.hashCode());
        result = prime * result + ((licenseId == null) ? 0 : licenseId.hashCode());
        result = prime * result + ((requestApprovalStatus == null) ? 0 : requestApprovalStatus.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RequestPojo other = (RequestPojo) obj;
        if (applicationId == null) {
            if (other.applicationId != null) {
                return false;
            }
        } else if (!applicationId.equals(other.applicationId)) {
            return false;
        }
        if (componentId == null) {
            if (other.componentId != null) {
                return false;
            }
        } else if (!componentId.equals(other.componentId)) {
            return false;
        }
        if (licenseId == null) {
            if (other.licenseId != null) {
                return false;
            }
        } else if (!licenseId.equals(other.licenseId)) {
            return false;
        }
        if (requestApprovalStatus != other.requestApprovalStatus) {
            return false;
        }
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        } else if (!requestId.equals(other.requestId)) {
            return false;
        }
        return true;
    }

}
