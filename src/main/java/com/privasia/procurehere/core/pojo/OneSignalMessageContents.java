/**
 * 
 */

package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nitin Otageri
 */
@JsonInclude(Include.NON_NULL)
public class OneSignalMessageContents implements Serializable {

	private static final long serialVersionUID = -1822279407092778296L;

	private String en;

	@JsonProperty(value = "zh-Hans")
	private String ch;

	public OneSignalMessageContents(String alert) {
		en = alert;
	}

	/**
	 * @return the en
	 */
	public String getEn() {
		return en;
	}

	/**
	 * @param en the en to set
	 */
	public void setEn(String en) {
		this.en = en;
	}

	/**
	 * @return the ch
	 */
	public String getCh() {
		return ch;
	}

	/**
	 * @param ch the ch to set
	 */
	public void setCh(String ch) {
		this.ch = ch;
	}

}
