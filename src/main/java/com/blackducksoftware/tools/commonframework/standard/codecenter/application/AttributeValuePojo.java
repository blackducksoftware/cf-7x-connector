package com.blackducksoftware.tools.commonframework.standard.codecenter.application;

public class AttributeValuePojo {
    private final String attrId;
    private final String name;
    private final String value;

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

    @Override
    public String toString() {
	return "AttributeValuePojo [attrId=" + attrId + ", name=" + name
		+ ", value=" + value + "]";
    }

}
