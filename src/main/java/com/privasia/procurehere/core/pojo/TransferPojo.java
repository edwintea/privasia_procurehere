package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author yogesh
 */
public class TransferPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5720906896784036868L;

	private String awardRefranceNo;
	private TransferAwardPojo award;

	/**
	 * @return the awardRefranceNo
	 */
	public String getAwardRefranceNo() {
		return awardRefranceNo;
	}

	/**
	 * @param awardRefranceNo the awardRefranceNo to set
	 */
	public void setAwardRefranceNo(String awardRefranceNo) {
		this.awardRefranceNo = awardRefranceNo;
	}

	/**
	 * @return the award
	 */
	public TransferAwardPojo getAward() {
		return award;
	}

	/**
	 * @param award the award to set
	 */
	public void setAward(TransferAwardPojo award) {
		this.award = award;
	}

}
