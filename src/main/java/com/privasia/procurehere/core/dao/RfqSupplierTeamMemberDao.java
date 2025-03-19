package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

public interface RfqSupplierTeamMemberDao extends GenericSupplierTeamMemberDao<RfqSupplierTeamMember, String> {
	/**
	 * @param eventId
	 * @return
	 */
	List<RfqSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventId(String eventId);

	List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

}