/*******************************************************************************
 * Copyright (C) 2015 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *******************************************************************************/

package com.blackducksoftware.tools.commonframework.connector.protex.identification;

import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.Discovery;
import com.blackducksoftware.sdk.protex.project.codetree.discovery.StringSearchDiscovery;

public interface Identifier {

    /**
     * Make a Code Match or Declared Identification on the given file.
     *
     * @param path
     * @param target
     * @throws SdkFault
     */
    void makeIdentificationOnFile(String path, Discovery target)
	    throws SdkFault;

    /**
     * Make a String Search Identification on the given file.
     *
     * @param path
     * @param target
     * @param componentId
     * @param componentVersionId
     * @throws SdkFault
     */
    void makeStringSearchIdentificationOnFile(String path,
	    StringSearchDiscovery target, String componentId,
	    String componentVersionId) throws SdkFault;

    /**
     * Returns true if the BOM should be refreshed after the ID is made.
     *
     * @return
     */
    boolean isFinalBomRefreshRequired();

    /**
     * Returns true if Identification strategy being used requires multiple
     * passes (until # pendingIds == 0)
     *
     * @return
     */
    boolean isMultiPassIdStrategy();
}
