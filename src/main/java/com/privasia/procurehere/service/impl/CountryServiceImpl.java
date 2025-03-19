/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.CountryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.CountryService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {
	Logger log = LogManager.getLogger(CountryServiceImpl.class);

	@Autowired(required = true)
	CountryDao countryDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Override
	public List<Country> findAllActiveCountries() {
		List<Country> list = countryDao.findAllActiveCountries();
		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				if (CollectionUtil.isNotEmpty(country.getStates())) {
					for (State state : country.getStates()) {
						state.getStateName();
					}
				}
			}
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String createCountry(Country country) {
		log.info("CODE : " + country.getCountryCode() + " Name : " + country.getCountryName());
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + country.getCountryName() + "' Country settings created", country.getCreatedBy().getTenantId(), country.getCreatedBy(), new Date(), ModuleType.Country);
		ownerAuditTrailDao.save(ownerAuditTrail);
		country = countryDao.saveOrUpdate(country);
		return (country != null ? country.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCountry(Country country) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + country.getCountryName() + "' Country settings updated", country.getModifiedBy().getTenantId(), country.getModifiedBy(), new Date(), ModuleType.Country);
		ownerAuditTrailDao.save(ownerAuditTrail);
		countryDao.update(country);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCountry(Country country) {
		String countryName = country.getCountryName();
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + countryName + "' Country settings deleted ", country.getModifiedBy().getTenantId(), country.getModifiedBy(), new Date(), ModuleType.Country);
		ownerAuditTrailDao.save(ownerAuditTrail);
		countryDao.delete(country);

	}

	@Override
	public Country searchCountryById(String id) {
		return countryDao.findById(id);
	}

	/*
	 * @Override public boolean isExist(String countryCode) { return countryDao.findByProperty("countryCode",
	 * countryCode) != null ? true : false; }
	 */

	/*
	 * /(non-Javadoc) new isexists
	 */

	@Override
	public boolean isExists(Country country) {
		return countryDao.isExists(country);
	}

	@Override
	public List<Country> getAllCountries() {
		return countryDao.getAllCountries();
	}

	@Override
	public List<Country> getAllCountriesOrderByCountryCode() {
		return countryDao.getAllCountriesOrderByCountryCode();
	}

	@Override
	public List<Country> getActiveCountriesForIntegration() {
		List<Country> list = countryDao.getAllCountries();
		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				country.setCreatedBy(null);
				country.setCreatedDate(null);
				country.setModifiedBy(null);
				country.setModifiedDate(null);
			}
		}
		return list;
	}

	@Override
	public Country getCountryById(String countryId) {
		return countryDao.findById(countryId);
	}

	@Override
	public Country getCountryByCode(String countryCode) {
		return countryDao.findByProperty("countryCode", countryCode);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CountryPojo> findCountriesPojo(int start, int length, String order) {
		List<CountryPojo> returnList = new ArrayList<CountryPojo>();
		List<Country> list = countryDao.findCountries(start, length, order);
		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				if (country.getCreatedBy() != null)
					country.getCreatedBy().getLoginId();
				if (country.getModifiedBy() != null)
					country.getModifiedBy().getLoginId();
				CountryPojo cp = new CountryPojo(country);
				returnList.add(cp);
			}
		}

		return returnList;

	}

	@Override
	public List<Country> loadById(String countryId) {
		return countryDao.loadById(countryId);
	}

	@Override
	public List<Country> searchCountiesByNameOrCode(String searchValue) {
		return countryDao.searchCountiesByNameOrCode(searchValue);
	}

	@Override
	public List<Country> findAllCountryList(TableDataInput tableParams) {
		return countryDao.findAllCountryList(tableParams);
	}

	@Override
	public long findTotalFilteredCountryList(TableDataInput tableParams) {
		return countryDao.findTotalFilteredCountryList(tableParams);
	}

	@Override
	public long findTotalCountryList() {
		return countryDao.findTotalCountryList();
	}

	@Override
	public Country getCountryWithStatesByCode(String code) {
		return countryDao.getCountryWithStatesByCode(code);
	}

	@Override
	public List<Country> getActiveCountries() {
		return countryDao.getActiveCountries();
	}

}
