<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#eventConclusion"><spring:message code="summarydetails.event.conclusion.title" /></a>
		</h4>
	</div>
	<div id="eventConclusion" class="panel-collapse collapse accortab">
		<div class=" panel-body">
			<div class="col-md-12 marg-bottom-20 marg-top-20 white-bg">
				<%-- <c:forEach items="${envelopList}" var="env"> --%>
				<c:if test="${nextEvent != null}">
					<div class="col-md-12 main-panal-box">
						<label><spring:message code="eventsummary.eventdetail.next" />: </label>
						<c:if test="${nextEvent.status == 'DRAFT'}">
							<p>
								<a href="${pageContext.request.contextPath}/buyer/${event.nextEventType}/createEventDetails/${event.nextEventId}">${nextEvent.eventName}
									- <i>[${nextEvent.eventId} ${event.nextEventType}]</i>
								</a>
							</p>
						</c:if>
						<c:if test="${nextEvent.status != 'DRAFT'}">
							<p>
								<a href="${pageContext.request.contextPath}/buyer/${event.nextEventType}/eventSummary/${event.nextEventId}">${nextEvent.eventName}
									- <i>[${nextEvent.eventId} ${event.nextEventType}]</i>
								</a>
							</p>
						</c:if>
					</div>
				</c:if>
				<div class="col-md-12 main-panal-box">
					<label><spring:message code="summarydetails.event.conclusion.remarks" />: </label>
					<p>${event.concludeRemarks}</p>
				</div>
				<c:if test="${nextEvent == null and !empty awardedSupplierList and eventType != 'RFI'}">
					<div class="col-md-12 main-panal-box">
						<label><spring:message code="summarydetails.event.conclusion.award.suppliers" />: </label>
						<c:forEach items="${awardedSupplierList}" var="awardedSupp" varStatus="idx">
							<ul style="list-style-type: none">
								<li class="col-md-12">${awardedSupp.companyName}</li>
							</ul>
						</c:forEach>
					</div>
				</c:if>
				<div class="col-md-12 main-panal-box">
						<label><spring:message code="summarydetails.event.conclusion.date" />: </label>
						<p>
							<fmt:formatDate value="${event.concludeDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${sessionScope['timeZone']}" />
						</p>
				</div>
				<c:if test="${nextEvent == null and !empty awardedSupplierList and eventType != 'RFI'}">
					<div class="col-md-12 main-panal-box">
						<label><spring:message code="summarydetails.event.conclusion.award.price" />: </label>
						<p>
							<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${event.awardedPrice}" />
						</p>
					</div>
				
				</c:if>
				<div class="col-md-12 main-panal-box">
						<label><spring:message code="application.action.by" />: </label>
						<p>${event.concludeBy.name}</p>
				</div>
			</div>
		</div>
	</div>
</div>