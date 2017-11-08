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
package com.blackducksoftware.tools.connector.codecenter.common;

public class NameVersion {
    private final String name;

    private final String version;

    public NameVersion(String name, String version) {
        this.name = name;
        if (version == null) {
            this.version = "";
        } else {
            this.version = version;
        }
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "NameVersion [name=" + name + ", version=" + version + "]";
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
