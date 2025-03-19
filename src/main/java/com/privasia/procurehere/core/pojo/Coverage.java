package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.State;

public class Coverage implements Serializable {

	private static final long serialVersionUID = -331020261865262749L;

	public enum CoverageType {
		COUNTRY, STATE;
	}

	private String id;

	private String code;

	private String name;

	private CoverageType type;

	private List<Coverage> children;

	private boolean checked = false;
	private String countryName; // New field to store the country name

	public Coverage() {
	}

	public Coverage(Country country) {
		this.id = country.getId();
		this.code = country.getCountryCode();
		this.name = country.getCountryName();
		this.type = CoverageType.COUNTRY;
	}

/*	public Coverage(State state) {
		this.id = state.getId();
		this.code = state.getStateCode();
		this.name = state.getStateName();
		this.type = CoverageType.STATE;
	}*/

	public Coverage(State state) {
		this.id = state.getId();
		this.code = state.getStateCode();
		this.name = state.getStateName();
		this.type = CoverageType.STATE;
	}

	public Coverage(State state, boolean flag) {
		this.id = state.getId();
		this.code = state.getStateCode();
		this.name = state.getStateName();
		this.countryName = state.getStateName().concat(", ").concat(state.getCountry().getCountryName()); // Set the country name
		this.type = CoverageType.STATE;
	}

	// Getter and setter for countryName

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

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
	 * @return the type
	 */
	public CoverageType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CoverageType type) {
		this.type = type;
	}

	/**
	 * @return the children
	 */
	public List<Coverage> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Coverage> children) {
		this.children = children;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coverage other = (Coverage) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public String toLogString() {
		return "Coverage [id=" + id + ", code=" + code + ", name=" + name + ", type=" + type + ", checked=" + checked + "]";
	}

}
