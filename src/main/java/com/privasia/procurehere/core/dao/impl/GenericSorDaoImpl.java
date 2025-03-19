package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.GenericSorDao;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericSorDaoImpl<T extends Sor, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericSorDao<T, PK> {

    private static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

    @Autowired
    MessageSource messageSource;

    @Override
    public boolean isExists(final T sor, String eventId) {
        String hql = "select count(*) from " + sor.getClass().getName() + " b where b.rfxEvent.id = :eventId and upper(b.name) = :name";
        if (StringUtils.checkString(sor.getId()).length() > 0) {
            hql += " and b.id <> :id";
        }
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("name", StringUtils.checkString(sor.getName()).toUpperCase());
        query.setParameter("eventId", eventId);
        if (StringUtils.checkString(sor.getId()).length() > 0) {
            query.setParameter("id", sor.getId());
        }
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    @Override
    public List<T> findSorsByEventIdByOrder(String eventId) {
        final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp where sp.id = :id order by a.sorOrder");
        query.setParameter("id", eventId);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSorForEventId(String id) {
        Query query = getEntityManager().createQuery("delete from " + entityClass.getName() + " bq where bq.rfxEvent.id = :id ");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void deleteFromEnvSor(String eventId, RfxTypes rfxTypes) {
        // Remove from jointable
        Query query = null;
        String sql = null;

        switch (rfxTypes) {
            case RFQ:
                sql = "DELETE FROM PROC_RFQ_ENV_SOR USING PROC_RFQ_ENVELOP " +
                        "WHERE PROC_RFQ_ENV_SOR.event_id = PROC_RFQ_ENVELOP.id " +
                        "AND PROC_RFQ_ENVELOP.event_id = :id";
                break;
            case RFI:
                sql = "DELETE FROM PROC_RFI_ENV_SOR USING PROC_RFI_ENVELOP " +
                        "WHERE PROC_RFI_ENV_SOR.event_id = PROC_RFI_ENVELOP.id " +
                        "AND PROC_RFI_ENVELOP.event_id = :id";
                break;
            case RFA:
                sql = "DELETE FROM PROC_RFA_ENV_SOR USING PROC_RFA_ENVELOP " +
                        "WHERE PROC_RFA_ENV_SOR.event_id = PROC_RFA_ENVELOP.id " +
                        "AND PROC_RFA_ENVELOP.event_id = :id";
                break;
            case RFP:
                sql = "DELETE FROM PROC_RFP_ENV_SOR USING PROC_RFP_ENVELOP " +
                        "WHERE PROC_RFP_ENV_SOR.event_id = PROC_RFP_ENVELOP.id " +
                        "AND PROC_RFP_ENVELOP.event_id = :id";
                break;
            case RFT:
                sql = "DELETE FROM PROC_RFT_ENV_SOR USING PROC_RFT_ENVELOP " +
                        "WHERE PROC_RFT_ENV_SOR.event_id = PROC_RFT_ENVELOP.id " +
                        "AND PROC_RFT_ENVELOP.event_id = :id";
                break;
            default:
                throw new IllegalArgumentException("Unsupported RfxType: " + rfxTypes);
        }

        if (sql != null) {
            query = getEntityManager().createNativeQuery(sql);
            query.setParameter("id", eventId);
            query.executeUpdate();
        }
    }



    @Override
    public List<T> findSorsByEventIdForEnvelop(String eventId, List<String> bqIds) {
        StringBuilder hql = new StringBuilder("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp where sp.id = :id ");
        if (CollectionUtil.isNotEmpty(bqIds)) {
            hql.append(" and a.id not in (:bqIds)");
        }

        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("id", eventId);
        if (CollectionUtil.isNotEmpty(bqIds)) {
            query.setParameter("bqIds", bqIds);
        }
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAllSorsByIds(String[] ids) {
        final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.id in (:id)");
        query.setParameter("id", Arrays.asList(ids));
        return query.getResultList();
    }

    @Override
    public RfqEventSor getRfqEventSorBySorId(String id) {
        final Query query = getEntityManager().createQuery("from RfqEventSor r where r.id= :id");
        query.setParameter("id", id);
        @SuppressWarnings("unchecked")
        RfqEventSor bq = (RfqEventSor) query.getSingleResult();
        return bq;
    }

    @Override
    public RfpEventSor getRfpEventSorBySorId(String id) {
        final Query query = getEntityManager().createQuery("from RfpEventSor r where r.id= :id");
        query.setParameter("id", id);
        @SuppressWarnings("unchecked")
        RfpEventSor bq = (RfpEventSor) query.getSingleResult();
        return bq;
    }

    @Override
    public RfaEventSor getRfaEventSorBySorId(String id) {
        final Query query = getEntityManager().createQuery("from RfaEventSor r where r.id= :id");
        query.setParameter("id", id);
        @SuppressWarnings("unchecked")
        RfaEventSor bq = (RfaEventSor) query.getSingleResult();
        return bq;
    }

    @Override
    public RfiEventSor getRfiEventSorBySorId(String id) {
        final Query query = getEntityManager().createQuery("from RfiEventSor r where r.id= :id");
        query.setParameter("id", id);
        @SuppressWarnings("unchecked")
        RfiEventSor bq = (RfiEventSor) query.getSingleResult();
        return bq;
    }

    @Override
    public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
        Query query = null;
        switch (eventType) {
            case RFA:
                query = getEntityManager().createQuery("select count(distinct bq) from RfaEnvelop  ev left outer join ev.sorList as bq where bq.id =:id");
                break;
            case RFI:
                query = getEntityManager().createQuery("select count(distinct bq) from RfiEnvelop  ev left outer join ev.sorList as bq where bq.id =:id");
                break;
            case RFP:
                query = getEntityManager().createQuery("select count(distinct bq) from RfpEnvelop  ev left outer join ev.sorList as bq where bq.id =:id");
                break;
            case RFQ:
                query = getEntityManager().createQuery("select count(distinct bq) from RfqEnvelop  ev left outer join ev.sorList as bq where bq.id =:id");
                break;
            case RFT:
                query = getEntityManager().createQuery("select count(distinct bq) from RftEnvelop  ev left outer join ev.sorList as bq where bq.id =:id");
                break;
        }
        query.setParameter("id", id);
        if (((Number) query.getSingleResult()).longValue() > 0) {
            LOG.error(messageSource.getMessage("rfx.sor.info.envelope", new Object[] {}, Global.LOCALE));
            throw new NotAllowedException(messageSource.getMessage("rfx.sor.info.envelope", new Object[] {}, Global.LOCALE));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSor(String id) {
        Query query = getEntityManager().createQuery("from " + entityClass.getName() + " sor where sor.id = :id");
        query.setParameter("id", id);
        List<T> result = query.getResultList();
        for (T t : result) {
            getEntityManager().remove(t);
        }

    }

    @Override
    public void deletefieldInSor(String bqId, String label) {
        try {
            LOG.info("SOR ID :: " + bqId + " label :: " + label);
            StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " b set b." + label + "Label = null, b." + label + "FilledBy = null, b." + label + "ToShowSupplier = false, b." + label + "Required = false  where b.id = :bqId");
            Query query = getEntityManager().createQuery(hql.toString());
            query.setParameter("bqId", bqId);
            query.executeUpdate();
            LOG.info("hql : " + hql);
        } catch (NoResultException nr) {
            LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
        }
    }
}
