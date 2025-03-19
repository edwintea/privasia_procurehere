package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

public interface RfpSupplierTeamMemberDao extends GenericSupplierTeamMemberDao<RfpSupplierTeamMember, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventId(String eventId);

	List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

}