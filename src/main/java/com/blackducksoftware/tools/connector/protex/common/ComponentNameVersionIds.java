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
package com.blackducksoftware.tools.connector.protex.common;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.sdk.protex.common.ComponentKey;
import com.blackducksoftware.sdk.protex.component.Component;
import com.blackducksoftware.sdk.protex.project.bom.BomComponent;

public class ComponentNameVersionIds {
    private final String nameId;

    private final String versionId;

    public ComponentNameVersionIds(String nameId, String versionId) {
        this.nameId = nameId;
        if (versionId == null) {
            this.versionId = "";
        } else {
            this.versionId = versionId;
        }
    }

    public String getNameId() {
        return nameId;
    }

    public String getVersionId() {
        return versionId;
    }

    @Override
    public String toString() {
        return "ComponentNameVersionIds [nameId=" + nameId + ", versionId="
                + versionId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nameId == null) ? 0 : nameId.hashCode());
        result = prime * result
                + ((versionId == null) ? 0 : versionId.hashCode());
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
        ComponentNameVersionIds other = (ComponentNameVersionIds) obj;
        if (nameId == null) {
            if (other.nameId != null) {
                return false;
            }
        } else if (!nameId.equals(other.nameId)) {
            return false;
        }
        if (versionId == null) {
            if (other.versionId != null) {
                return false;
            }
        } else if (!versionId.equals(other.versionId)) {
            return false;
        }
        return true;
    }

    public static ComponentNameVersionIds valueOf(BomComponent bomComponent) {
        ComponentNameVersionIds nameVersionIds = new ComponentNameVersionIds(
                bomComponent.getComponentKey().getComponentId(), bomComponent
                        .getComponentKey().getVersionId());
        return nameVersionIds;
    }

    public static ComponentNameVersionIds valueOf(Component component) {
        ComponentNameVersionIds nameVersionIds = new ComponentNameVersionIds(
                component.getComponentKey().getComponentId(), component
                        .getComponentKey().getVersionId());
        return nameVersionIds;
    }

    public static ComponentKey toProtexComponentKey(
            ComponentNameVersionIds nameVersionIds) {
        ComponentKey key = new ComponentKey();
        key.setComponentId(nameVersionIds.getNameId());
        if (!StringUtils.isBlank(nameVersionIds.getVersionId())) {
            key.setVersionId(nameVersionIds.getVersionId());
        }
        return key;
    }
}
