<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<!-- page title block -->
<style>
.border-three-sides {
    border-top: 1px solid #e8e8e8;
    border-left: 1px solid #e8e8e8;
    border-right: 1px solid #e8e8e8;
    margin: 0px !important;
}
</style>
<div class="Section-title title_border gray-bg">
	<h2 class="trans-cap manage_icon"><spring:message code="sp.evaluation" /></h2>
	<h2 class="trans-cap pull-right">
		<spring:message code="application.status" />
		: ${spForm.formStatus}
	</h2>
</div>
<div class="row">
	<div class="col-md-6 col-sm-6 close-da">
		<div class="row">
			<div class="col-md-10">
				<div class="meeting2-heading-new">
					<jsp:useBean id="now" class="java.util.Date" />
					<c:if test="${now.time gt spForm.evaluationStartDate.time}">
						<label><spring:message code="application.closedate" /></label>
						<h3>
							<span> <fmt:formatDate value="${spForm.evaluationEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</span>
						</h3>
					</c:if>
					<c:if test="${now.time lt spForm.evaluationStartDate.time}">
						<label><spring:message code="application.startdate" /></label>
						<h3>
							<span> <fmt:formatDate value="${spForm.evaluationStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</span>
						</h3>
					</c:if>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${(spForm.formStatus != 'CANCELED' and spForm.formStatus != 'SUSPENDED' and spForm.formStatus != 'CLOSED' and spForm.formStatus != 'CONCLUDED')}">
		<div class="col-md-6 col-sm-6 close-da">
			<div id="main">
				<div class="content">
					<div class="counter">
						<jsp:useBean id="today" class="java.util.Date" />
						<c:if test="${today.time lt spForm.evaluationEndDate.time and today.time gt spForm.evaluationStartDate.time}">
							<div>
								<h3>
									<spring:message code="application.time.left.end" />
								</h3>
							</div>
							<div class="main-example">
								<fmt:formatDate value="${spForm.evaluationEndDate}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="evaluationEndDate" />
								<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/spForm/${spForm.id}" data-reload-url="${pageContext.request.contextPath}/buyer/viewSPFSummary?formId=${spForm.id}" data-date="${evaluationEndDate}"></div>
							</div>
						</c:if>
						<c:if test="${today.time lt spForm.evaluationStartDate.time}">
							<div>
								<h3>
									<spring:message code="application.time.left.start" />
								</h3>
							</div>
							<div class="main-example">
								<fmt:formatDate value="${spForm.evaluationStartDate}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="evaluationStartDate" />
								<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/spForm/${spForm.id}" data-reload-url="${pageContext.request.contextPath}/buyer/viewSPFSummary?formId=${spForm.id}" data-date="${evaluationStartDate}"></div>
							</div>
						</c:if>
					</div>
				</div>
			</div>
			<!-- /#Main Div -->
		</div>
	</c:if>
	<!-- /.Columns Div -->
</div>
<!-- /.Row Div -->


<div class="tab-main marg_top_20">

<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

	<ul class="tab event-details-tabs">
		
		<li class="tab-link border-three-sides ${showSummaryTab == true ? 'current' : ''}"><a id="idOngoingSumTab" class="${showSummaryTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/viewSPFSummary?formId=${spForm.id}">Summary</a></li>
 
 		<c:if test="${(spForm.formStatus == 'CLOSED' or spForm.formStatus == 'ACTIVE') and (isAdmin or spForm.formOwner.id eq loggedInUserId)}">
 			<li class="tab-link border-three-sides ${showProgressTab == true ? 'current' : ''}"><a id="idOngoingEvalTab" class="${showProgressTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/evaluationProgress/${spForm.id}"><spring:message code="evaluation.progress.tab" /></a></li>
 		</c:if>
 		<c:if test="${(spForm.formOwner.id eq loggedInUserId and (spForm.formStatus == 'CLOSED' or spForm.formStatus eq 'CONCLUDED')) or (isAdmin and spForm.formStatus eq 'CONCLUDED')}">
 			<li class="tab-link border-three-sides ${showScoreTab == true ? 'current' : ''}"><a id="scoreCardTab" class="${showScoreTab == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/buyer/scoreCard/${spForm.id}"><spring:message code="evaluation.score.card" /></a></li>
 		</c:if>
	</ul>
	
</div>
<div class="clear"></div>
<div class="tab-main-inner pad_all_15 border-all-side float-left width-100">
	<div class="row">
		<div class="col-md-3">
			<b><spring:message code="supplier.performance.form.id" />:</b> ${spForm.formId} <br/> <b><spring:message code="supplier.performance.form.owner" />:</b> ${spForm.formOwner.name}, ${spForm.formOwner.communicationEmail}<br />
		</div>
				
		<div class="col-sm-12 col-md-9 pull-right">
 			<c:if test="${!showScoreTab}">
					<div class="pull-right">
						<form:form action="${pageContext.request.contextPath}/buyer/downloadPerformanceEvaluationSummary/${spForm.id}" method="POST">
							<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='Download <spring:message code="performance.evaluation.summary.button" />'>
								<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
								</span> <span class="button-content"><spring:message code="performance.evaluation.summary.button" /></span>
							</button>
						</form:form>
					</div>
 			</c:if>
		</div>
	</div>
</div>
<style>
.counter h3 {
	margin-bottom: 10px;
}

#countdown {
	margin-bottom: 10px;
}

.counter.auction-page.height-150, .counter {
	min-height: 50px;
}

.w-indigo {
	color: #fff !important;
	background-color: #3f51b5 !important
}
</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/js-core/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/charts/morris/morris.js"></script>
<script>
	counterMeetings();
	//setInterval(function() {
	//counterMeetings();
	//}, 60000);
	function counterMeetings() {
		$('#countdown').each(
			function() {
				// set the date we're counting down to
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
					htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours
							+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
				}//$('#countdown').html(htmldata);
				$(countdown).html(htmldata);

			});
	}
</script>
