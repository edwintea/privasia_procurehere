package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.SorPojo;

/**
 * @author Ravi
 */
public interface GenericEnvelopDao<T extends Envelop, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	List<T> getAllEnvelopByEventId(String eventId, RfxTypes eventType);


	/**
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	List<T> getAllEnvelopSorByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param rftEnvelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(T rftEnvelop, String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<String> getBqsByEnvelopId(List<String> envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<String> getCqsByEnvelopId(List<String> envelopId);

	Integer getAllEnvelopCountByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<BqPojo> getBqNameAndIdsByEnvelopId(List<String> envelopId);

	/**
	 * @param eventId
	 * @return
	 */
	List<T> getAllPlainEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Envelop> getPlainEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<CqPojo> getCqsIdListByEnvelopIdByOrder(List<String> envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<BqPojo> getBqsIdListByEnvelopIdByOrder(List<String> envelopId);


	List<SorPojo> getSorsIdListByEnvelopIdByOrder(List<String> envelopId);
}
