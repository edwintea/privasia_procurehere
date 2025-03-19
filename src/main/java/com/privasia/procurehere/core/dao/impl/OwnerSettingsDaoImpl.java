package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository("ownerSettingsDao")
public class OwnerSettingsDaoImpl extends GenericDaoImpl<OwnerSettings, String> implements OwnerSettingsDao {

	@SuppressWarnings("unchecked")
	@Override
	public OwnerSettings getOwnersettingsByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from OwnerSettings os left outer join fetch os.fileTypes left outer join fetch os.timeZone as tz inner join fetch os.owner o left outer join fetch os.modifiedBy as mb where o.id = :id");
		query.setParameter("id", tenantId);
		List<OwnerSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OwnerSettings getOwnersettings() {
		final Query query = getEntityManager().createQuery("from OwnerSettings os left outer join fetch os.fileTypes");
		List<OwnerSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OwnerSettings getPlainOwnersettings() {
		final Query query = getEntityManager().createQuery("select distinct NEW OwnerSettings(os.id, os.supplierSignupNotificationEmailAccount, os.fileSizeLimit, os.supplierChargeStartDate) from OwnerSettings os");
		List<OwnerSettings> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getSupplierBuyerExpiryReminder() {
		final Query query = getEntityManager().createQuery("select os.supplierSubsExpiryReminder, os.buyerSubsExpiryReminder from OwnerSettings os");
		List<Object[]> list = query.getResultList();
		List<Integer> result = null;
		if (CollectionUtil.isNotEmpty(list)) {
			result = new ArrayList<>();
			for (Object[] values : list) {
				result.add((Integer) values[0]);
				result.add((Integer) values[1]);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBuyerSubsMailFromOwnersettings() {
		final Query query = getEntityManager().createQuery("select os.buyerSubscriptionNotificationEmail from OwnerSettings os");
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

}
