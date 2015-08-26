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

package com.blackducksoftware.tools.commonframework.standard.codecenter.pojo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.ApplicationDao;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.CodeCenterApplicationDao;
import com.blackducksoftware.tools.commonframework.standard.codecenter.dao.CodeCenterDaoConfigManager;

/**
 * Copies component-use-specific vulnerability metadata (remediation dates,
 * status, comment) from one app to another.
 *
 * @author sbillings
 *
 */
public class CcAppCompVulnCopier {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private final CodeCenterDaoConfigManager config;
    private final String appName;
    private final String appVersion;
    private final Map<String, VulnerabilityPojo> vulnMap; // Remembers metadata
							  // from
							  // source app; applied
							  // to
							  // destination app

    public CcAppCompVulnCopier(CodeCenterDaoConfigManager config,
	    String appName, String appVersion) {
	this.config = config;
	this.appName = appName;
	this.appVersion = appVersion;
	vulnMap = new HashMap<String, VulnerabilityPojo>(1000);
    }

    /**
     * Collect and cache componentuse vulnerability metadata from the given app.
     *
     * @param config
     * @param appName
     * @param version
     * @throws Exception
     */
    public void loadVulnerabilityMetadataIntoCache() throws Exception {
	processVulnerabilityMetadata(appName, appVersion, false);
    }

    /**
     * Apply the given metadata to the cached vulnerability.
     *
     * @param compName
     * @param compVersion
     * @param vulnName
     * @param targetRemediationDate
     * @param actualRemediationDate
     * @param statusName
     * @param comment
     * @return
     * @throws Exception
     */
    public boolean updateCachedVulnerabilityMetadata(String compName,
	    String compVersion, String vulnName, Date targetRemediationDate,
	    Date actualRemediationDate, String statusName, String comment) {

	String key = generateKey(compName, compVersion, vulnName);

	if (vulnMap.containsKey(key)) {
	    VulnerabilityPojo vuln = vulnMap.get(key);
	    vuln.setTargetRemediationDate(targetRemediationDate);
	    vuln.setActualRemediationDate(actualRemediationDate);

	    if ((statusName != null) && (statusName.length() > 0)) {
		vuln.setStatus(statusName);
	    }

	    if ((comment != null) && (comment.length() > 0)) {
		vuln.setStatusComment(comment);
	    }
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Apply the remembered componentuse vulnerability metadata to the GIVEN
     * app. In other words: copy vulnerability metadata from this app to given
     * app.
     *
     * @param config
     * @param appName
     * @param version
     * @throws Exception
     */
    public void applyCachedVulnerabilityMetadataToGivenApp(
	    CodeCenterDaoConfigManager config, String appName, String version)
	    throws Exception {
	processVulnerabilityMetadata(appName, version, true);
    }

    private Map<String, VulnerabilityPojo> processVulnerabilityMetadata(
	    String appName, String appVersion, boolean writeMode)
	    throws Exception {

	// Construct a new datasource each time since the application written to
	// != application read from
	ApplicationDao dataSource = new CodeCenterApplicationDao(config,
		appName, appVersion);

	log.info("Fetching application: " + appName + " version " + appVersion);
	ApplicationPojo app = dataSource.getApplication();
	if (app == null) {
	    throw new Exception("Unable to load application " + appName
		    + " version " + appVersion);
	}

	log.info("Fetching components and vulnerabilities");
	collectDataApplication(dataSource, app, writeMode);

	return vulnMap;
    }

    private void collectDataApplication(ApplicationDao dataSource,
	    ApplicationPojo app, boolean writeMode) throws Exception {
	List<ComponentUsePojo> compUses = dataSource.getComponentUses();
	for (ComponentUsePojo compUse : compUses) {
	    collectDataComponentUse(dataSource, app, compUse, writeMode);
	}
    }

    private void collectDataComponentUse(ApplicationDao dataSource,
	    ApplicationPojo app, ComponentUsePojo compUse, boolean writeMode)
	    throws Exception {
	ComponentPojo comp = dataSource.getComponent(compUse);
	collectDataComponent(dataSource, app, compUse, comp, writeMode);
    }

    private void collectDataComponent(ApplicationDao dataSource,
	    ApplicationPojo app, ComponentUsePojo compUse, ComponentPojo comp,
	    boolean writeMode) throws Exception {

	List<VulnerabilityPojo> vulns = dataSource.getVulnerabilities(comp,
		compUse);
	for (VulnerabilityPojo vuln : vulns) {
	    log.debug("Vulnerability: " + vuln.getName());

	    log.debug("App: " + app.getName() + " / " + app.getVersion()
		    + "; comp: " + comp.getName() + " / " + comp.getVersion()
		    + "; vuln: " + vuln.getName());

	    String key = generateKey(comp.getName(), comp.getVersion(),
		    vuln.getName());
	    VulnerabilityPojo origVuln = null;

	    if (!writeMode) {

		log.debug("Read from Code Center: target: "
			+ vuln.getTargetRemediationDate() + " ("
			+ getTimeMillis(vuln.getTargetRemediationDate()) + ")"
			+ ", actual: " + vuln.getActualRemediationDate() + " ("
			+ getTimeMillis(vuln.getActualRemediationDate()) + ")"
			+ "; vuln status comment: " + vuln.getStatusComment());
		vulnMap.put(key, vuln);
	    } else {
		if (!vulnMap.containsKey(key)) {
		    throw new Exception(key + " not found in original app");
		}

		origVuln = vulnMap.get(key);
		vuln.setTargetRemediationDate(origVuln
			.getTargetRemediationDate());
		vuln.setActualRemediationDate(origVuln
			.getActualRemediationDate());
		vuln.setStatus(origVuln.getStatus());
		vuln.setStatusComment(origVuln.getStatusComment());

		log.debug("Writing to Code Center: target: "
			+ vuln.getTargetRemediationDate() + " ("
			+ getTimeMillis(vuln.getTargetRemediationDate()) + ")"
			+ ", actual: " + vuln.getActualRemediationDate() + " ("
			+ getTimeMillis(vuln.getActualRemediationDate()) + ")"
			+ "; vuln status comment: " + vuln.getStatusComment());

		dataSource.updateCompUseVulnData(compUse, vuln);
	    }
	}
    }

    private long getTimeMillis(Date date) {
	if (date == null) {
	    return 0L;
	}
	return date.getTime();
    }

    private String generateKey(String compName, String compVersion,
	    String vulnName) {
	String delim = "|||";
	return compName + delim + compVersion + delim + vulnName;
    }
}
