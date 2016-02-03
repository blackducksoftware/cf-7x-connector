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
