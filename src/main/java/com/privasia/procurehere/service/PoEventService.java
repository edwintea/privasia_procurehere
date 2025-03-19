package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Edwin
 */
public interface PoEventService {


	/**
	 * @return
	 */
	PoEvent save(PoEvent poEvent);


	/**
	 * @param poId
	 */
	List<PoEvent> findEventByPoId(String poId);


	PoEvent getPoEventByeventId(String eventId);
}
