package com.blackducksoftware.tools.connector.protex.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.common.ComponentKey;
import com.blackducksoftware.sdk.protex.component.Component;
import com.blackducksoftware.sdk.protex.license.LicenseInfo;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.protex.ProtexAPIWrapper;
import com.blackducksoftware.tools.connector.protex.common.ComponentNameVersionIds;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentPojo;
import com.blackducksoftware.tools.connector.protex.common.ProtexComponentType;
import com.blackducksoftware.tools.connector.protex.license.ProtexLicensePojo;

public class ProtexComponentManager implements IProtexComponentManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final ProtexAPIWrapper apiWrapper;
    private final ILicenseManager<ProtexLicensePojo> licMgr;
    Map<ComponentNameVersionIds, Component> componentsByNameVersionIds = new HashMap<>();

    public ProtexComponentManager(ProtexAPIWrapper apiWrapper,
	    ILicenseManager<ProtexLicensePojo> licMgr) {
	this.apiWrapper = apiWrapper;
	this.licMgr = licMgr;
    }

    @Override
    public ProtexComponentPojo getComponentByNameVersionIds(
	    ComponentNameVersionIds nameVersionIds)
	    throws CommonFrameworkException {

	Component comp = getProtexComponentByNameVersionIds(nameVersionIds);

	return toPojo(nameVersionIds, comp);
    }

    @Override
    public List<ProtexComponentPojo> getComponentsByNameVersionIds(
	    List<ComponentNameVersionIds> nameVersionIds)
	    throws CommonFrameworkException {
	// TODO
	// Loop through components building a list of those that are not in
	// cache
	// SDK: Fetch those, and populate cache
	// serve the original request from the cache
	return null;
    }

    // Private methods

    private Component getProtexComponentByNameVersionIds(
	    ComponentNameVersionIds nameVersionIds)
	    throws CommonFrameworkException {

	if (componentsByNameVersionIds.containsKey(nameVersionIds)) {
	    return componentsByNameVersionIds.get(nameVersionIds);
	}

	// Get from protex
	ComponentKey key = new ComponentKey();
	key.setComponentId(nameVersionIds.getNameId());
	key.setVersionId(nameVersionIds.getVersionId());
	Component comp;
	try {
	    comp = apiWrapper.getComponentApi().getComponentByKey(key);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error getting Component ID "
		    + nameVersionIds.getNameId() + ", version "
		    + nameVersionIds.getVersionId());
	}

	addToCache(nameVersionIds, comp);

	return comp;
    }

    private ProtexComponentPojo toPojo(ComponentNameVersionIds nameVersionIds,
	    Component protexComp) throws CommonFrameworkException {
	List<LicenseInfo> protexCompLicenses = protexComp.getLicenses();
	List<ProtexLicensePojo> licenses;
	if (protexCompLicenses == null) {
	    licenses = null;
	} else {
	    licenses = new ArrayList<>(protexCompLicenses.size());

	    for (LicenseInfo protexLicenseInfo : protexCompLicenses) {
		ProtexLicensePojo lic = licMgr.getLicenseById(protexLicenseInfo
			.getLicenseId());
		licenses.add(lic);
	    }
	}

	ProtexComponentPojo comp = new ProtexComponentPojo(
		protexComp.getComponentName(), protexComp.getVersionName(),
		ApprovalStatus.valueOf(protexComp.getApprovalState()),
		protexComp.getHomePage(), protexComp.isDeprecated(),
		nameVersionIds, licenses,
		ProtexComponentType.valueOf(protexComp.getComponentType()),
		protexComp.getDescription(), protexComp.getPrimaryLicenseId());
	return comp;
    }

    private void addToCache(ComponentNameVersionIds nameVersionIds,
	    Component comp) {
	componentsByNameVersionIds.put(nameVersionIds, comp);
    }

}
