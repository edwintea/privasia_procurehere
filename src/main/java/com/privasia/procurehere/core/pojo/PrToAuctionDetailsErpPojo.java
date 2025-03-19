package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author parveen
 */
public class PrToAuctionDetailsErpPojo implements Serializable {

	private static final long serialVersionUID = -7957261076420386620L;

	private String id;
	
	@JsonProperty(value="itemNo")
	private String itmNo;

	private String svcNo;

	private String matNo;
	@JsonProperty(value = "itemDesc")
	private String matDesc;

	private String matGrp;

	private String qtyStkUom;

	private String stkUom;

	private String qty;

	private String ordrUom;

	private String delDate;

	private String valPrc;

	private String extSubItm;

	private String extSvcNo;

	private String extSvcQty;

	private String extBaseUom;

	private String extRate;

	private String extPrcUnit;

	private String extValItm;

	private String extSvcLnTxt;

	private String purGrp;

	private String purGrpDesc;

	private String purGrpTel;

	private String purGrpFax;

	private String purOrg;

	private String purOrgDesc;

	private String reg;

	private String delInd;

	private String bismt;

	private String mfrpn;

	@JsonProperty(value = "itemCategory")
	private String field1;

	@JsonProperty(value = "materialNo")
	private String field2;

	@JsonProperty(value = "materialGroup")
	private String field3;

	@JsonProperty(value = "brandDesc")
	private String field4;

	@JsonProperty(value = "mfr_PartNO")
	private String field5;

	@JsonProperty(value = "purhaseGroup")
	private String field6;

	@JsonProperty(value = "deliveryDate")
	private String field7;

	private String field8;

	private String field9;

	private String field10;

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
	 * @return the itmNo
	 */
	public String getItmNo() {
		return itmNo;
	}

	/**
	 * @param itmNo the itmNo to set
	 */
	public void setItmNo(String itmNo) {
		this.itmNo = itmNo;
	}

	/**
	 * @return the svcNo
	 */
	public String getSvcNo() {
		return svcNo;
	}

	/**
	 * @param svcNo the svcNo to set
	 */
	public void setSvcNo(String svcNo) {
		this.svcNo = svcNo;
	}

	/**
	 * @return the matNo
	 */
	public String getMatNo() {
		return matNo;
	}

	/**
	 * @param matNo the matNo to set
	 */
	public void setMatNo(String matNo) {
		this.matNo = matNo;
	}

	/**
	 * @return the matDesc
	 */
	public String getMatDesc() {
		return matDesc;
	}

	/**
	 * @param matDesc the matDesc to set
	 */
	public void setMatDesc(String matDesc) {
		this.matDesc = matDesc;
	}

	/**
	 * @return the matGrp
	 */
	public String getMatGrp() {
		return matGrp;
	}

	/**
	 * @param matGrp the matGrp to set
	 */
	public void setMatGrp(String matGrp) {
		this.matGrp = matGrp;
	}

	/**
	 * @return the qtyStkUom
	 */
	public String getQtyStkUom() {
		return qtyStkUom;
	}

	/**
	 * @param qtyStkUom the qtyStkUom to set
	 */
	public void setQtyStkUom(String qtyStkUom) {
		this.qtyStkUom = qtyStkUom;
	}

	/**
	 * @return the stkUom
	 */
	public String getStkUom() {
		return stkUom;
	}

	/**
	 * @param stkUom the stkUom to set
	 */
	public void setStkUom(String stkUom) {
		this.stkUom = stkUom;
	}

	/**
	 * @return the qty
	 */
	public String getQty() {
		return qty;
	}

	/**
	 * @param qty the qty to set
	 */
	public void setQty(String qty) {
		this.qty = qty;
	}

	/**
	 * @return the ordrUom
	 */
	public String getOrdrUom() {
		return ordrUom;
	}

	/**
	 * @param ordrUom the ordrUom to set
	 */
	public void setOrdrUom(String ordrUom) {
		this.ordrUom = ordrUom;
	}

	/**
	 * @return the delDate
	 */
	public String getDelDate() {
		return delDate;
	}

	/**
	 * @param delDate the delDate to set
	 */
	public void setDelDate(String delDate) {
		this.delDate = delDate;
	}

	/**
	 * @return the valPrc
	 */
	public String getValPrc() {
		return valPrc;
	}

	/**
	 * @param valPrc the valPrc to set
	 */
	public void setValPrc(String valPrc) {
		this.valPrc = valPrc;
	}

	/**
	 * @return the extSubItm
	 */
	public String getExtSubItm() {
		return extSubItm;
	}

	/**
	 * @param extSubItm the extSubItm to set
	 */
	public void setExtSubItm(String extSubItm) {
		this.extSubItm = extSubItm;
	}

	/**
	 * @return the extSvcNo
	 */
	public String getExtSvcNo() {
		return extSvcNo;
	}

	/**
	 * @param extSvcNo the extSvcNo to set
	 */
	public void setExtSvcNo(String extSvcNo) {
		this.extSvcNo = extSvcNo;
	}

	/**
	 * @return the extSvcQty
	 */
	public String getExtSvcQty() {
		return extSvcQty;
	}

	/**
	 * @param extSvcQty the extSvcQty to set
	 */
	public void setExtSvcQty(String extSvcQty) {
		this.extSvcQty = extSvcQty;
	}

	/**
	 * @return the extBaseUom
	 */
	public String getExtBaseUom() {
		return extBaseUom;
	}

	/**
	 * @param extBaseUom the extBaseUom to set
	 */
	public void setExtBaseUom(String extBaseUom) {
		this.extBaseUom = extBaseUom;
	}

	/**
	 * @return the extRate
	 */
	public String getExtRate() {
		return extRate;
	}

	/**
	 * @param extRate the extRate to set
	 */
	public void setExtRate(String extRate) {
		this.extRate = extRate;
	}

	/**
	 * @return the extPrcUnit
	 */
	public String getExtPrcUnit() {
		return extPrcUnit;
	}

	/**
	 * @param extPrcUnit the extPrcUnit to set
	 */
	public void setExtPrcUnit(String extPrcUnit) {
		this.extPrcUnit = extPrcUnit;
	}

	/**
	 * @return the extValItm
	 */
	public String getExtValItm() {
		return extValItm;
	}

	/**
	 * @param extValItm the extValItm to set
	 */
	public void setExtValItm(String extValItm) {
		this.extValItm = extValItm;
	}

	/**
	 * @return the extSvcLnTxt
	 */
	public String getExtSvcLnTxt() {
		return extSvcLnTxt;
	}

	/**
	 * @param extSvcLnTxt the extSvcLnTxt to set
	 */
	public void setExtSvcLnTxt(String extSvcLnTxt) {
		this.extSvcLnTxt = extSvcLnTxt;
	}

	/**
	 * @return the purGrp
	 */
	public String getPurGrp() {
		return purGrp;
	}

	/**
	 * @param purGrp the purGrp to set
	 */
	public void setPurGrp(String purGrp) {
		this.purGrp = purGrp;
	}

	/**
	 * @return the purGrpDesc
	 */
	public String getPurGrpDesc() {
		return purGrpDesc;
	}

	/**
	 * @param purGrpDesc the purGrpDesc to set
	 */
	public void setPurGrpDesc(String purGrpDesc) {
		this.purGrpDesc = purGrpDesc;
	}

	/**
	 * @return the purGrpTel
	 */
	public String getPurGrpTel() {
		return purGrpTel;
	}

	/**
	 * @param purGrpTel the purGrpTel to set
	 */
	public void setPurGrpTel(String purGrpTel) {
		this.purGrpTel = purGrpTel;
	}

	/**
	 * @return the purGrpFax
	 */
	public String getPurGrpFax() {
		return purGrpFax;
	}

	/**
	 * @param purGrpFax the purGrpFax to set
	 */
	public void setPurGrpFax(String purGrpFax) {
		this.purGrpFax = purGrpFax;
	}

	/**
	 * @return the purOrg
	 */
	public String getPurOrg() {
		return purOrg;
	}

	/**
	 * @param purOrg the purOrg to set
	 */
	public void setPurOrg(String purOrg) {
		this.purOrg = purOrg;
	}

	/**
	 * @return the purOrgDesc
	 */
	public String getPurOrgDesc() {
		return purOrgDesc;
	}

	/**
	 * @param purOrgDesc the purOrgDesc to set
	 */
	public void setPurOrgDesc(String purOrgDesc) {
		this.purOrgDesc = purOrgDesc;
	}

	/**
	 * @return the reg
	 */
	public String getReg() {
		return reg;
	}

	/**
	 * @param reg the reg to set
	 */
	public void setReg(String reg) {
		this.reg = reg;
	}

	/**
	 * @return the delInd
	 */
	public String getDelInd() {
		return delInd;
	}

	/**
	 * @param delInd the delInd to set
	 */
	public void setDelInd(String delInd) {
		this.delInd = delInd;
	}

	/**
	 * @return the bismt
	 */
	public String getBismt() {
		return bismt;
	}

	/**
	 * @param bismt the bismt to set
	 */
	public void setBismt(String bismt) {
		this.bismt = bismt;
	}

	/**
	 * @return the mfrpn
	 */
	public String getMfrpn() {
		return mfrpn;
	}

	/**
	 * @param mfrpn the mfrpn to set
	 */
	public void setMfrpn(String mfrpn) {
		this.mfrpn = mfrpn;
	}

	/**
	 * @return the field1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1 the field1 to set
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * @return the field2
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @param field2 the field2 to set
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}

	/**
	 * @return the field3
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * @param field3 the field3 to set
	 */
	public void setField3(String field3) {
		this.field3 = field3;
	}

	/**
	 * @return the field4
	 */
	public String getField4() {
		return field4;
	}

	/**
	 * @param field4 the field4 to set
	 */
	public void setField4(String field4) {
		this.field4 = field4;
	}

	/**
	 * @return the field5
	 */
	public String getField5() {
		return field5;
	}

	/**
	 * @param field5 the field5 to set
	 */
	public void setField5(String field5) {
		this.field5 = field5;
	}

	/**
	 * @return the field6
	 */
	public String getField6() {
		return field6;
	}

	/**
	 * @param field6 the field6 to set
	 */
	public void setField6(String field6) {
		this.field6 = field6;
	}

	/**
	 * @return the field7
	 */
	public String getField7() {
		return field7;
	}

	/**
	 * @param field7 the field7 to set
	 */
	public void setField7(String field7) {
		this.field7 = field7;
	}

	/**
	 * @return the field8
	 */
	public String getField8() {
		return field8;
	}

	/**
	 * @param field8 the field8 to set
	 */
	public void setField8(String field8) {
		this.field8 = field8;
	}

	/**
	 * @return the field9
	 */
	public String getField9() {
		return field9;
	}

	/**
	 * @param field9 the field9 to set
	 */
	public void setField9(String field9) {
		this.field9 = field9;
	}

	/**
	 * @return the field10
	 */
	public String getField10() {
		return field10;
	}

	/**
	 * @param field10 the field10 to set
	 */
	public void setField10(String field10) {
		this.field10 = field10;
	}

}
