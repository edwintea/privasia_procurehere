package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEnvelopeOpenerUserDao;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RftEnvelopeOpenerUserDaoImpl extends GenericDaoImpl<RftEnvelopeOpenerUser, String>  implements RftEnvelopeOpenerUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("from RftEnvelopeOpenerUser op inner join fetch op.envelope as en  where en.id =:id");
		query.setParameter("id", envelopId);
		List<RftEnvelopeOpenerUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

}
