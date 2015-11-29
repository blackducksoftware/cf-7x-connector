package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.common.ApprovalStatus;

/**
 * A non-SDK-specific class representing an application.
 *
 * TODO: The POJOs in this package and it's sister packages should be merged
 * with (into) the POJOs in commonframework in
 * com.blackducksoftware.tools.connector.codecenter.pojo. This application POJO
 * is functionally almost identical to the one in commonframework. The rest of
 * them are complementary (non-overlapping).
 *
 * @author sbillings
 *
 */
public class ApplicationPojo {
    private final String id;

    private final String name;

    private final String version;

    private final Map<String, AttributeValuePojo> attributeValuesByName = new HashMap<>();

    private final ApprovalStatus approvalStatus;

    public ApplicationPojo(String id, String name, String version,
	    List<AttributeValuePojo> attributeValues,
	    ApprovalStatus approvalStatus) {
	this.id = id;
	this.name = name;
	this.version = version;

	if (attributeValues != null) {
	    AttributeValues.addAttributeValuesToMap(attributeValuesByName,
		    attributeValues);
	}

	this.approvalStatus = approvalStatus;
    }

    public String getName() {
	return name;
    }

    public String getVersion() {
	return version;
    }

    public String getId() {
	return id;
    }

    public Map<String, AttributeValuePojo> getAttributeValuesByName() {
	return attributeValuesByName;
    }

    public String getAttributeByName(String name) {
	AttributeValuePojo attribute = attributeValuesByName.get(name);
	if (attribute != null) {
	    return attribute.getValue();
	}
	return null;
    }

    public ApprovalStatus getApprovalStatus() {
	return approvalStatus;
    }

    @Override
    public String toString() {
	return "ApplicationPojo [name=" + name + ", version=" + version + "]";
    }

}
