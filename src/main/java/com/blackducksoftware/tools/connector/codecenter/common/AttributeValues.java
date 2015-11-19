package com.blackducksoftware.tools.connector.codecenter.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionPojo;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;

/**
 * Utility functions for useful when dealing with AttributeValue objects.
 *
 * Intended to be used by Common Framework connector classes.
 *
 * @author sbillings
 *
 */
public class AttributeValues {
    private static final Logger log = LoggerFactory
	    .getLogger(AttributeValues.class.getName());

    /**
     * Convert a list of attribute values (SDK objects) to POJOs.
     *
     * @param attrValues
     * @return
     * @throws CommonFrameworkException
     */
    public static List<AttributeValuePojo> valueOf(
	    IAttributeDefinitionManager attrDefMgr,
	    List<AttributeValue> attrValues) throws CommonFrameworkException {
	List<AttributeValuePojo> pojos = new ArrayList<>();
	for (AttributeValue attrValue : attrValues) {
	    String attrId = getAttributeId(attrValue);
	    AttributeDefinitionPojo attrDefPojo = attrDefMgr
		    .getAttributeDefinitionById(attrId);
	    String attrName = attrDefPojo.getName();

	    String value = null;
	    List<String> valueList = attrValue.getValues();
	    if (valueList.size() > 1) {
		log.warn("Attribute "
			+ attrName
			+ " has multiple values, which is not supported; using the first value");
	    }
	    if ((valueList != null) && (valueList.size() > 0)) {
		value = attrValue.getValues().get(0);
	    }
	    log.debug("Processing attr id " + attrId + ", name " + attrName
		    + ", value " + value);

	    AttributeValuePojo pojo = new AttributeValuePojo(attrId, attrName,
		    value);
	    pojos.add(pojo);
	}
	return pojos;
    }

    public static void addAttributeValuesToMap(
	    Map<String, AttributeValuePojo> attributeValuesByName,
	    List<AttributeValuePojo> attributeValues) {
	for (AttributeValuePojo attrValue : attributeValues) {
	    attributeValuesByName.put(attrValue.getName(), attrValue);
	}
    }

    private static String getAttributeId(AttributeValue attrValue) {
	AttributeIdToken attrIdToken = (AttributeIdToken) attrValue
		.getAttributeId();
	String attrId = attrIdToken.getId();
	return attrId;
    }
}
