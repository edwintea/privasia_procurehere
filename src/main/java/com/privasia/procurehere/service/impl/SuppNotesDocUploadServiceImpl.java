package com.privasia.procurehere.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.privasia.procurehere.core.dao.SuppNotesDocUploadDao;
import com.privasia.procurehere.core.entity.SupplierNoteDocument;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.SuppNotesDocUploadService;

@Service
@Transactional(readOnly = true)
public class SuppNotesDocUploadServiceImpl implements SuppNotesDocUploadService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	SuppNotesDocUploadDao suppNotesDocUploadDao;

	@Override
	@Transactional(readOnly = false)
	public SupplierNoteDocument saveOrUpdate(SupplierNoteDocument notesDoc) {
		return suppNotesDocUploadDao.saveOrUpdate(notesDoc);
	}

	@Override
	public void downloadSuppNotesDoc(String docId, HttpServletResponse response) throws Exception {
		SupplierNoteDocument docs = findSuppNotesDocById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	public SupplierNoteDocument findSuppNotesDocById(String docId) {
		return suppNotesDocUploadDao.findById(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeSuuppNoteDocument(SupplierNoteDocument supplierNoteDocument) {
		suppNotesDocUploadDao.delete(supplierNoteDocument);
	}

	@Override
	public SupplierNoteDocument findSuppNoteDocById(String removeDocId) {
		return suppNotesDocUploadDao.findById(removeDocId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSuppNotesDocumentDesc(String docId, String docDesc, String supplierId) {
		SupplierNoteDocument supplierNoteDocument = findSuppNoteDocById(docId);
		supplierNoteDocument.setDescription(docDesc);
		supplierNoteDocument.setCreatedBy(SecurityLibrary.getLoggedInUser());
		suppNotesDocUploadDao.saveOrUpdate(supplierNoteDocument);
	}

	@Override
	public List<SupplierNoteDocument> findSuppNotesDocBySuppId(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input) {
		return suppNotesDocUploadDao.findSuppNotesDocBySuppId(suppId, loggedInUserTenantId, tenantType, input);
	}

	@Override
	public long findTotalFilteredSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input) {
		return suppNotesDocUploadDao.findTotalFilteredSupNotesDocList(suppId, loggedInUserTenantId, tenantType, input);
	}

	@Override
	public long findTotalSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType) {
		return suppNotesDocUploadDao.findTotalSupNotesDocList(suppId, loggedInUserTenantId, tenantType);
	}

}