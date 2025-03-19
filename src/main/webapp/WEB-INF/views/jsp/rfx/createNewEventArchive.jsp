<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- Modal content-->
<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">
	<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/copyEventTo" modelAttribute="event" id="idFrmConcludeWithEvent" method="post">
		<form:hidden path="id" />
		<div class="row marg-bottom-10" id="idSelectedEvent">
			<div class="row marg-top-10 marg_left_0 v-center">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label cssClass="marg-top-20"> <spring:message code="eventsummary.eventdetail.next" />
					</label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select id="rfxTypeList" name="selectedRfxType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" data-validation="required" >
						<c:forEach items="${rfxType}" var="rfxType">
							<option>${rfxType}</option>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="row marg-top-10 marg_left_0 hideSelect v-center">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label cssClass="marg-top-20"> <spring:message code="rfx.auction.type.label" />
					</label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select name="auctionType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" id="idAuctionType" data-validation="required" data-validation-depends-on="selectedRfxType" data-validation-depends-on-value="RFA" >
						<option value=""><spring:message code="eventarchieve.auction.type" /></option>
						<option value="FORWARD_ENGISH"><spring:message code="eventarchieve.forward.english" /></option>
						<option value="REVERSE_ENGISH"><spring:message code="eventarchieve.reverse.english" /></option>
						<option value="FORWARD_SEALED_BID"><spring:message code="eventarchieve.forward.sealedbid" /></option>
						<option value="REVERSE_SEALED_BID"><spring:message code="eventarchieve.reverse.sealedbid" /></option>
						<option value="FORWARD_DUTCH"><spring:message code="eventarchieve.forward.dutch" /></option>
						<option value="REVERSE_DUTCH"><spring:message code="eventarchieve.reverse.dutch" /></option>
					</select>
				</div>
			</div>

			<div class="row marg-top-10 marg_left_0 hideBq v-center">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class=""> <spring:message code="eventdescription.billofquantity.label" />
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


			<div class="row marg-top-10 marg_left_0 v-center">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class=""><spring:message code="eventarchieve.select.template" /></label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select name="idRfxTemplate" class="form-control chosen-select disablesearch" id="idRfxTemplate" value="${templateId}" data-validation="required" >
						<option value=""><spring:message code="eventarchieve.select.template" /></option>
						<c:forEach items="${rfxTemplateList}" var="template">
							<option value="${template.id}">${template.templateName}</option>
						</c:forEach>
					</select>
				</div>
				<input type="hidden" value="${event.id}" id="eventId">
			</div>


			<div id="businessUnit" class="row marg-top-10 marg_left_0 v-center">
				<div class="col-md-3 marg-left-10" style="white-space:nowrap;">
					<label class=""><spring:message code="pr.select.business.unit" /></label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select name="businessUnitId" id="businessUnitId" class="chosen-select disablesearch" >
						<c:forEach items="${businessUnits}" var="businessUnit">
							<option value="${businessUnit.id}">${businessUnit.unitName}</option>
						</c:forEach>
					</select>
				</div>
			</div>

		</div>


		<c:if test="${eventType != 'RFI'}">
			<div class="row marg-bottom-10">
				<div class="col-md-3 marg-left-10">
					<label class="marg-left-5"> <spring:message code="Product.remarks" />
					</label>
				</div>
				<div class="col-md-8">
					<spring:message code="Product.remarks" var="remarks" />
					<form:textarea id="AwRemk" path="concludeRemarks" data-validation="required length" placeholder="${remarks}" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
					<span class="sky-blue">Max 500 characters only</span>
				</div>
			</div>
		</c:if>

		<!-- 		<div class="panel sum-accord marg-top-5"> -->
		<!-- 			<div id="invitedSupplier" class="panel-collapse collapse"> -->
		<!-- 				<div class="panel-body"> -->
		<div class="table-responsive width100 borderAllData">
			<table class="tabaccor padding-none-td table" cellpadding="0" cellspacing="0" border="0">
				<thead>
					<tr>
						<th class="v-align-middle"><input type="checkbox" id="allInvited" />&nbsp;</th>
						<th class="align-left v-align-middle">Supplier Name</th>
						<th class="align-left v-align-middle">Contact Number</th>
						<th class="align-left v-align-middle">Communication Mail</th>

					</tr>
				</thead>
				<tbody>
					<c:forEach items="${invitedSupplier}" var="invitedSup">
						<tr>
							<!-- width_200 width_200_fix -->
							<td><input type="checkbox" value="${invitedSup.supplier.id}" name="invitedSupp" class="unchecked" />&nbsp;</td>
							<td class="align-left">${invitedSup.supplier.companyName}</td>
							<td class="align-left">${invitedSup.supplier.companyContactNumber}</td>
							<td class="align-left">${invitedSup.supplier.communicationEmail}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<!-- 				</div> -->
		<!-- 			</div> -->
		<!-- 		</div> -->
		<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			<c:if test="${eventType eq 'RFA'}">
				<input type="hidden" value="${event.auctionType}" id="RfaType">
			</c:if>
			<form:button type="button" id="btnConcludeWithNextEvent" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
				<spring:message code="eventarchieve.conclude" />
			</form:button>
			<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
				<spring:message code="application.cancel" />
			</button>
		</div>
	</form:form>
</div>
<c:if test="${eventType ne 'RFI'}">
	<div id="tab-2" class="tab-content doc-fir tab-main-inner" style="display: none;">
		<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/concludeEvent" id="idFrmConclude" modelAttribute="event" method="post">
			<form:hidden path="id" />
			<div class="row marg-bottom-10 marg-left-10 marg-right-10 marg-top-20">
				<div class="col-md-3 col-sm-4 col-xs-6">
					<label><spring:message code="eventarchieve.award.to" /></label>
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
					<spring:message code="enter.awarded.price.placeholder" var="awardprice" />
					<form:input path="awardedPrice" type="text" data-validation="required validate_custom_length" class="form-control textarea-counter" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" rows="3" placeholder="${awardprice}" />
				</div>
			</div>
			<div class="row marg-bottom-10 marg-left-10 marg-right-10">
				<div class="col-md-3">
					<label class="marg-top-10"> <spring:message code="eventarchieve.award.remark" />
					</label>
				</div>
				<div class="col-md-9">
					<form:textarea id="AwdRem" path="concludeRemarks" data-validation="required length" placeholder="${remarks}" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form:button type="button" id="btnConcludeSubmit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
					<spring:message code="eventarchieve.conclude.button" />
				</form:button>
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

.modal-dialog.for-delete-all.reminder {
	width: 670px !important;
}

.v-align-middle {
	vertical-align: middle !important;
}

.mt-9 {
	position: relative;
	top: 9px;
}

.mr-0 {
	margin-right: 0;
}

.marg-top-20 {
	margin-top: 20px !important;
}

.v-center {
	display: flex;
	align-items: center;
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
				}
			} else {
				$("#idAuctionType").removeAttr('data-validation');
				$(".hideSelect").hide();
				$("#bqList").removeAttr('data-validation');
				$(".hideBq").hide();
			}
		});
	}).change();


	$(document).ready(function() {
		
		$('#btnConcludeSubmit').click(function() {
			if($('#idFrmConclude').isValid()) {
				$(this).addClass('disabled');
				$('#loading').show();
				$('#idFrmConclude').submit();
			}
		});
		
		$('#btnConcludeWithNextEvent').click(function() {
			if($('#idFrmConcludeWithEvent').isValid()) {
				$(this).addClass('disabled');
				$('#loading').show();
				$('#idFrmConcludeWithEvent').submit();
			}
		});

		
		$("#idAuctionType").removeAttr('data-validation');
		$("#bqList").removeAttr('data-validation');

		$('.unchecked').click(function(){
           	if($(this).is(":not(:checked)")){
            	$("#allInvited").prop("checked", false);
            }
        });
				
		$("#cancelCreateNext").click(function(){
			var remark=	$('.textarea-counter').val("");
			});
		
		$("#allInvited").click(function(){
		    $('input:checkbox').not(this).prop('checked', this.checked);
		});
		
		
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
			modules : 'sanitize'
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
	
	
	$(document).delegate('#idAuctionType', 'change', function(e) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var auctionType = $('#idAuctionType option:selected').text();
		$.ajax({
			url : getContextPath() + '/buyer/RFA/getRfaTemplates/' + auctionType,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {

				var templateList = '<option value="" >Select default Template </option>';
				var templateAuctionType = '';
				var type = '';
				$.each(data, function(i, item) {
					if (i == 0) {
						templateAuctionType = item.templateAuctionType;
						type = item.type;
					}
					templateList += '<option value="' + item.id + '"  data-auctionType="' + getAuctionType(item.templateAuctionType) + '" >' + item.templateName + '</option>';
				});
				$('#idRfxTemplate').html(templateList).trigger("chosen:updated");

				if (type == 'RFA' && $('#idRfxTemplate option:selected').val() != '') {
					var html = '<label>Auction Type : </label> <span id="auctionTypeValue">' + getAuctionType(templateAuctionType) + '</span>';
					$('#auctionType').html(html);
					$('#auctionType').removeClass('hideDiv');
				} else {
					$('#auctionType').addClass('hideDiv');
				}
					
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	
	$(document).delegate('#rfxTypeList', 'change', function(e) {
		var auctionType = $('#RfaType').val();
		if(auctionType == "FORWARD_DUTCH" || auctionType == "REVERSE_DUTCH" ){
			if( $('#bqList').has('option').length == 0 ) {
				$('.hideBq').hide();
			}
		}
		
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var eventType = $('#rfxTypeList option:selected').text();
		
		$.ajax({
			url : getContextPath() + '/buyer/RFT/getRfxTemplates/' + eventType,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {

				var templateList = '<option value="" >Select default Template </option>';
				var templateAuctionType = '';
				var type = '';
				
				$.each(data, function(i, item) {
					if (i == 0) {
						templateAuctionType = item.templateAuctionType;
						type = item.type;
					}
					templateList += '<option value="' + item.id + '"  data-auctionType="' + getAuctionType(item.templateAuctionType) + '" >' + item.templateName + '</option>';
				});
				
				$('#idRfxTemplate').html(templateList).trigger("chosen:updated");

				if (type == 'RFA' && $('#idRfxTemplate option:selected').val() != '') {
					var html = '<label>Auction Type : </label> <span id="auctionTypeValue">' + getAuctionType(templateAuctionType) + '</span>';
					$('#auctionType').html(html);
					$('#auctionType').removeClass('hideDiv');
				} else {
					$('#auctionType').addClass('hideDiv');
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
	
	$(document).delegate('#idRfxTemplate', 'change', function(e) {
		console.log($('#idRfxTemplate option:selected').attr('data-auctionType'));
		$('#auctionTypeValue').text( $('#idRfxTemplate option:selected').attr('data-auctionType'));
	});
	
	$(document).delegate('#idRfxTemplate', 'change', function(e) {
		var templateId = $('#idRfxTemplate').val();
		checkBusinessUnit(templateId);				
	});

	function checkBusinessUnit(templateId) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url="/buyer/${eventType}/checkBusinessUnitEmpty/${event.id}";

		if(templateId != ''){
			var eventType = $('#rfxTypeList option:selected').text();
			url =  url +"/"+eventType+"/"+templateId;
		}
		
		$.ajax({
			url : getContextPath()+url,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$("#businessUnit").show();
				$("#businessUnit").prop('required',true);
				var templateList = '<option value="" >Select Business Unit</option>';
				var templateAuctionType = '';
				var type = '';
				$.each(data, function(i, item) {
					if (i == 0) {
						templateAuctionType = item.templateAuctionType;
						type = item.type;
					}
					templateList += '<option value="' + item.id + '"  data-auctionType="' + getAuctionType(item.templateAuctionType) + '" >' + item.unitName + '</option>';
				});
				
				$('#businessUnitId').html(templateList).trigger("chosen:updated");
			},
			error : function(request, textStatus, errorThrown) {
				$("#businessUnit").hide();
				$("#businessUnit").prop('required',false);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	}

	function checkBusinessUnitSetting(rfxType){
		
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url="/buyer/${eventType}/checkBusinessUnitEmpty/${event.id}"
		if(rfxType != '') {
			url =  url +"/"+rfxType;
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
			success : function(data, textStatus, request) {
				$("#businessUnit").show();
				$("#businessUnit").prop('required',true);
				var templateList = '<option value="" >Select Business Unit</option>';
				var templateAuctionType = '';
				var type = '';
				$.each(data, function(i, item) {
					if (i == 0) {
						templateAuctionType = item.templateAuctionType;
						type = item.type;
					}
					templateList += '<option value="' + item.id + '"  data-auctionType="' + getAuctionType(item.templateAuctionType) + '" >' + item.unitName + '</option>';
				});
				
				$('#businessUnitId').html(templateList).trigger("chosen:updated");
			},
			error : function(request, textStatus, errorThrown) {
				$("#businessUnit").hide();
				$("#businessUnit").prop('required',false);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	}

	
	
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
