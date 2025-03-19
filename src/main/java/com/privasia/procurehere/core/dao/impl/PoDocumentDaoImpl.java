package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoDocumentDao;
import com.privasia.procurehere.core.entity.PoDocument;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author parveen
 */
@Repository
public class PoDocumentDaoImpl extends GenericDaoImpl<PoDocument, String> implements PoDocumentDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<PoDocument> findAllPlainPoDocsbyPrId(String prId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PoDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb, p, pd.docType) from PoDocument pd inner join pd.pr p where p.id =:prId");
		query.setParameter("prId", prId);
		return query.getResultList();
	}

}
