package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfqEventSupplierDaoImpl extends GenericEventSupplierDaoImpl<RfqEventSupplier, String> implements RfqEventSupplierDao {

	@Autowired
	UserDao userDao;

	@Autowired
	SupplierDao supplierDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfqEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus   order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfqEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfqEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus  and e.disqualify = false order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfqEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RfqSupplierTeamMember tm join tm.user u join tm.eventSupplier es join es.supplier s where tm.event.id =:eventId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfqEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfqEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfqEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqSupplierTeamMember> getRfqSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("from RfqSupplierTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId and tm.eventSupplier.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfqSupplierTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		// Supplier supplier = new Supplier();
		Supplier supplier = supplierDao.findById(supplierId);
		// supplier.setId(supplierId);
		User adminUser = userDao.getAdminUserForSupplier(supplier);

		// Admin User
		if (adminUser != null && adminUser.getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getSupplierTeamMembersForEvent(eventId, supplierId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						break;
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		final Query query = getEntityManager().createQuery("select count(supplier) from RfqEventSupplier e where e.rfxEvent.id =:eventId  ");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(s) from RfqEvent e inner join e.suppliers s  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfqEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus  and ss in (:selectedSuppliers) order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("selectedSuppliers", selectedSuppliers);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfqEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqEventSupplier> getAllRfqEventSuppliersByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select res from RfqEventSupplier as res inner join fetch res.supplier as s join res.rfxEvent as re where re.id = :id and res.submitted = :submitted");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Supplier> getEventSuppliersForSummary(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfqEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify =:isQualify order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("isQualify", Boolean.FALSE);
		List<RfqEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (!returnList.contains(supplier)) {
					returnList.add(supplier);
				}
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllRfaEventSuppliersIdByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select res.supplier.id  from RfqEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id and res.submitted = :submitted and res.disqualify <> true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.FeePojo(a.feePaid, a.depositPaid,a.feePaidDate,a.depositPaidDate,a.feeReference,a.depositReference,sup.companyName,sup.fullName,sup.communicationEmail,sup.companyContactNumber,sup.id,a.selfInvited) from RfqEventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId order by sup.companyName ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfqEventSupplier as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and s.id =:supplierId order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct res from RfqEventSupplier as res,FavouriteSupplier fs inner join res.supplier as s left outer join fs.supplier as fss inner join res.rfxEvent as re where re.id = :id and s.id =fss.id and fs.vendorCode =:supplierCode and fs.buyer.id=:tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierCode", supplierCode);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id, s.id, s.companyName, s.companyContactNumber, s.communicationEmail) from RfqEventSupplier as res inner join res.supplier as s  inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setMaxResults(input.getLength());
		query.setFirstResult(input.getStart());
		return query.getResultList();
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct count(res) from RfqEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		String hql = "select distinct sup from RfqEventSupplier sup left outer join fetch sup.supplier left outer join fetch sup.acceptedBy  where sup.submissionStatus=:submissionStatus and sup.disqualify=:disqualify and sup.rfxEvent.id=:eventId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", false);
		query.setParameter("eventId", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId, TableDataInput input, RfxTypes eventType) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.FeePojo(a.feePaid, a.depositPaid,a.feePaidDate,a.depositPaidDate,a.feeReference,a.depositReference,sup.companyName,sup.fullName,sup.communicationEmail,sup.companyContactNumber,sup.id,a.selfInvited) from " + StringUtils.capitalize(eventType.name().toLowerCase()) + "EventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId order by sup.companyName ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setMaxResults(input.getLength());
		query.setFirstResult(input.getStart());
		return query.getResultList();
	}

	@Override
	public long getAllInvitedSuppliersFilterCountByEventId(String eventId, TableDataInput input, RfxTypes eventType) {
		StringBuilder hsql = new StringBuilder("select count(a.id) from " + StringUtils.capitalize(eventType.name().toLowerCase()) + "EventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getAllInvitedSuppliersCountByEventId(String eventId, RfxTypes eventType) {
		StringBuilder hsql = new StringBuilder("select count(a.id) from " + StringUtils.capitalize(eventType.name().toLowerCase()) + "EventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplier> getAllSubmitedSupplierByEevntId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id  and res.submissionStatus=:submissionStatus order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ArrayList<EventSupplier>();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null) or  (res.submissionStatus = :status and res.isRejectedAfterStart = :isRejectedAfterStart)) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("isRejectedAfterStart", Boolean.TRUE);
		query.setParameter("status", SubmissionStatusType.REJECTED);
		return query.getResultList();
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select e.eventName from RfqEvent e where e.id =:eventId");
		query.setParameter("eventId", eventId);
		return (String) query.getSingleResult();

	}

	@Override
	public RfqEventSupplier getSupplierByStripePaymentId(String paymentId) {
		Query query = getEntityManager().createQuery("select e from RfqEventSupplier e where e.feeReference =:paymentId");
		query.setParameter("paymentId", paymentId);
		try {
			return (RfqEventSupplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null)) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfqEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		String hsql = new String("select distinct new com.privasia.procurehere.core.entity.RfqEventSupplier(res.id, res.submissionStatus) from RfqEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where res.rfxEvent.id =:id ");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		return query.getResultList();
	}

}
