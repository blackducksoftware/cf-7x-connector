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
package com.blackducksoftware.tools.connector.codecenter.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

public class ApprovalStatusTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	assertTrue(ApprovalStatus.ALL.isEquivalent(ApprovalStatusEnum.ALL));
	assertTrue(ApprovalStatus.APPEALED
		.isEquivalent(ApprovalStatusEnum.APPEALED));
	assertTrue(ApprovalStatus.APPROVED
		.isEquivalent(ApprovalStatusEnum.APPROVED));
	assertTrue(ApprovalStatus.CANCELLED
		.isEquivalent(ApprovalStatusEnum.CANCELED));
	assertTrue(ApprovalStatus.DEFERRED
		.isEquivalent(ApprovalStatusEnum.DEFERRED));
	assertTrue(ApprovalStatus.MORE_INFO
		.isEquivalent(ApprovalStatusEnum.MOREINFO));
	assertTrue(ApprovalStatus.NOT_SUBMITTED
		.isEquivalent(ApprovalStatusEnum.NOTSUBMITTED));
	assertTrue(ApprovalStatus.PENDING
		.isEquivalent(ApprovalStatusEnum.PENDING));
	assertTrue(ApprovalStatus.REJECTED
		.isEquivalent(ApprovalStatusEnum.REJECTED));
	assertFalse(ApprovalStatus.REJECTED
		.isEquivalent(ApprovalStatusEnum.APPROVED));
    }

}
