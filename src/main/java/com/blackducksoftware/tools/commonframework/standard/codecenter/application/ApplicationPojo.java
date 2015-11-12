package com.blackducksoftware.tools.commonframework.standard.codecenter.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationPojo {
    private final String id;
    private final String name;
    private final String version;
    private final Map<String, String> attributeValuesByName = new HashMap<>();

    public ApplicationPojo(String id, String name, String version,
	    List<AttributeValuePojo> attributeValues) {
	this.id = id;
	this.name = name;
	this.version = version;

	addAttributeValuesToMap(attributeValues);
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

    private void addAttributeValuesToMap(
	    List<AttributeValuePojo> attributeValues) {
	for (AttributeValuePojo attrValue : attributeValues) {
	    attributeValuesByName
		    .put(attrValue.getName(), attrValue.getValue());
	}
    }
}
