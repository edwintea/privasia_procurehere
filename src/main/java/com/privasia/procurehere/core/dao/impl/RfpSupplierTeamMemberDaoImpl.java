package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpSupplierTeamMemberDao;
import com.privasia.procurehere.core.entity.RfpSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

@Repository
public class RfpSupplierTeamMemberDaoImpl extends GenericSupplierTeamMemberDaoImpl<RfpSupplierTeamMember, String> implements RfpSupplierTeamMemberDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfpSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfpSupplierTeamMember>) query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfpSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String supplierID) {
		Query query = getEntityManager().createQuery("select distinct new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfpSupplierTeamMember st left outer join st.eventSupplier es where st.event.id = :eventId and es.supplier.id=:supplierID").setParameter("eventId", eventId).setParameter("supplierID", supplierID);
		return (List<User>) query.getResultList();
	}
}
