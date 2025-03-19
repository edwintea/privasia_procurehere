/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.Cq;
import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.utils.CollectionUtil;

public class SupplierCqItem implements Serializable {

	private static final long serialVersionUID = 7901786296713024961L;

	private String id;

	private Event event;

	private CqItem cqItem;

	private Cq cq;

	private List<CqOption> listAnswers;

	private String textAnswers;

	private Supplier supplier;

	private byte[] fileData;

	private String fileName;

	private String credContentType;

	private List<CqOption> listOptAnswers;

	private MultipartFile attachment;
	
	private List<EventDocument> eventDocuments;

	public List<EventDocument> getEventDocuments() {
		return eventDocuments;
	}

	public void setEventDocuments(List<EventDocument> eventDocuments) {
		this.eventDocuments = eventDocuments;
	}

//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	// @Temporal(TemporalType.DATE)
	//private String dateAnswer;

/*	public String getDateAnswer() {
		return dateAnswer;
	}

	public void setDateAnswer(String dateAnswer) {
		this.dateAnswer = dateAnswer;
	}*/

	public SupplierCqItem() {
	}

	public SupplierCqItem(RftSupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent(cqItem.getEvent());
		this.setCq(cqItem.getCq());
		this.setCqItem(cqItem.getCqItem());
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			listAnswers = new ArrayList<CqOption>();
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}

		this.setTextAnswers(cqItem.getTextAnswers());
		/*if (cqItem.getDateAnswer() != null) {
			this.setDateAnswer(cqItem.getDateAnswer());
		}*/
		this.setSupplier(cqItem.getSupplier());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

	}

	public SupplierCqItem(RfpSupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent(cqItem.getEvent());
		this.setCqItem(cqItem.getCqItem());
		this.setCq(cqItem.getCq());
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			listAnswers = new ArrayList<CqOption>();
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}
		this.setTextAnswers(cqItem.getTextAnswers());
/*		if (cqItem.getDateAnswer() != null) {
			this.setDateAnswer(cqItem.getDateAnswer());
		}
*/		this.setSupplier(cqItem.getSupplier());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

	}

	public SupplierCqItem(RfqSupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent(cqItem.getEvent());
		this.setCqItem(cqItem.getCqItem());
		this.setCq(cqItem.getCq());
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			listAnswers = new ArrayList<CqOption>();
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}
		this.setTextAnswers(cqItem.getTextAnswers());
/*		if (cqItem.getDateAnswer() != null) {
			this.setDateAnswer(cqItem.getDateAnswer());
		}
*/		this.setSupplier(cqItem.getSupplier());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

	}

	public SupplierCqItem(RfiSupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent(cqItem.getEvent());
		this.setCqItem(cqItem.getCqItem());
		this.setCq(cqItem.getCq());
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			listAnswers = new ArrayList<CqOption>();
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}
		this.setTextAnswers(cqItem.getTextAnswers());
/*		if (cqItem.getDateAnswer() != null) {
			this.setDateAnswer(cqItem.getDateAnswer());
		}
*/		this.setSupplier(cqItem.getSupplier());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

	}

	public SupplierCqItem(RfaSupplierCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setEvent(cqItem.getEvent());
		this.setCqItem(cqItem.getCqItem());
		this.setCq(cqItem.getCq());
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			listAnswers = new ArrayList<CqOption>();
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				this.listAnswers.add(op);
			}
		}
		this.setTextAnswers(cqItem.getTextAnswers());
		/*if (cqItem.getDateAnswer() != null) {
			this.setDateAnswer(cqItem.getDateAnswer());
		}*/
		this.setSupplier(cqItem.getSupplier());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setOrder(option.getOrder());
				op.setScoring(option.getScoring());
				op.setValue(option.getValue());
				listOptAnswers.add(op);
			}
		}

	}

	public void copyListForEdit() {
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(this.listAnswers)) {
			for (CqOption option : listAnswers) {
				listOptAnswers.add(option);
			}
		}
	}

	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return the cqItem
	 */
	public CqItem getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(CqItem cqItem) {
		this.cqItem = cqItem;
	}

	/**
	 * @return the cq
	 */
	public Cq getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(Cq cq) {
		this.cq = cq;
	}

	/**
	 * @return the listAnswers
	 */
	public List<CqOption> getListAnswers() {
		return listAnswers;
	}

	/**
	 * @param listAnswers the listAnswers to set
	 */
	public void setListAnswers(List<CqOption> listAnswers) {
		this.listAnswers = listAnswers;
	}

	/**
	 * @return the listOptAnswers
	 */
	public List<CqOption> getListOptAnswers() {
		return listOptAnswers;
	}

	/**
	 * @param listOptAnswers the listOptAnswers to set
	 */
	public void setListOptAnswers(List<CqOption> listOptAnswers) {
		this.listOptAnswers = listOptAnswers;
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

	public String toLogString() {
		return "RftSupplierCqItem [id=" + id + ", event=" + event.getId() + ", cqItem=" + cqItem.getId() + ", cq=" + cq.getId() + ", listAnswers=" + listAnswers + ", textAnswers=" + textAnswers + ", listOptAnswers=" + listOptAnswers + "]";
	}

}
