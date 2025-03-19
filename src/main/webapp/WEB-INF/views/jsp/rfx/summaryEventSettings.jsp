<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
.rowLeftMargin0 {
	margin-left: 0px !important;
}

.secondColumnMarginMinus15 {
	margin-left: -15px !important;
}
</style>

<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseEventSettings"><spring:message code="eventDetails.event.settings" /> </a>
		</h4>
	</div>
	<div id="collapseEventSettings" class="panel-collapse collapse">
		<div class="panel-body pad_all_15  border-bottom">
			<div class="row rowLeftMargin0">
				<div class="main-panal-box">
					<label><spring:message code="rfi.createrfi.urgent.event" /> : </label>
					<p>${event.urgentEvent? 'Yes': 'No'}</p>
				</div>
				<div class="main-panal-box secondColumnMarginMinus15">
					<label><spring:message code="rfi.createrfi.allow.suspend" /> : </label>
					<p>${event.allowToSuspendEvent? 'Allowed':'Not Allowed'}</p>
				</div>
			</div>
			<div class="row rowLeftMargin0">
				<div class="main-panal-box">
					<label><spring:message code="rfi.createrfi.allow.close" /> : </label>
					<p>${event.closeEnvelope? 'Allowed':'Not Allowed'}</p>
				</div>
				<div class="main-panal-box secondColumnMarginMinus15">
					<label><spring:message code="summaryDetails.unmasking.owners" /> : </label>
					<p>
						<c:if test="${(!event.viewSupplerName) and ( !empty unMaskedUser or !empty event.unMaskedUsers )}">
							<ul style="margin-left: -40px;">
								<c:if test="${!empty unMaskedUser.name }">
									<li>${unMaskedUser.name}<br /> ${unMaskedUser.loginId}<br />
									</li>
								</c:if>
								<c:forEach items="${event.unMaskedUsers}" var="unMaskedUser">
									<li>${unMaskedUser.user.name}<br /> ${unMaskedUser.user.loginId}<br />
									</li>
								</c:forEach>
							</ul>
						</c:if>
						<c:if test="${event.viewSupplerName}">
							<p>N/A</p>
						</c:if>
					</p>
				</div>
			</div>
			<div class="row rowLeftMargin0">

				<div class="main-panal-box">
					<label><spring:message code="summaryDetails.evaluation.declaration" /> : </label>
					<p>${event.enableEvaluationDeclaration and event.evaluationProcessDeclaration !=null? event.evaluationProcessDeclaration.title:'N/A'}</p>
				</div>
				<div class="main-panal-box secondColumnMarginMinus15">
					<label><spring:message code="summaryDetails.eventdetail.evaluationConclusion" /> : </label>
					<div class="main-panal-box" style="margin-top: 0px;">
						<c:if test="${event.enableEvaluationConclusionUsers and !empty event.evaluationConclusionUsers }">
							<ul style="margin-left: -40px;">
								<c:forEach items="${event.evaluationConclusionUsers}" var="user">
									<li>${user.user.name}<br /> ${user.user.loginId}<br />
									</li>
								</c:forEach>
							</ul>
						</c:if>
						<c:if test="${!event.enableEvaluationConclusionUsers}">
							<p>N/A</p>
						</c:if>
					</div>
				</div>
			</div>

			<div class="row rowLeftMargin0">
				<div class="main-panal-box">
					<label><spring:message code="envelope.sequence.opening" /> : </label>
					<c:if test="${event.rfxEnvelopeOpening}">
						<c:if test="${event.rfxEnvOpeningAfter == 'OPENING'}">
							<p>After each Opening</p>
						</c:if>
						<c:if test="${event.rfxEnvOpeningAfter == 'EVALUATION'}">
							<p>After each Evaluation</p>
						</c:if>
					</c:if>
					<c:if test="${!event.rfxEnvelopeOpening}">
						<p>N/A</p>
					</c:if>
				</div>
					<div class="main-panal-box secondColumnMarginMinus15">
						<label><spring:message code="envelope.sequence.opening.allow" /> : </label>
						<p>${event.allowDisqualifiedSupplierDownload? 'Allowed':'Not Allowed'}</p>
					</div>
			</div>
			<c:if test="${eventType eq 'RFA'}">
				<div class="row rowLeftMargin0">
					<div class="main-panal-box ">
						<label>Allow viewing of Auction Hall after event ends: </label>
						<p>${event.viewAuctionHall? 'Allowed':'Not Allowed'}</p>
					</div>

					<div class="main-panal-box secondColumnMarginMinus15">
						<label><spring:message code="summaryDetails.revert.lastbid.owner" /> : </label>
						<c:if test="${(event.revertLastBid) and (!empty revertBidUser)}">
							<p>${revertBidUser.name}<br />${revertBidUser.loginId}<br />${revertBidUser.phoneNumber}</p>
						</c:if>
						<c:if test="${!event.revertLastBid}">
							<p>N/A</p>
						</c:if>
					</div>
				</div>
			</c:if>
			<div class="row rowLeftMargin0">
				<div class="main-panal-box">
					<label><spring:message code="summaryDetails.supplier.declaration" /> : </label>
					<p>${event.enableSupplierDeclaration and event.supplierAcceptanceDeclaration !=null? event.supplierAcceptanceDeclaration.title:'N/A'}</p>
				</div>
				<c:if test="${eventType ne 'RFA'}">
					<div class="main-panal-box secondColumnMarginMinus15">
						<label><spring:message code="rfi.createrfi.allow.add" /> : </label>
						<p>${event.addSupplier? 'Allowed':'Not Allowed'}</p>
					</div>
				</c:if>
			</div>
			

		</div>
	</div>
</div>
