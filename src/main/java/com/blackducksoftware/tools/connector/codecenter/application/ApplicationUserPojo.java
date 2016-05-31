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
