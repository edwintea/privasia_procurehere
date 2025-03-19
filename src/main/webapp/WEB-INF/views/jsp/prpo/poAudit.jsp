<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord marg-top-10 ">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseAudit"> <spring:message code="poSummary.audit.label" /> </a>
		</h4>
	</div>
	<div id="collapseAudit" class="panel-collapse collapse in">
		<div class="panel-body">
			<div class="table-responsive width100 borderAllData">
				<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom:0px;">
					<thead>
						<tr>
							<th class="align-left width_200_fix"><spring:message code="application.action.date" /></th>
							<th class="align-left width_200 width_200_fix"><spring:message code="application.action.by" /></th>
							<th class="align-left width_200_fix"><spring:message code="application.action1" /></th>
							<th class="align-left width_200"><spring:message code="Product.remarks" /></th>
							<th class="align-left width_200">Snapshot</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${poAuditList}" var="audit">
							<tr>
								<td class="align-left width_200_fix"><fmt:formatDate value="${audit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" /></td>
								<td class="align-left width_200 width_200_fix"><span style="display: block">${audit.actionBy.name}</span></td>
								<td class="align-left width_200_fix">${audit.action}</td>
								<td class="align-left width_200 white-space-pre" style="white-space: normal;">${audit.description}</td>
								<td class="align-left width_100_fix">
									<c:if test="${not empty audit.snapshot}">
										<fmt:formatDate value="${audit.actionDate}" pattern="ddMMyyyyhhmma" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="actionDate" />
										<form:form method="GET">
											<c:url var="download" value="/buyer/downloadPoAuditFile/${audit.id}/${audit.action}/${actionDate}" />
											<a class="downloadSnapshot" href="${download}"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download"></a>
										</form:form>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>