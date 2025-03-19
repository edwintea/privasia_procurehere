<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_PURCHASE_REQUISITION')" var="buyerPR" />
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<spring:message code="eventsummary.checkbox.editor" var="editorLabel" />
<spring:message code="eventsummary.checkbox.viewer" var="viewerLabel" />
<spring:message code="eventsummary.checkbox.associate.owner" var="associateOwnerLabel" />
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
.bg-lbl{
	margin-bottom: 10px;
    background: red;    
    color: #fff !important;    
    display: inline-block;
    border-radius: 5px;
    padding-left: 10px !important;
    padding-right: 10px !important;
    margin-left: 10px !important;
}
.alert-lbl {
	color: red;
	padding: 7px 5px;
	font-size: 14px;
	font-weight: bold;
}
</style>
<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<input type="hidden" id="prId" value="${pr.id}">
			<div class="tag-line">
				<h2>
					<spring:message code="prSummary.pr.number" />
					: ${pr.prId}
				</h2>
				<br>
				<h2>
					<spring:message code="pr.creator" />
					: ${pr.createdBy.name}/${pr.createdBy.communicationEmail}
				</h2>
				<br>
				<h2>
					<spring:message code="pr.requester" />
					: ${pr.requester}
				</h2>
				<br>
				<c:if test="${pr.isPo}">
					<h2>
					<spring:message code="pr.po.requester" /> :
					<c:if test="${po != null}">
						<c:choose>
							<c:when test="${po.status ne 'DRAFT' &&  po.status ne 'SUSPENDED' }">
								<a href="${pageContext.request.contextPath}/buyer/poView/${poId}?prId=${pr.id}">${pr.poNumber}</a>
							</c:when>
							<c:otherwise>
								<a href="${pageContext.request.contextPath}/buyer/poCreate/${poId}?prId=${pr.id}">${pr.poNumber}</a>
							</c:otherwise>
						</c:choose>
					</c:if>
					</h2>
				</c:if>
			</div>
		</div>
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="pull-right tag-line-right">
				<h2>
					<spring:message code="prtemplate.template.name" />
					: ${prTemplate.templateName != null ? prTemplate.templateName : 'N/A'}
				</h2>
			</div>
		</div>
	</div>

	<div class="pull-right">
		<form:form action="${pageContext.request.contextPath}/buyer/downlaodPrSummary/${pr.id}" method="GET">
			<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.prsummary.download.summary" />'>
				<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
				</span> <span class="button-content"><spring:message code="prsummary.summary.label" /></span>
			</button>
		</form:form>
	</div>
	<c:if test="${(pr.status eq 'APPROVED' or pr.status eq 'TRANSFERRED') and (!isAutoCreatePo and !pr.isPo ) }">
		<div class="pull-right">
			<%-- <form:form action="${pageContext.request.contextPath}/buyer/createPo" method="POST"> --%>
			<form:form id="createPoForm" method="post">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="prId" value="${pr.id}">
				<button class="btn btn-sm float-right btn-success hvr-pop marg-left-10" type="button" id="createPoId">
					<span class="button-content"><spring:message code="prsummary.create.po.button" /></span>
				</button>
			</form:form>
		</div>
	</c:if>
</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="prsummary.general.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.reference.number" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.referenceNumber}</p>
			</div>
		</div>
		<c:if test="${erpSetup.isErpEnable}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="prsummary.erp.doc.no" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${pr.erpDocNo}</p>
				</div>
			</div>
		</c:if>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="application.date" /> &amp; <spring:message code="application.time" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${pr.prCreatedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${pr.createdBy.name} <br> ${pr.createdBy.communicationEmail} <br>
					<c:if test="${not empty pr.buyer.companyContactNumber}">
						<spring:message code="prdraft.tel" />: ${pr.buyer.companyContactNumber}</c:if>
					<c:if test="${not empty pr.buyer.faxNumber}">
						<spring:message code="prtemplate.fax" />: ${pr.buyer.faxNumber}</c:if>
					<c:if test="${not empty pr.createdBy.phoneNumber}">
						<spring:message code="prtemplate.hp" />: ${ pr.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.requester" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.requester}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.description" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.description}</p>
			</div>
		</div>
		<!-- <div class="form-tander1 requisition-summary-box"> -->
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="rfs.summary.finance.information" />
		</h4>
		<div class="form-tander requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prtemplate.lock.budget" /> </label>
			</div>
			<div>
				<div class="col-sm-1 col-md-1 col-xs-1">
					<c:if test="${pr.lockBudget eq true }">
						<label class="line-height35"><img class="" style="margin-bottom: 7px;" height="20" width="20" title="Locked" src="${pageContext.request.contextPath}/resources/images/lock.png"> <spring:message code="application.yes2" /></label>
					</c:if>
					<c:if test="${pr.lockBudget eq false }">
						<label class="line-height35"><img class="" style="margin-bottom: 7px;" height="20" width="20" title="Locked" src="${pageContext.request.contextPath}/resources/images/lock.png"> <spring:message code="application.no2" /></label>
					</c:if>
				</div>
				<c:if test="${not empty pr.remainingBudgetAmount and pr.lockBudget==true}">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<fmt:formatNumber var="remainingAmt" type="number" minFractionDigits="2" maxFractionDigits="6" value="${pr.remainingBudgetAmount}" />
						<p class="alert-lbl bg-lbl" id="remainAmt">Remaining Amount : ${remainingAmt} ${pr.budgetCurrencyCode}</p>
					</div>
				</c:if>		
			</div>	
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.currency.currencyCode}</p>
			</div>
		</div>
		<c:if test="${pr.conversionRate != null}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="label.budget.conversionRate" /> :
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${pr.conversionRate}</p>
				</div>
			</div>
		</c:if>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.costcenter.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.costCenter.costCenter}-${pr.costCenter.description}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.businessUnit" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<c:if test="${empty pr.businessUnit}">
						<spring:message code="application.not.applicable" />
					</c:if>${pr.businessUnit.unitName}</p>
			</div>
		</div>
		<c:if test="${!empty templateFieldsVisibilty ? (tf:prVisibility(templateFieldsVisibilty, 'AVAILABLE_BUDGET')) : true}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="label.availableBudget" /> :
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${pr.availableBudget}</p>
				</div>
			</div>
		</c:if>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.paymentterm.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pr.paymentTerm}</p>
			</div>
		</div>
		<c:if test="${not empty pr.paymentTermDays}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> Payment Days :</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${pr.paymentTermDays}</p>
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
					<c:if test="${not empty pr.correspondenceAddress}">
						${pr.correspondenceAddress.title} <br> ${pr.correspondenceAddress.line1},
						<c:if test="${not empty pr.correspondenceAddress.line2}">${pr.correspondenceAddress.line2},</c:if> ${ pr.correspondenceAddress.city}, ${ pr.correspondenceAddress.zip},
						<br>${ pr.correspondenceAddress.state.stateName},${ pr.correspondenceAddress.state.country.countryName}
					</c:if>
				</p>
			</div>
		</div>
	</div>
	<div class="panel sum-accord upload_download_wrapper clearfix marg-top-10 event_info">
		<div class="panel-heading">
			<h4 class="panel-title list-pad-0" style="margin-bottom: 0px;">
				<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseThree"><spring:message code="prsummary.pr.doucment" /> </a>
			</h4>
		</div>
		<div id="collapseThree" class="panel-collapse collapse in">
			<div class="panel-body mega">
				<table class="tabaccor padding-none-td header" width="100%" cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.name" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.datetime" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.size" /></th>
						</tr>
					</thead>
				</table>
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="deta pad-for-table">
					<tbody>
						<c:forEach var="listDoc" items="${listDocs}">
							<tr>
								<td class="width_200 width_200_fix align-left wo-rp"><a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadPrSummaryDocument/${listDoc.id}"> ${listDoc.fileName}</a></td>
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
			<spring:message code="application.supplier.detail" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prsummary.supplier.info" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty pr.supplier}">
					<div class="">
						<%-- <h5>${pr.supplier.supplier.companyName}</h5> --%>
						<span class='desc'>${pr.supplier.supplier.companyName}<br />${pr.supplier.fullName}<br />${pr.supplier.communicationEmail}<br />${pr.supplier.companyContactNumber}</span>
					</div>
				</c:if>
				<c:if test="${empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplierName}<br />${pr.supplierTelNumber}<br />${pr.supplierFaxNumber}<br />${pr.supplierTaxNumber}</span>
					</div>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prevent.supplier.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplier.supplier.line1}<br />${pr.supplier.supplier.line2}<br />${pr.supplier.supplier.city}</span>
					</div>
				</c:if>
				<c:if test="${empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplierAddress}</span>
					</div>
				</c:if>
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
				<p class="set-line-height">${pr.deliveryReceiver}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="eventsummary.eventdetail.deliveryadds" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty pr.deliveryAddress}">
						<h5>${pr.deliveryAddress.title}</h5>
						<span class='desc'>${pr.deliveryAddress.line1}, ${pr.deliveryAddress.line2}, ${pr.deliveryAddress.city}, ${pr.deliveryAddress.zip}, ${pr.deliveryAddress.state.stateName}, ${pr.deliveryAddress.state.country.countryName}</span>
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
					<fmt:formatDate value="${pr.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="prevent.purchase.item" />
		</h4>
		<%-- <div class="grand-price-heading">
			<label>Grand Price :</label>
			<p>
				(${pr.currency})
				<fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.grandTotal}" />
			</p>
		</div> --%>
		<c:if test="${pr.isPo}">
			<div class="col-md-2 pull-right">
				<div class="marg-top-10 marg-bottom-10">
					<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate">
						<i class="excel_icon"></i>
						<spring:message code="prsummary.export.excel.button" />
					</button>
				</div>
			</div>
		</c:if>
		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity.only" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${pr.currency})</th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.priceperunit" /> </th>

							<c:if test="${not empty pr.field1Label}">
								<th class="align-left width_100 width_100_fix">${pr.field1Label}</th>
							</c:if>
							<c:if test="${not empty pr.field2Label}">
								<th class="align-left width_100 width_100_fix">${pr.field2Label}</th>
							</c:if>
							<c:if test="${not empty pr.field3Label}">
								<th class="align-left width_100 width_100_fix">${pr.field3Label}</th>
							</c:if>
							<c:if test="${not empty pr.field4Label}">
								<th class="align-left width_100 width_100_fix">${pr.field4Label}</th>
							</c:if>
							<c:if test="${not empty pr.field5Label}">
								<th class="align-left width_100 width_100_fix">${pr.field5Label}</th>
							</c:if>
							<c:if test="${not empty pr.field6Label}">
								<th class="align-left width_100 width_100_fix">${pr.field6Label}</th>
							</c:if>
							<c:if test="${not empty pr.field7Label}">
								<th class="align-left width_100 width_100_fix">${pr.field7Label}</th>
							</c:if>
							<c:if test="${not empty pr.field8Label}">
								<th class="align-left width_100 width_100_fix">${pr.field8Label}</th>
							</c:if>
							<c:if test="${not empty pr.field9Label}">
								<th class="align-left width_100 width_100_fix">${pr.field9Label}</th>
							</c:if>
							<c:if test="${not empty pr.field10Label}">
								<th class="align-left width_100 width_100_fix">${pr.field10Label}</th>
							</c:if>

							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (${pr.currency})</th>
							<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" /> (${pr.currency})</th>
							<th class="width_200 width_200_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> (${pr.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${prItemlist}" var="item">
							<tr>
								<td class="width_50 width_50_fix">${item.level}.${item.order}</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${not empty item.unit ? item.unit.uom : item.product.uom.uom }</td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.quantity}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.unitPrice}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.pricePerUnit}" /></td>

								<c:if test="${not empty pr.field1Label}">
									<td class=" align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field2Label}">
									<td class="align-left width_100 width_100_fix">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field3Label}">
									<td class="align-left width_100 width_100_fix">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field4Label}">
									<td class="align-left width_100 width_100_fix">${item.field4}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field5Label}">
									<td class=" align-left width_100 width_100_fix">${item.field5}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field6Label}">
									<td class="align-left width_100 width_100_fix">${item.field6}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field7Label}">
									<td class="align-left width_100 width_100_fix">${item.field7}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field8Label}">
									<td class="align-left width_100 width_100_fix">${item.field8}&nbsp;</td>
								</c:if>

								<c:if test="${not empty pr.field9Label}">
									<td class=" align-left width_100 width_100_fix">${item.field9}&nbsp;</td>
								</c:if>
								<c:if test="${not empty pr.field10Label}">
									<td class="align-left width_100 width_100_fix">${item.field10}&nbsp;</td>
								</c:if>

								<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.totalAmount}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${item.totalAmountWithTax}" />
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="3" cellpadding="3" style="width: 98.8%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${pr.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${pr.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${pr.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.additionalTax}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${pr.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${pr.decimal}" maxFractionDigits="${pr.decimal}" value="${pr.grandTotal}" /></td>
				</tr>
			</table>
		</div>

	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info remark-tab">
		<h4>
			<spring:message code="Product.remarks" />
		</h4>
		<div class="pad_all_15 float-left width-100">
			<label><spring:message code="prtemplate.general.remark" /></label>
			<p>${pr.remarks}</p>
		</div>
		<div class="pad_all_15 float-left width-100">
			<label><spring:message code="prtemplate.terms.condition" /></label>
			<p>${pr.termsAndConditions}</p>
		</div>
	</div>

	<div class="panel sum-accord marg-top-5">
		<div class="panel-heading">
			<h4 class="panel-title pad0">
				<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseTeam"> <spring:message code="prsummary.pr.team.member" />
				</a>
				<c:if test="${eventPermissions.owner  and !buyerReadOnlyAdmin}">
                    <button class="editTeamMemberList sixbtn" title='<spring:message code="tooltip.edit" />'>
                        <img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" />
                    </button>
				</c:if>
			</h4>
		</div>
		<div id="collapseTeam" class="panel-collapse collapse accortab in">
			<div class="panel-body">
				<div class="clearfix"></div>
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="main_table_wrapper">
						<table id="prTeamMembersList" class="display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
							<thead>
								<tr class="tableHeaderWithongoing">
									<th class="for-left width_150 width_150_fix"><spring:message code="application.username" /></th>
									<th class="for-left width_150 width_150_fix"><spring:message code="buyer.profilesetup.loginmail" /></th>
									<th class="for-left width_150 width_150_fix"><spring:message code="application.access" /></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${not empty pr.prApprovals}">
		<div class="upload_download_wrapper clearfix marg-top-10 event_info Approval-tab">
			<h4>
				<spring:message code="rfi.createrfi.approvalroute.label" />
				<c:if test="${eventPermissions.owner and pr.status == 'PENDING' and !buyerReadOnlyAdmin}">
					<a class="marg-left-20 editApprovalPopupButton" title=<spring:message code="tooltip.edit" />> <img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
					</a>
				</c:if>
			</h4>
			<c:if test="${pr.enableApprovalReminder}">
			<div class="pad_all_15 float-left appr-div position-relative">
				<div>
				  <div class="row">
					<label style="margin-left: 17px;">Reminder Settings: </label>
				  </div>
				  <div class="marg-left-10">
					 <p>Reminder emails sent every ${pr.reminderAfterHour} hours.</p>
					 <p>Maximum ${pr.reminderCount} reminder emails.</p>
					 <c:if test="${pr.notifyEventOwner}">
					 <p><spring:message code="pr.notification.owner.message" /></p>
					 </c:if>
				  </div>
				</div>
			</div>
			</c:if>
			<c:forEach items="${pr.prApprovals}" var="approval">
				<div class="pad_all_15 float-left appr-div position-relative">
					<label>Level ${approval.level}</label>
					<c:if test="${approval.active}">
						<div class="color-green marg-left-10">
							&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
						</div>
					</c:if>
					<div class="Approval-lavel1-upper">
						<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
							<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
								<c:if test="${!user.user.active}">
										[<span class="inactiveCaption">INACTIVE</span>]
									</c:if>
							</div>
							<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
								<span class="or-seg">${approval.approvalType}</span>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
			<div class="clear"></div>
			<div class="remark-tab pad0">
				<h4>
					<spring:message code="summarydetails.approval.comments" />
				</h4>
				<div class="pad_all_15 float-left width-100">
					<c:forEach items="${prCommentList}" var="comment">
						<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
							<h3>${comment.createdBy.name}
								<span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
							<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
						</div>
					</c:forEach>
				</div>
				<c:if test="${pr.status eq 'PENDING' and eventPermissions.activeApproval}">
					<div class="form_field">
						<form id="approvedRejectForm" method="post">
							<div class="row">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="prId" id="prId" value="${pr.id}">


								<div class="col-sm-12 col-md-12 align-right col-xs-12 ">
									<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-12 ">
									<textarea name="remarks" id="approvalremarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
								</div>

							</div>
							<div class="row">
								<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
									<button type="button" id="approvedButton" class="btn btn-info ph_btn hvr-pop marg-left-10 hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
										<spring:message code="buyer.dashboard.approve" />
									</button>
									<button type="button" id="rejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-10 hvr-rectangle-out1  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
										<spring:message code="application.reject" />
									</button>
								</div>
							</div>
						</form>
					</div>
				</c:if>
			</div>
		</div>
	</c:if>

 	<jsp:include page="prAudit.jsp" />
</div>
<c:if test="${pr.status eq 'DRAFT'}">
	<div class="row">
		<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
			<c:url var="prRemark" value="/buyer/prRemark/${pr.id}" />
			<c:if test="${empty viewMode}">
				<a href="${prRemark}" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" id="previousButton"><spring:message code="application.previous" /></a>
			</c:if>
			<c:if test="${eventPermissions.owner}">
				<c:url var="prFinish" value="/buyer/prFinish/${pr.id}" />
				<a href="${prFinish}" id="prFinish" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }"><spring:message code="application.finish" /></a>
			</c:if>
			<c:if test="${(isAdmin or eventPermissions.owner)}">
				<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-left-10 ph_btn hvr-pop right-header-button  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="prtemplate.cancel.pr" /></a>
			</c:if>
		</div>
	</div>
</c:if>

<!-- check condition for erp integartion enable -->
<c:if test="${erpSetup.isErpEnable and pr.status eq 'APPROVED' and pr.isFinalApproved and (empty pr.erpPrTransferred or pr.erpPrTransferred == false) }">
	<a href="${pageContext.request.contextPath}/buyer/transferPoToErp/${pr.id}" role="button" class="btn btn-success marg-bottom-10 marg-left-10 ph_btn_custom hvr-pop  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"> <span class="glyph-icon icon-separator"> <i class="glyphicon glyphicon-transfer"></i>
	</span> <span class="button-content">Transfer PO</span>
	</a>
</c:if>

<!-- Edit Approval Dialog -->
<div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/updatePrApproval" var="updatePrApproval" />
		<form:form id="prSummaryForm" action="${updatePrApproval}" method="post" modelAttribute="pr">
			<form:hidden path="id" />
			<jsp:include page="prApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<div class="flagvisibility dialogBox" id="teamMemberListPopup" title="PR Team Members">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="form_field">
			<div class="form-group ">
				<div class="pull-left width100">
					<h3 class="modal-title text-center">
						<spring:message code="pr.event.team.member" />
					</h3>
					<hr />
					<div class="col-md-12 marginBottomA">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B">
									<select id="TeamMemberList" class="user-list-normal chosen-select" name="approverList1" selected-id="approver-id" cssClass="form-control user-list-normal chosen-select">
										<option value=""><spring:message code="prsummary.select.team.member" /></option>
										<c:forEach items="${userTeamMemberList}" var="TeamMember">
											<c:if test="${TeamMember.id == '-1' }">
												<option value="-1" disabled="true">${TeamMember.name}</option>
											</c:if>
											<c:if test="${TeamMember.id != '-1' }">
												<option value="${TeamMember.id}">${TeamMember.name}</option>
											</c:if>
										</c:forEach>
									</select>
									<div class="input-group-btn">


										<button class="btn btn-primary addTeamMemberToList" type="button">+</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12 margeinAllMDZero">
						<div class="mem-tab">
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
								<c:forEach items="${pr.prTeamMembers}" var="teamMembers">
									<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
										<td class="width_50_fix ">
											<!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
										</td>
										<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
										</td>

										<td>
											<div class="cqa_del">
												<a href="javascript:void(0);" list-type="Team Member" class="removeApproversList"><spring:message code="tooltip.delete" /></a>
											</div>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
					<div class="col-md-12 d-flex margeinAllMDZero">
						<a class="center-btn closeDialog btn btn-black pull-right marg-left-10 marg_top_20 hvr-pop ph_btn_small hvr-rectangle-out1" href="javascript:void(0);" onclick="window.location.reload();"> <spring:message code="eventReport.cls" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width800 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListType" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />
				?
			</p>
		</div>
		<div class="event_form">
			<input id="id" type="hidden" value="${pr.id}">
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title="Delete"><spring:message code="tooltip.delete" /></a>
					<button type="button" class="closeDialog-btn btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!-- cancel pr popup  -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="prtemplate.sure.want.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation" />
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<style>
.d-flex {
	display: flex;
}

.center-btn {
	text-align: center;
	margin: 0 auto;
	margin-top: 25px !important;
}

.width-90 {
	width: 90%;
}

.editTeamMemberList {
	margin-left: 50px;
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

#appUsersList td {
	padding: 5px;
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

.dropdown-menu .checkbox input[type="checkbox"] {
	position: relative !important;
	margin-left: 0 !important;
}

.open>.dropdown-menu {
	padding-bottom: 17px;
	padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #CCC
}

#eventTeamMembersList td, #eventTeamMembersList th {
	text-align: left !important;
	max-width: 100px !important;
}

#eventTeamMembersList {
	margin: 0 !important;
}

#eventTeamMembersList_length, #eventTeamMembersList_info,
	#eventTeamMembersList_paginate {
	display: none;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.editTeamMemberList {
	margin-left: 50px;
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
</style>
<script>
	$(document).delegate('.editTeamMemberList', 'click', function(e) {

		$("#teamMemberListPopup").dialog({
			modal : true,
			minWidth : 300,
			width : '50%',
			maxWidth : 600,
			minHeight : 200,
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").addClass("hide");
			},

			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$(".dialogBlockLoaded").css({
			"position" : "absolute",
			"top" : $(this).offset().top - 300
		});
	});

	$('.closeDialog-btn').click(function() {
		$('#removeApproverListPopup').dialog('close');
	});
</script>
<script type="text/javascript">
	$('document').ready(function() {

		$('#prFinish').click(function() {
			$(this).addClass('disabled');
		});

		$('#approvedButton').click(function() {
			$(this).addClass('disabled');
		});

		$('#rejectedButton').click(function() {
			$(this).addClass('disabled');
		});

		$('#cancelPr').click(function() {
			$(this).addClass('disabled');
		});

		$('.childBlocks').css('margin-top', $('.parentBlocks').height());
		var eventTemMembersTable = $('#prTeamMembersList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"paging" : false,
			"info" : false,
			"ordering" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/buyer/prTeamMembersList/${pr.id}',
			},
			"columns" : [ {
				"data" : "user.name",
				"className" : "align-left",
				"defaultContent" : "Demo Test"
			}, {
				"data" : "user.loginId",
				"className" : "align-left",
				"defaultContent" : "Demo 2"
			}, {
				"data" : "teamMemberType",
				"className" : "align-left",
				"defaultContent" : "",
				"render": function(data, type, row) {
					// Check if the teamMemberType is "Viewer" or "Editor" and change to "Associate Owner"
					if (data === 'Viewer' || data === 'Editor') {
						return 'Associate Owner';
					}
					// If it's already "Associate Owner," just return it
					return data === 'Associate_Owner' ? 'Associate Owner' : data;
				}
			} ]
		});

		$('#deliverPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/deliverPo");
				$('#poStatusForm').submit();
			}
		})

		$('#transferPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/transferPo");
				$('#poStatusForm').submit();
			}
		})

		$('#cancelPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/cancelPo");
				$('#poStatusForm').submit();
			}
		})
		
		$('.editApprovalPopupButton').click(function(e){
			e.preventDefault();
			$("#idReminderSettings").hide();
		})

		$('#createPoId').on('click', function(e) {
			e.preventDefault();
			if($("#createPoForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#createPoForm').attr('action', getContextPath() + '/buyer/createPo');
				$("#createPoForm").submit();
			}else{
				return;
			}
		});
		
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton,#approvedButton,#rejectedButton,#idSumDownload,.s1_view_desc,.bluelink,#approvalremarks, .downloadPoBtn, #downloadTemplate';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
</script>
<script>
	<c:if test="${buyerPR and !isAdmin}">
	$(window).bind('load', function() {
		$('#createPoId').addClass('disabled');
	});
	</c:if>

	$.validate({
		lang: 'en',
		modules: 'file'
	});
</script>


<script type="text/javascript" src="<c:url value="/resources/js/view/prCreate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prSummary.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
