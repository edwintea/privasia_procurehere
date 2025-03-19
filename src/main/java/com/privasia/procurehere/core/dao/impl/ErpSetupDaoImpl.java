package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author parveen
 */
@Repository
public class ErpSetupDaoImpl extends GenericDaoImpl<ErpSetup, String> implements ErpSetupDao {

	@Override
	@SuppressWarnings("unchecked")
	public ErpSetup getErpConfigBytenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select ec from ErpSetup ec where ec.tenantId = :tenantId ");
		query.setParameter("tenantId", tenantId);
		List<ErpSetup> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ErpSetup getErpConfigWithTepmlateByAppId(String appId) {
		final Query query = getEntityManager().createQuery("select distinct ec from ErpSetup ec left outer join fetch ec.rfaTemplate at left outer join fetch ec.rftTemplate tt left outer join fetch ec.rfpTemplate pt left outer join fetch ec.rfqTemplate qt where ec.appId = :appId ");
		query.setParameter("appId", appId);
		List<ErpSetup> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ErpSetup getErpConfigByAppId(String appId) {
		final Query query = getEntityManager().createQuery("select ec from ErpSetup ec where ec.appId = :appId ");
		query.setParameter("appId", appId);
		List<ErpSetup> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ErpSetup findErpByWithTepmlateTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct ec from ErpSetup ec left outer join fetch ec.rfaTemplate at left outer join fetch ec.rftTemplate tt left outer join fetch ec.rfpTemplate pt left outer join fetch ec.rfqTemplate qt where ec.tenantId = :tenantId ");
		query.setParameter("tenantId", tenantId);
		List<ErpSetup> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MobileEventPojo> getEventTypeFromPrNo(String prNo, String tenantId) {
		final Query query = getEntityManager().createNativeQuery(findAggregateEventDetailByERPPr(), "erpEventResult");
		query.setParameter("prNo", prNo);
		query.setParameter("tenantId", tenantId);
		return (List<MobileEventPojo>) query.getResultList();
		// return list.toString();

		/*
		 * if (CollectionUtil.isNotEmpty(list)) { return list.get(0); } else { return null; }
		 */
	}

	/**
	 * @return
	 */
	protected String findAggregateEventDetailByERPPr() {
		String sql = "SELECT * from ( ";
		sql += " SELECT e.ID AS id, e.EVENT_ID AS eventId, 'RFT' AS eventType , e.STATUS AS status from PROC_RFT_EVENTS e where e.ERP_DOC_NO =:prNo and e.TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT e.ID AS id, e.EVENT_ID AS eventId, 'RFP' AS eventType , e.STATUS AS status from PROC_RFP_EVENTS e where e.ERP_DOC_NO =:prNo and e.TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT e.ID AS id, e.EVENT_ID AS eventId, 'RFQ' AS eventType , e.STATUS AS status from PROC_RFQ_EVENTS e where e.ERP_DOC_NO =:prNo and e.TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT e.ID AS id, e.EVENT_ID AS eventId, 'RFA' AS eventType , e.STATUS AS status from PROC_RFA_EVENTS e where e.ERP_DOC_NO =:prNo and e.TENANT_ID = :tenantId ";
		sql += ") e ";
		return sql;
	}

}
