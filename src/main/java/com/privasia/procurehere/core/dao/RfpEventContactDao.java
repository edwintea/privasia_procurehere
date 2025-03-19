package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventContact;

/**
 * @author Ravi
 */

public interface RfpEventContactDao extends GenericDao<RfpEventContact, String> {

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventContact> findAllEventContactById(String id);

	/**
	 * @param rfpEventContact
	 * @return
	 */
	boolean isExists(RfpEventContact rfpEventContact);

}
