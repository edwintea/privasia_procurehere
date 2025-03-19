package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface CurrencyDao extends GenericDao<Currency, String> {

	/**
	 * @return
	 */
	public List<Currency> getAllCurrencies();

	/**
	 * @param currency
	 * @return
	 */
	public boolean isExists(Currency currency);

	/**
	 * @param list
	 * @param createdByUser
	 */
	public void loadCurrencyMasterData(List<Currency> list, User createdByUser);

	/**
	 * @param start
	 * @param length
	 * @param order
	 * @return
	 */
	List<Currency> findCurrency(int start, int length, String order);

	/**
	 * @param tableParams
	 * @return
	 */
	public List<Currency> findAllCurrencyList(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	public long findTotalFilteredCurrencyList(TableDataInput tableParams);

	/**
	 * @return
	 */
	public long findTotalCurrencyList();

	public List<Currency> getAllActiveCurrencies();

	/**
	 * @return
	 */
	public List<Currency> getAllActiveCurrenciesForMobileApi();

	/**
	 * @param currCode
	 * @return
	 */
	public Currency findByCurrencyCode(String currCode);

	public List<Currency> getAllActiveCurrencyCode();

	/**
	 * 
	 * @return
	 */
	List<Currency> getlActiveCurrencies();

	/**
	 * @param search
	 * @return
	 */
	public List<Currency> fetchAllActiveCurrencies(String search);

	/**
	 * @return
	 */
	public long countConstructQueryToFetchCurrencies();

}