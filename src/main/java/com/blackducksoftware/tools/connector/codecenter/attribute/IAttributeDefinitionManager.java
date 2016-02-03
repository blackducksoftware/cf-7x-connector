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

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Provides a higher level of abstraction for accessing Code Center attribute
 * definitions.
 *
 * The objects returned are POJOs, not SDK objects.
 *
 * @author sbillings
 *
 */
public interface IAttributeDefinitionManager {

    /**
     * Look for the given attribute within the given group (application,
     * component, etc.), and return its AttributeValueType.
     *
     * Use this to be sure an expected attribute is defined on the group you
     * expect, AND get its value type.
     *
     * @param groupType
     * @param attrName
     * @return
     * @throws CommonFrameworkException
     */
    AttributeValueType getAttributeValueTypeWithinGroup(
	    AttributeGroupType groupType, String attrName)
	    throws CommonFrameworkException;

    /**
     * Find out if the given attribute is defined in the given group
     * (application, component, etc.).
     *
     * Use this to be sure an expected attribute is defined on the group you
     * expect when you're not concerned about validating its value type.
     *
     * @param groupType
     * @param attrName
     * @return
     */
    boolean validateAttributeTypeName(AttributeGroupType groupType,
	    String attrName);

    /**
     * Get an attribute definition by attribute ID.
     *
     * @param attributeId
     * @return
     * @throws CommonFrameworkException
     */
    AttributeDefinitionPojo getAttributeDefinitionById(String attributeId)
	    throws CommonFrameworkException;

    /**
     * Get an attribute definition by name.
     *
     * @param attributeName
     * @return
     * @throws CommonFrameworkException
     */
    AttributeDefinitionPojo getAttributeDefinitionByName(String attributeName)
	    throws CommonFrameworkException;
}
