<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<spring:message var="prSummaryDesk" code="application.pr.create.summary" />
<spring:message var="prTeamMemberDesk" code="application.pr.team.members" />
<spring:message var="prApprovalDesk" code="application.pr.approvals" />
<style>
.dd-same .divider {
	margin: 0 !important;
}

.dd-same #crateNewEventId {
	margin-top: 0;
}

.dd-same {
	border: 1px solid #e5e5e5;
	padding-bottom: 0 !important;
}

.dd-same li a {
	padding: 7px 20px;
	font-weight: 600;
}

.caret-btn {
	float: right;
	top: 16px;
	position: relative;
}

.caret {
	color: #fff !important;
}

.min-width-button{
   min-width: 150px;
}

.modal-dialog.for-delete-all.reminder {
	width: 590px !important;
}

.modal-dialog.conclude-confirm {
	max-width: 90%;
	min-width: 500px;
}

</style>

<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prSummaryDesk},${prTeamMemberDesk},${prApprovalDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="request.purchase.requisition" /></li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="request.purchase.requisition" />
					<h2 class="trans-cap pull-right">Status : ${sourcingFormRequest.status}</h2>
				</h2>
			</div>
			<div class="clear"></div>
			<jsp:include page="requestSummary.jsp"></jsp:include>


			<c:if test="${(sourcingFormRequest.status eq 'APPROVED' or sourcingFormRequest.status eq 'FINISHED') and  eventPermissions.owner}">
				<div class="btn-group dropup">
					<button type="button" class="btn btn-warning dropdown-toggle1 min-width-button" data-toggle="dropdown" aria-expanded="false">
					 <spring:message code="sourcingsummary.button.conclude" />
					 <span class="caret caret-btn"></span>
					</button>
				 <ul class="dropdown-menu dd-same" role="menu">
					<li id="crateNewEventId"><a><spring:message code="eventsummary.create.next.event.button" /></a></li>
					<li class="divider"></li>
					<li id="idConcludeEvent"><a><spring:message code="sourcingsummary.button.conclude.remark" /></a></li>
					</ul>
				</div>
			</c:if>


		</div>
	</div>
</div>



<div class="modal fade" id="crateNewEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<div class="modal-content">
			<div class="modal-header">
				<h3>Create Event</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<jsp:include page="createNewEventArchive.jsp"></jsp:include>
		</div>
	</div>
</div>

<!-- CONCLUDE EVENT -->
<div class="modal fade" id="concludeEvent" role="dialog">
	<div class="modal-dialog for-delete-all reminder conclude-remarks">
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="sourcingsummary.button.conclude" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			 <jsp:include page="concludeSourcingRequestArchive.jsp"></jsp:include> 
		</div>
	</div>
</div>

 <!-- CONCLUDE CONFIMR EVENT -->
<div class="modal fade" id="concludeConfirmEvent" role="dialog">
	<div class="modal-dialog conclude-confirm confirm">
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="sourcingsummary.button.conclude.confirm"/>
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>	
			<div class="row marg-bottom-10 marg-top-20 marg-left-10 marg-right-10 marg-bottom-20">
				<label>	 <spring:message code="sourcingsummary.button.conclude.confirm.msg"/> </label>
 			</div>			
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" id="btnConcludeConfirm" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out"><spring:message code="application.confirm" /></button>
				<button type="button" id="btnConcludeCancel" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" ><spring:message code="application.cancel" /></button>
			</div>
	</div>
	</div>
</div>

<script>
$(document).ready(function() {
	
	 $('#crateNewEventId').click(function (event) {
		 // Reset all form fields within the modal
		 $('#crateNewEvent').find('form')[0].reset();

		 // After resetting, manually set the default value to 'RFI'
		 $('#crateNewEvent').find('select.chosen-select').val('RFI').trigger('chosen:updated');
		 
		 // Remove any user interaction and revert checkboxes to their initial state (re-render the state from server-side)
		 $('#crateNewEvent').find('input.custom-doc-checkbox').each(function() {
			 // Reset the checkbox to its default state
			 this.checked = this.defaultChecked;
		 });

		 $('#crateNewEvent').modal('show')
	    });

	 $('#idConcludeEvent').click(function (event) {
			$('#btnConcludeConfirm').removeClass('disabled');
			$('.modal-dialog.conclude-remarks .textarea-counter').val("");	 		
		 $('#concludeEvent').modal('show')
	 });
});
</script>
