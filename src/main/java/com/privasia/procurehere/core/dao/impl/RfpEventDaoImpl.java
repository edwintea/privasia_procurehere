package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfpApprovalUser;
import com.privasia.procurehere.core.entity.RfpAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventApproval;
import com.privasia.procurehere.core.entity.RfpEventAwardApproval;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfpUnMaskedUser;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
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
public class RfpEventDaoImpl extends GenericEventDaoImpl<RfpEvent, String> implements RfpEventDao {

	private static final Logger LOG = LogManager.getLogger(RfpEventDaoImpl.class);

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r inner join fetch r.eventOwner eo left outer join fetch r.template t left outer join fetch r.deliveryAddress left outer join fetch r.participationFeeCurrency pfc left outer join fetch r.depositCurrency left outer join fetch r.baseCurrency bc left outer join fetch r.costCenter cc left outer join fetch r.industryCategory ec left outer join fetch r.suppliers sup where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfpEvent> uList = query.getResultList();
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
	public boolean isExists(RfpEvent rfpEvent) {
		LOG.info("Referance No : " + rfpEvent.getReferanceNumber() + " Tenant Id : " + rfpEvent.getTenantId());
		StringBuilder hsql = new StringBuilder("from RfpEvent re where re.referanceNumber= :referanceNumber and re.id <> :id and re.tenantId= :tenantId ");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("referanceNumber", rfpEvent.getReferanceNumber());
		query.setParameter("tenantId", rfpEvent.getTenantId());
		query.setParameter("id", rfpEvent.getId());

		List<RfpEvent> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvent findEventForCqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r left outer join fetch r.cqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfpEvent> uList = query.getResultList();
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
	public RfpEvent findBySupplierEventId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r inner join fetch r.eventOwner as eo left outer join fetch r.deliveryAddress as da  left outer join fetch da.state as s left outer join fetch s.country left outer join fetch r.eventBqs as bq where r.id =:eventId order by bq.bqOrder");
			query.setParameter("eventId", id);
			@SuppressWarnings("unchecked")
			List<RfpEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				// RfpEvent event = uList.get(0);
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
	public Integer getCountOfEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(ev) from RfpEvent e inner join e.rfxEnvelop ev  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllRfpEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) {

		StringBuffer sb = new StringBuffer("SELECT e.ID as id,e.EVENT_NAME as eventName,null as auctionType , e.STATUS as eventStatus,e.EVENT_DESCRIPTION as descripiton, CASE WHEN template.TEMPLATE_STATUS='INACTIVE' THEN  1  ELSE 0 END as templateStatus, e.REFERANCE_NUMBER as referenceNumber,e.EVENT_ID as eventId,e.EVENT_START as startDate,e.EVENT_END as endDate, (SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFP_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories FROM PROC_RFP_EVENTS e  ");
		sb.append(" LEFT OUTER JOIN PROC_USER  eventOwner on e.EVENT_OWNER = eventOwner.ID  LEFT OUTER JOIN PROC_RFX_TEMPLATE   template on e.TEMPLATE_ID = template.ID ");
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" INNER JOIN  PROC_RFP_EVENT_INDUS_CAT cat ON cat.EVENT_ID = e.ID ");
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

		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		LOG.info(sb);
		List<DraftEventPojo> pojoList = query.getResultList();
		return pojoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser) {
		StringBuffer sb = new StringBuffer("select distinct e from RfpEvent e left outer join fetch e.industryCategories i where e.tenantId = :tenantId ");

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

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select ss from RfpEvent e left outer join e.suppliers s inner join s.supplier ss where e.id =:eventId order by ss.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpTeamMember getRfpTeamMemberByUserIdAndEventId(String eventId, String userId) {
		final Query query = getEntityManager().createQuery("from RfpTeamMember r where r.event.id =:eventId and r.user.id= :userId");
		query.setParameter("eventId", eventId);
		query.setParameter("userId", userId);
		List<RfpTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("from RfpTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfpEvent findEventForBqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r left outer join fetch r.eventBqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfpEvent> uList = query.getResultList();
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
	public List<RfpEventApproval> getAllApprovalsForEvent(String id) {
		final Query query = getEntityManager().createQuery("select a from RfpEvent e join e.approvals a where e.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfpEvent event = getPlainEventById(eventId);

		// use loop for ph-773

		// if (event.getUnMaskedUser() != null && event.getUnMaskedUser().getId().equals(userId) && EventStatus.COMPLETE
		// == event.getStatus() && Boolean.FALSE == event.getDisableMasking()) {
		// permissions.setUnMaskUser(true);
		// }

		for (RfpUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}

		}
		boolean isAllUserConcludedDone = false;
		if (event.getEnableEvaluationConclusionUsers()) {
			isAllUserConcludedDone = true;
		}
		for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
			if (user.getConcluded() == null || Boolean.FALSE == user.getConcluded()) {
				isAllUserConcludedDone = false;
			}
		}
		permissions.setAllUserConcludedPermatury(isAllUserConcludedDone);

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
		List<RfpEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfpEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfpApprovalUser> users = approval.getApprovalUsers();
				for (RfpApprovalUser user : users) {
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
		List<RfpEventSuspensionApproval> suspApprovals = event.getSuspensionApprovals();
		for (RfpEventSuspensionApproval approval : suspApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfpSuspensionApprovalUser> users = approval.getApprovalUsers();
				for (RfpSuspensionApprovalUser user : users) {
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

		// Award Approver
			List<RfpEventAwardApproval> awardApprovals = event.getAwardApprovals();
			for (RfpEventAwardApproval approval : awardApprovals) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					List<RfpAwardApprovalUser> users = approval.getApprovalUsers();
					for (RfpAwardApprovalUser user : users) {
						if (user.getUser().getId().equals(userId)) {
							permissions.setAwardApprover(true);
							permissions.setApprover(true);
							if (approval.isActive()) {
								permissions.setActiveAwardApproval(true);
								break;
							}
						}
					}
				}
			}


		// Envelop Opener and Evaluator
		List<RfpEnvelop> envelopes = rfpEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfpEnvelop envelop : envelopes) {
				List<RfpEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfpEvaluatorUser user : evaluators) {
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
				List<RfpEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfpEnvelopeOpenerUser user : openerUser) {
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
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfpEvent event = getPlainEventById(eventId);

		// use loop for ph-773

		// if (event.getUnMaskedUser() != null && event.getUnMaskedUser().getId().equals(userId) && EventStatus.COMPLETE
		// == event.getStatus() && Boolean.FALSE == event.getDisableMasking()) {
		// permissions.setUnMaskUser(true);
		// }

		for (RfpUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}

		}
		for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
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
		List<RfpEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfpEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfpApprovalUser> users = approval.getApprovalUsers();
				for (RfpApprovalUser user : users) {
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
		List<RfpEnvelop> envelopes = rfpEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfpEnvelop envelop : envelopes) {
				List<RfpEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfpEvaluatorUser user : evaluators) {
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
					for (RfpEnvelopeOpenerUser op : envelop.getOpenerUsers()) {
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
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();
		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfpEvent event = getPlainEventById(eventId);
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
		List<RfpEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfpEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfpApprovalUser> users = approval.getApprovalUsers();
				for (RfpApprovalUser user : users) {
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
		List<RfpEnvelop> envelopes = rfpEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfpEnvelop envelop : envelopes) {
				if (!envelop.getId().equals(envelopeId)) {
					continue;
				}
				List<RfpEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfpEvaluatorUser user : evaluators) {
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
				List<RfpEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfpEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct s from RfpEvent e left outer join e.awardedSuppliers s where e.id =:eventId order by s.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public RfpEvent getPlainEventById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfpEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvent> getAllRfpEventByLoginId(String loginId) {

		String sql = "select re from RfpEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getEventSuppliersId(String id) {
		Query query = getEntityManager().createQuery("select s.supplier.id from RfpEvent e left outer join e.suppliers s where e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.EventSupplierPojo(s.supplier.id,s.supplier.companyName, ss.timeZone.timeZone) from RfpEventSupplier s inner join s.rfxEvent e, SupplierSettings ss where ss.supplier.id = s.supplier.id and  e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpTeamMember> getBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfpTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfpTeamMember>) query.getResultList();
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail, u.emailNotifications, u.tenantId) from RfpEvent e inner join e.eventOwner u where e.id = :eventId");
		query.setParameter("eventId", eventId);
		return (User) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlag(String eventId) {
		Query query = getEntityManager().createQuery("update RfpEvent e set e.startMessageSent = true where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.tenantId) from RfpTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		StringBuilder hql = new StringBuilder("select count(e) from RfpEvent e where e.template.id =:templateId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RfpTeamMember tm left outer join tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvent getMobileEventDetails(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r inner join fetch r.eventOwner eo left outer join fetch r.baseCurrency bc left outer join fetch r.eventBqs where r.id =:eventId");
			query.setParameter("eventId", id);
			List<RfpEvent> uList = query.getResultList();
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
	@SuppressWarnings("unchecked")
	public RfpSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.grandTotal = (select min(r.grandTotal) from RfpSupplierBq r where r.event.id =:eventId and r.bq.id =:bqId and r.supplier.id in (select es.supplier.id from RfpEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false and es.submissionStatus=:submissionStatus )) and sb.event.id =:eventId and sb.bq.id =:bqId and s.id in (select ess.supplier.id from RfpEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false and ess.submissionStatus=:submissionStatus ) ");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			// if more suppliers have same lowest price then sending the first submit supplier.
			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfpSupplierBq rfpSupplierBq : uList) {
					LOG.info("suuplier name: " + rfpSupplierBq.getSupplier().getCompanyName() + "======= rfpSupplierBq:  " + rfpSupplierBq.getGrandTotal());
					suppIds.add(rfpSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfpEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfpEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<RfpEventSupplier> auctionBidsList = bidHistoryQuery.getResultList();
				/*
				 * for (RftEventSupplier rftEventSupplier : auctionBidsList) {
				 * LOG.info("-------------------"+rftEventSupplier.getSupplierSubmittedTime()+"------------"+
				 * rftEventSupplier.getSupplier().getCompanyName()); }
				 */
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					Collections.sort(auctionBidsList, new Comparator<RfpEventSupplier>() {
						public int compare(RfpEventSupplier o1, RfpEventSupplier o2) {
							return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfpSupplierBq rfpSupplierBq : uList) {
							if (rfpSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getSupplier().getId())) {
								return rfpSupplierBq;
							}
						}
					}

				}
			}
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfpSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.event.id =:eventId and sb.bq.id =:bqId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RfpSupplierBq> uList = query.getResultList();
		return uList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfpSupplierBqItem getMinItemisePrice(String id, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfpSupplierBqItem sb left outer join fetch sb.supplier s left outer join fetch sb.event e  left outer join fetch sb.bqItem bi where sb.totalAmount = (select min(r.totalAmount) from RfpSupplierBqItem r where r.bqItem.id = :bqItemId  and r.supplier.id in (select es.supplier.id from RfpEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false)) and bi.id = :bqItemId and s.id in (select ess.supplier.id from RfpEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false )");
		query.setParameter("bqItemId", id);
		query.setParameter("eventId", eventId);
		List<RfpSupplierBqItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfpSupplierBqItem rfpSupplierBq : uList) {
					LOG.info("suuplier name: " + rfpSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rfpSupplierBq.getUnitPrice());
					suppIds.add(rfpSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfpEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfpEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<RfpEventSupplier> auctionBidsList = bidHistoryQuery.getResultList();
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					for (RfpEventSupplier rftEventSupplier : auctionBidsList) {
						LOG.info("-------------------" + rftEventSupplier.getSupplierSubmittedTime() + "------------" + rftEventSupplier.getSupplier().getCompanyName());
					}
					Collections.sort(auctionBidsList, new Comparator<RfpEventSupplier>() {
						public int compare(RfpEventSupplier o1, RfpEventSupplier o2) {
							return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfpSupplierBqItem rfPSupplierBq : uList) {
							if (rfPSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getSupplier().getId())) {
								return rfPSupplierBq;
							}
						}
					}

				}
			}

			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfpSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.event.id =:eventId and sb.bq.id =:bqId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isExistsRfpEventId(String tenantId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(p) from RfpEvent p  where p.eventId= :eventId and p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	/*********************************************** delete data *****************************************************/
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventIdList(String tenantId) {
		StringBuilder hsql = new StringBuilder("select id from RfpEvent p  where p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpComment pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllDocument(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventDocument pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getenvelopIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpEnvelop p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvaluatorUser(String envelopID) {
		StringBuilder hsql = new StringBuilder("delete from RfpEvaluatorUser pc where pc.envelope.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEnvelop(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEnvelop pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAddress(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventCorrespondenceAddress pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getApprovalIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpEventApproval p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApprovalUser(String aprovalID) {
		StringBuilder hsql = new StringBuilder("delete from RfpApprovalUser pc where pc.approval.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", aprovalID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAproval(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventApproval p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAudit p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAwardAudit p  where p.buyer.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAward(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAward p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqEvaluationComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpBqEvaluationComments p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetail(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAwardDetails p  where p.bqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventBq p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierComments(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfpSupplierComment p  where p.supplierBqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqSupplierItem(String bqId, boolean isParent) {

		String hsql = "delete from RfpSupplierBqItem p  where p.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierBqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpSupplierBq p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpSupplierBq p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventContact(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventContact p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvalutionCqComts(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpCqEvaluationComments p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfpCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfpCqOption p  where p.rfpCqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletSuppCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfpSupplierCqOption p  where p.cqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from  RfpCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpSupplierCqItem p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfpSupplierCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqItem(String eventId) {

		StringBuilder hsql = new StringBuilder("delete from   RfpCqItem p  where p.rfxEvent.id = :id and p.parent is not null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqParentItem(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpCqItem p  where p.rfxEvent.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpSupplierCqItem p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqParentItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpSupplierCqItem p  where p.cq.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItem(String bqId, boolean isParent) {
		String hsql = "delete from   RfpBqItem p  where p.id = :id ";
		if (isParent) {
			hsql += "and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMeetingIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfpEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingContact(String mid) {
		StringBuilder hsql = new StringBuilder("delete from   RfpEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", mid);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingDoc(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpEventMeetingDocument p  where p.tenantId = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpEventMeetingReminder p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierMeetingAtt(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpSupplierMeetingAttendance p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpSupplierTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventSupplier(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpEventSupplier p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteTimeLine(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpEventTimeLine p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfpReminder p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqItemList(String bqId) {
		StringBuilder hsql = new StringBuilder("select id from RfpBqItem p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSppBqItemList(String bqId) {

		StringBuilder hsql = new StringBuilder("select id from RfpSupplierBqItem p  where p.supplierBq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItems(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteeventBqItems(String eventId, boolean isParent) {

		String hsql = "delete from RfpBqItem p  where p.rfxEvent.id = :id ";
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
	public void deleteSupplierBqItems(String eventId, boolean isParent) {
		String hsql = "delete from RfpSupplierBqItem p  where p.event.id = :id ";
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
	public void deleteSupplierCqOption(String cqId) {
		String hsql = "delete from RfpSupplierCqOption p  where p.cqItem.id = :id ";

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetails(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAward p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAwardByBq(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAward p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetailbyItem(String bqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventAwardDetails p  where p.bqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvent(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEvent p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeeting(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMettingContact(String meetingId) {
		StringBuilder hsql = new StringBuilder("delete from RfpEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventMeettingIds(String eventId) {

		StringBuilder hsql = new StringBuilder("select id from RfpEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventMessage(String eventId, boolean isParent) {

		String hsql = "delete from RfpEventMessage p  where p.event.id = :id ";
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
		StringBuilder hsql = new StringBuilder("delete from RfpTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvent findRfpEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		final Query query = getEntityManager().createQuery("from RfpEvent e where e.erpAwardRefId like :erpAwardRefId and e.tenantId = :tenantId");
		query.setParameter("erpAwardRefId", "%" + erpAwardRefId + "%");
		query.setParameter("tenantId", tenantId);
		List<RfpEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvent> getEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {

		final Query query = searchFilterEventReport(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	private Query searchFilterEventReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		String hql = "select distinct e from RfpEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

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
	public List<RfpEvent> getSearchEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		return searchFilterEventReportList(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
	}

	@SuppressWarnings("unchecked")
	private List<RfpEvent> searchFilterEventReportList(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "select distinct e from RfpEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

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
	public List<RfpEvent> getAllRfpEventByTenantId(String tenantId) {

		String hql = "from RfpEvent re where re.tenantId=:tenantId";
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
		final Query query = getEntityManager().createQuery("from RfpEvent r where r.eventId =:eventRefranceNo and r.tenantId =:tenantId and r.status in (:status)");
		query.setParameter("eventRefranceNo", eventRefranceNo);
		query.setParameter("tenantId", tenantId);
		List<EventStatus> status = new ArrayList<EventStatus>();
		status.add(EventStatus.APPROVED);
		status.add(EventStatus.ACTIVE);
		query.setParameter("status", status);
		List<RfpEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User getUnMaskedUserNameAndMailByEventId(String eventId) {
		String hql = "select new User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId)from RfpEvent e left outer join e.unMaskedUser u where e.id=:id";
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
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyCode, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,r.enableSupplierDeclaration, r.disableTotalAmount, r.suspensionType) from RfpEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc where r.id =:eventId");
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
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyName, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,da,rbc.currencyCode,bu.displayName, das.stateName, dac.countryName,r.deposit,rdc.currencyCode, r.suspensionType) from RfpEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc left outer join r.depositCurrency as rdc left outer join r.deliveryAddress da left outer join r.businessUnit bu left outer join da.state das left outer join das.country dac where r.id =:eventId");
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
		StringBuffer sb = new StringBuffer("select distinct NEW com.privasia.procurehere.core.entity.IndustryCategory(ind.id, ind.code, ind.name) from RfpEvent re left outer join re.industryCategories ind where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStatus(EventStatus status, String eventId) {
		StringBuffer sb = new StringBuffer("update RfpEvent e set e.status=:status where e.id=:eventId");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", status);
		query.executeUpdate();
	}

	@Override
	public RfpEvent getRfpEventForShortSummary(String eventId) {
		String hql = "select e from RfpEvent e left outer join fetch e.baseCurrency  where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (RfpEvent) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		String hql = "select count(*) from RfpEventSupplier sup  where  sup.rfxEvent.id=:eventId";

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
		String hql = "select count(*) from RfpEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
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
		String hql = "select count(*) from RfpEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
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
	public List<RfpSupplierBq> getLowestSubmissionPrice(String eventId) {
		String hql = "select  bq from RfpSupplierBq  bq  left outer join fetch bq.supplier where  bq.event.id=:eventId and  bq.grandTotal=(select MIN(bq.grandTotal) FROM bq  where  bq.event.id=:id)";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		query.setParameter("id", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getSuppliersByStatus(String eventId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.pojo.EventSupplierPojo(sup.id, s.id, s.companyName, sum(bq.totalAfterTax)) from RfpEventSupplier sup left outer join sup.supplier s, RfpSupplierBq bq where bq.supplier.id = sup.supplier.id and sup.rfxEvent.id = :eventId and sup.submissionStatus = :submissionStatusType and sup.disqualify = :disqualify and bq.event.id = :eventId group by sup.id, s.id, s.companyName order by SUM(bq.totalAfterTax)");

		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", false);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * @Override public RfpSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) { final Query query =
	 * getEntityManager().
	 * createQuery("select bq from RfpSupplierBq bq where bq.supplier.id=:id and bq.event.id=:eventId");
	 * query.setParameter("id", supplierId); query.setParameter("eventId", eventId); try { return (RfpSupplierBq)
	 * query.getSingleResult(); } catch (Exception e) { return null; } }
	 */

	@Override
	public RfpEnvelop getBqForEnvelope(String envelopeId) {
		final Query query = getEntityManager().createQuery("select re from RfpEnvelop re left outer join fetch re.bqList bq where re.id =:envelopeId ");
		query.setParameter("envelopeId", envelopeId);
		try {
			return (RfpEnvelop) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventPojo getRfpForPublicEventByeventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id,r.eventId,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate,r.deliveryDate, r.tenantId, r.status, r.paymentTerm, rbc.currencyName,rbc.currencyCode,rob.companyName, r.participationFees, pfc.currencyCode) from RfpEvent r inner join r.eventOwner as ro inner join ro.buyer as rob  left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as pfc where r.id =:eventId");
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
	public List<IndustryCategory> getIndustryCategoriesForRfpById(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ic from RfpEvent as r  join r.industryCategories as ic  where r.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfpEvent as r where r.id = :eventId and (r.minimumSupplierRating is not null or r.maximumSupplierRating is not null)");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Supplier findWinnerSupplier(String id) {
		Query query = getEntityManager().createNativeQuery("SELECT DISTINCT sb.SUPPLIER_ID AS id, sum(TOTAL_AFTER_TAX) AS totalAfterTax, es.SUPPLIER_BID_SUBMITTED_TIME AS createdDate FROM PROC_RFP_SUPPLIER_BQ sb JOIN PROC_RFP_EVENT_SUPPLIERS es ON sb.SUPPLIER_ID = es.SUPPLIER_ID WHERE sb.EVENT_ID =:eventId	AND es.IS_DISQUALIFY = 0 AND es.SUBMISSION_STATUS = 'COMPLETED'	AND es.EVENT_ID = sb.EVENT_ID GROUP BY sb.SUPPLIER_ID, es.SUPPLIER_BID_SUBMITTED_TIME	ORDER BY totalAfterTax ASC, es.SUPPLIER_BID_SUBMITTED_TIME", "winnerSupplierResult");
		query.setParameter("eventId", id);
		List<Supplier> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateEventPushedDate(String eventId) {
		String hql = "update RfpEvent re set re.eventPushDate =:eventPushDate where re.id=:eventId and re.eventPushDate is null";
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

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		String hql = "select AVG(bq.grandTotal) from RfpSupplierBq bq where bq.event.id = :id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			return (Double) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updatePrPushDate(String eventId) {
		String hql = "update RfpEvent re set re.pushToPr =:pushToPr where re.id=:eventId and re.pushToPr is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("pushToPr", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateEventAward(String eventId) {
		String hql = "update RfpEvent re set re.awardDate =:awardDate where re.id=:eventId and re.awardDate is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("awardDate", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateImmediately(String eventId, EventStatus status) {
		String hql = "update RfpEvent re set re.status = :status where re.id = :eventId ";
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

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser) {
		StringBuffer sb = new StringBuffer("from RfpEvent re left outer join fetch re.industryCategory where re.tenantId =:tenantId ");
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" and re.eventOwner.id = :loggedInUser");
		}
		sb.append("  order by re.createdDate desc ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setFirstResult(0);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}
		return query.getResultList();
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(e) from RfpEvent e where e.id =:id");
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
	public List<RfpEvent> getAllActiveEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfpEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RfpEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		List<RfpEvent> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpEvent> getAllApprovedEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfpEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RfpEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RfpEvent> list = query.getResultList();
		return list;
	}

	@Override
	public void updateRfaForAwardPrice(String eventId, BigDecimal sumAwardPrice) {
		try {
			RfpEvent rfpEvent = findById(eventId);
			rfpEvent.setAwardedPrice(sumAwardPrice);
			saveOrUpdate(rfpEvent);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r where r.eventId = :eventId and r.tenantId = :tenantId");
			query.setParameter("eventId", eventId);
			query.setParameter("tenantId", tenantId);
			List<RfpEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (Exception nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventAwardAudit> getRfpEventAwardByEventId(String eventId) {
		String hql = " from RfpEventAwardAudit award where award.event.id=:eventId order by award.actionDate  desc";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		query.setFirstResult(0);
		query.setMaxResults(1);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId) {
		final Query query = getEntityManager().createQuery("select t.rfxEnvelopeReadOnly from RfpEvent  event left outer join event.template t  where event.id=:eventId ");
		query.setParameter("eventId", eventId);
		return (Boolean) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvent> getAllRfpEventWhereUnMaskingUserIsNotNull() {
		String sql = "select e from RfpEvent e where e.unMaskedUser is not null";
		final Query query = getEntityManager().createQuery(sql);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventUnMaskUser(String eventId) {
		Query query = getEntityManager().createQuery("update RfpEvent e set e.unMaskedUser = null where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		final Query query = getEntityManager().createQuery("from RfpEvaluationConclusionUser u where u.id = :evalConUserId and u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.setParameter("evalConUserId", evalConUserId);
		List<RfpEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public RfpEvent getPlainEventWithOwnerById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r left outer join fetch r.eventOwner ew where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfpEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RfpEvaluationConclusionUser u where  u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		List<RfpEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> findAssociateOwnerOfRfp(String id, TeamMemberType associateOwner) {
		String hql = " from RfpTeamMember ptm where ptm.event.id = :prId and ptm.teamMemberType =:associateOwner";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("prId", id);
		query.setParameter("associateOwner", associateOwner);

		List<EventTeamMember> prTeamList = query.getResultList();
		return prTeamList;
	}

	@Override
	public RfpEvent getEventDetailsForFeePayment(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfpEvent(e.id, e.eventId, e.eventName, e.referanceNumber, e.participationFees, c, cb) from RfpEvent e left outer join e.participationFeeCurrency c left outer join e.createdBy cb where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (RfpEvent) query.getSingleResult();
	}

	@Override
	public long getRfpEventCountByTenantId(String searchValue, String tenantId, String userId, String industryCategory) {
		StringBuffer sb = new StringBuffer("select count(distinct re.id) from RfpEvent re left outer join re.industryCategories i left outer join re.industryCategory where re.tenantId =:tenantId ");
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
	public List<RfpEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct e from RfpEvent e left outer join fetch e.industryCategories i left outer join fetch e.industryCategory where e.tenantId = :tenantId ");

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
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.loginId, tm.user.tenantId, tm.user.deviceId) from RfpTeamMember tm where tm.event.id = :eventId and tm.teamMemberType =:memberType");
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", memberType);
		return (List<User>) query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revertEventAwardStatus(String eventId) {
//		Query query = getEntityManager().createQuery("update RfpEvent e set e.status = :status where e.id = :eventId").setParameter("status", EventStatus.COMPLETE).setParameter("eventId", eventId);
//		query.executeUpdate();

		Query query = getEntityManager().createQuery("update RfpEvent e set e.status = :status, e.awardStatus = :awardStatus, e.awarded = false where e.id = :eventId")//
				.setParameter("status", EventStatus.COMPLETE)//
				.setParameter("awardStatus", AwardStatus.DRAFT)//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfpEventAward e set e.transferred = false where e.rfxEvent.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfpEventAwardApproval e set e.done = false, e.active = false where e.event.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfpAwardApprovalUser e set e.actionDate = null, e.approvalStatus = :approvalStatus, e.remarks = null where e.approval.id in (select a.id from RfpEventAwardApproval a where a.event.id = :eventId ) ")//
				.setParameter("approvalStatus", ApprovalStatus.PENDING)//
				.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getAllRfpEventIdsByLoginId(String loginId) {
		LOG.info("Login Id : " + loginId);
		String sql = "select distinct new com.privasia.procurehere.core.entity.Event(re.id,  re.eventId, re.status) from RfpEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTransferOwnerForEvent(String fromUserId, String toUserId) {

		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_RFP_EVENTS SET CREATED_BY = :toUserId, EVENT_OWNER = :toUserId  where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info(" RFP Creators transferred: {}", recordsUpdated);

		//transfer the user to team members too
		Query query2 = getEntityManager().createQuery(
				"UPDATE RfpTeamMember team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2.setParameter("sourceUser", sourceUser);
		query2.setParameter("targetUser", targetUser);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Form Owners transferred: {}", recordsUpdated);

		//event viewers
		Query query3 = getEntityManager().createNativeQuery("UPDATE PROC_RFP_EVENT_VIEWERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query3.setParameter("toUserId", toUserId);
		query3.setParameter("fromUserId", fromUserId);
		recordsUpdated = query3.executeUpdate();
		LOG.info("Event Viewers transferred: {}", recordsUpdated);

		//event editors
		Query query4 = getEntityManager().createNativeQuery("UPDATE PROC_RFP_EVENT_EDITORS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query4.setParameter("toUserId", toUserId);
		query4.setParameter("fromUserId", fromUserId);
		recordsUpdated = query4.executeUpdate();
		LOG.info("Event Editors transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE RfpApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);

		Query query6 = getEntityManager().createQuery(
				"UPDATE RfpUnMaskedUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		recordsUpdated = query6.executeUpdate();
		LOG.info("Unmasked user transferred: {}", recordsUpdated);

		Query query7 = getEntityManager().createQuery(
				"UPDATE RfpEvaluationConclusionUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query7.setParameter("sourceUser", sourceUser);
		query7.setParameter("targetUser", targetUser);
		recordsUpdated = query7.executeUpdate();
		LOG.info("Evaluation Conclusion user transferred: {}", recordsUpdated);

		Query query8 = getEntityManager().createQuery(
				"UPDATE RfpSuspensionApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query8.setParameter("sourceUser", sourceUser);
		query8.setParameter("targetUser", targetUser);
		recordsUpdated = query8.executeUpdate();
		LOG.info("Suspension Approval user transferred: {}", recordsUpdated);

		//envelope openers
		Query query9 = getEntityManager().createNativeQuery("UPDATE PROC_RFP_ENV_OPEN_USERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query9.setParameter("toUserId", toUserId);
		query9.setParameter("fromUserId", fromUserId);
		recordsUpdated = query9.executeUpdate();
		LOG.info("Envelope Openers transferred: {}", recordsUpdated);

		Query query10 = getEntityManager().createNativeQuery("UPDATE PROC_RFP_EVALUATOR_USER SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query10.setParameter("toUserId", toUserId);
		query10.setParameter("fromUserId", fromUserId);
		recordsUpdated = query10.executeUpdate();
		LOG.info("Evaluator users transferred: {}", recordsUpdated);

		Query query11 = getEntityManager().createQuery(
				"UPDATE RfpEnvelop sfaur " +
						"SET sfaur.leadEvaluater = :targetUser " +
						"WHERE sfaur.leadEvaluater = :sourceUser "
		);
		query11.setParameter("targetUser", targetUser);
		query11.setParameter("sourceUser", sourceUser);
		recordsUpdated = query11.executeUpdate();
		LOG.info("LEAD Evaluator users transferred: {}", recordsUpdated);
		// award approver
		Query query12 = getEntityManager().createQuery(
				"UPDATE RfpAwardApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query12.setParameter("targetUser", targetUser);
		query12.setParameter("sourceUser", sourceUser);
		recordsUpdated = query12.executeUpdate();
		LOG.info("LEAD Evaluator users transferred: {}", recordsUpdated);

	}

	@Override
	public RfpEvent findEventForSapByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfpEvent r where r.eventId =:eventId");
			query.setParameter("eventId", eventId);
			List<RfpEvent> uList = query.getResultList();
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
	public RfpEvent findEventSorByEventId(String id) {
		try {
			// final Query query = getEntityManager().createQuery("from RfqEvent r inner join fetch r.eventOwner as eo
			// left outer join fetch r.eventBqs as bq where r.id =:eventId");
			final Query query = getEntityManager().createQuery("from RfpEvent r  left outer join fetch r.eventSors as sor where r.id =:eventId order by sor.sorOrder");
			query.setParameter("eventId", id);
			@SuppressWarnings("unchecked")
			List<RfpEvent> uList = query.getResultList();
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
