package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.CurrencyPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface CurrencyService {

	/**
	 * @param currency
	 * @return
	 */
	String createCurrency(Currency currency);

	/**
	 * @param currency
	 */
	void updateCurrency(Currency currency);

	/**
	 * @param currency
	 */
	void deleteCurrency(Currency currency, User loggedInUser);

	/**
	 * @return
	 */
	List<Currency> getAllCurrency();

	/**
	 * @param id
	 * @return
	 */
	Currency getCurrency(String id);

	/**
	 * @param currency
	 * @return
	 */
	boolean isExists(Currency currency);

	/**
	 * @param start
	 * @param length
	 * @param order
	 * @return
	 */
	List<CurrencyPojo> getAllCurrencyPojo(int start, int length, String order);

	/**
	 * @param input
	 * @return
	 */
	List<Currency> findAllCurrencyList(TableDataInput tableParams);

	/**
	 * @param input
	 * @return
	 */
	long findTotalFilteredCurrencyList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalCurrencyList();

	List<Currency> getAllActiveCurrencies();

	/**
	 * @return
	 */
	List<Currency> getAllActiveCurrenciesForMobileApi();

	List<Currency> getAllActiveCurrencyCode();

	/**
	 * @return
	 */
	List<Currency> getlActiveCurrencies();

	/**
	 * @param search
	 * @return
	 */
	List<Currency> fetchAllActiveCurrencies(String search);

}
