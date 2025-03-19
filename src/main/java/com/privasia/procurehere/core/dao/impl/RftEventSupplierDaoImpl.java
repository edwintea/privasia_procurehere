package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RftEventSupplierDaoImpl extends GenericEventSupplierDaoImpl<RftEventSupplier, String> implements RftEventSupplierDao {

	@Autowired
	UserDao userDao;

	@Autowired
	SupplierDao supplierDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RftEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RftEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventSupplier eventSupplier : list) {
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
		final Query query = getEntityManager().createQuery("select e from RftEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify = false order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RftEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventSupplier eventSupplier : list) {
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
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RftSupplierTeamMember tm join tm.user u join tm.eventSupplier es join es.supplier s where tm.event.id =:eventId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftSupplierTeamMember> getRftSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("from RftSupplierTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId and tm.eventSupplier.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RftEvent> uList = query.getResultList();
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
	public RftSupplierTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId) {
		final Query query = getEntityManager().createQuery("from RftSupplierTeamMember r where r.event.id =:eventId and r.user.id= :userId");
		query.setParameter("eventId", eventId);
		query.setParameter("userId", userId);
		List<RftSupplierTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForSummary(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RftEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify =:isQualify order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("isQualify", Boolean.FALSE);
		List<RftEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				returnList.add(supplier);
			}
		}
		return returnList;
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

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
		final Query query = getEntityManager().createQuery("select count(supplier) from RftEventSupplier e where e.rfxEvent.id =:eventId  ");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(s) from RftEvent e inner join e.suppliers s  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers) {
		// TODO Auto-generated method stub
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RftEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and ss in (:selectedSuppliers) order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("selectedSuppliers", selectedSuppliers);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RftEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventSupplier eventSupplier : list) {
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
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.FeePojo(a.feePaid, a.depositPaid,a.feePaidDate,a.depositPaidDate,a.feeReference,a.depositReference,sup.companyName,sup.fullName,sup.communicationEmail,sup.companyContactNumber,sup.id,a.selfInvited) from RftEventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId order by sup.companyName ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RftEventSupplier as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and s.id =:supplierId order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct res from RftEventSupplier as res,FavouriteSupplier fs inner join res.supplier as s left outer join fs.supplier as fss inner join res.rfxEvent as re where re.id = :id and s.id =fss.id and fs.vendorCode =:supplierCode and fs.buyer.id=:tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierCode", supplierCode);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id, s.id, s.companyName, s.companyContactNumber, s.communicationEmail) from RftEventSupplier as res inner join res.supplier as s  inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setMaxResults(input.getLength());
		query.setFirstResult(input.getStart());
		return query.getResultList();
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct count(res) from RftEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierSearchPojo> favoriteSuppliersOfBuyerByState(String buyerId, SupplierSearchPojo searchParams, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive, String eventType, String eventId, boolean isMinMaxPresent) {
		String hql = "";
		hql += "SELECT distinct NEW com.privasia.procurehere.core.pojo.SupplierSearchPojo(s.id,s.companyName,s.communicationEmail,s.companyContactNumber)from FavouriteSupplier as fs ";

		if (isMinMaxPresent) {
			hql += " , " + StringUtils.capitalize(eventType.toLowerCase()) + "Event e ";
		}

		hql += " inner join fs.supplier s left outer join s.registrationOfCountry c left outer join s.state sts left outer join fs.supplierTags st left outer join fs.industryCategory ic inner join fs.buyer as b where b.id =:id and fs.status = :status ";

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			hql += " and c.id = :country";
		}
		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			hql += " and (s.state.id) in (:state)";
		}
		if (CollectionUtil.isNotEmpty(industryCategories)) {
			hql += " and ic in (:industryCategories)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			hql += " and (st.id)  in (:supplierTagName)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			hql += " and (st.id) not in (:supplierTagName)";
		}

		if (isMinMaxPresent) {
			LOG.info("Min max present:");
			hql += " AND e.id = :eventId ";
			hql += " AND case when e.minimumSupplierRating is not null and e.maximumSupplierRating is null and (fs.ratings >= e.minimumSupplierRating or fs.ratings is null) then 1 ";
			hql += " when e.maximumSupplierRating is not null and e.minimumSupplierRating is null and (fs.ratings <= e.maximumSupplierRating or fs.ratings is null) then 1 ";
			hql += " when e.maximumSupplierRating is not null and e.minimumSupplierRating is not null and ((fs.ratings >= e.minimumSupplierRating and fs.ratings <= e.maximumSupplierRating) or (fs.ratings is null)) then 1 ";
			hql += " else 0 end = 1 ";
		}
		LOG.info(" hsql :" + hql.toString());
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", buyerId);

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			query.setParameter("country", searchParams.getRegistrationOfCountry());
		}

		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			query.setParameter("state", Arrays.asList(searchParams.getState()));
		}
		if (CollectionUtil.isNotEmpty(industryCategories)) {
			query.setParameter("industryCategories", industryCategories);
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if (isMinMaxPresent) {
			query.setParameter("eventId", eventId);
		}
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierSearchPojo> favoriteSuppliersOfBuyer(String buyerId, SupplierSearchPojo searchParams, Boolean exclusive, Boolean inclusive, boolean isMinMaxPresent, String eventType, String eventId) {
		/*String hql = "";
		hql += " SELECT distinct NEW com.privasia.procurehere.core.pojo.SupplierSearchPojo(s.id,s.companyName,s.communicationEmail ,s.companyContactNumber) from FavouriteSupplier as fs ";

		if (isMinMaxPresent) {
			hql += " , " + StringUtils.capitalize(eventType.toLowerCase()) + "Event e ";
		}

		hql += " inner join fs.supplier s left outer join s.registrationOfCountry c left outer join s.state sts left outer join fs.supplierTags st inner join fs.buyer as b where b.id =:id and fs.status = :status ";

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			hql += " and c.id = :country";
		}
		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			hql += " and (s.state.id) in (:state)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			hql += " and (st.id)  in (:supplierTagName)";
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			hql += " and (st.id) not in (:supplierTagName)";
		}
		if (isMinMaxPresent) {
			LOG.info("Min max present:");
			hql += " AND e.id = :eventId ";
			hql += " AND case when e.minimumSupplierRating is not null and e.maximumSupplierRating is null and (fs.ratings >= e.minimumSupplierRating or fs.ratings is null) then 1 ";
			hql += " when e.maximumSupplierRating is not null and e.minimumSupplierRating is null and (fs.ratings <= e.maximumSupplierRating or fs.ratings is null) then 1 ";
			hql += " when e.maximumSupplierRating is not null and e.minimumSupplierRating is not null and ((fs.ratings >= e.minimumSupplierRating and fs.ratings <= e.maximumSupplierRating) or (fs.ratings is null)) then 1 ";
			hql += " else 0 end = 1 ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", buyerId);

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			query.setParameter("country", searchParams.getRegistrationOfCountry());
		}

		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			query.setParameter("state", Arrays.asList(searchParams.getState()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == inclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE == exclusive) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if (isMinMaxPresent) {
			query.setParameter("eventId", eventId);
		}
		query.setParameter("status", FavouriteSupplierStatus.ACTIVE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}*/
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT s.SUPPLIER_ID, s.COMPANY_NAME, s.COMMUNICATION_EMAIL, s.COMPANY_CONTACT_NUMBER ");
		sql.append("FROM PROC_FAVOURITE_SUPPLIER fs ");
		sql.append("INNER JOIN PROC_SUPPLIER s ON fs.SUPPLIER_ID = s.SUPPLIER_ID ");
		sql.append("LEFT JOIN PROC_COUNTRIES c ON s.REGISTRATION_COUNTRY = c.ID ");
		sql.append("LEFT JOIN PROC_STATE sts ON s.STATE_ID = sts.ID ");
		sql.append("LEFT JOIN PROC_FAV_SUPPLIER_TAGS supplierta4_ ON fs.FAV_SUPPLIER_ID = supplierta4_.FAV_SUPP_ID ");
		sql.append("LEFT JOIN PROC_SUPPLIER_TAGS supplierta5_ ON supplierta4_.SUPP_TAG_ID = supplierta5_.ID ");
		sql.append("INNER JOIN PROC_BUYER buyer6_ ON fs.BUYER_ID = buyer6_.BUYER_ID ");
		sql.append("LEFT OUTER JOIN PROC_SUPPLIER_STATES pss ON pss.SUPPLIER_ID = s.SUPPLIER_ID ");
		if (isMinMaxPresent) {
			sql.append("CROSS JOIN PROC_").append(StringUtils.capitalize(eventType.toLowerCase())).append("_EVENTS e ");
		}
		sql.append("WHERE buyer6_.BUYER_ID = :buyerId ");
		sql.append("AND fs.SUPPLIER_STATUS = 'ACTIVE' ");

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			sql.append("AND c.ID = :country ");
		}
		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			sql.append("AND sts.ID IN (:state) ");
		}
		if (searchParams.getCoverage() != null && searchParams.getCoverage().length > 0) {
			sql.append("AND pss.STATE_ID IN (:coverage) ");
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE.equals(inclusive)) {
			sql.append("AND supplierta5_.ID IN (:supplierTagName) ");
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE.equals(exclusive)) {
			sql.append("AND supplierta5_.ID NOT IN (:supplierTagName) ");
		}
		if (isMinMaxPresent) {
			sql.append("AND e.ID = :eventId ");
			sql.append("AND (CASE ");
			sql.append("WHEN e.MINIMUM_SUPPLIER_RATING IS NOT NULL AND e.MAXIMUM_SUPPLIER_RATING IS NULL AND (fs.RATINGS >= e.MINIMUM_SUPPLIER_RATING OR fs.RATINGS IS NULL) THEN 1 ");
			sql.append("WHEN e.MAXIMUM_SUPPLIER_RATING IS NOT NULL AND e.MINIMUM_SUPPLIER_RATING IS NULL AND (fs.RATINGS <= e.MAXIMUM_SUPPLIER_RATING OR fs.RATINGS IS NULL) THEN 1 ");
			sql.append("WHEN e.MAXIMUM_SUPPLIER_RATING IS NOT NULL AND e.MINIMUM_SUPPLIER_RATING IS NOT NULL AND ((fs.RATINGS >= e.MINIMUM_SUPPLIER_RATING AND fs.RATINGS <= e.MAXIMUM_SUPPLIER_RATING) OR (fs.RATINGS IS NULL)) THEN 1 ");
			sql.append("ELSE 0 END) = 1 ");
		}

		final Query query = getEntityManager().createNativeQuery(sql.toString());
		query.setParameter("buyerId", buyerId);

		if (StringUtils.checkString(searchParams.getRegistrationOfCountry()).length() > 0) {
			query.setParameter("country", searchParams.getRegistrationOfCountry());
		}
		if (searchParams.getState() != null && searchParams.getState().length > 0) {
			query.setParameter("state", Arrays.asList(searchParams.getState()));
		}
		if (searchParams.getCoverage() != null && searchParams.getCoverage().length > 0) {
			query.setParameter("coverage", Arrays.asList(searchParams.getCoverage()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE.equals(inclusive)) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if ((searchParams.getSupplierTagName() != null && searchParams.getSupplierTagName().length > 0) && Boolean.TRUE.equals(exclusive)) {
			query.setParameter("supplierTagName", Arrays.asList(searchParams.getSupplierTagName()));
		}
		if (isMinMaxPresent) {
			query.setParameter("eventId", eventId);
		}

		try {
			List<Object[]> results = query.getResultList();
			List<SupplierSearchPojo> searchResults = new ArrayList<>();
			for (Object[] result : results) {
				SupplierSearchPojo pojo = new SupplierSearchPojo();
				pojo.setId((String) result[0]);
				pojo.setCompanyName((String) result[1]);
				pojo.setCommunicationEmail((String) result[2]);
				pojo.setCompanyContactNumber((String) result[3]);
				searchResults.add(pojo);
			}
			return searchResults;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public boolean isSupplierExistsForPublicEvent(String supplierId, String eventId, RfxTypes eventType) {
		StringBuilder hsql = new StringBuilder("select count(res.id) from R" + eventType.name().substring(1, 3).toLowerCase() + "EventSupplier as res where res.supplier.id = :sid and  res.rfxEvent.id =:reid");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("reid", eventId);
		query.setParameter("sid", supplierId);
		return ((Number) query.getSingleResult()).longValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		String hql = "select distinct sup from RftEventSupplier sup left outer join fetch sup.supplier left outer join fetch sup.acceptedBy  where sup.submissionStatus=:submissionStatus and sup.disqualify=:disqualify and sup.rfxEvent.id=:eventId";
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
	public List<RftEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id  and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null) or  (res.submissionStatus = :status and res.isRejectedAfterStart = :isRejectedAfterStart) ) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("isRejectedAfterStart", Boolean.TRUE);
		query.setParameter("status", SubmissionStatusType.REJECTED);
		return query.getResultList();
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select e.eventName from RftEvent e where e.id =:eventId");
		query.setParameter("eventId", eventId);
		return (String) query.getSingleResult();
	}

	@Override
	public RftEventSupplier getSupplierByStripePaymentId(String paymentId) {
		Query query = getEntityManager().createQuery("select e from RftEventSupplier e where e.feeReference =:paymentId");
		query.setParameter("paymentId", paymentId);
		try {
			return (RftEventSupplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id  and ( res.disqualify=:disqualify or (res.reQualifiedTime is not null) ) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		String hsql = new String("select distinct new com.privasia.procurehere.core.entity.RftEventSupplier(res.id, res.submissionStatus) from RftEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where res.rfxEvent.id =:id ");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		return query.getResultList();
	}
}
