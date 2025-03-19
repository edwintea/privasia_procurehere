<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
	.custom-label-container {
		white-space: nowrap;
		margin-left: 10px;
		margin-top: 20px;
	}

	.custom-row {
		margin-top: 20px;
	}

	.custom-table-border {
		width: 100%;
		border: 1px solid #ccc;
		padding: 10px;
		box-sizing: border-box;
	}

	.custom-table-wrapper {
		width: 100%;
	}

	.custom-table {
		width: 100%;
		table-layout: fixed; /* Forces fixed table layout */
	}

	.custom-table th,
	.custom-table td {
		word-wrap: break-word;
		overflow-wrap: break-word;
		white-space: normal; /* Allow text to wrap */
		max-width: 100%; /* Ensure it doesn't exceed the container width */
		box-sizing: border-box; /* Include padding and borders in the width calculation */
		padding: 10px; /* Add padding for better appearance */
	}

	.custom-table th {
		background-color: #d3d3d3; /* Grey out header row */
		color: #333; /* Text color */
		padding: 10px; /* Add padding for better appearance */
		text-align: left; /* Align text to the left */
	}

	.custom-table thead {
		background-color: #d3d3d3; /* Grey out the entire header row */
	}

	.custom-table .checkbox-stylling input[type="checkbox"] {
		background-color: #ccc; /* Grey out checkbox */
		border: 1px solid #999; /* Border color for greyed-out checkbox */
	}


	.width-5 {
		width: 5%;
	}

	.width-35 {
		width: 35%;
	}
	.width-20 {
		width: 20%;
	}
	.custom-checkAllbox {
		cursor: pointer;
	}

	.custom-doc-checkbox {
		cursor: pointer;
	}

	.checkbox-stylling {
		text-align: center;
	}
</style>
<!-- Modal content-->
<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">
	<form:form action="${pageContext.request.contextPath}/buyer/copyRequestTo" modelAttribute="sourcingFormRequest" id="frmCreateNextEvent" method="post">
		<form:hidden path="id" />
		<div class="row marg-bottom-10" id="idSelectedEvent">
			<div class="row marg-top-10 marg_left_0">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label cssClass="marg-top-20"> Next Event </label>
				</div>
				<div class="col-md-8  marg-bottom-10">
					<select id="rfxTypeList" name="selectedRfxType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" data-validation="required">
						<c:forEach items="${rfxType}" var="rfxType">
							<option>${rfxType}</option>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="row marg-top-10 marg_left_0 hideSelect">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label cssClass="marg-top-20" > <spring:message code="rfx.auction.type.label" />
					</label>
				</div>
				<div class="col-md-8  marg-bottom-10">
					<select name="auctionType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" id="idAuctionType" data-validation="required" data-validation-depends-on="selectedRfxType" data-validation-depends-on-value="RFA">
						<option value="">Select Auction Type</option>
						<option value="FORWARD_ENGISH">Forward English Auction</option>
						<option value="REVERSE_ENGISH">Reverse English Auction</option>
						<option value="FORWARD_SEALED_BID">Forward Sealed Bid</option>
						<option value="REVERSE_SEALED_BID">Reverse Sealed Bid</option>
						<option value="FORWARD_DUTCH">Forward Dutch Auction</option>
						<option value="REVERSE_DUTCH">Reverse Dutch Auction</option>
					</select>
				</div>
			</div>

			<div class="row marg-top-10 marg_left_0 hideBq">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class="marg-top-20" > <spring:message code="eventdescription.billofquantity.label" />
					</label>
				</div>
				<div class="col-md-8  marg-bottom-10">
					<select id="bqList" name="bqId" class="form-control chosen-select disablesearch" data-validation="required" data-validation-depends-on="selectedRfxType" data-validation-depends-on-value="RFA">
						<c:forEach items="${bqList}" var="bq">
							<option value="${bq.id}">${bq.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>


<%--			<div class="row marg-top-10 marg_left_0 hideSor">--%>
<%--				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">--%>
<%--					<label class="marg-top-20" > <spring:message code="eventdescription.schedule.rate.label" />--%>
<%--					</label>--%>
<%--				</div>--%>
<%--				<div class="col-md-8  marg-bottom-10">--%>
<%--					<select id="sorList" name="sorId" class="form-control chosen-select disablesearch" data-validation="required" data-validation-depends-on="selectedRfxType" data-validation-depends-on-value="RFA">--%>
<%--						<c:forEach items="${sorList}" var="sor">--%>
<%--							<option value="${sor.id}">${sor.name}</option>--%>
<%--						</c:forEach>--%>
<%--					</select>--%>
<%--				</div>--%>
<%--			</div>--%>

			<div class="row marg-top-10 marg_left_0">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class="marg-top-20">Select Template</label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select name="idRfxTemplate" class="form-control chosen-select disablesearch" id="idRfxTemplate" value="${templateId}" data-validation="required">
						<option value="">Select Template</option>
						<c:forEach items="${rfxTemplateList}" var="template">
							<option value="${template.id}">${template.templateName}</option>
						</c:forEach>
					</select>
				</div>

			</div>

			<div id="businessUnit" class="row marg-top-10 marg_left_0">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class="marg-top-20">Select Business Unit</label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select name="businessUnitId" id="businessUnitId" class="chosen-select disablesearch">
						<c:forEach items="${businessUnits}" var="businessUnit">
							<option value="${businessUnit.id}">${businessUnit.unitName}</option>
						</c:forEach>
					</select>
				</div>

			</div>

		</div>

		<c:if test="${eventType != 'RFI'}">
			<div class="row marg-bottom-10">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class="marg-top-10 marg-left-5"> Remarks </label>
				</div>
				<div class="col-md-8">
					<form:textarea id="AwRemk" path="concludeRemarks" data-validation="required length" placeholder="Remarks" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
		</c:if>
		<div class="marg-bottom-10">
			<div class="custom-label-container">
				<label>Select documents to carry forward to RFX:</label>
			</div>
		</div>
		<div class="custom-row">
			<div class="custom-table-border">
				<div class="custom-table-wrapper">
					<table class="custom-table" border="0" cellspacing="0" cellpadding="0">
						<thead>
						<tr>
							<th class="width-5 checkbox-stylling">
								<input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all" onclick="toggleSelectAll(this)">
							</th>
							<th class="width-35"><spring:message code="event.document.filename" /></th>
							<th class="width-35"><spring:message code="event.document.description" /></th>
							<th class="width-20"><spring:message code="event.document.uploadby" /></th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="doc" items="${listDocs}">
							<tr>
								<td class="width-5">
									<input type="checkbox" class="custom-doc-checkbox" name="selectedDocs" value="${doc.id}"
										   <c:if test="${!doc.internal}">checked</c:if>>
								</td>
								<td class="width-35">${doc.fileName}</td>
								<td class="width-35">${doc.description}</td>
								<td class="width-20">${doc.uploadBy.name}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>

			</div>
		</div>
		<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			<form:button type="button" id="btnCreateNextEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">Conclude</form:button>
			<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">Cancel</button>
		</div>
	</form:form>
</div>
<c:if test="${eventType ne 'RFI'}">
	<div id="tab-2" class="tab-content doc-fir tab-main-inner" style="display: none;">
		<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/concludeEvent" modelAttribute="event" method="post">
			<form:hidden path="id" />
			<div class="row marg-bottom-10 marg-left-10 marg-right-10 marg-top-20">
				<div class="col-md-3 col-sm-4 col-xs-6">
					<label>Award To</label>
				</div>
				<div class="col-md-9 col-sm-8 col-xs-6">
					<div class="invite-supplier delivery-address white-bg">
						<div class="role-upper"></div>
						<div class="chk_scroll_box" id="perferct_scroll">
							<div class="scroll_box_inner overscrollInnerBox">
								<div class="role-main">
									<div class="role-bottom checkbox-set concluedeData">
										<ul>
											<c:forEach var="sup" items="${eventSuppliers}">
												<li><input type="checkbox" name="awardedSuppliers" value="${sup.id}" data-value="${sup.companyName}" class="custom-checkbox" id="inlineCheckbox115"> <span>
														<strong> ${sup.companyName}</strong>
													</span></li>
											</c:forEach>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row marg-bottom-10 marg-left-10 marg-right-10">
				<div class="col-md-3 col-sm-4 col-xs-6 marg-top-20">
					<label>Awarded Price</label>
				</div>
				<div class="col-md-9 marg-top-10 marg-bottom-10">
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
					<form:input path="awardedPrice" type="text" data-validation="required validate_custom_length" class="form-control textarea-counter" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" rows="3" placeholder="Enter Awarded Price.." />
				</div>
			</div>
			<div class="row marg-bottom-10 marg-left-10 marg-right-10">
				<div class="col-md-3">
					<label class="marg-top-10"> Awarded Remarks </label>
				</div>
				<div class="col-md-9">
					<form:textarea id="AwdRem" path="concludeRemarks" data-validation="required length" placeholder="Remarks" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form:button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">Conclude</form:button>
				<button type="button" id="buttonCancel" class="btn btn-black btn-default hvr-pop ph_btn_small hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
				<%-- <form:button class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">Cancel</button> --%>
			</div>
		</form:form>
	</div>
</c:if>



<style>
.concluedeData.role-bottom.checkbox-set ul li span {
	padding: 0 !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>
$("#rfxTypeList").change(function() {
	
	$('#AwdRem').val("");
	$('#AwRemk').val("");
	
	$(this).find("option:selected").each(function() {
		if ($(this).val() == "RFA") {
			$("#idAuctionType").attr('data-validation', 'required');
			$(".hideSelect").show();
			if(${eventType != 'RFI'}){
				$("#bqList").attr('data-validation', 'required');
				$(".hideBq").show();
				$(".hideSor").show();
			}
		} else {
			$("#idAuctionType").removeAttr('data-validation');
			$(".hideSelect").hide();
			$(".hideBq").hide();
			$(".hideSor").hide();
		}

	});

}).change();

$(document).ready(function() {

	$('#btnCreateNextEvent').click(function() {
		if($('#frmCreateNextEvent').isValid()) {
			$(this).addClass('disabled');
			$('#loading').show();
			$('#frmCreateNextEvent').submit();
		}
	});
	
	
		$("#idAuctionType").removeAttr('data-validation');
		$("#bqList").removeAttr('data-validation');
		$("#businessUnit").hide();
		$("#businessUnit").prop('required',false);
		
 		$.formUtils.addValidator({
			name : 'validate_custom_length',
			validatorFunction : function(value, $el, config, language, $form) {
				var val = value.split(".");
				if (val[0].replace(/,/g, '').length > 10) {
					return false;
				} else {
					return true;
				}
			},
			errorMessage : 'The input value is longer than 10 characters',
			errorMessageKey : 'validateLengthCustom'
		});
		$.validate({
			lang : 'en',
			onfocusout : false,
			validateOnBlur : true,
			modules : 'file'
		});
		$('ul.tabs li').click(function() {
			var tab_id = $(this).attr('data-tab');

			$('ul.tabs li').removeClass('current');
			$('.tab-content').removeClass('current');

			$(this).addClass('current');
			$("#" + tab_id).addClass('current');
		})

		$('.role-bottom').find('input[type="checkbox"]').change(function() {

			var roleUperBlok = '';
			$(this).parents('.role-bottom').find('input[type="checkbox"]:checked').each(function() {
				var roleValue = $(this).val();
				var roleName = $(this).data("value");
				roleUperBlok += '<div class="role-upper-inner">' + roleName + '<a href="#" data-val="' + roleValue + '">X</a></div>';
			});
			$(this).parents('.chk_scroll_box').prev('.role-upper').html(roleUperBlok);
		});

		/* this code for refresh multiselcte checkbox on load time */
		$('.role-bottom').find('input[type="checkbox"]').trigger('change');

		$(document).delegate('.role-upper a', 'click', function(e) {

			e.preventDefault();
			var checkboxVal = $(this).attr('data-val');
			var checkObj = $(this).parents('.role-upper').next('.chk_scroll_box').find('input[type="checkbox"][value="' + checkboxVal + '"]');

			checkObj.prop('checked', false);
			$(this).parent('.role-upper-inner').remove();
			$.uniform.update(checkObj);
		});

		$('#question_list').perfectScrollbar({
			suppressScrollX : true
		});

	});
 	
$(document).delegate('#idAuctionType','change', function(e) {
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				var auctionType = $('#idAuctionType option:selected').text();
				$.ajax({
							url : getContextPath()
									+ '/buyer/RFA/getRfaTemplates/'
									+ auctionType,
							type : "GET",
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								xhr.setRequestHeader("Accept","application/json");
								xhr.setRequestHeader("Content-Type","application/json");
								$('#loading').show();
							},
							success : function(data, textStatus,request) {

								var templateList = '<option value="" >Select default Template </option>';
								var templateAuctionType = '';
								var type = '';
								$.each(data, function(i, item) {
										if (i == 0) {
											templateAuctionType = item.templateAuctionType;
											type = item.type;
										}
										templateList += '<option value="'
												+ item.id
												+ '"  data-auctionType="'
												+ getAuctionType(item.templateAuctionType)
												+ '" >'
												+ item.templateName
												+ '</option>';
									});
								$('#idRfxTemplate').html(templateList).trigger("chosen:updated");

								if (type == 'RFA'&& $('#idRfxTemplate option:selected').val() != '') {
									var html = '<label>Auction Type : </label> <span id="auctionTypeValue">' + getAuctionType(templateAuctionType)+ '</span>';
									$('#auctionType').html(html);
									$('#auctionType').removeClass(
											'hideDiv');
								} else {
									$('#auctionType').addClass(
											'hideDiv');
								}
								
								console.log("===============");
									
							},
							complete : function() {
								$('#loading').hide();
							}
						});

			});

	
	
	
	$(document)
	.delegate(
			'#rfxTypeList',
			'change',
			function(e) {
				var header = $("meta[name='_csrf_header']").attr(
						"content");
				var token = $("meta[name='_csrf']").attr("content");
				var eventType = $('#rfxTypeList option:selected').text();
				$.ajax({
							url : getContextPath()
									+ '/buyer/RFT/getRfxTemplates/'
									+ eventType,
							type : "GET",
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								xhr.setRequestHeader("Accept",
										"application/json");
								xhr.setRequestHeader("Content-Type",
										"application/json");
								$('#loading').show();
							},
							success : function(data, textStatus,
									request) {

								var templateList = '<option value="" >Select default Template </option>';
								var templateAuctionType = '';
								var type = '';
								$
										.each(
												data,
												function(i, item) {
													if (i == 0) {
														templateAuctionType = item.templateAuctionType;
														type = item.type;
													}
													templateList += '<option value="'
															+ item.id
															+ '"  data-auctionType="'
															+ getAuctionType(item.templateAuctionType)
															+ '" >'
															+ item.templateName
															+ '</option>';
												});
								$('#idRfxTemplate').html(templateList)
										.trigger("chosen:updated");

								if (type == 'RFA'
										&& $(
												'#idRfxTemplate option:selected')
												.val() != '') {
									var html = '<label>Auction Type : </label> <span id="auctionTypeValue">'
											+ getAuctionType(templateAuctionType)
											+ '</span>';
									$('#auctionType').html(html);
									$('#auctionType').removeClass(
											'hideDiv');
								} else {
									$('#auctionType').addClass(
											'hideDiv');
								}
							},
							complete : function() {
								$('#loading').hide();
							}
							
							
							
							
						});
				
				checkBusinessUnitSetting(eventType);

			});
	
	function getAuctionType(auctionType) {
		console
		var result = '';
		if (auctionType == 'FORWARD_ENGISH') {
			result = 'Forward English Auction';
		} else if (auctionType == 'REVERSE_ENGISH') {
			result = 'Reverse English Auction';
		} else if (auctionType == 'FORWARD_SEALED_BID') {
			result = 'Forward Sealed Bid';
		} else if (auctionType == 'REVERSE_SEALED_BID') {
			result = 'Reverse Sealed Bid';
		} else if (auctionType == 'FORWARD_DUTCH') {
			result = 'Forward Dutch Auction';
		} else if (auctionType == 'REVERSE_DUTCH') {
			result = 'Reverse Dutch Auction';
		}
		return result;
	}
	
	$(document).delegate(
			'#idRfxTemplate',
			'change',
			function(e) {
				console.log($('#idRfxTemplate option:selected').attr(
						'data-auctionType'));
				$('#auctionTypeValue').text(
						$('#idRfxTemplate option:selected').attr(
								'data-auctionType'));
			});
	
	
	
	
	
	
	$(document)
	.delegate(
			'#idRfxTemplate',
			'change',
			function(e) {
				var templateId = $('#idRfxTemplate').val();
				
				checkBusinessUnit(templateId);				
			});

	
	
	function checkBusinessUnit(templateId){
		
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
		console.log("=============check business unit called ==============="+templateId);

		var eventId = "${event.id}";
		var eventType="${eventType}";
		
		var url="/buyer/";

		if(eventType==null || eventType == ""){
			
			url+="SR";
		}else{
			url+=eventType;
		}
		 url+="/checkBusinessUnitEmpty/";
		if(eventId=="" || eventId == null){
			
			url+="${sourcingFormRequest.id}";
		}else{
			url+=eventId;
		}
			
		console.log("=============check business unit for template  ==============="+url);
					if(templateId != ''){
						
						var eventType = $('#rfxTypeList option:selected').text();
						console.log("=============check business unit for template  ==============="+eventType);
						url =  url +"/"+eventType+"/"+templateId;
					}
		$.ajax({
	
			url : getContextPath()+url,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept",
						"application/json");
				xhr.setRequestHeader("Content-Type",
						"application/json");
				$('#loading').show();
			},
			success : function(data, textStatus,
					request) {
				$("#businessUnit").show();
				$("#businessUnit").prop('required',true);
				var templateList = '<option value="" >Select Business Unit</option>';
				var templateAuctionType = '';
				var type = '';
				$
						.each(
								data,
								function(i, item) {
									if (i == 0) {
										templateAuctionType = item.templateAuctionType;
										type = item.type;
									}
									templateList += '<option value="'
											+ item.id
											+ '"  data-auctionType="'
											+ getAuctionType(item.templateAuctionType)
											+ '" >'
											+ item.unitName
											+ '</option>';
								});
				$('#businessUnitId').html(templateList)
						.trigger("chosen:updated");

				
			},
			error : function(request, textStatus, errorThrown) {
				console.log(">>>>>>  " , request);
				$("#businessUnit").hide();
				$("#businessUnit").prop('required',false);

			},
			complete : function() {
				$('#loading').hide();
			}
		});

	}

	function checkBusinessUnitSetting(rfxType){
		$("#businessUnitId").removeAttr('data-validation');
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");


var eventId = "${event.id}";
var eventType="${eventType}";

var url="/buyer/";

if(eventType==null || eventType == ""){
	
	url+="SR";
}else{
	url+=eventType;
}
 url+="/checkBusinessUnitEmpty/";
if(eventId=="" || eventId == null){
	
	url+="${sourcingFormRequest.id}";
}else{
	url+=eventId;
}

			if(rfxType != '')
				url =  url +"/"+rfxType;
 			console.log("=============check business unit called ==============="+url);			
$.ajax({
	

	url : getContextPath()+url,
	type : "GET",
	beforeSend : function(xhr) {
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader("Accept",
				"application/json");
		xhr.setRequestHeader("Content-Type",
				"application/json");
		$('#loading').show();
	},
	success : function(data, textStatus,
			request) {
		$("#businessUnit").show();
		$("#businessUnit").prop('required',true);
		$("#businessUnitId").attr('data-validation', 'required');
		var templateList = '<option value="" >Select Business Unit</option>';
		var templateAuctionType = '';
		var type = '';
		$
				.each(
						data,
						function(i, item) {
							if (i == 0) {
								templateAuctionType = item.templateAuctionType;
								type = item.type;
							}
							templateList += '<option value="'
									+ item.id
									+ '"  data-auctionType="'
									+ getAuctionType(item.templateAuctionType)
									+ '" >'
									+ item.unitName
									+ '</option>';
						});
		$('#businessUnitId').html(templateList)
				.trigger("chosen:updated");

		
	},
	error : function(request, textStatus, errorThrown) {
		console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>> " , request);
		$("#businessUnit").hide();
		$("#businessUnit").prop('required',false);

	},
	complete : function() {
		$('#loading').hide();
	}
});

}
</script>
<script>
	function toggleSelectAll(source) {
		var checkboxes = document.querySelectorAll('.custom-doc-checkbox');
		checkboxes.forEach(checkbox => {
			checkbox.checked = source.checked;
		});
	}
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
