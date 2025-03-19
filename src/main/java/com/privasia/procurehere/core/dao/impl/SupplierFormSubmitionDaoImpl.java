/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormSubmitionDao;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author ravi
 */
@Repository
public class SupplierFormSubmitionDaoImpl extends GenericDaoImpl<SupplierFormSubmition, String> implements SupplierFormSubmitionDao {

	@Override
	public SupplierFormSubmition findSupplierFormBySupplierId(String supplierId) {
		final Query query = getEntityManager().createQuery("select sf from SupplierFormSubmition sf inner join sf.supplier s where s.id= :supplierId and sf.status =:status and sf.isOnboardingForm = :isOnboardingForm  ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", SupplierFormSubmitionStatus.PENDING);
		query.setParameter("isOnboardingForm", Boolean.TRUE);
		List<SupplierFormSubmition> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}
}