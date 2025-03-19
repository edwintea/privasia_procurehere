package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrItemErp2DeletePojo implements Serializable {

	private static final long serialVersionUID = 9202614736960706231L;

	@JsonProperty("PRItemPreqItem")
	private String prItemSeqNo;

	@JsonProperty("PRItemMaterial")
	private String itemCode;

	@JsonProperty("Operation")
	private String operation;

	public PrItemErp2DeletePojo() {
	}

	public PrItemErp2DeletePojo(PrItem prItem, Pr pr, Date approvedDate, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = prItem.getProduct() != null ? prItem.getProduct().getProductCode() : "Non Item";
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(SourcingFormRequestBqItem item, SourcingFormRequest req, Date approvedDate, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(SourcingFormRequestBqItem item, int sequence, boolean isDelete) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(RfqBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(RftBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(RfaBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	public PrItemErp2DeletePojo(RfpBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
		this.operation = "D";
	}

	/**
	 * @return the prItemSeqNo
	 */
	public String getPrItemSeqNo() {
		return prItemSeqNo;
	}

	/**
	 * @param prItemSeqNo the prItemSeqNo to set
	 */
	public void setPrItemSeqNo(String prItemSeqNo) {
		this.prItemSeqNo = prItemSeqNo;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

}
