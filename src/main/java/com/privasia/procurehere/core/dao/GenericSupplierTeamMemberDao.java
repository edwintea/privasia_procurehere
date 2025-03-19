package com.privasia.procurehere.core.dao;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.EventTeamMember;

/**
 * @author Priyanka Singh
 */
public interface GenericSupplierTeamMemberDao<T extends EventTeamMember, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param supplierId
	 * @param userId
	 */
	void deleteSupplierTeamMemberBySupplierIdForEvent(String supplierId, String userId);
}
