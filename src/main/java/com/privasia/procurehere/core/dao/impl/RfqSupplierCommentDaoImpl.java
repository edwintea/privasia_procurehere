package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqSupplierCommentDao;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierComment;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Vipul
 */
@Repository
public class RfqSupplierCommentDaoImpl extends GenericDaoImpl<RfqSupplierComment, String> implements RfqSupplierCommentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqSupplierComment> findSupplierCommentByBqIdAndSupplierId(String bqItemId, String supplierId) {
		final Query query = getEntityManager().createQuery("select rc from RfqSupplierComment as rc where supplierBqItem.id =:id and rc.supplierBqItem.supplier.id =:supplierId");
		query.setParameter("id", bqItemId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteSupplierComments(String eventId) {
		Query query = getEntityManager().createQuery("select rc from RfqSupplierBqItem as rc where rc.event.id =:id ");
		query.setParameter("id", eventId);
		List<RfqSupplierBqItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqSupplierBqItem item : list) {
				query = getEntityManager().createQuery("delete from RfqSupplierComment as rc where rc.supplierBqItem.id =:id");
				query.setParameter("id", item.getId());
				query.executeUpdate();
			}
		}
	}

}