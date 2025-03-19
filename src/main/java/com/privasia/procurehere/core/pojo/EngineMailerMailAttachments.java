/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ravi
 */
public class EngineMailerMailAttachments implements Serializable {

	private static final long serialVersionUID = -437950027653411502L;

	@JsonProperty("Filename")
	private String filename;

	@JsonProperty("Content")
	private String content;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
