$(document).ready(
	function () {

		var files = [];
		$(document).on("change", "#otherCredentialUpload", function (event) {
			files = event.target.files;
		});
		$('.mega').on('scroll', function () {
			$(this).find('.header').css('top', $(this).scrollTop());
		});

		// upload company profile
		$('#companyProfileUpload').click(
			function (e) {

				e.preventDefault();
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();

				if (!$('#companyProfileForm').isValid()) {

					return false;
				}

				if ($('#companyProfile').val().length == 0) {
					$('p[id=idGlobalErrorMessage]').html("Please select company profile upload file.");
					$('div[id=idGlobalError]').show();
					return;
				}

				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				var oMyForm = new FormData();
				oMyForm.append("companyProfileFile", $('#companyProfile')[0].files[0]);
				console.log(oMyForm);
				$.ajax({
					url: getContextPath() + "/companyProfileUpload",
					data: oMyForm,
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					beforeSend: function (xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success: function (data, textStatus, request) {
						document.getElementById("companyProfile").value = "";
						var info = request.getResponseHeader('error');
						$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
						$('div[id=idGlobalSuccess]').show();
						$('span[id=idProfilefileUploadSpan]').text('');
						$("#companyProfileUpload").removeClass('btn-blue').addClass('btn-gray');
						var table = '';
						$.each(data, function (i, item) {
							table += '<tr>' + '<td class="width-60 "><form:form method="GET">' + '<a class="word-break" href="' + getContextPath() + '/downloadCompanyProfile/' + item.id + '">' + item.fileName + '</a>' + '<form:form></td>'
								+ '<td class="width-40"><span class="removeProfileFile" removeProfileId="' + item.id + '" companyFileName="' + item.fileName
								+ '"><span class="col-sm-10 no-padding">' + item.uploadDate + '</span><span class="col-sm-2 no-padding align-right"><a href="#"><i class="fa fa-trash-o" aria-hidden="true"></a></i></span></span></td>' + '</tr>';
						});
						// console.log(table);
						$('#uploadCompnayProfileDisplay tbody').html(table);

					},
					error: function (request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
					},
					complete: function () {
						$('#loading').hide();
					}
				});

			});

		/* Remove company profile */
		$(document).delegate('.removeProfileFile', 'click', function (e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			var profileId = $(this).attr('removeProfileId');
			var companyFileName = $(this).attr('companyFileName');
			console.log("Profile Id : " + profileId + "File Name :" + companyFileName);

			$.ajax({
				url: getContextPath() + "/removeCompanyProfile",
				data: {
					profileId: profileId,
					companyFileName: companyFileName
				},
				type: "GET",
				beforeSend: function (xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function (data, textStatus, request) {
					$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('info').split(",").join("<br/>"));
					$('div[id=idGlobalSuccess]').show();
					$('#uploadCompnayProfileDisplay tbody').html('');
				},
				error: function (request, textStatus, errorThrown) {
					console.log(JSON.stringify(result));
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete: function () {
					$('#loading').hide();
				}
			});

		});

		/* Remove Track Project */
		$(document).delegate('.removeProjectFile', 'click', function (e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var currentRecord = $(this);
			var projectId = $(this).attr('removeProjectId');
			var removeProject = $(this).attr('removeProject');
			console.log("Project Id : " + projectId);

			$.ajax({
				url: getContextPath() + "/removeTrackProject",
				data: {
					projectId: projectId,
					removeProject: removeProject
				},
				type: "GET",
				beforeSend: function (xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function (data, textStatus, request) {
					$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('info').split(",").join("<br/>"));
					$('div[id=idGlobalSuccess]').show();
					currentRecord.parents('tr').remove();
				},
				error: function (request, textStatus, errorThrown) {
					console.log(JSON.stringify(result));
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete: function () {
					$('#loading').hide();
				}
			});

		});

		// upload Other files
		$('#OtherCredUpload').click(
			function () {
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();

				if (!$('#otherCredentialUploadForm').isValid()) {
					return false;
				}

				if ($('#otherCredentialUpload').val().length == 0) {
					$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
					$('div[id=idGlobalError]').show();
					return;
				}
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");

				var otherCredentialDesc = $('#otherCredentialDesc').val();

				var oMyForm = new FormData();

				oMyForm.append("file", $('#otherCredentialUpload')[0].files[0]);
				oMyForm.append("desc", otherCredentialDesc);
				console.log(oMyForm);
				$.ajax({
					url: getContextPath() + "/otherCredentialUpload",
					data: oMyForm,
					type: "POST",
					enctype: 'multipart/form-data',
					processData: false,
					contentType: false,
					beforeSend: function (xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success: function (data, textStatus, request) {
						var info = request.getResponseHeader('error');
						console.log("Success message : " + info);
						document.getElementById("otherCredentialUpload").value = "";
						document.getElementById("otherCredentialDesc").value = "";
						$('#OtherCredUpload').removeClass('btn-blue').addClass('btn-gray');
						$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
						$('div[id=idGlobalSuccess]').show();
						$('span[id=idOtherFileUploadSpan]').text('');
						var table = '';
						$.each(data, function (i, item) {
							var itemdescription = '&nbsp;';
							if (item.description != null) {
								itemdescription = item.description;
							}
							table += '<tr>' + '<td class="width-33"><form:form method="GET">' + '<a class="word-break" href="' + getContextPath() + '/supplierreg/downloadOtherCredential/' + item.id + '">' + item.fileName + '</a>' + '<form:form>' + '</td>'
								+ '<td class="width-33">' + itemdescription + '</td>' + '<td class="width-33" align="center"><span class="removeOtherFile" removeOtherId="' + item.id + '" otherCredFile="' + item.fileName
								+ '"><span class="col-sm-10 no-padding">' + item.uploadDate + '</span><span class="col-sm-2 no-padding align-right"><a href=""><i class="fa fa-trash-o" aria-hidden="true"></i>' +
								'</a></span></span></td></tr>';
						});
						// console.log(table);
						$('#uploadOtherFiless tbody').html(table);

					},
					error: function (request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
					},
					complete: function () {
						$('#loading').hide();
					}
				});

			});

		/* Remove Other */

		$(document).delegate(
			'.removeOtherFile',
			'click',
			function (e) {
				e.preventDefault();
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();

				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				var removeOtherId = $(this).attr('removeOtherId');
				var otherCredFile = $(this).attr('otherCredFile');
				console.log("Profile Id : " + removeOtherId);

				$.ajax({
					url: getContextPath() + "/removeOtherCredential",
					data: {
						removeOtherId: removeOtherId,
						otherCredFile: otherCredFile
					},
					type: "GET",
					beforeSend: function (xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success: function (data, textStatus, request) {
						var info = request.getResponseHeader('error');
						$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
						$('div[id=idGlobalSuccess]').show();
						var table = '';
						$.each(data, function (i, item) {
							var itemdescription = '&nbsp;';
							if (item.description != null) {
								itemdescription = item.description;
							}
							table += '<tr>' + '<td class="width-33"><form:form method="GET">' + '<a class="word-break" href="' + getContextPath() + '/supplierreg/downloadOtherCredential/' + item.id + '">' + item.fileName + '</a>' + '<form:form>' + '</td>'
								+ '<td class="width-33">' + itemdescription + '</td>' + '<td class="width-33" align="center"><span class="removeOtherFile" otherCredFile="' + item.fileName
								+ '"><span class="col-sm-10 no-padding">' + item.uploadDate + '</span><span class="col-sm-2 no-padding align-right"><a href=""><i class="fa fa-trash-o" aria-hidden="true"></i>' +
								'</a></span></span></td></tr>';
						});
						// console.log(table);
						$('#uploadOtherFiless tbody').html(table);
					},
					error: function (request, textStatus, errorThrown) {
						console.log(JSON.stringify(request));
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
					},
					complete: function () {
						$('#loading').hide();
					}
				});

			});
		$('#uploadOtherFiles').on('click', '#buttonSelector', function () {
			$(this).closest('tr').remove();
		});

		$('#idTrackrecord').click(function () {
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			// console.log($('#demo-form').serialize());
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var supplierId = $("#id").val();
			$.ajax({
				url: getContextPath() + "/saveSupplierInSession/5",
				data: $('#demo-form').serialize(),
				type: "POST",
				beforeSend: function (xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function (result) {
					window.location.href = getContextPath() + "/supplierTrackRecord?supplierId=" + supplierId;

				},
				error: function (result) {
					console.log(JSON.stringify(result));
					$('#loading').hide();
				},
				complete: function () {
					$('#loading').hide();
				}
			});
		});

		$('#projectDelete').click(function () {
			window.location.href = getContextPath() + "/supplierProfile";
		});

	});

$(".open11").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$(".tab-pane.active , .form-wizard .active").removeClass("active");
	$("#step-1, .tb_1").addClass("active");
	$(".tb_1").prevAll().addClass("active");
	$(".tb_1").nextAll().removeClass("active");
	$(".tb_1").nextAll().removeClass("activeprev");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});
$(".open21").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	if (!$('#demo-form1').isValid({
		ignore: ":not(.chosen-select)"
	}))
		return false;
	$(".tab-pane.active , .form-wizard .active").removeClass("active");
	$("#step-2, .tb_2").addClass("active");
	$(".tb_2").prevAll().addClass("active");
	$(".tb_2").nextAll().removeClass("active");
	$(".tb_2").nextAll().removeClass("activeprev");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});
$(".open31").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	if (!$('#demo-form1, #demo-form2').isValid({
		ignore: ":not(.chosen-select)"
	}))
		return false;
	$('#step-3').find('.alert').remove();
	$(".tab-pane.active , .form-wizard .active").removeClass("active");
	$("#step-3, .tb_3").addClass("active");
	$(".tb_3").prevAll().addClass("active");
	$(".tb_3").nextAll().removeClass("active");
	$(".tb_3").nextAll().removeClass("activeprev");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});
$(".open41").unbind().click(function (event) {
	event.preventDefault();

	if ($(this).attr('data-move') == 0) {
		return false;
	}
	if (!$('#demo-form1, #demo-form2, #demo-form3').isValid({
		ignore: ":not(.chosen-select)"
	}))
		return false;
	$(".tab-pane.active , .form-wizard .active").removeClass("active");
	$("#step-4, .tb_4").addClass("active");
	$(".tb_4").prevAll().addClass("active");
	$(".tb_4").nextAll().removeClass("active");
	$(".tb_4").nextAll().removeClass("activeprev");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});
$(".open51").unbind().click(function (event) {
	event.preventDefault();
	// window.location.href=getContextPath() + "/registration?step=5"
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$(".tab-pane.active , .form-wizard .active").removeClass("active");
	$("#step-5, .tb_5").addClass("active");
	$(".tb_5").prevAll().addClass("active");
	$(".tb_5").nextAll().removeClass("active");
	$(".tb_5").nextAll().removeClass("activeprev");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});

$(".open52").unbind().click(function (event) {
	event.preventDefault();
	window.location.href = getContextPath() + "/supplierProfile?step=5"
});

// Binding next button on first step
$(".open1").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#demo-form1').isValid())
		return false;

	var buttonId = this.id;
	if (buttonId !== "idBtnPrevious2") {
		saveData(event, "idBtnNext1", "1");
		$("a.open1").attr('data-move', '1');
	} else {
		$(".tab-pane.active , .form-wizard .active").removeClass("active");
		$("#step-" + 1 + " , .tb_" + 1).addClass("active");
		$(".tb_" + 1).prevAll().addClass("active");
		$(".tb_" + 1).nextAll().removeClass("active");
		$(".tb_" + 1).nextAll().removeClass("activeprev");
		return false;
	}

});

// Binding next button on second step
$(".open2").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#demo-form1').isValid({
		ignore: ":not(.chosen-select)"
	}))
		return false;

	var buttonId = this.id;
	if (buttonId !== "idBtnPrevious3") {
		saveData(event, "idBtnNext2", "2");
		$("a.open11").attr('data-move', '1');
	} else {
		$(".tab-pane.active , .form-wizard .active").removeClass("active");
		$("#step-" + 2 + " , .tb_" + 2).addClass("active");
		$(".tb_" + 2).prevAll().addClass("active");
		$(".tb_" + 2).nextAll().removeClass("active");
		$(".tb_" + 2).nextAll().removeClass("activeprev");
		return false;
	}
});

// Binding back button on second step
$(".open3").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#demo-form2').isValid())
		return false;

	var buttonId = this.id;
	if (buttonId !== "idBtnPrevious4") {
		saveData(event, "idBtnNext3", "3");
		$("a.open21").attr('data-move', '1');
	} else {
		$(".tab-pane.active , .form-wizard .active").removeClass("active");
		$("#step-" + 3 + " , .tb_" + 3).addClass("active");
		$(".tb_" + 3).prevAll().addClass("active");
		$(".tb_" + 3).nextAll().removeClass("active");
		$(".tb_" + 3).nextAll().removeClass("activeprev");
		return false;
	}
});

// Binding back button on third step
$(".open4").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$("a.open31").attr('data-move', '1');
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#demo-form3').isValid())
		return false;

	saveData(event, "idBtnNext4", "4");
});

$(".open5").unbind().click(function (event) {
	event.preventDefault();
	if ($(this).attr('data-move') == 0) {
		return false;
	}
	$("a.open41").attr('data-move', '1');
	$("a.open51").attr('data-move', '1');
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	saveData(event, "idBtnNext5", "5");
});

$(".open6").unbind().click(function (event) {
	event.preventDefault();
	$('#loading').show();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var supplierTrackDesc = $('#idServicesOffered').val();
	window.location.href = getContextPath() + "/supplierProfile?step=6&supplierTrackDesc=" + supplierTrackDesc;

});

$('a[data-toggle="tab"]').unbind().click(function (e) {
	$(this).parent('li').prevAll().addClass("activeprev");
	$(this).parent('li').nextAll().removeClass("activeprev");
	/*
	 * if($(this).attr('href') == '#step-2'){ $(".tab-pane.active , .form-wizard .active").removeClass("active"); }
	 */
});

/* ===== tooltip ========== */
$('[data-toggle="tooltip"]').tooltip();

/* ===== multi select tree ========== */
$("#test-select , #test-select2").treeMultiselect({
	enableSelectAll: true,
	sortable: true,
	collapsible: true
});

$(document).ready(
	function () {
		// Search country/state list
		$('.searchListCheckCountry')
			.keyup(
				function () {
					var valList = $.trim($(this).val());
					if (valList.length > 2 || valList.length == 0) {
						var fromTable = $(this).attr('data-from');
						var relclass = $(this).attr('data-relclass');
						var fildInpName = $(this).attr('data-inpname');
						var supplierId = $('form#demo-form2').find('#id').val();
						var projectId = $('form#demo-form2').find('#projId').val();

						var activeTab = $('.tab-pane.active').attr('id');
						var selectted = '';
						var listlevel = 0;
						var connectedBlock = $('.' + relclass);
						listlevel = findchild(connectedBlock, listlevel);
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						var checkedAlredy = [];
						checkedAlredy.push('0');
						console.log(relclass);
						connectedBlock.find('input[type="checkbox"]:checked').each(function () {
							checkedAlredy.push($(this).val());
						});
						var track = $('#trackDet').val();
						if ((activeTab == 'undefined' || activeTab == null || (activeTab != null && activeTab == '')) && track == 'true') {
							activeTab = 'step-6';
						}

						if ((activeTab == 'undefined' || activeTab == null || (activeTab != null && activeTab == '')) && (track == 'undefined' || track == null || (track != null && track == ''))) {
							activeTab = 'step-2';
						}

						$
							.ajax({
								url: getContextPath() + "/supplier/searchCoverage",
								data: {
									'search': valList,
									'activeTab': activeTab,
									'supplierId': supplierId,
									'projectId': projectId,
									'checkedAlredy': checkedAlredy
								},
								type: 'POST',
								dataType: 'JSON',
								beforeSend: function (xhr) {
									$('#loading').show();
									xhr.setRequestHeader(header, token);
								},
								success: function (obj) {
									var htm = '';
									if (obj.length > 0) {
										$
											.each(
												obj,
												function (key, value) {
													htm += "<li ><span class='nvigator-place'>";
													if (value.children != null && value.children != '') {
														htm += "<i class='fa fa-minus' aria-hidden='true'></i></span>";
													} else {
														htm += "<i class='fa fa-plus' aria-hidden='true'></i></span>";
													}
													htm += "<input type='checkbox' data-validation='checkbox_group' data-validation-qty='min1' data-validation-error-msg-container='#" + fildInpName + "-error-dialog'  name='" + fildInpName
														+ "' " + selectted + "  value='" + value.id + "'" + (value.checked ? 'checked="checked"' : '') + " ><input type='hidden' name='_" + fildInpName
														+ "' value='on'><span class='number tree_heading'>" + value.name + "</span>";
													if (value.children != null && value.children != '') {
														htm += '<ul>';
														$
															.each(
																value.children,
																function (key1, value1) {
																	htm += "<li ><span class='nvigator-place'><i class='fa fa-minus' aria-hidden='true'></i></span><input type='checkbox' data-validation='checkbox_group' data-validation-qty='min1' data-validation-error-msg-container='#"
																		+ fildInpName
																		+ "-error-dialog' name='"
																		+ fildInpName
																		+ "' "
																		+ selectted
																		+ "  value='"
																		+ value1.id
																		+ "'"
																		+ (value1.checked ? 'checked="checked"' : '')
																		+ " ><input type='hidden' name='_" + fildInpName + "' value='on'><span class='number tree_heading'>" + value1.name + "</span></li>";
																});
														htm += '</ul>';
													}
													htm += '</li>';
												});
									}
									// connectedBlock.find('input[type="checkbox"]').not(':checked').parents('li').remove();
									connectedBlock.html(htm);
									loadAllCheckboxValuesFilter();
								},
								error: function (request, textStatus, errorThrown) {
									if (request.getResponseHeader('error')) {
										$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
										$('div[id=idGlobalError]').show();
									}
									$('#loading').hide();
								},
								complete: function () {
									$('#loading').hide();
								}
							});
					}
				});

		// Search Industry category
		$('.searchListCheck').keyup(function () {
			var valList = $.trim($(this).val());
			if (valList.length > 2 || valList.length == 0) {
				var fromTable = $(this).attr('data-from');
				var relclass = $(this).attr('data-relclass');
				var fildInpName = $(this).attr('data-inpname');
				var supplierId = $('form#demo-form2').find('#id').val();
				var projectId = $('form#demo-form2').find('#projId').val();

				var activeTab = $('.tab-pane.active').attr('id');
				var selectted = '';
				var listlevel = 0;
				var connectedBlock = $('.' + relclass);
				listlevel = findchild(connectedBlock, listlevel);
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				var checkedAlredy = ['0'];
				connectedBlock.find('input[type="checkbox"]:checked').each(function () {
					checkedAlredy.push($(this).val());
				});

				var track = $('#trackDet').val();
				console.log("Coverages Search....." + activeTab + " supplierId : " + supplierId + " projectId : " + projectId + " trackDet " + $('#trackDet').val());
				if ((activeTab == 'undefined' || activeTab == null || (activeTab != null && activeTab == '')) && track == 'true') {
					activeTab = 'step-6';
				}

				if ((activeTab == 'undefined' || activeTab == null || (activeTab != null && activeTab == '')) && (track == 'undefined' || track == null || (track != null && track == ''))) {
					activeTab = 'step-2';
				}

				console.log("activeTab : " + activeTab);

				$.ajax({
					url: getContextPath() + "/supplier/searchCategory",
					data: {
						'search': valList,
						'activeTab': activeTab,
						'supplierId': supplierId,
						'projectId': projectId,
						'checkedAlredy': checkedAlredy
					},
					type: 'POST',
					dataType: 'JSON',
					beforeSend: function (xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					},
					success: function (obj) {
						var htm = '';
						console.log(obj);
						console.log(obj.length);
						if (obj.length > 0) {
							htm += childLoopCategory(selectted, fildInpName, htm, obj);
						}
						connectedBlock.html(htm);
						loadAllCheckboxValuesFilter();
					},
					error: function (request, textStatus, errorThrown) {
						if (request.getResponseHeader('error')) {
							$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
							$('div[id=idGlobalError]').show();
						}
						$('#loading').hide();
					},
					complete: function () {
						$('#loading').hide();
					}
				});
			}
		});

	});


function childLoopCategory(selectted, fildInpName, htm, obj) {
	$.each(obj, function (key, value) {
		htm += "<li ><span class='nvigator'>";
		if (value.children != null && value.children != '') {
			htm += "<i class='fa fa-minus' aria-hidden='true'></i></span>";
		} else {
			htm += "<i class='fa fa-plus' aria-hidden='true'></i></span>";
		}
		htm += "<input type='checkbox' data-validation='checkbox_group' data-validation-qty='min1' data-validation-error-msg-container='#serviceIndustry-error-dialog'  name='" + fildInpName + "' " + selectted + "  value='" + value.id + "'"
			+ (value.checked ? 'checked="checked"' : '') + " ><input type='hidden' name='_" + fildInpName + "' value='on'><span class='number tree_heading'>" + value.categoryCode + " - " + value.categoryName + "</span>";
		if (value.children != null && value.children != '') {
			htm += '<ul>';
			htm += childLoopCategory(selectted, fildInpName, '', value.children);
			htm += '</ul>';
		}
		htm += '</li>';
	});
	return htm;
}

function findchild(connectedBlock, listlevel) {
	var levelData = [];
	connectedBlock.children('li').each(function () {
		listlevel = 0;
		if ($(this).children('ul').length > 0 && $.trim($(this).children('ul').html()) != '') {
			listlevel = listlevel + 1;
			findchild($(this).children('ul'), listlevel);
		}
		levelData.push(listlevel);
	});
	var listlevel = Math.max.apply(Math, levelData);
	return listlevel;
}

loadAllCheckboxValuesFilter();
function loadAllCheckboxValuesFilter() {
	$('.leftSideOfCheckbox').each(function () {
		var currentleftBlock = $(this);
		currentleftBlock.next('.rightSideOfCheckbox').html('');
		currentleftBlock.find('input[type="checkbox"]:checked').each(function () {
			if ($(this).siblings('ul').find('input[type="checkbox"]:checked').length == 0) {
				var htmldata = '<div class="item" data-value="' + $(this).val() + '"><span class="remove-selected">&#10005;</span>' + $(this).siblings(".number").text() + '</div>';
				console.log($(this).val());
				$(this).closest('.chk_scroll_box').find('.rightSideOfCheckbox').append(htmldata);
			}
		});
	});
}

function saveData(event, buttonId, step) {

	event.preventDefault();
	$("#" + buttonId).prop("disabled", true);
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		url: getContextPath() + "/saveSupplierInSession/" + step,
		data: $('#demo-form1, #demo-form2, #demo-form3, #demo-form4, #demo-form5').serialize(),
		type: "POST",
		beforeSend: function (xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function (result) {
			$(".tab-pane.active , .form-wizard .active").removeClass("active");
			$("#step-" + step + " , .tb_" + step).addClass("active");
			$(".tb_" + step).prevAll().addClass("active");
			$(".tb_" + step).nextAll().removeClass("active");
			$(".tb_" + step).nextAll().removeClass("activeprev");
		},
		error: function (request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		complete: function () {
			$('#loading').hide();
			$("#" + buttonId).prop("disabled", false);
		}
	});

}

/* Edit of Track Record */
$(document).delegate('.editRecord > a', 'click', function (e) {
	e.preventDefault();
	$('#loading').show();
	var editid = $(this).attr('editId');
	window.location.href = getContextPath() + "/supplierProfile?step=6&projectId=" + editid;
});
/*
 * $('#check-form').on('click', function() { // reset error array errors = []; if( !$(this).isValid(lang, conf, false) ) { displayErrors( errors ); }
 * else { // The form is valid } });
 */

$('#projectAdd').click(
	function (e) {
		e.preventDefault();
		// $('#track-form').validate();
		if (!$('#track-form').isValid())
			return false;

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var step = $('#supplierStep').val();
		// console.log($('#demo-form').serialize());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url: getContextPath() + "/supplierTrackRecord",
			data: $('#track-form').serialize(),
			type: "POST",
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function (data, textStatus, request) {
				var table = '';
				var idasData = [];
				var info = request.getResponseHeader('error');
				$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
				$('div[id=idGlobalSuccess]').show();
				$.each(data, function (i, item) {
					var url = getContextPath() + '/supplier/updateSupplierProfileTrackRecord/' + item.id;
					/*
					 * if($.inArray(item.id, idasData) > 0){ return false; } else { return true; }
					 */
					console.log($.inArray(item.id, idasData));
					console.log(info);
					if ($.inArray(item.id, idasData) == -1) {
						idasData.push(item.id);
						table += '<tr>' + '<td class="width_20">' + item.year + '</td>' + '<td class="width_150 editRecord"> <a href="' + url + '" editId="' + item.id + '">' + item.projectName + '</a> </td>' + '<td class="numeric width_100">'
							+ item.contactValue + '</td>' + '<td class="width_20" align="center"><span class="removeProjectFile" removeProjectId="' + item.id + '" removeProject="' + item.projectName
							+ '"><a href=""><i class="fa fa-trash-o" aria-hidden="true"></i></a></span></td>' + '</tr>';
					}
				});
				$("#projectName , #clientName , #idYearE ,#idCurrency , #projId").val("");
				$("[name='contactValue']").val("");
				$("[name='clientEmail']").val("");
				jQuery("#idCurrency").val("");
				jQuery("#idCurrency").trigger("chosen:update");
				// console.log(table);
				$('#addProjectTrackRecord tbody').html(table);
				console.log("STEP ::" + step);
				$(".tab-pane.active , .form-wizard .active").removeClass("active");
				$("#step-" + step + " , .tb_" + step).addClass("active");
				$(".tb_" + step).prevAll().addClass("active");
				$(".tb_" + step).nextAll().removeClass("active");
				$(".tb_" + step).nextAll().removeClass("activeprev");

				if (typeof (history.pushState) != "undefined") {
					var obj = {
						Title: 'Registration',
						Url: '?step=' + step
					};
					history.pushState(obj, obj.Title, obj.Url);
				} else {
					window.location.href = getContextPath() + '/supplierProfile?step=' + step;
				}

			},
			error: function (result) {
				console.log(JSON.stringify(result));
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});

	});

$('#idBtnFinish').click(function (e) {
	finish(e);
});

function finish(event) {
	var step = 5;
	event.preventDefault();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		url: getContextPath() + "/supplierProfile",
		data: $('#demo-form1, #demo-form2, #demo-form3, #demo-form4, #demo-form5').serialize(),
		type: "POST",
		beforeSend: function (xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function (result) {
			console.log("Success : " + JSON.stringify(result));
			window.location.href = getContextPath() + "/supplier/supplierDashboard"
		},
		error: function (request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
				$('div[id=idGlobalError]').show();
				$(".tab-pane.active , .form-wizard .active").removeClass("active");
				$("#step-" + step + " , .tb_" + step).addClass("active");
				$(".tb_" + step).prevAll().addClass("active");
				$(".tb_" + step).nextAll().removeClass("active");
				$(".tb_" + step).nextAll().removeClass("activeprev");
			}
			$('#loading').hide();
		},
		complete: function () {
			$('#loading').hide();
		}
	});

}

// Copied from jsp

$(document).ready(
	function () {

		$('select.chosen-select').change(function () {
			$('select.chosen-select').validate();
		});

		if ($('#declaration1').is(':checked')) {
			$('#declaration1').parent('span').addClass('checked');
		}
		$('#declaration1').change(function () {
			if ($('#declaration1').is(':checked')) {
				$('#declaration1').parent('span').addClass('checked');
			} else {
				$('#declaration1').parent('span').removeClass('checked');
			}
		});
		$(document).delegate('#searchTrackRecord', 'keyup', function () {
			var $rows = $('.for-pad-data tbody tr');
			var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
			$rows.show().filter(function () {
				var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
				return !~text.indexOf(val);
			}).hide();
		});
		/* this cod add plus minus icon to chekbox */
		/*
		 * $(".search_ul_1 [type=checkbox], .search_ul [type=checkbox]").each(function () { if ($(this).parent('li').find('ul').length > 0) { var
		 * htm = '<span class="nvigator"><i class="fa fa-minus" aria-hidden="true"></i></span>'; $(this).before(htm); } });
		 * 
		 * 
		 */
		$(document).on('change', '.nvigator+input[type="checkbox"]', function () {
			if ($('.rightSideOfCheckbox')[0] && $('.rightSideOfCheckbox')[0].children.length > 25) {
				if ($('font-grey').context.documentURI.includes('supplierProfileCategory')) {
					var message = 'Please select a maximum of 25 industry sectors only'
					$('p[id=idGlobalErrorMessage]').html(message.split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					$(this).prop('checked', false)
					return;
				}
			}

			if ($(this).prop('checked') == true) {
				industryCatExpnd($(this).prev());
			}
		});
		// expand industry category
		$(document).on('click', '.nvigator', function () {
			var obj1 = $(this);
			if (obj1.find('i').hasClass('fa-plus')) {
				industryCatExpnd(obj1);
			} else {
				obj1.parent('li').find('ul').slideToggle('slow');
				obj1.find('i').removeClass('fa-minus').addClass('fa-plus');
			}
		});

		function industryCatExpnd(currentBlock) {
			var appenHtm = currentBlock.parent();
			var parentvalue = currentBlock.next().val();
			var fieldLabelName = currentBlock.next().attr('name');
			var selectted = '';
			var plusMinusClass = 'fa fa-plus';
			if (currentBlock.next().is(':checked')) {
				selectted = 'checked="checked"';
				plusMinusClass = 'fa fa-minus'
			}
			// appenHtm.find('ul').remove();
			var data = {}
			data["id"] = parentvalue;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			if (currentBlock.find('i').hasClass('fa-plus') || selectted != '') {
				$.ajax({
					type: "POST",
					url: getContextPath() + "/supplier/findChildIndustry",
					data: JSON.stringify(data),
					beforeSend: function (xhr) {
						$('#loading').show();
						currentBlock.find('i').removeClass('fa-plus').addClass('fa-spinner');
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					success: function (obj) {
						var htm = '<ul>';
						$.each(obj, function (key, value) {
							console.log(obj);
							console.log("Level : " + value.level);
							if (value.level > 0) {
								htm += "<li ><span class='nvigator' data-id='" + value.id + "' data-level='" + value.level + "'><i class='" + plusMinusClass + "' aria-hidden='true'></i></span><input type='checkbox' name='" + fieldLabelName
									+ "' " + selectted + " value=" + value.id + " ><span class='number tree_heading'>" + value.categoryCode + " - " + value.categoryName + "</span>";
								if (selectted != '' && value.children != null && value.children != '') {
									htm += '<ul>';
									$.each(value.children, function (childKey, keyValue) {
										htm += "<li ><span class='nvigator' data-id='" + keyValue.id + "' data-level='" + keyValue.level + "'><i class='" + plusMinusClass + "' aria-hidden='true'></i></span><input type='checkbox' name='"
											+ fieldLabelName + "' " + selectted + " value=" + keyValue.id + " ><span class='number tree_heading'>" + keyValue.categoryCode + " - " + keyValue.categoryName + "</span>";
										console.log(" Level >> " + keyValue.level);
										if (keyValue.children != null && keyValue.children != '') {
											htm += '<ul>';
											$.each(keyValue.children, function (child1Key, key1Value) {
												console.log("Level >>>> " + key1Value.level);
												htm += "<li ><span class='nvigator' data-id='" + key1Value.id + "' data-level='" + key1Value.level + "'><i class='" + plusMinusClass
													+ "' aria-hidden='true'></i></span><input type='checkbox' name='" + fieldLabelName + "' " + selectted + " value=" + key1Value.id + " ><span class='number tree_heading'>"
													+ key1Value.categoryCode + " - " + key1Value.categoryName + "</span>";
												if (key1Value.children != null && key1Value.children != '') {
													htm += '<ul>';
													$.each(key1Value.children, function (child2Key, key2Value) {
														console.log("Level >>>>>> " + key2Value.level);
														htm += "<li ><span class='nvigator' data-id='" + key2Value.id + "' data-level='" + key2Value.level + "'><i class='" + plusMinusClass
															+ "' aria-hidden='true'></i></span><input type='checkbox' name='" + fieldLabelName + "' " + selectted + " value=" + key2Value.id + " ><span class='number tree_heading'>"
															+ key2Value.categoryCode + " - " + key2Value.categoryName + "</span></li>";
													});
													htm += '</ul>';
												}
												htm += '</li>';
											});
											htm += '</ul>';
										}
										htm += '</li>';
									});
									htm += '</ul>';
								}
								htm += '</li>';
							} else {
								htm += "<li ><span class='nvigator' data-id='" + value.id + "' data-level='" + value.level + "'>";
								htm += "<input type='checkbox' name='" + fieldLabelName + "'   value=" + value.id + " style='display:none;'>"
								htm += "<i class='" + plusMinusClass + "' aria-hidden='true'></i></span><span class='number tree_heading'>" + value.categoryCode + " - " + value.categoryName + "</span>";

							}
						});
						htm += '</ul>';
						appenHtm.find('ul').remove();
						// console.log(htm);
						appenHtm.append(htm);
						currentBlock.find('i').removeClass('fa-plus fa-spinner').addClass('fa-minus');
					},
					error: function (e) {

					},
					complete: function () {
						$('#loading').hide();
						loadAllCheckboxValues();
					}
				});
			} else {
				currentBlock.parent('li').find('ul').slideToggle('slow');
				currentBlock.find('i').removeClass('fa-minus').addClass('fa-plus');
			}
		}

		function loadChildIndustryCat(parentvalue, fieldLabelName, selectted, plusMinusClass) {
			var data = {}
			data["id"] = parentvalue;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var htm = '';
			$.ajax({
				type: "POST",
				url: getContextPath() + "/supplier/findChildIndustry",
				data: JSON.stringify(data),
				async: false,
				beforeSend: function (xhr) {
					$('#loading').show();
					// currentBlock.find('i').removeClass('fa-plus').addClass('fa-spinner');
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				success: function (obj) {
					htm += '<ul>';
					$.each(obj, function (key, value) {
						htm += "<li ><span class='nvigator'><i class='" + plusMinusClass + "' aria-hidden='true'></i></span><input type='checkbox' name='" + fieldLabelName + "' " + selectted + " value=" + value.id
							+ " ><span class='number tree_heading'>" + value.categoryCode + " - " + value.categoryName + "</span>";
						// if(value.children != null && value.children != ''){
						if (selectted != '') {
							htm += loadChildIndustryCat(value.id, fieldLabelName, selectted, plusMinusClass);
						}
						// }
						htm += "</li>";
					});
					htm += '</ul>';
				},
				error: function (e) {

				},
				complete: function () {
					$('#loading').hide();
					loadAllCheckboxValues();
				}
			});
			return htm;
		}

		$('.nvigator-place+input[type="checkbox"]').click(function () {
			if ($(this).prop('checked') == true) {
				nvigatorplace($(this).prev());
			}
		});

		$(document).on('click', '.nvigator-place', function () {
			var obj1 = $(this);
			if (obj1.find('i').hasClass('fa-plus')) {
				nvigatorplace(obj1);
			} else {
				obj1.parent('li').find('ul').slideToggle('slow');
				obj1.find('i').removeClass('fa-minus').addClass('fa-plus');
			}
		});

		function nvigatorplace(currentBlock) {
			var appenHtm = currentBlock.parent();
			var countryId = currentBlock.next().val();
			var fieldLabelName = currentBlock.next().attr('name');
			var selectted = '';
			if (currentBlock.next().is(':checked')) {
				selectted = 'checked="checked"';
			}

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			if (currentBlock.find('i').hasClass('fa-plus') || selectted != '') {
				$.ajax({
					type: "GET",
					url: getContextPath() + "/supplier/findStates",
					data: {
						countryId: countryId
					},
					beforeSend: function (xhr) {
						$('#loading').show();
						currentBlock.find('i').removeClass('fa-plus').addClass('fa-spinner');
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					success: function (obj) {
						// console.log(obj);
						var htm = '<ul>';
						$.each(obj, function (key, value) {
							htm += "<li ><span class='nvigator-place'><i class='fa fa-minus' aria-hidden='true'></i></span><input type='checkbox' name='" + fieldLabelName + "' " + selectted + "  value=" + value.id
								+ " ><span class='number tree_heading'>" + value.name + "</span></li>";
						});
						htm += "</ul>";
						appenHtm.find('ul').remove();
						// console.log(htm);
						appenHtm.append(htm);
						currentBlock.find('i').removeClass('fa-plus fa-spinner').addClass('fa-minus');
					},
					error: function (e) {

					},
					complete: function () {
						$('#loading').hide();
						loadAllCheckboxValues();
					}
				});
			} else {
				currentBlock.parent('li').find('ul').slideToggle('slow');
				currentBlock.find('i').removeClass('fa-minus').addClass('fa-plus');
			}
		}

		loadAllCheckboxValues();
		function loadAllCheckboxValues() {
			$('.leftSideOfCheckbox').each(function () {
				var currentleftBlock = $(this);
				currentleftBlock.next('.rightSideOfCheckbox').html('');
				currentleftBlock.find('input[type="checkbox"]:checked').each(function () {
					if ($(this).siblings('ul').find('input[type="checkbox"]:checked').length == 0) {
						var htmldata = '<div class="item" data-value="' + $(this).val() + '"><span class="remove-selected">&#10005;</span>' + $(this).siblings(".number").text() + '</div>';
						$(this).closest('.chk_scroll_box').find('.rightSideOfCheckbox').append(htmldata);
					}
				});
			});

			if ($('.rightSideOfCheckbox')[0] && $('.rightSideOfCheckbox')[0].children.length > 25) {
				if ($('font-grey').context.documentURI.includes('supplierProfileCategory')) {
					var message = 'Please select a maximum of 25 industry sectors only'
					$('p[id=idGlobalErrorMessage]').html(message.split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					$(this).prop('checked', false)

				}
			} else {
				if ($('font-grey').context.documentURI.includes('supplierProfileCategory')
					&& $('div[id=idGlobalError]').text().includes("25")) {
					$('div[id=idGlobalError]').hide();
					$('div[id=idGlobalSuccess]').hide();
				}
			}

		}

		$(document).on('change', '.leftSideOfCheckbox input[type=checkbox]', function () {
			$(this).parent().find('li input[type=checkbox]').prop('checked', $(this).is(':checked'));
			if ($(this).closest('ul').find('input[type=checkbox]:checked').length == 0) {
				$(this).closest('ul').siblings('input[type=checkbox]').prop('checked', false).attr('data-123', '123456ty7u8i9');
				$(this).closest('ul').siblings('input[type=checkbox]').change();
			} else {
				$(this).parents('ul').siblings('input[type=checkbox]').prop('checked', true);
			}
			loadAllCheckboxValues();
		});

		$(document).delegate('.remove-selected', 'click', function () {
			var deselVal = $(this).parent().attr('data-value');
			console.log("deselVal : " + deselVal);
			$(this).parent().remove();
			$('.leftSideOfCheckbox').find('input[type="checkbox"][value="' + deselVal + '"]').prop('checked', false);
			$('.leftSideOfCheckbox').find('input[type="checkbox"][value="' + deselVal + '"]').change();
		});

		$(document).on("change", "#companyProfile", function () {
			$(".show_name1").html($(this).val());
			if ($(this).val() != "") {
				$("#companyProfileUpload").removeClass('btn-gray').addClass('btn-blue');
			}
		});

		$(document).on("change", "#otherCredentialUpload", function () {
			if ($(this).val() != "") {
				$('.addmorefeature').removeClass('btn-gray').addClass('btn-blue');
			} else {
				$('.addmorefeature').removeClass('btn-blue').addClass('btn-gray');
			}
			$(".show_name").html($(this).val());
			$(".up_btn").removeClass('btn-gray').addClass('btn-blue');
		});

		/* START this code work for plus sign button to add text to next append */

		$(document).on("click", ".addmorefeature", function () {
			if ($(this).hasClass('btn-blue')) {
				var txt = $("#load_file").val();
				$('.add_more_feture_ul').append("<li><lable>" + txt + "</lable><a href='javascript:void(0);'><img src='images/black-xross.png' alt='feature image'></a></li>");
				$("#load_file").val('');
				$('.addmorefeature').removeClass('btn-blue').addClass('btn-gray');
				$(".show_name").empty();

			}

			$(".up_btn").removeClass('btn-gray').addClass('btn-blue');
		})
		$(document).on("click", ".add_more_feture_ul li a", function () {
			$(this).closest('li').remove();

			var tot = $(".add_more_feture_ul li").length;
			if (tot == 0 && $('.show_name1').is(':empty')) {
				$(".up_btn").addClass('btn-gray').removeClass('btn-blue');
			}

		})

		/* END this code work for plus sign button to add text to next append */

		/* call search fileter by this function */

		// searchFilter("search_textbox","search_ul");
		$(document).on('keyup change', '.searchListCheck, .searchListCheckCountry', function () {
			var searchTerm = $(this).val();
			var dataRelClass = $(this).attr('data-relclass');
			console.log("dataRelClass : " + dataRelClass);
			// $('.' + dataRelClass).removeHighlight();
			/*
			 * if (searchTerm) { $('.' + dataRelClass).highlight(searchTerm); }
			 */
		});
		/*
		 * $(document).on('keyup change', '.search_textbox_1', function () { var searchTerm = $(this).val(); $('.search_ul_1').removeHighlight();
		 * if (searchTerm) { $('.search_ul_1').highlight(searchTerm); } });
		 */

	});

// new
/*
 * Edit of Track Record $(document).delegate('.editSupplierRecord', 'click', function(e) { e.preventDefault(); $('#loading').show(); var editid =
 * $(this).attr('editId'); if (typeof editid == "undefined") { return; } var header = $("meta[name='_csrf_header']").attr("content"); var token =
 * $("meta[name='_csrf']").attr("content"); $.ajax({ url : getContextPath() + "/updateSupplierTrackRecord/" + editid, data :
 * $('#track-form').serialize(), type : "POST", beforeSend : function(xhr) { xhr.setRequestHeader(header, token); $('#loading').show(); }, success :
 * function(data, textStatus, request) { jQuery("#extrFrm").collapse("show"); jQuery("#projectName").val(data.projectName);
 * jQuery("#clientName").val(data.clientName); jQuery("#idYearE").val(data.year); jQuery("#idCurrency").val(); jQuery("#projId").val(data.id);
 * 
 * jQuery("#idCurrency").trigger("chosen:update"); $('#loading').hide(); $("[name='contactValue']").val(data.contactValue);
 * $("[name='clientEmail']").val(data.clientEmail);
 * 
 * $("#extrFrm").collapse('show'); $("html, body").animate({ scrollTop : $('#projectName').offset().top - 50 }, 1000); $('#projectName').focus();
 * $("#projectUpdateSupplier").show(); $("#projectAddSupplier").hide(); // contactValue NAME // clientEmail } }); });
 */
$('#projectAddSupplier11').click(
	function (e) {
		e.preventDefault();
		// $('#track-form').validate();
		if (!$('#track-form').isValid())
			return false;

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		// console.log($('#demo-form').serialize());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url: getContextPath() + "/supplier/supplierEditProfileTrackRecord",
			data: $('#track-form').serialize(),
			type: "POST",
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function (data, textStatus, request) {
				console.log(data);
				var table = '';
				var idasData = [];
				var info = request.getResponseHeader('error');
				$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
				$('div[id=idGlobalSuccess]').show();
				$.each(data, function (i, item) {
					var url = getContextPath() + '/supplier/updateSupplierProfileTrackRecord/' + item.id;
					var removeurl = getContextPath() + '/supplier/removeSupplierProfileTrackRecord/' + item.id;
					console.log(url);
					/*
					 * if($.inArray(item.id, idasData) > 0){ return false; } else { return true; }
					 */

					if ($.inArray(item.id, idasData) == -1) {
						idasData.push(item.id);
						table += '<tr>' + '<td class="width_20">' + item.year + '</td>' + '<td class="width_150 "> <a href="' + url + '" class="editSupplierRecord" editId="' + item.id + '">' + item.projectName + '</a> </td>'
							+ '<td class="numeric width_100">' + item.contactValue + '</td>' + '<td class="width_20" align="center"><a href="' + removeurl + '"><i class="fa fa-trash-o" aria-hidden="true"></i></a></span></td>' + '</tr>';
					}
				});
				// console.log(table);
				jQuery("#extrFrm").collapse("hide");
				$("#projectName , #clientName , #idYearE ,#idCurrency , #projId").val("");
				$("[name='contactValue']").val("");
				$("[name='clientEmail']").val("");
				jQuery("#idCurrency").val("");
				$('#addProjectTrackRecord tbody').html(table);
				/*
				 * $(".tab-pane.active , .form-wizard .active").removeClass("active"); $("#step-" + step + " , .tb_" + step).addClass("active");
				 * $(".tb_" + step).prevAll().addClass("active"); $(".tb_" + step).nextAll().removeClass("active"); $(".tb_" +
				 * step).nextAll().removeClass("activeprev");
				 */

			},
			error: function (result) {
				console.log(JSON.stringify(result));
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});

	});

$('#projectUpdateSupplier11').click(
	function (e) {
		e.preventDefault();
		// $('#track-form').validate();
		if (!$('#track-form').isValid())
			return false;

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var projId = $('form#track-form').find('#id').val();
		// console.log($('#demo-form').serialize());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		console.log("Project Id : " + projId);
		$.ajax({
			url: getContextPath() + "/supplier/editSupplierTrackRecord/" + projId,
			data: $('#track-form').serialize(),
			type: "POST",
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function (data, textStatus, request) {
				var table = '';
				var idasData = [];
				var info = request.getResponseHeader('error');
				$.each(data, function (i, item) {
					var url = getContextPath() + '/supplier/updateSupplierProfileTrackRecord/' + item.id;
					var removeurl = getContextPath() + '/supplier/removeSupplierProfileTrackRecord/' + item.id;
					console.log(url);
					/*
					 * if($.inArray(item.id, idasData) > 0){ return false; } else { return true; }
					 */

					if ($.inArray(item.id, idasData) == -1) {
						idasData.push(item.id);
						table += '<tr>' + '<td class="width_20">' + item.year + '</td>' + '<td class="width_150 "> <a href="' + url + '" >' + item.projectName + '</a> </td>' + '<td class="numeric width_100">' + item.contactValue + '</td>'
							+ '<td class="width_20" align="center"><a href="' + removeurl + '"><i class="fa fa-trash-o" aria-hidden="true"></i></a></span></td>' + '</tr>';
					}
				});
				jQuery("#extrFrm").collapse("hide");
				$("#projectName , #clientName , #idYearE ,#idCurrency , #projId").val("");
				$("[name='contactValue']").val("");
				$("[name='clientEmail']").val("");
				jQuery("#idCurrency").val("");
				$('#addProjectTrackRecord tbody').html(table);
				// jQuery("#idCurrency").trigger("chosen:update");

				/*
				 * $('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>")); $('div[id=idGlobalSuccess]').show();
				 */
				/*
				 * $.each(data, function(i, item) {
				 * 
				 * console.log($.inArray(item.id, idasData)); console.log(info); if ($.inArray(item.id, idasData) == -1) { idasData.push(item.id);
				 * 
				 * table += '<tr>' + '<td class="width_20">' + item.year + '</td>' + '<td class="width_150 "> <a class="editSupplierRecord"
				 * href="" editid="'+ item.id +'">' + item.projectName + '</a> </td>' + '<td class="numeric width_100">' + item.contactValue + '</td>' + '<td class="width_20" align="center"><span
				 * class="removeProjectFile" removeProjectId="'+ item.id+'" removeProject="'+ item.projectName +'"><a href=""><i class="fa
				 * fa-trash-o" aria-hidden="true"></i></a></span></td>' + '</tr>'; } });
				 */

			},
			error: function (result) {
				console.log(JSON.stringify(result));
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});

	});




