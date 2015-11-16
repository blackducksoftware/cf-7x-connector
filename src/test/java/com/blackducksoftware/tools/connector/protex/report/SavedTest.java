/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/
package com.blackducksoftware.tools.connector.protex.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Workbook;

import com.blackducksoftware.tools.commonframework.core.config.ConfigurationManager;
import com.blackducksoftware.tools.commonframework.test.TestProtexConfigurationManager;
import com.blackducksoftware.tools.commonframework.test.TestUtils;

/**
 * Base class for Saved Report Tests
 * 
 * @author akamen
 *
 */
public class SavedTest {

    protected static final String PROTEX_USER = "unitTester@blackducksoftware.com";
    protected static final String PROTEX_PASSWORD = "blackduck";

    protected static ConfigurationManager initConfig(String protexServerName) {
	Properties props = new Properties();
	props.setProperty("protex.server.name", protexServerName);
	props.setProperty("protex.user.name", PROTEX_USER);
	props.setProperty("protex.password", PROTEX_PASSWORD);
	ConfigurationManager config = new TestProtexConfigurationManager(props);
	return config;
    }

    protected String writeOutReportToTempFile(Workbook wb) throws IOException {
	String reportFilename = TestUtils.getTempReportFilePath();
	OutputStream os = new FileOutputStream(reportFilename);
	wb.write(os);
	os.close();

	return reportFilename;
    }

}
