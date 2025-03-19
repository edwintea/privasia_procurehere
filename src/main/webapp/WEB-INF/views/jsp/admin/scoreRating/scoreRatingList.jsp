<%@page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>">
</script>
<sec:authorize access="hasRole('SCORE_RATING_EDIT') or hasRole('ADMIN')"
var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')"
var="buyerReadOnlyAdmin" />
<spring:message var="scoreRatingListDesk" code="application.score.rating.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${scoreRatingListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canEdit() {
		return "${canEdit}";
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
<div id="page-content" view-name="scoreRating">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard""> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="score.rating.list" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="score.rating.list" />
			</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block"> <header class="form_header"> <form:errors path="*" cssClass="error" /> <c:if test="${not empty errors}">
							<span class="error">${errors}</span>
						</c:if> </header> <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="row">
							<div class="col-xs-12">
								<c:url value="/admin/scoreRating/exportScoreRatingCsvReport" var="downloadCsvReport" />
								<form:form action="${downloadCsvReport}" method="post" id="exportScoreRatingForm" ModelAttribute="scoreRatingPojo">
									<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
										<div class="row">
											<div class="col-md-5 col-sm-8 col-lg-5"></div>
											<div class="col-md-2 col-sm-4 resetBtn-padding"></div>
											<div class="col-md-5">
												<button id="exportCsvReport" class="btn btn-sm btn-success hvr-pop marg-left-10 f-r h-37" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="score.rating.csv.export.button"/>'>
													<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
													</span> <span class="button-content"><spring:message code="score.rating.csv.export.button" /></span>
												</button>
											</div>
										</div>
									</div>
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="scoretableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type=""><spring:message code="application.action" /></th>
													<th search-type="text"><spring:message code="label.minscore" /></th>
													<th search-type="text"><spring:message code="label.maxscore" /></th>
													<th search-type="text"><spring:message code="label.rating" /></th>
													<th search-type="text"><spring:message code="application.description" /></th>
													<th search-type="text"><spring:message code="application.createdby" /></th>
													<th search-type=""><spring:message code="application.createddate" /></th>
													<th search-type="text"><spring:message code="application.modifiedby" /></th>
													<th search-type=""><spring:message code="application.modifieddate" /></th>
													<th search-type="select" search-options="statusList"><spring:message code="application.status" /></th>
												</tr>
											</thead>
										</table>
										<spring:url value="/admin/scoreRating/createScoreRating" var="createUrl" htmlEscape="true" />
										<a href="${canEdit ? createUrl : '#'}" class="btn btn-info top-marginAdminList ${canEdit ? '' : 'disableEve'} ${!buyerReadOnlyAdmin ? '':'disabled'}"> <spring:message code="score.rating.create" />
										</a>
									</div>
								</form:form>
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

<!-- Delete confirmation modal -->
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="score.rating.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/scoreRating/deleteScoreRating?id=" title=<spring:message code="tooltip.delete" />> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>



<input type="hidden" id="delId" value="" />
<!-- NEw add -->
<script type="text/javascript">
	$('document').ready(function() {

		var table = $('#scoretableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
				$('#select-all').prop('checked', false);
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/admin/scoreRating/scoreRatingListData",
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
			"columns" : [{
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"className":"action-class",
				"mRender" : function(data, type, row) {
					var tImg = "";
					if (canEdit() === "true") {
						tImg = '<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
					}
					return '<a href="editScoreRating?id=' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png" style="margin-right: 5px;"></a>  <a href="#myModal" onClick="javascript:updateLink(\'' + row.id + '\');"  title=<spring:message code="tooltip.delete" /> role="button" class="" data-toggle="modal">' + tImg + '</a>';
				}
			}, {
				"mData" : "minScore"
			},{
				"mData" : "maxScore"
			}, {
				"mData" : "rating"
			},{
				"mData" : "description",
				//"className":"desc-nowrap",
				"defaultContent" : ""
			}, {
				"mData" : "createdBy.loginId",
				"defaultContent" : ""
			}, {
				"mData" : "createdDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "modifiedBy.loginId",
				"defaultContent" : ""
			}, {
				"mData" : "modifiedDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "status",
				"defaultContent" : ""
			} ],
		
		"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#scoretableList thead tr:nth-child(1) th').each(function(i) {
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
					htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
				}
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#scoretableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 0 || this.value.length == 0) {
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
		link.attr("href", '${pageContext.request.contextPath}/admin/scoreRating/deleteScoreRating?id=');
	}
	
	$('.custom-checkAllbox').on('change', function() {
		var check = this.checked;
		$("[type=checkbox]").each(function() {
			$(".custom-checkbox1").prop('checked', check);
			$.uniform.update($(this));
		});
	});
	
</script>

<style>
#scoretableList td {
	text-align: center;
    border-bottom: 0px;
}

#exportCsvReport {
	margin-left: 63%;
	float: right;
}

.action-class {
   white-space: nowrap;
}
.desc-nowrap {
   white-space: nowrap;
}
</style>
