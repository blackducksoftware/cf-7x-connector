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
import com.blackducksoftware.tools.connector.common.ApprovalStatus;
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
	log.info("Getting component " + nameVersionIds.getNameId() + " / "
		+ nameVersionIds.getVersionId());
	Component comp = getProtexComponentByNameVersionIds(nameVersionIds);
	return toPojo(nameVersionIds, comp);
    }

    @Override
    public List<ProtexComponentPojo> getComponentsByNameVersionIds(
	    List<ComponentNameVersionIds> nameVersionIdsList)
	    throws CommonFrameworkException {
	log.info("Getting a list of components");
	// Derive a list of those components not already in the cache
	List<ComponentKey> missingFromCache = getComponentsMissingFromCache(nameVersionIdsList);

	// use SDK to fetch those missing from cache, adding them to the cache
	if (missingFromCache.size() > 0) {
	    List<Component> protexComponents;
	    try {
		protexComponents = apiWrapper.getComponentApi()
			.getComponentsByKey(missingFromCache);
	    } catch (SdkFault e) {
		throw new CommonFrameworkException(
			"Error getting a list of components: " + e.getMessage());
	    }
	    addToCache(protexComponents);
	}

	// serve the original request from the now-fully-populated cache
	List<ProtexComponentPojo> results = getComponentsFromCache(nameVersionIdsList);
	return results;
    }

    // Private methods

    private List<ProtexComponentPojo> getComponentsFromCache(
	    List<ComponentNameVersionIds> nameVersionIdsList)
	    throws CommonFrameworkException {
	List<ProtexComponentPojo> results = new ArrayList<>(
		nameVersionIdsList.size());
	for (ComponentNameVersionIds nameVersionIds : nameVersionIdsList) {
	    Component protexComp = componentsByNameVersionIds
		    .get(nameVersionIds);
	    ProtexComponentPojo comp = toPojo(nameVersionIds, protexComp);
	    results.add(comp);
	}
	return results;
    }

    private List<ComponentKey> getComponentsMissingFromCache(
	    List<ComponentNameVersionIds> nameVersionIdsList) {
	List<ComponentKey> missingFromCache = new ArrayList<>(
		nameVersionIdsList.size());
	for (ComponentNameVersionIds nameVersionIds : nameVersionIdsList) {
	    if (!componentsByNameVersionIds.containsKey(nameVersionIds)) {
		ComponentKey protexCompKey = ComponentNameVersionIds
			.toProtexComponentKey(nameVersionIds);
		missingFromCache.add(protexCompKey);
	    }
	}
	return missingFromCache;
    }

    private Component getProtexComponentByNameVersionIds(
	    ComponentNameVersionIds nameVersionIds)
	    throws CommonFrameworkException {

	if (componentsByNameVersionIds.containsKey(nameVersionIds)) {
	    return componentsByNameVersionIds.get(nameVersionIds);
	}

	// Get from protex
	ComponentKey key = ComponentNameVersionIds
		.toProtexComponentKey(nameVersionIds);
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

	String primaryLicenseName = null;
	if (protexComp.getPrimaryLicenseId() != null) {
	    primaryLicenseName = licMgr.getLicenseById(
		    protexComp.getPrimaryLicenseId()).getName();
	}
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
		protexComp.getDescription(), protexComp.getPrimaryLicenseId(),
		primaryLicenseName);
	return comp;
    }

    private void addToCache(ComponentNameVersionIds nameVersionIds,
	    Component comp) {
	componentsByNameVersionIds.put(nameVersionIds, comp);
    }

    private void addToCache(List<Component> protexComponents) {
	for (Component protexComponent : protexComponents) {
	    ComponentNameVersionIds nameVersionIds = ComponentNameVersionIds
		    .valueOf(protexComponent);
	    componentsByNameVersionIds.put(nameVersionIds, protexComponent);
	}
    }
}
