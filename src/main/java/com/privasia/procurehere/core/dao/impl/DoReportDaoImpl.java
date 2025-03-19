package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.DoReportDao;
import com.privasia.procurehere.core.entity.DoReport;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author sana
 */
@Repository
public class DoReportDaoImpl extends GenericDaoImpl<DoReport, String> implements DoReportDao {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public DoReport findReportByDoId(String id, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from DoReport d join fetch d.deliveryOrder as do where do.id = :id and d.tenantId = :tenantId");
			query.setParameter("id", id);
			query.setParameter("tenantId", tenantId);
			List<DoReport> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {

				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting report data : " + e.getMessage(), e);
			return null;
		}
	}

}
