package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Priyanka Ghadage
 */
public class AwardResponsePojo implements Serializable {

	private static final long serialVersionUID = -5671703852447911777L;

	private String success;

	private String error;

	private List<AwardReferenceNumberPojo> refNumList;

	public AwardResponsePojo() {
	}

	/**
	 * @return the success
	 */
	public String getSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(String success) {
		this.success = success;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the refNumList
	 */
	public List<AwardReferenceNumberPojo> getRefNumList() {
		return refNumList;
	}

	/**
	 * @param refNumList the refNumList to set
	 */
	public void setRefNumList(List<AwardReferenceNumberPojo> refNumList) {
		this.refNumList = refNumList;
	}

}
