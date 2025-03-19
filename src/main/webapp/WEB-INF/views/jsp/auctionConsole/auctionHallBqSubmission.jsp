<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<input type="hidden" value="${event.id}" id="eventIdRefresh">
<c:if test="${not empty auctionRules.lumsumBiddingWithTax}">
	<div class="mar-top-20 lump_sum ">
		<div class="panel pad_all_10 float-left width-100 marg-none marg-bottom-20">
			<div class="col-md-3 col-sm-3">
				<label><spring:message code="supplierauction.enter.bid.amt" /></label>
			</div>
			<form action="${pageContext.request.contextPath}/supplier/submitLumsumEnglishAuction/${event.id}" method="post">
				<div class="col-md-2 col-sm-2">
					<div class="reduce1">
						<input type="text" id="idTotalAmt" placeholder='<spring:message code="enter.amount.placeholder" />' name="totalAmount" class="form-control total_Amount number2"> <input type="hidden" value="${rfaSupplierBq.bq.id}" name="bqId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					</div>
				</div>
				<div class="col-md-5 col-sm-5">
					<label><spring:message code="supplierauction.current.total.amt" /> : ${event.baseCurrency}</label>&nbsp;&nbsp;<label id="finalTotal" class="currentFinalTotal"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="6" value="${rfaSupplierBq.totalAfterTax}" /></label>
				</div>
			</form>
			<c:if test="${event.status eq 'ACTIVE'}">
				<div class="col-md-2 col-sm-2">
					<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out float-right" id="idTakeBid" data-toggle="modal" data-target="#myModal2">
						<spring:message code="supplierauction.submit.price" />
					</button>
				</div>
			</c:if>
		</div>
	</div>
</c:if>
<c:if test="${empty auctionRules.lumsumBiddingWithTax}">
	<div class="mar-top-20 lump_sum ">
		<div class="panel pad_all_10 float-left width-100 marg-none marg-bottom-20">
			<div class="col-md-3 col-sm-3">
				<c:if test="${auctionRules.fowardAuction}">
					<label><spring:message code="supplierauction.increase.by.percent" /></label>
				</c:if>
				<c:if test="${!auctionRules.fowardAuction}">
					<label><spring:message code="supplierbq.reduce.all.item" /></label>
				</c:if>
			</div>
			<div class="col-md-2 col-sm-2">
				<div class="reduce input-group mrg15T mrg15B ">
					<input type="text" placeholder='<spring:message code="enter.value.placeholder" />' class="form-control number" ${!auctionRules.fowardAuction ? "onchange='handleChange(this);'" : ''} id="reduce"> <span class="input-group-addon"> <i class="fa fa-percent" aria-hidden="true"></i>
					</span>
				</div>
			</div>
			<div class="col-md-1 col-sm-1">
				<c:if test="${auctionRules.fowardAuction}">
					<button class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out reduceAllPrices">
						<spring:message code="application.preview" />
					</button>
				</c:if>
				<c:if test="${!auctionRules.fowardAuction}">
					<button class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out reduceAllPrices">
						<spring:message code="application.preview" />
					</button>
				</c:if>
			</div>
			<div class="col-md-1 col-sm-1">
				<div class="undoButtonDiv" style="display: none;">
					<a id="undoButton" data-toggle="tooltip" title='<spring:message code="tooltip.revert.changes" />' class="btn  btn-primary refreshButtonClass marg-left-10 hvr-pop hvr-rectangle-out"> <i class="fa fa-undo" aria-hidden="true"></i>
					</a>
				</div>
			</div>
			<div class="col-md-4 col-sm-4">
				<label><spring:message code="supplierbq.total.amount" /> : ${event.baseCurrency}</label>&nbsp;&nbsp;<label id="finalTotal"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.totalAfterTax}" /></label>
			</div>
			<c:if test="${event.status eq 'ACTIVE'}">
				<div class="col-md-1 col-sm-1">
					<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out float-right" id="idTakeBid" data-toggle="modal" data-target="#myModal1">
						<spring:message code="supplierauction.submit.price" />
					</button>
					<!-- <button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out float-right" id="submitBidPrice" type="submit">Submit Price</button> -->
				</div>
			</c:if>
		</div>
	</div>
</c:if>
<c:if test="${not empty auctionRules.lumsumBiddingWithTax}">
	<div class="mar-top-20 lump_sum ">
		<div class="panel pad_all_10 float-left width-100 marg-none marg-bottom-20 col-md-12 col-sm-12">
			<form action="${pageContext.request.contextPath}/supplier/submitLumsumEnglishAuction/${event.id}" method="post">
				<div class="col-md-3 col-sm-3">
					<c:if test="${auctionRules.fowardAuction}">
						<label><spring:message code="supplierauction.increase.by.percent" /></label>
					</c:if>
					<c:if test="${!auctionRules.fowardAuction}">
						<label><spring:message code="supplierbq.reduce.all.item" /></label>
					</c:if>
				</div>
				<div class="col-md-2 col-sm-2">
					<div class="reduce input-group mrg15T mrg15B ">
						<input type="text" placeholder='<spring:message code="enter.value.placeholder" />' class="form-control number" ${!auctionRules.fowardAuction ? "onchange='handleChange(this);'" : ''}> <span class="input-group-addon"> <i class="fa fa-percent" aria-hidden="true"></i>
						</span> <input type="hidden" value="${rfaSupplierBq.bq.id}" name="bqId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					</div>
				</div>
				<div class="col-md-1 col-sm-1">
					<c:if test="${auctionRules.fowardAuction}">
						<button class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out reduceAllPricesLumsum">
							<spring:message code="application.preview" />
						</button>
					</c:if>
					<c:if test="${!auctionRules.fowardAuction}">
						<button class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out reduceAllPricesLumsum">
							<spring:message code="application.preview" />
						</button>
					</c:if>
				</div>
				<div class="col-md-1 col-sm-1">
					<div class="undoButtonDiv" style="display: none;">
						<a id="undoButton" data-toggle="tooltip" title='<spring:message code="tooltip.revert.changes" />' class="btn  btn-primary refreshButtonClass marg-left-10 hvr-pop hvr-rectangle-out"> <i class="fa fa-undo" aria-hidden="true"></i>
						</a>
					</div>
				</div>
				<div class="col-md-4 col-sm-4">
					<input type="hidden" name="totalAfterTax" value="${rfaSupplierBq.totalAfterTax}" /> <label><spring:message code="supplierbq.total.amount" /> : ${event.baseCurrency}</label>&nbsp;&nbsp;<label id="finalTotal"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.totalAfterTax}" /></label>
				</div>
			</form>
			<c:if test="${event.status eq 'ACTIVE'}">
				<div class="col-md-1 col-sm-1">
					<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out float-right" id="idTakeBid1" data-toggle="modal" data-target="#myModal2">
						<spring:message code="supplierauction.submit.price" />
					</button>
				</div>
			</c:if>
		</div>
	</div>
</c:if>
<div class="fut-bid mar-top-20">
	<div class="row">
		<!-- <div class="col-md-6 col-sm-6 col-xs-6 align-left li-32">Next feasible bid RM 1,000</div>
							<div class="col-md-6 col-XS-6 col-sm-6 align-right">
								<a href="Pre_bid_price.html" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out fr"></a>
							</div> -->
	</div>
</div>
<c:if test="${empty auctionRules.lumsumBiddingWithTax}">
	<form:form id="supplierBqList" method="post" action="${pageContext.request.contextPath}/supplier/submitEnglishAuction/${event.id}/${bqId}" modelAttribute="rfaSupplierBq">
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
		<div class=" float-left width-100">
			<div class="pad0">
				<div class="main_table_wrapper ph_table_border payment height-full1 mega" style="max-height: 500px !important;">
					<table cellpadding="0" cellspacing="0" border="0" width="100%" class="ph_table font-set table" style="top: 0px;">
						<form:hidden path="bq.id" id="bqId" />
						<form:hidden path="id" />
						<input type="hidden" name="supplierId" id="supplierId" value="${supplierId}" />
						<%-- <input type="hidden" value="${rfaSupplierBq.bq.id}" id="eventBqId" name="eventBqId"> --%>
						<input type="hidden" value="${event.id}" id="rfteventId" name="rfteventId">
						<thead>
							<tr>
								<th class="width_50 width_50_fix"><spring:message code="rfaevent.no.col" /></th>

								<th class="width_300 width_300_fix align-left"><spring:message code="label.rftbq.th.itemname" /></th>
								<th class="width_100 width_100_fix align-center"><spring:message code="rfaevent.units.label" /></th>
								<th class="width_200 width_200_fix align-center"><spring:message code="rfaevent.qty.label" /></th>
								<th class="width_200 width_200_fix align-center"><spring:message code="rfaevent.unitprice.label" /></th>
								<th class="width_200 width_200_fix"><spring:message code="prtemplate.total.amount" /></th>
								<c:if test="${auctionRules.itemizedBiddingWithTax}">
									<th class="width_300 width_300_fix align-center pr-75"><spring:message code="rfs.bqsummary.case.tax" /></th>
								</c:if>
								<c:if test="${rfaSupplierBq.field1ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field1Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field2ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field2Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field3ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field3Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field4ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field4Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field5ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field5Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field6ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field6Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field7ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field7Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field8ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field8Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field9ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field9Label}</th>
								</c:if>
								<c:if test="${rfaSupplierBq.field10ToShowSupplier}">
									<th class="width_200 width_200_fix align-center">${field10Label}</th>
								</c:if>
								<c:if test="${auctionRules.itemizedBiddingWithTax}">
									<th class="width_200 width_200_fix align-center"><spring:message code="rfaevent.total.amt.withtax" /></th>
								</c:if>
							</tr>
						</thead>
					</table>
					<table class=" deta ph_table table bq-table" width="100%" border="0" cellspacing="0" cellpadding="0">
						<tbody>
							<c:forEach items="${rfaSupplierBq.supplierBqItems}" var="supplierBqItem" varStatus="vs">
								<c:if test="${empty supplierBqItem.quantity}">
									<tr class=" gradeX parent_box" data-item="${supplierBqItem.id}">
										<form:hidden path="supplierBqItems[${vs.index}].id" />
										<td class="width_50 width_50_fix"><span>${supplierBqItem.level}.${supplierBqItem.order}&nbsp;</span></td>
										<td class="width_300 width_300_fix align-left">
											<p>
												<strong>${supplierBqItem.itemName}</strong>
											</p> <c:if test="${not empty supplierBqItem.itemDescription}">
												<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
												<p class="s1_tent_tb_description s1_text_small">${supplierBqItem.itemDescription}</p>
											</c:if>
										</td>
										<td class="width_100 width_100_fix align-center"></td>
										<td class="width_200 width_200_fix align-center"></td>
										<td class="width_200 width_200_fix align-center"></td>
										<td class="width_200 width_200_fix align-center"><span>&nbsp;</span></td>
										<c:if test="${auctionRules.itemizedBiddingWithTax}">
											<td class="width_300 width_300_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field1ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field2ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field3ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field4ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field5ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field6ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field7ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field8ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field9ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field10ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
										<c:if test="${auctionRules.itemizedBiddingWithTax}">
											<td class="width_200 width_200_fix align-center"></td>
										</c:if>
									</tr>
								</c:if>
								<c:if test="${!empty supplierBqItem.quantity}">
									<tr class="sub_item gradeX sub-child" data-item="${supplierBqItem.id}">
										<input type="hidden" name="totalAmountWithTax" id="" value="${supplierBqItem.totalAmountWithTax}" />
										<input type="hidden" name="itemName" id="" value="${supplierBqItem.itemName}" />
										<input type="hidden" name="quantity" id="" value="${supplierBqItem.quantity}" />
										<%-- <form:hidden path="supplierBqItems[${vs.index}].id" /> --%>
										<form:hidden path="supplierBqItems[${vs.index}].id" />
										<form:hidden path="supplierBqItems[${vs.index}].unitPrice" />
										<form:hidden path="supplierBqItems[${vs.index}].priceType" />
										<form:hidden path="supplierBqItems[${vs.index}].totalAmount" />
										<form:hidden path="supplierBqItems[${vs.index}].tax" />
										<form:hidden path="supplierBqItems[${vs.index}].taxType" />
										<form:hidden path="supplierBqItems[${vs.index}].field1" />
										<form:hidden path="supplierBqItems[${vs.index}].field2" />
										<form:hidden path="supplierBqItems[${vs.index}].field3" />
										<form:hidden path="supplierBqItems[${vs.index}].field4" />
										<form:hidden path="supplierBqItems[${vs.index}].field5" />
										<form:hidden path="supplierBqItems[${vs.index}].field6" />
										<form:hidden path="supplierBqItems[${vs.index}].field7" />
										<form:hidden path="supplierBqItems[${vs.index}].field8" />
										<form:hidden path="supplierBqItems[${vs.index}].field9" />
										<form:hidden path="supplierBqItems[${vs.index}].field10" />
										<form:hidden path="supplierBqItems[${vs.index}].totalAmountWithTax" />
										<td class="width_50 width_50_fix align-center"><span>${supplierBqItem.level}.${supplierBqItem.order}&nbsp;</span></td>
										<td class="width_300 width_300_fix align-left">
											<p>${supplierBqItem.itemName}</p> <c:if test="${not empty supplierBqItem.itemDescription}">
												<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
												<p class="s1_tent_tb_description s1_text_small">${supplierBqItem.itemDescription}</p>
											</c:if>
										</td>
										<td class="width_100 width_100_fix align-center">${supplierBqItem.uom.uom}</td>
										<td class="width_200 width_200_fix align-center"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.quantity}" /></td>
										<td class="width_200 width_200_fix align-center"><fmt:formatNumber var="unitPriceFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.unitPrice}" /> <input type="text" class="form-control tab_input" data-pos="1" data-type="${supplierBqItem.priceType}" name="unitPrice" value="${unitPriceFormated}" data-validation="required" ${supplierBqItem.priceType != undefined ? (supplierBqItem.priceType == 'BUYER_FIXED_PRICE' ? ' readonly="readonly" ' : '') : ''}></td>
										<td class="width_200 width_200_fix align-center"><fmt:formatNumber var="totalAmountFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.totalAmount != undefined ? supplierBqItem.totalAmount : ''}" /> 
											<input data-pos="2" class="validate form-control text-right" data-pos="2" type="text" name="totalAmount" data-validation="required length" data-validation-length="max20" value="${totalAmountFormated}" placeholder="${child.totalAmount}"
											${supplierBqItem.priceType != undefined ? (supplierBqItem.priceType == 'BUYER_FIXED_PRICE' || event.disableTotalAmount ? ' readonly="readonly" ' : '') : ''} />
										</td>
										<c:if test="${auctionRules.itemizedBiddingWithTax}">
											<td class="width_300 width_300_fix align-left"><select name="taxType" id="custom_${supplierBqItem.id}" class="chosen-select disablesearch" data-pos="4">
													<c:forEach items="${taxTypeList}" var="taxType">
														<option value="${taxType}" ${taxType eq supplierBqItem.taxType ? 'selected':''}>${taxType}</option>
													</c:forEach>
											</select> <fmt:formatNumber var="taxFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.tax != undefined ? supplierBqItem.tax : ''}" /> <input data-pos="3" type="text" data-pos="3" name="tax" class="validate form-control width_30per" value="${taxFormated}" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field1ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="5" name="field1" ${child.supplierBq.field1FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field1}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field2ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="6" name="field2" ${child.supplierBq.field2FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field2}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field3ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="7" name="field3" ${child.supplierBq.field3FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field3}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field4ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="8" name="field4" ${child.supplierBq.field4FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field4}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field5ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="9" name="field5" ${child.supplierBq.field5FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field5}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field6ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="10" name="field6" ${child.supplierBq.field6FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field6}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field7ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="11" name="field7" ${child.supplierBq.field7FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field7}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field8ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="12" name="field8" ${child.supplierBq.field8FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field8}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field9ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="13" name="field9" ${child.supplierBq.field9FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field9}"></td>
										</c:if>
										<c:if test="${rfaSupplierBq.field10ToShowSupplier}">
											<td class="width_200 width_200_fix align-center"><input type="text" data-pos="14" name="field10" ${child.supplierBq.field10FilledBy == 'BUYER' ? 'readonly' : ''} class="validate form-control align-center text-right" value="${supplierBqItem.field10}"></td>
										</c:if>
										<input type="hidden" name="priceType" value="${supplierBqItem.priceType}">
										</td>
										<c:if test="${auctionRules.itemizedBiddingWithTax}">
											<td class="width_200 width_200_fix align-center"><span class="totalAmountWithTax"> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.totalAmountWithTax}" />
											</span></td>
										</c:if>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="total-with-tax pad_all_15 marg-top-10 marg-bottom-10">
					<div class="total-with-tax-inner">
						<label><spring:message code="submission.report.grandtotal" /> : </label> <input type="hidden" name="grandTotalOfBq" id="idGrandTotalOfBq" value="${rfaSupplierBq.grandTotal}">
						<form:hidden path="grandTotal" />
						<label>&nbsp;${event.baseCurrency.currencyCode}&nbsp;</label>
						<p id="grandTotalOfBq">
							<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.grandTotal}" />
						</p>
					</div>
				</div>
				<c:if test="${auctionRules.itemizedBiddingWithTax}">
					<div class="additinol-text pad_all_15 marg-top-20 marg-bottom-20">
						<div class="add_tex">
							<label>&nbsp;</label>
							<%-- <form:hidden path="additionalTax" /> don't know why this is here please check  --%>
							<fmt:formatNumber var="additionalTaxFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.additionalTax}" />
							<input type="text" name="additionalTax" id="additionalTax" class="validateregex form-control" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$" value="${additionalTaxFormated}" placeholder='<spring:message code="rfaevent.additional.tax.placeholder" />' data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}">
						</div>
						<div class="Description_lower">
							<label><spring:message code="rfaevent.additional.tax.case1" /></label>
							<%-- <form:hidden path="taxDescription" />  --%>
							<input type="hidden" name="additionalTaxDescription" id="idAdditionalTaxDescription" value="${rfaSupplierBq.taxDescription}">
							<textarea class="form-control" name="taxDescription"  >${rfaSupplierBq.taxDescription}</textarea>
						</div>
					</div>
				</c:if>
				<form:hidden path="totalAfterTax" />
				<input type="hidden" name="amountAfterTax" id="idAmountAfterTax" value="${rfaSupplierBq.totalAfterTax}">
				<c:if test="${auctionRules.itemizedBiddingWithTax}">
					<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">
						<div class="total-with-tax-inner">
							<label><spring:message code="rfaevent.total.after.tax" /> : </label> <label class="color-red">&nbsp;${event.baseCurrency.currencyCode}&nbsp;</label>
							<p class="color-red" id="amountAfterTax">
								<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.totalAfterTax}" />
							</p>
						</div>
					</div>
				</c:if>
				<!-- <div class="row">
							<div class="col-md-12 col-xs-12 col-sm-12">
								<button class="btn btn-black ph_btn_midium back_to_BQ hvr-pop hvr-rectangle-out1">Back to Bill of Quantity</button>
								<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out saveSupplierBq">Save</button>
							</div>
						</div> -->
			</div>
		</div>
		<%-- <c:if test="${event.status eq 'ACTIVE'}">
			<button type="submit" class="btn btn-info btn-save hvr-pop hvr-rectangle-out fr">Save</button>
		</c:if> --%>
	</form:form>
</c:if>


<div class="modal fade" id="myModal1" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<label style="font-size: 16px;"> <spring:message code="bidsupplier.confirm.submit.bid" />
				</label>
				<button type="button" class="close for-absulate" data-dismiss="modal">&times;</button>
			</div>

			<div class="modal-body">
				<spring:message code="supplierevent.sure.want.submit" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1" style="margin-top: 2%;">
				<button class="btn btn-info  ph_btn_small hvr-pop hvr-rectangle-out del_inv pull-left" style="margin-left: 1%;" id="submitBidPrice" type="submit">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black ph_btn_small hvr-pop hvr-rectangle-out1" style="margin-left: 44%;" data-dismiss="modal">
					<spring:message code="application.no2" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="myModal2" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/supplier/submitLumsumEnglishAuction/${event.id}" method="post">
			<div class="modal-content">
				<div class="modal-header">
					<label style="font-size: 16px;"> <spring:message code="bidsupplier.confirm.submit.bid" />
					</label>
					<button type="button" class="close for-absulate" data-dismiss="modal">&times;</button>
				</div>
				<div class="modal-body">
					<label> <spring:message code="supplierevent.sure.want.submit" />
					</label>
				</div>
				<input type="hidden" name="totalAmount" id="totalAmountModel" value="${rfaSupplierBq.totalAfterTax}" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" value="${rfaSupplierBq.bq.id}" name="bqId">
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1" style="margin-top: 2%;">
					<button class="btn btn-info  ph_btn_small hvr-pop hvr-rectangle-out del_inv pull-left" style="margin-left: 1%;" id="submitBidPrice1" type="submit">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black ph_btn_small hvr-pop hvr-rectangle-out1" style="margin-left: 44%;" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>



<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-1.11.3.min.js"/>"></script>
<script>

	var isLumpsum = ${not empty auctionRules.lumsumBiddingWithTax};

	function handleChange(input) {
		if (input.value < 0)
			input.value = 0;
		if (input.value > 100)
			input.value = 100;
	}
	
		
	$('.number').keypress(function(event) {
	    var $this = $(this);
	    if ((event.which != 46 || $this.val().indexOf('.') != -1) &&
	       ((event.which < 48 || event.which > 57) &&
	       (event.which != 0 && event.which != 8))) {
	           event.preventDefault();
	    }

	    var text = $(this).val();
	    if ((event.which == 46) && (text.indexOf('.') == -1)) {
	        setTimeout(function() {
	            if ($this.val().substring($this.val().indexOf('.')).length > 3) {
	                $this.val($this.val().substring(0, $this.val().indexOf('.') + 3));
	            }
	        }, 1);
	    }

	    if ((text.indexOf('.') != -1) &&
	        (text.substring(text.indexOf('.')).length > 2) &&
	        (event.which != 0 && event.which != 8) &&
	        ($(this)[0].selectionStart >= text.length - 2)) {
	            event.preventDefault();
	    }      
	});
	
	var decimal = '${event.decimal}';
	$('.number2').keypress(function(event) {
	    var $this = $(this);
	    if ((event.which != 46 || $this.val().indexOf('.') != -1) &&
	       ((event.which < 48 || event.which > 57) &&
	       (event.which != 0 && event.which != 8))) {
	           event.preventDefault();
	    }

	    var text = $(this).val();
	    if ((event.which == 46) && (text.indexOf('.') == -1)) {
	        setTimeout(function() {
	            if ($this.val().substring($this.val().indexOf('.')).length > 3) {
	                $this.val($this.val().substring(0, $this.val().indexOf('.') + 3));
	            }
	        }, 1);
	    }

	    if ((text.indexOf('.') != -1) &&
	        (text.substring(text.indexOf('.')).length > parseFloat(decimal)) &&
	        (event.which != 0 && event.which != 8) &&
	        ($(this)[0].selectionStart >= text.length - parseFloat(decimal))) {
	            event.preventDefault();
	    }      
	});
</script>
<style>
.refreshButtonClass {
	height: 35px;
	min-width: 40px;
	line-height: 35px;
	margin-left: 30px;
}

.pr-75 {
	padding-right: 75px !important;
}
</style>