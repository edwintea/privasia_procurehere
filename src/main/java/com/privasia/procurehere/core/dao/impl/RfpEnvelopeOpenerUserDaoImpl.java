package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEnvelopeOpenerUserDao;
import com.privasia.procurehere.core.entity.RfpEnvelopeOpenerUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RfpEnvelopeOpenerUserDaoImpl extends GenericDaoImpl<RfpEnvelopeOpenerUser, String>  implements RfpEnvelopeOpenerUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("from RfpEnvelopeOpenerUser op inner join fetch op.envelope as en  where en.id =:id");
		query.setParameter("id", envelopId);
		List<RfpEnvelopeOpenerUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

}
