/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.CountryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Arc
 */
public interface CountryService {

	/**
	 * @return
	 */
	List<Country> findAllActiveCountries();

	/**
	 * @param country
	 */
	public String createCountry(Country country);

	/**
	 * @param country
	 */
	public void updateCountry(Country country);

	/**
	 * @param country
	 */
	public void deleteCountry(Country country);

	/**
	 * @param id
	 * @return
	 */
	Country searchCountryById(String id);

	/**
	 * @return
	 */
	List<Country> getAllCountries();

	/**
	 * @param countryCode
	 * @return
	 */
	/* boolean isExist(String countryCode); */

	boolean isExists(Country country);

	/**
	 * @param countryId
	 * @return
	 */
	Country getCountryById(String countryId);

	/**
	 * @param countryCode
	 * @return
	 */
	Country getCountryByCode(String countryCode);

	/*
	 * 
	 * */
	List<CountryPojo> findCountriesPojo(int start, int length, String order);

	/**
	 * @param countryId
	 * @return
	 */
	List<Country> loadById(String countryId);

	/**
	 * @param searchValue
	 * @return
	 */
	List<Country> searchCountiesByNameOrCode(String searchValue);

	/**
	 * @param input
	 * @return
	 */
	List<Country> findAllCountryList(TableDataInput tableParams);

	/**
	 * @param input
	 * @return
	 */
	long findTotalFilteredCountryList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalCountryList();

	/**
	 * @return
	 */
	List<Country> getActiveCountriesForIntegration();

	List<Country> getAllCountriesOrderByCountryCode();

	/**
	 * @param code
	 * @return
	 */
	Country getCountryWithStatesByCode(String code);

	/**
	 * @return
	 */
	List<Country> getActiveCountries();
}
