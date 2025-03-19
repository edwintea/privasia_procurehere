<%@page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="supplierRfxBqDesk" code="application.supplier.rfx.bill.of.quantities" />
<sec:authentication property="principal.bqPageLength" var="bqPageLength" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierRfxBqDesk}] });
});
</script>
<script type="text/javascript">
	var stompClient = null;
	function setConnected(connected) {
		document.getElementById('connect').disabled = connected;
		document.getElementById('disconnect').disabled = !connected;
		document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
		document.getElementById('response').innerHTML = '';
	}

	function connect() {
		var socket = new SockJS('${pageContext.request.contextPath}/auctions');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			//setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/auctionTopic/${event.id}', function(messageOutput) {
				console.log("the responce : " + messageOutput.body);
				var redirectUrl = getContextPath() + "/supplier/viewBqList/${eventType}/${event.id}";
				if (messageOutput.body == 'CLOSED') {
					window.location.href = redirectUrl;
				} else if (messageOutput.body == 'SUSPENDED') {
					window.location.href = redirectUrl;
				}else if(messageOutput.body == 'RESUME'){
					//showMessageOutput("Event has been closed.");
					window.location.href = redirectUrl;
				}
			});
		});
	}

	var errorHandler = function() {
		console.log("Connection lost..... reconnecting...");
	    setTimeout(function(){ connect(); }, 5 * 1000); // retry connection in 5 secs
	}

	function disconnect() {
		if (stompClient != null && stompClient.ws.readyState === stompClient.ws.OPEN) {
			stompClient.disconnect();
		}
		console.log("Disconnected");
	}

	$(window).unload(function(){
		if(stompClient.connected) {
			console.log('Closing websocket');
			disconnect();
		}
	});
	
	$(document).ready(function() {
		connect();
	});
	
	
	var allowBqChanges=${eventSupplier.submissionStatus =='REJECTED' && eventSupplier.isRejectedAfterStart};
	
</script>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- page title block -->
			<input type="hidden" id="bqPageLength" value="${bqPageLength}"> <input type="hidden" id="eventCurrency" value="${event.baseCurrency}"> <input type="hidden" id="eventDecimal" value="${event.decimal}">
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
			<div class="clear"></div>
			<c:if test="${eventType == 'RFA' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && eventSupplier.revisedBidSubmitted == false && (eventSupplier.auctionRankingOfSupplier  != null and eventSupplier.numberOfBids ge 0)}">
				<c:set var="warn" value="You have not yet submitted your revised auction Bill of Quantities" scope="request" />
			</c:if>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div id="newDiv">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			</div>
			<jsp:include page="supplierSubmissionHeader.jsp" />
			<c:if test="${!(eventSupplier.submissionStatus != 'INVITED' and ((eventType != 'RFA' and today ge event.eventStart) or eventType eq 'RFA') and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY'))}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-5" class="tab-content">
						<div class="doc-fir-inner1">
							<h3><spring:message code="supplierevent.bq.not.allow"/></h3>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${eventSupplier.submissionStatus != 'INVITED' and ((eventType != 'RFA' and today ge event.eventStart) or eventType eq 'RFA') and (event.suspensionType != 'DELETE_NOTIFY' and event.suspensionType != 'DELETE_NO_NOTIFY')}">
				<div class="tab-main-inner pad_all_15">
					<div id="tab-5" class="tab-content">
						<div class="doc-fir-inner1">
							<h3>${ eventType == 'RFA' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) ? 'Revised' : '' }Bill of Quantity</h3>
							<div class="Invited-Supplier-List dashboard-main">
								<div class="Invited-Supplier-List-table add-supplier id-list">
									<div class="ph_tabel_wrapper">
										<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table  mega1 questionnaire-table1">
											<table class="table ph_table border-none header documents-page fix-top-head " width="100%" border="0" cellspacing="0" cellpadding="0">
												<thead>
													<tr>
														<th class=" align-center width_50_fix"><spring:message code="supplier.no.col"/></th>
														<th class=" align-center width_200_fix"><spring:message code="application.name"/></th>
														<th class=" align-center width_200_fix"><spring:message code="application.createddate"/></th>
														<th class=" align-center width_200_fix"><spring:message code="application.cq.completion.status" /></th>
														<!-- <th class=" align-center width_200_fix">Modified Date</th> -->
														<th class=" align-center width_200_fix"></th>
													</tr>
												</thead>
											</table>
											<c:forEach var="bqObj" items="${bqList}" varStatus="idx">
												<table class="ph_table table border-none tab_trans timezone induslist documents-page" width="100%" border="0" cellspacing="0" cellpadding="0">
													<tbody class="catecontent">
														<tr>
														<c:set var="submStatus" value="${supplierBqList[idx.index].supplierBqStatus}"/>
														   <c:if test="${bqObj.bqOrder != null}">
															 <td class="width_50_fix">${bqObj.bqOrder}</td>
														   </c:if>
														   <c:if test="${bqObj.bqOrder == null}">
															  <td class="width_50_fix">${idx.index + 1}</td>
														   </c:if>
															<td class="align-center width_200_fix">${bqObj.name}</td>
															<td class="align-center width_200_fix"><fmt:formatDate value="${bqObj.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
															<td class="align-center width_200_fix ${supplierBqList != null ? (submStatus == 'DRAFT' ? 'text-warning' : 'text-success'): ''}">${supplierBqList != null ? ( submStatus == "PENDING" ? '' : submStatus) : ''}</td>
<%-- 															${supplierBqList[idx.index]} --%>
															<%-- <td class="align-center width_200_fix">
														<fmt:formatDate value="${bqObj.modifiedDate}" pattern="dd/MMM/yyyy -HH:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													</td> --%>
															<td class="align-center width_200_fix"><a id="viewSupBq" href rftevent-id="${event.id}" eventBq-id="${bqObj.id}" bq-status="${submStatus}" class="viewSupplierBillOfQuantity  btn btn-info"><spring:message code="rfs.view.button"/></a>
														</tr>
													</tbody>
												</table>
											</c:forEach>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="bqlistDetails"></div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>
<div class="flagvisibility dialogBox" id="remarkPopUp" title="Add Remarks">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="cancelMeetingForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="itemId" value="${eventMeeting.id}">
			<div class="row">
				<div class="col-sm-12 form-group">
					<div class="ph_table_border remarksBlock">
						<div class="reminderList marginDisable">
							<div class="row" id="">
								<div class="col-md-12">
									<p><spring:message code="supplierevent.add.remark"/></p>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-12">
					<div class="form-group">
						<textarea placeholder='<spring:message code="supplierevent.write.remark.placeholder"/>' rows="4" name="" id="" data-validation="required length" data-validation-length="max250" class="form-control"></textarea>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<spring:url value="${pageContext.request.contextPath}/buyer/${ eventType}/createMeeting" var="createUrl" htmlEscape="true" />
						<button type="button" id="saveRemarksBtn" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out cancelOkMeeting"><spring:message code="application.save" /></button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<!-- Pop up for complete event -->
<div class="modal fade" id="confirmCompleteEvent" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.completion" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> Are you sure you want to complete this BQ without the price submission for some line items?</label> 
				<input type="hidden" id="finishIdEvent" /> 
				<input type="hidden" class="siteVisitMandatoryCheck" value="true">
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out finish-invitation complete-event" value="${event.id}">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no2" />
				</button>
			</div>
		</div>
	</div>
</div>
<script>

	var currencyCode = '${event.baseCurrency}';
	var disableTotalAmount = ${event.disableTotalAmount};

	var revisedSubmissionMode = ${ eventType == 'RFA' && (event.status == 'CLOSED') && (!empty auctionRules.lumsumBiddingWithTax) && !eventPermissions.viewer && !eventSupplier.revisedBidSubmitted ? 'true' : 'false' };

	var isWithoutTax = ${eventType == 'RFA' && (auctionRules.lumsumBiddingWithTax == false || auctionRules.itemizedBiddingWithTax == false)};
	var isErpEnable=${not empty event.erpEnable?event.erpEnable:false};
	var allowedFields = '';
	<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'DELETE_NOTIFY' or event.suspensionType == 'DELETE_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		<c:if test="${eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
			if(revisedSubmissionMode == false) {
				allowedFields = '.tab-link > a,#eventFilter,.back_to_BQ,.remark-toggle-btn,#bqItemSearch,#resetButton,#pagination,#chooseSection,#selectPageLen,.page-link, .do-not-disable';
				$(window).bind('load', function() {
					//var disableAnchers = ['#reloadMsg'];        
					disableFormFields(allowedFields);
				});
			}
		</c:if>
	</c:if>

	<c:if test="${eventPermissions.viewer or eventSupplier.submissionStatus == 'COMPLETED' or event.status == 'CLOSED' or event.status == 'FINISHED'}">
		<c:if test="${eventSupplier.submissionStatus !='REJECTED' and !eventSupplier.isRejectedAfterStart}">
			allowedFields = '.tab-link > a,#eventFilter,.back_to_BQ,.viewSupplierBillOfQuantity,.remark-toggle-btn,#idDutchConsole,#idEnglishConsole,#bqItemSearch,#resetButton,#pagination,#chooseSection,#selectPageLen,.page-link, .do-not-disable';
			if(revisedSubmissionMode == false) {
				$(window).bind('load', function() {
					//var disableAnchers = ['#reloadMsg'];        
					disableFormFields(allowedFields);
				});
			}
		</c:if>
	</c:if>
	
	<c:if test="${eventSupplier.submissionStatus =='REJECTED' and eventSupplier.isRejectedAfterStart}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a,.viewSupplierBillOfQuantity, .back_to_BQ,#bqItemSearch, .do-not-disable';
			disableFormFields(allowedFields);
		});
	</c:if>

	
	var decimalSet = "${decimalSet}";

	$(document).ready(function() {
	
		 $("#viewSupBq").click(function() {
             $("#viewSupBq").addClass("disableCq");
     })
     
		$(".fade-btn").click(function() {
			$(".meeting-detail").hide();
		});
		$(".fade-btn").click(function() {
			$(".no-attand-meeting").show();
		});
	});

	$(document).ready(function() {
		$(".back_to_Question").click(function() {
			$(".Gen-ques").hide();
		});

		$(".back_to_Question").click(function() {
			$(".Quest-Scoring").hide();
		});
		$(document).delegate(".back_to_Question", 'click', function() {
			$(".Quest-Scoring").hide();
			$(".Gen-ques").hide();
			$(".doc-fir-inner").show();
		});

		$(document).delegate(".back_to_BQ", 'click', function(e) {
			e.preventDefault();
			 $("#viewSupBq").removeClass("disableCq");
			 $(".viewSupplierBillOfQuantity").removeClass("disabled");
			//$(".doc-fir-inner1").show();
			//$(".bqlistDetails").html('');
			 window.location.href = getContextPath() + "/supplier/viewBqList/" + getEventType() + '/${event.id}';
			
		});

	});

	$(document).ready(function() {
		$(".Questi-btn").click(function() {
			$(".doc-fir-inner").hide();
		});
		$(".Questi-btn").click(function() {
			$(".Quest-Scoring").show();
		});
	});

	// set the date we're counting down to

	// get tag element
	//var countdown = document.getElementById('countdown');
	//alert(countdown);
	// update the tag with id "countdown" every 1 second
	counterMeetings();
	setInterval(function() {
		counterMeetings();
	}, 60000);

	function counterMeetings() {
		$('.countDownTimer1').each(function() {

			var target_date = new Date($(this).attr('data-date')).getTime();

			// variables for time units
			var days, hours, minutes, seconds;

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
			htmldata = '';
			if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
				htmldata = '<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + days + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + hours + '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + minutes + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">' + seconds + '</span></span>';
			}//$('#countdown').html(htmldata);
			$(this).html(htmldata);

		});
	}

	
	// set the date we're counting down to

	// get tag element
	//var countdown = document.getElementById('countdown');
	//alert(countdown);
	// update the tag with id "countdown" every 1 second
	counterMeetings1();
	setInterval(function() {

		counterMeetings1();

	}, 60000);
	function counterMeetings1() {
		$('.countDownTimer').each(
				function() {

					var target_date = new Date($(this).attr('data-date')).getTime();

					// variables for time units
					var days, hours, minutes, seconds;

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
					htmldata = '';
					if (days >= 0 && hours >= 0 && minutes >= 0 && seconds >= 0) {
						htmldata = '<span class="days"><b>Days</b><br>' + days + '</span><span class="hours"><b>Hours</b><br>' + hours + '</span><span class="minutes"><b>Minutes</b><br>' + minutes
								+ '</span><span class="seconds"><b>Seconds</b><br>' + seconds + '</span>';
					}//$('#countdown').html(htmldata);
					$(this).html(htmldata);

				});
	}
	
	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});

	$('.table-1 .custom-checkbox').on('change', function() {
		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});
		enableDownload();
	});

	$('.table-2 [type=checkbox]').on('change', function() {
		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox');
		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		enableDownload();
		$.uniform.update(firstObj);
	});

	$("#test-select ").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
	function enableDownload() {
		if ($('.table-2 [type=checkbox]:checked').length > 0) {
			$('#downloadDoc').removeClass('disabled');
		} else {
			$('#downloadDoc').addClass('disabled');
		}
	}

	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<style>
#supplierBqList .ph_table td, #supplierBqList .ph_table th {
	text-align: left;
}

.header+* {
	margin-top: 52px;
}

.main_table_wrapper .table>tbody>tr>td {
	/* border-top: 1px solid red !important; */
	border-bottom: 1px solid transparent !important;
	border-top: 1px solid #ddd;
}

.main_table_wrapper .table>tbody>tr:last-child {
	border-bottom: 2px solid #ddd !important;
}

.bqlistDetails .table>tbody {
    font-family: 'open_sansregular', "Helvetica Neue", Helvetica, Arial, sans-serif;
    font-size: 13px;
    color: #262626 !important;
    background-color: #fff;
    line-height: 1.42857143; font-weight:normal;
}
.disableCq{
    pointer-events: none !important;
}

.chosen-container{
margin-left: 5px !important;
    width: 59% !important;
    text-align: left;
}
</style>


<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
$(document).ready(function() {
	
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
});
	
</script>


<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/js/view/supplierBqSubmission.js?4"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>



