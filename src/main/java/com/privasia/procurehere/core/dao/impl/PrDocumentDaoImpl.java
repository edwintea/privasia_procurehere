package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PrDocumentDao;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author parveen
 */
@Repository
public class PrDocumentDaoImpl extends GenericDaoImpl<PrDocument, String> implements PrDocumentDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<PrDocument> findAllPrDocsbyPrId(String prId) {
		final Query query = getEntityManager().createQuery("from PrDocument pd inner join fetch pd.pr p where p.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrDocument> findAllPlainPrDocsbyPrId(String prId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PrDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb, p) from PrDocument pd inner join pd.pr p where p.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrDocument> findAllPrDocsNameAndId(String prId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PrDocument(pd.id, pd.fileName, pd.fileSizeInKb, pd.credContentType) from PrDocument pd where pd.pr.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}
}
