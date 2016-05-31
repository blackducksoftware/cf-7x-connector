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
package com.blackducksoftware.tools.connector.protex.obligation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.obligation.AssignedObligation;
import com.blackducksoftware.sdk.protex.obligation.ObligationCategory;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;

public class ObligationManager implements IObligationManager {
    private final ProtexAPIWrapper apiWrapper;
    private final Map<String, String> obligationCategoryByIdCache = new HashMap<>();

    public ObligationManager(ProtexAPIWrapper apiWrapper) {
	this.apiWrapper = apiWrapper;
    }

    @Override
    public List<ObligationPojo> getObligationsByLicenseId(String licenseId)
	    throws CommonFrameworkException {

	List<AssignedObligation> protexAssignedObligations;
	try {
	    protexAssignedObligations = apiWrapper.getLicenseApi()
		    .getLicenseObligations(licenseId);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting obligations for license ID " + licenseId
			    + ": " + e.getMessage());
	}
	List<ObligationPojo> obligationPojos = new ArrayList<>(
		protexAssignedObligations.size());
	for (AssignedObligation protexAssignedObligation : protexAssignedObligations) {
	    String obligationId = protexAssignedObligation.getObligationId();

	    // Now lookup obligation category name
	    String obligationCategoryName = getObligationCategoryName(
		    obligationId, protexAssignedObligation);

	    ObligationPojo obligationPojo = toPojo(protexAssignedObligation,
		    obligationCategoryName);
	    obligationPojos.add(obligationPojo);
	}
	return obligationPojos;
    }

    // TODO this does not work; seems to be an sdk bug
    // public ObligationPojo getObligationById(String obligationId)
    // throws CommonFrameworkException {
    // if (obligationByIdCache.containsKey(obligationId)) {
    // Obligation obligation = obligationByIdCache.get(obligationId);
    // String obligationCategoryName = obligationCategoryByIdCache
    // .get(obligation.getObligationCategoryId());
    // return toPojo(obligation, obligationCategoryName);
    // }
    // Obligation obligation;
    // try {
    // obligation = apiWrapper.getProxy().getObligationApi()
    // .getObligationById(obligationId);
    // } catch (SdkFault e) {
    // throw new CommonFrameworkException("Error getting obligation ID "
    // + obligationId + ": " + e.getMessage());
    // }
    //
    // // Now lookup obligation category name
    // String obligationCategoryName = getObligationCategoryName(obligationId,
    // obligation);
    //
    // obligationByIdCache.put(obligationId, obligation);
    //
    // return toPojo(obligation, obligationCategoryName);
    // }

    private String getObligationCategoryName(String obligationId,
	    AssignedObligation obligation) throws CommonFrameworkException {

	String obligationCategoryName = "";
	String obligationCategoryId = obligation.getObligationCategoryId();
	ObligationCategory obligationCategory;
	if (obligationCategoryId != null) {
	    if (obligationCategoryByIdCache.containsKey(obligationCategoryId)) {
		return obligationCategoryByIdCache.get(obligationCategoryId);
	    }
	    try {
		obligationCategory = apiWrapper.getProxy().getObligationApi()
			.getObligationCategoryById(obligationCategoryId);
	    } catch (SdkFault e) {
		throw new CommonFrameworkException(
			"Error getting obligation ID " + obligationId + ": "
				+ e.getMessage());
	    }
	    if (obligationCategory != null) {
		obligationCategoryName = obligationCategory.getName();
		obligationCategoryByIdCache.put(obligationCategoryId,
			obligationCategoryName);
	    }
	}

	return obligationCategoryName;
    }

    private ObligationPojo toPojo(AssignedObligation protexObligation,
	    String obligationCategoryName) {
	return new ObligationPojo(protexObligation.getObligationId(),
		protexObligation.getName(), protexObligation.getDescription(),
		protexObligation.getObligationCategoryId(),
		obligationCategoryName);
    }

}
