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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.connector.codecenter.common;

public class NameVersion {
    private final String name;

    private final String version;

    public NameVersion(String name, String version) {
        this.name = name;
        if (version == null) {
            this.version = "";
        } else {
            this.version = version;
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "NameVersion [name=" + name + ", version=" + version + "]";
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof NameVersion)) {
            return false;
        }
        NameVersion otherNameVersion = (NameVersion) otherObj;
        if (getName().equals(otherNameVersion.getName())
                && getVersion().equals(otherNameVersion.getVersion())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getName().hashCode() << 1) + getVersion().hashCode();
    }

}
