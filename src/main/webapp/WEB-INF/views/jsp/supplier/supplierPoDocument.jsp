<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:if test="${not empty listPoDocs}">	
	<div class="panel sum-accord upload_download_wrapper clearfix marg-top-10 event_info">
		<div class="panel-heading">
			<h4 class="panel-title list-pad-0" style="margin-bottom: 0px;">
				<spring:message code="supplier.po.documents" />
			</h4>
		</div>
	<div class="Invited-Supplier-List-table pad_all_15 add-supplier">
		<div class="ph_table_border">
			<div class="mega">
				<table class="header ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<th class="width_100 width_100_fix align-center">
								<spring:message code="application.action" />
							</th>
							<th class="width_200 width_200_fix align-left">
								<spring:message code="event.document.filename" />
							</th>
							<th class="width_300 width_300_fix align-left">
								<spring:message code="event.document.description" />
							</th>
							<th class="width_100 width_100_fix align-left"><spring:message code="podocument.document.type" /></th>
							<th class="width_150 width_150_fix align-left"><spring:message code="event.document.publishdate" /></th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0" id="poDocList">
					<tbody>
						<c:forEach var="poDoc" items="${listPoDocs}">
							<tr>
								<td class="width_100 width_100_fix align-center">
									<form method="GET">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<c:url var="downloadPoDocument" value="/supplier/downloadPoDocumentForSupplier/${poDoc.id}" />
										<a id="downloadButton" href="${downloadPoDocument}" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.download" />'>
											<img src="${pageContext.request.contextPath}/resources/images/download.png">
										</a>
										&nbsp;
									</form>
								</td>
								<td class="width_200 width_200_fix align-left">${poDoc.fileName}</td>
								<td class="width_300 width_300_fix align-left">${poDoc.description}</td>
								<fmt:formatDate var="uploadDate" value="${poDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								<td class="width_100 width_100_fix align-left">${poDoc.docType}</td>
								<td class="width_150 width_150_fix align-left">${uploadDate}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
</c:if>
<script type="text/javascript" src="<c:url value="/resources/js/view/poDocument.js"/>"></script>
