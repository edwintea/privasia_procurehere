<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord Approval-tabs marg-top-10">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseeleven"><spring:message code="summarydetails.event.approvals" /></a>
		</h4>
	</div>
	<div id="collapseeleven" class="panel-collapse collapse accortab in">
		<div class="panel-body eleven-contant pad_all_15">
		  <c:if test="${po.enableApprovalReminder}">
		  <div>
			<label>Reminder Settings: </label>
			 <div>
				<p>Reminder emails sent every ${po.reminderAfterHour} hours.</p>
				<p>Maximum	${po.reminderCount} reminder emails.</p>
				 <c:if test="${po.notifyEventOwner}">
				   <p><spring:message code="po.notification.owner.message" /></p>
				 </c:if>
			 </div>
		  </div>
		  </c:if>
			<c:forEach items="${po.approvals}" var="approval">
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
	<div class="clear"></div>
	<div class="remark-tab event_info pad0">
		<h4><spring:message code="summarydetails.approval.comments" /></h4>
		<div class="panel-body pad_all_15">
			<c:forEach items="${comments}" var="comment">
				<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
					<h3>
						${comment.createdBy.name}
						<span>
							<fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</span>
					</h3>
					<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
				</div>
			</c:forEach>
		</div>
		<c:if test="${po.status == 'PENDING' and poPermissions.activeApproval}">
			<form id="approvedRejectForm" method="post" action="" >
				<div class="row">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="id" id="id" value="${po.id}">
					<div class="col-sm-12 col-md-12 col-xs-12">
				
						<div class="col-sm-12 col-md-12 col-xs-12 ">
							<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
						</div>
						<div class="col-sm-8 col-md-8 col-xs-12 ">
							<textarea name="remarks" id="remarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
						</div>
					</div>
				</div>
				<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
					<button type="button" id="approvedButton" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out" style="margin-right: 20px;"><spring:message code="buyer.dashboard.approve" /></button>
					<button type="button" id="rejectedButton" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.reject" /></button>
				</div>
			</form>
		</c:if>
	</div>
</div>

<script type="text/JavaScript">
$('document').ready(function() {
	
	
});

</script>