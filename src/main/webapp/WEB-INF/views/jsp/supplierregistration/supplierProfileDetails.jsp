<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message var="supplierProfileDesk" code="application.supplier.profile" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierProfileDesk}] });
});
</script>

<!-- <div id="page-content-wrapper">
	<div id="page-content">
		<div class="container"> -->


<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/supplier/supplierDashboard"> <spring:message code="application.dashboard"/> </a></li>
	<li class="active"><spring:message code="supplier.compny.profile"/></li>
</ol>
<!-- page title block -->
<div class="Section-title title_border gray-bg">
	<h2 class="trans-cap progress-head"><spring:message code="supplier.compny.profile"/></h2>
	<div class="right-header-button">
		<!-- 	<button class="btn btn-default ph_btn_midium hvr-pop hvr-rectangle-out3" onclick="window.location.href='Mail_Inbox.html'">Event Inbox</button> -->
	</div>
</div>
<!-- page title block -->
<div class="ports-tital-heading">
	<div class="row">
		<div class="col-md-10 col-xs-10 marg-bottom-15"></div>
		<div class="col-md-2 col-xs-2"></div>
	</div>
</div>
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<!-- </div> -->

<div class="clear"></div>
<div class="tab-main">
	<ul class="tab event-details-tabs supplier-tab">
		<li class="tab-link ${supplierProfileInfo == true ? 'current' : ''}"><a class="${supplierProfileInfo == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/supplierProfileDetails"><spring:message code="supplierprofile.details"/></a><div id="suppDetails" class="incomp-flag-icon" style="display: none;">i</div>
		</li>
		<li class="tab-link ${supplierProfileCategory == true ? 'current' : ''}"><a class="${supplierProfileCategory == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/supplierProfileCategory"><spring:message code="supplierprofile.category"/><div id="suppCategory" class="incomp-flag-icon" style="display: none;">i</div>
			</a></li>
		<li class="tab-link ${suppCompProfileAttachments == true ? 'current' : ''}"><a class="${suppCompProfileAttachments == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/suppCompProfileAttachments"><spring:message code="supplierprofile.attachments"/>
			</a></li>
		<li class="tab-link ${suppAddEditTrackRecord == true ? 'current' : ''}"><a class="${suppAddEditTrackRecord == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/suppAddEditTrackRecord"><spring:message code="supplierprofile.track.record"/>
			</a></li>
		<li class="tab-link ${suppFinancialInformation == true ? 'current' : ''}"><a class="${suppFinancialInformation == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/suppFinancialInformation"><spring:message code="supplierprofile.financial.info"/><div id="suppFinInfo" class="incomp-flag-icon" style="display: none;">i</div>
			</a></li>
		<li class="tab-link ${suppBoardOfDirectors == true ? 'current' : ''}"><a class="${suppBoardOfDirectors == true ? 'font-gray' : 'font-white'}" href="${pageContext.request.contextPath}/supplier/suppBoardOfDirectors"><spring:message code="supplierprofile.organizational.info"/><div id="suppOrgDet" class="incomp-flag-icon" style="display: none;">i</div>
			</a></li>
	</ul>
</div>
<!-- </div>
</div> -->
<!-- WIDGETS -->
<!-- Admin theme -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<!-- JS Core -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/js-core/animated-search-filter.js"/>"></script> --%>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<%--     <script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap-filestyle.min.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js?1"/>"></script>
<script src="<c:url value="/resources/js/view/supplierProfile.js"/>"></script>
<!-- WIDGETS -->
<!-- Uniform -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
<!-- Chosen -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<!-- Bootstrap Tooltip -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>
<!-- Perfact scroll -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>

<script>



$('ul.tabs li').click(function() {
	var tab_id = $(this).attr('data-tab');

	$('ul.tabs li').removeClass('current');
	$('.tab-content').removeClass('current');

	$(this).addClass('current');
	$("#" + tab_id).addClass('current');
})



	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$(this).find('.header').css('top', $(this).scrollTop());

		});
	});

	<c:set var="tab"  value="${step}"/>
	<c:if test="${not empty tab }" >
	$(document).ready(function() {
		<c:choose>
		<c:when test="${tab == 5}">
		$(".tb_4").addClass("active");
		$(".tb_4").prevAll().addClass("active");
		$(".tb_4").nextAll().removeClass("active");
		$(".tb_4").nextAll().removeClass("activeprev");
		$('.tab-pane').removeClass("active");
		$('#step-5').addClass('active');
		</c:when>
		<c:otherwise>
		$(".tb_${tab}").prevAll().addClass("active activeprev");
		$(".tb_${tab}").nextAll().removeClass("active");
		$(".tb_${tab}").nextAll().removeClass("activeprev");
		$(".tab-pane.active").removeClass("active");
		$("#step-${tab}, .tb_${tab}").addClass("active");
		</c:otherwise>
		</c:choose>
	});
	</c:if>

	$.formUtils.addValidator({
		name : 'year_established',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var currentYear = new Date().getFullYear();
			console.log(currentYear);
			if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
				response = false;
			}
			return response;
		},
		errorMessage : 'Year established is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
		errorMessageKey : 'badYearEstablished'
	});
	
	$.formUtils.addValidator({
		  name : 'crn_number',
		  validatorFunction : function(value, $el, config, language, $form) {
			  var countryID = $('#regCountryId').val();
			  var crnNum = $('#idCompRegNum').val();
			  var id = $('#supplierId').val();
			  var response = true;
			  var header = $("meta[name='_csrf_header']").attr("content");
			  var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
	              url:  getContextPath() +"/checkRegistrationNumberWithId",
	              data : {countryID : countryID, crnNum : crnNum, id : id},
	              async: false,
	      		beforeSend : function(xhr) {
	    			xhr.setRequestHeader(header, token);
	    		},
	              success:function(data){
	            	  console.log(data);
	            	  response = true;
	              },
	              error : function(request, textStatus, errorThrown) {
	            	  console.log(textStatus);
	            	  response = false;
	              }
	          });
		     return response;
		  },
		  errorMessage : 'Company Registration Number Already registered in the system',
		  errorMessageKey: 'badCrnNumber'
		});
	
	$.formUtils.addValidator({
		  name : 'company_name',
		  validatorFunction : function(value, $el, config, language, $form) {
			  var countryID = $('#regCountryId').val();
			  var companyName = $('#idCompanyName').val();
			  var id = $('#supplierId').val();
			  var response = true;
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");			  
			  $.ajax({
	              url:  getContextPath() +"/checkCompanyNameExistWithId",
	              data : {countryID : countryID, companyName : companyName, id : id},
	              async: false,
	      		beforeSend : function(xhr) {
	    			xhr.setRequestHeader(header, token);
	    		},
	              success:function(data){
	            	  console.log(data);
	            	  response = true;
	              },
	              error : function(request, textStatus, errorThrown) {
	            	  console.log(textStatus);
	            	  response = false;
	              }
	          });
		     return response;
		  },
		  errorMessage : 'Company Name Already registered in the system',
		  errorMessageKey: 'badCompanyName'
		});
	

	$.formUtils.addValidator({
		name : 'year_comp',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var currentYear = new Date().getFullYear();
			console.log(currentYear);
			if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
				response = false;
			}
			return response;
		},
		errorMessage : 'Year completion is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
		errorMessageKey : 'badYearCompletion'
	});

	$(document).ready(function() {
		$('#idFax').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"
		});
		$('#idTelPhone').mask('+00 00000000000', {
			placeholder : "e.g. +60 322761533"
		});
		$('#idYearEst').mask('0000', {
			placeholder : "e.g. 1989"
		});
		$('#idYearE').mask('0000', {
			placeholder : "e.g. 1989"
		});
		$('#idAdMoNo').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"
		});
		//$('#idCompanyWebsite').mask('http://www.company.com', {placeholder: "http://www.company.com"});
	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	$(document).delegate(".addNewTrackRecord", "click", function(e) {
		e.preventDefault();
		return;

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();

		var ajaxUrl = getContextPath() + "/saveNewTrackRecord";

		var supplierId = $('#supplierId').val();

		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(),
			beforeSend : function(xhr) {

				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {

				$.jGrowl(data, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});
	$(document).delegate(".removeProjectFile", "click", function(e) {
		jQuery("#extrFrm").collapse("hide");
		jQuery("#projectName").val("");
		jQuery("#clientName").val("");
		jQuery("#idYearE").val("");
		jQuery("#idCurrency").val("");
		jQuery("#idCurrency").trigger("chosen:update");
		$('#loading').hide();
		$("[name='contactValue']").val("");
		$("[name='clientEmail']").val("");
	});

	$("#idTrackrecord001").click(function() {
		$("#extrFrm").collapse('show');
		$("#projectName , #clientName , #idYearE ,#idCurrency , #projId").val("");
		$("[name='contactValue']").val("");
		$("[name='clientEmail']").val("");

		$("html, body").animate({
			scrollTop : $('#projectName').offset().top - 50
		}, 1000);
		$('#projectName').focus();
		$("#projectUpdateSupplier").hide();
		$("#projectAddSupplier").show();

	});

	<c:if test="${incompleteTaxReg == true}">
		$('document').ready(function() {
		$('#suppDetails').css('display','flex');
		// suppDetails
	});	
	</c:if>

	<c:if test="${incompleteFinanceSection == true}">
		$('document').ready(function() {
		$('#suppFinInfo').css('display','flex');
	});	
	</c:if>

	<c:if test="${incompleteOrgSection == true}">
		$('document').ready(function() {
		$('#suppOrgDet').css('display','flex');
	});	
	</c:if>

	<c:if test="${incompleteIndustryCategorySection == true}">
		$('document').ready(function() {
		$('#suppCategory').css('display','flex');
	});	
	</c:if>

	<c:if test="${showValidationCheckFlag == true}">
		$('document').ready(function() {
		$('#suppCategory').css('display','flex');
		var message = 'Please select a maximum of 25 industry sectors only'
		$('p[id=idGlobalErrorMessage]').html(message.split(",").join("<br/>"));
		$('div[id=idGlobalError]').show();
	});	
	</c:if>

	<c:if test="${incompleteTaxReg == false && incompleteFinanceSection == false && incompleteOrgSection == false && incompleteIndustryCategorySection == false}">
		$('document').ready(function() {
		$('.incomp-icon').hide();
	});	
	</c:if>

	<c:if test="${incompleteTaxReg == true || incompleteFinanceSection == true || incompleteOrgSection == true || incompleteIndustryCategorySection == true}">
		$('document').ready(function() {
		$('.incomp-icon').show();
	});	
	</c:if>

</script>


<style type="text/css">

	.incomp-flag-icon
	{
	position: absolute;
    top: 12px;
    right: 0px;
    width: 15px;
    height: 15px;
    color: white;
    background-color: #3e98d3;
    display: flex;
    justify-content: center;
    align-items: center;
	border-radius: 50px;
	
	}
.leftSideOfCheckbox {
	width: 47%;
	float: left;
	border-right: 1px solid #d8d8d8;
	margin: 0 2% 0 0;
	height: 300px;
	overflow-y: auto;
}

.rightSideOfCheckbox {
	width: 50%;
	float: left;
	height: 300px;
	overflow-y: auto;
}

.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.tree li {
	position: relative;
}

table.data.for-pad-data.bordered tr {
	border-bottom: 1px solid #ccc;
}

.for-pad-data tr td {
	font-size: 13px;
}

.highlight {
	background-color: #fff34d;
	-moz-border-radius: 5px;
	/* FF1+ */
	-webkit-border-radius: 5px;
	/* Saf3-4 */
	border-radius: 5px;
	/* Opera 10.5, IE 9, Saf5, Chrome */
	-moz-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* FF3.5+ */
	-webkit-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* Saf3.0+, Chrome */
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* Opera 10.5+, IE 9.0 */
}

.form-group {
	float: none;
}

.highlight {
	padding: 1px 1px;
	margin: 0 -4px;
}

.animated-search-filter>* {
	position: inherit !important;
}

.search_ul_1 li, .search_ul li {
	position: relative;
	transform: translateY(0) !important;
}

.nvigator, .nvigator-place {
	position: absolute;
	left: -14px;
	cursor: pointer;
	top: 4px;
}
/* this css work for plus added text */
.add_more_feture_ul li {
	list-style: outside none none;
	padding: 5px 8px;
}

.add_more_feture_ul li a {
	float: right;
}

.add_more_feture_ul {
	padding-left: 8px;
}

.step4_table {
	margin-top: 58px;
}

.panel .sum-accord {
	overflow: visible;
}

.step_table.mega {
	margin-top: 60px;
	border: 1px solid;
}

ul.tab li a.font-white {
	padding: 10px 20px 10px 5px !important;
	display: inline-block;
}
/* this css work for plus added text */
</style>
