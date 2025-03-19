package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_BUSINESS_UNIT")
public class BusinessUnit implements Serializable {

	private static final long serialVersionUID = 5520741881476162127L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "BUSINESS_UNIT_NAME", length = 64, nullable = false)
	private String unitName;

	@Column(name = "DISPLAY_NAME", length = 64, nullable = false)
	private String displayName;

	@Column(name = "LINE1", length = 64)
	private String line1;

	@Column(name = "LINE2", length = 64)
	private String line2;

	@Column(name = "LINE3", length = 64)
	private String line3;

	@Column(name = "LINE4", length = 64)
	private String line4;

	@Column(name = "LINE5", length = 64)
	private String line5;

	@Column(name = "LINE6", length = 64)
	private String line6;

	@Column(name = "LINE7", length = 64)
	private String line7;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileAttatchment;

	@Column(name = "FILE_NAME", length = 200)
	private String fileName;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String contentType;

	@Column(name = "FILE_SIZE_KB", length = 200)
	private Integer fileSizeKb;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TANENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUS_UNIT_BUY_CODE"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_BUS_UNIT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUS_UNIT_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "ID_SEQUENCE")
	private Integer idSequence;

	@Column(name = "BUSINESS_UNIT_CODE", length = 12)
	private String unitCode;

	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUS_UNIT_PARENT"))
	private BusinessUnit parent;

	@Column(name = "BUDGET_CHECK")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean budgetCheck = Boolean.FALSE;

	@Column(name = "SPM_INTEGRATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean spmIntegration = Boolean.FALSE;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_BUSINESS_COST_MAPPING", joinColumns = @JoinColumn(name = "BUSINESS_UNIT_ID"), inverseJoinColumns = @JoinColumn(name = "COST_CENTER_ID"))
	private List<CostCenter> costCenter;

	@Column(name = "PR_ID_SEQUENCE")
	private Integer prIdSequence;

	@Column(name = "PO_ID_SEQUENCE")
	private Integer poIdSequence;

	@Column(name = "RFA_ID_SEQUENCE")
	private Integer rfaIdSequence;

	@Column(name = "RFI_ID_SEQUENCE")
	private Integer rfiIdSequence;

	@Column(name = "RFP_ID_SEQUENCE")
	private Integer rfpIdSequence;

	@Column(name = "RFQ_ID_SEQUENCE")
	private Integer rfqIdSequence;

	@Column(name = "RFT_ID_SEQUENCE")
	private Integer rftIdSequence;

	@Column(name = "SR_ID_SEQUENCE")
	private Integer srIdSequence;

	@Column(name = "BG_ID_SEQUENCE")
	private Integer bgIdSequence;

	@Column(name = "GRN_ID_SEQUENCE")
	private Integer grnIdSequence;

	@Column(name = "CTR_ID_SEQUENCE")
	private Integer ctrIdSequence;

	@Column(name = "SP_ID_SEQUENCE")
	private Integer spIdSequence;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_BUSNS_GROUP_CD_MAPPING", joinColumns = @JoinColumn(name = "BUSINESS_UNIT_ID"), inverseJoinColumns = @JoinColumn(name = "GROUP_CODE_ID"))
	private List<GroupCode> assignGroupCodes;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_BUSNS_AGR_TYP_MAPPING", joinColumns = @JoinColumn(name = "BUSINESS_UNIT_ID"), inverseJoinColumns = @JoinColumn(name = "AGREEMENT_TYPE_ID"))
	private List<AgreementType> agreementType;

	@Column(name = "CONTRACT_INTEGRATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean contractIntegration = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean recursive = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean budgetCheckOld = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean spmIntegrationOld = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean spmIntegrationRecursive = Boolean.FALSE;

	/**
	 * @return the idSequence
	 */
	public Integer getIdSequence() {
		return idSequence;
	}

	/**
	 * @param idSequence the idSequence to set
	 */
	public void setIdSequence(Integer idSequence) {
		this.idSequence = idSequence;
	}

	/**
	 * @return the unitCode
	 */
	public String getUnitCode() {
		return unitCode;
	}

	/**
	 * @param unitCode the unitCode to set
	 */
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public BusinessUnit() {
	}

	public BusinessUnit(String id, String unitName, String displayName) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
	}

	public BusinessUnit(String id, String unitName, String displayName, Status status) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
	}

	public BusinessUnit(String id, String unitName, String unitCode, String displayName, Status status) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		this.unitCode = unitCode;
	}

	public BusinessUnit(String id, String unitName, String unitCode, String displayName, Status status, Integer idSequence) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		this.unitCode = unitCode;
		this.idSequence = idSequence;
	}

	public BusinessUnit(String id, String unitName, String displayName, Status status, User createdBy, Date createdDate, User modifiedBy, Date modifiedDate) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public BusinessUnit(String id, String unitName, String displayName, Status status, User createdBy, Date createdDate, User modifiedBy, Date modifiedDate, String parentBusinessUnitId, String parentBusinessUnitCode, String parentBusinessUnitName) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;

		if (StringUtils.checkString(parentBusinessUnitName).length() > 0 && StringUtils.checkString(parentBusinessUnitId).length() > 0) {
			BusinessUnit parent = new BusinessUnit();
			parent.setId(parentBusinessUnitId);
			parent.setUnitName(parentBusinessUnitName);
			parent.setUnitCode(parentBusinessUnitCode);
			this.parent = parent;
		}

	}

	public BusinessUnit(String id, String unitName, String displayName, Status status, String line1, String line2, String line3, String line4, String line5, String line6, String line7) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.line4 = line4;
		this.line5 = line5;
		this.line6 = line6;
		this.line7 = line7;
	}

	public BusinessUnit(String id, String unitName, String displayName, Status status, String line1, String line2, String line3, String line4, String line5, String line6, String line7, Boolean budgetCheck, Boolean spmIntegration) {
		this.id = id;
		this.unitName = unitName;
		this.displayName = displayName;
		this.status = status;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.line4 = line4;
		this.line5 = line5;
		this.line6 = line6;
		this.line7 = line7;
		this.budgetCheck = budgetCheck;
		this.spmIntegration = spmIntegration;
	}

	public BusinessUnit(String id, String unitName, String unitCode, String displayName, String parentBusinessUnitName, String parentBusinessUnitCode, Status status, String line1, String line2, String line3, String line4, String line5, String line6, String line7) {
		this.id = id;
		this.unitName = unitName;
		this.unitCode = unitCode;
		this.displayName = displayName;
		if (StringUtils.checkString(parentBusinessUnitName).length() > 0) {
			BusinessUnit parent = new BusinessUnit();
			parent.setUnitName(parentBusinessUnitName);
			parent.setUnitCode(parentBusinessUnitCode);
			this.parent = parent;
		}
		this.status = status;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.line4 = line4;
		this.line5 = line5;
		this.line6 = line6;
		this.line7 = line7;
	}

	public BusinessUnit(String id, String unitName, String unitCode, String displayName, String parentBusinessUnitName, String parentBusinessUnitCode, Status status, String line1, String line2, String line3, String line4, String line5, String line6, String line7, Boolean budgetCheck, Boolean spmIntegration) {
		this.id = id;
		this.unitName = unitName;
		this.unitCode = unitCode;
		this.displayName = displayName;
		if (StringUtils.checkString(parentBusinessUnitName).length() > 0) {
			BusinessUnit parent = new BusinessUnit();
			parent.setUnitName(parentBusinessUnitName);
			parent.setUnitCode(parentBusinessUnitCode);
			this.parent = parent;
		}
		this.status = status;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.line4 = line4;
		this.line5 = line5;
		this.line6 = line6;
		this.line7 = line7;
		this.budgetCheck = budgetCheck;
		this.spmIntegration = spmIntegration;
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
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the line3
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * @param line3 the line3 to set
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}

	/**
	 * @return the line4
	 */
	public String getLine4() {
		return line4;
	}

	/**
	 * @param line4 the line4 to set
	 */
	public void setLine4(String line4) {
		this.line4 = line4;
	}

	/**
	 * @return the line5
	 */
	public String getLine5() {
		return line5;
	}

	/**
	 * @param line5 the line5 to set
	 */
	public void setLine5(String line5) {
		this.line5 = line5;
	}

	/**
	 * @return the line6
	 */
	public String getLine6() {
		return line6;
	}

	/**
	 * @param line6 the line6 to set
	 */
	public void setLine6(String line6) {
		this.line6 = line6;
	}

	/**
	 * @return the line7
	 */
	public String getLine7() {
		return line7;
	}

	/**
	 * @param line7 the line7 to set
	 */
	public void setLine7(String line7) {
		this.line7 = line7;
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

	/**
	 * @return the fileAttatchment
	 */
	public byte[] getFileAttatchment() {
		return fileAttatchment;
	}

	/**
	 * @param fileAttatchment the fileAttatchment to set
	 */
	public void setFileAttatchment(byte[] fileAttatchment) {
		this.fileAttatchment = fileAttatchment;
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
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the fileSizeKb
	 */
	public Integer getFileSizeKb() {
		return fileSizeKb;
	}

	/**
	 * @param fileSizeKb the fileSizeKb to set
	 */
	public void setFileSizeKb(Integer fileSizeKb) {
		this.fileSizeKb = fileSizeKb;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
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
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
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
	 * @return the parent
	 */
	public BusinessUnit getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(BusinessUnit parent) {
		this.parent = parent;
	}

	/**
	 * @return the budgetCheck
	 */
	public Boolean getBudgetCheck() {
		return budgetCheck;
	}

	/**
	 * @param budgetCheck the budgetCheck to set
	 */
	public void setBudgetCheck(Boolean budgetCheck) {
		this.budgetCheck = budgetCheck;
	}

	/**
	 * @return the recursive
	 */
	public Boolean getRecursive() {
		return recursive;
	}

	/**
	 * @param recursive the recursive to set
	 */
	public void setRecursive(Boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * @return the budgetCheckOld
	 */
	public Boolean getBudgetCheckOld() {
		return budgetCheckOld;
	}

	/**
	 * @param budgetCheckOld the budgetCheckOld to set
	 */
	public void setBudgetCheckOld(Boolean budgetCheckOld) {
		this.budgetCheckOld = budgetCheckOld;
	}

	/**
	 * @return the costCenter
	 */
	public List<CostCenter> getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(List<CostCenter> costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the prIdSequence
	 */
	public Integer getPrIdSequence() {
		return prIdSequence;
	}

	/**
	 * @param prIdSequence the prIdSequence to set
	 */
	public void setPrIdSequence(Integer prIdSequence) {
		this.prIdSequence = prIdSequence;
	}

	/**
	 * @return the poIdSequence
	 */
	public Integer getPoIdSequence() {
		return poIdSequence;
	}

	/**
	 * @param poIdSequence the poIdSequence to set
	 */
	public void setPoIdSequence(Integer poIdSequence) {
		this.poIdSequence = poIdSequence;
	}

	/**
	 * @return the rfaIdSequence
	 */
	public Integer getRfaIdSequence() {
		return rfaIdSequence;
	}

	/**
	 * @param rfaIdSequence the rfaIdSequence to set
	 */
	public void setRfaIdSequence(Integer rfaIdSequence) {
		this.rfaIdSequence = rfaIdSequence;
	}

	/**
	 * @return the rfiIdSequence
	 */
	public Integer getRfiIdSequence() {
		return rfiIdSequence;
	}

	/**
	 * @param rfiIdSequence the rfiIdSequence to set
	 */
	public void setRfiIdSequence(Integer rfiIdSequence) {
		this.rfiIdSequence = rfiIdSequence;
	}

	/**
	 * @return the rfpIdSequence
	 */
	public Integer getRfpIdSequence() {
		return rfpIdSequence;
	}

	/**
	 * @param rfpIdSequence the rfpIdSequence to set
	 */
	public void setRfpIdSequence(Integer rfpIdSequence) {
		this.rfpIdSequence = rfpIdSequence;
	}

	/**
	 * @return the rfqIdSequence
	 */
	public Integer getRfqIdSequence() {
		return rfqIdSequence;
	}

	/**
	 * @param rfqIdSequence the rfqIdSequence to set
	 */
	public void setRfqIdSequence(Integer rfqIdSequence) {
		this.rfqIdSequence = rfqIdSequence;
	}

	/**
	 * @return the rftIdSequence
	 */
	public Integer getRftIdSequence() {
		return rftIdSequence;
	}

	/**
	 * @param rftIdSequence the rftIdSequence to set
	 */
	public void setRftIdSequence(Integer rftIdSequence) {
		this.rftIdSequence = rftIdSequence;
	}

	/**
	 * @return the srIdSequence
	 */
	public Integer getSrIdSequence() {
		return srIdSequence;
	}

	/**
	 * @param srIdSequence the srIdSequence to set
	 */
	public void setSrIdSequence(Integer srIdSequence) {
		this.srIdSequence = srIdSequence;
	}

	/**
	 * @return the bgIdSequence
	 */
	public Integer getBgIdSequence() {
		return bgIdSequence;
	}

	/**
	 * @param bgIdSequence the bgIdSequence to set
	 */
	public void setBgIdSequence(Integer bgIdSequence) {
		this.bgIdSequence = bgIdSequence;
	}

	public Integer getGrnIdSequence() {
		return grnIdSequence;
	}

	public void setGrnIdSequence(Integer grnIdSequence) {
		this.grnIdSequence = grnIdSequence;
	}

	/**
	 * @return the assignGroupCodes
	 */
	public List<GroupCode> getAssignGroupCodes() {
		return assignGroupCodes;
	}

	/**
	 * @param assignGroupCodes the assignGroupCodes to set
	 */
	public void setAssignGroupCodes(List<GroupCode> assignGroupCodes) {
		this.assignGroupCodes = assignGroupCodes;
	}

	/**
	 * @return the ctrIdSequence
	 */
	public Integer getCtrIdSequence() {
		return ctrIdSequence;
	}

	/**
	 * @param ctrIdSequence the ctrIdSequence to set
	 */
	public void setCtrIdSequence(Integer ctrIdSequence) {
		this.ctrIdSequence = ctrIdSequence;
	}

	/**
	 * @return the spIdSequence
	 */
	public Integer getSpIdSequence() {
		return spIdSequence;
	}

	/**
	 * @param spIdSequence the spIdSequence to set
	 */
	public void setSpIdSequence(Integer spIdSequence) {
		this.spIdSequence = spIdSequence;
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

	/**
	 * @return the spmIntegrationOld
	 */
	public Boolean getSpmIntegrationOld() {
		return spmIntegrationOld;
	}

	/**
	 * @param spmIntegrationOld the spmIntegrationOld to set
	 */
	public void setSpmIntegrationOld(Boolean spmIntegrationOld) {
		this.spmIntegrationOld = spmIntegrationOld;
	}

	/**
	 * @return the spmIntegrationRecursive
	 */
	public Boolean getSpmIntegrationRecursive() {
		return spmIntegrationRecursive;
	}

	/**
	 * @param spmIntegrationRecursive the spmIntegrationRecursive to set
	 */
	public void setSpmIntegrationRecursive(Boolean spmIntegrationRecursive) {
		this.spmIntegrationRecursive = spmIntegrationRecursive;
	}

	/**
	 * @return the agreementType
	 */
	public List<AgreementType> getAgreementType() {
		return agreementType;
	}

	/**
	 * @param agreementType the agreementType to set
	 */
	public void setAgreementType(List<AgreementType> agreementType) {
		this.agreementType = agreementType;
	}

	public Boolean getContractIntegration() {
		return contractIntegration;
	}

	public void setContractIntegration(Boolean contractIntegration) {
		this.contractIntegration = contractIntegration;
	}

	/**
	 * /* (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessUnit other = (BusinessUnit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "BusinessUnit [id=" + id + ", unitName=" + unitName + ", displayName=" + displayName + ", line1=" + line1 + ", line2=" + line2 + ", line3=" + line3 + ", line4=" + line4 + ", line5=" + line5 + ", line6=" + line6 + ", line7=" + line7 + ", status=" + status + ", fileName=" + fileName + ", fileSizeKb=" + fileSizeKb + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}
}
