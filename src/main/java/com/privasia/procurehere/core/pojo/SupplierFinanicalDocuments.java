package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class SupplierFinanicalDocuments implements Serializable {

	private static final long serialVersionUID = -8109623548085186418L;

	private String fileName;

	private String uploadDate;

	private String description;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}