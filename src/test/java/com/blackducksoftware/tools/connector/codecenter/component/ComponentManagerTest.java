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
package com.blackducksoftware.tools.connector.codecenter.component;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionOrIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentPageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.license.LicenseManager;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;

public class ComponentManagerTest {

	private static final String TEST_COMP_VERSION = "testCompVersion";
	private static final String TEST_COMP_NAME = "testComp1";
	private static final String TEST_COMP_ID = "testCompId";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetComponentByIdFromCodeCenter() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);

		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		Mockito.when(mockColaApi.getCatalogComponent(Mockito.any(ComponentNameVersionOrIdToken.class))).thenReturn(
				testComponents.get(0));

		compMgr.getComponentById(CodeCenterComponentPojo.class, TEST_COMP_ID);

		// Make sure component was fetched from Code Center
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	@Test
	public void testGetComponentByIdFromCache() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);
		Mockito.when(mockColaApi.searchCatalogComponents(Mockito.anyString(), Mockito.any(ComponentPageFilter.class))).thenReturn(testComponents);
		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		compMgr.populateComponentCacheFromCatalog(1000);

		compMgr.getComponentById(CodeCenterComponentPojo.class, TEST_COMP_ID);

		// Make sure component was fetched from cache, not Code Center
		Mockito.verify(mockColaApi, Mockito.times(0)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}


	@Test
	public void testGetComponentByNameVersionFromCodeCenter() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);

		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		Mockito.when(mockColaApi.getCatalogComponent(Mockito.any(ComponentNameVersionOrIdToken.class))).thenReturn(
				testComponents.get(0));

		compMgr.getComponentByNameVersion(CodeCenterComponentPojo.class, TEST_COMP_NAME, TEST_COMP_VERSION);

		// Make sure component was fetched from Code Center
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	@Test
	public void testGetComponentByNameVersionFromCache() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);
		Mockito.when(mockColaApi.searchCatalogComponents(Mockito.anyString(), Mockito.any(ComponentPageFilter.class)))
		.thenReturn(testComponents);
		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		compMgr.populateComponentCacheFromCatalog(1000);

		compMgr.getComponentByNameVersion(CodeCenterComponentPojo.class, TEST_COMP_NAME, TEST_COMP_VERSION);

		// Make sure component was fetched from cache, not Code Center
		Mockito.verify(mockColaApi, Mockito.times(0)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	@Test
	public void testGetComponentByNameVersionPopulatesByIdCache() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);

		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		Mockito.when(mockColaApi.getCatalogComponent(Mockito.any(ComponentNameVersionOrIdToken.class))).thenReturn(
				testComponents.get(0));

		compMgr.getComponentByNameVersion(CodeCenterComponentPojo.class, TEST_COMP_NAME, TEST_COMP_VERSION);

		// Make sure component was fetched from Code Center
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));

		compMgr.getComponentById(CodeCenterComponentPojo.class, TEST_COMP_ID);

		// Make sure component was fetched from cache
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	@Test
	public void testGetComponentByIdPopulatesByNameVersionCache() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);

		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		Mockito.when(mockColaApi.getCatalogComponent(Mockito.any(ComponentNameVersionOrIdToken.class))).thenReturn(
				testComponents.get(0));

		compMgr.getComponentById(CodeCenterComponentPojo.class, TEST_COMP_ID);

		// Make sure component was fetched from Code Center
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));

		compMgr.getComponentByNameVersion(CodeCenterComponentPojo.class, TEST_COMP_NAME, TEST_COMP_VERSION);

		// Make sure component was fetched from cache
		Mockito.verify(mockColaApi, Mockito.times(1)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	@Test
	public void testCacheReset() throws CommonFrameworkException, SdkFault {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		final List<Component> testComponents = generateTestComponentList();
		final ColaApi mockColaApi = Mockito.mock(ColaApi.class);
		Mockito.when(mockColaApi.searchCatalogComponents(Mockito.anyString(), Mockito.any(ComponentPageFilter.class)))
		.thenReturn(testComponents);
		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		compMgr.populateComponentCacheFromCatalog(1000);

		compMgr.getComponentById(CodeCenterComponentPojo.class, TEST_COMP_ID);

		// Make sure component was fetched from cache, not Code Center
		Mockito.verify(mockColaApi, Mockito.times(0)).getCatalogComponent(Mockito.any(ComponentIdToken.class));

		long numCacheEntriesDiscarded = compMgr.resetComponentCache(50000, 7, TimeUnit.DAYS);
		assertEquals(1, numCacheEntriesDiscarded);

		numCacheEntriesDiscarded = compMgr.resetComponentCache(1000, 30, TimeUnit.MINUTES);
		assertEquals(0, numCacheEntriesDiscarded);
	}

	private List<Component> generateTestComponentList() {
		final List<Component> testComponents = new ArrayList<>();
		final Component comp = new Component();
		comp.setName(TEST_COMP_NAME);
		final ComponentIdToken compIdToken = new ComponentIdToken();
		compIdToken.setId(TEST_COMP_ID);
		comp.setId(compIdToken);
		final ComponentNameVersionToken compNameVersionToken = new ComponentNameVersionToken();
		compNameVersionToken.setName(TEST_COMP_NAME);
		compNameVersionToken.setVersion(TEST_COMP_VERSION);
		comp.setNameVersion(compNameVersionToken);
		comp.setVersion(TEST_COMP_VERSION);
		comp.setApprovalStatus(ApprovalStatusEnum.APPROVED);
		testComponents.add(comp);
		return testComponents;
	}

}
