package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.FinanceSettingsDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

@Repository("financeSettingsDao")
public class FinanceSettingsDaoImpl extends GenericDaoImpl<FinanceCompanySettings, String> implements FinanceSettingsDao {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Autowired
	TimeZoneDao timeZoneDao;

	@Autowired
	CurrencyDao currencyDao;

	@SuppressWarnings("unchecked")
	@Override
	public FinanceCompanySettings getFinanceSettingsByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from FinanceCompanySettings b left outer join fetch b.timeZone as tz  left outer join fetch b.createdBy as cb left outer join fetch b.modifiedBy as mb where b.tenantId =:id");
		query.setParameter("id", tenantId);
		List<FinanceCompanySettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	/**
	 * @param buyer
	 * @param createdDate
	 * @param createdBy
	 */
	@Override
	@Transactional(readOnly = false)
	public void createDefaultFinanceSettings(FinanceCompany financeCompany, Date createdDate, User createdBy) {
		try {
			// TODO: Implement logic to set currency and timezone based on country of registration of finance
			FinanceCompanySettings settings = new FinanceCompanySettings();
			settings.setCreatedBy(createdBy);
			settings.setCreatedDate(createdDate);
			settings.setPoSequenceLength(6);
			settings.setPoSequenceNumber("0");
			settings.setPoSequencePrefix("-");
			settings.setTenantId(financeCompany.getId());
			settings.setTimeZone(timeZoneDao.findByProperties(new String[] { "timeZoneDescription", "status" }, new Object[] { "Asia/Kuala_Lumpur", Status.ACTIVE }));
			save(settings);
		} catch (Exception e) {
			LOG.error("Error creating default buyer settings : " + e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public String getFinanceTimeZoneByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("select tz.timeZone from FinanceCompanySettings bs left outer join bs.timeZone as tz  where bs.tenantId = :id");
		query.setParameter("id", tenantId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

}
