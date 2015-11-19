package com.blackducksoftware.tools.connector.common;

import java.net.MalformedURLException;
import java.net.URL;

import com.blackducksoftware.tools.commonframework.core.exception.CommonFrameworkException;

/**
 * Utility methods for parsing filepaths.
 *
 * TODO: There may be some open source library that does this. If not, this
 * should move to common framework base.
 *
 * @author sbillings
 *
 */
public class FilePaths {

    public static String normalizeSlashes(String filePath) {
	return filePath.replaceAll("\\\\", "/");
    }

    public static String getFilename(String filePath) {
	filePath = FilePaths.normalizeSlashes(filePath);
	if (!filePath.contains("/")) {
	    return filePath;
	}

	int firstSlashPosition;
	while ((firstSlashPosition = filePath.indexOf('/')) >= 0) {
	    filePath = filePath.substring(firstSlashPosition + 1);
	}
	return filePath;
    }

    public static URL createFilePathUrl(String filePath)
	    throws CommonFrameworkException {

	if (!filePath.startsWith("/")) {
	    filePath = System.getProperty("user.dir") + "/" + filePath;
	}
	if (filePath.substring(1, 2).equals(":")) {
	    filePath = filePath.substring(2);
	}
	URL url;
	try {
	    url = new URL("file://localhost" + filePath);
	} catch (MalformedURLException e) {
	    throw new CommonFrameworkException(
		    "Error creating URL from file path " + filePath + ": "
			    + e.getMessage());
	}
	return url;
    }

    public static String assemblePath(String dirPath, String filename) {
	return dirPath + "/" + filename;
    }

}
