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

	public CodeCenterUserManager(final CodeCenterAPIWrapper ccApiWrapper) {
		this.ccApiWrapper = ccApiWrapper;
	}

	@Override
	public String createUser(final String username, final String password, final String firstName, final String lastName, final String email, final boolean active) throws CommonFrameworkException {
		final UserCreate userCreate = new UserCreate();
		userCreate.setName(username);
		userCreate.setPassword(password);
		userCreate.setEmail(email);
		userCreate.setFirstName(firstName);
		userCreate.setLastName(lastName);
		userCreate.setActive(active);

		UserIdToken userIdToken;
		try {
			userIdToken = ccApiWrapper.getUserApi().createUser(userCreate);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error creating user " + username + ": " + e.getMessage());
		}

		return userIdToken.getId();
	}

	@Override
	public CodeCenterUserPojo getUserByName(final String userName) throws CommonFrameworkException {
		log.debug("getUserByName(): Username: " + userName);
		final User sdkUser = getSdkUserByNameCached(userName);
		return toPojo(sdkUser);
	}

	@Override
	public CodeCenterUserPojo getUserById(final String userId) throws CommonFrameworkException {
		final User sdkUser = getSdkUserByIdCached(userId);
		return toPojo(sdkUser);
	}

	@Override
	public void deleteUserById(final String userId) throws CommonFrameworkException {
		removeUserByIdFromCache(userId);
		final UserIdToken userIdToken = new UserIdToken();
		userIdToken.setId(userId);
		try {
			ccApiWrapper.getUserApi().deleteUser(userIdToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error deleting user with ID " + userId + ": " + e.getMessage());
		}
	}

	private void removeUserByIdFromCache(final String userId) {
		if (usersByIdCache.containsKey(userId)) {
			final User sdkUser = usersByIdCache.get(userId);
			usersByIdCache.remove(userId);
			usersByNameCache.remove(sdkUser.getName().getName());
		}
	}

	@Override
	public void setUserActiveStatus(final String userId, final boolean active) throws CommonFrameworkException {
		final User sdkUser = getSdkUserByIdCached(userId);
		log.info("Setting user " + sdkUser.getId().getId() + " active flag to: " + active);
		removeUserByIdFromCache(userId);
		final UserUpdate userUpdate = new UserUpdate();
		userUpdate.setId(sdkUser.getId());
		userUpdate.setActive(active);
		try {
			ccApiWrapper.getUserApi()
			.updateUser(userUpdate);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error setting active status on user ID: " + userId + ": " + e.getMessage());
		}

	}

	private CodeCenterUserPojo toPojo(final User sdkUser) {
		return new CodeCenterUserPojo(sdkUser.getId().getId(), sdkUser.getName().getName(), sdkUser.getFirstName(), sdkUser.getLastName(), sdkUser.getEmail(),
				sdkUser.isActive());
	}

	private User getSdkUserByNameCached(final String userName) throws CommonFrameworkException {
		if (usersByNameCache.containsKey(userName)) {
			final User sdkUser = usersByNameCache.get(userName);
			return sdkUser;
		}
		final UserNameToken userNameToken = new UserNameToken();
		userNameToken.setName(userName);
		User sdkUser;
		try {
			log.debug("SDK: Getting user: " + userName);
			sdkUser = ccApiWrapper.getUserApi().getUser(userNameToken);
			log.debug("SDK: Done getting user");
		} catch (final SdkFault e) {
			log.debug("SDK: Error getting user");
			throw new CommonFrameworkException("Error getting user with name " + userName + ": " + e.getMessage());
		}

		usersByIdCache.put(sdkUser.getId().getId(), sdkUser);
		usersByNameCache.put(sdkUser.getName().getName(), sdkUser);
		return sdkUser;
	}

	private User getSdkUserByIdCached(final String userId) throws CommonFrameworkException {
		if (usersByIdCache.containsKey(userId)) {
			final User sdkUser = usersByIdCache.get(userId);
			return sdkUser;
		}
		final UserIdToken userIdToken = new UserIdToken();
		userIdToken.setId(userId);
		User sdkUser;
		try {
			sdkUser = ccApiWrapper.getUserApi().getUser(userIdToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error getting user with ID " + userId + ": " + e.getMessage());
		}

		usersByIdCache.put(sdkUser.getId().getId(), sdkUser);
		usersByNameCache.put(sdkUser.getName().getName(), sdkUser);
		return sdkUser;
	}

	@Override
	public List<ApplicationRolePojo> getApplicationRolesByUserName(final String userName) throws CommonFrameworkException {
		final List<ApplicationRolePojo> appRolePojos = new ArrayList<ApplicationRolePojo>();

		final UserNameToken userToken = new UserNameToken();
		userToken.setName(userName);
		List<UserRoleAssignment> roles = null;

		try {
			roles = ccApiWrapper.getProxy().getRoleApi().getUserRoles(userToken);
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error getting roles for user: " + userName + ": " + e.getMessage());
		}

		for (final RoleAssignment role : roles) {

			if (role instanceof ApplicationRoleAssignment) {
				final ApplicationRoleAssignment appRole = (ApplicationRoleAssignment) role;
				final String appName = appRole.getApplicationNameVersionToken().getName();
				final String appVersion = appRole.getApplicationNameVersionToken().getVersion();
				final String roleName = appRole.getRoleNameToken().getName();
				log.debug("User " + userName + " has role " + roleName + " on Application " + appName + " / " + appVersion);
				final ApplicationRolePojo appRolePojo = new ApplicationRolePojo(appRole.getApplicationIdToken().getId(), appName, appVersion,
						appRole.getUserIdToken().getId(), appRole.getUserNameToken().getName(), appRole.getRoleIdToken().getId(),
						roleName);
				appRolePojos.add(appRolePojo);
			}

		}
		return appRolePojos;
	}

}
