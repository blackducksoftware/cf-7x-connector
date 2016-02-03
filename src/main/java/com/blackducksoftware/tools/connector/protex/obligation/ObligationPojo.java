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
package com.blackducksoftware.tools.connector.protex.obligation;

public class ObligationPojo {
    private final String id;
    private final String name;
    private final String description;
    private final String obligationCategoryId;
    private final String obligationCategoryName;

    public ObligationPojo(String id, String name, String description,
	    String obligationCategoryId, String obligationCategoryName) {
	this.id = id;
	this.name = name;
	this.description = description;
	this.obligationCategoryId = obligationCategoryId;
	this.obligationCategoryName = obligationCategoryName;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getObligationCategoryId() {
	return obligationCategoryId;
    }

    public String getObligationCategoryName() {
	return obligationCategoryName;
    }

    @Override
    public String toString() {
	return "ObligationPojo [id=" + id + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	ObligationPojo other = (ObligationPojo) obj;
	if (id == null) {
	    if (other.id != null) {
		return false;
	    }
	} else if (!id.equals(other.id)) {
	    return false;
	}
	return true;
    }

}
