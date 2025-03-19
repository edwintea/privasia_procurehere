package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoReportDao;
import com.privasia.procurehere.core.entity.PoReport;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author sana
 */
@Repository
public class PoReportDaoImpl extends GenericDaoImpl<PoReport, String> implements PoReportDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public PoReport findReportByPoId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from PoReport p join fetch p.po as po where po.id = :id");
			query.setParameter("id", id);
			List<PoReport> uList = query.getResultList();
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

	@Override
	public void deletePoReportByPoId(String poId) {
		try {
			StringBuilder hsql = new StringBuilder("delete from PoReport p where p.po.id = :poId");
			Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("poId", poId);
			query.executeUpdate();
		} catch(Exception e) {
			LOG.info("Error while deleting Po report ..: "+e.getMessage(), e);
		}
	}
}
