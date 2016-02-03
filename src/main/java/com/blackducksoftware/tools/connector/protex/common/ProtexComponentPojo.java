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
package com.blackducksoftware.tools.connector.protex.common;

import java.util.List;

import com.blackducksoftware.tools.connector.common.ComponentPojo;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class ProtexComponentPojo extends ComponentPojo {

    private ComponentNameVersionIds nameVersionIds;
    private List<ProtexLicensePojo> licenses;
    private ProtexComponentType type;
    private String primaryLicenseId;
    private String primaryLicenseName;
    private LicenseConflictStatus licenseConflictStatus = LicenseConflictStatus.UNKNOWN;

    public ComponentNameVersionIds getNameVersionIds() {
	return nameVersionIds;
    }

    public List<ProtexLicensePojo> getLicenses() {
	return licenses;
    }

    public ProtexComponentType getType() {
	return type;
    }

    public String getPrimaryLicenseId() {
	return primaryLicenseId;
    }

    public String getPrimaryLicenseName() {
	return primaryLicenseName;
    }

    public void setNameVersionIds(ComponentNameVersionIds nameVersionIds) {
	this.nameVersionIds = nameVersionIds;
    }

    public void setLicenses(List<ProtexLicensePojo> licenses) {
	this.licenses = licenses;
    }

    public void setType(ProtexComponentType type) {
	this.type = type;
    }

    public void setPrimaryLicenseId(String primaryLicenseId) {
	this.primaryLicenseId = primaryLicenseId;
    }

    public void setPrimaryLicenseName(String primaryLicenseName) {
	this.primaryLicenseName = primaryLicenseName;
    }

    public LicenseConflictStatus getLicenseConflictStatus() {
	return licenseConflictStatus;
    }

    public void setLicenseConflictStatus(
	    LicenseConflictStatus licenseConflictStatus) {
	this.licenseConflictStatus = licenseConflictStatus;
    }

    @Override
    public String toString() {
	return "ProtexComponentPojo [type=" + type + ", getName()=" + getName()
		+ ", getVersion()=" + getVersion() + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((nameVersionIds == null) ? 0 : nameVersionIds.hashCode());
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
	ProtexComponentPojo other = (ProtexComponentPojo) obj;
	if (nameVersionIds == null) {
	    if (other.nameVersionIds != null) {
		return false;
	    }
	} else if (!nameVersionIds.equals(other.nameVersionIds)) {
	    return false;
	}
	return true;
    }

}
