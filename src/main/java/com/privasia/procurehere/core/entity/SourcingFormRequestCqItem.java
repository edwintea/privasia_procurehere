/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.pojo.SourcingFormReqCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOUR_FORM_CQ_ITEM_REQ")
public class SourcingFormRequestCqItem implements Serializable {

	private static final long serialVersionUID = -3886431885670707069L;

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "SOUR_FORM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_REQ_CQ_FORM"))
	private SourcingFormTemplate sourcingForm;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_CQ_ITEM__ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_REQ_CQ_ITEM"))
	private SourcingTemplateCqItem cqItem;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_CQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SF_TEMP_REQ_CQ_ID"))
	private SourcingTemplateCq cq;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "formCqItemRequest", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<SourcingFormRequestCqOption> listAnswers;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_REQ_ID", foreignKey = @ForeignKey(name = "FK_SF_REQ_FORM_ID"))
	private SourcingFormRequest sourcingFormRequest;

	@Column(name = "TEXT_ANSWERS", length = 1000)
	private String textAnswers;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "SOUR_REQ_ATTACH_FILE_DATA")
	private byte[] fileData;

	@Column(name = "SOUR_REQ_ATTACH_FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "SOUR_REQ_ATTACH_CONT_TYPE", length = 160)
	private String credContentType;

	@Transient
	private List<SourcingFormCqOption> listOptAnswers;

	@Transient
	private MultipartFile attachment;

	public SourcingFormRequestCqItem() {

	}

	public SourcingFormRequestCqItem(SourcingTemplateCqItem cqItem) {
		this.setCq(cqItem.getCq());
		this.setCqItem(cqItem);
		this.setSourcingForm(cqItem.getSourcingForm());
	}

	public SourcingFormRequestCqItem(SourcingFormReqCqItem item) {
		this.setId(item.getId());
		this.setSourcingForm(item.getSourcingFormRequest().getSourcingForm());
		this.setCq((SourcingTemplateCq) item.getCq());
		this.setCqItem((SourcingTemplateCqItem) item.getCqItem());
		if (CollectionUtil.isNotEmpty(item.getListOptAnswers())) {
			List<SourcingFormRequestCqOption> cqOptionList = new ArrayList<SourcingFormRequestCqOption>();
			for (CqOption option : item.getListOptAnswers()) {
				if (option != null) {
					SourcingFormRequestCqOption op = new SourcingFormRequestCqOption();
					op.setId(option.getId());
					op.setOrder(option.getOrder());
					cqOptionList.add(op);
				}
			}
			this.setListAnswers(cqOptionList);
		}
		this.setTextAnswers(item.getTextAnswers());
		this.setSourcingFormRequest(item.getSourcingFormRequest());
		if (item.getAttachment() != null) {
			try {
				this.setFileData(item.getAttachment().getBytes());
			} catch (IOException e) {
			}
			this.setFileName(item.getAttachment().getOriginalFilename());
			LOG.info("File Name............" + item.getAttachment().getOriginalFilename());
			this.setCredContentType(item.getAttachment().getContentType());
		}
		listOptAnswers = new ArrayList<SourcingFormCqOption>();
		if (CollectionUtil.isNotEmpty(item.getListOptAnswers())) {
			for (CqOption option : item.getListOptAnswers()) {
				if (option != null) {
					SourcingFormCqOption op = new SourcingFormCqOption();
					
					op.setId(option.getId());
					op.setOrder(option.getOrder());
					listOptAnswers.add(op);
				}
			}
			this.setListOptAnswers(listOptAnswers);
		}
	}

	public void copyListForEdit() {
		listOptAnswers = new ArrayList<SourcingFormCqOption>();
		if (CollectionUtil.isNotEmpty(listAnswers)) {
			for (SourcingFormRequestCqOption option : listAnswers) {
				listOptAnswers.add(new SourcingFormCqOption(option));
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
	 * @return the sourcingForm
	 */
	public SourcingFormTemplate getSourcingForm() {
		return sourcingForm;
	}

	/**
	 * @param sourcingForm the sourcingForm to set
	 */
	public void setSourcingForm(SourcingFormTemplate sourcingForm) {
		this.sourcingForm = sourcingForm;
	}

	/**
	 * @return the cqItem
	 */
	public SourcingTemplateCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(SourcingTemplateCqItem cqItem) {
		this.cqItem = cqItem;
	}

	/**
	 * @return the cq
	 */
	public SourcingTemplateCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(SourcingTemplateCq cq) {
		this.cq = cq;
	}

	/**
	 * @return the listAnswers
	 */
	public List<SourcingFormRequestCqOption> getListAnswers() {
		return listAnswers;
	}

	/**
	 * @param listAnswers the listAnswers to set
	 */
	public void setListAnswers(List<SourcingFormRequestCqOption> listAnswers) {
		if (this.listAnswers == null) {
			this.listAnswers = new ArrayList<SourcingFormRequestCqOption>();
		} else {
			this.listAnswers.clear();
		}
		if (listAnswers != null) {
			this.listAnswers.addAll(listAnswers);
		}
	}

	/**
	 * @return the sourcingFormRequest
	 */
	public SourcingFormRequest getSourcingFormRequest() {
		return sourcingFormRequest;
	}

	/**
	 * @param sourcingFormRequest the sourcingFormRequest to set
	 */
	public void setSourcingFormRequest(SourcingFormRequest sourcingFormRequest) {
		this.sourcingFormRequest = sourcingFormRequest;
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
	public List<SourcingFormCqOption> getListOptAnswers() {
		return listOptAnswers;
	}

	/**
	 * @param listOptAnswers the listOptAnswers to set
	 */
	public void setListOptAnswers(List<SourcingFormCqOption> listOptAnswers) {
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

	public SourcingFormRequestCqItem createShallowCopy() {
		SourcingFormRequestCqItem itemReq = new SourcingFormRequestCqItem();
		itemReq.setId(getId());
		itemReq.setCq(getCq());
		itemReq.setCqItem(getCqItem());
		itemReq.setSourcingForm(getSourcingForm());
		itemReq.setSourcingFormRequest(getSourcingFormRequest());
		itemReq.setFileData(getFileData());
		itemReq.setFileName(getFileName());
		itemReq.setCredContentType(getCredContentType());
		if (itemReq.getCqItem().getChildren() != null) {
			itemReq.getCqItem().setChildren(null);
		}
		if (itemReq.getCqItem().getParent() != null) {
			itemReq.getCqItem().getParent().setChildren(null);
		}
		itemReq.setListAnswers(getListAnswers());
		itemReq.setTextAnswers(getTextAnswers());
		return itemReq;
	}

	public String toLogString() {
		return "SourcingFormRequestCqItem [id=" + id + ", sourcingForm=" + sourcingForm.getId() + ", cqItem=" + cqItem.getId() + ", cq=" + cq.getId() + ", listAnswers=" + listAnswers + ", textAnswers=" + textAnswers + ", listOptAnswers=" + listOptAnswers + "]";
	}

}
