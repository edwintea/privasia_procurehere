package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.PoDocumentType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author edwin
 */

@Entity
@Table(name = "PROC_PO_EVENTS")
public class PoEvent extends Event implements Serializable {

	private static final long serialVersionUID = -2183132189846045138L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PO_ID", foreignKey = @ForeignKey(name = "FK_PO_EVENT"))
	private Po po;

	public PoEvent() {
	}

	public Po getPo() {
		return po;
	}

	public void setPo(Po po) {
		this.po = po;
	}
}
