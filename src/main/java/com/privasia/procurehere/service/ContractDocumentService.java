package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.ContractDocument;

public interface ContractDocumentService {

	ContractDocument saveContractDocument(ContractDocument contractDocument);

	List<ContractDocument> findAllContractdocsbyContractId(String productContractId);

	void removeContractDocument(String removeContractDocID);

	void downloadContractDocument(String id, HttpServletResponse response) throws Exception;

	List<ContractDocument>  findDocumentById(String documentId);

	void deleteDocument(ContractDocument contractDocument);

	List<ContractDocument> findProductContractByIdForDocument(String contractId);

	ContractDocument findContractDocumentById(String id);


}
