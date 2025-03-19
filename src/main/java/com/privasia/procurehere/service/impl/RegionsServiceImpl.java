package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.dao.RegionsDao;
import com.privasia.procurehere.core.entity.Regions;
import com.privasia.procurehere.service.RegionsService;

@Service
@Transactional(readOnly = true)
public class RegionsServiceImpl implements RegionsService {

	Logger log = LogManager.getLogger(RecaptchaServiceImpl.class);

	@Autowired
	RegionsDao regionsDao;

	@Autowired
	CountryDao countryDao;

	@Override
	public List<Regions> findAllActiveRegions() {
		return regionsDao.findAllActiveRegions();
	}

	@Override
	@Transactional(readOnly = false)
	public void createRegions(Regions regions) {
		regionsDao.saveOrUpdate(regions);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateRegions(Regions regions) {
		regionsDao.update(regions);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRegions(Regions regions) {
		regionsDao.delete(regions);

	}

	@Override
	public Regions searchRegionsById(String id) {
		return regionsDao.findById(id);
	}

	@Override
	public List<Regions> getAllRegions() {
		return regionsDao.findAll();
	}

	@Override
	public boolean isExist(String regionsName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Regions getRegionsById(String regionsId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Regions getRegionsid(String id) {
		Regions regions = regionsDao.findById(id);
		if (regions.getCountry() != null) {
			regions.getCountry().getCountryCode();
		}
		return regions;

	}
	

}
