<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<script>

</script>
<div class="Invited-Supplier-List white-bg">
	<div class=" meeting2-heading">
		<h3><spring:message code="label.meeting.create" /> </h3>
	</div>
	<c:url var="saveMeeting" value="saveMeeting" />
	<form:form id="demo-form4" cssClass="form-horizontal bordered-row " method="post" modelAttribute="meetingObject">
		<div class="meeting_main pad_all_15">
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="label.meeting.type" /></label>
				</div>
				<div class="col-md-5">
					<div>
						<form:select path="meetingType" cssClass="form-control chosen-select" class="custom-select" data-parsley-id="0644" id="idMeetingType">
							<form:option value=""><spring:message code="label.select.meeting.type" /></form:option>
							<form:options items="${meetingType}" />
						</form:select>
						<form:errors path="meetingType" cssClass="error" />
					</div>
					<ul id="parsley-id-0644" class="parsley-errors-list">
					</ul>
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="eventmeetings.appointment.date" /> &amp; <spring:message code="application.time" /></label>
				</div>
				<div class="col-md-3 col-xs-6">
					<div class="input-prepend input-group">
						<form:input path="appointmentDateTime" id="idAppointmentDateTime" class="bootstrap-datepicker form-control for-clander-view" type="text" data-date-format="dd/MM/yyyy" />
					</div>
				</div>
				<div class="col-md-2 col-xs-5">
					<div class="bootstrap-timepicker dropdown">
						<form:input path="appointmentTime" id="idAppointmentTime" class="timepicker-example for-timepicker-view form-control" type="text" data-date-format="HH:mm a" />
					</div>
				</div>
				<div class="col-md-1 col-xs-1">
					<div class="ring plus_btn_wrap">
						<a class="btn btn-info btn-tooltip" title='<spring:message code="tooltip.add.reminder"/> ' data-placement="top" data-target="#myModal6" data-toggle="modal">
							<img src="${pageContext.request.contextPath}/resources/images/ring_cion.png" style="margin-top: 10px">
						</a>
					</div>
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="appliaction.contact.person" /></label>
				</div>
				<div class="col-md-5 col-xs-11">
					<input type="hidden" name="contactPersonId" id="contactPersonId" value="" />
					<form:select path="contactPersons" id="contactPersons" cssClass="form-control chosen-select" class="custom-select" data-parsley-id="0644" name="">
						<form:options items="${userList}" itemLabel="name" itemValue="id" />
					</form:select>
				</div>
				<div class="col-md-1 col-xs-1">
					<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" title="" data-placement="top" data-toggle="tooltip" data-original-title="More Search Fields">
						<i class="glyph-icon icon-plus" aria-hidden="true"></i>
					</button>
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="label.meeting.venue" /></label>
				</div>
				<div class="col-md-5">
					<spring:message code="label.meeting.venue.placeholder" var="placevenue"/>
					<form:textarea path="venue" class="form-control textarea-counter" rows="3" placeholder="${placevenue}" />
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="pr.remark" /></label>
				</div>
				<div class="col-md-5">
					<spring:message code="label.meeting.remark" var="placeremark"/>
					<form:textarea path="remarks" class="form-control textarea-counter" rows="3" name="" placeholder="${placeremark}" />
				</div>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"><spring:message code="label.meeting.invite.supplier" /></label>
				</div>
				<c:if test="${not empty true}">
					<div class="col-md-5">
						<div class="invite-supplier delivery-address">
							<div class="chk_scroll_box" id="perferct_scroll">
								<div class="scroll_box_inner overscrollInnerBox">
									<form:select path="inviteSuppliers" id="test-select" name="mlt_1" multiple="multiple">
										<form:options items="${List}" data-section="Supplier Name" />
									</form:select>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${ empty true}">
					<div class="col-md-5">
						<input type="text" class="form-control" />
					</div>
					<div class="col-md-1 col-xs-1">
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" title="" data-placement="top" data-toggle="tooltip" data-original-title="More Search Fields">
							<i class="glyph-icon icon-plus" aria-hidden="true"></i>
						</button>
					</div>
				</c:if>
			</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3">
					<label class="marg-top-10"></label>
				</div>
				<div class="col-md-3 marg-bottom-20">
					<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" href="#" title=""><spring:message code="button.meeting.invite" /></button>
				</div>
			</div>
			<div class="row marg-bottom-10 add-past-check">
				<div class="col-md-3">
					<label class="marg-top-10"></label>
				</div>
				<div class="col-md-3">
					<label data-toggle="modal"> <input type="checkbox" id="inlineCheckbox200" class="custom-checkbox" /> <spring:message code="label.meeting.past.add" />
					</label>
				</div>
			</div>
		</div>
</div>
<div class="row">
	<div class="col-md-12 marg-top-20 col-xs-12">
		<button class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" href="#"><spring:message code="application.previous" /></button>
		<button type="submit" class="btn btn-info ph_btn marg-left-10 hvr-pop hvr-rectangle-out submitStep4"><spring:message code="application.next" /></button>
	</div>
	</form:form>
</div>
<div class="modal" id="myModal4">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="label.meeting.past.add" /></h3>
				<button class="close for-absulate" data-dismiss="modal" type="button">×</button>
			</div>
			<div class="form-group float-left width100 pad_all_15 white-bg">
				<form action="">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="event_form">
						<div class="form_field">
							<div class="form-group">
								<label class="col-sm-4 control-label"><spring:message code="supplier.name" /></label>
								<div class="col-sm-6 col-md-6">
									<div class="input_wrapper">
										<input type="text" data-validation="email" placeholder='<spring:message code="label.meeting.email"/> ' class="form-control mar-b10">
									</div>
								</div>
								<div class="col-sm-1 col-md-1">
									<div class="plus_btn_wrap gray ">
										<a href="#" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.add.more"/> ' class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out">
											<i aria-hidden="true" class="glyph-icon icon-plus"></i>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="pr.remark" /></label>
							<div class="col-sm-6 col-md-6">
								<div class="form-group">
									<textarea placeholder='<spring:message code="label.meeting.remark"/> ' rows="4" name="textarea1" data-validation="required" class="form-control"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="label.meeting.date" /></label>
							<div class="col-sm-6 col-md-6">
								<div class="input-prepend input-group">
									<input type="text" value="04-15-2016" data-validation="birthdate" data-validation-format="mm-dd-yyyy" class="bootstrap-datepicker1 form-control for-clander-view">
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="form-group">
							<label class="col-sm-4 control-label"><spring:message code="label.meeting.personal.details" /></label>
							<div class="col-sm-6 col-md-6">
								<div class="form-group">
									<textarea rows="4" name="textarea1" data-validation="required" class="form-control"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div class="form_field">
						<div class="col-sm-offset-4 col-md-6 col-sm-6">
							<div class="form-group">
								<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"><spring:message code="button.supplier.add" /></button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!--  <!-----------popup-------------------->
<div class="modal fade" id="myModal6" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<spring:message code="eventmeetings.modal.content" />
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="label.add.reminder" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="label.remind.me" /></label> <input type="text" class="form-control" /> <select class="custom-select">
					<option><spring:message code="rfaevent.hours" /></option>
					<option><spring:message code="label.minute" /></option>
					<option><spring:message code="application.second" /></option>
				</select>
				<div class="before-time-msg"><spring:message code="label.event.start.date.time" /> &amp; <spring:message code="application.time2" /></div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal"><spring:message code="application.save" /></button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.reset" /></button>
			</div>
		</div>
	</div>
</div>
-->
<script type="text/javascript" src="<c:url value="/resources/js/view/createRftMeeting.js?2"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/> "></script>
<!-- Timepicker js and css -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
