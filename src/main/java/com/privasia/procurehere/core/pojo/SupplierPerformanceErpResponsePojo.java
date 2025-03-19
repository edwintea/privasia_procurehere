package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Nitin Otageri
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierPerformanceErpResponsePojo implements Serializable {

	private static final long serialVersionUID = 8903208397781717555L;

	private SupplierPerformanceErpResponse res;

	public SupplierPerformanceErpResponsePojo() {

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SupplierPerformanceErpResponse {

		private SupplierPerformanceErpInnerResponse res;

		public SupplierPerformanceErpResponse() {

		}

		/**
		 * @return the res
		 */
		public SupplierPerformanceErpInnerResponse getRes() {
			return res;
		}

		/**
		 * @param res the res to set
		 */
		@JsonSetter("PH_res")
		public void setRes(SupplierPerformanceErpInnerResponse res) {
			this.res = res;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class SupplierPerformanceErpInnerResponse {

		@JsonProperty("TYPE")
		private String type;

		@JsonProperty("ID")
		private String id;

		@JsonProperty("NUMBER")
		private String number;

		@JsonProperty("MESSAGE")
		private String message;

		public SupplierPerformanceErpInnerResponse() {

		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		@JsonSetter("TYPE")
		public void setType(String type) {
			this.type = type;
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
		@JsonSetter("ID")
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the number
		 */
		public String getNumber() {
			return number;
		}

		/**
		 * @param number the number to set
		 */
		@JsonSetter("NUMBER")
		public void setNumber(String number) {
			this.number = number;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		@JsonSetter("MESSAGE")
		public void setMessage(String message) {
			this.message = message;
		}

	}

	/**
	 * @return the res
	 */
	public SupplierPerformanceErpResponse getRes() {
		return res;
	}

	/**
	 * @param res the res to set
	 */
	@JsonSetter("MT_SupplierPerformMgmt_PHV2_Res")
	public void setRes(SupplierPerformanceErpResponse res) {
		this.res = res;
	}
}
