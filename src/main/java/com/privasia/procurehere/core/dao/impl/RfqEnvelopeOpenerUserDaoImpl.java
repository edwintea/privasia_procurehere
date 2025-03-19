package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEnvelopeOpenerUserDao;
import com.privasia.procurehere.core.entity.RfqEnvelopeOpenerUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RfqEnvelopeOpenerUserDaoImpl extends GenericDaoImpl<RfqEnvelopeOpenerUser, String>  implements RfqEnvelopeOpenerUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfqEnvelopeOpenerUser op inner join fetch op.envelope as en  where en.id =:id");
		query.setParameter("id", envelopId);
		List<RfqEnvelopeOpenerUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

}
