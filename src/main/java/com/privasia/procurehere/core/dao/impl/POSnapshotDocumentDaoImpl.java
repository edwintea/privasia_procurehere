package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.POSnapshotDocumentDao;
import com.privasia.procurehere.core.entity.POSnapshotDocument;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author ravi
 */
@Repository
public class POSnapshotDocumentDaoImpl extends GenericDaoImpl<POSnapshotDocument, String> implements POSnapshotDocumentDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<POSnapshotDocument> findAllPoDocsbyPoId(String prId) {
		final Query query = getEntityManager().createQuery("from POSnapshotDocument pd inner join fetch pd.po p where p.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<POSnapshotDocument> findAllPlainPoDocsbyPoId(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.POSnapshotDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb,p, pd.internal) from POSnapshotDocument pd inner join pd.po p where p.id =:poId");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<POSnapshotDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.POSnapshotDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb,p, pd.internal) from POSnapshotDocument pd inner join pd.po p where p.id =:poId and pd.internal = false");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<POSnapshotDocument> findAllPoDocsNameAndId(String poId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.POSnapshotDocument(pd.id, pd.fileName, pd.fileSizeInKb, pd.credContentType) from POSnapshotDocument pd where pd.po.id =:poId");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public POSnapshotDocument findPoDocument(String poId, String docId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.POSnapshotDocument(pd.id, pd.fileName, pd.fileSizeInKb, pd.credContentType) from POSnapshotDocument pd where pd.po.id =:poId and pd.id = :docId");
		query.setParameter("poId", poId);
		query.setParameter("docId", docId);
		List<POSnapshotDocument> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@Override
	public void deleteDocument(String poId) {
		final Query query = getEntityManager().createQuery("delete from POSnapshotDocument pd where pd.po.id = :poId");
		query.setParameter("poId", poId);
		query.executeUpdate();
	}
}
