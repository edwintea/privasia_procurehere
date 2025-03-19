<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreatingSummaryDesk" code="application.rfx.create.summary" />
<spring:message var="rfxTeamMembersDesk" code="application.rfx.team.members" />
<spring:message var="rfxbidHistoryDesk" code="application.rfa.bid.history" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxCreatingSummaryDesk}, ${rfxTeamMembersDesk}, ${rfxbidHistoryDesk}] });
});
</script>
<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active">${eventType.value}${' '}</li>
				<li><spring:message code="application.summary" /></li>
			</ol>
			<jsp:include page="ongoinEventHeader.jsp" />
			<div class="clear" />
			<jsp:include page="summaryDetails.jsp" />
		</div>
	</div>






	<div class="modal fade" id="myModal-template" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<form action="${pageContext.request.contextPath}/buyer/${eventType}/copyEventTo" method="post">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-content" style="width: 50%; margin-left: 404px;">
					<div class="modal-header">
						<h3><spring:message code="eventsummary.which.businessunit" /></h3>
						<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
					</div>


					<div class="modal-body">

						<input type="hidden" name="templateId" value="${templateId}"> <input type="hidden" name="eventType" value="${eventType}"> <select name="businessUnitId" class="chosen-select disablesearch">
							<c:forEach items="${businessUnits}" var="businessUnit">
								<option value="${businessUnit.id}">${businessUnit.unitName}</option>
							</c:forEach>
						</select>

					</div>
					<div class="modal-footer border-none float-left width-100 pad-top-0 ">
						<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 "><spring:message code="application.next" /></button>
					</div>
				</div>
			</form>
		</div>
	</div>




</div>
<script type="text/javascript">
	
	
	
	$(document).ready(function() {
		
	<c:if test="${openModelForTemplateBu}">

	
	
	
	
 	$('#crateNewEvent').modal();
	</c:if>
	
		
		
	
		
	});


		/* Datepicker bootstrap */

		$(function() {
			"use strict";
			var nowTemp = new Date();
			var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

			$('.bootstrap-datepicker').bsdatepicker({
				format : 'dd/mm/yyyy',
				minDate : now,
				onRender : function(date) {
					return date.valueOf() < now.valueOf() ? 'disabled' : '';
				}

			}).on('changeDate', function(e) {
				$(this).blur();
				//$(this).val()
				$("#auctionResumeDateTime").val($(this).val());

				$(this).bsdatepicker('hide');
			});

			$('.timepicker-example').timepicker();
			$('.timepicker-example').change(function(e) {

				$("#auctionResumeTime").val($(this).val());
			});
			var hours = nowTemp.getHours();
			  var minutes = nowTemp.getMinutes();
			  var ampm = hours >= 12 ? 'PM' : 'AM';
			  hours = hours % 12;
			  hours = hours ? hours : 12; // the hour '0' should be '12'
			  minutes = minutes < 10 ? '0'+(minutes+1) : (minutes+1);
			  var onlyTime = hours + ':' + (minutes);
			  var strTime = onlyTime + ' ' + ampm;
			$("#auctionResumeTime").val(strTime);
			$("#auctionResumeDateTime").val($.datepicker.formatDate('dd/mm/yy', now));

		});
	</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script>
		$.validate({
			lang : 'en',
			modules : 'date'
		});
		
	</script>