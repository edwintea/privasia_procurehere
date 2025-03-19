package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

public interface RfiSupplierTeamMemberDao extends GenericSupplierTeamMemberDao<RfiSupplierTeamMember, String> {
	/**
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventId(String eventId);

	List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

}