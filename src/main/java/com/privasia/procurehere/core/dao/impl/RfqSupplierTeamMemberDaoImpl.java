package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqSupplierTeamMemberDao;
import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

@Repository
public class RfqSupplierTeamMemberDaoImpl extends GenericSupplierTeamMemberDaoImpl<RfqSupplierTeamMember, String> implements RfqSupplierTeamMemberDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfqSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfqSupplierTeamMember>) query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfqSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String supplierId) {
		Query query = getEntityManager().createQuery("select distinct new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RfqSupplierTeamMember st left outer join st.eventSupplier es where st.event.id = :eventId and es.supplier.id=:supplierId").setParameter("eventId", eventId).setParameter("supplierId", supplierId);
		return (List<User>) query.getResultList();
	}

}
