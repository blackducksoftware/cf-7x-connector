package com.blackducksoftware.tools.commonframework.standard.codecenter.attribute;

public class AttributeDefinitionPojo {
    private final String id;
    private final String name;
    private final String attrType;
    private final String description;
    private final String question;

    public AttributeDefinitionPojo(String id, String name, String attrType,
	    String description, String question) {
	this.id = id;
	this.name = name;
	this.attrType = attrType;
	this.description = description;
	this.question = question;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getAttrType() {
	return attrType;
    }

    public String getDescription() {
	return description;
    }

    public String getQuestion() {
	return question;
    }

    @Override
    public String toString() {
	return "AttributeDefinitionPojo [name=" + name + ", attrType="
		+ attrType + ", description=" + description + ", question="
		+ question + "]";
    }

}
