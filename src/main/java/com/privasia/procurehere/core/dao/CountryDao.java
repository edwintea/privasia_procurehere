package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Arc
 */
public interface CountryDao extends GenericDao<Country, String> {

	/**
	 * @return
	 */
	List<Country> findAllActiveCountries();

	/**
	 * @param searchValue
	 * @return
	 */
	List<Country> searchCountiesByNameOrCode(String searchValue);

	/**
	 * @param country
	 * @return
	 */
	boolean isExists(Country country);

	/**
	 * @return
	 */
	List<Country> getAllCountries();

	/**
	 * @param countryId
	 * @return
	 */
	List<Country> loadById(String countryId);

	/**
	 * @param list
	 * @param createdBy
	 */
	void loadCountryMasterData(List<Country> list, User createdBy);

	/**
	 * @param start
	 * @param length
	 * @param order TODO
	 * @return
	 */
	List<Country> findCountries(int start, int length, String order);

	/**
	 * @param tableParams
	 * @return
	 */
	List<Country> findAllCountryList(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredCountryList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalCountryList();

	List<Country> getAllCountriesOrderByCountryCode();

	/**
	 * @param code
	 * @return
	 */
	Country getCountryWithStatesByCode(String code);

	/**
	 * 
	 * @return
	 */
	List<Country> getActiveCountries();
}
