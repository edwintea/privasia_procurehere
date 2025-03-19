/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PasswordHistoryDao;
import com.privasia.procurehere.core.entity.PasswordHistory;

/**
 * @author Nitin Otageri
 */
@Repository
public class PasswordHistoryDaoImpl extends GenericDaoImpl<PasswordHistory, String> implements PasswordHistoryDao {

	private static final Logger LOG = LogManager.getLogger(PasswordHistoryDaoImpl.class);

	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<PasswordHistory> findPasswordHistoryByUserId(String userId) {
		LOG.debug("Fetching user's password history : " + userId);
		final Query query = getEntityManager().createQuery("from PasswordHistory ph where ph.user.id = :userId order by ph.passwordChangedDate desc");
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PasswordHistory> getPasswordHistory(Integer numberOfPasswordRemember, String userId) {
		final Query query = getEntityManager().createQuery("from PasswordHistory ph where ph.user.id = :userId order by ph.passwordChangedDate desc");
		query.setParameter("userId", userId);
		if (numberOfPasswordRemember != null) {
			query.setMaxResults(numberOfPasswordRemember);
		}
		return query.getResultList();
	}

}
