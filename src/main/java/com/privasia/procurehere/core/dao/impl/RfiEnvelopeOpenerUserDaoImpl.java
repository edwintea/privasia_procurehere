package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEnvelopeOpenerUserDao;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RfiEnvelopeOpenerUserDaoImpl extends GenericDaoImpl<RfiEnvelopeOpenerUser, String>  implements RfiEnvelopeOpenerUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfiEnvelopeOpenerUser op inner join fetch op.envelope as en  where en.id =:id");
		query.setParameter("id", envelopId);
		List<RfiEnvelopeOpenerUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

}
