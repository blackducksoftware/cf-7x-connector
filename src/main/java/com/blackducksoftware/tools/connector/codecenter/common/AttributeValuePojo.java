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
public class AttributeValuePojo {
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

}
