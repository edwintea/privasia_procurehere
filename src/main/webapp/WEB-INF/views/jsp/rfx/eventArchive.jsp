<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- Modal content-->
<c:if test="${eventType != 'RFI'}">
	<div class="modal-body">
		<div class="clear"></div>
		<div class="home_tab_wrap">
			<ul class="tabs pull-left">
				<li href="#" class="tab-link current" data-tab="tab-1"><spring:message code="eventsummary.conclude.event" /></li>
				<li class="tab-link" data-tab="tab-2"><spring:message code="eventarchieve.award.to" /></li>
			</ul>
		</div>
	</div>
</c:if>
<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">
	<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/copyEventTo" modelAttribute="event" method="post">
		<form:hidden path="id" />
		<div class="row marg-bottom-10" id="idSelectedEvent">
			<%-- <div class="row marg-top-10 marg_left_0">
				<div class="col-md-3 marg-left-10">
					<label cssClass="marg-top-20"> From </label>
				</div>
				<div class="col-md-3">
					<label class="marg-top-10"> ${eventType} </label>
				</div>
			</div> --%>
			<div class="row marg-top-10 marg_left_0">
				<div class="col-md-3 marg-left-10">
					<label cssClass="marg-top-20"> <spring:message code="eventsummary.eventdetail.next" /> </label>
				</div>
				<div class="col-md-8 marg-top-10 marg-bottom-10">
					<select id="rfxTypeList" name="selectedRfxType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch">
						<option value=""><spring:message code="eventarchieve.no.further.action" /></option>
						<c:forEach items="${rfxType}" var="rfxType">
							<option>${rfxType}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="row marg-top-10 marg_left_0 hideSelect">
				<div class="col-md-3 marg-left-10">
					<label cssClass="marg-top-20"> <spring:message code="rfx.auction.type.label" /> </label>
				</div>
				<div class="col-md-8  marg-bottom-10">
					<select name="auctionType" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" id="idAuctionType" data-validation="required">
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
			<div class="row marg-top-10 marg_left_0 hideBq">
				<div class="col-md-3 marg-left-10">
					<label cssClass="marg-top-20"> <spring:message code="eventdescription.billofquantity.label" /> </label>
				</div>
				<div class="col-md-8  marg-bottom-10">
					<select id="bqList" name="bqId" class="chosen-select disablesearch" cssClass="form-control chosen-select disablesearch" data-validation="required">
						<c:forEach items="${bqList}" var="bq">
							<option value="${bq.id}">${bq.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</div>
		<c:if test="${eventType != 'RFI'}">
			<div class="row marg-bottom-10">
				<div class="col-md-3 marg-left-10">
					<label class="marg-top-10 marg-left-5"> <spring:message code="pr.remark" /> </label>
				</div>
				<div class="col-md-8">
					<spring:message code="conclude.remark.placeholder" var="concluderemarks"/>
					<form:textarea id="AwRemk" path="concludeRemarks" data-validation="required length" placeholder="${concluderemarks}" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
		</c:if>
		<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			<form:button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="eventarchieve.conclude" /></form:button>
			<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.cancel" /></button>
		</div>
	</form:form>
</div>
<c:if test="${eventType ne 'RFI'}">
	<div id="tab-2" class="tab-content doc-fir tab-main-inner" style="display: none;">
		<form:form action="${pageContext.request.contextPath}/buyer/${eventType}/concludeEvent" modelAttribute="event" method="post">
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
												<li><input type="checkbox" name="awardedSuppliers" value="${sup.id}" data-value="${sup.companyName}" class="custom-checkbox" id="inlineCheckbox115"> <span> <strong> ${sup.companyName}</strong>
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
					<label><spring:message code="summarydetails.event.conclusion.award.price" /></label>
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
					<spring:message code="enter.awarded.price.placeholder"  var="awardprice"/>
					<form:input path="awardedPrice" type="text" data-validation="required validate_custom_length" class="form-control textarea-counter" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}"  rows="3" placeholder="${awardprice}" />
				</div>
			</div>
			<div class="row marg-bottom-10 marg-left-10 marg-right-10">
				<div class="col-md-3">
					<label class="marg-top-10"> <spring:message code="eventarchieve.award.remark" /> </label>
				</div>
				<div class="col-md-9">
					<form:textarea id="AwdRem" path="concludeRemarks" data-validation="required length" placeholder="${concluderemarks}" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form:button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="eventarchieve.conclude" /></form:button>
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
			$(".hideSelect").show();
			if(${eventType != 'RFI'}){
			$(".hideBq").show();
			}
		} else {

			$(".hideSelect").hide();
			$(".hideBq").hide();

		}

	});

}).change();



	$(document).ready(function() {
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
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
