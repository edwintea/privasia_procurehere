<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<title><spring:message code="rftenvelop.title" /></title>
<spring:message var="rfxEvaluationDesk" code="application.rfx.envelope.evaluation" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxEvaluationDesk}] });
});
</script>
<style type="text/css">
#evaluationDeclarationModal.reset-that {
  all: initial;
  * {
    all: unset;
  };
  word-break: break-all;
}
.mb {
  color:#ffffff
  };
</style>
<div id="sb-site">
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<div class="container">
					<!-- pageging  block -->
					<ol class="breadcrumb">
						<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
						</a></li>
						<li class="active">${eventType.value} </li><li><spring:message code="buyer.eventReport.evaluationtab" /></li>
<%-- 					    <li><spring:message code="label.rftenvelop" /></li>  --%>
					</ol>
					<jsp:include page="ongoinEventHeader.jsp" />
					<div class="clear"></div>
					<section class="create_list_sectoin">
						<div class="row clearfix">
							<div class="col-sm-12">
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							</div>
						</div>
						<div class="row clearfix">
							<div class="col-sm-12">
								<section class="sa_envelop_accordian">
									<div class="panel-group sa_ph_accordian" id="accordion1" role="tablist" aria-multiselectable="true">
										<c:forEach items="${envelopList}" var="envelop">
											<div class="panel panel-default">
												<div class="panel-heading" role="tab" id="headingOne">
													<h4 class="panel-title">
														<a role="button" data-idenv="${envelop.id}" class="acrdian" data-toggle="collapse" data-parent="#accordion" id="getBqcq" href="#collapseOne${envelop.id}" aria-expanded="false" aria-controls="collapseOne"> <i aria-hidden="true" class="glyph-icon icon-envelope${envelop.isOpen ? '-o' : ''}"></i> <span> ${envelop.envelopTitle}</span> <span class="sa_normal_txt">[Status : ${envelop.isOpen  ? 'Open':'Closed'}]</span> <span class="pull-right pad_all_15"><spring:message code="buyer.eventReport.evaluationtab" />:
																${envelop.evaluationStatus}</span> <br /> <span class="sa_sealed_date">${envelop.description}</span>
														</a>
													</h4>
												</div>
												<div id="collapseOne${envelop.id}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
													<div class="panel-body" style="padding: 15px 0;">
														<div class="col-md-4">
															<ul class="s1_question_list bqcqListsEnvlop">
															</ul>
														</div>
														<input type="hidden" id="permission" value="${eventPermissions.opener and !(eventPermissions.owner or eventPermissions.viewer or eventPermissions.leadEvaluator or eventPermissions.evaluator)}">
														<div class="col-sm-12 col-md-8 pull-right">
															<div class="row">
																<c:if test="${!isOpenerForAnyEnvelope}">
																	<c:set var="isOpenerForAnyEnvelope" value="${envelop.opener != null && loggedInUserId == envelop.opener.id}" />
																</c:if>




																<c:choose>
																	<c:when test="${!allowToView}">
																		<%-- <c:if test="${envelop.isOpen and eventPermissions.owner}">
																			<div class="pull-right">
																				<form:form action="${pageContext.request.contextPath}/buyer/downloadSubmissionReport/${eventType}/${event.id}/${envelop.id}/${envelop.envelopTitle}" id="downloadEnvelope">
																					<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" data-toggle="tooltip" data-placement="top" data-original-title="Download Envelope Submission" type="submit">
																						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
																						</span> <span class="button-content">Submission Report</span>
																					</button>
																				</form:form>
																			</div>
																		</c:if> --%>
																	</c:when>
																	<c:otherwise>
																		<c:if test="${envelop.showOpen or envelop.evaluationStatus eq 'COMPLETE'}">
																			<c:if test="${!envelop.evaluationCompletedPrematurely}">
																				<div class="pull-right">
																					<form:form action="${pageContext.request.contextPath}/buyer/downloadSubmissionReport/${eventType}/${event.id}/${envelop.id}/${envelop.envelopTitle}" id="downloadEnvelope">
																						<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.envelope.submission" />' type="submit">
																							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
																							</span> <span class="button-content"><spring:message code="button.envelopes.submission.report" /></span>
																						</button>
																					</form:form>
																				</div>
																			</c:if>
																			<c:if test="${!envelop.evaluationCompletedPrematurely}">
																				<div class="pull-right mr-10">
																					<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadShortEvaluationSummary/${event.id}/${envelop.id}" method="get">
																						<button class="btn btn-sm float-right btn-warning hvr-pop marg-left-10 evaluationSummaryReport" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="download.short.summary.label" />' type="submit">
																							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
																							</span> <span class="button-content"><spring:message code="download.short.summary.label" /></span>
																						</button>
																					</form:form>
																				</div>
																			</c:if>
																		</c:if>
																	</c:otherwise>
																</c:choose>



																<!-- openerUsers -->
																<c:forEach items="${envelop.openerUsers}" var="openerUse">
																 <c:if test="${closeEnv and envelop.isOpen and envelop.permitToOpenClose and openerUse.user.id eq loggedInUserId and openerUse.isOpen}">
																	<c:if test="${!envelop.evaluationCompletedPrematurely && envelop.showOpen}">
																		<div class="pull-right">
																			<input type="hidden" value="${envelop.id}" id="hiddenEnv">
																			<c:if test="${envelop.envelopType != 'OPEN'}">
																				<c:if test="${envelop.evaluationStatus eq 'PENDING'}">
																					<button class="btn btn-sm btn-info hvr-pop marg-right-10 viewEnvelope closeEnvelope" data-id="${envelop.id}">
																						<span class="glyph-icon icon-separator"> <i class="fa fa-lock"></i>
																						</span> <span class="button-content"><spring:message code="button.envelopes.close" /></span>
																					</button>
																				</c:if>
																			</c:if>
																		</div>
																	</c:if>
																</c:if> 
																</c:forEach>
																

																<c:if test="${!envelop.evaluationCompletedPrematurely}">
																	<c:choose>
																		<c:when test="${!allowToView}">
																			<c:if test="${envelop.isOpen and eventPermissions.owner and event.status eq 'COMPLETE'}">
																				<div class="pull-right">
																					<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationReport/${event.id}/${envelop.id}">
																						<button class="btn btn-sm  hvr-pop marg-right-10 btn-warning" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.evaluation.report" />' type="submit">
																							<span class="button-content"><spring:message code="button.envelopes.evaluation" /> </span>
																						</button>
																					</form:form>
																				</div>
																			</c:if>
																		</c:when>
																		<c:otherwise>
																			<c:if test="${event.status eq 'COMPLETE' and envelop.isOpen}">
																				<div class="pull-right">
																					<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationReport/${event.id}/${envelop.id}">
																						<button class="btn btn-sm  hvr-pop marg-right-10 btn-warning" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.evaluation.report" />' type="submit">
																							<span class="button-content"><spring:message code="button.envelopes.evaluation" /> </span>
																						</button>
																					</form:form>
																				</div>
																			</c:if>
																		</c:otherwise>
																	</c:choose>
																</c:if>

			 
																<c:if test="${ !envelop.evaluationCompletedPrematurely && ( !(event.status eq 'ACTIVE') || envelop.envelopType == 'OPEN') }">
																	<c:choose>
																		<c:when test="${envelop.permitToOpenClose and !envelop.isOpen }">
																		<c:forEach items="${envelop.openerUsers}" var="openerUse">
																		<c:if test="${!openerUse.isOpen  and openerUse.user.id eq loggedInUserId }">
																			<div class="pull-right" id="viewEnvelopes-${envelop.isOpen}">
																				<a href="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="envlp-${envelop.id}" data-status="${envelop.isOpen}" data-envelopId="${envelop.id}" class="${envelop.isOpen ? '':'lockedEnv'} btn btn-sm btn-info hvr-pop marg-right-10"> <span class="glyph-icon icon-separator"> <i class="fa ${envelop.isOpen  ? 'fa-unlock':'fa-lock'}"></i>
																				</span> <span class="button-content">${openerUse.isOpen  ? 'View':'Open'} <spring:message code="label.rftenvelop" /></span>
																				</a>
																			</div>
																			</c:if>
																			</c:forEach>
																		</c:when>
																		<c:otherwise>

																			<c:choose>
																				<c:when test="${!allowToView}">
																					<c:if test="${envelop.evaluationStatus eq 'PENDING'}">
																						<c:if test="${envelop.showView and envelop.isOpen }">
																							<div class="pull-right">
																								<a href="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="envlp-${envelop.id}" data-status="${envelop.isOpen}" data-envelopId="${envelop.id}"  class="${envelop.showEvaluationDeclaration ? 'showEvalutionDeclaration':''} btn btn-sm btn-info hvr-pop marg-right-10 viewEnvelope"> <span class="glyph-icon icon-separator"> <i class="fa fa-unlock"></i>
																								</span> <span class="button-content"><spring:message code="button.envelopes.view" /> </span>
																								</a>
																							</div>
																						</c:if>
																					</c:if>

																					<c:if test="${envelop.evaluationStatus eq 'COMPLETE' and event.status eq 'COMPLETE' and eventPermissions.owner}">
																						<div class="pull-right">
																							<a href="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="envlp-${envelop.id}" data-status="${envelop.isOpen}" data-envelopId="${envelop.id}" class="btn btn-sm btn-info hvr-pop marg-right-10 viewEnvelope"> <span class="glyph-icon icon-separator"> <i class="fa fa-unlock"></i>
																							</span> <span class="button-content"><spring:message code="button.envelopes.view" /> </span>
																							</a>
																						</div>
																					</c:if>
																				</c:when>
																				<c:otherwise>
																					<c:if test="${envelop.showView and envelop.isOpen }">
																						<div class="pull-right">
																							<a href="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="envlp-${envelop.id}" data-status="${envelop.isOpen}" data-envelopId="${envelop.id}" class="${envelop.showEvaluationDeclaration ? 'showEvalutionDeclaration':''} btn btn-sm btn-info hvr-pop marg-right-10 viewEnvelope"> <span class="glyph-icon icon-separator"> <i class="fa fa-unlock"></i>
																							</span> <span class="button-content"><spring:message code="button.envelopes.view" /> </span>
																							</a>
																						</div>
																					</c:if>
																				</c:otherwise>
																			</c:choose>


																		</c:otherwise>
																	</c:choose>
																</c:if>
															</div>
														</div>
													</div>
													<div class="col-md-12 main-panal-box marg-bottom-20">
														<label><spring:message code="eventsummary.envelopes.type" />:</label>
														<p>${envelop.envelopType}</p>
													</div>
													<div class="col-md-12 main-panal-box marg-bottom-20">
														<label><spring:message code="eventsummary.envelopes.evaluation.owner" />: </label>
														<p>${envelop.leadEvaluater.name}</p>
													</div>
													<div class="col-md-12 main-panal-box">
														<label><spring:message code="eventsummary.envelopes.evaluation.team" />: </label>
														<c:forEach items="${envelop.evaluators}" var="evalTeam" varStatus="idx">
															<ul style="list-style-type: none">
																<li class="col-md-12">
																	<div class="Approval-lavel1 ${evalTeam.evaluationStatus eq 'PENDING' ? 'cross-waiting-mark' : ''} ${evalTeam.evaluationStatus eq 'COMPLETE' ? 'right-green-mark' : ''}">${evalTeam.user.name}</div>
																</li>
															</ul>
														</c:forEach>
													</div>
													<c:if test="${envelop.envelopType != 'OPEN'}">
														<div class="col-md-12 main-panal-box marg-bottom-20">
															<label><spring:message code="eventsummary.envelope.opener" />: </label>
															<div class="envopenpos">
															<c:forEach items="${envelop.openerUsers}" var="opener" varStatus="idx">
																	<div class="Approval-lavel1 ${opener.isOpen eq true ? 'glyph-icon icon-envelope-open' : 'glyph-icon icon-envelope-close'}">${opener.user.name}</div>
														   </br>
														   </c:forEach>
														   </div>
														</div>
													</c:if>
													
														<c:if test="${event.rfxEnvelopeOpening}">
														<div class="col-md-12 main-panal-box marg-bottom-20">
															<label><spring:message code="envelope.sequence.opening" />: </label>
															<p>${envelop.envelopSequence}</p>
														</div>
													</c:if>
													
												</div>
											</div>
										</c:forEach>
									</div>
								</section>
							</div>
						</div>
						<div class="clear"></div>
						<div>

							<c:if test="${!isMaskingEnable and eventPermissions.unMaskUser and event.status eq 'COMPLETE'}">
								<input type="submit" id="disableMasking" type="button" class="btn btn-danger ph_btn_midium  " style="margin: 10px;" value="Conclude Evaluation and Unmask" />
							</c:if>
							<c:if test="${eventPermissions.evaluationConclusionUser}">
								<input type="button" id="concludeEvaluation" type="button" class="btn btn-danger ph_btn_midium  " style="margin: 10px;" value="Conclude Evaluation" />
							</c:if>
						</div>
					</section>
				</div>
			</div>
		</div>
	</div>
</div>





<div class="modal fade" title="Enter password to view envelop" id="closedEnvelopModal" role="dialog">
	<div class="modal-dialog modal-width-custom">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="button.envelopes.password.toopen" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="" id="idPasswordDialog">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" id="eventType" value="${eventType}"> <input type="hidden" id="eventId" value="${event.id}"> <input type="hidden" id="evelopeID" value="">
				<div class="modal-body">
					<div class="row marg-bottom-20">
						<label class="col-md-3 control-label"> </label>
						<div class="col-md-6">
							<div class="input-group mrg15T mrg15B ">
								<span class="input-group-addon btn-info"> <i class="fa fa-lock" aria-hidden="true"></i>
								</span> <input style="margin-top: 0; border: #2098d1 solid 1px;" id="envlPass" data-validation="required" data-validation-error-msg-container="#password-error-dialog" name="envlPass" placeholder='<spring:message code="ongoingenvelop.enter.pw.placeholder" />' class="form-control mar-b10" value="" type="password">
							</div>
							<div id="password-error-dialog"></div>
						</div>
					</div>
					<div class="row align-center">
						<button id="submitPass" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20">
							<spring:message code="button.envelopes.open" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter password to view envelop" id="closeEnv" role="dialog">
	<div class="modal-dialog modal-width-custom">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="button.envelopes.password.toclose" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="" id="idPasswordDialog1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" id="eventType" value="${eventType}"> <input type="hidden" id="eventId" value="${event.id}"> <input type="hidden" id="evelopeCloseID" value="">
				<div class="modal-body">
					<div class="row marg-bottom-20">
						<label class="col-md-3 control-label"> </label>
						<div class="col-md-6">
							<div class="input-group mrg15T mrg15B ">
								<span class="input-group-addon btn-info"> <i class="fa fa-lock" aria-hidden="true"></i>
								</span> <input style="margin-top: 0; border: #2098d1 solid 1px;" id="closePass" data-validation="required" data-validation-error-msg-container="#password-error-dialog" name="envlPass" placeholder='<spring:message code="ongoingenvelop.enter.pw.placeholder" />' class="form-control mar-b10" value="" type="password">
							</div>
							<div id="password-error-dialog"></div>
						</div>
					</div>
					<div class="row align-center">
						<button id="submitClose" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20">
							<spring:message code="button.envelopes.close" />
						</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade" title="Enter password to Unmask envelopes" id="removeBlackList" role="dialog">
	<div class="modal-dialog modal-width-custom">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventaward.enter.pwd.unmask" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div>
				<form action="${pageContext.request.contextPath}/buyer/${eventType}/disableMasking/${event.id}" method="get">


					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="modal-body">
						<div class="row marg-bottom-20">
							<label class="col-md-3 control-label"> </label>
							<div class="col-md-6">
								<div class="input-group mrg15T mrg15B ">
									<span class="input-group-addon btn-info"> <i class="fa fa-lock" aria-hidden="true"></i>
									</span> <input style="margin-top: 0; border: #2098d1 solid 1px;" id="disableMaskingPwd" data-validation="required" data-validation-error-msg-container="#unmask-password-error-dialog" name="unmaskPwd" placeholder='<spring:message code="ongoingenvelop.enter.pw.placeholder" />' class="form-control mar-b10" value="" type="password">
								</div>
								<div id="unmask-password-error-dialog"></div>
							</div>
						</div>
						<div class="row align-center">
							<div class="row">
								<input type="submit" id="disableMasking" type="button" class="btn btn-info ph_btn_midium" style="margin: 10px;" value="Yes" /> 
								<input type="button" id="cancelMasking" type="button" class="btn btn-black ph_btn_midium" style="margin: 10px;" value="No" />
							</div>

						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" title="Enter password to Conclude Evaluation Prematurely" id="concludeEvaluationDialog" role="dialog">
	<div class="modal-dialog modal-width-700">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3 id="headerText">
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div>
				<form id="frmConcludeEvaluation" action="#" method="put">
					<div class="modal-body">
						<div class="row">
							<label class="col-md-12 control-label confirmMessage">Are you sure you want to Conclude the Evaluation Process?</label>
						</div>
						<div class="row marg-bottom-20">
							<label class="col-md-3 control-label"></label>
							<div class="col-md-6 passwordDetails">
								<div class="input-group mrg15T mrg15B ">
									<span class="input-group-addon btn-info"> <i class="fa fa-lock" aria-hidden="true"></i>
									</span> <input style="margin-top: 0; border: #2098d1 solid 1px;" id="concludeEvaluationPwd" data-validation="required" data-validation-error-msg-container="#conclude-password-error-dialog" name="concludeEvaluationPwd" placeholder='<spring:message code="ongoingenvelop.enter.pw.placeholder" />' class="form-control mar-b10" value="" type="password">
								</div>
								<div id="conclude-password-error-dialog"></div>
							</div>
							<div class="col-md-12 concludeDetails ">
								<textarea name="concludeEvaluationRemarks" id="concludeEvaluationRemarks" data-validation="required length" data-validation-length="max1000" class="form-control width-100" placeholder="Mention reason..."></textarea>
							</div>
							<div class="col-md-12 concludeDetails">
								<div class="col-md-6 pad_all_15">
									<c:set var="fileType" value="" />
									<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
										<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
									</c:forEach>
									<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										<spring:message code="meeting.doc.file.length" var="filelength" />
										<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />
										<div data-trigger="fileinput" class="form-control">
											<span class="fileinput-filename show_name"></span>
										</div>
										<span class="input-group-addon btn btn-black btn-file"> 
											<span class="fileinput-new"> 
												<spring:message code="application.selectfile" />
											</span> 
											<span class="fileinput-exists"> 
												<spring:message code="event.document.change" />
											</span> 
											<input type="file" name="concludeEvalDocument" id="concludeEvalDocument" data-validation-optional="true" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
										</span>
									</div>
									<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
									<div class="progressbar flagvisibility" data-value="0">
										<div class="progressbar-value bg-purple">
											<div class="progress-overlay"></div>
											<div class="progress-label">0%</div>
										</div>
									</div>
									<span> Note:<br />
										<ul>
											<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
											<li>Allowed file extensions: ${fileType}.</li>
										</ul>
									</span>
								</div>
								<div class="col-md-6 pad_all_15">
									<spring:message code="event.doc.file.descrequired" var="descrequired" />
									<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
									<spring:message code="event.document.filedesc" var="filedesc" />
									<input type="text" class="form-control" style="margin-top:0px !important;" name="docDescription" id="docDescription" data-validation-optional="true" data-validation="length" data-validation-length="max250" placeholder="${filedesc} ${maxlimit}">
								</div>
							</div>
							
						</div>
						
						<div class="row align-center">
							<div class="row">
								<input type="button" id="btnConcludeEvaluation" class="btn btn-info ph_btn_midium " style="margin: 10px;" value="Conclude Evaluation" /> 
								<input type="button" id="cancelConcludeEvaluation" type="button" class="btn btn-black ph_btn_midium" style="margin: 10px;" value="Cancel" />
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<!-- Declaration POP UP -->
<div class="flagvisibility dialogBox" id="evaluationDeclarationModal"  style="overflow: auto;" title="<spring:message code="declaration.popup.title" />">
	<div class="float-left width800 pad_all_15 white-bg">
		<div class="row" style="margin-left:10px;">
		<div>
				<span class="reset-that" id="declarationContent">${event.evaluationProcessDeclaration.content}</span>
		</div>
		<form cssClass="form-horizontal" action="${pageContext.request.contextPath}/buyer/acceptEvaluationDeclaration/${eventType}" method="post" id="evalDeclarationForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="eventId" value="${event.id}" />
			<input type="hidden" id="envelopId" name="envelopId" value="">
			<div class="form-group" style="margin-top:25px;">
				<input id="acceptDeclaration" name="acceptDeclaration" class="custom-checkbox" type="checkbox">
				<h5 class="control-label"> <spring:message code="evaluation.accept.declaration" /></h5>
			</div>
			<div class="align-center">
				<input type="submit" id="acceptButton" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out disabled acceptDeclaration" value="Accept"></input>
				<button type="button" id="buttonCancel" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1 val-rm">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</form>
		</div>
	</div>
</div>





<style>
.s1_question_list.bqcqListsEnvlop>li {
	cursor: default;
	margin-bottom: 5px;
	padding: 5px;
}

#submitPass {
	margin-left: 0;
	height: 38px;
}

.mr-10 {
	margin-right: 20px;
}

.modal-width-700 {
	width: 700px !important;
}

.icon-envelope-open:before {
    content: "\f2b7";
    position: absolute;
	width: 20px;
	height: 20px;
	left: -21px;
	top: 3px;
}

.icon-envelope-close:before {
    content: "\f0e0";
    position: absolute;
	width: 20px;
	height: 20px;
	left: -21px;
	top: 3px;
}

.envopenpos {
     position: relative; 
     float: left;
}

</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : true,
		validateOnBlur : true,
		modules : 'date,sanitize,file'
	});
</script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<!-- PieGage -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/piegage/piegage-demo.js"/>"></script>
<!-- Morris charts -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/raphael.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/morris/morris.js"/>"></script>
<!-- Morris charts demo -->
<script>
<c:if test="${buyerReadOnlyAdmin or eventPermissions.approverUser}">
$(window).bind('load', function() {
	var allowedFields = '#dashboard,.viewEnvelope,#downloadEnvelope,#idSumDownload,.evaluationSummaryReport,.downloadBuyerAuctionReport,.acrdian,#idOngoingProgTab,#idOngoingSumTab,#idOngoingEvalTab,#idOngoingMessageTab';
	<c:if test="${eventType eq 'RFA' and buyerReadOnlyAdmin and ((event.status eq 'CLOSED' || event.status eq 'COMPLETE' || event.status eq 'FINISHED') and event.viewAuctionHall)}">
		allowedFields +=',#idDutchConsole,#idEnglishConsole,.auctionBidSupplier,.downloadBuyerAuctionReport';
	</c:if>
	disableFormFields(allowedFields);
});

</c:if>

function updateProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
		progress(percentComplete, $('.progressbar'));
		var percentVal = percentComplete + '%';
		console.log(percentVal);
	} else {
		// Unable to compute progress information since the total size is
		// unknown
		console.log('unable to complete');
	}
}

	$(document).ready(function() {
		$('#page-content').find('#evaluationConlusionReport').hide();
		$('#page-content').find('#evaluationConlusionReport').remove();
		$('#disableMasking').click(function(){
			$('#removeBlackList').modal('show');
		});
	
		$('#cancelMasking').click(function(){
			$('#removeBlackList').modal('hide');
			$('#disableMaskingPwd').val('');
		});
		
		var passwordValidated = false;
		
		$(document).on("change", "#concludeEvalDocument", function() {
			$(".show_name").html($(this).val());
		});
		
		
		$('#concludeEvaluation').click(function(){
			$('#concludeEvaluationDialog').modal('show');
			if(!passwordValidated) {
				$('.concludeDetails').each(function (){
					$(this).hide();
				});
				$('.passwordDetails').show();
				$('.confirmMessage').hide();
				$('#headerText').html('Enter your password to Conclude Evaluation');
				$('#btnConcludeEvaluation').val('Conclude Evaluation');
				$(".show_name").html('');
				document.getElementById("concludeEvalDocument").value = "";
				document.getElementById("docDescription").value = "";
				$('#concludeEvaluationRemarks').val('');
			}
		});
	
		$('#cancelConcludeEvaluation').click(function(){
			$('#concludeEvaluationDialog').modal('hide');
			$('#concludeEvaluationPwd').val('');
			passwordValidated = false;
		});
		
		
		$('#btnConcludeEvaluation').click(function(e){
			e.preventDefault();
			if(!$('#frmConcludeEvaluation').isValid()) {
				return;
			}
			
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			if(!passwordValidated) {
				
				var ajaxUrl = getBuyerContextPath('validatePassword');

				$.ajax({
					url : ajaxUrl + "/" + $("#eventId").val(),
					type : 'POST',
					data : {
						'userData' : $("#concludeEvaluationPwd").val()
					},
					/* dataType : 'json', */
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success : function(data, textStatus, request) {
						var success = request.getResponseHeader('success');
						console.log(success);
						$.jGrowl(success, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});
						
						$('.concludeDetails').each(function (){
							$(this).show();
						});
						$('#headerText').html('Confirm Evaluation Conclusion');
						$('#btnConcludeEvaluation').val('Conclude');
						$('.passwordDetails').hide();
						$('.confirmMessage').show();
						passwordValidated = true;
						
					},
					error : function(request, textStatus, errorThrown) {
						$("#concludeEvaluationPwd").val('');
						var error = request.getResponseHeader('error');
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
						$.jGrowl(error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});
					},
					complete : function() {
						$('#loading').hide();
					}
				});
			
			} else {
				
				console.log('Submitting conclusion data....');
				
				var file_data = $('#concludeEvalDocument').prop('files')[0];
				var form_data = new FormData();
				if(file_data != undefined) {
					form_data.append('concludeEvalDocument', file_data);
					form_data.append("desc", $('#docDescription').val());
				}
				form_data.append("concludeEvaluationPwd", $("#concludeEvaluationPwd").val());
				form_data.append("concludeEvaluationRemarks", $("#concludeEvaluationRemarks").val());
				
				var ajaxUrl = getBuyerContextPath('concludeEvaluationPrematurely');
				
				$.ajax({
					url : ajaxUrl + "/" + $("#eventId").val(),
					data : form_data,
					cache : false,
					xhr : function() { // custom xhr
						myXhr = $.ajaxSettings.xhr();
						if (myXhr.upload) { 
							// check if upload property exists
							myXhr.upload.addEventListener('progress', updateProgress, false); 
							// for handling the progress of the upload
						}
						return myXhr;
					},
					type : "POST",
					enctype : 'multipart/form-data',
					processData : false,
					contentType : false,
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
						$('.progressbar').removeClass('flagvisibility');
					},
					success : function(data, textStatus, request) {
						var info = request.getResponseHeader('success');
						console.log("Success message : " + info);
						$(".show_name").html('');
						$('p[id=idGlobalSuccessMessage]').html(info);
						$('div[id=idGlobalSuccess]').show();
						$.jGrowl(info, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});
						
						window.location.href = getBuyerContextPath('envelopList/' + $("#eventId").val() + '?success='+info);
						
						// Dont hide loader as we want to keep it on until the page reloads.
					},
					error : function(request, textStatus, errorThrown) {
						var error = request.getResponseHeader('error');
						$('p[id=idGlobalErrorMessage]').html(error);
						$('div[id=idGlobalError]').show();

						$.jGrowl(error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});
						$('#loading').hide();
					},
					complete : function() {
						$('.progressbar').addClass('flagvisibility');
						progress(0, $('.progressbar'));
					}
				});
				
				passwordValidated = false;
			}
			
			//$('#frmConcludeEvaluation').submit();
			
			
		});
		
			
		if($('#permission').val().toString()=="true"){
			$('#viewEnvelopes-true').hide();
			
		}
		
		$(".closeEnvelope").click(function(){
				$("#evelopeCloseID").val($(this).data("id"));	
			 $('#closeEnv').modal('show');
			});
		
		$('.role-bottom').find('input[type="checkbox"]').change(function() {

			var roleUperBlok = '';
			$(this).parents('.role-bottom').find('input[type="checkbox"]:checked').each(function() {
				var roleValue = $(this).val();
				roleUperBlok += '<div class="role-upper-inner">' + roleValue + '<a href="#" data-val="' + roleValue + '">X</a></div>';
			});
			$(this).parents('.chk_scroll_box').prev('.role-upper').html(roleUperBlok);
		});

		/* this code for refresh multiselcte checkbox on load time */
		$('.role-bottom').find('input[type="checkbox"]').trigger('change');

		$(document).delegate('.role-upper a', 'click', function(e) {

			e.preventDefault();
			var checkboxVal = $(this).attr('data-val');
			var checkObj = $(this).parents('.role-upper').next('.chk_scroll_box').find('input[type="checkbox"][value="' + checkboxVal + '"]');

			checkObj.prop('checked', false);
			$(this).parent('.role-upper-inner').remove();
			$.uniform.update(checkObj);
		});

		$('#question_list').perfectScrollbar({
			suppressScrollX : true
		});

	});

    var flag= false;
	$(function() {

		$('[data-toggle="collapse"]').click(function(e) {
			e.preventDefault();
			var target_element = $(this).attr("href");
			var envelopId = $.trim($(this).attr('data-idenv'));
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getBuyerContextPath('bqCqList'),
				data : {
					'envelopId' : envelopId
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var html = '';
					if ($.isEmptyObject(data)) {
						html = 'No items found in this envelope.';
					}
					
					if(flag == false){
					   $('.breadcrumb').append('<li><spring:message code="label.rftenvelop" /></li>');
					}
					flag=true;
					
					$.each(data, function(i, item) {
						if (item.indexOf('Bill of Quan') != -1) {
							html += '<li>';
							html += '<span class="glyph-icon icon-separator">';
							html += '<i class="fa fa-table"></i>';
							html += '</span>';
							html += '<span class="button-content">' + item + '</span><br/>';
							html += '</li>';
						} else {
							html += '<li>';
							html += '<span class="glyph-icon icon-separator">';
							html += '<i class="fa fa-commenting-o"></i>';
							html += '</span>';
							html += '<span class="button-content">' + item + '</span><br/>';
							html += '</li>';
						}
					});
					$(target_element).find('.bqcqListsEnvlop').html(html);
				},
				error : function(error) {
					console.log(error);
				},
				complete : function() {
					$('#loading').hide();
				}
			});
			$(target_element).collapse('toggle');
			return false;
		});

		$(document).on("change", "#load_file", function() {
			$(".show_name").html($(this).val());
		});

	});
	$('.deleteEnvelop').click(function(e) {
		e.preventDefault();

		$('#confirmRemoveenvelopId').val($(this).siblings('.envelopId').val());
		$('#myModalEnvelopDelete').modal('show');
		return false;
	});
	$(document).delegate('#confirmRemoveenvlop', 'click', function(e) {
		e.preventDefault();
		var id = $('#confirmRemoveenvelopId').val();
		$('#myModalEnvelop').modal('hide');
		$('#delete_' + id).submit();
	});

	$('#submitPass').click(function(e) {

		e.preventDefault();

		if ($("#envlPass").val() == '' || !$('#idPasswordDialog').isValid()) {
			return;
		}

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		var ajaxUrl = getBuyerContextPath('openEnvelop');

		$.ajax({
			url : ajaxUrl,
			type : 'POST',
			data : {
				'envelopId' : $("#evelopeID").val(),
				'eventId' : $("#eventId").val(),
				'password' : $("#envlPass").val()
			},

			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				console.log(success);
				var envlip = $("#evelopeID").val();

				window.location.href = getBuyerContextPath('envelopList/' + $("#eventId").val() + '?success='+success);
				/*if (data === true) {
				console.log($('#envlp-' + envlip));
				$('#envlp-' + envlip).unbind("click");
				$('#envlp-' + envlip).removeClass("lockedEnv");
				$('#envlp-' + envlip).find(".oprtion").html('View');
				$('#envlp-' + envlip).find(".badge-info").html('<i class="fa fa-unlock" aria-hidden="true"></i>');
				
				} */
				$.jGrowl(success, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
				$('#closedEnvelopModal').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$("#envlPass").val('');
				var error = request.getResponseHeader('error');
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				$.jGrowl(error, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	
	
	
	$('#submitClose').click(function(e) {
		
		e.preventDefault();

		if ($("#closelPass").val() == '' || !$('#idPasswordDialog1').isValid()) {
			return;
		}

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		var ajaxUrl = getBuyerContextPath('closeEnvelope');
		
		$.ajax({
			url : ajaxUrl,
			type : 'POST',
			data : {
				'envelopId' : $("#evelopeCloseID").val(),
				'eventId' : $("#eventId").val(),
				'password' : $("#closePass").val()
			},

			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				console.log(success);
				var envlip = $("#evelopeID").val();

				window.location.href = getBuyerContextPath('envelopList/' + $("#eventId").val() + '?success='+success);
				/*if (data === true) {
				console.log($('#envlp-' + envlip));
				$('#envlp-' + envlip).unbind("click");
				$('#envlp-' + envlip).removeClass("lockedEnv");
				$('#envlp-' + envlip).find(".oprtion").html('View');
				$('#envlp-' + envlip).find(".badge-info").html('<i class="fa fa-unlock" aria-hidden="true"></i>');
				
				} */
				$.jGrowl(success, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
				$('#closedEnvelopModal').hide();
			},
			error : function(request, textStatus, errorThrown) {
				 $("#closePass").val('');
					var error = request.getResponseHeader('error');
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				$.jGrowl(error, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	$('.lockedEnv').click(function(e) {
		e.preventDefault();
		console.log($(this).data("envelopid"))
		$("#evelopeID").val($(this).data("envelopid"));
		$("#closedEnvelopModal").modal({
			backdrop : 'static',
			keyboard : false
		});
	});
	$(".showEvalutionDeclaration").click(function(e){
		e.preventDefault();
		console.log("envelopId:",$(this).data("envelopid"))
		$("#envelopId").val($(this).data("envelopid"));
			$("#evaluationDeclarationModal").dialog({
				modal : true,
				minWidth : 300,
				width : '50%',
				maxWidth : 600,
				minHeight : 200,
				maxHeight : 500,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded",
				backdrop: 'static',
				keyboard : false
			});
	});
	$('#acceptDeclaration').on('change', function() {
		if ($(this).is(':checked')) {
			$('.acceptDeclaration').removeClass('disabled');
		} else {
			$('.acceptDeclaration').addClass('disabled');
		}
	});
	$('#acceptButton').click(function(){
		$('.acceptDeclaration').addClass('disabled');
		$('#loading').show();
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/rftEnvelop.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
