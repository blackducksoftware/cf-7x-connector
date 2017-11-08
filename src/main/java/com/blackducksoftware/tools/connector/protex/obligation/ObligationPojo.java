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
package com.blackducksoftware.tools.connector.protex.obligation;

public class ObligationPojo {
    private final String id;
    private final String name;
    private final String description;
    private final String obligationCategoryId;
    private final String obligationCategoryName;

    public ObligationPojo(String id, String name, String description,
	    String obligationCategoryId, String obligationCategoryName) {
	this.id = id;
	this.name = name;
	this.description = description;
	this.obligationCategoryId = obligationCategoryId;
	this.obligationCategoryName = obligationCategoryName;
    }

    public String getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }

    public String getObligationCategoryId() {
	return obligationCategoryId;
    }

    public String getObligationCategoryName() {
	return obligationCategoryName;
    }

    @Override
    public String toString() {
	return "ObligationPojo [id=" + id + ", name=" + name + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	ObligationPojo other = (ObligationPojo) obj;
	if (id == null) {
	    if (other.id != null) {
		return false;
	    }
	} else if (!id.equals(other.id)) {
	    return false;
	}
	return true;
    }

}
