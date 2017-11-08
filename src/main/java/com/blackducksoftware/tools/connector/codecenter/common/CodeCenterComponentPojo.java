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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.common.ComponentPojo;
import com.blackducksoftware.tools.connector.common.LicensePojo;

/**
 * A component.
 *
 * The subComponents field is only populated when the object is part of a
 * recursively-generated full-depth list, where a subcomponent at the top level
 * may point to a list of subcomponents at the next level, etc. TODO:
 * subComponents really does not belong in this class.
 *
 * @author sbillings
 *
 */
public class CodeCenterComponentPojo<T> extends ComponentPojo {
    private String id;

    private String intendedAudiences;

    private List<LicensePojo> licenses;

    private String kbComponentId;

    private String kbReleaseId;

    private boolean applicationComponent;

    private String applicationId;

    private Map<String, AttributeValuePojo> attributeValuesByName = new HashMap<>();

    private List<T> subComponents;

    // If available, the ID of the license on the request.
    private LicensePojo requestedLicense;

    public CodeCenterComponentPojo() {

    }

    public void setAttributeValues(List<AttributeValuePojo> attributeValues) {
        AttributeValues.addAttributeValuesToMap(attributeValuesByName,
                attributeValues);
    }

    public void setAttributeValues(
            Map<String, AttributeValuePojo> attributeValues) {
        attributeValuesByName = new HashMap<>(attributeValues);
    }

    public void setSubComponents(List<T> subComponents) {
        if ((subComponents != null) && (subComponents.size() > 0)) {
            this.subComponents = new ArrayList<>(subComponents.size());
            this.subComponents.addAll(subComponents);
        } else {
            this.subComponents = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getIntendedAudiences() {
        return intendedAudiences;
    }

    public List<LicensePojo> getLicenses() {
        return licenses;
    }

    public String getKbComponentId() {
        return kbComponentId;
    }

    public String getKbReleaseId() {
        return kbReleaseId;
    }

    public boolean isApplicationComponent() {
        return applicationComponent;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getAttributeByName(String name) {
        if (!attributeValuesByName.containsKey(name)) {
            return null;
        }
        AttributeValuePojo valuePojo = attributeValuesByName.get(name);
        if (valuePojo == null) {
            return null;
        }
        String value = valuePojo.getValue();
        return value;
    }

    public Map<String, AttributeValuePojo> getAttributeValues() {
        return new HashMap<String, AttributeValuePojo>(attributeValuesByName);
    }

    public List<T> getSubComponents() {
        if (subComponents == null) {
            return null;
        }
        return new ArrayList<T>(subComponents);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIntendedAudiences(String intendedAudiences) {
        this.intendedAudiences = intendedAudiences;
    }

    public void setLicenses(List<LicensePojo> licenses) {
        this.licenses = licenses;
    }

    public void setKbComponentId(String kbComponentId) {
        this.kbComponentId = kbComponentId;
    }

    public void setKbReleaseId(String kbReleaseId) {
        this.kbReleaseId = kbReleaseId;
    }

    public void setApplicationComponent(boolean applicationComponent) {
        this.applicationComponent = applicationComponent;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        return "CodeCenterComponentPojo [name=" + getName() + ", version="
                + getVersion() + ", kbComponentId=" + getKbComponentId() + ", kbReleaseId=" + getKbReleaseId() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result
                + ((getVersion() == null) ? 0 : getVersion().hashCode());
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
        CodeCenterComponentPojo other = (CodeCenterComponentPojo) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }
        if (getVersion() == null) {
            if (other.getVersion() != null) {
                return false;
            }
        } else if (!getVersion().equals(other.getVersion())) {
            return false;
        }
        return true;
    }

    public LicensePojo getRequestedLicense() {
        return requestedLicense;
    }

    public void setRequestedLicense(LicensePojo requestedLicense) {
        this.requestedLicense = requestedLicense;
    }

}
