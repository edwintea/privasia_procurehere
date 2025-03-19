<%-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%> --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxMailMessagesDesk" code="application.rfx.mail.messages" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxMailMessagesDesk}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li>
						<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">
							<spring:message code="application.dashboard" />
						</a>
					</li>
					<li class="active">${eventType.value}</li>
					<li><spring:message code="buyer.eventReport.messagetab" /></li>
				</ol>
				<!-- page title block -->
				<jsp:include page="ongoinEventHeader.jsp" />
				<section class="progress_list_sectoin">
					<div class="tab-pane active error-gap-div">
						<div class="clear"></div>
						<!-- /.Container Div -->
						<div class="Progress-Report-main marg-top-10 white-bg">
							<div class="Progress-Report-main-inner pad_all_15">
								<div id="mailbox">
									<jsp:include page="mailBox.jsp" />
								</div>
							</div>
						</div>
				</section>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
$(window).bind('load', function() {
	var allowedFields = '#dashboard,.bluelink,.navigation, #reloadMsg,#idOngoingProgTab,#idSumDownload,.evaluationSummaryReport,.downloadBuyerAuctionReport,#ReplayOkWithSame,#cancelReply,#idOngoingSumTab,#idOngoingEvalTab,#idOngoingMessageTab';
	<c:if test="${eventType eq 'RFA' and (eventPermissions.viewer or buyerReadOnlyAdmin) and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
		allowedFields +=',#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,.downloadBuyerAuctionReport';
	</c:if>
	disableFormFields(allowedFields);
});
</c:if>
	$(document).ready(function() {

		// Check if messagePermission is true, then show the button
		<c:if test="${not empty messagePermission}">
			if (!${messagePermission}) {
				$('#compose_new').hide();
			}
		</c:if>

		$('#page-content').find('#evaluationConlusionReport').hide();
		$('#page-content').find('#evaluationConlusionReport').remove();
		$.validate({
			lang : 'en',
			modules : 'file'
		});
	});
</script>