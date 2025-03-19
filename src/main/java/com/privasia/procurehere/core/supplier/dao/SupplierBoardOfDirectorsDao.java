package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;

public interface SupplierBoardOfDirectorsDao extends GenericDao<SupplierBoardOfDirectors, String> {

	List<SupplierBoardOfDirectors> findAllBySupplierId(String id);

	List<SupplierBoardOfDirectors> findIfRecordExistsWithDuplicateIdnumber(String idNumber);

	void deleteById(String id);

	long findTotalBoardOfDirectorBySuppId(String id);

}
