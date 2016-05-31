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
import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.codecenter.user.UserStatus;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * Provides a higher level of abstraction for accessing Code Center
 * applications.
 *
 * The objects returned are POJOs, not SDK objects.
 *
 * @author sbillings
 *
 */
public interface IApplicationManager {

    /**
     * Get all applications the current user can see that (if search string
     * provided) match the given search string.
     *
     * @param searchString
     *            set to null for all applications
     * @return
     * @throws CommonFrameworkException
     */
    List<ApplicationPojo> getApplications(int firstRow, int lastRow,
	    String searchString) throws CommonFrameworkException;

    /**
     * Get a subset (or all) of the applications that the user can access.
     *
     * @param firstRow
     *            get rows starting at this index (first = 0)
     * @param lastRow
     *            get rows ending at this index (use Integer.MAX_VALUE for all)
     * @return
     * @throws CommonFrameworkException
     */
    List<ApplicationPojo> getApplications(int firstRow, int lastRow)
	    throws CommonFrameworkException;

    /**
     * Get all applications.
     *
     * @return
     * @throws CommonFrameworkException
     */
    List<ApplicationPojo> getAllApplications() throws CommonFrameworkException;

    /**
     * Get all applications, building the full list by retrieving subsets of
     * size chunksize.
     *
     * @param chunkSize
     * @return
     * @throws CommonFrameworkException
     */
    List<ApplicationPojo> getAllApplications(int chunkSize)
	    throws CommonFrameworkException;

    /**
     * Get an application by name/version.
     *
     * @param name
     * @param version
     * @return
     * @throws CommonFrameworkException
     */
    ApplicationPojo getApplicationByNameVersion(String name, String version)
	    throws CommonFrameworkException;

    /**
     * Get an application by ID.
     *
     * @param id
     * @return
     * @throws CommonFrameworkException
     */
    ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException;

    /**
     * Get an application's requests, by application ID.
     *
     * @param appId
     * @return
     * @throws CommonFrameworkException
     */
    List<RequestPojo> getRequestsByAppId(String appId)
	    throws CommonFrameworkException;

    /**
     * Get an application's components, optionally recursively, by application
     * ID, limited to components with an approval status that is in the provided
     * list.
     *
     * If recursive: For each component (at any level in the tree) that is an
     * application published as a component, returns the approved components
     * pointed to the published application's requests instead of the published
     * application itself.
     *
     * @param appId
     * @param limitToApprovalStatusValues
     *            If not null and not empty, components included in the return
     *            value are limited to those with an ApprovalStatus that appears
     *            in the given list of ApprovalStatus values.
     * @param recursive
     * @return
     * @throws CommonFrameworkException
     */
    <T extends CodeCenterComponentPojo> List<T> getComponentsByAppId(
	    Class<T> pojoClass, String appId,
	    List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
	    throws CommonFrameworkException;

    /**
     * Gets all User's that are assigned to the specified Application.
     *
     * @param appId
     *            String
     * @return List<(ApplicationUserPojo)>
     * @throws CommonFrameworkException
     */
    List<ApplicationUserPojo> getAllUsersAssignedToApplication(String appId)
	    throws CommonFrameworkException;

    /**
     * Add the given set of users (user IDs) to the given application, each with
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
    void addUsersByIdToApplicationTeam(String appId, Set<String> userIds,
	    Set<String> roleNames, boolean circumventLock)
	    throws CommonFrameworkException;

    /**
     * Add the given set of users (usernames) to the given application, each
     * with the given roles.
     *
     * @param appId
     *            Application ID
     * @param userNames
     *            User names
     * @param roleNames
     *            Role names
     * @param circumventLock
     *            if true: if application is locked, unlock it, add users,
     *            re-lock it
     * @throws CommonFrameworkException
     */
    void addUsersByNameToApplicationTeam(String appId, Set<String> userNames,
	    Set<String> roleNames, boolean circumventLock)
	    throws CommonFrameworkException;

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
    void removeUserByIdFromApplicationTeam(String appId, String userId,
	    String roleId, boolean circumventLock)
	    throws CommonFrameworkException;

    /**
     * Remove a given set of users (by username), all roles, from the given
     * application.
     *
     * @param appId
     * @param usernames
     * @param circumventLock
     * @throws CommonFrameworkException
     */
    List<UserStatus> removeUsersByNameFromApplicationAllRoles(String appId,
	    Set<String> usernames, boolean circumventLock)
	    throws CommonFrameworkException;

    /**
     * Get attachment details for a given set of attachments from the given
     * Application.
     *
     * @param applicationId
     * @param searchString
     * @return
     * @throws CommonFrameworkException
     */
    List<AttachmentDetails> searchAttachments(String applicationId,
	    String searchString) throws CommonFrameworkException;

    /**
     * Download an Application attachment to the given directory.
     *
     * @param attachmentId
     * @param targetDirPath
     * @return
     * @throws CommonFrameworkException
     */
    File downloadAttachment(String applicationId, String filename,
	    String targetDirPath) throws CommonFrameworkException;

    /**
     * Attach the named file to the given Application.
     *
     * @param applicationId
     * @param sourceFilePath
     * @param description
     * @throws CommonFrameworkException
     */
    void attachFile(String applicationId, String sourceFilePath,
	    String description) throws CommonFrameworkException;

    /**
     * Delete the given attachment from the given application.
     *
     * @param attachmentId
     */
    void deleteAttachment(String applicationId, String filename)
	    throws CommonFrameworkException;

    /**
     * Update some attribute values on the given application.
     *
     * @param appId
     * @param changedAttrValues
     * @throws CommonFrameworkException
     */
    void updateAttributeValues(String appId,
	    Set<AttributeValuePojo> changedAttrValues)
	    throws CommonFrameworkException;

    /**
     * Remove the application with the given ID (and its requests) from the
     * cache (if it is there).
     * 
     * @param id
     * @throws CommonFrameworkException
     */
    void removeApplicationFromCacheById(String id)
	    throws CommonFrameworkException;

    /**
     * Remove the application with the given name/version (and its requests)
     * from the cache (if it is there).
     * 
     * @param name
     * @param version
     * @throws CommonFrameworkException
     */
    void removeApplicationFromCacheByNameVersion(String name, String version)
	    throws CommonFrameworkException;
}
