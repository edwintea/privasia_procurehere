package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoDaoForSpentAnalysis;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForInternalAndExternal;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;

@Repository
public class PoDaoForSpentAnalysisImpl extends GenericDaoImpl<Po, String> implements PoDaoForSpentAnalysis {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Override
	public BigDecimal poCountForMonth(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select count(*) from Po p where p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO Volume by month for start date : " + startDate + " and end date : " + endDate);
		return new BigDecimal(((long) query.getSingleResult())).setScale(0, RoundingMode.UP);
	}

	@Override
	public BigDecimal poCountForMonthForSubsidiary(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select count(*) from Po p where p.supplier.subsidiary =:subsidiary AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", Boolean.TRUE);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO Volume by month for subsidiary for start date : " + startDate + " and end date : " + endDate);
		return new BigDecimal(((long) query.getSingleResult())).setScale(0, RoundingMode.UP);
	}

	@Override
	public BigDecimal poCountForMonthForNonSubsidiary(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select count(*) from Po p left outer join p.supplier sup where (sup.subsidiary =:subsidiary or sup.subsidiary is null) AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", Boolean.FALSE);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO Volume by month for non-subsidiary for start date : " + startDate + " and end date : " + endDate);
		return new BigDecimal(((long) query.getSingleResult())).setScale(0, RoundingMode.UP);
	}

	@Override
	public BigDecimal poValueForMonth(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select sum(p.grandTotal) from Po p where p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO Value by month for start date : " + startDate + " and end date : " + endDate);
		return ((BigDecimal) query.getSingleResult()) != null ? ((BigDecimal) query.getSingleResult()) : BigDecimal.ZERO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> poCountBasedOnStatus(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PO Volume by status for start date : " + startDate + " and end date : " + endDate);

		StringBuilder hsql = new StringBuilder("select p.status, count(p) from Po p where p.buyer.id = :loggedInId and p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') group by p.status order by count(p) desc");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();
		List<Object[]> records = query.getResultList();

		if (CollectionUtil.isEmpty(records)) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName("READY");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ORDERED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ACCEPTED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("DECLINED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("CANCELLED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			return dataList;
		}

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();

			if (PoStatus.ACCEPTED.equals(result[0])) {
				data.setName("ACCEPTED");
			}

			if (PoStatus.CANCELLED.equals(result[0])) {
				data.setName("CANCELLED");
			}

			if (PoStatus.DECLINED.equals(result[0])) {
				data.setName("DECLINED");
			}

			if (PoStatus.ORDERED.equals(result[0])) {
				data.setName("ORDERED");
			}

			if (PoStatus.READY.equals(result[0])) {
				data.setName("READY");
			}

			data.setValue(new BigDecimal(((long) result[1])).setScale(0, RoundingMode.UP));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		SpentAnalysisPojo data = new SpentAnalysisPojo();
		dataList.add(data);

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojoForInternalAndExternal> findPoDataForInternalAndExternalCo(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PO value for internal and external co for start date : " + startDate + " and end date : " + endDate);

		StringBuilder hsql = new StringBuilder("SELECT SUM(GRAND_TOTAL), subsidiary, DISPLAY_NAME FROM ( SELECT pp.GRAND_TOTAL, CASE WHEN pfs.SUBSIDIARY = 1 THEN 'INTERNAL' ELSE 'EXTERNAL' END AS subsidiary, pbu.DISPLAY_NAME, pp.BUSINESS_UNIT_ID FROM PROC_PO pp LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER pfs ON pp.PO_SUPPLIER_ID = pfs.FAV_SUPPLIER_ID JOIN PROC_BUSINESS_UNIT pbu ON pbu.ID = pp.BUSINESS_UNIT_ID WHERE pp.BUSINESS_UNIT_ID IN ( SELECT BU FROM ( SELECT pp.BUSINESS_UNIT_ID AS BU FROM PROC_PO pp WHERE pp.BUYER_ID =:buyerId GROUP BY pp.BUSINESS_UNIT_ID ORDER BY SUM(pp.GRAND_TOTAL ) DESC ) AS subquery LIMIT 9 ) AND pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') ) AS main_query GROUP BY BUSINESS_UNIT_ID, DISPLAY_NAME, subsidiary ORDER BY BUSINESS_UNIT_ID");
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		List<Object[]> records = query.getResultList();
		List<SpentAnalysisPojoForInternalAndExternal> dataList = new ArrayList<>();
		for (Object[] result : records) {
			SpentAnalysisPojoForInternalAndExternal data = new SpentAnalysisPojoForInternalAndExternal();
			data.setName((String) result[1]);
			data.setValue((new BigDecimal(result[0].toString())));
			data.setDisplayName((String) result[2]);
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}
		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojoForInternalAndExternal> findPoCountForInternalAndExternalCo(int month, int year) throws ParseException {

		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			String startDate = formatter.format(cal.getTime()) + " 00:00:00";
			cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.YEAR, year);
			int res = cal.getActualMaximum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, res);
			String endDate = formatter.format(cal.getTime()) + " 23:59:59";

			LOG.info("PO Volume for internal and external co for start date : " + startDate + " and end date : " + endDate);

			StringBuilder hsql = new StringBuilder("SELECT COUNT(*) AS VOLUME, subsidiary, DISPLAY_NAME FROM ( SELECT CASE WHEN pfs.SUBSIDIARY = 1 THEN 'INTERNAL' ELSE 'EXTERNAL' END AS subsidiary, pbu.DISPLAY_NAME, pp.BUSINESS_UNIT_ID FROM PROC_PO pp LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER pfs ON pp.PO_SUPPLIER_ID = pfs.FAV_SUPPLIER_ID JOIN PROC_BUSINESS_UNIT pbu ON pbu.ID = pp.BUSINESS_UNIT_ID WHERE pp.BUSINESS_UNIT_ID IN ( SELECT BU FROM ( SELECT pp.BUSINESS_UNIT_ID AS BU FROM PROC_PO pp WHERE pp.BUYER_ID =:buyerId GROUP BY pp.BUSINESS_UNIT_ID ORDER BY SUM(pp.GRAND_TOTAL ) DESC ) AS subquery LIMIT 9 ) AND pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') ) AS main_query GROUP BY BUSINESS_UNIT_ID, DISPLAY_NAME, subsidiary ORDER BY BUSINESS_UNIT_ID");
			Query query = getEntityManager().createNativeQuery(hsql.toString());
			query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			List<Object[]> records = query.getResultList();
			List<SpentAnalysisPojoForInternalAndExternal> dataList = new ArrayList<>();
			for (Object[] result : records) {
				SpentAnalysisPojoForInternalAndExternal data = new SpentAnalysisPojoForInternalAndExternal();
				data.setName((String) result[1]);
				data.setValue(new BigDecimal(result[0].toString()));
				data.setDisplayName((String) result[2]);
				data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
				data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public BigDecimal poValueForMonthForSubsidiary(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select sum(p.grandTotal) from Po p where p.supplier.subsidiary =:subsidiary AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", Boolean.TRUE);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO value for subsidiary for start date : " + startDate + " and end date : " + endDate);
		return ((BigDecimal) query.getSingleResult()) != null ? ((BigDecimal) query.getSingleResult()) : BigDecimal.ZERO;
	}

	@Override
	public BigDecimal poValueForMonthForNonSubsidiary(String startDate, String endDate) {
		StringBuilder hsql = new StringBuilder("select sum(p.grandTotal) from Po p left outer join p.supplier sup where (sup.subsidiary =:subsidiary or sup.subsidiary is null) AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND p.buyer.id = :loggedInId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setParameter("subsidiary", Boolean.FALSE);
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		LOG.info("PO value for non-subsidiary for start date : " + startDate + " and end date : " + endDate);
		return ((BigDecimal) query.getSingleResult()) != null ? ((BigDecimal) query.getSingleResult()) : BigDecimal.ZERO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> poCountBasedOnStatusForSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PO Volume by status for subsidiary for start date : " + startDate + " and end date : " + endDate);

		StringBuilder hsql = new StringBuilder("select p.status, count(p) from Po p where p.supplier.subsidiary =:subsidiary AND p.buyer.id = :loggedInId AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') group by p.status order by count(p) desc");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("subsidiary", Boolean.TRUE);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		List<SpentAnalysisPojo> dataList = new ArrayList<>();
		List<Object[]> records = query.getResultList();

		if (CollectionUtil.isEmpty(records)) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName("READY");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ORDERED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ACCEPTED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("DECLINED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("CANCELLED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			return dataList;
		}

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();

			if (PoStatus.ACCEPTED.equals(result[0])) {
				data.setName("ACCEPTED");
			}

			if (PoStatus.CANCELLED.equals(result[0])) {
				data.setName("CANCELLED");
			}

			if (PoStatus.DECLINED.equals(result[0])) {
				data.setName("DECLINED");
			}

			if (PoStatus.ORDERED.equals(result[0])) {
				data.setName("ORDERED");
			}

			if (PoStatus.READY.equals(result[0])) {
				data.setName("READY");
			}
			data.setValue(new BigDecimal(((long) result[1])).setScale(0, RoundingMode.UP));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		SpentAnalysisPojo data = new SpentAnalysisPojo();
		dataList.add(data);

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> poCountBasedOnStatusForNonSubsidiary(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("PO Volume by status for non-subsidiary for start date : " + startDate + " and end date : " + endDate);

		StringBuilder hsql = new StringBuilder("select p.status, count(p) from Po p left outer join p.supplier sup where (sup.subsidiary =:subsidiary or sup.subsidiary is null) AND p.buyer.id = :loggedInId AND p.createdDate BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') group by p.status order by count(p) desc");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("loggedInId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("subsidiary", Boolean.FALSE);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		List<SpentAnalysisPojo> dataList = new ArrayList<>();
		List<Object[]> records = query.getResultList();

		if (CollectionUtil.isEmpty(records)) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName("READY");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ORDERED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("ACCEPTED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("DECLINED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			data = new SpentAnalysisPojo();
			data.setName("CANCELLED");
			data.setValue(BigDecimal.ZERO);
			dataList.add(data);

			return dataList;
		}

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();

			if (PoStatus.ACCEPTED.equals(result[0])) {
				data.setName("ACCEPTED");
			}

			if (PoStatus.CANCELLED.equals(result[0])) {
				data.setName("CANCELLED");
			}

			if (PoStatus.DECLINED.equals(result[0])) {
				data.setName("DECLINED");
			}

			if (PoStatus.ORDERED.equals(result[0])) {
				data.setName("ORDERED");
			}

			if (PoStatus.READY.equals(result[0])) {
				data.setName("READY");
			}
			data.setValue(new BigDecimal(((long) result[1])).setScale(0, RoundingMode.UP));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}

		SpentAnalysisPojo data = new SpentAnalysisPojo();
		dataList.add(data);

		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> getTopSuppliersByVolume(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("Top suppliers by volume for start date : " + startDate + " and end date : " + endDate);

//		StringBuilder hsql = new StringBuilder("SELECT pp.SUPPLIER_NAME, COUNT(*) FROM PROC_PO pp WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId GROUP BY pp.SUPPLIER_NAME ORDER BY COUNT(*) DESC");
		
		StringBuilder hsql = new StringBuilder("SELECT ps.COMPANY_NAME, COUNT(*) FROM PROC_PO pp LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON fs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER ps ON ps.SUPPLIER_ID = fs.SUPPLIER_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId AND pp.PO_SUPPLIER_ID IS NOT NULL GROUP BY ps.COMPANY_NAME ORDER BY COUNT(*) DESC");
		
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();
		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue(new BigDecimal(result[1].toString()));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}
		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpentAnalysisPojo> getTopSuppliersByValue(int month, int year) throws ParseException {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String startDate = formatter.format(cal.getTime()) + " 00:00:00";
		cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		int res = cal.getActualMaximum(Calendar.DATE);
		cal.set(Calendar.DAY_OF_MONTH, res);
		String endDate = formatter.format(cal.getTime()) + " 23:59:59";

		LOG.info("Top suppliers by value for start date : " + startDate + " and end date : " + endDate);

//		StringBuilder hsql = new StringBuilder("SELECT pp.SUPPLIER_NAME, SUM(pp.GRAND_TOTAL) FROM PROC_PO pp WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId GROUP BY pp.SUPPLIER_NAME ORDER BY SUM(pp.GRAND_TOTAL) DESC");
	
		StringBuilder hsql = new StringBuilder("SELECT ps.COMPANY_NAME , SUM(pp.GRAND_TOTAL) FROM PROC_PO pp LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON fs.FAV_SUPPLIER_ID = pp.PO_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER ps ON ps.SUPPLIER_ID = fs.SUPPLIER_ID WHERE pp.PO_CREATED_DATE BETWEEN TO_DATE(:startDate , 'YYYY-MM-DD hh24:mi:ss') AND TO_DATE(:endDate , 'YYYY-MM-DD hh24:mi:ss') AND pp.BUYER_ID =:buyerId AND pp.PO_SUPPLIER_ID IS NOT NULL GROUP BY ps.COMPANY_NAME ORDER BY SUM(pp.GRAND_TOTAL) DESC");
		
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.setParameter("buyerId", SecurityLibrary.getLoggedInUser().getBuyer().getId());
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		query.setMaxResults(10);
		List<SpentAnalysisPojo> dataList = new ArrayList<>();
		List<Object[]> records = query.getResultList();

		for (Object[] result : records) {
			SpentAnalysisPojo data = new SpentAnalysisPojo();
			data.setName((String) result[0]);
			data.setValue(new BigDecimal(result[1].toString()));
			data.setStartDate(new SimpleDateFormat("MMMM YYYY").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(startDate))));
			data.setEndDate(new SimpleDateFormat("MMMM yyyy").format((new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(endDate))));
			dataList.add(data);
		}
		return dataList;
	}

}
