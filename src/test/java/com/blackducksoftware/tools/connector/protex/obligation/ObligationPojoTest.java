package com.blackducksoftware.tools.connector.protex.obligation;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ObligationPojoTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() {
	ObligationPojo obligation = new ObligationPojo("testObligationId",
		"testObligationName", "testObligationDescription",
		"testObligationCategoryId", "testObligationCategoryName");

	assertEquals("testObligationId", obligation.getId());
	assertEquals("testObligationName", obligation.getName());
	assertEquals("testObligationDescription", obligation.getDescription());
	assertEquals("testObligationCategoryId",
		obligation.getObligationCategoryId());
	assertEquals("testObligationCategoryName",
		obligation.getObligationCategoryName());
    }

}
