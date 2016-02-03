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
package com.blackducksoftware.tools.connector.codecenter.protexservers;

import com.blackducksoftware.tools.commonframework.standard.protex.ProtexProjectPojo;
import com.blackducksoftware.tools.connector.protex.IProtexServerWrapper;

/**
 * A Protex server name, url, and ProtexServerWrapper.
 * 
 * @author sbillings
 * 
 */
public class NamedProtexServer {
    private final String name;

    private String url;

    private final IProtexServerWrapper<ProtexProjectPojo> psw;

    public NamedProtexServer(String name, String url,
            IProtexServerWrapper<ProtexProjectPojo> psw) {
        this.name = name;
        this.url = url;
        this.psw = psw;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public IProtexServerWrapper<ProtexProjectPojo> getProtexServerWrapper() {
        return psw;
    }

}
