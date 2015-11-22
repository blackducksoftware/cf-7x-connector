package com.blackducksoftware.tools.connector.codecenter.component;

import java.util.List;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;

public interface ICodeCenterComponentManager {

    /**
     * Get a component by its component ID (not kbComponentId).
     *
     * @param componentId
     * @return
     * @throws CommonFrameworkException
     */
    CodeCenterComponentPojo getComponentById(String componentId)
	    throws CommonFrameworkException;

    /**
     * Get a component by its name/version.
     *
     * @param componentName
     * @param componentVersion
     * @return
     * @throws CommonFrameworkException
     */
    CodeCenterComponentPojo getComponentByNameVersion(String componentName,
	    String componentVersion) throws CommonFrameworkException;

    /**
     * Get the list of components named in a list of requests.
     *
     * @param requests
     * @return
     * @throws CommonFrameworkException
     */
    List<CodeCenterComponentPojo> getComponentsForRequests(List<RequestPojo> requests)
	    throws CommonFrameworkException;

    /**
     * Get the list of components named in a list of requests, limited to those
     * with one of the ApprovalStatus values in the provided list.
     *
     * @param requests
     * @param limitToApprovalStatusValues
     *            If not null and not empty, components included in the return
     *            value are limited to those with an ApprovalStatus that appears
     *            in the given list of ApprovalStatus values.
     * @return
     * @throws CommonFrameworkException
     */
    List<CodeCenterComponentPojo> getComponentsForRequests(List<RequestPojo> requests,
	    List<ApprovalStatus> limitToApprovalStatusValues)
	    throws CommonFrameworkException;

    /**
     * Get attachment details for a given set of attachments from the given
     * Component.
     *
     * @param componentId
     * @param searchString
     * @return
     * @throws CommonFrameworkException
     */
    List<AttachmentDetails> searchAttachments(String componentId,
	    String searchString) throws CommonFrameworkException;

    /**
     * Download a Component attachment to the given directory.
     *
     * @param attachmentId
     * @param targetDirPath
     * @throws CommonFrameworkException
     */
    void downloadAttachment(String componentId, String filename,
	    String targetDirPath) throws CommonFrameworkException;

    /**
     * Attach the named file to the given Component.
     *
     * @param componentId
     * @param sourceFilePath
     * @param description
     * @throws CommonFrameworkException
     */
    void attachFile(String componentId, String sourceFilePath,
	    String description) throws CommonFrameworkException;

    /**
     * Delete the given attachment from the given component.
     *
     * @param attachmentId
     */
    void deleteAttachment(String componentId, String filename)
	    throws CommonFrameworkException;

}
