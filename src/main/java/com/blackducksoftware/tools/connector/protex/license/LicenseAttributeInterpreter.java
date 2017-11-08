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
package com.blackducksoftware.tools.connector.protex.license;

import com.blackducksoftware.sdk.protex.license.LicenseAttributes;
import com.blackducksoftware.sdk.protex.license.LicenseExtensionLevel;
import com.blackducksoftware.sdk.protex.license.PermittedOrRequired;
import com.blackducksoftware.sdk.protex.license.RestrictionType;
import com.blackducksoftware.sdk.protex.license.RightToDistributeBinaryForMaximumUsage;

/**
 * Provides the description and text representation of a license.
 *
 * @author sbillings
 *
 */
public enum LicenseAttributeInterpreter {

    /** The right to distribute binary. */
    RIGHT_TO_DISTRIBUTE_BINARY("Right to Distribute Binary"),

    /** The carries distribution obligations. */
    CARRIES_DISTRIBUTION_OBLIGATIONS("Carries Distribution Obligations"),

    /** The source code distribution. */
    SOURCE_CODE_DISTRIBUTION("Source Code Distribution"),

    /** The right to copy. */
    RIGHT_TO_COPY("Right to Copy"),

    /** The right to modify. */
    RIGHT_TO_MODIFY("Right to Modify"),

    /** The right to reverse engineer. */
    RIGHT_TO_REVERSE_ENGINEER("Right to Reverse Engineer"),

    /** The discriminatory restrictions. */
    DISCRIMINATORY_RESTRICTIONS("Discriminatory Restrictions"),

    /** The charging fees. */
    CHARGING_FEES("Charging Fees"),

    /** The patent retaliation. */
    PATENT_RETALIATION("Patent Retaliation"),

    /** The express patent license. */
    EXPRESS_PATENT_LICENSE("Express Patent License"),

    /** The anti drm provision. */
    ANTI_DRM_PROVISION("Anti DRM Provision"),

    /** The notice required. */
    NOTICE_REQUIRED("Notice Required"),

    /** The change notice. */
    CHANGE_NOTICE("Change Notice"),

    /** The license back. */
    LICENSE_BACK("License Back"),

    /** The warranty disclaimer. */
    WARRANTY_DISCLAIMER("Warranty Disclaimer"),

    /** The limitation of liability. */
    LIMITATION_OF_LIABILITY("Limitation of Liability"),

    /** The indemnification obligation. */
    INDEMNIFICATION_OBLIGATION("Indemnification Obligation"),

    /** The include license. */
    INCLUDE_LICENSE("Include License"),

    /** The promotion restriction. */
    PROMOTION_RESTRICTION("Promotion Restriction"),

    /** The reciprocity. */
    RECIPROCITY("Reciprocity"),

    /** The integration level. */
    INTEGRATION_LEVEL("Integration Level");

    /** The description. */
    private String description;

    /**
     * Instantiates a new license attribute interpreter.
     *
     * @param description
     *            the description
     */
    private LicenseAttributeInterpreter(String description) {
	this.description = description;
    }

    /**
     * Get the description of a license.
     *
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Get a text representation of the value of a license attribute.
     *
     * @param licAttrs
     *            the lic attrs
     * @return the text value
     */
    public String getTextValue(LicenseAttributes licAttrs) {
	String textValue = "<unknown>";

	if (name().equals("RIGHT_TO_DISTRIBUTE_BINARY")) {
	    textValue = getRightToDistributeBinaryForMaximumUsageAttributeText(licAttrs);
	} else if (name().equals("CARRIES_DISTRIBUTION_OBLIGATIONS")) {
	    textValue = licAttrs.isCarriesDistributionObligations() ? "T" : "F";
	} else if (name().equals("SOURCE_CODE_DISTRIBUTION")) {
	    textValue = getPermOrReqAttributeText(licAttrs
		    .getSourceCodeDistribution());
	} else if (name().equals("RIGHT_TO_COPY")) {
	    textValue = getPermOrReqAttributeText(licAttrs
		    .getGrantRecipientRightToCopy());
	} else if (name().equals("RIGHT_TO_MODIFY")) {
	    textValue = getPermOrReqAttributeText(licAttrs
		    .getGrantRecipientRightToModify());
	} else if (name().equals("RIGHT_TO_REVERSE_ENGINEER")) {
	    textValue = getPermOrReqAttributeText(licAttrs
		    .getGrantRecipientRightToReverseEngineer());
	} else if (name().equals("DISCRIMINATORY_RESTRICTIONS")) {
	    textValue = getRestrictionTypeAttributeText(licAttrs
		    .getDiscriminatoryRestrictions());
	} else if (name().equals("CHARGING_FEES")) {
	    textValue = getPermOrReqAttributeText(licAttrs.getChargingFees());
	} else if (name().equals("PATENT_RETALIATION")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isPatentRetaliation());
	} else if (name().equals("EXPRESS_PATENT_LICENSE")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isExpressPatentLicense());
	} else if (name().equals("ANTI_DRM_PROVISION")) {
	    textValue = getTrueFalseAttributeText(licAttrs.isAntiDrmProvision());
	} else if (name().equals("NOTICE_REQUIRED")) {
	    textValue = getTrueFalseAttributeText(licAttrs.isNotice());
	} else if (name().equals("CHANGE_NOTICE")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isChangeNoticeRequired());
	} else if (name().equals("LICENSE_BACK")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isLicenseBackRequired());
	} else if (name().equals("WARRANTY_DISCLAIMER")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isWarrantyDisclaimerRequired());
	} else if (name().equals("LIMITATION_OF_LIABILITY")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isLimitationOfLiabilityRequired());
	} else if (name().equals("INDEMNIFICATION_OBLIGATION")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isIndemnificationRequired());
	} else if (name().equals("INCLUDE_LICENSE")) {
	    textValue = getTrueFalseAttributeText(licAttrs.isIncludeLicense());
	} else if (name().equals("PROMOTION_RESTRICTION")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isPromotionRestriction());
	} else if (name().equals("RECIPROCITY")) {
	    textValue = getTrueFalseAttributeText(licAttrs
		    .isShareAlikeReciprocity());
	} else if (name().equals("INTEGRATION_LEVEL")) {
	    textValue = getLicenseExtensionLevelText(licAttrs
		    .getIntegrationLevelForLicenseApplication());
	}
	return textValue;
    }

    /**
     * Gets the right to distribute binary for maximum usage attribute text.
     *
     * @param licAttrs
     *            the lic attrs
     * @return the right to distribute binary for maximum usage attribute text
     */
    private String getRightToDistributeBinaryForMaximumUsageAttributeText(
	    LicenseAttributes licAttrs) {

	RightToDistributeBinaryForMaximumUsage attr = licAttrs
		.getRightToDistributeBinaryForMaximumUsage();
	if (attr == RightToDistributeBinaryForMaximumUsage.ANY) {
	    return "ANY";
	} else if (attr == RightToDistributeBinaryForMaximumUsage.INTERNAL_EVALUATION) {
	    return "INTERNAL EVALUATION";
	} else if (attr == RightToDistributeBinaryForMaximumUsage.INTERNAL_PRODUCTION_USE) {
	    return "INTERNAL PRODUCTION USE";
	} else if (attr == RightToDistributeBinaryForMaximumUsage.NON_COMMERCIAL_OR_PERSONAL_USE) {
	    return "NON-COMMERCIAL OR PERSONAL USE";
	} else { // unrecognized
	    return "UNKNOWN_VALUE";
	}
    }

    /**
     * Gets the perm or req attribute text.
     *
     * @param permOrReq
     *            the perm or req
     * @return the perm or req attribute text
     */
    private String getPermOrReqAttributeText(PermittedOrRequired permOrReq) {
	if (permOrReq == PermittedOrRequired.NOT_PERMITTED) {
	    return "NOT_PERMITED";
	} else if (permOrReq == PermittedOrRequired.PERMITTED) {
	    return "PERMITED";
	} else if (permOrReq == PermittedOrRequired.REQUIRED) {
	    return "REQUIRED";
	} else {
	    return "UNKNOWN_VALUE";
	}
    }

    /**
     * Gets the restriction type attribute text.
     *
     * @param restrictionType
     *            the restriction type
     * @return the restriction type attribute text
     */
    private String getRestrictionTypeAttributeText(
	    RestrictionType restrictionType) {
	if (restrictionType == RestrictionType.HAS_NO_RESTRICTIONS) {
	    return "HAS_NO_RESTRICTIONS";
	} else if (restrictionType == RestrictionType.HAS_NO_RESTRICTIONS_AND_CAN_NOT_ADD_ANY) {
	    return "HAS_NO_RESTRICTIONS_AND_CAN_NOT_ADD_ANY";
	} else if (restrictionType == RestrictionType.HAS_RESTRICTIONS) {
	    return "HAS_RESTRICTIONS";
	} else {
	    return "UNKNOWN_VALUE";
	}
    }

    /**
     * Gets the true false attribute text.
     *
     * @param attributeValue
     *            the attribute value
     * @return the true false attribute text
     */
    private String getTrueFalseAttributeText(boolean attributeValue) {
	return attributeValue ? "T" : "F";
    }

    /**
     * Gets the license extension level text.
     *
     * @param licenseExtensionLevel
     *            the license extension level
     * @return the license extension level text
     */
    private String getLicenseExtensionLevelText(
	    LicenseExtensionLevel licenseExtensionLevel) {
	if (licenseExtensionLevel == LicenseExtensionLevel.ACCOMPANYING_SOFTWARE_USING_PER_SLEEPY_CAT) {
	    return "ACCOMPANYING_SOFTWARE_USING_PER_SLEEPY_CAT";
	} else if (licenseExtensionLevel == LicenseExtensionLevel.DYNAMIC_LIBRARY_PER_LGPL) {
	    return "DYNAMIC_LIBRARY_PER_LGPL";
	} else if (licenseExtensionLevel == LicenseExtensionLevel.FILE_PER_MPL) {
	    return "FILE_PER_MPL";
	} else if (licenseExtensionLevel == LicenseExtensionLevel.NON) {
	    return "NON";
	} else if (licenseExtensionLevel == LicenseExtensionLevel.WORK_BASED_ON_PER_GPL) {
	    return "WORK_BASED_ON_PER_GPL";
	} else if (licenseExtensionLevel == LicenseExtensionLevel.MODULE_PER_EPL_CPL) {
	    return "MODULE_PER_EPL_CPL";
	} else {
	    return "UNKNOWN_VALUE";
	}
    }
}
