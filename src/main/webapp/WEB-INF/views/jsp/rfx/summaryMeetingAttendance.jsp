<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="flagvisibility dialogBox" id="attendancePopUp" title="Attendance">
	<div class="Invited-Supplier-List white-bg">
		<div class=" pad_all_15 width-100 pull-left invited-list">
		<form action="" method="post" id="meeting-form-attendance">
		<input type="hidden" value="${listMeeting.id}" id="meetingId" name="meetingId">
		<input type="hidden" value="${event.id}" id="eventId" name="eventId">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="mega ">
				<table class="header ph_table border-none headerOfTable" width="100%" border="0" cellspacing="0" cellpadding="0">
					<thead>
						<tr>
							<th class=" width_200"><spring:message code="eventsummary.invitedsupplier.companyname" /></th>
							<th class=" width_200 align-center"><spring:message code="meeting.attendance.label" /></th>
							<th class=" width_200 align-center" align="center"><spring:message code="supplier.designation" /></th>
							<th class=" width_200 align-center" align="center"><spring:message code="application.mobile.number" /></th>
							<th class=" width_200 align-center" align="center"><spring:message code="meeting.attendance.attended" /></th>
							<th class=" width_200 align-center" align="center"><spring:message code="pr.remark" /></th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none contantDataHtmlData" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tbody>
					</tbody>
				</table>
			</div>
			<div class="marg-bottom-10 width50 pull-left">
				<div class="col-md-2 marg-top-20">
					<button class="btn btn-info ph_btn btn-margin-top hvr-pop hvr-rectangle-out submitMeetingAttandence" type="button"><spring:message code="application.save" /></button>
				</div>
			</div>
			
			<div class="marg-bottom-10 width50 pull-right">
				<div class="col-md-2 marg-top-20">
					<button class="btn btn-info ph_btn btn-margin-top hvr-pop hvr-rectangle-out completMeetingAttandence" type="button"><spring:message code="button.meeting.attendance.complete.meeting" /></button>
				</div>
				
			</div>
			
			</form>
		</div>
	</div>
</div>
<div class="flagvisibility dialogBox" id="attendancePopUpComplete" title="Complete Meeting">
	<div class="Invited-Supplier-List white-bg">
		<div class="pad_all_15 width-100 pull-left invited-list">
			<div class="marg-bottom-10 width50 pull-left">
				<div class="col-md-2 col-sm-6 col-6 marg-top-20">
					<button class="btn btn-danger ph_btn btn-margin-top hvr-pop completeNoMeetingAttandence" type="button">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
			<div class="marg-bottom-10 width50 pull-right">
				<div class="col-md-2 col-sm-6 col-6 marg-top-20">
					<button class="btn btn-info ph_btn btn-margin-top hvr-pop hvr-rectangle-out completeYesMeetingAttandence" type="button">
						<spring:message code="button.meeting.attendance.complete.meeting" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
#attendancePopUp{
	margin-top: -7px;
}
.dialogBlockLoaded[aria-describedby="attendancePopUp"]{
	max-width: 900px!important;
}
#meeting-form-attendance .mega{
	max-height:300px;
}
#meeting-form-attendance .ph_table.header th, #meeting-form-attendance .ph_table.data td{
    padding: 5px;
}
[aria-describedby="attendancePopUpComplete"] .ui-dialog-titlebar.ui-widget-header{
	position: relative!important;
}
#meeting-form-attendance .chosen-container{
	max-width:80px!important;
}
.attandAttended{
	display:none!important;
}
@media (max-width: 767px) {
	.marg-bottom-10 {
		margin-bottom: 10px; /* Adjust spacing for smaller screens */
	}
}

</style>