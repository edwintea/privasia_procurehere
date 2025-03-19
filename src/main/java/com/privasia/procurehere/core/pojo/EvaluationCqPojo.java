package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

public class EvaluationCqPojo implements Serializable {

	private static final long serialVersionUID = 4852650543746786532L;

	private String name;
	private String description;

	private List<EvaluationCqItemPojo> cqItem;

	private String title;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the cqItem
	 */
	public List<EvaluationCqItemPojo> getCqItem() {
		return cqItem;
	}

	/**
	 * @param cqItem the cqItem to set
	 */
	public void setCqItem(List<EvaluationCqItemPojo> cqItem) {
		this.cqItem = cqItem;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationCqPojo [name=" + name + ", description=" + description + ", cqItem=" + cqItem + ", title=" + title + "]";
	}

}
