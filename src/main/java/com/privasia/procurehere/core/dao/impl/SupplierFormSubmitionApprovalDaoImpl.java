package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormSubmitionApprovalDao;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author ravi
 */
@Repository
public class SupplierFormSubmitionApprovalDaoImpl extends GenericDaoImpl<SupplierFormSubmitionApproval, String> implements SupplierFormSubmitionApprovalDao {

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormSubmitionApproval findSupplierFormSubmitionApproval(String id) {
		StringBuilder hql = new StringBuilder("from SupplierFormSubmitionApproval sub where sub.id = :id");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		List<SupplierFormSubmitionApproval> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}