package com.blackducksoftware.tools.commonframework.standard.codecenter.attribute;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public interface IAttributeDefinitionManager {
    AttributeValueType getAttributeValueType(AttributeGroupType groupType,
	    String attrName) throws CommonFrameworkException;

    boolean validateAttributeTypeName(AttributeGroupType groupType,
	    String attrName);

    AttributeDefinitionPojo getAttributeDefinitionById(String attributeId)
	    throws CommonFrameworkException;

    AttributeDefinitionPojo getAttributeDefinitionByName(String attributeName)
	    throws CommonFrameworkException;
}
