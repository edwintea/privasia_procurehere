package com.privasia.procurehere.service.impl;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.entity.ProductContract;


import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface SnapShotAuditService {
	public void doRfqAudit(RfqEvent event, HttpSession session, RfqEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	public void doRfaAudit(RfaEvent event, HttpSession session, RfaEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	public void doRfpAudit(RfpEvent event, HttpSession session, RfpEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	public void doRftAudit(RftEvent event, HttpSession session, RftEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	public void doRfiAudit(RfiEvent event, HttpSession session, RfiEvent persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	void doPoAudit(Po po, User loginUser, PoAuditType type, String message, JRSwapFileVirtualizer virtualizer, PoAuditVisibilityType visibilityType);

	/**
	 * @param form
	 * @param session
	 * @param loginUser
	 * @param type
	 * @param message
	 * @param virtualizer
	 */
	public void doSupplierPerformanceFormAudit(SupplierPerformanceForm form, HttpSession session, User loginUser, SupplierPerformanceAuditActionType type, String message, JRSwapFileVirtualizer virtualizer);

	void doContractAudit(ProductContract event, HttpSession session, ProductContract persistObj, User loginUser, AuditActionType type, String message, JRSwapFileVirtualizer virtualizer);


}
