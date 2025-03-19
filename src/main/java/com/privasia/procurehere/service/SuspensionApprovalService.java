/**
 * 
 */
package com.privasia.procurehere.service;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
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
public interface SuspensionApprovalService {

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfpEvent doApproval(RfpEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, Model model) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfaEvent doApproval(RfaEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, Model model) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfiEvent doApproval(RfiEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, Model model) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfqEvent doApproval(RfqEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, Model model) throws Exception;

	/**
	 * @param event
	 * @param session
	 * @param user
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RftEvent doApproval(RftEvent event, HttpSession session, User user, JRSwapFileVirtualizer virtualizer, Model model) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 * @throws ApplicationException 
	 */
	RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException, ApplicationException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 */
	RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 */
	RfiEvent doApproval(RfiEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 */
	RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 */
	RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Model model) throws NotAllowedException;

	/**
	 * @param eventId
	 * @param rfxTypes
	 * @return
	 */
	String findBusinessUnit(String eventId, RfxTypes rfxTypes);

}
