package com.blackducksoftware.tools.commonframework.standard.codecenter.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.commonframework.standard.codecenter.ICodeCenterServerWrapper;

public class AttributeDefinitionManager implements IAttributeDefinitionManager {
    private final ICodeCenterServerWrapper ccsw;
    private final Map<String, AbstractAttribute> attrDefsByName = new HashMap<>();
    private final Map<String, AbstractAttribute> attrDefsById = new HashMap<>();

    public AttributeDefinitionManager(ICodeCenterServerWrapper ccsw) {
	this.ccsw = ccsw;
    }

    @Override
    public boolean validateAttributeTypeName(AttributeGroupType type,
	    String name) {
	try {
	    getAttributeValueType(type, name);
	    return true;
	} catch (CommonFrameworkException e) {
	    return false;
	}
    }

    @Override
    public AttributeValueType getAttributeValueType(
	    AttributeGroupType groupType, String attrName)
	    throws CommonFrameworkException {

	List<AbstractAttribute> attrDefs;
	try {
	    attrDefs = ccsw
		    .getInternalApiWrapper()
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

    private void addToCache(AbstractAttribute attrDef) {
	attrDefsById.put(attrDef.getId().getId(), attrDef);
	attrDefsByName.put(attrDef.getName(), attrDef);
    }

    @Override
    public AttributeDefinitionPojo getAttributeDefinitionById(String attributeId)
	    throws CommonFrameworkException {
	AbstractAttribute attrDef;

	if (attrDefsById.containsKey(attributeId)) {
	    attrDef = attrDefsById.get(attributeId);
	} else {
	    AttributeIdToken attrToken = new AttributeIdToken();
	    attrToken.setId(attributeId);
	    try {
		attrDef = ccsw.getInternalApiWrapper().getAttributeApi()
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

	if (attrDefsByName.containsKey(attributeName)) {
	    attrDef = attrDefsByName.get(attributeName);
	} else {
	    AttributeNameToken attrToken = new AttributeNameToken();
	    attrToken.setName(attributeName);
	    try {
		attrDef = ccsw.getInternalApiWrapper().getAttributeApi()
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
}
