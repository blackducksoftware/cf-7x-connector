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
package com.blackducksoftware.tools.connector.common;

public abstract class ComponentPojo {
    private String name;
    private String version;
    private String description;
    private ApprovalStatus approvalStatus;

    private String homepage;
    private boolean deprecated;

    public String getName() {
	return name;
    }

    public String getVersion() {
	return version;
    }

    public ApprovalStatus getApprovalStatus() {
	return approvalStatus;
    }

    public String getHomepage() {
	return homepage;
    }

    public boolean isDeprecated() {
	return deprecated;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
	this.approvalStatus = approvalStatus;
    }

    public void setHomepage(String homepage) {
	this.homepage = homepage;
    }

    public void setDeprecated(boolean deprecated) {
	this.deprecated = deprecated;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
