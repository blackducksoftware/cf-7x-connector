/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.tools.connector.protex.report;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.blackducksoftware.tools.commonframework.standard.protex.report.HocElement;

/**
 * Common class for parsing functionality
 *
 * @author akamen
 *
 */
public class AdHocParser<T extends HocElement> {

    /**
     * We want to wrap the instantiation of the class carefully to avoid
     * uncaught RunTime exceptions in case the wrong class is being shoved into
     * here.
     *
     * @param hocElementClass
     *            the hoc element class
     * @return the hoc element
     * @throws Exception
     *             the exception
     */
    protected T generateNewInstance(Class<T> hocElementClass) throws Exception {
	T adHocRow = null;
	Constructor<?> constructor = null;
	;
	try {
	    constructor = hocElementClass.getConstructor();
	} catch (SecurityException e) {
	    throw new Exception(e.getMessage());
	} catch (NoSuchMethodException e) {
	    throw new Exception(e.getMessage());
	}

	try {
	    adHocRow = (T) constructor.newInstance();
	} catch (IllegalArgumentException e) {
	    throw new Exception(e.getMessage());
	} catch (InstantiationException e) {
	    throw new Exception(e.getMessage());
	} catch (IllegalAccessException e) {
	    throw new Exception(e.getMessage());
	} catch (InvocationTargetException e) {
	    throw new Exception(e.getMessage());
	}

	return adHocRow;
    }
}
