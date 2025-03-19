package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.pojo.TemplateFieldPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_RFX_TEMPLATE", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_RFX_TMPLATE_TENANT_ID") })
public class RfxTemplate implements Serializable {

	private static final long serialVersionUID = 4881806787401705083L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{rfxTemplate.name.empty}")
	@Size(min = 1, max = 128, message = "{rfxTemplate.name.length}")
	@Column(name = "TEMPLATE_NAME", length = 128)
	private String templateName;

	@Size(min = 0, max = 300, message = "{rfxTemplate.description.length}")
	@Column(name = "TEMPLATE_DESCRIPTION", length = 300)
	private String templateDescription;

	@NotNull(message = "{rfxTemplate.type.empty}")
	@Convert(converter = RfxTypesCoverter.class)
	@Column(name = "TEMPLATE_TYPE", length = 50, nullable = false)
	private RfxTypes type;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFX_TMPLATE_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "template", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<TemplateField> fields;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_RFX_TMPLATE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFX_TMPLATE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPLATE_STATUS")
	private Status status;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<TemplateEventApproval> approvals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<TemplateEventTeamMembers> teamMembers;

	@NotNull
	@Column(name = "IS_APPROVAL_VISIBLE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalVisible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_APPROVAL_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalReadOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_APPROVAL_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalOptional = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPLATE_AUCTION_TYPE")
	private AuctionType templateAuctionType;

	@Column(name = "SUPPLIER_BASED_ON_CATEG", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean supplierBasedOnCategory = Boolean.FALSE;

	@Column(name = "AUTO_POPULATE_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoPopulateSupplier = Boolean.FALSE;

	@Column(name = "READ_ONLY_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlySupplier = Boolean.FALSE;

	@Column(name = "VIEW_SUPPLIER_NAME", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean viewSupplerName = Boolean.TRUE;

	@Column(name = "ALLOW_CLOSE_ENVELOPE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean closeEnvelope = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_VIEW_SUPPLIER_NAME", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleViewSupplierName = Boolean.TRUE;

	@Column(name = "READ_ONLY_VIEW_SUPPLIER_NAME", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyViewSupplierName = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_CLOSE_ENVELOPE ", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleCloseEnvelope = Boolean.FALSE;

	@Column(name = "READ_ONLY_CLOSE_ENVELOPE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyCloseEnvelope = Boolean.FALSE;

	@Column(name = "ALLOW_TO_SUSPEND_EVENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowToSuspendEvent = Boolean.TRUE;

	@Column(name = "VISIBLE_ALLOW_TO_SUSPEND_EVENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleAllowToSuspendEvent = Boolean.TRUE;

	@Column(name = "READ_ONLY_ALLOW_TO_SUSPEND")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyAllowToSuspendEvent = Boolean.FALSE;

	@Column(name = "ALLOW_ADD_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addSupplier = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_ADD_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleAddSupplier = Boolean.FALSE;

	@Column(name = "READ_ONLY_ADD_SUPPLIER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyAddSupplier = Boolean.FALSE;

	@Column(name = "ALLOW_VIEW_AUCTION_HALL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean viewAuctionHall = Boolean.FALSE;

	@Column(name = "VISIBLE_VIEW_AUCTION_HALL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleViewAuctionHall = Boolean.FALSE;

	@Column(name = "READ_ONLY_VIEW_AUCTION_HALL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyViewAuctionHall = Boolean.FALSE;

	@Column(name = "ALLOW_SUPPLIER_BASED_ON_STATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean supplierBasedOnState = Boolean.FALSE;

	@Column(name = "RESTRICT_SUPPLIER_BY_STATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean restrictSupplierByState = Boolean.FALSE;

	@Column(name = "IS_OPTIONAL_SUPPLIER_TAGS", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean optionalSupplierTags = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_SUPPLIER_TAGS", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean visibleSupplierTags = Boolean.FALSE;

	@Column(name ="IS_OPTIONAL_GEOGRAPHICAL_COVERAGE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean optionalGeographicalCoverage = Boolean.FALSE;

	@Column(name="IS_VISIBLE_GEOGRAPHICAL_COVERAGE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean visibleGeographicalCoverage = Boolean.FALSE;

	@Column(name = "IS_PRIVATE_EVENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean privateEvent = Boolean.TRUE;

	@Column(name = "IS_PARTIAL_EVENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean partialEvent = Boolean.TRUE;

	@Column(name = "IS_PUBLIC_EVENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean publicEvent = Boolean.TRUE;

	@Column(name = "IS_ALLOW_REVERT_LAST_BID", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean revertLastBid = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_REVERT_LAST_BID", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleRevertLastBid = Boolean.FALSE;

	@Column(name = "IS_READ_ONLY_REVERT_LAST_BID", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyRevertLastBid = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REVERT_BID_USER", nullable = true, foreignKey = @ForeignKey(name = "FK_RFX_TMPLATE_REVBID"))
	private User revertBidUser;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UNMASKED_USER", nullable = true, foreignKey = @ForeignKey(name = "FK_RFX_TMPLATE_UNMASK_USR"))
	private User unMaskedUser;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<TemplateUnmaskUser> unMaskedUsers;

	@Column(name = "ADD_BILL_OF_QUANTITY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addBillOfQuantity = Boolean.TRUE;

	@Column(name = "RFX_ENVELOPE_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfxEnvelopeReadOnly = Boolean.FALSE;

	@Column(name = "RFX_ENVELOPE_OPENING", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean rfxEnvelopeOpening = Boolean.FALSE;

	@Column(name = "RFX_ENV_OPENING_AFTER", nullable = true)
	private String rfxEnvOpeningAfter;

	@Column(name = "RFX_ENVELOPE_1")
	private String rfxEnvelope1;

	@Column(name = "RFX_SEQUENCE_1")
	private Integer rfxSequence1;

	@Column(name = "RFX_ENVELOPE_2")
	private String rfxEnvelope2;

	@Column(name = "RFX_SEQUENCE_2")
	private Integer rfxSequence2;

	@Column(name = "RFX_ENVELOPE_3")
	private String rfxEnvelope3;

	@Column(name = "RFX_SEQUENCE_3")
	private Integer rfxSequence3;

	@Column(name = "RFX_ENVELOPE_4")
	private String rfxEnvelope4;

	@Column(name = "RFX_SEQUENCE_4")
	private Integer rfxSequence4;

	@Column(name = "RFX_ENVELOPE_5")
	private String rfxEnvelope5;

	@Column(name = "RFX_SEQUENCE_5")
	private Integer rfxSequence5;

	@Column(name = "RFX_ENVELOPE_6")
	private String rfxEnvelope6;

	@Column(name = "RFX_SEQUENCE_6")
	private Integer rfxSequence6;

	@NotNull
	@Column(name = "READ_ONLY_TEAM_MEMBER", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyTeamMember = Boolean.FALSE;

	@Column(name = "IS_ENABLE_EVAL_DECLARATION", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEvaluationDeclaration = Boolean.FALSE;

	@Column(name = "IS_ENABLE_SUPPLIER_DECLARATION", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSupplierDeclaration = Boolean.FALSE;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_TEMPLATE_EVAL_CON_USERS", joinColumns = @JoinColumn(name = "TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> evaluationConclusionUsers;

	@Column(name = "IS_ENABLE_EVAL_CON_USR", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableEvaluationConclusionUsers = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_EVAL_CON_USR", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleEvaluationConclusionUsers = Boolean.FALSE;

	@Column(name = "READ_ONLY_EVAL_CON_USR", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyEvaluationConclusionUsers = Boolean.FALSE;

	@Column(name = "IS_ENABLE_SUSPEND_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableSuspendApproval = Boolean.FALSE;

	@Column(name = "IS_VISIBLE_SUSPEND_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleSuspendApproval = Boolean.FALSE;

	@Column(name = "IS_READ_ONLY_SUSPEND_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlySuspendApproval = Boolean.FALSE;

	@Column(name = "IS_OPTIONAL_SUSPEND_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean optionalSuspendApproval = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<TemplateSuspensionApproval> suspensionApprovals;

	@Column(name = "ALLOW_DISQ_SUP_DOWNLOAD", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean allowDisqualifiedSupplierDownload = Boolean.FALSE;
	
	@Column(name = "IS_ENABLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardApproval = Boolean.FALSE;
	
	@Column(name = "IS_VISIBLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean visibleAwardApproval = Boolean.FALSE;

	@Column(name = "IS_READ_ONLY_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean readOnlyAwardApproval = Boolean.FALSE;

	@Column(name = "IS_OPTIONAL_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean optionalAwardApproval = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate")
	private List<RfxTemplateDocument> documents;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<TemplateAwardApproval> awardApprovals;

	@Column(name = "COMPLETE_TEMPLATE_DETAILS", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean completeTemplateDetails = Boolean.FALSE;

	@Column(name = "IS_TEMPLATE_USED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean isTemplateUsed = Boolean.FALSE;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	private Boolean documentCompleted = Boolean.FALSE;

	@Transient
	boolean checkControl = true;

	@Transient
	private TemplateFieldPojo templateFieldBinding;

	public RfxTemplate() {
		this.approvalOptional = Boolean.FALSE;
		this.approvalReadOnly = Boolean.FALSE;
		this.approvalVisible = Boolean.FALSE;
		this.supplierBasedOnCategory = Boolean.FALSE;
		this.autoPopulateSupplier = Boolean.FALSE;
		this.readOnlySupplier = Boolean.FALSE;
		this.viewSupplerName = Boolean.TRUE;
		this.closeEnvelope = Boolean.FALSE;
		this.visibleViewSupplierName = Boolean.TRUE;
		this.readOnlyViewSupplierName = Boolean.FALSE;
		this.visibleCloseEnvelope = Boolean.FALSE;
		this.readOnlyCloseEnvelope = Boolean.FALSE;
		this.addSupplier = Boolean.FALSE;
		this.visibleAddSupplier = Boolean.FALSE;
		this.readOnlyAddSupplier = Boolean.FALSE;
		this.allowToSuspendEvent = Boolean.TRUE;
		this.visibleAllowToSuspendEvent = Boolean.TRUE;
		this.readOnlyAllowToSuspendEvent = Boolean.FALSE;
		this.viewAuctionHall = Boolean.FALSE;
		this.visibleViewAuctionHall = Boolean.FALSE;
		this.readOnlyViewAuctionHall = Boolean.FALSE;
		this.privateEvent = Boolean.TRUE;
		this.partialEvent = Boolean.TRUE;
		this.publicEvent = Boolean.TRUE;
		this.revertLastBid = Boolean.FALSE;
		this.visibleRevertLastBid = Boolean.FALSE;
		this.readOnlyRevertLastBid = Boolean.FALSE;
		this.addBillOfQuantity = Boolean.TRUE;
		this.readOnlyTeamMember = Boolean.FALSE;
		this.enableEvaluationDeclaration = Boolean.FALSE;
		this.enableSupplierDeclaration = Boolean.FALSE;
		this.enableEvaluationConclusionUsers = Boolean.FALSE;
		this.visibleEvaluationConclusionUsers = Boolean.FALSE;
		this.readOnlyEvaluationConclusionUsers = Boolean.FALSE;
		this.completeTemplateDetails = Boolean.FALSE;
		this.isTemplateUsed = Boolean.FALSE;
		this.documentCompleted = Boolean.FALSE;
	}

	/**
	 * @return
	 */
	public String getTypeString() {
		if (type != null) {
			return type.getValue();
		} else {
			return null;
		}
	}

	public RfxTemplate createShallowCopy() {
		RfxTemplate ret = new RfxTemplate();
		ret.setCreatedBy(createdBy);
		ret.setCreatedDate(createdDate);
		ret.setId(id);
		ret.setModifiedBy(modifiedBy);
		ret.setModifiedDate(modifiedDate);
		ret.setStatus(status);
		ret.setTemplateDescription(templateDescription);
		ret.setTemplateName(templateName);
		ret.setType(type);
		ret.setTemplateAuctionType(templateAuctionType);
		return ret;
	}

	/**
	 * @return the rfxEnvelopeOpening
	 */
	public Boolean getRfxEnvelopeOpening() {
		return rfxEnvelopeOpening;
	}

	/**
	 * @param rfxEnvelopeOpening the rfxEnvelopeOpening to set
	 */
	public void setRfxEnvelopeOpening(Boolean rfxEnvelopeOpening) {
		this.rfxEnvelopeOpening = rfxEnvelopeOpening;
	}

	/**
	 * @return the rfxEnvOpeningAfter
	 */
	public String getRfxEnvOpeningAfter() {
		return rfxEnvOpeningAfter;
	}

	/**
	 * @param rfxEnvOpeningAfter the rfxEnvOpeningAfter to set
	 */
	public void setRfxEnvOpeningAfter(String rfxEnvOpeningAfter) {
		this.rfxEnvOpeningAfter = rfxEnvOpeningAfter;
	}

	/**
	 * @return the rfxSequence1
	 */
	public Integer getRfxSequence1() {
		return rfxSequence1;
	}

	/**
	 * @param rfxSequence1 the rfxSequence1 to set
	 */
	public void setRfxSequence1(Integer rfxSequence1) {
		this.rfxSequence1 = rfxSequence1;
	}

	/**
	 * @return the rfxSequence2
	 */
	public Integer getRfxSequence2() {
		return rfxSequence2;
	}

	/**
	 * @param rfxSequence2 the rfxSequence2 to set
	 */
	public void setRfxSequence2(Integer rfxSequence2) {
		this.rfxSequence2 = rfxSequence2;
	}

	/**
	 * @return the rfxSequence3
	 */
	public Integer getRfxSequence3() {
		return rfxSequence3;
	}

	/**
	 * @param rfxSequence3 the rfxSequence3 to set
	 */
	public void setRfxSequence3(Integer rfxSequence3) {
		this.rfxSequence3 = rfxSequence3;
	}

	/**
	 * @return the rfxSequence4
	 */
	public Integer getRfxSequence4() {
		return rfxSequence4;
	}

	/**
	 * @param rfxSequence4 the rfxSequence4 to set
	 */
	public void setRfxSequence4(Integer rfxSequence4) {
		this.rfxSequence4 = rfxSequence4;
	}

	/**
	 * @return the rfxSequence5
	 */
	public Integer getRfxSequence5() {
		return rfxSequence5;
	}

	/**
	 * @param rfxSequence5 the rfxSequence5 to set
	 */
	public void setRfxSequence5(Integer rfxSequence5) {
		this.rfxSequence5 = rfxSequence5;
	}

	/**
	 * @return the rfxSequence6
	 */
	public Integer getRfxSequence6() {
		return rfxSequence6;
	}

	/**
	 * @param rfxSequence6 the rfxSequence6 to set
	 */
	public void setRfxSequence6(Integer rfxSequence6) {
		this.rfxSequence6 = rfxSequence6;
	}

	/**
	 * @return the rfxEnvelopeReadOnly
	 */
	public Boolean getRfxEnvelopeReadOnly() {
		return rfxEnvelopeReadOnly;
	}

	/**
	 * @param rfxEnvelopeReadOnly the rfxEnvelopeReadOnly to set
	 */
	public void setRfxEnvelopeReadOnly(Boolean rfxEnvelopeReadOnly) {
		this.rfxEnvelopeReadOnly = rfxEnvelopeReadOnly;
	}

	/**
	 * @return the rfxEnvelope1
	 */
	public String getRfxEnvelope1() {
		return rfxEnvelope1;
	}

	/**
	 * @param rfxEnvelope1 the rfxEnvelope1 to set
	 */
	public void setRfxEnvelope1(String rfxEnvelope1) {
		this.rfxEnvelope1 = rfxEnvelope1;
	}

	/**
	 * @return the rfxEnvelope2
	 */
	public String getRfxEnvelope2() {
		return rfxEnvelope2;
	}

	/**
	 * @param rfxEnvelope2 the rfxEnvelope2 to set
	 */
	public void setRfxEnvelope2(String rfxEnvelope2) {
		this.rfxEnvelope2 = rfxEnvelope2;
	}

	/**
	 * @return the rfxEnvelope3
	 */
	public String getRfxEnvelope3() {
		return rfxEnvelope3;
	}

	/**
	 * @param rfxEnvelope3 the rfxEnvelope3 to set
	 */
	public void setRfxEnvelope3(String rfxEnvelope3) {
		this.rfxEnvelope3 = rfxEnvelope3;
	}

	/**
	 * @return the rfxEnvelope4
	 */
	public String getRfxEnvelope4() {
		return rfxEnvelope4;
	}

	/**
	 * @param rfxEnvelope4 the rfxEnvelope4 to set
	 */
	public void setRfxEnvelope4(String rfxEnvelope4) {
		this.rfxEnvelope4 = rfxEnvelope4;
	}

	/**
	 * @return the rfxEnvelope5
	 */
	public String getRfxEnvelope5() {
		return rfxEnvelope5;
	}

	/**
	 * @param rfxEnvelope5 the rfxEnvelope5 to set
	 */
	public void setRfxEnvelope5(String rfxEnvelope5) {
		this.rfxEnvelope5 = rfxEnvelope5;
	}

	/**
	 * @return the rfxEnvelope6
	 */
	public String getRfxEnvelope6() {
		return rfxEnvelope6;
	}

	/**
	 * @param rfxEnvelope6 the rfxEnvelope6 to set
	 */
	public void setRfxEnvelope6(String rfxEnvelope6) {
		this.rfxEnvelope6 = rfxEnvelope6;
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
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateDescription
	 */
	public String getTemplateDescription() {
		return templateDescription;
	}

	/**
	 * @param templateDescription the templateDescription to set
	 */
	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	/**
	 * @return the type
	 */
	public RfxTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RfxTypes type) {
		this.type = type;
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
	 * @return the fields
	 */
	public List<TemplateField> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<TemplateField> fields) {
		this.fields = fields;
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
	 * @return the templateFieldBinding
	 */
	public TemplateFieldPojo getTemplateFieldBinding() {
		return templateFieldBinding;
	}

	/**
	 * @param templateFieldBinding the templateFieldBinding to set
	 */
	public void setTemplateFieldBinding(TemplateFieldPojo templateFieldBinding) {
		this.templateFieldBinding = templateFieldBinding;
	}

	/**
	 * @return the checkControl
	 */
	public boolean isCheckControl() {
		return checkControl;
	}

	/**
	 * @param checkControl the checkControl to set
	 */
	public void setCheckControl(boolean checkControl) {
		this.checkControl = checkControl;
	}

	/**
	 * @return the approvalVisible
	 */
	public Boolean getApprovalVisible() {
		return approvalVisible;
	}

	/**
	 * @param approvalVisible the approvalVisible to set
	 */
	public void setApprovalVisible(Boolean approvalVisible) {
		this.approvalVisible = approvalVisible;
	}

	/**
	 * @return the approvalReadOnly
	 */
	public Boolean getApprovalReadOnly() {
		return approvalReadOnly;
	}

	/**
	 * @param approvalReadOnly the approvalReadOnly to set
	 */
	public void setApprovalReadOnly(Boolean approvalReadOnly) {
		this.approvalReadOnly = approvalReadOnly;
	}

	/**
	 * @return the approvalOptional
	 */
	public Boolean getApprovalOptional() {
		return approvalOptional;
	}

	/**
	 * @param approvalOptional the approvalOptional to set
	 */
	public void setApprovalOptional(Boolean approvalOptional) {
		this.approvalOptional = approvalOptional;
	}

	/**
	 * @return the approvals
	 */
	public List<TemplateEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @return the templateAuctionType
	 */
	public AuctionType getTemplateAuctionType() {
		return templateAuctionType;
	}

	/**
	 * @param templateAuctionType the templateAuctionType to set
	 */
	public void setTemplateAuctionType(AuctionType templateAuctionType) {
		this.templateAuctionType = templateAuctionType;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<TemplateEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<TemplateEventApproval>();
		} else {
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}
	}

	/**
	 * @return the supplierBasedOnCategory
	 */
	public Boolean getSupplierBasedOnCategory() {
		return supplierBasedOnCategory;
	}

	/**
	 * @param supplierBasedOnCategory the supplierBasedOnCategory to set
	 */
	public void setSupplierBasedOnCategory(Boolean supplierBasedOnCategory) {
		this.supplierBasedOnCategory = supplierBasedOnCategory;
	}

	/**
	 * @return the autoPopulateSupplier
	 */
	public Boolean getAutoPopulateSupplier() {
		return autoPopulateSupplier;
	}

	/**
	 * @param autoPopulateSupplier the autoPopulateSupplier to set
	 */
	public void setAutoPopulateSupplier(Boolean autoPopulateSupplier) {
		this.autoPopulateSupplier = autoPopulateSupplier;
	}

	/**
	 * @return the readOnlySupplier
	 */
	public Boolean getReadOnlySupplier() {
		return readOnlySupplier;
	}

	/**
	 * @param readOnlySupplier the readOnlySupplier to set
	 */
	public void setReadOnlySupplier(Boolean readOnlySupplier) {
		this.readOnlySupplier = readOnlySupplier;
	}

	public List<TemplateEventTeamMembers> getTeamMembers() {
		return teamMembers;
	}

	public Boolean getViewSupplerName() {
		return viewSupplerName;
	}

	public void setViewSupplerName(Boolean viewSupplerName) {
		this.viewSupplerName = viewSupplerName;
	}

	/**
	 * @return the closeEnvelope
	 */
	public Boolean getCloseEnvelope() {
		return closeEnvelope;
	}

	/**
	 * @param closeEnvelope the closeEnvelope to set
	 */
	public void setCloseEnvelope(Boolean closeEnvelope) {
		this.closeEnvelope = closeEnvelope;
	}

	public Boolean getVisibleViewSupplierName() {
		return visibleViewSupplierName;
	}

	public void setVisibleViewSupplierName(Boolean visibleViewSupplierName) {
		this.visibleViewSupplierName = visibleViewSupplierName;
	}

	public Boolean getReadOnlyViewSupplierName() {
		return readOnlyViewSupplierName;
	}

	public void setReadOnlyViewSupplierName(Boolean readOnlyViewSupplierName) {
		this.readOnlyViewSupplierName = readOnlyViewSupplierName;
	}

	public Boolean getVisibleCloseEnvelope() {
		return visibleCloseEnvelope;
	}

	public void setVisibleCloseEnvelope(Boolean visibleCloseEnvelope) {
		this.visibleCloseEnvelope = visibleCloseEnvelope;
	}

	public Boolean getReadOnlyCloseEnvelope() {
		return readOnlyCloseEnvelope;
	}

	public void setReadOnlyCloseEnvelope(Boolean readOnlyCloseEnvelope) {
		this.readOnlyCloseEnvelope = readOnlyCloseEnvelope;
	}

	/**
	 * @return the unMaskedUsers
	 */
	public List<TemplateUnmaskUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<TemplateUnmaskUser> unMaskedUsers) {
		this.unMaskedUsers = unMaskedUsers;
	}

	/**
	 * @return the addSupplier
	 */
	public Boolean getAddSupplier() {
		return addSupplier;
	}

	/**
	 * @param addSupplier the addSupplier to set
	 */
	public void setAddSupplier(Boolean addSupplier) {
		this.addSupplier = addSupplier;
	}

	/**
	 * @return the visibleAddSupplier
	 */
	public Boolean getVisibleAddSupplier() {
		return visibleAddSupplier;
	}

	/**
	 * @param visibleAddSupplier the visibleAddSupplier to set
	 */
	public void setVisibleAddSupplier(Boolean visibleAddSupplier) {
		this.visibleAddSupplier = visibleAddSupplier;
	}

	/**
	 * @return the readOnlyAddSupplier
	 */
	public Boolean getReadOnlyAddSupplier() {
		return readOnlyAddSupplier;
	}

	/**
	 * @param readOnlyAddSupplier the readOnlyAddSupplier to set
	 */
	public void setReadOnlyAddSupplier(Boolean readOnlyAddSupplier) {
		this.readOnlyAddSupplier = readOnlyAddSupplier;
	}

	public Boolean getAllowToSuspendEvent() {
		return allowToSuspendEvent;
	}

	public void setAllowToSuspendEvent(Boolean allowToSuspendEvent) {
		this.allowToSuspendEvent = allowToSuspendEvent;
	}

	public Boolean getVisibleAllowToSuspendEvent() {
		return visibleAllowToSuspendEvent;
	}

	public void setVisibleAllowToSuspendEvent(Boolean visibleAllowToSuspendEvent) {
		this.visibleAllowToSuspendEvent = visibleAllowToSuspendEvent;
	}

	public Boolean getReadOnlyAllowToSuspendEvent() {
		return readOnlyAllowToSuspendEvent;
	}

	public void setReadOnlyAllowToSuspendEvent(Boolean readOnlyAllowToSuspendEvent) {
		this.readOnlyAllowToSuspendEvent = readOnlyAllowToSuspendEvent;
	}

	/**
	 * @return the supplierBasedOnState
	 */
	public Boolean getSupplierBasedOnState() {
		return supplierBasedOnState;
	}

	/**
	 * @param supplierBasedOnState the supplierBasedOnState to set
	 */
	public void setSupplierBasedOnState(Boolean supplierBasedOnState) {
		this.supplierBasedOnState = supplierBasedOnState;
	}

	/**
	 * @return the restrictSupplierByState
	 */
	public Boolean getRestrictSupplierByState() {
		return restrictSupplierByState;
	}

	/**
	 * @param restrictSupplierByState the restrictSupplierByState to set
	 */
	public void setRestrictSupplierByState(Boolean restrictSupplierByState) {
		this.restrictSupplierByState = restrictSupplierByState;
	}

	public Boolean getVisibleSupplierTags() {
		return visibleSupplierTags;
	}

	public void setVisibleSupplierTags(Boolean visibleSupplierTags) {
		this.visibleSupplierTags = visibleSupplierTags;
	}

	public Boolean getOptionalSupplierTags() {
		return optionalSupplierTags;
	}

	public void setOptionalSupplierTags(Boolean optionalSupplierTags) {
		this.optionalSupplierTags = optionalSupplierTags;
	}

	public Boolean getOptionalGeographicalCoverage() {
		return optionalGeographicalCoverage;
	}

	public void setOptionalGeographicalCoverage(Boolean optionalGeographicalCoverage) {
		this.optionalGeographicalCoverage = optionalGeographicalCoverage;
	}

	public Boolean getVisibleGeographicalCoverage() {
		return visibleGeographicalCoverage;
	}

	public void setVisibleGeographicalCoverage(Boolean visibleGeographicalCoverage) {
		this.visibleGeographicalCoverage = visibleGeographicalCoverage;
	}

	public void setTeamMembers(List<TemplateEventTeamMembers> teamMembers) {
		if (this.teamMembers == null) {
			this.teamMembers = new ArrayList<TemplateEventTeamMembers>();
		} else {
			this.teamMembers.clear();
		}
		if (teamMembers != null) {
			this.teamMembers.addAll(teamMembers);
		}
	}

	/**
	 * @return the viewAuctionHall
	 */
	public Boolean getViewAuctionHall() {
		return viewAuctionHall;
	}

	/**
	 * @param viewAuctionHall the viewAuctionHall to set
	 */
	public void setViewAuctionHall(Boolean viewAuctionHall) {
		this.viewAuctionHall = viewAuctionHall;
	}

	/**
	 * @return the visibleViewAuctionHall
	 */
	public Boolean getVisibleViewAuctionHall() {
		return visibleViewAuctionHall;
	}

	/**
	 * @param visibleViewAuctionHall the visibleViewAuctionHall to set
	 */
	public void setVisibleViewAuctionHall(Boolean visibleViewAuctionHall) {
		this.visibleViewAuctionHall = visibleViewAuctionHall;
	}

	/**
	 * @return the readOnlyViewAuctionHall
	 */
	public Boolean getReadOnlyViewAuctionHall() {
		return readOnlyViewAuctionHall;
	}

	/**
	 * @param readOnlyViewAuctionHall the readOnlyViewAuctionHall to set
	 */
	public void setReadOnlyViewAuctionHall(Boolean readOnlyViewAuctionHall) {
		this.readOnlyViewAuctionHall = readOnlyViewAuctionHall;
	}

	/**
	 * @return the privateEvent
	 */
	public Boolean getPrivateEvent() {
		return privateEvent;
	}

	/**
	 * @param privateEvent the privateEvent to set
	 */
	public void setPrivateEvent(Boolean privateEvent) {
		this.privateEvent = privateEvent;
	}

	/**
	 * @return the partialEvent
	 */
	public Boolean getPartialEvent() {
		return partialEvent;
	}

	/**
	 * @param partialEvent the partialEvent to set
	 */
	public void setPartialEvent(Boolean partialEvent) {
		this.partialEvent = partialEvent;
	}

	/**
	 * @return the publicEvent
	 */
	public Boolean getPublicEvent() {
		return publicEvent;
	}

	/**
	 * @param publicEvent the publicEvent to set
	 */
	public void setPublicEvent(Boolean publicEvent) {
		this.publicEvent = publicEvent;
	}

	public Boolean getRevertLastBid() {
		return revertLastBid;
	}

	public void setRevertLastBid(Boolean revertLastBid) {
		this.revertLastBid = revertLastBid;
	}

	public Boolean getVisibleRevertLastBid() {
		return visibleRevertLastBid;
	}

	public void setVisibleRevertLastBid(Boolean visibleRevertLastBid) {
		this.visibleRevertLastBid = visibleRevertLastBid;
	}

	public Boolean getReadOnlyRevertLastBid() {
		return readOnlyRevertLastBid;
	}

	public void setReadOnlyRevertLastBid(Boolean readOnlyRevertLastBid) {
		this.readOnlyRevertLastBid = readOnlyRevertLastBid;
	}

	public User getRevertBidUser() {
		return revertBidUser;
	}

	public void setRevertBidUser(User revertBidUser) {
		this.revertBidUser = revertBidUser;
	}

	public User getUnMaskedUser() {
		return unMaskedUser;
	}

	public void setUnMaskedUser(User unMaskedUser) {
		this.unMaskedUser = unMaskedUser;
	}

	public Boolean getAddBillOfQuantity() {
		return addBillOfQuantity;
	}

	public void setAddBillOfQuantity(Boolean addBillOfQuantity) {
		this.addBillOfQuantity = addBillOfQuantity;
	}

	public Boolean getReadOnlyTeamMember() {
		return readOnlyTeamMember;
	}

	public void setReadOnlyTeamMember(Boolean readOnlyTeamMember) {
		this.readOnlyTeamMember = readOnlyTeamMember;
	}

	/**
	 * @return the enableEvaluationDeclaration
	 */
	public Boolean getEnableEvaluationDeclaration() {
		return enableEvaluationDeclaration;
	}

	/**
	 * @param enableEvaluationDeclaration the enableEvaluationDeclaration to set
	 */
	public void setEnableEvaluationDeclaration(Boolean enableEvaluationDeclaration) {
		this.enableEvaluationDeclaration = enableEvaluationDeclaration;
	}

	/**
	 * @return the enableSupplierDeclaration
	 */
	public Boolean getEnableSupplierDeclaration() {
		return enableSupplierDeclaration;
	}

	/**
	 * @param enableSupplierDeclaration the enableSupplierDeclaration to set
	 */
	public void setEnableSupplierDeclaration(Boolean enableSupplierDeclaration) {
		this.enableSupplierDeclaration = enableSupplierDeclaration;
	}

	/**
	 * @return the evaluationConclusionUsers
	 */
	public List<User> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<User> evaluationConclusionUsers) {
		if (this.evaluationConclusionUsers == null) {
			this.evaluationConclusionUsers = new ArrayList<User>();
		} else {
			this.evaluationConclusionUsers.clear();
		}
		if (evaluationConclusionUsers != null) {
			this.evaluationConclusionUsers.addAll(evaluationConclusionUsers);
		}
	}

	/**
	 * @return the enableEvaluationConclusionUsers
	 */
	public Boolean getEnableEvaluationConclusionUsers() {
		return enableEvaluationConclusionUsers;
	}

	/**
	 * @param enableEvaluationConclusionUsers the enableEvaluationConclusionUsers to set
	 */
	public void setEnableEvaluationConclusionUsers(Boolean enableEvaluationConclusionUsers) {
		this.enableEvaluationConclusionUsers = enableEvaluationConclusionUsers;
	}

	/**
	 * @return the visibleEvaluationConclusionUsers
	 */
	public Boolean getVisibleEvaluationConclusionUsers() {
		return visibleEvaluationConclusionUsers;
	}

	/**
	 * @param visibleEvaluationConclusionUsers the visibleEvaluationConclusionUsers to set
	 */
	public void setVisibleEvaluationConclusionUsers(Boolean visibleEvaluationConclusionUsers) {
		this.visibleEvaluationConclusionUsers = visibleEvaluationConclusionUsers;
	}

	/**
	 * @return the readOnlyEvaluationConclusionUsers
	 */
	public Boolean getReadOnlyEvaluationConclusionUsers() {
		return readOnlyEvaluationConclusionUsers;
	}

	/**
	 * @param readOnlyEvaluationConclusionUsers the readOnlyEvaluationConclusionUsers to set
	 */
	public void setReadOnlyEvaluationConclusionUsers(Boolean readOnlyEvaluationConclusionUsers) {
		this.readOnlyEvaluationConclusionUsers = readOnlyEvaluationConclusionUsers;
	}

	/**
	 * @return the enableSuspendApproval
	 */
	public Boolean getEnableSuspendApproval() {
		return enableSuspendApproval;
	}

	/**
	 * @param enableSuspendApproval the enableSuspendApproval to set
	 */
	public void setEnableSuspendApproval(Boolean enableSuspendApproval) {
		this.enableSuspendApproval = enableSuspendApproval;
	}

	/**
	 * @return the visibleSuspendApproval
	 */
	public Boolean getVisibleSuspendApproval() {
		return visibleSuspendApproval;
	}

	/**
	 * @param visibleSuspendApproval the visibleSuspendApproval to set
	 */
	public void setVisibleSuspendApproval(Boolean visibleSuspendApproval) {
		this.visibleSuspendApproval = visibleSuspendApproval;
	}

	/**
	 * @return the readOnlySuspendApproval
	 */
	public Boolean getReadOnlySuspendApproval() {
		return readOnlySuspendApproval;
	}

	/**
	 * @param readOnlySuspendApproval the readOnlySuspendApproval to set
	 */
	public void setReadOnlySuspendApproval(Boolean readOnlySuspendApproval) {
		this.readOnlySuspendApproval = readOnlySuspendApproval;
	}

	/**
	 * @return the optionalSuspendApproval
	 */
	public Boolean getOptionalSuspendApproval() {
		return optionalSuspendApproval;
	}

	/**
	 * @param optionalSuspendApproval the optionalSuspendApproval to set
	 */
	public void setOptionalSuspendApproval(Boolean optionalSuspendApproval) {
		this.optionalSuspendApproval = optionalSuspendApproval;
	}

	/**
	 * @return the suspensionApprovals
	 */
	public List<TemplateSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<TemplateSuspensionApproval> suspensionApprovals) {
		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<TemplateSuspensionApproval>();
		} else {
			this.suspensionApprovals.clear();
		}
		if (suspensionApprovals != null) {
			this.suspensionApprovals.addAll(suspensionApprovals);
		}
	}

	/**
	 * @return the allowDisqualifiedSupplierDownload
	 */
	public Boolean getAllowDisqualifiedSupplierDownload() {
		return allowDisqualifiedSupplierDownload;
	}

	/**
	 * @param allowDisqualifiedSupplierDownload the allowDisqualifiedSupplierDownload to set
	 */
	public void setAllowDisqualifiedSupplierDownload(Boolean allowDisqualifiedSupplierDownload) {
		this.allowDisqualifiedSupplierDownload = allowDisqualifiedSupplierDownload;
	}
	
	/**
	 * @return the enableAwardApproval
	 */
	public Boolean getEnableAwardApproval() {
		return enableAwardApproval;
	}

	/**
	 * @return the visibleAwardApproval
	 */
	public Boolean getVisibleAwardApproval() {
		return visibleAwardApproval;
	}

	/**
	 * @return the readOnlyAwardApproval
	 */
	public Boolean getReadOnlyAwardApproval() {
		return readOnlyAwardApproval;
	}

	/**
	 * @return the optionalAwardApproval
	 */
	public Boolean getOptionalAwardApproval() {
		return optionalAwardApproval;
	}

	/**
	 * @param enableAwardApproval the enableAwardApproval to set
	 */
	public void setEnableAwardApproval(Boolean enableAwardApproval) {
		this.enableAwardApproval = enableAwardApproval;
	}

	/**
	 * @param visibleAwardApproval the visibleAwardApproval to set
	 */
	public void setVisibleAwardApproval(Boolean visibleAwardApproval) {
		this.visibleAwardApproval = visibleAwardApproval;
	}

	/**
	 * @param readOnlyAwardApproval the readOnlyAwardApproval to set
	 */
	public void setReadOnlyAwardApproval(Boolean readOnlyAwardApproval) {
		this.readOnlyAwardApproval = readOnlyAwardApproval;
	}

	/**
	 * @param optionalAwardApproval the optionalAwardApproval to set
	 */
	public void setOptionalAwardApproval(Boolean optionalAwardApproval) {
		this.optionalAwardApproval = optionalAwardApproval;
	}
	
	/**
	 * @return the awardApprovals
	 */
	public List<TemplateAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	public List<RfxTemplateDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RfxTemplateDocument> documents) {
		this.documents = documents;
	}

	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<TemplateAwardApproval> awardApprovals) {
		if (this.awardApprovals == null) {
			this.awardApprovals = new ArrayList<TemplateAwardApproval>();
		} else {
			this.awardApprovals.clear();
		}
		if (awardApprovals != null) {
			this.awardApprovals.addAll(awardApprovals);
		}
	}

	public Boolean getCompleteTemplateDetails() {
		return completeTemplateDetails;
	}

	public void setCompleteTemplateDetails(Boolean completeTemplateDetails) {
		this.completeTemplateDetails = completeTemplateDetails;
	}

	public Boolean getTemplateUsed() {
		return isTemplateUsed;
	}

	public void setTemplateUsed(Boolean templateUsed) {
		isTemplateUsed = templateUsed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approvalOptional == null) ? 0 : approvalOptional.hashCode());
		result = prime * result + ((approvalReadOnly == null) ? 0 : approvalReadOnly.hashCode());
		result = prime * result + ((approvalVisible == null) ? 0 : approvalVisible.hashCode());
		result = prime * result + ((autoPopulateSupplier == null) ? 0 : autoPopulateSupplier.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((readOnlySupplier == null) ? 0 : readOnlySupplier.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((supplierBasedOnCategory == null) ? 0 : supplierBasedOnCategory.hashCode());
		result = prime * result + ((templateAuctionType == null) ? 0 : templateAuctionType.hashCode());
		result = prime * result + ((templateDescription == null) ? 0 : templateDescription.hashCode());
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RfxTemplate other = (RfxTemplate) obj;
		if (approvalOptional == null) {
			if (other.approvalOptional != null)
				return false;
		} else if (!approvalOptional.equals(other.approvalOptional))
			return false;
		if (approvalReadOnly == null) {
			if (other.approvalReadOnly != null)
				return false;
		} else if (!approvalReadOnly.equals(other.approvalReadOnly))
			return false;
		if (approvalVisible == null) {
			if (other.approvalVisible != null)
				return false;
		} else if (!approvalVisible.equals(other.approvalVisible))
			return false;
		if (autoPopulateSupplier == null) {
			if (other.autoPopulateSupplier != null)
				return false;
		} else if (!autoPopulateSupplier.equals(other.autoPopulateSupplier))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (readOnlySupplier == null) {
			if (other.readOnlySupplier != null)
				return false;
		} else if (!readOnlySupplier.equals(other.readOnlySupplier))
			return false;
		if (status != other.status)
			return false;
		if (supplierBasedOnCategory == null) {
			if (other.supplierBasedOnCategory != null)
				return false;
		} else if (!supplierBasedOnCategory.equals(other.supplierBasedOnCategory))
			return false;
		if (templateAuctionType != other.templateAuctionType)
			return false;
		if (templateDescription == null) {
			if (other.templateDescription != null)
				return false;
		} else if (!templateDescription.equals(other.templateDescription))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public String toLogString() {
		return "RfxTemplate [id=" + id + ", templateName=" + templateName + ", templateDescription=" + templateDescription + ", type=" + type + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
	}
}
