package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForInternalAndExternal;

public interface PoDaoForSpentAnalysis extends GenericDao<Po, String> {

	BigDecimal poCountForMonth(String startDate, String endDate);

	BigDecimal poValueForMonth(String startDate, String endDate);

	List<SpentAnalysisPojo> poCountBasedOnStatus(int month, int year) throws ParseException;

	List<SpentAnalysisPojoForInternalAndExternal> findPoDataForInternalAndExternalCo(int month, int year) throws ParseException;

	List<SpentAnalysisPojoForInternalAndExternal> findPoCountForInternalAndExternalCo(int month, int year) throws ParseException;

	BigDecimal poCountForMonthForSubsidiary(String startDate, String endDate);

	BigDecimal poCountForMonthForNonSubsidiary(String startDate, String endDate);

	BigDecimal poValueForMonthForSubsidiary(String startDate, String endDate);

	BigDecimal poValueForMonthForNonSubsidiary(String startDate, String endDate);

	List<SpentAnalysisPojo> poCountBasedOnStatusForSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> poCountBasedOnStatusForNonSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getTopSuppliersByVolume(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getTopSuppliersByValue(int month, int year) throws ParseException;

}
