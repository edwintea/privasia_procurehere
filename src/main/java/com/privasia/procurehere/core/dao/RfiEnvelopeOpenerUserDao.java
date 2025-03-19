package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;

/**
 * @author Ravi
 */

public interface RfiEnvelopeOpenerUserDao extends GenericDao<RfiEnvelopeOpenerUser, String> {

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiEnvelopeOpenerUser> findEnvelopeOpenerByEnvelopId(String envelopId);

}
