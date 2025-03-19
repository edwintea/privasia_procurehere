<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
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

.eval_score > .table > thead > tr > th {
    padding-top: 5px !important;
    padding-bottom: 5px !important;
    border-left: 1px solid #ccc !important;
}

.summary_row {
	background-color: #EAF2F8;
	font-weight: bold;
}

.bold_text {
	font-weight: bold;
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
					<li><spring:message code="evaluation.score.card" /></li>
				</ol>
				<!-- page title block -->
				<jsp:include page="spfHeader.jsp" />
				<section class="progress_list_sectoin">
					<div class="tab-pane active error-gap-div">
						<div class="clear"></div>
						<!-- /.Container Div --> 
						<div class="Progress-Report-main marg-top-20 white-bg">
							<c:if test="${(spForm.formOwner.id eq loggedInUserId and (spForm.formStatus == 'CLOSED' or spForm.formStatus eq 'CONCLUDED')) or (isAdmin and spForm.formStatus eq 'CONCLUDED')}">
								<div>
									<form:form action="${pageContext.request.contextPath}/buyer/downloadScoreCard/${spForm.id}" method="POST">
										<button class="btn btn-sm float-right btn-success hvr-pop marg-right-10" id="idScoreCardDownload" data-toggle="tooltip" style="margin-top: 8px" data-placement="top" data-original-title='<spring:message code="score.card.button" />'>
											<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> </span>
											<span class="button-content"><spring:message code="score.card.button" /></span>
										</button>
									</form:form>
								</div>
								<div class="pull-right">
								<form:form action="${pageContext.request.contextPath}/buyer/spfEvaluationScore/${spForm.id}" method="POST">
									<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" id="idSumDownload" style="margin-top: 8px" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="evaluation.score.button" />'>
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
										</span> <span class="button-content"><spring:message code="evaluation.score.button" /></span>
									</button>
								</form:form>
							</div>
							</c:if>
							<div class="panel-heading">
								<h4 class="panel-title">
									<a href="#"> <spring:message code="sp.score.card" /> </a>
								</h4>
							</div>
							<div class="tab-main-inner pad_all_15 border-all-side float-left width-100">
								<div class="row">
									<div class="col-md-3 marg-left-20">
										<label><spring:message code="supplier.name" />:</label> ${spForm.awardedSupplier.companyName} <br/> 
										<label><spring:message code="label.businessUnit" />:</label> ${spForm.businessUnit.unitName}<br/>
									</div>
								</div>
								<div class="panel-body pad_all_15">
									<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20 col-md-10">
										<div>
											<h4 style="padding-left:5px !important;margin-bottom:5px;"><spring:message code="total.score.by.evaluator" /></h4>
										</div>
										<div class="import-supplier-inner-first-new global-list form-middle marg-top-10 marg-bottom-15">
											<div class="ph_tabel_wrapper">
												<div class="main_table_wrapper ph_table_border eval_score" style="margin: 0;">
													<table class="ph_table border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" >
														<colgroup>
															<col class="width_50_fix" />
														</colgroup>													
														<thead>
															<tr>
																<th class="align-left" colspan="2" >Evaluator</th>
																<c:forEach items="${evaluators}" var="evaluator">
																	<th class="align-center">${evaluator}</th>
																</c:forEach>
																<th>&nbsp;</th>
															</tr>
															<tr>
																<th class="align-left " colspan="2" ><spring:message code="label.appraisal.criteria" /></th>
																<c:forEach items="${evaluators}" var="evaluator">																
																	<th class="align-center " >Total Score (%)</th>
																</c:forEach>
																<th class="align-right">Average Score</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${scoreCardList}" var="scoreCard">
																<c:set var="evalCount" value="0" />
																<c:forEach items="${scoreCard.score}" var="score">
																	<c:set var="evalCount" value="${evalCount + 1}" />
																</c:forEach>
																<c:if test="${scoreCard.level > 1 and scoreCard.order eq 0 }">
																	<tr>
																		<td class="align-right summary_row">&nbsp;</td>
																		<td class="align-left summary_row bold_text" colspan="${evalCount + 1}">Average Total Criteria Score</td>
																		<td class="align-right summary_row">${subTotal}</td>
																	</tr>
																</c:if>
																<tr>
																	<td class="align-right ${scoreCard.order == 0 ? 'bold_text' : '' }">${scoreCard.level}.${scoreCard.order}</td>
																	<td class="align-left ${scoreCard.order == 0 ? 'bold_text' : '' }">${scoreCard.criteria}</td>
																	<c:forEach items="${scoreCard.score}" var="score">
																		<td class="align-center ${scoreCard.order == 0 ? 'bold_text' : '' }">${scoreCard.order ne 0 ? score : ''}</td>
																	</c:forEach>
																	<td class="align-right">${scoreCard.order ne 0 ? scoreCard.average : ''}</td>
																</tr>
																<c:if test="${scoreCard.order eq 0 }">
																	<c:set var="subTotal" value="${scoreCard.average}" />
																</c:if>
															</c:forEach>
															<tr>
																<td class="align-right summary_row">&nbsp;</td>
																<td class="align-left summary_row bold_text" colspan="${evalCount + 1}">Average Total Criteria Score</td>
																<td class="align-right summary_row">${subTotal}</td>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
									
									<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20 col-md-10">
										<div>
											<h4 style="padding-left:5px !important; margin-bottom:5px;"><spring:message code="label.consolidate.score" /></h4>
										</div>
										<div class="import-supplier-inner-first-new global-list form-middle marg-top-10 marg-bottom-15">
											<div class="ph_tabel_wrapper">
												<div class="main_table_wrapper ph_table_border" style="margin: 0;">
													<table class="ph_table border-none display table table-bordered" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
														<colgroup>
															<col class="width_50_fix" />
														</colgroup>													
														<thead>
															<tr>
																<th class="for-left" colspan="2" ><spring:message code="label.appraisal.criteria" /></th>
																<th class="for-center">Average Total Criteria Score</th>
																<th class="for-center">Criteria Weightage (%)</th>
																<th class="for-center">Average Score with Weightage</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${conScoreList}" var="score">
																<tr>
																	<td class="align-right">${score.level}.${score.order}</td>
																	<td class="align-left bold_text">${score.criteria}</td>
																	<td class="align-center">${score.averageScore}</td>
																	<td class="align-center">${score.weightage}</td>
																	<td class="align-center">${score.totalScore}</td>
																</tr>
															</c:forEach>
															<tr>
																<td class="align-left summary_row"></td>
																<td class="align-left summary_row">Overall Score</td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row">${overallScore}</td>
															</tr>
															<tr>
																<td class="align-left summary_row"></td>
																<td class="align-left summary_row">Rating</td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row">${scoreRating.rating}</td>
															</tr>
															<tr>
																<td class="align-left summary_row"></td>
																<td class="align-left summary_row">Rating Description</td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row"></td>
																<td class="align-center summary_row">${scoreRating.description}</td>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
									
									<div class="row">
										<div class="col-sm-6 col-md-6 col-lg-4 col-xs-12">
											<form:form id="concludeForm" action="${pageContext.request.contextPath}/buyer/concludeForm/${spForm.id}" method="post" >
												<%-- <input type="hidden" name="overallScore" value="${overallScore}"> --%>
												<input type="hidden" name="ratingId" value="${scoreRating.id}">
												<h4><spring:message code="label.meeting.remark" />:</h4>
												<c:if test="${spForm.formStatus ne 'CONCLUDED'}">
													<textarea id="concludeRemark" name="concludeRemarks" class="form-control textarea-counter" data-validation="length" data-validation-length="max250" rows="2"></textarea>
												</c:if>
												<c:if test="${spForm.formStatus eq 'CONCLUDED'}">
													<div>${spForm.remarks}</div>
												</c:if>
											</form:form>
										</div>
									</div>
<%-- 									<button type="button" id="btnConcludeSubmit" class="btn btn-warning ph_btn_small hvr-pop hvr-rectangle-out" style="margin-top: 10px">
										<spring:message code="eventarchieve.conclude.button" />
									</button>
 --%>												
 
 									<c:if test="${spForm.formOwner.id eq loggedInUserId and spForm.formStatus eq 'CLOSED'}">
	 									<a href="#confirmConclude" id="confirmConcludeBtn" role="button" class="btn btn-warning ph_btn hvr-pop align-center" style="margin-top: 10px" ><spring:message code="eventarchieve.conclude.button" /></a>
									</c:if>
								</div>
							</div>
							
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="confirmConclude" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="form.conclude.confirm" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="form.conclude.messege" />
							</label>
						</div>
					</div>
			</div>
			<input type="hidden" name="id" value="${spForm.id}"> 
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="button" id="conclude" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no2" />
				</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
	
	$('#confirmConcludeBtn').click(function(e) {
		if($('#concludeForm').isValid()) {
			$("#confirmConclude").modal('show');
		}
	});
	
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
	
	$('#conclude').click(function(e) {
		e.preventDefault();
			//$('#demo-form1').attr('action', getBuyerContextPath('saveSupplierPerformanceForm'));
		if($('#concludeForm').isValid()) {
			$('#loading').show();
			$('#concludeForm').submit();
			$("#confirmConclude").modal('hide');
		} else {
			$("#confirmConclude").modal('hide');
		}
	});
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