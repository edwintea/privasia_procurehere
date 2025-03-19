<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.marLeft {
	margin-left: 45px;
}

.mega {
	max-height: 660px;
}

.ph_table.header th {
	font-weight: 600;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard"/></a></li>
				<li class="active"><spring:message code="erp.event.list"/></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head"><spring:message code="erp.event.list"/></h2>
				<div class="right-header-button"></div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="tab-pane active">
				<div class="tab-content Invited-Supplier-List ">
					<div class="tab-pane active white-bg" id="step-1" style="min-height: 670px;">
						<div class="upload_download_wrapper clearfix mar-t20 event_info">
							<div class="ph_table_border margin">
								<div class="mega range-header">
									
									<table class="table data ph_table border-none" width="100%">
									<thead>
											<tr>
												<th class="width_100 width_100_fix align-center"><spring:message code="application.action1"/></th>
												<th class="width_150 width_150_fix align-center"><spring:message code="application.date"/></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="erp.reference.no"/></th>
												<th class="width_100 width_100_fix align-left">
													<spring:message code="application.status" /> <select name="statusAction" id="statusAction">
													
														<c:forEach items="${actionTypeList}" var="action">
															<option value="${action}">${action}</option>
														</c:forEach>
													</select>
												</th>
											</tr>
										</thead>
										<tbody id="manualList">
											<c:forEach items="${erpEventAuditList}" var="eventAudit">
												<tr>
													<td class="width_100 width_100_fix align-center">
														<a href="${pageContext.request.contextPath}/buyer/viewErpEventDetail/${eventAudit.id}" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font"><spring:message code="import.view.details"/></a>
													</td>
													<td class="width_150 width_150_fix align-center">
														<fmt:formatDate value="${eventAudit.actionDate}" timeZone="${sessionScope['timeZone']}" pattern="dd/MM/yyyy hh:mm a" />

													</td>
													<td class="width_200 width_200_fix align-left">${eventAudit.prNo}</td>
													<td class="width_200 width_100_fix align-left">${eventAudit.action}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.tab-pane {
	padding: 20px !important;
}

.select-radio-lineHgt {
	line-height: 5px !important;
}

.radio {
	margin-top: 13px !important;
}

.hideDiv {
	display: none;
}

.btn-font {
	color: white !important;
	font-weight: 600;
}
/* .header+* {
 margin-top: 90px;
} */
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind(
			'load',
			function() {
				var allowedFields = '.button-previous, #dashboardLink';
				//var disableAnchers = ['#reloadMsg'];        
				disableFormFields(allowedFields);
				$('#page-content').find('select').not(allowedFields).parent(
						'div').addClass('disabled');
			});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
	
	$(document).delegate('#statusAction', 'change', function(e) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var actionType = $('#statusAction option:selected').text();
	console.log("actionType: "+ actionType);
		 $.ajax({
			url : getContextPath() + '/buyer/erpManualList/' + actionType,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var manualList = '';
				$.each(data, function(i, item) {
					manualList += '<tr>';
					manualList += '<td class="width_100 width_100_fix align-center">';
					manualList += '<a href="' +getContextPath() + '/buyer/viewErpEventDetail/'+item.id+ '" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out btn-font">View Details</a>';
					manualList += '</td>'; 
					manualList += '<td class="width_150 width_150_fix align-center">' +item.actionDate+ '</td>';
					manualList += '<td class="width_200 width_200_fix align-left">' +item.prNo+ '</td>';
					manualList += '<td class="width_200 width_100_fix align-left">' +item.action+ '</td>';
					manualList += '</tr>';			
				}); 
				$('#manualList').html(manualList);
			},
			complete : function() {
				$('#loading').hide();
			}
		}); 

	});
	
</script>