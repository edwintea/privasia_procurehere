package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Regions;

public interface RegionsService {

	
List<Regions> findAllActiveRegions();
	
	public void createRegions(Regions regions);
	
	public void updateRegions(Regions regions);
	
	public void deleteRegions(Regions regions); 
	
	Regions searchRegionsById(String id);
	
	List<Regions> getAllRegions();
	
	/**
	 * 
	 * @param countryCode
	 * @return
	 */
	boolean isExist(String regionsName);

	/**
	 * 
	 * @param countryId
	 * @return
	 */
	Regions getRegionsById(String regionsId);
	
	
	/**
	 * 
	 * @param countryName
	 * @return
	 */
	public Regions getRegionsid(String id);
	
	/**
	 * 
	 * @param
	 * @return
	 */
	
}
