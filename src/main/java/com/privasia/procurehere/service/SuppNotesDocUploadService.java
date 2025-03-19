package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.SupplierNoteDocument;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

//import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Teja
 */

public interface SuppNotesDocUploadService {

	/**
	 * @param notesDoc
	 * @return
	 */
	SupplierNoteDocument saveOrUpdate(SupplierNoteDocument notesDoc);

	/**
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadSuppNotesDoc(String docId, HttpServletResponse response) throws Exception;

	/**
	 * @param supplierNoteDocument
	 */
	void removeSuuppNoteDocument(SupplierNoteDocument supplierNoteDocument);

	/**
	 * @param removeDocId
	 * @return
	 */
	SupplierNoteDocument findSuppNoteDocById(String removeDocId);

	/**
	 * @param docId
	 * @param docDesc
	 * @param supplierId
	 */
	void updateSuppNotesDocumentDesc(String docId, String docDesc, String supplierId);

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @param input
	 * @return
	 */

	List<SupplierNoteDocument> findSuppNotesDocBySuppId(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input);

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @param input
	 * @return
	 */
	long findTotalFilteredSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input);

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @return
	 */
	long findTotalSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType);
}