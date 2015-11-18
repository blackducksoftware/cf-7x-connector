package com.blackducksoftware.tools.connector.codecenter.common;

public class NameVersion {
    private final String name;
    private final String version;

    public NameVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    protected String getName() {
        return name;
    }

    protected String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof NameVersion)) {
    	return false;
        }
        NameVersion otherNameVersion = (NameVersion) otherObj;
        if (getName().equals(otherNameVersion.getName())
    	    && getVersion().equals(otherNameVersion.getVersion())) {
    	return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getName().hashCode() << 1) + getVersion().hashCode();
    }

}