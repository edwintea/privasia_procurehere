/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ravi
 */
public class EngineMailerResponse implements Serializable {

	private static final long serialVersionUID = -7286963888500863625L;

	@JsonProperty("Result")
	private EngineMailerResponseResult result;

	/**
	 * @return the result
	 */
	public EngineMailerResponseResult getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(EngineMailerResponseResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "EngineMailerResponse [result=" + result.toLogString() + "]";
	}

	
}
