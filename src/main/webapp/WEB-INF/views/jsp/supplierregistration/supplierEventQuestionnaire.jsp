<%@ page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxQuestionnairesDesk" code="application.supplier.rfx.questionnaires" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxQuestionnairesDesk}] });
});
</script>


<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>

<script>

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

	$('.bootstrap-datepicker').datepicker().on('changeDate', function (ev) {
	    $('.bootstrap-datepicker').change();
	});
	
	$('.bootstrap-datepicker').change(function () {
		//alert($('.bootstrap-datepicker').val());
	});
});
</script>
<script type="text/javascript">
	var stompClient = null;
	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
				var redirectUrl = getContextPath() + "/supplier/viewSupplierCq/${eventType}/${event.id}";
				if (messageOutput.body == 'CLOSED') {
					window.location.href = redirectUrl;
				} else if (messageOutput.body == 'SUSPENDED') {
					window.location.href = redirectUrl;
				}else if(messageOutput.body == 'RESUME'){
					window.location.href = redirectUrl;
				}
			});
		});
	}

	var errorHandler = function() {
		console.log("Connection lost..... reconnecting...");
	    setTimeout(function(){ connect(); }, 5 * 1000); // retry connection in 5 secs
	}

	function disconnect() {
		if (stompClient != null && stompClient.ws.readyState === stompClient.ws.OPEN) {
			stompClient.disconnect();
		}
		console.log("Disconnected");
	}

	$(window).unload(function(){
		if(stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});
	
	$(document).ready(function() {
		connect();
	});
</script>


<div id="page-content-wrapper">
	<div id="page-content">
		<jsp:useBean id="today" class="java.util.Date" />
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="supplierSubmissionHeader.jsp" />
			<c:if test="${!(eventSupplier.submissionStatus != 'INVITED' and ((eventType != 'RFA' and today ge event.eventStart) or eventType == 'RFA') and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY'))}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-4" class="tab-content">
						<div class="doc-fir-inner">
							<h3>Not Allowed to View</h3>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${eventSupplier.submissionStatus != 'INVITED' and ((eventType != 'RFA' and today ge event.eventStart) or eventType == 'RFA')  and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY')}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-4" class="tab-content">
						<c:if test="${showCq }">
							<div class="doc-fir-inner">
								<h3>
									<spring:message code="questionnaire.label" />
								</h3>
								<div class="Invited-Supplier-List dashboard-main">
									<div class="Invited-Supplier-List-table add-supplier id-list">
										<div class="ph_tabel_wrapper">
											<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table  mega questionnaire-table">
												<table class="table ph_table border-none header documents-page top-fix-head" width="100%" border="0" cellspacing="0" cellpadding="0">
													<thead>
														<tr>
															<th class=" align-center width_50_fix"><spring:message code="supplier.no.col" /></th>
															<th class=" align-center width_200_fix"><spring:message code="application.name" /></th>
															<th class=" align-center width_200_fix"><spring:message code="application.createddate" /></th>
															<th class=" align-center width_200_fix"><spring:message code="application.cq.completion.status" /></th>
															<!-- 														<th class=" align-center width_200_fix">Modified Date</th>
 -->
															<th class=" align-center width_200_fix"></th>
														</tr>
													</thead>
												</table>
												<c:forEach var="cqobj" items="${eventCqs}" varStatus="idx">
													<table class="ph_table table border-none tab_trans timezone induslist documents-page" width="100%" border="0" cellspacing="0" cellpadding="0">
														<tbody class="catecontent">
															<tr>
															<c:set var="cqStatus" value="${cqList[idx.index].supplierCqStatus}"/>
															  <c:if test="${cqobj.cqOrder != null}">
																 <td class="width_50_fix">${cqobj.cqOrder}</td>
														      </c:if>
															  <c:if test="${cqobj.cqOrder == null}">
															     <td class="width_50_fix">${idx.index + 1}</td>
															  </c:if>
																<td class="align-center width_200_fix">${cqobj.name}</td>
																<td class="align-center width_200_fix"><fmt:formatDate value="${cqobj.createdDate}" pattern="dd/MMM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
																<td class="align-center width_200_fix ${cqList != null ? (cqStatus == 'DRAFT' ? 'text-warning' : 'text-success'): ''}">${cqList != null ? ( cqStatus == "PENDING" ? '' : cqStatus) : ''}</td>
																<%-- 														<td class="align-center width_200_fix">
																<fmt:formatDate value="${cqobj.modifiedDate}" pattern="dd/MMM/yyyy -HH:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
															</td>
	 --%>
																<td class="align-center width_200_fix"><div class="disableViewCq">
																		<a href="${pageContext.request.contextPath}/supplier/viewCqDetails/${eventType}/${cqobj.id}/${event.id}" rftevent-id="${event.id}" eventCq-id="${cqobj.id}" class=" btn btn-info btn-cq-view"><spring:message code="rfs.view.button" /></a>
																	</div></td>
															</tr>
														</tbody>
													</table>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
							</div>
						</c:if>
						<!-- </div> -->
						<c:if test="${!showCq}">
							<div class="cqlistDetails">
								<form:form id="supplierCqList" action="${pageContext.request.contextPath}/supplier/saveCq/${eventType}/${event.id}?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="supplierCqItemPojo" enctype="multipart/form-data">
									<form:hidden path="cqId" />
									<div class="Gen-ques">
										<h3 class="marg-left-none">
											<spring:message code="rfs.cq.general" />
										</h3>
										<div class="Gen-ques-inner">
											<!-- <p>The answer format required is included for most questions. Please complete your answers only in the field column provoided. Your answers will be evaluated against only the original questions, (not any changes made to them). Where</p> -->
											<p class="marg-top-15 marg-bottom-15">
												<font color="red">*</font>
												<spring:message code="rfs.cq.required.field" />
											</p>
											<div class="Gen-ques-inner1 pad_all_15">
												<c:forEach var="item" items="${supplierCqItemPojo.itemList}" varStatus="status">
													<form:hidden path="itemList[${status.index}].id" />
													<form:hidden path="itemList[${status.index}].cq.id" />
													<form:hidden path="itemList[${status.index}].cqItem.id" />
													<form:hidden path="itemList[${status.index}].event.id" />
													<div class="Quest-textbox">
														<div class="row">
															<div class="col-md-12 col-sm-12 col-xs-12">
																<label>${item.cqItem.level}.${item.cqItem.order} ${item.cqItem.optional ? '<font color="red">*</font>' : ''}&nbsp;&nbsp; ${item.cqItem.itemName}</label>
															</div>
														</div>
														<div class="row">
															<div class="col-md-12">
																<div class="col-md-12 col-xs-12 note-tag mobileDesc">${item.cqItem.itemDescription}.</div>
															</div>
															<div class="col-md-5 col-sm-5 col-xs-6">
																<c:if test="${item.cqItem.cqType.value == 'Text'}">
																	<div>
																		<form:textarea maxlength="1000" data-validation="${item.cqItem.optional ? 'required':'' } length" class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers" data-validation-length="max1000"></form:textarea>
																	<span class="sky-blue">Max 1000 characters only</span>
																	</div>
																	<!-- 	<div class="sky-blue" id="info"></div> -->
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'Choice'}">
																	<%-- 	<c:forEach var="cqOptions" items="${item.cqItem.cqOptions}"> --%>
																	<div class="radio_yes-no width100">
																		<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																			<c:if test="${ fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" /> ${cqOptions.value}
																				</label>
																			</c:if>
																			<c:if test="${!fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton class="custom-radio" data-validation-error-msg-container="#${item.cqItem.id}-rediooption-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" /> ${cqOptions.value}
																				</label>
																			</c:if>
																		</c:forEach>
																		<div id="${item.cqItem.id}-rediooption-err-msg"></div>
																	</div>
																	<%-- </c:forEach> --%>
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'Choice with Score'}">
																	<%-- 	<c:forEach var="cqOptions" items="${item.cqItem.cqOptions}"> --%>
																	<div class="radio_yes-no width100">
																		<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																			<c:if test="${ fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton data-validation-error-msg-container="#${item.cqItem.id}-redio-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" checked="checked" /> ${cqOptions.value}
																				</label>
																			</c:if>
																			<c:if test="${!fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																				<label class="width100 inlineStyle"> <form:radiobutton data-validation-error-msg-container="#${item.cqItem.id}-redio-err-msg" data-validation="${item.cqItem.optional ? 'required':'' }" class="custom-radio" path="itemList[${status.index}].listOptAnswers" value="${cqOptions}" /> ${cqOptions.value}
																				</label>
																			</c:if>


																		</c:forEach>

																		<div id="${item.cqItem.id}-redio-err-msg"></div>
																	</div>
																	<%-- </c:forEach> --%>
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'List'}">
																	<form:select cssClass="chosen-select form-control" path="itemList[${status.index}].listOptAnswers" data-validation="${item.cqItem.optional ? 'required':'' }">
																		<form:option value="" disabled="true">Select</form:option>
																		<form:options items="${item.cqItem.displayCqOptions}" itemLabel="value" />
																	</form:select>
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'Checkbox'}">
																	<div>
																		<c:forEach var="cqOptions" items="${item.cqItem.displayCqOptions}" varStatus="opIndex">
																			<div class="radio_yes-no width100">
																				<c:if test="${ fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<form:checkbox class="custom-checkbox ${item.cqItem.id}-class" data-validation-error-msg-container="#${item.cqItem.id}-err-msg" data-group="${item.cqItem.id}" data-validation="${item.cqItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" checked="checked" /> ${cqOptions.value} </label>
																				</c:if>
																				<c:if test="${!fn:contains(supplierCqItemPojo.itemList[status.index].listOptAnswers, cqOptions)}">
																					<form:checkbox class="custom-checkbox ${item.cqItem.id}-class" data-validation-error-msg-container="#${item.cqItem.id}-err-msg" data-group="${item.cqItem.id}" data-validation="${item.cqItem.optional ?  'required_checkbox' : '' }" path="itemList[${status.index}].listOptAnswers[${opIndex.index}]" value="${cqOptions}" /> ${cqOptions.value} </label>
																				</c:if>
																			</div>
																		</c:forEach>
																		<div id="${item.cqItem.id}-err-msg"></div>
																	</div>
																</c:if>
																<!--  PH-278  -->
																<c:if test="${item.cqItem.cqType.value == 'Date'}">
																	<div class="input-prepend input-group">
																		<!--  -->
																		<form:input id="dateTypeAnswer" type="text" cssClass="bootstrap-datepicker  nvclick form-control for-clander-view" path="itemList[${status.index}].textAnswers" autocomplete="off" data-validation="${item.cqItem.optional ? 'required':'' }" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy"></form:input>
																	</div>
																</c:if>

																<c:if test="${item.cqItem.cqType.value == 'Number'}">
																	<div id="numberTypeInput">
																		<form:input id="numberAnswerinput" onkeypress="return isDecimalNumberKey(event)" data-validation="${item.cqItem.optional ? 'required':'' } custom" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-custom="Numeric upto two decimals only" class="numberTypeAnswer form-control textarea-autosize" path="itemList[${status.index}].textAnswers" ></form:input>
 																	</div>
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'Paragraph'}">
																	<div>
																		<form:textarea rows="4" maxlength="1500" data-validation=" ${item.cqItem.optional ? 'required':'' } length" data-validation-length="max1500" class="form-control textarea-autosize" path="itemList[${status.index}].textAnswers"></form:textarea>
																		<span class="sky-blue">Max 1500 characters only</span>
																	</div>
																</c:if>
																<c:if test="${item.cqItem.cqType.value == 'Document download link'}">
																	<div class="tab-main-inner pad_all_15 mb-13">
																		<ul>
																			<c:forEach var="eventDocument" items="${item.eventDocuments}">
																				<li><c:url var="download" value="/supplier/downloadSupplierDocument/${eventType}/${eventDocument.id}" /> <a href="${download}" data-placement="top" title="Download"> ${eventDocument.fileName} </a></li>
																			</c:forEach>
																		</ul>
																	</div>
																</c:if>
																<c:if test="${item.cqItem.attachment}">
																	<div class="margin-top-10">
																		<label>${item.cqItem.isSupplierAttachRequired ? '<font color="red">*</font>':'' }</label>
																		<div class="fileinput fileinput-new input-group uploadFileBlockQuesInput" data-provides="fileinput" data-itemId="${item.cqItem.id}" <c:if test="${not empty supplierCqItemPojo.itemList[status.index].fileName}">style="display:none;"</c:if>>
																			<div class="form-control" data-trigger="fileinput">
																				<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span>
																			</div>
																			<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"><spring:message code="application.selectfile" /> </span> <span class="fileinput-exists"><spring:message code="event.document.change" /></span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
																					<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
																				</c:forEach>
																				<form:input class="uploadQuestionFileAttch" type="file" path="itemList[${status.index}].attachment" id="uploadFileAttachId" data-validation-allowing="${fileType}" data-validation="${item.cqItem.isSupplierAttachRequired ? 'required':'' } extension size" data-validation-error-msg-container="#Load_File-error-dialog${status.index}" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation-error-msg-required="Please select file"
																					data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="You can only upload file pdf, docx xls and xlsx"></form:input>
																			</span> <a href="#" class="input-group-addon btn btn-black fileinput-exists" data-dismiss="fileinput"><spring:message code="application.remove" /></a>
																		</div>
																		<div id="Load_File-error-dialog${status.index}" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
																		<div class="ph_table_border uploadedFileBlockQues pull-left width100 <c:if test="${empty supplierCqItemPojo.itemList[status.index].fileName}">flagvisibility</c:if>">
																			<div class="reminderList marginDisable">
																				<div class="row" id="">
																					<div class="col-md-12">
																						<p>
																							<c:if test="${not empty supplierCqItemPojo.itemList[status.index].fileName}">
																								<form:form method="GET">
																									<c:url var="download" value="/supplier/downloadAttachment/${eventType}/${event.id}/${supplierCqItemPojo.itemList[status.index].id}" />
																									<a class="pull-left" href="${download}">${supplierCqItemPojo.itemList[status.index].fileName}</a>
																								</form:form>
																								<a class="pull-right removeFile" removeOtherId='${supplierCqItemPojo.itemList[status.index].id}' otherCredFile='${supplierCqItemPojo.itemList[status.index].fileName}'> <i class="fa fa-trash-o"></i>
																								</a>
																							</c:if>
																						</p>
																					</div>
																				</div>
																			</div>
																		</div>
																		<c:if test="${empty supplierCqItemPojo.itemList[status.index].fileName}">
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
															<div class="col-md-5 col-sm-5 col-xs-6 note-tag deskTopDesc">${item.cqItem.itemDescription}.</div>
														</div>
													</div>
												</c:forEach>
											</div>
											<input type="hidden" name="eventTypeHiddenData" id="eventTypeHiddenData" value="${eventType}" /> 
											<input type="hidden" name="eventidHiddenData" id="eventidHiddenData" value="${event.id}" />
											<div class="row">
												<div class="col-md-11 col-xs-12 col-sm-12 margin-top-10 supplierCqSubmtBtons">
													<!-- <button class="btn btn-black ph_btn_midium back_to_Question" >Back to Questionnaire</button> -->
													<a href="${pageContext.request.contextPath}/supplier/viewSupplierCq/${eventType}/${event.id}" class="btn btn-black ph_btn_midium back_to_Question"><spring:message code="rfs.cq.back.quetionnaire" /></a>
													
													<form:button class="btn btn-info ph_btn_midium marg-left-10 pull-right" id="savebutton" type="button" >
														<spring:message code="application.cq.complete" />
													</form:button>

													<c:if test="${supplierCqStatus.supplierCqStatus ne 'COMPLETED'}">
														<form:button type="button" class="btn btn-black ph_btn_midium marg-left-10 pull-right skipvalidation" id="saveDraftbutton">
															<spring:message code="application.cq.save.draft"/>
														</form:button>
													</c:if>
													
												</div>
											</div>
										</div>
									</div>
								</form:form>
							</div>
						</c:if>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/file-input/file-input.js"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
$.validate({
	lang : 'en',
	modules : 'file'
});


$.formUtils.addValidator({ name : 'required_checkbox', validatorFunction : function(value, $el, config, language, $form) {
	console.log('Required Checkbox group : ', $el.attr('data-group') , ' Total Checked : ', $('.' + $el.attr('data-group') + '-class :checked').length);
	var cbxGroup = $el.attr('data-group') + '-class';
	var totalChecked = $('input:checkbox.'+cbxGroup + ':checked').length;
	
	return (totalChecked > 0);
	
}, errorMessage : 'Select at least one option', errorMessageKey : 'err_required_checkbox' });

</script>
<style>
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
var allowedFields = '';
	<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'DELETE_NOTIFY' or event.suspensionType == 'DELETE_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		<c:if test="${eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
			$(window).bind('load', function() {
					var allowedFields = '.tab-link > a,.back_to_Question,.small-accordin-tab, .do-not-disable';
					//var disableAnchers = ['#reloadMsg'];        
					disableFormFields(allowedFields);
				});
		</c:if>
	</c:if>

	<c:if test="${eventPermissions.viewer or eventSupplier.submissionStatus == 'COMPLETED' or event.status == 'CLOSED' or event.status == 'FINISHED'}">
		<c:if test="${eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
			$(window).bind('load', function() {
					var allowedFields = '.tab-link > a,.back_to_Question,.small-accordin-tab,.btn-cq-view,#idDutchConsole,#idEnglishConsole, .do-not-disable';
					//var disableAnchers = ['#reloadMsg'];        
					disableFormFields(allowedFields);
				});
		</c:if>
	</c:if>
	
	<c:if test="${eventSupplier.submissionStatus =='REJECTED' and eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a,.back_to_Question, .btn-cq-view, .do-not-disable';
			disableFormFields(allowedFields);
		});
	</c:if>

	$(document).ready(function() {
		$(".general-btn").click(function() {
			$(".doc-fir-inner").hide();
		});
		$(".general-btn").click(function() {
			$(".Gen-ques").show();
		});

		$(".bill-quantityA").click(function() {
			$(".doc-fir-inner1").hide();
		});
		$(".bill-quantityA").click(function() {
			$(".Bill-option-A").show();
		});

		$(".bill-quantityB").click(function() {
			$(".doc-fir-inner1").hide();
		});
		$(".bill-quantityB").click(function() {
			$(".Bill-option-B").show();
		});
		$('.uploadQuestionFileAttch').change(function(e) {
			e.preventDefault();
// 			$('#supplierCqList').isValid();
		});
		
 	 	$('#savebutton').click(function(e){
			e.preventDefault();
			
			$('form.has-validation-callback :input').each(function() {
				if($(this).attr('type') != 'hidden') {
					$(this).on('beforeValidation', function(value, lang, config) {
						if($(this).attr('data-validation-skipped') != undefined )
						$(this).removeAttr('data-validation-skipped');
					});
				}	
			});
		
			if ($('#supplierCqList').isValid()) {
 				$('#loading').show();
				$('#supplierCqList').submit();
 			} else {
				console.log("Form is not valid.................");
				$('#loading').hide();
			}
			
		}); 
	});
</script>
<script>

	$(document).ready(function() {
		
		$(".disableViewCq").click(function() {
			
			$(this).addClass("disabled");	
			
		});

		$(".fade-btn").click(function() {
			$(".meeting-detail").hide();
		});
		$(".fade-btn").click(function() {
			$(".no-attand-meeting").show();
		});
	});
</script>
<script>
	$(document).ready(function() {
		$(".back_to_Question").click(function() {
			$(".Gen-ques").hide();
		});

		$(".back_to_Question").click(function() {
			$(".Quest-Scoring").hide();
		});
		$(".back_to_Question").click(function() {
			$(".doc-fir-inner").show();
		});

		$(".back_to_BQ").click(function() {
			$(".doc-fir-inner1").show();
		});

		$(".back_to_BQ").click(function() {
			$(".Bill-option-B").hide();
		});
		$(".back_to_BQ").click(function() {
			$(".Bill-option-A").hide();
		});
		$('.event-details-tabs li').click(function() {
			$(this).find('form').submit();
		});

	});
</script>
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
	counterMeetings();
	setInterval(function() {
		counterMeetings();
	}, 60000);
	function counterMeetings() {
		$('.countDownTimer1').each(
				function() {
					var target_date = new Date($(this).attr('data-date')).getTime();
					var days, hours, minutes, seconds;
					var current_date = new Date().getTime();
					var seconds_left = (target_date - current_date) / 1000;
					days = parseInt(seconds_left / 86400);
					seconds_left = seconds_left % 86400;
					hours = parseInt(seconds_left / 3600);
					seconds_left = seconds_left % 3600;
					minutes = parseInt(seconds_left / 60);
					seconds = parseInt(seconds_left % 60);
					htmldata = '';
					if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
						htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours
								+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
					}
					$(this).html(htmldata);
				});
	}
</script>
<script>
		// set the date we're counting down to

		// get tag element
		//var countdown = document.getElementById('countdown');
		//alert(countdown);
		// update the tag with id "countdown" every 1 second
		counterMeetings1();
		setInterval(function() {

			counterMeetings1();

		}, 60000);
		function counterMeetings1() {
			$('.countDownTimer').each(
					function() {

						var target_date = new Date($(this).attr('data-date')).getTime();

						// variables for time units
						var days, hours, minutes, seconds;

						// find the amount of "seconds" between now and target
						var current_date = new Date().getTime();
						var seconds_left = (target_date - current_date) / 1000;

						// do some time calculations
						days = parseInt(seconds_left / 86400);
						seconds_left = seconds_left % 86400;

						hours = parseInt(seconds_left / 3600);
						seconds_left = seconds_left % 3600;

						minutes = parseInt(seconds_left / 60);
						seconds = parseInt(seconds_left % 60);

						// format countdown string + set tag value
						htmldata = '';
						if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
							htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes
									+ '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
						}//$('#countdown').html(htmldata);
						$(this).html(htmldata);

					});
		}
</script>
<script type="text/javascript">
	$('.table-1 .custom-checkbox').on('change', function() {

		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});

	});
	
	$('.table-2 [type=checkbox]').on('change', function() {

		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox');

		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		$.uniform.update(firstObj);

	});
	
	$('#saveDraftbutton').click(function(e){
		e.preventDefault();

		$('form.has-validation-callback :input').each(function() {
			if($(this).attr('type') != 'file' || $(this).val() == '') {
				$(this).on('beforeValidation', function(value, lang, config) {
					if($(this).attr('data-validation-skipped') == undefined )
					$(this).attr('data-validation-skipped', 1);
				});
			}	
		});

		$('#supplierCqList').attr('action', getSupplierContextPath() + "/saveAsDraftCq/${eventType}/${event.id}?${_csrf.parameterName}=${_csrf.token}");
		$('#supplierCqList').submit();

	});
	

	$("#test-select ").treeMultiselect({ enableSelectAll : true,
	sortable : true });
</script>
<script>

	function isDecimalNumberKey(evt)
	{
	   var charCode = (evt.which) ? evt.which : event.keyCode;
	   console.log("charCode " + charCode);
	   if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 190 && charCode != 110 && charCode != 46)
	      return false;
	
	   return true;
	}
	
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
		
	  /*   function isNumberKey(evt, obj) {
	         var charCode = (evt.which) ? evt.which : event.keyCode
	        var value = obj.value;
	        var dotcontains = value.indexOf(".") != -1;
	        if (dotcontains)
	            if (charCode == 46) return false;
	        if (charCode == 46) return true;
	        if (charCode > 31 && (charCode < 48 || charCode > 57))
	            return false;
	        return true; 
	    } */
		

	    
/* 	 	  $('.numberTypeAnswer').keyup(function () {
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
 */	});
	
	
	
</script>
<script>
	// filter meetings
	$('.radio_meeting').on('click', '#showAll', function() {
		$('.meeting-detail').show();
		$('#eventFilter').children().first().html('All Meeting');
	});
	$('.radio_meeting').on('click', '#showAccepted', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.accepted').show();
		$('#eventFilter').children().first().html('Accepted Meeting');
	});
	$('.radio_meeting').on('click', '#showPending', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.pending').show();
		$('#eventFilter').children().first().html('Pending Meeting');
	});
	$('.radio_meeting').on('click', '#showCanceled', function() {
		$('.meeting-detail').hide();
		$('.meeting-detail.canceled').show();
		$('#eventFilter').children().first().html('Canceled Meeting');
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierCqSubmission.js"/>"></script> 

<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>"> --%>
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>"> --%>
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script> --%>
<%-- <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>"> --%>
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script> --%>
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script> --%>
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script> --%>
 
