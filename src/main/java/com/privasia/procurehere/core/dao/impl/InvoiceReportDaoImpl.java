package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.InvoiceReportDao;
import com.privasia.procurehere.core.entity.InvoiceReport;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author sana
 */
@Repository
public class InvoiceReportDaoImpl extends GenericDaoImpl<InvoiceReport, String> implements InvoiceReportDao {

	private static final Logger LOG = LogManager.getLogger(Global.INV_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public InvoiceReport findReportByInvoiceId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from InvoiceReport i join fetch i.invoice as ic where ic.id = :id");
			query.setParameter("id", id);
			List<InvoiceReport> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {

				return null;
			}
		} catch (Exception e) {
			LOG.error("Error while getting invoice report data : " + e.getMessage(), e);
			return null;
		}
	}

}
