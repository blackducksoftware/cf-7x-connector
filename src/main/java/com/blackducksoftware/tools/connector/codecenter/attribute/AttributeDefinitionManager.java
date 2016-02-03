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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;

public class AttributeDefinitionManager implements IAttributeDefinitionManager {
    private final CodeCenterAPIWrapper ccApiWrapper;
    private final Map<String, AbstractAttribute> attrDefsByNameCache = new HashMap<>();
    private final Map<String, AbstractAttribute> attrDefsByIdCache = new HashMap<>();

    public AttributeDefinitionManager(CodeCenterAPIWrapper ccApiWrapper) {
	this.ccApiWrapper = ccApiWrapper;
    }

    @Override
    public boolean validateAttributeTypeName(AttributeGroupType type,
	    String name) {
	try {
	    getAttributeValueTypeWithinGroup(type, name);
	    return true;
	} catch (CommonFrameworkException e) {
	    return false;
	}
    }

    @Override
    public AttributeValueType getAttributeValueTypeWithinGroup(
	    AttributeGroupType groupType, String attrName)
	    throws CommonFrameworkException {

	List<AbstractAttribute> attrDefs;
	try {
	    attrDefs = ccApiWrapper
		    .getAttributeApi()
		    .getAllAttributesByAttributeGroupType(groupType.getCcType());
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error fetching "
		    + groupType.toString() + " attribute definitions");
	}
	for (AbstractAttribute attrDef : attrDefs) {
	    addToCache(attrDef);
	    if (attrDef.getName().equals(attrName)) {
		return AttributeValueType.translateAttributeValueType(attrDef
			.getAttrType());
	    }
	}
	throw new CommonFrameworkException(groupType.toString() + " attribute "
		+ attrName + " not found.");
    }

    @Override
    public AttributeDefinitionPojo getAttributeDefinitionById(String attributeId)
	    throws CommonFrameworkException {
	AbstractAttribute attrDef;

	if (attrDefsByIdCache.containsKey(attributeId)) {
	    attrDef = attrDefsByIdCache.get(attributeId);
	} else {
	    AttributeIdToken attrToken = new AttributeIdToken();
	    attrToken.setId(attributeId);
	    try {
		attrDef = ccApiWrapper.getAttributeApi()
			.getAttribute(attrToken);
		addToCache(attrDef);
	    } catch (SdkFault e) {
		throw new CommonFrameworkException(
			"Error fetching attribute definition for id "
				+ attributeId + ": " + e.getMessage());
	    }
	}
	return new AttributeDefinitionPojo(attrDef.getId().getId(),
		attrDef.getName(), attrDef.getAttrType(),
		attrDef.getDescription(), attrDef.getQuestion());
    }

    @Override
    public AttributeDefinitionPojo getAttributeDefinitionByName(
	    String attributeName) throws CommonFrameworkException {
	AbstractAttribute attrDef;

	if (attrDefsByNameCache.containsKey(attributeName)) {
	    attrDef = attrDefsByNameCache.get(attributeName);
	} else {
	    AttributeNameToken attrToken = new AttributeNameToken();
	    attrToken.setName(attributeName);
	    try {
		attrDef = ccApiWrapper.getAttributeApi()
			.getAttribute(attrToken);
		addToCache(attrDef);
	    } catch (SdkFault e) {
		throw new CommonFrameworkException(
			"Error fetching attribute definition for name "
				+ attributeName + ": " + e.getMessage());
	    }
	}
	return new AttributeDefinitionPojo(attrDef.getId().getId(),
		attrDef.getName(), attrDef.getAttrType(),
		attrDef.getDescription(), attrDef.getQuestion());
    }

    private void addToCache(AbstractAttribute attrDef) {
	attrDefsByIdCache.put(attrDef.getId().getId(), attrDef);
	attrDefsByNameCache.put(attrDef.getName(), attrDef);
    }
}
