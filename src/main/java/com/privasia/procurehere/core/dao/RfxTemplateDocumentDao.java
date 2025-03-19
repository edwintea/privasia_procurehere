package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;

import java.util.List;

public interface RfxTemplateDocumentDao extends GenericDao<RfxTemplateDocument, String> {
    List<RfxTemplateDocument> findAllTemplateDocsBytemplateId(String templateId);

    void deleteById(String id);

    RfxTemplateDocument findDocsById(String id);
}
