package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RegionsDao;
import com.privasia.procurehere.core.entity.Regions;

@Repository("RegionsDao")
public class RegionsDaoImpl extends GenericDaoImpl<Regions, String> implements RegionsDao {

	@Resource
	MessageSource messagesource;

	@SuppressWarnings("unchecked")
	@Override
	public List<Regions> findAllActiveRegions() {
		final Query query = getEntityManager().createQuery("from Regions a  where a.active =:active order by a.regionName");
		query.setParameter("active", true);
		return query.getResultList();
	}

	@Override
	public List<Regions> getRegionsByName(String regionsName) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Regions> findAll() {

		StringBuilder hsql = new StringBuilder("from Regions as r inner join fetch r.country as c order by r.regionName");
		final Query query = getEntityManager().createQuery(hsql.toString());

		return query.getResultList();
	}
 
}
