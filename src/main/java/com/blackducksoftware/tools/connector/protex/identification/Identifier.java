/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 *  under the License.
 *
 *******************************************************************************/

package com.blackducksoftware.tools.connector.protex.identification;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;

public interface Identifier {

    /**
     * Make a Code Match or Declared Identification on the given file.
     *
     * @param path
     * @param target
     * @throws SdkFault
     */
    void makeIdentificationOnFile(String path, Discovery target)
	    throws SdkFault;

    /**
     * Make a String Search Identification on the given file.
     *
     * @param path
     * @param target
     * @param componentId
     * @param componentVersionId
     * @throws SdkFault
     */
    void makeStringSearchIdentificationOnFile(String path,
	    StringSearchDiscovery target, String componentId,
	    String componentVersionId) throws SdkFault;

    /**
     * Returns true if the BOM should be refreshed after the ID is made.
     *
     * @return
     */
    boolean isFinalBomRefreshRequired();

    /**
     * Returns true if Identification strategy being used requires multiple
     * passes (until # pendingIds == 0)
     *
     * @return
     */
    boolean isMultiPassIdStrategy();
}
