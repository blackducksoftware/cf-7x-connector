package com.blackducksoftware.tools.connector.protex.report;

import javax.activation.DataHandler;

public class ReportPojo {
    private final DataHandler fileContent;
    private final String filename;

    public ReportPojo(DataHandler fileContent, String filename) {
	this.fileContent = fileContent;
	this.filename = filename;
    }

    public DataHandler getFileContent() {
	return fileContent;
    }

    public String getFilename() {
	return filename;
    }

    @Override
    public String toString() {
	return "ReportPojo [filename=" + filename + "]";
    }

}
