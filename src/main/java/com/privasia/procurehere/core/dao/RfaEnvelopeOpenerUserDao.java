package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;

/**
 * @author Ravi
 */

public interface RfaEnvelopeOpenerUserDao extends GenericDao<RfaEnvelopeOpenerUser, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfaEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId);

}
