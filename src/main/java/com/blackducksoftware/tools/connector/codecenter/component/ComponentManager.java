package com.blackducksoftware.tools.connector.codecenter.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.application.data.ApplicationIdToken;
import com.blackducksoftware.sdk.codecenter.cola.ColaApi;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachment;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachmentCreate;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentAttachmentToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentNameVersionToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentIdToken;
import com.blackducksoftware.sdk.codecenter.cola.data.KbComponentReleaseIdToken;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentContent;
import com.blackducksoftware.sdk.codecenter.common.data.AttachmentPageFilter;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterAPIWrapper;
import com.blackducksoftware.tools.connector.codecenter.attribute.IAttributeDefinitionManager;
import com.blackducksoftware.tools.connector.codecenter.common.ApprovalStatus;
import com.blackducksoftware.tools.connector.codecenter.common.AttachmentDetails;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValuePojo;
import com.blackducksoftware.tools.connector.codecenter.common.AttributeValues;
import com.blackducksoftware.tools.connector.codecenter.common.CodeCenterComponentPojo;
import com.blackducksoftware.tools.connector.codecenter.common.NameVersion;
import com.blackducksoftware.tools.connector.codecenter.common.RequestPojo;
import com.blackducksoftware.tools.connector.common.ILicenseManager;
import com.blackducksoftware.tools.connector.common.LicensePojo;
import com.blackducksoftware.tools.connector.common.Licenses;

public class ComponentManager implements ICodeCenterComponentManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
	    .getName());

    private final CodeCenterAPIWrapper codeCenterApiWrapper;
    private final IAttributeDefinitionManager attrDefMgr;
    private final ILicenseManager<LicensePojo> licenseManager;
    private final Map<NameVersion, Component> componentsByNameVersionCache = new HashMap<>();
    private final Map<String, Component> componentsByIdCache = new HashMap<>();

    public ComponentManager(CodeCenterAPIWrapper codeCenterApiWrapper,
	    IAttributeDefinitionManager attrDefMgr,
	    ILicenseManager<LicensePojo> licenseManager) {
	this.codeCenterApiWrapper = codeCenterApiWrapper;
	this.attrDefMgr = attrDefMgr;
	this.licenseManager = licenseManager;
    }

    /**
     * Get a component by its ID.
     *
     * Components fetched are cached.
     */
    @Override
    public CodeCenterComponentPojo getComponentById(String componentId)
	    throws CommonFrameworkException {

	Component sdkComp = getSdkComponentById(componentId);

	return createPojo(sdkComp);
    }

    /**
     * Get a component by its name/version.
     *
     * Components fetched are cached.
     */
    @Override
    public CodeCenterComponentPojo getComponentByNameVersion(
	    String componentName, String componentVersion)
	    throws CommonFrameworkException {
	NameVersion nameVersion = new NameVersion(componentName,
		componentVersion);

	// Check cache first
	if (componentsByNameVersionCache.containsKey(nameVersion)) {
	    return createPojo(componentsByNameVersionCache.get(nameVersion));
	}

	ComponentNameVersionToken componentNameVersionToken = new ComponentNameVersionToken();
	componentNameVersionToken.setName(componentName);
	componentNameVersionToken.setVersion(componentVersion);
	ColaApi colaApi = codeCenterApiWrapper.getColaApi();
	Component sdkComp;
	try {
	    sdkComp = colaApi.getCatalogComponent(componentNameVersionToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error getting component "
		    + componentName + " / " + componentVersion + ": "
		    + e.getMessage());
	}

	// Add to caches
	addToCache(nameVersion, sdkComp);

	return createPojo(sdkComp);
    }

    /**
     * Get a list of components that correspond to the given list of requests.
     *
     */
    @Override
    public List<CodeCenterComponentPojo> getComponentsForRequests(
	    List<RequestPojo> requests) throws CommonFrameworkException {

	List<CodeCenterComponentPojo> components = new ArrayList<>(
		requests.size());
	for (RequestPojo request : requests) {
	    CodeCenterComponentPojo comp = getComponentById(request
		    .getComponentId());
	    components.add(comp);
	}

	return components;
    }

    @Override
    public List<CodeCenterComponentPojo> getComponentsForRequests(
	    List<RequestPojo> requests,
	    List<ApprovalStatus> limitToApprovalStatusValues)
	    throws CommonFrameworkException {

	List<CodeCenterComponentPojo> components = new ArrayList<>(
		requests.size());
	for (RequestPojo request : requests) {
	    CodeCenterComponentPojo comp = getComponentById(request
		    .getComponentId());
	    if ((limitToApprovalStatusValues == null)
		    || (limitToApprovalStatusValues.size() == 0)
		    || (limitToApprovalStatusValues.contains(comp
			    .getApprovalStatus()))) {
		components.add(comp);
	    }
	}

	return components;
    }

    /**
     * Get attachment details for a given set of attachments from the given
     * Component.
     *
     * @param componentId
     * @param searchString
     * @return
     * @throws CommonFrameworkException
     */
    @Override
    public List<AttachmentDetails> searchAttachments(String componentId,
	    String searchString) throws CommonFrameworkException {

	Component comp = getSdkComponentById(componentId);

	AttachmentPageFilter pageFilter = new AttachmentPageFilter();
	pageFilter.setFirstRowIndex(0);
	pageFilter.setLastRowIndex(Integer.MAX_VALUE);
	List<ComponentAttachment> sdkAttachments;
	try {
	    sdkAttachments = codeCenterApiWrapper.getColaApi()
		    .searchComponentAttachments("", pageFilter, comp.getId());
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error searching applications for component ID "
			    + componentId + ": " + e.getMessage());
	}

	List<AttachmentDetails> attachmentDetailsList = new ArrayList<>(
		sdkAttachments.size());
	for (ComponentAttachment sdkAttachment : sdkAttachments) {
	    AttachmentDetails attachmentDetails = createAttachmentDetails(sdkAttachment);
	    attachmentDetailsList.add(attachmentDetails);
	}

	return attachmentDetailsList;
    }

    /**
     * Download a Component attachment to the given directory.
     *
     * @param attachmentId
     * @param targetDirPath
     * @throws CommonFrameworkException
     */
    @Override
    public void downloadAttachment(String componentId, String filename,
	    String targetDirPath) throws CommonFrameworkException {
	ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
	ComponentIdToken compToken = new ComponentIdToken();
	compToken.setId(componentId);
	attachmentToken.setComponentId(compToken);
	attachmentToken.setFileName(filename);
	AttachmentContent content;
	try {
	    content = codeCenterApiWrapper.getColaApi()
		    .getComponentAttachmentContent(attachmentToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting data handler for component ID "
			    + componentId + " attachment " + filename + ": "
			    + e.getMessage());
	}
	DataHandler dataHandler = content.getAttachmentContent();
	downloadAttachment(componentId, filename, targetDirPath, dataHandler);
    }

    /**
     * Attach the named file to the given Component.
     *
     * @param componentId
     * @param sourceFilePath
     * @param description
     * @throws CommonFrameworkException
     */
    @Override
    public void attachFile(String componentId, String sourceFilePath,
	    String description) throws CommonFrameworkException {

	File file = new File(sourceFilePath);

	Component comp = getSdkComponentById(componentId);
	ComponentAttachmentCreate attachmentCreateBean = new ComponentAttachmentCreate();
	attachmentCreateBean.setComponentId(comp.getId());
	attachmentCreateBean.setFileName(file.getName());
	attachmentCreateBean.setDescription(description);
	attachmentCreateBean.setName(file.getName());

	URL url = null;
	try {
	    url = new File(sourceFilePath).toURI().toURL();
	} catch (MalformedURLException mue) {
	    throw new CommonFrameworkException(mue.getMessage());
	}

	DataHandler dataHandler = new DataHandler(url);
	attachmentCreateBean.setAttachmentContent(dataHandler);
	try {

	    codeCenterApiWrapper.getColaApi().createComponentAttachment(
		    attachmentCreateBean);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error creating component attachment on component ID "
			    + componentId + " from file " + url.toString()
			    + ": " + e.getMessage());
	}

    }

    /**
     * Delete the given attachment from the given component.
     *
     * @param attachmentId
     */
    @Override
    public void deleteAttachment(String componentId, String filename)
	    throws CommonFrameworkException {
	ComponentIdToken componentIdToken = new ComponentIdToken();
	componentIdToken.setId(componentId);

	ComponentAttachmentToken attachmentToken = new ComponentAttachmentToken();
	attachmentToken.setComponentId(componentIdToken);
	attachmentToken.setFileName(filename);
	try {
	    codeCenterApiWrapper.getColaApi().deleteComponentAttachment(
		    attachmentToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException("Error deleting file "
		    + filename + " from component ID " + componentId + ": "
		    + e.getMessage());
	}

    }

    // Private methods

    private Component getSdkComponentById(String componentId)
	    throws CommonFrameworkException {
	// Check cache first
	if (componentsByIdCache.containsKey(componentId)) {
	    return componentsByIdCache.get(componentId);
	}

	ComponentIdToken componentIdToken = new ComponentIdToken();
	componentIdToken.setId(componentId);
	ColaApi colaApi = codeCenterApiWrapper.getColaApi();
	Component sdkComp;
	try {
	    sdkComp = colaApi.getCatalogComponent(componentIdToken);
	} catch (SdkFault e) {
	    throw new CommonFrameworkException(
		    "Error getting component for ID " + componentId + ": "
			    + e.getMessage());
	}

	// Add to caches
	NameVersion nameVersion = new NameVersion(sdkComp.getName(),
		sdkComp.getVersion());
	addToCache(nameVersion, sdkComp);

	return sdkComp;
    }

    private CodeCenterComponentPojo createPojo(Component sdkComp)
	    throws CommonFrameworkException {
	List<AttributeValue> sdkAttrValues = sdkComp.getAttributeValues();
	List<AttributeValuePojo> attrValues = AttributeValues.valueOf(
		attrDefMgr, sdkAttrValues);

	String appId = getAppId(sdkComp);
	String kbComponentId = getKbComponentId(sdkComp);
	String kbComponentReleaseId = getKbComponentReleaseId(sdkComp);

	List<LicensePojo> licenses = Licenses.valueOf(licenseManager,
		sdkComp.getDeclaredLicenses());
	log.info("Component: " + sdkComp.getName() + " / "
		+ sdkComp.getVersion() + "; Approval: "
		+ sdkComp.getApprovalStatus());

	// sdkComp.isApplicationComponent() always returns false,
	// so set flag in pojo based on whether or not
	// applicationId has a value
	boolean applicationComponent = sdkComp.getApplicationId() != null;
	CodeCenterComponentPojo comp = new CodeCenterComponentPojo(sdkComp
		.getId().getId(), sdkComp.getName(), sdkComp.getVersion(),
		ApprovalStatus.valueOf(sdkComp.getApprovalStatus()),
		sdkComp.getHomepage(), sdkComp.getIntendedAudiences(),
		kbComponentId, kbComponentReleaseId, applicationComponent,
		appId, sdkComp.isDeprecated(), attrValues, licenses, null);
	return comp;
    }

    private String getKbComponentReleaseId(Component sdkComp) {
	KbComponentReleaseIdToken kbCompIdToken = sdkComp.getKbReleaseId();
	String kbComponentReleaseId;
	if (kbCompIdToken == null) {
	    kbComponentReleaseId = null;
	} else {
	    kbComponentReleaseId = kbCompIdToken.getId();
	}
	return kbComponentReleaseId;
    }

    private String getKbComponentId(Component sdkComp) {
	KbComponentIdToken kbCompIdToken = sdkComp.getKbComponentId();
	String kbComponentId;
	if (kbCompIdToken == null) {
	    kbComponentId = null;
	} else {
	    kbComponentId = kbCompIdToken.getId();
	}
	return kbComponentId;
    }

    private String getAppId(Component sdkComp) {
	ApplicationIdToken appIdToken = sdkComp.getApplicationId();
	String appId;
	if (appIdToken == null) {
	    appId = null;
	} else {
	    appId = appIdToken.getId();
	}
	return appId;
    }

    private AttachmentDetails createAttachmentDetails(
	    ComponentAttachment sdkAttachment) {
	AttachmentDetails attachmentDetails = new AttachmentDetails(
		sdkAttachment.getId(), sdkAttachment.getFileName(),
		sdkAttachment.getDescription(),
		sdkAttachment.getTimeUploaded(), sdkAttachment
			.getUserUploaded().getId(),
		sdkAttachment.getContentType(),
		sdkAttachment.getFilesizeBytes());
	return attachmentDetails;
    }

    private void addToCache(NameVersion nameVersion, Component sdkComp) {
	componentsByIdCache.put(sdkComp.getId().getId(), sdkComp);
	componentsByNameVersionCache.put(nameVersion, sdkComp);
    }

    private void downloadAttachment(String componentId, String filename,
	    String targetDirPath, DataHandler dataHandler)
	    throws CommonFrameworkException {
	InputStream attachmentData = null;
	OutputStream fileOutputStream = null;

	try {

	    try {
		attachmentData = dataHandler.getInputStream();
	    } catch (IOException e) {
		throw new CommonFrameworkException(
			"Error getting input stream for attachment " + filename
				+ " on component ID " + componentId + ": "
				+ e.getMessage());
	    }

	    File outputFile = new File(targetDirPath + "/" + filename);

	    try {
		fileOutputStream = new FileOutputStream(outputFile);
	    } catch (FileNotFoundException e) {
		throw new CommonFrameworkException(
			"Error getting output stream for attachment "
				+ filename + " on component ID " + componentId
				+ ": " + e.getMessage());
	    }

	    try {
		IOUtils.copy(attachmentData, fileOutputStream);
	    } catch (IOException e) {
		throw new CommonFrameworkException(
			"Error downloading data for attachment " + filename
				+ " on component ID " + componentId + ": "
				+ e.getMessage());
	    }

	} finally {
	    if (attachmentData != null) {
		try {
		    attachmentData.close();
		} catch (IOException e) {
		}
	    }
	    if (fileOutputStream != null) {
		try {
		    fileOutputStream.close();
		} catch (IOException e) {
		}
	    }
	}
    }
}
