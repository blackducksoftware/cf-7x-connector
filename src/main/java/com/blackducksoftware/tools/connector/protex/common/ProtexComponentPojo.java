/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
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
