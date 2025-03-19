package com.privasia.procurehere.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.privasia.procurehere.core.dao.BudgetDao;
import com.privasia.procurehere.core.dao.PoDaoForSpentAnalysis;
import com.privasia.procurehere.core.dao.PrItemDao;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForBudget;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForInternalAndExternal;
import com.privasia.procurehere.service.SpentAnalysisService;

@Service
public class SpentAnalysisServiceImpl implements SpentAnalysisService {

	@Autowired
	PoDaoForSpentAnalysis poDao;

	@Autowired
	PrItemDao prItemDao;

	@Autowired
	BudgetDao budgetDaoImpl;

	@Override
	public List<SpentAnalysisPojo> getPoDataByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public Map<String, List<SpentAnalysisPojo>> getPoDataForPreviousYears(int year) {

		Map<String, List<SpentAnalysisPojo>> dataMap = new HashMap<>();
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR, year);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

		// For Current Year
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-1)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-2)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -2);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-3)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -3);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-4)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -4);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		return dataMap;
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataBasedOnStatus(int month, int year) throws ParseException {
		return poDao.poCountBasedOnStatus(month, year);
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataBasedOnCategory(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemForSpentAnalysis(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poValueForMonth(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueBasedOnCategory(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemValueForSpentAnalysis(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojoForInternalAndExternal> findPoDataForInternalAndExternalCo(int month, int year) throws ParseException {
		List<SpentAnalysisPojoForInternalAndExternal> list = poDao.findPoDataForInternalAndExternalCo(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojoForInternalAndExternal> findPoCountForInternalAndExternalCo(int month, int year) throws ParseException {
		List<SpentAnalysisPojoForInternalAndExternal> list = poDao.findPoCountForInternalAndExternalCo(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataForSubsidiaryByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataForNonSubsidiaryByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueForSubsidiaryByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poValueForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueForNonSubsidiaryByMonth(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		for (int i = 0; i <= month; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poValueForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			pojo.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			pojo.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(pojo);

		}
		return dataList;
	}

	@Override
	public Map<String, List<SpentAnalysisPojo>> getPoDataForSubsidiaryPreviousYears(int year) {
		Map<String, List<SpentAnalysisPojo>> dataMap = new HashMap<>();
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR, year);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

		// For Current Year
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-1)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-2)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -2);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-3)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -3);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-4)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -4);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		return dataMap;
	}

	@Override
	public Map<String, List<SpentAnalysisPojo>> getPoDataForNonSubsidiaryPreviousYears(int year) {
		Map<String, List<SpentAnalysisPojo>> dataMap = new HashMap<>();
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR, year);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

		// For Current Year
		List<SpentAnalysisPojo> dataList = new ArrayList<SpentAnalysisPojo>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-1)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-2)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);

		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -2);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-3)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -3);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		// For Previous year(-4)

		dataList = new ArrayList<SpentAnalysisPojo>();
		calender.add(Calendar.YEAR, -1);
		for (int i = 0; i < 12; i++) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			SpentAnalysisPojo pojo = new SpentAnalysisPojo();
			cal.add(Calendar.YEAR, -4);
			cal.set(Calendar.MONTH, i);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int res = cal.getActualMaximum(Calendar.DATE);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";
			pojo.setValue(poDao.poCountForMonthForNonSubsidiary(startDate, endDate));
			pojo.setName(new SimpleDateFormat("MMMM").format(cal.getTime()));
			dataList.add(pojo);
		}

		dataMap.put(sdf.format(calender.getTime()), dataList);

		return dataMap;
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataForSubsidiaryBasedOnStatus(int month, int year) throws ParseException {
		return poDao.poCountBasedOnStatusForSubsidiary(month, year);
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataForNonsubsidiaryBasedOnStatus(int month, int year) throws ParseException {
		return poDao.poCountBasedOnStatusForNonSubsidiary(month, year);
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataBasedOnCategoryForSubsidiary(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemForSpentAnalysisForSubsidiary(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojo> getPoDataBasedOnCategoryForNonsubsidiary(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemForSpentAnalysisForNonSubsidiary(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueBasedOnCategoryForSubsidiary(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemValueForSpentAnalysisForSubsidiary(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojo> getPoValueBasedOnCategoryForNonSubsidiary(int month, int year) throws ParseException {
		List<SpentAnalysisPojo> list = prItemDao.findPrItemValueForSpentAnalysisForNonSubsidiary(month, year);
		return list;
	}

	@Override
	public List<SpentAnalysisPojoForBudget> getBudgetValue(String costCenter, String businessUnit, int month, int year) {
		return budgetDaoImpl.getBudgetBasedOnCostCenterAndBusinessUnit((costCenter.length() > 0 && !costCenter.equals("null")) ? costCenter : null, (!businessUnit.equals("null") && businessUnit.length() > 0) ? businessUnit : null, month, year);
	}

	@Override
	public List<SpentAnalysisPojo> getTopSuppliersByVolume(int month, int year) throws ParseException {
		return poDao.getTopSuppliersByVolume(month, year);
	}

	@Override
	public List<SpentAnalysisPojo> getTopSuppliersByValue(int month, int year) throws ParseException {
		return poDao.getTopSuppliersByValue(month, year);
	}

}
