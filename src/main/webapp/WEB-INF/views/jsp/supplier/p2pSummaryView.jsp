<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
 		<div class="col_12">
		<div class="white_box_brd pad_all_15">
			<section class="index_table_block">
				<div class="row">
					<div class="col-xs-12">
							<table   class="display table table-bordered noarrow" cellspacing="0" width="100%">
								<thead>
									<tr class="tableHeaderWithSearch">
										<th class="align-left width_200_fix">DO Number</th>
										<th class="align-left width_200_fix">Do Status</th>
										<th class="align-left width_150_fix">GR Number</th>
										<th class="align-left width_150_fix">GR Status</th>
										<th class="align-left width_150_fix">Invoice Number</th>
										<th class="align-right width_150_fix">Invoice Status</th>
										<th class="align-left width_150_fix">Payment Number</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${grnList}" var="goodsReceiptNote">
										<tr id="${goodsReceiptNote.id}">
											<td>
												<a href="${pageContext.request.contextPath}/buyer/grnView/${goodsReceiptNote.id}" role="button" data-toggle="modal">${goodsReceiptNote.grnId}</a>
											</td>
											<td>
												${goodsReceiptNote.grnTitle}
											</td>
											<td>
												${goodsReceiptNote.createdBy}
											</td>
											<td>
												<fmt:formatDate value="${goodsReceiptNote.grnReceivedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</td>
											<td>
												<fmt:formatDate value="${goodsReceiptNote.goodsReceiptDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											</td>

											<td class="align-right width_100_fix">
												<fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${goodsReceiptNote.grandTotal}" />
											</td>
											<td>
												${goodsReceiptNote.status}
											</td>

										</tr>
									</c:forEach>
								</tbody>
							</table>
 					</div>
				</div>
			</section>
		</div>
	</div>
</div>