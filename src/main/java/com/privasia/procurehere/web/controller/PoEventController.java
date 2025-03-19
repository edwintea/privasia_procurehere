package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.service.*;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Parveen
 * @author RT-Kapil
 */

@Controller
@RequestMapping("/buyer/PO")
public class PoEventController extends EventSummaryBase {

	@Autowired
	RfqApprovalEditor rfqApprovalEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	RfqSuspensionApprovalEditor rfqSuspensionApprovalEditor;

	public PoEventController() {
		super(RfxTypes.PO);
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder, HttpSession session) {

		binder.registerCustomEditor(Supplier.class, supplierEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(User.class, userEditor);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		format.setTimeZone(timeZone);
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "feePaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "feePaidDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "depositPaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "depositPaidDate", new CustomDateEditor(format, true));

	}
}
