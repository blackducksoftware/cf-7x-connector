package com.blackducksoftware.tools.connector.codecenter.user;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationRolePojo;

public interface ICodeCenterUserManager {

    String createUser(String username, String password, String firstName, String lastName, String email, boolean active) throws CommonFrameworkException;

    CodeCenterUserPojo getUserById(String userId) throws CommonFrameworkException;

    CodeCenterUserPojo getUserByName(String userName) throws CommonFrameworkException;

    void deleteUserById(String userId) throws CommonFrameworkException;

    void setUserActiveStatus(String userId, boolean active) throws CommonFrameworkException;

    List<ApplicationRolePojo> getApplicationRolesByUserName(String userName) throws CommonFrameworkException;
}
