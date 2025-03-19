package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ScoreRatingDao;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class ScoreRatingDaoImpl extends GenericDaoImpl<ScoreRating, String> implements ScoreRatingDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreRating> getAllActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType tenantType) {
		String sql = "from ScoreRating sr left outer join fetch sr.createdBy as cb left outer join fetch sr.modifiedBy as mb where sr.status='ACTIVE' ";
		if (TenantType.BUYER == tenantType) {
			sql += " and sr.buyer.id =:tenantId ";
		}
		sql += " order by sr.createdDate desc";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreRating> findScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		final Query query = constructScoreRatingForTenantQuery(loggedInUserTenantId, input, false, tenantType);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredScoreRatingForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		final Query query = constructScoreRatingForTenantQuery(loggedInUserTenantId, input, true, tenantType);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveScoreRatingForTenant(String loggedInUserTenantId, TenantType tenantType) {
		StringBuilder hql = new StringBuilder("select count (sr) from ScoreRating sr where sr.status =:status ");
		if (TenantType.BUYER == tenantType) {
			hql.append(" and sr.buyer.id =:tenantId ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructScoreRatingForTenantQuery(String tenantId, TableDataInput input, boolean isCount, TenantType tenantType) {
		LOG.info("TENANT ID........" + tenantId);

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(sr) ";
		}

		hql += " from ScoreRating sr ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch sr.createdBy as cb left outer join fetch sr.modifiedBy as mb ";
		}

		if (TenantType.BUYER == tenantType) {
			hql += " where sr.buyer.id = :tenantId ";
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and sr.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy.loginId") || cp.getData().equals("createdBy.loginId") || cp.getData().equals("description")) {
					hql += " and upper(sr." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and sr." + cp.getData() + " like (:" + cp.getData() + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and sr.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			hql += " order by ";
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " sr." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " sr.createdDate desc ";
			}
		}
//		else {
//			hql += " order by sr.createdDate desc ";
//		}
		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("modifiedBy.loginId") || cp.getData().equals("createdBy.loginId") || cp.getData().equals("description")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				} else {
					query.setParameter(cp.getData(), Integer.parseInt(cp.getSearch().getValue()));
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", Status.ACTIVE);
		}
		return query;
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from ScoreRating a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public long findTotalScoreRatingCountForCsv(String loggedInUserTenantId, TenantType tenantType) {
		String hql = "select count(sr) from ScoreRating sr ";

		if (TenantType.BUYER == tenantType) {
			hql += " where sr.buyer.id = :tenantId ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoreRating> getAllScoreRatingForCsv(String loggedInUserTenantId, TenantType tenantType, int pAGE_SIZE, int pageNo) {
		String sql = "from ScoreRating sr left outer join fetch sr.createdBy as cb left outer join fetch sr.modifiedBy as mb where 1=1 ";
		if (TenantType.BUYER == tenantType) {
			sql += " and sr.buyer.id =:tenantId ";
		}
		sql += " order by sr.createdDate desc";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pageNo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ScoreRating getScoreRatingForScoreAndTenant(String tenantId, BigDecimal overallScore) {
		LOG.info("Getting Score Rating for TenantId: " + tenantId + " and Overall Score " + overallScore);
		String sql = "from ScoreRating sr where :overallScore between sr.minScore and sr.maxScore and sr.buyer.id = :tenantId and sr.status = :status";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("overallScore", overallScore.intValue());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		List<ScoreRating> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(String loggedInUserTenantId, ScoreRating scoreRating) {
		String sql = "from ScoreRating sr where sr.minScore = :minScore  and sr.maxScore= :maxScore and sr.buyer.id = :tenantId ";
		if (StringUtils.checkString(scoreRating.getId()).length() > 0) {
			sql += " and sr.id <> :id ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("minScore", scoreRating.getMinScore());
		query.setParameter("maxScore", scoreRating.getMaxScore());
		// query.setParameter("status", scoreRating.getStatus());and sr.status = :status
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(scoreRating.getId()).length() > 0) {
			query.setParameter("id", scoreRating.getId());
		}
		List<ScoreRating> sr = query.getResultList();
		return CollectionUtil.isNotEmpty(sr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkScoreRangeOverlap(String loggedInUserTenantId, ScoreRating scoreRating) {
		LOG.info(" loggedInUserTenantId " + loggedInUserTenantId);
		String sql = "from ScoreRating sr where sr.buyer.id = :tenantId and sr.status = :status ";
		sql += " and ((:minScore between sr.minScore and sr.maxScore or :maxScore between sr.minScore and sr.maxScore) or (sr.minScore between :minScore and :maxScore or sr.maxScore between :minScore and :maxScore))";
		if (StringUtils.checkString(scoreRating.getId()).length() > 0) {
			sql += " and sr.id <> :id ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("minScore", scoreRating.getMinScore());
		query.setParameter("maxScore", scoreRating.getMaxScore());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		if (StringUtils.checkString(scoreRating.getId()).length() > 0) {
			query.setParameter("id", scoreRating.getId());
		}
		List<ScoreRating> sr = query.getResultList();
		return CollectionUtil.isNotEmpty(sr);
	}

}
