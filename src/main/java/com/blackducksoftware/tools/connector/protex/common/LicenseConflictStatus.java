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

/**
 * When used in the context of a BOM, the license conflict status will be NONE,
 * LICENSE, OR DECLARED.
 *
 * When used without the context of a BOM, license conflict status will be
 * UNKNOWN.
 *
 * @author sbillings
 *
 */
public enum LicenseConflictStatus {
    UNKNOWN("Unknown"), NONE("No Conflict"), LICENSE("Component Conflict"), DECLARED(
	    "Declared Conflict");

    private final String description;

    private LicenseConflictStatus(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return description;
    }
}
