package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PurchaseOrderDocumentDao;
import com.privasia.procurehere.core.entity.PurchaseOrderDocument;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author ravi
 */
@Repository
public class PurchaseOrderDocumentDaoImpl extends GenericDaoImpl<PurchaseOrderDocument, String> implements PurchaseOrderDocumentDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<PurchaseOrderDocument> findAllPoDocsbyPoId(String prId) {
		final Query query = getEntityManager().createQuery("from PurchaseOrderDocument pd inner join fetch pd.po p where p.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PurchaseOrderDocument> findAllPlainPoDocsbyPoId(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PurchaseOrderDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb,p, pd.internal) from PurchaseOrderDocument pd inner join pd.po p where p.id =:poId order by pd.uploadDate");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PurchaseOrderDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PurchaseOrderDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb,p, pd.internal) from PurchaseOrderDocument pd inner join pd.po p where p.id =:poId and pd.internal = false");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PurchaseOrderDocument> findAllPoDocsNameAndId(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PurchaseOrderDocument(pd.id, pd.fileName, pd.fileSizeInKb, pd.credContentType) from PurchaseOrderDocument pd where pd.po.id =:poId");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@Override
	public void deleteDocument(String docId, String poId) {
		final Query query = getEntityManager().createQuery("delete from PurchaseOrderDocument pd where pd.id =:docId and pd.po.id = :poId");
		query.setParameter("docId", docId);
		query.setParameter("poId", poId);
		query.executeUpdate();
	}
}
