package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormCqOptionDao;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sarang
 */
@Repository
public class SourcingFormCqOptionDaoImpl extends GenericDaoImpl<SourcingFormCqOption, String> implements SourcingFormCqOptionDao {
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormCqOption> findCqItemOptionForCqItemId(String searchValue, String cqItemId) {
		String hql = "";
		hql += " select distinct new SourcingFormCqOption(so.id, so.value, so.order) from SourcingFormCqOption so left outer join so.formCqItem tci where tci.id =:cqItemId";

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>if searchValue : "+searchValue);
			hql += " and upper(so.value) like (:searchValue) ";
		}
		hql += " order by so.order ";
		
		LOG.info("!!!!!!!!! hql : "+hql);
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("cqItemId", cqItemId);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		
		List<SourcingFormCqOption> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return new ArrayList<SourcingFormCqOption>();
		}
	}

	@Override
	public long getCountOfAllOptionsForCqItem(String cqItemId) {
		Query query = getEntityManager().createQuery(" select count (so) from SourcingFormCqOption so left outer join so.formCqItem tci where tci.id =:cqItemId ");
		query.setParameter("cqItemId", cqItemId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormCqOption> findCqItemOptionForCqItemId(String cqItemId) {
		LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>> cqItemId : "+cqItemId);
		String hql = "";
		hql += " select distinct new SourcingFormCqOption(so.id, so.value, so.order) from SourcingFormCqOption so left outer join so.formCqItem tci where tci.id =:cqItemId order by so.order ";

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("cqItemId", cqItemId);
		query.setMaxResults(10);
		
		List<SourcingFormCqOption> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return new ArrayList<SourcingFormCqOption>();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormCqOption findByTemplateCqItemIdAndOptionOrder(String templtCqItemId, Integer order) {
		String hql = "";
		hql += " select so from SourcingFormCqOption so left outer join so.formCqItem tci where tci.id =:templtCqItemId and so.order =:optOrder order by so.order ";

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templtCqItemId", templtCqItemId);
		query.setParameter("optOrder", order);
		query.setMaxResults(10);
		
		List<SourcingFormCqOption> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return new SourcingFormCqOption();
		}
	}
	
}
