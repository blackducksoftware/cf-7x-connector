/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
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
