package com.blackducksoftware.tools.connector.codecenter.request;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.common.ApplicationCache;

public class RequestManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		final CodeCenterAPIWrapper ccApiWrapper = Mockito.mock(CodeCenterAPIWrapper.class);
		final ApplicationCache applicationCache = Mockito.mock(ApplicationCache.class);
		final RequestManager requestManager = new RequestManager(ccApiWrapper, applicationCache);
		// TODO finish this test
	}

}
