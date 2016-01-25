package com.blackducksoftware.tools.connector.codecenter.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeCenterUserPojoTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
        CodeCenterUserPojo user = new CodeCenterUserPojo("testId", "testUsername",
                "testFirst", "testLast", "testEmail", true);

        assertEquals("testId", user.getId());
        assertEquals("testUsername", user.getUsername());
        assertEquals("testFirst", user.getFirstName());
        assertEquals("testLast", user.getLastName());
        assertEquals("testEmail", user.getEmail());
        assertTrue(user.isActive());
    }

}
