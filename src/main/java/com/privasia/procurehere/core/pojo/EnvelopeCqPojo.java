package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author yogesh
 */
public class EnvelopeCqPojo implements Serializable {

	private static final long serialVersionUID = 4852650543746786532L;

	private String name;
	private String description;

	List<EvaluationCqPojo> allCqs;

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

	public List<EvaluationCqPojo> getAllCqs() {
		return allCqs;
	}

	public void setAllCqs(List<EvaluationCqPojo> allCqs) {
		this.allCqs = allCqs;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

}
