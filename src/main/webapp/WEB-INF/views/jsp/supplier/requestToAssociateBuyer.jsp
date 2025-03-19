<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css" />">
<style>
	.ph_btn_custom {
		height: 40px !important;
		min-width: 170px !important;
		font-size: 16px !important;
		line-height: 39px;
		font-weight: 500;
	}

	.color-red {
		color: #ff5757;
	}
</style>
<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="supplierDashboard" value="/supplier/supplierDashboard" />
				<li><a href="${supplierDashboard}">
						<spring:message code="application.dashboard" />
					</a></li>
				<li class="active">
						Associate Request
				</li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="supplier.view.request.buyerLabel" /> ${requestAssociteBuyerObj.buyerCompanyName}
				</h2>
			</div>
			<div class="clear"></div>
			<div class="white-bg border-all-side float-left width-100 pad_all_15">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<form:form class="bordered-row" id="requestForm" method="post" modelAttribute="requestAssociteBuyerObj" action="${pageContext.request.contextPath}/supplier/sentRequestToAssociateBuyer">
					<form:hidden id="requestBuyerId" path="buyerId" value="${requestAssociteBuyerObj.buyerId}" />
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.email" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${requestAssociteBuyerObj.communicationEmail}</p>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.contactNo" /></label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${requestAssociteBuyerObj.contactNumber}</p>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.contactPerson" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${requestAssociteBuyerObj.contactPerson}</p>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.website" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<a href="http://${requestAssociteBuyerObj.website}" target="_blank" id="idWebsite">${requestAssociteBuyerObj.website}</a>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.infoSupp" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${requestAssociteBuyerObj.infoToSupplier}</p>
						</div>
					</div>
					<input type="hidden" id="isAllowCategoriesId" value="${requestAssociteBuyerObj.isAllowCategories}">
					<c:if test="${requestAssociteBuyerObj.isAllowCategories}">
						<div class="row marg-bottom-20">
							<div class="col-sm-4 col-md-3 col-xs-6">
								<label>
									<spring:message code="supplier.view.request.selectCategories" />
								</label>
								<div>(Min:${requestAssociteBuyerObj.minimumCategories} Max:${requestAssociteBuyerObj.maximumCategories})</div>
								<input type="hidden" value="${requestAssociteBuyerObj.minimumCategories}" id="minCat">
								<input type="hidden" value="${requestAssociteBuyerObj.maximumCategories}" id="maxCat">
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<input type="text" id="demo-input-local" name="blah" data-validation="required" />
								<div class="col-md-12 selectListAjax"></div>
								<div id="catValErr"></div>
							</div>
							<div class="col-sm-4 col-md-3 col-xs-6">
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<div style="color: #4689cb !important;"><spring:message code="supplier.view.request.selectCategories.suggestion" /></div>
							</div>
						</div>
					</c:if>
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label>
								<spring:message code="supplier.view.request.remark" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<form:textarea path="supplierRemark" class="form-control" maxlength="500"
								data-validation="required length" data-validation-length="max500" id="remarksId" />
						</div>
					</div>
				
				</form:form>
				
				<c:if test="${supplierForm.id ne null and supplierForm.isOnboardingForm}">
					<div class="panel">
					  <div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" class="collapsed" data-parent="#accordion" href="#collapseForm"><spring:message code="defultMenu.suppliers.qualification.form" /> </a>
							</h4>
						</div>
						<div id="collapseForm" class="panel-collapse collapse">
						<div class="panel-body">
						   <jsp:include page="supplierFormOnboardDetails.jsp"></jsp:include>  
						</div>
					  </div>
					</div>
				
					<div class="panel sum-accord" style="margin-top:-14px">
					 <jsp:include page="supplierFormSubmissionAudit.jsp"></jsp:include> 
					 </div>
		    	</c:if>
				<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<button type="submit" value="request" id="sentRequest"
								class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" form="requestForm">Request</button>
							<a href="${pageContext.request.contextPath}/supplier/associateBuyerList"
								class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous" id="cancelRequest">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>"/>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script>
	$.validate({
		lang: 'en',
	});
	
	$.formUtils.addValidator({ name : 'required_checkbox', validatorFunction : function(value, $el, config, language, $form) {
		console.log('Required Checkbox group : ', $el.attr('data-group') , ' Total Checked : ', $('.' + $el.attr('data-group') + '-class :checked').length);
		var cbxGroup = $el.attr('data-group') + '-class';
		var totalChecked = $('input:checkbox.'+cbxGroup + ':checked').length;
		
		return (totalChecked > 0);
		
	}, errorMessage : 'Select at least one option', errorMessageKey : 'err_required_checkbox' });

	if('${supplierForm.status}' == 'PENDING'){
		$('#sentRequest').addClass('disabled');
	}
	else{
		$('#sentRequest').removeClass('disabled');
	}
</script>
<script type="text/javascript">
	$(document).ready(function () {
		$("#demo-input-local").tokenInput("searchIndustryCategories/" + $('#requestBuyerId').val(), {
			minChars: 1,
			method: 'GET',
			hintText: "Start typing to search industry categories...",
			noResultsText: "No results",
			searchingText: "Searching...",
			queryParam: "search",
			propertyToSearch: "name",
			propertyToSearchCode: "code",
			preventDuplicates: true,
			minChars: 3,
			prePopulate: ''
		});
	});

	$("#requestForm").submit(function (eventObj) {
		if($('#isAllowCategoriesId').val() === 'true'){
		var indCat = [];
		select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
		select.each(function () {
			indCat.push($(this).find('input').val());
		});
		var minValue = $("#minCat").val();
		var maxValue = $("#maxCat").val();
		maxValidation(maxValue, minValue, eventObj);
		if (minValue != '' && indCat.length < minValue) {
			eventObj.preventDefault();
			return;
		}

		if (maxValue != '' && indCat.length > maxValue) {
			eventObj.preventDefault();
			return;
		}
		$("<input />").attr("type", "hidden")
			.attr("name", "indCat")
			.attr("value", indCat)
			.appendTo("#requestForm");
		}
	});

	$('#demo-input-local').on('change', function (eventObj) {
		$('.chosen-search-input').attr('placeholder', '');
		var minValue = $("#minCat").val();
		var maxValue = $("#maxCat").val();
		maxValidation(maxValue, minValue, eventObj);
	});

	function maxValidation(maxValue, minValue, eventObj) {
		var indCat = [];
		select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
		select.each(function () {
			indCat.push($(this).find('input').val());
		});
		if (maxValue != '' && indCat.length > Number(maxValue)) {
			// eventObj.preventDefault();
			var errorMessage = 'Please select maximum of ' + maxValue + ' industry categories';
			$('#catValErr').html('<span class="help-block form-error color-red">'+errorMessage+'</span>');
			$("#token-input-demo-input-local").closest(".token-input-list").css({
				'border-color': '#a94442'
			});
			return;
		} else if (minValue != '' && indCat.length < Number(minValue)) {
			// eventObj.preventDefault();
			var errorMessage = 'Please select minimum of ' + minValue + ' industry categories';
			$('#catValErr').html('<span class="help-block form-error color-red">'+errorMessage+'</span>');
			$("#token-input-demo-input-local").closest(".token-input-list").css({
				'border-color': '#a94442'
			});
			return;
		} else {
			$("#token-input-demo-input-local").closest(".token-input-list").css({
				'border-color': '#dfe8f1'
			});
			$('#catValErr').html('');
		}
	}

	$('#sentRequest').click(function (eventObj) {
		var minValue = $("#minCat").val();
		var maxValue = $("#maxCat").val();
		if($('#isAllowCategoriesId').val() === 'true'){
			maxValidation(maxValue, minValue, eventObj);
		}
	})
	$(document).on('click', '#idWebsite', function(e){ 
	    e.preventDefault(); 
	    var url = $(this).attr('href'); 
	 	// Search the pattern 
	    if (url && !url.match(/^http([s]?):\/\/.*/)) { 
	          
	        // If not exist then add http 
	        url = "http://" + url; 
	    } 
	    window.open(url, '_blank');
	});
</script>