<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxProgressReportDesk" code="application.rfx.progress.report" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxProgressReportDesk}] });
});

</script>
<style>
.right-mark-main {
	width: 30px;
	height: 30px;
	margin: 10px auto;
}

.Progress-Report-table td {
	font-family: 'open_sansregular';
	font-weight: normal;
	font-size: 14px;
	color: #4f4d4d;
	text-align: center;
}
.bg-blue {
	background: blue;
    color: #fff;
    padding: 5px;
    border-radius: 2px;
    font-weight: 600;
    margin-left: 100px;
}
.mt-7 {
	margin-top: 7px;
}
.rejectedEvent.cross-mark {
	background-color: red;
}
</style>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active"><a href="${pageContext.request.contextPath}/buyer/supplierPerformanceList"> Performance Evaluation List</a></li>
					<li><spring:message code="buyer.eventReport.progresstab" /></li>
				</ol>
				<!-- page title block -->
				<jsp:include page="spfHeader.jsp" />
				<section class="progress_list_sectoin">
					<div class="tab-pane active error-gap-div">
						<div class="clear"></div>
						<!-- /.Container Div -->
						<div class="Progress-Report-main marg-top-20 white-bg">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a href="#"> <spring:message code="sp.evaluation.progress" /> </a>
								</h4>
							</div>
							<div class="Progress-Report-main-inner pad_all_15 cartMainBlockMax">
								<div class="chart-main">
									<%-- <img src="<c:url value="/resources/images/chart.png"/>" alt="chart"> --%>
									<div id="morris-bar-progress" class="graph"></div>
								</div>
							</div>
							<div class="Progress-Report-table pad_all_15 marg-top-20">
								<div class="Progress-Report-table-inner">
									<table cellpadding="0" cellspacing="0" width="100%">
										<thead>
											<tr>
												<th class="align-center width_200"><spring:message code="eventsummary.envelopes.evaluator" /></th>
												<th class="align-center width_200"><spring:message code="application.preview" /></th>
												<th class="align-center width_200"><spring:message code="buyer.dashboard.evaluate" /></th>
												<th class="align-center width_200"><spring:message code="owner.approved" /></th>
											</tr>
										</thead>
										<tbody>
											<input type="hidden" id="evaluatorCount" value="${evaluatorCount}">
											<input type="hidden" id="evaluateCnt" value="${evaluateCnt}">
											<input type="hidden" id="approvedCnt" value="${approvedCnt}">
											<input type="hidden" id="revisedSubmittedCnt" value="${revisedSubmittedCnt}">
											<input type="hidden" id="evalPreviewCnt" value="${evalPreviewCnt}">
											<c:forEach items="${evaluatorList}" var="evalList">
												<tr>
													
													<td style="white-space:nowrap;">
														${evalList.evaluator.name}
													</td>
													<td>
														<div class="${not empty evalList.previewDate ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty evalList.previewDate ?  'right-mark' : 'cross-mark'}" href="#"> <i class="${not empty evalList.previewDate ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove'}"></i>
															</a>
														</div> <fmt:formatDate value="${evalList.previewDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
													</td>
													<td>
														<div class="${not empty evalList.evaluateDate ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty evalList.evaluateDate ? 'right-mark' : 'cross-mark'}" href="#"> <i class="${not empty evalList.evaluateDate ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove'}"></i>
															</a>
														</div> <fmt:formatDate value="${evalList.evaluateDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
													</td>
													<td>
														<div class="${not empty evalList.approvedDate && evalList.evaluationStatus == 'APPROVED' ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty evalList.approvedDate && evalList.evaluationStatus == 'APPROVED' ? 'right-mark' : 'cross-mark'}" href="#"> 
																<i class="${not empty evalList.approvedDate && evalList.evaluationStatus == 'APPROVED' ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove' }"></i>
															</a>
														</div> 
														<c:if test="${ evalList.evaluationStatus == 'APPROVED' }">
															<fmt:formatDate value="${evalList.approvedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
														</c:if>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		$(".for-toggle").click(function() {
			$(".add-contact-contant").toggle();
		});
	});
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#dashboard,.bluelink,.navigation,#idOngoingSumTab,#idOngoingEvalTab,#idOngoingMessageTab';
		<c:if test="${eventType eq 'RFA' and (eventPermissions.viewer or buyerReadOnlyAdmin) and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
			allowedFields +=',#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,.downloadBuyerAuctionReport';
		</c:if>
		disableFormFields(allowedFields);
	});
	</c:if>
</script>
<script>
	var evalCnt = $('#evaluatorCount').val();
	var preview = $('#evalPreviewCnt').val();
	var evaluateCnt = $('#evaluateCnt').val();
	var approved = $('#approvedCnt').val();
	
	// If no suppliers, dont display chart
	if(evalCnt > 0) {
		var previewPercentage = ((preview / evalCnt) * 100).toFixed(2);
		var evaluatePercentage = ((evaluateCnt / evalCnt) * 100).toFixed(2);
		var submitPercentage = ((approved / evalCnt) * 100).toFixed(2);

		Morris.Bar({
			element : 'morris-bar-progress',
			data : [ {
				y : 'Preview(' + preview + '/' + evalCnt + ')',
				a : previewPercentage
			}, {
				y : 'Evaluate(' + evaluateCnt + '/' + evalCnt + ')',
				a : evaluatePercentage
			}, {
				y : 'Approved(' + approved + '/' + evalCnt + ')',
				a : submitPercentage

			} ],
			labels : [ 'Percentage' ], // TODO : Display labels should Dynamically.
			xkey : 'y',
			ymax : 100,
			ykeys : [ 'a' ],
			stacked : true,
			hideHover : 'auto',
			resize : true, //defaulted to true
			gridLineColor : '#eeeeee',
			barColors : [ '#06ccb3' ]
		});
	}

</script>

<style>

 .required-mark{
color:#ff5757;
} 


.selfinvite:before{
     content:"*" ;
     color:#ff5757 ;   
     }
     
.selfinvite {
	color: #ff5757;
	font-family: 'open_sanssemibold';
	font-size: 14px;
}

.selfinvitenote {
	color: #ff5757;
	font-family: 'open_sanssemibold';
	font-size: 14px;
}

</style>