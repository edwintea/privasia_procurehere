<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('SUBSCRIPTION_DETAILS') or hasRole('ADMIN')" var="canEdit" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="promotionCodeListDesk" code="application.owner.promotion.code.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${promotionCodeListDesk}] });
});
</script>


<div id="page-content" view-name="promotionalCode">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">

			<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>
			<li class="active"><spring:message code="promotion.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="" style="color: white;">
				<img src="${pageContext.request.contextPath}/resources/images/promo1.png" height="30px" width="30px">
				<spring:message code="promotion.list" />

			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">
									<div class="form-group col-md-12 bordered-row">
										<div class="ph_tabel_wrapper scrolableTable_list">
											<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type="text"><spring:message code="application.action" /></th>
														<th search-type="text"><spring:message code="promotion.title" /></th>
														<th search-type="text"><spring:message code="promotion.promotionName" /></th>

														<th search-type="" class="width_200 width_200_fix"><spring:message code="promotion.promotionDiscount" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="promotion.promotionDiscountType" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="promo.code.usagelimit" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="promo.code.usageLimitType" /></th>
														<th search-type="text"><spring:message code="application.createdby" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="application.createddate" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="promotion.promotionEffective" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="promotion.promotionExpiry" /></th>

														<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
													</tr>
												</thead>
											</table>
											<spring:url value="/admin/promotionalCode" var="createUrl" htmlEscape="true" />

											<a href="${createUrl}" class="btn btn-info top-marginAdminList ${canEdit ? '':'disabled'}">
												<spring:message code="promotion.code.create" />

											</a>
										</div>
										<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
									</div>
								</div>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<input type="hidden" id="delId" value="" />
<script type="text/javascript">
	$('document').ready(function() {

		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,

			"pageLength" : 10,
			"serverSide" : true,

			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/admin/promotionalCodeData",
				"data" : function(d) {



				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error) {
						showMessage('ERROR', error);
					}
					$('#loading').hide();
				}
			},
			"order" : [],
			"columns" : [ {
				"data" : "id",
				"searchable" : false,
				"orderable" : false,
				"render" : function(data, type, row) {

					var ret = '<a href="editPromotionalCode?id=' + row.id + '"  data-placement="top" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
					ret += '<a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');" data-placement="top" title=<spring:message code="tooltip.delete" /> role="button" data-toggle="modal">';
					ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
					ret += '</a>';

					return ret;
				}
			}, {
				"data" : "promoCode"
			}, {
				"data" : "promoName",
				"defaultContent" : ""
			}, {
				"data" : "promoDiscount",
				"defaultContent" : ""
			}, {
				"data" : "discountType",

				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "usageLimit",
				"defaultContent" : ""
			}, {
				"data" : "usageLimitType",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "createdBy.loginId",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "effectiveDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "expiryDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "status",
				"defaultContent" : ""
			} ],
			
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				if ($(this).attr('search-type') == 'select') {
					var optionsType = $(this).attr('search-options');
					htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="filter.placeholoder" /> ' + title + '</option>';

					/* 	if (optionsType == 'discountList') {
							<c:forEach items="${discountList}" var="item">
							htmlSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						} */

					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" name="filter' + title.replace(' ', '') + '" placeholder="<spring:message code="application.search" /> ' + title + '" data-index="' + i + '" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			console.log(this.name);
			if ((this.name == 'filterDisplayOrder' && this.value.length >= 1) || (this.name == 'filterDisplayOrder' && this.value.length == 0)) {
				table.column($(this).data('index')).search(this.value).draw();
			} else if (this.value.length > 3 || this.value.length == 0) {
				table.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(table.table().container()).on('change', 'thead select', function() {
			table.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	});


	function updateLink(id) {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}

	function doCancel() {
		var link = $("a#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/admin/deletePromotionalCode?id=');
	}

</script>
<style>
#tableList td {
	text-align: center;
}

#tableList td:nth-child(3) {
	text-align: left;
}
</style>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">

				<label> <spring:message code="promotion.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/deletePromotionalCode?id=" title=<spring:message code="tooltip.delete" />>

					<spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
