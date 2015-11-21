package com.blackducksoftware.tools.connector.protex.common;

import com.blackducksoftware.sdk.protex.common.ComponentType;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public enum ProtexComponentType {
    CUSTOM(ComponentType.CUSTOM), LOCAL(ComponentType.LOCAL), PROJECT(
	    ComponentType.PROJECT), STANDARD(ComponentType.STANDARD), STANDARD_MODIFIED(
	    ComponentType.STANDARD_MODIFIED);

    private ComponentType protexType;

    private ProtexComponentType(ComponentType protexType) {
	this.protexType = protexType;
    }

    ComponentType getProtexType() {
	return protexType;
    }

    boolean isEquivalent(ComponentType otherProtexType) {
	return protexType.equals(otherProtexType);
    }

    public static ProtexComponentType valueOf(ComponentType protexType)
	    throws CommonFrameworkException {
	switch (protexType) {
	case CUSTOM:
	    return ProtexComponentType.CUSTOM;
	case LOCAL:
	    return ProtexComponentType.LOCAL;
	case PROJECT:
	    return ProtexComponentType.PROJECT;
	case STANDARD:
	    return ProtexComponentType.STANDARD;
	case STANDARD_MODIFIED:
	    return ProtexComponentType.STANDARD_MODIFIED;
	default:
	    throw new CommonFrameworkException("Unsupported ComponentType: "
		    + protexType);
	}
    }
}
