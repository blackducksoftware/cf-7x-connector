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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LicensePojoTest {
    private static final String OBLIGATION2 = "obligation2";
    private static final String OBLIGATION1 = "obligation1";
    private static final String TEST_TEXT = "Test License Text";
    private static final String TEST_SUFFIX = "Test Suffix";
    private static final String TEST_EXPLANATION = "Test Explanation";
    private static final String TEST_COMMENT = "Test Comment";
    private static final String TEST_NAME = "Test Name";
    private static final String TEST_ID = "testId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	List<String> obligationIds = new ArrayList<>(2);
	obligationIds.add(OBLIGATION1);
	obligationIds.add(OBLIGATION2);

	ProtexLicensePojo lic = new ProtexLicensePojo(TEST_ID, TEST_NAME,
		TEST_COMMENT, TEST_EXPLANATION, TEST_SUFFIX,
		ProtexLicensePojo.ApprovalState.DISAPPROVED, TEST_TEXT,
		obligationIds);

	assertEquals(TEST_ID, lic.getId());
	assertEquals(TEST_NAME, lic.getName());
	assertEquals(TEST_COMMENT, lic.getComment());
	assertEquals(TEST_EXPLANATION, lic.getExplanation());
	assertEquals(TEST_SUFFIX, lic.getSuffix());
	assertEquals(ProtexLicensePojo.ApprovalState.DISAPPROVED,
		lic.getApprovalState());
	assertEquals(TEST_TEXT, lic.getLicenseText());

	assertEquals(OBLIGATION1, lic.getObligationIds().get(0));
	assertEquals(OBLIGATION2, lic.getObligationIds().get(1));
    }
}
