package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.EventSettingsDao;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author nitin
 */
@Repository("eventSettingsDao")
public class EventSettingsDaoImpl extends GenericDaoImpl<EventSettings, String> implements EventSettingsDao {

	@SuppressWarnings("unchecked")
	@Override
	public EventSettings findByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from EventSettings s where s.buyer.id = :tenantId");
		query.setParameter("tenantId", tenantId);
		List<EventSettings> list = query.getResultList();

		if(CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

}
