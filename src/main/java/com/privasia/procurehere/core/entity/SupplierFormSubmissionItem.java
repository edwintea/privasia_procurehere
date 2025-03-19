/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.pojo.SupplierFormSubItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SUPPLIER_FORM_SUBM_ITEM")
public class SupplierFormSubmissionItem implements Serializable {

	private static final long serialVersionUID = 1561033800614030321L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_ITEM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFS_FORM_ITEM_ID"))
	private SupplierFormItem supplierFormItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_SUB_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFS_SUB_FORM_ID"))
	private SupplierFormSubmition supplierFormSubmition;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierFormSubmitiomItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<SupplierFormSubmitionItemOption> listAnswers;

	@Column(name = "TEXT_ANSWERS", length = 1500)
	private String textAnswers;

	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA")
	private byte[] fileData;

	@Column(name = "FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "CONTENT_TYPE", length = 160)
	private String contentType;

	@Transient
	private List<SupplierFormItemOption> listOptAnswers;

	@Transient
	private MultipartFile attachment;

	public SupplierFormSubmissionItem() {
	}

	public SupplierFormSubmissionItem(SupplierFormItem item) {
		this.setSupplierFormItem(item);
	}

	public SupplierFormSubmissionItem(SupplierFormSubItem item) {
		this.setId(item.getId());
		this.setSupplierFormItem(item.getFormItem());
		this.setSupplierFormSubmition(item.getFormSub());
		if (CollectionUtil.isNotEmpty(item.getListOptAnswers())) {
			List<SupplierFormSubmitionItemOption> optionList = new ArrayList<SupplierFormSubmitionItemOption>();
			for (SupplierFormItemOption option : item.getListOptAnswers()) {
				if (option != null) {
					SupplierFormSubmitionItemOption op = new SupplierFormSubmitionItemOption();
					op.setId(option.getId());
					optionList.add(op);
				}
			}
			this.setListAnswers(optionList);
		}
		LOG.info("item.getTextAnswers()  : " + item.getTextAnswers());
		this.setTextAnswers(item.getTextAnswers());
		if (item.getAttachment() != null) {
			try {
				this.setFileData(item.getAttachment().getBytes());
			} catch (IOException e) {
			}
			this.setFileName(item.getAttachment().getOriginalFilename());
			LOG.info("File Name............" + item.getAttachment().getOriginalFilename());
			this.setContentType(item.getAttachment().getContentType());
		}
		listOptAnswers = new ArrayList<SupplierFormItemOption>();
		if (CollectionUtil.isNotEmpty(item.getListOptAnswers())) {
			for (SupplierFormItemOption option : item.getListOptAnswers()) {
				if (option != null) {
					SupplierFormItemOption op = new SupplierFormItemOption();
					op.setId(option.getId());
					listOptAnswers.add(op);
				}
			}
			this.setListOptAnswers(listOptAnswers);
		}
	}

	public SupplierFormSubmissionItem createShallowCopy() {
		SupplierFormSubmissionItem ic = new SupplierFormSubmissionItem();
		ic.setId(getId());
		ic.setSupplierFormItem(getSupplierFormItem());
		ic.setSupplierFormSubmition(getSupplierFormSubmition());
		ic.setFileData(getFileData());
		ic.setFileName(getFileName());
		ic.setContentType(getContentType());
		if (ic.getSupplierFormItem().getChildren() != null) {
			ic.getSupplierFormItem().setChildren(null);
		}
		if (ic.getSupplierFormItem().getParent() != null) {
			ic.getSupplierFormItem().getParent().setChildren(null);
		}
		ic.setListAnswers(getListAnswers());
		ic.setTextAnswers(getTextAnswers());
		return ic;
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
	 * @return the supplierFormItem
	 */
	public SupplierFormItem getSupplierFormItem() {
		return supplierFormItem;
	}

	/**
	 * @param supplierFormItem the supplierFormItem to set
	 */
	public void setSupplierFormItem(SupplierFormItem supplierFormItem) {
		this.supplierFormItem = supplierFormItem;
	}

	/**
	 * @return the supplierFormSubmition
	 */
	public SupplierFormSubmition getSupplierFormSubmition() {
		return supplierFormSubmition;
	}

	/**
	 * @param supplierFormSubmition the supplierFormSubmition to set
	 */
	public void setSupplierFormSubmition(SupplierFormSubmition supplierFormSubmition) {
		this.supplierFormSubmition = supplierFormSubmition;
	}

	/**
	 * @return the listAnswers
	 */
	public List<SupplierFormSubmitionItemOption> getListAnswers() {
		return listAnswers;
	}

	/**
	 * @param listAnswers the listAnswers to set
	 */
	public void setListAnswers(List<SupplierFormSubmitionItemOption> listAnswers) {
		if (this.listAnswers == null) {
			this.listAnswers = new ArrayList<SupplierFormSubmitionItemOption>();
		} else {
			this.listAnswers.clear();
		}
		if (listAnswers != null) {
			this.listAnswers.addAll(listAnswers);
		}
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

}
