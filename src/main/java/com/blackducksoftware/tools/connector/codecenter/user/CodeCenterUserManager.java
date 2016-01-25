package com.blackducksoftware.tools.connector.codecenter.user;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.user.data.User;
import com.blackducksoftware.sdk.codecenter.user.data.UserCreate;
import com.blackducksoftware.sdk.codecenter.user.data.UserIdToken;
import com.blackducksoftware.sdk.codecenter.user.data.UserNameToken;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;

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
        if (usersByIdCache.containsKey(userId)) {
            User sdkUser = usersByIdCache.get(userId);
            usersByIdCache.remove(userId);
            usersByNameCache.remove(sdkUser.getName().getName());
        }
        UserIdToken userIdToken = new UserIdToken();
        userIdToken.setId(userId);
        try {
            ccApiWrapper.getUserApi().deleteUser(userIdToken);
        } catch (SdkFault e) {
            throw new CommonFrameworkException("Error deleting user with ID " + userId + ": " + e.getMessage());
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

}
