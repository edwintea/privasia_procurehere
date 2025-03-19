<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord marg-top-5">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseAudit"> <spring:message code="eventsummary.event.audit.title" />
			</a>
			<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEventAudit/${event.id}">
				<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-original-title='<spring:message code="eventsummary.download.eventbtn" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
					</span> <span class="button-content"><spring:message code="download.event.audit.button" /></span>
				</button>
			</form:form>
		</h4>
	</div>
	<div id="collapseAudit" class="panel-collapse collapse">
		<div class="panel-body">
			<div class="table-responsive width100 borderAllData">
				<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr>
							<th class="align-left width_200_fix"><spring:message code="application.action.date" /></th>
							<th class="align-left width_200 width_200_fix"><spring:message code="application.action.by" /></th>
							<th class="align-left width_100_fix"><spring:message code="application.action1" /></th>
							<th class="align-left width_200 width_200_fix"><spring:message code="application.description" /></th>
							<th class="align-left width_100_fix"><spring:message code="application.snapshot" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${eventAudit}" var="evtAudit">
							<tr>
								<td class="align-left width_200_fix"><fmt:formatDate value="${evtAudit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern=" dd/MM/yyyy hh:mm a" /></td>
								<td class="align-left width_200 width_200_fix"><span style="display: block">${evtAudit.actionBy.name}</span></td>
								<td class="align-left width_100_fix">${evtAudit.action.value}</td>
								<td class="align-left width_200 width_200_fix word-break-all">${evtAudit.description}</td>
								<td class="align-left width_100_fix">
									<c:if test="${not empty evtAudit.buyer}">
										<fmt:formatDate value="${evtAudit.actionDate}" pattern="ddMMyyyyhhmma" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="actionDate" />
										<form:form method="GET">
											<c:url var="download" value="/buyer/${eventType}/downloadAuditFile/${evtAudit.id}/${evtAudit.action}/${actionDate}" />
											<a class="downloadSnapshot" href="${download}"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download"></a>
										</form:form>
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<style>
.word-break-all {
	word-break: break-all;
}

.panel.sum-accord .panel-title button {
	right: 50px;
	top: 12px;
}
</style>