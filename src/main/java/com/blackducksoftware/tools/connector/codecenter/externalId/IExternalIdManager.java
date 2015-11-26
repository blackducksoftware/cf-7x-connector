package com.blackducksoftware.tools.connector.codecenter.externalId;

import java.util.List;

import com.blackducksoftware.sdk.codecenter.externalid.data.ExternalIdInfo;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

public interface IExternalIdManager {

    /**
     * Gets any external Id information that this Code Center object may have
     * for the source specified.
     *
     * @param source
     *            String
     * @param objectId
     *            String
     * @return List<(ExternalIdInfo)>
     * @throws CommonFrameworkException
     */
    List<ExternalIdInfo> getExternalIdObjectById(String source, String objectId)
            throws CommonFrameworkException;
}
