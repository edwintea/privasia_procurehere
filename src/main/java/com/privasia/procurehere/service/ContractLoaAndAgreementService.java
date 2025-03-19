package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;

public interface ContractLoaAndAgreementService {

	ContractLoaAndAgreement saveLoaAndAgrContractDocument(ContractLoaAndAgreement contractLoaAndAgrDocument);

	List<ContractLoaAndAgreement> getPlainContractLoaAndAgreementByContractId(String id);

	void downloadLoaDocument(String id, HttpServletResponse response) throws Exception;

	void downloadAgreementDocument(String id, HttpServletResponse response) throws Exception;

	void removeLoaDocument(String documentId);

	void removeAgreementDocument(String documentId);

	ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String contractId);

}
