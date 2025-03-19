<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEditt" />
<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_VIEW_ONLY')" var="readOnlyPermission" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<spring:message code="sourcing.template.inactivate.label" var="inactivateLabel" />
<spring:message code="buyer.dashboard.active" var="activeLabel" />
<spring:message code="sourcing.template.inactive.label" var="inactiveLabel" />				
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canEditt() {
		return "${canEditt}";
	}
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li>
						<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" /></a>
					</li>
					<li>
						<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/spTemplateList"><spring:message code="sptemplate.list" /></a>
					</li>
					<li class="active">
						<spring:message code="sptemplate.create" />
					</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">${createSpTemplate}</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />: ${!supplierPerformanceTemplate.performanceCriteriaCompleted ? 'DRAFT' : supplierPerformanceTemplate.status}
						</h2>
					</div>
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="spTemplateHeader.jsp"></jsp:include>
					<div class="clear"></div>
					<c:url var="saveSupplierPerformanceTemplate" value="/buyer/saveSupplierPerformanceTemplate" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<form:form id="sourcetemplate" action="${saveSupplierPerformanceTemplate}" method="post" modelAttribute="supplierPerformanceTemplate">
						<input type="hidden" name="id" value="${supplierPerformanceTemplate.id}">
						<div class="tab-pane active">
							<div class="tab-content Invited-Supplier-List ">
								<div class="tab-pane active white-bg" id="step-1">
									<div class="upload_download_wrapper clearfix mar-t20 event_info">
										<h4>
											<spring:message code="sourcing.template.details" />
										</h4>
										<div class="row">
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> <spring:message code="rfxTemplate.templateName" />
													</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6 ">
													<spring:message var="templatename" code="sourcingtemplate.name" />
													<form:input path="templateName" type="text" placeholder="${templatename}" data-validation="required length" data-validation-length="max64" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> <spring:message code="template.description" />
													</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<spring:message var="placeDescription" code="sourcingtemplate.description" />
													<form:textarea path="templateDescription" maxlength="1000" placeholder="${placeDescription}" rows="5" data-validation="length" data-validation-length="max250" class="form-control"></form:textarea>
													<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> <spring:message code="application.status" /></label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus" data-validation="required">
														<form:options items="${statusList}" />
													</form:select>
												</div>
											</div>
											
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-10 marg-top-10">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="sourcing.procurement.info" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list">

								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="procurement.info.category" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenProcurementCategory" path="procurementCategory">
											<form:option value="">
												<spring:message code="procurement.info.category.select" />
											</form:option>
											<form:options items="${procurementCategoryList}" itemValue="id" itemLabel="procurementCategories" />
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox id="procurementCategoryVisibleId" path="procurementCategoryVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox id="procurementCategoryReadOnlyId" path="procurementCategoryDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.optional" var="optional" />
										<form:checkbox id="procurementCategoryOptionalId" path="procurementCategoryOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
							</div>
						</div>
						<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-10">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="label.maxscore" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list">
								<div class="row marg-bottom-10">
									<div class="col-md-1 dd sky ${isTemplateUsed ? 'disabled':''} ">
<%-- 										<form:input path="maximumScore" type="text" data-validation="required length" data-validation-length="max64" class="form-control" /> --%>
										<form:input path="maximumScore" value="${maximumScore}" id="maximumScore" name="maximumScore" placeholder="99" class="form-control" data-validation="number required"
										data-validation-allowing="range[1;999]" data-validation-regexp="/^(0|[1-9]\d*)$/" data-validation-error-msg-number="Input value must be numeric within range from 1 to 999" data-validation-error-msg-container="#errorContainer" />
									</div>
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.maxscore" /></label>
									</div>
								</div>
								<div id="errorContainer"></div>
							</div>
						</div>
						<div class="upload_download_wrapper collapseable clearfix event_info Approval-tab  in">
							<h4>
								<spring:message code="template.user" />
							</h4>
							<div id="templateUserTab" data-aproval="" class="pad_all_15 collapse in float-left width-100 position-relative in ${isTemplateUsed? 'disabled':''}">
								<div class="row">
									<div class="col-sm-4 col-md-3">
										<label class="marg-top-10"><spring:message code="assign.template.user" /></label>
									</div>
									<div class="col-md-4 col-sm-6">
										<div class="input-group search_box_gray disp-flex">
											<select class="chosen-select user-list-normal-sp-template" id="selectedUserList" selected-id="data-value">
												<option value=""><spring:message code="placeholder.search.user.templates" /></option>
												<c:forEach items="${assignedUserListDropDown}" var="userList">
													<c:if test="${userList.id == '-1'}">
														<option value="-1" disabled>${userList.name}</option>
													</c:if>
													<c:if test="${userList.id != '-1' }">
														<option value="${userList.id}">${userList.name}</option>
													</c:if>
												</c:forEach>
											</select>
										</div>
										<div class="error templateUserError" hidden>
											<spring:message code="please.select.user" />
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreUsers">
											<i class="fa fa-plus" aria-hidden="true"></i>
										</button>
									</div>
								</div>
								<div class="clear"></div>
								<div class="container-fluid">
									<div class="row">
										<div class="col-xs-12">
											<section class="index_table_block marg-top-20">
												<div class="ph_tabel_wrapper scrolableTable_UserList">
													<table id="tableList1" class="ph_table display table table-bordered noarrow" cellspacing="0">
														<thead>
															<tr>
																<th class=""><spring:message code="application.action1" /></th>
																<th class=""><spring:message code="application.username" /></th>
															</tr>
														</thead>
													</table>
												</div>
											</section>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="btn-next">
							<c:if test="${!(readOnlyPermission or buyerReadOnlyAdmin or isTemplateUsed)}">
								<form:button type="submit" id="saveRfxTemplate" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1">
								${button}
								</form:button>
							</c:if>
							<c:if test="${readOnlyPermission or buyerReadOnlyAdmin or isTemplateUsed}">
								<c:url value="/buyer/supplierPerformanceTemplateCriteriaList/${spTemplate.id}" var="createUrl" />
								<a href="${createUrl}" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1 netxBtn"><spring:message code="application.next" /></a>
							</c:if>
							<spring:message code="application.cancel" var="cancel" />
							<input type="button" id="submitStep1PrDetailDraft" class="btn1 top-marginAdminList step_btn_1 btn btn-black marg-left-10 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 skipvalidation" value="${cancel}" onclick="location.href='spTemplateList';" />
							<input type="hidden" name="userId" id="userId" />
							<c:if test="${not empty spTemplate.id and !(spTemplate.status eq 'DRAFT') and canEditt}">
								<spring:message code="application.saveas" var="saveas" />
								<input type="button" id="saveAsSPTemplate" style="margin-right: 1%;" class="btn1 btn ph_btn step_btn_1 marg-top-20 btn-warning hvr-pop hvr-rectangle-out pull-right submitStep1" value="${saveas}" />
							</c:if>
						</div>
					</form:form>
				</section>	
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="spTemplateSaveAsPopup" title="Template Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="templateId" value="${templateId}" />
		<form>
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<input type="hidden" id="templateId" name="templateId" value="${templateId}" />
					<div class=" col-md-4">
						<label> <spring:message code="rfxTemplate.templateName" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
						<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
						<spring:message code="prtemplate.enter.template" var="tempnameplace" />
						<input data-validation="required length" data-validation-length="max64" class="form-control marg-bottom-10" name="tempName" id="tempName" placeholder="${tempnameplace}" /> <span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label> <spring:message code="rfxTemplate.templateDescription" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="prtemplate.template.description" var="tempdesc" />
						<textarea name="tempDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" id="tempDescription" placeholder="${tempdesc}"></textarea>
						<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" title="" class=" btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsSPTemp" data-original-title="Delete">
						<spring:message code="application.create" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE ACTIVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateactivePopup" title="Template Active">
	<div class="float-left width100 pad_all_15 white-bg">
		<form>
			<div class="marg-top-20 tempaleData"></div>
			<input type="hidden" value="${supplierPerformanceTemplate.status eq 'ACTIVE' ? inactiveLabel : activeLabel }" id="tempTitle">
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<form action="${pageContext.request.contextPath}/buyer/activeSourcingFormTemplate" method="post">
						<input type="hidden" id="templateId" name="templateId" value="${templateId}" /> 
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 						<input type="submit" class="btn ph_btn_midium btn-tooltip hvr-pop  ${supplierPerformanceTemplate.status eq 'ACTIVE' ? 'btn-danger' : 'btn-info hvr-rectangle-out' }"  value="${supplierPerformanceTemplate.status eq 'ACTIVE' ? inactivateLabel : activeLabel }" /> 
						<a class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1"><spring:message code="application.cancel" /></a>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>

<!--Template user Delete  popup-->
<div class="flagvisibility dialogBox" id="removeTemplateUserListPopup" title='<spring:message code="tooltip.remove.template.user" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<!-- <input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListName" name="approverListName" value=""> -->
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="template.confirm.to.remove.userlist" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteUser" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<style>
<!--
.btn1 {
	height: 38px !important;
	min-width: 180px !important;
	font-size: 16px !important;
	line-height: 38px;
}

.marg-left-15 {
	margin-left: 15px;
}

.marg-right-1 {
	margin-right: 1%;
}
-->
</style>
<style>
.radio>span {
	padding: 0 !important;
}

.pl-0 {
	padding-left: 0;
}

.pl-7 {
	padding-left: 7px;
}

.check-wrapper {
	width: 115px;
}

@media ( min-width : 992px) {
	.col-md-2 {
		width: 25% !important;
	}
}

.templateUserError {
	color: #ff5757 !important;
}

.dialogBlockLoaded2 {
	border: 1px solid rgba(0, 0, 0, .2)!;
	-webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
}
</style>
<script>

<c:if test="${readOnlyPermission or buyerReadOnlyAdmin or isTemplateUsed}">
$(window).bind('load', function() {
	var allowedFields = '.wiz-step,#saveRfxTemplate,#previousButton,#selectedUserList,#dashboardLink,#submitStep1PrDetailDraft,.netxBtn,#saveAsSPTemplate';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>

<c:if test="${assignedCount > 0}">
//Remove click event handler on checkbox labels. It affects disabled checkboxes
$('.check-wrapper').on('click', function(e){
	e.preventDefault();
	return false;
});
</c:if>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});

	$(function() {
		$("[data-role=submit]").click(function() {
			$(this).closest("form").submit();
		});
	});

	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 13) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 13 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	$.formUtils.addValidator({
		name : 'validateMax',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#maximumSupplierRating").val()) < parseFloat($("#minimumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Maximum supplier Rating/Grade must be greater than Minimum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	$.formUtils.addValidator({
		name : 'validateMin',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#minimumSupplierRating").val()) > parseFloat($("#maximumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Minimum supplier Rating/Grade must be smaller than Maximum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	
	var userList2=[];
	var userId2=[];
	
	$(document).ready(function() {
					
	$("#addMoreUsers").on('click', function(){
		var selectedName=$("#selectedUserList option:selected").text();
		var selectedId=$("#selectedUserList option:selected").val();
		if(selectedId.length > 0){
			$('.templateUserError').attr('hidden','');
			var isPresentId=userId2.indexOf(selectedId.toString());
			var isPresentName=userList2.indexOf(selectedName.toString());
			if(isPresentId == -1){
				userList2.push(selectedName.toString());
				userId2.push(selectedId.toString().trim());
				var markup = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+selectedId.toString()+" data-value="+selectedId.toString()+" title=<spring:message code='application.remove'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+selectedName.toString()+"</td></tr>"
				//index++;
				$('#tableList1').append(markup);
				$('#userId').val(userId2);
				$('#selectedUserList').find('option[value="' + selectedId + '"]').remove();
				$('#selectedUserList').trigger("chosen:updated");
			}
		}else {
			$('.templateUserError').removeAttr('hidden');
		}
	});

		<c:forEach items="${assignedUserList}" var="user">
			$('#tableList1').append("<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' data-value='${user.id}' title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+'${user.name}'+"</td></tr>");
			var isAvailableUser=userId2.indexOf('${user.id}');
			var isAvailableUserName=userList2.indexOf('${user.name}');
			if(isAvailableUser==-1){
				userId2.push('${user.id}');
				userList2.push('${user.name}');
			}
			$('#userId').val('');
			$('#userId').val(userId2);
		</c:forEach>
		
	$(document).delegate('.deleteUserForTemplate', 'click', function(e) {
			e.preventDefault();
			text2= $(this).parent().next('td').text();
			id2=$(this).data("value");

			$("#removeTemplateUserListPopup").dialog({
				modal : true,
				maxWidth : 400,
				minHeight : 100,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded2"
			});
			$('.ui-widget-overlay').addClass('bg-white opacity-60');
			$('.ui-dialog-title').addClass('title-ellipsis');
			$("#removeTemplateUserListPopup").find('.approverInfoBlock2').find('span:first-child').text("\""+text2+"\"");
			$('.title-ellipsis').text('Remove Template User');
	});
	
	var id2;
	var text2;

	$(document).delegate('#deleteUser', 'click', function(e){
		e.preventDefault();
	    var id=id2;
	    var index = userId2.indexOf(id);
	    if (index !== -1) userId2.splice(index, 1);
	    
	    $('#userId').val(userId2);
	    var text = text2;
	    var index2 = userList2.indexOf(text);
	    if (index2 !== -1) userList2.splice(index2, 1);
	    
	    $('#tableList1 tbody').empty();
	    var i;
	    var html="";
	    for (i = 0; i < userList2.length; i++) {
	    	var userNameFromArray=userList2[i];
	    	var userIdFromArray=userId2[i];
	      html = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+userIdFromArray.toString()+" data-value="+userIdFromArray.toString()+" title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+userNameFromArray.toString()+"</td></tr>";
	      $('#tableList1').append(html);
	    }
	    $("#removeTemplateUserListPopup").dialog('close');
		var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
		$('#selectedUserList').append(newRow);
		updateUserList('', $("#selectedUserList"), 'NORMAL_USER');
	    $('#selectedUserList').trigger("chosen:updated");
	});
	
	$('#saveAsSPTemplate').click(function () {
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		$('.customError').html('');
		$('#tempName').val("");
		$('#tempDescription').val("");
		$("#spTemplateSaveAsPopup").dialog({
			modal: true,
			minWidth: 300,
			width: '50%',
			maxWidth: 400,
			minHeight: 150,
			dialogClass: "",
			show: "fadeIn",
			draggable: true,
			resizable: false,
			dialogClass: "dialogBlockLoaded"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$('#spTemplateSaveAsPopup').find('#tempId').val($('#templateId').val());
		$('.ui-dialog-title').text(templateSaveLabel);
	});
	
	
	$('#saveAsSPTemp').click(function () {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var templateId = $('#templateId').val();
		var templateName = $('#tempName').val();
		var templateDesc = $('#tempDescription').val();

		var ajaxUrl = getContextPath() + '/buyer/copySPTemplate'
		var tempName = $('#tempName').val();
		if (tempName.length > 0 && tempName.length < 64) {
			if (templateDesc.length <= 250) {
				$.ajax({
					url: ajaxUrl,
					data: ({
						templateId: templateId,
						templateName: templateName,
						templateDesc: templateDesc
					}),
					type: "POST",
					beforeSend: function (xhr) {
						xhr.setRequestHeader(header, token);
						$('div[id=idMessageJsp]').html("");
						$('#loading').show();
					},
					success: function (data, textStatus, request) {
						var info = request.getResponseHeader('success');
						$('p[id=idGlobalSuccessMessage]').html(info);
						$('div[id=idGlobalSuccess]').show();
						$("#templateSaveAsPopup").dialog('close');
						window.location.href = getContextPath() + "/buyer/editSPTemplate?id=" + data + "&success=" + info;

					},
					error: function (request, textStatus, errorThrown) {
						console.log("Error :" + request.getResponseHeader('error'));
						$('.customError').html('<font color="red">' + request.getResponseHeader('error') + '</font>');
						$('#loading').hide();
					},
					complete: function () {
						$('#loading').hide();

					}
				});
			}
		}
	});
	
	$('input[type="checkbox"][title="Read Only"], input[type="checkbox"][title="Optional"]').not('.approvalCheck').change(function () {
		$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().removeClass('has-error').find('span.help-block.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().children('input, textarea, select').val());
			console.log("labelVal :" + labelVal);
			if (labelVal == '' && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="help-block form-error">This is a required field</span>').addClass('has-error');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
	});
	
	
	$('input[type="checkbox"][title="Visible"]').change(function(e) {
		console.log('Visible checkbox...', $(this).prop('checked')); 
		if ($(this).prop('checked') == false) {
			$(this).closest('.check-wrapper').siblings('.check-wrapper').find('input[type="checkbox"]').prop('checked', false);
			$.uniform.update();
			$(this).closest('.check-wrapper').prev().find('input, textarea').val('');
			$('#chosenProcurementCategory').val('').trigger("chosen:updated");
			//$(this).closest('.check-wrapper').prev().find('.chosen-select').trigger("chosen:updated")
		}
	});	
	
	
	$('select').change(function() {
		$(this).parent().removeClass('has-error').find('span.help-block.form-error')
		// .children removed because validation message wasn't getting removed even after selcting value
		/* .children('span.help-block.form-error') */
		.remove();
	});
	
	$('.check-wrapper.first').prev().find('select').chosen().change(function() {
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

});
	
	
	$('#saveRfxTemplate').click(function(){
		if($('#sourcetemplate').isValid()) {
			$('#loading').show();
		}
	});
</script>
