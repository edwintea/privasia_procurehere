<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

#page-content-wrapper {
	min-height: 1200px;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.marLeft {
	margin-left: 45px;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a></li>
				<li class="active"><spring:message code="erp.integration.erpsetup"/></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head"><spring:message code="erp.integration.erpsetup"/></h2>
				<div class="right-header-button"></div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<c:url var="erpSetupUrl" value="/buyer/erpSetup" />
			<form:form action="${erpSetupUrl}" id="erp-form" method="post" modelAttribute="erpSetup" autocomplete="off">
				<form:hidden path="id" />
				<div class="tab-pane active">
					<div class="tab-content Invited-Supplier-List ">
						<div class="tab-pane active white-bg" id="step-1">
							<div class="upload_download_wrapper clearfix mar-t20 event_info">
								<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"><label style="line-height: 0px;"><spring:message code="erp.integration.enable"/></label></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<spring:message code="tooltip.erp.enable.integration" var="erpenabletip"/>
											<form:checkbox path="isErpEnable" class="custom-checkbox" title="${erpenabletip}" />
										</div>
									</div>
								</div>
								<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"><label style="line-height: 0px;"><spring:message code="erp.integration.generate.po"/></label></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<spring:message code="tooltip.erp.generate.po" var="erppo"/>
											<form:checkbox path="isGeneratePo" class="custom-checkbox" title="${erppo}" />
											
										</div>
									</div>
								</div>

								<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"><label style="line-height: 0px;">Enable Pull Interface</label></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:checkbox path="awardInterfaceTypePull" class="custom-checkbox" title="Check this if ERP is going to pull the award details by calling Procurehere API instead of Procurehere pushing the award details by invoking ERP APIs" />
										</div>
									</div>
								</div>

								<%-- 	<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:checkbox path="isErpEnable" class="custom-checkbox" title="Procurehere PR TO SAP PR" />
											<label style="line-height: 0px;">Procurehere PR TO SAP PR</label>
										</div>
									</div>
								</div> --%>


								<div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label><spring:message code="erp.integration.agent.url"/></label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:input type="text" path="erpUrl" placeholder="" class="rec_inp_style1" title="URL of the Procurehere Agent in case in direct integration to ERP is not possible over internet" />
										</div>
									</div>
								</div>

								<div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label><spring:message code="erp.integration.appid"/></label> 
										</div>

										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6 ">
											<%-- 		<form:input type="text" path="appId" id="appId" placeholder="" class="rec_inp_style1" readonly="true" /> --%>

											<div class="input-group">
												<spring:message code="erp.generate.appid.placeholder" var="appid"/>
												<form:input type="text" path="appId" id="appId" placeholder="${appid}" class="form-control rec_inp_style1"  title="This security key will be used by ERP to invoke Procurehere Integration APIs" readonly="true" />
												<div class="input-group-btn">
													<button id="copyKey" class="btn btn-default" type="button" data-toggle="tooltip" data-placement="bottom" title='<spring:message code="tooltip.copy"/>' >
														<i class="glyphicon  glyphicon-copy"></i>
													</button>
												</div>
											</div>
										</div>
										<div class="col-sm-3 col-md-3 col-xs-6 col-xs-6">
											<button type="button" class="btn btn-success" id="generateKey" title="Click to generate a new integration security key. This key will be used by ERP to invoke Procurehere Integration APIs" ><spring:message code="erp.generate.key.btn"/></button>
										</div>
									</div>
								</div>

								<%-- <div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label>Create Events</label>
										</div>
										<div class="radio-info col-sm-5 col-md-1 col-xs-6 col-xs-6">
											<label class="select-radio-lineHgt">
												<form:radiobutton path="createEventAuto" class="custom-radio autoCreation" value="true" label="Auto" title="Auto Create Events" />
											</label>
										</div>
										<div class="radio-info col-sm-5 col-md-3 col-xs-6 col-xs-6">
											<label class="select-radio-lineHgt">
												<form:radiobutton path="createEventAuto" class="custom-radio manualCreation" value="false" label="Manual" title="Manual Create Events" />
											</label>
										</div>
									</div>
								</div> --%>
								<div id="autoCreationDiv">
									<div class="row marg-top-20">
										<div class="form-tander1 pad_left_15">
											<div class="col-sm-3 col-md-2 col-xs-6">
												<label><spring:message code="erp.default.event.type"/></label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:select path="type" cssClass="form-control chosen-select disablesearch" id="idType" title="Choose the default Event Type to be created when ERP sends in a create request." >
													<form:options items="${eventTypeList}" itemLabel="value" />
												</form:select>
											</div>
										</div>
									</div>
									<div class="row marg-top-20">
										<div class="form-tander1 pad_left_15">
											<div class="col-sm-3 col-md-2 col-xs-6">
												<label><spring:message code="erp.default.template"/></label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<form:select path="rfxTemplate" cssClass="form-control chosen-select disablesearch" id="idRfxTemplate" title="Choose the default Template to be used during event creation when ERP sends in a create request." >
													<%-- <form:options items="${rfxTemplateList}" itemValue="id" itemLabel="templateName" data-auctionType="" /> --%>
													<form:option value="">
													<spring:message code="erp.select.default.template"/> 
												</form:option>
													<c:forEach items="${rfxTemplateList}" var="template">
														<form:option value="${template.id}" data-auctionType="${template.templateAuctionType}">${template.templateName}</form:option>
													</c:forEach>
												</form:select>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6" id="auctionType">
												<c:if test="${erpSetup.type eq 'RFA' and not empty erpSetup.rfxTemplate }">
													<label><spring:message code="rfx.auction.type.label"/> : </label>
													<span id="auctionTypeValue"></span>
												</c:if>
											</div>
										</div>
									</div>
								</div>
								<%-- 	<div id="manualCreationDiv">
									<div class="ph_table_border margin">
								<div class="mega range-header">
									<table class="header ph_table border-none" width="100%">
										<thead>
											<tr>
												<th class="width_200 width_200_fix align-center">Date</th>
												<th class="width_200 width_200_fix align-left">Reference No.</th>
												<th class="width_150 width_150_fix align-left">Action</th>
											</tr>
										</thead>
									</table>
									<table class="data ph_table border-none" width="100%">
												<tbody>
													<c:forEach items="${erpEventAuditList}" var="eventAudit">
														<tr>
															<td class="width_200 width_200_fix align-center">
																<fmt:formatDate value="${eventAudit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" />
																
															</td>
															<td class="width_200 width_200_fix align-left">${eventAudit.prNo}</td>
															<td class="width_150 width_150_fix align-left"><a href="${pageContext.request.contextPath}/buyer/viewErpEventDetail/${eventAudit.id}" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font" >View</a></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
								</div>
							</div>
								</div> --%>
								
								
								<div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label></label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:button type="submit" id="saveErp" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10"><spring:message code="application.save"/></form:button>
										<a href="${pageContext.request.contextPath}/buyer/buyerDashboard" id="cancelButton"
											class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
										</a>
										</div>
									</div>
								</div>
								
								
								
							</div>
						</div>
					</div>
			</form:form>

		</div>
	</div>
</div>
<style>
.tab-pane {
	padding: 20px !important;
}

.select-radio-lineHgt {
	line-height: 5px !important;
}

.radio {
	margin-top: 13px !important;
}

.hideDiv {
	display: none;
}

.btn-font {
	color: white !important;
	font-weight: 600;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind(
			'load',
			function() {
				var allowedFields = '.button-previous, #dashboardLink';
				//var disableAnchers = ['#reloadMsg'];        
				disableFormFields(allowedFields);
				$('#page-content').find('select').not(allowedFields).parent(
						'div').addClass('disabled');
			});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});

	$(document).delegate('.manualCreation', 'click', function(e) {
		$('#autoCreationDiv').addClass('hideDiv');
		$('#manualCreationDiv').removeClass('hideDiv');
	});

	$(document).delegate('.autoCreation', 'click', function(e) {
		$('#manualCreationDiv').addClass('hideDiv');
		$('#autoCreationDiv').removeClass('hideDiv');
	});

	$(document)
			.ready(
					function() {
						//var autoCreation = $('input[name=createEventAuto]:checked').val();
						var autoCreation = 'true';
						console.log("autoCreation :" + autoCreation);
						if (autoCreation == 'true') {
							$('#manualCreationDiv').addClass('hideDiv');
							$('#autoCreationDiv').removeClass('hideDiv');
						} else {
							$('#autoCreationDiv').addClass('hideDiv');
							$('#manualCreationDiv').removeClass('hideDiv');
						}

						$('#auctionTypeValue')
								.text(
										getAuctionType('${erpSetup.rfxTemplate.templateAuctionType}'));

					});

	$(document)
			.delegate(
					'#idType',
					'change',
					function(e) {
						var header = $("meta[name='_csrf_header']").attr(
								"content");
						var token = $("meta[name='_csrf']").attr("content");
						var eventType = $('#idType option:selected').text();
						$
								.ajax({
									url : getContextPath()
											+ '/buyer/getRfxTemplates/'
											+ eventType,
									type : "GET",
									beforeSend : function(xhr) {
										xhr.setRequestHeader(header, token);
										xhr.setRequestHeader("Accept",
												"application/json");
										xhr.setRequestHeader("Content-Type",
												"application/json");
										$('#loading').show();
									},
									success : function(data, textStatus,
											request) {

										var templateList = '<option value="" >Select default Template </option>';
										var templateAuctionType = '';
										var type = '';
										$
												.each(
														data,
														function(i, item) {
															if (i == 0) {
																templateAuctionType = item.templateAuctionType;
																type = item.type;
															}
															templateList += '<option value="'
																	+ item.id
																	+ '"  data-auctionType="'
																	+ getAuctionType(item.templateAuctionType)
																	+ '" >'
																	+ item.templateName
																	+ '</option>';
														});
										$('#idRfxTemplate').html(templateList)
												.trigger("chosen:updated");

										if (type == 'RFA'
												&& $(
														'#idRfxTemplate option:selected')
														.val() != '') {
											var html = '<label>Auction Type : </label> <span id="auctionTypeValue">'
													+ getAuctionType(templateAuctionType)
													+ '</span>';
											$('#auctionType').html(html);
											$('#auctionType').removeClass(
													'hideDiv');
										} else {
											$('#auctionType').addClass(
													'hideDiv');
										}
									},
									complete : function() {
										$('#loading').hide();
									}
								});

					});

	function getAuctionType(auctionType) {
		var result = '';
		if (auctionType == 'FORWARD_ENGISH') {
			result = 'Forward English Auction';
		} else if (auctionType == 'REVERSE_ENGISH') {
			result = 'Reverse English Auction';
		} else if (auctionType == 'FORWARD_SEALED_BID') {
			result = 'Forward Sealed Bid';
		} else if (auctionType == 'REVERSE_SEALED_BID') {
			result = 'Reverse Sealed Bid';
		} else if (auctionType == 'FORWARD_DUTCH') {
			result = 'Forward Dutch Auction';
		} else if (auctionType == 'REVERSE_DUTCH') {
			result = 'Reverse Dutch Auction';
		}
		return result;
	}

	$(document).delegate(
			'#idRfxTemplate',
			'change',
			function(e) {
				console.log($('#idRfxTemplate option:selected').attr(
						'data-auctionType'));
				$('#auctionTypeValue').text(
						$('#idRfxTemplate option:selected').attr(
								'data-auctionType'));
			});

	$("#generateKey").click(function() {

		$.ajax({
			url : getContextPath() + '/buyer/erpAppKeyGenrate',
			type : "POST",
			beforeSend : function() {

				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$("#appId").val(data);
				console.log("---------" + data);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	
	
	
	$( '#copyKey' ).click( function()
			 {
			     var clipboardText = "";

			     clipboardText = $( '#appId' ).val();

			     copyToClipboard( clipboardText );
			  
			 });
	
	
	
	function copyToClipboard(text) {

		   var textArea = document.createElement( "textarea" );
		   textArea.value = text;
		   document.body.appendChild( textArea );

		   textArea.select();

		   try {
		      var successful = document.execCommand( 'copy' );
		      var msg = successful ? 'successful' : 'unsuccessful';
		      console.log('Copying text command was ' + msg);
		   } catch (err) {
		      console.log('Oops, unable to copy');
		   }

		   document.body.removeChild( textArea );
		}
</script>
<script>
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();   
});
</script>