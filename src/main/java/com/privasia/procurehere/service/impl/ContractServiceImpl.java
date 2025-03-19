package com.privasia.procurehere.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.privasia.procurehere.core.dao.ContractDocumentDao;
import com.privasia.procurehere.core.entity.ContractDocument;
import com.privasia.procurehere.service.ContractDocumentService;

@Service
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractDocumentService {
	
	@Autowired
	ContractDocumentDao contractDocumentDao;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public ContractDocument saveContractDocument(ContractDocument contractDocument) {
		return contractDocumentDao.saveOrUpdate(contractDocument);
	}

	@Override
	public List<ContractDocument> findAllContractdocsbyContractId(String productContractId) {
		return contractDocumentDao.findAllContractdocsbyContractId(productContractId);
	}

	@Override
	public void removeContractDocument(String removeContractDocID) {
		contractDocumentDao.deleteById(removeContractDocID);
	}
	
	@Override
	public void downloadContractDocument(String docId, HttpServletResponse response) throws Exception {
		ContractDocument docs = contractDocumentDao.findContractDocsById(docId);
		response.setContentType(docs.getContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public List<ContractDocument>  findDocumentById(String documentId) {
		return contractDocumentDao.findDocumentsById(documentId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteDocument(ContractDocument contractDocument) {
		contractDocumentDao.delete(contractDocument);
	}
	
	@Override
	public List<ContractDocument> findProductContractByIdForDocument(String contractId) {
		return contractDocumentDao.findProductContractByIdForDocument(contractId);
	}
	
	@Override
	public ContractDocument findContractDocumentById(String id) {
		return contractDocumentDao.findById(id);
	}
	

	

}
