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
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.ApprovalStatusEnum;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.request.data.RequestPageFilter;
import com.blackducksoftware.sdk.codecenter.request.data.RequestSummary;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.attribute.AttributeDefinitionPojo;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;

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
    private final Map<NameVersion, Application> appsByNameVersionCache = new HashMap<>();
    private final Map<String, Application> appsByIdCache = new HashMap<>();
    private final Map<String, List<RequestSummary>> requestListsByAppIdCache = new HashMap<>();

    public ApplicationManager(CodeCenterAPIWrapper ccApiWrapper,
	    IAttributeDefinitionManager attrDefMgr) {
	this.ccApiWrapper = ccApiWrapper;
	this.attrDefMgr = attrDefMgr;
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
	    return new ApplicationPojo(app.getId().getId(), name, version,
		    toPojos(app.getAttributeValues()),
		    toPojo(app.getApprovalStatus()));
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

	return new ApplicationPojo(app.getId().getId(), name, version,
		toPojos(app.getAttributeValues()),
		toPojo(app.getApprovalStatus()));
    }

    // Moved to ApprovalStatus; delete this:
    private ApprovalStatus toPojo(ApprovalStatusEnum ccApprovalStatus)
	    throws CommonFrameworkException {
	switch (ccApprovalStatus) {
	case ALL:
	    return ApprovalStatus.ALL;
	case APPEALED:
	    return ApprovalStatus.APPEALED;
	case APPROVED:
	    return ApprovalStatus.APPROVED;
	case CANCELED:
	    return ApprovalStatus.CANCELLED;
	case DEFERRED:
	    return ApprovalStatus.DEFERRED;
	case MOREINFO:
	    return ApprovalStatus.MORE_INFO;
	case NOTSUBMITTED:
	    return ApprovalStatus.NOT_SUBMITTED;
	case PENDING:
	    return ApprovalStatus.PENDING;
	case REJECTED:
	    return ApprovalStatus.REJECTED;
	default:
	    throw new CommonFrameworkException("Unsupported ApprovalStatus: "
		    + ccApprovalStatus);
	}
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
	    return new ApplicationPojo(id, app.getName(), app.getVersion(),
		    toPojos(app.getAttributeValues()),
		    toPojo(app.getApprovalStatus()));
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

	List<AttributeValuePojo> attrValuePojos = toPojos(app
		.getAttributeValues());

	return new ApplicationPojo(app.getId().getId(), app.getName(),
		app.getVersion(), attrValuePojos,
		toPojo(app.getApprovalStatus()));
    }

    /**
     * Convert a list of attribute values (SDK objects) to POJOs.
     *
     * @param attrValues
     * @return
     * @throws CommonFrameworkException
     */
    private List<AttributeValuePojo> toPojos(List<AttributeValue> attrValues)
	    throws CommonFrameworkException {
	List<AttributeValuePojo> pojos = new ArrayList<>();
	for (AttributeValue attrValue : attrValues) {
	    String attrId = getAttributeId(attrValue);
	    AttributeDefinitionPojo attrDefPojo = attrDefMgr
		    .getAttributeDefinitionById(attrId);
	    String attrName = attrDefPojo.getName();

	    String value = null;
	    List<String> valueList = attrValue.getValues();
	    if (valueList.size() > 1) {
		log.warn(attrName
			+ " has multiple values, which is not supported; using the first value");
	    }
	    if ((valueList != null) && (valueList.size() > 0)) {
		value = attrValue.getValues().get(0);
	    }
	    AttributeValuePojo pojo = new AttributeValuePojo(attrId, attrName,
		    value);
	    pojos.add(pojo);
	}
	return pojos;
    }

    private String getAttributeId(AttributeValue attrValue) {
	AttributeIdToken attrIdToken = (AttributeIdToken) attrValue
		.getAttributeId();
	String attrId = attrIdToken.getId();
	return attrId;
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
		toPojo(sdkRequest.getApprovalStatus()), sdkRequest
			.getLicenseInfo().getId().getId());
	return request;
    }
}