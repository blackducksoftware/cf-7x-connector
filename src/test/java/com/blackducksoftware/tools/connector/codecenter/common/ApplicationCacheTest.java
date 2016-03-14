package com.blackducksoftware.tools.connector.codecenter.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;

public class ApplicationCacheTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testApplication() {
        ApplicationCache appCache = new ApplicationCache();

        // Put an application in the cache
        Application app = new Application();
        app.setName("testAppName");
        app.setVersion("testAppVersion");
        ApplicationIdToken appIdToken = new ApplicationIdToken();
        appIdToken.setId("testAppId");
        app.setId(appIdToken);
        appCache.putApplication(app);

        // Verify that app is found in cache
        assertTrue(appCache.containsApplication("testAppId"));
        assertEquals("testAppName", appCache.getApplication("testAppId").getName());
        NameVersion nameVersion = new NameVersion("testAppName", "testAppVersion");
        assertTrue(appCache.containsApplication(nameVersion));
        assertEquals("testAppVersion", appCache.getApplication(nameVersion).getVersion());
    }

    @Test
    public void testRequestList() {
        ApplicationCache appCache = new ApplicationCache();

        // Add a request summary list to the cache
        List<RequestSummary> requestSummaries = new ArrayList<RequestSummary>(1);
        requestSummaries.add(null);
        appCache.putRequestList("testAppId", requestSummaries);

        // Verify that the request summary list can be found
        assertTrue(appCache.containsRequestList("testAppId"));
        assertEquals(null, appCache.getRequestList("testAppId").get(0));
    }

}
