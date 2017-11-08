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
package com.blackducksoftware.tools.connector.codecenter.user;

/**
 * One User's status consisting of the username, a boolean indicating status
 * (true means OK), and a message that will describe the problem associated with
 * this user if there is one.
 *
 * @author sbillings
 *
 */
public class UserStatus {
    private final String username;
    private final boolean ok;
    private final String message;

    public UserStatus(String username, boolean ok, String message) {
	super();
	this.username = username;
	this.ok = ok;
	this.message = message;
    }

    public String getUsername() {
	return username;
    }

    public boolean isOk() {
	return ok;
    }

    public String getMessage() {
	if (message == null) {
	    return "";
	}
	return message;
    }
}
