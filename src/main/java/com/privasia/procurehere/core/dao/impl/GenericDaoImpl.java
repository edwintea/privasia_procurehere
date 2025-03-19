/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
 *
 */
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.privasia.procurehere.core.pojo.TableDataInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.Global;

@Transactional(propagation = Propagation.REQUIRED)
@PropertySource(value = { "classpath:application.properties" })
public abstract class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK> {

	private static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	private Environment env;

	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager entityManager;

	protected String findAggregateEventAwardedPriceValueForTenant() {
		String sql = "SELECT SUM(a.AWARDED_PRICE) from ( ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.AWARDED_PRICE from PROC_RFT_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.AWARDED_PRICE from PROC_RFP_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.AWARDED_PRICE from PROC_RFQ_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.AWARDED_PRICE from PROC_RFA_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " ) a where a.TENANT_ID = :tenantId ";
		return sql;
	}

	protected String findAggregateEventBudgetAmountValueForTenant() {
		String sql = "SELECT SUM(a.BUDGET_AMOUNT) from ( ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.BUDGET_AMOUNT from PROC_RFT_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.BUDGET_AMOUNT from PROC_RFI_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.BUDGET_AMOUNT from PROC_RFP_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.BUDGET_AMOUNT from PROC_RFQ_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID, e.BUDGET_AMOUNT from PROC_RFA_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " ) a where a.TENANT_ID = :tenantId ";
		return sql;
	}

	protected String findAggregateEventCountForTenant() {
		String sql = "SELECT COUNT(*) from ( ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID from PROC_RFT_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID from PROC_RFI_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID from PROC_RFP_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID from PROC_RFQ_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " UNION ";
		sql += " SELECT e.ID , e.STATUS , e.EVENT_PUBLISH_DATE, bc.CURRENCY_CODE, e.TENANT_ID from PROC_RFA_EVENTS e left Outer join PROC_CURRENCY bc ON bc.ID = e.CURRENCY_ID ";
		sql += " ) a where a.TENANT_ID = :tenantId ";
		return sql;
	}

	/**
	 * @return
	 */
	protected String findAggregateClosedCompletedEventValueForTenant() {
		String sql = "SELECT SUM(sumTotal) FROM( ";
		sql += " SELECT min(o.total) AS sumTotal, o.CURRENCY_CODE AS currency , O.EVENT_ID AS eveId ,o.EVENT_PUBLISH_DATE AS publish ,o.tenanId  FROM ( ";
		sql += " SELECT w.total, w.CURRENCY_CODE , w.EVENT_ID , w.EVENT_PUBLISH_DATE ,w.tenanId FROM ( ";
		sql += " SELECT b.EVENT_ID,b.TOTAL_AFTER_TAX AS total, e.AWARDED_PRICE AS awar, c.CURRENCY_CODE , e.EVENT_PUBLISH_DATE, 'RFT' AS auc ,e.TENANT_ID AS tenanId ";
		sql += " FROM PROC_RFT_SUPPLIER_BQ  b LEFT OUTER JOIN PROC_RFT_EVENTS e LEFT OUTER JOIN PROC_CURRENCY c ON c.ID = e.CURRENCY_ID ON e.ID = b.EVENT_ID ";
		sql += " WHERE e.STATUS IN (:status) ";
		sql += " UNION ALL ";
		sql += " SELECT b.EVENT_ID,b.TOTAL_AFTER_TAX AS total, e.AWARDED_PRICE AS awar, c.CURRENCY_CODE , e.EVENT_PUBLISH_DATE, 'RFQ' AS auc ,e.TENANT_ID AS tenanId ";
		sql += " FROM PROC_RFQ_SUPPLIER_BQ  b LEFT OUTER JOIN PROC_RFQ_EVENTS e LEFT OUTER JOIN PROC_CURRENCY c ON c.ID = e.CURRENCY_ID ON e.ID = b.EVENT_ID ";
		sql += " WHERE e.STATUS IN (:status) ";
		sql += " UNION ALL ";
		sql += " SELECT b.EVENT_ID,b.TOTAL_AFTER_TAX AS total, e.AWARDED_PRICE AS awar, c.CURRENCY_CODE , e.EVENT_PUBLISH_DATE, 'RFP' AS auc, e.TENANT_ID AS tenanId ";
		sql += " FROM PROC_RFP_SUPPLIER_BQ  b LEFT OUTER JOIN PROC_RFP_EVENTS e LEFT OUTER JOIN PROC_CURRENCY c ON c.ID = e.CURRENCY_ID ON e.ID = b.EVENT_ID ";
		sql += " WHERE e.STATUS IN (:status) ";
		sql += " UNION ALL ";
		sql += " SELECT b.EVENT_ID, (MAX(b.TOTAL_AFTER_TAX)-MAX(b.INITIAL_PRICE)) AS total, e.AWARDED_PRICE AS awar , c.CURRENCY_CODE ,e.EVENT_PUBLISH_DATE, e.AUCTION_TYPE AS auc ,e.TENANT_ID AS tenanId FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += "LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID LEFT OUTER JOIN PROC_CURRENCY c ON c.ID = e.CURRENCY_ID WHERE e.AUCTION_TYPE IN ('Reverse English Auction' , 'Reverse Sealed Bid' ,'Reverse Dutch Auction') ";
		sql += " AND e.STATUS IN (:status)  group by b.EVENT_ID, e.AWARDED_PRICE, e.AUCTION_TYPE ,c.CURRENCY_CODE,e.EVENT_PUBLISH_DATE ,e.TENANT_ID ";
		sql += " UNION ALL ";
		sql += "SELECT b.EVENT_ID, (MIN(b.INITIAL_PRICE)-MIN(b.TOTAL_AFTER_TAX)) AS total, e.AWARDED_PRICE AS awar , c.CURRENCY_CODE ,e.EVENT_PUBLISH_DATE, e.AUCTION_TYPE AS auc ,e.TENANT_ID AS tenanId FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += " LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID LEFT OUTER JOIN PROC_CURRENCY c ON c.ID = e.CURRENCY_ID WHERE e.AUCTION_TYPE IN ('Reverse English Auction' , 'Reverse Sealed Bid' ,'Reverse Dutch Auction') ";
		sql += "AND e.STATUS IN (:status)  group by b.EVENT_ID, e.AWARDED_PRICE, e.AUCTION_TYPE ,c.CURRENCY_CODE,e.EVENT_PUBLISH_DATE ,e.TENANT_ID ";
		sql += " ) w ";
		sql += " ) o GROUP BY O.EVENT_ID, o.CURRENCY_CODE , o.EVENT_PUBLISH_DATE ,o.tenanId ) s  ";
		sql += " where s.tenanId = :tenantId ";
		return sql;
	}

	/**
	 * Query for Current supplier count for each of the top 5 categories(Mobile metrics)
	 * 
	 * @return
	 */
	protected String getCurrentSupplierCountForTopFiveCategories() {
		String sql = "SELECT count(sup.FAV_SUPPLIER_ID) as supplierCount, ic.CATEGORY_NAME as industryCategoryName from ";
		sql += " ( SELECT ec.EVENT_CATEGORY, row_number() OVER () AS THE_ORDER FROM  ( ";
		sql += " SELECT a.EVENT_CATEGORY, count(*) FROM ( ";
		sql += " SELECT e.EVENT_CATEGORY from PROC_RFI_EVENTS e where e.STATUS not in (:status) AND e.TENANT_ID = :tenantId ";
		sql += " UNION ALL ";
		sql += " SELECT e.EVENT_CATEGORY from PROC_RFP_EVENTS e where e.STATUS not in (:status) AND  e.TENANT_ID = :tenantId ";
		sql += " UNION ALL ";
		sql += " SELECT e.EVENT_CATEGORY from PROC_RFQ_EVENTS e where e.STATUS not in (:status) AND  e.TENANT_ID = :tenantId ";
		sql += " UNION ALL ";
		sql += " SELECT e.EVENT_CATEGORY from PROC_RFT_EVENTS e where e.STATUS not in (:status) AND  e.TENANT_ID = :tenantId ";
		sql += " UNION ALL ";
		sql += " SELECT e.EVENT_CATEGORY from PROC_RFA_EVENTS e where e.STATUS not in (:status) AND  e.TENANT_ID = :tenantId ";
		sql += " ) a GROUP by a.EVENT_CATEGORY ORDER by count(*) DESC ";
        sql += " ) ec LIMIT 5 ";
		sql += " ) dc ";
		sql += " LEFT OUTER JOIN PROC_FAV_SUPP_IND_CAT sc ON sc.IND_CAT_ID = dc.EVENT_CATEGORY ";
		sql += " LEFT OUTER JOIN PROC_INDUSTRY_CATEGORY ic ON ic.ID = dc.EVENT_CATEGORY ";
		sql += " LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER sup ON sup.FAV_SUPPLIER_ID = sc.FAV_SUPP_ID AND sup.BUYER_ID = :tenantId ";
		sql += " GROUP by ic.CATEGORY_NAME, dc.THE_ORDER ORDER BY dc.THE_ORDER ";
		return sql;
	}

	protected String getMyEvent() {
		String sql = " SELECT distinct a.id as id, a.eventName as eventName, a.createdDate as createdDate, a.referenceNumber as referenceNumber,a.status as status,a.unitName as unitName, puser.USER_NAME as creatorName, a.EVENT_TYPE as eventType, a.AUCTION_TYPE as auctionType, a.mySupplierName as mySupplierName, a.openSupplier as openSupplier from( ";
		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.PR_NAME as eventName, e.PR_CREATED_DATE as createdDate, e.REFERENCE_NUMBER as referenceNumber,e.STATUS as status,bu.BUSINESS_UNIT_NAME as unitName, fs.FULL_NAME as mySupplierName, e.SUPPLIER_NAME as openSupplier ";
		sql += " ,'PR' as EVENT_TYPE, 'PR' as AUCTION_TYPE ";
		sql += " FROM PROC_PR e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID  left outer join PROC_PR_TEAM tm on tm.PR_ID = e.ID left outer join PROC_FAVOURITE_SUPPLIER fs on e.PR_SUPPLIER_ID = fs.FAV_SUPPLIER_ID ";
		sql += " where e.BUYER_ID = :tenantId and (usr.USER_ID = :userId or tm.USER_ID = :userId or e.CREATED_BY = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.SOURCING_FORM_NAME as eventName, e.CREATED_DATE as createdDate, e.REFERANCE_NUMBER as referenceNumber,e.STATUS as status,bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'REQUEST' as EVENT_TYPE, 'REQUEST' as AUCTION_TYPE ";
		sql += " FROM PROC_SOURCING_FORM_REQ e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_SOURCING_FORM_APP_REQ app on app.FORM_ID = e.ID left outer join PROC_FORM_APPROVAL_USER_REQ usr on usr.FORM_APPROVAL_ID = app.ID ";
		sql += " where e.TENANT_ID = :tenantId and (usr.USER_ID = :userId or e.CREATED_BY = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate , e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'" + RfxTypes.RFT.name() + "' as EVENT_TYPE, 'RFT' as AUCTION_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID  ";
		sql += " left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId or ev.USER_ID = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate , e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'" + RfxTypes.RFP.name() + "' as EVENT_TYPE, 'RFP' as AUCTION_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID  ";
		sql += " left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId or ev.USER_ID = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate , e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'" + RfxTypes.RFI.name() + "' as EVENT_TYPE, 'RFI' as AUCTION_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID  ";
		sql += " left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId or ev.USER_ID = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate , e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'" + RfxTypes.RFQ.name() + "' as EVENT_TYPE, 'RFQ' as AUCTION_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID  ";
		sql += " left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId or ev.USER_ID = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate , e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier ";
		sql += " ,'" + RfxTypes.RFA.name() + "' as EVENT_TYPE, e.AUCTION_TYPE as AUCTION_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID  ";
		sql += " left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId or ev.USER_ID = :userId or usr.USER_ID = :userId ) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getMyToDoList() {
		String sql = " SELECT distinct a.id as id, a.eventId as eventId, a.eventName as eventName, a.edate as edate, a.referenceNumber as referenceNumber,a.status as status,a.unitName as unitName, a.creatorName as creatorName, a.EVENT_TYPE as type, a.urgentEvent as urgentEvent from( ";
		sql += " SELECT distinct e.ID as id, e.PR_ID as eventId, e.PR_NAME as eventName, e.PR_CREATED_DATE as edate, e.REFERENCE_NUMBER as referenceNumber,e.STATUS as status,bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_PR as urgentEvent ";
		sql += " ,'PR' as EVENT_TYPE ";
		sql += " FROM PROC_PR e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY";
		sql += " where e.STATUS in (:status) and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION ";

		sql += " SELECT distinct e.ID as id, e.FORM_ID as eventId, e.SOURCING_FORM_NAME as eventName, e.CREATED_DATE as edate, e.REFERANCE_NUMBER as referenceNumber,e.STATUS as status,bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_FORM as urgentEvent ";
		sql += " ,'REQUEST' as EVENT_TYPE ";
		sql += " FROM PROC_SOURCING_FORM_REQ e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_SOURCING_FORM_APP_REQ app on app.FORM_ID = e.ID left outer join PROC_FORM_APPROVAL_USER_REQ usr on usr.FORM_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION ";

        sql += " SELECT distinct e.ID as id,e.EVENT_ID as eventId,  e.EVENT_NAME as eventName, CASE WHEN e.STATUS ='FINISHED' THEN  e.EVENT_END WHEN e.STATUS ='CLOSED' THEN  e.EVENT_END  ELSE e.EVENT_START END edate, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_EVENT as urgentEvent ";
		sql += " ,'" + RfxTypes.RFT.name() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or ((en.ENVELOP_OPENER = :userId and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) )";
		sql += " UNION ";

        sql += " SELECT distinct e.ID as id,e.EVENT_ID as eventId,  e.EVENT_NAME as eventName, CASE WHEN e.STATUS ='FINISHED' THEN  e.EVENT_END WHEN e.STATUS ='CLOSED' THEN  e.EVENT_END  ELSE e.EVENT_START END edate, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_EVENT as urgentEvent ";
		sql += " ,'" + RfxTypes.RFP.name() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or ((en.ENVELOP_OPENER = :userId and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) )";
		sql += " UNION ";

        sql += " SELECT distinct e.ID as id,e.EVENT_ID as eventId,  e.EVENT_NAME as eventName, CASE WHEN e.STATUS ='FINISHED' THEN  e.EVENT_END WHEN e.STATUS ='CLOSED' THEN  e.EVENT_END  ELSE e.EVENT_START END edate, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_EVENT as urgentEvent ";
		sql += " ,'" + RfxTypes.RFI.name() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or ((en.ENVELOP_OPENER = :userId and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) )";
		sql += " UNION ";

        sql += " SELECT distinct e.ID as id,e.EVENT_ID as eventId,  e.EVENT_NAME as eventName, CASE WHEN e.STATUS ='FINISHED' THEN  e.EVENT_END WHEN e.STATUS ='CLOSED' THEN  e.EVENT_END  ELSE e.EVENT_START END edate, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_EVENT as urgentEvent ";
		sql += " ,'" + RfxTypes.RFQ.name() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or ((en.ENVELOP_OPENER = :userId and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) )";
		sql += " UNION";

        sql += " SELECT distinct e.ID as id,e.EVENT_ID as eventId,  e.EVENT_NAME as eventName, CASE WHEN e.STATUS ='FINISHED' THEN  e.EVENT_END WHEN e.STATUS ='CLOSED' THEN  e.EVENT_END  ELSE e.EVENT_START END edate, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as creatorName, e.URGENT_EVENT as urgentEvent ";
		sql += " ,'" + RfxTypes.RFA.name() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or ((en.ENVELOP_OPENER = :userId and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) )";
		sql += " ) a ";
		return sql;
	}

	/*
	 * protected String getMyApprovedRejectPrQuery() { String sql =
	 * "SELECT distinct e.ID as id, e.PR_ID as eventId, e.PR_NAME as eventName, puser.LOGIN_ID as createdBy, e.PR_CREATED_DATE as createdDate, e.PR_MODIFIED_DATE as modifiedDate, e.REFERENCE_NUMBER as referenceNumber, e.GRAND_TOTAL as grandTotal, pc.APPROVED as approvalStatus ,pc.CREATED_DATE as actionDate"
	 * ; sql +=
	 * " FROM PROC_PR e left outer join PROC_USER puser on puser.ID = e.CREATED_BY left outer join PROC_PR_COMMENTS pc on pc.PR_ID = e.ID "
	 * ; sql +=
	 * " where e.BUYER_ID = :tenantId and pc.CREATED_BY = :userId and pc.CREATED_DATE = (select MAX(c.CREATED_DATE) from PROC_PR_COMMENTS c where c.CREATED_BY = pc.CREATED_BY and c.PR_ID = e.ID ) "
	 * ; return sql; }
	 */

	protected String getMyApprovedRejectRfxQuery() {
		String sql = "select distinct a.id as id, puser.USER_NAME as createdBy, a.createdDate as createdDate, a.actionDate as actionDate, a.eventName as eventName, a.referenceNumber as referenceNumber, a.status as status, a.isApproved as isApproved, a.unitName as unitName, a.EVENT_TYPE as \"type\", a.AUCTION_TYPE as auctionType, a.mySupplierName as mySupplierName, a.openSupplier as openSupplier, a.actionType as actionType, a.userComment as userComment, 1 as dummayFlag from (";
		sql += "SELECT distinct e.ID as id, e.CREATED_BY, e.PR_CREATED_DATE as createdDate, pc.CREATED_DATE as actionDate, e.PR_NAME as eventName, e.REFERENCE_NUMBER as referenceNumber, e.STATUS as status, pc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, fs.FULL_NAME as mySupplierName, e.SUPPLIER_NAME as openSupplier, pc.USER_COMMENTS as userComment, ";
        sql += " 'PR' as EVENT_TYPE, 'PR' as AUCTION_TYPE , CASE WHEN pc.APPROVED =1 THEN  'APPROVED' WHEN pc.APPROVED =0 THEN  'REJECTED'  ELSE '' END as actionType ";
		sql += " FROM PROC_PR e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_PR_COMMENTS pc on pc.PR_ID = e.ID left outer join PROC_FAVOURITE_SUPPLIER fs on e.PR_SUPPLIER_ID = fs.FAV_SUPPLIER_ID ";
		sql += " where e.BUYER_ID = :tenantId and pc.CREATED_BY = :userId ";
		sql += " UNION";

		sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, pc.CREATED_DATE as actionDate, e.SOURCING_FORM_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, pc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier, pc.USER_COMMENTS as userComment, ";
        sql += " 'REQUEST' as EVENT_TYPE, 'REQUEST' as AUCTION_TYPE , CASE WHEN pc.APPROVED =1 THEN  'APPROVED' WHEN pc.APPROVED =0 THEN  'REJECTED'  ELSE '' END as actionType ";
		sql += " FROM PROC_SOURCING_FORM_REQ e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_REQUEST_COMMENTS pc on pc.RQUEST_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and pc.CREATED_BY = :userId ";
		sql += " UNION";

        sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, CASE WHEN rc.CREATED_DATE IS NULL THEN  en.OPEN_DATE ELSE rc.CREATED_DATE END as actionDate, e.EVENT_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, rc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier,rc.USER_COMMENTS as userComment , ";
        sql += " '" + RfxTypes.RFT.name() + "' as EVENT_TYPE, 'RFT' as AUCTION_TYPE , CASE WHEN rc.APPROVED =1 THEN  'APPROVED' WHEN rc.APPROVED =0 THEN  'REJECTED'  ELSE 'OPENED' END as actionType ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_COMMENTS rc on rc.EVENT_ID = e.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and ( rc.CREATED_BY = :userId OR (en.ENVELOP_OPENER = :userId AND en.ENVELOP_TYPE = 'Closed' AND en.EVALUATION_STATUS = 'COMPLETE'))";
		sql += " UNION";
        sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, CASE WHEN rc.CREATED_DATE IS NULL THEN  en.OPEN_DATE ELSE rc.CREATED_DATE END as actionDate, e.EVENT_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, rc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier, rc.USER_COMMENTS as userComment,  ";
        sql += " '" + RfxTypes.RFP.name() + "' as EVENT_TYPE, 'RFP' as AUCTION_TYPE , CASE WHEN rc.APPROVED =1 THEN  'APPROVED' WHEN rc.APPROVED =0 THEN  'REJECTED'  ELSE 'OPENED' END as actionType ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_COMMENTS rc on rc.EVENT_ID = e.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and ( rc.CREATED_BY = :userId OR (en.ENVELOP_OPENER = :userId  AND en.ENVELOP_TYPE = 'Closed' AND en.EVALUATION_STATUS = 'COMPLETE')) ";
		sql += " UNION";
        sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, CASE WHEN rc.CREATED_DATE IS NULL THEN en.OPEN_DATE ELSE rc.CREATED_DATE END as actionDate, e.EVENT_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, rc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName,'' as mySupplierName, '' as openSupplier, rc.USER_COMMENTS as userComment,  ";
        sql += " '" + RfxTypes.RFI.name() + "' as EVENT_TYPE, 'RFI' as AUCTION_TYPE , CASE WHEN rc.APPROVED =1 THEN  'APPROVED' WHEN rc.APPROVED =0 THEN  'REJECTED'  ELSE 'OPENED' END as actionType ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_COMMENTS rc on rc.EVENT_ID = e.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and ( rc.CREATED_BY = :userId OR (en.ENVELOP_OPENER = :userId AND en.ENVELOP_TYPE = 'Closed' AND en.EVALUATION_STATUS = 'COMPLETE')) ";
		sql += " UNION";
        sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, CASE WHEN rc.CREATED_DATE IS NULL THEN en.OPEN_DATE ELSE rc.CREATED_DATE END as actionDate, e.EVENT_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, rc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier, rc.USER_COMMENTS as userComment, ";
        sql += " '" + RfxTypes.RFQ.name() + "' as EVENT_TYPE, 'RFQ' as AUCTION_TYPE , CASE WHEN rc.APPROVED =1 THEN  'APPROVED' WHEN rc.APPROVED =0 THEN  'REJECTED'  ELSE 'OPENED' END as actionType ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_COMMENTS rc on rc.EVENT_ID = e.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and ( rc.CREATED_BY = :userId OR (en.ENVELOP_OPENER = :userId AND en.ENVELOP_TYPE = 'Closed' AND en.EVALUATION_STATUS = 'COMPLETE'))  ";
		sql += " UNION";
        sql += " SELECT distinct e.ID as id, e.CREATED_BY, e.CREATED_DATE as createdDate, CASE WHEN rc.CREATED_DATE IS NULL THEN en.OPEN_DATE ELSE rc.CREATED_DATE END as actionDate, e.EVENT_NAME as eventName, e.REFERANCE_NUMBER as referenceNumber, e.STATUS as status, rc.APPROVED as isApproved, bu.BUSINESS_UNIT_NAME as unitName, '' as mySupplierName, '' as openSupplier, rc.USER_COMMENTS as userComment, ";
        sql += " '" + RfxTypes.RFA.name() + "' as EVENT_TYPE, e.AUCTION_TYPE as AUCTION_TYPE , CASE WHEN rc.APPROVED =1 THEN  'APPROVED' WHEN rc.APPROVED =0 THEN  'REJECTED'  ELSE 'OPENED' END as actionType ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_COMMENTS rc on rc.EVENT_ID = e.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID ";
		sql += " where e.TENANT_ID = :tenantId and ( rc.CREATED_BY = :userId OR (en.ENVELOP_OPENER = :userId AND en.ENVELOP_TYPE = 'Closed' AND en.EVALUATION_STATUS = 'COMPLETE')) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeClosedRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (eo.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (eo.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (eo.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (eo.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (eo.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId)  ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID  ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId )  ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeCanceldRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd ,a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName,a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from ( ";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID  left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID  left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID  left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID  left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID  left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId )  ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeFinishRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID =:userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID =:userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID =:userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID =:userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID =:userId )  ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeCompletedRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID  ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0) ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_UNMASKED_USER umu on e.ID = umu.EVENT_ID  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFI_UNMASKED_USER umu on e.ID = umu.EVENT_ID  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId ) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_UNMASKED_USER umu on e.ID = umu.EVENT_ID  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId ) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId ) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeOngoingRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID  left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeOngoingRfxCountQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID  left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeRfxQueryForSuspendedEvents() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId or evl.ID = :userId) and en.ENVELOP_STATUS = 1))";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId or evl.ID = :userId) and en.ENVELOP_STATUS = 1))";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((tm.USER_ID = :userId  or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId or evl.ID = :userId) and en.ENVELOP_STATUS = 1)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId or evl.ID = :userId ) and en.ENVELOP_STATUS = 1))";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or e.REVERT_BID_BY_USER = :userId) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId or evl.ID = :userId ) and en.ENVELOP_STATUS = 1))";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeSupplierRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber from (";
		sql += "SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getMyPendingRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_ID as eventId, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.EVENT_PUBLISH_DATE as publishDate, a.EVENT_VISIBILITY as visibilityType, a.REFERANCE_NUMBER as referenceNumber,a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START";
		} else {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START OFFSET 0 ROWS";
		}
		return sql;
	}

	protected String getMyTotalPendingRfxQuery() {
		String sql = "SELECT count(distinct a.id) from (";
		sql += "select distinct a.ID as id, a.EVENT_ID as eventId, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.EVENT_PUBLISH_DATE as publishDate, a.EVENT_VISIBILITY as visibilityType, a.REFERANCE_NUMBER as referenceNumber,a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, ";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getMyPendingPrQuery() {
		String sql = " SELECT distinct e.ID as id, e.PR_ID as eventId, e.PR_NAME as eventName, puser.LOGIN_ID as createdBy, e.PR_CREATED_DATE as createdDate, e.PR_MODIFIED_DATE as modifiedDate, e.REFERENCE_NUMBER as referenceNumber, e.GRAND_TOTAL as grandTotal,bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as prUserName, e.PR_DECIMAL as prDecimal, e.URGENT_PR as urgentPr ";
		sql += " FROM PROC_PR e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY";
		sql += " where e.STATUS in (:status) and e.IS_ERP_TRANSFER = :erptransferrd and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " order by e.PR_CREATED_DATE DESC";
		} else {
			sql += " order by e.PR_CREATED_DATE DESC OFFSET 0 ROWS";
		}
		return sql;
	}

	protected String getMyPendingPrCountQuery() {
		String sql = "SELECT count(distinct a.id) from (";
		sql += " SELECT distinct e.ID as id, e.PR_ID as eventId, e.PR_NAME as eventName, puser.LOGIN_ID as createdBy, e.PR_CREATED_DATE as createdDate, e.PR_MODIFIED_DATE as modifiedDate, e.REFERENCE_NUMBER as referenceNumber, e.GRAND_TOTAL as grandTotal,bu.BUSINESS_UNIT_NAME as unitName, puser.USER_NAME as prUserName  ";
		sql += " FROM PROC_PR e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY";
		sql += " where e.STATUS in (:status) and e.IS_ERP_TRANSFER = :erptransferrd and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1)";
		return sql;
	}

	protected String getMyPendingCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL awardUsr on awardUsr.EVENT_ID = e.ID left outer join PROC_RFT_AWARD_APPR_USER awardAppUsr on awardAppUsr.RFT_EVENT_AWARD_APPR_ID = awardUsr.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or (awardAppUsr.USER_ID = :userId and awardAppUsr.APPROVAL_STATUS = 'PENDING' and awardUsr.ACTIVE = 1) ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID ";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL awardUsr on awardUsr.EVENT_ID = e.ID left outer join PROC_RFP_AWARD_APPR_USER awardAppUsr on awardAppUsr.RFP_EVENT_AWARD_APPR_ID = awardUsr.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or (awardAppUsr.USER_ID = :userId and awardAppUsr.APPROVAL_STATUS = 'PENDING' and awardUsr.ACTIVE = 1) ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID ";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL awardUsr on awardUsr.EVENT_ID = e.ID left outer join PROC_RFQ_AWARD_APPR_USER awardAppUsr on awardAppUsr.RFQ_EVENT_AWARD_APPR_ID = awardUsr.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or (awardAppUsr.USER_ID = :userId and awardAppUsr.APPROVAL_STATUS = 'PENDING' and awardUsr.ACTIVE = 1) ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID ";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL awardUsr on awardUsr.EVENT_ID = e.ID left outer join PROC_RFA_AWARD_APPR_USER awardAppUsr on awardAppUsr.RFA_EVENT_AWARD_APPR_ID = awardUsr.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) or (awardAppUsr.USER_ID = :userId and awardAppUsr.APPROVAL_STATUS = 'PENDING' and awardUsr.ACTIVE = 1) ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_PR e left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_SOURCING_FORM_REQ e left outer join PROC_SOURCING_FORM_APP_REQ app on app.FORM_ID = e.ID left outer join PROC_FORM_APPROVAL_USER_REQ usr on usr.FORM_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_SUPPLIER_FORM_SUBM e left outer join PROC_SUPP_FORM_SUB_APPROVAL app on app.FORM_SUB_ID = e.ID left outer join PROC_SUPP_FORM_SUB_APPR_USER usr on usr.FORM_SUB_APPR_ID = app.ID";
		sql += " where  e.STATUS in (:status) and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getMyEventPendingCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getMyPrPendingCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_PR e left outer join PROC_PR_APPROVAL app on app.PR_ID = e.ID left outer join PROC_PR_APPROVAL_USER usr on usr.PR_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getMyRfsPendingCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_SOURCING_FORM_REQ e left outer join PROC_SOURCING_FORM_APP_REQ app on app.FORM_ID = e.ID left outer join PROC_FORM_APPROVAL_USER_REQ usr on usr.FORM_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getMySupplierFormPendingCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_SUPPLIER_FORM_SUBM e left outer join PROC_SUPP_FORM_SUB_APPROVAL app on app.FORM_SUB_ID = e.ID left outer join PROC_SUPP_FORM_SUB_APPR_USER usr on usr.FORM_SUB_APPR_ID = app.ID";
		sql += " where  e.STATUS = :status and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getPendingSPEValuationQuery() {
		String sql = "SELECT distinct evl.ID AS id,sp.FORM_ID AS formId, sp.REFERENCE_NUMBER AS referenceNumber,	sup.COMPANY_NAME  AS supplierName,sp.EVALUATION_START_DATE AS evaluationStartDate, sp.EVALUATION_END_DATE AS evaluationEndDate,	usr.USER_NAME AS formOwner,	bu.BUSINESS_UNIT_NAME AS unitName " 
				+ "FROM PROC_SUPP_PERF_EVAL_USER evl LEFT OUTER JOIN PROC_SUPPLIER_PERFORMANCE_FORM sp ON sp.ID = evl.FORM_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON sp.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_USER usr ON sp.FORM_OWNER_ID  = usr.ID LEFT OUTER JOIN PROC_SUPPLIER sup ON sp.AWARDED_SUPPLIER_ID = sup.SUPPLIER_ID " 
				+ "WHERE sp.FORM_STATUS = :status AND evl.EVALUATOR_ID = :userId AND evl.EVALUATION_STATUS IN (:evaluationStatus) ";
		return sql;
	}
	
	protected String getPendingApprovalSPEValuationQuery() {
		String sql = "SELECT DISTINCT evl.ID AS id, sp.FORM_ID AS formId, sp.REFERENCE_NUMBER AS referenceNumber, sup.COMPANY_NAME AS supplierName, sp.EVALUATION_START_DATE AS evaluationStartDate, sp.EVALUATION_END_DATE AS evaluationEndDate, evlusr.USER_NAME AS evaluator,pusr.USER_NAME AS formOwner, bu.BUSINESS_UNIT_NAME AS unitName "
				+ " FROM PROC_SUPP_PERF_EVAL_USER evl LEFT OUTER JOIN PROC_SUPPLIER_PERFORMANCE_FORM sp ON sp.ID = evl.FORM_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON sp.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_PER_EVALUATION_APPROVAL app ON app.SP_EVAL_USER_ID = evl.ID LEFT OUTER JOIN PROC_PER_EVAL_APPROVAL_USER usr ON usr.PER_EVALUATION_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_USER evlusr ON evlusr.ID = evl.EVALUATOR_ID LEFT OUTER JOIN PROC_USER pusr ON sp.FORM_OWNER_ID = pusr.ID LEFT OUTER JOIN PROC_SUPPLIER sup ON sp.AWARDED_SUPPLIER_ID = sup.SUPPLIER_ID "
				+ " WHERE sp.FORM_STATUS = :status AND evl.EVALUATION_STATUS = 'PENDING' AND (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1)";
		return sql;
	}
	protected String getSupplierPerformanceEvaluationQuery() {
		String sql = "SELECT DISTINCT sp.ID as id, sp.FORM_ID as formId, sp.FORM_NAME as formName, sp.REFERENCE_NUMBER as referenceNumber, sp.REFERENCE_NAME as referenceName, usr.USER_NAME as formOwner, usr.ID as formOwnerId, cat.PROCUREMENT_CATEGORIES as procurementCategory, bu.BUSINESS_UNIT_NAME as unitName, sup.COMPANY_NAME as supplierName, COUNT(evl.FORM_ID) AS totalEvaluator, COUNT(CASE WHEN evl.EVALUATION_STATUS = 'APPROVED' THEN 1 END) AS totalEvaluationComplete, sp.EVALUATION_START_DATE as evaluationStartDate, sp.EVALUATION_END_DATE as evaluationEndDate, sp.RECURRENCE_EVALUATION as recurrenceEvaluation, sp.IS_RECURRENCE_EVALUATION as isRecurrenceEvaluation, sp.FORM_STATUS AS formStatus, sp.CONCLUDE_DATE AS concludeDate,	sr.RATING AS scoreRating, sp.OVERALL_SCORE AS overallScore, sp.CREATED_DATE AS createdDate, sp.BUSINESS_UNIT_ID  "
				+ " FROM PROC_SUPPLIER_PERFORMANCE_FORM sp LEFT OUTER JOIN PROC_USER usr ON	sp.FORM_OWNER_ID = usr.ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES cat ON sp.PROCUREMENT_CATEGORY_ID = cat.ID LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON sp.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_SUPPLIER sup ON	sp.AWARDED_SUPPLIER_ID = sup.SUPPLIER_ID LEFT OUTER JOIN PROC_SCORE_RATING sr ON sp.SCORE_RATING_ID = sr.ID LEFT OUTER JOIN PROC_BUYER buyer ON sp.TENANT_ID = buyer.BUYER_ID LEFT OUTER JOIN PROC_SUPP_PERF_EVAL_USER evl ON sp.ID = evl.FORM_ID where  buyer.BUYER_ID = :buyerId GROUP BY sp.ID, sp.FORM_ID, sp.FORM_NAME, sp.REFERENCE_NUMBER, sp.REFERENCE_NAME, usr.USER_NAME, usr.ID, cat.PROCUREMENT_CATEGORIES, bu.BUSINESS_UNIT_NAME, sup.COMPANY_NAME, sp.EVALUATION_START_DATE, sp.EVALUATION_END_DATE, sp.RECURRENCE_EVALUATION, sp.IS_RECURRENCE_EVALUATION, sp.FORM_STATUS, sp.CONCLUDE_DATE, sr.RATING, sp.OVERALL_SCORE, sp.CREATED_DATE ";
		return sql;
	}
	
	protected String getMyPendingEvaluationRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser  from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and  en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (cu.USER_ID = :userId and cu.IS_CONCLUDED = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFP_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and   en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (cu.USER_ID = :userId and cu.IS_CONCLUDED = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFI_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (cu.USER_ID = :userId and cu.IS_CONCLUDED = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFQ_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and  en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (cu.USER_ID = :userId and cu.IS_CONCLUDED = 0)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFA_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (cu.USER_ID = :userId and  cu.IS_CONCLUDED = 0)) ";
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_END DESC ";
		} else {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_END DESC OFFSET 0 ROWS ";
		}
		return sql;
	}

	protected String getMyPendingEvaluationCountQuery() {
		String sql = "select count(distinct a.ID) from ( ";
		sql += "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser  from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and  en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId and eo.ENVELOP_STATUS = 0 and  en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ((eo.USER_ID = :userId eo.ENVELOP_STATUS = 0 and  and en.ENVELOP_STATUS = 0 and en.ENVELOP_TYPE = 'Closed') or (en.LEAD_EVALUATER = :userId and en.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open')) or (ev.USER_ID = :userId and ev.EVALUATION_STATUS = 'PENDING' and (en.ENVELOP_STATUS = 1 or en.ENVELOP_TYPE = 'Open'))) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_END DESC ) a";
		return sql;
	}

	protected String getSimpleNativeRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd ,a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName,a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID,  e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER,bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId  ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY,'" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY,'" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY,'" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId ) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getSimpleAdminNativeRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd ,a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName,a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID,  e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER,bu.BUSINESS_UNIT_NAME,e.CREATED_BY, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY,'" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY ,'" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,e.CREATED_BY, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeRfxQueryForBuyerGlobalSearch() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName,a.EVENT_DESCRIPTION as eventDescription, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate,a.STATUS as status, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStartDate, a.EVENT_END as eventEndDate, a.REFERANCE_NUMBER as referenceNumber, buyer.COMPANY_NAME as eventOwner, puser.USER_NAME as createdBy, a.EVENT_ID as eventId ,a.BUSINESS_UNIT_NAME as unitName from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eou on eou.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFT_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID left outer join PROC_RFT_UNMASKED_USER umu on umu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER =  :userId or eou.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId or umu.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eou on eou.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFP_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID left outer join PROC_RFP_UNMASKED_USER umu on umu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER =  :userId or eou.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId or umu.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eou on eou.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFI_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID left outer join PROC_RFI_UNMASKED_USER umu on umu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER =  :userId or eou.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId or umu.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eou on eou.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFQ_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID left outer join PROC_RFQ_UNMASKED_USER umu on umu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER =  :userId or eou.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or cu.USER_ID = :userId or umu.USER_ID = :userId  ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY,bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eou on eou.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFA_EVAL_CON_USERS cu on cu.EVENT_ID = e.ID left outer join PROC_RFA_UNMASKED_USER umu on umu.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER =  :userId or eou.USER_ID = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId or e.REVERT_BID_BY_USER = :userId or cu.USER_ID = :userId or umu.USER_ID = :userId  ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " ) a , PROC_BUYER buyer , PROC_USER puser where a.TENANT_ID = buyer.BUYER_ID  and a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeRfxQueryForBuyerAdminGlobalSearch() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName,a.EVENT_DESCRIPTION as eventDescription, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate,a.STATUS as status, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStartDate, a.EVENT_END as eventEndDate, a.REFERANCE_NUMBER as referenceNumber, buyer.COMPANY_NAME as eventOwner, puser.USER_NAME as createdBy, a.EVENT_ID as eventId ,a.BUSINESS_UNIT_NAME as unitName from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId   ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " ) a , PROC_BUYER buyer , PROC_USER puser where a.TENANT_ID = buyer.BUYER_ID  and a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getNativeRfxQueryForSupplierGlobalSearch() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.EVENT_DESCRIPTION as eventDescription, a.REFERANCE_NUMBER as referenceNumber, a.CREATED_DATE as createdDate, a.STATUS as status, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStartDate, a.EVENT_END as eventEndDate, a.TENANT_ID as tenantId, buyer.COMPANY_NAME as eventOwner,usr.USER_NAME as createdBy, a.EVENT_ID as eventId,a.BUSINESS_UNIT_NAME as unitName from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId and (tm.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId and (tm.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId and (tm.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId and (tm.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId and (tm.USER_ID = :userId ) ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " ) a , PROC_BUYER buyer, PROC_USER usr where a.TENANT_ID = buyer.BUYER_ID  and usr.ID = a.EVENT_OWNER ";
		return sql;
	}

	protected String getNativeRfxQueryForSupplierAdminGlobalSearch() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.EVENT_DESCRIPTION as eventDescription, a.REFERANCE_NUMBER as referenceNumber, a.CREATED_DATE as createdDate, a.STATUS as status, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStartDate, a.EVENT_END as eventEndDate, a.TENANT_ID as tenantId, buyer.COMPANY_NAME as eventOwner, usr.USER_NAME as createdBy, a.EVENT_ID as eventId,a.BUSINESS_UNIT_NAME as unitName from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.PARTICIPATION_FEE_CURRENCY,bu.BUSINESS_UNIT_NAME, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_SUPPLIER_TEAM tm on tm.EVENT_ID = e.ID AND tm.EVENT_SUPPLIER_ID = s.ID ";
		sql += " where e.STATUS in (:status) and s.SUPPLIER_ID = :tenantId ";
		sql += " AND (((e.EVENT_ID) like :searchVal) or (upper(e.REFERANCE_NUMBER) like :searchVal) or (upper(e.EVENT_NAME) like :searchVal) or (upper(e.EVENT_DESCRIPTION) like :searchVal)) ";
		sql += " ) a , PROC_BUYER buyer, PROC_USER usr where a.TENANT_ID = buyer.BUYER_ID and usr.ID = a.EVENT_OWNER ";
		return sql;
	}

	protected String getNativeRfxZipQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser  from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getNativeRfxAdminQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getNativeRfxAdminCountQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "   '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "   '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getNativeRfxSecduleQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and ( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += "  '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeRfxCountQuery() {
		String sql = "select count(distinct a.ID) from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a ";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeScheduledRfxCountQuery() {
		String sql = "select count(distinct a.ID) from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += "  '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID,";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or  e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a ";
		return sql;
	}

	protected String getNativeRfxDraftCountQuery() {
		String sql = "select count(distinct a.ID) from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, evo.LOGIN_ID, ";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER evo on evo.ID = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, evo.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER evo on evo.ID = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, evo.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER evo on evo.ID = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, evo.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER evo on evo.ID = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId ) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, evo.LOGIN_ID,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID left outer join PROC_USER evo on evo.ID = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or en.LEAD_EVALUATER = :userId or en.ENVELOP_OPENER = :userId) ";
		sql += " ) a ";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeRfxCountAdminQuery() {
		String sql = "select count(distinct a.ID) from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID, ";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID, ";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID, ";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID, ";
		sql += " '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME, u.LOGIN_ID, ";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_USER u on u.id = e.EVENT_OWNER ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a ";
		return sql;
	}

	protected Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	protected Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public GenericDaoImpl() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(final Class<T> clazz) {
		return entityManager.createQuery("from " + clazz.getName()).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T findById(final PK serializedId) {
		final Class<?> typeClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		T t = (T) entityManager.find(typeClazz, serializedId);
		// Converting Hibernate proxy to real object - As in some cases
		// Hibernate proxies get loaded instead of full
		// object. This leads to problems such as in case of hashcode/equals
		// (collection.contains())
		if (t != null) {
			t = (T) getEntityManager().unwrap(SessionImplementor.class).getPersistenceContext().unproxy(t);
		}
		return t;
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.eccs.core.dao.GenericDao#save(java.lang.Object)
	 */
	@Override
	public T save(T t) {
		this.entityManager.persist(t);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.eccs.core.dao.GenericDao#update(java.lang.Object)
	 */
	@Override
	public T update(T e) {
		return this.entityManager.merge(e);
	}

	/**
	 * @param transientObject
	 */
	@Override
	public T saveOrUpdate(T transientObject) {
		return (T) entityManager.merge(transientObject);
	}

	/**
	 * @param transientObject
	 */
	@Override
	public T saveOrUpdateWithFlush(T transientObject) {
		T mergedEntity = entityManager.merge(transientObject);
		entityManager.flush();
		return mergedEntity;
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.eccs.core.dao.GenericDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(T t) {
		t = this.entityManager.merge(t);
		this.entityManager.remove(t);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int findCount(T t) {
		final Query query = getEntityManager().createQuery("select ds from " + t + " as ds");
		List<T> list = query.getResultList();
		return list != null ? list.size() : 0;
	}

	@Override
	public T findByProperty(String propertyName, Object value) {
		try {
			return entityManager.createQuery(createQueryByProperty(propertyName, value)).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	public T findByProperties(String[] propertyNames, Object[] values) {
		try {
			return entityManager.createQuery(createQueryByProperties(propertyNames, values)).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public List<T> findAllByProperty(String propertyName, Object value) {

		return entityManager.createQuery(createQueryByProperty(propertyName, value)).getResultList();
	}

	@SuppressWarnings("unchecked")
	public Class<T> returnEntityClass() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	private CriteriaQuery<T> createQueryByProperty(String property, Object value) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(returnEntityClass());
		Root<T> root = cq.from(returnEntityClass());
		cq = cq.where(cb.equal(root.get(property), value));
		return cq;
	}

	private CriteriaQuery<T> createQueryByProperties(String[] property, Object[] value) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(returnEntityClass());
		Root<T> root = cq.from(returnEntityClass());
		cq.select(root);
		List<Predicate> p = new ArrayList<Predicate>();
		for (int i = 0; i < property.length; i++) {
			p.add(cb.equal(root.get(property[i]), value[i]));
		}
		cq = cq.where(p.toArray(new Predicate[p.size()]));
		return cq;
	}

	/**
	 * FindByExample Method
	 * 
	 * @param object
	 * @param matchMode
	 * @param ignoreCase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByExample(T object, MatchMode matchMode, boolean ignoreCase) {
		// get the native hibernate session
		Session session = (Session) getEntityManager().getDelegate();
		Example example = Example.create(object);
		if (matchMode != null)
			example.enableLike(matchMode);
		if (ignoreCase)
			example.ignoreCase();

		Criteria criteria = session.createCriteria(object.getClass()).add(example);
		return criteria.list();
	}

	protected CriteriaBuilder createCriteria() {
		return entityManager.getCriteriaBuilder();
	}

	@Override
	public void batchInsert(List<T> dataList) {
		int batchSize = 0;
		if (dataList == null)
			return;

		int recCount = 0;
		for (T e : dataList) {
			if (e == null)
				continue;
			this.entityManager.persist(e);
			batchSize++;
			if (batchSize % Global.BATCH_INSERT_SIZE == 0) {
				this.entityManager.flush();
				this.entityManager.clear();
			}
			recCount++;
			if ((recCount % 1000) == 0) // Log every 1000 records processed
			{
				LOG.info("Inserted " + recCount + " records...");
			}
		}

	}

	protected String getNativeRfxReportQuery() {

		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, puser.LOGIN_ID as createdBy,a.CREATED_DATE as createdDate, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser , a.STATUS as status from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getNativeRfxReportQueryDate() {

		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, puser.LOGIN_ID as createdBy,a.CREATED_DATE as createdDate, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser , a.STATUS as status from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  and e.CREATED_DATE between :startDate and :endDate ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.CREATED_DATE between :startDate and :endDate ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.CREATED_DATE between :startDate and :endDate ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.CREATED_DATE between :startDate and :endDate  ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.CREATED_DATE between :startDate and :endDate ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	/**
	 * @return
	 */
	protected String getNativeRfxReportCountQuery() {
		String sql = "select count(distinct a.ID) from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a ";
		return sql;
	}

	protected String getNativeRfxReportQueryForReportExport() {

		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, puser.LOGIN_ID as createdBy,a.CREATED_DATE as createdDate, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser , a.STATUS as status from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.ID in :id ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.ID in :id ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.ID in :id ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.ID in :id ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " s.SUPPLIER_ID, s.IS_BID_SUBMITTED, s.SUPPLIER_EVENT_READ_TIME, s.SUPPLIER_INVITED_TIME, s.SUPPLIER_BID_SUBMITTED_TIME, e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_SUPPLIERS s on e.ID = s.EVENT_ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and e.ID in :id ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	public String getTopEventCategoryNativeQuery() {
		String sql = null;
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql = "SELECT i.ID,i.CATEGORY_CODE,i.CATEGORY_NAME FROM PROC_INDUSTRY_CATEGORY  i WHERE i.TANENT_ID = :tenantId and i.ID in (";
			sql += " SELECT ec.EVENT_CATEGORY FROM (";
			sql += " SELECT a.EVENT_CATEGORY, count(*) FROM ( ";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFI_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId ";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFP_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFQ_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFT_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += ") a GROUP by a.EVENT_CATEGORY ORDER by count(*) DESC ";
            sql += ") ec WHERE ec.EVENT_CATEGORY IS NOT NULL LIMIT 5 )";
			return sql;
		} else {
			sql = "SELECT i.ID,i.CATEGORY_CODE,i.CATEGORY_NAME FROM PROC_INDUSTRY_CATEGORY  i WHERE i.TANENT_ID = :tenantId and i.ID in (";
			sql += " SELECT TOP 5 ec.EVENT_CATEGORY FROM (";
			sql += " SELECT a.EVENT_CATEGORY, count(*) as cou FROM ( ";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFI_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId ";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFP_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFQ_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += " UNION ALL";
			sql += " SELECT e.EVENT_CATEGORY from PROC_RFT_EVENTS e where e.STATUS not in ('CANCELLED', 'DRAFT') AND  e.TENANT_ID = :tenantId";
			sql += ") a GROUP by a.EVENT_CATEGORY ORDER by count(*) DESC OFFSET 0 ROWS";
			sql += ") ec WHERE ec.EVENT_CATEGORY IS NOT NULL )";
			return sql;
		}

	}

	public String getRfxEventDetails() {
		String sql = "select distinct e.ID as id, e.EVENT_NAME as eventName, e.CREATED_DATE as createdDate, e.TENANT_ID as tenantId, e.CREATED_BY as createdBy, e.MODIFIED_DATE as modifiedDate, e.EVENT_TYPE as type, e.EVENT_START as eventStart, e.EVENT_END as eventEnd, e.REFERANCE_NUMBER as referenceNumber, e.BUSINESS_UNIT_NAME as unitName, e.EVENT_ID as sysEventId, e.EVENT_USER as eventUser";
		sql += " FROM PROC_RFX_EVENT_DETAILS e";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  ";
		return sql;
	}

	public String getTopTwentyEventCategoryNativeQuery() {
		String sql = null;
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
            sql = "SELECT i.ID,i.CATEGORY_CODE,i.CATEGORY_NAME FROM PROC_INDUSTRY_CATEGORY  i WHERE i.TANENT_ID = :tenantId LIMIT 19 ";
		} else {
			sql = "SELECT TOP 20 i.ID,i.CATEGORY_CODE,i.CATEGORY_NAME FROM PROC_INDUSTRY_CATEGORY  i WHERE i.TANENT_ID = :tenantId ";
		}
		return sql;

	}

	protected String getNativePendingRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,  e.CREATED_BY, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}
	
	protected String getSuspendedEventsPendingApprovalQuery() {
		
		String sql = " select distinct a.ID as id, a.EVENT_ID as eventId, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.EVENT_PUBLISH_DATE as publishDate, a.EVENT_VISIBILITY as visibilityType, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER usr on usr.RFT_EVENT_SUSP_APPR_ID = app.ID ";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER usr on usr.RFP_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER usr on usr.RFI_EVENT_SUSP_APPR_ID = app.ID ";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION ";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER usr on usr.RFQ_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION ";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER usr on usr.RFA_EVENT_SUSP_APPR_ID = app.ID ";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START";
		} else {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START OFFSET 0 ROWS";
		}
		return sql;
	}

	
	protected String findCountOfSuspendedEventsPendingApprovals() {
		String sql = "SELECT count(distinct a.id) from (";
		sql += " select distinct a.ID as id, a.EVENT_ID as eventId, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.EVENT_PUBLISH_DATE as publishDate, a.EVENT_VISIBILITY as visibilityType, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER usr on usr.RFT_EVENT_SUSP_APPR_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER usr on usr.RFP_EVENT_SUSP_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER usr on usr.RFI_EVENT_SUSP_APPR_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION ";
		
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER usr on usr.RFQ_EVENT_SUSP_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION ";
		
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER usr on usr.RFA_EVENT_SUSP_APPR_ID = app.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
	
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}
	
	protected String findCountOfSuspendedEventPendingApprovals() {
		String sql = "select count(distinct a.ID) as total from (";
			sql += "SELECT distinct e.ID ";
			sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER usr on usr.RFT_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			sql += " SELECT distinct e.ID";
			sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER usr on usr.RFP_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			sql += " SELECT distinct e.ID ";
			sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFI_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER usr on usr.RFI_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			sql += " SELECT distinct e.ID ";
			sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER usr on usr.RFQ_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			sql += " SELECT distinct e.ID ";
			sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_SUSPENSION_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER usr on usr.RFA_EVENT_SUSP_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " ) a ";
		return sql;
	}

	protected String getRevisePendingPoCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_PO e left outer join PROC_PO_APPROVAL app on app.PO_ID = e.ID left outer join PROC_PO_APPROVAL_USER usr on usr.PO_APPROVAL_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
		return sql;
	}

	protected String getPendingRevisePoQuery() {
		
		String sql = " SELECT distinct e.ID as id, e.PO_NUMBER as eventId, e.PO_NAME as eventName, puser.LOGIN_ID as createdBy, e.PO_CREATED_DATE as createdDate, e.PO_MODIFIED_DATE as modifiedDate, e.GRAND_TOTAL as grandTotal, bu.BUSINESS_UNIT_NAME as unitName, cur.CURRENCY_CODE as currency, puser.USER_NAME as prUserName, e.PO_DECIMAL as prDecimal ";
		
		sql += " FROM PROC_PO e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_CURRENCY cur on e.BASE_CURRENCY_ID = cur.ID left outer join PROC_PO_APPROVAL app on app.PO_ID = e.ID left outer join PROC_PO_APPROVAL_USER usr on usr.PO_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY ";
		sql += " where e.STATUS in (:status) and e.IS_ERP_TRANSFER = :erptransferrd and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " order by e.PO_CREATED_DATE DESC";
		} else {
			sql += " order by e.PO_CREATED_DATE OFFSET 0 ROWS";
		}
		return sql;
	}

	protected String getMyRevisePendingPoCountQuery() {
		String sql = "SELECT count(distinct a.id) from (";
		sql += " SELECT distinct e.ID as id, e.PO_NUMBER as eventId, e.PO_NAME as eventName, puser.LOGIN_ID as createdBy, e.PO_CREATED_DATE as createdDate, e.PO_MODIFIED_DATE as modifiedDate, e.GRAND_TOTAL as grandTotal, bu.BUSINESS_UNIT_NAME as unitName, cur.CURRENCY_CODE as currency, puser.USER_NAME as prUserName, e.PO_DECIMAL as prDecimal ";
		sql += " FROM PROC_PO e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_CURRENCY cur on e.BASE_CURRENCY_ID = cur.ID left outer join PROC_PO_APPROVAL app on app.PO_ID = e.ID left outer join PROC_PO_APPROVAL_USER usr on usr.PO_APPROVAL_ID = app.ID left outer join PROC_USER puser on puser.ID = e.CREATED_BY";
		sql += " where e.STATUS in (:status) and e.IS_ERP_TRANSFER = :erptransferrd and e.BUYER_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1)";
		return sql;
	}
	
	protected String getMyPendingAwardCountQuery() {
		String sql = "select count(distinct a.ID) as total from (";
		sql += "SELECT distinct e.ID ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_AWARD_APPR_USER usr on usr.RFT_EVENT_AWARD_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFP_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_AWARD_APPR_USER usr on usr.RFP_EVENT_AWARD_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFQ_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_AWARD_APPR_USER usr on usr.RFQ_EVENT_AWARD_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " UNION";
		sql += " SELECT distinct e.ID ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFA_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_AWARD_APPR_USER usr on usr.RFA_EVENT_AWARD_APPR_ID = app.ID";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
		sql += " ) a ";
	return sql;
	}
	
	protected String getAwardEventPendingApprovalQuery() {
		String sql = " select distinct a.ID as id, a.EVENT_ID as eventId, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as \"type\", a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.EVENT_PUBLISH_DATE as publishDate, a.EVENT_VISIBILITY as visibilityType, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser from (";
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_AWARD_APPR_USER usr on usr.RFT_EVENT_AWARD_APPR_ID = app.ID ";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_AWARD_APPR_USER usr on usr.RFP_EVENT_AWARD_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_AWARD_APPR_USER usr on usr.RFQ_EVENT_AWARD_APPR_ID = app.ID";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			sql += " UNION ";
			
			sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.COST_CENTER, e.CREATED_BY, e.EVENT_OWNER, e.MODIFIED_BY, e.MODIFIED_DATE, bu.BUSINESS_UNIT_NAME, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
			sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_AWARD_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_AWARD_APPR_USER usr on usr.RFA_EVENT_AWARD_APPR_ID = app.ID ";
			sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId and (usr.USER_ID = :userId and usr.APPROVAL_STATUS = 'PENDING' and app.ACTIVE = 1) ";
			
        if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START";
		} else {
			sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID order by a.EVENT_START OFFSET 0 ROWS";
		}
		return sql;
	}

	//4105
	protected String getFinishedAndClosedRfxBizUnitQuery(String userId,List<String> businessUnitIds) {
		String whereCondition=" ";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId or eo.USER_ID = :userId or  en.LEAD_EVALUATER = :userId or evl.ID = :userId or cu.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) ";
				whereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId or eo.USER_ID = :userId or  en.LEAD_EVALUATER = :userId or evl.ID = :userId or cu.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) ";
			}
		}

		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId or eo.USER_ID = :userId or  en.LEAD_EVALUATER = :userId or evl.ID = :userId or cu.USER_ID = :userId OR sauser.USER_ID=:userId) ";
				rfiWhereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId or eo.USER_ID = :userId or  en.LEAD_EVALUATER = :userId or evl.ID = :userId or cu.USER_ID = :userId  OR sauser.USER_ID=:userId) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser, a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID, MAX(a.ENV_OPEN_USER_ID) as ENV_OPEN_USER_ID, MAX(a.ENVELOP_LEAD_EVALUATER) as ENVELOP_LEAD_EVALUATER, MAX(a.EVL_USER_ID) as EVL_USER_ID, MAX(a.EVL_CON_USER_ID) as EVL_CON_USER_ID from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, eo.USER_ID as ENV_OPEN_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, eo.USER_ID as ENV_OPEN_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, eo.USER_ID as ENV_OPEN_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, eo.USER_ID as ENV_OPEN_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, eo.USER_ID as ENV_OPEN_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID,";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String getRfxAdminBizUnitQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber , a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser,a.BUSINESS_UNIT_ID from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID ";
		return sql;
	}

	protected String getSimpleAdminBizUnitRfxQuery() {
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd ,a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName,a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser,a.BUSINESS_UNIT_ID from (";
		sql += "SELECT distinct e.ID,  e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER,bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, e.CREATED_BY, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID,e.CREATED_BY,'" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, e.CREATED_BY, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE,  e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START,  e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, e.CREATED_BY ,'" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID,  e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID,e.CREATED_BY, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId ";
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		return sql;
	}

	protected String getBizUnitRfxQueryForSuspendedEvents(String userId,List<String> businessUnitIds) {
		//4105
		String whereCondition="";
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND ((tm.USER_ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  OR sauser.USER_ID=:userId or raau.USER_ID= :userId or ((evl.ID  = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
				whereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND (tm.USER_ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId OR sauser.USER_ID=:userId or raau.USER_ID= :userId or ((evl.ID  = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1))";
			}
		}

		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND (((tm.USER_ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) OR sauser.USER_ID=:userId or ((evl.ID  = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1)) ";
				rfiWhereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND ((tm.USER_ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId) OR sauser.USER_ID=:userId or ((evl.ID  = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1))";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser,a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) AS TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID, MAX(a.EVL_USER_ID) AS EVL_USER_ID, MAX(a.ENVELOP_LEAD_EVALUATER) as ENVELOP_LEAD_EVALUATER, MAX(a.ENVELOP_STATUS) as ENVELOP_STATUS from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String geBizUnitCompletedRfxQuery(String userId,List<String> businessUnitIds) {
		String whereCondition=" ";
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND (((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or raau.USER_ID= :userId OR sauser.USER_ID=:userId or tm.USER_ID = :userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
				whereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER  = :userId or raau.USER_ID= :userId OR sauser.USER_ID=:userId or tm.USER_ID = :userId or evl.ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or  cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
			}
		}

		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND (((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER = :userId or tm.USER_ID = :userId OR sauser.USER_ID=:userId or evl.ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
				rfiWhereCondition += " OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND ((en.ENVELOP_OPENER = :userId or en.LEAD_EVALUATER  = :userId or tm.USER_ID = :userId OR sauser.USER_ID=:userId or evl.ID  = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId  or  cu.USER_ID = :userId) or (umu.USER_ID = :userId and umu.USER_UNMASKED = 0 and e.DISABLE_MASKING = 0) or (e.UNMASKED_BY = :userId and e.DISABLE_MASKING = 0)) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser,a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID, MAX(a.ENVELOP_OPENER) as ENVELOP_OPENER, MAX(a.ENVELOP_LEAD_EVALUATER) as ENVELOP_LEAD_EVALUATER, MAX(a.EVL_USER_ID) as EVL_USER_ID, MAX(a.EVL_CON_USER_ID) as EVL_CON_USER_ID, MAX(a.UN_USER_ID) as UN_USER_ID, MAX(a.UN_USER_UNMASKED) as UN_USER_UNMASKED, MAX(a.DISABLE_MASKING) as DISABLE_MASKING, MAX(a.UNMASKED_BY) as UNMASKED_BY from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, en.ENVELOP_OPENER as ENVELOP_OPENER, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, umu.USER_ID as UN_USER_ID, umu.USER_UNMASKED as UN_USER_UNMASKED, e.DISABLE_MASKING, e.UNMASKED_BY, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, en.ENVELOP_OPENER as ENVELOP_OPENER, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, umu.USER_ID as UN_USER_ID, umu.USER_UNMASKED as UN_USER_UNMASKED, e.DISABLE_MASKING, e.UNMASKED_BY, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, en.ENVELOP_OPENER as ENVELOP_OPENER, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, umu.USER_ID as UN_USER_ID, umu.USER_UNMASKED as UN_USER_UNMASKED, e.DISABLE_MASKING, e.UNMASKED_BY, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, en.ENVELOP_OPENER as ENVELOP_OPENER, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, umu.USER_ID as UN_USER_ID, umu.USER_UNMASKED as UN_USER_UNMASKED, e.DISABLE_MASKING, e.UNMASKED_BY, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, en.ENVELOP_OPENER as ENVELOP_OPENER, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, evl.ID as EVL_USER_ID, cu.USER_ID as EVL_CON_USER_ID, umu.USER_ID as UN_USER_ID, umu.USER_UNMASKED as UN_USER_UNMASKED, e.DISABLE_MASKING, e.UNMASKED_BY, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_RFT_UNMASKED_USER umu on e.ID = umu.EVENT_ID left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String getBizUnitCanceldRfxQuery(String userId,List<String> businessUnitIds) {
		String whereCondition="";
		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) ";
			}
		}
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR sauser.USER_ID=:userId) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser, a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFT_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFP_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFI_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFQ_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,bu.id AS BUSINESS_UNIT_ID, ";
		sql += " tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID  left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_ENV_OPEN_USERS eo on eo.ENVELOPE_ID = en.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID LEFT OUTER JOIN PROC_RFA_EVAL_CON_USERS cu ON cu.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String getBizUnitRfxQueryForPendingEvents(String userId, List<String> businessUnitIds) {
		String whereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId  OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId  OR sauser.USER_ID=:userId) ";
			}
		}
		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR sauser.USER_ID=:userId ) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR sauser.USER_ID=:userId) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy,  a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser, a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID from (";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE, ";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID ";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME,  e.CREATED_BY, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE, ";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE, ";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE, ";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.CREATED_DATE, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_START, e.REFERANCE_NUMBER, e.STATUS, e.TENANT_ID, e.EVENT_OWNER, bu.BUSINESS_UNIT_NAME, e.CREATED_BY, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE, ";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID  left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";

		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String getBizUnitActiveRfxQuery(String userId,List<String> businessUnitIds) {
		String whereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition += " AND ((e.EVENT_OWNER = :userId OR  tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				whereCondition += " AND (e.EVENT_OWNER = :userId OR  tm.USER_ID = :userId OR usr.USER_ID = :userId OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) ";
			}
		}
		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition += " AND ((e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId  OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds)) ";
			} else {
				rfiWhereCondition += " AND (e.EVENT_OWNER = :userId OR tm.USER_ID = :userId OR usr.USER_ID = :userId OR sauser.USER_ID=:userId ) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser, a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID from (";
		sql += "SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += " '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += " '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, ";
		sql += "  '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}

	protected String getBizUnitOngoingdRfxQuery(String userId,List<String> businessUnitIds) {
		//4105
		String whereCondition="";
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				whereCondition +=  "AND ((( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1) OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds))";
			} else {
				whereCondition += " AND (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1) OR raau.USER_ID= :userId OR sauser.USER_ID=:userId) ";
			}
		}

		String rfiWhereCondition="";
		//4105
		if (userId != null) {
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				rfiWhereCondition +=  "AND ((( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1) OR  sauser.USER_ID=:userId) OR e.BUSINESS_UNIT_ID IN (:businessUnitIds))";
			} else {
				rfiWhereCondition += " AND (( tm.USER_ID = :userId or e.EVENT_OWNER = :userId or usr.USER_ID = :userId ) or ((evl.ID = :userId or en.LEAD_EVALUATER = :userId) and en.ENVELOP_STATUS = 1) OR  sauser.USER_ID=:userId) ";
			}
		}
		String sql = "select distinct a.ID as id, a.EVENT_NAME as eventName, a.CREATED_DATE as createdDate, puser.LOGIN_ID as createdBy, a.MODIFIED_DATE as modifiedDate, a.EVENT_TYPE as type, a.EVENT_START as eventStart, a.EVENT_END as eventEnd, a.REFERANCE_NUMBER as referenceNumber, a.BUSINESS_UNIT_NAME as unitName, a.EVENT_ID as sysEventId, puser.USER_NAME as eventUser, a.BUSINESS_UNIT_ID, MAX(a.EVENT_OWNER) as EVENT_OWNER, MAX(a.TEAM_USER_ID) as TEAM_USER_ID, MAX(a.APPROVAL_USER_ID) as APPROVAL_USER_ID, MAX(a.EVL_USER_ID) as EVL_USER_ID, MAX(a.ENVELOP_LEAD_EVALUATER) as ENVELOP_LEAD_EVALUATER, MAX(a.ENVELOP_STATUS) as ENVELOP_STATUS from (";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFT.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFT_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFT_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFT_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFT_EVALUATOR_USER ev on ev.RFT_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID left outer join PROC_RFT_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFT_APPROVAL_USER usr on usr.RFT_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFT_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFT_AWARD_APPR_USER raau ON raau.RFT_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFT_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFT_SUSPENSION_APPR_USER sauser ON sauser.RFT_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFP.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFP_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFP_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFP_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFP_EVALUATOR_USER ev on ev.RFP_ENVELOPE_ID = en.ID  left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFP_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFP_APPROVAL_USER usr on usr.RFP_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFP_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFP_AWARD_APPR_USER raau ON raau.RFP_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFP_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFP_SUSPENSION_APPR_USER sauser ON sauser.RFP_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId  "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFI.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFI_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFI_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFI_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFI_EVALUATOR_USER ev on ev.RFI_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFI_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFI_APPROVAL_USER usr on usr.RFI_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFI_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFI_SUSPENSION_APPR_USER sauser ON sauser.RFI_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId "+rfiWhereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFQ.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFQ_EVENTS e  left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFQ_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFQ_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFQ_EVALUATOR_USER ev on ev.RFQ_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFQ_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFQ_APPROVAL_USER usr on usr.RFQ_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFQ_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFQ_AWARD_APPR_USER raau ON raau.RFQ_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFQ_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFQ_SUSPENSION_APPR_USER sauser ON sauser.RFQ_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId "+whereCondition;
		sql += " UNION";
		sql += " SELECT distinct e.ID, e.IS_BILLOFQUANTITY_REQ, e.BUDGET_AMOUNT, e.CREATED_DATE, e.BUYER_SET_DECIMAL, e.IS_DOCUMENT_REQ, e.EVENT_DESCRIPTION, e.EVENT_END, e.EVENT_ID, e.EVENT_NAME, e.EVENT_PUBLISH_DATE, e.EVENT_START, e.EVENT_VISIBILITY, e.HISTORICAL_AMOUNT, e.IS_MEETINGS_REQ, e.MODIFIED_DATE, e.PARTICIPATION_FEES, e.PAYMENT_TERM, e.IS_QUESTIONNAIRES_REQ, e.REFERANCE_NUMBER, e.STATUS, e.SUBMISSION_VALIDITY_DAYS, e.TENANT_ID, e.CURRENCY_ID, e.COST_CENTER, e.CREATED_BY, e.DELIVERY_ADDRESS, e.EVENT_OWNER, e.EVENT_CATEGORY, e.MODIFIED_BY, e.PARTICIPATION_FEE_CURRENCY, e.TEMPLATE_ID, e.IS_BQ_COMPLETED, e.IS_CQ_COMPLETED, e.IS_DOCUMENT_COMPLETED, e.IS_ENVELOP_COMPLETED, e.IS_EVENT_DETAIL_COMPLETED, e.IS_MEETINGS_COMPLETED, e.IS_SUMMARY_COMPLETED, e.IS_SUPPLIER_COMPLETED, e.ACTION_DATE, e.ACTION_BY, bu.BUSINESS_UNIT_NAME,";
		sql += " bu.id AS BUSINESS_UNIT_ID, tm.USER_ID as TEAM_USER_ID, usr.USER_ID as APPROVAL_USER_ID, evl.ID as EVL_USER_ID, en.LEAD_EVALUATER as ENVELOP_LEAD_EVALUATER, en.ENVELOP_STATUS as ENVELOP_STATUS, ";
		sql += " e.SUSPENSION_TYPE, '" + RfxTypes.RFA.getValue() + "' as EVENT_TYPE ";
		sql += " FROM PROC_RFA_EVENTS e left outer join PROC_BUSINESS_UNIT bu on e.BUSINESS_UNIT_ID = bu.ID left outer join PROC_RFA_ENVELOP en on en.EVENT_ID = e.ID left outer join PROC_RFA_TEAM tm on tm.EVENT_ID = e.ID left outer join PROC_RFA_EVALUATOR_USER ev on ev.RFA_ENVELOPE_ID = en.ID left outer join PROC_USER evl on ev.USER_ID = evl.ID  left outer join PROC_RFA_EVENT_APPROVAL app on app.EVENT_ID = e.ID left outer join PROC_RFA_APPROVAL_USER usr on usr.RFA_EVENT_APPROVAL_ID = app.ID";
		sql += " left outer join PROC_RFA_AWARD_APPROVAL raa on e.ID=raa.EVENT_ID left outer join PROC_RFA_AWARD_APPR_USER raau ON raau.RFA_EVENT_AWARD_APPR_ID=raa.ID ";
		sql += " left outer join PROC_RFA_SUSPENSION_APPROVAL sa ON sa.EVENT_ID=e.ID left outer join PROC_RFA_SUSPENSION_APPR_USER sauser ON sauser.RFA_EVENT_SUSP_APPR_ID=sa.ID ";
		sql += " where e.STATUS in (:status) and e.TENANT_ID = :tenantId "+whereCondition;
		sql += " ) a , PROC_USER puser where a.CREATED_BY = puser.ID";
		sql += " GROUP BY a.ID, a.EVENT_NAME, a.CREATED_DATE, puser.LOGIN_ID, a.MODIFIED_DATE, a.EVENT_TYPE, a.EVENT_START, a.EVENT_END, a.REFERANCE_NUMBER, a.BUSINESS_UNIT_NAME, a.EVENT_ID, puser.USER_NAME, a.BUSINESS_UNIT_ID ";
		return sql;
	}
	
}
