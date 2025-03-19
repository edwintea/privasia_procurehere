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
			<a data-toggle="collapse" class="accordion" href="#collapseAudit"> Supplier Status Audit </a>
		</h4>
	</div>
	<div id="collapseAudit" class="panel-collapse collapse">
		<div class="panel-body">
			<div class="table-responsive width100 borderAllData">
				<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr>
							<th class="align-left width_200_fix">Action Date</th>
							<th class="align-left width_200 width_200_fix">Action By</th>
							<th class="align-left width_100_fix">Action</th>
							<th class="align-left width_100_fix">Remark</th>
							
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${eventAudit}" var="evtAudit">
							<tr>
								<td class="align-left width_200_fix"><fmt:formatDate value="${evtAudit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern=" dd/MM/yyyy hh:mm a" /></td>
								<td class="align-left width_200 width_200_fix"><span style="display: block">${evtAudit.actionBy.name}</span></td>
								<td class="align-left width_200 width_200_fix">${evtAudit.description}</td>
								<td class="align-left width_200 width_200_fix">${evtAudit.remark}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>