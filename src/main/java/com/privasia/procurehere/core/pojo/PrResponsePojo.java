package com.privasia.procurehere.core.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.enums.ProcurehereDocumentType;
import com.privasia.procurehere.core.enums.SapDocType;

/**
 * @author Nitin Otageri
 */
public class PrResponsePojo implements Serializable {

	private static final long serialVersionUID = -7094290956150607806L;

	@ApiModelProperty(notes = "Request ID", required = true)
	private String requestId;

	@ApiModelProperty(notes = "Req Transaction ID", required = true)
	private String requestTransactionId;

	@ApiModelProperty(notes = "Consumer ID", required = true)
	private String consumerId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
	@ApiModelProperty(notes = "Request Timestamp", required = true)
	private Date requestTimestamp;

	@ApiModelProperty(notes = "Status [SUCC or FAIL]", required = true)
	private String status;

	@ApiModelProperty(notes = "Consumer Ref ID", required = true)
	private String consumerReferenceId;

	@ApiModelProperty(notes = "Client ID", required = true)
	private String clientId;

	@ApiModelProperty(notes = "Message", required = true)
	private Object message;

	@ApiModelProperty(notes = "SAP Ref No", required = true)
	private String sapRefNo;

	@ApiModelProperty(notes = "SAP Doc Type [PR or PO]", required = true)
	private SapDocType sapDocType;

	@ApiModelProperty(notes = "Operation [C = Create, U = Update or D = Delete]", required = true)
	private OperationType operation;

	@ApiModelProperty(notes = "Transaction ID", required = true)
	private String transactionId;

	@ApiModelProperty(notes = "Transaction Type [RFI, RFT, RFA, RFP, RFQ, PR, SR] ", required = true)
	private ProcurehereDocumentType transactionType;

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the requestTransactionId
	 */
	public String getRequestTransactionId() {
		return requestTransactionId;
	}

	/**
	 * @param requestTransactionId the requestTransactionId to set
	 */
	public void setRequestTransactionId(String requestTransactionId) {
		this.requestTransactionId = requestTransactionId;
	}

	/**
	 * @return the consumerId
	 */
	public String getConsumerId() {
		return consumerId;
	}

	/**
	 * @param consumerId the consumerId to set
	 */
	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	/**
	 * @return the requestTimestamp
	 */
	public Date getRequestTimestamp() {
		return requestTimestamp;
	}

	/**
	 * @param requestTimestamp the requestTimestamp to set
	 */
	public void setRequestTimestamp(Date requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the consumerReferenceId
	 */
	public String getConsumerReferenceId() {
		return consumerReferenceId;
	}

	/**
	 * @param consumerReferenceId the consumerReferenceId to set
	 */
	public void setConsumerReferenceId(String consumerReferenceId) {
		this.consumerReferenceId = consumerReferenceId;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the message
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(Object message) {
		this.message = message;
	}

	/**
	 * @return the sapRefNo
	 */
	public String getSapRefNo() {
		return sapRefNo;
	}

	/**
	 * @param sapRefNo the sapRefNo to set
	 */
	public void setSapRefNo(String sapRefNo) {
		this.sapRefNo = sapRefNo;
	}

	/**
	 * @return the sapDocType
	 */
	public SapDocType getSapDocType() {
		return sapDocType;
	}

	/**
	 * @param sapDocType the sapDocType to set
	 */
	public void setSapDocType(SapDocType sapDocType) {
		this.sapDocType = sapDocType;
	}

	/**
	 * @return the operation
	 */
	public OperationType getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionType
	 */
	public ProcurehereDocumentType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(ProcurehereDocumentType transactionType) {
		this.transactionType = transactionType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PrResponsePojo [requestId=" + requestId + ", requestTransactionId=" + requestTransactionId + ", consumerId=" + consumerId + ", requestTimestamp=" + requestTimestamp + ", status=" + status + ", consumerReferenceId=" + consumerReferenceId + ", clientId=" + clientId + ", message=" + message + ", sapRefNo=" + sapRefNo + ", sapDocType=" + sapDocType + ", operation=" + operation + ", transactionId=" + transactionId + ", transactionType=" + transactionType + "]";
	}

}
