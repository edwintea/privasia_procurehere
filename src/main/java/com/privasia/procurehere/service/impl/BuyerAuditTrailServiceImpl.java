package com.privasia.procurehere.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.pojo.AuditTrailPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerAuditTrailService;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class BuyerAuditTrailServiceImpl implements BuyerAuditTrailService {

	final Logger LOG = LogManager.getLogger(BuyerAuditTrailServiceImpl.class);

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<BuyerAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate, String moduleType) {
		List<BuyerAuditTrail> list = buyerAuditTrailDao.findAuditTrailForTenant(tenantId, input, startDate, endDate, moduleType);
		for (BuyerAuditTrail buyerAuditTrail : list) {
			if (buyerAuditTrail.getModuleType() != null) {
				buyerAuditTrail.setModuleTypeName(buyerAuditTrail.getModuleType().getValue());
			}
		}
		return list;
	}

	@Override
	public long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, String moduleType) {
		return buyerAuditTrailDao.findTotalFilteredAuditTrailForTenant(loggedInUserTenantId, input, startDate, endDate, moduleType);
	}

	@Override
	public long findTotalAuditTrailForTenant(String loggedInUserTenantId) {
		return buyerAuditTrailDao.findTotalAuditTrailForTenant(loggedInUserTenantId);
	}

	@Override
	public JasperPrint getAuditTrailPdf(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, HttpSession session, String moduleType) {
		JasperPrint jasperPrint = null;
		List<AuditTrailPojo> auditTrailSummary = new ArrayList<AuditTrailPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/AuditTrailReport.jasper");
			File jasperfile = resource.getFile();

			List<BuyerAuditTrail> auditTrail = findAuditTrailForTenant(loggedInUserTenantId, input, startDate, endDate, moduleType);
			if (CollectionUtil.isNotEmpty(auditTrail)) {
				for (BuyerAuditTrail item : auditTrail) {
					AuditTrailPojo audit = new AuditTrailPojo();
					audit.setAction(item.getActivity() != null ? item.getActivity().name() : "");
					audit.setActionBy(item.getActionBy() != null ? item.getActionBy().getLoginId() : "");
					audit.setActionDate(item.getActionDate() != null ? sdf.format(item.getActionDate()) : "");
					audit.setDescription(item.getDescription());
					audit.setCurrentDate(sdf.format(new java.util.Date()));
					if (startDate != null && endDate != null) {
						audit.setTitle("Buyer Audit Report from " + sdf.format(startDate) + " to " + sdf.format(endDate));
					}
					auditTrailSummary.add(audit);
				}
			}
			parameters.put("AUDIT_TRAIL", auditTrailSummary);
			// LOG.info(" SUMMARY DETAILS :: " + auditTrailSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auditTrailSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Audit Trail PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	@Transactional(readOnly = false)
	public void save(BuyerAuditTrail ownerAuditTrail) {
		buyerAuditTrailDao.save(ownerAuditTrail);
	}

}
