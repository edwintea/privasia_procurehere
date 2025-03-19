<!--TODO DELETE THIS NOT IN USE  -->
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
			<a data-toggle="collapse" class="accordion" href="#collapse12"><spring:message code="summarydetails.approval.comments"/></a>
		</h4>
	</div>
	<div id="collapse12" class="panel-collapse collapse">
		<div class="panel-body pad_all_15">
			<c:forEach items="${comments}" var="comment">
				<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
					<h3>
						${comment.createdBy.name} <span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</span>
					</h3>
					<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
				</div>
			</c:forEach>
		</div>
	</div>
	<c:if test="${event.status == 'PENDING' and eventPermissions.activeApproval}">
		<form id="approvedRejectForm" method="post" action="">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="id" id="id" value="${event.id}">
			<label class="col-sm-12 col-md-12 align-left col-xs-12 control-label"><spring:message code="pr.remark" /> : </label>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<textarea name="remarks" id="remarks" rows="4" class="form-control"></textarea>
			</div>
			<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
				<button type="button" id="approvedButton" class="btn btn-info ph_btn hvr-pop marg-right-20 hvr-rectangle-out" style="margin-right:20px;"><spring:message code="application.approve" /></button>
				<button type="button" id="rejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-20 hvr-rectangle-out1"><spring:message code="application.reject" /></button>
			</div>
		</form>
	</c:if>
</div>
