package com.blackducksoftware.tools.connector.codecenter.application;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApplicationUserPojoTest {

    private static final String APP_VERSION = "TestAppVersion";

    private static final String APP_NAME = "Test Application";

    private static final String APP_ID = "testAppId";

    private static final String USER_NAME = "testUserName";

    private static final String USER_ID = "testUserId";

    private static final String ROLE_NAME = "testRoleName";

    private static final String ROLE_ID = "testRoleId";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {

        ApplicationUserPojo appUser = new ApplicationUserPojo(APP_NAME, APP_VERSION,
                APP_ID, USER_NAME, USER_ID, ROLE_NAME, ROLE_ID);

        assertEquals(APP_VERSION, appUser.getApplicationVersion());
        assertEquals(APP_NAME, appUser.getApplicationName());
        assertEquals(APP_ID, appUser.getApplicationId());
        assertEquals(USER_NAME, appUser.getUserName());
        assertEquals(USER_ID, appUser.getUserId());
        assertEquals(ROLE_NAME, appUser.getRoleName());
        assertEquals(ROLE_ID, appUser.getRoleId());
    }

}
