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
package com.blackducksoftware.tools.connector.protex.report;

import com.blackducksoftware.sdk.protex.report.ReportSectionType;

public enum ReportSectionSelection {
    ANALYSIS_SUMMARY(ReportSectionType.ANALYSIS_SUMMARY),
    ANALYSIS_WARNINGS_AND_ERRORS(ReportSectionType.ANALYSIS_WARNINGS_AND_ERRORS),
    BILL_OF_MATERIALS(ReportSectionType.BILL_OF_MATERIALS),
    CODE_LABEL(ReportSectionType.CODE_LABEL),
    CODE_MATCHES_ALL(ReportSectionType.CODE_MATCHES_ALL),
    CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION(ReportSectionType.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION),
    CODE_MATCHES_PRECISION(ReportSectionType.CODE_MATCHES_PRECISION),
    COMPARE_CODE_MATCHES_ALL(ReportSectionType.COMPARE_CODE_MATCHES_ALL),
    COMPARE_CODE_MATCHES_PRECISION(ReportSectionType.COMPARE_CODE_MATCHES_PRECISION),
    DEPENDENCIES_ALL(ReportSectionType.DEPENDENCIES_ALL),
    DEPENDENCIES_JAVA_IMPORT_STATEMENTS(ReportSectionType.DEPENDENCIES_JAVA_IMPORT_STATEMENTS),
    DEPENDENCIES_JAVA_PACKAGE_STATEMENTS(ReportSectionType.DEPENDENCIES_JAVA_PACKAGE_STATEMENTS),
    DEPENDENCIES_NON_JAVA(ReportSectionType.DEPENDENCIES_NON_JAVA),
    EXCLUDED_COMPONENTS(ReportSectionType.EXCLUDED_COMPONENTS),
    FILE_DISCOVERY_PATTERN_MATCHES_PENDING_IDENTIFICATION(ReportSectionType.FILE_DISCOVERY_PATTERN_MATCHES_PENDING_IDENTIFICATION),
    FILE_DISCOVERY_PATTERNS(ReportSectionType.FILE_DISCOVERY_PATTERNS),
    FILE_INVENTORY(ReportSectionType.FILE_INVENTORY),
    IDENTIFICATION_AUDIT_TRAIL(ReportSectionType.IDENTIFICATION_AUDIT_TRAIL),
    IDENTIFIED_FILES(ReportSectionType.IDENTIFIED_FILES),
    IP_ARCHITECTURE(ReportSectionType.IP_ARCHITECTURE),
    LICENSE_CONFLICTS(ReportSectionType.LICENSE_CONFLICTS),
    LICENSE_TEXTS(ReportSectionType.LICENSE_TEXTS),
    LICENSES_IN_EFFECT(ReportSectionType.LICENSES_IN_EFFECT),
    LINK_TO_EXTERNAL_DOCUMENTS(ReportSectionType.LINK_TO_EXTERNAL_DOCUMENTS),
    OBLIGATIONS(ReportSectionType.OBLIGATIONS),
    POTENTIAL_BILL_OF_MATERIALS(ReportSectionType.POTENTIAL_BILL_OF_MATERIALS),
    RAPID_ID_CONFIGURATIONS(ReportSectionType.RAPID_ID_CONFIGURATIONS),
    STRING_SEARCH_HITS_PENDING_ID(ReportSectionType.STRING_SEARCH_HITS_PENDING_ID),
    STRING_SEARCH_PATTERNS(ReportSectionType.STRING_SEARCH_PATTERNS),
    STRING_SEARCHES(ReportSectionType.STRING_SEARCHES),
    SUMMARY(ReportSectionType.SUMMARY),
    WORK_HISTORY_BILL_OF_MATERIALS(ReportSectionType.WORK_HISTORY_BILL_OF_MATERIALS),
    WORK_HISTORY_FILE_INVENTORY(ReportSectionType.WORK_HISTORY_FILE_INVENTORY);

    private final ReportSectionType ccType;

    private ReportSectionSelection(ReportSectionType ccType) {
        this.ccType = ccType;
    }

    public boolean isEquivalent(ReportSectionType otherCcType) {
        return ccType.equals(otherCcType);
    }

    ReportSectionType getCcType() {
        return ccType;
    }
}
