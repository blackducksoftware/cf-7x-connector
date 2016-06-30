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
package com.blackducksoftware.tools.connector.codecenter.component;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

public interface ICodeCenterComponentManager {

	/**
	 * Get a component by its component ID (not kbComponentId), creating a POJO
	 * of the given class.
	 *
	 * @param pojoClass
	 * @param componentId
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> T getComponentById(Class<T> pojoClass,
			String componentId) throws CommonFrameworkException;

	/**
	 * Get a component by its component ID (not kbComponentId), creating a POJO
	 * of the given class with the given license.
	 *
	 * @param pojoClass
	 * @param componentId
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> T getComponentById(Class<T> pojoClass,
			String componentId, String licenseId) throws CommonFrameworkException;

	/**
	 * Get a component by its name/version.
	 *
	 * @param componentName
	 * @param componentVersion
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> T getComponentByNameVersion(
			Class<T> pojoClass, String componentName, String componentVersion)
					throws CommonFrameworkException;

	/**
	 * Get a subset of the component catalog.
	 *
	 * @param firstRowIndex
	 *            first row in catalog = 0
	 * @param lastRowIndex
	 *            to get all rows, pass Integer.MAX_VALUE
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> List<T> getComponents(
			Class<T> pojoClass, int firstRowIndex, int lastRowIndex)
					throws CommonFrameworkException;

	/**
	 * Get the list of components named in a list of requests.
	 *
	 * @param requests
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> List<T> getComponentsForRequests(
			Class<T> pojoClass, List<RequestPojo> requests)
					throws CommonFrameworkException;

	/**
	 * Get the list of components named in a list of requests, limited to those
	 * with one of the ApprovalStatus values in the provided list.
	 *
	 * @param requests
	 * @param limitToApprovalStatusValues
	 *            If not null and not empty, components included in the return
	 *            value are limited to those with an ApprovalStatus that appears
	 *            in the given list of ApprovalStatus values.
	 * @return
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> List<T> getComponentsForRequests(
			Class<T> pojoClass, List<RequestPojo> requests,
			List<ApprovalStatus> limitToApprovalStatusValues)
					throws CommonFrameworkException;

	<T extends CodeCenterComponentPojo> T instantiatePojo(Class<T> pojoClass)
			throws CommonFrameworkException;

	/**
	 * Get attachment details for a given set of attachments from the given
	 * Component.
	 *
	 * @param componentId
	 * @param searchString
	 * @return
	 * @throws CommonFrameworkException
	 */
	List<AttachmentDetails> searchAttachments(String componentId,
			String searchString) throws CommonFrameworkException;

	/**
	 * Download a Component attachment to the given directory.
	 *
	 * @param attachmentId
	 * @param targetDirPath
	 * @return
	 * @throws CommonFrameworkException
	 */
	File downloadAttachment(String componentId, String filename,
			String targetDirPath) throws CommonFrameworkException;

	/**
	 * Attach the named file to the given Component.
	 *
	 * @param componentId
	 * @param sourceFilePath
	 * @param description
	 * @throws CommonFrameworkException
	 */
	void attachFile(String componentId, String sourceFilePath,
			String description) throws CommonFrameworkException;

	/**
	 * Delete the given attachment from the given component.
	 *
	 * @param attachmentId
	 */
	void deleteAttachment(String componentId, String filename)
			throws CommonFrameworkException;

	/**
	 * Update the attribute values on an application.
	 *
	 * @param app
	 *            The application to be updated, with the new attribute values.
	 * @throws CommonFrameworkException
	 */
	<T extends CodeCenterComponentPojo> void updateAttributeValues(Class<T> pojoClass, String compId, Set<AttributeValuePojo> changedAttrValues)
			throws CommonFrameworkException;

	/**
	 * Read all catalog components into the component cache for faster access
	 * later.
	 *
	 * @param batchSize
	 *            The number to read per SDK request.
	 * @throws CommonFrameworkException
	 */
	void populateComponentCacheFromCatalog(int batchSize) throws CommonFrameworkException;

}
