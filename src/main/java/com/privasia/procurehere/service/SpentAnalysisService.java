package com.privasia.procurehere.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForBudget;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForInternalAndExternal;

public interface SpentAnalysisService {

	List<SpentAnalysisPojo> getPoDataByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataBasedOnStatus(int month, int year) throws ParseException;

	Map<String, List<SpentAnalysisPojo>> getPoDataForPreviousYears(int year);

	List<SpentAnalysisPojo> getPoDataBasedOnCategory(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoValueByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoValueBasedOnCategory(int month, int year) throws ParseException;

	List<SpentAnalysisPojoForInternalAndExternal> findPoDataForInternalAndExternalCo(int month, int year) throws ParseException;

	List<SpentAnalysisPojoForInternalAndExternal> findPoCountForInternalAndExternalCo(int month, int year) throws ParseException;

	Map<String, List<SpentAnalysisPojo>> getPoDataForNonSubsidiaryPreviousYears(int year);

	Map<String, List<SpentAnalysisPojo>> getPoDataForSubsidiaryPreviousYears(int year);

	List<SpentAnalysisPojo> getPoValueForNonSubsidiaryByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoValueForSubsidiaryByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataForNonSubsidiaryByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataForSubsidiaryByMonth(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataForSubsidiaryBasedOnStatus(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataForNonsubsidiaryBasedOnStatus(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataBasedOnCategoryForSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoDataBasedOnCategoryForNonsubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoValueBasedOnCategoryForSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getPoValueBasedOnCategoryForNonSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojoForBudget> getBudgetValue(String costCenter, String businessUnit, int month, int year);

	List<SpentAnalysisPojo> getTopSuppliersByVolume(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> getTopSuppliersByValue(int month, int year) throws ParseException;

}
