<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
</style>
<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<input type="hidden" id="poId" value="${po.id}">
			<div class="tag-line">
				<h2>
					<spring:message code="supplier.po.summary.poNumber" />
					: ${po.poNumber}
				</h2>
				<br>
				<h2>
					<spring:message code="supplier.po.summary.poDate" />
					:
					<fmt:formatDate value="${po.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</h2>
				<br>
				<h2>
					<spring:message code="label.buyer.poList.reviseDate" />
					: <c:if test="${not empty po.poRevisedDate}"><fmt:formatDate value="${po.poRevisedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></c:if> <c:if test="${empty po.poRevisedDate}">-</c:if>
				</h2>
				
				<br>
				<h2>
					<spring:message code="application.lable.buyer" />
					: ${po.buyer.companyName}
				</h2>
				<br />
				<h2>
					<spring:message code="supplier.po.summary.businessUnit" />
					:
					<c:if test="${empty po.businessUnit}">N/A</c:if>${po.businessUnit.unitName}
				</h2>

				<c:if test="${poFinanceRequest != null}">
					<br />
					<div class="text-warning">
						<b>Note:</b> You have applied for Financing of this PO.
					</div>
					<div class="text-warning">
						<b>Application Date:</b>
						<fmt:formatDate value="${poFinanceRequest.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						.
					</div>
					<div class="text-warning">
						<b>Financing Application Status:</b> ${poFinanceRequest.status}
					</div>
					<div class="text-warning">
						<b>Proceed of Invoice(s) linked to this PO shall be paid directly to FinansHere Sdn Bhd</b>
					</div>
					<div class="text-warning">
						<b>Please do not create invoice until the Financing Application Status has changed to ACTIVE.</b>
					</div>
				</c:if>
			</div>
		</div>
		<div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">
			<c:if test="${po.status == 'ORDERED'}">
				<button id="declinedPo" class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.po.declined" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span> <span class="button-content"><spring:message code="supplier.po.summary.declined" /></span>
				</button>

				<button id="acceptPo" class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.po.accept" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span> <span class="button-content"><spring:message code="supplier.po.summary.accept" /></span>
				</button>
			</c:if>
			<c:if test="${po.status=='ACCEPTED'}">
				<div class="btn-group pull-right marg-right-10">
					<button type="button" id="idActionBtn" class="btn ph_btn_small float-right btn-primary hvr-pop marg-left-10 dropdown-toggle" data-toggle="dropdown">
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-angle-down"></i></span> <span class="button-content">Action</span>
					</button>
					<ul class="dropdown-menu" role="menu">
						<li><a href="${pageContext.request.contextPath}/supplier/supplierPoReport/${po.id}"><spring:message code="application.download" /></a></li>
						<li class="divider"></li>

						<c:if test="${!isShare && !onboarded}">
							<li><a href="#" id="poShare"><spring:message code="supplier.po.share.po" /></a></li>
						</c:if>
						<c:if test="${!onboarded}">
							<%-- <li><a href="#" id="poRequest"><spring:message code="supplier.po.request.po" /></a></li> --%>
							<li><a href="https://app.finanshere.com/">Sign Up for FinansHere</a></li>
						</c:if>

						<li><a id="doMPA" href="#"><spring:message code="supplier.ship.mpa" /></a></li>

						<li><a href="https://www.myparcelasia.com/track" target="_blank">Track Item</a></li>

						<c:if test="${onboarded and poFinanceRequest == null}">
							<li><a href="#" id="finanshereRequest" data-toggle="modal" data-target="#modal-finanshereRequest"><spring:message code="supplier.po.finanshere.request" /></a></li>
						</c:if>
						<c:if test="${!onboarded or (onboarded and poFinanceRequest == null)}">
							<li class="divider"></li>
						</c:if>
						<li><a href="#" id="doCreate"><spring:message code="supplier.po.summary.doCreate" /></a></li>
						<li><a href="#" id="invoiceCreate"><spring:message code="supplier.po.summary.invoice" /></a></li>
					</ul>
				</div>
			</c:if>

		</div>
	</div>
</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">

	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="prsummary.general.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="po.reference.number" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.referenceNumber}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
            <div class="col-sm-4 col-md-3 col-xs-6">
                <label> <spring:message code="poSummary.creator" />
                </label>
            </div>
            <div class="col-sm-5 col-md-5 col-xs-6">
                <p>
                    ${po.createdBy.name} <br> ${po.createdBy.communicationEmail} <br>
                    <c:if test="${not empty po.buyer.companyContactNumber}">
                        <spring:message code="prdraft.tel" />: ${po.buyer.companyContactNumber}</c:if>
                    <c:if test="${not empty po.buyer.faxNumber}">
                        <spring:message code="prtemplate.fax" />: ${po.buyer.faxNumber}</c:if>
                    <c:if test="${not empty po.createdBy.phoneNumber}">
                        <spring:message code="prtemplate.hp" />: ${ po.createdBy.phoneNumber}</c:if>
                </p>
            </div>
        </div>
        <div class="form-tander1 requisition-summary-box">
            <div class="col-sm-4 col-md-3 col-xs-6">
                <label> <spring:message code="pr.requester" />
                </label>
            </div>
            <div class="col-sm-5 col-md-5 col-xs-6">
                <p>${po.requester}</p>
            </div>
        </div>
        <div class="form-tander1 requisition-summary-box">
            <div class="col-sm-4 col-md-3 col-xs-6">
                <label> <spring:message code="pr.description" />
                </label>
            </div>
            <div class="col-sm-5 col-md-5 col-xs-6">
                <p>${po.description}</p>
            </div>
        </div>

	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="rfs.summary.finance.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.paymentterm.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.paymentTerm}</p>
			</div>
		</div>
		<c:if test="${not empty po.paymentTermDays}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> Payment Days : </label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${po.paymentTermDays}</p>
				</div>
			</div>
		</c:if>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.correspondence.address" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="prsummary.address.title" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty po.correspondAddressTitle}">
						${po.correspondAddressTitle} <br> ${po.correspondAddressLine1},
						<c:if test="${not empty po.correspondAddressLine2}">${po.correspondAddressLine2} ,</c:if>${ po.correspondAddressZip},
						<br>${ po.correspondAddressState},${ po.correspondAddressCountry}
					</c:if>
				</p>
			</div>
		</div>
	</div>
<div class="panel sum-accord upload_download_wrapper clearfix marg-top-10 event_info">
		<div class="panel-heading">
			<h4 class="panel-title list-pad-0" style="margin-bottom: 0px;">
				<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseThree"><spring:message code="posummary.po.doucment" /> </a>
			</h4>
		</div>
		<div id="collapseThree" class="panel-collapse collapse in">
			<div class="panel-body mega">
				<table class="tabaccor padding-none-td header" width="100%" cellpadding="0" cellspacing="0" border="0" id="poDocList">
					<thead>
						<tr>
							<th class="width_100 width_100_fix align-left">
								<spring:message code="application.action" />
							</th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.name" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.datetime" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.size" /></th>
						</tr>
					</thead>
				</table>
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="deta pad-for-table" id="poDocList">
					<tbody>
						<c:forEach var="listDoc" items="${listDocs}">
							<tr>
								<td class="width_100 width_100_fix align-left wo-rp">
									<form method="GET">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<c:url var="downloadPoDocument" value="/supplier/po/downloadPoDocument/${listDoc.id}" />
										<a id="downloadButton" href="${downloadPoDocument}" data-placement="top" title='<spring:message code="tooltip.download" />' > <img src="${pageContext.request.contextPath}/resources/images/download.png">
										</a> 
									</form>
 								</td>
								<td class="width_200 width_200_fix align-left">${listDoc.fileName}</td>
								<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
								<td class="width_100 width_100_fix align-left wo-rp"><fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
								<td class="width_100 width_100_fix align-left wo-rp">${listDoc.fileSizeInKb}KB</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.delivery.detail" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="prsummary.receiver" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${po.deliveryReceiver}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="eventsummary.eventdetail.deliveryadds" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty po.deliveryAddressTitle}">
						<h5>${po.deliveryAddressTitle}</h5>
						<span class='desc'>${po.deliveryAddressLine1}, ${po.deliveryAddressLine2}, ${po.deliveryAddressCity}, ${po.deliveryAddressZip}, ${po.deliveryAddressState}, ${po.deliveryAddressCountry}</span>
					</c:if>
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box ">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="eventsummary.eventdetail.deliverydate" /> &amp; <spring:message code="application.time" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${po.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div>
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
    	<h4>Purchase Item</h4>
    	<div class="pad_all_15 float-left width-100">
    		<div class="main_table_wrapper ph_table_border mega">
    			<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
    				<thead>
    					<tr>
    						<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
    						<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
    						<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.unit" /></th>
    						<th class="align-right width_100 width_150_fix"><spring:message code="label.rftbq.th.quantity" /></th>
    						<th class="align-right width_100 width_150_fix"><spring:message code="label.rftbq.th.locked.quantity" /></th>
    						<th class="align-right width_100 width_150_fix"><spring:message code="label.rftbq.th.balance.quantity" /></th>
    						<th class=" align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.unitprice" /> (${po.currency})</th>
    						<th class=" align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.pricePerUnit" /> (${po.currency})</th>
    						<c:if test="${not empty po.field1Label}">
    							<th class="align-left width_100 width_100_fix">${po.field1Label}</th>
    						</c:if>
    						<c:if test="${not empty po.field2Label}">
    							<th class="align-left width_100 width_100_fix">${po.field2Label}</th>
    						</c:if>
    						<c:if test="${not empty po.field3Label}">
    							<th class="align-left width_100 width_100_fix">${po.field3Label}</th>
    						</c:if>
    						<c:if test="${not empty po.field4Label}">
    							<th class="align-left width_100 width_100_fix">${po.field4Label}</th>
    						</c:if>
    						<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" />  (${po.currency})</th>
    						<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" />  (${po.currency})</th>
    						<th class="width_250 width_250_fix align-right"><spring:message code="prtemplate.total.amount.tax" />  (${po.currency})</th>
    					</tr>
    				</thead>
    			</table>

    			<c:if test="${not empty poItemlist}">
                	<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
                		<tbody>
                			<c:forEach items="${poItemlist}" var="item">
                            	<tr>
                            		<td class="width_50 width_50_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.level}.${item.order}</td>
                            		<td class="align-left width_200_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.itemName}<c:if test="${not empty item.itemDescription}">
                            				<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" />
                            				</span>
                            			</c:if>
                            			<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
                            		</td>
                            		<td class="align-left width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${not empty item.product.uom.uom ? item.product.uom.uom : item.unit.uom}</td>

                            		<td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.quantity}" /></td>

                                    <c:choose>
                                        <c:when test="${not empty item.lockedQuantity}">
                                            <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                                ${item.itemIndicator == '003' ? '' : item.lockedQuantity}
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"></td>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:choose>
                                        <c:when test="${not empty item.balanceQuantity}">
                                            <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                                ${item.itemIndicator == '003' ? '' : item.balanceQuantity}
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"></td>
                                        </c:otherwise>
                                    </c:choose>

                                    <td class="align-right width_150 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                        ${item.itemIndicator == '003' ? '' : item.unitPrice}
                                    </td>

                                    <c:choose>
                                        <c:when test="${not empty item.pricePerUnit}">
                                            <td class="align-right width_150 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                                ${item.itemIndicator == '003' ? '' : item.pricePerUnit}
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td class="align-right width_150 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"></td>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:if test="${not empty po.field1Label}">
                                        <td class=" align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field1}&nbsp;</td>
                                    </c:if>
                                    <c:if test="${not empty po.field2Label}">
                                        <td class="align-left width_200 approvalUsers width_200_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field2}&nbsp;</td>
                                    </c:if>
                                    <c:if test="${not empty po.field3Label}">
                                        <td class="align-left width_200 width_200_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field3}&nbsp;</td>
                                    </c:if>
                                    <c:if test="${not empty po.field4Label}">
                                        <td class="align-left width_200 width_200_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field4}&nbsp;</td>
                                    </c:if>
                                    <td class="width_150 width_150_fix align-right ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
                                            <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmount}" />
                                        </c:if></td>
                                    <td class="width_150 align-right width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
                                            <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.taxAmount}" />
                                        </c:if></td>
                                    <td class="width_250 width_250_fix align-right ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
                                            <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmountWithTax}" />
                                        </c:if></td>
                            	</tr>
                            </c:forEach>
                		</tbody>
                	</table>
                </c:if>
    		</div>

    		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
    			<table cellspacing="3" cellpadding="3" style="width: 100%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
    				<tr>
    					<td>&nbsp;</td>
    					<td>&nbsp;</td>
    					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${po.currency}) :</strong></td>
    					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.total}" /></td>
    				</tr>
    				<tr>
    					<td style="white-space: nowrap; vertical-align: top;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
    					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${po.taxDescription}</td>
    					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${po.currency}):</strong></td>
    					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.additionalTax}" /></td>
    				</tr>
    				<tr>
    					<td>&nbsp;</td>
    					<td>&nbsp;</td>
    					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${po.currency}):</strong></td>
    					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.grandTotal}" /></td>
    				</tr>
    			</table>
    		</div>
    	</div>
    </div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info remark-tab">
		<c:if test="${not empty po.remarks}">
			<h4>
				<spring:message code="Product.remarks" />
			</h4>
			<div class="pad_all_15 float-left width-100">
				<label><spring:message code="prtemplate.general.remark" /></label>
				<p>${po.remarks}</p>
			</div>
		</c:if>
		<c:if test="${not empty po.termsAndConditions}">
			<div class="pad_all_15 float-left width-100">
				<label><spring:message code="prtemplate.terms.condition" /></label>
				<p>${po.termsAndConditions}</p>
			</div>
		</c:if>
	</div>

	<c:if test="${isShare}">
		<div class="upload_download_wrapper clearfix marg-top-10 event_info remark-tab">
			<h4>
				<spring:message code="supplier.finance.po" />
			</h4>
			<div class="pad_all_15 float-left width-100">
				<c:if test="${not  empty shareDate}">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label><spring:message code="supplier.po.shared" /> </label>
						<p>${financeCompanyName}</p>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<label><spring:message code="supplier.po.shared.on" /></label>
						<p>
							<fmt:formatDate value="${shareDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>
				</c:if>

			</div>
			<c:if test="${isRequest}">
				<div class="pad_all_15 float-left width-100">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label><spring:message code="supplier.po.requested" /></label>
						<p>${financeCompanyName}</p>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<label><spring:message code="supplier.po.requested.on" /></label>
						<p>
							<fmt:formatDate value="${requestDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>


				</div>
			</c:if>
		</div>
	</c:if>
	<jsp:include page="supplierPoAudit.jsp" />
</div>

<div class="modal fade" id="modal-FinanceCompany" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/supplier/sharePo" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-content" style="width: 50%; float: left; margin-left: 319px;">
				<div class="modal-header">
					<h3>
						<spring:message code="supplier.finance.share.po" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<div class="modal-body">
					<div class="auction-body">
						<input type="hidden" id="poId" name="poId" value="${po.id}"> <select name="financeCompanyId" class="chosen-select disablesearch" id="idSettingType">
							<c:forEach items="${financeCompanys}" var="financecompany">
								<option value="${financecompany.id}">${financecompany.companyName}</option>
							</c:forEach>
						</select>

					</div>
				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton">
						<spring:message code="supplier.pr.share" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="modal fade" id="modal-sharedPo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content" style="width: 50%; float: left; margin-left: 319px;">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.po.already.requested" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>

			<div class="modal-body">
				<h4 align="center">
					<spring:message code="supplier.po.already.requested" />
					: ${financeCompanyName}
				</h4>
			</div>
			<div class="modal-footer border-none float-left width-100 pad-top-0 ">
				<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="modal-poRequest" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/supplier/requestPo" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-content" style="width: 50%; float: left; margin-left: 319px;">
				<div class="modal-header">
					<h3>
						<spring:message code="supplier.po.confirm.request" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<div class="modal-body">
					<h4 align="left">
						<spring:message code="supplier.po.requesting.to" />
						: ${financeCompanyName}
					</h4>
					<select name="financeCompanyId" class="chosen-select disablesearch" id="idSettingType">
						<c:forEach items="${financeCompanys}" var="financecompany">
							<option value="${financecompany.id}">${financecompany.companyName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">

					<input type="hidden" id="poId" name="poId" value="${po.id}">
					<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton" type="submit">
						<spring:message code="supplier.po.request.po" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="modal fade" id="modal-finanshereRequest" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<form action="${pageContext.request.contextPath}/supplier/finanshereRequest" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-content" style="width: 50%; float: left; margin-left: 319px;">
				<div class="modal-header">
					<h3>
						<spring:message code="supplier.po.finanshere.confirm.request" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
				</div>

				<div class="modal-body">
					<h4 align="left">Are you sure you want to apply for financing from FinansHere?</h4>
					</select>
				</div>
				<div class="modal-footer border-none float-left width-100 pad-top-0 ">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 no-button" data-dismiss="modal">Cancel</button>
					<input type="hidden" id="poId" name="poId" value="${po.id}">
					<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20 auctionTypeButton" type="submit">
						<spring:message code="supplier.po.finanshere.request.po" />
					</button>
				</div>
			</div>
		</form>
	</div>
</div>

<!-- Accept Po -->
<div class="modal fade" id="modal-poAccept" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poAccept.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<%-- <form id="" action="${pageContext.request.contextPath}/supplier/acceptPo" method="post"> --%>
			<form id="acceptPoForm" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplier.po.accept.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Optional)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="acceptPoId" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Decline Po -->
<div class="modal fade" id="modal-poDeclined" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poDecline.confirm" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<%-- <form id="" action="${pageContext.request.contextPath}/supplier/declinePo" method="post"> --%>
			<form id="declinePoForm" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplier.po.decline.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="declinePoId" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Create Do popup  -->
<div class="modal fade" id="modal-createDo" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.createDO" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/createDo" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.createDo" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="createDo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Create Invoice popup  -->
<div class="modal fade" id="modal-createInvoice" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.createInvoice" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/createInvoice" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.createInvoice" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="createInvoice" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Create Do popup  -->
<div class="modal fade" id="modal-shipmpa" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.ship" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/createDo" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.shipitem" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idShipMpa">
						<spring:message code="application.yes2" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<style>
.width-90 {
	width: 90%;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

//
.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.grand-price-heading {
	width: 250px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}

.white-space-pre {
	white-space: pre;
}

.grey-background {
	background-color: #d3d3d3 !important; /* Light grey */
}
</style>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'file'
	});


	$('#poShare').click(function() {
		if (${isShare} == false)
			$('#modal-FinanceCompany').modal();
		
	});
	
	
	$('#poRequest').click(function() {
		if (${isShare} == true && ${isRequest} == true){
			$('#modal-sharedPo').modal();
		} else {
			$('#modal-poRequest').modal();
		}
	});

	$('#acceptPo').click(function() {
		$('#modal-poAccept').modal();
	});
	

	$('#declinedPo').click(function() {
		$('#modal-poDeclined').modal();
	});

	$('#doCreate').click(function(e) {
		e.preventDefault();
		$('#modal-createDo').modal();
	});

	$('#invoiceCreate').click(function() {
		$('#modal-createInvoice').modal();
	});
	
	$('#createInvoice').click(function(e){
		$(this).addClass('disabled');
		$('#idActionBtn').addClass('disabled');
		$('.no-button').addClass('disabled');
	});

	$('#createDo').click(function(e){
		$(this).addClass('disabled');
		$('#idActionBtn').addClass('disabled');
		$('.no-button').addClass('disabled');
	});
	
	$('#doMPA').click(function(e) {
		e.preventDefault();
		$('#modal-shipmpa').modal();
	});
	$('#idShipMpa').click(function(e) {
		window.open("https://myparcelasia.com/procurehere");
		$('#modal-shipmpa').modal('hide');
	});
</script>
<script type="text/javascript">
$('document').ready(function() {
	
		$('#acceptPoId').on('click', function(e) {
			e.preventDefault();
			if($("#acceptPoForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#acceptPo').addClass('disabled');
				$('#declinedPo').addClass('disabled');
	 			$('#acceptPoForm').attr('action', getContextPath() + '/supplier/acceptPo');
				$("#acceptPoForm").submit();
			}else{
				return;
			}
		});
		
		$('#declinePoId').on('click', function(e) {
			e.preventDefault();
			if($("#declinePoForm").isValid()) {
				$(this).addClass('disabled');
				$('#declinedPo').addClass('disabled');
	 			$('#acceptPo').addClass('disabled');
	 			$('#declinePoForm').attr('action', getContextPath() + '/supplier/declinePo');
				$("#declinePoForm").submit();
			}else{
				return;
			}
		});
});
		
</script>

<script type="text/javascript" src="<c:url value="/resources/js/view/prCreate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prSummary.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">