package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpSupplierCommentDao;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierComment;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Vipul
 */
@Repository
public class RfpSupplierCommentDaoImpl extends GenericDaoImpl<RfpSupplierComment, String> implements RfpSupplierCommentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId) {
		final Query query = getEntityManager().createQuery("select rc from RfpSupplierComment as rc where rc.supplierBqItem.id =:id and rc.supplierBqItem.supplier.id =:supplierId");
		query.setParameter("id", bqItemId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();

	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteSupplierComments(String eventId) {
		Query query = getEntityManager().createQuery("select rc from RfpSupplierBqItem as rc where rc.event.id =:id ");
		query.setParameter("id", eventId);
		List<RfpSupplierBqItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpSupplierBqItem item : list) {
				query = getEntityManager().createQuery("delete from RfpSupplierComment as rc where rc.supplierBqItem.id =:id");
				query.setParameter("id", item.getId());
				query.executeUpdate();
			}
		}
	}
}