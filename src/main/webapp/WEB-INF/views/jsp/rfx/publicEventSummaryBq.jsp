<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading evnt-common-border">
		<h4 class="panel-title evnt-info" >
			<a data-toggle="collapse" class="accordion" href="#collapseEight"> Bill of Quantity </a>
		</h4>
	</div>
	<div id="collapseEight" class="panel-collapse collapse">
		<div class="panel-body pad_all_15">
			<c:forEach items="${bqList}" var="bq">
				<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
					<div>
						<h3>&nbsp ${bq.name}</h3>
					</div>
					<div class="import-supplier-inner-first-new global-list form-middle">
						<div class="ph_tabel_wrapper">
							<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
								<table class="ph_table border-none header" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
									<thead>
										<tr>
											<th class="for-left width_50 width_50_fix">No</th>
											<th class="for-left width_200 width_200_fix">Item Name</th>
											<th class="for-left width_100 width_100_fix">Quantity</th>
											<th class="for-left width_100 width_100_fix">UOM</th>
											
										</tr>
									</thead>
								</table>
								<table class="ph_table data border-none" width="100%" border="0" cellspacing="0" cellpadding="0" >
									<tbody>
										<c:forEach items="${bq.bqItems}" var="item">
											<tr>
												<td class="for-left width_50 width_50_fix">${item.level}<c:if test="${item.order != '0'}">.${item.order}</c:if>
												</td>
												<td class="for-left width_200 width_200_fix" align="center"><span class="item_name">
													<c:if test="${item.priceType == 'TRADE_IN_PRICE'}">
														<span class="bs-label label-info">Trade In Price</span>&nbsp;
												</c:if>
													<c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">
														<span class="bs-label label-success">Buyer Fixed Price</span>&nbsp;
												</c:if>
													${item.itemName}
													</span> <span data-toggle="collapse" class="s2_view_desc">${not empty item.itemDescription?'View Description':''}</span>
												</td>
												<td class="for-left width_100 width_100_fix" align="center">${item.quantity}</td>
												<td class="for-left width_100 width_100_fix" align="center">${item.uom.uom}</td>
											</tr>
											<tr   class="collapse">
											<td  style="  border-top: 0 !important;"></td>
											<td class="for-left width_200 width_200_fix" style=" border-top: 0 !important;"><span class="item_detail s2_text_small">${item.itemDescription}</span></td>
													
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<style>

.s2_text_small {
    margin: 5px 0 0 0;
    font-size: 11px;
    display: none;
    max-height: 150px;
    text-align: left;
    margin-top: -1%;
    }   
</style>
<script type="text/javascript" >
$(document).on('click', '.s2_view_desc', function(event) {
		event.preventDefault();
		$(this).closest('tr').next('tr').slideToggle();
		$(this).text(function(i, text) {
			return text === "View Description" ? "Close Description" : "View Description";
		})
	});
</script>