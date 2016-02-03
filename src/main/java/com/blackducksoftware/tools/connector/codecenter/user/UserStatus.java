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
