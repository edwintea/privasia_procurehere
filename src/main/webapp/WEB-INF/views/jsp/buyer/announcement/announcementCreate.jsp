
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<sec:authorize access="(hasRole('VIEW_ANNOUNCEMENT_EDIT') or hasRole('ADMIN')) and hasRole('BUYER')" var="canEdit" />
<sec:authorize access="hasRole('VIEW_ANNOUNCEMENT_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="uomCreateDesk" code="application.uom.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${uomCreateDesk}] });
});
</script>
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}
.ckContentDisabled {
  cursor:no-drop;
  pointer-events: none;
  touch-action: none;
}
select[readonly] option {
  background: #eee;
  cursor:no-drop;
  pointer-events: none;
  touch-action: none;
}
</style>
<div id="page-content" view-name="Announcement">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>
			<c:url value="/buyer/announcementList" var="listUrl" />
			<li><a id="listLink" href="${listUrl}">
					<spring:message code="announcement.list" />
				</a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="label.announcement" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
			<c:out value='${btnValue}' /> <spring:message code="label.announcement" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="announcementCreate" cssClass="form-horizontal bordered-row" method="post" action="createAnnouncement" modelAttribute="announcementObj">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<form:input type="hidden" path="readOnlyData" value="${readOnlyData}"/>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="title" cssClass="marg-top-10">
								<spring:message code="announcement.title" />
							</form:label>
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="announcement.title" var="title" />
							<spring:message code="announcement.title.required" var="required" />
							<spring:message code="announcement.title.length" var="length" />
							<form:input path="title" value="" class="disableOnEdit"  data-validation="required length alphanumeric" data-validation-length="1-64" data-validation-allowing="-_/ ." cssClass="form-control " id="idTitle" placeholder="${title}" readonly="${readOnlyData}" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label><spring:message code="announcement.start" /> </label>
						</div>
	
						<div class="col-md-3" >
							<div class="input-prepend input-group ${readOnlyData ? 'disabled' : ''}" >
								<form:input path="announcementStart"  autocomplete="off" id="announcementStart" data-placement="top" data-toggle="tooltip" data-original-title="Announcement Start date" class="bootstrap-datepicker form-control for-clander-view disableOnEdit" data-validation="required date"   data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy"  timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</div>
						</div>
						<div class="col-md-2 ${readOnlyData ? 'disabled' : ''}">
							<form:input path="announcementStartTime" id="announcementStartTime" onfocus="this.blur()" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control disableOnEdit" data-validation="required" autocomplete="off"  timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</div>
						
					</div>	
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label><spring:message code="announcement.end" /> </label>
						</div>
						
						<div class="col-md-3  disabledOnPublic ${!announcementObj.publicAnnouncement ? 'disabled' : ''}" >
							<form:input path="announcementEnd"  id="announcementEnd" data-startdate="" class="bootstrap-datepicker form-control for-clander-view" type="text" data-date-format="mm/dd/yy" autocomplete="off" placeholder="dd/mm/yyyy"  timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/>
						</div>
						<div class="col-md-2  disabledOnPublic ${!announcementObj.publicAnnouncement ? 'disabled' : ''}">
							<form:input path="announcementEndTime"  id="announcementEndTime"  onfocus="this.blur()" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" autocomplete="off"  timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"/>
						</div>
						
					</div>	
					<div class="row marg-bottom-20 notifyType-group required">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="announcement.selectNotificationType" /></label>
						</div>
						<div class="col-md-2 marg-top-10 sup-category">
							<span><div id="disabledPublic" class="${readOnlyData ? 'disabled' : ''}"> <form:checkbox path="publicAnnouncement" id="publicAnnouncement" class="custom-checkbox notifyType disableOnEdit publicContent"  title="Public Announcement" name="notifyType" /></div>
							<label style="white-space: nowrap;"><spring:message code="announcement.label.publicPage"/></label>
							</span>
						</div>
						<div class="col-md-2 marg-top-10 sup-category">
							<span> 
								<div id="disabledFax" class="${readOnlyData ? 'disabled' : ''}">
									<form:checkbox path="fax" id="notifyThroughFax" class="custom-checkbox notifyType disableOnEdit faxContent"  title="Notify through FAX." name="notifyType" />
								</div>
							<label><spring:message code="announcement.label.fax"/></label>
							</span>
						</div>
						<div class="col-md-2 marg-top-10 sup-category">
							<span><div id="disabledSms"  class="${readOnlyData ? 'disabled' : ''}"> <form:checkbox path="sms" id="notifyThroughSms" class="custom-checkbox notifyType disableOnEdit smsContent"  title="Notify Through SMS" name="notifyType"  /></div>
							<label><spring:message code="announcement.label.sms"/></label>
							</span>
						</div>
						<div class="col-md-2 marg-top-10 sup-category">
							<span><div id="disabledEmail"  class="${readOnlyData ? 'disabled' : ''}"> <form:checkbox path="email" id="notifyThroughEmail" class="custom-checkbox notifyType disableOnEdit emailContent"  title="Notify Through Email" name="notifyType"  /></div>
							<label><spring:message code="announcement.label.email"/></label>
							</span>
						</div>						
					</div>	
					<div class="row">
						<div class="col-md-3">							
						</div>
						<div class="col-md-9">
							<div id="selectNotifyTypeError"></div>
						</div>
					</div>
					
					<div class="row marg-bottom-20">
					</div>	
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label path="content" cssClass="marg-top-10">
								<spring:message code="announcement.content" />
							</label>
						</div>
						<div class="col-md-5 ${readOnlyData ? 'disabled' : ''}" id="addContent">
							<spring:message code="announcement.content" var="desc" />
							<form:textarea path="publicOrEmailContent" id="annoucementContentEditor" class="disablePublicContentOnEdit"  name="annoucementContentEditor" data-validation="length" maxlength="4000" data-validation-length="max4000"  data-validation-error-msg-length="Input Value must be between 1 to 4000." cssClass="form-control"   placeholder="${desc}"  />
							<span class="sky-blue">Max 4000 characters only</span>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label  cssClass="marg-top-10">
								<spring:message code="announcement.fax" />
							</label>
						</div>
						<div class="col-md-5  ${readOnlyData ? 'disabled' : ''}" id="faxContentDisable">
							<spring:message code="announcement.fax" var="faxContent" />
							<sprin:message code="announcement.faxContent.length" var="faxLength"/>
							<form:textarea path="faxContent" class="disableOnEdit" cssClass="form-control" rows="3" id="idFaxContent" placeholder="${faxContent}"  data-validation="length" data-validation-length="max1000" data-validation-error-msg-length="${faxLength}" />
							<span class="sky-blue">Max 1000 characters only</span>
						</div>
					</div>	
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label  cssClass="marg-top-10">
								<spring:message code="announcement.sms" />
							</label>
						</div>
						<div class="col-md-5 ${readOnlyData ? 'disabled' : ''}" id="smsContentDisable">
							<spring:message code="announcement.sms" var="smsContent" />
								<sprin:message code="announcement.smsContent.length" var="smsLength"/>
							<form:textarea path="smsContent" class="disableOnEdit" cssClass="form-control" id="idSmsContent" placeholder="${smsContent}" data-validation="length" data-validation-length="max160" data-validation-error-msg-length="${smsLength}"/>
							<span class="sky-blue">Max 160 characters only</span>
						</div>
					</div>	
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select disableOnEdit" id="idStatus" >
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
						<c:if test="${canEdit}">
							<form:button type="submit" value="save" id="saveAnnouncement" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" >${btnValue}</form:button>
						</c:if>	
							<c:url value="/buyer/announcementList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div> 
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
	CKEDITOR.env.isCompatible = true;
	CKEDITOR.config.fillEmptyBlocks = false;	
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>



<script>
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
	});
	</c:if>

	$.validate({
		lang : 'en',
		modules : 'date'
	});
	
</script>
<script type="text/javascript">
	$('document').ready(function(){
		
		
		/* CKEDITOR.editorConfig = function( config ) {
			config.toolbarGroups = [
				{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
				{ name: 'clipboard', groups: [ 'clipboard' ] },
				{ name: 'editing', groups: [ 'find', 'selection', 'editing' ] },
				{ name: 'forms', groups: [ 'forms' ] },
				{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
				{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
				'/',
				{ name: 'links', groups: [ 'links' ] },
				{ name: 'insert', groups: [ 'insert' ] },
				{ name: 'styles', groups: [ 'styles' ] },
				{ name: 'colors', groups: [ 'colors' ] },
				{ name: 'tools', groups: [ 'tools' ] },
				{ name: 'others', groups: [ 'others' ] },
				{ name: 'about', groups: [ 'about' ] },
				'/'
			];

			config.removeButtons = 'Source,Save,Templates,NewPage,Preview,Print,PasteText,PasteFromWord,Undo,Redo,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,CopyFormatting,RemoveFormat,CreateDiv,BidiLtr,BidiRtl,Language,Unlink,Anchor,Flash,PageBreak,Iframe,Styles,Maximize,ShowBlocks,About';
		}; */
		
		
		editorcmps = CKEDITOR.replace('annoucementContentEditor',
					{
							toolbarLocation : 'bottom',
							width : '100%',
							height : '200px',
							removePlugins : 'dragdrop,basket,elementspath,resize,images',
							toolbarGroups : [
								{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
								{ name: 'clipboard', groups: [ 'clipboard' ] },
								{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
								{ name: 'forms', groups: [ 'forms' ] },
								{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
								{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
								{ name: 'links', groups: [ 'links' ] },
								{ name: 'insert', groups: [ 'insert' ] },
								/* { name: 'styles', groups: [ 'format', 'font', 'fontsize' ] }, */
								{ name: 'colors', groups: [ 'colors' ] },
								{ name: 'tools', groups: [ 'tools' ] },
								{ name: 'others', groups: [ 'others' ] },
								'/'
							],
							
							/* toolbar : [
									{
										name : 'basicstyles',
										items : [
												'Bold',
												'Italic' ]
									},
									{
										name : 'paragraph',
										items : [
												'NumberedList',
												'BulletedList' ]
									},
									{
										name : 'styles',
										items : ['Format' ]
									},
									{
										name : 'colors',
										items : [
												'TextColor',
												'BGColor' ]
									}
							] */
						});
		

		editorcmps.on('instanceReady',function(ev) {
							// Prevent drag-and-drop.
							ev.editor.document.on('drop',function(ev) {
												ev.data.preventDefault(true);
											});
						}); 
		<c:if test="${!readOnlyData}">
			<c:if test="${!announcementObj.publicAnnouncement && !announcementObj.email}">
				$('#addContent').addClass('disabled');
			</c:if>
			<c:if test="${!announcementObj.fax}">
				$('#faxContentDisable').addClass('disabled');
			</c:if>
			<c:if test="${!announcementObj.sms}">
				$('#smsContentDisable').addClass('disabled');
			</c:if>
		</c:if>
 
 	$("#announcementCreate").submit(function(){
		 if ($('div.notifyType-group.required :checkbox:checked').length == 0){
	        $("#selectNotifyTypeError").text("Please select atleast one notification type").css("color", "#ff5757");
	        return false;
		 }
		 $('#annoucementContentEditor').parent().removeClass('has-error');
		 $(".checkError").remove();
		 if($('#publicAnnouncement').prop("checked") == true && CKEDITOR.instances['annoucementContentEditor'].getData()==''){
			$('#annoucementContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">This is a required field.</span>');
			 return false;
 		}
		 if($('#notifyThroughEmail').prop("checked") == true && CKEDITOR.instances['annoucementContentEditor'].getData()==''){
		 		$('#annoucementContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">This is a required field.</span>');
	 		return false;
 		}
		var publicContent = CKEDITOR.instances['annoucementContentEditor'].getData();
		 if(CKEDITOR.instances['annoucementContentEditor'].getData()!='' && publicContent.length > 4000){
			 $('#annoucementContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">The input value is longer than 4000 characters.</span>');
			 return false;
		 }
		 
 	});
 	
 	if($('#notifyThroughSms').prop("checked") == true){
		$('#idSmsContent').attr('data-validation', 'required length');
	}
	if($('#notifyThroughFax').prop("checked") == true){
		$('#idFaxContent').attr('data-validation', 'required length');
	}
});
	
	$(document).on('change', '#notifyThroughFax', function() {
 		if ($(this).is(':checked')) {
  			$('#idFaxContent').attr('data-validation', 'required length');
  			$('#faxContentDisable').removeClass('disabled');
  		}
  		else{
 			$('#idFaxContent').removeAttr('data-validation');
 			$('#faxContentDisable').addClass('disabled');
 			$('#idFaxContent').parent().find('.form-error').remove();
 			$('#idFaxContent').val('');
 		}
	 });
	
	$(document).on('change', '#notifyThroughSms', function() {
      	if ($(this).is(':checked')) {
       		$('#idSmsContent').attr('data-validation', 'required length');
       		$('#smsContentDisable').removeClass('disabled');
       	}
       	else{
      		$('#idSmsContent').removeAttr('data-validation');
      		$('#idSmsContent').parent().find('.form-error').remove();
      		$('#smsContentDisable').addClass('disabled');
      		$('#idSmsContent').val('');
      	}
	  });
	
	$(document).on('change', '#publicAnnouncement', function() {
      	if ($(this).is(':checked')) {
      		$('#addContent').removeClass('disabled');
      		$('.disabledOnPublic').removeClass('disabled');
      		$('#announcementEnd').attr('data-validation', 'required');
      		$('#announcementEndTime').attr('data-validation', 'required');
       	}
       	else{
       		$('.disabledOnPublic').addClass('disabled');
       		$('.disabledOnPublic').validate(function(valid, elem) {});
       		$('#announcementEnd').removeAttr('data-validation');
       		$('#announcementEndTime').removeAttr('data-validation');
       		$('#announcementEnd').val('');
       		$('#announcementEndTime').val('');
       		if($('#notifyThroughEmail').prop("checked") == false){
       			$('#annoucementContentEditor').parent().removeClass('has-error').find('.form-error').remove();
      			$('#addContent').addClass('disabled');
      			CKEDITOR.instances['annoucementContentEditor'].setData('');
       		}
      	}
	  });
	
 	$(document).on('change', '#notifyThroughEmail', function() {
      	if ($(this).is(':checked')) {
      		$('#addContent').removeClass('disabled');
       	}
       	else{
       		if($('#publicAnnouncement').prop("checked") == false){
      			$('#addContent').addClass('disabled');
      			$('#annoucementContentEditor').parent().removeClass('has-error').find('.form-error').remove();
      			CKEDITOR.instances['annoucementContentEditor'].setData('');
       		}
      	}
	  });    
 	$(document).on('change', '.notifyType', function () {
	    if (this.checked) {
	    	 $("#selectNotifyTypeError").text('');
	    }
	});
</script>
<script>
$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	$('.bootstrap-datepicker').bsdatepicker({
		format : 'dd/mm/yyyy',
		minDate : now,
		onRender : function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}

	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
		$('.bootstrap-datepicker').validate(function(valid, elem) {});
	});
	1

	$('.bootstrap-datepicker').datepicker().on('changeDate', function (ev) {
	    $('.bootstrap-datepicker').change();
	});
});

$('document').ready(function(){
	$('.timepicker-example').timepicker({
		disableFocus : false,
		explicitMode : false
	}).on('show.timepicker', function(e) {
		setTimeout(function() {
			$('.bootstrap-timepicker-widget.dropdown-menu.open input').addClass('disabled');
			$('.timepicker-example').validate(function(valid, elem) {});
		}, 500);
		
		
	});
});
</script>
