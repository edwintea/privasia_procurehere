package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierCommentDao;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierComment;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Vipul
 */
@Repository
public class RftSupplierCommentDaoImpl extends GenericDaoImpl<RftSupplierComment, String> implements RftSupplierCommentDao {

	private static final Logger LOG = LogManager.getLogger(RftSupplierCommentDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId) {
		LOG.info("findByBqId bqItemId : " + bqItemId);
		final Query query = getEntityManager().createQuery("select rc from RftSupplierComment as rc where rc.rftSupplierBqItem.id =:id and rc.rftSupplierBqItem.supplier.id =:supplierId");
		query.setParameter("id", bqItemId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteSupplierComments(String eventId) {
		Query query = getEntityManager().createQuery("select rc from RftSupplierBqItem as rc where rc.event.id =:id ");
		query.setParameter("id", eventId);
		List<RftSupplierBqItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftSupplierBqItem item : list) {
				query = getEntityManager().createQuery("delete from RftSupplierComment as rc where rc.rftSupplierBqItem.id =:id");
				query.setParameter("id", item.getId());
				query.executeUpdate();
			}
		}
	}
}