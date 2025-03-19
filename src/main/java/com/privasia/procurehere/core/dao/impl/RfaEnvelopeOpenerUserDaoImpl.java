package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEnvelopeOpenerUserDao;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */

@Repository
public class RfaEnvelopeOpenerUserDaoImpl extends GenericDaoImpl<RfaEnvelopeOpenerUser, String>  implements RfaEnvelopeOpenerUserDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId) {
		final Query query = getEntityManager().createQuery("select op from RfaEnvelopeOpenerUser op inner join op.envelope en  where en.id =:id");
		query.setParameter("id", envelopId);
		List<RfaEnvelopeOpenerUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

}
