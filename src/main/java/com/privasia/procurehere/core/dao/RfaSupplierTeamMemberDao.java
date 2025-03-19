package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

public interface RfaSupplierTeamMemberDao extends GenericSupplierTeamMemberDao<RfaSupplierTeamMember, String> {
	/**
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserSupplierTeamMemberByEventId(String eventId);

	List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String id);

}