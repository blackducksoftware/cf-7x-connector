package com.blackducksoftware.tools.connector.protex.common;

/**
 * When used in the context of a BOM, the license conflict status will be NONE,
 * LICENSE, OR DECLARED.
 *
 * When used without the context of a BOM, license conflict status will be
 * UNKNOWN.
 *
 * @author sbillings
 *
 */
public enum LicenseConflictStatus {
    UNKNOWN("Unknown"), NONE("No License Conflicts"), LICENSE(
	    "Conflicts with another component"), DECLARED(
	    "Conflicts with the project's declared license"), BOTH(
	    "Conflicts with another component and the project's declared license");

    private final String description;

    private LicenseConflictStatus(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return description;
    }
}
