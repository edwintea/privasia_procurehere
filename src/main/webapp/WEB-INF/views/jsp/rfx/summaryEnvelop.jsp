<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion collapsed" href="#collapseNine"><spring:message code="rftenvelope.envelopes" /></a>
		</h4>
	</div>
	<div id="collapseNine" class="panel-collapse collapse accortab">
		<div class=" panel-body">
			<div class="width100 pull-left marg-bottom-20 marg-left-10" style="padding-right:20px;">
			<!-- issue 35 P2 functional transfer ownership -->
				<c:forEach items="${envelopList}" var="env">
					<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
						<div class="tag-line marg-left-10 marg-top-10">
							<h3 class="marg-bottom-10 Supplier-list-table">
								<i aria-hidden="true" class="glyph-icon icon-envelope${env.envelopType == 'OPEN' ? '-o' : ''}"></i> ${env.envelopTitle}
								<c:if test="${(event.status eq 'ACTIVE' or event.status eq 'CLOSED') and (eventPermissions.owner or loggedInUserId == env.leadEvaluater.id) and event.eventStart le now and env.evaluationStatus == 'PENDING'}">
									<button class="editEnvelopList sixbtn" data-id="${env.id}">
										<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
									</button>
								</c:if>
							</h3>
							<p class="marg-bottom-20">${env.description}</p>
						</div>
						<div class="col-md-12  main-panal-box">
							<div class="row">
								<div class="col-md-4">
									<ul class="s1_question_list bqcqListsEnvlop">
										<c:forEach items="${env.cqList}" var="cq">
											<li>${cq.name}[<spring:message code="questionnaire.label" />]</li>
										</c:forEach>
										<c:if test="${event.billOfQuantity}">
											<c:forEach items="${env.bqList}" var="bq">
												<li>${bq.name}[<spring:message code="eventdescription.billofquantity.label" />]</li>
											</c:forEach>
										</c:if>
										<c:if test="${event.scheduleOfRate}">
											<c:forEach items="${env.sorList}" var="bq">
												<li>${bq.name}[<spring:message code="eventdescription.schedule.rate.label" />]</li>
											</c:forEach>
										</c:if>
									</ul>
								</div>
							</div>
						</div>
						<div class="col-md-12 main-panal-box">
							<label><spring:message code="eventsummary.envelopes.type" />: </label>
							<p>${env.envelopType}</p>
						</div>
						<div class="col-md-12 main-panal-box">
							<label><spring:message code="eventsummary.envelopes.evaluation.owner" />: </label>
							<p>${env.leadEvaluater.name}</p>
						</div>
							
						
						<div class="col-md-12 main-panal-box">
							<label><spring:message code="eventsummary.envelopes.evaluation.team" />:</label>
							<c:if test="${empty env.evaluators}">
							-
							</c:if>
							<c:forEach items="${env.evaluators}" var="evalTeam" varStatus="idx">
								<ul style="list-style-type: none">
									<li class="col-md-12">${idx.index + 1}.${evalTeam.user.name}</li>
								</ul>
							</c:forEach>
						</div>
						
						<c:if test="${event.rfxEnvelopeOpening}">
						<div class="col-md-12 main-panal-box">
							<label><spring:message code="envelope.sequence.opening" />: </label>
							<p>${env.envelopSequence}</p>
						</div>
						</c:if>
						<c:if test="${env.envelopType != 'OPEN'}">
							<div class="col-md-12 main-panal-box">
								<label><spring:message code="eventsummary.envelope.opener" />: </label>
								<div class="envopenpos">
							 <c:forEach items="${env.openerUsers}" var="openerUser" varStatus="idx">
									${idx.index + 1}.${openerUser.user.name}</br>
							</c:forEach>
							</div>
							</div>
						</c:if>
					</div>
				</c:forEach>
				<c:if test="${event.enableEvaluationConclusionUsers && !empty event.evaluationConclusionEnvelopeNonEvaluatedCount }">
					<c:set var="haveUserConcluded" value="${false}" />
					<c:set var="haveUserConcludedWithAttachment" value="${false}" />
					<c:forEach items="${event.evaluationConclusionUsers}" var="usr" varStatus="idx">
						<c:if test="${usr.concluded}">
							<c:set var="haveUserConcluded" value="${true}" />
						</c:if>
						<c:if test="${!empty usr.fileName }">
							<c:set var="haveUserConcludedWithAttachment" value="${true}" />
						</c:if>
					</c:forEach>

					<c:if test="${haveUserConcluded}">
						<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
							<div class="row">
									<div class="col-md-12">
										<div class="row marg-top-10">
											<div class="col-md-12 marg-top-10">
												<div class="col-md-3">
													<label>Evaluation Concluded Prematurely:</label>
												</div>
												<div class="col-md-9">
													<label>	YES</label>
												</div>
											</div>
											<div class="col-md-12 marg-top-10">
												<div class="col-md-3">
													<label>Envelopes Evaluated: </label>
												</div>
												<div class="col-md-9">${event.evaluationConclusionEnvelopeEvaluatedCount != null ? event.evaluationConclusionEnvelopeEvaluatedCount : 'N/A' }</div>
											</div>
											<div class="col-md-12 marg-top-10">
												<div class="col-md-3">
													<label>Envelopes Unevaluated: </label>
												</div>
												<div class="col-md-9">${event.evaluationConclusionEnvelopeNonEvaluatedCount != null ? event.evaluationConclusionEnvelopeNonEvaluatedCount : 'N/A' }</div>
											</div>
											<div class="col-md-12 marg-top-10">
												<div class="col-md-3">
													<label>Disqualified Suppliers: </label>
												</div>
												<div class="col-md-9">${event.evaluationConclusionDisqualifiedSupplierCount != null ? event.evaluationConclusionDisqualifiedSupplierCount : 'N/A' }</div>
											</div>
											<div class="col-md-12 marg-top-10">
												<div class="col-md-3">
													<label>Remaining Qualified Suppliers: </label>
												</div>
												<div class="col-md-9">${event.evaluationConclusionRemainingSupplierCount != null ? event.evaluationConclusionRemainingSupplierCount : 'N/A' }</div>
											</div>
											<div class="col-md-12 marg-top-10" >
												<div class="col-md-3">
													<label>Evaluation Conclusion Owners: </label>
												</div>
											</div>
											<div class="col-md-12">
												<c:set var="indexNo" value="1" />
												<c:forEach items="${event.evaluationConclusionUsers}" var="usr" varStatus="idx">
													<c:if test="${usr.concluded}">
														<ul style="list-style-type: none">
															<li class="col-md-12 marg-top-10">
																${indexNo}. ${usr.user.name} - 
															 	(<fmt:formatDate value="${usr.concludedTime}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />)
															 	<div class="col-md-12 marg-top-10">
																	<div class="col-md-3">
																		<label>Remarks: </label>
																	</div>
																</div>
																<div class="col-md-12" style="margin-left: 14px; word-break: break-all">
																		${usr.remarks}
																</div>
																<c:if test="${empty usr.fileName }">
																	<div class="col-md-12 marg-top-10">
																		<div class="col-md-3">
																			<label>Attachment: </label>
																			N/A
																		</div>
																	</div>
																</c:if>
																<c:if test="${!empty usr.fileName }">
																	<div class="col-md-12 marg-top-10">
																		<div class="col-md-12">
																			<label>Attachment:</label>
																			<label>
																				<form:form method="GET">
																				<c:url var="download" value="/buyer/${eventType}/downloadEvalConclusionAttachment/${event.id}/${usr.id}" />
																					<a class="downloadSnapshot" href="${download}">${usr.fileName}</a>
																				</form:form>
																			</label>
																		</div>
																	</div>
																</c:if>
																<div class="col-md-12 marg-top-10">
																	<div class="col-md-12">
																		<label>File Description:</label>
																		${!empty usr.fileDesc? usr.fileDesc:'N/A'}
																	</div>
																</div>
															</li>
														</ul>
														<c:set var="indexNo" value="${indexNo + 1}" />
													</c:if>
												</c:forEach>
											</div>
										</div>
									</div>
							</div>
						</div>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>
</div>

<style>

.envopenpos {
     position: relative; 
     float: left;
}
</style>