package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CqItemPojo implements Serializable {

	private static final long serialVersionUID = 6190552906988907105L;

	private String id;
	private String itemName;
	private Integer level;
	private Integer order;
	private String cq;
	private String parent;
	private String rftEvent;
	private String itemDescription;
	private String cqType;
	private boolean attachment;
	private boolean optional;
	private List<String> options;
	private List<String> optionScore;
	private Boolean isSupplierAttachRequired;

	public CqItemPojo() {
	}

	public CqItemPojo(RftCqItem rftCqItem) {
		this.itemName = rftCqItem.getId();
		this.itemName = rftCqItem.getItemName();
		this.level = rftCqItem.getLevel();
		this.order = rftCqItem.getOrder();
		this.cq = rftCqItem.getCq().getId();
		this.parent = rftCqItem.getParent() != null ? rftCqItem.getParent().getId() : null;
		this.rftEvent = rftCqItem.getRfxEvent() != null ? rftCqItem.getRfxEvent().getId() : null;
		this.itemDescription = rftCqItem.getItemDescription();
		this.cqType = rftCqItem.getCqType() != null ? rftCqItem.getCqType().toString() : "";
		this.attachment = rftCqItem.getAttachment() != null ? rftCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = rftCqItem.getIsSupplierAttachRequired() != null ? rftCqItem.getIsSupplierAttachRequired() : false;
		this.optional = rftCqItem.getOptional() != null ? rftCqItem.getOptional() : false;
		this.options = rftCqItem.getCqOptions() != null ? getOptionValues(rftCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(rftCqItem.getCqOptions()) ? getRftOptionScores(rftCqItem.getCqOptions()) : null;
	}

	public CqItemPojo(RfiCqItem rfiCqItem) {
		this.itemName = rfiCqItem.getId();
		this.itemName = rfiCqItem.getItemName();
		this.level = rfiCqItem.getLevel();
		this.order = rfiCqItem.getOrder();
		this.cq = rfiCqItem.getCq().getId();
		this.parent = rfiCqItem.getParent() != null ? rfiCqItem.getParent().getId() : null;
		this.rftEvent = rfiCqItem.getRfxEvent() != null ? rfiCqItem.getRfxEvent().getId() : null;
		this.itemDescription = rfiCqItem.getItemDescription();
		this.cqType = rfiCqItem.getCqType() != null ? rfiCqItem.getCqType().toString() : "";
		this.attachment = rfiCqItem.getAttachment() != null ? rfiCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = rfiCqItem.getIsSupplierAttachRequired() != null ? rfiCqItem.getIsSupplierAttachRequired() : false;
		this.optional = rfiCqItem.getOptional() != null ? rfiCqItem.getOptional() : false;
		this.options = rfiCqItem.getCqOptions() != null ? getRfiOptionValues(rfiCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(rfiCqItem.getCqOptions()) ? getRfiOptionScores(rfiCqItem.getCqOptions()) : null;
	}

	public CqItemPojo(RfpCqItem rfpCqItem) {
		this.itemName = rfpCqItem.getId();
		this.itemName = rfpCqItem.getItemName();
		this.level = rfpCqItem.getLevel();
		this.order = rfpCqItem.getOrder();
		this.cq = rfpCqItem.getCq().getId();
		this.parent = rfpCqItem.getParent() != null ? rfpCqItem.getParent().getId() : null;
		this.rftEvent = rfpCqItem.getRfxEvent() != null ? rfpCqItem.getRfxEvent().getId() : null;
		this.itemDescription = rfpCqItem.getItemDescription();
		this.cqType = rfpCqItem.getCqType() != null ? rfpCqItem.getCqType().toString() : "";
		this.attachment = rfpCqItem.getAttachment() != null ? rfpCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = rfpCqItem.getIsSupplierAttachRequired() != null ? rfpCqItem.getIsSupplierAttachRequired() : false;
		this.optional = rfpCqItem.getOptional() != null ? rfpCqItem.getOptional() : false;
		this.options = rfpCqItem.getCqOptions() != null ? getRfpOptionValues(rfpCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(rfpCqItem.getCqOptions()) ? getRfpOptionScores(rfpCqItem.getCqOptions()) : null;
	}

	public CqItemPojo(RfqCqItem rfqCqItem) {
		this.itemName = rfqCqItem.getId();
		this.itemName = rfqCqItem.getItemName();
		this.level = rfqCqItem.getLevel();
		this.order = rfqCqItem.getOrder();
		this.cq = rfqCqItem.getCq().getId();
		this.parent = rfqCqItem.getParent() != null ? rfqCqItem.getParent().getId() : null;
		this.rftEvent = rfqCqItem.getRfxEvent() != null ? rfqCqItem.getRfxEvent().getId() : null;
		this.itemDescription = rfqCqItem.getItemDescription();
		this.cqType = rfqCqItem.getCqType() != null ? rfqCqItem.getCqType().toString() : "";
		this.attachment = rfqCqItem.getAttachment() != null ? rfqCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = rfqCqItem.getIsSupplierAttachRequired() != null ? rfqCqItem.getIsSupplierAttachRequired() : false;
		this.optional = rfqCqItem.getOptional() != null ? rfqCqItem.getOptional() : false;
		this.options = rfqCqItem.getCqOptions() != null ? getRfqOptionValues(rfqCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(rfqCqItem.getCqOptions()) ? getRfqOptionScores(rfqCqItem.getCqOptions()) : null;
	}

	public CqItemPojo(RfaCqItem rfaCqItem) {
		this.itemName = rfaCqItem.getId();
		this.itemName = rfaCqItem.getItemName();
		this.level = rfaCqItem.getLevel();
		this.order = rfaCqItem.getOrder();
		this.cq = rfaCqItem.getCq().getId();
		this.parent = rfaCqItem.getParent() != null ? rfaCqItem.getParent().getId() : null;
		this.rftEvent = rfaCqItem.getRfxEvent() != null ? rfaCqItem.getRfxEvent().getId() : null;
		this.itemDescription = rfaCqItem.getItemDescription();
		this.cqType = rfaCqItem.getCqType() != null ? rfaCqItem.getCqType().toString() : "";
		this.attachment = rfaCqItem.getAttachment() != null ? rfaCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = rfaCqItem.getIsSupplierAttachRequired() != null ? rfaCqItem.getIsSupplierAttachRequired() : false;
		this.optional = rfaCqItem.getOptional() != null ? rfaCqItem.getOptional() : false;
		this.options = rfaCqItem.getCqOptions() != null ? getRfaOptionValues(rfaCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(rfaCqItem.getCqOptions()) ? getRfaOptionScores(rfaCqItem.getCqOptions()) : null;
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
	 * @return the cq
	 */
	public String getCq() {
		return cq;
	}

	/**
	 * @param cq the cq to set
	 */
	public void setCq(String cq) {
		this.cq = cq;
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
	 * @return the rftEvent
	 */
	public String getRftEvent() {
		return rftEvent;
	}

	/**
	 * @param rftEvent the rftEvent to set
	 */
	public void setRftEvent(String rftEvent) {
		this.rftEvent = rftEvent;
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

	private List<String> getOptionValues(List<RftCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RftCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getRfiOptionValues(List<RfiCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfiCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getRfpOptionValues(List<RfpCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfpCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getRfaOptionValues(List<RfaCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfaCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getRfqOptionValues(List<RfqCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfqCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getRftOptionScores(List<RftCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RftCqOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
	}

	private List<String> getRfiOptionScores(List<RfiCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfiCqOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
	}

	private List<String> getRfpOptionScores(List<RfpCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfpCqOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
	}

	private List<String> getRfaOptionScores(List<RfaCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfaCqOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
	}

	private List<String> getRfqOptionScores(List<RfqCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (RfqCqOption option : list) {
			returnList.add(String.valueOf(option.getScoring()));
		}
		return returnList;
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

	public Boolean getIsSupplierAttachRequired() {
		return isSupplierAttachRequired;
	}

	public void setIsSupplierAttachRequired(Boolean isSupplierAttachRequired) {
		this.isSupplierAttachRequired = isSupplierAttachRequired;
	}

	public String toLogString() {
		return "RftCqItemPojo [itemName=" + itemName + ", level=" + level + ", order=" + order + ", cq=" + cq + ", parent=" + parent + ", rftEvent=" + rftEvent + ", itemDescription=" + itemDescription + ", cqType=" + cqType + ", attachment=" + attachment + ", optional=" + optional + "]";
	}

}
