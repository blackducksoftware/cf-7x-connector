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

import java.util.Date;

public class AttachmentDetails {
    private final String id;
    private final String fileName;
    private final String description;
    private final Date timeUploaded;
    private final String userUploaded;
    private final String contentType;
    private final long fileSizeBytes;

    public AttachmentDetails(String id, String fileName, String description,
	    Date timeUploaded, String userUploaded, String contentType,
	    long fileSizeBytes) {
	this.id = id;
	this.fileName = fileName;
	this.description = description;
	this.timeUploaded = timeUploaded;
	this.userUploaded = userUploaded;
	this.contentType = contentType;
	this.fileSizeBytes = fileSizeBytes;
    }

    public String getId() {
	return id;
    }

    public String getFileName() {
	return fileName;
    }

    public String getDescription() {
	return description;
    }

    public Date getTimeUploaded() {
	return timeUploaded;
    }

    public String getUserUploaded() {
	return userUploaded;
    }

    public String getContentType() {
	return contentType;
    }

    public long getFileSizeBytes() {
	return fileSizeBytes;
    }

    @Override
    public String toString() {
	return "AttachmentDetails [fileName=" + fileName + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	AttachmentDetails other = (AttachmentDetails) obj;
	if (id == null) {
	    if (other.id != null) {
		return false;
	    }
	} else if (!id.equals(other.id)) {
	    return false;
	}
	return true;
    }

}
