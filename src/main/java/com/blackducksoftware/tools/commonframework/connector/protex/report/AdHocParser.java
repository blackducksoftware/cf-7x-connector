/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
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
package com.blackducksoftware.tools.commonframework.connector.protex.report;

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
