package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author ravi
 */
public class SupplierDetailsCountPojo implements Serializable {

	private static final long serialVersionUID = 5152355255164171099L;

	private String id;

	private Long companyProfile = 0l;

	private Long otherDocs = 0l;

	private Long trackRecord = 0l;

	private Long fincDocs = 0l;

	private Long bordDir = 0l;

	public SupplierDetailsCountPojo() {
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
	 * @return the companyProfile
	 */
	public Long getCompanyProfile() {
		return companyProfile;
	}

	/**
	 * @param companyProfile the companyProfile to set
	 */
	public void setCompanyProfile(Long companyProfile) {
		this.companyProfile = companyProfile;
	}

	/**
	 * @return the otherDocs
	 */
	public Long getOtherDocs() {
		return otherDocs;
	}

	/**
	 * @param otherDocs the otherDocs to set
	 */
	public void setOtherDocs(Long otherDocs) {
		this.otherDocs = otherDocs;
	}

	/**
	 * @return the trackRecord
	 */
	public Long getTrackRecord() {
		return trackRecord;
	}

	/**
	 * @param trackRecord the trackRecord to set
	 */
	public void setTrackRecord(Long trackRecord) {
		this.trackRecord = trackRecord;
	}

	/**
	 * @return the fincDocs
	 */
	public Long getFincDocs() {
		return fincDocs;
	}

	/**
	 * @param fincDocs the fincDocs to set
	 */
	public void setFincDocs(Long fincDocs) {
		this.fincDocs = fincDocs;
	}

	/**
	 * @return the bordDir
	 */
	public Long getBordDir() {
		return bordDir;
	}

	/**
	 * @param bordDir the bordDir to set
	 */
	public void setBordDir(Long bordDir) {
		this.bordDir = bordDir;
	}

	public String toLogString() {
		return "SupplierDetailsCountPojo [id=" + id + ", companyProfile=" + companyProfile + ", otherDocs=" + otherDocs + ", trackRecord=" + trackRecord + ", fincDocs=" + fincDocs + ", bordDir=" + bordDir + "]";
	}

	
}
