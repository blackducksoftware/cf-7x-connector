/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.tools.connector.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo.ApprovalState;

public class ProtexProjectPojoTest {

    private static final String OBLIGATION2 = "obligation2";
    private static final String OBLIGATION1 = "obligation1";
    private static final String TEST_LICENSE_NAME = "test license name";
    private static final String VERSION_ID = "versionId";
    private static final String NAME_ID = "nameId";
    private static final String TEST_PRIMARY_LICENSE_ID = "testPrimaryLicenseId";
    private static final String TEST_PRIMARY_LICENSE_NAME = "Test Primary License Name";
    private static final String TEST_COMPONENT_DESCRIPTION = "test component description";
    private static final String TEST_HOMEPAGE = "testHomepage";
    private static final String TEST_COMPONENT_VERSION = "testComponentVersion";
    private static final String TEST_COMPONENT_NAME = "testComponentName";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	ComponentNameVersionIds nameVersionIds = new ComponentNameVersionIds(
		NAME_ID, VERSION_ID);
	List<String> obligationIds = new ArrayList<>(2);
	obligationIds.add(OBLIGATION1);
	obligationIds.add(OBLIGATION2);
	List<ProtexLicensePojo> licenses = new ArrayList<>();
	ProtexLicensePojo license = new ProtexLicensePojo("testLicenseId",
		TEST_LICENSE_NAME, "test comment", "", "",
		ApprovalState.APPROVED, "test license text", obligationIds);
	licenses.add(license);

	ProtexComponentPojo comp = new ProtexComponentPojo();

	comp.setName(TEST_COMPONENT_NAME);
	comp.setVersion(TEST_COMPONENT_VERSION);
	comp.setApprovalStatus(ApprovalStatus.APPROVED);
	comp.setHomepage(TEST_HOMEPAGE);
	comp.setDeprecated(true);
	comp.setNameVersionIds(nameVersionIds);
	comp.setLicenses(licenses);
	comp.setType(ProtexComponentType.STANDARD);
	comp.setDescription(TEST_COMPONENT_DESCRIPTION);
	comp.setPrimaryLicenseId(TEST_PRIMARY_LICENSE_ID);
	comp.setPrimaryLicenseName(TEST_PRIMARY_LICENSE_NAME);

	assertEquals(TEST_COMPONENT_NAME, comp.getName());
	assertEquals(TEST_COMPONENT_VERSION, comp.getVersion());
	assertEquals(ApprovalStatus.APPROVED, comp.getApprovalStatus());
	assertEquals(TEST_HOMEPAGE, comp.getHomepage());
	assertEquals(true, comp.isDeprecated());
	assertEquals(NAME_ID, comp.getNameVersionIds().getNameId());
	assertEquals(VERSION_ID, comp.getNameVersionIds().getVersionId());
	assertEquals(TEST_LICENSE_NAME, comp.getLicenses().get(0).getName());
	assertEquals(ProtexComponentType.STANDARD, comp.getType());
	assertEquals(TEST_COMPONENT_DESCRIPTION, comp.getDescription());
	assertEquals(TEST_PRIMARY_LICENSE_ID, comp.getPrimaryLicenseId());
	assertEquals(TEST_PRIMARY_LICENSE_NAME, comp.getPrimaryLicenseName());

	assertEquals(OBLIGATION1, comp.getLicenses().get(0).getObligationIds()
		.get(0));
	assertEquals(OBLIGATION2, comp.getLicenses().get(0).getObligationIds()
		.get(1));
    }
}
