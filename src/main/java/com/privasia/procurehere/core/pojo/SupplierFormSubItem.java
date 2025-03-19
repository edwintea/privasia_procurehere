/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.SupplierFormSubmissionItem;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionItemOption;
import com.privasia.procurehere.core.utils.CollectionUtil;

public class SupplierFormSubItem implements Serializable {

	private static final long serialVersionUID = -1204899549621550287L;

	private String id;

	private SupplierFormSubmition formSub;

	private SupplierFormItem formItem;

	private List<SupplierFormItemOption> listAnswers;

	private String textAnswers;

	private FavouriteSupplier favSupplier;

	private byte[] fileData;

	private String fileName;

	private String credContentType;

	private List<SupplierFormItemOption> listOptAnswers;

	private MultipartFile attachment;

	private List<SupplierFormItemAttachment> itemAttachment;

	public SupplierFormSubItem() {
	}

	public SupplierFormSubItem(SupplierFormSubmissionItem formObj) {
		this.setId(formObj.getId());
		this.setFormSub(formObj.getSupplierFormSubmition());
		this.setFormItem(formObj.getSupplierFormItem());
		if (CollectionUtil.isNotEmpty(formObj.getListAnswers())) {
			listAnswers = new ArrayList<SupplierFormItemOption>();
			for (SupplierFormSubmitionItemOption option : formObj.getListAnswers()) {
				SupplierFormItemOption op = new SupplierFormItemOption();
				op.setOrder(option.getOrder());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}
		this.setTextAnswers(formObj.getTextAnswers());
		this.setFileData(formObj.getFileData());
		this.setFileName(formObj.getFileName());
		this.setCredContentType(formObj.getContentType());
		listOptAnswers = new ArrayList<SupplierFormItemOption>();
		if (CollectionUtil.isNotEmpty(formObj.getListAnswers())) {
			for (SupplierFormSubmitionItemOption option : formObj.getListAnswers()) {
				SupplierFormItemOption op = new SupplierFormItemOption();
				op.setOrder(option.getOrder());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

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
	 * @return the formSub
	 */
	public SupplierFormSubmition getFormSub() {
		return formSub;
	}

	/**
	 * @param formSub the formSub to set
	 */
	public void setFormSub(SupplierFormSubmition formSub) {
		this.formSub = formSub;
	}

	/**
	 * @return the formItem
	 */
	public SupplierFormItem getFormItem() {
		return formItem;
	}

	/**
	 * @param formItem the formItem to set
	 */
	public void setFormItem(SupplierFormItem formItem) {
		this.formItem = formItem;
	}

	/**
	 * @return the listAnswers
	 */
	public List<SupplierFormItemOption> getListAnswers() {
		return listAnswers;
	}

	/**
	 * @param listAnswers the listAnswers to set
	 */
	public void setListAnswers(List<SupplierFormItemOption> listAnswers) {
		this.listAnswers = listAnswers;
	}

	/**
	 * @return the textAnswers
	 */
	public String getTextAnswers() {
		return textAnswers;
	}

	/**
	 * @param textAnswers the textAnswers to set
	 */
	public void setTextAnswers(String textAnswers) {
		this.textAnswers = textAnswers;
	}

	/**
	 * @return the favSupplier
	 */
	public FavouriteSupplier getFavSupplier() {
		return favSupplier;
	}

	/**
	 * @param favSupplier the favSupplier to set
	 */
	public void setFavSupplier(FavouriteSupplier favSupplier) {
		this.favSupplier = favSupplier;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
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
	 * @return the credContentType
	 */
	public String getCredContentType() {
		return credContentType;
	}

	/**
	 * @param credContentType the credContentType to set
	 */
	public void setCredContentType(String credContentType) {
		this.credContentType = credContentType;
	}

	/**
	 * @return the listOptAnswers
	 */
	public List<SupplierFormItemOption> getListOptAnswers() {
		return listOptAnswers;
	}

	/**
	 * @param listOptAnswers the listOptAnswers to set
	 */
	public void setListOptAnswers(List<SupplierFormItemOption> listOptAnswers) {
		this.listOptAnswers = listOptAnswers;
	}

	/**
	 * @return the attachment
	 */
	public MultipartFile getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(MultipartFile attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the itemAttachment
	 */
	public List<SupplierFormItemAttachment> getItemAttachment() {
		return itemAttachment;
	}

	/**
	 * @param itemAttachment the itemAttachment to set
	 */
	public void setItemAttachment(List<SupplierFormItemAttachment> itemAttachment) {
		this.itemAttachment = itemAttachment;
	}

}
