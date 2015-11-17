package com.blackducksoftware.tools.connector.codecenter.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.tools.connector.codecenter.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.AttributeValues;

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
    private final Map<String, String> attributeValuesByName = new HashMap<>();
    private final ApprovalStatus approvalStatus;

    public ApplicationPojo(String id, String name, String version,
	    List<AttributeValuePojo> attributeValues,
	    ApprovalStatus approvalStatus) {
	this.id = id;
	this.name = name;
	this.version = version;

	AttributeValues.addAttributeValuesToMap(attributeValuesByName,
		attributeValues);

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

    public String getAttributeByName(String name) {
	return attributeValuesByName.get(name);
    }

    public ApprovalStatus getApprovalStatus() {
	return approvalStatus;
    }

    @Override
    public String toString() {
	return "ApplicationPojo [name=" + name + ", version=" + version + "]";
    }

}
