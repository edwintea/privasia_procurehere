package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Javed Ahmed
 */
public class NotesPojo implements Serializable {

	private static final long serialVersionUID = -8588879165082845085L;

	private String id;

	private String incidentType;

	private String description;

	private String createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;

	@JsonIgnore
	private Supplier supplier;

	@JsonIgnore
	private Buyer buyer;

	public NotesPojo() {
	}
	/**
	 * @param notes
	 */
	public NotesPojo(Notes notes) {

		this.id = notes.getId();
		this.incidentType = notes.getIncidentType();
		this.description = notes.getDescription();
		this.createdBy = notes.getCreatedBy() != null ? notes.getCreatedBy().getLoginId() : null;
		this.createdDate = notes.getCreatedDate();
		this.supplier = notes.getSupplier();
		this.buyer = notes.getBuyer();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

}
