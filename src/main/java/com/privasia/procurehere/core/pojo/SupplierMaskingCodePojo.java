package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author yogesh
 */
public class SupplierMaskingCodePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8160798343768927186L;

	private String enevelopeName;
	private String makedCode;

	/**
	 * @return the enevelopeName
	 */
	public String getEnevelopeName() {
		return enevelopeName;
	}

	/**
	 * @param enevelopeName the enevelopeName to set
	 */
	public void setEnevelopeName(String enevelopeName) {
		this.enevelopeName = enevelopeName;
	}

	/**
	 * @return the makedCode
	 */
	public String getMakedCode() {
		return makedCode;
	}

	/**
	 * @param makedCode the makedCode to set
	 */
	public void setMakedCode(String makedCode) {
		this.makedCode = makedCode;
	}

}
