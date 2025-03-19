/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Nitin Otageri
 */

public class EvaluationCqItemPojo implements Serializable {
	private static final long serialVersionUID = -2185953129548393145L;

	private String itemName;
	private String itemDescription;
	private String level;
	private List<EvaluationCqItemSupplierPojo> suppliers;

	private String answer;
	private String attachments;
	private String optionType;
	private String fileName;
	private String cqAnswer;
	private Boolean isSection;

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
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the suppliers
	 */
	public List<EvaluationCqItemSupplierPojo> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<EvaluationCqItemSupplierPojo> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * @return the attachments
	 */
	public String getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the optionType
	 */
	public String getOptionType() {
		return optionType;
	}

	/**
	 * @param optionType the optionType to set
	 */
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCqAnswer() {
		return cqAnswer;
	}

	public void setCqAnswer(String cqAnswer) {
		this.cqAnswer = cqAnswer;
	}

	/**
	 * @return the isSection
	 */
	public Boolean getIsSection() {
		return isSection;
	}

	/**
	 * @param isSection the isSection to set
	 */
	public void setIsSection(Boolean isSection) {
		this.isSection = isSection;
	}

}
