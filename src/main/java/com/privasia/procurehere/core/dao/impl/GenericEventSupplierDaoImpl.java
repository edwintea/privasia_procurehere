/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
 *
 */
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericEventSupplierDao;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericEventSupplierDaoImpl<T extends EventSupplier, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericEventSupplierDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public boolean isExists(T eventSupplier, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(res.id) from " + entityClass.getSimpleName() + " as res where res.supplier.id = :sid and  res.rfxEvent.id =:reid");
		if (StringUtils.checkString(eventSupplier.getId()).length() > 0) {
			hsql.append(" and res.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("sid", eventSupplier.getSupplier().getId());
		if (StringUtils.checkString(eventId).length() > 0) {
			query.setParameter("reid", eventId);
		}
		if (StringUtils.checkString(eventSupplier.getId()).length() > 0) {
			query.setParameter("id", eventSupplier.getId());
		}

		return ((Number) query.getSingleResult()).longValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSupplierById(String id) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where res.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		List<T> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEventSupplierBySupplierAndEventId(String supplierId, String eventID) {
		LOG.info("Supplier : " + supplierId + " Event : " + eventID);

		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where s.id =:supplierId and re.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventID);
		List<T> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSupplierBySupplierId(String supplierId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s where s.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", supplierId);
		List<T> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@Override
	public int updatePrivewTime(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("update " + entityClass.getSimpleName() + "  set  previewTime = :previewTime  where previewTime is null and supplier.id =:supplierId and rfxEvent.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("previewTime", new Date());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public void updateSubmiTimeOnEventSuspend(String eventId) {
		StringBuilder hsql = new StringBuilder("update " + entityClass.getSimpleName() + "  set  supplierSubmittedTime = null , submitted = :submitted, subbmitedBy = null, submissionStatus =:submissionStatus  where submissionStatus =:submiStatus and rfxEvent.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("submitted", Boolean.FALSE);
		query.setParameter("submissionStatus", SubmissionStatusType.ACCEPTED);
		query.setParameter("submiStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkAnySupplierSubmited(String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as  es where es.rfxEvent.id =:id and es.submitted =:submitted ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		List<T> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplier> getSubmittedSuppliersByEventId(String eventID) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and res.submitted =:submitted order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEventSupplierForAuctionBySupplierAndEventId(String supplierId, String eventID) {
		LOG.info("Supplier : -------------------------" + supplierId + " Event --------------------------: " + eventID);

		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where s.id =:supplierId and re.id = :eventId and res.submissionStatus = :status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventID);
		query.setParameter("status", SubmissionStatusType.COMPLETED);

		List<T> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String eventID) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and res.submissionStatus = :status ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventID) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id,s.id,ss.timeZone.timeZone, e.tenantId, u.name, u.communicationEmail, u.emailNotifications, u.deviceId, bu.displayName,u.id,s.companyName) from " + entityClass.getName() + " res,User u inner join res.supplier as s inner join u.userRole ur inner join ur.accessControlList acl left outer join res.rfxEvent e left outer join e.businessUnit as bu ,SupplierSettings ss where ss.supplier.id = s.id and  e.id = :id and acl.aclValue = 'ROLE_ADMIN' and res.notificationSent = false and s.id = u.tenantId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllDetailsForSendAllInvitation(String eventID) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id,s.id,ss.timeZone.timeZone, e.tenantId, u.name, u.communicationEmail,u.deviceId, bu.displayName,u.id,s.companyName,s.faxNumber,s.mobileNumber,u.emailNotifications) from " + entityClass.getName() + " res,User u inner join res.supplier as s inner join u.userRole ur inner join ur.accessControlList acl left outer join res.rfxEvent e left outer join e.businessUnit as bu ,SupplierSettings ss where ss.supplier.id = s.id and  e.id = :id and acl.aclValue = 'ROLE_ADMIN' and res.notificationSent = false and s.id = u.tenantId and s.status =:status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		query.setParameter("status", SupplierStatus.APPROVED);
		return query.getResultList();
	}

	@Override
	public void updateEventSuppliersNotificationFlag(String id) {
		StringBuilder hsql = new StringBuilder("update " + entityClass.getSimpleName() + " res set res.notificationSent=true where res.id=:id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllSuppliersWithNoNotificationSentByEventId(String eventID) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id,s.id, ss.timeZone.timeZone, re.tenantId, u.name, u.communicationEmail, u.emailNotifications, u.deviceId, bu.displayName,u.id,s.companyName) from  " + entityClass.getSimpleName() + " as res , User u inner join  res.supplier as s inner join  res.rfxEvent as re left outer join re.businessUnit as bu,SupplierSettings ss where ss.supplier.id = s.id and re.id = :id and res.notificationSent = false and s.id = u.tenantId  order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		return query.getResultList();
	}

	@Override
	public int deleteAllSuppliersByEventId(String eventID) {
		StringBuilder hsql = new StringBuilder("delete from " + entityClass.getSimpleName() + " as res where res.rfxEvent.id  = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		return query.executeUpdate();
	}

	@Override
	public List<EventSupplierPojo> getSubmitedSuppliers(String eventID) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(s.id, s.companyName, res.disqualify, de.id, de.envelopSequence) from " + entityClass.getName() + " res inner join res.supplier as s left outer join res.rfxEvent e left outer join res.disqualifiedEnvelope de where e.id = :id and res.submissionStatus = :status and res.submitted  = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventID);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();
	}

}
