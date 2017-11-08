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
