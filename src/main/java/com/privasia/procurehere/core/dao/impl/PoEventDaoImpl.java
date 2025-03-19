package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.PoEventDao;
import com.privasia.procurehere.core.entity.PoEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * @author parveen
 */
@Repository
public class PoEventDaoImpl extends GenericDaoImpl<PoEvent, String> implements PoEventDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.PO_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEvent> findEventByPoId(String poId) {
		final Query query = getEntityManager().createQuery("SELECT pe FROM com.privasia.procurehere.core.entity.PoEvent pe WHERE pe.po.id = :poId");
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@Override
	public PoEvent findByEventId(String eventId) {
		try {
			LOG.info("Event Id : " + eventId);
			final Query query = getEntityManager().createQuery("from PoEvent pe inner join fetch pe.po where pe.id =:eventId");
			query.setParameter("eventId", eventId);
			List<PoEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

}
