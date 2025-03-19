package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * @author Nitin Otageri
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractChangeErpResponsePojo implements Serializable {

	private static final long serialVersionUID = 8903208397781717555L;

	private ContractErpResponse res;

	public ContractChangeErpResponsePojo() {

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ContractErpResponse {

		private ContractErpInnerResponse res;

		public ContractErpResponse() {

		}

		/**
		 * @return the res
		 */
		public ContractErpInnerResponse getRes() {
			return res;
		}

		/**
		 * @param res the res to set
		 */
		@JsonSetter("PH_Res")
		public void setRes(ContractErpInnerResponse res) {
			this.res = res;
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ContractErpInnerResponse {

		@JsonProperty("TYPE")
		private String type;

		@JsonProperty("ID")
		private String id;

		@JsonProperty("NUMBER")
		private String number;

		@JsonProperty("MESSAGE")
		private String message;

		public ContractErpInnerResponse() {

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

		
		public String getNumber() {
			return number;
		}

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
	public ContractErpResponse getRes() {
		return res;
	}

	/**
	 * @param res the res to set
	 */
	@JsonSetter("MT_ContractChange_PHV2_Res")
	public void setRes(ContractErpResponse res) {
		this.res = res;
	}
}
