package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.privasia.procurehere.core.enums.Status;

/**
 * @author Sana
 */

public class BusinessUnitPojo implements Serializable {

	private static final long serialVersionUID = 676020086530363797L;

	private String id;

	private String displayName;

	private String unitName;

	private String unitCode;

	private String parentBusinessUnit;

	private String createdBy;

	private Date createdDate;

	private String modifiedBy;

	private Date modifiedDate;

	private Status status;

	private int srNo;

	private String parentUnitCode;

	private String line1;

	private String line2;

	private String line3;

	private String line4;

	private String line5;

	private String line6;

	private String line7;

	private Boolean budgetCheck;

	private Boolean spmIntegration;

	public BusinessUnitPojo(String id, String displayName, String unitName) {
		this.id = id;
		this.displayName = displayName;
		this.unitName = unitName;
	}

	public BusinessUnitPojo(String id, String displayName, String unitName, String unitCode) {
		this.id = id;
		this.displayName = displayName;
		this.unitName = unitName;
		this.unitCode = unitCode;
	}

	public Boolean getBudgetCheck() {
		return budgetCheck;
	}

	public void setBudgetCheck(Boolean budgetCheck) {
		this.budgetCheck = budgetCheck;
	}

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public BusinessUnitPojo() {
	}

	public BusinessUnitPojo(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public BusinessUnitPojo(String id) {
		this.id = id;
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
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * @return the createdBy
	 */

	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */

	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */

	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */

	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the status
	 */

	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getParentBusinessUnit() {
		return parentBusinessUnit;
	}

	public void setParentBusinessUnit(String parentBusinessUnit) {
		this.parentBusinessUnit = parentBusinessUnit;
	}

	public String getParentUnitCode() {
		return parentUnitCode;
	}

	public void setParentUnitCode(String parentUnitCode) {
		this.parentUnitCode = parentUnitCode;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getLine4() {
		return line4;
	}

	public void setLine4(String line4) {
		this.line4 = line4;
	}

	public String getLine5() {
		return line5;
	}

	public void setLine5(String line5) {
		this.line5 = line5;
	}

	public String getLine6() {
		return line6;
	}

	public void setLine6(String line6) {
		this.line6 = line6;
	}

	public String getLine7() {
		return line7;
	}

	public void setLine7(String line7) {
		this.line7 = line7;
	}

	/**
	 * @return the spmIntegration
	 */
	public Boolean getSpmIntegration() {
		return spmIntegration;
	}

	/**
	 * @param spmIntegration the spmIntegration to set
	 */
	public void setSpmIntegration(Boolean spmIntegration) {
		this.spmIntegration = spmIntegration;
	}

}
