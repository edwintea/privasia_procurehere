package com.privasia.procurehere.service.impl;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ErpSetupService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class ErpSetupServiceImpl implements ErpSetupService {

	private static final Logger LOG = LogManager.getLogger(ErpSetupServiceImpl.class);

	@Autowired
	ErpSetupDao erpSetupDao;

	@Override
	@Transactional(readOnly = false)
	public ErpSetup save(ErpSetup erpSetup) {

		switch (erpSetup.getType()) {
		case RFA:
			erpSetup.setRfaTemplate(erpSetup.getRfxTemplate());
			break;
		case RFP:
			erpSetup.setRfpTemplate(erpSetup.getRfxTemplate());
			break;
		case RFQ:
			erpSetup.setRfqTemplate(erpSetup.getRfxTemplate());
			break;
		case RFT:
			erpSetup.setRftTemplate(erpSetup.getRfxTemplate());
			break;
		default:
			LOG.info(" Rfx type does not match :" + erpSetup.getType());
			break;
		}

		return erpSetupDao.saveOrUpdate(erpSetup);
	}

	@Override
	public ErpSetup findErpByWithTepmlateTenantId(String loggedInUserTenantId) {
		return erpSetupDao.findErpByWithTepmlateTenantId(loggedInUserTenantId);
	}

	@Override
	public ErpSetup getErpConfigBytenantId(String tenantId) {
		return erpSetupDao.getErpConfigBytenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String genrateSquanceNumber() {
		try {

			ErpSetup erpConfig = getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());

			Calendar c = Calendar.getInstance();
			int currentMonth = c.get(Calendar.MONTH);

			if (erpConfig != null) {

				int erpSeqMonth = erpConfig.getErpSeqMonth() != null ? erpConfig.getErpSeqMonth() : currentMonth;

				int erpSeqNo = erpConfig.getErpSeqNo() != null ? erpConfig.getErpSeqNo() : 0;

				erpSeqNo++;
				if (erpSeqMonth != currentMonth) {
					erpSeqNo = 1;
					erpConfig.setErpSeqMonth(currentMonth);
					erpConfig.setErpSeqNo(erpSeqNo);
				} else {
					erpConfig.setErpSeqMonth(erpSeqMonth);
					erpConfig.setErpSeqNo(erpSeqNo);
				}
				erpSetupDao.update(erpConfig);
				LOG.info("genrated sequnce number=========" + erpSeqNo);
				return StringUtils.lpad("" + erpSeqNo, 3, '0');
			}
		} catch (Exception e) {
			LOG.info("error erp sequance number :" + e.getMessage(), e);
		}
		return "000";
	}

}
