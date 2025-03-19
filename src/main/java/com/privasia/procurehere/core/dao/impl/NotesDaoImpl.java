package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.NotesDao;
import com.privasia.procurehere.core.entity.Notes;

/**
 * @author Javed Ahmed
 */
@Repository
public class NotesDaoImpl extends GenericDaoImpl<Notes, String> implements NotesDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Notes> findAll() {
		StringBuilder hsql = new StringBuilder("from Notes  as n inner join fetch n.supplier as s");
		final Query query = getEntityManager().createQuery(hsql.toString());

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notes> notesForSupplier(String id, String loggedInTenantId) {

		StringBuilder hsql = new StringBuilder("from Notes a inner join fetch a.supplier sp  inner join fetch a.createdBy c where sp.id =:id and c.tenantId =:loggedInUserId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.setParameter("loggedInUserId", loggedInTenantId);
		return query.getResultList();
	}

}
