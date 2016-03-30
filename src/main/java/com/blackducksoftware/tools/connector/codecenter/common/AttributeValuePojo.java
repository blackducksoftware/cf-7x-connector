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
