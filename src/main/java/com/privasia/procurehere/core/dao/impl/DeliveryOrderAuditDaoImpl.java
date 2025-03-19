package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.DeliveryOrderAuditDao;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.core.enums.DoAuditVisibilityType;

/**
 * @author ravi
 */

@Repository
public class DeliveryOrderAuditDaoImpl extends GenericDaoImpl<DeliveryOrderAudit, String> implements DeliveryOrderAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrderAudit> getDoAuditByDoIdForSupplier(String doId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.DeliveryOrderAudit(da.id, u.name, da.actionDate, da.action, da.description) from DeliveryOrderAudit da left outer join da.actionBy u where da.deliveryOrder.id = :doId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("doId", doId);
		query.setParameter("visibilityType", Arrays.asList(DoAuditVisibilityType.SUPPLIER, DoAuditVisibilityType.BOTH));
		List<DeliveryOrderAudit> list = query.getResultList();
		return list;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeliveryOrderAudit> getDoAuditByDoIdForBuyer(String doId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.DeliveryOrderAudit(da.id, u.name, da.actionDate, da.action, da.description) from DeliveryOrderAudit da left outer join da.actionBy u where da.deliveryOrder.id = :doId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("doId", doId);
		query.setParameter("visibilityType", Arrays.asList(DoAuditVisibilityType.BUYER, DoAuditVisibilityType.BOTH));
		List<DeliveryOrderAudit> list = query.getResultList();
		return list;
	}

}
