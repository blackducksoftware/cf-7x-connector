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
package com.blackducksoftware.tools.connector.codecenter.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.apache.cxf.helpers.IOUtils;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public class AttachmentUtils {
    /**
     * Download an attachment from the given object using the attachment's
     * DataHandler.
     *
     * @param objectType
     * @param objectId
     * @param filename
     * @param targetDirPath
     * @param dataHandler
     * @return
     * @throws CommonFrameworkException
     */
    public static File downloadAttachment(String objectType, String objectId,
	    String filename, String targetDirPath, DataHandler dataHandler)
	    throws CommonFrameworkException {
	InputStream attachmentData = null;
	OutputStream fileOutputStream = null;
	File outputFile = null;
	try {

	    try {
		attachmentData = dataHandler.getInputStream();
	    } catch (IOException e) {
		throw new CommonFrameworkException(
			"Error getting input stream for attachment " + filename
				+ " on " + objectType + "ID " + objectId + ": "
				+ e.getMessage());
	    }

	    outputFile = new File(targetDirPath + File.separator + filename);

	    try {
		fileOutputStream = new FileOutputStream(outputFile);
	    } catch (FileNotFoundException e) {
		throw new CommonFrameworkException(
			"Error getting output stream for attachment "
				+ filename + " on " + objectType + "ID "
				+ objectId + ": " + e.getMessage());
	    }

	    try {
		IOUtils.copy(attachmentData, fileOutputStream);
	    } catch (IOException e) {
		throw new CommonFrameworkException(
			"Error downloading data for attachment " + filename
				+ " on " + objectType + "ID " + objectId + ": "
				+ e.getMessage());
	    }

	} finally {
	    if (attachmentData != null) {
		try {
		    attachmentData.close();
		} catch (IOException e) {
		}
	    }
	    if (fileOutputStream != null) {
		try {
		    fileOutputStream.close();
		} catch (IOException e) {
		}
	    }
	}

	return outputFile;
    }
}
