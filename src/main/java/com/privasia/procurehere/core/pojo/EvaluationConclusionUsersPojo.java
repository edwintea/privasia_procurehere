package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author pooja
 */
public class EvaluationConclusionUsersPojo implements Serializable {

	private static final long serialVersionUID = 705024913269604483L;

	private String userName;

	private String concludedDate;

	private String remark;

	private String fileName;

	private String fileDescription;

	private Integer userIndex;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the concludedDate
	 */
	public String getConcludedDate() {
		return concludedDate;
	}

	/**
	 * @param concludedDate the concludedDate to set
	 */
	public void setConcludedDate(String concludedDate) {
		this.concludedDate = concludedDate;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileDescription
	 */
	public String getFileDescription() {
		return fileDescription;
	}

	/**
	 * @param fileDescription the fileDescription to set
	 */
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	/**
	 * @return the userIndex
	 */
	public Integer getUserIndex() {
		return userIndex;
	}

	/**
	 * @param userIndex the userIndex to set
	 */
	public void setUserIndex(Integer userIndex) {
		this.userIndex = userIndex;
	}
}