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
