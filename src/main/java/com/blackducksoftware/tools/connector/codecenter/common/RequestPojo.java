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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
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
