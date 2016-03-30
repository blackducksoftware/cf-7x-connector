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

    public ApplicationManager(CodeCenterAPIWrapper ccApiWrapper,
            IAttributeDefinitionManager attrDefMgr,
            ICodeCenterComponentManager compMgr,
            ICodeCenterUserManager userMgr,
            ApplicationCache applicationCache) {
        this.ccApiWrapper = ccApiWrapper;
        this.attrDefMgr = attrDefMgr;
        this.compMgr = compMgr;
        this.userMgr = userMgr;
        this.applicationCache = applicationCache;
    }

    /**
     * Get a subset (or all) of the applications that the current user can access.
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
    public List<ApplicationPojo> getApplications(int firstRow, int lastRow)
            throws CommonFrameworkException {
        return getApplications(firstRow, lastRow, "");
    }

    /**
     * Get a subset (or all) of the applications that the current user can access.
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
    public List<ApplicationPojo> getApplications(int firstRow, int lastRow, String searchString)
            throws CommonFrameworkException {
        if (searchString == null) {
            searchString = "";
        }

        ApplicationPageFilter pageFilter = new ApplicationPageFilter();
        pageFilter.setFirstRowIndex(firstRow);
        pageFilter.setLastRowIndex(lastRow);
        List<Application> ccApps;

        try {
            log.info("SDK: Searching applications for '" + searchString + "'");
            ccApps = ccApiWrapper.getApplicationApi().searchApplications(searchString,
                    pageFilter);
            log.debug("SDK: Done searching applications");
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting applications "
                    + firstRow + " to " + lastRow + ": " + e.getMessage());
        }

        List<ApplicationPojo> apps = new ArrayList<>(ccApps.size());
        for (Application ccApp : ccApps) {
            ApplicationPojo app = toPojo(ccApp);
            apps.add(app);
        }

        return apps;

    }

    /**
     * Get an application by name/version.
     *
     * Applications are cached.
     */
    @Override
    public ApplicationPojo getApplicationByNameVersion(String name,
            String version) throws CommonFrameworkException {
        NameVersion nameVersion = new NameVersion(name, version);
        if (applicationCache.containsApplication(nameVersion)) {
            Application app = applicationCache.getApplication(nameVersion);
            ApplicationPojo appPojo = toPojo(app);
            return appPojo;
        }
        ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
        appToken.setName(name);
        appToken.setVersion(version);
        Application app = null;
        try {
            log.debug("SDK: Getting application " + name + " / " + version);
            app = ccApiWrapper.getApplicationApi().getApplication(appToken);
            log.debug("SDK: Done getting application");
        } catch (SdkFault e) {
            log.warn("SDK: Error getting application");
            throw new CommonFrameworkException("Error getting application "
                    + name + " / " + version + ": " + e.getMessage());
        }
        applicationCache.putApplication(app);

        return toPojo(app);
    }

    private ApplicationPojo toPojo(Application app)
            throws CommonFrameworkException {
        ApplicationPojo appPojo = new ApplicationPojo(app.getId().getId(),
                app.getName(), app.getVersion(), AttributeValues.valueOf(
                        attrDefMgr, app.getAttributeValues()),
                ApprovalStatus.valueOf(app.getApprovalStatus()), app.isLocked(), app.getOwnerId().getId());
        return appPojo;
    }

    /**
     * Get an application by ID.
     *
     * Applications are cached.
     */
    @Override
    public ApplicationPojo getApplicationById(String id)
            throws CommonFrameworkException {

        Application app = getSdkApplicationByIdCached(id);

        return toPojo(app);
    }

    private Application getSdkApplicationByIdCached(String applicationId)
            throws CommonFrameworkException {
        if (applicationCache.containsApplication(applicationId)) {
            Application app = applicationCache.getApplication(applicationId);
            return app;
        }
        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(applicationId);
        Application app = null;
        try {
            log.debug("SDK: Getting application ID " + applicationId);
            app = ccApiWrapper.getApplicationApi().getApplication(appToken);
            log.debug("SDK: Done getting application");
        } catch (SdkFault e) {
            log.error("SDK: Error getting application");
            throw new CommonFrameworkException(
                    "Error getting application with ID " + applicationId + ": "
                            + e.getMessage());
        }
        NameVersion nameVersion = new NameVersion(app.getName(),
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
    public List<RequestPojo> getRequestsByAppId(String appId)
            throws CommonFrameworkException {

        // Check cache first
        if (applicationCache.containsRequestList(appId)) {
            return createRequestPojoList(appId,
                    applicationCache.getRequestList(appId));
        }

        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(appId);

        RequestPageFilter pageFilter = new RequestPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        List<RequestSummary> requestSummaries;
        try {
            log.debug("SDK: Searching application requests for app ID " + appId);
            requestSummaries = ccApiWrapper.getApplicationApi()
                    .searchApplicationRequests(appToken, null, pageFilter);
            log.debug("SDK: Done searching applications");
        } catch (SdkFault e) {
            log.error("SDK: Error searching applications");
            throw new CommonFrameworkException(
                    "Error fetching requests for application ID " + appId
                            + ": " + e.getMessage());
        }

        // Cache the request list for this appId
        applicationCache.putRequestList(appId, requestSummaries);

        // Convert from sdk request list to pojo request list
        List<RequestPojo> requests = createRequestPojoList(appId,
                requestSummaries);

        return requests;
    }

    @Override
    public <T extends CodeCenterComponentPojo> List<T> getComponentsByAppId(
            Class<T> pojoClass, String appId,
            List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
            throws CommonFrameworkException {

        List<T> allLevelComponents = collectComponents(pojoClass, appId,
                limitToApprovalStatusValues, recursive);
        return allLevelComponents;
    }

    // Private methods

    private <T extends CodeCenterComponentPojo> List<T> collectComponents(
            Class<T> pojoClass, String appId,
            List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
            throws CommonFrameworkException {
        List<RequestPojo> requests = getRequestsByAppId(appId);
        List<T> thisLevelComponents;

        thisLevelComponents = compMgr.getComponentsForRequests(pojoClass,
                requests, limitToApprovalStatusValues);

        List<T> thisLevelAndBelowComponentsMinusApps = new ArrayList<>();
        for (T comp : thisLevelComponents) {
            log.debug("Component: " + comp.getName() + " / "
                    + comp.getVersion());
            if (recursive && (comp.getApplicationId() != null)
                    && (comp.getApplicationId().length() > 0)) {
                List<T> appCompsMinusApps = collectComponents(pojoClass,
                        comp.getApplicationId(), limitToApprovalStatusValues,
                        recursive);
                // thisLevelAndBelowComponentsMinusApps.addAll(appCompsMinusApps);
                T appComp = compMgr.instantiatePojo(pojoClass);
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

    private List<RequestPojo> createRequestPojoList(String appId,
            List<RequestSummary> requestSummaries)
            throws CommonFrameworkException {
        List<RequestPojo> requests = new ArrayList<>(requestSummaries.size());
        for (RequestSummary sdkRequest : requestSummaries) {
            RequestPojo request = createRequestPojo(appId, sdkRequest);
            requests.add(request);
        }
        return requests;
    }

    private RequestPojo createRequestPojo(String appId,
            RequestSummary sdkRequest) throws CommonFrameworkException {
        RequestPojo request = new RequestPojo(sdkRequest.getId().getId(),
                appId, sdkRequest.getComponentId().getId(),
                ApprovalStatus.valueOf(sdkRequest.getApprovalStatus()),
                sdkRequest.getLicenseInfo().getId().getId());
        return request;
    }

    private ApplicationUserPojo toAppUserPojo(
            ApplicationRoleAssignment roleAssignment)
            throws CommonFrameworkException {
        ApplicationUserPojo appPojo = new ApplicationUserPojo();

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
            String appId) throws CommonFrameworkException {

        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(appId);

        UserRolePageFilter userFilter = new UserRolePageFilter();
        userFilter.setFirstRowIndex(0);
        userFilter.setLastRowIndex(Integer.MAX_VALUE);

        List<ApplicationRoleAssignment> roleAssignments = null;
        try {
            log.debug("SDK: Searching application with ID " + appId + " for all users on team");
            roleAssignments = ccApiWrapper.getApplicationApi()
                    .searchUserInApplicationTeam(appToken, "", userFilter);
            log.debug("SDK: Done searching application for users");
        } catch (SdkFault e) {
            log.error("SDK: Error searching application for users");
            throw new CommonFrameworkException(
                    "Error getting User's assigned to this Application :"
                            + e.getMessage());
        }

        List<ApplicationUserPojo> appUsers = new ArrayList<>(
                roleAssignments.size());
        for (ApplicationRoleAssignment roleAssignment : roleAssignments) {
            ApplicationUserPojo appUser = toAppUserPojo(roleAssignment);
            appUsers.add(appUser);
        }

        return appUsers;
    }

    /**
     * Add the given list of users (by ID) to the given application, each with the given roles.
     *
     * @param appId
     *            Application ID
     * @param userIds
     *            User IDs
     * @param roleNames
     *            Role names
     * @param circumventLock
     *            if true: if application is locked, unlock it, add users, re-lock it
     * @throws CommonFrameworkException
     */
    @Override
    public void addUsersByIdToApplicationTeam(String appId, Set<String> userIds, Set<String> roleNames,
            boolean circumventLock)
            throws CommonFrameworkException {

        Application app = getSdkApplicationByIdCached(appId);
        boolean origLockValue = ensureUnlocked(circumventLock, app);
        List<UserNameOrIdToken> userIdTokens = generateUserTokensFromUserIds(userIds);
        List<RoleNameOrIdToken> roleNameTokens = generateRoleTokens(roleNames);

        addUsersToApp(app, userIdTokens, roleNameTokens);

        restoreLock(app, origLockValue);
    }

    /**
     * Add the given list of users (by username) to the given application, each with the given roles.
     *
     * @param appId
     *            Application ID
     * @param userNames
     *            User Names
     * @param roleNames
     *            Role names
     * @param circumventLock
     *            if true: if application is locked, unlock it, add users, re-lock it
     * @throws CommonFrameworkException
     */
    @Override
    public void addUsersByNameToApplicationTeam(String appId, Set<String> userNames, Set<String> roleNames,
            boolean circumventLock)
            throws CommonFrameworkException {

        Application app = getSdkApplicationByIdCached(appId);
        boolean origLockValue = ensureUnlocked(circumventLock, app);
        List<UserNameOrIdToken> userIdTokens = generateUserTokensFromUserNames(userNames);
        List<RoleNameOrIdToken> roleNameTokens = generateRoleTokens(roleNames);

        addUsersToApp(app, userIdTokens, roleNameTokens);

        restoreLock(app, origLockValue);
    }

    private void addUsersToApp(Application app, List<UserNameOrIdToken> userIdTokens, List<RoleNameOrIdToken> roleNameTokens) throws CommonFrameworkException {
        if (userIdTokens.size() == 0) {
            log.warn("Application " + app.getName() + " / " + app.getVersion() + ": addUsersToApp(): No users specified");
            return;
        }
        if (roleNameTokens.size() == 0) {
            log.warn("Application " + app.getName() + " / " + app.getVersion() + ": addUsersToApp(): No roles specified");
            return;
        }
        try {
            log.debug("SDK: Adding " + userIdTokens.size() + " users to application " + app.getName() + " / " + app.getVersion());
            ccApiWrapper
                    .getApplicationApi()
                    .addUserToApplicationTeam(app.getNameVersion(), userIdTokens,
                            roleNameTokens);
            log.debug("SDK: Done adding users to application");
        } catch (SdkFault e) {
            String msg = "Error adding users to application " +
                    app.getName() + " / " + app.getVersion() + ": " + e.getMessage();
            log.error(msg, e);
            throw new CommonFrameworkException(msg);
        }
    }

    private List<RoleNameOrIdToken> generateRoleTokens(Set<String> roleNames) {
        List<RoleNameOrIdToken> roleNameTokens = new ArrayList<>(roleNames.size());
        for (String roleName : roleNames) {
            RoleNameToken roleNameToken = new RoleNameToken();
            roleNameToken.setName(roleName);
            roleNameTokens.add(roleNameToken);
        }
        return roleNameTokens;
    }

    private List<UserNameOrIdToken> generateUserTokensFromUserIds(Set<String> userIds) {
        List<UserNameOrIdToken> userIdTokens = new ArrayList<>(userIds.size());
        for (String userId : userIds) {
            UserIdToken userIdToken = new UserIdToken();
            userIdToken.setId(userId);
            userIdTokens.add(userIdToken);
        }
        return userIdTokens;
    }

    private List<UserNameOrIdToken> generateUserTokensFromUserNames(Set<String> userNames) {
        List<UserNameOrIdToken> userIdTokens = new ArrayList<>(userNames.size());
        for (String userName : userNames) {
            UserNameToken userNameToken = getUserToken(userName);
            userIdTokens.add(userNameToken);
        }
        return userIdTokens;
    }

    private void restoreLock(Application app, boolean origLockValue) throws CommonFrameworkException {
        if (origLockValue) {
            // If app was locked and we got this far, circumventLock must be true
            try {
                lock(app, true); // lock
            } catch (SdkFault e) {
                throw new CommonFrameworkException("Error re-locking (after adjusting users) application " +
                        app.getName() + " / " + app.getVersion() + ": " + e.getMessage());
            }
        }
    }

    private boolean ensureUnlocked(boolean circumventLock, Application app) throws CommonFrameworkException {
        boolean origLockValue = app.isLocked();
        if (origLockValue) {
            if (!circumventLock) {
                throw new CommonFrameworkException("Error adjusting users on application " + app.getName() +
                        " / " + app.getVersion() + ": Application is locked");
            } else {
                try {
                    lock(app, false); // unlock
                } catch (SdkFault e) {
                    throw new CommonFrameworkException("Error unlocking (for adjusting users) application " +
                            app.getName() + " / " + app.getVersion() + ": " + e.getMessage());
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
     *            if true: if application is locked, unlock it, remove users, re-lock it
     * @throws CommonFrameworkException
     */
    @Override
    public void removeUserByIdFromApplicationTeam(String appId, String userId, String roleId,
            boolean circumventLock)
            throws CommonFrameworkException {

        Application app = getSdkApplicationByIdCached(appId);
        boolean origLockValue = ensureUnlocked(circumventLock, app);

        UserIdToken userIdToken = new UserIdToken();
        userIdToken.setId(userId);

        RoleIdToken roleIdToken = new RoleIdToken();
        roleIdToken.setId(roleId);

        try {
            log.debug("SDK: Removing user " + userId + " / role ID " + roleId + " from application " + app.getName() + " / " + app.getVersion());
            ccApiWrapper
                    .getApplicationApi()
                    .removeUserInApplicationTeam(app.getNameVersion(), userIdToken,
                            roleIdToken);
            log.debug("SDK: Done removing user from application");
        } catch (SdkFault e) {
            log.error("SDK: Error removing user from application");
            throw new CommonFrameworkException("Error removing user ID " + userId + " from application " +
                    app.getName() + " / " + app.getVersion() + ": " + e.getMessage());
        }
        restoreLock(app, origLockValue);
    }

    /**
     * Remove a given set of users (by username), all roles, from the given application.
     *
     * @param appId
     * @param usernames
     * @param circumventLock
     * @throws CommonFrameworkException
     */
    @Override
    public List<UserStatus> removeUsersByNameFromApplicationAllRoles(String appId, Set<String> usernames, boolean circumventLock)
            throws CommonFrameworkException {
        Application app = getSdkApplicationByIdCached(appId);
        ApplicationNameVersionToken appToken = getAppToken(app);
        boolean origLockValue = ensureUnlocked(circumventLock, app);

        log.debug("removeUsersByNameFromApplicationAllRoles(): App: " + app.getName() + " / " + app.getVersion() + ": Users: " + usernames);
        List<UserStatus> userDeletionStatus = new ArrayList<UserStatus>(
                usernames.size());
        if (usernames.size() == 0) {
            return userDeletionStatus;
        }

        List<ApplicationUserPojo> originalTeam = getAllUsersAssignedToApplication(app.getId().getId());
        for (String username : usernames) {
            log.info("Removing user: " + username + " from app "
                    + app.getName());

            String targetUserId = getUserId(username);
            UserNameToken userToken = getUserToken(username);

            for (ApplicationUserPojo originalTeamMemberRole : originalTeam) {

                boolean targetedUser = checkForTargetedUser(app, targetUserId, originalTeamMemberRole);
                if (!targetedUser) {
                    log.debug("This user (" + originalTeamMemberRole.getUserName() + ") is not the user we're looking for... skipping it");
                    continue;
                }

                RoleIdToken roleToken = getRoleToken(originalTeamMemberRole);
                try {
                    log.debug("SDK: Removing user " + username + " / role ID " + roleToken.getId() + " from application " + appToken.getName() + " / "
                            + appToken.getVersion());
                    ccApiWrapper
                            .getApplicationApi()
                            .removeUserInApplicationTeam(appToken, userToken,
                                    roleToken);
                    log.debug("SDK: Done removing user " + username);
                    userDeletionStatus
                            .add(new UserStatus(username, true, null));
                } catch (SdkFault e) {
                    String msg = "Error removing user " + username
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

    private RoleIdToken getRoleToken(ApplicationUserPojo originalTeamMemberRole) {
        RoleIdToken roleToken = new RoleIdToken();
        roleToken.setId(originalTeamMemberRole.getRoleId());
        return roleToken;
    }

    private UserNameToken getUserToken(String username) {
        UserNameToken userToken = new UserNameToken();
        userToken.setName(username);
        return userToken;
    }

    private ApplicationNameVersionToken getAppToken(Application app) {
        ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
        appToken.setName(app.getName());
        appToken.setVersion(app.getVersion());
        return appToken;
    }

    private String getUserId(String username) throws CommonFrameworkException {
        CodeCenterUserPojo targetUser = userMgr.getUserByName(username);
        String targetUserId = targetUser.getId();
        return targetUserId;
    }

    private boolean checkForTargetedUser(Application app, String targetUserId, ApplicationUserPojo originalTeamMember) {
        boolean targetedUser = false;
        log.debug("Checking user " + originalTeamMember.getUserName() + " ("
                + originalTeamMember.getUserId() + ") "
                + " role " + originalTeamMember.getRoleName()
                + " from app " + app.getName());

        if (originalTeamMember.getUserId()
                .equals(targetUserId)) {
            targetedUser = true;
        }
        return targetedUser;
    }

    @Override
    public List<AttachmentDetails> searchAttachments(String applicationId,
            String searchString) throws CommonFrameworkException {
        Application comp = getSdkApplicationByIdCached(applicationId);

        AttachmentPageFilter pageFilter = new AttachmentPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        List<ApplicationAttachment> sdkAttachments;
        try {
            log.debug("SDK: Searching for application attachment " + comp.getId());
            sdkAttachments = ccApiWrapper.getApplicationApi()
                    .searchApplicationAttachments("", pageFilter, comp.getId());
            log.debug("SDK: Done searching for application attachment");
        } catch (SdkFault e) {
            log.warn("SDK: Error searching for application attachment");
            throw new CommonFrameworkException(
                    "Error searching attachments for application ID "
                            + applicationId + ": " + e.getMessage());
        }

        List<AttachmentDetails> attachmentDetailsList = new ArrayList<>(
                sdkAttachments.size());
        for (ApplicationAttachment sdkAttachment : sdkAttachments) {
            AttachmentDetails attachmentDetails = createAttachmentDetails(sdkAttachment);
            attachmentDetailsList.add(attachmentDetails);
        }

        return attachmentDetailsList;
    }

    private AttachmentDetails createAttachmentDetails(
            ApplicationAttachment sdkAttachment) {
        AttachmentDetails attachmentDetails = new AttachmentDetails(
                sdkAttachment.getId(), sdkAttachment.getFileName(),
                sdkAttachment.getDescription(),
                sdkAttachment.getTimeUploaded(), sdkAttachment
                        .getUserUploaded().getId(),
                sdkAttachment.getContentType(),
                sdkAttachment.getFilesizeBytes());
        return attachmentDetails;
    }

    @Override
    public File downloadAttachment(String applicationId, String filename,
            String targetDirPath) throws CommonFrameworkException {
        ApplicationAttachmentToken attachmentToken = new ApplicationAttachmentToken();
        ApplicationIdToken compToken = new ApplicationIdToken();
        compToken.setId(applicationId);
        attachmentToken.setApplicationId(compToken);
        attachmentToken.setFileName(filename);
        AttachmentContent content;
        try {
            log.debug("SDK: Getting application attachment content for application ID " + applicationId + " filename " + filename);
            content = ccApiWrapper.getApplicationApi()
                    .getApplicationAttachmentContent(attachmentToken);
            log.debug("SDK: Done getting application attachment");
        } catch (SdkFault e) {
            log.error("SDK: Error getting application attachment");
            throw new CommonFrameworkException(
                    "Error getting data handler for application ID "
                            + applicationId + " attachment " + filename + ": "
                            + e.getMessage());
        }
        DataHandler dataHandler = content.getAttachmentContent();
        return AttachmentUtils.downloadAttachment("application", applicationId,
                filename, targetDirPath, dataHandler);
    }

    @Override
    public void attachFile(String applicationId, String sourceFilePath,
            String description) throws CommonFrameworkException {
        File file = new File(sourceFilePath);

        Application comp = getSdkApplicationByIdCached(applicationId);
        ApplicationAttachmentCreate attachmentCreateBean = new ApplicationAttachmentCreate();
        attachmentCreateBean.setApplicationId(comp.getId());
        attachmentCreateBean.setFileName(file.getName());
        attachmentCreateBean.setDescription(description);
        attachmentCreateBean.setName(file.getName());

        URL url = null;
        try {
            url = new File(sourceFilePath).toURI().toURL();
        } catch (MalformedURLException mue) {
            throw new CommonFrameworkException(mue.getMessage());
        }

        DataHandler dataHandler = new DataHandler(url);
        attachmentCreateBean.setAttachmentContent(dataHandler);
        try {
            log.debug("SDK: Creating application attachment on application " + applicationId + " sourceFilePath " + sourceFilePath);
            ccApiWrapper.getApplicationApi().createApplicationAttachment(
                    attachmentCreateBean);
            log.debug("SDK: Done creating application attachment");
        } catch (SdkFault e) {
            log.error("SDK: Error creating application attachment");
            throw new CommonFrameworkException(
                    "Error creating application attachment on application ID "
                            + applicationId + " from file " + url.toString()
                            + ": " + e.getMessage());
        }

    }

    @Override
    public void deleteAttachment(String applicationId, String filename)
            throws CommonFrameworkException {
        ApplicationIdToken applicationIdToken = new ApplicationIdToken();
        applicationIdToken.setId(applicationId);

        ApplicationAttachmentToken attachmentToken = new ApplicationAttachmentToken();
        attachmentToken.setApplicationId(applicationIdToken);
        attachmentToken.setFileName(filename);
        try {
            log.debug("SDK: Deleting application attachment on application " + applicationId + " / filename " + filename);
            ccApiWrapper.getApplicationApi().deleteApplicationAttachment(
                    attachmentToken);
            log.debug("SDK: Done deleting application attachment");
        } catch (SdkFault e) {
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
    public void updateAttributeValues(String appId, Set<AttributeValuePojo> changedAttrValues) throws CommonFrameworkException {

        log.info("updateAttributeValues() called with application ID: " + appId);
        Application app = getSdkApplicationByIdCached(appId);

        ApplicationUpdate applicationUpdate = new ApplicationUpdate();

        applicationUpdate.setId(app.getId());

        for (AttributeValuePojo attrValue : changedAttrValues) {

            String attrName = attrValue.getName();

            AttributeValue attrValueObject = new AttributeValue();
            AttributeIdToken attrIdToken = new AttributeIdToken();
            attrIdToken.setId(attrValue.getAttrId());
            attrValueObject.setAttributeId(attrIdToken);
            attrValueObject.getValues().add(attrValue.getValue());

            log.info("Setting attribute " + attrName + " to "
                    + attrValue.getValue());
            applicationUpdate.getAttributeValues().add(attrValueObject);
        }

        try {
            log.debug("SDK: Updating custom attribute values on application");
            ccApiWrapper.getApplicationApi().updateApplication(applicationUpdate);
            log.debug("SDK: Done updating custom attribute values on application");
        } catch (SdkFault e) {
            log.error("SDK: Error updating custom attribute values on application");
            throw new CommonFrameworkException("Error updating attribute values on application " + app.getName() + ": "
                    + e.getMessage());
        }
        applicationCache.removeApplication(app); // remove stale cache entry
    }

    private void lock(Application app, boolean lockValue) throws SdkFault {
        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(app.getId().getId());

        try {
            log.debug("SDK: Changing application " + app.getName() + " / " + app.getVersion() + " lock value to: " + lockValue);
            ccApiWrapper.getApplicationApi().lockApplication(appToken, lockValue);
            log.debug("SDK: Done changing lock");
        } catch (SdkFault e) {
            log.error("SDK: Error changing lcok on application");
            throw e;
        }
    }

}
