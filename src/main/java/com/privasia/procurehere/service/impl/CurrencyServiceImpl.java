package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.CurrencyPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.CurrencyService;

@Service
@Transactional(readOnly = true)
public class CurrencyServiceImpl implements CurrencyService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	public String createCurrency(Currency currency) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + currency.getCurrencyName() + "' Currency settings created ", currency.getCreatedBy().getTenantId(), currency.getCreatedBy(), new Date(), ModuleType.Currency);
		ownerAuditTrailDao.save(ownerAuditTrail);
		currency = currencyDao.saveOrUpdate(currency);
		return currency != null ? currency.getId() : null;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCurrency(Currency currency) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + currency.getCurrencyName() + "' Currency settings updated ", currency.getModifiedBy().getTenantId(), currency.getModifiedBy(), new Date(), ModuleType.Currency);
		ownerAuditTrailDao.save(ownerAuditTrail);
		currencyDao.saveOrUpdate(currency);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCurrency(Currency currency, User LoggedInUser) {
		currency = currencyDao.findById(currency.getId());
		String currencyName = currency.getCurrencyName();
		LOG.info("currency--" + currencyName);
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + currencyName + "' Currency settings deleted ", LoggedInUser.getTenantId(), LoggedInUser, new Date(), ModuleType.Currency);
		ownerAuditTrailDao.save(ownerAuditTrail);
		currencyDao.delete(currency);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Currency> getAllCurrency() {
		return currencyDao.findAll(Currency.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Currency getCurrency(String id) {
		Currency currency = currencyDao.findById(id);

		return currency;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExists(Currency currency) {
		return currencyDao.isExists(currency);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CurrencyPojo> getAllCurrencyPojo(int start, int length, String order) {
		List<CurrencyPojo> returnList = new ArrayList<CurrencyPojo>();

		List<Currency> list = currencyDao.findCurrency(start, length, order);
		if (CollectionUtil.isNotEmpty(list)) {
			for (Currency currency : list) {
				if (currency.getCreatedBy() != null)
					currency.getCreatedBy().getLoginId();
				if (currency.getModifiedBy() != null)
					currency.getModifiedBy().getLoginId();

				CurrencyPojo cp = new CurrencyPojo(currency);
				LOG.info("inside list service impl:name" + cp.getCurrencyName());
				returnList.add(cp);
			}

		}
		return returnList;

	}

	@Override
	public List<Currency> findAllCurrencyList(TableDataInput tableParams) {
		return currencyDao.findAllCurrencyList(tableParams);
	}

	@Override
	public long findTotalFilteredCurrencyList(TableDataInput tableParams) {
		return currencyDao.findTotalFilteredCurrencyList(tableParams);
	}

	@Override
	public long findTotalCurrencyList() {
		return currencyDao.findTotalCurrencyList();
	}

	@Override
	public List<Currency> getAllActiveCurrencies() {
		return currencyDao.getAllActiveCurrencies();
	}

	@Override
	public List<Currency> getAllActiveCurrenciesForMobileApi() {
		return currencyDao.getAllActiveCurrenciesForMobileApi();
	}

	@Override
	public List<Currency> getAllActiveCurrencyCode() {
		return currencyDao.getAllActiveCurrencyCode();
	}

	@Override
	public List<Currency> getlActiveCurrencies() {
		return currencyDao.getlActiveCurrencies();
	}

	@Override
	public List<Currency> fetchAllActiveCurrencies(String search) {
		List<Currency> list = currencyDao.fetchAllActiveCurrencies(search);
		long count = currencyDao.countConstructQueryToFetchCurrencies();
		LOG.info("Count: " + count + " List size: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			if (list != null && count > list.size()) {
				Currency more = new Currency();
				more.setCurrencyName("+" + (count - list.size()) + " more. Continue typing to find match...");
				list.add(more);
			}
		}
		return list;
	}

}