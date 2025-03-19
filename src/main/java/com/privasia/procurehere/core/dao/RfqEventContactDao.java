package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventContact;

/**
 * @author Ravi
 */

public interface RfqEventContactDao extends GenericDao<RfqEventContact, String> {

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventContact> findAllEventContactById(String id);

	boolean isExists(RfqEventContact rfqEventContact);

}
