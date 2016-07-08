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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachment;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachmentCreate;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachmentToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationUpdate;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentContent;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentPageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.common.data.UserRolePageFilter;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.sdk.codecenter.role.data.ApplicationRoleAssignment;
import com.blackducksoftware.sdk.codecenter.role.data.RoleIdToken;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.role.data.RoleNameToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameOrIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentUtils;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.codecenter.component.ICodeCenterComponentManager;
import com.blackducksoftware.tools.connector.codecenter.user.CodeCenterUserPojo;
import com.blackducksoftware.tools.connector.codecenter.user.ICodeCenterUserManager;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * Provides a higher level of abstraction for accessing Code Center
 * applications.
 *
 * The objects returned are POJOs, not SDK objects.
 *
 * Applications are cached in case they are requested again.
 *
 * Attribute values are part of the application to which they are assigned.
 *
 * Multiple value attributes are not supported. If a multiple-value attribute is
 * read, the first value (only) will be used.
 *
 * @author sbillings
 *
 */
public class ApplicationManager implements IApplicationManager {
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	private final CodeCenterAPIWrapper ccApiWrapper;

	private final IAttributeDefinitionManager attrDefMgr;

	private final ICodeCenterComponentManager compMgr;

	private final ApplicationCache applicationCache;

	private final ICodeCenterUserManager userMgr;

	public ApplicationManager(final CodeCenterAPIWrapper ccApiWrapper,
			final IAttributeDefinitionManager attrDefMgr,
			final ICodeCenterComponentManager compMgr,
			final ICodeCenterUserManager userMgr, final ApplicationCache applicationCache) {
		this.ccApiWrapper = ccApiWrapper;
		this.attrDefMgr = attrDefMgr;
		this.compMgr = compMgr;
		this.userMgr = userMgr;
		this.applicationCache = applicationCache;
	}

	/**
	 * Get a subset (or all) of the applications that the current user can
	 * access.
	 *
	 * @param firstRow
	 *            get rows starting at this index (first = 0)
	 * @param lastRow
	 *            get rows ending at this index (use Integer.MAX_VALUE for all)
	 * @param searchString
	 *            set to null or "" to match all applications
	 * @return
	 * @throws CommonFrameworkException
	 */
	@Override
	public List<ApplicationPojo> getApplications(final int firstRow, final int lastRow)
			throws CommonFrameworkException {
		return getApplications(firstRow, lastRow, "");
	}

	/**
	 * Get a subset (or all) of the applications that the current user can
	 * access.
	 *
	 * Cache is not used, and not populated.
	 *
	 * @param firstRow
	 *            get rows starting at this index (first = 0)
	 * @param lastRow
	 *            get rows ending at this index (use Integer.MAX_VALUE for all)
	 * @param searchString
	 *            set to null or "" to match all applications
	 * @return
	 * @throws CommonFrameworkException
	 */
	@Override
	public List<ApplicationPojo> getApplications(final int firstRow, final int lastRow,
			String searchString) throws CommonFrameworkException {
		if (searchString == null) {
			searchString = "";
		}

		final ApplicationPageFilter pageFilter = new ApplicationPageFilter();
		pageFilter.setFirstRowIndex(firstRow);
		pageFilter.setLastRowIndex(lastRow);
		List<Application> ccApps;

		try {
			log.info("SDK: Searching applications for '" + searchString + "'"
					+ " index " + firstRow + "-" + lastRow);
			ccApps = ccApiWrapper.getApplicationApi().searchApplications(
					searchString, pageFilter);
			log.debug("SDK: Done searching applications; got: " + ccApps.size());
		} catch (final SdkFault e) {
			throw new CommonFrameworkException("Error getting applications "
					+ firstRow + " to " + lastRow + ": " + e.getMessage());
		}

		final List<ApplicationPojo> apps = new ArrayList<>(ccApps.size());
		for (final Application ccApp : ccApps) {
			final ApplicationPojo app = toPojo(ccApp);
			apps.add(app);
		}

		return apps;

	}

	@Override
	public List<ApplicationPojo> getAllApplications(final int chunkSize)
			throws CommonFrameworkException {
		final List<ApplicationPojo> fullAppList = new ArrayList<>();

		if ((chunkSize < 1) || (chunkSize >= (Integer.MAX_VALUE - 1))) {
			return getAllApplications();
		}

		long firstRow = 0;
		long lastRow = chunkSize - 1;
		while (true) {
			if (firstRow > Integer.MAX_VALUE) {
				break;
			}
			if (lastRow > Integer.MAX_VALUE) {
				lastRow = Integer.MAX_VALUE;
			}
			final int firstRowAsInt = (int) firstRow;
			final int lastRowAsInt = (int) lastRow;
			log.debug("Fetching applications: index " + firstRowAsInt + "-"
					+ lastRowAsInt);
			final List<ApplicationPojo> partialAppList = getApplications(
					firstRowAsInt, lastRowAsInt, "");
			if (partialAppList.size() == 0) {
				break; // there are no more
			}
			fullAppList.addAll(partialAppList);
			firstRow += chunkSize;
			lastRow += chunkSize;
		}

		return fullAppList;
	}

	@Override
	public List<ApplicationPojo> getAllApplications()
			throws CommonFrameworkException {
		return getApplications(0, Integer.MAX_VALUE);
	}

	/**
	 * Get an application by name/version.
	 *
	 * Applications are cached.
	 */
	@Override
	public ApplicationPojo getApplicationByNameVersion(final String name,
			final String version) throws CommonFrameworkException {
		final NameVersion nameVersion = new NameVersion(name, version);
		if (applicationCache.containsApplication(nameVersion)) {
			final Application app = applicationCache.getApplication(nameVersion);
			final ApplicationPojo appPojo = toPojo(app);
			return appPojo;
		}
		final ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
		appToken.setName(name);
		appToken.setVersion(version);
		Application app = null;
		try {
			log.debug("SDK: Getting application " + name + " / " + version);
			app = ccApiWrapper.getApplicationApi().getApplication(appToken);
			log.debug("SDK: Done getting application");
		} catch (final SdkFault e) {
			log.warn("SDK: Error getting application");
			throw new CommonFrameworkException("Error getting application "
					+ name + " / " + version + ": " + e.getMessage());
		}
		applicationCache.putApplication(app);

		return toPojo(app);
	}

	private ApplicationPojo toPojo(final Application app)
			throws CommonFrameworkException {
		final ApplicationPojo appPojo = new ApplicationPojo(app.getId().getId(),
				app.getName(), app.getVersion(), AttributeValues.valueOf(
						attrDefMgr, app.getAttributeValues()),
						ApprovalStatus.valueOf(app.getApprovalStatus()),
						app.isLocked(), app.getOwnerId().getId());
		return appPojo;
	}

	/**
	 * Get an application by ID.
	 *
	 * Applications are cached.
	 */
	@Override
	public ApplicationPojo getApplicationById(final String id)
			throws CommonFrameworkException {

		final Application app = getSdkApplicationByIdCached(id);

		return toPojo(app);
	}

	private Application getSdkApplicationByIdCached(final String applicationId)
			throws CommonFrameworkException {
		if (applicationCache.containsApplication(applicationId)) {
			final Application app = applicationCache.getApplication(applicationId);
			return app;
		}
		final ApplicationIdToken appToken = new ApplicationIdToken();
		appToken.setId(applicationId);
		Application app = null;
		try {
			log.debug("SDK: Getting application ID " + applicationId);
			app = ccApiWrapper.getApplicationApi().getApplication(appToken);
			log.debug("SDK: Done getting application");
		} catch (final SdkFault e) {
			log.error("SDK: Error getting application");
			throw new CommonFrameworkException(
					"Error getting application with ID " + applicationId + ": "
							+ e.getMessage());
		}
		final NameVersion nameVersion = new NameVersion(app.getName(),
				app.getVersion());
		applicationCache.putApplication(app);
		return app;
	}

	/**
	 * Get an application's requests, by application ID.
	 *
	 * Request lists are cached by application ID.
	 */
	@Override
	public List<RequestPojo> getRequestsByAppId(final String appId)
			throws CommonFrameworkException {

		// Check cache first
		if (applicationCache.containsRequestList(appId)) {
			return createRequestPojoList(appId,
					applicationCache.getRequestList(appId));
		}

		final ApplicationIdToken appToken = new ApplicationIdToken();
		appToken.setId(appId);

		final RequestPageFilter pageFilter = new RequestPageFilter();
		pageFilter.setFirstRowIndex(0);
		pageFilter.setLastRowIndex(Integer.MAX_VALUE);
		List<RequestSummary> requestSummaries;
		try {
			log.debug("SDK: Searching application requests for app ID " + appId);
			requestSummaries = ccApiWrapper.getApplicationApi()
					.searchApplicationRequests(appToken, null, pageFilter);
			log.debug("SDK: Done searching application requests for app ID " + appId);
		} catch (final SdkFault e) {
			log.error("SDK: Error searching applications");
			throw new CommonFrameworkException(
					"Error fetching requests for application ID " + appId
					+ ": " + e.getMessage());
		}

		// Cache the request list for this appId
		applicationCache.putRequestList(appId, requestSummaries);

		// Convert from sdk request list to pojo request list
		final List<RequestPojo> requests = createRequestPojoList(appId,
				requestSummaries);

		return requests;
	}

	@Override
	public <T extends CodeCenterComponentPojo> List<T> getComponentsByAppId(
			final Class<T> pojoClass, final String appId,
			final List<ApprovalStatus> limitToApprovalStatusValues, final boolean recursive)
					throws CommonFrameworkException {

		final List<T> allLevelComponents = collectComponents(pojoClass, appId,
				limitToApprovalStatusValues, recursive);
		return allLevelComponents;
	}

	// Private methods

	private <T extends CodeCenterComponentPojo> List<T> collectComponents(
			final Class<T> pojoClass, final String appId,
			final List<ApprovalStatus> limitToApprovalStatusValues, final boolean recursive)
					throws CommonFrameworkException {
		final List<RequestPojo> requests = getRequestsByAppId(appId);
		List<T> thisLevelComponents;

		thisLevelComponents = compMgr.getComponentsForRequests(pojoClass,
				requests, limitToApprovalStatusValues);

		final List<T> thisLevelAndBelowComponentsMinusApps = new ArrayList<>();
		for (final T comp : thisLevelComponents) {
			log.debug("Component: " + comp.getName() + " / "
					+ comp.getVersion());
			if (recursive && (comp.getApplicationId() != null)
					&& (comp.getApplicationId().length() > 0)) {
				final List<T> appCompsMinusApps = collectComponents(pojoClass,
						comp.getApplicationId(), limitToApprovalStatusValues,
						recursive);
				// thisLevelAndBelowComponentsMinusApps.addAll(appCompsMinusApps);
				final T appComp = compMgr.instantiatePojo(pojoClass);
				appComp.setId(comp.getId());
				appComp.setName(comp.getName());
				appComp.setVersion(comp.getVersion());
				appComp.setApprovalStatus(comp.getApprovalStatus());
				appComp.setHomepage(comp.getHomepage());
				appComp.setIntendedAudiences(comp.getIntendedAudiences());
				appComp.setKbComponentId(comp.getKbComponentId());
				appComp.setKbReleaseId(comp.getKbReleaseId());
				appComp.setApplicationComponent(comp.isApplicationComponent());
				appComp.setApplicationId(comp.getApplicationId());
				appComp.setDeprecated(comp.isDeprecated());
				appComp.setAttributeValues(comp.getAttributeValues());
				appComp.setLicenses(comp.getLicenses());
				appComp.setSubComponents(appCompsMinusApps);

				thisLevelAndBelowComponentsMinusApps.add(appComp);
			} else {
				thisLevelAndBelowComponentsMinusApps.add(comp);
			}
		}

		return thisLevelAndBelowComponentsMinusApps;
	}

	private List<RequestPojo> createRequestPojoList(final String appId,
			final List<RequestSummary> requestSummaries)
					throws CommonFrameworkException {
		final List<RequestPojo> requests = new ArrayList<>(requestSummaries.size());
		for (final RequestSummary sdkRequest : requestSummaries) {
			final RequestPojo request = createRequestPojo(appId, sdkRequest);
			requests.add(request);
		}
		return requests;
	}

	private RequestPojo createRequestPojo(final String appId,
			final RequestSummary sdkRequest) throws CommonFrameworkException {
		final RequestPojo request = new RequestPojo(sdkRequest.getId().getId(),
				appId, sdkRequest.getComponentId().getId(),
				ApprovalStatus.valueOf(sdkRequest.getApprovalStatus()),
				sdkRequest.getLicenseInfo().getId().getId());
		return request;
	}

	private ApplicationUserPojo toAppUserPojo(
			final ApplicationRoleAssignment roleAssignment)
					throws CommonFrameworkException {
		final ApplicationUserPojo appPojo = new ApplicationUserPojo();

		appPojo.setApplicationName(roleAssignment
				.getApplicationNameVersionToken().getName());
		appPojo.setApplicationVersion(roleAssignment
				.getApplicationNameVersionToken().getVersion());
		appPojo.setApplicationId(roleAssignment.getApplicationIdToken().getId());
		appPojo.setRoleId(roleAssignment.getRoleIdToken().getId());
		appPojo.setRoleName(roleAssignment.getRoleNameToken().getName());
		appPojo.setUserId(roleAssignment.getUserIdToken().getId());
		appPojo.setUserName(roleAssignment.getUserNameToken().getName());

		return appPojo;
	}

	/**
	 * Gets all User's that are assigned to the specified Application.
	 *
	 * @param appId
	 *            String
	 * @return List<(ApplicationUserPojo)>
	 * @throws CommonFrameworkException
	 */
	@Override
	public List<ApplicationUserPojo> getAllUsersAssignedToApplication(
			final String appId) throws CommonFrameworkException {

		final ApplicationIdToken appToken = new ApplicationIdToken();
		appToken.setId(appId);

		final UserRolePageFilter userFilter = new UserRolePageFilter();
		userFilter.setFirstRowIndex(0);
		userFilter.setLastRowIndex(Integer.MAX_VALUE);

		List<ApplicationRoleAssignment> roleAssignments = null;
		try {
			log.debug("SDK: Searching application with ID " + appId
					+ " for all users on team");
			roleAssignments = ccApiWrapper.getApplicationApi()
					.searchUserInApplicationTeam(appToken, "", userFilter);
			log.debug("SDK: Done searching application for users");
		} catch (final SdkFault e) {
			log.error("SDK: Error searching application for users");
			throw new CommonFrameworkException(
					"Error getting User's assigned to this Application :"
							+ e.getMessage());
		}

		final List<ApplicationUserPojo> appUsers = new ArrayList<>(
				roleAssignments.size());
		for (final ApplicationRoleAssignment roleAssignment : roleAssignments) {
			final ApplicationUserPojo appUser = toAppUserPojo(roleAssignment);
			appUsers.add(appUser);
		}

		return appUsers;
	}

	/**
	 * Add the given list of users (by ID) to the given application, each with
	 * the given roles.
	 *
	 * @param appId
	 *            Application ID
	 * @param userIds
	 *            User IDs
	 * @param roleNames
	 *            Role names
	 * @param circumventLock
	 *            if true: if application is locked, unlock it, add users,
	 *            re-lock it
	 * @throws CommonFrameworkException
	 */
	@Override
	public void addUsersByIdToApplicationTeam(final String appId,
			final Set<String> userIds, final Set<String> roleNames, final boolean circumventLock)
					throws CommonFrameworkException {

		final Application app = getSdkApplicationByIdCached(appId);
		final boolean origLockValue = ensureUnlocked(circumventLock, app);
		final List<UserNameOrIdToken> userIdTokens = generateUserTokensFromUserIds(userIds);
		final List<RoleNameOrIdToken> roleNameTokens = generateRoleTokens(roleNames);

		addUsersToApp(app, userIdTokens, roleNameTokens);

		restoreLock(app, origLockValue);
	}

	/**
	 * Add the given list of users (by username) to the given application, each
	 * with the given roles.
	 *
	 * @param appId
	 *            Application ID
	 * @param userNames
	 *            User Names
	 * @param roleNames
	 *            Role names
	 * @param circumventLock
	 *            if true: if application is locked, unlock it, add users,
	 *            re-lock it
	 * @throws CommonFrameworkException
	 */
	@Override
	public void addUsersByNameToApplicationTeam(final String appId,
			final Set<String> userNames, final Set<String> roleNames, final boolean circumventLock)
					throws CommonFrameworkException {

		final Application app = getSdkApplicationByIdCached(appId);
		final boolean origLockValue = ensureUnlocked(circumventLock, app);
		final List<UserNameOrIdToken> userIdTokens = generateUserTokensFromUserNames(userNames);
		final List<RoleNameOrIdToken> roleNameTokens = generateRoleTokens(roleNames);

		addUsersToApp(app, userIdTokens, roleNameTokens);

		restoreLock(app, origLockValue);
	}

	private void addUsersToApp(final Application app,
			final List<UserNameOrIdToken> userIdTokens,
			final List<RoleNameOrIdToken> roleNameTokens)
					throws CommonFrameworkException {
		if (userIdTokens.size() == 0) {
			log.warn("Application " + app.getName() + " / " + app.getVersion()
					+ ": addUsersToApp(): No users specified");
			return;
		}
		if (roleNameTokens.size() == 0) {
			log.warn("Application " + app.getName() + " / " + app.getVersion()
					+ ": addUsersToApp(): No roles specified");
			return;
		}
		try {
			log.debug("SDK: Adding " + userIdTokens.size()
					+ " users to application " + app.getName() + " / "
					+ app.getVersion());
			ccApiWrapper.getApplicationApi().addUserToApplicationTeam(
					app.getNameVersion(), userIdTokens, roleNameTokens);
			log.debug("SDK: Done adding users to application");
		} catch (final SdkFault e) {
			final String msg = "Error adding users to application " + app.getName()
					+ " / " + app.getVersion() + ": " + e.getMessage();
			log.error(msg, e);
			throw new CommonFrameworkException(msg);
		}
	}

	private List<RoleNameOrIdToken> generateRoleTokens(final Set<String> roleNames) {
		final List<RoleNameOrIdToken> roleNameTokens = new ArrayList<>(
				roleNames.size());
		for (final String roleName : roleNames) {
			final RoleNameToken roleNameToken = new RoleNameToken();
			roleNameToken.setName(roleName);
			roleNameTokens.add(roleNameToken);
		}
		return roleNameTokens;
	}

	private List<UserNameOrIdToken> generateUserTokensFromUserIds(
			final Set<String> userIds) {
		final List<UserNameOrIdToken> userIdTokens = new ArrayList<>(userIds.size());
		for (final String userId : userIds) {
			final UserIdToken userIdToken = new UserIdToken();
			userIdToken.setId(userId);
			userIdTokens.add(userIdToken);
		}
		return userIdTokens;
	}

	private List<UserNameOrIdToken> generateUserTokensFromUserNames(
			final Set<String> userNames) {
		final List<UserNameOrIdToken> userIdTokens = new ArrayList<>(userNames.size());
		for (final String userName : userNames) {
			final UserNameToken userNameToken = getUserToken(userName);
			userIdTokens.add(userNameToken);
		}
		return userIdTokens;
	}

	private void restoreLock(final Application app, final boolean origLockValue)
			throws CommonFrameworkException {
		if (origLockValue) {
			// If app was locked and we got this far, circumventLock must be
			// true
			try {
				lock(app, true); // lock
			} catch (final SdkFault e) {
				throw new CommonFrameworkException(
						"Error re-locking (after adjusting users) application "
								+ app.getName() + " / " + app.getVersion()
								+ ": " + e.getMessage());
			}
		}
	}

	private boolean ensureUnlocked(final boolean circumventLock, final Application app)
			throws CommonFrameworkException {
		final boolean origLockValue = app.isLocked();
		if (origLockValue) {
			if (!circumventLock) {
				throw new CommonFrameworkException(
						"Error adjusting users on application " + app.getName()
						+ " / " + app.getVersion()
						+ ": Application is locked");
			} else {
				try {
					lock(app, false); // unlock
				} catch (final SdkFault e) {
					throw new CommonFrameworkException(
							"Error unlocking (for adjusting users) application "
									+ app.getName() + " / " + app.getVersion()
									+ ": " + e.getMessage());
				}
			}
		}
		return origLockValue;
	}

	/**
	 * Remove the given user+role from the given application's team.
	 *
	 * @param appId
	 *            Application ID
	 * @param userId
	 *            User ID
	 * @param roleId
	 *            Role ID
	 * @param circumventLock
	 *            if true: if application is locked, unlock it, remove users,
	 *            re-lock it
	 * @throws CommonFrameworkException
	 */
	@Override
	public void removeUserByIdFromApplicationTeam(final String appId, final String userId,
			final String roleId, final boolean circumventLock)
					throws CommonFrameworkException {

		final Application app = getSdkApplicationByIdCached(appId);
		final boolean origLockValue = ensureUnlocked(circumventLock, app);

		final UserIdToken userIdToken = new UserIdToken();
		userIdToken.setId(userId);

		final RoleIdToken roleIdToken = new RoleIdToken();
		roleIdToken.setId(roleId);

		try {
			log.debug("SDK: Removing user " + userId + " / role ID " + roleId
					+ " from application " + app.getName() + " / "
					+ app.getVersion());
			ccApiWrapper.getApplicationApi().removeUserInApplicationTeam(
					app.getNameVersion(), userIdToken, roleIdToken);
			log.debug("SDK: Done removing user from application");
		} catch (final SdkFault e) {
			log.error("SDK: Error removing user from application");
			throw new CommonFrameworkException("Error removing user ID "
					+ userId + " from application " + app.getName() + " / "
					+ app.getVersion() + ": " + e.getMessage());
		}
		restoreLock(app, origLockValue);
	}

	/**
	 * Remove a given set of users (by username), all roles, from the given
	 * application.
	 *
	 * @param appId
	 * @param usernames
	 * @param circumventLock
	 * @throws CommonFrameworkException
	 */
	@Override
	public List<UserStatus> removeUsersByNameFromApplicationAllRoles(
			final String appId, final Set<String> usernames, final boolean circumventLock)
					throws CommonFrameworkException {
		final Application app = getSdkApplicationByIdCached(appId);
		final ApplicationNameVersionToken appToken = getAppToken(app);
		final boolean origLockValue = ensureUnlocked(circumventLock, app);

		log.debug("removeUsersByNameFromApplicationAllRoles(): App: "
				+ app.getName() + " / " + app.getVersion() + ": Users: "
				+ usernames);
		final List<UserStatus> userDeletionStatus = new ArrayList<UserStatus>(
				usernames.size());
		if (usernames.size() == 0) {
			return userDeletionStatus;
		}

		final List<ApplicationUserPojo> originalTeam = getAllUsersAssignedToApplication(app
				.getId().getId());
		for (final String username : usernames) {
			log.info("Removing user: " + username + " from app "
					+ app.getName());

			final String targetUserId = getUserId(username);
			final UserNameToken userToken = getUserToken(username);

			for (final ApplicationUserPojo originalTeamMemberRole : originalTeam) {

				final boolean targetedUser = checkForTargetedUser(app, targetUserId,
						originalTeamMemberRole);
				if (!targetedUser) {
					log.debug("This user ("
							+ originalTeamMemberRole.getUserName()
							+ ") is not the user we're looking for... skipping it");
					continue;
				}

				final RoleIdToken roleToken = getRoleToken(originalTeamMemberRole);
				try {
					log.debug("SDK: Removing user " + username + " / role ID "
							+ roleToken.getId() + " from application "
							+ appToken.getName() + " / "
							+ appToken.getVersion());
					ccApiWrapper.getApplicationApi()
					.removeUserInApplicationTeam(appToken, userToken,
							roleToken);
					log.debug("SDK: Done removing user " + username);
					userDeletionStatus
					.add(new UserStatus(username, true, null));
				} catch (final SdkFault e) {
					final String msg = "Error removing user " + username
							+ " with role "
							+ originalTeamMemberRole.getRoleName()
							+ " from application " + app.getName()
							+ " version " + app.getVersion() + ": "
							+ e.getMessage();
					log.error(msg);
					userDeletionStatus.add(new UserStatus(username, false, e
							.getMessage()));

				}
			}
		}

		restoreLock(app, origLockValue);
		return userDeletionStatus;
	}

	private RoleIdToken getRoleToken(final ApplicationUserPojo originalTeamMemberRole) {
		final RoleIdToken roleToken = new RoleIdToken();
		roleToken.setId(originalTeamMemberRole.getRoleId());
		return roleToken;
	}

	private UserNameToken getUserToken(final String username) {
		final UserNameToken userToken = new UserNameToken();
		userToken.setName(username);
		return userToken;
	}

	private ApplicationNameVersionToken getAppToken(final Application app) {
		final ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
		appToken.setName(app.getName());
		appToken.setVersion(app.getVersion());
		return appToken;
	}

	private String getUserId(final String username) throws CommonFrameworkException {
		final CodeCenterUserPojo targetUser = userMgr.getUserByName(username);
		final String targetUserId = targetUser.getId();
		return targetUserId;
	}

	private boolean checkForTargetedUser(final Application app, final String targetUserId,
			final ApplicationUserPojo originalTeamMember) {
		boolean targetedUser = false;
		log.debug("Checking user " + originalTeamMember.getUserName() + " ("
				+ originalTeamMember.getUserId() + ") " + " role "
				+ originalTeamMember.getRoleName() + " from app "
				+ app.getName());

		if (originalTeamMember.getUserId().equals(targetUserId)) {
			targetedUser = true;
		}
		return targetedUser;
	}

	@Override
	public List<AttachmentDetails> searchAttachments(final String applicationId,
			final String searchString) throws CommonFrameworkException {
		final Application comp = getSdkApplicationByIdCached(applicationId);

		final AttachmentPageFilter pageFilter = new AttachmentPageFilter();
		pageFilter.setFirstRowIndex(0);
		pageFilter.setLastRowIndex(Integer.MAX_VALUE);
		List<ApplicationAttachment> sdkAttachments;
		try {
			log.debug("SDK: Searching for application attachment "
					+ comp.getId());
			sdkAttachments = ccApiWrapper.getApplicationApi()
					.searchApplicationAttachments("", pageFilter, comp.getId());
			log.debug("SDK: Done searching for application attachment");
		} catch (final SdkFault e) {
			log.warn("SDK: Error searching for application attachment");
			throw new CommonFrameworkException(
					"Error searching attachments for application ID "
							+ applicationId + ": " + e.getMessage());
		}

		final List<AttachmentDetails> attachmentDetailsList = new ArrayList<>(
				sdkAttachments.size());
		for (final ApplicationAttachment sdkAttachment : sdkAttachments) {
			final AttachmentDetails attachmentDetails = createAttachmentDetails(sdkAttachment);
			attachmentDetailsList.add(attachmentDetails);
		}

		return attachmentDetailsList;
	}

	private AttachmentDetails createAttachmentDetails(
			final ApplicationAttachment sdkAttachment) {
		final AttachmentDetails attachmentDetails = new AttachmentDetails(
				sdkAttachment.getId(), sdkAttachment.getFileName(),
				sdkAttachment.getDescription(),
				sdkAttachment.getTimeUploaded(), sdkAttachment
				.getUserUploaded().getId(),
				sdkAttachment.getContentType(),
				sdkAttachment.getFilesizeBytes());
		return attachmentDetails;
	}

	@Override
	public File downloadAttachment(final String applicationId, final String filename,
			final String targetDirPath) throws CommonFrameworkException {
		final ApplicationAttachmentToken attachmentToken = new ApplicationAttachmentToken();
		final ApplicationIdToken compToken = new ApplicationIdToken();
		compToken.setId(applicationId);
		attachmentToken.setApplicationId(compToken);
		attachmentToken.setFileName(filename);
		AttachmentContent content;
		try {
			log.debug("SDK: Getting application attachment content for application ID "
					+ applicationId + " filename " + filename);
			content = ccApiWrapper.getApplicationApi()
					.getApplicationAttachmentContent(attachmentToken);
			log.debug("SDK: Done getting application attachment");
		} catch (final SdkFault e) {
			log.error("SDK: Error getting application attachment");
			throw new CommonFrameworkException(
					"Error getting data handler for application ID "
							+ applicationId + " attachment " + filename + ": "
							+ e.getMessage());
		}
		final DataHandler dataHandler = content.getAttachmentContent();
		return AttachmentUtils.downloadAttachment("application", applicationId,
				filename, targetDirPath, dataHandler);
	}

	@Override
	public void attachFile(final String applicationId, final String sourceFilePath,
			final String description) throws CommonFrameworkException {
		final File file = new File(sourceFilePath);

		final Application comp = getSdkApplicationByIdCached(applicationId);
		final ApplicationAttachmentCreate attachmentCreateBean = new ApplicationAttachmentCreate();
		attachmentCreateBean.setApplicationId(comp.getId());
		attachmentCreateBean.setFileName(file.getName());
		attachmentCreateBean.setDescription(description);
		attachmentCreateBean.setName(file.getName());

		URL url = null;
		try {
			url = new File(sourceFilePath).toURI().toURL();
		} catch (final MalformedURLException mue) {
			throw new CommonFrameworkException(mue.getMessage());
		}

		final DataHandler dataHandler = new DataHandler(url);
		attachmentCreateBean.setAttachmentContent(dataHandler);
		try {
			log.debug("SDK: Creating application attachment on application "
					+ applicationId + " sourceFilePath " + sourceFilePath);
			ccApiWrapper.getApplicationApi().createApplicationAttachment(
					attachmentCreateBean);
			log.debug("SDK: Done creating application attachment");
		} catch (final SdkFault e) {
			log.error("SDK: Error creating application attachment");
			throw new CommonFrameworkException(
					"Error creating application attachment on application ID "
							+ applicationId + " from file " + url.toString()
							+ ": " + e.getMessage());
		}

	}

	@Override
	public void deleteAttachment(final String applicationId, final String filename)
			throws CommonFrameworkException {
		final ApplicationIdToken applicationIdToken = new ApplicationIdToken();
		applicationIdToken.setId(applicationId);

		final ApplicationAttachmentToken attachmentToken = new ApplicationAttachmentToken();
		attachmentToken.setApplicationId(applicationIdToken);
		attachmentToken.setFileName(filename);
		try {
			log.debug("SDK: Deleting application attachment on application "
					+ applicationId + " / filename " + filename);
			ccApiWrapper.getApplicationApi().deleteApplicationAttachment(
					attachmentToken);
			log.debug("SDK: Done deleting application attachment");
		} catch (final SdkFault e) {
			log.error("SDK: Error deleting application attachment");
			throw new CommonFrameworkException("Error deleting file "
					+ filename + " from application ID " + applicationId + ": "
					+ e.getMessage());
		}

	}

	/**
	 * Update some attribute values on the given application.
	 *
	 * Only supports single-value attributes.
	 */
	@Override
	public void updateAttributeValues(final String appId,
			final Set<AttributeValuePojo> changedAttrValues)
					throws CommonFrameworkException {

		log.info("updateAttributeValues() called with application ID: " + appId);
		final Application app = getSdkApplicationByIdCached(appId);

		final ApplicationUpdate applicationUpdate = new ApplicationUpdate();

		applicationUpdate.setId(app.getId());

		for (final AttributeValuePojo attrValue : changedAttrValues) {

			final String attrName = attrValue.getName();

			final AttributeValue attrValueObject = new AttributeValue();
			final AttributeIdToken attrIdToken = new AttributeIdToken();
			attrIdToken.setId(attrValue.getAttrId());
			attrValueObject.setAttributeId(attrIdToken);
			attrValueObject.getValues().add(attrValue.getValue());

			log.info("Setting attribute " + attrName + " to "
					+ attrValue.getValue());
			applicationUpdate.getAttributeValues().add(attrValueObject);
		}

		try {
			log.debug("SDK: Updating custom attribute values on application");
			ccApiWrapper.getApplicationApi().updateApplication(
					applicationUpdate);
			log.debug("SDK: Done updating custom attribute values on application");
		} catch (final SdkFault e) {
			log.error("SDK: Error updating custom attribute values on application");
			throw new CommonFrameworkException(
					"Error updating attribute values on application "
							+ app.getName() + ": " + e.getMessage());
		}
		applicationCache.removeApplication(app); // remove stale cache entry
	}

	private void lock(final Application app, final boolean lockValue) throws SdkFault {
		final ApplicationIdToken appToken = new ApplicationIdToken();
		appToken.setId(app.getId().getId());

		try {
			log.debug("SDK: Changing application " + app.getName() + " / "
					+ app.getVersion() + " lock value to: " + lockValue);
			ccApiWrapper.getApplicationApi().lockApplication(appToken,
					lockValue);
			log.debug("SDK: Done changing lock");
		} catch (final SdkFault e) {
			log.error("SDK: Error changing lcok on application");
			throw e;
		}
	}

	@Override
	public void removeApplicationFromCacheById(final String appId)
			throws CommonFrameworkException {
		applicationCache.remove(appId);
	}

	@Override
	public void removeApplicationFromCacheByNameVersion(final String name,
			final String version) throws CommonFrameworkException {

		applicationCache.remove(name, version);
	}
}
