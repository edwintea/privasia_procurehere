package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.Cq;
import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Pooja
 */
public class SourcingFormReqCqItem implements Serializable {
	

	private static final long serialVersionUID = 7901786296713024961L;

	private String id;

	private Cq cq;

	private CqItem cqItem;

//	private List<CqOption> listAnswers;

	private String textAnswers;

	private byte[] fileData;

	private String fileName;

	private String credContentType;

	private SourcingFormRequest sourcingFormRequest;

	private SourcingFormTemplate sourcingForm;

	private List<CqOption> listOptAnswers;

	private MultipartFile attachment;

	public SourcingFormReqCqItem() {
	}

	public SourcingFormReqCqItem(SourcingFormRequestCqItem cqItem) {
		this.setId(cqItem.getId());
		this.setCq(cqItem.getCq());
		this.setCqItem(cqItem.getCqItem());
//		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
//			listAnswers = new ArrayList<CqOption>();
//			for (CqOption option : cqItem.getListAnswers()) {
//				CqOption op = new CqOption();
//				op.setOrder(option.getOrder());
//				op.setValue(option.getValue());
//				this.listAnswers.add(op);
//			}
//		}
		this.setTextAnswers(cqItem.getTextAnswers());
		this.setFileData(cqItem.getFileData());
		this.setFileName(cqItem.getFileName());
		this.setSourcingFormRequest(cqItem.getSourcingFormRequest());
		this.setSourcingForm(cqItem.getSourcingForm());
		this.setCredContentType(cqItem.getCredContentType());
		listOptAnswers = new ArrayList<CqOption>();
		if (CollectionUtil.isNotEmpty(cqItem.getListAnswers())) {
			for (CqOption option : cqItem.getListAnswers()) {
				CqOption op = new CqOption();
				op.setId(option.getId());
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

//	/**
//	 * @return the listAnswers
//	 */
//	public List<CqOption> getListAnswers() {
//		return listAnswers;
//	}
//
//	/**
//	 * @param listAnswers the listAnswers to set
//	 */
//	public void setListAnswers(List<CqOption> listAnswers) {
//		this.listAnswers = listAnswers;
//	}

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
		return "SourcingFormReqCqItem [id=" + id + ", sourcingFormRequest=" + sourcingFormRequest.getId() + ", cqItem=" + cqItem.getId() + ", cq=" + cq.getId() + ", textAnswers=" + textAnswers + ", listOptAnswers=" + listOptAnswers + "]";
	}
}
