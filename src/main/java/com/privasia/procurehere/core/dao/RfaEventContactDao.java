package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventContact;

public interface RfaEventContactDao extends GenericDao<RfaEventContact, String> {

	List<RfaEventContact> findAllEventContactById(String id);

	boolean isExists(RfaEventContact rfaEventContact);

}
