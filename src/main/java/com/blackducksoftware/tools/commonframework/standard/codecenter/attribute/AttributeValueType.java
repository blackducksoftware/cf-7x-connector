package com.blackducksoftware.tools.commonframework.standard.codecenter.attribute;

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