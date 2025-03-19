package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author sarang
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourcingFormCqItemPojo implements Serializable {

	private static final long serialVersionUID = 6190552906988907105L;

	private String cqId;
	private String id;
	private String itemName;
	private Integer level;
	private Integer order;
	private String cq;
	private String parent;
	private String itemDescription;
	private String cqType;
	private boolean isRequired;

	private boolean optional;
	private List<String> options;
	private List<String> optionScore;
	private Boolean isSupplierAttachRequired;
	private boolean attachment;

	public SourcingFormCqItemPojo() {
	}

	public SourcingFormCqItemPojo(SourcingTemplateCqItem sourcingTemplateCqItem) {
		this.itemName = sourcingTemplateCqItem.getId();
		this.itemName = sourcingTemplateCqItem.getItemName();
		this.level = sourcingTemplateCqItem.getLevel();
		this.order = sourcingTemplateCqItem.getOrder();
		this.cq = sourcingTemplateCqItem.getCq().getId();
		this.parent = sourcingTemplateCqItem.getParent() != null ? sourcingTemplateCqItem.getParent().getId() : null;
		this.itemDescription = sourcingTemplateCqItem.getItemDescription();
		this.cqType = sourcingTemplateCqItem.getCqType() != null ? sourcingTemplateCqItem.getCqType().toString() : "";
		this.attachment = sourcingTemplateCqItem.getAttachment() != null ? sourcingTemplateCqItem.getAttachment() : false;
		this.isSupplierAttachRequired = sourcingTemplateCqItem.getIsSupplierAttachRequired() != null ? sourcingTemplateCqItem.getIsSupplierAttachRequired() : false;
		this.optional = sourcingTemplateCqItem.getOptional() != null ? sourcingTemplateCqItem.getOptional() : false;
		this.options = sourcingTemplateCqItem.getCqOptions() != null ? getRfaOptionValues(sourcingTemplateCqItem.getCqOptions()) : null;
		this.optionScore = CollectionUtil.isNotEmpty(sourcingTemplateCqItem.getCqOptions()) ? getOptionScores(sourcingTemplateCqItem.getCqOptions()) : null;
	}

	public boolean isAttachment() {
		return attachment;
	}

	public void setAttachment(boolean attachment) {
		this.attachment = attachment;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
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

	private List<String> getRfaOptionValues(List<SourcingFormCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (SourcingFormCqOption option : list) {
			returnList.add(option.getValue());
		}
		return returnList;
	}

	private List<String> getOptionScores(List<SourcingFormCqOption> list) {
		List<String> returnList = new ArrayList<String>();
		for (SourcingFormCqOption option : list) {
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

	public String getCqId() {
		return cqId;
	}

	public void setCqId(String cqId) {
		this.cqId = cqId;
	}

	public String toLogString() {
		return "RftCqItemPojo [itemName=" + itemName + ", level=" + level + ", order=" + order + ", cq=" + cq + ", parent=" + parent + "itemDescription=" + itemDescription + ", cqType=" + cqType + ", attachment=" + isRequired + ", optional=" + optional + "]";
	}

}
