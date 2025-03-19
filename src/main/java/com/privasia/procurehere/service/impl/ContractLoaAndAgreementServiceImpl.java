package com.privasia.procurehere.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.privasia.procurehere.core.dao.ContractLoaAndAgreementDao;
import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.service.ContractLoaAndAgreementService;

@Service
@Transactional(readOnly = true)
public class ContractLoaAndAgreementServiceImpl implements ContractLoaAndAgreementService {
	
	@Autowired
	ContractLoaAndAgreementDao contractLoaAndAgreementDao;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public ContractLoaAndAgreement saveLoaAndAgrContractDocument(ContractLoaAndAgreement contractLoaAndAgreement) {
		return contractLoaAndAgreementDao.saveOrUpdate(contractLoaAndAgreement);
	}

	@Override
	public List<ContractLoaAndAgreement> getPlainContractLoaAndAgreementByContractId(String id) {
		return contractLoaAndAgreementDao.findPlainContractLoaAndAgreementByContractId(id);
	}
	
	@Override
	public ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String contractId) {
		return contractLoaAndAgreementDao.findContractLoaAndAgreementByContractId(contractId);
	}

	@Override
	public void downloadLoaDocument(String id, HttpServletResponse response) throws Exception {
		ContractLoaAndAgreement doc =  contractLoaAndAgreementDao.findLoaAndAgreeDocsByDocId(id);
		response.setContentType(doc.getLoaContentType());
		response.setContentLength(doc.getLoaFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getLoaFileName() + "\"");
		FileCopyUtils.copy(doc.getLoaFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	public void downloadAgreementDocument(String id, HttpServletResponse response) throws Exception {
		ContractLoaAndAgreement doc =  contractLoaAndAgreementDao.findLoaAndAgreeDocsByDocId(id);
		response.setContentType(doc.getAgreementContentType());
		response.setContentLength(doc.getAgreementFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getAgreementFileName() + "\"");
		FileCopyUtils.copy(doc.getAgreementFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeLoaDocument(String documentId) {
		ContractLoaAndAgreement doc =  contractLoaAndAgreementDao.findLoaAndAgreeDocsByDocId(documentId);
		doc.setLoaContentType(null);
//		doc.setLoaDate(null);
//		doc.setLoaDescription(null);
		doc.setLoaFileData(null);
		doc.setLoaFileName(null);
		doc.setLoaFileSizeInKb(null);
		doc.setLoaUploadDate(null);
		doc.setLoaUploadedBy(null);
		contractLoaAndAgreementDao.saveOrUpdate(doc);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void removeAgreementDocument(String documentId) {
		ContractLoaAndAgreement doc =  contractLoaAndAgreementDao.findLoaAndAgreeDocsByDocId(documentId);
		doc.setAgreementContentType(null);
//		doc.setAgreementDate(null);
//		doc.setAgreementDescription(null);
		doc.setAgreementFileData(null);
		doc.setAgreementFileName(null);
		doc.setAgreementFileSizeInKb(null);
		doc.setAgreementUploadDate(null);
		doc.setAgreementUploadedBy(null);
		contractLoaAndAgreementDao.saveOrUpdate(doc);
	}
}

