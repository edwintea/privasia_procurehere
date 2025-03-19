package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierTeamMemberDao;
import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.User;

@Repository
public class RftSupplierTeamMemberDaoImpl extends GenericSupplierTeamMemberDaoImpl<RftSupplierTeamMember, String> implements RftSupplierTeamMemberDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<RftSupplierTeamMember> getSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RftSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RftSupplierTeamMember>) query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserSupplierTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RftSupplierTeamMember st where st.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getUserSupplierTeamMemberByEventIdAndSupplierId(String eventId, String supplierId) {
		Query query = getEntityManager().createQuery("select distinct new User(st.user.id, st.user.name, st.user.communicationEmail, st.user.emailNotifications, st.user.tenantId) from RftSupplierTeamMember st left outer join st.eventSupplier es where st.event.id = :eventId and es.supplier.id=:supplierId").setParameter("eventId", eventId).setParameter("supplierId", supplierId);
		return (List<User>) query.getResultList();
	}

}
