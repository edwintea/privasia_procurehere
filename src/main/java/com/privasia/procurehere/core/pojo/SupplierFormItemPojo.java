package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierFormItemPojo implements Serializable {
	private static final long serialVersionUID = -4710268788286125788L;
	private String id;
	private String itemName;
	private Integer level;
	private Integer order;
	private String supplierForm;
	private String parent;
	private String itemDescription;
	private String cqType;
	private boolean attachment;
	private boolean optional;
	private List<String> options;
	private List<String> optionScore;
	private Boolean isSupplierAttachRequired;
	private MultipartFile[] itemAttachFilesArr;
	private List<MultipartFile> itemAttachFiles;
	private List<SupplierFormItemAttachment> formItemAttachments;

	public SupplierFormItemPojo() {
	}

	public SupplierFormItemPojo(SupplierFormItem item) {
		this.itemName = item.getId();
		this.itemName = item.getItemName();
		this.level = item.getLevel();
		this.order = item.getOrder();
		this.supplierForm = item.getSupplierForm().getId();
		this.parent = item.getParent() != null ? item.getParent().getId() : null;
		this.itemDescription = item.getItemDescription();
		this.cqType = item.getCqType() != null ? item.getCqType().toString() : "";
		this.attachment = item.getAttachment() != null ? item.getAttachment() : false;
		this.isSupplierAttachRequired = item.getIsSupplierAttachRequired() != null ? item.getIsSupplierAttachRequired() : false;
		this.optional = item.getOptional() != null ? item.getOptional() : false;
		this.options = item.getFormOptions() != null ? getOptionValues(item.getFormOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(item.getFormOptions()) ? getFormOptionScores(item.getFormOptions()) : null;
		if (CollectionUtil.isNotEmpty(item.getFormItemAttachments())) {
			List<SupplierFormItemAttachment> formItemAttachments = new ArrayList<SupplierFormItemAttachment>();
			for (SupplierFormItemAttachment formAttachmentData : item.getFormItemAttachments()) {
				SupplierFormItemAttachment formAttachMentObj = new SupplierFormItemAttachment();
				formAttachMentObj.setFileName(formAttachmentData.getFileName());
				formAttachMentObj.setFileData(formAttachmentData.getFileData());
				formAttachMentObj.setId(formAttachmentData.getId());
				formAttachMentObj.setContentType(formAttachmentData.getContentType());
				formItemAttachments.add(formAttachMentObj);
			}
			this.formItemAttachments = formItemAttachments;
		}
	}

	private List<String> getOptionValues(List<SupplierFormItemOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (SupplierFormItemOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getFormOptionScores(List<SupplierFormItemOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (SupplierFormItemOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
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
	 * @return the supplierForm
	 */
	public String getSupplierForm() {
		return supplierForm;
	}

	/**
	 * @param supplierForm the supplierForm to set
	 */
	public void setSupplierForm(String supplierForm) {
		this.supplierForm = supplierForm;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
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
	 * @return the cqType
	 */
	public String getCqType() {
		return cqType;
	}

	/**
	 * @param cqType the cqType to set
	 */
	public void setCqType(String cqType) {
		this.cqType = cqType;
	}

	/**
	 * @return the attachment
	 */
	public boolean isAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the options
	 */
	public List<String> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * @return the optionScore
	 */
	public List<String> getOptionScore() {
		return optionScore;
	}

	/**
	 * @param optionScore the optionScore to set
	 */
	public void setOptionScore(List<String> optionScore) {
		this.optionScore = optionScore;
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
	 * @return the itemAttachFilesArr
	 */
	public MultipartFile[] getItemAttachFilesArr() {
		return itemAttachFilesArr;
	}

	/**
	 * @param itemAttachFilesArr the itemAttachFilesArr to set
	 */
	public void setItemAttachFilesArr(MultipartFile[] itemAttachFilesArr) {
		this.itemAttachFilesArr = itemAttachFilesArr;
	}

	/**
	 * @return the itemAttachFiles
	 */
	public List<MultipartFile> getItemAttachFiles() {
		return itemAttachFiles;
	}

	/**
	 * @param itemAttachFiles the itemAttachFiles to set
	 */
	public void setItemAttachFiles(List<MultipartFile> itemAttachFiles) {
		this.itemAttachFiles = itemAttachFiles;
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
		this.formItemAttachments = formItemAttachments;
	}

}
