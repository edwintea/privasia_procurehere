package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.core.pojo.ContractLoaAndAgreementPojo;

/**
 * @author anshul
 */
public interface ContractLoaAndAgreementDao extends GenericDao<ContractLoaAndAgreement, String> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String id);

	/**
	 * 
	 * @param Id
	 * @return
	 */
	List<ContractLoaAndAgreement> findPlainContractLoaAndAgreementByContractId(String Id);

	ContractLoaAndAgreement findLoaAndAgreeDocsByDocId(String id);

}
