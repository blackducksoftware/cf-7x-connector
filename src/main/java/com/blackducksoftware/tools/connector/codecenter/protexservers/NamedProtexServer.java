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
