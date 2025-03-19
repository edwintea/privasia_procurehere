$(function() {
	currentPrBlocks();
	$('.nav-tabs a.createEventsTopTabs').on('hidden.bs.tab', function(e) {
		currentPrBlocks();
	});
	$(window).resize(function() {
		currentPrBlocks();
	});
	$('.searchrftEventt')
			.click(
					function(event) {
						event.preventDefault();
						$("#idGlobalInfo").hide();
						$("#idGlobalError").hide();
						$("#idGlobalWarn").hide();
						$("#idEventInfo").hide();

						// var referenceNumber = $('#idRefNumber').val();
						var searchValue = $('#searchValue').val();
						var header = $("meta[name='_csrf_header']").attr(
								"content");
						var token = $("meta[name='_csrf']").attr("content");
						$
								.ajax({
									type : "POST",
									url : getContextPath() + "/buyer/searchPr",
									data : {
										searchValue : searchValue,
									},
									dataType : "json",
									beforeSend : function(xhr) {
										$('#loading').show();
										xhr.setRequestHeader(header, token);
									},
									complete : function() {
										$('#loading').hide();
									},
									success : function(data) {
										var html = '';
										var found = false;
										$
												.each(
														data,
														function(key, value) { 
															found = true;
															html += '<div class="col-md-3 marg-bottom-10 idRftEvent" id="'
																	+ value.id
																	+ '" data-value="'
																	+ value.id
																	+ '" style="display: block">';
															html += '<div class="lower-bar-search-contant-main-block min-height-300" id="test">';
															html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
															html += '<h4>'
																	+ (value.name ? value.name
																			: '')
																	+ '</h4></div>';
															html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
															html += '<label>Reference Number :</label> <span class="green">'
																	+ (value.referenceNumber ? value.referenceNumber
																			: '')
																	+ '</span> </div>';
															html += '<div class="lower-bar-search-contant-main-contant pad-top-side-10">';
															html += '<label>Created By : </label> <span class="green">'
																	+ (value.createdBy ? value.createdBy.name
																			: '')
																	+ '</span></div>';
															html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
															html += '<label>Created Date :</label> <span class="green">'
																	+ (value.prCreatedDate ? value.prCreatedDate
																			: '')
																	+ '</span></div>';
															html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
															html += '<form action="'
																	+ getContextPath()
																	+ '/buyer/copyFromPr" class="col-md-12 hover_tooltip-top" method="post" style="float: right;">';
															html += '<input type="hidden" id="prId" value="'
																	+ value.id
																	+ '" name="prId">';
															html += '<input type="hidden" id="_csrf" value="'
																	+ token
																	+ '" name="_csrf">';
															//html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
															
															
															if (value.templateActive) {
																html += '<button disabled class="btn btn-black btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This '
																		
																		+ '</button>';
																html += '<span class="tooltiptext-top"> Not able to copy due to template is inactive </span>'
															} else {
																html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This '
																		
																		+ '</button>';
																html += '<span class="tooltiptext-top"> Create New PR </span>'
															}

															
															html += '</form>'
																	+ '</div></div></div></div>';
														});
										$('#rftEvents').show();
										$('#rftEvents > .row').html(html);
										currentPrBlocks();
										if (!found) {
											$("#idEventInfoMessage").html(
													'No matching data found');
											$("#idEventInfo").show();
											$('#rftEvents').hide();
										}
									},
									error : function(request, textStatus,
											errorThrown) {
										if (request.getResponseHeader('error')) {
											$("#idGlobalErrorMessage").html(
													request.getResponseHeader(
															'error').replace(
															",", "<br/>")
															.replace(",",
																	"<br/>")
															.replace(",",
																	"<br/>"));
											$("#idGlobalError").show();
										}
										if (request.getResponseHeader('info')) {
											$("#idGlobalInfoMessage").html(
													request.getResponseHeader(
															'info').replace(
															",", "<br/>")
															.replace(",",
																	"<br/>")
															.replace(",",
																	"<br/>"));
											$("#idGlobalInfo").show();
										}
									}
								});
					});

});

$(document).delegate('.quickview ', 'click', function(e) {

	diag_id = $(this).data("qv");

	$("#" + diag_id).dialog({
		modal : true,
		minWidth : 300,
		width : '90%',
		maxWidth : 600,
		minHeight : 200,
		dialogClass : "",
		show : "fadeIn",
		draggable : false,
		dialogClass : "dialogBlockLoaded"
	});
});

$('.searchPrTemplatee')
		.click(
				function(event) {
					event.preventDefault();
					$("#idGlobalInfo").hide();
					$("#idTemplateInfo").hide();
					$("#idGlobalError").hide();
					$("#idGlobalWarn").hide();

					var templateName = $('#idTemplateName').val();
					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");
					$
							.ajax({
								type : "POST",
								url : getContextPath()
										+ "/buyer/searchPrTemplate",
								data : {
									templateName : templateName,
								},
								dataType : "json",
								beforeSend : function(xhr) {
									$('#loading').show();
									xhr.setRequestHeader(header, token);
								},
								complete : function() {
									$('#loading').hide();
								},
								success : function(data) {
									var html = '';
									/*
									 * html += '<div class="col-md-3">'; html += '<div
									 * class="previous-box blank-div">'; html += '<a
									 * href="' + getContextPath() +
									 * '/buyer/prCreate">Create from blank</a>';
									 * html += '</div>'; html += '</div>';
									 */
									var found = false;
									$
											.each(
													data,
													function(key, value) {
														found = true;
														html += '<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
														html += '<div class="lower-bar-search-contant-main-block copy-frm-prev onHoverDiv" id="test">';
														html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
														html += '<h4 class="ellip-title" title="' + value.templateName + '">' + value.templateName + '</h4>';
														html += '</div>';
														html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-flex disp-f h-30">';
														html += '<div><label style="width: 83px;">Description :</label></div>'
														html += '<div><span data-toggle="tooltip" data-original-title="'+value.templateDescription+'" class="green ellip-desc">' + ( value.templateDescription == undefined ? '' : value.templateDescription ) + '</span></div>'
														html += '</div>' 
														html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
														html += '<label>Created By :</label> <span class="green">'
																+ (value.createdBy ? value.createdBy.name
																		: '')
																+ '</span></div>';
														html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
														html += '<label>Created Date :</label> <span class="green">'
																+ value.createdDate
																+ '</span> </div>';
														html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
														html += '<form action="'
																+ getContextPath()
																+ '/buyer/createPrFromAward" class="col-md-12" method="post" style="float: right;">';
														html += '<input type="hidden" id="templateId" value="'
																+ value.id
																+ '" name="templateId">';
														html += '<input type="hidden" id="_csrf" value="'
																+ token
																+ '" name="_csrf">';
														html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
														html += '</form></div>';
														// html += '<div><button
														// class="btn btn-black
														// for-form-back
														// hvr-pop
														// hvr-rectangle-out1"
														// type="submit">Quick
														// View</button></div>';
														html += '</div></div></div>';
													});
									$('#prTemplates > .row').html(html);
									currentPrBlocks();
									if (!found) {
										$("#idTemplateInfoMessage").html(
												'No matching data found');
										$("#idTemplateInfo").show();
										$('#rftEvents').hide();
									}
								},
								error : function(request, textStatus,
										errorThrown) {
									if (request.getResponseHeader('error')) {
										$("#idGlobalErrorMessage").html(
												request.getResponseHeader(
														'error').replace(",",
														"<br/>").replace(",",
														"<br/>").replace(",",
														"<br/>"));
										$("#idGlobalError").show();
									}
									if (request.getResponseHeader('info')) {
										$("#idGlobalInfoMessage").html(
												request.getResponseHeader(
														'info').replace(",",
														"<br/>").replace(",",
														"<br/>").replace(",",
														"<br/>"));
										$("#idGlobalInfo").show();
									}
								}
							});
				});

$('#tabTemplateId').click(function() {
	template = true;
	previousPr = false;
	currentPrBlocks();
	$('.searchTemplatefield').removeClass('flagvisibility');
	$('.searchpreviousfield').addClass('flagvisibility');
});
$('#tabPreviousId').click(function() {
	previousPr = true;
	template = false;
	currentPrBlocks();
	$('.searchpreviousfield').removeClass('flagvisibility');
	$('.searchTemplatefield').addClass('flagvisibility');
});
var template = true;
var previousPr = false;
var scrollTimer, lastScrollFireTime = 0;
// on scroll template loading templates
var templatePageNo = 0;


// on scroll copy previous loading previous pr
var prPageNo = 0;

function currentPrBlocks() {
	var heights = [];
	var fullWidth = parseInt($('.tab-pane.active').width());
	var blkWidth = parseInt($('.currentTemplates:first').width());
	var noofBlok = parseInt(fullWidth / blkWidth);
	$(".currentTemplates:visible").each(function(i) {
		var eachhgt = {};
		if ($(this).attr('data-value') != undefined) {
			if ($(this).hasClass('hightedBlock')) {
				eachhgt['hgt'] = $(this).height() - 83;
			} else {
				eachhgt['hgt'] = $(this).height();
			}
		} else {
			eachhgt['hgt'] = 0;
		}
		eachhgt['value'] = $(this).attr('data-value');
		heights.push(eachhgt);
	});
	console.log(heights);
	var maxhgt = [];
	var prevBlocks = [];
	$.each(heights, function(i, hgt) {
		maxhgt.push(hgt.hgt);
		prevBlocks.push(hgt.value);
		if (i == (noofBlok - 1)
				|| (i > (noofBlok - 1) && ((i + 1) % noofBlok) == 0)) {
			var MaxValHgt = Math.max.apply(null, maxhgt);
			var descHgt = [];
			var createdHgt = [];
			$.each(prevBlocks, function(i, blk) {
				if (blk != undefined) {
					$(".currentTemplates[data-value=" + blk + "] > div").css(
							'min-height', (MaxValHgt + 83)).parent().addClass(
							'hightedBlock');
				}
			});
			maxhgt = [];
			prevBlocks = [];
		}
	});
	var MaxValHgt = Math.max.apply(null, maxhgt);
	$.each(prevBlocks, function(i, blk) {
		if (blk != undefined) {
			$(".currentTemplates[data-value=" + blk + "] > div").css(
					'min-height', (MaxValHgt + 83)).parent().addClass(
					'hightedBlock');
		}
	});
}
function priviousTemplatesBlocks() {
	var heights = [];
	$(".priviousTemplates:visible").each(function(i) {
		var eachhgt = {};
		if ($(this).attr('data-value') != undefined) {
			eachhgt['hgt'] = $(this).height();
		} else {
			eachhgt['hgt'] = 0;
		}
		eachhgt['value'] = $(this).attr('data-value');
		heights.push(eachhgt);
	});
	console.log(heights);
	var maxhgt = [];
	var prevBlocks = [];
	$.each(heights, function(i, hgt) {
		maxhgt.push(hgt.hgt);
		prevBlocks.push(hgt.value);
		if (i == 3 || (i > 3 && ((i + 1) % 4) == 0)) {
			var MaxValHgt = Math.max.apply(null, maxhgt);
			var descHgt = [];
			var createdHgt = [];
			$.each(prevBlocks, function(i, blk) {
				if (blk != undefined) {
					$(".priviousTemplates[data-value=" + blk + "] > div").css(
							'min-height', (MaxValHgt + 83));
				}
			});
			maxhgt = [];
			prevBlocks = [];
		}
	});
	var MaxValHgt = Math.max.apply(null, maxhgt);
	$.each(prevBlocks, function(i, blk) {
		if (blk != undefined) {
			$(".priviousTemplates[data-value=" + blk + "] > div").css(
					'min-height', (MaxValHgt + 83));
		}
	});
}
