<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="example-box-wrapper wigad-new">
	<div id="form-wizard-2" class="form-wizard">
		<ul>
		
		<!-- this.urgentForm = Boolean.FALSE;
		this.formDetailCompleted = Boolean.FALSE;
		this.bqCompleted = Boolean.FALSE;
		this.cqCompleted = Boolean.FALSE;
		this.summaryCompleted = Boolean.FALSE; -->
		
		
			<li class="tb_1  ${sourcingFormRequest.formDetailCompleted ? (step == '1' ? 'active' : 'completed' ) : (step == '1' ? 'current' : '' )} ">
				<a href="${pageContext.request.contextPath}/buyer/createSourcingFormDetails/${sourcingFormRequest.id}" id="bubble" data-toggle="${sourcingFormRequest.formDetailCompleted ? '' : 'tab'}">
					<label class="wizard-step"> <span class="inner_circle">
							<span class="step_num">1</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description">
						<spring:message code="formdetails.form.details" />
					</span>
				</a>
			</li>
			<li class="tb_2 ${sourcingFormRequest.documentCompleted ? (step == '2' ? 'active' : 'completed' ) : (step == '2' ? 'current' : '' )}"><a href="${pageContext.request.contextPath}/buyer/rfsDocument/${sourcingFormRequest.id}" id="bubble" data-toggle="${sourcingFormRequest.formDetailCompleted ? '' : 'tab'}" style="${sourcingFormRequest.documentCompleted  ?'pointer-events: auto;':'pointer-events:none;'}"> <label class="wizard-step">
						<span class="inner_circle">
							<span class="step_num">2</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label> <span class="wizard-description">
						<spring:message code="application.document" />
					</span>
			</a></li>
			
			
			<li class="tb_3 <%-- <c:if test="${sourcingFormRequest.questionnaires != true}"> flagvisibility</c:if> --%> ${sourcingFormRequest.cqCompleted ? (step == '3' ? 'active' : 'completed' ) : (step == '3' ? 'current' : '' )} ">
				<a href="${pageContext.request.contextPath}/buyer/sourcingFormRequestCqList/${sourcingFormRequest.id}" id="bubble" data-toggle="${ next || sourcingFormRequest.cqCompleted  ? '' : 'tab'}">
					<label class="wizard-step"> <span class="inner_circle">
							<span class="step_num">3</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description">
						<spring:message code="application.questionnaire" />
					</span>
				</a>
			</li>
			<c:set var="next" value="${false}" />
		
			
			<li class="tb_4 <%-- <c:if test="${sourcingFormRequest.billOfQuantity != true}"> flagvisibility</c:if> --%> ${sourcingFormRequest.bqCompleted ? (step == '4' ? 'active' : 'completed' ) : (step == '4' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/createSourcingRequestBqList/${sourcingFormRequest.id}" id="bubble" data-toggle="${next || sourcingFormRequest.bqCompleted ? '' : 'tab'}">
					<label class="wizard-step"> <span class="inner_circle">
							<span class="step_num">4</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description">
						<spring:message code="eventdescription.billofquantity.label" />
					</span>
				</a>
			</li>
			<li class="tb_5 <%-- <c:if test="${sourcingFormRequest.billOfQuantity != true}"> flagvisibility</c:if> --%> ${sourcingFormRequest.sorCompleted ? (step == '5' ? 'active' : 'completed' ) : (step == '5' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/createSourcingRequestSorList/${sourcingFormRequest.id}" id="bubble" data-toggle="${next || sourcingFormRequest.sorCompleted ? '' : 'tab'}">
					<label class="wizard-step"> <span class="inner_circle">
							<span class="step_num">5</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description">
						<spring:message code="eventdescription.schedule.rate.label" />
					</span>
				</a>
			</li>
			<c:set var="next" value="${false}" />
			<li class="tb_6 ${sourcingFormRequest.summaryCompleted ? (step == '6' ? 'active' : 'completed' ) : (step == '6' ? 'current' : '' )}">
				<a href="${pageContext.request.contextPath}/buyer/sourcingRequestSummary/${sourcingFormRequest.id}" id="bubble" data-toggle="${sourcingFormRequest.bqCompleted ? '' : 'tab'}">
					<label class="wizard-step"> <span class="inner_circle">
							<span class="step_num">6</span>
							<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
						</span>
					</label>
					<span class="wizard-description">
						<spring:message code="application.summary" />
					</span>
				</a>
			</li>
		</ul>
	</div>
</div>
<div class="white-bg border-all-side float-left width-100 pad_all_10">
	<div class="row">
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="tag-line">
				<h2>
					<b><spring:message code="rfs.sourcing.form" />:</b> ${sourcingFormRequest.sourcingFormName}
				</h2>
				<p>
					<b><spring:message code="prtemplate.case.id" />:</b> ${sourcingFormRequest.formId}
				</p>
				<p>
					<b><spring:message code="rfs.from.owner2" />:</b> ${sourcingFormRequest.formOwner.name}/${sourcingFormRequest.formOwner.communicationEmail}
				</p>
			</div>
		</div>
	</div>
</div>
