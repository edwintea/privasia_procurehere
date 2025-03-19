<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div class="panel bgnone">
	<div class="panel-body">
		<div class="example-box-wrapper">
			<div class="panel-group" id="accordion">
				<div class="panel sum-accord marg-top-20">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a data-toggle="collapse" class="accordion" href="#collapseHistory"> <spring:message code="eventawards.history" /> </a>
						</h4>
					</div>
					<div id="collapseHistory" class="row padding-left-20 panel-collapse collapse in accortab">
						<c:if test="${empty awardAuditList}">
							<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
								<tr>
									<th>No history available</th>
								</tr>
							</table>
						</c:if>
						<c:if test="${not empty awardAuditList}">
							<div class="table-responsive width100 borderAllData" style="margin-bottom: 0px; padding: 0px 15px;">
								<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
									<tr>
										<th class="align-left width_200_fix"><spring:message code="application.action.date" /></th>
										<th class="align-left width_200 width_200_fix"><spring:message code="application.action.by" /></th>
										<th class="align-left width_200 "><spring:message code="application.description"/></th>
										<th class="align-left width_100 text-nowrap"><spring:message code="application.snapshot" /></th>
									</tr>
									<tbody>
										<c:forEach items="${awardAuditList}" var="awardAudit">
											<tr>
												<td class="align-left width_200_fix"><fmt:formatDate value="${awardAudit.actionDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
												<td class="align-left width_200 width_200_fix">
													<c:if test="${awardAudit.actionBy != null && !empty awardAudit.actionBy.name }"> 
														${awardAudit.actionBy.name} 
													</c:if>
												</td>
												<td class="align-left width_200">${awardAudit.description}</td>
												<td class="align-left width_100 text-nowrap" style="padding: 0px;">
													<c:if test="${awardAudit.actionBy != null && !empty awardAudit.actionBy.name }">
														<c:if test="${awardAudit.hasSnapshot}">
	 														<c:url var="download" value="/buyer/${eventType}/downloadAwardSnapShot/${awardAudit.id}" />
															<a class="downloadSnapshot" href="${download}"> 
																<img src="${pageContext.request.contextPath}/resources/images/pdf_icon.png" title="PDF Snapshot" style="height: 35px;" alt="download">
															</a>
														</c:if>
														<c:if test="${awardAudit.hasExcelSnapshot}">
															<c:url var="download" value="/buyer/${eventType}/downloadAwardExcelSnapShot/${awardAudit.id}" />
															<a class="downloadSnapshot" href="${download}"> 
																<img src="${pageContext.request.contextPath}/resources/images/excel-Icon.png" title="Excel Snapshot" style="height: 23px;" alt="download">
															</a>
														</c:if>
														 <c:if test="${awardAudit.fileName != null &&  !empty awardAudit.fileName }">
															<c:url var="download" value="/buyer/${eventType}/downloadAwardAttachFile/${awardAudit.id}" />
																<a class="downloadSnapshot marg-left-10" href="${download}">
																	<img src="${pageContext.request.contextPath}/resources/images/download.png"  style="height: 23px;" alt="download" title="${awardAudit.fileName}">
																</a>
														</c:if>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>