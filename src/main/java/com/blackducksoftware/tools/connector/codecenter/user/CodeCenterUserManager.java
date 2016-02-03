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
package com.blackducksoftware.tools.connector.codecenter.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.RoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.UserRoleAssignment;
import com.blackducksoftware.sdk.codecenter.user.data.User;
import com.blackducksoftware.sdk.codecenter.user.data.UserCreate;
import com.blackducksoftware.sdk.codecenter.user.data.UserIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserUpdate;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationRolePojo;

public class CodeCenterUserManager implements ICodeCenterUserManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final CodeCenterAPIWrapper ccApiWrapper;

    private final Map<String, User> usersByIdCache = new HashMap<>();

    private final Map<String, User> usersByNameCache = new HashMap<>();

    public CodeCenterUserManager(CodeCenterAPIWrapper ccApiWrapper) {
        this.ccApiWrapper = ccApiWrapper;
    }

    @Override
    public String createUser(String username, String password, String firstName, String lastName, String email, boolean active) throws CommonFrameworkException {
        UserCreate userCreate = new UserCreate();
        userCreate.setName(username);
        userCreate.setPassword(password);
        userCreate.setEmail(email);
        userCreate.setFirstName(firstName);
        userCreate.setLastName(lastName);
        userCreate.setActive(active);

        UserIdToken userIdToken;
        try {
            userIdToken = ccApiWrapper.getUserApi().createUser(userCreate);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error creating user " + username + ": " + e.getMessage());
        }

        return userIdToken.getId();
    }

    @Override
    public CodeCenterUserPojo getUserByName(String userName) throws CommonFrameworkException {
        User sdkUser = getSdkUserByNameCached(userName);
        return toPojo(sdkUser);
    }

    @Override
    public CodeCenterUserPojo getUserById(String userId) throws CommonFrameworkException {
        User sdkUser = getSdkUserByIdCached(userId);
        return toPojo(sdkUser);
    }

    @Override
    public void deleteUserById(String userId) throws CommonFrameworkException {
        removeUserByIdFromCache(userId);
        UserIdToken userIdToken = new UserIdToken();
        userIdToken.setId(userId);
        try {
            ccApiWrapper.getUserApi().deleteUser(userIdToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error deleting user with ID " + userId + ": " + e.getMessage());
        }
    }

    private void removeUserByIdFromCache(String userId) {
        if (usersByIdCache.containsKey(userId)) {
            User sdkUser = usersByIdCache.get(userId);
            usersByIdCache.remove(userId);
            usersByNameCache.remove(sdkUser.getName().getName());
        }
    }

    @Override
    public void setUserActiveStatus(String userId, boolean active) throws CommonFrameworkException {
        User sdkUser = getSdkUserByIdCached(userId);
        log.info("Setting user " + sdkUser.getId().getId() + " active flag to: " + active);
        removeUserByIdFromCache(userId);
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setId(sdkUser.getId());
        userUpdate.setActive(active);
        try {
            ccApiWrapper.getUserApi()
                    .updateUser(userUpdate);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error setting active status on user ID: " + userId + ": " + e.getMessage());
        }

    }

    private CodeCenterUserPojo toPojo(User sdkUser) {
        return new CodeCenterUserPojo(sdkUser.getId().getId(), sdkUser.getName().getName(), sdkUser.getFirstName(), sdkUser.getLastName(), sdkUser.getEmail(),
                sdkUser.isActive());
    }

    private User getSdkUserByNameCached(String userName) throws CommonFrameworkException {
        if (usersByNameCache.containsKey(userName)) {
            User sdkUser = usersByNameCache.get(userName);
            return sdkUser;
        }
        UserNameToken userNameToken = new UserNameToken();
        userNameToken.setName(userName);
        User sdkUser;
        try {
            sdkUser = ccApiWrapper.getUserApi().getUser(userNameToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting user with name " + userName + ": " + e.getMessage());
        }

        usersByIdCache.put(sdkUser.getId().getId(), sdkUser);
        usersByNameCache.put(sdkUser.getName().getName(), sdkUser);
        return sdkUser;
    }

    private User getSdkUserByIdCached(String userId) throws CommonFrameworkException {
        if (usersByIdCache.containsKey(userId)) {
            User sdkUser = usersByIdCache.get(userId);
            return sdkUser;
        }
        UserIdToken userIdToken = new UserIdToken();
        userIdToken.setId(userId);
        User sdkUser;
        try {
            sdkUser = ccApiWrapper.getUserApi().getUser(userIdToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting user with ID " + userId + ": " + e.getMessage());
        }

        usersByIdCache.put(sdkUser.getId().getId(), sdkUser);
        usersByNameCache.put(sdkUser.getName().getName(), sdkUser);
        return sdkUser;
    }

    @Override
    public List<ApplicationRolePojo> getApplicationRolesByUserName(String userName) throws CommonFrameworkException {
        List<ApplicationRolePojo> appRolePojos = new ArrayList<ApplicationRolePojo>();

        UserNameToken userToken = new UserNameToken();
        userToken.setName(userName);
        List<UserRoleAssignment> roles = null;

        try {
            roles = ccApiWrapper.getProxy().getRoleApi().getUserRoles(userToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting roles for user: " + userName + ": " + e.getMessage());
        }

        for (RoleAssignment role : roles) {

            if (role instanceof ApplicationRoleAssignment) {
                ApplicationRoleAssignment appRole = (ApplicationRoleAssignment) role;
                String appName = appRole.getApplicationNameVersionToken().getName();
                String appVersion = appRole.getApplicationNameVersionToken().getVersion();
                String roleName = appRole.getRoleNameToken().getName();
                log.debug("User " + userName + " has role " + roleName + " on Application " + appName + " / " + appVersion);
                ApplicationRolePojo appRolePojo = new ApplicationRolePojo(appRole.getApplicationIdToken().getId(), appName, appVersion,
                        appRole.getUserIdToken().getId(), appRole.getUserNameToken().getName(), appRole.getRoleIdToken().getId(),
                        roleName);
                appRolePojos.add(appRolePojo);
            }

        }
        return appRolePojos;
    }

}
