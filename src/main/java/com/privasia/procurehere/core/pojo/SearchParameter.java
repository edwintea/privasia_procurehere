/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Nitin Otageri
 */
public class SearchParameter implements Serializable{
	
	private static final long serialVersionUID = -5589268781179501472L;

	/**
	 * Global search value. To be applied to all columns which have searchable as true.
	 */
	private String value;

	/**
	 * true if the global filter should be treated as a regular expression for advanced searching, false otherwise. Note
	 * that normally server-side processing scripts will not perform regular expression searching for performance
	 * reasons on large data sets, but it is technically possible and at the discretion of your script.
	 */
	private Boolean regex;

	public SearchParameter() {
	}

	public SearchParameter(String value, Boolean regex) {
		super();
		this.value = value;
		this.regex = regex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getRegex() {
		return regex;
	}

	public void setRegex(Boolean regex) {
		this.regex = regex;
	}

	@Override
	public String toString() {
		return "SearchParameter [value=" + value + ", regex=" + regex + "]";
	}
}