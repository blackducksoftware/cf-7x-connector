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
	return "RequestPojo [applicationId=" + applicationId + ", componentId="
		+ componentId + "]";
    }

}
