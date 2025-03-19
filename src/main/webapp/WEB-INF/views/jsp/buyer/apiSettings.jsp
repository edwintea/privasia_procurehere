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
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
				<li class="active"><spring:message code="api.setup.label" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head"><spring:message code="api.setup.label" /></h2>
				<div class="right-header-button"></div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<c:url var="apiSetupUrl" value="/buyer/apiSettings" />
			<form:form action="${apiSetupUrl}" id="api-form" method="post" modelAttribute="buyerSettings" autocomplete="off">
				<form:hidden path="id" />
				<div class="tab-pane active">
					<div class="tab-content Invited-Supplier-List ">
						<div class="tab-pane active white-bg" id="step-1">
							<div class="upload_download_wrapper clearfix mar-t20 event_info">
								<%-- <div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:checkbox path="isApproved" class="custom-checkbox" title="Approved" />
											<label style="line-height: 0px;">Approved</label>
										</div>
									</div>
								</div>
								<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:checkbox path="isOrigin" class="custom-checkbox" title="Origin" />
											<label style="line-height: 0px;">Origin</label>
										</div>
									</div>
								</div>

								<div class="row marg-top-20">
									<div class="form-tander1">
										<div class="col-sm-3 col-md-2 col-xs-6"></div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:checkbox path="isPublished" class="custom-checkbox" title="Published" />
											<label style="line-height: 0px;">Published</label>
										</div>
									</div>
								</div> --%>
								<div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label><spring:message code="erp.integration.appid" /></label>
										</div>

										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6 ">
											<%-- 		<form:input type="text" path="appId" id="appId" placeholder="" class="rec_inp_style1" readonly="true" /> --%>

											<div class="input-group">
												<form:input type="text" path="buyerKey" id="buyerKey" placeholder="Generate App Id" class="form-control rec_inp_style1" readonly="true" data-validation="required"/>
												<div class="input-group-btn">
													<button id="copyKey" class="btn btn-default" type="button" data-toggle="tooltip" data-placement="bottom" title="Copy">
														<i class="glyphicon  glyphicon-copy"></i>
													</button>
												</div>
											</div>
										</div>
										<div class="col-sm-3 col-md-3 col-xs-6 col-xs-6">
											<button type="button" class="btn btn-success" id="generateKey"><spring:message code="erp.generate.key.btn" /></button>
										</div>
									</div>
								</div>
								<div class="row marg-top-20">
									<div class="form-tander1 pad_left_15">
										<div class="col-sm-3 col-md-2 col-xs-6">
											<label></label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<form:button type="submit" id="saveApi" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10"><spring:message code="application.save" /></form:button>
											<a href="${pageContext.request.contextPath}/buyer/buyerDashboard" id="cancelButton" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
											</a>
										</div>
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
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
	$("#generateKey").click(function() {

		$.ajax({
			url : getContextPath() + '/buyer/keyGenerateForBuyer',
			type : "POST",
			beforeSend : function() {

				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$("#buyerKey").val(data);
				console.log("---------" + data);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	$('#copyKey').click(function() {
		var clipboardText = "";

		clipboardText = $('#buyerKey').val();

		copyToClipboard(clipboardText);

	});
	function copyToClipboard(text) {

		var textArea = document.createElement("textarea");
		textArea.value = text;
		document.body.appendChild(textArea);

		textArea.select();

		try {
			var successful = document.execCommand('copy');
			var msg = successful ? 'successful' : 'unsuccessful';
			console.log('Copying text command was ' + msg);
		} catch (err) {
			console.log('Oops, unable to copy');
		}

		document.body.removeChild(textArea);
	}
</script>
<script>
	$(document).ready(function() {
		$('[data-toggle="tooltip"]').tooltip();
	});

	
</script>