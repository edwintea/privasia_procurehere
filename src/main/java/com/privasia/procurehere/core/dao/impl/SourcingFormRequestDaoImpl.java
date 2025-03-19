package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import org.hibernate.Hibernate;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RequestComment;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author pooja
 */
@Repository
public class SourcingFormRequestDaoImpl extends GenericDaoImpl<SourcingFormRequest, String> implements SourcingFormRequestDao {

    private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

    @Autowired
    UserDao userDao;

    @Override
    public boolean isExistFormId(String tenantId, String formId) {
        StringBuilder hql = new StringBuilder("select count(s) from  SourcingFormRequest s where s.formId=:formId and s.tenantId=:tenantId");
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        query.setParameter("formId", formId);
        return (((Number) query.getSingleResult()).longValue() > 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest findByFormId(String formId) {
        try {
            final Query query = getEntityManager().createQuery("from SourcingFormRequest s join fetch s.formOwner join fetch s.createdBy left outer join fetch s.modifiedBy left outer join fetch s.sourcingForm  where s.id =:formId");
            query.setParameter("formId", formId);
            List<SourcingFormRequest> uList = query.getResultList();
            if (CollectionUtil.isNotEmpty(uList)) {
                return uList.get(0);
            } else {

                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while getting user : " + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> findAllSourcingFormForTenant(String loggedInUserTenantId, TableDataInput input) {
        String hql = "select distinct new SourcingFormRequest(p.id, p.sourcingFormName,p.referanceNumber,p.description ,p.createdDate, cb) from SourcingFormRequest p left outer join p.createdBy as cb where p.tenantId=:tenantId order by p.createdDate desc";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest getSourcingBqByFormId(String formId) {
        try {
            String hql = "from SourcingFormRequest s left outer join fetch s.sourcingRequestBqs bq where s.id=:formId";
            final Query query = getEntityManager().createQuery(hql);
            query.setParameter("formId", formId);
            List<SourcingFormRequest> ulist = query.getResultList();
            if (CollectionUtil.isNotEmpty(ulist)) {
                return ulist.get(0);
            } else {
                return null;
            }
        } catch (NoResultException nr) {
            LOG.info("Error while getting  Bq found..............." + nr.getMessage(), nr);
            return null;
        }
    }


    @Override
    public SourcingFormRequest getSourcingSorByFormId(String formId) {
        try {
            String hql = "from SourcingFormRequest s left outer join fetch s.sourcingRequestSors bq where s.id=:formId";
            final Query query = getEntityManager().createQuery(hql);
            query.setParameter("formId", formId);
            List<SourcingFormRequest> ulist = query.getResultList();
            if (CollectionUtil.isNotEmpty(ulist)) {
                return ulist.get(0);
            } else {
                return null;
            }
        } catch (NoResultException nr) {
            LOG.info("Error while getting  Bq found..............." + nr.getMessage(), nr);
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingTemplateCq> getCq(String formId) {
        String hql = "from SourcingTemplateCq  cq  left outer join fetch cq.cqItems cqItem where cq.sourcingForm.id=:formId";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("formId", formId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestBq> getBq(String requestId) {
        String hql = "from SourcingFormRequestBq  bq  left outer join fetch bq.bqItems bqItem where bq.sourcingFormRequest.id=:requestId";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @Override
    public SourcingFormTemplate getSourcingFormByReqId(String requestId) {
        String hql = "select rq.sourcingForm from SourcingFormRequest rq left outer join  rq.sourcingForm form where rq.id=:requestId";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return (SourcingFormTemplate) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormApprovalRequest> getApproval(String requestId) {
        String hql = "select rq.sourcingFormApprovalRequests from SourcingFormRequest rq  left outer join rq.sourcingFormApprovalRequests where rq.id=:requestId ";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestCqItem> getCqItembyRequestId(String requestId) {
        String hql = "  from SourcingFormRequestCqItem cq left outer join fetch cq.cqItem cqi left outer join fetch cq.listAnswers left outer join fetch cq.cq where cq.sourcingFormRequest.id=:requestId order by cqi.level,cqi.order";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @Override
    public long findTotalMyPendingRequestCount(String loggedInUserTenantId, String id) {

        final Query query = getEntityManager().createQuery("select count(s) from SourcingFormRequest s left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au left outer join au.user u where s.tenantId =:tenantId and s.status =:reqStatus and ( u.id =:id and au.approvalStatus =:status and sfa.active = 1 )");

        query.setParameter("reqStatus", SourcingFormStatus.PENDING);
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", ApprovalStatus.PENDING);

        query.setParameter("id", id);

        return ((Number) query.getSingleResult()).longValue();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> findTotalMyPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input) {

        final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId, s.description, s.createdDate, s.createdBy,s.formOwner,s.businessUnit) from SourcingFormRequest s left outer join s.formOwner left outer join s.createdBy left outer join  s.businessUnit  left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au left outer join au.user u where s.tenantId =:tenantId and s.status =:reqStatus and ( u.id =:id and au.approvalStatus =:status and sfa.active = 1 )");

        query.setParameter("reqStatus", SourcingFormStatus.PENDING);
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", ApprovalStatus.PENDING);
        query.setParameter("id", id);
        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myDraftRequestList(String loggedInUserTenantId, String userId, TableDataInput input) {

        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join s.sourcingFormTeamMember tm left outer join tm.user tu  where s.tenantId =:tenantId and s.status =:status ");

        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and (cb.id=:userId or tu.id =:userId ) ");
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }

        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        LOG.info("*****************formDrafts************** " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.DRAFT);

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }

        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public long myDraftRequestListCount(String loggedInUserTenantId, String userId, TableDataInput input) {

        StringBuilder hql = new StringBuilder("select count(distinct s.id) from SourcingFormRequest s left outer join  s.formOwner left outer join  s.createdBy cb ");
        hql.append(" left outer join  s.businessUnit left outer join s.sourcingFormTeamMember tm left outer join tm.user tu where s.tenantId =:tenantId and s.status =:status ");

        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and ( cb.id=:userId or tu.id = :userId )");
        }

        // Add on search filter conditions
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql.append(" and type = :" + cp.getData().replace(".", ""));
                    } else if (cp.getData().equals("formOwner")) {
                        hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                    } else if (cp.getData().equals("businessUnit")) {
                        hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                    } else {
                        hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                    }
                }
            }
        }

        LOG.info("*****************formDrafts Count************** " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.DRAFT);

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                    } else {
                        query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }

        return ((Number) query.getSingleResult()).longValue();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myPendingRequestList(String loggedInUserTenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId, s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit  left outer join s.sourcingFormApprovalRequests ar ");
        hql.append(" left outer join ar.approvalUsersRequest aur left outer join aur.user ap left outer join s.sourcingFormTeamMember tm left outer join tm.user tu where s.tenantId =:tenantId and s.status =:status ");

        if (StringUtils.checkString(userId).length() > 0) {
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql.append(" and (cb.id=:userId or ap.id = :userId or tu.id= :userId ");
                hql.append(" or s.businessUnit.id IN (:businessUnitIds)) ");
            }else
                hql.append(" and (cb.id=:userId or ap.id = :userId or tu.id= :userId )");
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }

        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        LOG.info("****************myPendingRequestList**********" + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.PENDING);

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
        }

        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }

        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myPendingRequestAppList(String loggedInUserTenantId, String id, TableDataInput input) {

        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId, s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s  left outer join  s.formOwner left outer join  s.createdBy left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests app left outer join app.approvalUsersRequest usr where s.tenantId =:tenantId and s.status =:status and (app.sourcingFormRequest.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true)");

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }
        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit.unitName";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());

        // final Query query = getEntityManager().createQuery();
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("userId", id);
        query.setParameter("approvalStatus", ApprovalStatus.PENDING);
        query.setParameter("status", SourcingFormStatus.PENDING);
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());

        return query.getResultList();

    }

    @Override
    public long myPendingRequestListCount(String loggedInUserTenantId, String userId, TableDataInput input,List<String> bizUnitIds) {
        StringBuilder hql = new StringBuilder("select count(distinct s.id) from SourcingFormRequest s left outer join  s.formOwner left outer join  s.createdBy cb ");
        hql.append(" left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests ar left outer join ar.approvalUsersRequest aur left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.tenantId =:tenantId and s.status =:status ");

        if (StringUtils.checkString(userId).length() > 0) {
            if (bizUnitIds != null && !bizUnitIds.isEmpty()) {
                hql.append(" and (cb.id =:userId or ap.id = :userId or tu.id = :userId or ");
                hql.append("s.businessUnit.id IN (:businessUnitIds)) ");
            }else
                hql.append(" and (cb.id =:userId or ap.id = :userId or tu.id = :userId ) ");
        }

        if (input != null) {

            // Add on search filter conditions
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql.append(" and upper(s.type) = :" + cp.getData().replace(".", ""));
                    } else if (cp.getData().equals("formOwner")) {
                        hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                    } else if (cp.getData().equals("businessUnit")) {
                        hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                    } else {
                        hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                    }
                }
            }
        }

        LOG.info("++++++++++ pending Request list count+++++++++  " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.PENDING);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
            if (bizUnitIds != null && !bizUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", bizUnitIds);
            }
        }
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
                    } else {
                        query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestBq> getSourcingRequestBq(String requestId) {
        // TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
        // String hql = "select distinct a from SourcingFormRequestBq a inner join fetch a.bqItems item where
        // a.sourcingFormRequest.id=:requestId order by item.level,item.order";
        String hql = "select distinct a from  SourcingFormRequestBq a inner join fetch a.bqItems item where a.sourcingFormRequest.id=:requestId order by a.bqOrder";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();

    }


    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestSor> getSourcingRequestSor(String requestId) {
        // TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
        // String hql = "select distinct a from SourcingFormRequestBq a inner join fetch a.bqItems item where
        // a.sourcingFormRequest.id=:requestId order by item.level,item.order";
        String hql = "select distinct a from  SourcingFormRequestSor a inner join fetch a.sorItems item where a.sourcingFormRequest.id=:requestId order by a.sorOrder";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();

    }

    @Override
    public long getBqItemCount(String requestId) {

        // for (SourcingFormRequestBq bq : getSourcingRequestBq(requestId)) {
        // LOG.info(bq.getBqItems().size());
        // }
        String hql = "select count(a) from SourcingFormRequestBqItem a where a.sourcingFormRequest.id = :requestId and a.parent is not null";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        long count = ((Number) query.getSingleResult()).longValue();
        if (count == 0) {
            for (SourcingFormRequestBq bq : getSourcingRequestBq(requestId)) {
                count = bq.getBqItems().size();
            }

        }
        return count;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getSourcingRequestBqNames(String requestId) {
        String hql = "select a.name from SourcingFormRequestBq a where a.sourcingFormRequest.id=:requestId";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @Override
    public String getBusineessUnitnamerequest(String id) {
        StringBuilder hsql = new StringBuilder("select bu.displayName from SourcingFormRequest sr left outer join sr.businessUnit as bu where sr.id =:id ");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("id", id);
        return (String) query.getSingleResult();

    }

    @Override
    public EventPermissions getUserPemissionsForRequest(String userId, String requestId) {
        EventPermissions permissions = new EventPermissions();
        User loggedInUser = userDao.findById(userId);
        if(SecurityLibrary.ifAnyGranted("ROLE_ADMIN"))
            permissions.setOwner(true) ;
        if (UserType.REQUESTOR_USER == loggedInUser.getUserType()) {

            permissions.setRequesterUser(true);
        }

        // sourcing Owner
        SourcingFormRequest request = findById(requestId);
        if (request.getCreatedBy().getId().equals(loggedInUser.getId())) {
            permissions.setOwner(true);
        } else {
            // Viewer Editor
            List<SourcingFormTeamMember> teamMembers = request.getSourcingFormTeamMember();
            for (EventTeamMember member : teamMembers) {
                if (member.getUser().getId().equals(loggedInUser.getId())) {
                    if (member.getTeamMemberType() == TeamMemberType.Viewer) {
                        permissions.setViewer(true);
                    }
                    if (member.getTeamMemberType() == TeamMemberType.Editor) {
                        permissions.setEditor(true);
                        permissions.setViewer(false);
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
        List<SourcingFormApprovalRequest> approvals = request.getSourcingFormApprovalRequests();
        for (SourcingFormApprovalRequest approval : approvals) {
            if (CollectionUtil.isNotEmpty(approval.getApprovalUsersRequest())) {
                List<SourcingFormApprovalUserRequest> users = approval.getApprovalUsersRequest();
                for (SourcingFormApprovalUserRequest user : users) {
                    if (user.getUser().getId().equals(loggedInUser.getId())) {
                        permissions.setApprover(true);
                        if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
                            permissions.setActiveApproval(true);
                            break;
                        }
                    }
                }
            }
        }
        return permissions;
    }

    @Override
    public boolean checkSourcingRequestStatus(String formId) {
        final Query query = getEntityManager().createQuery("select count(s) from SourcingFormRequest s left outer join s.sourcingForm pt where pt is not null and pt.status= :status  and s.id =:formId");
        query.setParameter("status", SourcingStatus.INACTIVE);
        query.setParameter("formId", formId);
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> searchSourcingRequestByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo) {
        LOG.info("assignedSourcingTemplates 2");
        StringBuffer sb = new StringBuffer("select distinct s from SourcingFormRequest s left outer join fetch s.createdBy cb left outer join fetch s.costCenter cs left outer join fetch cs.createdBy ctd left outer join fetch s.sourcingForm temp where s.tenantId =:tenantId");

        sb.append(" and s.sourcingForm.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) and s.sourcingForm.status =:status ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(s.sourcingFormName) like :searchValue or upper(s.referanceNumber)like :searchValue)");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id=:userId");
        }

        sb.append(" order by s.createdDate desc ");

        final Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }
        query.setParameter("userId", userId);
        query.setParameter("status", SourcingStatus.ACTIVE);

        if (StringUtils.checkString(pageNo).length() > 0) {
            LOG.info("pageNo:" + pageNo);
            query.setFirstResult(Integer.parseInt(pageNo) * 20);
            query.setMaxResults(10);

        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myCompletedRequestList(String loggedInUserTenantId, String userId, TableDataInput input) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId, s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit)" +
                " from SourcingFormRequest s left outer join  s.formOwner " +
                " left outer join  s.createdBy cb left outer join  s.businessUnit " +
                " left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu "+
                " left outer join s.sourcingFormApprovalRequests far left outer join far.approvalUsersRequest aur" +
                " left outer join aur.user au where s.tenantId=:tenantId and s.status =:status ");
        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and cb.id=:userId");
        }
        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.APPROVED);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myApprvedRequestList(String loggedInUserTenantId, String userId, TableDataInput input) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests ar left outer join ar.approvalUsersRequest aur left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.tenantId =:tenantId and s.status =:status ");
        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and (cb.id=:userId or ap.id = :userId or tu.id =:userId)");
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }
        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.APPROVED);

        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }
    @Override
    public List<SourcingFormRequest> myApprvedRequestListBizUnit(String loggedInUserTenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
        LOG.info("BIZ UNIT ID APPROVED REQUEST LIST TAB >>>>>>> "+businessUnitIds);
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests ar left outer join ar.approvalUsersRequest aur left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.status =:status ");
        hql.append(" AND s.tenantId =:tenantId ");
        if (StringUtils.checkString(userId).length() > 0) {
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql.append(" AND (cb.id=:userId or ap.id = :userId or tu.id =:userId");
                hql.append(" OR s.businessUnit.id IN (:businessUnitIds)) ");
            }else{
                hql.append(" AND (cb.id=:userId or ap.id = :userId or tu.id =:userId)");
            }
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }
        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.APPROVED);

      if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
            query.setParameter("businessUnitIds", businessUnitIds);
        }
      }

        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }
    @Override
    public long myApprovedRequestListCount(String loggedInUserTenantId, String userId, TableDataInput input) {
        StringBuilder hql = new StringBuilder("SELECT COUNT(DISTINCT s.id) FROM PROC_SOURCING_FORM_REQ s WHERE s.tenant_id = :tenantId AND s.status = :status ");
        hql.append("and case when Length(:userId) > 0 then ( s.created_by = :userId or exists ( select 1 from PROC_SOURCING_FORM_APP_REQ ar join PROC_FORM_APPROVAL_USER_REQ aur on aur.form_approval_id = ar.id ");
        hql.append("join PROC_USER ap on ap.id = aur.user_id where ar.form_id = s.id and ap.id = :userId) ");
        hql.append("or exists (select 1 from PROC_SOURCING_FORM_REQ_TEAM tm join PROC_USER tu on tu.id = tm.user_id where tm.form_id = s.id and tu.id = :userId )) ");
        hql.append(" else (1=1) end ");

//        if (StringUtils.checkString(userId).length() > 0) {
//            hql.append(" and (s.createdBy.id=:userId or ap.id = :userId or tu.id = :userId)");
//        }

        // Add on search filter conditions
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql.append(" and s.type = :" + cp.getData().replace(".", ""));
                    } else if (cp.getData().equals("formId")) {
                        hql.append(" and upper(s.form_id) like (:" + cp.getData().replace(".", "") + ")");
                    } else if (cp.getData().equals("sourcingFormName")) {
                        hql.append(" and upper(s.sourcing_form_name) like (:" + cp.getData().replace(".", "") + ")");
                    } else if (cp.getData().equals("referenceNumber")) {
                        hql.append(" and upper(s.referance_number) like (:" + cp.getData().replace(".", "") + ")");
                    } else if (cp.getData().equals("businessUnit")) {
                        hql.append(" and upper(s.business_unit_id)  in (select id from PROC_BUSINESS_UNIT where BUSINESS_UNIT_NAME like (:" + cp.getData().replace(".", "") + "))");
                    } else if (cp.getData().equals("formOwner")) {
                        hql.append(" and upper(s.FORM_OWNER) in (select id from PROC_USER where USER_NAME like (:" + cp.getData().replace(".", "") + "))");
                    } else {
                        hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                    }
                }
            }
        }

        LOG.info("+++++++++++++++myApprove request count+++++++++++  " + hql);
        final Query query = getEntityManager().createNativeQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.APPROVED.name());

//        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", StringUtils.checkString(userId).isEmpty() ? "" : userId);
//        }
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                    } else {
                        query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long myApprovedRequestListCountBizUnit(String loggedInUserTenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
        LOG.info("BIZUNIT ID FOR APPROVED REQ LIST >>> "+businessUnitIds);
        StringBuilder hql = new StringBuilder("select count(distinct s.id) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests ar left outer join ar.approvalUsersRequest aur left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.status =:status ");
        hql.append(" AND s.tenantId =:tenantId ");

        if (StringUtils.checkString(userId).length() > 0) {
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                hql.append(" AND (cb.id=:userId or ap.id = :userId or tu.id =:userId");
                hql.append(" OR s.businessUnit.id IN (:businessUnitIds)) ");
            }else{
                hql.append(" AND (cb.id=:userId or ap.id = :userId or tu.id =:userId)");
            }
        }

        // Add on search filter conditions
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql.append(" and type = :" + cp.getData().replace(".", ""));
                    } else if (cp.getData().equals("formOwner")) {
                        hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                    } else if (cp.getData().equals("businessUnit")) {
                        hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                    } else {
                        hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                    }
                }
            }
        }

        LOG.info("+++++++++++++++myApprove request count+++++++++++  " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.APPROVED);

        if (StringUtils.checkString(userId).length() > 0) {
             query.setParameter("userId", userId);
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
        }
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                    } else {
                        query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long myPendingRequestAppListCount(String loggedInUserTenantId, String userid, TableDataInput input) {

        StringBuilder hql = new StringBuilder("select count(distinct s) from SourcingFormRequest s  left outer join  s.formOwner left outer join  s.createdBy left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests app left outer join app.approvalUsersRequest usr where s.tenantId =:tenantId and s.status =:status and (app.sourcingFormRequest.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true)");
        // + "SourcingFormRequest s left outer join s.sourcingFormApprovalRequests app left outer join
        // app.approvalUsersRequest usr where s.tenantId =:tenantId and s.status =:status and
        // (app.sourcingFormRequest.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and
        // app.active = true)");
        /*
         * final Query query = getEntityManager().createQuery(
         * "select count(distinct s) from SourcingFormRequest s left outer join s.sourcingFormApprovalRequests app left outer join app.approvalUsersRequest usr where s.tenantId =:tenantId and s.status =:status and (app.sourcingFormRequest.id = s.id and usr.user.id = :userId and usr.approvalStatus = :approvalStatus and app.active = true)"
         * );
         */
        // Add on search filter conditions
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql.append(" and type = :" + cp.getData().replace(".", ""));
                    } else if (cp.getData().equals("formOwner")) {
                        hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                    } else if (cp.getData().equals("unitName")) {
                        hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                    } else {
                        hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                    }
                }
            }
        }

        final Query query = getEntityManager().createQuery(hql.toString());

        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("userId", userid);
        query.setParameter("approvalStatus", ApprovalStatus.PENDING);
        query.setParameter("status", SourcingFormStatus.PENDING);

        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                    } else {
                        query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long finishedRequestCount(String loggedInUserTenantId, TableDataInput input, String userId) {
//        String aSql = "";
//        String bSql = "";
//        String cSql = "";
//        if (StringUtils.checkString(userId).length() > 0) {
//            aSql += " and (s.createdBy.id = :userId or ap.id = :userId or tu.id = :userId) ";
//            bSql += "  left outer join ar.approvalUsersRequest aur left outer join aur.user ap left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ";
//        }

        try {
            String hql = "SELECT COUNT(DISTINCT s.id) FROM PROC_SOURCING_FORM_REQ s WHERE s.tenant_id = :tenantId AND s.status = :status " +
                    "and case when Length(:userId) > 0 then ( s.created_by = :userId or exists ( select 1 from PROC_SOURCING_FORM_APP_REQ ar join PROC_FORM_APPROVAL_USER_REQ aur on aur.form_approval_id = ar.id " +
                    "join PROC_USER ap on ap.id = aur.user_id where ar.form_id = s.id and ap.id = :userId ) " +
                    "or exists (select 1 from PROC_SOURCING_FORM_REQ_TEAM tm join PROC_USER tu on tu.id = tm.user_id where tm.form_id = s.id and tu.id = :userId )) " +
                    " else (1=1) end ";
//            String hql = "SELECT COUNT(DISTINCT s.id) FROM PROC_SOURCING_FORM_REQ s WHERE s.tenant_id = :tenantId AND s.status = :status " +
//                    "and (exists (select 1 from PROC_SOURCING_FORM_APP_REQ ar where ar.form_id = s.id " +
//                    "or exists (select 1 from PROC_FORM_APPROVAL_USER_REQ aur where aur.form_approval_id = ar.id " +
//                    "or exists (select 1 from PROC_USER ap where ap.id = aur.user_id and (case when length(:userId) > 0 then (s.created_by = :userId or ap.id = :userId) else (1=1) end)))) " +
//                    "or exists (select 1 from PROC_SOURCING_FORM_REQ_TEAM tm where tm.form_id = s.id " +
//                    "or exists (select 1 from PROC_USER tu where tu.id = tm.user_id and (case when length(:userId) > 0 then (s.created_by = :userId or tu.id = :userId) else (1=1) end))))";
//        String hql = "select count(distinct s.id) from SourcingFormRequest s ";
//        hql += " where s.tenantId=:tenantId and s.status =:status " +
//                "and (exists (select 1 from s.sourcingFormApprovalRequests ar where " +
//                "exists (select 1 from ar.approvalUsersRequest aur where " +
//                "exists (select 1 from aur.user ap where CASE length(:userId) when > 0 then ( s.createdBy.id = :userId or ap.id = :userId) else (1=1) end))) " +
//                "or exists (select 1 from s.sourcingFormTeamMember tm where " +
//                "exists (select 1 from tm.user tu where CASE length(:userId)  when > 0 then ( s.createdBy.id = :userId or tu.id = :userId) else (1=1) end))) ";

            // Add on search filter conditions
            if (input != null) {
                for (ColumnParameter cp : input.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                        // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                        // cp.getSearch().getValue());
                        if (cp.getData().equals("type")) {
                            hql += " and s.type = :" + cp.getData().replace(".", "");
                        } else if (cp.getData().equals("formId")) {
                            hql += " and upper(s.form_id) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("sourcingFormName")) {
                            hql += " and upper(s.sourcing_form_name) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("referenceNumber")) {
                            hql += " and upper(s.referance_number) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("businessUnit")) {
                            hql += " and upper(s.business_unit_id)  in (select id from PROC_BUSINESS_UNIT where BUSINESS_UNIT_NAME like (:" + cp.getData().replace(".", "") + "))";
                        } else if (cp.getData().equals("formOwner")) {
                            hql += " and upper(s.FORM_OWNER) in (select id from PROC_USER where USER_NAME like (:" + cp.getData().replace(".", "") + "))";
                        } else {
                            hql += " and upper(s." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
                        }
                    }
                }
            }

            LOG.info("++++++++++++++ finished request count +++++++  " + hql);
            Query query = getEntityManager().createNativeQuery(hql);
            query.setParameter("tenantId", loggedInUserTenantId);
            query.setParameter("status", SourcingFormStatus.FINISHED.name());
//        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", StringUtils.checkString(userId).isEmpty() ? "" : userId);
//        }
            if (input != null) {
                for (ColumnParameter cp : input.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                        // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                        // cp.getSearch().getValue());
                        if (cp.getData().equals("type")) {
                            query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                        } else {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
            try {
                return ((Number) query.getSingleResult()).longValue();
            } catch (Exception e) {
                LOG.error(e);
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public long finishedRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

        try {
            String hql = "SELECT COUNT(DISTINCT s.id) FROM PROC_SOURCING_FORM_REQ s LEFT OUTER JOIN PROC_SOURCING_FORM_REQ_TEAM t ON t.form_id=s.id" +
                    " LEFT OUTER JOIN PROC_SOURCING_FORM_APP_REQ app ON app.form_id=s.id " +
                    " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON bu.id=s.business_unit_id "+
                    " LEFT OUTER JOIN PROC_FORM_APPROVAL_USER_REQ usr ON usr.form_approval_id=app.id " +
                    " LEFT OUTER JOIN PROC_USER owner ON owner.id=s.form_owner "+
                    " WHERE s.tenant_id = :tenantId AND s.status = :status ";


            if (StringUtils.checkString(userId).length() > 0) {
                if (businessUnitIds != null && !businessUnitIds.isEmpty()) {

                    hql+=("AND (s.created_by=:userId OR t.user_id=:userId OR usr.user_id=:userId OR  s.business_unit_id IN (:businessUnitIds) )");
                } else {
                    hql+=("AND (s.created_by=:userId OR t.user_id=:userId OR usr.user_id=:userId)");
                }
            }

            // Add on search filter conditions
            if (input != null) {
                for (ColumnParameter cp : input.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                        // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                        // cp.getSearch().getValue());
                        if (cp.getData().equals("type")) {
                            hql += " and s.type = :" + cp.getData().replace(".", "");
                        } else if (cp.getData().equals("formId")) {
                            hql += " and upper(s.form_id) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("sourcingFormName")) {
                            hql += " and upper(s.sourcing_form_name) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("referenceNumber")) {
                            hql += " and upper(s.referance_number) like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("businessUnit")) {
                            hql += " and upper(bu.business_unit_name)  like (:" + cp.getData().replace(".", "") + ")";
                        } else if (cp.getData().equals("formOwner")) {
                            hql += " and upper(owner.user_name)  like (:" + cp.getData().replace(".", "") + ")";
                        } else {
                            hql += " and upper(s." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
                        }
                    }
                }
            }

            LOG.info("++++++++++++++ finished request count +++++++  " + hql);
            Query query = getEntityManager().createNativeQuery(hql);
            query.setParameter("tenantId", loggedInUserTenantId);
            query.setParameter("status", SourcingFormStatus.FINISHED.name());

            if (StringUtils.checkString(userId).length() > 0) {
                query.setParameter("userId",  userId);
                if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                    query.setParameter("businessUnitIds", businessUnitIds);
                }
            }
            if (input != null) {
                for (ColumnParameter cp : input.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                        // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                        // cp.getSearch().getValue());
                        if (cp.getData().equals("type")) {
                            query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                        } else {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
            try {
                return ((Number) query.getSingleResult()).longValue();
            } catch (Exception e) {
                LOG.error(e);
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> getAllSourcingRequestList(User user, String id, TableDataInput input, Date startDate, Date endDate) {
        // StringBuffer hsql = new StringBuffer("select distinct NEW
        // com.privasia.procurehere.core.entity.SourcingFormRequest(s.id,
        // s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy
        // ,s.formOwner,s.businessUnit,s.costCenter,s.status) from SourcingFormRequest s ");
        // hsql.append(" left outer join s.formOwner left outer join s.businessUnit left outer join s.costCenter left
        // outer join s.createdBy as user left outer join s.sourcingFormApprovalRequests sfa ");
        // hsql.append(" left outer join sfa.approvalUsersRequest au where (s.tenantId =:tenantId or
        // au.user.id=:appUserId) and s.status in (:status) ");

        StringBuffer hsql = new StringBuffer("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit,s.costCenter,s.status, s.currency) from SourcingFormRequest s ");
        hsql.append(" left outer join  s.formOwner  left outer join  s.businessUnit left outer join  s.costCenter left outer join s.createdBy as user ");
        hsql.append(" left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hsql.append(" left outer join s.currency bc ");
        hsql.append(" left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ");
        hsql.append(" where s.tenantId =:tenantId ");

        LOG.info("id" + id);
        // search with Date range
        if (startDate != null && endDate != null) {
            hsql.append(" and s.createdDate between :startDate and :endDate ");
        }

        if (StringUtils.checkString(id).length() > 0) {
            hsql.append(" and (( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2))) ");
        } else {
            hsql.append(" and (s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2)))  ");
        }

        hsql.append(" order by s.createdDate desc");

        LOG.info("HQL: " + hsql);
        final Query query = getEntityManager().createQuery(hsql.toString());

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        query.setParameter("userId", user.getId());
        query.setParameter("tenantId", user.getTenantId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        return query.getResultList();
    }

    @Override
    public long getAllSourcingRequestListCount(User user, String id, Date startDate, Date endDate) {

        StringBuffer hsql = new StringBuffer("select count(distinct s.id) from SourcingFormRequest s ");
        hsql.append(" left outer join  s.formOwner  left outer join  s.businessUnit left outer join  s.costCenter left outer join s.createdBy as user ");
        hsql.append(" left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hsql.append(" left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ");
        hsql.append(" where s.tenantId =:tenantId  ");

        LOG.info("id" + id);
        // search with Date range
        if (startDate != null && endDate != null) {
            hsql.append(" and  s.createdDate between :startDate and :endDate ");
        }

        if (StringUtils.checkString(id).length() > 0) {
            hsql.append(" and ( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2)) ");
        } else {
            hsql.append(" and s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2))  ");
        }

        // hsql.append(" order by s.createdDate desc");

        final Query query = getEntityManager().createQuery(hsql.toString());

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        query.setParameter("userId", user.getId());
        query.setParameter("tenantId", user.getTenantId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        return ((Number) query.getSingleResult()).longValue();

    }

    @Override
    public long getCancelRequestCount(String loggedInUserTenantId, TableDataInput input, String userId) {
        String hql = "select count(distinct s.id) from SourcingFormRequest s left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu where s.tenantId=:tenantId and s.status =:status ";
        if (StringUtils.checkString(userId).length() > 0) {
            hql += " and ( s.createdBy.id = :userId or tu.id = :userId)";
        }

        // Add on search filter conditions
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        hql += " and type = :" + cp.getData().replace(".", "");
                    } else if (cp.getData().equals("formId")) {
                        hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ")";
                    } else if (cp.getData().equals("sourcingFormName")) {
                        hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ")";
                    } else if (cp.getData().equals("referenceNumber")) {
                        hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ")";
                    } else if (cp.getData().equals("businessUnit")) {
                        hql += " and upper(s.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
                    } else if (cp.getData().equals("formOwner")) {
                        hql += " and upper(s.formOwner.name) like (:" + cp.getData().replace(".", "") + ")";
                    } else {
                        hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ";
                    }
                }
            }
        }

        Log.info("+++++++++++++cancel request count ++++++++  " + hql);
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.CANCELED);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        if (input != null) {
            for (ColumnParameter cp : input.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                    // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                    // cp.getSearch().getValue());
                    if (cp.getData().equals("type")) {
                        query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                    } else {
                        query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                    }
                }
            }
        }

        return ((Number) query.getSingleResult()).longValue();

    }

    @Override
    public long getCancelRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds) {
        String hql = "select count(distinct s.id) from SourcingFormRequest s left outer join  s.formOwner  left outer join  s.createdBy cb  left outer join  s.businessUnit left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu" +
                " left outer join s.sourcingFormApprovalRequests far left outer join far.approvalUsersRequest aur" +
                " left outer join aur.user au where s.tenantId=:tenantId and s.status =:status ";
        if (StringUtils.checkString(userId).length() > 0) {

            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {

                hql += " and ( s.createdBy.id = :userId or tu.id = :userId or au.id=:userId ";
                hql += " or s.businessUnit.id IN (:businessUnitIds)) ";
            } else {
                hql += " and ( s.createdBy.id = :userId or tu.id = :userId or au.id=:userId )";
            }
        }

        // Add on search filter conditions
        if (input != null) {
        LOG.info("FILTER AFFECT THE COUNT NUMBER");
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql += " and type = :" + cp.getData().replace(".", "");
                } else if (cp.getData().equals("formId")) {
                    hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ")";
                } else if (cp.getData().equals("sourcingFormName")) {
                    hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ")";
                } else if (cp.getData().equals("referenceNumber")) {
                    hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ")";
                } else if (cp.getData().equals("businessUnit")) {
                    hql += " and upper(s.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
                } else if (cp.getData().equals("formOwner")) {
                    hql += " and upper(s.formOwner.name) like (:" + cp.getData().replace(".", "") + ")";
                } else {
                    hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ";
                }
            }
        }
        }
        LOG.info("+++++++++++++cancel request count ++++++++  " + hql);
        Query query = getEntityManager().createQuery(hql);
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.CANCELED);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
        }
         if (input != null) {
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
         }

        return ((Number) query.getSingleResult()).longValue();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> myCancelRequestList(String loggedInUserTenantId, String userId, TableDataInput input) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu where s.tenantId =:tenantId and s.status =:status ");
        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and (cb.id=:userId or tu.id =:userId)");
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }
        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.CANCELED);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    public List<SourcingFormRequest> myCancelRequestListBizUnit(String loggedInUserTenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu" +
                " left outer join s.sourcingFormApprovalRequests far left outer join far.approvalUsersRequest aur" +
                " left outer join aur.user au "+
                " where s.tenantId =:tenantId and s.status =:status ");
        if (StringUtils.checkString(userId).length() > 0) {
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {

                hql.append(" and ( s.createdBy.id = :userId or tu.id = :userId or au.id=:userId ");

                hql.append(" OR s.businessUnit.id IN (:businessUnitIds)) ");
            } else {
                hql.append(" and ( s.createdBy.id = :userId or tu.id = :userId or au.id=:userId) ");
            }
        }

        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and type = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }
        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }
        LOG.info("myCancelRequestListBizUnit "+hql.toString());
        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        query.setParameter("status", SourcingFormStatus.CANCELED);
        if (StringUtils.checkString(userId).length() > 0) {
             query.setParameter("userId", userId);
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
        }
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SourcingFormRequest> myFinishRequestList(String loggedInUserTenantId, String userId, TableDataInput input) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit left outer join s.sourcingFormApprovalRequests ar left outer join ar.approvalUsersRequest aur left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.tenantId =:tenantId and s.status =:status ");

        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and ( cb.id=:userId or ap.id = :userId or tu.id= :userId)");
        }
        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and upper(s.type) = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }

        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
        if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }
        query.setParameter("status", SourcingFormStatus.FINISHED);
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SourcingFormRequest> myFinishRequestListBizUnit(String loggedInUserTenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
        StringBuilder hql = new StringBuilder("select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit) from SourcingFormRequest s ");
        hql.append(" left outer join  s.formOwner left outer join  s.createdBy cb left outer join  s.businessUnit " +
                "left outer join s.sourcingFormApprovalRequests ar" +
                " left outer join ar.approvalUsersRequest aur" +
                " left outer join aur.user ap ");
        hql.append(" left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hql.append(" where s.tenantId =:tenantId and s.status =:status ");

/*        if (StringUtils.checkString(userId).length() > 0) {
            hql.append(" and ( cb.id=:userId or ap.id = :userId or tu.id= :userId)");
        }*/
        if (StringUtils.checkString(userId).length() > 0) {
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {

                hql.append(" and ( cb.id=:userId or ap.id = :userId or tu.id= :userId ");
                hql.append(" or s.businessUnit.id IN (:businessUnitIds)) ");
            } else {
                hql.append(" and ( cb.id=:userId or ap.id = :userId or tu.id= :userId) ");
            }
        }
        // Add on search filter conditions
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    hql.append(" and upper(s.type) = :" + cp.getData().replace(".", ""));
                } else if (cp.getData().equals("formOwner")) {
                    hql.append(" and upper(s.formOwner.name) like (:" + cp.getData() + ")");
                } else if (cp.getData().equals("businessUnit")) {
                    hql.append(" and upper(s.businessUnit.unitName) like (:" + cp.getData() + ")");
                } else {
                    hql.append(" and upper(s." + cp.getData() + ") like (:" + cp.getData() + ") ");
                }
            }
        }

        // Implement order by
        List<OrderParameter> orderList = input.getOrder();
        if (CollectionUtil.isNotEmpty(orderList)) {
            hql.append(" order by ");
            for (OrderParameter order : orderList) {
                String orderColumn = input.getColumns().get(order.getColumn()).getData();
                String dir = order.getDir();
                if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                    orderColumn = "s.sourcingFormName";
                } else if (orderColumn.equals("type")) {
                    orderColumn = "s.type";
                } else if (orderColumn.equals("referanceNumber")) {
                    orderColumn = "s.referanceNumber";
                } else if (orderColumn.equals("createdDate")) {
                    orderColumn = "s.createdDate";
                } else if (orderColumn.equals("createdBy")) {
                    orderColumn = "s.createdBy";
                } else if (orderColumn.equals("formId")) {
                    orderColumn = "s.formId";
                } else if (orderColumn.equals("formOwner")) {
                    orderColumn = "s.formOwner";
                } else if (orderColumn.equals("businessUnit")) {
                    orderColumn = "s.businessUnit";
                }
                hql.append(" " + orderColumn + " " + dir);
            }
            if (hql.lastIndexOf(",") == hql.length() - 1) {
                hql.substring(0, hql.length() - 1);
            }
        } else {
            hql.append(" order by eventEnd DESC");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", loggedInUserTenantId);
       /* if (StringUtils.checkString(userId).length() > 0) {
            query.setParameter("userId", userId);
        }*/
        if (StringUtils.checkString(userId).length() > 0) {
             query.setParameter("userId", userId);
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
        }
        query.setParameter("status", SourcingFormStatus.FINISHED);
        for (ColumnParameter cp : input.getColumns()) {
            if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                // LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
                // cp.getSearch().getValue());
                if (cp.getData().equals("type")) {
                    query.setParameter(cp.getData(), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
                } else {
                    query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                }
            }
        }
        query.setFirstResult(input.getStart());
        query.setMaxResults(input.getLength());
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest findByFormIdById(String formId) {
        try {
            final Query query = getEntityManager().createQuery("from SourcingFormRequest s join fetch s.formOwner join fetch s.createdBy left outer join fetch s.modifiedBy left outer join fetch s.sourcingForm left outer join fetch s.costCenter cc left outer join fetch s.rfsDocuments as pd where s.id =:formId");
            query.setParameter("formId", formId);
            List<SourcingFormRequest> uList = query.getResultList();
            if (CollectionUtil.isNotEmpty(uList)) {
                return uList.get(0);
            } else {

                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while getting user : " + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest findByApprovalDocumentFormIdById(String formId) {
        try {
            final Query query = getEntityManager().createQuery("from SourcingFormRequest s join fetch s.formOwner join fetch s.createdBy left outer join fetch s.modifiedBy left outer join fetch s.sourcingForm left outer join fetch s.costCenter cc left outer join fetch s.approvalDocuments as pd where s.id =:formId");
            query.setParameter("formId", formId);
            List<SourcingFormRequest> uList = query.getResultList();
            if (CollectionUtil.isNotEmpty(uList)) {
                return uList.get(0);
            } else {

                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while getting user : " + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest getSourcingFormForAdditionalApproverById(String formId) {
        try {
            final Query query = getEntityManager().createQuery("select distinct s from SourcingFormRequest s left outer join fetch s.sourcingFormApprovalRequests sfa where s.id =:formId order by sfa.batchNo asc");
            query.setParameter("formId", formId);
            List<SourcingFormRequest> uList = query.getResultList();
            if (CollectionUtil.isNotEmpty(uList)) {
                return uList.get(0);
            } else {

                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while getting user : " + e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormApprovalRequest> getApprovalWithOutDone(String rfsId) {
        String hql = "select distinct sra from SourcingFormRequest rq  left outer join rq.sourcingFormApprovalRequests sra where rq.id=:requestId and sra.done=false";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", rfsId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EventTeamMember> getPlainTeamMembersForSourcing(String formId) {
        final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from SourcingFormTeamMember tm left outer join tm.user u where tm.sourcingFormRequest.id =:formId");
        query.setParameter("formId", formId);
        List<EventTeamMember> list = query.getResultList();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormTeamMember getTeamMemberByUserIdAndFormId(String formId, String userId) {
        StringBuilder hsql = new StringBuilder("from SourcingFormTeamMember tm where tm.sourcingFormRequest.id =:formId and tm.user.id =:userId");
        Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("formId", formId);
        query.setParameter("userId", userId);
        List<SourcingFormTeamMember> uList = query.getResultList();
        if (CollectionUtil.isNotEmpty(uList)) {
            return uList.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RequestComment> findAllSourcingCommentsByFormId(String formId) {
        StringBuilder hsql = new StringBuilder("select distinct rc from RequestComment rc inner join fetch rc.request r left outer join fetch rc.createdBy where r.id= :formId order by rc.createdDate");
        final Query query = getEntityManager().createQuery(hsql.toString());
        query.setParameter("formId", formId);
        List<RequestComment> prCommentList = query.getResultList();
        return prCommentList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SourcingFormRequest getSourcingFormByFormIdAndTenant(String formId, String tenantId) {
        StringBuilder hql = new StringBuilder("select distinct s from SourcingFormRequest s join fetch s.formOwner own join fetch s.createdBy cb where s.formId = :formId and s.tenantId = :tenantId");
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        query.setParameter("formId", formId);
        List<SourcingFormRequest> list = query.getResultList();
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestCqItem> getCqItembyRequestIdCqId(String requestId, String cqId) {
        String hql = "select distinct cq from SourcingFormRequestCqItem cq left outer join fetch cq.cqItem cqi left outer join fetch cq.listAnswers left outer join fetch cq.cq where cq.sourcingFormRequest.id=:requestId and cqi.cq.id=:cqId order by cqi.level,cqi.order";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        query.setParameter("cqId", cqId);
        List<SourcingFormRequestCqItem> list = query.getResultList();

        // Tables with BLOB type do not return distinct results. We have to manually eliminate it

        LinkedHashMap<String, SourcingFormRequestCqItem> map = new LinkedHashMap<String, SourcingFormRequestCqItem>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (SourcingFormRequestCqItem item : list) {
                map.put(item.getId(), item);
            }
        }
        return new ArrayList<SourcingFormRequestCqItem>(map.values());

    }

    @Override
    public boolean isBudgetCheckingEnabledForBusinessUnit(String tenantId, String formId) {
        StringBuilder hql = new StringBuilder("select s.businessUnit.budgetCheck from  SourcingFormRequest s  where s.id = :formId and s.tenantId = :tenantId");
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        query.setParameter("formId", formId);
        return (((Boolean) query.getSingleResult()).booleanValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deleteRfs(String tenantId) {

        final Query queryrfs1 = getEntityManager().createQuery("select distinct id from SourcingFormRequest where tenantId = :tenantId").setParameter("tenantId", tenantId);
        List<String> rfslist = queryrfs1.getResultList();
        for (String sid : rfslist) {
            final Query query1 = getEntityManager().createQuery("select distinct id from SourcingFormApprovalRequest where sourcingFormRequest.id = :id").setParameter("id", sid);
            List<String> applist = query1.getResultList();
            for (String id : applist) {
                Query query2 = getEntityManager().createNativeQuery("DELETE FROM PROC_FORM_APPROVAL_USER_REQ  WHERE FORM_APPROVAL_ID = :id").setParameter("id", id);
                query2.executeUpdate();
            }
            Query query2 = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_APP_REQ  WHERE FORM_ID = :id").setParameter("id", sid);
            query2.executeUpdate();

            final Query querybq = getEntityManager().createQuery("select distinct id from SourcingFormRequestBq where sourcingFormRequest.id = :id").setParameter("id", sid);
            List<String> bqList = querybq.getResultList();
            for (String bqid : bqList) {
                Query querybqdchilditem = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_BQ_ITEM_REQ  WHERE BQ_ID = :id AND PARENT_ID IS NOT NULL ").setParameter("id", bqid);
                querybqdchilditem.executeUpdate();
                Query querybqdep = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_BQ_ITEM_REQ  WHERE BQ_ID = :id").setParameter("id", bqid);
                querybqdep.executeUpdate();
            }

            Query queryAudit = getEntityManager().createNativeQuery("DELETE FROM PROC_REQ_AUDIT WHERE REQ_ID = :id").setParameter("id", sid);
            queryAudit.executeUpdate();

            Query queryComment = getEntityManager().createNativeQuery("DELETE FROM PROC_REQUEST_COMMENTS WHERE RQUEST_ID = :id").setParameter("id", sid);
            queryComment.executeUpdate();

            Query queryTeam = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_REQ_TEAM WHERE FORM_ID = :id").setParameter("id", sid);
            queryTeam.executeUpdate();

            Query queryDocument = getEntityManager().createNativeQuery("DELETE FROM PROC_RFS_DOCUMENTS WHERE RFS_ID = :id").setParameter("id", sid);
            queryDocument.executeUpdate();

            Query querybqdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_BQ_REQ  WHERE FORM_REQ_ID = :id").setParameter("id", sid);
            querybqdel.executeUpdate();

            Query querycqOpsdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOUR_FORM_CQ_ITEM_OPT_REQ  WHERE REQ_CQ_ITEM_ID in (SELECT cqi.ID FROM PROC_SOUR_FORM_CQ_ITEM_REQ cqi WHERE cqi.FORM_REQ_ID  = :id)").setParameter("id", sid);
            querycqOpsdel.executeUpdate();

            Query querycqdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOUR_FORM_CQ_ITEM_REQ  WHERE FORM_REQ_ID = :id").setParameter("id", sid);
            querycqdel.executeUpdate();

        }

        Query query0 = getEntityManager().createQuery("delete from SourcingFormRequest  where tenantId = :tenantId").setParameter("tenantId", tenantId);
        query0.executeUpdate();

        final Query query = getEntityManager().createQuery("select distinct id from SourcingFormTemplate where tenantId = :tenantId");
        query.setParameter("tenantId", tenantId);
        List<String> list = query.getResultList();
        for (String tid : list) {
            final Query query1 = getEntityManager().createQuery("select distinct id from SourcingTemplateApproval where sourcingForm.id = :id").setParameter("id", tid);
            List<String> applist = query1.getResultList();
            for (String id : applist) {
                Query query2 = getEntityManager().createNativeQuery("DELETE FROM PROC_FORM_APPROVAL_USER  WHERE TEMPLATE_APPROVAL_ID = :id").setParameter("id", id);
                query2.executeUpdate();
            }
            Query querycqOpsdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOUR_FORM_CQ_ITEM_OPT  WHERE  CQ_ITEM_ID in (SELECT cqi.ID FROM PROC_SOURCING_FORM_CQ_ITEM cqi WHERE cqi.FORM_ID  = :id)").setParameter("id", tid);
            querycqOpsdel.executeUpdate();

            Query querycqChildItemdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_CQ_ITEM  WHERE PARENT_ID IS NOT NULL AND FORM_ID = :id").setParameter("id", tid);
            querycqChildItemdel.executeUpdate();

            Query querycqItemdel = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_CQ_ITEM  WHERE FORM_ID = :id").setParameter("id", tid);
            querycqItemdel.executeUpdate();

            Query queryTcq = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_CQ  WHERE FORM_ID = :id").setParameter("id", tid);
            queryTcq.executeUpdate();

            Query queryTf = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_TEMPLATE_FIELD  WHERE TEMPLATE_ID = :id").setParameter("id", tid);
            queryTf.executeUpdate();

            Query query2 = getEntityManager().createNativeQuery("DELETE FROM PROC_SOURCING_FORM_APP  WHERE TEMPLATE_ID = :id").setParameter("id", tid);
            query2.executeUpdate();
        }

        // PROC_SOURCING_FORM_APP
        Query query2 = getEntityManager().createQuery("delete from SourcingFormTemplate  where tenantId = :tenantId").setParameter("tenantId", tenantId);
        query2.executeUpdate();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> findAllSourcingRequestForTenant(String tenantId) {
        StringBuilder hql = new StringBuilder("select s from  SourcingFormRequest s where s.tenantId=:tenantId");
        Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("tenantId", tenantId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SourcingFormRequestPojo> getAllSourcingWithSearchFilter(String tenantId, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate) {

        List<EventStatus> list = new ArrayList<EventStatus>();
        String sql = "";

        sql += getSqlForAllSourcingReport(tenantId, eventIds, sourcingFormRequestPojo, select_all, startDate, endDate, list);

        LOG.info("====>" + sql);
        final Query query = getEntityManager().createNativeQuery(sql, "sourcingReportExcelData");

        if (!(select_all)) {
            if (eventIds != null && eventIds.length > 0) {
                query.setParameter("eventIds", Arrays.asList(eventIds));
            }
        }

        if (sourcingFormRequestPojo != null) {
            if (StringUtils.checkString(sourcingFormRequestPojo.getRequestid()).length() > 0) {
                query.setParameter("requestid", "%" + sourcingFormRequestPojo.getRequestid().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getNameofrequest()).length() > 0) {
                query.setParameter("nameofrequest", "%" + sourcingFormRequestPojo.getNameofrequest().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getReferencenumber()).length() > 0) {
                query.setParameter("referencenumber", "%" + sourcingFormRequestPojo.getReferencenumber().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getRequestowner()).length() > 0) {
                query.setParameter("requestowner", "%" + sourcingFormRequestPojo.getRequestowner().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getCreatedBy()).length() > 0) {
                query.setParameter("createdBy", "%" + sourcingFormRequestPojo.getCreatedBy().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getCostcenter()).length() > 0) {
                query.setParameter("costcenter", "%" + sourcingFormRequestPojo.getCostcenter().toUpperCase() + "%");
            }
            if (sourcingFormRequestPojo.getBaseCurrency() != null) {
                query.setParameter("baseCurrency", sourcingFormRequestPojo.getBaseCurrency().toUpperCase() + "%");
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getBusinessunit()).length() > 0) {
                query.setParameter("businessunit", "%" + sourcingFormRequestPojo.getBusinessunit().toUpperCase() + "%");
            }
            if (sourcingFormRequestPojo.getStatus() != null) {
                query.setParameter("status", sourcingFormRequestPojo.getStatus().toString());
            }

        }

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        query.setParameter("tenantId", tenantId);
        // query.getResultList();
        List<SourcingFormRequestPojo> finalList = query.getResultList();

        return finalList;
    }

    private String getSqlForAllSourcingReport(String tenantId, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list) {
        String sql = "";

        sql += "SELECT sf.ID AS id, sf.FORM_ID AS formId, sf.SOURCING_FORM_NAME AS sourcingFormName, sf.REFERANCE_NUMBER AS referanceNumber, owner.USER_NAME AS createdBy, sf.CREATED_DATE AS createdDate, owner.USER_NAME AS formOwner, bu.BUSINESS_UNIT_NAME AS businessUnit, cc.COST_CENTER AS costCenter, bc.CURRENCY_CODE AS baseCurrency,sf.STATUS AS status, sf.GROUP_CODE AS groupCode,psft.FORM_NAME AS templateName, sf.DESCRIPTION AS description, sf.BUDGET_AMOUNT AS availableBudget,sf.HISTORICAL_AMOUNT AS estimatedBudget, sf.SUBMITTED_DATE AS submittedDate,sf.APPROVED_DATE AS approvedDate,sf.APPROVAL_DAYS_HOURS AS approvalDaysHours,sf.APPROVAL_TOTAL_LEVELS AS approvalTotalLevels, sf.APPROVAL_TOTAL_USERS AS approvalTotalUsers FROM PROC_SOURCING_FORM_REQ sf " + "LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON sf.BUSINESS_UNIT_ID = bu.ID " + "LEFT OUTER JOIN PROC_COST_CENTER cc ON sf.COST_CENTER = cc.ID " + "LEFT OUTER JOIN PROC_CURRENCY bc ON sf.BASE_CURRENCY = bc.ID " + "LEFT OUTER JOIN PROC_SOURCING_FORM_TEMPLATE psft ON sf.SOURCING_TEMPLATE_ID= psft.ID " + "JOIN PROC_USER owner ON sf.FORM_OWNER = owner.ID AND sf.CREATED_BY= owner.ID " + "WHERE sf.TENANT_ID = :tenantId ";

        if (!(select_all)) {
            if (eventIds != null && eventIds.length > 0) {
                sql += " and sf.ID in (:eventIds)";
            }
        }

        if (sourcingFormRequestPojo != null) {
            if (StringUtils.checkString(sourcingFormRequestPojo.getRequestid()).length() > 0) {
                sql += " and upper(sf.FORM_ID) like :requestid";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getNameofrequest()).length() > 0) {
                sql += " and upper(sf.SOURCING_FORM_NAME) like :nameofrequest";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getReferencenumber()).length() > 0) {
                sql += " and upper(sf.REFERANCE_NUMBER) like :referencenumber";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getCreatedBy()).length() > 0) {
                sql += " and upper(sf.CREATED_BY) like :createdBy";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getRequestowner()).length() > 0) {
                sql += " and upper(owner.USER_NAME) like :requestowner";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getBusinessunit()).length() > 0) {
                sql += " and upper(bu.BUSINESS_UNIT_NAME) like :businessunit";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getCostcenter()).length() > 0) {
                sql += " and upper(cc.COST_CENTER) like :costcenter";
            }
            if (StringUtils.checkString(sourcingFormRequestPojo.getCostcenter()).length() > 0) {
                sql += " and upper(bc.BASE_CURRENCY) like :baseCurrency";
            }
            if (sourcingFormRequestPojo.getStatus() != null) {
                sql += " and sf.STATUS = :status";
            }

        }
        // search with Date range
        if (startDate != null && endDate != null) {
            sql += " and sf.CREATED_DATE between :startDate and :endDate ";
        }
        // sql += " GROUP BY sf.FORM_ID, sf.SOURCING_FORM_NAME, sf.CREATED_BY, sf.CREATED_DATE, sf.FORM_OWNER,
        // bu.DISPLAY_NAME, cc.COST_CENTER, sf.STATUS, sf.REFERANCE_NUMBER";
        return sql;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> getAllSourcingFormRequestList(User user, String id, TableDataInput tableParams, Date startDate, Date endDate) {
        final Query query = constructAllSourcingFormRequestList(user, id, tableParams, startDate, endDate, false);
        query.setFirstResult(tableParams.getStart());
        query.setMaxResults(tableParams.getLength());
        return query.getResultList();
    }

    @Override
    public long getAllSourcingFormRequestFilterList(User user, String id, TableDataInput tableParams, Date startDate, Date endDate) {
        final Query query = constructAllSourcingFormRequestList(user, id, tableParams, startDate, endDate, true);
        try {
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private Query constructAllSourcingFormRequestList(User user, String id, TableDataInput tableParams, Date startDate, Date endDate, boolean isCount) {

        String hql = "";

        if (isCount) {
            hql += "select count(distinct s.id) ";
        } else {
            hql += "select distinct NEW com.privasia.procurehere.core.entity.SourcingFormRequest(s.id, s.sourcingFormName,s.referanceNumber,s.formId,s.description, s.createdDate, s.createdBy ,s.formOwner,s.businessUnit,s.costCenter,s.status) ";
        }

        hql += " from SourcingFormRequest s left outer join  s.sourcingFormTeamMember tm left outer join tm.user tu left outer join  s.formOwner fo left outer join  s.businessUnit bu left outer join s.costCenter cc left outer join s.createdBy as user left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ";
        hql += " where (s.tenantId =:tenantId  and s.status in (:status)";
        if (StringUtils.checkString(id).length() > 0) {
            hql += " and (user.id =:userId or au.user.id=:userId or tu.id =:userId) ";
        }

        LOG.info("id" + id);
        // search with Date range
        if (startDate != null && endDate != null) {
            hql += " and  s.createdDate between :startDate and :endDate ";
        }

        // Add on search filter conditions
        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            hql += " and s.status =:status ";
                        } else {
                            hql += " and s.status in (:status) ";
                        }
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            if (cp.getData().equalsIgnoreCase("referanceNumber")) {
                                hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equalsIgnoreCase("sourcingFormName")) {
                                hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equalsIgnoreCase("formId")) {
                                hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equalsIgnoreCase("formOwner")) {
                                hql += " and upper(fo.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equalsIgnoreCase("businessUnit")) {
                                hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equalsIgnoreCase("costCenter")) {
                                hql += " and upper(cc.costCenter) like (:" + cp.getData().replace(".", "") + ") ";
                            } else {
                                hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                            }

                        }

                    }
                }
            }
        }

        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql += " order by ";
                for (OrderParameter order : orderList) {
                    String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
                    String dir = order.getDir();
                    if (orderColumn.equalsIgnoreCase("referanceNumber")) {
                        hql += " s.referanceNumber " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                        hql += " s.sourcingFormName " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("formId")) {
                        hql += " s.formId " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("formOwner")) {
                        hql += " fo.name " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("businessUnit")) {
                        hql += " bu.unitName " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("costCenter")) {
                        hql += " cc.costCenter " + dir + ",";
                    } else {
                        hql += " s." + orderColumn + " " + dir + ",";
                    }

                }
                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = hql.substring(0, hql.length() - 1);
                }
            } else {
                hql += " order by s.createdDate desc ";
            }
        }

        // hql += " order by s.createdDate desc";
        final Query query = getEntityManager().createQuery(hql);

        // Apply search filter values
        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter("status", SourcingFormStatus.fromString(cp.getSearch().getValue()));
                        } else {
                            List<SourcingFormStatus> status = Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED);
                            query.setParameter("status", status);
                        }
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
        }

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        if (StringUtils.checkString(id).length() > 0) {
            query.setParameter("userId", id);
        }
        query.setParameter("tenantId", user.getTenantId());
        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getNotSectionAddedRfsBq(String requestId) {
        final Query query = getEntityManager().createQuery("select bq1.name from SourcingFormRequestBq bq1 where bq1.sourcingFormRequest.id = :requestId and bq1.id not in (select bq.id from SourcingFormRequestBq bq inner join bq.bqItems item where bq.sourcingFormRequest.id = :requestId and item.order > 0 group by bq.id having count(item) > 0)");
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getNotSectionItemAddedRfsBq(String requestId) {
        final Query query = getEntityManager().createQuery("select bq1.name from SourcingFormRequestBqItem i inner join i.bq bq1 where i.sourcingFormRequest.id = :requestId and i.parent is null and (select count(child.id) from SourcingFormRequestBqItem child where child.parent.id = i.id ) = 0");
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @Override
    public long findTotaApprovalLevelsRequestCount(String requestId) {
        final Query query = getEntityManager().createQuery("select count(sfa) from  SourcingFormApprovalRequest sfa where sfa.sourcingFormRequest.id =:requestId )");
        query.setParameter("requestId", requestId);
        return ((Number) query.getSingleResult()).longValue();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormApprovalRequest> getApprovalRequestList(String requestId) {
        String hql = "select distinct rq from SourcingFormApprovalRequest rq  inner join rq.approvalUsersRequest where rq.sourcingFormRequest.id=:requestId ";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("requestId", requestId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormTeamMember> findAssociateOwnerOfRfs(String id, TeamMemberType associateOwner) {
        String hql = " from SourcingFormTeamMember ptm where ptm.sourcingFormRequest.id = :prId and ptm.teamMemberType =:associateOwner";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("prId", id);
        query.setParameter("associateOwner", associateOwner);

        List<SourcingFormTeamMember> prTeamList = query.getResultList();
        return prTeamList;
    }

    @Override
    public long getTotalSourcingRequestCountForList(User user, String id, Date startDate, Date endDate) {
        LOG.info("IS THIS BEING USED ?????????????");
        StringBuffer hsql = new StringBuffer("select count(*) from SourcingFormRequest s where s.id in (select distinct s.id from SourcingFormRequest s ");
        hsql.append(" left outer join  s.formOwner  left outer join  s.businessUnit left outer join  s.costCenter left outer join s.createdBy as user ");
        hsql.append(" left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hsql.append(" left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ");
        hsql.append(" where s.tenantId =:tenantId  ");

        // search with Date range
        if (startDate != null && endDate != null) {
            hsql.append(" and  s.createdDate between :startDate and :endDate ");
        }

        if (StringUtils.checkString(id).length() > 0) {
            hsql.append(" and ( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2)) ");
        } else {
            hsql.append(" and s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2))  ");
        }
        hsql.append(" )");
        final Query query = getEntityManager().createQuery(hsql.toString());

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        LOG.info(">>>>>>>>>> COUNT tenantId " + user.getTenantId());
        LOG.info(">>>>>>>>>> COUNT userId " + user.getId());
        query.setParameter("userId", user.getId());
        query.setParameter("tenantId", user.getTenantId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        try {
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestPojo> getAllSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate, int pageSize, int pageNo) {
        LOG.info("loaded this function>>>>>>>>>>>>>>>>>>>>>>>> startDate: " + startDate);
        Query query = constructCountQueryForSourcingRequestForList(user, id, input, startDate, endDate, false);
        if (pageSize != 0) {
            query.setFirstResult((pageSize * pageNo));
            query.setMaxResults(pageSize);
        } else {
            query.setFirstResult(input.getStart());
            query.setMaxResults(input.getLength());
        }
        return query.getResultList();
    }

    @Override
    public long getFilteredCountOfSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate) {
        LOG.info("filtered sourcing list loaded id>>>>>>>>>>>>>>  " + id);
        final Query query = constructCountQueryForSourcingRequestForList(user, id, input, startDate, endDate, true);
        try {
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private Query constructCountQueryForSourcingRequestForList(User user, String id, TableDataInput tableParams, Date startDate, Date endDate, boolean isCount) {
        LOG.info("original sourcing list loaded");

        String hql = "";

        if (isCount) {
            hql += "select count(*) from SourcingFormRequest s where s.id in (select distinct s.id ";
        } else {
            hql += "select distinct NEW com.privasia.procurehere.core.pojo.SourcingFormRequestPojo(s.id, s.sourcingFormName, s.referanceNumber, s.formId, s.description, s.createdDate, user.name ,owner.name, bu.unitName, cc.costCenter, s.status, bc.currencyCode, temp.formName, s.budgetAmount, s.historicaAmount, s.submittedDate, s.approvedDate, s.approvalDaysHours, s.approvalTotalLevels, s.approvalTotalUsers, gc.groupCode) ";
        }
        hql += "from SourcingFormRequest s ";
        hql += "left outer join s.formOwner as owner left outer join s.businessUnit as bu left outer join s.costCenter as cc left outer join s.createdBy as user ";
        hql += "left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ";
        hql += "left outer join s.currency bc left outer join s.sourcingForm temp ";
        hql += "left outer join s.groupCode as gc ";
        hql += "left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ";
        hql += "where s.tenantId =:tenantId ";

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += "and s.createdDate between :startDate and :endDate ";
        }

        if (StringUtils.checkString(id).length() > 0) {
            hql += " and (( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2))) ";
        } else {
            hql += " and (s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2))) ";
        }

        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
                    LOG.info("-----------------cp.getSearch().getValue() :::- " + cp.getSearch().getValue() + " --------- " + cp.getData());
                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            hql += " and s.status =:statuses ";
                        } else {
                            hql += " and s.status in (:statuses) ";
                        }
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            if (cp.getData().equals("sourcingFormName")) {
                                hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("sourcingrequestid")) {
                                hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("referencenumber")) {
                                hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("createdBy")) {
                                hql += " and upper(user.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("requestowner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("baseCurrency")) {
                                hql += " and upper(bc.currencyCode) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("businessunit")) {
                                hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("formOwner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("eventIds")) {
                                hql += " and s.id in (:ids) ";
                            } else if (cp.getData().equals("costcenter")) {
                                hql += " and upper(cc.costCenter) like (:" + cp.getData().replace(".", "") + ") ";
                            } else {
                                hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                            }
                        }
                    }
                }
            }
        }

        if (!isCount) {
            List<OrderParameter> orderList = tableParams.getOrder();
            if (CollectionUtil.isNotEmpty(orderList)) {
                hql += " order by ";
                for (OrderParameter op : orderList) {
                    String orderColumn = tableParams.getColumns().get(op.getColumn()).getData();
                    String dir = op.getDir();
                    if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                        hql += " s.sourcingFormName " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("formId")) {
                        hql += " s.formId " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("referanceNumber")) {
                        hql += " s.referanceNumber " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("createdBy")) {
                        hql += " user.name " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("createdDate")) {
                        hql += " s.createdDate " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("formOwner")) {
                        hql += " owner.name " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("businessunit")) {
                        hql += " bu.unitName " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("costcenter")) {
                        hql += " cc.costCenter " + dir + ",";
                    } else if (orderColumn.equalsIgnoreCase("status")) {
                        hql += " s.status " + dir + ",";
                    } else {
                        hql += " e." + orderColumn + " " + dir + ",";
                    }
                }

                if (hql.lastIndexOf(",") == hql.length() - 1) {
                    hql = hql.substring(0, hql.length() - 1);
                }
            } else {
                if (!isCount) {
                    hql += " order by s.createdDate desc ";
                }
            }
        }
        if (isCount) {
            hql += " )";
        }
        LOG.info("HQL >>>>>>>>: " + hql);
        final Query query = getEntityManager().createQuery(hql.toString());

        // Apply search filter values
        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter("statuses", SourcingFormStatus.valueOf(cp.getSearch().getValue()));
                        } else {
                            List<SourcingFormStatus> statusList = Arrays.asList(SourcingFormStatus.APPROVED, SourcingFormStatus.DRAFT, SourcingFormStatus.PENDING, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED);
                            query.setParameter("statuses", statusList);
                        }
                    } else if (cp.getData().equals("eventIds")) {
                        query.setParameter("ids", Arrays.asList(cp.getSearch().getValue().split(",")));
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
        }
        LOG.info(">>>>>>>>>>tenantId " + user.getTenantId());
        LOG.info(">>>>>>>>>>userId " + user.getId());
        query.setParameter("tenantId", user.getTenantId());
        query.setParameter("userId", user.getId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
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
    public List<SourcingFormRequestPojo> getAllSourcingRequestForCsv(User user, String id, TableDataInput tableParams, Date startDate, Date endDate, int page_size, int pageNo) {
        String hql = "";

        hql += "select distinct NEW com.privasia.procurehere.core.pojo.SourcingFormRequestPojo(s.id, s.createdDate, s.sourcingFormName, s.referanceNumber, s.formId, s.description, user.name, s.status, owner.name, pm.procurementMethod, pc.procurementCategories, bu.unitName, cc.costCenter, bc.currencyCode, temp.formName, s.budgetAmount, s.estimatedBudget, s.historicaAmount, s.submittedDate, s.approvedDate, s.approvalDaysHours, s.approvalTotalLevels, s.approvalTotalUsers, gc.groupCode || '-' || gc.description, s.groupCodeOld) ";
        hql += "from SourcingFormRequest s ";
        hql += "left outer join s.formOwner as owner left outer join s.businessUnit as bu left outer join s.costCenter as cc left outer join s.createdBy as user ";
        hql += "left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ";
        hql += "left outer join s.currency bc left outer join s.sourcingForm temp ";
        hql += "left outer join s.groupCode as gc ";
        hql += "left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ";
        hql += "left outer join s.procurementMethod pm left outer join s.procurementCategories pc ";
        hql += "where s.tenantId =:tenantId ";

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += "and s.createdDate between :startDate and :endDate ";
        }

        if (StringUtils.checkString(id).length() > 0) {
            hql += " and (( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2))) ";
        } else {
            hql += " and (s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2))) ";
        }

        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
                    LOG.info("-----------------cp.getSearch().getValue() :::- " + cp.getSearch().getValue() + " --------- " + cp.getData());
                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            hql += " and s.status =:statuses ";
                        } else {
                            hql += " and s.status in (:statuses) ";
                        }
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            if (cp.getData().equals("sourcingFormName")) {
                                hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("sourcingrequestid")) {
                                hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("referencenumber")) {
                                hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("createdBy")) {
                                hql += " and upper(user.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("requestowner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("baseCurrency")) {
                                hql += " and upper(bc.currencyCode) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("businessunit")) {
                                hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("formOwner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("eventIds")) {
                                hql += " and s.id in (:ids) ";
                            } else if (cp.getData().equals("costcenter")) {
                                hql += " and upper(cc.costCenter) like (:" + cp.getData().replace(".", "") + ") ";
                            } else {
                                hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                            }
                        }
                    }
                }
            }
        }

        LOG.info("HQL >>>>>>>>: " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());

        // Apply search filter values
        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter("statuses", SourcingFormStatus.valueOf(cp.getSearch().getValue()));
                        } else {
                            List<SourcingFormStatus> statusList = Arrays.asList(SourcingFormStatus.APPROVED, SourcingFormStatus.DRAFT, SourcingFormStatus.PENDING, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED);
                            query.setParameter("statuses", statusList);
                        }
                    } else if (cp.getData().equals("eventIds")) {
                        query.setParameter("ids", Arrays.asList(cp.getSearch().getValue().split(",")));
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
        }

        query.setParameter("tenantId", user.getTenantId());
        query.setParameter("userId", user.getId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        query.setFirstResult((page_size * pageNo));
        query.setMaxResults(page_size);
        return query.getResultList();
    }

    @Override
    public long getTotalSourcingRequestCountForTenantId(String searchValue, String tenantId, String userId) {
        LOG.info("assignedSourcingTemplates 3");
        StringBuffer sb = new StringBuffer("select count(s) from SourcingFormRequest s left outer join s.createdBy cb left outer join s.sourcingForm temp where s.tenantId =:tenantId");

        sb.append(" and s.sourcingForm.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) and s.sourcingForm.status =:status ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(s.sourcingFormName) like :searchValue or upper(s.referanceNumber)like :searchValue)");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id=:userId");
        }

        final Query query = getEntityManager().createQuery(sb.toString());
        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }

        query.setParameter("userId", userId);
        query.setParameter("status", SourcingStatus.ACTIVE);

        return ((Number) query.getSingleResult()).longValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequest> searchSourcingRequestByNameAndRefNumForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId) {
        LOG.info("assignedSourcingTemplates 4");
        StringBuffer sb = new StringBuffer("select distinct s from SourcingFormRequest s left outer join fetch s.createdBy cb left outer join fetch s.costCenter cs left outer join fetch cs.createdBy ctd left outer join fetch s.sourcingForm temp where s.tenantId =:tenantId");

        sb.append(" and s.sourcingForm.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) and s.sourcingForm.status =:status ");

        if (StringUtils.checkString(searchValue).length() > 0) {
            sb.append(" and (upper(s.sourcingFormName) like :searchValue or upper(s.referanceNumber)like :searchValue)");
        }

        if (StringUtils.checkString(userId).length() > 0) {
            sb.append(" and cb.id=:userId");
        }
        sb.append(" order by s.createdDate desc");

        final Query query = getEntityManager().createQuery(sb.toString());

        query.setParameter("tenantId", tenantId);
        if (StringUtils.checkString(searchValue).length() > 0) {
            query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
        }
        query.setParameter("userId", userId);
        query.setParameter("status", SourcingStatus.ACTIVE);

        if (start != null && pageLength != null) {
            query.setFirstResult(start);
            query.setMaxResults(pageLength);
        }

        return query.getResultList();
    }

    //4105_uc_1.1
    public long getTotalSourcingRequestCountBizUnit(User user, String id, Date startDate, Date endDate,List<String> businessUnitIds) {
        LOG.info(" NEW IS THIS BEING USED ?????????????");
        StringBuffer hsql = new StringBuffer("select count(*) from SourcingFormRequest s where s.id in (select distinct s.id from SourcingFormRequest s ");
        hsql.append(" left outer join  s.formOwner  left outer join  s.businessUnit left outer join  s.costCenter left outer join s.createdBy as user ");
        hsql.append(" left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ");
        hsql.append(" left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ");
        hsql.append(" where s.tenantId =:tenantId  ");

        // search with Date range
        if (startDate != null && endDate != null) {
            hsql.append(" and  s.createdDate between :startDate and :endDate ");
        }
        if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
            LOG.info("businessUnitIds NOT EMPTYY");
            hsql.append(" AND s.businessUnit IN (:businessUnitIds) ") ;
        } else {
            LOG.info("businessUnitIds EMPTYY");
        }

        if (StringUtils.checkString(id).length() > 0) {
            hsql.append(" and (s.status not in (:status2)) ");
        } else {
            hsql.append(" and s.status in (:status) or ( s.status in (:status1)) or (  s.status not in (:status2))  ");
        }
        hsql.append(" )");
        LOG.info(" >>>>>>>>>>>> hsql >>>>>>>>: " +hsql);
        final Query query = getEntityManager().createQuery(hsql.toString());

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        //query.setParameter("userId", user.getId());
        query.setParameter("tenantId", user.getTenantId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        try {
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public long getFilteredCountOfSourcingRequestListBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds) {
        LOG.info(" NEW filtered sourcing list loaded id>>>>>>>>>>>>>>  " + id);
        final Query query = countQueryForSourcingRequestListBizUnitAssigned(user, id, input, startDate, endDate, true,businessUnitIds);
        try {
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            return 0;
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<SourcingFormRequestPojo> getAllSourcingRequestListForAssignedBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate, int pageSize, int pageNo, List<String> businessUnitIds) {
        LOG.info("loaded this NEW function>>>>>>>>>>>>>>>>>>>>>>>> startDate: " + startDate);
        Query query = countQueryForSourcingRequestListBizUnitAssigned(user, id, input, startDate, endDate, false, businessUnitIds);
        if (pageSize != 0) {
            query.setFirstResult((pageSize * pageNo));
            query.setMaxResults(pageSize);
        } else {
            query.setFirstResult(input.getStart());
            query.setMaxResults(input.getLength());
        }
        return query.getResultList();
    }

    private Query countQueryForSourcingRequestListBizUnitAssigned(User user, String id, TableDataInput tableParams, Date startDate, Date endDate, boolean isCount, List<String> businessUnitIds) {
        LOG.info("businessUnitIds >>>>>> latest query for uc_1_1 "+businessUnitIds);
        try {
            String hql = "";

            if (isCount) {
                hql += "select count(*) from SourcingFormRequest s where s.id in (select distinct s.id ";
            } else {
                hql += "select distinct NEW com.privasia.procurehere.core.pojo.SourcingFormRequestPojo(s.id, s.sourcingFormName, s.referanceNumber, s.formId, s.description, s.createdDate, user.name ,owner.name, bu.unitName, cc.costCenter, s.status, bc.currencyCode, temp.formName, s.budgetAmount, s.historicaAmount, s.submittedDate, s.approvedDate, s.approvalDaysHours, s.approvalTotalLevels, s.approvalTotalUsers, gc.groupCode) ";
            }
            hql += "from SourcingFormRequest s ";
            hql += "left outer join s.formOwner as owner left outer join s.businessUnit as bu left outer join s.costCenter as cc left outer join s.createdBy as user ";
            hql += "left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ";
            hql += "left outer join s.currency bc left outer join s.sourcingForm temp ";
            hql += "left outer join s.groupCode as gc ";
            hql += "left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ";
            hql += "where s.tenantId =:tenantId ";
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                LOG.info("businessUnitIds NOT EMPTYY");
                hql += " AND bu.id IN (:businessUnitIds) ";
            } else {
                LOG.info("businessUnitIds EMPTYY");
            }

            // search with Date range
            if (startDate != null && endDate != null) {
                hql += "and s.createdDate between :startDate and :endDate ";
            }

            if (StringUtils.checkString(id).length() > 0) {
                hql += " and ( (s.status not in (:status2))) ";
            } else {
                hql += " and (s.status in (:status) or ( s.status in (:status1)) or ( s.status not in (:status2))) ";
            }

            if (tableParams != null) {
                for (ColumnParameter cp : tableParams.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
                        LOG.info("-----------------cp.getSearch().getValue() :::- " + cp.getSearch().getValue() + " --------- " + cp.getData());
                        if (cp.getData().equals("status")) {
                            if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                                hql += " and s.status =:statuses ";
                            } else {
                                hql += " and s.status in (:statuses) ";
                            }
                        } else {
                            if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                                if (cp.getData().equals("sourcingFormName")) {
                                    hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("sourcingrequestid")) {
                                    hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("referencenumber")) {
                                    hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("createdBy")) {
                                    hql += " and upper(user.name) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("requestowner")) {
                                    hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("baseCurrency")) {
                                    hql += " and upper(bc.currencyCode) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("businessunit")) {
                                    hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("formOwner")) {
                                    hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                                } else if (cp.getData().equals("eventIds")) {
                                    hql += " and s.id in (:ids) ";
                                } else if (cp.getData().equals("costcenter")) {
                                    hql += " and upper(cc.costCenter) like (:" + cp.getData().replace(".", "") + ") ";
                                } else {
                                    hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                                }
                            }
                        }
                    }
                }
            }

            if (!isCount) {
                List<OrderParameter> orderList = tableParams.getOrder();
                if (CollectionUtil.isNotEmpty(orderList)) {
                    hql += " order by ";
                    for (OrderParameter op : orderList) {
                        String orderColumn = tableParams.getColumns().get(op.getColumn()).getData();
                        String dir = op.getDir();
                        if (orderColumn.equalsIgnoreCase("sourcingFormName")) {
                            hql += " s.sourcingFormName " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("formId")) {
                            hql += " s.formId " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("referanceNumber")) {
                            hql += " s.referanceNumber " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("createdBy")) {
                            hql += " user.name " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("createdDate")) {
                            hql += " s.createdDate " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("formOwner")) {
                            hql += " owner.name " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("businessunit")) {
                            hql += " bu.unitName " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("costcenter")) {
                            hql += " cc.costCenter " + dir + ",";
                        } else if (orderColumn.equalsIgnoreCase("status")) {
                            hql += " s.status " + dir + ",";
                        } else {
                            hql += " e." + orderColumn + " " + dir + ",";
                        }
                    }

                    if (hql.lastIndexOf(",") == hql.length() - 1) {
                        hql = hql.substring(0, hql.length() - 1);
                    }
                } else {
                    if (!isCount) {
                        hql += " order by s.createdDate desc ";
                    }
                }
            }
            if (isCount) {
                hql += " )";
            }
            LOG.info("HQL >>>>>>>>: " + hql);
            final Query query = getEntityManager().createQuery(hql.toString());

            // Apply search filter values
            if (tableParams != null) {
                for (ColumnParameter cp : tableParams.getColumns()) {
                    if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

                        if (cp.getData().equals("status")) {
                            if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                                query.setParameter("statuses", SourcingFormStatus.valueOf(cp.getSearch().getValue()));
                            } else {
                                List<SourcingFormStatus> statusList = Arrays.asList(SourcingFormStatus.APPROVED, SourcingFormStatus.DRAFT, SourcingFormStatus.PENDING, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED);
                                query.setParameter("statuses", statusList);
                            }
                        } else if (cp.getData().equals("eventIds")) {
                            query.setParameter("ids", Arrays.asList(cp.getSearch().getValue().split(",")));
                        } else {
                            if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                                query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                            }
                        }
                    }
                }
            }
            LOG.info(">>>>>>>>>>tenantId " + user.getTenantId());
            LOG.info(">>>>>>>>>>userId " + user.getId());
            query.setParameter("tenantId", user.getTenantId());
            //query.setParameter("userId", user.getId());

            if (StringUtils.checkString(id).length() == 0) {
                LOG.info("what status??");
                query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
                query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
                query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
            } else {
                LOG.info("what ELSE status??");
                query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
            }
            if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
                query.setParameter("businessUnitIds", businessUnitIds);
            }
            else{
                //query.setParameter("userId", user.getId());
            }
            // set parameter Date range
            if (startDate != null && endDate != null) {
                query.setParameter("startDate", startDate);
                query.setParameter("endDate", endDate);
            }

            return query;
        } catch (Exception e) {
            LOG.error("Error while creating query for sourcing request list with assigned business units: ", e);
            // Optionally, throw a custom exception or return null
            throw new RuntimeException("Failed to create the query", e); // Custom handling as needed
        }

    }

    public List<SourcingFormRequestPojo> getAllSourcingRequestBizUnitForCsv(User user, String id, TableDataInput tableParams, Date startDate, Date endDate, int page_size, int pageNo, List<String> businessUnitIds) {
        String hql = "";

        hql += "select distinct NEW com.privasia.procurehere.core.pojo.SourcingFormRequestPojo(s.id, s.createdDate, s.sourcingFormName, s.referanceNumber, s.formId, s.description, user.name, s.status, owner.name, pm.procurementMethod, pc.procurementCategories, bu.unitName, cc.costCenter, bc.currencyCode, temp.formName, s.budgetAmount, s.estimatedBudget, s.historicaAmount, s.submittedDate, s.approvedDate, s.approvalDaysHours, s.approvalTotalLevels, s.approvalTotalUsers, gc.groupCode || '-' || gc.description, s.groupCodeOld) ";
        hql += "from SourcingFormRequest s ";
        hql += "left outer join s.formOwner as owner left outer join s.businessUnit as bu left outer join s.costCenter as cc left outer join s.createdBy as user ";
        hql += "left outer join s.sourcingFormTeamMember tm left outer join tm.user tu ";
        hql += "left outer join s.currency bc left outer join s.sourcingForm temp ";
        hql += "left outer join s.groupCode as gc ";
        hql += "left outer join s.sourcingFormApprovalRequests sfa left outer join sfa.approvalUsersRequest au ";
        hql += "left outer join s.procurementMethod pm left outer join s.procurementCategories pc ";
        hql += "where s.tenantId =:tenantId ";

        // search with Date range
        if (startDate != null && endDate != null) {
            hql += "and s.createdDate between :startDate and :endDate ";
        }

        if (StringUtils.checkString(id).length() > 0) {
            hql += " and (( user.id =:userId or tu.id =:userId ) or (au.user.id=:userId  and s.status not in (:status2))) ";
        } else {
            hql += " and (s.status in (:status) or ( user.id =:userId and s.status in (:status1)) or ( au.user.id=:userId  and s.status not in (:status2))) ";
        }

        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
                    LOG.info("-----------------cp.getSearch().getValue() :::- " + cp.getSearch().getValue() + " --------- " + cp.getData());
                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            hql += " and s.status =:statuses ";
                        } else {
                            hql += " and s.status in (:statuses) ";
                        }
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            if (cp.getData().equals("sourcingFormName")) {
                                hql += " and upper(s.sourcingFormName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("sourcingrequestid")) {
                                hql += " and upper(s.formId) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("referencenumber")) {
                                hql += " and upper(s.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("createdBy")) {
                                hql += " and upper(user.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("requestowner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("baseCurrency")) {
                                hql += " and upper(bc.currencyCode) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("businessunit")) {
                                hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("formOwner")) {
                                hql += " and upper(owner.name) like (:" + cp.getData().replace(".", "") + ") ";
                            } else if (cp.getData().equals("eventIds")) {
                                hql += " and s.id in (:ids) ";
                            } else if (cp.getData().equals("costcenter")) {
                                hql += " and upper(cc.costCenter) like (:" + cp.getData().replace(".", "") + ") ";
                            } else {
                                hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
                            }
                        }
                    }
                }
            }
        }

        LOG.info("HQL >>>>>>>>: " + hql);

        final Query query = getEntityManager().createQuery(hql.toString());

        // Apply search filter values
        if (tableParams != null) {
            for (ColumnParameter cp : tableParams.getColumns()) {
                if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

                    if (cp.getData().equals("status")) {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter("statuses", SourcingFormStatus.valueOf(cp.getSearch().getValue()));
                        } else {
                            List<SourcingFormStatus> statusList = Arrays.asList(SourcingFormStatus.APPROVED, SourcingFormStatus.DRAFT, SourcingFormStatus.PENDING, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED);
                            query.setParameter("statuses", statusList);
                        }
                    } else if (cp.getData().equals("eventIds")) {
                        query.setParameter("ids", Arrays.asList(cp.getSearch().getValue().split(",")));
                    } else {
                        if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
                            query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
                        }
                    }
                }
            }
        }

        query.setParameter("tenantId", user.getTenantId());
        query.setParameter("userId", user.getId());

        if (StringUtils.checkString(id).length() == 0) {
            query.setParameter("status", Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED));
            query.setParameter("status1", Arrays.asList(SourcingFormStatus.DRAFT));
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        } else {
            query.setParameter("status2", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED));
        }

        // set parameter Date range
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        query.setFirstResult((page_size * pageNo));
        query.setMaxResults(page_size);
        return query.getResultList();
    }

    @Override
    public void transferOwnership(String sourceUserId, String targetUserId) {

        User sourceUser = getEntityManager().find(User.class, sourceUserId);
        User targetUser = getEntityManager().find(User.class, targetUserId);

        Query query = getEntityManager().createQuery(
                "UPDATE SourcingFormRequest SET createdBy.id = :targetUserId WHERE createdBy.id = :sourceUserId"
        );
        query.setParameter("sourceUserId", sourceUserId);
        query.setParameter("targetUserId", targetUserId);
        int recordsUpdated = query.executeUpdate();
        LOG.info("Creators transferred: {}", recordsUpdated);

        Query query2 = getEntityManager().createQuery(
                "UPDATE SourcingFormRequest SET formOwner.id = :targetUserId WHERE formOwner.id  = :sourceUserId"
        );
        query2.setParameter("sourceUserId", sourceUserId);
        query2.setParameter("targetUserId", targetUserId);
        recordsUpdated = query2.executeUpdate();
        LOG.info("Form Owners transferred: {}", recordsUpdated);

        //transfer the user to team members too
        Query query3 = getEntityManager().createQuery(
                "UPDATE SourcingFormTeamMember team " +
                        "SET team.user = :targetUser " +
                        "WHERE team.user = :sourceUser "
        );
        query3.setParameter("sourceUser", sourceUser);
        query3.setParameter("targetUser", targetUser);
        recordsUpdated = query3.executeUpdate();
        LOG.info("Team members transferred: {}", recordsUpdated);

        //transfer ownership of approvals only for RFS with status of DRAFT and PENDING
        Query query4 = getEntityManager().createQuery(
                "UPDATE SourcingFormApprovalUserRequest sfaur " +
                        "SET sfaur.user = :targetUser " +
                        "WHERE sfaur.user = :sourceUser "
        );

        query4.setParameter("sourceUser", sourceUser);
        query4.setParameter("targetUser", targetUser);
        recordsUpdated = query4.executeUpdate();
        LOG.info("Approval user transferred: {}", recordsUpdated);


    }

}
