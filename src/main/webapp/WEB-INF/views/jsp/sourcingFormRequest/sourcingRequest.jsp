<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord historyDocu">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseAudit"> <spring:message code="praudit.history" /> </a>
		</h4>
	</div>
	<div id="collapseAudit" class="panel-collapse collapse in pad_all_15">
		<div class="panel-body">
			<div class="table-responsive width100 borderAllData">
				<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 0px;">
					<thead>
						<tr>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.action.date" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.action.by" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.action1"/></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="Product.remarks"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${aequestAudit}" var="audit">
							<tr>
								<td class="width_200 width_200_fix align-left wo-rp"><fmt:formatDate value="${audit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" /></td>
								<td class="width_200 width_200_fix align-left wo-rp"><span style="display: block">${audit.actionBy.name}</span></td>
								<td class="align-left width_200_fix">${audit.action}</td>
								<td class="align-left width_200 white-space-pre" style="color: ${audit.action == 'CANCELLED' or audit.action == 'ERROR' ? '#e74c3c' : ''}; white-space: normal;">${audit.description}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<style>
.historyDocu {
	margin-top: -1%;
}

</style>