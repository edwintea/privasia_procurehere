package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.enums.Status;

public class NaicsCodesPojo implements Serializable {

	private static final long serialVersionUID = -7946607341128577104L;

	private String id;
	private Integer categoryCode;
	private String categoryName;

	private NaicsCodes parent;

	@JsonIgnore
	private List<NaicsCodes> children;
	private Integer level = 1;
	private Integer order = 1;
	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;
	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date modifiedDate;
	private Status status;

	public NaicsCodesPojo() {
	}

	/**
	 * @param id
	 * @param categoryCode
	 * @param categoryName
	 */
	public NaicsCodesPojo(String id, Integer categoryCode, String categoryName) {
		super();
		this.id = id;
		this.categoryCode = categoryCode;
		this.categoryName = categoryName;
	}

	public NaicsCodesPojo(NaicsCodes naicsCode) {

		this.id = naicsCode.getId();
		this.categoryCode = naicsCode.getCategoryCode();
		this.categoryName = naicsCode.getCategoryName();
		this.parent = naicsCode.getParent();
		this.children = naicsCode.getChildren();
		this.level = naicsCode.getLevel();
		this.order = naicsCode.getOrder();
		this.createdBy = naicsCode.getCreatedBy() != null ? naicsCode.getCreatedBy().getLoginId() : null;
		this.createdDate = naicsCode.getCreatedDate();
		this.modifiedBy = naicsCode.getModifiedBy() != null ? naicsCode.getModifiedBy().getLoginId() : null;
		this.modifiedDate = naicsCode.getModifiedDate();
		this.status = naicsCode.getStatus();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public NaicsCodes getParent() {
		return parent;
	}

	public void setParent(NaicsCodes parent) {
		this.parent = parent;
	}

	public List<NaicsCodes> getChildren() {
		return children;
	}

	public void setChildren(List<NaicsCodes> children) {
		this.children = children;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
