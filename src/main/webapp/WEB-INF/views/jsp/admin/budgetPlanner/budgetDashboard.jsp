<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prDetailsDesk" code="application.pr.create.details" />
<%-- <script type="text/javascript" src="<c:url value="/resources/js/chart/d3.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/chart/d3pie.js"/>"></script> --%>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js"></script>
<style>
.panel {
	padding-top: 20px;
}

#pie1 {
	padding-bottom: 15px;
	margin: 0 auto;
}

#pie2 {
	padding-bottom: 15px;
	margin: 0 auto;
}

#pie1>svg {
	margin: 0 auto;
	display: block;
}

#pie2>svg {
	margin: 0 auto;
	display: block;
}

.svg-content {
	display: inline-block;
	position: absolute;
	top: 0;
	left: 0;
}
svg




































:not


















 


















(
:root


















 


















){
overflow




































:


















 


















visible




































;
}
</style>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li>
					<a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">
					<spring:message code="defaultmenu.budget.summary" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="defaultmenu.budget.summary" />
				</h2>
			</div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row" style="position: relative; top: 10px;">
				<div class="col-md-3">
					<label>Select Business Unit:</label> <select id="businessUnit" class="form-control chosen-select">
						<option value="">Select Business unit</option>
						<c:forEach items="${businessUnitList}" var="businessUnit">
							<option value="${businessUnit.id}">${businessUnit.unitName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-3">
					<label>Select Cost Center:</label> <select id="costCenter" class="form-control chosen-select">
						<option value="">Select Cost Center</option>
						<c:forEach items="${costCenterList}" var="costCenter">
							<option value="${costCenter.id}">${costCenter.costCenter}</option>
						</c:forEach>
					</select>
				</div>

				<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" style="margin-top: 24px; margin-left: 16px;" id="updatePie">View</button>
				<div class="col-md-6"></div>
			</div>

			<div class="row" style="margin-top: 40px;">
				<div class="col-md-6">
					<div class="panel">
						<canvas id="pie1"></canvas>
					</div>
				</div>
				<div class="col-md-6">
					<div class="panel">
						<canvas id="pie2"></canvas>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	//window.addEventListener('resize', updatePie);
	var dataPie1 = [];
	var dataPie2 = [];
	var myChart1;
	var myChart2;
	var currencyCode;
	$('#updatePie').on('click', function(e) {
		e.preventDefault();

		var businessUnitId = $("#businessUnit").val();
		var costCenterId = $("#costCenter").val();

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		var ajaxUrl = getContextPath() + "/admin/budgets/checkBudgetSummary/";

		$.ajax({
			type : "GET",
			url : ajaxUrl + businessUnitId + "/" + costCenterId,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				//$('div[id=idGlobalError]').hide();
				$('#loading').hide();
				$('#idGlobalError').hide();
				//$('p[id=idGlobalInfoMessage]').html(request.getResponseHeader('info'));
				//$('div[id=idGlobalInfo]').show();
				if (myChart1 != undefined) {
					myChart1.destroy();
				}
				if (myChart1 != undefined) {
					myChart2.destroy();
				}
				dataPie1 = [ data.remaining, data.pending, data.approved, data.locked ];
				dataPie2 = [ data.approved, data.paid ];
				currencyCode = request.getResponseHeader('currencyCode');
				updatePie();

				var errorMsg = request.getResponseHeader('error');
				if (errorMsg != undefined) {
					$('p[id=idGlobalErrorMessage]').html(errorMsg);
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				}

			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	function updatePie() {
		var pie1 = document.getElementById('pie1').getContext('2d');
		myChart1 = new Chart(pie1, {
			type : 'bar',
			data : {
				labels : [ 'Remaining Amount (' + (currencyCode != null ? currencyCode : "") + ')', 'Pending Amount (' + (currencyCode != null ? currencyCode : "") + ')', 'Approved Amount (' + (currencyCode != null ? currencyCode : "") + ')',
						'Locked Amount (' + (currencyCode != null ? currencyCode : "") + ')' ],
				datasets : [ {
					data : dataPie1,
					backgroundColor : [ 'rgba(255, 159, 64, 0.8)', 'rgba(54, 162, 235, 0.8)', 'rgba(153, 102, 255, 0.8)', 'rgba(255, 99, 132, 0.8)' ],
					borderWidth : 1
				} ]
			},
		 	options : {
				responsive : true,
				  scales: {
			            yAxes: [{
			                ticks: {
			                    beginAtZero: true
			                }
			            }],
			            xAxes: [{
			                barThickness: 52,  // number (pixels) or 'flex'
			                maxBarThickness: 80 // number (pixels)
			              //  barPercentage: 0.5
			            }]
			        },
				legend : {
					display : false,
					position : 'bottom',
				},
				tooltips : {
					bodyFontSize : 14,
					callbacks : {
						label : function(tooltipItem, data) {
							var allData = data.datasets[tooltipItem.datasetIndex].data;
							var tooltipLabel = data.labels[tooltipItem.index];
							var tooltipData = allData[tooltipItem.index];
							var total = 0;
							for ( var i in allData) {
								total += allData[i];
							}
							var tooltipPercentage = Math.round((tooltipData / total) * 100);
							return /* tooltipLabel + ': ' + */ tooltipData + ' (' + tooltipPercentage + '%)';
						}
						/* afterBody : function(t, d) {
							return "    " + currencyCode;
						} */
					}
				}
			} 
		});

		var pie2 = document.getElementById('pie2').getContext('2d');
		myChart2 = new Chart(pie2, {
			type : 'bar',
			data : {
				labels : [ 'Approved Amount (' + (currencyCode != null ? currencyCode : "") + ')', 'Paid Amount (' + (currencyCode != null ? currencyCode : "") + ')' ],
			datasets : [ {
				data : dataPie2,
				backgroundColor : [ 'rgba(153, 102, 255, 0.8)', 'rgba(14, 159, 114, 0.8)'],
				borderWidth : 1
			} ]
		},
			options : {
				responsive : true,
				  scales: {
			            yAxes: [{
			                ticks: {
			                    beginAtZero: true
			                }
			            }],
			            xAxes: [{
			                barThickness: 52,  // number (pixels) or 'flex'
			                maxBarThickness: 80 // number (pixels)
			                //barPercentage: 0.5
			            }]
			        },
				legend : {
					display : false,
					position : 'bottom',
				},
				tooltips : {
					bodyFontSize : 14,
					callbacks : {
						label : function(tooltipItem, data) {
							var allData = data.datasets[tooltipItem.datasetIndex].data;
							var tooltipLabel = data.labels[tooltipItem.index];
							var tooltipData = allData[tooltipItem.index];
							var total = 0;
							for ( var i in allData) {
								total += allData[i];
							}
							var tooltipPercentage = Math.round((tooltipData / total) * 100);
							return /* tooltipLabel + ': ' + */ tooltipData + ' (' + tooltipPercentage + '%)';
						}/* ,
						afterBody : function(t, d) {
							return "    " + currencyCode;
						} */
					}
				}
			}
		});
	}
</script>
