<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord Approval-tabs">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseSuspAppr"><spring:message code="summarydetails.suspension.approvals" /></a>
			<c:if test="${eventPermissions.owner and event.status == 'COMPLETE'}">
				<button class="editAwardApprovalPopupButton editSuspBtn" data-toggle="dropdown"  title="Edit" style="display: none;">
					<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
				</button>
			</c:if>
		</h4>
	</div>
	<div id="collapseSuspAppr" class="panel-collapse collapse accortab in">
		<div class="panel-body eleven-contant pad_all_15">

			<c:forEach items="${event.awardApprovals}" var="approval">
				<div class="pad_all_15 float-left width-100 position-relative">
					<label>Level ${approval.level}</label>
					<c:if test="${approval.active}">
						<span class="color-green marg-left-10">
							<i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[<spring:message code="buyer.dashboard.active" />]
						</span>
					</c:if>
					<div class="Approval-lavel1-upper">
						<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
							<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}   <c:if test="${!user.user.active}">[<span class="inactiveCaption" ><spring:message code="prsummary.inacive.status" /></span>]</c:if></div>
							<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
								<span class="pull-left">&nbsp;&nbsp${approval.approvalType}&nbsp;&nbsp</span>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<script>

</script>