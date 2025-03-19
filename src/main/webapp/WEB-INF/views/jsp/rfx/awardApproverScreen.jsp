<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${event.status == 'COMPLETE' && eventPermissions.activeAwardApproval}">
<div class="panel sum-accord Approval-tabs">
	<div class="clear"></div>
	<div class="remark-tab event_info pad0">
			<form id="approvedApproveForm" method="post" >
				<div class="row">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="eventId" id="eventId" value="${event.id}">
					<input type="hidden" name="eventType" id="eventType" value="${eventType}">
					<input type="hidden" name="bqId" class="bqId" value="${suppBqId}" />
					<div class="col-sm-12 col-md-12 col-xs-12">
				
						<div class="col-sm-12 col-md-12 col-xs-12 marg-top-20">
							<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
						</div>
						<div class="col-sm-8 col-md-8 col-xs-12 ">
							<textarea name="remarks" id="remarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
						</div>
					</div>
				</div>
				<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
					<button type="button" id="approverApprovedButton" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out" style="margin-right: 20px;"><spring:message code="buyer.dashboard.approve" /></button>
					<button type="button" id="approverRejectedButton" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.reject" /></button>
				</div>
			</form>
	</div>
</div>
</c:if>

<script>
$(document).ready(function() {
	$('#approverApprovedButton').click(function(e) {
		e.preventDefault();
		if($('#approvedApproveForm').isValid()){
			console.log('Valid');
		} else {
			console.log('Invalid');
		}
		$('#approvedApproveForm').attr('action', getContextPath() + "/buyer/RFP/approveAward");
		$('#approvedApproveForm').submit();

		
	});
	
	$('#approverRejectedButton').click(function(e) {
		e.preventDefault();
		console.log(".......... Reject");
		$(this).addClass('disabled');
		$('#approvedApproveForm').attr('action', getContextPath() + "/buyer/RFP/rejectAward");
		$('#approvedApproveForm').submit();
	
	});
	
});
</script>