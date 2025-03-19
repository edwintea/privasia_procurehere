package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.GrnReportDao;
import com.privasia.procurehere.core.entity.GrnReport;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
@Repository
public class GrnReportDaoImpl extends GenericDaoImpl<GrnReport, String> implements GrnReportDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public GrnReport findReportByGrnId(String id, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from GrnReport g join fetch g.goodsReceiptNote as grn where grn.id = :id and g.tenantId = :tenantId");
			query.setParameter("id", id);
			query.setParameter("tenantId", tenantId);
			List<GrnReport> uList = query.getResultList();
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
