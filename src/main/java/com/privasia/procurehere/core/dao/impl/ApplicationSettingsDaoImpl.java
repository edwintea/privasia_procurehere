package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ApplicationSettingsDao;
import com.privasia.procurehere.core.entity.ApplicationSettings;

/**
 * @author Ravi
 */
@Repository
public class ApplicationSettingsDaoImpl extends GenericDaoImpl<ApplicationSettings, String> implements ApplicationSettingsDao {

	@Override
	public ApplicationSettings loadById(String id) {
		final Query query = getEntityManager().createQuery("from ApplicationSettings a where a.id =:id");
		query.setParameter("id", id);
		return (ApplicationSettings) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ApplicationSettings> findAll() {
		final Query query = getEntityManager().createQuery("from ApplicationSettings ur order by ur.parameterName");
		return query.getResultList();
	}

}
