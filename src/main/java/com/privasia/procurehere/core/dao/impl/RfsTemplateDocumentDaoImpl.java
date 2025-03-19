package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfsTemplateDocumentDao;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Repository
public class RfsTemplateDocumentDaoImpl extends GenericDaoImpl<RfsTemplateDocument, String> implements RfsTemplateDocumentDao {

    @Override
    public String findUploadFileName(String docId) {
        StringBuilder builder = new StringBuilder("select rfs.fileName from RfsTemplateDocument rfs where rfs.id =:id ");
        final Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("id", docId);
        return (String) query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RfsTemplateDocument> findAllTemplateDocsBytemplateId(String templateId) {
        final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfsTemplateDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, a.fileSizeInKb, sp, up.name) from RfsTemplateDocument a left join a.sourcingFormTemplate sp left join a.uploadBy up where sp.id =:id order by a.uploadDate desc ");
        query.setParameter("id", templateId);
        return query.getResultList();
    }

    @Override
    public void deleteById(String id) {
        final Query query = getEntityManager().createQuery("delete from RfsTemplateDocument a where a.id =:id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public RfsTemplateDocument findDocsById(String id) {
        String cleanDocId = id.trim().replaceAll("[^a-zA-Z0-9]", "");
        RfsTemplateDocument doc = null;
        try {
            doc = (RfsTemplateDocument) getEntityManager().createQuery("FROM RfsTemplateDocument d WHERE d.id = :id order by d.uploadDate desc ")
                    .setParameter("id", cleanDocId)
                    .getSingleResult();
        } catch (NoResultException e) {
            doc = null;
        }
        return doc;
    }
}
