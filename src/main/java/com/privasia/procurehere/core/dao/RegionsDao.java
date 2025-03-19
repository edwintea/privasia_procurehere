package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Regions;

/**
 * @author Arc
 */
public interface RegionsDao extends GenericDao<Regions, String> {

	List<Regions> findAllActiveRegions();
	
	List<Regions> getRegionsByName(String regionName);
	
	List<Regions> findAll();

}
