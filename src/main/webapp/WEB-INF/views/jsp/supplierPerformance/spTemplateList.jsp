<%@ page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEditt" />
<spring:message var="prTemplateListDesk" code="application.pr.template.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${prTemplateListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canEditt() {
		return "${canEditt}";
	}
</script>
<div id="page-content" view-name="spTemplate">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a href='<c:url value="buyerDashboard"/>'> <spring:message code="application.dashboard" /></a>
			</li>
			<li class="active">
				<spring:message code="sptemplate.list" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="sptemplate.title" />
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
														<th search-type="" class="width_100_fix"><spring:message code="application.action" /></th>
														<th search-type="text"><spring:message code="rfxTemplate.templateName" /></th>
														<th search-type="text" class="width_300_fix"><spring:message code="rfxTemplate.templateDescription" /></th>
														<th search-type="text" class=""><spring:message code="application.createdby" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="application.createddate" /></th>
														<th search-type="text" class="width_200 width_200_fix"><spring:message code="application.modifiedby" /></th>
														<th search-type="" class="width_200 width_200_fix"><spring:message code="application.modifieddate" /></th>
														<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
													</tr>
												</thead>
											</table>
											<spring:url value="/buyer/createPerformanceTemplate" var="createUrl" htmlEscape="true" />
											<a href="${canEditt ? createUrl : '#'}" class="btn btn-info top-marginAdminList  ${canEditt and !buyerReadOnlyAdmin ? '' : 'disabled' }"> <spring:message code="supplier.template.create" />
											</a>
										</div>
										<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
									</div>
								</div>
							</div>	
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
th.width_300_fix, td.width_300_fix {
	width: 400px !important;
	-ms-word-break: break-all;
	word-break: break-all;
	word-break: break-word;
	-webkit-hyphens: auto;
	-moz-hyphens: auto;
	-ms-hyphens: auto;
	hyphens: auto;
}
</style>
<input type="hidden" id="delId" value="" />
<script type="text/javascript">
	$('document').ready(function() {

		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				setTimeout(function() { 
					$('div[id=idGlobalError]').hide();//PH-655
				}, 20000);
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				// in case your overlay needs to be put away automatically you can put it here
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/spTemplateListData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
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
					var tImg = "";
					if (canEditt() === "true") {
						tImg = '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
					}
					return '<a href="editSPTemplate?id=' + row.id + '"  title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id
							+ '\');" title=<spring:message code="tooltip.delete" /> role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">' + tImg + '</a>';
				}
			}, {
				"data" : "templateName",
				"defaultContent" : ""
			}, {
				"data" : "templateDescription",
				"defaultContent" : "",
				"class" : 'width_300_fix templateDescription'
			}, {
				"data" : "createdBy.loginId",
				"defaultContent" : ""
			}, {
				"data" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "modifiedBy.loginId",
				"defaultContent" : ""
			}, {
				"data" : "modifiedDate",
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
					htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>'; 
					if (optionsType == 'statusList') {
						<c:forEach items="${statusList}" var="item">
						htmlSearch += '<option value="${item}">${item}</option>';
						</c:forEach>
					}
					htmlSearch += '</select></th>';
				} else {
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
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
		var link = $("#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}
	function doCancel() {
		var link = $("#idConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteSPTemplate?id=');
	}
</script>
<style>
.disableEve:hover {
	cursor: default;
}

.disableEve {
	background: #0095d5 none repeat scroll 0 0;
	border-color: #0095d5;
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
				<label> <spring:message code="rfxTemplate.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/deleteSPTemplate?id=" title=<spring:message code="tooltip.delete" />> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
