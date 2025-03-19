package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_EVENTID_SETTINGS")
public class EventIdSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6133444179934889618L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@Column(name = "RFT_EVENT_SEQUENCE", length = 64)
	private Integer rftIdSequence;

	@Column(name = "RFT_EVENTID_PREFIX", length = 64)
	private String rftIdPerfix;

	@Column(name = "RFT_EVENT_DELIMITER", length = 64)
	private String rftIdDelimiter;

	@Column(name = "RFT_DATE_PATTERN", length = 64)
	private String rftIdDatePattern;

	@Column(name = "RFT_PADDING_LENGTH")
	private Integer rftPaddingLength = null;

	@Column(name = "RFQ_EVENT_SEQUENCE", length = 64)
	private Integer rfqIdSequence;

	@Column(name = "RFQ_EVENTID_PREFIX", length = 64)
	private String rfqIdPerfix;

	@Column(name = "RFQ_EVENT_DELIMITER", length = 64)
	private String rfqIdDelimiter;

	@Column(name = "RFQ_DATE_PATTERN", length = 64)
	private String rfqIdDatePattern;

	@Column(name = "RTQ_PADDING_LENGTH")
	private Integer rfqPaddingLength = null;

	@Column(name = "RFP_EVENT_SEQUENCE", length = 64)
	private Integer rfpIdSequence;

	@Column(name = "RFP_EVENTID_PREFIX", length = 64)
	private String rfpIdPerfix;

	@Column(name = "RFP_EVENT_DELIMITER", length = 64)
	private String rfpIdDelimiter;

	@Column(name = "RFP_DATE_PATTERN", length = 64)
	private String rfpIdDatePattern;

	@Column(name = "RTP_PADDING_LENGTH")
	private Integer rfpPaddingLength = null;

	@Column(name = "RFI_EVENT_SEQUENCE", length = 64)
	private Integer rfiIdSequence;

	@Column(name = "RFI_EVENTID_PREFIX", length = 64)
	private String rfiIdPerfix;

	@Column(name = "RFI_EVENT_DELIMITER", length = 64)
	private String rfiIdDelimiter;

	@Column(name = "RFI_DATE_PATTERN", length = 64)
	private String rfiIdDatePattern;

	@Column(name = "RTI_PADDING_LENGTH")
	private Integer rfiPaddingLength = null;

	@Column(name = "PR_ID_SEQUENCE", length = 64)
	private Integer prIdSequence;

	@Column(name = "PR_ID_PREFIX", length = 64)
	private String prIdPerfix;

	@Column(name = "PR_ID_DELIMITER", length = 64)
	private String prIdDelimiter;

	@Column(name = "PR_ID_DATE_PATTERN", length = 64)
	private String prIdDatePattern;

	@Column(name = "USE_COMMON_SEQUENCE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean useCommonSequence = Boolean.FALSE;

	@Column(name = "PR_PADDING_LENGTH")
	private Integer prPaddingLength = null;

	@Column(name = "RFA_EVENT_SEQUENCE", length = 64)
	private Integer rfaIdSequence;

	@Column(name = "RFA_EVENTID_PREFIX", length = 64)
	private String rfaIdPerfix;

	@Column(name = "RFA_EVENT_DELIMITER", length = 64)
	private String rfaIdDelimiter;

	@Column(name = "RFA_DATE_PATTERN", length = 64)
	private String rfaIdDatePattern;

	@Column(name = "RFA_PADDING_LENGTH")
	private Integer rfaPaddingLength = null;

	public EventIdSettings() {
		this.useCommonSequence = Boolean.FALSE;
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
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the rftIdSequence
	 */
	public Integer getRftIdSequence() {
		return rftIdSequence;
	}

	/**
	 * @param rftIdSequence the rftIdSequence to set
	 */
	public void setRftIdSequence(Integer rftIdSequence) {
		this.rftIdSequence = rftIdSequence;
	}

	/**
	 * @return the rftIdPerfix
	 */
	public String getRftIdPerfix() {
		return rftIdPerfix;
	}

	/**
	 * @param rftIdPerfix the rftIdPerfix to set
	 */
	public void setRftIdPerfix(String rftIdPerfix) {
		this.rftIdPerfix = rftIdPerfix;
	}

	/**
	 * @return the rftIdDelimiter
	 */
	public String getRftIdDelimiter() {
		return rftIdDelimiter;
	}

	/**
	 * @param rftIdDelimiter the rftIdDelimiter to set
	 */
	public void setRftIdDelimiter(String rftIdDelimiter) {
		this.rftIdDelimiter = rftIdDelimiter;
	}

	/**
	 * @return the rftIdDatePattern
	 */
	public String getRftIdDatePattern() {
		return rftIdDatePattern;
	}

	/**
	 * @param rftIdDatePattern the rftIdDatePattern to set
	 */
	public void setRftIdDatePattern(String rftIdDatePattern) {
		this.rftIdDatePattern = rftIdDatePattern;
	}

	/**
	 * @return the rfqIdSequence
	 */
	public Integer getRfqIdSequence() {
		return rfqIdSequence;
	}

	/**
	 * @param rfqIdSequence the rfqIdSequence to set
	 */
	public void setRfqIdSequence(Integer rfqIdSequence) {
		this.rfqIdSequence = rfqIdSequence;
	}

	/**
	 * @return the rfqIdPerfix
	 */
	public String getRfqIdPerfix() {
		return rfqIdPerfix;
	}

	/**
	 * @param rfqIdPerfix the rfqIdPerfix to set
	 */
	public void setRfqIdPerfix(String rfqIdPerfix) {
		this.rfqIdPerfix = rfqIdPerfix;
	}

	/**
	 * @return the rfqIdDelimiter
	 */
	public String getRfqIdDelimiter() {
		return rfqIdDelimiter;
	}

	/**
	 * @param rfqIdDelimiter the rfqIdDelimiter to set
	 */
	public void setRfqIdDelimiter(String rfqIdDelimiter) {
		this.rfqIdDelimiter = rfqIdDelimiter;
	}

	/**
	 * @return the rfqIdDatePattern
	 */
	public String getRfqIdDatePattern() {
		return rfqIdDatePattern;
	}

	/**
	 * @param rfqIdDatePattern the rfqIdDatePattern to set
	 */
	public void setRfqIdDatePattern(String rfqIdDatePattern) {
		this.rfqIdDatePattern = rfqIdDatePattern;
	}

	/**
	 * @return the rfpIdSequence
	 */
	public Integer getRfpIdSequence() {
		return rfpIdSequence;
	}

	/**
	 * @param rfpIdSequence the rfpIdSequence to set
	 */
	public void setRfpIdSequence(Integer rfpIdSequence) {
		this.rfpIdSequence = rfpIdSequence;
	}

	/**
	 * @return the rfpIdPerfix
	 */
	public String getRfpIdPerfix() {
		return rfpIdPerfix;
	}

	/**
	 * @param rfpIdPerfix the rfpIdPerfix to set
	 */
	public void setRfpIdPerfix(String rfpIdPerfix) {
		this.rfpIdPerfix = rfpIdPerfix;
	}

	/**
	 * @return the rfpIdDelimiter
	 */
	public String getRfpIdDelimiter() {
		return rfpIdDelimiter;
	}

	/**
	 * @param rfpIdDelimiter the rfpIdDelimiter to set
	 */
	public void setRfpIdDelimiter(String rfpIdDelimiter) {
		this.rfpIdDelimiter = rfpIdDelimiter;
	}

	/**
	 * @return the rfpIdDatePattern
	 */
	public String getRfpIdDatePattern() {
		return rfpIdDatePattern;
	}

	/**
	 * @param rfpIdDatePattern the rfpIdDatePattern to set
	 */
	public void setRfpIdDatePattern(String rfpIdDatePattern) {
		this.rfpIdDatePattern = rfpIdDatePattern;
	}

	/**
	 * @return the rfiIdSequence
	 */
	public Integer getRfiIdSequence() {
		return rfiIdSequence;
	}

	/**
	 * @param rfiIdSequence the rfiIdSequence to set
	 */
	public void setRfiIdSequence(Integer rfiIdSequence) {
		this.rfiIdSequence = rfiIdSequence;
	}

	/**
	 * @return the rfiIdPerfix
	 */
	public String getRfiIdPerfix() {
		return rfiIdPerfix;
	}

	/**
	 * @param rfiIdPerfix the rfiIdPerfix to set
	 */
	public void setRfiIdPerfix(String rfiIdPerfix) {
		this.rfiIdPerfix = rfiIdPerfix;
	}

	/**
	 * @return the rfiIdDelimiter
	 */
	public String getRfiIdDelimiter() {
		return rfiIdDelimiter;
	}

	/**
	 * @param rfiIdDelimiter the rfiIdDelimiter to set
	 */
	public void setRfiIdDelimiter(String rfiIdDelimiter) {
		this.rfiIdDelimiter = rfiIdDelimiter;
	}

	/**
	 * @return the rfiIdDatePattern
	 */
	public String getRfiIdDatePattern() {
		return rfiIdDatePattern;
	}

	/**
	 * @param rfiIdDatePattern the rfiIdDatePattern to set
	 */
	public void setRfiIdDatePattern(String rfiIdDatePattern) {
		this.rfiIdDatePattern = rfiIdDatePattern;
	}

	/**
	 * @return the prIdSequence
	 */
	public Integer getPrIdSequence() {
		return prIdSequence;
	}

	/**
	 * @param prIdSequence the prIdSequence to set
	 */
	public void setPrIdSequence(Integer prIdSequence) {
		this.prIdSequence = prIdSequence;
	}

	/**
	 * @return the prIdPerfix
	 */
	public String getPrIdPerfix() {
		return prIdPerfix;
	}

	/**
	 * @param prIdPerfix the prIdPerfix to set
	 */
	public void setPrIdPerfix(String prIdPerfix) {
		this.prIdPerfix = prIdPerfix;
	}

	/**
	 * @return the prIdDelimiter
	 */
	public String getPrIdDelimiter() {
		return prIdDelimiter;
	}

	/**
	 * @param prIdDelimiter the prIdDelimiter to set
	 */
	public void setPrIdDelimiter(String prIdDelimiter) {
		this.prIdDelimiter = prIdDelimiter;
	}

	/**
	 * @return the prIdDatePattern
	 */
	public String getPrIdDatePattern() {
		return prIdDatePattern;
	}

	/**
	 * @param prIdDatePattern the prIdDatePattern to set
	 */
	public void setPrIdDatePattern(String prIdDatePattern) {
		this.prIdDatePattern = prIdDatePattern;
	}

	/**
	 * @return the useCommonSequence
	 */
	public Boolean getUseCommonSequence() {
		return useCommonSequence;
	}

	/**
	 * @param useCommonSequence the useCommonSequence to set
	 */
	public void setUseCommonSequence(Boolean useCommonSequence) {
		this.useCommonSequence = useCommonSequence;
	}

	/**
	 * @return the rftPaddingLength
	 */
	public Integer getRftPaddingLength() {
		return rftPaddingLength;
	}

	/**
	 * @param rftPaddingLength the rftPaddingLength to set
	 */
	public void setRftPaddingLength(Integer rftPaddingLength) {
		this.rftPaddingLength = rftPaddingLength;
	}

	/**
	 * @return the rfqPaddingLength
	 */
	public Integer getRfqPaddingLength() {
		return rfqPaddingLength;
	}

	/**
	 * @param rfqPaddingLength the rfqPaddingLength to set
	 */
	public void setRfqPaddingLength(Integer rfqPaddingLength) {
		this.rfqPaddingLength = rfqPaddingLength;
	}

	/**
	 * @return the rfpPaddingLength
	 */
	public Integer getRfpPaddingLength() {
		return rfpPaddingLength;
	}

	/**
	 * @param rfpPaddingLength the rfpPaddingLength to set
	 */
	public void setRfpPaddingLength(Integer rfpPaddingLength) {
		this.rfpPaddingLength = rfpPaddingLength;
	}

	/**
	 * @return the rfiPaddingLength
	 */
	public Integer getRfiPaddingLength() {
		return rfiPaddingLength;
	}

	/**
	 * @param rfiPaddingLength the rfiPaddingLength to set
	 */
	public void setRfiPaddingLength(Integer rfiPaddingLength) {
		this.rfiPaddingLength = rfiPaddingLength;
	}

	/**
	 * @return the prPaddingLength
	 */
	public Integer getPrPaddingLength() {
		return prPaddingLength;
	}

	/**
	 * @param prPaddingLength the prPaddingLength to set
	 */
	public void setPrPaddingLength(Integer prPaddingLength) {
		this.prPaddingLength = prPaddingLength;
	}

	/**
	 * @return the rfaIdSequence
	 */
	public Integer getRfaIdSequence() {
		return rfaIdSequence;
	}

	/**
	 * @param rfaIdSequence the rfaIdSequence to set
	 */
	public void setRfaIdSequence(Integer rfaIdSequence) {
		this.rfaIdSequence = rfaIdSequence;
	}

	/**
	 * @return the rfaIdPerfix
	 */
	public String getRfaIdPerfix() {
		return rfaIdPerfix;
	}

	/**
	 * @param rfaIdPerfix the rfaIdPerfix to set
	 */
	public void setRfaIdPerfix(String rfaIdPerfix) {
		this.rfaIdPerfix = rfaIdPerfix;
	}

	/**
	 * @return the rfaIdDelimiter
	 */
	public String getRfaIdDelimiter() {
		return rfaIdDelimiter;
	}

	/**
	 * @param rfaIdDelimiter the rfaIdDelimiter to set
	 */
	public void setRfaIdDelimiter(String rfaIdDelimiter) {
		this.rfaIdDelimiter = rfaIdDelimiter;
	}

	/**
	 * @return the rfaIdDatePattern
	 */
	public String getRfaIdDatePattern() {
		return rfaIdDatePattern;
	}

	/**
	 * @param rfaIdDatePattern the rfaIdDatePattern to set
	 */
	public void setRfaIdDatePattern(String rfaIdDatePattern) {
		this.rfaIdDatePattern = rfaIdDatePattern;
	}

	/**
	 * @return the rfaPaddingLength
	 */
	public Integer getRfaPaddingLength() {
		return rfaPaddingLength;
	}

	/**
	 * @param rfaPaddingLength the rfaPaddingLength to set
	 */
	public void setRfaPaddingLength(Integer rfaPaddingLength) {
		this.rfaPaddingLength = rfaPaddingLength;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rfiIdDatePattern == null) ? 0 : rfiIdDatePattern.hashCode());
		result = prime * result + ((rfiIdDelimiter == null) ? 0 : rfiIdDelimiter.hashCode());
		result = prime * result + ((rfiIdPerfix == null) ? 0 : rfiIdPerfix.hashCode());
		result = prime * result + ((rfiIdSequence == null) ? 0 : rfiIdSequence.hashCode());
		result = prime * result + ((rfpIdDatePattern == null) ? 0 : rfpIdDatePattern.hashCode());
		result = prime * result + ((rfpIdDelimiter == null) ? 0 : rfpIdDelimiter.hashCode());
		result = prime * result + ((rfpIdPerfix == null) ? 0 : rfpIdPerfix.hashCode());
		result = prime * result + ((rfpIdSequence == null) ? 0 : rfpIdSequence.hashCode());
		result = prime * result + ((rfqIdDatePattern == null) ? 0 : rfqIdDatePattern.hashCode());
		result = prime * result + ((rfqIdDelimiter == null) ? 0 : rfqIdDelimiter.hashCode());
		result = prime * result + ((rfqIdPerfix == null) ? 0 : rfqIdPerfix.hashCode());
		result = prime * result + ((rfqIdSequence == null) ? 0 : rfqIdSequence.hashCode());
		result = prime * result + ((rftIdDatePattern == null) ? 0 : rftIdDatePattern.hashCode());
		result = prime * result + ((rftIdDelimiter == null) ? 0 : rftIdDelimiter.hashCode());
		result = prime * result + ((rftIdPerfix == null) ? 0 : rftIdPerfix.hashCode());
		result = prime * result + ((rftIdSequence == null) ? 0 : rftIdSequence.hashCode());
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		result = prime * result + ((useCommonSequence == null) ? 0 : useCommonSequence.hashCode());
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
		EventIdSettings other = (EventIdSettings) obj;
		if (rfiIdDatePattern == null) {
			if (other.rfiIdDatePattern != null)
				return false;
		} else if (!rfiIdDatePattern.equals(other.rfiIdDatePattern))
			return false;
		if (rfiIdDelimiter == null) {
			if (other.rfiIdDelimiter != null)
				return false;
		} else if (!rfiIdDelimiter.equals(other.rfiIdDelimiter))
			return false;
		if (rfiIdPerfix == null) {
			if (other.rfiIdPerfix != null)
				return false;
		} else if (!rfiIdPerfix.equals(other.rfiIdPerfix))
			return false;
		if (rfiIdSequence == null) {
			if (other.rfiIdSequence != null)
				return false;
		} else if (!rfiIdSequence.equals(other.rfiIdSequence))
			return false;
		if (rfpIdDatePattern == null) {
			if (other.rfpIdDatePattern != null)
				return false;
		} else if (!rfpIdDatePattern.equals(other.rfpIdDatePattern))
			return false;
		if (rfpIdDelimiter == null) {
			if (other.rfpIdDelimiter != null)
				return false;
		} else if (!rfpIdDelimiter.equals(other.rfpIdDelimiter))
			return false;
		if (rfpIdPerfix == null) {
			if (other.rfpIdPerfix != null)
				return false;
		} else if (!rfpIdPerfix.equals(other.rfpIdPerfix))
			return false;
		if (rfpIdSequence == null) {
			if (other.rfpIdSequence != null)
				return false;
		} else if (!rfpIdSequence.equals(other.rfpIdSequence))
			return false;
		if (rfqIdDatePattern == null) {
			if (other.rfqIdDatePattern != null)
				return false;
		} else if (!rfqIdDatePattern.equals(other.rfqIdDatePattern))
			return false;
		if (rfqIdDelimiter == null) {
			if (other.rfqIdDelimiter != null)
				return false;
		} else if (!rfqIdDelimiter.equals(other.rfqIdDelimiter))
			return false;
		if (rfqIdPerfix == null) {
			if (other.rfqIdPerfix != null)
				return false;
		} else if (!rfqIdPerfix.equals(other.rfqIdPerfix))
			return false;
		if (rfqIdSequence == null) {
			if (other.rfqIdSequence != null)
				return false;
		} else if (!rfqIdSequence.equals(other.rfqIdSequence))
			return false;
		if (rftIdDatePattern == null) {
			if (other.rftIdDatePattern != null)
				return false;
		} else if (!rftIdDatePattern.equals(other.rftIdDatePattern))
			return false;
		if (rftIdDelimiter == null) {
			if (other.rftIdDelimiter != null)
				return false;
		} else if (!rftIdDelimiter.equals(other.rftIdDelimiter))
			return false;
		if (rftIdPerfix == null) {
			if (other.rftIdPerfix != null)
				return false;
		} else if (!rftIdPerfix.equals(other.rftIdPerfix))
			return false;
		if (rftIdSequence == null) {
			if (other.rftIdSequence != null)
				return false;
		} else if (!rftIdSequence.equals(other.rftIdSequence))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		if (useCommonSequence == null) {
			if (other.useCommonSequence != null)
				return false;
		} else if (!useCommonSequence.equals(other.useCommonSequence))
			return false;
		return true;
	}

	public String toLogString() {
		return "EventIdSettings [id=" + id + ", tenantId=" + tenantId + ", rftIdSequence=" + rftIdSequence + ", rftIdPerfix=" + rftIdPerfix + ", rftIdDelimiter=" + rftIdDelimiter + ", rftIdDatePattern=" + rftIdDatePattern + ", rfqIdSequence=" + rfqIdSequence + ", rfqIdPerfix=" + rfqIdPerfix + ", rfqIdDelimiter=" + rfqIdDelimiter + ", rfqIdDatePattern=" + rfqIdDatePattern + ", rfpIdSequence=" + rfpIdSequence + ", rfpIdPerfix=" + rfpIdPerfix + ", rfpIdDelimiter=" + rfpIdDelimiter + ", rfpIdDatePattern=" + rfpIdDatePattern + ", rfiIdSequence=" + rfiIdSequence + ", rfiIdPerfix=" + rfiIdPerfix + ", rfiIdDelimiter=" + rfiIdDelimiter + ", rfiIdDatePattern=" + rfiIdDatePattern + ", useCommonSequence=" + useCommonSequence + "]";
	}

}
