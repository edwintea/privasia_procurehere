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
			<a data-toggle="collapse" class="accordion" href="#collapseThree"><spring:message code="eventsummary.listdocuments.title" /> </a>
		</h4>
	</div>
	<div id="collapseThree" class="panel-collapse collapse">
		<div class="panel-body">
			<div class="table-responsive width100 borderAllData">
				<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.name" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.datetime" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.size" /></th>
							<th class="width_200 width_200_fix align-left"><spring:message code="event.document.uploadby" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="event.document.internal" /></th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${empty listDocs}">
							<td valign="top" colspan="4" class="dataTables_empty">
								<spring:message code="application.nodata" />
							</td>
						</c:if>
						<c:if test="${!empty listDocs}">
							<c:forEach var="listDoc" items="${listDocs}">
								<tr>
									<td class="width_200 width_200_fix align-left wo-rp">
										<a class="bluelink" href="${pageContext.request.contextPath}/buyer/${eventType}/downloadRftSummaryDocument/${listDoc.id}"> ${listDoc.fileName}</a>
									</td>
									<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
									<td class="width_200 width_200_fix align-left wo-rp">
										<fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</td>
									<td class="width_100 width_100_fix align-left wo-rp">
										<fmt:formatNumber type="number" maxFractionDigits="1" value="${listDoc.fileSizeInKb}" />KB
									</td>
									<td class="width_200 width_200_fix align-left">${listDoc.uploadBy.name}</td>
									<td class="width_100 width_100_fix align-left wo-rp">
										<c:if test="${listDoc.internal == true}">
											<spring:message code="eventDocument.document.internal" />
										</c:if>
										<c:if test="${listDoc.internal == false}">
											<spring:message code="event.document.external" />
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
