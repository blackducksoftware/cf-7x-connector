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
package com.blackducksoftware.tools.connector.protex.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.protex.license.LicenseApprovalState;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class ApprovalStateTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertTrue(ProtexLicensePojo.ApprovalState.APPROVED
		.isEquivalent(LicenseApprovalState.APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.BLANKET_APPROVED
		.isEquivalent(LicenseApprovalState.BLANKET_APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.DISAPPROVED
		.isEquivalent(LicenseApprovalState.DIS_APPROVED));
	assertTrue(ProtexLicensePojo.ApprovalState.NOT_REVIEWED
		.isEquivalent(LicenseApprovalState.NOT_REVIEWED));

	assertEquals("APPROVED", ProtexLicensePojo.ApprovalState.APPROVED.toString());
    }

}
