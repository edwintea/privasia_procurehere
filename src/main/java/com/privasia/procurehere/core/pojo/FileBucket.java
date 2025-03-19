package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class FileBucket implements Serializable{
	
	private static final long serialVersionUID = -1626076761550546319L;
	
	MultipartFile file;
	String description;
	/**
	 * @return the file
	 */
	public MultipartFile getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
