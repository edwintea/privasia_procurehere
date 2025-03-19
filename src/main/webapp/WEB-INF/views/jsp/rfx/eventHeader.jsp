<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard" style="display: ${((eventPermissions.owner or eventPermissions.viewer or eventPermissions.editor) and (event.status eq 'DRAFT' or event.status eq 'SUSPENDED')) ? 'inline' : 'none'};">
		<ul>
			<li class="tb_1 ${event.eventDetailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/createEventDetails/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${event.eventDetailCompleted ? '' : 'tab'}" style="${event.eventDetailCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> 
			<span class="step_num">1</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="eventdetails.event.details" />
				</span>
			</a>
			</li>
			<li class="tb_2 <c:if test="${event.documentReq != true}"> flagvisibility</c:if> ${event.documentCompleted ? (step == '2' ? 'active' : 'completed' ) : (step == '2' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/createEventDocuments/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${event.eventDetailCompleted || event.documentCompleted ? '' : 'tab'}" style="${event.documentCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span
							class="step_num">2</span> <img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="application.document" />
				</span>
			</a></li>
			<c:set var="next" value="${false}" />
			<c:choose>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<li class="tb_3 <c:if test="${event.eventVisibility == 'PUBLIC'}"> flagvisibility</c:if> ${event.supplierCompleted ? (step == '3' ? 'active' : 'completed' ) : (step == '3' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/addSupplier/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${next || event.supplierCompleted  ? '' : 'tab'}" style="${event.supplierCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">3</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="application.supplier.detail" />
				</span>
			</a></li>
			<c:set var="next" value="${false}" />
			<c:choose>
				<c:when test="${event.eventVisibility != 'PUBLIC'}">
					<c:set var="next" value="${event.supplierCompleted}" />
				</c:when>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<li class="tb_4 <c:if test="${event.meetingReq != true}"> flagvisibility</c:if>  ${event.meetingCompleted ? (step == '4' ? 'active' : 'completed' ) : (step == '4' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/meetingList/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${next || event.meetingCompleted ? '' : 'tab'}" style="${event.meetingCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">4</span> <img
							src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="label.meeting" />
				</span>
			</a></li>
			<c:set var="next" value="${false}" />
			<c:choose>
				<c:when test="${event.meetingReq}">
					<c:set var="next" value="${event.meetingCompleted}" />
				</c:when>
				<c:when test="${event.eventVisibility != 'PUBLIC'}">
					<c:set var="next" value="${event.supplierCompleted}" />
				</c:when>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<li class="tb_5 <c:if test="${event.questionnaires != true}"> flagvisibility</c:if> ${event.cqCompleted ? (step == '5' ? 'active' : 'completed' ) : (step == '5' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/eventCqList/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${ next || event.cqCompleted  ? '' : 'tab'}" style="${event.cqCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">5</span> <img
							src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="application.questionnaire" />
				</span>
			</a></li>
			<c:set var="next" value="${false}" />
			<c:choose>
				<c:when test="${event.questionnaires}">
					<c:set var="next" value="${event.cqCompleted}" />
				</c:when>
				<c:when test="${event.meetingReq}">
					<c:set var="next" value="${event.meetingCompleted}" />
				</c:when>
				<c:when test="${event.eventVisibility != 'PUBLIC'}">
					<c:set var="next" value="${event.supplierCompleted}" />
				</c:when>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<li class="tb_6 <c:if test="${event.billOfQuantity != true}"> flagvisibility</c:if> ${event.bqCompleted ? (step == '6' ? 'active' : 'completed' ) : (step == '6' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/createBQList/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${next || event.bqCompleted ? '' : 'tab'}" style="${event.bqCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">6</span> <img
							src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="eventdescription.billofquantity.label" />
				</span>
			</a></li>
			<c:set var="next" value="${false}" />
			<c:choose>
				<c:when test="${event.billOfQuantity}">
					<c:set var="next" value="${event.bqCompleted}" />
				</c:when>
				<c:when test="${event.questionnaires}">
					<c:set var="next" value="${event.cqCompleted}" />
				</c:when>
				<c:when test="${event.meetingReq}">
					<c:set var="next" value="${event.meetingCompleted}" />
				</c:when>
				<c:when test="${event.eventVisibility != 'PUBLIC'}">
					<c:set var="next" value="${event.supplierCompleted}" />
				</c:when>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<li class="tb_7 <c:if test="${event.scheduleOfRate != true}"> flagvisibility</c:if> ${event.sorCompleted ? (step == '7' ? 'active' : 'completed' ) : (step == '7' ? 'current' : '' )}">
				<c:url value="/buyer/${eventType}/createSorList/${event.id}" var="createUrl" />
				<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${next || event.sorCompleted ? '' : 'tab'}" style="${event.sorCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">7</span> <img
						src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="eventdescription.schedule.rate.label" />
				</span>
				</a></li>
			<c:choose>
				<c:when test="${event.scheduleOfRate}">
					<c:set var="next" value="${event.sorCompleted}" />
				</c:when>
				<c:when test="${event.billOfQuantity}">
					<c:set var="next" value="${event.bqCompleted}" />
				</c:when>
				<c:when test="${event.questionnaires}">
					<c:set var="next" value="${event.cqCompleted}" />
				</c:when>
				<c:when test="${event.meetingReq}">
					<c:set var="next" value="${event.meetingCompleted}" />
				</c:when>
				<c:when test="${event.eventVisibility != 'PUBLIC'}">
					<c:set var="next" value="${event.supplierCompleted}" />
				</c:when>
				<c:when test="${event.documentReq}">
					<c:set var="next" value="${event.documentCompleted}" />
				</c:when>
				<c:otherwise>
					<c:set var="next" value="${true}" />
				</c:otherwise>
			</c:choose>
			<c:if test="${(event.billOfQuantity or event.questionnaires or event.scheduleOfRate)}">
				<li class="tb_8 ${event.envelopCompleted ? (step == '8' ? 'active' : 'completed' ) : (step == '8' ? 'current' : '' )}">
				<c:url value="/buyer/${eventType}/envelopList/${event.id}" var="createUrl" />
				<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${next || event.envelopCompleted ? '' : 'tab'}" style="${event.envelopCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">8</span> <img
								src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description"> <spring:message code="application.submission" />
					</span>
				</a></li>
			</c:if>
			<li class="tb_9  ${event.summaryCompleted ? (step == '9' ? 'active' : 'completed' ) : (step == '9' ? 'current' : '' )}">
			<c:url value="/buyer/${eventType}/eventSummary/${event.id}" var="createUrl" />
			<a href="${eventPermissions.viewer ? '#' : createUrl}" id="bubble" data-toggle="${event.envelopCompleted ? '' : 'tab'}" style="${event.summaryCompleted ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step"> <span class="inner_circle"> <span class="step_num">9</span> <img
							src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
					</span>
				</label> <span class="wizard-description"> <spring:message code="application.summary" />
				</span>
			</a></li>
		</ul>
	</div>
</div>
<div class="white-bg border-all-side float-left width-100 pad_all_10">
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="tag-line">
				<h2>
					<b>${eventType}:</b> ${event.eventName}
					<c:if test="${rfxTemplate.templateName != null}">
					<span class="pull-right"><b><spring:message code="application.event.template.name" /> :</b> ${rfxTemplate.templateName}</span>
					</c:if>
				</h2>
				<p>
					<b><spring:message code="prtemplate.case.id" />:</b> ${event.eventId}
				</p>
				<p>
					<b><spring:message code="application.eventowner" />:</b> ${event.eventOwner.name}/${event.eventOwner.communicationEmail}
				</p>
				<c:if test="${eventType eq 'RFA'}">
					<p><b><spring:message code="rfx.auction.type.label" /></b> :${event.auctionType.value}</p>
				</c:if>
			</div>
			<div class="print-down">
				<!-- Below download button should display only event status has completed.
					Below Download Buttons should access only Admin, owner, editor and Viewer.
				-->
				
				<c:if test="${eventPermissions.owner or eventPermissions.editor or eventPermissions.viewer or isAdmin}">
					<c:if test="${(event.status eq 'COMPLETE' or event.status eq 'FINISHED')}">
						<c:if test="${empty event.evaluationConclusionEnvelopeEvaluatedCount}">
						<div class="pull-right">
							<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/evaluationSummary/${event.id}">
								<button type="submit" class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idEvalDownload" data-toggle="tooltip" data-placement="top" data-original-title="Download Evaluation Summary">
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
									</span> <span class="button-content pad_all_15">Evaluation Summary</span>
								</button>
							</form:form>
						</div>
						</c:if>
					</c:if>
					<div class="pull-right">
						<c:if test="${event.status ne 'DRAFT'}">
							<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationSummary/${event.id}">
								<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title="Download Event Summary">
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
									</span> <span class="button-content pad_all_15">Event Summary</span>
								</button>
							</form:form>
						</c:if>
					</div>

					<%-- <c:if test="${eventType eq 'RFA' and (event.status eq 'FINISHED' or event.status eq 'COMPLETE' or (event.auctionType == 'REVERSE_SEALED_BID' or event.auctionType == 'FORWARD_SEALED_BID' ? (false) : (event.status eq 'CLOSED') ) )}">
						<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadBuyerAuctionReport/${event.id}">
							<button class="download" id="idSumDownload">
								<button class="btn float-right btn-info hvr-pop hvr-rectangle-out" type="submit">&nbsp; Download Auction Report &nbsp;</button>
							</button>
						</form:form>
					</c:if> --%>
				</c:if>
				<c:if test="${eventPermissions.owner or isAdmin or eventPermissions.conclusionUser or eventPermissions.leadEvaluator or buyerReadOnlyAdmin}">
					<c:if test="${(event.status eq 'COMPLETE' or event.status eq 'FINISHED')}">
						<c:if test="${event.enableEvaluationConclusionUsers and eventPermissions.allUserConcludedPermatury}">
							<div class="pull-right"  id="evaluationConlusionReport">
								<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/downloadEvaluationConclustionReport/${event.id}">
									<button type="submit" class="btn btn-sm float-right btn-warning hvr-pop marg-left-10" id="idEvalConclusionDownload" data-toggle="tooltip" data-placement="top" data-original-title="Download Evaluation Conclusion Report">
										<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
										</span> <span class="button-content pad_all_15"><spring:message code="eventReport.evaluation.conclusion.button" /></span>
									</button>
								</form:form>
							</div>
						</c:if>
					</c:if>
				</c:if>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmLeavePageOnSave" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="event.sure.to.leave" /></h3>
				<button class="close for-absulate" id="closeConfirmLeavePageDialog" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="event.discard.all.changes" /> </label> <input type="hidden" id="currentClickedUrl" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="leavePageModal"><spring:message code="application.continue.button" /></button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" id="stayOnpageModal"><spring:message code="application.stay.button" /></button>
			</div>
		</div>
	</div>
</div>
<script>
	renumberWizard();
	function renumberWizard() {
		$('#form-wizard-2').find('li.flagvisibility').hide().removeClass('flagvisibility');
		$('#form-wizard-2').find('li:visible').each(function(i) {
			$(this).find('.step_num').text(i + 1);
		});
	}
	var isChange;
	$(document).ready(function() {
		$('#page-content').find("input, select, textarea").change(function() {
			isChange = true;
		});
		$('#form-wizard-2 li a').click(function() {
			if (($(this).attr('data-toggle') == undefined || $(this).attr('data-toggle') == '') && isChange == true && $(this).attr('href') != '#') {
				$('#currentClickedUrl').val($(this).attr('href'));
				$('#confirmLeavePageOnSave').modal({
					backdrop : 'static',
					keyboard : false
				});
				return false;
			}
		});
		$('#leavePageModal').click(function() {
			var targetUrl = $('#currentClickedUrl').val();
			window.location.href = targetUrl;
			$('#confirmLeavePageOnSave').modal('hide');
		});
		$('#stayOnpageModal').click(function() {
			$('#currentClickedUrl').val('');
			$('#confirmLeavePageOnSave').modal('hide');
		});
	});
</script>
