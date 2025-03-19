package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.RfsTemplateDocument;

import java.util.List;

public interface RfsTemplateDocumentDao extends GenericDao<RfsTemplateDocument, String>{
    String findUploadFileName(String docId);

    List<RfsTemplateDocument> findAllTemplateDocsBytemplateId(String templateId);

    void deleteById(String id);

    RfsTemplateDocument findDocsById(String id);
}
