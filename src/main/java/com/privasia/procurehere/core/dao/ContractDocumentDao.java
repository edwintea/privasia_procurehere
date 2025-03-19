package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ContractDocument;
import com.privasia.procurehere.core.entity.RfaEventDocument;

public interface ContractDocumentDao extends GenericDao<ContractDocument, String> {

	List<ContractDocument> findAllContractdocsbyContractId(String productContractId);

	void deleteById(String removeContractDocID);

	ContractDocument findContractDocsById(String docId);

	List<ContractDocument> findDocumentsById(String documentId);

	List<ContractDocument> findProductContractByIdForDocument(String contractId);

}
