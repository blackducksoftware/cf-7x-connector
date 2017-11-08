/**
 * CommonFramework 7.x Connector
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.tools.connector.codecenter.attribute;

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
