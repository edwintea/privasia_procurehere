/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.BqUserTypes;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */
@MappedSuperclass
public class Bq implements Serializable {

	private static final long serialVersionUID = 8446708672025636557L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "NAME", length = 128)
	private String name;

	@Column(name = "DESCRIPTION", length = 550)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Column(name = "FIELD1_LABEL", nullable = true, length = 32)
	private String field1Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD1_FILLEDBY")
	private BqUserTypes field1FilledBy;

	@Column(name = "FIELD1_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field1ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD1_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field1Required = Boolean.FALSE;

	@Column(name = "FIELD2_LABEL", nullable = true, length = 32)
	private String field2Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD2_FILLEDBY")
	private BqUserTypes field2FilledBy;

	@Column(name = "FIELD2_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field2ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD2_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field2Required = Boolean.FALSE;

	@Column(name = "FIELD2_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field2Visible = Boolean.FALSE;

	@Column(name = "FIELD3_LABEL", nullable = true, length = 32)
	private String field3Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD3_FILLEDBY")
	private BqUserTypes field3FilledBy;

	@Column(name = "FIELD3_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field3ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD3_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field3Required = Boolean.FALSE;

	@Column(name = "FIELD3_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field3Visible = Boolean.FALSE;

	@Column(name = "FIELD4_LABEL", nullable = true, length = 32)
	private String field4Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD4_FILLEDBY")
	private BqUserTypes field4FilledBy;

	@Column(name = "FIELD4_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field4ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD4_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field4Required = Boolean.FALSE;

	@Column(name = "FIELD4_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field4Visible = Boolean.FALSE;

	@Column(name = "FIELD5_LABEL", nullable = true, length = 32)
	private String field5Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD5_FILLEDBY")
	private BqUserTypes field5FilledBy;

	@Column(name = "FIELD5_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field5ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD5_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field5Required = Boolean.FALSE;

	@Column(name = "FIELD5_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field5Visible = Boolean.FALSE;

	@Column(name = "FIELD6_LABEL", nullable = true, length = 32)
	private String field6Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD6_FILLEDBY")
	private BqUserTypes field6FilledBy;

	@Column(name = "FIELD6_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field6ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD6_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field6Required = Boolean.FALSE;

	@Column(name = "FIELD6_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field6Visible = Boolean.FALSE;

	@Column(name = "FIELD7_LABEL", nullable = true, length = 32)
	private String field7Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD7_FILLEDBY")
	private BqUserTypes field7FilledBy;

	@Column(name = "FIELD7_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field7ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD7_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field7Required = Boolean.FALSE;

	@Column(name = "FIELD7_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field7Visible = Boolean.FALSE;

	@Column(name = "FIELD8_LABEL", nullable = true, length = 32)
	private String field8Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD8_FILLEDBY")
	private BqUserTypes field8FilledBy;

	@Column(name = "FIELD8_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field8ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD8_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field8Required = Boolean.FALSE;

	@Column(name = "FIELD8_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field8Visible = Boolean.FALSE;

	@Column(name = "FIELD9_LABEL", nullable = true, length = 32)
	private String field9Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD9_FILLEDBY")
	private BqUserTypes field9FilledBy;

	@Column(name = "FIELD9_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field9ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD9_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field9Required = Boolean.FALSE;

	@Column(name = "FIELD9_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field9Visible = Boolean.FALSE;

	@Column(name = "FIELD10_LABEL", nullable = true, length = 32)
	private String field10Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "FIELD10_FILLEDBY")
	private BqUserTypes field10FilledBy;

	@Column(name = "FIELD10_SHOW_SUPPLIER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field10ToShowSupplier = Boolean.FALSE;

	@Column(name = "FIELD10_REQUIRED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field10Required = Boolean.FALSE;

	@Column(name = "FIELD10_VISIBLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean field10Visible = Boolean.FALSE;
	
	@Column(name = "BQ_ORDER")
	private Integer bqOrder;

	@Transient
	Integer headerCount;

	@Transient
	List<BqItem> bqItem;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the field1FilledBy
	 */
	public BqUserTypes getField1FilledBy() {
		return field1FilledBy;
	}

	/**
	 * @param field1FilledBy the field1FilledBy to set
	 */
	public void setField1FilledBy(BqUserTypes field1FilledBy) {
		this.field1FilledBy = field1FilledBy;
	}

	/**
	 * @return the field1ToShowSupplier
	 */
	public Boolean getField1ToShowSupplier() {
		return field1ToShowSupplier;
	}

	/**
	 * @param field1ToShowSupplier the field1ToShowSupplier to set
	 */
	public void setField1ToShowSupplier(Boolean field1ToShowSupplier) {
		this.field1ToShowSupplier = field1ToShowSupplier;
	}

	/**
	 * @return the field1Required
	 */
	public Boolean getField1Required() {
		return field1Required;
	}

	/**
	 * @param field1Required the field1Required to set
	 */
	public void setField1Required(Boolean field1Required) {
		this.field1Required = field1Required;
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
	 * @return the field2FilledBy
	 */
	public BqUserTypes getField2FilledBy() {
		return field2FilledBy;
	}

	/**
	 * @param field2FilledBy the field2FilledBy to set
	 */
	public void setField2FilledBy(BqUserTypes field2FilledBy) {
		this.field2FilledBy = field2FilledBy;
	}

	/**
	 * @return the field2ToShowSupplier
	 */
	public Boolean getField2ToShowSupplier() {
		return field2ToShowSupplier;
	}

	/**
	 * @param field2ToShowSupplier the field2ToShowSupplier to set
	 */
	public void setField2ToShowSupplier(Boolean field2ToShowSupplier) {
		this.field2ToShowSupplier = field2ToShowSupplier;
	}

	/**
	 * @return the field2Required
	 */
	public Boolean getField2Required() {
		return field2Required;
	}

	/**
	 * @param field2Required the field2Required to set
	 */
	public void setField2Required(Boolean field2Required) {
		this.field2Required = field2Required;
	}

	/**
	 * @return the field2Visible
	 */
	public Boolean getField2Visible() {
		return field2Visible;
	}

	/**
	 * @param field2Visible the field2Visible to set
	 */
	public void setField2Visible(Boolean field2Visible) {
		this.field2Visible = field2Visible;
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
	 * @return the field3FilledBy
	 */
	public BqUserTypes getField3FilledBy() {
		return field3FilledBy;
	}

	/**
	 * @param field3FilledBy the field3FilledBy to set
	 */
	public void setField3FilledBy(BqUserTypes field3FilledBy) {
		this.field3FilledBy = field3FilledBy;
	}

	/**
	 * @return the field3ToShowSupplier
	 */
	public Boolean getField3ToShowSupplier() {
		return field3ToShowSupplier;
	}

	/**
	 * @param field3ToShowSupplier the field3ToShowSupplier to set
	 */
	public void setField3ToShowSupplier(Boolean field3ToShowSupplier) {
		this.field3ToShowSupplier = field3ToShowSupplier;
	}

	/**
	 * @return the field3Required
	 */
	public Boolean getField3Required() {
		return field3Required;
	}

	/**
	 * @param field3Required the field3Required to set
	 */
	public void setField3Required(Boolean field3Required) {
		this.field3Required = field3Required;
	}

	/**
	 * @return the field3Visible
	 */
	public Boolean getField3Visible() {
		return field3Visible;
	}

	/**
	 * @param field3Visible the field3Visible to set
	 */
	public void setField3Visible(Boolean field3Visible) {
		this.field3Visible = field3Visible;
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
	 * @return the field4FilledBy
	 */
	public BqUserTypes getField4FilledBy() {
		return field4FilledBy;
	}

	/**
	 * @param field4FilledBy the field4FilledBy to set
	 */
	public void setField4FilledBy(BqUserTypes field4FilledBy) {
		this.field4FilledBy = field4FilledBy;
	}

	/**
	 * @return the field4ToShowSupplier
	 */
	public Boolean getField4ToShowSupplier() {
		return field4ToShowSupplier;
	}

	/**
	 * @param field4ToShowSupplier the field4ToShowSupplier to set
	 */
	public void setField4ToShowSupplier(Boolean field4ToShowSupplier) {
		this.field4ToShowSupplier = field4ToShowSupplier;
	}

	/**
	 * @return the field4Required
	 */
	public Boolean getField4Required() {
		return field4Required;
	}

	/**
	 * @param field4Required the field4Required to set
	 */
	public void setField4Required(Boolean field4Required) {
		this.field4Required = field4Required;
	}

	/**
	 * @return the field4Visible
	 */
	public Boolean getField4Visible() {
		return field4Visible;
	}

	/**
	 * @param field4Visible the field4Visible to set
	 */
	public void setField4Visible(Boolean field4Visible) {
		this.field4Visible = field4Visible;
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
	 * @return the field5FilledBy
	 */
	public BqUserTypes getField5FilledBy() {
		return field5FilledBy;
	}

	/**
	 * @param field5FilledBy the field5FilledBy to set
	 */
	public void setField5FilledBy(BqUserTypes field5FilledBy) {
		this.field5FilledBy = field5FilledBy;
	}

	/**
	 * @return the field5ToShowSupplier
	 */
	public Boolean getField5ToShowSupplier() {
		return field5ToShowSupplier;
	}

	/**
	 * @param field5ToShowSupplier the field5ToShowSupplier to set
	 */
	public void setField5ToShowSupplier(Boolean field5ToShowSupplier) {
		this.field5ToShowSupplier = field5ToShowSupplier;
	}

	/**
	 * @return the field5Required
	 */
	public Boolean getField5Required() {
		return field5Required;
	}

	/**
	 * @param field5Required the field5Required to set
	 */
	public void setField5Required(Boolean field5Required) {
		this.field5Required = field5Required;
	}

	/**
	 * @return the field5Visible
	 */
	public Boolean getField5Visible() {
		return field5Visible;
	}

	/**
	 * @param field5Visible the field5Visible to set
	 */
	public void setField5Visible(Boolean field5Visible) {
		this.field5Visible = field5Visible;
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
	 * @return the field6FilledBy
	 */
	public BqUserTypes getField6FilledBy() {
		return field6FilledBy;
	}

	/**
	 * @param field6FilledBy the field6FilledBy to set
	 */
	public void setField6FilledBy(BqUserTypes field6FilledBy) {
		this.field6FilledBy = field6FilledBy;
	}

	/**
	 * @return the field6ToShowSupplier
	 */
	public Boolean getField6ToShowSupplier() {
		return field6ToShowSupplier;
	}

	/**
	 * @param field6ToShowSupplier the field6ToShowSupplier to set
	 */
	public void setField6ToShowSupplier(Boolean field6ToShowSupplier) {
		this.field6ToShowSupplier = field6ToShowSupplier;
	}

	/**
	 * @return the field6Required
	 */
	public Boolean getField6Required() {
		return field6Required;
	}

	/**
	 * @param field6Required the field6Required to set
	 */
	public void setField6Required(Boolean field6Required) {
		this.field6Required = field6Required;
	}

	/**
	 * @return the field6Visible
	 */
	public Boolean getField6Visible() {
		return field6Visible;
	}

	/**
	 * @param field6Visible the field6Visible to set
	 */
	public void setField6Visible(Boolean field6Visible) {
		this.field6Visible = field6Visible;
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
	 * @return the field7FilledBy
	 */
	public BqUserTypes getField7FilledBy() {
		return field7FilledBy;
	}

	/**
	 * @param field7FilledBy the field7FilledBy to set
	 */
	public void setField7FilledBy(BqUserTypes field7FilledBy) {
		this.field7FilledBy = field7FilledBy;
	}

	/**
	 * @return the field7ToShowSupplier
	 */
	public Boolean getField7ToShowSupplier() {
		return field7ToShowSupplier;
	}

	/**
	 * @param field7ToShowSupplier the field7ToShowSupplier to set
	 */
	public void setField7ToShowSupplier(Boolean field7ToShowSupplier) {
		this.field7ToShowSupplier = field7ToShowSupplier;
	}

	/**
	 * @return the field7Required
	 */
	public Boolean getField7Required() {
		return field7Required;
	}

	/**
	 * @param field7Required the field7Required to set
	 */
	public void setField7Required(Boolean field7Required) {
		this.field7Required = field7Required;
	}

	/**
	 * @return the field7Visible
	 */
	public Boolean getField7Visible() {
		return field7Visible;
	}

	/**
	 * @param field7Visible the field7Visible to set
	 */
	public void setField7Visible(Boolean field7Visible) {
		this.field7Visible = field7Visible;
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
	 * @return the field8FilledBy
	 */
	public BqUserTypes getField8FilledBy() {
		return field8FilledBy;
	}

	/**
	 * @param field8FilledBy the field8FilledBy to set
	 */
	public void setField8FilledBy(BqUserTypes field8FilledBy) {
		this.field8FilledBy = field8FilledBy;
	}

	/**
	 * @return the field8ToShowSupplier
	 */
	public Boolean getField8ToShowSupplier() {
		return field8ToShowSupplier;
	}

	/**
	 * @param field8ToShowSupplier the field8ToShowSupplier to set
	 */
	public void setField8ToShowSupplier(Boolean field8ToShowSupplier) {
		this.field8ToShowSupplier = field8ToShowSupplier;
	}

	/**
	 * @return the field8Required
	 */
	public Boolean getField8Required() {
		return field8Required;
	}

	/**
	 * @param field8Required the field8Required to set
	 */
	public void setField8Required(Boolean field8Required) {
		this.field8Required = field8Required;
	}

	/**
	 * @return the field8Visible
	 */
	public Boolean getField8Visible() {
		return field8Visible;
	}

	/**
	 * @param field8Visible the field8Visible to set
	 */
	public void setField8Visible(Boolean field8Visible) {
		this.field8Visible = field8Visible;
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
	 * @return the field9FilledBy
	 */
	public BqUserTypes getField9FilledBy() {
		return field9FilledBy;
	}

	/**
	 * @param field9FilledBy the field9FilledBy to set
	 */
	public void setField9FilledBy(BqUserTypes field9FilledBy) {
		this.field9FilledBy = field9FilledBy;
	}

	/**
	 * @return the field9ToShowSupplier
	 */
	public Boolean getField9ToShowSupplier() {
		return field9ToShowSupplier;
	}

	/**
	 * @param field9ToShowSupplier the field9ToShowSupplier to set
	 */
	public void setField9ToShowSupplier(Boolean field9ToShowSupplier) {
		this.field9ToShowSupplier = field9ToShowSupplier;
	}

	/**
	 * @return the field9Required
	 */
	public Boolean getField9Required() {
		return field9Required;
	}

	/**
	 * @param field9Required the field9Required to set
	 */
	public void setField9Required(Boolean field9Required) {
		this.field9Required = field9Required;
	}

	/**
	 * @return the field9Visible
	 */
	public Boolean getField9Visible() {
		return field9Visible;
	}

	/**
	 * @param field9Visible the field9Visible to set
	 */
	public void setField9Visible(Boolean field9Visible) {
		this.field9Visible = field9Visible;
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
	 * @return the field10FilledBy
	 */
	public BqUserTypes getField10FilledBy() {
		return field10FilledBy;
	}

	/**
	 * @param field10FilledBy the field10FilledBy to set
	 */
	public void setField10FilledBy(BqUserTypes field10FilledBy) {
		this.field10FilledBy = field10FilledBy;
	}

	/**
	 * @return the field10ToShowSupplier
	 */
	public Boolean getField10ToShowSupplier() {
		return field10ToShowSupplier;
	}

	/**
	 * @param field10ToShowSupplier the field10ToShowSupplier to set
	 */
	public void setField10ToShowSupplier(Boolean field10ToShowSupplier) {
		this.field10ToShowSupplier = field10ToShowSupplier;
	}

	/**
	 * @return the field10Required
	 */
	public Boolean getField10Required() {
		return field10Required;
	}

	/**
	 * @param field10Required the field10Required to set
	 */
	public void setField10Required(Boolean field10Required) {
		this.field10Required = field10Required;
	}

	/**
	 * @return the field10Visible
	 */
	public Boolean getField10Visible() {
		return field10Visible;
	}

	/**
	 * @param field10Visible the field10Visible to set
	 */
	public void setField10Visible(Boolean field10Visible) {
		this.field10Visible = field10Visible;
	}

	/**
	 * @return the bqItem
	 */
	public List<BqItem> getBqItem() {
		return bqItem;
	}

	/**
	 * @param bqItem the bqItem to set
	 */
	public void setBqItem(List<BqItem> bqItem) {
		this.bqItem = bqItem;
	}

	/**
	 * @return the headerCount
	 */
	public Integer getHeaderCount() {
		return headerCount;
	}

	/**
	 * @param headerCount the headerCount to set
	 */
	public void setHeaderCount(Integer headerCount) {
		this.headerCount = headerCount;
	}

	/**
	 * @return the bqOrder
	 */
	public Integer getBqOrder() {
		return bqOrder;
	}

	/**
	 * @param bqOrder the bqOrder to set
	 */
	public void setBqOrder(Integer bqOrder) {
		this.bqOrder = bqOrder;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
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
		Bq other = (Bq) obj;
		if (modifiedDate == null) {
			if (other.modifiedDate != null)
				return false;
		} else if (!modifiedDate.equals(other.modifiedDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		return true;
	}

	public String toLogString() {
		return "BillOfQuantity [name=" + name + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}

}
