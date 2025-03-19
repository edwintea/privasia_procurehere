package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfiApprovalUser;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfiEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventApproval;
import com.privasia.procurehere.core.entity.RfiEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfiSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfiUnMaskedUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RfiEventDaoImpl extends GenericEventDaoImpl<RfiEvent, String> implements RfiEventDao {

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfiEvent rfiEvent) {
		StringBuilder hsql = new StringBuilder("from RfiEvent re where re.referanceNumber= :referanceNumber and re.id <> :id and re.tenantId= :tenantId ");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("referanceNumber", rfiEvent.getReferanceNumber());
		query.setParameter("tenantId", rfiEvent.getTenantId());
		query.setParameter("id", rfiEvent.getId());

		List<RftEvent> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select ss from RfiEvent e left outer join e.suppliers s inner join s.supplier ss where e.id =:eventId order by ss.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllRfiEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) {
		StringBuffer sb = new StringBuffer("SELECT e.ID as id,e.EVENT_NAME as eventName,null as auctionType , e.STATUS as eventStatus,e.EVENT_DESCRIPTION as descripiton, CASE WHEN template.TEMPLATE_STATUS='INACTIVE' THEN  1  ELSE 0 END  as templateStatus, e.REFERANCE_NUMBER as referenceNumber,e.EVENT_ID as eventId,e.EVENT_START as startDate,e.EVENT_END as endDate, (SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFI_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories FROM PROC_RFI_EVENTS e ");
		sb.append(" LEFT OUTER JOIN PROC_USER  eventOwner on e.EVENT_OWNER = eventOwner.ID  LEFT OUTER JOIN PROC_RFX_TEMPLATE   template on e.TEMPLATE_ID = template.ID ");
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" INNER JOIN  PROC_RFI_EVENT_INDUS_CAT cat ON cat.EVENT_ID = e.ID ");
		}
		sb.append("  WHERE e.TENANT_ID =:tenantId  ");
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" AND e.EVENT_OWNER = :eventOwner ");
		}
		if (StringUtils.checkString(serchVal).length() > 0) {
			sb.append(" AND (UPPER(e.EVENT_NAME ) like :searchValue or upper(e.REFERANCE_NUMBER) like :searchValue or upper(e.EVENT_ID) like :searchValue)  ");
		}
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" AND cat.IND_CAT_ID = :indCat ");
		}
		sb.append("order by e.CREATED_DATE desc ");
		LOG.info(sb.toString());
		final Query query = getEntityManager().createNativeQuery(sb.toString(), "copyFromPreviousEvent");
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(serchVal).length() > 0) {
			query.setParameter("searchValue", "%" + serchVal.toUpperCase() + "%");
		}

		if (StringUtils.checkString(indCat).length() > 0) {
			query.setParameter("indCat", indCat);
		}

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("eventOwner", loggedInUser);
		}
		query.setFirstResult((Integer.parseInt(pageNo)) * 10);
		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser) {
		StringBuffer sb = new StringBuffer("select distinct e from RfiEvent e left outer join fetch e.industryCategories i where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" and e.eventOwner.id = :loggedInUser ");
		}
		sb.append(" order by e.eventName");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}

		return query.getResultList();
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(ev) from RfiEvent e inner join e.rfiEnvelop ev  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvent findByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEvent r inner join fetch r.eventOwner eo left outer join fetch r.template t left outer join fetch r.depositCurrency left outer join fetch r.deliveryAddress left outer join fetch r.participationFeeCurrency pfc left outer join fetch r.baseCurrency bc left outer join fetch r.costCenter cc left outer join fetch r.industryCategory ec left outer join fetch r.suppliers sup where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RfiEvent> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiTeamMember getRfiTeamMemberByUserIdAndEventId(String eventId, String userId) {
		final Query query = getEntityManager().createQuery("from RfiTeamMember r where r.event.id =:eventId and r.user.id= :userId");
		query.setParameter("eventId", eventId);
		query.setParameter("userId", userId);
		List<RfiTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("from RfiTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEventApproval> getAllApprovalsForEvent(String id) {
		final Query query = getEntityManager().createQuery("select a from RfiEvent e join e.approvals a where e.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfiEvent findEventForCqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r left outer join fetch r.cqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfiEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {

		EventPermissions permissions = new EventPermissions();

		// Event Owner
		RfiEvent event = getPlainEventById(eventId);

		for (RfiUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}
		}

		boolean isAllUserConcludedDone = false;
		if (event.getEnableEvaluationConclusionUsers()) {
			isAllUserConcludedDone = true;
		}
		for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
			if (user.getConcluded() == null || Boolean.FALSE == user.getConcluded()) {
				isAllUserConcludedDone = false;
			}
		}
		permissions.setAllUserConcludedPermatury(isAllUserConcludedDone);

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						// break;
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfiEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfiEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfiApprovalUser> users = approval.getApprovalUsers();
				for (RfiApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Suspension Approver
		List<RfiEventSuspensionApproval> suspApprovals = event.getSuspensionApprovals();
		for (RfiEventSuspensionApproval approval : suspApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfiSuspensionApprovalUser> users = approval.getApprovalUsers();
				for (RfiSuspensionApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveSuspensionApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfiEnvelop> envelopes = rfiEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfiEnvelop envelop : envelopes) {
				List<RfiEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfiEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
					if (!permissions.isOwner()) {
						permissions.setViewer(true);
					}
				}
				// if (envelop.getOpener() != null && envelop.getOpener().getId().equals(userId)) {
				// permissions.setOpener(true);
				// }
				List<RfiEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfiEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public EventPermissions getUserEventPemissions(String userId, String eventId) {

		EventPermissions permissions = new EventPermissions();

		// Event Owner
		RfiEvent event = getPlainEventById(eventId);

		if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
			for (RfiUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
				if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
					permissions.setUnMaskUser(true);
				}
			}
		}

		if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
			for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
				if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
					permissions.setEvaluationConclusionUser(true);
				}
			}

			for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
				if (user.getUser() != null && user.getUser().getId().equals(userId)) {
					permissions.setConclusionUser(true);
				}
			}

		}

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						// break;
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfiEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfiEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfiApprovalUser> users = approval.getApprovalUsers();
				for (RfiApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfiEnvelop> envelopes = rfiEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfiEnvelop envelop : envelopes) {
				List<RfiEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfiEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
				}
				if (envelop.getOpener() != null && envelop.getOpener().getId().equals(userId)) {
					permissions.setOpener(true);
				}
				if (envelop.getOpenerUsers() != null) {
					for (RfiEnvelopeOpenerUser op : envelop.getOpenerUsers()) {
						if (op.getUser().getId().equals(userId)) {
							permissions.setOpener(true);
							break;
						}

					}
				}

			}
		}
		return permissions;
	}

	@Override
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {

		EventPermissions permissions = new EventPermissions();

		// Event Owner
		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		RfiEvent event = getPlainEventById(eventId);
		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						// break;
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfiEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfiEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfiApprovalUser> users = approval.getApprovalUsers();
				for (RfiApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfiEnvelop> envelopes = rfiEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfiEnvelop envelop : envelopes) {
				if (!envelop.getId().equals(envelopeId)) {
					continue;
				}
				List<RfiEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfiEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
					if (!permissions.isOwner()) {
						permissions.setViewer(true);
					}
				}
				// if (envelop.getOpener() != null && envelop.getOpener().getId().equals(userId)) {
				// permissions.setOpener(true);
				// }
				List<RfiEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfiEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public RfiEvent getPreviousEventById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfiEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> getAllRfiEventByLoginId(String loginId) {

		String sql = "select re from RfiEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getEventSuppliersId(String id) {
		Query query = getEntityManager().createQuery("select s.supplier.id from RfiEvent e left outer join e.suppliers s where e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.EventSupplierPojo(s.supplier.id,s.supplier.companyName, ss.timeZone.timeZone) from RfiEventSupplier s inner join s.rfxEvent e, SupplierSettings ss where ss.supplier.id = s.supplier.id and  e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiTeamMember> getBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfiTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfiTeamMember>) query.getResultList();
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail, u.emailNotifications, u.tenantId) from RfiEvent e inner join e.eventOwner u where e.id = :eventId");
		query.setParameter("eventId", eventId);
		return (User) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlag(String eventId) {
		Query query = getEntityManager().createQuery("update RfiEvent e set e.startMessageSent = true where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.tenantId) from RfiTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		StringBuilder hql = new StringBuilder("select count(e) from RfiEvent e where e.template.id =:templateId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public RfiEvent getPlainEventById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfiEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RfiTeamMember tm left outer join tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvent getMobileEventDetails(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r inner join fetch r.eventOwner eo left outer join fetch r.baseCurrency bc left outer join fetch r.suppliers where r.id =:eventId");
			query.setParameter("eventId", id);
			List<RfiEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Event : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public boolean isExistsRfiEventId(String tenantId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(p) from RfiEvent p  where p.eventId= :eventId and p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	/*********************************************** delete data *****************************************************/
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventIdList(String tenantId) {
		StringBuilder hsql = new StringBuilder("select id from RfiEvent p  where p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiComment pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllDocument(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventDocument pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getenvelopIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfiEnvelop p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvaluatorUser(String envelopID) {
		StringBuilder hsql = new StringBuilder("delete from RfiEvaluatorUser pc where pc.envelope.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEnvelop(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEnvelop pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAddress(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventCorrespondenceAddress pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getApprovalIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfiEventApproval p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApprovalUser(String aprovalID) {
		StringBuilder hsql = new StringBuilder("delete from RfiApprovalUser pc where pc.approval.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", aprovalID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAproval(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventApproval p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventAudit p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventContact(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventContact p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfiCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvalutionCqComts(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiCqEvaluationComments p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfiCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfiCqOption p  where p.cqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletSuppCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfiSupplierCqOption p  where p.cqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from  RfiCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfiSupplierCqItem p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfiSupplierCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqItem(String eventId) {

		StringBuilder hsql = new StringBuilder("delete from   RfiCqItem p  where p.rfxEvent.id = :id and p.parent is not null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqParentItem(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiCqItem p  where p.rfxEvent.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiSupplierCqItem p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqParentItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiSupplierCqItem p  where p.cq.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMeetingIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfiEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingContact(String mid) {
		StringBuilder hsql = new StringBuilder("delete from   RfiEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", mid);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingDoc(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiEventMeetingDocument p  where p.tenantId = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiEventMeetingReminder p  where p.rfiEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierMeetingAtt(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiSupplierMeetingAttendance p  where p.rfiEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiSupplierTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventSupplier(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiEventSupplier p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteTimeLine(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiEventTimeLine p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfiReminder p  where p.rfiEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqOption(String cqId) {
		String hsql = "delete from RfiSupplierCqOption p  where p.cqItem.id = :id ";

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvent(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEvent p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeeting(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMettingContact(String meetingId) {
		StringBuilder hsql = new StringBuilder("delete from RfiEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventMeettingIds(String eventId) {

		StringBuilder hsql = new StringBuilder("select id from RfiEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventMessage(String eventId, boolean isParent) {

		String hsql = "delete from RfiEventMessage p  where p.event.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBuyerTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfiTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> getEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		final Query query = searchFilterEventReport(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	private Query searchFilterEventReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		String hql = "select distinct e from RfiEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				hql += " and e.id in (:eventIds)";
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();

		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				hql += " and upper(e.eventName) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(e.referanceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				hql += " and upper(e.eventId) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				hql += " and upper(user.name) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(unit.unitName) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				hql += " and upper(e.status) in (:status)";
				String[] types = searchFilterEventPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(EventStatus.valueOf(ty));
					}
				}
			}

		}
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("eventName")) {
					hql += " and upper(e.eventName" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(e.referanceNumber" + ")like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("sysEventId")) {
					hql += " and upper(e.eventId" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("eventUser")) {
					hql += " and upper(user.name" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("unitName")) {
					hql += " and upper(unit.unitName" + ")like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("status")) {
					hql += " and (e.status" + ")like (:" + cp.getData() + ")";
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!select_all) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					LOG.info("orderColumn" + orderColumn);
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("eventName")) {
						hql += " order by e.eventName " + dir + ",";
					} else {
						if (orderColumn.equals("referenceNumber")) {
							hql += " order by e.referanceNumber " + dir + ",";
						} else if (orderColumn.equals("sysEventId")) {
							hql += " order by e.eventId " + dir + ",";
						} else if (orderColumn.equals("eventStart")) {
							hql += " order by e.eventStart " + dir + ",";
						} else if (orderColumn.equals("eventEnd")) {
							hql += " order by e.eventEnd " + dir + ",";
						} else if (orderColumn.equals("status")) {
							hql += " order by e.status " + dir + ",";
						} else if (orderColumn.equals("eventUser")) {
							hql += " order by e.eventOwner " + dir + ",";
						} else if (orderColumn.equals("unitName")) {
							hql += " order by e.businessUnit " + dir + ",";
						}
					}
				}

				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}

		}

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("eventName")) {
					query.setParameter("eventName", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("referenceNumber")) {
					query.setParameter("referenceNumber", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("sysEventId")) {
					query.setParameter("sysEventId", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("eventUser")) {
					query.setParameter("eventUser", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("unitName")) {
					query.setParameter("unitName", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("status")) {
					query.setParameter("status", EventStatus.valueOf(cp.getSearch().getValue()));
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		return query;
	}

	@Override
	public List<RfiEvent> getSearchEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date endDate, Date startDate) {
		return searchFilterEventReportList(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
	}

	@SuppressWarnings("unchecked")
	private List<RfiEvent> searchFilterEventReportList(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "select distinct e from RfiEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				hql += " and e.id in (:eventIds)";
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();

		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				hql += " and upper(e.eventName) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(e.referanceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				hql += " and upper(e.eventId) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				hql += " and upper(user.name) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(unit.unitName) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				hql += " and upper(e.status) in (:status)";
				String[] types = searchFilterEventPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(EventStatus.valueOf(ty));
					}
				}
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}
		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}

		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEvent> getAllRfiEventByTenantId(String tenantId) {
		String hql = "from RfiEvent re where re.tenantId=:tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId) {
		final Query query = getEntityManager().createQuery("from RfiEvent r where r.eventId =:eventRefranceNo and r.tenantId =:tenantId and r.status in (:status)");
		query.setParameter("eventRefranceNo", eventRefranceNo);
		query.setParameter("tenantId", tenantId);
		List<EventStatus> status = new ArrayList<EventStatus>();
		status.add(EventStatus.APPROVED);
		status.add(EventStatus.ACTIVE);
		query.setParameter("status", status);
		List<RfiEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User getUnMaskedUserNameAndMailByEventId(String eventId) {
		String hql = "select new User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId)from RfiEvent e left outer join e.unMaskedUser u where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyCode, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,r.enableSupplierDeclaration, r.suspensionType) from RfiEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc where r.id =:eventId");
			query.setParameter("eventId", eventId);
			@SuppressWarnings("unchecked")
			List<EventPojo> uList = query.getResultList();
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
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyName, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,da,rbc.currencyCode,bu.displayName, das.stateName, dac.countryName,r.deposit,rdc.currencyCode,r.suspensionType) from RfiEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc left outer join r.depositCurrency as rdc left outer join r.deliveryAddress da left outer join r.businessUnit bu left outer join da.state das left outer join das.country dac where r.id =:eventId");
			query.setParameter("eventId", eventId);
			@SuppressWarnings("unchecked")
			List<EventPojo> uList = query.getResultList();
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
	public List<IndustryCategory> getIndustryCategoryListForRfaById(String eventId) {
		StringBuffer sb = new StringBuffer("select distinct NEW com.privasia.procurehere.core.entity.IndustryCategory(ind.id, ind.code, ind.name) from RfiEvent re left outer join re.industryCategories ind where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStatus(EventStatus status, String eventId) {
		StringBuffer sb = new StringBuffer("update RfiEvent e set e.status=:status where e.id=:eventId");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", status);
		query.executeUpdate();
	}

	@Override
	public RfiEvent getRfiEventForShortSummary(String eventId) {
		String hql = "select e from RfiEvent e left outer join fetch e.baseCurrency  where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (RfiEvent) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		String hql = "select count(*) from RfiEventSupplier sup  where  sup.rfxEvent.id=:eventId";

		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		String hql = "select count(*) from RfiEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.ACCEPTED);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		String hql = "select count(*) from RfiEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventPojo getRfiForPublicEventByeventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id,r.eventId,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate,r.deliveryDate, r.tenantId, r.status, r.paymentTerm, rbc.currencyName,rbc.currencyCode,rob.companyName, r.participationFees, pfc.currencyCode) from RfiEvent r inner join r.eventOwner as ro inner join ro.buyer as rob  left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as pfc where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<EventPojo> uList = query.getResultList();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfiById(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ic from RfiEvent as r  join r.industryCategories as ic  where r.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfiEvent as r where r.id = :eventId and (r.minimumSupplierRating is not null or r.maximumSupplierRating is not null)");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateEventPushedDate(String eventId) {
		String hql = "update RfiEvent re set re.eventPushDate =:eventPushDate where re.id=:eventId and re.eventPushDate is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventPushDate", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> getAllRfiEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUSer) {
		StringBuffer sb = new StringBuffer("from RfiEvent re left outer join fetch re.industryCategory where re.tenantId =:tenantId ");
		if (StringUtils.checkString(loggedInUSer).length() > 0) {
			sb.append(" and re.eventOwner.id = :loggedInUser");
		}
		sb.append("  order by re.createdDate desc ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setFirstResult(0);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUSer).length() > 0) {
			query.setParameter("loggedInUser", loggedInUSer);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventTeamMember(String eventId) {
		String sql = "SELECT u.USER_NAME  FROM PROC_USER u RIGHT OUTER JOIN PROC_RFI_TEAM  team ON  team.USER_ID = u.ID WHERE team.MEMBER_TYPE =:memberType AND team.EVENT_ID =:eventId";
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", TeamMemberType.Associate_Owner);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateImmediately(String eventId, EventStatus status) {
		String hql = "update RfiEvent re set re.status = :status where re.id = :eventId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(e) from RfiEvent e where e.id =:id");
		query.setParameter("id", eventId);
		try {
			Integer count = ((Number) query.getSingleResult()).intValue();
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEvent> getAllActiveEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfiEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RfiEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		List<RfiEvent> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfiEvent> getAllApprovedEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfiEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RfiEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RfiEvent> list = query.getResultList();
		return list;
	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId, User loggedInUser) {
		final Query query = getEntityManager().createQuery("select t.rfxEnvelopeReadOnly from RfiEvent  event left outer join event.template t  where event.id=:eventId ");
		query.setParameter("eventId", eventId);
		return (Boolean) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> getAllRfiEventWhereUnMaskingUserIsNotNull() {
		String sql = "select e from RfiEvent e where e.unMaskedUser is not null";
		final Query query = getEntityManager().createQuery(sql);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventUnMaskUser(String eventId) {
		Query query = getEntityManager().createQuery("update RfiEvent e set e.unMaskedUser = null where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluationConclusionUser u where u.id = :evalConUserId and u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.setParameter("evalConUserId", evalConUserId);
		List<RfiEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public RfiEvent getPlainEventWithOwnerById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r left outer join fetch r.eventOwner ew where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfiEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RfiEvaluationConclusionUser u where  u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		List<RfiEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> findAssociateOwnerOfRfi(String id, TeamMemberType associateOwner) {
		String hql = " from RfiTeamMember ptm where ptm.event.id = :prId and ptm.teamMemberType =:associateOwner";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("prId", id);
		query.setParameter("associateOwner", associateOwner);

		List<EventTeamMember> prTeamList = query.getResultList();
		return prTeamList;
	}

	@Override
	public RfiEvent getEventDetailsForFeePayment(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfiEvent(e.id, e.eventId, e.eventName, e.referanceNumber, e.participationFees, c, cb) from RfiEvent e left outer join e.participationFeeCurrency c left outer join e.createdBy cb where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (RfiEvent) query.getSingleResult();
	}

	@Override
	public long getRfiEventCountByTenantId(String searchValue, String tenantId, String userId, String industryCategory) {
		StringBuffer sb = new StringBuffer("select count(distinct re.id) from RfiEvent re left outer join re.industryCategories i left outer join re.industryCategory where re.tenantId =:tenantId ");
		if (StringUtils.checkString(userId).length() > 0) {
			sb.append(" and re.eventOwner.id = :userId");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(re.eventName) like :searchValue or upper(re.referanceNumber) like :searchValue or upper(re.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}

		final Query query = getEntityManager().createQuery(sb.toString());

		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("userId", userId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
			LOG.info("searchValue" + searchValue.toUpperCase());
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct e from RfiEvent e left outer join fetch e.industryCategories i left outer join fetch e.industryCategory where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}
		if (StringUtils.checkString(userId).length() > 0) {
			sb.append(" and e.eventOwner.id = :loggedInUser ");
		}
		sb.append(" order by e.createdDate");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
			LOG.info("searchValue" + searchValue.toUpperCase());
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("loggedInUser", userId);
		}

		if (start != null && pageLength != null) {
			query.setFirstResult(start);
			query.setMaxResults(pageLength);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.loginId, tm.user.tenantId, tm.user.deviceId) from RfiTeamMember tm where tm.event.id = :eventId and tm.teamMemberType =:memberType");
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", memberType);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getAllRfiEventIdsByLoginId(String loginId) {
		String sql = "select distinct new com.privasia.procurehere.core.entity.Event(re.id,  re.eventId, re.status) from RfiEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTransferOwnerForEvent(String fromUserId, String toUserId) {


		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_RFI_EVENTS SET CREATED_BY = :toUserId, EVENT_OWNER = :toUserId  where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);


		//transfer the user to team members too
		Query query2 = getEntityManager().createQuery(
				"UPDATE RfiTeamMember team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2.setParameter("sourceUser", sourceUser);
		query2.setParameter("targetUser", targetUser);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Form Owners transferred: {}", recordsUpdated);

		//event viewers
		Query query3 = getEntityManager().createNativeQuery("UPDATE PROC_RFI_EVENT_VIEWERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query3.setParameter("toUserId", toUserId);
		query3.setParameter("fromUserId", fromUserId);
		recordsUpdated = query3.executeUpdate();
		LOG.info("Event Viewers transferred: {}", recordsUpdated);

		//event editors
		Query query4 = getEntityManager().createNativeQuery("UPDATE PROC_RFI_EVENT_EDITORS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query4.setParameter("toUserId", toUserId);
		query4.setParameter("fromUserId", fromUserId);
		recordsUpdated = query4.executeUpdate();
		LOG.info("Event Editors transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE RfiApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);

		Query query6 = getEntityManager().createQuery(
				"UPDATE RfiUnMaskedUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		recordsUpdated = query6.executeUpdate();
		LOG.info("Unmasked user transferred: {}", recordsUpdated);

		Query query7 = getEntityManager().createQuery(
				"UPDATE RfiEvaluationConclusionUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query7.setParameter("sourceUser", sourceUser);
		query7.setParameter("targetUser", targetUser);
		recordsUpdated = query7.executeUpdate();
		LOG.info("Evaluation Conclusion user transferred: {}", recordsUpdated);


		Query query8 = getEntityManager().createQuery(
				"UPDATE RfiSuspensionApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query8.setParameter("sourceUser", sourceUser);
		query8.setParameter("targetUser", targetUser);
		recordsUpdated = query8.executeUpdate();
		LOG.info("Suspension Approval user transferred: {}", recordsUpdated);

		//envelope openers
		Query query9 = getEntityManager().createNativeQuery("UPDATE PROC_RFI_ENV_OPEN_USERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query9.setParameter("toUserId", toUserId);
		query9.setParameter("fromUserId", fromUserId);
		recordsUpdated = query9.executeUpdate();
		LOG.info("Envelope Openers transferred: {}", recordsUpdated);

		Query query10 = getEntityManager().createNativeQuery("UPDATE PROC_RFI_EVALUATOR_USER SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query10.setParameter("toUserId", toUserId);
		query10.setParameter("fromUserId", fromUserId);
		recordsUpdated = query10.executeUpdate();
		LOG.info("Evaluator users transferred: {}", recordsUpdated);

	}

	@Override
	public RfiEvent findEventForSapByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfiEvent r where r.eventId =:eventId");
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

}
