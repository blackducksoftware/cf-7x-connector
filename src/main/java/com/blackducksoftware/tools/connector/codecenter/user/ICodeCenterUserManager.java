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
