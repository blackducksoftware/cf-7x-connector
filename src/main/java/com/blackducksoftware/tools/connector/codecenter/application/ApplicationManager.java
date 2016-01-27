package com.blackducksoftware.tools.connector.codecenter.application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.ApplicationApi;
import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachment;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachmentCreate;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationAttachmentToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationPageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentContent;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentPageFilter;
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
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentUtils;
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

    private final Map<NameVersion, Application> appsByNameVersionCache = new HashMap<>();

    private final Map<String, Application> appsByIdCache = new HashMap<>();

    private final Map<String, List<RequestSummary>> requestListsByAppIdCache = new HashMap<>();

    private final ICodeCenterUserManager userMgr;

    public ApplicationManager(CodeCenterAPIWrapper ccApiWrapper,
            IAttributeDefinitionManager attrDefMgr,
            ICodeCenterComponentManager compMgr,
            ICodeCenterUserManager userMgr) {
        this.ccApiWrapper = ccApiWrapper;
        this.attrDefMgr = attrDefMgr;
        this.compMgr = compMgr;
        this.userMgr = userMgr;
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
            ccApps = ccApiWrapper.getApplicationApi().searchApplications(searchString,
                    pageFilter);
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
        if (appsByNameVersionCache.containsKey(nameVersion)) {
            Application app = appsByNameVersionCache.get(nameVersion);
            ApplicationPojo appPojo = toPojo(app);
            return appPojo;
        }
        ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
        appToken.setName(name);
        appToken.setVersion(version);
        Application app = null;
        try {
            app = ccApiWrapper.getApplicationApi().getApplication(appToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error getting application "
                    + name + " / " + version + ": " + e.getMessage());
        }
        addAppToCache(nameVersion, app);

        return toPojo(app);
    }

    private ApplicationPojo toPojo(Application app)
            throws CommonFrameworkException {
        ApplicationPojo appPojo = new ApplicationPojo(app.getId().getId(),
                app.getName(), app.getVersion(), AttributeValues.valueOf(
                        attrDefMgr, app.getAttributeValues()),
                ApprovalStatus.valueOf(app.getApprovalStatus()));
        return appPojo;
    }

    private void addAppToCache(NameVersion nameVersion, Application app) {
        appsByNameVersionCache.put(nameVersion, app);
        appsByIdCache.put(app.getId().getId(), app);
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
        if (appsByIdCache.containsKey(applicationId)) {
            Application app = appsByIdCache.get(applicationId);
            return app;
        }
        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(applicationId);
        Application app = null;
        try {
            app = ccApiWrapper.getApplicationApi().getApplication(appToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error getting application with ID " + applicationId + ": "
                            + e.getMessage());
        }
        NameVersion nameVersion = new NameVersion(app.getName(),
                app.getVersion());
        addAppToCache(nameVersion, app);
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
        if (requestListsByAppIdCache.containsKey(appId)) {
            return createRequestPojoList(appId,
                    requestListsByAppIdCache.get(appId));
        }

        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(appId);

        RequestPageFilter pageFilter = new RequestPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        List<RequestSummary> requestSummaries;
        try {
            requestSummaries = ccApiWrapper.getApplicationApi()
                    .searchApplicationRequests(appToken, null, pageFilter);
        } catch (SdkFault e) {
            throw new CommonFrameworkException(
                    "Error fetching requests for application ID " + appId
                            + ": " + e.getMessage());
        }

        // Cache the request list for this appId
        requestListsByAppIdCache.put(appId, requestSummaries);

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
            roleAssignments = ccApiWrapper.getApplicationApi()
                    .searchUserInApplicationTeam(appToken, "", userFilter);
        } catch (SdkFault e) {
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
            ccApiWrapper
                    .getApplicationApi()
                    .addUserToApplicationTeam(app.getNameVersion(), userIdTokens,
                            roleNameTokens);
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
            UserNameToken userNameToken = new UserNameToken();
            userNameToken.setName(userName);
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
            ccApiWrapper
                    .getApplicationApi()
                    .removeUserInApplicationTeam(app.getNameVersion(), userIdToken,
                            roleIdToken);
        } catch (SdkFault e) {
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
        boolean origLockValue = ensureUnlocked(circumventLock, app);

        log.debug("removeUsersByNameFromApplicationAllRoles()");
        // dumpUserNameIdMap();
        List<UserStatus> userDeletionStatus = new ArrayList<UserStatus>(
                usernames.size());
        if (usernames.size() == 0) {
            return userDeletionStatus;
        }

        List<ApplicationUserPojo> originalTeam = getAllUsersAssignedToApplication(app.getId().getId());
        for (String username : usernames) {
            log.info("Removing user: " + username + " from app "
                    + app.getName());
            // updateUserNameIdMap(username);
            CodeCenterUserPojo targetUser = userMgr.getUserByName(username);
            String targetUserId = targetUser.getId();

            ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
            appToken.setName(app.getName());
            appToken.setVersion(app.getVersion());

            UserNameToken userToken = new UserNameToken();
            userToken.setName(username);

            // Get username's roles on this application
            // UserRolePageFilter filter = new UserRolePageFilter();
            // filter.setFirstRowIndex(0);
            // filter.setLastRowIndex(Integer.MAX_VALUE);
            // log.debug("Getting role assignments for user " + username
            // + " in application " + app.getName());
            // List<ApplicationRoleAssignment> roleAssignments;
            // try {
            // roleAssignments = ccApiWrapper.getApplicationApi()
            // .searchUserInApplicationTeam(app.getId(), username, filter);
            // } catch (SdkFault e1) {
            // throw new CommonFrameworkException("Error searching for user " +
            // username + " in team for application: " + app.getName() + " / " + app.getVersion());
            // }

            // log.debug("Found " + assignedUsers.size()
            // + " role assignments for user " + username);

            for (ApplicationUserPojo roleToCheckAndMaybeRemove : originalTeam) {
                try {
                    log.debug("Checking user " + roleToCheckAndMaybeRemove.getUserName() + " ("
                            + roleToCheckAndMaybeRemove.getUserId() + ") "
                            + " role " + roleToCheckAndMaybeRemove.getRoleName()
                            + " from app " + app.getName());

                    // searchUserInApplicationTeam() returns a superset of
                    // users, such as
                    // user1, user10, and user100 when we search for user1.

                    if (!roleToCheckAndMaybeRemove.getUserId()
                            .equals(targetUserId)) {
                        log.debug("This user (" + roleToCheckAndMaybeRemove.getUserName() + ") is not the user we're looking for... skipping it");
                        continue;
                    }

                    RoleIdToken roleToken = new RoleIdToken();
                    roleToken.setId(roleToCheckAndMaybeRemove.getRoleId());
                    ccApiWrapper
                            .getApplicationApi()
                            .removeUserInApplicationTeam(appToken, userToken,
                                    roleToken);
                    log.debug("Removal of user " + username
                            + " was successful");
                    userDeletionStatus
                            .add(new UserStatus(username, true, null));
                } catch (SdkFault e) {
                    String msg = "Error removing user " + username
                            + " with role "
                            + roleToCheckAndMaybeRemove.getRoleName()
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

    @Override
    public List<AttachmentDetails> searchAttachments(String applicationId,
            String searchString) throws CommonFrameworkException {
        Application comp = getSdkApplicationByIdCached(applicationId);

        AttachmentPageFilter pageFilter = new AttachmentPageFilter();
        pageFilter.setFirstRowIndex(0);
        pageFilter.setLastRowIndex(Integer.MAX_VALUE);
        List<ApplicationAttachment> sdkAttachments;
        try {
            sdkAttachments = ccApiWrapper.getApplicationApi()
                    .searchApplicationAttachments("", pageFilter, comp.getId());
        } catch (SdkFault e) {
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
            content = ccApiWrapper.getApplicationApi()
                    .getApplicationAttachmentContent(attachmentToken);
        } catch (SdkFault e) {
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

            ccApiWrapper.getApplicationApi().createApplicationAttachment(
                    attachmentCreateBean);
        } catch (SdkFault e) {
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
            ccApiWrapper.getApplicationApi().deleteApplicationAttachment(
                    attachmentToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error deleting file "
                    + filename + " from application ID " + applicationId + ": "
                    + e.getMessage());
        }

    }

    private void lock(Application app, boolean lockValue) throws SdkFault {
        ApplicationApi applicationApi = ccApiWrapper.getApplicationApi();
        ApplicationIdToken appToken = new ApplicationIdToken();
        appToken.setId(app.getId().getId());

        applicationApi.lockApplication(appToken, lockValue);
    }

}
