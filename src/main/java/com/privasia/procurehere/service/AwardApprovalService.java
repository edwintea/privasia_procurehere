/**
 * 
 */
package com.privasia.procurehere.service;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;

import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author jayshree
 *
 */
public interface AwardApprovalService {

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @param awardId TODO
	 * @param isConclude TODO
	 * @return
	 * @throws Exception
	 */
	RfpEvent doApproval(RfpEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @param awardId TODO
	 * @param isConclude TODO
	 * @return
	 * @throws Exception
	 */
	RfaEvent doApproval(RfaEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @param awardId TODO
	 * @param isConclude TODO
	 * @return
	 * @throws Exception
	 */
	RfqEvent doApproval(RfqEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @param awardId TODO
	 * @param isConclude TODO
	 * @return
	 * @throws Exception
	 */
	RftEvent doApproval(RftEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, String awardId, Boolean isConclude) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @param bqId TODO
	 * @return
	 * @throws NotAllowedException
	 * @throws ApplicationException 
	 */
	RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException, ApplicationException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @param bqId TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @param bqId TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @param bqId TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model, String bqId) throws NotAllowedException;

	/**
	 * @param eventId
	 * @param rfxTypes
	 * @return
	 */
	String findBusinessUnit(String eventId, RfxTypes rfxTypes);

}
