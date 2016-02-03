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
package com.blackducksoftware.tools.connector.codecenter.application;

/**
 * A non-SDK-specific class representing a User assigned to an Application.
 *
 *
 * @author jrichard
 *
 */
public class ApplicationUserPojo {

    private String applicationName;

    private String applicationVersion;

    private String applicationId;

    private String userName;

    private String userId;

    private String roleName;

    private String roleId;

    public ApplicationUserPojo() {

    }

    public ApplicationUserPojo(String applicationName, String applicationVersion, String applicationId,
            String userName, String userId, String roleName, String roleId) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.applicationId = applicationId;
        this.userName = userName;
        this.userId = userId;
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "ApplicationUserPojo [applicationName=" + applicationName + ", applicationVersion=" + applicationVersion + ", userName=" + userName
                + ", roleName=" + roleName + "]";
    }

}
