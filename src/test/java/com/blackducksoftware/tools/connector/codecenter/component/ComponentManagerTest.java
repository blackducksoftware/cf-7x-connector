package com.blackducksoftware.tools.connector.codecenter.component;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
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

	// @Test
	public void testPopulateComponentCacheFromCatalog() throws CommonFrameworkException {
		final CodeCenterAPIWrapper mockCodeCenterApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final IAttributeDefinitionManager mockAttrDefMgr = Mockito.mock(AttributeDefinitionManager.class);
		final ILicenseManager<LicensePojo> mockLicenseManager = Mockito.mock(LicenseManager.class);

		// codeCenterApiWrapper.getColaApi().searchCatalogComponents("",
		// pageFilter);

		// TODO mock the above call and finish the test

		final ComponentManager compMgr = new ComponentManager(mockCodeCenterApiWrapper, mockAttrDefMgr,
				mockLicenseManager);

		compMgr.populateComponentCacheFromCatalog(1000);
	}

}
