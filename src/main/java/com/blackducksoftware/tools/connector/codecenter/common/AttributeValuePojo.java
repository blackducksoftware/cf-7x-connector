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
package com.blackducksoftware.tools.connector.codecenter.common;

/**
 * An attribute value.
 *
 * @author sbillings
 *
 */
public class AttributeValuePojo implements Comparable {
    private final String attrId;

    private final String name;

    private String value;

    public AttributeValuePojo(String attrId, String name, String value) {
        this.attrId = attrId;
        this.name = name;
        this.value = value;
    }

    public String getAttrId() {
        return attrId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeValuePojo [attrId=" + attrId + ", name=" + name
                + ", value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attrId == null) ? 0 : attrId.hashCode());
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
        AttributeValuePojo other = (AttributeValuePojo) obj;
        if (attrId == null) {
            if (other.attrId != null) {
                return false;
            }
        } else if (!attrId.equals(other.attrId)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object obj) {
        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            return -1;
        }
        if (getClass() != obj.getClass()) {
            return -1;
        }
        AttributeValuePojo other = (AttributeValuePojo) obj;
        return getAttrId().compareTo(other.getAttrId());
    }

}
