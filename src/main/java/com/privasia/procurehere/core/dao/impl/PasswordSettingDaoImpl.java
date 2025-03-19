/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PasswordSettingDao;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Yogesh
 */
@Repository
public class PasswordSettingDaoImpl extends GenericDaoImpl<PasswordSettings, String> implements PasswordSettingDao {

	@SuppressWarnings("unchecked")
	@Override
	public PasswordSettings getPasswordSettingsByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from PasswordSettings a where a.tenantId =:id");
		query.setParameter("id", tenantId);
		List<PasswordSettings> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
