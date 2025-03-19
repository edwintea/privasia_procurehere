<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

				<div class="tab-main-inner pad_all_15">
					<div id="tab-4" class="tab-content">
					<div class="cqlistDetails">
								<form:form id="supplierForm" action="${pageContext.request.contextPath}/supplier/submitSupplierform/${supplierForm.id}?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="supplierFormSubmissionItemPojo" enctype="multipart/form-data">
									<form:hidden path="formId" /> 
									<div class="Gen-ques">
										<h3 class="marg-left-none">
											<spring:message code="prsummary.general.information" />
										</h3>
										<div class="Gen-ques-inner">
											<p class="marg-top-15 marg-bottom-15">
												<font color="red">*</font>
												<spring:message code="rfs.cq.required.field" />
											</p>
											<div class="Gen-ques-inner1 pad_all_15">
											
											<c:forEach var="item" items="${supplierFormSubmissionItemPojo.itemList}" varStatus="status">
													<form:hidden path="itemList[${status.index}].id" /> 
													<form:hidden path="itemList[${status.index}].formSub.id" />
													<form:hidden path="itemList[${status.index}].formItem.id" />
													<div class="Quest-textbox">
														<div class="row">
															<div class="col-md-12 col-sm-12 col-xs-12">
																<label>${item.formItem.level}.${item.formItem.order} ${item.formItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.formItem.itemName}</label>
															</div>
														</div>
														<div class="row">
															<div class="col-md-12">
																<div class="col-md-12 col-xs-12 note-tag mobileDesc">${item.formItem.itemDescription}.</div>
															</div>
														 <div class="col-md-5 col-sm-5 col-xs-6">
																<c:if test="${item.formItem.cqType.value == 'Text'}">
																	<div>
																		<form:textarea maxlength="1000" data-validation="${item.formItem.optional ? 'required':'' } length" class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers" data-validation-length="max1000"></form:textarea>
																	<span class="sky-blue">Max 1000 characters only</span>
																	</div>
																</c:if>
																	<c:if test="${item.formItem.cqType.value == 'Choice'}">
																	<div class="radio_yes-no width100">
																		<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
																			<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton disabled="${supplierForm.status == 'PENDING' ? false : true }" data-validation="${item.formItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" /> ${cqOptions.value}
																				</label>
																			</c:if>
																			<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton disabled="${supplierForm.status == 'PENDING' ? false : true }" class="custom-radio" data-validation-error-msg-container="#${item.formItem.id}-rediooption-err-msg" data-validation="${item.formItem.optional ? 'required':'' }" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" /> ${cqOptions.value}
																				</label>
																			</c:if>
																		</c:forEach>
																		<div id="${item.formItem.id}-rediooption-err-msg"></div>
																	</div>
																</c:if>
																<c:if test="${item.formItem.cqType.value == 'Choice with Score'}">
																	<div class="radio_yes-no width100">
																		<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
																			<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton disabled="${supplierForm.status == 'PENDING' ? false : true }" data-validation-error-msg-container="#${item.formItem.id}-redio-err-msg" data-validation="${item.formItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" /> ${cqOptions.value}
																				</label>
																			</c:if>
																			<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton disabled="${supplierForm.status == 'PENDING' ? false : true }" data-validation-error-msg-container="#${item.formItem.id}-redio-err-msg" data-validation="${item.formItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" /> ${cqOptions.value}
																				</label>
																			</c:if>
																		</c:forEach>
																		<div id="${item.formItem.id}-redio-err-msg"></div>
																	</div>
																</c:if>
																<c:if test="${item.formItem.cqType.value == 'List'}">
																	<div>
																		<form:select cssClass="chosen-select form-control" path="itemList[${status.index}].listOptAnswers" data-validation="${item.formItem.optional ? 'required':'' }">
																			<form:option value="" disabled="disabled">Select</form:option>
																			<form:options items="${item.formItem.displayCqOptions}" itemLabel="value" />
																		</form:select>
																	</div>
																</c:if>
																<c:if test="${item.formItem.cqType.value == 'Checkbox'}">
																	<div>
																		<c:forEach var="cqOptions" items="${item.formItem.displayCqOptions}" varStatus="opIndex">
																			<div class="radio_yes-no width100">
																				<c:if test="${ fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<form:checkbox class="custom-checkbox ${item.formItem.id}-class" data-validation-error-msg-container="#${item.formItem.id}-err-msg" data-group="${item.formItem.id}" data-validation="${item.formItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" checked="checked" /> ${cqOptions.value} </label>
																				</c:if>
																				<c:if test="${!fn:contains(supplierFormSubmissionItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<form:checkbox class="custom-checkbox ${item.formItem.id}-class" data-validation-error-msg-container="#${item.formItem.id}-err-msg" data-group="${item.formItem.id}" data-validation="${item.formItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" /> ${cqOptions.value} </label>
																				</c:if>
																			</div>
																		</c:forEach>
																		<div id="${item.formItem.id}-err-msg"></div>
																	</div>
																</c:if>
																<!--  PH-278  -->
																<c:if test="${item.formItem.cqType.value == 'Date'}">
																	<div class="input-prepend input-group">
																		<!--  -->
																		<form:input id="dateTypeAnswer" type="text" cssClass="bootstrap-datepicker  nvclick form-control for-clander-view" path="itemList[${status.index}].textAnswers" autocomplete="off" data-validation="${item.formItem.optional ? 'required':'' }" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy"></form:input>
																	</div>
																</c:if>

																<c:if test="${item.formItem.cqType.value == 'Number'}">
																	<div id="numberTypeInput">
																		<form:input id="numberAnswerinput" data-validation="${item.formItem.optional ? 'required':'' }" class="numberTypeAnswer form-control textarea-autosize" path="itemList[${status.index}].textAnswers"></form:input>
																		<span id="errormsgNumberAnswer" class="sky-blue">Numeric upto two decimals only</span>
																	</div>
																</c:if>
																<c:if test="${item.formItem.cqType.value == 'Paragraph'}">
																	<div>
																		<form:textarea rows="4" maxlength="1500" data-validation=" ${item.formItem.optional ? 'required':'' } length" data-validation-length="max1500" class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers"></form:textarea>
																		<span class="sky-blue">Max 1500 characters only</span>
																	</div>
																</c:if>
																<c:if test="${not empty item.itemAttachment}">
																<div class="margin-top-10">
																	<div class="tab-main-inner pad_all_15 mb-13">
																		<p>Buyer Attachments</p>
																		<div class="row" id="" style="margin-top:10px;">
																			<ul>
																				<c:forEach var="eventDocument" items="${item.itemAttachment}">
																				<li><c:url var="download" value="/supplier/downloadSupplierDocument/${eventDocument.id}" />
	                                                                             <a href="${download}" class="buyerItemAttachment" data-placement="top" title="Download"> ${eventDocument.fileName}</a>
																				</li>
																				</c:forEach>
																			</ul>
																		</div>
																	</div>
																	</div>
																	</c:if>
																<c:if test="${item.formItem.attachment}">
																	<div class="margin-top-10">
																		<div class="fileinput fileinput-new input-group uploadFileBlockQuesInput" data-provides="fileinput" data-itemId="${item.formItem.id}" <c:if test="${not empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">style="display:none;"</c:if>>
																			<div class="form-control" data-trigger="fileinput">
																				<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span>
																			</div>
																			<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"><spring:message code="application.selectfile" /> </span> <span class="fileinput-exists"><spring:message code="event.document.change" /></span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
																					<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
																				</c:forEach> <form:input class="uploadQuestionFileAttch" type="file" path="itemList[${status.index}].attachment" id="uploadFileAttachId" data-validation-allowing="${fileType}" data-validation="${item.formItem.isSupplierAttachRequired ? 'required':'' } extension size" data-validation-error-msg-container="#Load_File-error-dialog${status.index}" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation-error-msg-required="Please select file"
																					data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="You can only upload file pdf, docx xls and xlsx"></form:input>
																			</span> <a href="#" class="input-group-addon btn btn-black fileinput-exists" data-dismiss="fileinput"><spring:message code="application.remove" /></a>
																		</div>
																		<div id="Load_File-error-dialog${status.index}" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
																		<div class="ph_table_border uploadedFileBlockQues pull-left width100 <c:if test="${empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">flagvisibility</c:if>">
																			<div class="reminderList marginDisable">
																				<div class="row" id="">
																					<div class="col-md-12">
																						<p>
																							<c:if test="${not empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">
																								<form:form method="GET">
																									<c:url var="download" value="/supplier/downloadAttachment/${supplierForm.id}/${supplierFormSubmissionItemPojo.itemList[status.index].id}" />
																									<a class="pull-left formAttachmentDownload" href="${download}">${supplierFormSubmissionItemPojo.itemList[status.index].fileName}</a>
																								</form:form>
																								<a class="pull-right removeFile" removeOtherId='${supplierFormSubmissionItemPojo.itemList[status.index].id}' otherCredFile='${supplierFormSubmissionItemPojo.itemList[status.index].fileName}'> <i class="fa fa-trash-o"></i>
																								</a>
																							</c:if>
																						</p>
																					</div>
																				</div>
																			</div>
																		</div>
																		<c:if test="${empty supplierFormSubmissionItemPojo.itemList[status.index].fileName}">
																			<span> <spring:message code="application.note" />:<br />
																				<ul>
																					<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
																					<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
																				</ul>
																			</span>
																		</c:if>
																	</div>
																</c:if>
															</div> 
															<div class="col-md-5 col-sm-5 col-xs-6 note-tag deskTopDesc">${item.formItem.itemDescription}.</div>
														</div>
													</div>
												</c:forEach>
												
											</div>
											<div class="row">
												<div class="col-md-12 col-xs-12 col-sm-12 margin-top-10 supplierCqSubmtBtons text-right">
													<c:if test="${supplierForm.status =='PENDING'}">
													  <spring:message code="application.draft" var="draft"/>
						                               <input type="button" id="draftbutton" class="btn btn-info ph_btn_midium marg-left-10 orangecol skipvalidation" value="${draft}" />
													<form:button class="btn btn-info ph_btn_midium marg-left-10" id="submitbutton" type="submit">
														<spring:message code="application.submit" />
													</form:button> 
													</c:if>
												</div>
											</div>
										</div>
									</div>
								</form:form>
							</div>
				 </div>
				</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/file-input/file-input.js"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<style>
.orangecol{
background: none 0px 0px repeat scroll rgb(230, 126, 34) !important;
}
.mb-13 {
	margin-bottom: 13px;
}

.radio_yes-no {
	padding-bottom: 15px;
}

.radio_yes-no span {
	display: inline-block;
	width: 100%;
}

.Gen-ques-inner1  .radio_yes-no span label {
	width: auto;
	float: none;
	padding-left: 10px;
}

.width100.inlineStyle .radio+label {
	width: auto !important;
	padding: 0;
}

.width100.inlineStyle label {
	float: left;
	width: 95% !important;
	display: inline-block;
}

.width100.inlineStyle input {
	display: inline-block;
	float: left;
}

.hideElement {
	width: 0 !important;
	height: 0 !important;
	padding: 0 !important;
	min-width: 0 !important;
	max-width: 0;
	border: 0;
}

.disableCq {
	pointer-events: none !important;
}
.radio input[type="radio"] {
    margin-left: 0;
}
</style>
<script>
	$(document).ready(function() {
		$("#datepicker").datepicker();
		
		$('select').chosen({ disable_search : true });
		$(".Questi-btn").click(function() {
			$(".doc-fir-inner").hide();
		});
		$(".Questi-btn").click(function() {
			$(".Quest-Scoring").show();
		});
	});
</script>
<script>
$.validate({
	lang : 'en',
	modules : 'file'
});

//Skip JQuery validations for save draft
$(".skipvalidation ").on('click', function(e) {
	if ($("#skipper").val() == undefined) {
		e.preventDefault();
		$(this).after("<input type='hidden' id='skipper' value='1'>");
		$('form.has-validation-callback :input').each(function() {
			$(this).on('beforeValidation', function(value, lang, config) {
				$(this).attr('data-validation-skipped', 1);
			});
		});
		$(this).trigger("click");
	}
});

$('#draftbutton').click(function() {
	$('#supplierForm').attr('action', getContextPath() + "/supplier/saveSupplierFormAsDraft/${formId}?${_csrf.parameterName}=${_csrf.token}");
	$('#supplierForm').submit();
});

$(document).ready(function() {
 	$('#numberAnswerinput').keyup(function () {
	    if (this.value.match(/^(\d+)?([.]?\d{0,2})?$/)) {
	    	  $('#numberAnswerinput').removeAttr('style');
	    	  $('#errormsgNumberAnswer').removeAttr('style');
	    	  $('#errormsgNumberAnswer').addClass('sky-blue');
	    } else {
	    	$('#errormsgNumberAnswer').removeClass('sky-blue');
	        $('#numberAnswerinput').attr('style', 'border-color: rgb(185, 74, 72)');
	        $('#errormsgNumberAnswer').attr('style', 'color: rgb(185, 74, 72)');
	    }
	}); 
 	
	 $('.uploadQuestionFileAttch').change(function(e) {
		e.preventDefault();
	}); 
	 
	$('.uploadQuestionFileAttch').on('change.bs.fileinput', function(event) {
		if ($(this).val() != '') {
			$(this).parent().addClass('hideElement');
		} else {
			$(this).parent().removeClass('hideElement');
		}
	});
	
	$('#submitbutton').click(function(e){
		e.preventDefault();
		if (!$('#supplierForm').isValid()) {
			return false;
		}
		if($("#numberTypeInput").length){
			if($('#errormsgNumberAnswer').hasClass('sky-blue')){
				$(this).addClass('disabled');
				$('#supplierForm').submit();	
			}
		}else{
		$(this).addClass('disabled');
		$('#supplierForm').submit();
		}
	}); 
});

$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	$('.bootstrap-datepicker').bsdatepicker({
		format : 'dd/mm/yyyy',
		//minDate : now,
		onRender : function(date) {
			//return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
		
	});
});

$.formUtils.addValidator({ name : 'required_checkbox', validatorFunction : function(value, $el, config, language, $form) {
	console.log('Required Checkbox group : ', $el.attr('data-group') , ' Total Checked : ', $('.' + $el.attr('data-group') + '-class :checked').length);
	var cbxGroup = $el.attr('data-group') + '-class';
	var totalChecked = $('input:checkbox.'+cbxGroup + ':checked').length;
	
	return (totalChecked > 0);
	
}, errorMessage : 'Select at least one option', errorMessageKey : 'err_required_checkbox' });

<c:if test="${supplierForm.status == 'ACCEPTED' or supplierForm.status == 'SUBMITTED' or supplierForm.status == 'REJECTED'}">
$(window).bind('load', function() {
	var allowedFields = '#draftbutton,#submitbutton, .formAttachmentDownload,.buyerItemAttachment';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>

$(document).delegate('.removeFile', 'click', function(e) {
	e.preventDefault();
	var currentParentBlock = $(this).closest('.uploadedFileBlockQues');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var removeOtherId = $(this).attr('removeOtherId');
	var otherCredFile = $(this).attr('otherCredFile');
	//var eventTypeHiddenData = $('#eventTypeHiddenData').val();
	//var eventidHiddenData = $('#eventidHiddenData').val();
	var formId = $('#formId').val();
	var ajaxUrl = getContextPath() + '/supplier/removeSupplierFormDoc/' + formId + '/' + removeOtherId;
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			if (data == true) {
				$('div[id=idGlobalSuccess]').show();
				currentParentBlock.hide();
				currentParentBlock.siblings('.uploadFileBlockQuesInput').show().find('[data-dismiss="fileinput"]').click();
				var message = request.getResponseHeader('sucess');
				window.location.href = getContextPath() + "/supplier/supplierFormView/" + formId + "?success=" + $.trim(message);
			} else {
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			if (request.getResponseHeader('error')) {
				$('div[id=idGlobalError]').show();
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});
</script>