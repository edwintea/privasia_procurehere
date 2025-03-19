package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiSupplierTeamMemberDao;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

@Repository
public class RfiSupplierTeamMemberDaoImpl extends GenericSupplierTeamMemberDaoImpl<RfiSupplierTeamMember, String> implements RfiSupplierTeamMemberDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfiSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfiSupplierTeamMember>) query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfiSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String loggedInUserTenantId) {
		Query query = getEntityManager().createQuery("select distinct new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfiSupplierTeamMember st left outer join st.eventSupplier es where st.event.id = :eventId and es.supplier.id=:supplierID").setParameter("eventId", eventId).setParameter("supplierID", loggedInUserTenantId);
		return (List<User>) query.getResultList();
	}
}
