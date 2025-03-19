package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.Query;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPrPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Parveen
 */
@Repository
public class PrDaoImpl extends GenericDaoImpl<Pr, String> implements PrDao {

    private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

    @Autowired
    UserDao userDao;

    @Override
    public Pr findByPrId(String prId) {
        LOG.debug("prId:" + prId);
        StringBuilder hsql = new StringBuilder("from Pr p left outer join fetch p.template left outer join fetch p.supplier sp join fetch p.createdBy cb left outer join fetch p.modifiedBy mb left outer join fetch p.currency c left outer join fetch p.costCenter cc left outer join fetch p.prDocuments as pd left outer join fetch p.deliveryAddress as da left outer join fetch da.state as dst left outer join fetch dst.country left outer join fetch p.paymentTermes where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        Pr pr = (Pr) query.getSingleResult();
        return pr;
    }

    @Override
    public Pr findPrByPrId(String prId) {
        LOG.debug("prId:" + prId);
        StringBuilder hsql = new StringBuilder("from Pr p  where p.prId like :prId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        Pr pr = (Pr) query.getSingleResult();
        return pr;
    }

    @Override
    public Pr findPrForApprovalById(String prId) {
        StringBuilder hsql = new StringBuilder("from Pr p left outer join fetch p.prApprovals ap join fetch p.createdBy cb left outer join fetch p.modifiedBy mb where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        Pr pr = (Pr) query.getSingleResult();
        return pr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findEditorsByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select e from Pr p inner join p.prEditors e where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findViewersByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select v from Pr p inner join p.prViewers v where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        return query.getResultList();

    }

    @Override
    public Pr findPrSupplierByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select distinct p from Pr p left outer join fetch p.supplier sp left outer join fetch p.modifiedBy mb left outer join fetch p.prItems where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        Pr pr = (Pr) query.getSingleResult();
        pr = (Pr) getEntityManager().unwrap(SessionImplementor.class).getPersistenceContext().unproxy(pr);
        return pr;
    }

    @Override
    public PrContact getPrContactById(String contactId) {
        StringBuilder hsql = new StringBuilder("from PrContact pc where pc.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", contactId);
        PrContact prContact = (PrContact) query.getSingleResult();
        return prContact;
    }

    @Override
    public void deletePrContact(String contactId) {
        StringBuilder hsql = new StringBuilder("delete from PrContact pc where pc.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", contactId);
        query.executeUpdate();
    }

    @Override
    public boolean isExists(PrContact prContact, String prId) {
        StringBuilder hsql = new StringBuilder("select count(*) from PrContact pc where pc.contactName= :contactName and pc.pr.id = :prId");
        if (StringUtils.checkString(prContact.getId()).length() > 0) {
            hsql.append(" and pc.id <> :id");
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("contactName", prContact.getContactName());
        query.setParameter("prId", prId);
        if (StringUtils.checkString(prContact.getId()).length() > 0) {
            query.setParameter("id", prContact.getId());
        }
        LOG.debug("hsql :" + hsql);
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrComment> getAllPrCommentByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select distinct pc from PrComment pc inner join fetch pc.pr p left outer join fetch pc.createdBy  where p.id= :prId order by pc.createdDate");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        List<PrComment> prCommentList = query.getResultList();
        return prCommentList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrContact> findAllPrContactsByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select c from Pr p join p.prContacts c where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrApproval> getAllPrApprovalsByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select distinct pa from PrApproval pa left outer join fetch pa.approvalUsers au where pa.pr.id= :id order by au.actionDate desc");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventTeamMember> getTeamMembersForPr(String prId) {
        StringBuilder hsql = new StringBuilder("from PrTeamMember tm left outer join fetch tm.user u where tm.pr.id =:prId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PrTeamMember getPrTeamMemberByUserIdAndPrId(String prId, String userId) {
        StringBuilder hsql = new StringBuilder("from PrTeamMember tm where tm.pr.id =:prId and tm.user.id =:userId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        query.setParameter("userId", userId);
        List<PrTeamMember> uList = query.getResultList();
        if (CollectionUtil.isNotEmpty(uList)) {
            return uList.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllPendingPr(String userId, String tenantId, TableDataInput input) {
        final Query query = constructPrForTenantQuery(userId, tenantId, input, false, PrStatus.PENDING, null, null);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        List<Object[]> objects = query.getResultList();
        return getPrList(objects, PrStatus.PENDING);
    }

    @Override
    public long findTotalFilteredPendingPr(String tenantId, String userId, TableDataInput input) {
        final Query query = constructPrForTenantQuery(userId, tenantId, input, true, PrStatus.PENDING, null, null);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalPendingPr(String tenantId, String userId,List<String> businessUnitIds) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au left outer join p.businessUnit as bu where p.buyer.id = :tenantId and p.status = :status");
        LOG.info("TOTAL PENDING PR QUERY: " + hsql);
        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);
        LOG.info("TOTAL PENDING PR QUERY: " + hsql);
        query.setParameter("status", PrStatus.PENDING);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalDraftPr(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {

        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.businessUnit as bu where p.buyer.id = :tenantId and p.status = :status");
        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId)");
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");
        LOG.info("Possible problematic query 1: " + businessUnitIds);
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);
        LOG.info("Possible problematic query 2: " + businessUnitIds);
        query.setParameter("status", PrStatus.DRAFT);
        LOG.info("Total Draft Pr Query: " + hsql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllDraftPr(String userId, String tenantId, TableDataInput input) {
        LOG.debug("userId :" + userId);
        final Query query = constructPrForTenantQuery(userId, tenantId, input, false, PrStatus.DRAFT, null, null);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        List<Object[]> objects = query.getResultList();
        return getPrList(objects, PrStatus.DRAFT);
    }

    @Override
    public long findTotalFilteredDraftPr(String userId, String tenantId, TableDataInput input) {
        final Query query = constructPrForTenantQuery(userId, tenantId, input, true, PrStatus.DRAFT, null, null);
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
        final Query query = constructPrForTenantQuery(userId, tenantId, input, false, PrStatus.APPROVED, startDate, endDate);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        List<Object[]> objects = query.getResultList();
        return getPrList(objects, PrStatus.APPROVED);
    }

    @Override
    public long findTotalFilteredPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
        final Query query = constructPrForTenantQuery(userId, tenantId, input, true, PrStatus.APPROVED, startDate, endDate);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalApprovedPrs(String tenantId, String userId,List<String> businessUnitIds) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm");
        hsql.append(" left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau ");
        hsql.append(" left outer join pau.user as au  left outer join p.businessUnit as bu  where p.buyer.id = :buyerId and p.status = :status");

        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");
        LOG.info("Possible problematic query 5: " + businessUnitIds);
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);
        query.setParameter("status", PrStatus.APPROVED);
        LOG.info("TOTAL APPROVED PR QUERY: " + hsql);
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> getAllPrPoFromGlobalSearch(String searchVal, String tenantId, boolean isSupplier, String userId, String status, String type, Date startDate, Date endDate) {
        LOG.info("searchValue:" + searchVal + " isSupplier " + isSupplier + " tenantId : " + tenantId + " userId : " + userId);

        List<PrStatus> statusList = new ArrayList<PrStatus>();

        if (StringUtils.checkString(status).length() > 0) {
            String[] statusArray = status.split(",");
            if (statusArray != null && statusArray.length > 0) {
                for (String ss : statusArray) {
                    statusList.add(PrStatus.valueOf(ss));
                }
            }
        }
        StringBuilder hsql;
        if (!isSupplier) {
            hsql = new StringBuilder("select p from Pr p left outer join fetch p.modifiedBy mb join fetch p.createdBy cb left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au left outer join fetch p.businessUnit as bu left outer join fetch p.supplier as fs where p.buyer.id = :tenantId ");
        } else {
            hsql = new StringBuilder("select p from Pr p left outer join fetch p.businessUnit as bu left outer join fetch p.supplier as fs where fs.supplier.id = :tenantId ");
        }

        if (!isSupplier) {
            if (userId != null) {
                hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
            }
        }

        if (StringUtils.checkString(searchVal).length() > 0) {
            LOG.info(searchVal);
            if (searchVal.substring(0, 2).equals("PO")) {
                hsql.append(" and ((upper(p.poNumber) like :searchVal) or (upper(p.referenceNumber) like :searchVal) or (upper(p.name) like :searchVal) or (upper(p.description) like :searchVal) or (upper(bu.unitName) like :searchVal)) ");

            } else {
                hsql.append(" and ((upper(p.prId) like :searchVal) or (upper(p.referenceNumber) like :searchVal) or (upper(p.name) like :searchVal) or (upper(p.description) like :searchVal) or (upper(bu.unitName) like :searchVal)) ");
            }
        }
        if (!isSupplier) {
            if (CollectionUtil.isNotEmpty(statusList)) {
                hsql.append(" and (p.status in (:statusList)) ");
            }
        } else {
            hsql.append(" and (p.status =:status) ");
        }
        if (startDate != null && endDate != null) {
            hsql.append(" and ((p.prCreatedDate between :startDate and :endDate) or (p.poCreatedDate between :startDate and :endDate)) ");
        }
        LOG.info("HSQL :" + hsql);
        Query query = getEntityManager().createQuery(hsql.toString());

        query.setParameter("tenantId", tenantId);
        if (!isSupplier) {
            if (userId != null) {
                query.setParameter("userId", userId);
            }
        }
        if (StringUtils.checkString(searchVal).length() > 0) {
            query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
        }
        if (!isSupplier) {
            if (CollectionUtil.isNotEmpty(statusList)) {
                query.setParameter("statusList", statusList);
            }
        } else {
            query.setParameter("status", PrStatus.APPROVED);
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        List<Pr> list = query.getResultList();
        LinkedHashSet<Pr> resultList = new LinkedHashSet<Pr>();
        for (Pr pr : list) {
            resultList.add(pr);
        }
        return new ArrayList<Pr>(resultList);
    }

    /**
     * @param userId
     * @param tenantId
     * @param tableParams
     * @param isCount
     * @param status
     * @param startDate
     * @param endDate
     * @param
     * @return
     */
    private Query constructPrForTenantQuery(String userId, String tenantId, TableDataInput tableParams, boolean isCount, PrStatus status, Date startDate, Date endDate) {

        String hql = "";
        String orderSql = "";
        String querySql = "";
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();
                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        orderSql += " sup.companyName " + dir + ", p.supplierName " + dir;
                        querySql += " sup.companyName " + ", p.supplierName ";
                    } else {

                        orderSql += " p." + orderColumn + " " + dir + ",";
                        querySql += " p." + orderColumn + " " + ",";
                    }
                }
                if (orderSql.lastIndexOf(",") == orderSql.length() - 1) {
                    orderSql = orderSql.substring(0, orderSql.length() - 1);
                    querySql = querySql.substring(0, querySql.length() - 1);
                }
            } else {
                // by default order by created date
                if (status != PrStatus.APPROVED) {
                    orderSql += " p.prCreatedDate desc";
                    querySql += " p.prCreatedDate ";
                } else {
                    orderSql += " p.poCreatedDate desc";
                    querySql += " p.poCreatedDate ";
                }
            }
        }

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql += "select count(distinct p) ";
        } else {
            if (status == PrStatus.APPROVED) {
                hql += "select distinct p.id, p.name, p.lockBudget, p.modifiedDate, p.grandTotal, cb, mb, p.prCreatedDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName, p.supplierName, p.status,p.isPoReportSent,c.currencyCode, " + querySql;
            } else {
                hql += "select distinct p.id, p.name, p.lockBudget, p.modifiedDate, p.grandTotal, cb, mb, p.prCreatedDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.prId,  bu.unitName,sup.companyName, p.supplierName, p.status, p.isPoReportSent,c.currencyCode, " + querySql;
            }
        }

        hql += " from Pr p ";

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b  left outer join p.currency as c";
        }

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred ";
        if (status == PrStatus.DRAFT && userId != null) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId) ";
        }
        if ((status == PrStatus.PENDING || status == PrStatus.APPROVED) && userId != null) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
        }

        boolean isSelectOn = false;
        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {
                    hql += " and p.status in (:status) ";
                    isSelectOn = true;
                } else {
                    if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
                        hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
                    } else {
                        hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

                    }
                }
            }
        }

        if (!isSelectOn) {
            hql += " and p.status = :status ";
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  p.poCreatedDate between :startDate and :endDate ";
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
//			List<OrderParameter> orderList = tableParams.getOrder();
//			if (CollectionUtil.isNotEmpty(orderList)) {
            hql += " order by " + orderSql;
//				for (OrderParameter order : orderList) {
//					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
//					String dir = order.getDir();
//
//					if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
//						hql += " sup.companyName " + dir + ", p.supplierName " + dir;
//					} else {
//
//						hql += " p." + orderColumn + " " + dir + ",";
//					}
//				}
//				if (hql.lastIndexOf(",") == hql.length() - 1) {
//					hql = hql.substring(0, hql.length() - 1);
//				}
//			} else {
//				// by default order by created date
//				if (status != PrStatus.APPROVED) {
//					hql += " order by p.prCreatedDate desc ";
//				} else {
//					hql += " order by p.poCreatedDate desc ";
//				}
//			}
        }
        System.out.println(hql);
        LOG.info("*********prdraftsQuery*********** " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        if (status == PrStatus.DRAFT && userId != null) {
            query.setParameter("userId", userId);
        }

        if ((status == PrStatus.PENDING || status == PrStatus.APPROVED) && userId != null) {
            query.setParameter("userId", userId);
        }
        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {
                    if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
                        List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
                        query.setParameter("status", statuses);
                    } else {
                        query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                    }
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        if (!isSelectOn) {
            query.setParameter("status", status);
        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        query.setParameter("erpPrTransferred", Boolean.FALSE);
        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> searchPrByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo) {
        StringBuffer sb = new StringBuffer("select distinct e from Pr e left outer join fetch e.createdBy cb where e.buyer.id = :tenantId ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(e.name) like :searchValue or upper(e.referenceNumber) like :searchValue) ");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id = :userId ");
        }

        sb.append(" order by e.prCreatedDate desc");

        final Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }

        if (StringUtils.checkString(pageNo).length() > 0) {
            LOG.debug("pageNo :" + pageNo);
            query.setFirstResult(Integer.parseInt(pageNo) * 10);
            query.setMaxResults(10);
        }
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllPrForTenant(String loggedInUserTenantId, String userId, TableDataInput input) {
        String hql = "select distinct new Pr(p.id, p.name, p.referenceNumber, p.prCreatedDate, cb) from Pr p left outer join p.createdBy as cb left outer join p.buyer b where p.buyer.id = :tenantId ";
        if (StringUtils.checkString(userId).length() > 0) {
            hql += " and cb.id = :userId ";
        }
        hql += " order by p.prCreatedDate ";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", loggedInUserTenantId);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public EventPermissions getUserPemissionsForPr(String userId, String prId) {
        LOG.debug("userId :" + userId + " prId: " + prId);
        EventPermissions permissions = new EventPermissions();

        User loggedInUser = userDao.findById(userId);
        LOG.info("Login id********:" + loggedInUser.getLoginId());
        if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
            LOG.info("*************Checking userType");
            permissions.setApproverUser(true);
        }

        // Event Owner
        Pr pr = findById(prId);
        if (pr != null && pr.getStatus() == PrStatus.DRAFT) {
            permissions.setPrDraft(true);
            permissions.setPrId(pr.getId());
        }
        if (pr.getCreatedBy().getId().equals(userId)) {
            permissions.setOwner(true);
        } else {
            // Viewer Editor
            List<PrTeamMember> teamMembers = pr.getPrTeamMembers();
            for (EventTeamMember member : teamMembers) {
                if (member.getUser().getId().equals(userId)) {
                    if (member.getTeamMemberType() == TeamMemberType.Viewer) {
                        permissions.setViewer(true);
                    }
                    if (member.getTeamMemberType() == TeamMemberType.Editor) {
                        permissions.setEditor(true);
                        permissions.setViewer(false);
                        // break;
                    }
                    if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
                        permissions.setEditor(false);
                        permissions.setViewer(false);
                        permissions.setOwner(true);
                        break;
                    }
                }
            }
        }

        // Approver
        List<PrApproval> approvals = pr.getPrApprovals();
        for (PrApproval approval : approvals) {
            if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
                List<PrApprovalUser> users = approval.getApprovalUsers();
                for (PrApprovalUser user : users) {
                    if (user.getUser().getId().equals(userId)) {
                        permissions.setApprover(true);
                        if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
                            permissions.setActiveApproval(true);
                            break;
                        }
                    }
                }
            }
        }
        LOG.debug("permissions : " + permissions.toLogString());
        return permissions;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrComment> findAllPrCommentsByPrId(String prId) {
        StringBuilder hsql = new StringBuilder("select distinct pc from PrComment pc inner join fetch pc.pr p left outer join fetch pc.createdBy where p.id= :prId order by pc.createdDate");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        List<PrComment> prCommentList = query.getResultList();
        return prCommentList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> getAllPrByLoginId(String loginId) {
        String sql = "select p from Pr p inner join p.createdBy cr where cr.id =:loginId";
        final Query query = getEntityManager().createQuery(sql);
        query.setParameter("loginId", loginId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventTeamMember> getPlainTeamMembersForPr(String prId) {
        final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from PrTeamMember tm left outer join tm.user u where tm.pr.id =:prId");
        query.setParameter("prId", prId);
        List<EventTeamMember> list = query.getResultList();
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PrApprovalUser> fetchAllApprovalUsersByPrId(String id) {
        final Query query = getEntityManager().createQuery("select distinct apu from PrApproval pa left outer join pa.approvalUsers apu inner join apu.user us where pa.pr.id =:prId");
        query.setParameter("prId", id);
        return query.getResultList();
    }

    @Override
    public boolean checkTemplateStatusForPr(String prId) {
        final Query query = getEntityManager().createQuery("select count(p) from Pr p left outer join p.template pt where pt is not null and pt.status= :status  and p.id =:prId");
        query.setParameter("status", Status.INACTIVE);
        query.setParameter("prId", prId);
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    @Override
    public String getBusineessUnitname(String prId) {
        StringBuilder hsql = new StringBuilder("select bu.displayName from Pr p left outer join p.businessUnit as bu where p.id =:prId ");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        return (String) query.getSingleResult();
    }

    @Override
    public Pr getMobilePrDetails(String id) {
        StringBuilder hsql = new StringBuilder("select p from Pr p left outer join fetch p.createdBy cb left outer join fetch p.currency pc left outer join fetch p.prItems pi where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", id);
        return (Pr) query.getSingleResult();
    }

    @Override
    public long findPrPoCountForTenant(String tenantId, PrStatus status, RequestParamPojo filter) throws Exception {
        String hsql = "select Count(*) from Pr p where p.buyer.id= :tenantId ";
        if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
            hsql += " and upper(p.currency.currencyCode) = :currencyCode ";
        }
        if (status == PrStatus.APPROVED && filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
            hsql += " and p.poCreatedDate between :startDate and :endDate ";
        } else if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
            hsql += " and p.prCreatedDate between :startDate and :endDate ";
        }
        if (status != null) {
            hsql += " and p.status = :status ";
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
        if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
            query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
        }
        if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            LOG.info("Start Date :" + df.parse(filter.getStartDate()));
            Date endDate = df.parse(filter.getEndDate());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endDate = endCal.getTime();
            LOG.info("end Date :" + endDate);

            query.setParameter("startDate", df.parse(filter.getStartDate()));
            query.setParameter("endDate", endDate);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public BigDecimal findPrPoValueForTenant(String tenantId, PrStatus status, RequestParamPojo filter) throws Exception {
        String hsql = "select Sum(p.grandTotal) from Pr p where p.buyer.id= :tenantId ";
        if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
            hsql += " and upper(p.currency.currencyCode) = :currencyCode ";
        }
        if (status == PrStatus.APPROVED && filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
            hsql += " and p.poCreatedDate between :startDate and :endDate ";
        } else if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
            hsql += " and p.prCreatedDate between :startDate and :endDate ";
        }
        if (status != null) {
            hsql += " and p.status = :status ";
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
        if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
            query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
        }
        if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            LOG.info("Start Date :" + df.parse(filter.getStartDate()));
            Date endDate = df.parse(filter.getEndDate());
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR, 23);
            endCal.set(Calendar.MINUTE, 59);
            endCal.set(Calendar.SECOND, 59);
            endDate = endCal.getTime();
            LOG.info("end Date :" + endDate);

            query.setParameter("startDate", df.parse(filter.getStartDate()));
            query.setParameter("endDate", endDate);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        BigDecimal value = (BigDecimal) query.getSingleResult();
        return value != null ? value : new BigDecimal(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findPrByIds(String[] prArr) {
        String sql = "select p from Pr p inner join p.createdBy cr left outer join p.businessUnit as bu left outer join p.supplier as fs left outer join fs.supplier as sup where p.id in :prIds order by p.poCreatedDate desc ";
        final Query query = getEntityManager().createQuery(sql);
        query.setParameter("prIds", Arrays.asList(prArr));
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllSearchFilterPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses) {
        final Query query = constructPrSearchFilterForTenantQuery(userId, tenantId, input, false, startDate, endDate, statuses);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    /**
     * @param userId
     * @param tenantId
     * @param tableParams
     * @param isCount
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    private Query constructPrSearchFilterForTenantQuery(String userId, String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, List<PrStatus> statuses) {

        LOG.info("--------------------------------------------------------------------------------");
        String hql = "";

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql += "select count(distinct p) ";
        } else {
            if (statuses.contains(PrStatus.APPROVED) || statuses.contains(PrStatus.TRANSFERRED) || statuses.contains(PrStatus.DELIVERED) || statuses.contains(PrStatus.CANCELED)) {
                hql += "select distinct NEW Pr(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.poCreatedDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName,p.supplierName, p.status,p.isPoReportSent) ";
            } else {
                hql += "select distinct NEW Pr(p.id, p.name, p.prCreatedDate, p.modifiedDate, p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.prId,  bu.unitName, sup.companyName, p.supplierName, p.status, p.isPoReportSent) ";
            }
        }

        hql += " from Pr p ";

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b ";
        }

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs";

        hql += " left outer join fs.supplier as sup";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId ";
        if (statuses.contains(PrStatus.DRAFT) && userId != null) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId) ";
        }
        if ((statuses.contains(PrStatus.PENDING) || statuses.contains(PrStatus.APPROVED)) && userId != null) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
        }

        boolean isSelectOn = false;
        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {
                    hql += " and p.status in (:status) ";
                    isSelectOn = true;
                } else {
                    if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
                        hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
                    } else {
                        hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

                    }
                }
            }
        }

        if (!isSelectOn) {
            hql += " and p.status in (:statuses) ";
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  p.poCreatedDate between :startDate and :endDate ";
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql += " order by ";
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();
                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        hql += " sup.companyName " + dir + ", p.supplierName " + dir;
                    } else {

                        hql += " p." + orderColumn + " " + dir + ",";
                    }
                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = hql.substring(0, hql.length() - 1);
                }
            } else {
                // by default order by created date

                if (statuses.contains(PrStatus.APPROVED) || statuses.contains(PrStatus.TRANSFERRED) || statuses.contains(PrStatus.DELIVERED) || statuses.contains(PrStatus.CANCELED)) {
                    hql += " order by p.poCreatedDate desc ";
                } else {
                    hql += " order by p.prCreatedDate desc ";
                }
            }
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        if (statuses.contains(PrStatus.DRAFT) && userId != null) {
            query.setParameter("userId", userId);
        }

        if ((statuses.contains(PrStatus.PENDING) || statuses.contains(PrStatus.APPROVED)) && userId != null) {
            query.setParameter("userId", userId);
        }
        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {
                    if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
                        List<PrStatus> statuss = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
                        query.setParameter("status", statuss);
                    } else {
                        query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                    }
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        if (!isSelectOn) {
            query.setParameter("statuses", statuses);
        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        return query;
    }

    @Override
    public long findTotalSearchFilterPo(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> statuses) {
        final Query query = constructPrSearchFilterForTenantQuery(userId, tenantId, input, true, startDate, endDate, statuses);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pr getPrForErpById(String prId) {
        StringBuilder hsql = new StringBuilder("select p from Pr p left outer join fetch p.createdBy cb left outer join fetch p.costCenter cc left outer join fetch p.currency pc left outer join fetch p.supplier left outer join fetch p.prItems pi where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        List<Pr> prList = query.getResultList();
        if (CollectionUtil.isNotEmpty(prList)) {
            return prList.get(0);
        } else {
            return null;
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public Pr findPrBySystemGeneratedPrIdAndTenantId(String prId, String tenantId) {
        StringBuilder hsql = new StringBuilder("select distinct p from Pr p where upper(p.prId) = :prId and p.buyer.id = :tenantId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId.toUpperCase());
        query.setParameter("tenantId", tenantId);
        List<Pr> prList = query.getResultList();
        if (CollectionUtil.isNotEmpty(prList)) {
            return prList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pr findPrByErpDocNo(String prDocNo) {
        StringBuilder hsql = new StringBuilder("select distinct p from Pr p where upper(p.erpDocNo) = :erpDocNo");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("erpDocNo", prDocNo.toUpperCase());
        List<Pr> prList = query.getResultList();
        if (CollectionUtil.isNotEmpty(prList)) {
            return prList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public long findTotalPrTransfer(String tenantId, String userId,List<String> businessUnitIds){
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu ");
        hsql.append(" left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau ");
        hsql.append(" left outer join pau.user as au  left outer join p.businessUnit as bu  where p.buyer.id = :buyerId and p.status = :status");

        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");
        LOG.info("Possible problematic query 7: " + businessUnitIds);
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);
        LOG.info("Possible problematic query 8: " + businessUnitIds);
        query.setParameter("status", PrStatus.TRANSFERRED);

        LOG.info("Total Transfer PR query:" + hsql);

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalCompletePr(String tenantId, String userId,List<String> businessUnitIds) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au  left outer join p.businessUnit as bu  where p.buyer.id = :tenantId and p.status = :status");
        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");


        LOG.info("Total Complete Query: " + hsql);

        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);
        query.setParameter("status", PrStatus.COMPLETE);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalCancelledPr(String tenantId, String userId,List<String> businessUnitIds) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au  left outer join p.businessUnit as bu  where p.buyer.id = :tenantId and p.status = :status");

        // if not admin
//        if (userId != null) {
//            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
//        }


        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            hsql.append(" AND bu.id IN (:businessUnitIds) ");

        LOG.info("Total Cancelled Query: " + hsql);
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
//        if (userId != null) {
//            query.setParameter("userId", userId);
//        }


        if (businessUnitIds != null && !businessUnitIds.isEmpty())
            query.setParameter("businessUnitIds", businessUnitIds);


        query.setParameter("status", PrStatus.CANCELED);
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
        final Query query = constructPrTransferForTenantQuery(userId, tenantId, input, false);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public long findTotalFilteredPoTransfer(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate) {
        final Query query = constructPrTransferForTenantQuery(userId, tenantId, input, true);
        return ((Number) query.getSingleResult()).longValue();
    }

    private Query constructPrTransferForTenantQuery(String userId, String tenantId, TableDataInput tableParams, boolean isCount) {

        String hql = "";

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql += "select count(distinct p) ";
        } else {
            hql += "select distinct NEW Pr(p.id, p.name, p.prCreatedDate, p.modifiedDate,p.isPo, p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.prId,  bu.unitName, sup.companyName, p.supplierName, p.status, p.isPoReportSent) ";
        }

        hql += " from Pr p ";

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b ";
        }

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs left outer join fs.supplier as sup";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred ";

        if (userId != null) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
        }

        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
                    hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
                } else {
                    hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

                }
            }
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql += " order by ";
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();
                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        hql += " sup.companyName " + dir + ", p.supplierName " + dir;
                    } else {

                        hql += " p." + orderColumn + " " + dir + ",";
                    }
                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = hql.substring(0, hql.length() - 1);
                }
            } else {
                // by default order by created date
                hql += " order by p.prCreatedDate desc ";
            }
        }

        LOG.info("HQL : +++++++++++++++++++++++++++++++++++++++++++++++++++++" + hql);

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        if (userId != null) {
            query.setParameter("userId", userId);
        }
        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
            }
        }
        query.setParameter("erpPrTransferred", Boolean.TRUE);
        return query;
    }

    @Override
    public boolean isExistsPrId(String tenantId, String prId) {
        StringBuilder hsql = new StringBuilder("select count(p) from Pr p  where p.prId = :prId and p.buyer.id = :tenantId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("tenantId", tenantId);
        query.setParameter("prId", prId);
        return (((Number) query.getSingleResult()).longValue() > 0);
    }

    @Override
    public void deletePrContactsByPrId(String prId) {
        Query query = getEntityManager().createQuery("delete from PrContact con where con.pr.id = :id");
        query.setParameter("id", prId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllSearchFilterPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType) {
        final Query query = constructPrReportSearchFilterForTenantQuery(userId, tenantId, input, false, prStatus, startDate, endDate,selectedStatus,prType, null);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public List<Pr> findAllSearchFilterPrBizUnit(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType,List<String> businessUnitIds) {
        final Query query = constructPrReportSearchFilterForBizUnitQuery(userId, tenantId, input, false, prStatus, startDate, endDate,selectedStatus,prType,businessUnitIds);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    /**
     * @param endDate
     * @param startDate
     * @param approved
     * @param b
     * @param input
     * @param tenantId
     * @param userId
     * @param userId
     * @param tenantId
     * @param tableParams
     * @param isCount
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    private Query constructPrReportSearchFilterForTenantQuery(
            String userId, String tenantId, TableDataInput tableParams,
            boolean isCount, List<PrStatus> prStatus, Date startDate, Date endDate,
            String selectedStatus, String prType, List<String> businessUnitIds) {
        StringBuilder hql = new StringBuilder("select ");

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql.append("count(distinct p) ");
        } else {

            hql.append("distinct NEW Pr(p.id, p.name, p.lockBudget, p.prCreatedDate, p.modifiedDate, " +
                    "p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, " +
                    "p.description, p.prId,  bu.unitName, p.status, p.isPoReportSent, " +
                    "c.currencyCode, sup.companyName, p.supplierName, p.poNumber) ");
        }
        hql.append(" from Pr p ");

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql.append(" join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.currency as c ");
        }
        hql.append(" left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ");
        hql.append(" left outer join p.prTeamMembers as tm left outer join tm.user as tmu ");
        hql.append(" left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ");
        hql.append(" where p.buyer.id = :tenantId  ");



        if(prType.equals("ME")){
            if (StringUtils.checkString(userId).length() > 0) {
                hql.append(" AND (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
            }
        }else{
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql.append(" AND bu.id IN (:businessUnitIds) ");
            }
        }

        boolean isSelectOn = false;
        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                LOG.info("-------------------------------------");
                if (cp.getData().equals("status")) {
                    LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
                    if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                        hql.append(" and ( ( p.erpPrTransferred = true and p.status not in (:statuses) ) or p.status in (:status) )");
                    } else {
                        hql.append(" and p.status in (:status) ");
                    }
                    isSelectOn = true;
                } else {
                    if (cp.getData().equalsIgnoreCase("supplier.fullName")) {
                        hql.append(" and upper(sup.companyName) like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    } else if (cp.getData().equalsIgnoreCase("approvedBy.name")) {
                        hql.append(" and upper(au.name) like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    } else {
                        hql.append(" and upper(p.")
                                .append(cp.getData()).append(") like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    }
                    LOG.info(cp.getData());
                }
            }
        }
        if(selectedStatus !=null){
            isSelectOn=true;
            hql.append(" and p.status = :status ");
        }
        if (!isSelectOn) {
            LOG.info("-------------------here------------------ in :status");
            hql.append(" and p.status in (:status) ");
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql.append(" and  p.prCreatedDate between :startDate and :endDate ");
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql.append(" order by ");
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();

                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        hql.append(" sup.companyName ").append(dir).append(", p.supplierName ").append(dir);
                    } else {
                        if (orderColumn.equals("approvedBy.name")) {
                            hql.append("   au.name ").append(dir).append(",");
                        } else if (orderColumn.equals("prApprovedDate")) {
                            hql.append("   pau.actionDate ").append(dir).append(",");
                        } else {
                            hql.append(" p.").append(orderColumn).append(" ").append(dir).append(",");
                        }
                    }
                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = new StringBuilder(hql.substring(0, hql.length() - 1));
                }
            } else {
                // by default order by created date
                hql.append(" order by p.prCreatedDate desc ");
            }
        }
        LOG.info("HQL --------------------->: " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);

        if(prType.equals("ME")){
            if (StringUtils.checkString(userId).length() > 0)
                query.setParameter("userId", userId);
        }else {
            if (businessUnitIds != null && !businessUnitIds.isEmpty())
                query.setParameter("businessUnitIds", businessUnitIds);
        }



        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {

                    if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
                        LOG.info("-----------------------ALL-------------------------------");
                        List<PrStatus> statuses = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
                        query.setParameter("status", statuses);
                    } else {
                        LOG.info("-----------------------OTHER-------------------------------");
                        if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                            List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.DELIVERED);
                            query.setParameter("statuses", statuses);
                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        } else {

                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        }

                    }
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    LOG.info(cp.getSearch().getValue());
                }
            }
        }
        if(selectedStatus !=null){
            query.setParameter("status", PrStatus.fromString(selectedStatus));
        }
        else{
            List<PrStatus> statuses = Arrays.asList(PrStatus.DRAFT, PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING, PrStatus.COMPLETE);
            query.setParameter("status", statuses);
        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        return query;
    }

    private Query constructPrReportSearchFilterForBizUnitQuery(
            String userId, String tenantId, TableDataInput tableParams,
            boolean isCount, List<PrStatus> prStatus, Date startDate, Date endDate,
            String selectedStatus, String prType, List<String> businessUnitIds) {
        LOG.info("help help why this used to be ok for 4105 : "+businessUnitIds);
        StringBuilder hql = new StringBuilder("select ");

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql.append("count(distinct p) ");
        } else {

            hql.append("distinct NEW Pr(p.id, p.name, p.lockBudget, p.prCreatedDate, p.modifiedDate, " +
                    "p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, " +
                    "p.description, p.prId,  bu.unitName, p.status, p.isPoReportSent, " +
                    "c.currencyCode, sup.companyName, p.supplierName, p.poNumber) ");
        }
        hql.append(" from Pr p ");

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql.append(" join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.currency as c ");
        }
        hql.append(" left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ");
        hql.append(" left outer join p.prTeamMembers as tm left outer join tm.user as tmu ");
        hql.append(" left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ");
        hql.append(" where p.buyer.id = :tenantId  ");


        if(prType.equals("ME")){
            if (StringUtils.checkString(userId).length() > 0) {
                hql.append(" AND (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
            }
        }else{
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql.append(" AND bu.id IN (:businessUnitIds) ");
            }
        }

        boolean isSelectOn = false;
        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                LOG.info("-------------------------------------");
                if (cp.getData().equals("status")) {
                    LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
                    if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                        hql.append(" and ( ( p.erpPrTransferred = true and p.status not in (:statuses) ) or p.status in (:status) )");
                    } else {
                        hql.append(" and p.status in (:status) ");
                    }
                    isSelectOn = true;
                } else {
                    if (cp.getData().equalsIgnoreCase("supplier.fullName")) {
                        hql.append(" and upper(sup.companyName) like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    } else if (cp.getData().equalsIgnoreCase("approvedBy.name")) {
                        hql.append(" and upper(au.name) like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    } else {
                        hql.append(" and upper(p.")
                                .append(cp.getData()).append(") like (:")
                                .append(cp.getData().replace(".", ""))
                                .append(") ");
                    }
                    LOG.info(cp.getData());
                }
            }
        }
        if(selectedStatus !=null){
            isSelectOn=true;
            hql.append(" and p.status = :status ");
        }
        if (!isSelectOn) {
            LOG.info("-------------------here------------------ in :status");
            hql.append(" and p.status in (:status) ");
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql.append(" and  p.prCreatedDate between :startDate and :endDate ");
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql.append(" order by ");
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();

                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        hql.append(" sup.companyName ").append(dir).append(", p.supplierName ").append(dir);
                    } else {
                        if (orderColumn.equals("approvedBy.name")) {
                            hql.append("   au.name ").append(dir).append(",");
                        } else if (orderColumn.equals("prApprovedDate")) {
                            hql.append("   pau.actionDate ").append(dir).append(",");
                        } else {
                            hql.append(" p.").append(orderColumn).append(" ").append(dir).append(",");
                        }
                    }
                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = new StringBuilder(hql.substring(0, hql.length() - 1));
                }
            } else {
                // by default order by created date
                hql.append(" order by p.prCreatedDate desc ");
            }
        }
        LOG.info("HQL --------------------->: " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);

        if(prType.equals("ME")){
            if (StringUtils.checkString(userId).length() > 0)
                query.setParameter("userId", userId);
        }else {
            if (businessUnitIds != null && !businessUnitIds.isEmpty())
                query.setParameter("businessUnitIds", businessUnitIds);
        }

        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {

                    if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
                        LOG.info("-----------------------ALL-------------------------------");
                        List<PrStatus> statuses = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
                        query.setParameter("status", statuses);
                    } else {
                        LOG.info("-----------------------OTHER-------------------------------");
                        if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                            List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.DELIVERED);
                            query.setParameter("statuses", statuses);
                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        } else {

                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        }

                    }
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    LOG.info(cp.getSearch().getValue());
                }
            }
        }
        if(selectedStatus !=null){
            query.setParameter("status", PrStatus.fromString(selectedStatus));
        }
        else{
            List<PrStatus> statuses = Arrays.asList(PrStatus.DRAFT, PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING, PrStatus.COMPLETE);
            query.setParameter("status", statuses);
        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public long findTotalFilteredPr(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus,String selectedStatus,String prType,List<String> businessUnitIds) {
        final Query query = constructPrReportSearchFilterForTenantQuery(userId, tenantId, input, true, prStatus, startDate, endDate,selectedStatus, prType, businessUnitIds);
        return ((Number) query.getSingleResult()).longValue();

    }

    @Override
    public long findTotalPr(String tenantId, String userId) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au where p.buyer.id = :buyerId ");
        // if not admin
        if (userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);
        if (userId != null) {
            query.setParameter("userId", userId);
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findSupplierAllPo(String tenantId) {

        String hql = new String("select distinct p from Pr p left outer join fetch p.createdBy cb left outer join fetch p.costCenter cc left outer join fetch p.currency pc left outer join fetch p.supplier fs left outer join fetch p.prItems pi left outer join fetch p.buyer b ");
        hql += " where fs.supplier.id = :tenantId and p.status not in (:status)  and p.isPo = true and p.poCreatedDate is not null and  p.poNumber is not null";
        List<PrStatus> statuses = Arrays.asList(PrStatus.CANCELED, PrStatus.DRAFT);
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("status", statuses);
        query.setParameter("tenantId", tenantId);
        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findBuyerAllPo(String tenantId, boolean isPo) {

        String hql = new String("select distinct p from Pr p left outer join fetch p.createdBy cb left outer join fetch p.costCenter cc left outer join fetch p.currency pc left outer join fetch p.supplier fs left outer join fetch p.prItems pi left outer join fetch p.buyer b ");
        hql += " where b.id = :tenantId  and p.status not in (:status)  ";

        if (isPo) {
            hql += " and p.isPo = true and p.poCreatedDate is not null and  p.poNumber is not null ";
        }

        Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", tenantId);
        List<PrStatus> statuses = Arrays.asList(PrStatus.CANCELED, PrStatus.DRAFT);
        query.setParameter("status", statuses);
        return query.getResultList();

    }

    @Override
    public void deletePrDoc(String id) {

        StringBuilder hsql = new StringBuilder("delete from PrDocument pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", id);
        query.executeUpdate();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPrIdList(String tenantId) {
        String hql = new String("select id from Pr where buyer.id = :tenantId ");
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", tenantId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPrAprovalIdList(String prId) {
        String hql = new String("select id from PrApproval where pr.id = :prId ");
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("prId", prId);
        return query.getResultList();
    }

    @Override
    public void removePrApprovalUserByPrApprove(String prApproveId) {
        StringBuilder hsql = new StringBuilder("delete from PrApprovalUser pc where pc.approval.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prApproveId);
        query.executeUpdate();

    }

    @Override
    public void removePrApproval(String prId) {
        StringBuilder hsql = new StringBuilder("delete from PrApproval pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();

    }

    @Override
    public void deletePrAudit(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from PrAudit pc where pc.buyer.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @Override
    public void deletePrComments(String prId) {
        StringBuilder hsql = new StringBuilder("delete from PrComment pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();

    }

    @Override
    public void deletePrContacts(String prId) {
        StringBuilder hsql = new StringBuilder("delete from PrContact pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();

    }

    @Override
    public void deletePrItem(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from PrItem pc where pc.buyer.id= :id and pc.parent is not null");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @Override
    public void deletePrItemParent(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from PrItem pc where pc.buyer.id= :id and pc.parent is null");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @Override
    public void deletePrTeam(String prId) {
        StringBuilder hsql = new StringBuilder("delete from PrTeamMember pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();
    }

    @Override
    public Pr findByPrIdForFinance(String prId) {
        LOG.debug("prId:" + prId);
        StringBuilder hsql = new StringBuilder("from Pr p left outer join fetch p.template left outer join fetch p.supplier sp join fetch sp.supplier ssp join fetch p.createdBy cb left outer join fetch p.modifiedBy mb left outer join fetch p.currency c left outer join fetch p.costCenter cc left outer join fetch p.prDocuments as pd left outer join fetch p.deliveryAddress as da left outer join fetch da.state as dst left outer join fetch dst.country where p.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        Pr pr = (Pr) query.getSingleResult();
        return pr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FinancePo> getPoForDateBetween(Date startDate, Date endDate) {
        try {

            StringBuilder hsql = new StringBuilder("from FinancePo p left outer join fetch p.supplier sp join fetch p.financeCompany cb left outer join fetch p.po po where p.paymentDate between :startDate and :endDate and p.financePoStatus in (:status)");
            Query query = getEntityManager().createQuery(hsql.toString());
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<FinancePoStatus> statuss = Arrays.asList(FinancePoStatus.SUBMITED, FinancePoStatus.FINANCE_SETTLED);
            query.setParameter("status", statuss);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void deletePr(String prId) {
        StringBuilder hsql = new StringBuilder("delete from Pr pc where pc.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();

    }

    @Override
    public void deletePrTemplateFieldByTanent(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from PrTemplateField pc where pc.buyer.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPrTemplateIdList(String tenantId) {
        String hql = new String("select id from PrTemplate where buyer.id = :tenantId ");
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", tenantId);
        return query.getResultList();
    }

    @Override
    public void deletePrTemplateApprovalUserByAproval(String apid) {
        StringBuilder hsql = new StringBuilder("delete from PrTemplateApprovalUser pc where pc.approval.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", apid);
        query.executeUpdate();

    }

    @Override
    public void deletePrTemplateApprovalById(String apid) {
        StringBuilder hsql = new StringBuilder("delete from PrTemplateApproval pc where pc.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", apid);
        query.executeUpdate();

    }

    @Override
    public void deletePrTemplateByTanent(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from PrTemplate pc where pc.buyer.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getPrTemplateApprovalIdList(String prtemplateId) {
        String hql = new String("select id from PrTemplateApproval where prTemplate.id = :prtemplateId ");
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("prtemplateId", prtemplateId);
        return query.getResultList();
    }

    @Override
    public void deletePrTemplateById(String prTemplateId) {
        StringBuilder hsql = new StringBuilder("delete from PrTemplate pc where pc.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prTemplateId);
        query.executeUpdate();

    }

    @Override
    public void deletePrrecord(String prTemplateId) {
        StringBuilder hsql = new StringBuilder("delete from PROC_USER_PR_TEMPLATE_MAPPING  where TEMPLATE_ID = '" + prTemplateId + "'");
        Query query = getEntityManager().createNativeQuery(hsql.toString());
        query.executeUpdate();

    }

    @Override
    public void deletePoDoc(String prId) {
        StringBuilder hsql = new StringBuilder("delete from PoDocument pc where pc.pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();

    }

    @Override
    public void deleteErpSettings(String tenantId) {
        StringBuilder hsql = new StringBuilder("delete from ErpSetup pc where pc.tenantId= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", tenantId);
        query.executeUpdate();

    }

    @Override
    public void deletePrTeamMember(String prTemplateId) {
        StringBuilder hsql = new StringBuilder("delete from TemplatePrTeamMembers tpt where tpt.prTemplate.id= :prTemplateId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prTemplateId", prTemplateId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllSearchFilterPrReport(String tenantId, String[] prArr, boolean select_all, SearchFilterPrPojo searchFilterPrPojo, Date startDate, Date endDate, String userId) {
        final Query query = constructPrReportExportSearchFilterForTenantQuery(tenantId, prArr, select_all, searchFilterPrPojo, startDate, endDate, userId);
        return query.getResultList();
    }

    private Query constructPrReportExportSearchFilterForTenantQuery(String tenantId, String[] prArr, boolean select_all, SearchFilterPrPojo searchFilterPrPojo, Date startDate, Date endDate, String userId) {

        String hql = "";

        // If count query is enabled, then add the select count(*) clause

        hql += "select distinct NEW Pr(p.id, p.name, p.prCreatedDate, p.modifiedDate, p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.prId,  bu.unitName, p.status, p.isPoReportSent,c.currencyCode,sup.companyName, p.supplierName) ";

        hql += " from Pr p ";

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.currency as c ";

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId  and p.status in (:status)";

        LOG.info("HQL --------------------->: " + hql);

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                hql += " and p.id in (:prArr)";
            }
        }
        if (StringUtils.checkString(userId).length() > 0) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
        }
        List<PrStatus> list = new ArrayList<PrStatus>();

        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                hql += " and upper(p.referenceNumber) like :referencenumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpr()).length() > 0) {
                hql += " and upper(p.name) like :nameofpr";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                hql += " and upper(sup.companyName) like :suppliername";
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrnumber()).length() > 0) {
                hql += " and upper(p.prId) like :prnumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrcreatedby()).length() > 0) {
                hql += " and upper(cb.name) like :prcreatedby";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrapprovedby()).length() > 0) {
                hql += " and upper(au.name) like :prapprovedby";
                LOG.info("prapprovedby" + searchFilterPrPojo.getPrapprovedby());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                hql += " and upper(bu.unitName) like :businessunit";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getCurrency()).length() > 0) {
                hql += " and upper(c.currencyCode) like :currency";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrstatus()).length() > 0) {
                hql += " and upper(p.status) in (:prstatus)";
                String[] types = searchFilterPrPojo.getPrstatus().split(",");
                if (types != null && types.length > 0) {
                    for (String ty : types) {
                        list.add(PrStatus.valueOf(ty));
                    }
                }
            }
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  p.prCreatedDate between :startDate and :endDate ";
        }

        final Query query = getEntityManager().createQuery(hql.toString());

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                query.setParameter("prArr", Arrays.asList(prArr));
            }
        }
        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                query.setParameter("referencenumber", "%" + searchFilterPrPojo.getReferencenumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpr()).length() > 0) {
                query.setParameter("nameofpr", "%" + searchFilterPrPojo.getNameofpr().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                query.setParameter("suppliername", "%" + searchFilterPrPojo.getSuppliername().toUpperCase() + "%");
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername().toUpperCase());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrnumber()).length() > 0) {
                query.setParameter("prnumber", "%" + searchFilterPrPojo.getPrnumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrcreatedby()).length() > 0) {
                query.setParameter("prcreatedby", "%" + searchFilterPrPojo.getPrcreatedby().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrapprovedby()).length() > 0) {
                query.setParameter("prapprovedby", "%" + searchFilterPrPojo.getPrapprovedby().toUpperCase() + "%");
                LOG.info("prapprovedby" + searchFilterPrPojo.getPrapprovedby().toUpperCase());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                query.setParameter("businessunit", "%" + searchFilterPrPojo.getBusinessunit().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getCurrency()).length() > 0) {
                query.setParameter("currency", "%" + searchFilterPrPojo.getCurrency().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrstatus()).length() > 0) {
                query.setParameter("prstatus", list);
            }

        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        query.setParameter("tenantId", tenantId);
        List<PrStatus> Prlist = new ArrayList<PrStatus>();
        Prlist.add(PrStatus.TRANSFERRED);
        Prlist.add(PrStatus.APPROVED);
        Prlist.add(PrStatus.CANCELED);
        Prlist.add(PrStatus.PENDING);
        query.setParameter("status", Prlist);
        return query;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findAllPrWithTemplateForTenant(String loggedInUserTenantId, String userId, TableDataInput input) {
        String hql = "select distinct new Pr(p.id, p.name, p.referenceNumber, p.prCreatedDate, cr) from Pr p left outer join p.createdBy as cr left outer join p.template as pt left outer join p.buyer b where p.buyer.id = :tenantId ";
        if (StringUtils.checkString(userId).length() > 0) {
            hql += " and cr.id = :userId ";
        }
        hql += " order by p.prCreatedDate ";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", loggedInUserTenantId);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public void deletePrForFinance(String prId) {
        StringBuilder hsql = new StringBuilder("delete FinancePo where pr.id= :id");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", prId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findSearchPoByIds(String tenantId, String[] prArr, boolean select_all, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo) {
        String hql = "select p from Pr p inner join p.createdBy cr left outer join p.businessUnit as bu left outer join p.supplier as fs left outer join fs.supplier as sup where p.status in (:status) and p.buyer.id = :tenantId";
        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                hql += " and p.id in (:prArr)";
                LOG.info("select_all" + select_all);
            }
        }
        List<PrStatus> list = new ArrayList<PrStatus>();
        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                hql += " and upper(p.referenceNumber) like :referencenumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpo()).length() > 0) {
                hql += " and upper(p.name) like :nameofpo";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                hql += " and upper(sup.companyName) like :suppliername";
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPonumber()).length() > 0) {
                hql += " and upper(p.poNumber) like :ponumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPocreatedby()).length() > 0) {
                hql += " and upper(cr.name) like :pocreatedby";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                hql += " and upper(bu.unitName) like :businessunit";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPostatus()).length() > 0) {
                hql += " and upper(p.status) in (:postatus)";
                String[] types = searchFilterPrPojo.getPostatus().split(",");
                if (types != null && types.length > 0) {
                    for (String ty : types) {
                        list.add(PrStatus.valueOf(ty));
                    }
                }
            }
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and p.poCreatedDate between :startDate and :endDate ";
        }

        final Query query = getEntityManager().createQuery(hql);

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                query.setParameter("prArr", Arrays.asList(prArr));
            }
        }

        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                query.setParameter("referencenumber", "%" + searchFilterPrPojo.getReferencenumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpo()).length() > 0) {
                query.setParameter("nameofpo", "%" + searchFilterPrPojo.getNameofpo().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                query.setParameter("suppliername", "%" + searchFilterPrPojo.getSuppliername().toUpperCase() + "%");
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername().toUpperCase());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPonumber()).length() > 0) {
                query.setParameter("ponumber", "%" + searchFilterPrPojo.getPonumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPocreatedby()).length() > 0) {
                query.setParameter("pocreatedby", "%" + searchFilterPrPojo.getPocreatedby().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                query.setParameter("businessunit", "%" + searchFilterPrPojo.getBusinessunit().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPostatus()).length() > 0) {
                LOG.info("getPostatus" + searchFilterPrPojo.getPostatus());
                query.setParameter("postatus", list);
            }

        }

        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
        query.setParameter("tenantId", tenantId);
        query.setParameter("status", statuses);
        return query.getResultList();

    }

    @Override
    public void updateSentPoReport(String[] prArr, boolean select_all, String tenantId, Date startDate, Date endDate, SearchFilterPrPojo searchFilterPrPojo) {
        String hql = "update Pr p set p.isPoReportSent = :poReportSent where p.status in (:status) and p.buyer.id = :tenantId";

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                hql += " and p.id in (:prArr)";
            }
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and p.poCreatedDate between :startDate and :endDate ";
        }

        final Query query = getEntityManager().createQuery(hql);

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                query.setParameter("prArr", Arrays.asList(prArr));
            }
        }

        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
        query.setParameter("tenantId", tenantId);
        query.setParameter("status", statuses);
        query.setParameter("poReportSent", Boolean.TRUE);
        int updateSentPoReport = query.executeUpdate();
        LOG.info("updateSentPoReport :" + updateSentPoReport);
    }

    @Override
    public long findTotalPoList(String tenantId, String userId, List<PrStatus> prStatus) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au where p.buyer.id = :buyerId and p.status in (:status)");
        // if not admin
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId) ");
        }
        if ((prStatus.contains(PrStatus.PENDING) || prStatus.contains(PrStatus.APPROVED)) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
        }

        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            query.setParameter("userId", userId);
        }
        if ((prStatus.contains(PrStatus.PENDING) || prStatus.contains(PrStatus.APPROVED)) && userId != null) {
            query.setParameter("userId", userId);
        }
        List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
        query.setParameter("status", statuses);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalNotDraftPrList(String tenantId, String userId, List<PrStatus> prStatus,String selectedStatus,String prType) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au left outer join p.supplier as fs  left outer join fs.supplier as sup where p.buyer.id = :buyerId ");

        // if not admin
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId) ");
        }
        if (prStatus.contains(PrStatus.PENDING) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
        }

        if (prType.equals("ME")) {
            if (StringUtils.checkString(userId).length() > 0) {
                hsql.append( " and p.createdBy.id =:userId ");
            }
        }
        Boolean isSelectFilterOn=false;
        if(selectedStatus !=null){
            isSelectFilterOn=true;
            hsql.append( " and p.status =:status ");
        }

        if (!isSelectFilterOn ) {
            hsql.append( " and p.status in (:status) ");
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);


        if(selectedStatus !=null) {
            query.setParameter("status",PrStatus.fromString(selectedStatus));
        }
        if (!isSelectFilterOn ) {
            List<PrStatus> statuses = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
            query.setParameter("status", statuses);
        }
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            query.setParameter("userId", userId);
        }
        if (prStatus.contains(PrStatus.PENDING) && userId != null) {
            query.setParameter("userId", userId);
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalPoReportList(String tenantId, String userId, List<PrStatus> prStatus) {
        StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.prTeamMembers as tm left outer join tm.user as tmu left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au where p.buyer.id = :buyerId and p.status in (:status)");
        // if not admin
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId) ");
        }
        if ((prStatus.contains(PrStatus.PENDING) || prStatus.contains(PrStatus.APPROVED)) && userId != null) {
            hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ");
        }
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("buyerId", tenantId);
        if (prStatus.contains(PrStatus.DRAFT) && userId != null) {
            query.setParameter("userId", userId);
        }

        if ((prStatus.contains(PrStatus.PENDING) || prStatus.contains(PrStatus.APPROVED)) && userId != null) {
            query.setParameter("userId", userId);
        }
        query.setParameter("status", prStatus);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long findTotalFilteredPoReportList(String userId, String tenantId, TableDataInput input, Date startDate, Date endDate, List<PrStatus> prStatus) {
        final Query query = constructPrSearchFilterForTenantQuery(userId, tenantId, input, true, startDate, endDate, prStatus);
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public void deletePrrecordForClose(String prTemplateId) {
        StringBuilder hsql = new StringBuilder("delete from PROC_USER_PR_TEMPLATE_MAPPING  where TEMPLATE_ID = '" + prTemplateId + "'");
        Query query = getEntityManager().createNativeQuery(hsql.toString());
        query.executeUpdate();

        StringBuilder hsql2 = new StringBuilder("delete from PROC_PR_TEMPLATE_FIELD  where TEMPLATE_ID = '" + prTemplateId + "'");
        Query query2 = getEntityManager().createNativeQuery(hsql2.toString());
        query2.executeUpdate();

    }

    public Pr findPrBUAndCCForBudgetById(String prId) {
        StringBuilder hsql = new StringBuilder("from Pr p left outer join fetch p.costCenter cc left outer join fetch p.currency c left outer join fetch p.businessUnit bu where p.id= :prId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("prId", prId);
        Pr pr = (Pr) query.getSingleResult();
        return pr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pr findSupplierByFavSupplierId(String id) {
        StringBuilder hsql = new StringBuilder("from Pr p left outer join fetch p.supplier as fs where fs.id = :id");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", id);
        List<Pr> uList = query.getResultList();
        if (CollectionUtil.isNotEmpty(uList)) {
            return uList.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PrTeamMember> findAssociateOwnerOfPr(String id, TeamMemberType associateOwner) {
        String hql = " from PrTeamMember ptm left outer join fetch ptm.user u left outer join fetch u.buyer where ptm.pr.id = :prId and ptm.teamMemberType =:associateOwner";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("prId", id);
        query.setParameter("associateOwner", associateOwner);

        List<PrTeamMember> prTeamList = query.getResultList();
        return prTeamList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findPrForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] prArr, SearchFilterPrPojo searchFilterPrPojo, boolean select_all, Date startDate, Date endDate, String userId,List<String> businessUnitIds,String prType,String status) {

        String hql = "";

        hql += "select distinct NEW Pr(p.id, p.referenceNumber, p.name, sup.companyName, p.supplierName, p.description, p.prId, cb.name, p.prCreatedDate, c.currencyCode, p.grandTotal,	bu.unitName, p.status, cb,p.poNumber) ";

        hql += " from Pr p ";

        hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.currency as c ";

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId  and p.status in (:status)";

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                hql += " and p.id in (:prArr)";
            }
        }
        if(prType.equals("ME")) {
            if (StringUtils.checkString(userId).length() > 0) {
                hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
            }
        }else{
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql+= " AND bu.id IN (:businessUnitIds) ";
            }
        }


        List<PrStatus> list = new ArrayList<PrStatus>();

        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                hql += " and upper(p.referenceNumber) like :referencenumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpr()).length() > 0) {
                hql += " and upper(p.name) like :nameofpr";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                hql += " and upper(sup.companyName) like :suppliername";
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrnumber()).length() > 0) {
                hql += " and upper(p.prId) like :prnumber";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrcreatedby()).length() > 0) {
                hql += " and upper(cb.name) like :prcreatedby";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrapprovedby()).length() > 0) {
                hql += " and upper(au.name) like :prapprovedby";
                LOG.info("prapprovedby..............." + searchFilterPrPojo.getPrapprovedby());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                hql += " and upper(bu.unitName) like :businessunit";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getCurrency()).length() > 0) {
                hql += " and upper(c.currencyCode) like :currency";
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrstatus()).length() > 0) {
                hql += " and upper(p.status) in (:prstatus)";
                String[] types = searchFilterPrPojo.getPrstatus().split(",");
                if (types != null && types.length > 0) {
                    for (String ty : types) {
                        list.add(PrStatus.valueOf(ty));
                    }
                }
            }
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  p.prCreatedDate between :startDate and :endDate ";
        }

        LOG.info("HQL =========================>>>>>>>>>.>: " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        if (!(select_all)) {
            if (prArr != null && prArr.length > 0) {
                query.setParameter("prArr", Arrays.asList(prArr));
            }
        }
        if (searchFilterPrPojo != null) {
            if (StringUtils.checkString(searchFilterPrPojo.getReferencenumber()).length() > 0) {
                query.setParameter("referencenumber", "%" + searchFilterPrPojo.getReferencenumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getNameofpr()).length() > 0) {
                query.setParameter("nameofpr", "%" + searchFilterPrPojo.getNameofpr().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getSuppliername()).length() > 0) {
                query.setParameter("suppliername", "%" + searchFilterPrPojo.getSuppliername().toUpperCase() + "%");
                LOG.info("getSuppliername" + searchFilterPrPojo.getSuppliername().toUpperCase());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrnumber()).length() > 0) {
                query.setParameter("prnumber", "%" + searchFilterPrPojo.getPrnumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrcreatedby()).length() > 0) {
                query.setParameter("prcreatedby", "%" + searchFilterPrPojo.getPrcreatedby().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrapprovedby()).length() > 0) {
                query.setParameter("prapprovedby", "%" + searchFilterPrPojo.getPrapprovedby().toUpperCase() + "%");
                LOG.info("prapprovedby ..... " + searchFilterPrPojo.getPrapprovedby());
            }
            if (StringUtils.checkString(searchFilterPrPojo.getBusinessunit()).length() > 0) {
                query.setParameter("businessunit", "%" + searchFilterPrPojo.getBusinessunit().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getCurrency()).length() > 0) {
                query.setParameter("currency", "%" + searchFilterPrPojo.getCurrency().toUpperCase() + "%");
            }
            if (StringUtils.checkString(searchFilterPrPojo.getPrstatus()).length() > 0) {
                query.setParameter("prstatus", "%" + searchFilterPrPojo.getPrstatus().toUpperCase() + "%");
            }

        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        if(prType.equals("ME")) {
            if (StringUtils.checkString(userId).length() > 0) {
                query.setParameter("userId", userId);
            }
        }else{
            if (businessUnitIds != null && !businessUnitIds.isEmpty())
                query.setParameter("businessUnitIds", businessUnitIds);
        }

        query.setParameter("tenantId", tenantId);
        if(null!=status)
            query.setParameter("status",PrStatus.fromString(status));
        else
            query.setParameter("status", Arrays.asList(PrStatus.DRAFT,PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED,PrStatus.COMPLETE, PrStatus.DELIVERED, PrStatus.PENDING));

        query.setFirstResult((pageSize * pageNo));
        query.setMaxResults(pageSize);

        List<Pr> prList = query.getResultList();

        if (CollectionUtil.isNotEmpty(prList)) {
            return prList;
        } else {
            return null;
        }

    }

    @Override
    public long getPrCountBySearchValueForTenant(String searchValue, String tenantId, String userId) {
        StringBuffer sb = new StringBuffer("select count(e) from Pr e left outer join e.createdBy cb where e.buyer.id = :tenantId ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(e.name) like :searchValue or upper(e.referenceNumber) like :searchValue) ");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id = :userId ");
        }

//		sb.append(" order by e.prCreatedDate desc");

        final Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Pr> findPrByNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId) {
        StringBuffer sb = new StringBuffer("select distinct e from Pr e left outer join fetch e.createdBy cb where e.buyer.id = :tenantId ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(e.name) like :searchValue or upper(e.referenceNumber) like :searchValue) ");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id = :userId ");
        }

        sb.append(" order by e.prCreatedDate desc");

        final Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }

        if (start != null && pageLength != null) {
            query.setFirstResult(start);
            query.setMaxResults(pageLength);
        }

        return query.getResultList();
    }

    @Override
    public void transferOwnership(String fromUserId, String toUserId) {

        User sourceUser = getEntityManager().find(User.class, fromUserId);
        User targetUser = getEntityManager().find(User.class, toUserId);

        Query query = getEntityManager().createNativeQuery("UPDATE PROC_PR SET CREATED_BY = :toUserId where CREATED_BY =:fromUserId");
        query.setParameter("toUserId", toUserId);
        query.setParameter("fromUserId", fromUserId);
        int recordsUpdated = query.executeUpdate();
        LOG.info("Creators transferred: {}", recordsUpdated);

        //transfer the user to team members too
        Query query2 = getEntityManager().createQuery(
                "UPDATE PrTeamMember team " +
                        "SET team.user = :targetUser " +
                        "WHERE team.user = :sourceUser "
        );
        query2.setParameter("sourceUser", sourceUser);
        query2.setParameter("targetUser", targetUser);
        recordsUpdated = query2.executeUpdate();
        LOG.info("Form Owners transferred: {}", recordsUpdated);

        //transfer ownership of approvals only for RFS with status of DRAFT and PENDING
        Query query5 = getEntityManager().createQuery(
                "UPDATE PrApprovalUser sfaur " +
                        "SET sfaur.user = :targetUser " +
                        "WHERE sfaur.user = :sourceUser "
        );
        query5.setParameter("sourceUser", sourceUser);
        query5.setParameter("targetUser", targetUser);
        recordsUpdated = query5.executeUpdate();
        LOG.info("Approval user transferred: {}", recordsUpdated);
    }

    private List<Pr> getPrList(List<Object[]> objects, PrStatus status) {
        List<Pr> prList = new ArrayList<>();
        objects.forEach(list -> {
            try {
                Pr pr = new Pr();
                BusinessUnit businessUnit = new BusinessUnit();
                FavouriteSupplier favouriteSupplier = new FavouriteSupplier();
                Currency currency = new Currency();
                pr.setId(list[0].toString());
                pr.setName(list[1] != null ? list[1].toString() : "");
                pr.setLockBudget(list[2].toString().equals("1") ? Boolean.TRUE : Boolean.FALSE);
                pr.setModifiedDate(list[3] != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(list[3].toString()) : null);
                pr.setGrandTotal(list[4] != null ? BigDecimal.valueOf(Double.parseDouble(list[4].toString())) : BigDecimal.ZERO);
                pr.setCreatedBy(list[5] != null ? (User) list[5] : null);
                pr.setModifiedBy(list[6] != null ? (User) list[6] : null);
                pr.setPrCreatedDate(list[7] != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(list[7].toString()) : null);
                pr.setDecimal(list[10] != null ? list[10].toString() : "");
                pr.setReferenceNumber(list[11] != null ? list[11].toString() : "");
                pr.setDescription(list[12] != null ? list[12].toString() : "");
                if (status == PrStatus.APPROVED) {
                    pr.setPoNumber(list[13] != null ? list[13].toString() : "");
                } else {
                    pr.setPrId(list[13] != null ? list[13].toString() : "");
                }
                businessUnit.setUnitName(list[14] != null ? list[14].toString() : "");
                pr.setBusinessUnit(businessUnit);
                favouriteSupplier.setFullName(list[15] != null ? list[15].toString() : "");
                pr.setSupplier(favouriteSupplier);
                pr.setSupplierName(list[16] != null ? list[16].toString() : "");
                pr.setStatus(list[17] != null ? PrStatus.valueOf(list[17].toString()) : null);
                pr.setIsPoReportSent(list[18] != null ? (list[18].toString().equals("1") ? Boolean.TRUE : Boolean.FALSE) : null);
                currency.setCurrencyCode(list[19] != null ? list[19].toString() : "");
                pr.setCurrency(currency);
                prList.add(pr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return prList;
    }

    private Query constructPrReportSearchFilterTenantRoleQuery(String userId, String tenantId, TableDataInput tableParams, boolean isCount, List<PrStatus> prStatus, Date startDate, Date endDate,boolean isAdmin) {

        String hql = "";
        LOG.info(" constructPrReportSearchFilterTenantRoleQuery userId >>>>>>>>>>>>>>>>>>>>>>>>>>>> "+userId);

        // If count query is enabled, then add the select count(*) clause
        if (isCount) {
            hql += "select count(distinct p) ";
        } else {

            hql += "select distinct NEW Pr(p.id, p.name, p.lockBudget, p.prCreatedDate, p.modifiedDate, p.grandTotal, cb, mb, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.prId,  bu.unitName, p.status, p.isPoReportSent,c.currencyCode, sup.companyName, p.supplierName) ";

        }

        hql += " from Pr p ";

        // If this is not a count query, only then add the join fetch. Count
        // query does not require its
        if (!isCount) {
            hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.currency as c ";
        }

        hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup ";

        hql += " left outer join p.prTeamMembers as tm left outer join tm.user as tmu ";

        hql += " left outer join p.prApprovals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

        hql += " where p.buyer.id = :tenantId  ";

//below logic brought over from dashboard logic for draft when no filter applied


        if (prStatus.contains(PrStatus.PENDING) && userId != null&&isAdmin==false) {
            hql += " and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId) ";
        }
        boolean isSelectOn = false;
        // Add on search filter conditions
        for (ColumnParameter cp : tableParams.getColumns()) {
            LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());

            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

                LOG.info("-------------------------------------");
                LOG.info("cp.getSearch().getValue() "+cp.getSearch().getValue());
                if (cp.getData().equals("status")) {
                    LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
                    if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                        hql += " and ( ( p.erpPrTransferred = true and p.status not in (:statuses) ) or p.status in (:status) )";
                    }
                    else if (cp.getSearch().getValue().equals("DRAFT")) {
                        //logic brought over by draft in dashboard WHEN FILTERED
                        hql += " and (tmu.id = :userIdDraft or p.createdBy.id = :userIdDraft) and p.status = :status ";
                    }
                    else if(!"DRAFT".equals(cp.getSearch().getValue()))  {
                        hql += " and p.status in (:status) ";
                    }
                    isSelectOn = true;
                } else {
                    if (cp.getData().equalsIgnoreCase("supplier.fullName")) {
                        hql += " and upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ") ";
                        LOG.info("" + cp.getData());
                    } else if (cp.getData().equalsIgnoreCase("approvedBy.name")) {
                        hql += " and upper(au.name) like (:" + cp.getData().replace(".", "") + ") ";
                        LOG.info("" + cp.getData());
                    } else {
                        hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                    }
                }
            }
        }

        if (!isSelectOn) {
            //below logic brought over from dashboard logic for draft when no filter applied
            if (prStatus.contains(PrStatus.DRAFT) && userId != null && isAdmin==true && isSelectOn==false) {
                LOG.info(" INITIAL userId draft PR LIST >>>>>"+userId);
                hql += " AND (\n" +
                        "  (p.status = 'DRAFT' AND p.erpPrTransferred = false AND (tmu.id = :userIdDraft or p.createdBy.id = :userIdDraft)) ";
            }
            LOG.info("-------------------here------------------");
            hql += " OR p.status IN :status ) ";
        }

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  p.prCreatedDate between :startDate and :endDate ";
        }

        // If it is not a count query then add order by clause
        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql += " order by ";
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();

                    if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
                        hql += " sup.companyName " + dir + ", p.supplierName " + dir;
                    } else {
                        if (orderColumn.equals("approvedBy.name")) {
                            hql += "   au.name " + dir + ",";
                        } else if (orderColumn.equals("prApprovedDate")) {
                            hql += "   pau.actionDate " + dir + ",";
                        } else {
                            hql += " p." + orderColumn + " " + dir + ",";
                        }
                    }
                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = hql.substring(0, hql.length() - 1);
                }
            } else {
                // by default order by created date
                hql += " order by p.prCreatedDate desc ";
            }
        }

        LOG.info("HQL --------------------->: " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        LOG.info("Current prStatus: " + prStatus);
        if (prStatus.contains(PrStatus.DRAFT) && userId != null&&isAdmin==true && isSelectOn==false) {
            query.setParameter("userIdDraft", userId);
            LOG.info("lalu DRAFT status all ?");

        }
        LOG.info("USER ID DONT SET  ?>>>>>>>>>>> "+prStatus.contains(PrStatus.PENDING));
        if (prStatus.contains(PrStatus.PENDING) && userId != null&&isAdmin==false&& isSelectOn==false) {
            query.setParameter("userId", userId);
        }

        // Apply search filter values
        for (ColumnParameter cp : tableParams.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("status")) {

                    if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
                        LOG.info("-----------------------ALL-------------------------------");
                        List<PrStatus> statuses = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
                        query.setParameter("status", statuses);
                    } else {
                        LOG.info("-----------------------OTHER-------------------------------");
                        if (cp.getSearch().getValue().equals("TRANSFERRED")) {
                            List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.DELIVERED);
                            query.setParameter("statuses", statuses);
                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        }
                        else if (cp.getSearch().getValue().equals("DRAFT")&& isSelectOn==true) {
                            LOG.info("SET USER ID Draft condition applied");
                           /* List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.DELIVERED);
                            query.setParameter("statuses", statuses);
                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));*/
                            query.setParameter("status", PrStatus.DRAFT);  // Set the DRAFT status parameter
                            query.setParameter("userIdDraft", userId);


                        }
                        else {

                            query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
                        }

                    }
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    LOG.info("" + cp.getSearch().getValue());
                }
            }
        }
        if (!isSelectOn) {
            List<PrStatus> statuses = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
            query.setParameter("status", statuses);
        }
        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        return query;

    }
}
