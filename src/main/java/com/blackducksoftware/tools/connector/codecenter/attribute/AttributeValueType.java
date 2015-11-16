package com.blackducksoftware.tools.connector.codecenter.attribute;

public enum AttributeValueType {
    STRING, DATE;

    static AttributeValueType translateAttributeValueType(String ccAttrType) {
	if ("datepicker".equals(ccAttrType)) {
	    return AttributeValueType.DATE;
	} else {
	    return AttributeValueType.STRING;
	}
    }
}