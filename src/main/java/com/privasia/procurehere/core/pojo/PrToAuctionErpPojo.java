package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author parveen
 */
public class PrToAuctionErpPojo implements Serializable {

	private static final long serialVersionUID = 8457828183553043056L;

	private String appId;

	private String id;
	@JsonProperty(value = "erpRefNo")
	private String prNo;

	private String prReqId;

	private String itemName;

	private String prReqName;

	private String itmCat;

	private String addy1;

	private String strt2;

	private String strt3;

	private String strtHseNo;

	private String strt4;

	private String postCode;

	private String city;

	private String cityKey;

	private String curr;

	@JsonProperty(value = "itemList")
	private List<PrToAuctionDetailsErpPojo> auctionDetails;

	private String field1Label;

	private String field2Label;

	private String field3Label;

	private String field4Label;

	private String field5Label;

	private String field6Label;

	private String field7Label;

	private String field8Label;

	private String field9Label;

	private String field10Label;

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the prNo
	 */
	public String getPrNo() {
		return prNo;
	}

	/**
	 * @param prNo the prNo to set
	 */
	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}

	/**
	 * @return the prReqId
	 */
	public String getPrReqId() {
		return prReqId;
	}

	/**
	 * @param prReqId the prReqId to set
	 */
	public void setPrReqId(String prReqId) {
		this.prReqId = prReqId;
	}

	/**
	 * @return the prReqName
	 */
	public String getPrReqName() {
		return prReqName;
	}

	/**
	 * @param prReqName the prReqName to set
	 */
	public void setPrReqName(String prReqName) {
		this.prReqName = prReqName;
	}

	/**
	 * @return the itmCat
	 */
	public String getItmCat() {
		return itmCat;
	}

	/**
	 * @param itmCat the itmCat to set
	 */
	public void setItmCat(String itmCat) {
		this.itmCat = itmCat;
	}

	/**
	 * @return the addy1
	 */
	public String getAddy1() {
		return addy1;
	}

	/**
	 * @param addy1 the addy1 to set
	 */
	public void setAddy1(String addy1) {
		this.addy1 = addy1;
	}

	/**
	 * @return the strt2
	 */
	public String getStrt2() {
		return strt2;
	}

	/**
	 * @param strt2 the strt2 to set
	 */
	public void setStrt2(String strt2) {
		this.strt2 = strt2;
	}

	/**
	 * @return the strt3
	 */
	public String getStrt3() {
		return strt3;
	}

	/**
	 * @param strt3 the strt3 to set
	 */
	public void setStrt3(String strt3) {
		this.strt3 = strt3;
	}

	/**
	 * @return the strtHseNo
	 */
	public String getStrtHseNo() {
		return strtHseNo;
	}

	/**
	 * @param strtHseNo the strtHseNo to set
	 */
	public void setStrtHseNo(String strtHseNo) {
		this.strtHseNo = strtHseNo;
	}

	/**
	 * @return the strt4
	 */
	public String getStrt4() {
		return strt4;
	}

	/**
	 * @param strt4 the strt4 to set
	 */
	public void setStrt4(String strt4) {
		this.strt4 = strt4;
	}

	/**
	 * @return the postCode
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * @param postCode the postCode to set
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the cityKey
	 */
	public String getCityKey() {
		return cityKey;
	}

	/**
	 * @param cityKey the cityKey to set
	 */
	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	/**
	 * @return the curr
	 */
	public String getCurr() {
		return curr;
	}

	/**
	 * @param curr the curr to set
	 */
	public void setCurr(String curr) {
		this.curr = curr;
	}

	/**
	 * @return the auctionDetails
	 */
	public List<PrToAuctionDetailsErpPojo> getAuctionDetails() {
		return auctionDetails;
	}

	/**
	 * @param auctionDetails the auctionDetails to set
	 */
	public void setAuctionDetails(List<PrToAuctionDetailsErpPojo> auctionDetails) {
		this.auctionDetails = auctionDetails;
	}

	/**
	 * @return the field1Label
	 */
	public String getField1Label() {
		return field1Label;
	}

	/**
	 * @param field1Label the field1Label to set
	 */
	public void setField1Label(String field1Label) {
		this.field1Label = field1Label;
	}

	/**
	 * @return the field2Label
	 */
	public String getField2Label() {
		return field2Label;
	}

	/**
	 * @param field2Label the field2Label to set
	 */
	public void setField2Label(String field2Label) {
		this.field2Label = field2Label;
	}

	/**
	 * @return the field3Label
	 */
	public String getField3Label() {
		return field3Label;
	}

	/**
	 * @param field3Label the field3Label to set
	 */
	public void setField3Label(String field3Label) {
		this.field3Label = field3Label;
	}

	/**
	 * @return the field4Label
	 */
	public String getField4Label() {
		return field4Label;
	}

	/**
	 * @param field4Label the field4Label to set
	 */
	public void setField4Label(String field4Label) {
		this.field4Label = field4Label;
	}

	/**
	 * @return the field5Label
	 */
	public String getField5Label() {
		return field5Label;
	}

	/**
	 * @param field5Label the field5Label to set
	 */
	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	/**
	 * @return the field6Label
	 */
	public String getField6Label() {
		return field6Label;
	}

	/**
	 * @param field6Label the field6Label to set
	 */
	public void setField6Label(String field6Label) {
		this.field6Label = field6Label;
	}

	/**
	 * @return the field7Label
	 */
	public String getField7Label() {
		return field7Label;
	}

	/**
	 * @param field7Label the field7Label to set
	 */
	public void setField7Label(String field7Label) {
		this.field7Label = field7Label;
	}

	/**
	 * @return the field8Label
	 */
	public String getField8Label() {
		return field8Label;
	}

	/**
	 * @param field8Label the field8Label to set
	 */
	public void setField8Label(String field8Label) {
		this.field8Label = field8Label;
	}

	/**
	 * @return the field9Label
	 */
	public String getField9Label() {
		return field9Label;
	}

	/**
	 * @param field9Label the field9Label to set
	 */
	public void setField9Label(String field9Label) {
		this.field9Label = field9Label;
	}

	/**
	 * @return the field10Label
	 */
	public String getField10Label() {
		return field10Label;
	}

	/**
	 * @param field10Label the field10Label to set
	 */
	public void setField10Label(String field10Label) {
		this.field10Label = field10Label;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}
