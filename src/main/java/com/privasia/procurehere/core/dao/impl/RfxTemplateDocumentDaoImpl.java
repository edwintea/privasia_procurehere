package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfxTemplateDocumentDao;
import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Repository
public class RfxTemplateDocumentDaoImpl extends GenericDaoImpl<RfxTemplateDocument, String> implements RfxTemplateDocumentDao {

    @Override
    public List<RfxTemplateDocument> findAllTemplateDocsBytemplateId(String templateId) {
        final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfxTemplateDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, a.fileSizeInKb, rt, a.internal, up.name) from RfxTemplateDocument a left join a.rfxTemplate rt left join a.uploadBy up where rt.id =:id order by a.uploadDate desc ");
        query.setParameter("id", templateId);
        return query.getResultList();
    }

    @Override
    public void deleteById(String id) {
        final Query query = getEntityManager().createQuery("delete from RfxTemplateDocument a where a.id =:id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public RfxTemplateDocument findDocsById(String id) {
        String cleanDocId = id.trim().replaceAll("[^a-zA-Z0-9]", "");
        RfxTemplateDocument doc = null;
        try {
            doc = (RfxTemplateDocument) getEntityManager().createQuery("FROM RfxTemplateDocument d WHERE d.id = :id order by d.uploadDate desc ")
                    .setParameter("id", cleanDocId)
                    .getSingleResult();
        } catch (NoResultException e) {
            doc = null;
        }
        return doc;
    }
    }

