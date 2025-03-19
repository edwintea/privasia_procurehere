package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventContact;

/**
 * @author Ravi
 */
public interface RfiEventContactDao extends GenericDao<RfiEventContact, String> {

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventContact> findAllEventContactById(String id);

	boolean isExists(RfiEventContact rfiEventContact);

}
