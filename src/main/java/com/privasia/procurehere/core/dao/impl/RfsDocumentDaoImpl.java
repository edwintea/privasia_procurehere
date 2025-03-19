package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfsDocumentDao;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author sudesha
 */
@Repository
public class RfsDocumentDaoImpl extends GenericDaoImpl<RfsDocument, String> implements RfsDocumentDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfsDocument> findAllPrDocsbyRfsId(String rfsId) {
		final Query query = getEntityManager().createQuery("from RfsDocument pd inner join fetch pd.sourcingFormRequest p where p.id =:rfsId");
		query.setParameter("rfsId", rfsId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfsDocument> findAllPlainRfsDocsbyRfsId(String rfsId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfsDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb, p, u) from RfsDocument pd inner join pd.sourcingFormRequest p left outer join pd.uploadBy u where p.id = :rfsId");
		query.setParameter("rfsId", rfsId);
		return query.getResultList();
	}

	@Override
	public List<RfsDocument> findAllPlainRfsDocsbyRfsIdWithInternal(String rfsId) {
		final Query query = getEntityManager().createQuery(
				"select new com.privasia.procurehere.core.entity.RfsDocument(pd.id, pd.fileName, pd.description, pd.uploadDate, pd.credContentType, pd.fileSizeInKb, pd.internal, p, u) " +
						"from RfsDocument pd " +
						"inner join pd.sourcingFormRequest p " +
						"left outer join pd.uploadBy u " +
						"where p.id = :rfsId " +
						"order by pd.internal asc"
		);
		query.setParameter("rfsId", rfsId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfsDocumentPojo> findAllPlainRfsDocsbyRfsIdAndUploadBy(String rfsId) {
		StringBuilder builder = new StringBuilder("select new com.privasia.procurehere.core.pojo.RfsDocumentPojo(rf.id, rf.fileName, rf.description, rf.uploadDate, rf.credContentType, rf.fileSizeInKb, p.id, up.id, up.loginId, up.name, rf.internal) from RfsDocument rf inner join rf.sourcingFormRequest as p inner join rf.uploadBy as up where p.id = :rfsId");
		final Query query = getEntityManager().createQuery(builder.toString());
		query.setParameter("rfsId", rfsId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfsDocument> findAllRfsDocsNameAndId(String rfsId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfsDocument(pd.id, pd.fileName, pd.fileSizeInKb, pd.credContentType) from RfsDocument pd where pd.sourcingFormRequest.id =:rfsId");
		query.setParameter("rfsId", rfsId);
		return query.getResultList();
	}

	@Override
	public String findUploadFileName(String id) {
		StringBuilder builder = new StringBuilder("select rfs.fileName from RfsDocument rfs where rfs.id =:id ");
		final Query query = getEntityManager().createQuery(builder.toString());
		query.setParameter("id", id);
		return (String) query.getSingleResult();
	}
}
