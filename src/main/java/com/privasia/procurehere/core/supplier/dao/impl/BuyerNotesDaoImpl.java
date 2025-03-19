package com.privasia.procurehere.core.supplier.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.supplier.dao.BuyerNotesDao;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Giridhar
 */

@Repository("buyerNotesDao")
public class BuyerNotesDaoImpl extends GenericDaoImpl<Notes, String> implements BuyerNotesDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from Notes a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Notes> findAllNotesById(String id) {
		final Query query = getEntityManager().createQuery("from Notes a inner join fetch a.buyer sp where sp.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}
