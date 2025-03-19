package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEnvelopeOpenerUser;

/**
 * @author Ravi
 */

public interface RfqEnvelopeOpenerUserDao extends GenericDao<RfqEnvelopeOpenerUser, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfqEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId);

}
