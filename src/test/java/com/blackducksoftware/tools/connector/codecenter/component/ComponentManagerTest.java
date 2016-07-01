package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.ArrayList;
import java.util.List;

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
		Mockito.when(mockColaApi.searchCatalogComponents(Mockito.anyString(), Mockito.any(ComponentPageFilter.class)))
		.thenReturn(testComponents);
		Mockito.when(mockCodeCenterApiWrapper.getColaApi()).thenReturn(mockColaApi);

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		Mockito.when(mockColaApi.getCatalogComponent(Mockito.any(ComponentNameVersionOrIdToken.class))).thenReturn(
				testComponents.get(0));

		compMgr.getComponentById(CodeCenterComponentPojo.class, "testCompId");

		// Make sure component was fetched from cache, not Code Center
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

		compMgr.getComponentById(CodeCenterComponentPojo.class, "testCompId");

		// Make sure component was fetched from cache, not Code Center
		Mockito.verify(mockColaApi, Mockito.times(0)).getCatalogComponent(Mockito.any(ComponentIdToken.class));
	}

	private List<Component> generateTestComponentList() {
		final List<Component> testComponents = new ArrayList<>();
		final Component comp = new Component();
		comp.setName("testComp1");
		final ComponentIdToken compIdToken = new ComponentIdToken();
		compIdToken.setId("testCompId");
		comp.setId(compIdToken);
		final ComponentNameVersionToken compNameVersionToken = new ComponentNameVersionToken();
		compNameVersionToken.setName("testComp1");
		compNameVersionToken.setVersion("testCompVersion");
		comp.setNameVersion(compNameVersionToken);
		comp.setVersion("testCompVersion");
		comp.setApprovalStatus(ApprovalStatusEnum.APPROVED);
		testComponents.add(comp);
		return testComponents;
	}

}
