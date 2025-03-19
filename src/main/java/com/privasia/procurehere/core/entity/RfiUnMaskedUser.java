package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PROC_RFI_UNMASKED_USER")
public class RfiUnMaskedUser extends RfxEventUnmaskUser  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8589752180288647647L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true,cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_UNMASKED_USER"))
	private RfiEvent event;

	
	public RfiUnMaskedUser() {
	}

	public RfiUnMaskedUser(User user) {
		if (user != null) {
			this.setUser(user);
			this.setId(user.getId());
		}
	}
	
	
	/**
	 * @return the event
	 */
	public RfiEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfiEvent event) {
		this.event = event;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	@Override
	public String toLogString() {
		return "RfiEventApproval [" + super.toLogString() + "]";
	}

	public RfiUnMaskedUser copyFrom() {
		RfiUnMaskedUser newTm = new RfiUnMaskedUser();
		newTm.setUser(getUser());
		newTm.setEvent(getEvent());
		newTm.setUserUnmasked(getUserUnmasked());
		newTm.setUserUnmaskedTime(getUserUnmaskedTime());
		return newTm;
	}

}
