package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaSupplierCommentDao;
import com.privasia.procurehere.core.entity.RfaSupplierComment;

/**
 * @author Vipul
 */
@Repository
public class RfaSupplierCommentDaoImpl extends GenericDaoImpl<RfaSupplierComment, String> implements RfaSupplierCommentDao {

	private static final Logger LOG = LogManager.getLogger(RfaSupplierCommentDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId) {
		LOG.info("findByBqId bqItemId : " + bqItemId);
		final Query query = getEntityManager().createQuery("select rc from RfaSupplierComment as rc where rc.rfaSupplierBqItem.id =:id and rc.rfaSupplierBqItem.supplier.id =:supplierId");
		query.setParameter("id", bqItemId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * @Override public RftSupplierComment findByRemarkId(String id, String supplierId) { LOG.info("findByBqId()" + id);
	 * try { final Query query = getEntityManager().
	 * createQuery("from RftSupplierComment as rc inner join fetch rc.rftSupplierBqItem as rb inner join rb.supplier s inner join rc.createdBy cb where rb.id =:id and s.id =:supplierId"
	 * ); query.setParameter("id", id); query.setParameter("supplierId", supplierId); List<RftSupplierComment> uList =
	 * query.getResultList(); LOG.info("RftSupplierComment()" + uList.size()); if (CollectionUtil.isNotEmpty(uList)) {
	 * return uList.get(0); } else { return null; } } catch (NoResultException nr) {
	 * LOG.info("Error while getting Comment BQ : " + nr.getMessage(), nr); return null; } }
	 */

}