package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

public interface RftSupplierTeamMemberDao extends GenericSupplierTeamMemberDao<RftSupplierTeamMember, String> {
	/**
	 * @param eventId
	 * @return
	 */
	List<RftSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String supplierId);

}