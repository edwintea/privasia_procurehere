<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="transferOwnershipDesk" code="application.buyer.transfer.ownership" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${transferOwnershipDesk}] });
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
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a id="dashboardLink" href="${dashboardUrl}">
					<spring:message code="application.dashboard" />
				</a></li>
			<li><spring:message code="buyer.setting.transfer.ownership" /> </a></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="buyer.setting.transfer.ownership" /></h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3><spring:message code="buyer.setting.transfer.ownership" /></h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<!-- <header class="form_header"> </header> -->
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<form id="saveTransferOwnership" method="post" action="${pageContext.request.contextPath}/buyer/saveTransferOwnership">
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="buyer.setting.from.user" /></label>
						</div>
						<div class="col-md-5">
							<select id="FromUserList" class="chosen-select" name="fromUser" cssClass="form-control chosen-container chosen-container-single" data-validation="required duplicate_check" data-validation-error-msg-required="${required}">
								<option value=""><spring:message code="buyer.setting.select.from.user" /></option>
								<c:forEach items="${userList}" var="user">
									<option value="${user.id}">${user.name}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="buyer.setting.to.user" /></label>
						</div>
						<div class="col-md-5">
							<select id="ToUserList" class="chosen-select" name="toUser" cssClass="form-control chosen-select chosen-container chosen-container-single" data-validation="required duplicate_check" data-validation-error-msg-required="${required}">
								<option value=""><spring:message code="buyer.setting.select.to.user" /></option>
								<c:forEach items="${userList}" var="user">
									<option value="${user.id}">${user.name}</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-5">
							<!-- <div class="align-center"> -->
							<div class="col-sm-6" style="padding-left: 0px;">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<button type="button" id="transferOwnership" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"><spring:message code="buyer.setting.transfer.ownership.btn" /></button>
								</div>
								<div class="col-sm-6">
								<a id="cancelTransferOwnership" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
									<spring:message code="application.cancel" />
								</a>
								</div>
							<!-- </div> -->
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!-- TRANSFER OWNERSHIP POPUP -->
<div class="flagvisibility dialogBox" id="transferOwnershipPopup" title="Transfer Ownership">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 ownershipInfoBlock">
				<spring:message code="buyer.setting.sure.transfer" /> <span style="font-weight: bold"></span> <spring:message code="application.to" /> <span style="font-weight: bold"></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<input type="button" id="doConfirmTransfer" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" value=<spring:message code="user.transferownership.transfer" /> />
					<a role="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</a>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.formUtils.addValidator({
		name : 'duplicate_check',
		validatorFunction : function(value, $el, config, language, $form) {
			var fid = $('#FromUserList').val();
			var tid = $('#ToUserList').val();
			//alert(loginId);
			return fid != tid;
		},
		errorMessage : 'From and To cannot be same User',
		errorMessageKey : 'duplicateUserSelection'
	});

	$.validate({
		lang : 'en'
	});

	$(document).delegate('#doConfirmTransfer', 'click', function(e) {
		e.preventDefault();
		$("#saveTransferOwnership").submit()
	});

	$(document).delegate('#transferOwnership', 'click', function(e) {
		e.preventDefault();

		if (!$('#saveTransferOwnership').isValid())
			return;

		var fid = $('#FromUserList').val();
		var tid = $('#ToUserList').val();

		$("#transferOwnershipPopup").dialog({
			modal : true,
			maxWidth : 400,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$("#fromUser").val(fid);
		$("#toUser").val(tid);
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$("#transferOwnershipPopup").find('.ownershipInfoBlock').find('span:first-child').text($('#FromUserList option:selected').text());
		$("#transferOwnershipPopup").find('.ownershipInfoBlock').find('span:last-child').text($('#ToUserList option:selected').text());
		$('.ui-dialog-title').text('Transfer Ownership');
	});
</script>