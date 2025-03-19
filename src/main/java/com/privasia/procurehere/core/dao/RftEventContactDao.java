package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventContact;

public interface RftEventContactDao extends GenericDao<RftEventContact, String> {

	List<RftEventContact> findAllEventContactById(String id);

	/**
	 * @param rftEventContact
	 * @return
	 */
	boolean isExists(RftEventContact rftEventContact);

}
