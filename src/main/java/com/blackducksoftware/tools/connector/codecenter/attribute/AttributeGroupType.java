package com.blackducksoftware.tools.connector.codecenter.attribute;

import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeGroupTypeEnum;

public enum AttributeGroupType {

    APPLICATION(AttributeGroupTypeEnum.APPLICATION), APPROVAL(
	    AttributeGroupTypeEnum.APPROVAL), COMPONENT(
	    AttributeGroupTypeEnum.COMPONENT), COMPONENT_USE(
	    AttributeGroupTypeEnum.COMPONENT_USE), TASK(
	    AttributeGroupTypeEnum.TASK);

    private AttributeGroupTypeEnum ccType;

    private AttributeGroupType(AttributeGroupTypeEnum ccType) {
	this.ccType = ccType;
    }

    public boolean isEquivalent(AttributeGroupTypeEnum otherCcType) {
	return ccType.equals(otherCcType);
    }

    AttributeGroupTypeEnum getCcType() {
	return ccType;
    }
}