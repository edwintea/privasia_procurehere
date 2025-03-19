package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ApprovalDocumentDao;
import com.privasia.procurehere.core.entity.ApprovalDocument;

/**
 * @author sudesha
 */
@Repository
public class ApprovalDocumentDaoImpl extends GenericDaoImpl<ApprovalDocument, String> implements ApprovalDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovalDocument> findAllPlainApprovalDocsbyRfsId(String formId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ApprovalDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb, p) from ApprovalDocument pd inner join pd.sourcingFormRequest p where p.id =:formId");
		query.setParameter("formId", formId);
		return query.getResultList();
	}
	
	
	
	
	
	
	
}
