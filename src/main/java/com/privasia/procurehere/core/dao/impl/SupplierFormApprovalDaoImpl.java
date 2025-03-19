package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormApprovalDao;
import com.privasia.procurehere.core.entity.SupplierFormApproval;

@Repository("supplierFormApprovalDao")
public class SupplierFormApprovalDaoImpl extends GenericDaoImpl<SupplierFormApproval, String> implements SupplierFormApprovalDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormApproval> getAllApprovalListByFormId(String formId) {
		Query query = getEntityManager().createQuery("select sa from SupplierFormApproval sa where sa.supplierForm.id = :formId order by sa.level");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

}