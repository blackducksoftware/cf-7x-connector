package com.blackducksoftware.tools.commonframework.standard.codecenter.license;


/**
 * License details.
 *
 * @author sbillings
 *
 */
public class LicensePojo {

    private final String id;
    private final String name;
    private final String licenseText;

    public LicensePojo(String id, String name, String licenseText) {
	this.id = id;
	this.name = name;
	this.licenseText = licenseText;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getLicenseText() {
	return licenseText;
    }

    @Override
    public String toString() {
	return "LicensePojo [id=" + id + ", name=" + name + "]";
    }
}
