<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseTen"> <spring:message code="eventsummary.timeline.title" /> </a>
		</h4>
	</div>
	<div id="collapseTen" class="panel-collapse collapse accortab">
		<div class="panel-body">
			<div class="mega border-none event-timeline-table">
				<table class="tabaccor padding-none-td header bot-set-head ph_table" width="100%" cellspacing="0" cellpadding="0" border="0">
					<thead>
						<tr>
							<th class="align-left width_200_fix"><spring:message code="eventsummary.listdocuments.datetime" /></th>
							<th class="align-left width_300_fix"><spring:message code="application.description"/></th>
							<th class="align-left width_150_fix"><spring:message code="application.type" /></th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table">
					<tbody>
					<c:if test="${empty eventTimeline}">
					<tr>
					<td valign="top" colspan="" class="align-left width_150_fix dataTables_empty"><spring:message code="application.nodata" /></td></tr>
					</c:if>
						<c:if test="${!empty eventTimeline}">
						<c:forEach items="${eventTimeline}" var="evttime">
							<tr>
								<td class="align-left width_200_fix">
									<fmt:formatDate value="${evttime.activityDate}" timeZone="${sessionScope['timeZone']}" pattern=" dd/MM/yyyy hh:mm a" />
								</td>
								<td class="align-left width_300_fix">
									<span style="display: block">${evttime.description}</span>
								</td>
								<td class="align-left width_150_fix">${evttime.activity}</td>
							</tr>
						</c:forEach>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>