<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<div class="meeting_main pad_all_15">
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="eventdetails.event.title" />
			</label>
		</div>
		<div class="col-md-5">
			<spring:message code="enter.title.placeholder" var="titleplace" />
			<form:input type="text" data-validation="required length" data-validation-length="1-80" path="title" readonly="${source == 'summary' ? 'true' : 'false'}" class="form-control textarea-counter" rows="3" placeholder="${titleplace}" />
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="label.meeting.type" />
			</label>
		</div>
		<div class="col-md-3 col-xs-6">
			<div>
				<form:select path="meetingType" data-validation="required" cssClass="form-control chosen-select disablesearch" id="idMeetingType">
					<form:options items="${meetingType}" itemLabel="value" />
				</form:select>
				<form:errors path="meetingType" cssClass="error" />
			</div>
		</div>
		<div class="col-md-2 col-xs-5" style="padding-top: 10px;">
			<label> <spring:message code="label.meeting.attend.mandatory" />
			</label>
			<form:checkbox class="custom-checkbox" path="meetingAttendMandatory" id="meetAttendMandatory" />
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="label.meeting.appointment.date.time" />
			</label>
		</div>
		<div class="col-md-3 col-xs-6">
			<div class="input-prepend input-group">
				<jsp:useBean id="now" class="java.util.Date" scope="request" />
				<c:if test="${now ge event.eventPublishDate}">
					<c:set var="allowedStartDate" value="${now}" />
				</c:if>
				<c:if test="${now lt event.eventPublishDate}">
					<c:set var="allowedStartDate" value="${event.eventPublishDate}" />
				</c:if>
				<fmt:formatDate var="eventPubDate" value="${allowedStartDate}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
				<fmt:formatDate var="eventEndDate" value="${event.eventEnd}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
				<fmt:formatDate var="appointmentDate" value="${eventMeeting.appointmentDateTime}" pattern="dd/MM/yyyy" timeZone="${sessionScope['timeZone']}" />
				<spring:message code="tooltip.meeting.date" var="appointmentdate" />
				<spring:message code="dateformat.placeholder" var="dateformat" />
				<form:input readonly="true" path="appointmentDateTime" data-placement="top" data-toggle="tooltip" data-original-title="${appointmentdate}" start-date="${eventPubDate}" end-date="${eventEndDate}" data-validation="required date" class="bootstrap-datepicker form-control for-clander-view" id="meetingDate" type="text" data-validation-format="dd/mm/yyyy" placeholder="${dateformat}" data-date-format="dd/MM/yyyy" value="${appointmentDate}" />
			</div>
		</div>
		<div class="col-md-2 col-xs-5">
			<div class="bootstrap-timepicker dropdown">
				<!--<fmt:formatDate var="appointmentTime" value="${eventMeeting.appointmentDateTime}" pattern="hh:mm a" timeZone="${sessionScope['timeZone']}" />-->
				<!--<form:input onfocus="this.blur()" readonly="true" path="appointmentTime" data-validation="required" class="timepicker-example for-timepicker-view form-control" type="text" data-date-format="HH:mm a" value="${appointmentTime}" />-->
				<form:input onfocus="this.blur()" path="appointmentTime" autocomplete="off" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" type="text" data-date-format="HH:mm a" value="${appointmentTime}" />
			</div>
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3"></div>
		<div class="col-md-5  col-xs-10">
			<div class="ph_table_border">
				<div class="reminderList marginDisable">
					<c:forEach items="${eventMeeting.rfxEventMeetingReminder}" var="rememberData">
						<fmt:formatDate var="reminderId" value="${rememberData.reminderDate}" pattern="ddMMyyyyHHmm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						<fmt:formatDate var="reminderDateTime" value="${rememberData.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						<div class="row myreminder" id="${reminderId}">
							<input type="hidden" name="reminderId" value="${rememberData.id}"> <input type="hidden" name="reminderDate[]" value="${reminderDateTime}" id="idreminderDateAndTime"> <input type="hidden" id="idReminderDuration" name="reminderDuration[]" value="${rememberData.interval}"> <input type="hidden" id="idReminderDurationType" name="reminderDurationType[]" value="${rememberData.intervalType}">
							<div class="col-md-10">
								<p>
									<b><spring:message code="rfi.createrfi.reminder.date" />: </b>${reminderDateTime}</p>
							</div>
							<div class="col-md-2">
								<a href="" class="deleteReminder" reminderid=""> <i class="fa fa-times-circle"></i>
								</a>
							</div>
						</div>
					</c:forEach>
					<c:if test="${empty eventMeeting.rfxEventMeetingReminder}">
						<div class="row placeHolderBlock" id="">
							<div class="col-md-12">
								<p>
									<spring:message code="label.add.reminder" />
								</p>
							</div>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		<div class="col-md-1 col-xs-2">
			<div class="ring plus_btn_wrap">
				<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out rfaMeetingIdAddReminder" title='<spring:message code="add.reminder.placeholder" />' data-placement="top" id="idAddReminder" data-toggle="tooltip"> <img src="${pageContext.request.contextPath}/resources/images/ring_cion.png">
				</a>
			</div>
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <label class="marg-top-10"> <spring:message code="label.meeting.contact.person" />
			</label>
			</label>
		</div>
		<div class="col-md-5 col-xs-10">
			<div class="ph_table_border">
				<div class="contactList marginDisable">
					<c:forEach items="${eventMeeting.rfxEventMeetingContacts}" var="contact">
						<c:set var="contactName" value="${fn:replace(contact.contactName,' ', '_')}" />
						<c:set var="contactName" value="${fn:replace(contactName,'.', '_')}" />
						<c:set var="contactName" value="${fn:replace(contactName,'#', '_')}" />
						<c:set var="contactName" value="${fn:replace(contactName,'@', '_')}" />
						<c:set var="contactName" value="${fn:replace(contactName,'\\'', '_')}" />
						<c:set var="contactName" value="${fn:replace(contactName,'\"', '_')}" />
						<div class="row" id="${empty contact.id ? contactName : contact.id}">
							<input type="hidden" name="contactId[]" value="${contact.id}"> <input type="hidden" name="contactName[]" value="<c:out value="${contact.contactName}" escapeXml="true" />"> <input type="hidden" name="contactEmail[]" value="${contact.contactEmail}"> <input type="hidden" name="contactNumber[]" value="${contact.contactNumber}">
							<div class="col-md-2">
								<a href="" class="editMeetContact pull-left" contactId="${contact.id}"> <i class="fa fa-edit"></i>
								</a>
							</div>
							<div class="col-md-8">
								<p>${contact.contactName}</p>
								<p>${contact.contactEmail}</p>
								<p>${contact.contactNumber}</p>
							</div>
							<div class="col-md-2">
								<a href="" class="deleteMeetContact" contactId="${contact.id}"> <i class="fa fa-times-circle"></i>
								</a>
							</div>
						</div>
					</c:forEach>
					<c:if test="${empty eventMeeting.rfxEventMeetingContacts}">
						<div class="row placeHolderBlock" id="">
							<div class="col-md-12">
								<p>
									<spring:message code="eventdetails.event.add.contactperson" />
								</p>
							</div>
						</div>
					</c:if>
				</div>
			</div>
			<div id="contactlistMettingError"></div>
			<input type="text" class="blankElementForValidation" id="noofcontacts" name="noofcontacts" value="" data-validation="required, number" data-validation-error-msg-container="#contactlistMettingError" data-validation-allowing="range[1;100]" data-validation-error-msg="This is a required field" />
		</div>
		<div class="col-md-2 col-xs-2">
			<button id="idAddContact" data-toggle="tooltip" title='<spring:message code="tooltip.add.contact" />' data-placement="top" class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out">
				<i id="addPlus" aria-hidden="true" class="glyph-icon icon-plus"> </i>
			</button>
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="label.meeting.venue" />
			</label>
		</div>
		<div class="col-md-5">
			<spring:message code="label.meeting.venue.placeholder" var="venue" />
			<form:textarea path="venue" data-validation="required length" data-validation-length="max500" class="form-control textarea-counter" rows="3" placeholder="${venue}" />
			<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
		</div>
	</div>
	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="label.meeting.remark" />
			</label>
		</div>
		<div class="col-md-5">
			<spring:message code="label.meeting.remark" var="remark" />
			<form:textarea path="remarks" data-validation="length" data-validation-length="max1000" class="form-control textarea-counter" rows="3" name="" placeholder="${remark}" />
			<span class="sky-blue"><spring:message code="dashboard.valid.max2.characters" /></span>
		</div>
	</div>

	<div class="row marg-bottom-10">
		<div class="col-md-3">
			<label class="marg-top-10"> <spring:message code="label.meeting.invite.supplier" />
			</label>
		</div>
		<div class="col-md-5">
			<%-- <div class="invite-supplier delivery-address">
				<div class="chk_scroll_box" id="perferct_scroll">
					<div class="scroll_box_inner overscrollInnerBox">
						<form:select path="inviteSuppliers" data-validation="required" id="test-select" name="mlt_1" multiple="multiple">
							<form:options items="${listSuppliers}" itemValue="id" itemLabel="companyName" data-section="Supplier Name" />
						</form:select>
					</div>
				</div>
			</div>
 --%>
			<div class="row">
				<div class="col-xs-12">
					<div class="bordered-row">
						<div class="ph_tabel_wrapper scrolableTable_list">
							<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th search-type="" class="checkbox-stylling width_50 width_50_fix align-center"><input type="checkbox" class="custom-checkAllbox custom-checkbox" name="selectAllSupplier" id="select-all"></th>
										<th search-type="text" style="text-align: left;" search-type="text" class=" align-left"><spring:message code="application.supplier" /></th>
									</tr>
								</thead>
							</table>

						</div>
						<div style="display: none;" id="selectedList"></div>
						<div style="display: none;" id="removedList"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="row marg-bottom-10">
			<div class="col-md-3">
				<label class="marg-top-10"></label>
			</div>
		</div>
		<div class="row marg-bottom-10">
			<div class="col-md-3">
				<label class="ml-15"> <spring:message code="label.meeting.document.add" />
				</label>
			</div>
			<div class="col-md-5">
				<div data-provides="fileinput" class="fileinput fileinput-new input-group">
					<spring:message code="meeting.doc.file.length" var="filelength" />
					<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />
					<div data-trigger="fileinput" class="form-control">
						<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename show_name"></span>
					</div>
					<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="application.selectfile" />
					</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
					</span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
							<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
						</c:forEach> <input name="rftUploadDocument" id="rftUploadDocument" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
					</span>
				</div>
				<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
				<div class="progressbar flagvisibility" data-value="0">
					<div class="progressbar-value bg-green">
						<div class="progress-overlay"></div>
						<div class="progress-label">0%</div>
					</div>
				</div>
				<span> <spring:message code="application.note" />:<br />
					<ul>
						<li><spring:message code="eventsummary.meetings.upload.files" /></li>
						<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
						<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
					</ul>
				</span> <a class="upload_btn btn btn-info cancel  ph_btn_midium hvr-pop hvr-rectangle-out marg-top-10" href="javascript:void(0);" id="uploadRftDoc"> <spring:message code="application.upload" />
				</a>
			</div>
		</div>
		<div class="row marg-bottom-10">
			<div class="col-md-3">
				<label class="ml-15"> <spring:message code="label.meeting.file.list" />
				</label>
			</div>
			<div class="col-md-5">
				<div class="ph_table_border">
					<div class="mega">
						<table class="header ph_table border-none" width="100%">
							<thead>
								<tr>
									<th class="width_200 width_200_fix align-left"><spring:message code="label.meeting.file.name" /></th>
									<th class="width_100 width_100_fix align-left"><spring:message code="event.document.fileSize" /></th>
									<th class="width_100 width_100_fix align-center"><spring:message code="application.action1" /></th>
								</tr>
							</thead>
						</table>
						<table class="data ph_table border-none" width="100%" id="rftDocList">
							<input type="hidden" id="meetingId" value="${eventMeeting.id}" />
							<tbody>
								<%-- <c:forEach items="${eventMeeting.rfxEventMeetingDocument}" var="docs">
														<tr>
															<td class="width_200 width_200_fix align-left"><a class="bluelink" href="downloadMeetingDocument?docId=${docs.id}&docRefId=">${docs.fileName}</a></td>
															<td class="width_100 width_100_fix align-center"><span class="removeDocFile" removeDocRefId="" removeDocId="${docs.id}"> <a href=""> <i class="fa fa-times-circle"></i>
																</a>
															</span></td>
														</tr>
													</c:forEach> --%>
								<c:if test="${not empty listMeetingDocument}">
									<c:forEach items="${listMeetingDocument}" var="docs">
										<tr>
											<td class="width_200 width_200_fix align-left"><a class="bluelink" href="downloadMeetingDocument?docId=${docs.id}&docRefId=">${docs.fileName}</a></td>
											<td class="width_100 width_100_fix align-left"><fmt:formatNumber type="number" maxFractionDigits="1" value="${docs.fileSizeInKb}" /> KB</td>
											<td class="width_100 width_100_fix align-center"><a href=""> <span class="removeDocFile" removeDocRefId="" removeDocId="${docs.id}"> <i class="fa fa-times-circle"></i>
												</span>
											</a></td>
										</tr>
									</c:forEach>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>




	<script type="text/javascript">
			var selectedArray = new Set();
			var unSelectedArray = new Set();
			var removedArray = new Set();
	$('document').ready(
			function() {

				var table = $('#tableList').DataTable(
						{
							"oLanguage" : {
								"sUrl" : getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
							},
							"processing" : true,
							"deferRender" : true,
							"preDrawCallback" : function(settings) {
								// $('div[id=idGlobalError]').hide();
								$('#loading').show();
								return true;
							},
							"drawCallback" : function() {
								// in case your overlay needs to be put away automatically you can put it here
								$('#loading').hide();
								$('input[type="checkbox"].custom-checkbox').uniform();
								$('.checker span').append('<i class="glyph-icon icon-check"></i>');
								
								 buildSelectedData();								
							},
							"serverSide" : true,
							"pageLength" : 10,
							"searching" : true,
							 "ordering": false,
							"ajax" : {
								"url" : getContextPath() + "/buyer/${eventType}/meetingSupplierList/${event.id}",
								
								"data" : function(d) {
									d.selectedList =  Array.from(selectedArray).join();
									d.unSelectedList =  Array.from(unSelectedArray).join();
									d.meetingId='${eventMeeting.id}';
									d.checkedAll=$('#select-all').is(":checked");
									console.log(d.selectedList);
								},
								"error" : function(request, textStatus, errorThrown) {
									var error = request.getResponseHeader('error');
									if (error != undefined) {
										$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
										$('div[id=idGlobalError]').show();
									}
									$('#loading').hide();
								}
							},
							"order" : [],
							"columns" : [ {
								'searchable' : false,
								'orderable' : false,
								'className' : 'checkbox-stylling',
								'render' : function(data, type, row) {
									if(row.inMeeting== true){
										selectedArray.add(row.id);
									}
									return '<input type="checkbox" class="custom-checkbox1 custom-checkbox"  '+(row.inMeeting== true?'checked':'')+'  value="'+row.id+'" id="inviteSuppliers" >';
								}
							}, {
								"data" : "companyName",
								"className" : "align-left",
								"defaultContent" : ""
							} ],
							"initComplete" : function(settings, json) {
								var htmlSearch = '<tr class="tableHeaderWithSearch">';
								$('#tableList thead tr:nth-child(1) th').each(
										function(i) {
											var title = $(this).text();
											if (!(title == "Actions") && $(this).attr('search-type') != '') {
												if ($(this).attr('search-type') == 'select') {
													var optionsType = $(this).attr('search-options');
													htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="' + i + '"  name="' + (title.replace(/ /g, "")).toLowerCase()
															+ '"><option value=""><spring:message code="buyercreation.all.case"/></option>';

													if (optionsType == 'rfxList') {
														<c:forEach items="${rfxList}" var="item">
														htmlSearch += '<option value="${item}">${item}</option>';
														</c:forEach>
													}

													if (optionsType == 'statusList') {
														<c:forEach items="${statusList}" var="item">
														htmlSearch += '<option value="${item}">${item}</option>';
														</c:forEach>
													}

													htmlSearch += '</select></th>';
												} else {
													htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase()
															+ '" placeholder="<spring:message code="buyercreation.search.case"/> ' + title + '" data-index="' + i + '" /></th>';
												}
											} else {
												htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
											}
										});
								htmlSearch += '</tr>';
								$('#tableList thead').append(htmlSearch);
								$(table.table().container()).on('keyup', 'thead input', function() {
									if ($.trim(this.value).length > 2 || this.value.length == 0) {
										table.column($(this).data('index')).search(this.value).draw();
									}
								});

								$(table.table().container()).on('change', 'thead select', function() {
									table.column($(this).data('index')).search(this.value).draw();
								});

								$('.custom-checkAllbox').on('change', function() {
									var check = this.checked;
									$(".custom-checkbox1").each(function() {
										$(".custom-checkbox1").prop('checked', check);
										var checked = this.checked;
										if(checked){
											selectedArray.add(this.value);
											unSelectedArray.delete(this.value);
											removedArray.delete(this.value);
										}else{
											selectedArray.delete(this.value);
											unSelectedArray.add(this.value);
											removedArray.add(this.value);
											$("#select-all").prop('checked', false);
											$.uniform.update();
										}
										buildSelectedData();
										buildRemovedData();
										$.uniform.update($(this));
									});
									
									
									
									
								});
								
								$(document).delegate('.custom-checkbox1','change', function(){ 
									var checked = this.checked;
									
									if(checked){
										selectedArray.add(this.value);
										unSelectedArray.delete(this.value);
										removedArray.delete(this.value);
									}else{
										selectedArray.delete(this.value);
										unSelectedArray.add(this.value);
										removedArray.add(this.value);
										$("#select-all").prop('checked', false);
										$.uniform.update();
									}
									buildSelectedData();
									buildRemovedData();
									
								});
								
							}
						});
				
				
				function buildSelectedData(){
					jQuery('#selectedList').empty();
					for (var it = selectedArray.values(), val= null; val=it.next().value; ) {
					    var input='<input type="hidden" name="selectedSuppliers" value="'+val+'" id="'+val+'">';
						jQuery('#selectedList').append(input);
					}
				}
				
				function buildRemovedData(){
					jQuery('#removedList').empty();
					for (var it = removedArray.values(), val= null; val=it.next().value; ) {
					    var input='<input type="hidden" name="removedSuppliers" value="'+val+'" id="'+val+'">';
						jQuery('#removedList').append(input);
					}
				}
				
				
			});
</script>