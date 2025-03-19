package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SettingsDao;
import com.privasia.procurehere.core.entity.Settings;

@Repository
public class SettingsDaoImpl extends GenericDaoImpl<Settings, String> implements SettingsDao {

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<Settings> findAll() {
		StringBuilder hsql = new StringBuilder("from Settings as n inner join fetch n.modifiedBy as s");
		final Query query = getEntityManager().createQuery(hsql.toString());
		return query.getResultList();
	}

}
