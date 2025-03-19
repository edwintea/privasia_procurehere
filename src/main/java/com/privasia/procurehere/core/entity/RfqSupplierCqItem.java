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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.pojo.SupplierCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author arc
 */
@Entity
@Table(name = "PROC_RFQ_EVENT_SUPP_CQ_ITEM")
public class RfqSupplierCqItem implements Serializable {

	private static final long serialVersionUID = -2977565624468254475L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_SUPP_CQ_ITEM"))
	private RfqEvent event;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "EVENT_CQ_ITEM__ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_BUY_CQ_ITEM"))
	private RfqCqItem cqItem;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_SUP_CQI_CQ"))
	private RfqCq cq;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cqItem", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("order")
	private List<RfqSupplierCqOption> listAnswers;

	@Column(name = "TEXT_ANSWERS", length = 1500)
	private String textAnswers;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_CQ_SUP_ID"))
	private Supplier supplier;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "ATTACHMENT_FILE_DATA")
	private byte[] fileData;

	@Column(name = "ATTACHMENT_FILE_NAME", length = 500)
	private String fileName;

	@Column(name = "ATTACHMENT_CONTENT_TYPE", length = 160)
	private String credContentType;

	@Transient
	private List<RfqCqOption> listOptAnswers;

	@Transient
	private MultipartFile attachment;

	// @Column(name="DATE_ANSWER", nullable= true)
	// @Temporal(TemporalType.DATE)
	// private Date dateAnswer;

	public RfqSupplierCqItem() {
	}

	public RfqSupplierCqItem(RfqCqItem cqItem) {
		this.setCq(cqItem.getCq());
		this.setCqItem(cqItem);
		this.setEvent(cqItem.getRfxEvent());
	}

	public RfqSupplierCqItem(SupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent((RfqEvent) cqItem.getEvent());
		this.setCqItem((RfqCqItem) cqItem.getCqItem());
		this.setCq((RfqCq) cqItem.getCq());
		if (CollectionUtil.isNotEmpty(cqItem.getListOptAnswers())) {
			for (CqOption option : cqItem.getListOptAnswers()) {
				if (option != null) {
					if (this.getListAnswers() == null)
						this.setListAnswers(new ArrayList<RfqSupplierCqOption>());
					RfqSupplierCqOption op = new RfqSupplierCqOption();
					op.setCqItem(this);
					op.setId(option.getId());
					op.setOrder(option.getOrder());
					op.setScoring(option.getScoring());
					op.setValue(option.getValue());
					this.getListAnswers().add(op);
				}
			}
		}
		this.setTextAnswers(cqItem.getTextAnswers());
		/*
		 * if (cqItem.getDateAnswer() != null) { this.setDateAnswer(cqItem.getDateAnswer()); }
		 */ this.setSupplier(cqItem.getSupplier());
		if (cqItem.getAttachment() != null) {
			try {
				this.setFileData(cqItem.getAttachment().getBytes());
			} catch (IOException e) {
			}
			this.setFileName(cqItem.getAttachment().getOriginalFilename());
			this.setCredContentType(cqItem.getAttachment().getContentType());
		}
		listOptAnswers = new ArrayList<RfqCqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListOptAnswers())) {
			for (CqOption option : cqItem.getListOptAnswers()) {
				if (option != null) {
					RfqCqOption op = new RfqCqOption();
					op.setId(option.getId());
					listOptAnswers.add(op);
				}
			}
		}
	}

	public void copyListForEdit() {
		listOptAnswers = new ArrayList<RfqCqOption>();
		if (CollectionUtil.isNotEmpty(listAnswers)) {
			for (RfqSupplierCqOption option : listAnswers) {
				listOptAnswers.add(new RfqCqOption(option));
			}
		}
	}

	public RfqSupplierCqItem(String id, String fileName, String textAnswers, String itemName, String itemDescription, Integer level, Integer order, CqType cqType) {
		this.id = id;

		this.cqItem = new RfqCqItem();
		this.cqItem.setItemName(itemName);
		this.cqItem.setItemDescription(itemDescription);
		this.cqItem.setLevel(level);
		this.cqItem.setOrder(order);
		this.cqItem.setCqType(cqType);

		this.textAnswers = textAnswers;
		this.fileName = fileName;
	}

	
	public RfqSupplierCqItem(String id, String fileName, String textAnswers, String itemName, String itemDescription, Integer level, Integer order, CqType cqType, String supplier, String companyName) {
		this.id = id;
		this.cqItem = new RfqCqItem();
		this.cqItem.setItemName(itemName);
		this.cqItem.setItemDescription(itemDescription);
		this.cqItem.setLevel(level);
		this.cqItem.setOrder(order);
		this.cqItem.setCqType(cqType);
		
		this.fileName = fileName;
		this.textAnswers = textAnswers;
 		this.supplier = new Supplier();
		this.supplier.setId(supplier);
		this.supplier.setCompanyName(companyName);
	}

	public RfqSupplierCqItem(String id, String fileName, String textAnswers, String itemName, String itemDescription, Integer level, Integer order, CqType cqType, Integer totalScore, String supplier, String companyName) {
		this.id = id;
		this.cqItem = new RfqCqItem();
		this.cqItem.setItemName(itemName);
		this.cqItem.setItemDescription(itemDescription);
		this.cqItem.setLevel(level);
		this.cqItem.setOrder(order);
		this.cqItem.setCqType(cqType);
		this.cqItem.setTotalScore(totalScore);
		
		this.fileName = fileName;
		this.textAnswers = textAnswers;
 		this.supplier = new Supplier();
		this.supplier.setId(supplier);
		this.supplier.setCompanyName(companyName);
	}
	
	public RfqSupplierCqItem(String id, Integer level, Integer order, String fileName, byte[] fileData) {
		this.id = id;
		this.cqItem = new RfqCqItem();
		this.cqItem.setLevel(level);
		this.cqItem.setOrder(order);

		this.fileName = fileName;
		this.fileData = fileData;
	}

	
	/*
	 * public Date getDateAnswer() { return dateAnswer; } public void setDateAnswer(Date dateAnswer) { this.dateAnswer =
	 * dateAnswer; }
	 */
	/**
	 * @return the event
	 */
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
	}

	/**
	 * @return the cqItem
	 */
	public RfqCqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(RfqCqItem cqItem) {
		this.cqItem = cqItem;
	}

	/**
	 * id
	 * 
	 * @return the cq
	 */
	public RfqCq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(RfqCq cq) {
		this.cq = cq;
	}

	/**
	 * @return the listAnswers
	 */
	public List<RfqSupplierCqOption> getListAnswers() {
		return listAnswers;
	}

	/**
	 * @param listAnswers the listAnswers to set
	 */
	public void setListAnswers(List<RfqSupplierCqOption> listAnswers) {
		if (this.listAnswers == null) {
			this.listAnswers = new ArrayList<RfqSupplierCqOption>();
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
	 * @return the listOptAnswers
	 */
	public List<RfqCqOption> getListOptAnswers() {
		return listOptAnswers;
	}

	/**
	 * @param listOptAnswers the listOptAnswers to set
	 */
	public void setListOptAnswers(List<RfqCqOption> listOptAnswers) {

		this.listOptAnswers = listOptAnswers;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

	public RfqSupplierCqItem createShallowCopy() {
		RfqSupplierCqItem ic = new RfqSupplierCqItem();
		ic.setId(getId());
		ic.setCq(getCq());
		ic.setCqItem(getCqItem());
		ic.setEvent(getEvent());
		ic.setFileData(getFileData());
		ic.setFileName(getFileName());
		ic.setCredContentType(getCredContentType());
		if (ic.getCqItem().getChildren() != null) {
			ic.getCqItem().setChildren(null);
		}
		if (ic.getCqItem().getParent() != null) {
			ic.getCqItem().getParent().setChildren(null);
		}
		ic.setListAnswers(getListAnswers());
		ic.setTextAnswers(getTextAnswers());
		// ic.setDateAnswer(getDateAnswer());
		return ic;
	}

	public String toLogString() {
		return "RftSupplierCqItem [id=" + id + ", event=" + event.getId() + ", cqItem=" + cqItem.getId() + ", cq=" + cq.getId() + ", listAnswers=" + listAnswers + ", textAnswers=" + textAnswers + ", listOptAnswers=" + listOptAnswers + "]";
	}

}
