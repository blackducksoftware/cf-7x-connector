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
package com.blackducksoftware.tools.connector.common;


/**
 * License details.
 *
 * @author sbillings
 *
 */
public class LicensePojo {

    private final String id;
    private final String name;
    private final String licenseText;

    public LicensePojo(String id, String name, String licenseText) {
	this.id = id;
	this.name = name;
	this.licenseText = licenseText;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getLicenseText() {
	return licenseText;
    }

    @Override
    public String toString() {
	return "ProtexLicensePojo [id=" + id + ", name=" + name + "]";
    }
}
