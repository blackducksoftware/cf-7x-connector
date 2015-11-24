package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.Application;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.application.data.ApplicationNameVersionToken;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.codecenter.component.ICodeCenterComponentManager;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * Provides a higher level of abstraction for accessing Code Center
 * applications.
 *
 * The objects returned are POJOs, not SDK objects.
 *
 * Applications are cached in case they are requested again.
 *
 * Attribute values are part of the application to which they are assigned.
 *
 * Multiple value attributes are not supported. If a multiple-value attribute is
 * read, the first value (only) will be used.
 *
 * @author sbillings
 *
 */
public class ApplicationManager implements IApplicationManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());
    private final CodeCenterAPIWrapper ccApiWrapper;
    private final IAttributeDefinitionManager attrDefMgr;
    private final ICodeCenterComponentManager compMgr;
    private final Map<NameVersion, Application> appsByNameVersionCache = new HashMap<>();
    private final Map<String, Application> appsByIdCache = new HashMap<>();
    private final Map<String, List<RequestSummary>> requestListsByAppIdCache = new HashMap<>();
    private boolean allApplicationsFetched = false;

    public ApplicationManager(CodeCenterAPIWrapper ccApiWrapper,
	    IAttributeDefinitionManager attrDefMgr,
	    ICodeCenterComponentManager compMgr) {
	this.ccApiWrapper = ccApiWrapper;
	this.attrDefMgr = attrDefMgr;
	this.compMgr = compMgr;
    }

    /**
     * Get all applications that the user can access.
     *
     * If this method has been called before, it is serviced using cache only.
     * Otherwise, all applications are fetched from Code Center.
     *
     */
    // @Override
    public List<ApplicationPojo> getAllApplications()
	    throws CommonFrameworkException {
	if (allApplicationsFetched) {
	    // TODO serve from cache
	    // List<ApplicationPojo> = new
	    // ArrayList<>(appsByIdCache.keySet().size());
	    for (String appId : appsByIdCache.keySet()) {

	    }

	    // TODO: Produce a sorted list
	}

	// TODO: fetch all from CC

	// TODO: cache all

	allApplicationsFetched = true;
	throw new UnsupportedOperationException(
		"This method has not been implemented yet.");

    }

    /**
     * Get an application by name/version.
     *
     * Applications are cached.
     */
    @Override
    public ApplicationPojo getApplicationByNameVersion(String name,
	    String version) throws CommonFrameworkException {
	NameVersion nameVersion = new NameVersion(name, version);
	if (appsByNameVersionCache.containsKey(nameVersion)) {
	    Application app = appsByNameVersionCache.get(nameVersion);
	    ApplicationPojo appPojo = toPojo(app);
	    return appPojo;
	}
	ApplicationNameVersionToken appToken = new ApplicationNameVersionToken();
	appToken.setName(name);
	appToken.setVersion(version);
	Application app = null;
	try {
	    app = ccApiWrapper.getApplicationApi().getApplication(appToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error getting application "
		    + name + " / " + version + ": " + e.getMessage());
	}
	addAppToCache(nameVersion, app);

	return toPojo(app);
    }

    private ApplicationPojo toPojo(Application app)
	    throws CommonFrameworkException {
	ApplicationPojo appPojo = new ApplicationPojo(app.getId().getId(),
		app.getName(), app.getVersion(), AttributeValues.valueOf(
			attrDefMgr, app.getAttributeValues()),
		ApprovalStatus.valueOf(app.getApprovalStatus()));
	return appPojo;
    }

    private void addAppToCache(NameVersion nameVersion, Application app) {
	appsByNameVersionCache.put(nameVersion, app);
	appsByIdCache.put(app.getId().getId(), app);
    }

    /**
     * Get an application by ID.
     *
     * Applications are cached.
     */
    @Override
    public ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException {

	if (appsByIdCache.containsKey(id)) {
	    Application app = appsByIdCache.get(id);
	    ApplicationPojo appPojo = toPojo(app);
	    return appPojo;
	}
	ApplicationIdToken appToken = new ApplicationIdToken();
	appToken.setId(id);
	Application app = null;
	try {
	    app = ccApiWrapper.getApplicationApi().getApplication(appToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting application with ID " + id + ": "
			    + e.getMessage());
	}
	NameVersion nameVersion = new NameVersion(app.getName(),
		app.getVersion());
	addAppToCache(nameVersion, app);

	return toPojo(app);
    }

    /**
     * Get an application's requests, by application ID.
     *
     * Request lists are cached by application ID.
     */
    @Override
    public List<RequestPojo> getRequestsByAppId(String appId)
	    throws CommonFrameworkException {

	// Check cache first
	if (requestListsByAppIdCache.containsKey(appId)) {
	    return createRequestPojoList(appId,
		    requestListsByAppIdCache.get(appId));
	}

	ApplicationIdToken appToken = new ApplicationIdToken();
	appToken.setId(appId);

	RequestPageFilter pageFilter = new RequestPageFilter();
	pageFilter.setFirstRowIndex(0);
	pageFilter.setLastRowIndex(Integer.MAX_VALUE);
	List<RequestSummary> requestSummaries;
	try {
	    requestSummaries = ccApiWrapper.getApplicationApi()
		    .searchApplicationRequests(appToken, null, pageFilter);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error fetching requests for application ID " + appId
			    + ": " + e.getMessage());
	}

	// Cache the request list for this appId
	requestListsByAppIdCache.put(appId, requestSummaries);

	// Convert from sdk request list to pojo request list
	List<RequestPojo> requests = createRequestPojoList(appId,
		requestSummaries);

	return requests;
    }

    @Override
    public <T extends CodeCenterComponentPojo> List<T> getComponentsByAppId(
	    Class<T> pojoClass, String appId,
	    List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
	    throws CommonFrameworkException {

	List<T> allLevelComponents = collectComponents(pojoClass, appId,
		limitToApprovalStatusValues, recursive);
	return allLevelComponents;
    }

    // Private methods

    private <T extends CodeCenterComponentPojo> List<T> collectComponents(
	    Class<T> pojoClass, String appId,
	    List<ApprovalStatus> limitToApprovalStatusValues, boolean recursive)
	    throws CommonFrameworkException {
	List<RequestPojo> requests = getRequestsByAppId(appId);
	List<T> thisLevelComponents;

	thisLevelComponents = compMgr.getComponentsForRequests(pojoClass,
		requests, limitToApprovalStatusValues);

	List<T> thisLevelAndBelowComponentsMinusApps = new ArrayList<>();
	for (T comp : thisLevelComponents) {
	    log.debug("Component: " + comp.getName() + " / "
		    + comp.getVersion());
	    if (recursive && (comp.getApplicationId() != null)
		    && (comp.getApplicationId().length() > 0)) {
		List<T> appCompsMinusApps = collectComponents(pojoClass,
			comp.getApplicationId(), limitToApprovalStatusValues,
			recursive);
		// thisLevelAndBelowComponentsMinusApps.addAll(appCompsMinusApps);
		T appComp = compMgr.instantiatePojo(pojoClass);
		appComp.setId(comp.getId());
		appComp.setName(comp.getName());
		appComp.setVersion(comp.getVersion());
		appComp.setApprovalStatus(comp.getApprovalStatus());
		appComp.setHomepage(comp.getHomepage());
		appComp.setIntendedAudiences(comp.getIntendedAudiences());
		appComp.setKbComponentId(comp.getKbComponentId());
		appComp.setKbReleaseId(comp.getKbReleaseId());
		appComp.setApplicationComponent(comp.isApplicationComponent());
		appComp.setApplicationId(comp.getApplicationId());
		appComp.setDeprecated(comp.isDeprecated());
		appComp.setAttributeValues(comp.getAttributeValues());
		appComp.setLicenses(comp.getLicenses());
		appComp.setSubComponents(appCompsMinusApps);

		thisLevelAndBelowComponentsMinusApps.add(appComp);
	    } else {
		thisLevelAndBelowComponentsMinusApps.add(comp);
	    }
	}

	return thisLevelAndBelowComponentsMinusApps;
    }

    private List<RequestPojo> createRequestPojoList(String appId,
	    List<RequestSummary> requestSummaries)
	    throws CommonFrameworkException {
	List<RequestPojo> requests = new ArrayList<>(requestSummaries.size());
	for (RequestSummary sdkRequest : requestSummaries) {
	    RequestPojo request = createRequestPojo(appId, sdkRequest);
	    requests.add(request);
	}
	return requests;
    }

    private RequestPojo createRequestPojo(String appId,
	    RequestSummary sdkRequest) throws CommonFrameworkException {
	RequestPojo request = new RequestPojo(sdkRequest.getId().getId(),
		appId, sdkRequest.getComponentId().getId(),
		ApprovalStatus.valueOf(sdkRequest.getApprovalStatus()),
		sdkRequest.getLicenseInfo().getId().getId());
	return request;
    }

}
