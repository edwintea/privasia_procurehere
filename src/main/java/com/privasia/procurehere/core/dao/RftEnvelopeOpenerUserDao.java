package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;

/**
 * @author Ravi
 */

public interface RftEnvelopeOpenerUserDao extends GenericDao<RftEnvelopeOpenerUser, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RftEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId);

}
