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
package com.blackducksoftware.tools.connector.codecenter.attribute;

public class AttributeDefinitionPojo {
    private final String id;
    private final String name;
    private final String attrType;
    private final String description;
    private final String question;

    public AttributeDefinitionPojo(String id, String name, String attrType,
	    String description, String question) {
	this.id = id;
	this.name = name;
	this.attrType = attrType;
	this.description = description;
	this.question = question;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getAttrType() {
	return attrType;
    }

    public String getDescription() {
	return description;
    }

    public String getQuestion() {
	return question;
    }

    @Override
    public String toString() {
	return "AttributeDefinitionPojo [name=" + name + ", attrType="
		+ attrType + ", description=" + description + ", question="
		+ question + "]";
    }

}
