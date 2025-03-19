package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.Status;

import io.swagger.annotations.ApiModelProperty;

public class ScoreRatingPojo implements Serializable {

	private static final long serialVersionUID = 3453442773357853852L;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Min Score", allowableValues = "range[1, 3]", required = true)
	private Integer minScore;

	@ApiModelProperty(notes = "Max Score", allowableValues = "range[1, 3]", required = true)
	private Integer maxScore;

	@ApiModelProperty(notes = "Rating", allowableValues = "range[1, 2]", required = true)
	private Integer rating;

	@ApiModelProperty(notes = "Description", allowableValues = "range[1, 300]")
	private String description;

	@ApiModelProperty(notes = "Status", required = false, hidden = true)
	private Status status;

	@ApiModelProperty(required = false, hidden = true)
	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@ApiModelProperty(required = false, hidden = true)
	private Date createdDate;

	@ApiModelProperty(required = false, hidden = true)
	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	@ApiModelProperty(required = false, hidden = true)
	private Date modifiedDate;

	public ScoreRatingPojo() {
	}

	public ScoreRatingPojo(ScoreRating sr) {

		this.id = sr.getId();
		this.minScore = sr.getMinScore();
		this.maxScore = sr.getMaxScore();
		this.rating = sr.getRating();
		this.description = sr.getDescription();
		this.status = sr.getStatus();
		this.createdBy = sr.getCreatedBy() != null ? sr.getCreatedBy().getLoginId() : null;
		this.createdDate = sr.getCreatedDate();
		this.modifiedBy = sr.getModifiedBy() != null ? sr.getModifiedBy().getLoginId() : null;
		this.modifiedDate = sr.getModifiedDate();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getMinScore() {
		return minScore;
	}

	public void setMinScore(Integer minScore) {
		this.minScore = minScore;
	}

	public Integer getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Integer maxScore) {
		this.maxScore = maxScore;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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

}
