
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
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
</style>
<div id="page-content" view-name="uom">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/buyer/idSettingsList" var="listUrl" />
			<li>
				<a id="listLink" href="${listUrl}"> <spring:message code="systemsetting.id.setting.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="systemsetting.id.setting" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="systemsetting.id.setting.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="systemsetting.id.setting" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="idSettingsUrl" value="/buyer/idSettings" />
				<form:form id="idSettingForm" cssClass="form-horizontal bordered-row" method="post" action="${idSettingsUrl}" modelAttribute="idSettings">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idType" cssClass="marg-top-10">
								<spring:message code="label.idtype" />
							</form:label>
						</div>
						<form:hidden path="id" />
						<div class="col-md-5 ${not empty idSettings.id ? 'disabled' : ''}">
							<spring:message code="setId.type" var="place" />
							<spring:message code="setId.idType.required" var="required" />
							<spring:message code="setId.idType.length" var="length" />

							<form:select path="idType" value="${idSettings.idType}" class="chosen-select disablesearch" id="chosenOrder">
								<form:option value="PR">PR</form:option>
								<form:option value="RFT">RFT</form:option>
								<form:option value="RFP">RFP</form:option>
								<form:option value="RFQ">RFQ</form:option>
								<form:option value="RFI">RFI</form:option>
								<form:option value="RFA">RFA</form:option>
								<form:option value="SR">SR</form:option>
								<form:option value="BG">BG</form:option>
								<form:option value="PO">PO</form:option>
								<form:option value="GRN">GRN</form:option>
								<form:option value="CTR">CTR</form:option>
								<form:option value="SP">SP</form:option>
							</form:select>
							<form:errors path="idType" cssClass="error" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idType" cssClass="marg-top-10">
								<spring:message code="systemsetting.id.setting.runningnumber" />
							</form:label>
						</div>
						<div class="col-md-5 ">
							<spring:message code="setId.type" var="place" />
							<spring:message code="setId.idType.required" var="required" />
							<spring:message code="setId.idType.length" var="length" />

							<form:select path="idSettingType" value="${idSettings.idSettingType}" class="chosen-select disablesearch" id="idSettingType">

								<form:options items="${idSettingType}" itemLabel="value"></form:options>n>
							</form:select>
							<form:errors path="idSettingType" cssClass="error" />
						</div>
					</div>


					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idType" cssClass="marg-top-10">
								<spring:message code="systemsetting.id.setting.pattern" />
							</form:label>
						</div>
						<div class="col-md-5 idSettingPattern">
							<spring:message code="setId.type" var="place" />
							<spring:message code="setId.idType.required" var="required" />
							<spring:message code="setId.idType.length" var="length" />

							<form:select path="idSettingPattern" value="${idSettings.idSettingPattern.value}" class="chosen-select disablesearch" id="idSettingPattern">

								<form:options items="${idSettingPattern}" itemLabel="value"></form:options>
							</form:select>

							<form:errors path="idSettingPattern" cssClass="error" />
						</div>
					</div>


					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="paddingLength" cssClass="marg-top-10">
								<spring:message code="systemsetting.id.setting.runninglength" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="setId.paddingLength" var="place" />
							<spring:message code="setId.paddingLength.required" var="required" />
							<spring:message code="setId.paddingLength.length" var="length" />
							<spring:message code="idsetting.running.number.length" var="runningno" />
							<spring:message code="tooltip.running.number.length" var="runninglength" />
							<form:input path="paddingLength" value="${idSettings.paddingLength}" data-validation="required number" data-validation-allowing="range[1;9]" data-validation-error-msg="The input value must be between 1 and 9" cssClass="form-control " id="paddingLength" placeholder="${runningno}" data-toggle="tooltip" data-placement="bottom" title="${runninglength}" />
							<form:errors path="paddingLength" cssClass="error" />
						</div>
						<div class="col-md-2" style="margin-left: -23px; margin-top: 9px;">
							<spring:message code="systemsetting.id.setting.runninglength.eg" />
						</div>
					</div>






					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idPerfix" cssClass="marg-top-10">
								<spring:message code="label.idprefix" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="setId.idPrefix" var="place" />
							<spring:message code="setId.idPrefix.required" var="required" />
							<spring:message code="setId.idPrefix.length" var="length" />
							<spring:message code="idsetting.enter.prefix" var="prefix" />
							<form:input path="idPerfix" value="${idSettings.idPerfix}" data-validation="required length alphanumeric" data-validation-length="1-5" data-validation-allowing="-_/ ." cssClass="form-control " id="idPerfix" placeholder="${prefix}" />
							<form:errors path="idPerfix" cssClass="error" />
						</div>
						<div class="col-md-2" style="margin-left: -23px; margin-top: 9px;">
							<spring:message code="systemsetting.id.setting.prefix.eg" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idDelimiter" cssClass="marg-top-10">
								<spring:message code="systemsetting.id.delimiter" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="setId.idDelimeter" var="place" />
							<spring:message code="setId.idDelimeter.required" var="required" />
							<spring:message code="setId.idDelimeter.length" var="length" />
							<spring:message code="idsetting.enter.delimeter" var="delimeterplace" />
							<form:input path="idDelimiter" value="${idSettings.idDelimiter}" data-validation=" length alphanumeric" data-validation-optional="true" data-validation-length="1-2" data-validation-allowing="-_/ ." cssClass="form-control " id="idDelimiter" placeholder="${delimeterplace}" />
							<form:errors path="idDelimiter" cssClass="error" />
						</div>
						<div class="col-md-2" style="margin-left: -23px; margin-top: 8px;">
							<spring:message code="systemsetting.id.delimiter.eg" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="idDatePattern" cssClass="marg-top-10">
								<spring:message code="systemsetting.date.pattern" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="setId.idDatepattern" var="place" />
							<spring:message code="setId.idDatepattern.required" var="required" />
							<spring:message code="setId.idDatepattern.length" var="length" />

							<div class="input-group">
								<spring:message code="idsetting.enter.date.pattern" var="datepattern" />
								<form:input path="idDatePattern" value="${idSettings.idDatePattern}" data-validation-length="1-64" data-validation-allowing="-_/ ." cssClass="form-control " id="idDatePattern" placeholder="${datepattern}" />
								<div class="input-group-btn">

									<button id="showHelp" class="btn btn-default" type="button" data-toggle="tooltip" data-placement="bottom" title='<spring:message code="systemsetting.date.pattern.help" />'>
										<i class="glyphicon glyphicon-question-sign"></i>
									</button>
								</div>
							</div>


							<%-- <form:input path="idDatePattern"  data-validation="required"
														data-validation-format="dd/mm/yyyy - dd/mm/yyyy  P" data-validation-allowing="-_/ ." cssClass="form-control " id="idDatePattern" placeholder="${place}"></form:input> --%>
							<form:errors path="idDatePattern" cssClass="error" />
						</div>
						<div class="col-md-2" style="margin-left: -23px; margin-top: 9px;">
							<spring:message code="systemsetting.id.setting.date" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label><spring:message code="systemsetting.id.setting.previewid" /></label>
						</div>
						<div class="col-md-5">
							<div class="input-group">
								<input type="text" id="idPreView" class="form-control rec_inp_style1" readonly="readonly" />
								<div class="input-group-btn">
									<button id="showPreView" class="btn btn-default" type="button" data-toggle="tooltip" data-placement="bottom" title='<spring:message code="systemsetting.id.setting.previewid" />'>
										<i class="glyphicon glyphicon-eye-open"></i>
									</button>
								</div>
							</div>

							<!-- <input type="text" id="idPreView" class="form-control " readonly="readonly" /> -->
						</div>

					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label><spring:message code="systemsetting.id.setting.enable.suffix" /></label>
						</div>
						<div class="col-md-5">
							<div class="checkbox checkbox-info">
								<label> <form:checkbox id="idEnableSuffix"  checked="${checked}" path="enableSuffix" class="custom-checkbox" />
								</label>
							</div>
						</div>
					</div>

					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<%-- <sec:authorize access="hasRole('UOM_EDIT') or hasRole('ADMIN')" var="canEdit" /> 
							<form:button type="submit" value="save" id="saveIdSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" disabled="${!canEdit}">${btnValue}</form:button> --%>
							<form:button type="submit" value="save" id="saveIdSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">${btnValue}</form:button>
							<c:url value="/buyer/idSettingsList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" id="myModal-help" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content" style="width: 100%; float: left;">
			<div class="modal-header">
				<h3>Help</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>

			<div class="modal-body">

				<table class="table">
					<tr>
						<th>pattern</th>

						<th>description</th>

						<th>Example</th>
					<tr>
						<td>y</td>
						<td>Year</td>
						<td>yyyy => 2018 / yy => 18</td>

					</tr>


					<tr>
						<td>M</td>
						<td>Month in year</td>
						<td>MM => 07</td>

					</tr>


					<tr>
						<td>m</td>
						<td>Minute in hour</td>
						<td>mm => 30</td>

					</tr>


					<tr>
						<td>D</td>
						<td>Day in year</td>
						<td>DD => 189</td>

					</tr>


					<tr>
						<td>d</td>
						<td>Day in month</td>
						<td>dd => 08</td>

					</tr>

				</table>


			</div>
			<div class="modal-footer border-none float-left width-100 pad-top-0 "></div>
		</div>
	</div>
</div>


<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	/* <c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if> */

	$(document).ready(function(){
		if($('#idSettingType').val()=="COMPANY_LEVEL"){
			
			$(".idSettingPattern").addClass("disabled");
		}
		
		$('#showHelp').click(function(){
			
		$('#myModal-help').modal();
		});
		 $('#showPreView').click(function(){
		        var type=$('#chosenOrder').val();
		        var basedOn=$('#idSettingType').val();
		        var length=$('#paddingLength').val();
		        var pattern=$('#idSettingPattern').val();
		        var preFix=$('#idPerfix').val();
		        var delimiter=$('#idDelimiter').val();
		        var datePattern=$('#idDatePattern').val();
		        
		        if (!$('#idSettingForm').isValid()) {
					return false;
				}
		        
		        $.ajax({
					type : "POST",
					url : getContextPath() + "/buyer/showPreView",
					data: { 
					      type: type, 
					      basedOn: basedOn,
					      length: length ,
					      pattern: pattern ,
					      preFix: preFix ,
					      delimiter: delimiter,
					      datePattern: datePattern
					  },
					beforeSend : function(xhr) {
						$('#loading').show();
					
					},
					complete : function() {
						$('#loading').hide();
					},
					success : function(data, textStatus, request) {
						/* showMessage('SUCCESS', request.getResponseHeader('success'));
						$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
						$('div[id=idGlobalSuccess]').show(); */
		        		$('#idPreView').val(data);
						
					},
					error : function(request, textStatus, errorThrown) {
					console.log("error");
						//	showMessage('ERROR', request.getResponseHeader('error'));
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						// alert('Error: ' + request.getResponseHeader('error'));
					}
				});
		        
		    });
		
		
	$('#idSettingType').on('change', function() {
		var value=this.value;
			
		if(value=="BUSINESS_UNIT"){
			$(".idSettingPattern").removeClass("disabled");
			$.ajax({
				type : "POST",
				url : getContextPath() + "/buyer/checkUnitCodeEmpty",
				beforeSend : function(xhr) {
					$('#loading').show();
				
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data, textStatus, request) {
					/* showMessage('SUCCESS', request.getResponseHeader('success'));
					$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
					$('div[id=idGlobalSuccess]').show(); */
					console.log("success");
				},
				error : function(request, textStatus, errorThrown) {
				console.log("error");
					//	showMessage('ERROR', request.getResponseHeader('error'));
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					// alert('Error: ' + request.getResponseHeader('error'));
				}
			});
		}
		else if(value=="COMPANY_LEVEL")
		{
			
			$("#idSettingPattern").val("STANDARD").trigger("chosen:updated");
			$(".idSettingPattern").addClass("disabled");
			
		}
		
		
		
		
		});
		
	
	
	});
		
	
	
	$.validate({
		lang : 'en'
	});
</script>