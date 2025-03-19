package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEnvelopeOpenerUser;

/**
 * @author Ravi
 */

public interface RfpEnvelopeOpenerUserDao extends GenericDao<RfpEnvelopeOpenerUser, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfpEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId);

}
