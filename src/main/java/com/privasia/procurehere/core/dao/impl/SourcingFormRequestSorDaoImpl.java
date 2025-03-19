package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.SourcingFormRequestSorDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Repository
public class SourcingFormRequestSorDaoImpl extends GenericDaoImpl<SourcingFormRequestSor, String> implements SourcingFormRequestSorDao {

    @Override
    public List<SourcingFormRequestSor> findSorByFormIdByOrder(String formId) {
        final Query query = getEntityManager().createQuery("select distinct b from SourcingFormRequestSor b inner join fetch b.sourcingFormRequest s left outer join fetch b.sorItems it where s.id=:formId order by b.sorOrder, b.createdDate");
        query.setParameter("formId", formId);
        return query.getResultList();
    }


    @Override
    public boolean isSorExists(String formId, String bqId, String name) {
        String hql = "select count(*) from SourcingFormRequestSor b where b.sourcingFormRequest.id= :formId and upper (b.name)= :name ";
        if (StringUtils.checkString(bqId).length() > 0) {
            hql += " and b.id <> :id";
        }
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("name", StringUtils.checkString(name.toUpperCase()));
        query.setParameter("formId", formId);
        if (StringUtils.checkString(bqId).length() > 0) {
            query.setParameter("id", bqId);
        }
        return ((Number) query.getSingleResult()).intValue() > 0;
    }


    @Override
    public void deleteSor(String bqId) {
        Query query = getEntityManager().createQuery("from SourcingFormRequestSor bq where bq.id=:bqId");
        query.setParameter("bqId", bqId);
        List<SourcingFormRequestSor> result = query.getResultList();
        for (SourcingFormRequestSor bq : result) {
            getEntityManager().remove(bq);
        }
    }

    @Override
    public List<SourcingFormRequestSor> findSorsByFormId(String formId) {
        final Query query = getEntityManager().createQuery("select bq from SourcingFormRequestSor bq inner join fetch bq.sourcingFormRequest sf where sf.id=:formId order by bq.sorOrder");
        query.setParameter("formId", formId);
        return query.getResultList();
    }

    @Override
    public void deletefieldInSor(String bqId, String label) {
        try {
            StringBuilder hql = new StringBuilder("update SourcingFormRequestSor b set b." + label + "Label = null, b." + label + "FilledBy = null, b." + label + "ToShowSupplier = false, b." + label + "Required = false  where b.id = :bqId");
            Query query = getEntityManager().createQuery(hql.toString());
            query.setParameter("bqId", bqId);
            query.executeUpdate();
        } catch (NoResultException nr) {
            nr.printStackTrace();
        }
    }
}
