package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.Notes;

/**
 * @author Giridhar
 */

public interface BuyerNotesDao  extends GenericDao<Notes, String>  {

	
	/**
	 * 
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * 
	 * @param id
	 * @return
	 */
	List<Notes> findAllNotesById(String id);

}
