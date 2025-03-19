package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ApprovalDocument;
import com.privasia.procurehere.core.entity.RfsDocument;

/**
 * @author sudesha
 */
public interface ApprovalDocumentDao extends GenericDao<ApprovalDocument, String> {


	List<ApprovalDocument> findAllPlainApprovalDocsbyRfsId(String formId);

}
