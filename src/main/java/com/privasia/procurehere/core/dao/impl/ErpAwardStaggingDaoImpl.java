package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ErpAwardStaggingDao;
import com.privasia.procurehere.core.entity.ErpAwardStaging;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class ErpAwardStaggingDaoImpl extends GenericDaoImpl<ErpAwardStaging, String> implements ErpAwardStaggingDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ErpAwardStaging> getStaggingData(String tenantId, String refranceNo) {
		StringBuilder hql = new StringBuilder("from ErpAwardStaging where tenantId=:tenantId ");
		if (StringUtils.checkString(refranceNo).length() > 0) {
			hql.append(" and docNo =:refranceNo order by actionDate desc");
		} else {
			hql.append("and sentFlag=:sentFlag");
		}
		Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(refranceNo).length() > 0) {
			query.setParameter("refranceNo", refranceNo);
			query.setMaxResults(1);
		} else {
			query.setParameter("sentFlag", false);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@Override
	public void updateOldAwardStagingFlag(String docNo, String tenantId, String id) {

		Query query = getEntityManager().createQuery("update ErpAwardStaging set sentFlag =:sentFlag where docNo=:docNo and tenantId=:tenantId and id <>:id");
		query.setParameter("docNo", docNo);
		query.setParameter("tenantId", tenantId);
		query.setParameter("id", id);
		query.setParameter("sentFlag", true);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public ErpAwardStaging findAwardStaggingByEventID(String eventId, String tenantId) {
		StringBuilder hql = new StringBuilder("from ErpAwardStaging where tenantId=:tenantId and docNo =:refranceNo");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("refranceNo", eventId);
		query.setParameter("tenantId", tenantId);
		List<ErpAwardStaging> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

}
