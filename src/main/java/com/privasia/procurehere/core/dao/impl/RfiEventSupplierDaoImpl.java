package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfiEventSupplierDaoImpl extends GenericEventSupplierDaoImpl<RfiEventSupplier, String> implements RfiEventSupplierDao {

	@Autowired
	UserDao userDao;

	@Autowired
	SupplierDao supplierDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfiEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus  order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfiEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEventSupplier eventSupplier : list) {
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
		final Query query = getEntityManager().createQuery("select e from RfiEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus  and e.disqualify = false order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfiEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEventSupplier eventSupplier : list) {
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
	public RfiEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfiEvent> uList = query.getResultList();
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
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications,  u.tenantId, u.deleted)from RfiSupplierTeamMember tm join tm.user u join tm.eventSupplier es join es.supplier s where tm.event.id =:eventId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiSupplierTeamMember> getRfiSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("from RfiSupplierTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId and tm.eventSupplier.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfiSupplierTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		// Supplier supplier = new Supplier();
		// supplier.setId(supplierId);
		Supplier supplier = supplierDao.findById(supplierId);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForSummary(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfiEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify =:isQualify order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("isQualify", Boolean.FALSE);
		List<RfiEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				returnList.add(supplier);
			}
		}
		return returnList;
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		final Query query = getEntityManager().createQuery("select count(supplier) from RfiEventSupplier e where e.rfxEvent.id =:eventId  ");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(s) from RfiEvent e inner join e.suppliers s  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers) {
		// TODO Auto-generated method stub

		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfiEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and ss in (:selectedSuppliers) order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("selectedSuppliers", selectedSuppliers);
		List<RfiEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEventSupplier eventSupplier : list) {
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
	public List<RfaSupplierBqPojo> findRfiSupplierParticipation(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.numberOfBids,sup.companyName,sup.id,es.disqualify,es.ipAddress,es.submissionStatus,es.rejectedTime,es.supplierEventReadTime,es.supplierSubmittedTime) from  RfiEventSupplier es inner join es.rfxEvent e inner join es.supplier sup where es.submissionStatus not in (:status) and e.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		List<SubmissionStatusType> list = new ArrayList<SubmissionStatusType>();
		list.add(SubmissionStatusType.INVITED);
		query.setParameter("status", list);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.FeePojo(a.feePaid, a.depositPaid,a.feePaidDate,a.depositPaidDate,a.feeReference,a.depositReference,sup.companyName,sup.fullName,sup.communicationEmail,sup.companyContactNumber,sup.id,a.selfInvited) from RfiEventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId order by sup.companyName ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfiEventSupplier as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and s.id =:supplierId order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct res from RfiEventSupplier as res,FavouriteSupplier fs inner join res.supplier as s left outer join fs.supplier as fss inner join res.rfxEvent as re where re.id = :id and s.id =fss.id and fs.vendorCode =:supplierCode and fs.buyer.id=:tenantId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierCode", supplierCode);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id, s.id, s.companyName, s.companyContactNumber, s.communicationEmail) from RfiEventSupplier as res inner join res.supplier as s  inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setMaxResults(input.getLength());
		query.setFirstResult(input.getStart());
		return query.getResultList();
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct count(res) from RfiEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public EventDocument findeventDocumentById(String docId) {
		final Query query = getEntityManager().createQuery("from RfiEventDocument r where r.id =:id");
		query.setParameter("id", docId);
		return (EventDocument) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		String hql = "select distinct sup from RfiEventSupplier sup left outer join fetch sup.supplier left outer join fetch sup.acceptedBy  where sup.submissionStatus=:submissionStatus and sup.disqualify=:disqualify and sup.rfxEvent.id=:eventId";
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
	public List<RfiEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null) or  (res.submissionStatus = :status  and res.isRejectedAfterStart = :isRejectedAfterStart)) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("isRejectedAfterStart", Boolean.TRUE);
		query.setParameter("status", SubmissionStatusType.REJECTED);
		return query.getResultList();
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select e.eventName from RfiEvent e where e.id =:eventId");
		query.setParameter("eventId", eventId);
		return (String) query.getSingleResult();
	}

	@Override
	public RfiEventSupplier getSupplierByStripePaymentId(String paymentId) {
		Query query = getEntityManager().createQuery("select e from RfiEventSupplier e where e.feeReference =:paymentId");
		query.setParameter("paymentId", paymentId);
		try {
			return (RfiEventSupplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null)) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		return query.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		String hsql = new String("select distinct new com.privasia.procurehere.core.entity.RfiEventSupplier(res.id, res.submissionStatus) from RfiEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where res.rfxEvent.id =:id ");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		return query.getResultList();
	}
}