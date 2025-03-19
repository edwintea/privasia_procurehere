package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.AnnouncementDao;
import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.AnnouncementPojo;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("announcementDao")
public class AnnouncementDaoImpl extends GenericDaoImpl<Announcement, String> implements AnnouncementDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<Announcement> findAnnouncementsByTeanantId(String tenantId, TableDataInput input) {
		final Query query = constructAnnouncementForTenantQuery(tenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredAnnouncementForTenant(String tenantId, TableDataInput input) {
		final Query query = constructAnnouncementForTenantQuery(tenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveAnnouncementForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(a) from Announcement a where a.status =:status and a.buyer.id =:tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructAnnouncementForTenantQuery(String tenantId, TableDataInput input, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct a.id) ";
		}
		if (!isCount) {
			hql += "select distinct a ";
		}
		hql += "from Announcement a ";
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += "left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ";
		}
		hql += "where a.buyer.id=:tenantId ";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and a.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(a." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and a.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += "order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += "a." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by a.announcementStart desc";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (!isStatusFilterOn) {
			query.setParameter("status", Status.ACTIVE);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Announcement> getAnnouncementListByBuyerId(String tenantId) {
		LOG.info("Getting announcement list for buyer id:" + tenantId);
		StringBuilder hql = new StringBuilder("select a from Announcement a where a.buyer.id = :tenantId and :date between a.announcementStart and a.announcementEnd and a.status = :status and a.publicAnnouncement = true order by a.announcementStart desc ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("date", new Date());
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnnouncementPojo> getAnnouncementList() {
		StringBuilder hql = new StringBuilder("select new com.privasia.procurehere.core.pojo.AnnouncementPojo(a.id, a.title, a.publicOrEmailContent, a.faxContent, a.smsContent, a.announcementStart, a.announcementEnd, a.status,  a.createdDate,  a.modifiedDate, a.buyer.id, a.fax, a.sms, a.email, a.isFaxSent, a.isSmsSent, a.isemailSent, a.publicAnnouncement) from Announcement a where :date > a.announcementStart and a.status = :status and (a.isFaxSent = false or a.isSmsSent =false or a.isemailSent=false) and (a.fax = true or a.sms =true or a.email=true)");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("date", new Date());
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@Override
	public void updateAnnouncementSMSSentFlag(String id) {
		String hql = "update Announcement re set re.isSmsSent = true where re.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}

	@Override
	public void updateAnnouncementFaxSentFlag(String id) {
		String hql = "update Announcement re set re.isFaxSent = true where re.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void updateAnnouncementEmailSentFlag(String id) {
		String hql = "update Announcement re set re.isemailSent = true where re.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

	}

	@Override
	public Announcement getAnnouncementById(String id) {
		LOG.info("Getting announcement list for buyer id:" + id);
		StringBuilder hql = new StringBuilder("select a from Announcement a inner join fetch a.buyer where a.id = :id");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return (Announcement) query.getSingleResult();
	}

	@Override
	public void deleteBuyerAllAoucment(String buyerID) {

		Query query9 = getEntityManager().createQuery("delete from Announcement p  where p.buyer.id = :id ").setParameter("id", buyerID);
		query9.executeUpdate();

		Query query10 = getEntityManager().createQuery("delete from SupplierTags p  where p.buyer.id = :id ").setParameter("id", buyerID);
		query10.executeUpdate();

	}

}
