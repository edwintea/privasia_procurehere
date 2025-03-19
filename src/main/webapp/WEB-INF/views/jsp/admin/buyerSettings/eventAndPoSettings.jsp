<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multiselect/multi-select.css"/>">

	<div class="Invited-Supplier-List import-supplier white-bg" id="eventAndPoSettPage">
		<form:form id="eventSettingsForm" data-parsley-validate="" cssClass="form-horizontal bordered-row" commandName="eventSettings" method="post" action="saveEventSettings">
			<div class="meeting2-heading">
				<h3 style="font-size: 15px;">
					<spring:message code="label.event.settings.control" />
				</h3>
			</div>
			<div class="meeting2-heading">
				<h3 class="fnt-clr">
					<spring:message code="note.event.settings.toAllUser" />
				</h3>
			</div>
			<div class="meeting2-heading">
				<h3 class="">
					<spring:message code="label.event.settings.heading" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list meeting2-heading">
					<header class="form_header"> </header>
<%-- 					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" /> --%>
					<form:hidden path="id" />
					
				<div class="marg-top-20">
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rfi" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpBlankRfi" path="rfiCreateFromBlank" class="custom-checkbox" label="Create from Blank" />
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRfi"  path="rfiCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rfq" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpBlankRfq"  path="rfqCreateFromBlank"  class="custom-checkbox" label="Create from Blank" />
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRfq"  path="rfqCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rfp" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpBlankRfp"  path="rfpCreateFromBlank"  class="custom-checkbox" label="Create from Blank" />
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRfp"  path="rfpCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rft" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpBlankRft"  path="rftCreateFromBlank"  class="custom-checkbox" label="Create from Blank" />
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRft"  path="rftCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rfa" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpBlankRfa"  path="rfaCreateFromBlank"  class="custom-checkbox" label="Create from Blank" />
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRfa"  path="rfaCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-4">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.rfs" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousRfs"  path="rfsCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-4">
							<label path="decimal" cssClass="marg-top-10">
								<spring:message code="label.event.settings.pr" />
							</label>
						</div>
						<div class="col-md-2">
							<form:checkbox id="idCpPreviousPr"  path="prCopyFromPrevious"  class="custom-checkbox" label="Copy From Previous" />
						</div>
					</div>
				  </div>

			</div>
			<div class="meeting2-heading">
				<h3 class="">
					<spring:message code="label.po.settings.heading" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<div class="row marg-bottom-10 form-tander1 requisition-summary-box">
					<div class="col-md-2 marg-top-10">
						<label path="decimal"> <spring:message code="buyer.setting.autocreatepo.label" />
						</label>
					</div>
<!-- 					<div class="col-md-5" style="margin-top: 2px;"> -->
<%-- 						<form:checkbox id="idAutoCreatePo" path="autoCreatePo" class="custom-checkbox" /> --%>
<!-- 					</div> -->

					<div class="col-md-8 col-sm-6">
						<div class="radio_yes-no-main width100">
							<div class="radio_yes-no">
								<div class="radio-info">
									<label class="select-radio mr-tp-7"> <form:radiobutton path="autoCreatePo" class="custom-radio " value="0" /> <spring:message code="application.no" /></label>
								</div>
							</div>
							<div class="radio_yes-no">
								<div class="radio-info">
									<label class="select-radio mr-tp-7"> <form:radiobutton path="autoCreatePo" class="custom-radio " value="1" /> <spring:message code="application.yes" /></label>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row marg-bottom-10 form-tander1 requisition-summary-box">
					<div class="col-md-2 marg-top-10">
						<label path="decimal"> <spring:message code="buyer.setting.autopublishpo.label" />
						</label>
					</div>
<!-- 					<div class="col-md-5" style="margin-top: 2px;"> -->
<%-- 						<form:checkbox id="idAutoPublishPo" path="autoPublishPo" class="custom-checkbox marg-top-10" /> --%>
<!-- 					</div> -->
					
					<div class="col-md-8 col-sm-6">
						<div class="radio_yes-no-main width100">
							<div class="radio_yes-no">
								<div class="radio-info">
									  <label class="select-radio mr-tp-7"><form:radiobutton path="autoPublishPo" class="custom-radio " value="0" /><spring:message code="application.no" /> </label>
								</div>
							</div>
							<div class="radio_yes-no">
								<div class="radio-info">
									<label class="select-radio mr-tp-7"> <form:radiobutton path="autoPublishPo" class="custom-radio " value="1" /> <spring:message code="application.yes" /> </label>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row marg-bottom-202">
					<div class="col-md-2">
						<label class="marg-top-10"></label>
					</div>
					<div class="col-md-9 dd sky mar_b_15 marg-top-10">
						<spring:message code="application.update" var="updateLabel" />
						<spring:message code="application.save" var="saveLabel" />
						<form:button type="submit" id="saveEventSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">${not empty eventSettings.id ? updateLabel : saveLabel }</form:button>
						<c:url value="/buyer/buyerDashboard" var="buyerDashboard" />
						<a href="${buyerDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
						</a>
					</div>
				</div>				
			</div>
			</form:form>			
		</div>

<style type="text/css">

.fnt-clr {
	    font-size: 14px !important;
   		color: #636363 !important;
}

.hide {
	display: none !important;
}

.show {
	display: inline-block !important;
}

.mr-tp-7 {
	margin-top: 7px;
}

</style>

<script>

	
</script>