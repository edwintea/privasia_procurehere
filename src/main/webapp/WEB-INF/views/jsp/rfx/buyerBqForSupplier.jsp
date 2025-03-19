<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfaPreBidBqDesk" code="application.rfa.pre.bid.bill.of.quantities" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfaPreBidBqDesk}] });
});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div id="sb-site">
	<jsp:useBean id="today" class="java.util.Date" />
	<c:choose>
		<c:when test="${event.decimal==1}">
			<c:set var="decimalSet" value="0,0.0"></c:set>
		</c:when>
		<c:when test="${event.decimal==2}">
			<c:set var="decimalSet" value="0,0.00"></c:set>
		</c:when>
		<c:when test="${event.decimal==3}">
			<c:set var="decimalSet" value="0,0.000"></c:set>
		</c:when>
		<c:when test="${event.decimal==4}">
			<c:set var="decimalSet" value="0,0.0000"></c:set>
		</c:when>
		<c:when test="${event.decimal==5}">
			<c:set var="decimalSet" value="0,0.00000"></c:set>
		</c:when>
		<c:when test="${event.decimal==6}">
			<c:set var="decimalSet" value="0,0.000000"></c:set>
		</c:when>
	</c:choose>
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<input type="hidden" value="${event.id}" id="eventId">
				<div class="container">
					<ol class="breadcrumb">
						<li>
							<a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
							</a>
						</li>
						<li class="active">
							<spring:message code="rfaevent.bq.supplier" />
						</li>
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap auction_hall_heading">
							<spring:message code="rfaevent.bq.supplier.case1" />
						</h2>
					</div>
					<div class="ports-tital-heading marg-bottom-10">
						<div class="row">
							<div class="col-md-8 col-xs-8">
								<div class="ports-tital-heading li-32">
									<spring:message code="rfaevent.bqsupplier.auction1" />
									: ${event.eventName}
								</div>
							</div>
						</div>
					</div>
					<div class="auction_hall">
						<jsp:include page="eventHeader.jsp" />
					</div>
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<input type="hidden" id="eventDecimal" value="${event.decimal}">
					<c:if test="${!isPresetPreBidForAllSuppliers}">
					<form action="${pageContext.request.contextPath}/buyer/${eventType}/getRfaSupplierBqItem/${event.id}" method="post">
						<div class="col-md-121">
							<div class="row marg-bottom-10">
								<div class="col-md-6">
									<select class="chosen-select disablesearch" data-validation="required" name="supplierId" id="supplierId">
										<option value=""><spring:message code="select.supplier.placeholder" /></option>
										<c:forEach items="${eventSuppliers}" var="supplier">
											<c:if test="${supplier.supplier.id eq supplierId }">
												<option value="${supplier.supplier.id}" selected="selected">${supplier.supplier.companyName}</option>
											</c:if>
											<c:if test="${supplier.supplier.id ne supplierId }">
												<option value="${supplier.supplier.id}">${supplier.supplier.companyName}</option>
											</c:if>
										</c:forEach>
									</select> <input class="btn btn-info hvr-pop hvr-rectangle-out marg-left-10" value="GO" type="submit" id="goBq" />
								</div>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</div>
						</div>
					</form>
					</c:if>
					<div class="clear"></div>
					<div class="row">
						<div class="col-md-12">
							<div class="table_f_action_btn">
								<form:form id="supplierBqList" method="post" action="${pageContext.request.contextPath}/buyer/RFA/submitSupplierBq/${event.id}" modelAttribute="rfaSupplierBq">
									<jsp:include page="/WEB-INF/views/jsp/rfx/auctionBq.jsp" />
									<div class="row pull-left">
										<div class="col-md-6">
											<input class="btn ph_btn btn-info hvr-pop hvr-rectangle-out margin-bottom-10 ${buyerReadOnlyAdmin ? 'disabled': '' }" value="Save" type="submit" id="saveBq" />
										</div>
									</div>
								</form:form>
								<form class="bordered-row pull-left" id="submitPriviousForm" method="post" action="${pageContext.request.contextPath}/buyer/${eventType}/bqListPrevious">
									<input type="hidden" id="eventId" value="${event.id}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<button type="submit" class="btn btn-lg btn-black hvr-pop hvr-rectangle-out1 marginDisableALl" value="Previous" name="previous" id="priviousStep">
										<spring:message code="application.previous" />
									</button>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerBqForSupplier.js" />">
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	<c:if test="${currentSupplier.confirmPriceSubmitted && event.status == 'SUSPENDED'}">
	$(window).bind('load', function() {
		var allowedFields = '#supplierId,#priviousStep,#goBq,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	
	<c:if test="${!currentSupplier.confirmPriceSubmitted && event.status == 'SUSPENDED' }">
	$(window).bind('load', function() {
		var allowedFields = '#supplierId,#priviousStep,#goBq,#saveBq,#unitPrice,#totalAmount,#tax,#field1,#field2,#field3,#field4,#additionalTax,#taxDesc,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	//validation for approver user
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#supplierId,#priviousStep,#goBq,.s1_view_desc';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	
	

	var decimalSet = '${decimalSet}';
	// set the date we're counting down to
	var target_date = new Date('Jan, 31, 2017').getTime();

	// variables for time units
	var days, hours, minutes, seconds;

	// get tag element
	//var countdown = document.getElementById('countdown1');
	//alert(countdown);
	// update the tag with id "countdown1" every 1 second
	setInterval(function() {

		// find the amount of "seconds" between now and target
		var current_date = new Date().getTime();
		var seconds_left = (target_date - current_date) / 1000;

		// do some time calculations
		days = parseInt(seconds_left / 86400);
		seconds_left = seconds_left % 86400;

		hours = parseInt(seconds_left / 3600);
		seconds_left = seconds_left % 3600;

		minutes = parseInt(seconds_left / 60);
		seconds = parseInt(seconds_left % 60);

		// format countdown string + set tag value
		htmldata = '<span class="days"><b>Days</b>' + days + '</span><span class="hours"><b>Hours</b>' + hours + '</span><span class="minutes"><b>Minutes</b>' + minutes + '</span><span class="seconds"><b>Seconds</b>' + seconds + '</span>';
		$('#countdown1').html(htmldata);

	}, 1000);

	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$(this).find('.header').css('top', $(this).scrollTop());

		});

		//$('select').select();
	});

	$(document).ready(function() {

	 	var sb=	${showSave}
		$('#saveBq').hide();
		if(sb != undefined){
		
		if(sb.toString()=="true"){
			$('#saveBq').show();	
		}
		}
		
		$(".Invited-Supplier-List").click(function() {
			$("#timer-accord").toggleClass("small-accordin-tab");
		});

		$('#submitBidPrice').click(function() {
			var eventId = $('#supplierBqList').find('[name="rfteventId"]').val();
			var supplierId = $('#supplierBqList').find('[name="supplierId"]').val();
			$('#supplierBqList').attr('action', getSupplierContextPath() + '/submitEnglishAuction/' + eventId);
			$('#supplierBqList').submit();
		});
	});

	$.validate({ lang : 'en',
	onfocusout : false,
	validateOnBlur : true,
	modules : 'date,sanitize' });
	
</script>
<style>
.chosen-container-single-nosearch {
	margin-left: 5px !important;
	width: 59% !important;
	text-align: left;
}

[name="taxType"] {
	display: none !important;
}

.deta.ph_table.table.bq-table {
	margin-bottom: 50px;
}

.marginDisableALl {
	margin: 0 !important;
}

.inputRight {
	text-align: right;
}

.ml-30 {
	margin-left: 30px;
}

.text-bold {
	font-weight: bold !important;
}
</style>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>