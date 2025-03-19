<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="prRemarkDesk" code="application.pr.create.remarks" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prRemarkDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li>
					<a id="dashboardLink" href="${buyerDashboard}">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">
					<spring:message code="pr.purchase.requisition" />
				</li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="pr.purchase.requisition" />
					</h2>
				</div>
				<jsp:include page="prHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<div class="white-bg border-all-side float-left width-100 pad_all_15">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tag-line">
								<h2>PR :${pr.name}</h2>
								<br>
								<h2><spring:message code="prtemplate.case.id" /> :${pr.prId}</h2>
							</div>
							<div class="print-down">
								<label><spring:message code="application.status" /> : </label>${pr.status}
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
				<c:url var="remarkUrl" value="/buyer/prRemark" />
				<form:form id="prRemarkForm" action="${remarkUrl}" method="post" modelAttribute="pr" acceptCharset="UTF-8">
					<form:hidden path="id" value="${pr.id}" />
					<div class="tab-pane" style="display: block">
						<!--<div class="heading-tab-pr">Remark</div>-->
						<div class="upload_download_wrapper clearfix mar-t20 event_info">
							<h4><spring:message code="label.meeting.remark" /></h4>
							<div class="form-tander1 pad_all_15">
								<div class="row">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="prtemplate.general.remark" /></label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
										<spring:message code="prtemplate.remark.showpo.placeholder" var="remarkshow"/>
										<form:textarea path="remarks" rows="4" class="form-control" maxlength="1000" placeholder="${remarkshow}" data-validation="length" data-validation-length="max1000" />
										<span class="sky-blue"><spring:message code="dashboard.valid.max2.characters" /></span>
									</div>
								</div>
							</div>
							<div class="form-tander1 pad_all_15 ">
								<div class="row">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="prtemplate.terms.condition" /></label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6 ${!empty templateFields ? (tf:prReadonly( templateFields, 'TERM_AND_CONDITION' ) ? 'disabled' : '') : '' }">
										<form:textarea path="termsAndConditions" rows="4" maxlength="850" class="form-control" data-validation="length" data-validation-length="max850" />
										<span class="sky-blue"><spring:message code="terms.max.characters.only" /></span>
									</div>
								</div>
							</div>
						</div>
						<div class="btn-next">
							<div class="row">
								<div class="col-md-12 col-xs-12 col-ms-12">
									<c:url var="prPurchaseItem" value="/buyer/prPurchaseItem/${pr.id}" />
									<a id="previousButton" href="${prPurchaseItem}" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.previous" /></a>
									<button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out" href="#"><spring:message code="application.next" /></button>
									<spring:message code="application.draft" var="draft" />
									<input type="button" id="submitStep1PrDetailDraft" class="step_btn_1 btn btn-black marg-top-20 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
									<c:if test="${pr.status eq 'DRAFT' && (isAdmin or eventPermissions.owner)}">
										<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="prtemplate.cancel.pr" /></a>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</section>
		</div>
	</div>
</div>
<!-- cancel pr popup  -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.cancel" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="prtemplate.sure.want.cancel" /> 
							</label>
						</div>
						<div class="form-group col-md-6">
						<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation" />
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">


$('document').ready(function() {
	
	$('#cancelPr').click(function() {
		$(this).addClass('disabled');
	});
});

	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
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

	// save draft
	$('#submitStep1PrDetailDraft').click(function(e) {
		$('#prRemarkForm').attr('action', getContextPath() + "/buyer/savePrRemarkDraft");
		$('#prRemarkForm').submit();

	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>
