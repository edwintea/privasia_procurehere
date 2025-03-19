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
					<li class="active">${eventType.value} </li>
					<li><spring:message code="buyer.eventReport.progresstab" /></li>
				</ol>
				<!-- page title block -->
				<jsp:include page="ongoinEventHeader.jsp" />
				<section class="progress_list_sectoin">
					<div class="tab-pane active error-gap-div">
						<div class="clear"></div>
						<!-- /.Container Div -->
						<div class="Progress-Report-main marg-top-20 white-bg">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a href="#"> <spring:message code="eventprogress.progressreport" /> </a>
								</h4>
							</div>
							<div class="Progress-Report-main-inner pad_all_15 cartMainBlockMax">
								<div class="chart-main">
									<%-- <img src="<c:url value="/resources/images/chart.png"/>" alt="chart"> --%>
									<div id="morris-bar-progress" class="graph"></div>
								</div>
							</div>
							<div class="Progress-Report-table pad_all_15 marg-top-20">
							<c:if test="${event.eventVisibility!='PRIVATE'}">
								<span class="selfinvitenote">&nbsp;&nbsp;&nbsp;Note:&nbsp;</span>&nbsp;&nbsp;<span class="selfinvite">&nbsp;&nbsp;&nbsp;Self Invited&nbsp;</span>
								</c:if>
								<div class="Progress-Report-table-inner">
									<table cellpadding="0" cellspacing="0" width="100%">
										<thead>
											<tr>
												<th class="align-left width_200"><spring:message code="application.supplier" /></th>
												<th class="align-center width_200"><spring:message code="application.preview" /></th>
												<th class="align-center width_200"><spring:message code="eventprogress.invite.accpted" /></th>
												<th class="align-center width_200"><spring:message code="eventprogress.submittedsubmission" /></th>
												<c:if test="${eventType == 'RFA' && event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax) }">
													<th class="align-center width_200"><spring:message code="eventprogress.revisedsubmission" /></th>
												</c:if>
											</tr>
										</thead>
										<tbody>
											<input type="hidden" id="supplierCount" value="${supplierCount}">
											<input type="hidden" id="acceptedCnt" value="${acceptedCnt}">
											<input type="hidden" id="submittedCnt" value="${submittedCnt}">
											<input type="hidden" id="revisedSubmittedCnt" value="${revisedSubmittedCnt}">
											<input type="hidden" id="supPreviewCnt" value="${supPreviewCnt}">
											<c:forEach items="${supplierList}" var="supList">
												<tr>
													
													<td style="white-space:nowrap;">
														<c:if test="${supList.selfInvited}">
															<span class="required-mark">&nbsp;*&nbsp;&nbsp;</span>
														</c:if>
														${supList.supplier.companyName}
													</td>
													<td>
														<div class="${not empty supList.previewTime ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty supList.previewTime ?  'right-mark' : 'cross-mark'}" href="#"> <i class="${not empty supList.previewTime ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove'}"></i>
															</a>
														</div> <fmt:formatDate value="${supList.previewTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
													</td>
													<td>
														<div class="${not empty supList.supplierEventReadTime && supList.submissionStatus != 'REJECTED' ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty supList.supplierEventReadTime && supList.submissionStatus != 'REJECTED' ? 'right-mark' : (not empty supList.isRejectedAfterStart and supList.submissionStatus == 'REJECTED') ?  'rejectedEvent cross-mark' : 'cross-mark'}" href="#"> 
															<i class="${not empty supList.supplierEventReadTime && supList.submissionStatus != 'REJECTED' ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove' }"></i>

															</a>
														</div> <c:if test="${ supList.submissionStatus != 'REJECTED' }">
															<fmt:formatDate value="${supList.supplierEventReadTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
														</c:if>
														<c:if test="${supList.submissionStatus == 'REJECTED' }">
															<p style="margin-top:7px;"><fmt:formatDate value="${supList.rejectedTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" /></p>
														</c:if>
													</td>
													<td>
														<div class="${not empty supList.supplierSubmittedTime ? 'right-mark-main' : 'cross-mark-main'}">
															<a class="${not empty supList.supplierSubmittedTime ? 'right-mark' : 'cross-mark'}" href="#"> <i class="${not empty supList.supplierSubmittedTime ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove'}"></i>
															</a>
														</div> <fmt:formatDate value="${supList.supplierSubmittedTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
													</td>
													<c:if test="${eventType == 'RFA' && event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax) }">
														<td>
															<div class="${supList.revisedBidSubmitted ? 'right-mark-main' : 'cross-mark-main'}">
																<a class="${supList.revisedBidSubmitted ? 'right-mark' : 'cross-mark'}" href="#"> <i class="${supList.revisedBidSubmitted ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove'}"></i>
																</a>
															</div> <fmt:formatDate value="${supList.revisedBidDateAndTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
														</td>
													</c:if>
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
	var suppCnt = $('#supplierCount').val();
	var Preview = $('#supPreviewCnt').val();
	var acceptedInvitation = $('#acceptedCnt').val();
	var submittedsubmission = $('#submittedCnt').val();
	var revisedSubmittedsubmission = $('#revisedSubmittedCnt').val();

	// If case of non-lumpsum auction event types this value will not be present.
	if(revisedSubmittedsubmission == '') {
		revisedSubmittedsubmission = 0;
	}
	
	// If no suppliers, dont display chart
	if(suppCnt > 0) {
		var previewPercentage = ((Preview / suppCnt) * 100).toFixed(2);
		var acceptedPercentage = ((acceptedInvitation / suppCnt) * 100).toFixed(2);
		var submitPercentage = ((submittedsubmission / suppCnt) * 100).toFixed(2);
		var revisedSubmitPercentage = (revisedSubmittedsubmission / suppCnt) * 100;

		Morris.Bar({
			element : 'morris-bar-progress',
			data : [ {
				y : 'Preview(' + Preview + '/' + suppCnt + ')',
				a : previewPercentage
			}, {
				y : 'Accepted Invitation(' + acceptedInvitation + '/' + suppCnt + ')',
				a : acceptedPercentage
			}, {
				y : 'Submitted Submission(' + submittedsubmission + '/' + suppCnt + ')',
				a : submitPercentage
			<c:if test="${eventType == 'RFA' && event.status == 'CLOSED' && (!empty auctionRules.lumsumBiddingWithTax)}">
			}, {
				y : 'Revised Submission(' + revisedSubmittedsubmission + '/' + suppCnt + ')',
				a : revisedSubmitPercentage
			</c:if>
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