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
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
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

    public ApplicationManager(CodeCenterAPIWrapper ccApiWrapper,
	    IAttributeDefinitionManager attrDefMgr) {
	this.ccApiWrapper = ccApiWrapper;
	this.attrDefMgr = attrDefMgr;
    }

    @Override
    public ApplicationPojo getApplicationByNameVersion(String name,
	    String version) throws CommonFrameworkException {
	NameVersion nameVersion = new NameVersion(name, version);
	if (appsByNameVersionCache.containsKey(nameVersion)) {
	    Application app = appsByNameVersionCache.get(nameVersion);
	    return new ApplicationPojo(app.getId().getId(), name, version,
		    toPojos(app.getAttributeValues()));
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
		toPojos(app.getAttributeValues()));
    }

    private void addAppToCache(NameVersion nameVersion, Application app) {
	appsByNameVersionCache.put(nameVersion, app);
	appsByIdCache.put(app.getId().getId(), app);
    }

    @Override
    public ApplicationPojo getApplicationById(String id)
	    throws CommonFrameworkException {

	if (appsByIdCache.containsKey(id)) {
	    Application app = appsByIdCache.get(id);
	    return new ApplicationPojo(id, app.getName(), app.getVersion(),
		    toPojos(app.getAttributeValues()));
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
		app.getVersion(), attrValuePojos);
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

    private class NameVersion {
	private final String name;
	private final String version;

	public NameVersion(String name, String version) {
	    this.name = name;
	    this.version = version;
	}

	protected String getName() {
	    return name;
	}

	protected String getVersion() {
	    return version;
	}

	@Override
	public boolean equals(Object otherObj) {
	    if (!(otherObj instanceof NameVersion)) {
		return false;
	    }
	    NameVersion otherNameVersion = (NameVersion) otherObj;
	    if (getName().equals(otherNameVersion.getName())
		    && getVersion().equals(otherNameVersion.getVersion())) {
		return true;
	    }
	    return false;
	}

	@Override
	public int hashCode() {
	    return (getName().hashCode() << 1) + getVersion().hashCode();
	}

    }
}
