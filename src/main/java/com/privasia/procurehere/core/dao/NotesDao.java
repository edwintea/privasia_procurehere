package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Notes;

/**
 * @author Javed Ahmed
 *
 */
public interface NotesDao extends GenericDao<Notes, String> {


	public List<Notes> findAll();
	/**
	 * @param id
	 * @return
	 */
	public List<Notes> notesForSupplier(String id, String loggedInTenantId);
	
}
