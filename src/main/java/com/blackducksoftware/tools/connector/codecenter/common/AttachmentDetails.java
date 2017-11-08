/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
