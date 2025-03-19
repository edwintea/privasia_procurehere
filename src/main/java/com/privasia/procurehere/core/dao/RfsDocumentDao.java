package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;

/**
 * @author sudesha
 */
public interface RfsDocumentDao extends GenericDao<RfsDocument, String> {

	List<RfsDocument> findAllPrDocsbyRfsId(String rfsId);

	List<RfsDocument> findAllPlainRfsDocsbyRfsId(String rfsId);

	List<RfsDocument> findAllRfsDocsNameAndId(String rfsId);

	List<RfsDocumentPojo> findAllPlainRfsDocsbyRfsIdAndUploadBy(String rfsId);

	String findUploadFileName(String docId);
	List<RfsDocument> findAllPlainRfsDocsbyRfsIdWithInternal(String formId);

}
