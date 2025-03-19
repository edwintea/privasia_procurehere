/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_SUPPLIER_FORM_ITEM", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_SFI_TENANT_ID") })
public class SupplierFormItem implements Serializable {

	private static final long serialVersionUID = 6681304586532834515L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ITEM_NAME", nullable = false, length = 250)
	private String itemName;

	@Column(name = "ITEM_DESCRIPTION", length = 1050)
	private String itemDescription;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFI_ITEM_FOMR_ID"))
	private SupplierForm supplierForm;

	@ManyToOne(optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFI_ITEM_PARENT_ID"))
	private SupplierFormItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierFormItem> children;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierFormItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<SupplierFormItemOption> formOptions;

	@Column(name = "ITEM_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@Column(name = "CQ_TYPE", nullable = true)
	@Enumerated(EnumType.STRING)
	private CqType cqType;

	@Column(name = "IS_ATTACHMENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean attachment = Boolean.FALSE;

	@Column(name = "IS_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean optional = Boolean.FALSE;

	@Column(name = "IS_SUPPLIER_ATTACH_REQUIRED", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isSupplierAttachRequired = Boolean.FALSE;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "formItem", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<SupplierFormItemAttachment> formItemAttachments;

	@Transient
	private List<String> options;

	@Transient
	private List<SupplierFormItemOption> displayCqOptions;

	@Transient
	List<MultipartFile> fileAttachList;

	public SupplierFormItem() {
		this.attachment = Boolean.FALSE;
		this.isSupplierAttachRequired = Boolean.FALSE;
		this.optional = Boolean.FALSE;
	}

	public SupplierFormItem createShallowCopy() {
		SupplierFormItem ic = new SupplierFormItem();
		ic.setItemDescription(getItemDescription());
		ic.setItemName(getItemName());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		return ic;
	}

	public SupplierFormItem copyFrom(SupplierForm supplierForm, String tenantId) {
		SupplierFormItem newFormItem = new SupplierFormItem();
		newFormItem.setItemDescription(getItemDescription());
		newFormItem.setSupplierForm(supplierForm);
		newFormItem.setItemName(getItemName());
		newFormItem.setLevel(getLevel());
		newFormItem.setOrder(getOrder());
		newFormItem.setCqType(getCqType());
		newFormItem.setAttachment(getAttachment());
		newFormItem.setIsSupplierAttachRequired(getIsSupplierAttachRequired());
		newFormItem.setOptional(getOptional());
		newFormItem.setTenantId(tenantId);
		if (CollectionUtil.isNotEmpty(getFormOptions())) {
			List<SupplierFormItemOption> options = new ArrayList<SupplierFormItemOption>();
			for (SupplierFormItemOption formOption : getFormOptions()) {
				SupplierFormItemOption newCqOption = formOption.copyFrom(newFormItem, tenantId);
				options.add(newCqOption);
			}
			newFormItem.setFormOptions(options);
		}
		if (CollectionUtil.isNotEmpty(getFormItemAttachments())) {
			List<SupplierFormItemAttachment> attachmentList = new ArrayList<SupplierFormItemAttachment>();
			for (SupplierFormItemAttachment attach : getFormItemAttachments()) {
				SupplierFormItemAttachment newAttach = attach.copyFrom(newFormItem, tenantId);
				attachmentList.add(newAttach);
			}
			newFormItem.setFormItemAttachments(attachmentList);
		}
		return newFormItem;
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

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the supplierForm
	 */
	public SupplierForm getSupplierForm() {
		return supplierForm;
	}

	/**
	 * @param supplierForm the supplierForm to set
	 */
	public void setSupplierForm(SupplierForm supplierForm) {
		this.supplierForm = supplierForm;
	}

	/**
	 * @return the parent
	 */
	public SupplierFormItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SupplierFormItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<SupplierFormItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<SupplierFormItem> children) {
		if (this.children == null) {
			this.children = new ArrayList<SupplierFormItem>();
		} else {
			this.children.clear();
		}
		if (children != null) {
			this.children.addAll(children);
		}
	}

	/**
	 * @return the formOptions
	 */
	public List<SupplierFormItemOption> getFormOptions() {
		return formOptions;
	}

	/**
	 * @param formOptions the formOptions to set
	 */
	public void setFormOptions(List<SupplierFormItemOption> formOptions) {
		if (this.formOptions == null) {
			this.formOptions = new ArrayList<SupplierFormItemOption>();
		} else {
			this.formOptions.clear();
		}
		if (formOptions != null) {
			this.formOptions.addAll(formOptions);
		}
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @return the cqType
	 */
	public CqType getCqType() {
		return cqType;
	}

	/**
	 * @param cqType the cqType to set
	 */
	public void setCqType(CqType cqType) {
		this.cqType = cqType;
	}

	/**
	 * @return the attachment
	 */
	public Boolean getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(Boolean attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the optional
	 */
	public Boolean getOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the isSupplierAttachRequired
	 */
	public Boolean getIsSupplierAttachRequired() {
		return isSupplierAttachRequired;
	}

	/**
	 * @param isSupplierAttachRequired the isSupplierAttachRequired to set
	 */
	public void setIsSupplierAttachRequired(Boolean isSupplierAttachRequired) {
		this.isSupplierAttachRequired = isSupplierAttachRequired;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the formItemAttachments
	 */
	public List<SupplierFormItemAttachment> getFormItemAttachments() {
		return formItemAttachments;
	}

	/**
	 * @param formItemAttachments the formItemAttachments to set
	 */
	public void setFormItemAttachments(List<SupplierFormItemAttachment> formItemAttachments) {
		if (this.formItemAttachments == null) {
			this.formItemAttachments = new ArrayList<SupplierFormItemAttachment>();
		} else {
			this.formItemAttachments.clear();
		}
		if (formItemAttachments != null) {
			this.formItemAttachments.addAll(formItemAttachments);
		}
	}

	/**
	 * @return the options
	 */
	public List<String> getOptions() {
		if (options == null) {
			options = new ArrayList<String>();
		}
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * @return the displayCqOptions
	 */
	public List<SupplierFormItemOption> getDisplayCqOptions() {
		if (getFormOptions() != null) {
			for (SupplierFormItemOption option : getFormOptions()) {
				SupplierFormItemOption op = new SupplierFormItemOption();
				op.setId(option.getId());
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				if (displayCqOptions == null)
					displayCqOptions = new ArrayList<SupplierFormItemOption>();
				displayCqOptions.add(op);
			}
		}
		return displayCqOptions;
	}

	/**
	 * @param displayCqOptions the displayCqOptions to set
	 */
	public void setDisplayCqOptions(List<SupplierFormItemOption> displayCqOptions) {
		this.displayCqOptions = displayCqOptions;
	}

	/**
	 * @return the fileAttachList
	 */
	public List<MultipartFile> getFileAttachList() {
		return fileAttachList;
	}

	/**
	 * @param fileAttachList the fileAttachList to set
	 */
	public void setFileAttachList(List<MultipartFile> fileAttachList) {
		this.fileAttachList = fileAttachList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

}
