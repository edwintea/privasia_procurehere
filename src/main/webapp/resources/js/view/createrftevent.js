$(document).ready(
		function() {

			// Multiple industry category search
			var indCate = $('#industryCatArrVal').val();

			var jsonIndCate = '';
			if (indCate !== undefined && indCate !== '') {
				jsonIndCate = jQuery.parseJSON(indCate);
				var indCat = [];
				$(jsonIndCate).each(function(i, item) {
					console.log("==" + i + "==" + item.id);
					indCat.push(item.id);
				});

				$('#industryCatArr').val(indCat);
			}

			$('.token-input-list').remove();
			$("#demo-input-local").tokenInput(getBuyerContextPath('searchCategory'), {
				minChars : 1,
				method : 'POST',
				hintText : "Start typing to search categories...",
				noResultsText : "No results",
				searchingText : "Searching...",
				queryParam : "search",
				propertyToSearch : "name",
				propertyToSearchCode : "code",
				minChars : 3,
				preventDuplicates : true,
				// setting saved value on update mode
				prePopulate : jsonIndCate
			});

			$.formUtils.addValidator({
				name : 'validate_custom_length',
				validatorFunction : function(value, $el, config, language, $form) {
					var val = value.split(".");
					if (val[0].replace(/,/g, '').length > 10) {
						return false;
					} else {
						return true;
					}
				},
				errorMessage : 'The input value is longer than 10 characters',
				errorMessageKey : 'validateLengthCustom'
			});

			$.formUtils.addValidator({
				name : 'validate_part_dept',
				validatorFunction : function(value, $el, config, language, $form) {
					var val = value.split(".");
					if (val[0].replace(/,/g, '').length > 6) {
						return false;
					} else {
						return true;
					}
				},
				errorMessage : 'Only 6 digit before decimal is allowed',
				errorMessageKey : 'validateLengthCustom'
			});

			$(document).delegate('.decimalChange', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				var budgetAmount = parseFloat($('#budgetAmount').val().replace(/\,|\s|\#/g, ''));
				if ($('#budgetAmount').val() === '' || $('#budgetAmount').val() === undefined) {
					$('#budgetAmount').val('');
				} else {
					budgetAmount = !isNaN(budgetAmount) ? budgetAmount.toFixed(decimalLimit) : '';
					$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount)));
				}

				var historicaAmount = parseFloat($('#historicaAmount').val().replace(/\,|\s|\#/g, ''));
				if ($('#historicaAmount').val() === '' || $('#historicaAmount').val() === undefined) {
					$('#historicaAmount').val('');
				} else {
					historicaAmount = !isNaN(historicaAmount) ? historicaAmount.toFixed(decimalLimit) : '';
					$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount)));
				}
				
				
			var estimatedBudget = parseFloat($('#estimatedBudget').val().replace(/\,|\s|\#/g, ''));
			if ($('#estimatedBudget').val() === '' || $('#estimatedBudget').val() === undefined) {
				$('#estimatedBudget').val('');
			} else {
				estimatedBudget = !isNaN(estimatedBudget) ? estimatedBudget : 0;
				$('#estimatedBudget').val(ReplaceNumberWithCommas((estimatedBudget).toFixed(decimalLimit)));
			}
				

			});

			$(document).delegate('input[name="budgetAmount"]', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				var budgetAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				budgetAmount = !isNaN(budgetAmount) ? budgetAmount.toFixed(decimalLimit) : '';
				console.log(ReplaceNumberWithCommas((budgetAmount)));
				$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount)));
			});

			$(document).delegate('input[name="historicaAmount"]', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				var historicaAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				historicaAmount = !isNaN(historicaAmount) ? historicaAmount.toFixed(decimalLimit) : '';
				console.log(ReplaceNumberWithCommas((historicaAmount)));
				$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount)));
			});
			
			$(document).delegate('input[name="estimatedBudget"]', 'change', function(e) {
			var decimalLimit = $('.decimalChange').val();
			var estimatedBudget = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
			estimatedBudget = !isNaN(estimatedBudget) ? estimatedBudget.toFixed(decimalLimit) : '';
			console.log(ReplaceNumberWithCommas((estimatedBudget)));
			$('#estimatedBudget').val(ReplaceNumberWithCommas((estimatedBudget)));
		});

			$(document).delegate('.access-list', 'click', function(e) {
				// $('.access-list').click(function() {
				$(this).children('ul').stop().slideToggle(400);
			});

			$("#eventPublishDate").blur(function() { // false validation alarm patch
				selector = $(this).closest(".form-group");
				setTimeout(function() {
					selector.removeClass("has-error");
					selector.find(".form-error").remove();
				}, 600);
			});

			// $('.access_check').on('click', function(e) {
			$(document).delegate('.access_check', 'click', function(e) {
				$('.access_check').prop('checked', false);
				$(this).prop('checked', true);
				tempId = $(this).attr("id");
				selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
				console.log(selector);
				if ($(this).val() == "Editor") {

					selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
				}
				if ($(this).val() == "Associate_Owner") {
					selector.html("<i class='fa fa-user-plus' aria-hidden='true '></i>");
				}
				if ($(this).val() == "Viewer") {
					selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
				}

				if ($(this).data('uid') == "" || $(this).data('uid') == undefined) return;

				/** ** Update ** */
				var memberType = $(this).val();
				var currentBlock = $(this);

				var userId = $(this).data('uid');
				var eventId = $('.event_form').find('#id').val();

				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					type : "POST",
					url : getBuyerContextPath('addTeamMemberToList'),
					data : {
						memberType : memberType,
						userId : userId,
						eventId : eventId
					},
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						$('#loading').hide();
					},
					// success : function(data) {
					// userList = userListForEvent(data);
					// alert("*");
					// // currentBlock.closest('.width100').next('.usersListTable').html(userList);
					// // currentBlock.parent().prev().find('select').find('option[value="'
					// // + userId + '"]').remove();
					// // currentBlock.parent().prev().find('select').val('').trigger("chosen:updated");
					// $('#loading').hide();
					// if ($('#eventTeamMembersList').length > 0) {
					// $('#eventTeamMembersList').DataTable().ajax.reload();
					// $('#loading').hide();
					// }
					// },
					//					

					success : function(data) {
						userList = userListForEvent(data);
						if ($('#eventTeamMembersList').length > 0) {
							$('#eventTeamMembersList').DataTable().ajax.reload();
						}

						$('#appUsersList').html("");
						$('#appUsersList').html(userList);
						$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
						$('#TeamMemberList').trigger("chosen:updated");
						$('#loading').hide();

					},
					error : function(request, textStatus, errorThrown) {

						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
					},
					complete : function() {
						$('#loading').hide();
						//$("#teamMemberListPopup").hide();
						//window.location.href = window.location.href;
					}
				});

			});

			var string = $.trim($('#daterangepicker-time').val());
			$('#eventPublishDate, #eventPublishTime, .bootstrap-timepicker-widget.dropdown-menu.open input').keypress(function() {
				return false;
			});
			$(document).delegate('keypress', '.bootstrap-timepicker-widget.dropdown-menu.open input', function() {
				return false;
			});
			// $('.bootstrap-timepicker-widget.dropdown-menu.open input').addClass('');
			if (string != "") { // event edit mode
				string = string.split(" ");
				console.log(string);
				var maxDateVal = '';
				if (string != '') {
					maxDateVal = string[4];
				}
				$("#eventPublishDate").datepicker("destroy");
				$("#eventPublishDate").datepicker({
					dateFormat : 'dd/mm/yy',
					changeYear : true,
					changeMonth : true,
					minDate : '0D',
					maxDate : maxDateVal,
					beforeShow : function() {
						setTimeout(function() {
							$('.ui-datepicker').css('z-index', 4001);
						}, 0);
					}
				});
			}

			$('.column_button_bar').on('click', '#s1_tender_adddel_btn', function(event) {
				event.preventDefault();
				$('#add_delete_column').show();
			});

			$('.column_button_bar').on('click', '#s1_tender_additem_btn', function(event) {
				event.preventDefault();
				$('#creat_seaction_form').show();
			});

			$('.create_list_sectoin').on('click', '.bq_tender_addsub_item', function(event) {
				event.preventDefault();
				$('#creat_subitem_form').show();
			});

			$(document).on("click", "#industryCategoryList > li", function() {
				$('#industryCategoryList').hide();
				var catText = $(this).html();
				var catVal = $(this).attr('data-value');
				$("#industryCategory").val(catText);
				$("#industryCategoryVal").val(catVal);
			});

			$('.chosen-select.disablesearch#idEventVisibility').chosen({
				disable_search : true
			}).change(function() {
				disableWizard($(this).val());
			});
			if ($('.chosen-select.disablesearch#idEventVisibility').length > 0) {
				disableWizard($('.chosen-select.disablesearch#idEventVisibility').val());
			}
			/*
			 * $(document).on("click", function () { $('#industryCategoryList').hide(); });
			 */

			var delay = (function() {
				var timer = 0;
				return function(callback, ms) {
					clearTimeout(timer);
					timer = setTimeout(callback, ms);
				};
			})();

			$(document).on("keyup", "#industryCategory", function() {
				var industryCat = $.trim($(this).val());
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				if (industryCat.length > 2) {
					$.ajax({
						url : getBuyerContextPath('searchCategory'),
						data : {
							'search' : industryCat
						},
						type : 'POST',
						dataType : 'json',
						beforeSend : function(xhr) {
							xhr.setRequestHeader(header, token);
						},
						success : function(data) {
							var html = '';
							if (data != '' && data != null && data.length > 0) {
								$.each(data, function(key, value) {
									html += '<li data-value="' + value.id + '">' + value.code + ' - ' + value.name + '</li>';
								});
							}
							$('#industryCategoryList').show();
							$('#industryCategoryList').html(html);
						},
						error : function(error) {
							console.log(error);
						},
						complete : function() {
							$('#loading').hide();
						}
					});
				}
			});

			/*
			 * START this code work for plus sign button to add text to next append
			 */
			$(document).on("keyup", ".feature_box", function() {
				if ($(this).val() != "") {
					$('.addmorefeature').removeClass('btn-black').addClass('btn-blue');
				} else {
					$('.addmorefeature').removeClass('btn-blue').addClass('btn-black');
				}
			})
			$(document).on("click", ".addmorefeature", function() {
				if ($(this).hasClass('btn-blue') && $(".feature_box").hasClass('valid')) {
					var txt = $(".feature_box").val();
					$('.add_more_feture_ul').append("<li><lable>" + txt + "</lable><a href='javascript:void(0);'><img src='images/black-xross.png' alt='feature image'></a></li>");
					$(".feature_box").val('');
					$('.addmorefeature').removeClass('btn-blue');
				}
			})

			$(document).on("click", ".add_more_feture_ul li a", function() {
				$(this).closest('li').remove();
			});

			$(document).on("click", ".addContactPerson", function(e) {
				e.preventDefault();
				if (!$('#demo-form-contact').isValid()) return false;
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$('#loading').show();
				var contactId = $.trim($('#addEditContactPopup').find('#contactId').val());
				var ajaxUrl = getBuyerContextPath('addContactPerson');
				$.ajax({
					url : ajaxUrl,
					data : $('#demo-form-contact').serialize(),
					type : "POST",
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					},
					success : function(data, textStatus, request) {
						// $('table.contactPersons').remove();
						$('#demo-form-contact').find('input[type="text"]').val('');
						var html = '';
						if (data != '' && data != null && data.length > 0) {
							$.each(data, function(key, value) {
								html += '<tr><td contact-id="' + value.id + '" class="text-left"><a class="editContact" href=""><img src="' + getContextPath() + '/resources/images/edit1.png"></a>';
								html += '<a class="deleteContact" href=""><img src="' + getContextPath() + '/resources/images/delete1.png"></a></td>';
								if (typeof value.title != "undefined") {
									html += '<td>' + value.title + '</td>';
								} else {
									html += '<td class="text-left">&nbsp</td>';
								}
								html += '<td class="text-left">' + value.contactName + '</td>';
								if (typeof value.designation != "undefined") {
									html += '<td>' + value.designation + '</td>';
								} else {
									html += '<td class="text-left">&nbsp</td>';
								}
								if (typeof value.contactNumber != "undefined") {
									html += '<td>' + value.contactNumber + '</td>';
								} else {
									html += '<td class="text-left">&nbsp</td>';
								}
								if (typeof value.mobileNumber != "undefined") {
									html += '<td>' + value.mobileNumber + '</td>';
								} else {
									html += '<td class="text-left">&nbsp</td>';
								}
								if (typeof value.comunicationEmail != "undefined") {
									html += '<td>' + value.comunicationEmail + '</td>';
								} else {
									html += '<td class="text-left">&nbsp</td>';
								}
							});
						}
						$('.contactPersons tbody').html(html);
						$('#addEditContactPopup').dialog('close');
						var info = request.getResponseHeader('success');
						$.jGrowl(info, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});
					},
					error : function(request, textStatus, errorThrown) {
						console.log("ERROR :  " + request.getResponseHeader('error'));
						if (request.getResponseHeader('error')) {
							var info = request.getResponseHeader('error').split(",").join("<br/>");
							$.jGrowl(info, {
								sticky : false,
								position : 'top-right',
								theme : 'bg-red'
							});
						}
						$('#loading').hide();
					},
					complete : function() {
						$('#loading').hide();
						$('#addEditContactPopup').modal('hide');
						// $("#" + buttonId).prop("disabled", false);
					}
				});
			});

			$('#idCountry').change(function() {
				var countryId = $('#idCountry').val();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					type : "GET",
					url : getBuyerContextPath('getStates'),
					data : {
						countryId : countryId
					},
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
					},
					success : function(data) {
						var stateList = '<option value="">Select State</option>';
						$.each(data, function(i, obj) {
							stateList += '<option value="' + obj.id + '">' + obj.stateName + '</option>';
						});
						// console.log(stateList);
						$('#stateList').html(stateList);
						$('#stateList').trigger("chosen:updated");
					},
					error : function(e) {
						console.log("Error");
					},
				});
			});

			$(document).on(
					"click",
					".addCorrespondenceAddress",
					function(e) {
						e.preventDefault();
						if (!$('#demo-form-address').isValid()) return false;

						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						$('#loading').show();
						$.ajax({
							url : getBuyerContextPath('addCorrespondenceAddress'),
							data : $('#demo-form-address').serialize(),
							type : "POST",
							beforeSend : function(xhr) {
								$('#loading').show();
								xhr.setRequestHeader(header, token);
							},
							success : function(data) {
								// $('table.contactPersonsAddress').remove();
								var html = '';
								if (data != '' && data != null && data.length > 0) {
									$.each(data, function(key, value) {
										html += '<tr><td>' + (key + 1) + '</td><td>' + value.line1 + '</td><td>' + value.line2 + '</td><td>' + value.city + '</td><td>' + value.state.stateName + '</td><td>' + value.country + '</td><td>' + value.zip
												+ '</td></tr>';
									});
								}
								$('.contactPersonsAddress tbody').html(html);
							},
							error : function(request, textStatus, errorThrown) {
								console.log("ERROR :  " + request.getResponseHeader('error'));
								if (request.getResponseHeader('error')) {
									$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
									$('div[id=idGlobalError]').show();
								}
								$('#loading').hide();
							},
							complete : function() {
								$('#loading').hide();
								// $("#" + buttonId).prop("disabled", false);
							}
						});
					});
			$(document).on("keyup", "#chosenCategoryAll_chosen .chosen-search input", function(e) {

				// ignore arrow keys
				switch (e.keyCode) {
					case 37:
						return false;
						break;
					case 38:
						return false;
						break;
					case 39:
						return false;
						break;
					case 40:
						return false;
						break;
				}
				var industryCat = $.trim($(this).val());
				var industryCatOrig = $(this).val();
				var currentSearchBlk = $(this);
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				if (industryCat.length > 2) {
					$.ajax({
						url : getBuyerContextPath('searchCategory'),
						data : {
							'search' : industryCat
						},
						type : 'POST',
						dataType : 'json',
						beforeSend : function(xhr) {
							xhr.setRequestHeader(header, token);
							$('#loading').show();
						},
						success : function(data) {
							var html = '<option value="">Select Category</option>';
							if (data != '' && data != null && data.length > 0) {
								$.each(data, function(key, value) {
									html += '<option value="' + value.id + '">' + value.code + ' - ' + value.name + '</option>';
								});
							}
							$('#chosenCategoryAll').html(html);
							$("#chosenCategoryAll").trigger("chosen:updated");
							currentSearchBlk.val(industryCatOrig);
						},
						error : function(error) {
							console.log(error);
						},
						complete : function() {
							$('#loading').hide();
							// $("#" + buttonId).prop("disabled", false);
						}

					});
				}
			});

			$(document).on("keyup", "#idChooseEvent_chosen .chosen-search input", function() {
				var rfaEvent = $.trim($(this).val());
				var rfaEventOrig = $(this).val();
				var currentSearchBlk = $(this);
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				if (rfaEvent.length > 2) {
					$.ajax({
						url : getBuyerContextPath('searchAuction'),
						data : {
							'search' : rfaEvent
						},
						type : 'POST',
						dataType : 'json',
						beforeSend : function(xhr) {
							xhr.setRequestHeader(header, token);
							$('#loading').show();
						},
						success : function(data) {
							var html = '<option value="">Select Previous Events</option>';
							if (data != '' && data != null && data.length > 0) {
								$.each(data, function(key, value) {
									html += '<option value="' + value.id + '">' + value.eventName + '</option>';
								});
							}
							$('#idChooseEvent').html(html);
							$("#idChooseEvent").trigger("chosen:updated");
							currentSearchBlk.val(rfaEventOrig);
						},
						error : function(error) {
							console.log(error);
						},
						complete : function() {
							$('#loading').hide();
							// $("#" + buttonId).prop("disabled", false);
						}

					});
				}
			});
			$("#idAddReminder").click(function(e) {
				e.preventDefault();
				console.log("reminderDurationType");
				var reminderDate = $.trim($('#daterangepicker-time').val());
				if (reminderDate != '') {
					$("#addReminder").find('input[type="text"]').val('');
					$("#addReminder").find('.remindMeTime').trigger("change");
					$("#addReminder").find('.remindMeTime option[value="DAYS"]').attr('selected', 'selected');
					$("#addReminder").modal("show");

				} else {
					$('#daterangepicker-time').blur();
				}
			});
			$("#daterangepicker-time").on("hide.daterangepicker", function(ev, picker) {
				/*
				 * startTimestamp = picker.startDate.toDate().getTime(); curTimestamp = $.now();
				 */

				// console.log(startTimestamp);
				// console.log(curTimestamp);
				/*
				 * if(startTimestamp <= curTimestamp){ $("#daterangepicker-time").val(""); $.jGrowl("Not a valid date or time", { sticky : false,
				 * position : 'top-right', theme : 'bg-red' }); //$(ev).trigger("hide.daterangepicker");
				 * 
				 * return false; }
				 */
			});

			$('#daterangepicker-time').on(
					'apply.daterangepicker',
					function(ev, picker) {
						ev.preventDefault();

						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						if (!$('#addReminder').isValid()) {
							return false;
						}

						var string = $.trim($('#daterangepicker-time').val());
						string = string.split(" ");
						console.log(string);
						$("#eventPublishDate").datepicker("destroy");
						$("#eventPublishDate").datepicker({
							dateFormat : 'dd/mm/yy',
							changeYear : true,
							changeMonth : true,
							minDate : '0D',
							maxDate : string[4],
							beforeShow : function() {
								setTimeout(function() {
									$('.ui-datepicker').css('z-index', 4001);
								}, 0);
							}
						});

						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						// var eventStartDate =
						// $.trim($('#demo-form1').find('#idStartEndDate').val());

						var dateRangeData = $.trim($('#daterangepicker-time').val());
						var reminderData = {
							'dateRangeData' : dateRangeData,
							'eventId' : $('#id').val()
						};

						var status = $('#demo-form1').find('#status').val();

						$.ajax({

							url : getBuyerContextPath('manageReminderOnDateChange'),
							data : reminderData,
							type : 'POST',
							dataType : 'json',
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								$('#loading').show();
							},
							success : function(data, textStatus, request) {

								var table = '';
								$.each(data, function(i, item) {
									var remindNotifyType = '';
									if (item.startReminder == true) {
										remindNotifyType = "Start Date";
									} else {
										remindNotifyType = "End Date";
									}
									console.log("remindNotifyType:" + remindNotifyType);
									table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder before ' + remindNotifyType + ': </b>'
											+ item.reminderDate + '</p>' + '</div><div class="col-md-2"><a href="" class="deleteReminder' + (status === 'SUSPENDED' && item.startReminder == true ? ' disabled' : '') + '" reminderId=""><i class="fa fa-times-circle"></i></a></div></div>';
								});
								if (table == '') {
									table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
								}
								$('.reminderList.marginDisable').html(table);

							},
							error : function(request, textStatus, errorThrown) {
								if (request.getResponseHeader('error')) {
									$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
									$('div[id=idGlobalError]').show();
									showMessage('ERROR', request.getResponseHeader('error'));
									console.log("remindNotifyTypewwwwww");
								}

							},
							complete : function() {

								/*
								 * var status = $('#demo-form1').find('#status').val(); if (status != undefined && status != '' && status === 'DRAFT') {
								 * $("#demo-form1").ajaxSubmit({ url : getBuyerContextPath('autoSaveDraft'), type : 'post' }) }
								 */
								$('#loading').hide();
							}
						});
					});

			$(".skipvalidation ").on('click', function(e) {
				if ($("#skipper").val() == undefined) {
					e.preventDefault();

					$(this).after("<input type='hidden' id='skipper' value='1'>");
					$('form.has-validation-callback :input').each(function() {
						$(this).on('beforeValidation', function(value, lang, config) {
							$(this).attr('data-validation-skipped', 1);
						});
					});
					$(this).trigger("click");
				}
			});

			// Reload the Team Member User List
			/*
			 * $(document).delegate('#TeamMemberList', 'change', function(e) { //e.preventDefault(); var optionSelected = $("option:selected", this);
			 * console.log("on Change ...................", optionSelected[0].label, optionSelected[0].value); updateUserList('', $(this),
			 * 'NORMAL_USER');
			 * 
			 * setTimeout(function(){ $('#TeamMemberList').append(new Option(optionSelected[0].label, optionSelected[0].value, true));
			 * $('#TeamMemberList').trigger("chosen:updated"); }, 1000);
			 * 
			 * //setTimeout(function(){ $('#TeamMemberList ~ .chosen-container input').val(searchText); console.log('assigned the search text') },
			 * 1000); });
			 */

		});

$(function() {

	$('.scroll_box_inner').perfectScrollbar();

	$(document).on("change", "#load_file", function() {
		$(".show_name").html($(this).val());
	});

	/*
	 * $('#idEventVisibility').chosen().change(function() { if ($(this).val() == 'PRIVATE') { $("#import").slideDown(); } else {
	 * $("#import").slideUp(); } });
	 */

	$(document).on("change", "#inlineCheckbox114", function(e) {
		/*
		 * if($('.role-bottom-ul li [type="radio"]:checked').length == 1){ $(this).prop('checked',true); $.uniform.update(); }
		 * $("#sub-credit").slideToggle();
		 */
		if ($(this).is(":checked")) {
			// alert($('.role-bottom-ul li [type="radio"]:checked').val());
			$("#sub-credit").slideDown();
		} else {
			// $(document).delegate('.role-bottom-ul li
			// [type="radio"]','click',function() {
			$('.role-bottom-ul li [type="radio"]').prop('checked', false);
			var dataAddress = $(this).closest('li').children('.del-add').html();
			$('.phisicalArressBlock').html('');
			$('#deleteDeliveryAddress').hide();
			// $("#sub-credit").slideUp();
			// });
			$.uniform.update();
			$("#sub-credit").slideUp();
		}
	});

	$(document).on("click", ".physicalCriterion + span.pull-left .col-md-10", function(e) {
		$("#sub-credit").slideToggle();
	});

	$(document).on("keyup", ".delivery_add", function() {
		if ($(this).val() != "") {
			$('.delivery_add_btn').removeClass('btn-black disabled').addClass('btn-blue');
		} else {
			$('.delivery_add_btn').removeClass('btn-blue').addClass('btn-black disabled');
		}
	});

	$(document).on("change", "#demo-input-local", function(e) {
		e.preventDefault();
		// adding arr of industry category
		var indCat = [];
		select1 = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
		select1.each(function() {
			indCat.push($(this).find('input').val());
		});
		// console.log(indCat);
		$('#industryCatArr').val(indCat);
	});

	$(document).on(
			"click",
			".delivery_add_btn",
			function() {
				if ($(this).hasClass('btn-blue')) {
					var txt = $(".delivery_add").val();

					var clo = '<li><div class="radio-primary"><label><input type="radio" id="inlineRadio110" name="example-radio1" ></label></div>  <div class="del-add"><span class="desc">' + txt
							+ '</span><div class="li-links"><a href="#">Edit</a><a class="remove_del" href="javascript:void(0);">Remove</a></div></div></li>';
					$('.role-bottom-ul').prepend(clo);

					$('.role-bottom-ul').find("[type=radio]").uniform();
					$('.role-bottom-ul li:first').find(".radio span").append('<i class="glyph-icon icon-circle"></i>');

					$(".delivery_add").val('');
					$('.delivery_add_btn').removeClass('btn-blue');
					$('.delivery_add_btn').addClass('btn-black disabled');
				}
			});

	$(document).on("click", ".remove_del", function() {
		$(this).closest("li").slideUp();
	});

	$('.role-bottom-ul').find("[type=radio]").uniform();

	$(document).on("keyup", ".import_box", function() {
		if ($(this).val() != "") {
			$('.add_import').removeClass('btn-black disabled').addClass('btn-blue');
		} else {
			$('.add_import').removeClass('btn-blue').addClass('btn-black disabled');
		}
	});

	$(document).on("click", ".add_import", function() {

		var ccd = '<div class="suppliers-name"><label>Ajay Garg</label><a class="del_import" style="float: right;" href="javascript:void(0);"><img src="images/black-xross.png"></a></div>';
		if ($(this).hasClass('btn-blue')) {
			var txt = $(".import_box").val();
			$('.insert_import').prepend(ccd);
			$('.insert_import .suppliers-name:first').find('label').html(txt);
			$(".import_box").val('');
			$('.add_import').removeClass('btn-blue');
			$('.add_import').addClass('btn-black disabled');
		}
	});

	$(document).on("click", ".del_import", function() {
		$(this).closest(".suppliers-name").slideUp();
	});

	$(document).on("click", ".radio_yes-no input[type='radio']", function(e) {
		/*
		 * var eventType=$('#eventType').val();
		 * 
		 * if(eventType =="RFA"){ $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'}) } else{
		 * $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraftDesc'), type: 'post'}) }
		 */
		if ($(this).val() == 0) {
			e.preventDefault();
			$(this).parent().removeClass('checked');
			var radioname = $(this).attr('name');
			$('input[type="radio"][name="' + radioname + '"][value="1"]').prop('checked', true).parent().addClass('checked');
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getBuyerContextPath('mangeEventRequirement'),
				data : {
					'eventRequirement' : radioname,
					'eventId' : $('#id').val()
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					$('input[type="radio"][name="' + radioname + '"][value="1"]').prop('checked', false).parent().removeClass('checked');
					$('input[type="radio"][name="' + radioname + '"][value="0"]').prop('checked', true).parent().addClass('checked');
					eventDescriptionWizard(radioname, 0);
				},
				error : function(request, textStatus, errorThrown) {
					console.log(request.responseText);
					if (request.responseText > 0 || request.responseText === "") {
						$('#myModalDeleteRelatedItems').find('#selectedrelated').val(radioname);
						var mesglabel = '';
						if (radioname == 'documentReq') {
							mesglabel = 'Documents';
						} else if (radioname == 'meetingReq') {
							mesglabel = 'Meetings';
						} else if (radioname == 'questionnaires') {
							mesglabel = 'Questionnaires';
						} else if (radioname == 'billOfQuantity') {
							mesglabel = 'Bill Of Quantities';
						} else if(radioname == 'scheduleOfRate') {
							mesglabel = 'Schedule of Rate'
						}
						$('#myModalDeleteRelatedItems').find('.radio_name').html('<label>Are you sure you want to delete ' + mesglabel + ' ?</label>');
						$('#myModalDeleteRelatedItems').modal('show');
					} else {
						$('input[type="radio"][name="' + radioname + '"][value="1"]').prop('checked', false).parent().removeClass('checked');
						$('input[type="radio"][name="' + radioname + '"][value="0"]').prop('checked', true).parent().addClass('checked');
						eventDescriptionWizard(radioname, 0);
					}
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		} else {
			eventDescriptionWizard($(this).attr('name'), 1);
		}
	});
	$(document).on("click", "#idConfirmDeleteRelatedItems", function(e) {
		var radioname = $('#myModalDeleteRelatedItems').find('#selectedrelated').val();
		$('#myModalDeleteRelatedItems').modal('hide');
		$('input[type="radio"][name="' + radioname + '"][value="1"]').prop('checked', false).parent().removeClass('checked');
		$('input[type="radio"][name="' + radioname + '"][value="0"]').prop('checked', true).parent().addClass('checked');
		eventDescriptionWizard(radioname, $('input[type="radio"][name="' + radioname + '"]:checked').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getBuyerContextPath('deleteEventRequirement'),
			data : {
				'eventRequirement' : radioname,
				'eventId' : $('#id').val()
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				console.log(data);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	$(document).delegate('.delivery_add', 'keyup', function() {
		var $rows = $('.role-bottom-ul li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});

	$(document)
			.delegate(
					'.role-bottom-ul li [type="radio"]',
					'click',
					function() {
						var dataAddress = '<div class=""><div class="col-md-10">';
						dataAddress += $(this).closest('li').children('.del-add').html();
						dataAddress += '</div><div class="col-md-2 align-right">';
						dataAddress += '<a class="pull-right" title="" data-placement="top" id="deleteDeliveryAddress" data-toggle="tooltip" data-original-title="Delete Delivery Address" style=" font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;"><i class="fa fa-times-circle"></i></a>';
						dataAddress += '</div></div>';
						$('.phisicalArressBlock').html(dataAddress);
						$('.physicalCriterion').addClass('flagvisibility').next().addClass('active').children('.phisicalArressBlock1').addClass('flagvisibility').next().addClass('buyerAddressRadios active');
						$('.physicalCriterion input[type="checkbox"]').prop('checked', true);
						$('#deleteDeliveryAddress').show();
						$.uniform.update();
						vertiCaleALignDeleteAddress();
						$("#sub-credit").slideUp();
					});
	vertiCaleALignDeleteAddress();
	$(document).delegate('#deleteDeliveryAddress', 'click', function() {
		$('.phisicalArressBlock').html('');
		$('.physicalCriterion').removeClass('flagvisibility').next().removeClass('active').children('.phisicalArressBlock1').removeClass('flagvisibility').next().removeClass('buyerAddressRadios active');
		$('.physicalCriterion input[type="checkbox"]').prop('checked', false);
		$('.role-bottom-ul li [type="radio"]').prop('checked', false);
		$('#deleteDeliveryAddress').hide();
		$.uniform.update();
		// $("#sub-credit").slideDown();
	});

	$(document).delegate('.addContactPersonPop', 'click', function(e) {
		e.preventDefault();
		$('#resetEventContctForm').click();
		$('#addEditContactPopup').find('#contactId, #id').val('');
		$("#addEditContactPopup").dialog({
			modal : true,

			minWidth : 300,
			width : '90%',
			maxWidth : 600,
			minHeight : 200,
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").addClass("closePd");
			},
			show : "fadeIn",
			draggable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$('.ui-dialog-title').text(addcontactlabel);
		$('#addEditContactPopup').find('a.addContactPerson').text(addcontactlabel);
	});

	$(document).delegate('.closePd', 'click', function(e) {
		$('#demo-form-contact').get(0).reset();
	})

	$(document).delegate('.editContact', 'click', function(e) {

		e.preventDefault();
		var contactId = $(this).closest('td').attr('contact-id');
		$('#addEditContactPopup').find('#contactId').val(contactId);

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getBuyerContextPath('editContact'),
			data : {
				'contactId' : contactId,
				'eventId' : $('#eventId').val()
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$.each(data, function(name, value) {
					$('#demo-form-contact').find('input[name="' + name + '"]').val(value);
					// $('#contactId').find('input[name="'+item.name+'"]').val(item.value);
				});
				$("#addEditContactPopup").dialog({
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
				$('.ui-widget-overlay').addClass('bg-white opacity-60');
				$('.ui-dialog-title').text(updateContactLabel);
				$('#addEditContactPopup').find('a.addContactPerson').text(updateContactLabel);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	$(document).delegate('.deleteContact', 'click', function(e) {
		e.preventDefault();
		var contactId = $(this).closest('td').attr('contact-id');
		$('#confirmDeleteContact').find('#deleteIdContact').val(contactId);
		$('#confirmDeleteContact').modal();
	});

	$(document).delegate('#confDelContact', 'click', function(e) {
		e.preventDefault();
		var contactId = $('#confirmDeleteContact').find('#deleteIdContact').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getBuyerContextPath('deleteContact'),
			data : {
				'contactId' : contactId,
				'eventId' : $('#id').val()
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$('#demo-form-contact').find('input[type="text"]').val('');
				$('#addEditContactPopup').find('#contactId').val('');

				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$.each(data, function(key, value) {
						html += '<tr><td contact-id="' + value.id + '" class="text-left"><a class="editContact" href=""><img src="' + getContextPath() + '/resources/images/edit1.png"></a>';
						html += '<a class="deleteContact" href=""><img src="' + getContextPath() + '/resources/images/delete1.png"></a></td>';
						if (typeof value.title != "undefined") {
							html += '<td>' + value.title + '</td>';
						} else {
							html += '<td class="text-left">&nbsp</td>';
						}
						html += '<td class="text-left">' + value.contactName + '</td>';
						if (typeof value.designation != "undefined") {
							html += '<td>' + value.designation + '</td>';
						} else {
							html += '<td class="text-left">&nbsp</td>';
						}
						if (typeof value.contactNumber != "undefined") {
							html += '<td>' + value.contactNumber + '</td>';
						} else {
							html += '<td class="text-left">&nbsp</td>';
						}
						if (typeof value.mobileNumber != "undefined") {
							html += '<td>' + value.mobileNumber + '</td>';
						} else {
							html += '<td class="text-left">&nbsp</td>';
						}
						if (typeof value.comunicationEmail != "undefined") {
							html += '<td>' + value.comunicationEmail + '</td>';
						} else {
							html += '<td class="text-left">&nbsp</td>';
						}
					});
				}
				$('.contactPersons tbody').html(html);
				$('#confirmDeleteContact').modal('hide');
			},
			error : function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
	$("#tags").autocomplete({
		source : availableTags
	});
	$("#tagres").autocomplete({
		source : availableTags
	});
});
$('#addReminder1').submit(function(e) {
	e.preventDefault();
	$('#reminderButton').click();
});
$('#reminderButton').click(
		function(e) {
			var status = $('#demo-form1').find('#status').val();

			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			if (!$('#addReminder').isValid()) {
				return false;
			}
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			// var eventStartDate =
			// $.trim($('#demo-form1').find('#idStartEndDate').val());
			var reminderDuration = $.trim($('#addReminder').find('#remindMe').val());
			var reminderDurationType = $.trim($('#addReminder').find('.remindMeTime').val());

			var attrReminder = $("#addReminder").attr('data-dtype');
			if (typeof attrReminder === typeof undefined || attrReminder === false) {
				var dateRangeData = $.trim($('#daterangepicker-time').val());
				var reminderId = $.trim($('#reminderId').val());
				var reminderNotifyType = $.trim($('#addReminder').find('input[name=reminderNotifyType]:checked').val());
				var reminderData = {
					'reminderDuration' : reminderDuration,
					'reminderDurationType' : reminderDurationType,
					'dateRangeData' : dateRangeData,
					'eventId' : $('#id').val(),
					'reminderId' : reminderId,
					'reminderNotifyType' : reminderNotifyType
				};
				$.ajax({
					url : getBuyerContextPath('addReminderOfEvent'),
					data : reminderData,
					type : 'POST',
					dataType : 'json',
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success : function(data, textStatus, request) {
						var table = '';
						$.each(data, function(i, item) {
							var remindNotifyType = '';
							if (item.startReminder == true) {
								remindNotifyType = "Start Date";
							} else {
								remindNotifyType = "End Date";
							}
							table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder before ' + remindNotifyType + ': </b>'
									+ item.reminderDate + '</p>' + '</div><div class="col-md-2"><a href="" class="deleteReminder' + (status === 'SUSPENDED' && item.startReminder == true ? ' disabled' : '') + '" reminderId=""><i class="fa fa-times-circle"></i></a></div></div>';
						});
						if (table == '') {
							table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
						}
						$('.reminderList.marginDisable').html(table);
					},
					error : function(request, textStatus, errorThrown) {
						console.log(request.getResponseHeader('error'));
						var error = request.getResponseHeader('error');
						$.jGrowl(error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});

					},
					complete : function() {
						$('#loading').hide();
					}
				});
				$('#loading').hide();
			} else {
				var currentBlock = $('.rfaIdAddReminder[data-dtype="' + attrReminder + '"]');
				var reminderDate = $.trim(currentBlock.closest('.row').find('.for-clander-view').val());
				var reminderTime = $.trim(currentBlock.closest('.row').find('.for-timepicker-view').val());
				var reminderId = $.trim($('#reminderId').val());
				var reminderData = {
					'reminderDuration' : reminderDuration,
					'reminderDurationType' : reminderDurationType,
					'eventDate' : reminderDate,
					'eventTime' : reminderTime,
					'reminderType' : attrReminder,
					'eventId' : $('#id').val(),
					'reminderId' : reminderId
				};
				$.ajax({
					url : getBuyerContextPath('addReminderOfEventStartEnd'),
					data : reminderData,
					type : 'POST',
					dataType : 'json',
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						$('#loading').show();
					},
					success : function(data, textStatus, request) {
						var table = '';
						$.each(data, function(i, item) {
							table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder: </b>' + item.reminderDate + '</p>'
									+ '</div><div class="col-md-2"><a href="" class="deleteReminder" data-remtype="' + attrReminder + '" reminderId=""><i class="fa fa-times-circle"></i></a></div></div>';
						});
						if (table == '') {
							table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
						}
						currentBlock.closest('.row').next().find('.reminderList.marginDisable').html(table);
					},
					error : function(request, textStatus, errorThrown) {
						console.log(request);
						var error = request.getResponseHeader('error');
						$.jGrowl(error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});
					},
					complete : function() {
						$('#loading').hide();
					}
				});
			}
		});

$(document).delegate('.deleteReminder', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteReminder').find('#deleteIdReminder').val($(this).closest('.row').attr('id'));
	$('#confirmDeleteReminder').find('.modal-body').find('b').text($(this).closest('.row').find('.reminderDateDel').val());
	$('#confirmDeleteReminder').modal();
});

$(document).delegate(
		'#confDelReminder',
		'click',
		function(e) {
			e.preventDefault();
			var reminderId = $('#confirmDeleteReminder').find('#deleteIdReminder').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var attrReminder = $('.row#' + reminderId).find('.deleteReminder').attr('data-remtype');
			var reminderType = '';
			var status = $('#demo-form1').find('#status').val();
			if (typeof attrReminder !== typeof undefined && attrReminder !== false) {
				reminderType = attrReminder;
			}
			$.ajax({
				url : getBuyerContextPath('deleteReminder'),
				data : {
					'reminderId' : reminderId,
					'eventId' : $('#id').val(),
					'reminderType' : reminderType
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var table = '';
					$.each(data, function(i, item) {
						if (typeof attrReminder !== typeof undefined && attrReminder !== false) {
							table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder:</b>' + item.reminderDate + '</p>'
									+ '</div><div class="col-md-2"><a href="" class="deleteReminder"';
						} else {
							var remindNotifyType = '';
							if (item.startReminder == true) {
								remindNotifyType = "Start Date";
							} else {
								remindNotifyType = "End Date";
							}
							table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder before ' + remindNotifyType + ' :</b>'
									+ item.reminderDate + '</p>' + '</div><div class="col-md-2"><a href="" class="deleteReminder' + (status === 'SUSPENDED' && item.startReminder == true ? ' disabled' : '') + '"';
						}
						if (typeof attrReminder !== typeof undefined && attrReminder !== false) {
							table += ' data-remtype="' + attrReminder + '"';
						}
						table += ' reminderId=""><i class="fa fa-times-circle"></i></a></div></div>';
					});
					if (table == '') {
						table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
					}
					$('#confirmDeleteReminder').modal('hide');
					if (typeof attrReminder !== typeof undefined && attrReminder !== false) {
						var currentBlock = $('.rfaIdAddReminder[data-dtype="' + attrReminder + '"]');
						currentBlock.closest('.row').next().find('.reminderList.marginDisable').html(table);
					} else {
						$('.reminderList.marginDisable').html(table);
					}
				},
				error : function(request, textStatus, errorThrown) {
					console.log(request);
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		});

$('.addTeamMemberToList').click(function(e) {
	// alert();
	e.preventDefault();
	var currentBlock = $(this);

	var memberType = $(this).closest('div').find('.access_check:checkbox:checked').val(); // $('.access_check:checkbox:checked').val();//
	// Viewer
	// currentBlock.attr('list-type');
	// console.log(memberType);
	if (memberType == undefined || memberType == "") {
		memberType = "Viewer";
	}
	// $('.access_check').prop('checked', false);
	var userId = $("#TeamMemberList").val();// currentBlock.parent().prev().find('select').val();
	var eventId = $('.event_form').find('#id').val();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	if (userId == "") {
		// $('p[id=idGlobalErrorMessage]').html("Please Select User");
		// $('div[id=idGlobalError]').show();
		$("#editor-err").removeClass("hide ");
		return;
	}
	$("#editor-err").addClass("hide");
	$.ajax({
		type : "POST",
		url : getBuyerContextPath('addTeamMemberToList'),
		data : {
			memberType : memberType,
			userId : userId,
			eventId : eventId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			$('#loading').hide();
		},
		success : function(data) {
			userList = userListForEvent(data);

			$('#appUsersList').html("");
			$('#appUsersList').html(userList);
			$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
			$('#TeamMemberList').trigger("chosen:updated");
			$('#loading').hide();
			if ($('#eventTeamMembersList').length > 0) {
				$('#eventTeamMembersList').DataTable().ajax.reload();
			}
			updateUserList('', $("#TeamMemberList"), 'NORMAL_USER');
		},
		error : function(request, textStatus, errorThrown) {

			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();

			$.jGrowl(request.getResponseHeader('error'), {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
		},
		complete : function() {
			$('#loading').hide();
//			window.location.href = window.location.href;
		}
	});

});

// remove approvers list
$(document).delegate('.removeApproversList', 'click', function(e) {

	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');

	var listUserId = currentBlock.closest('tr').attr('approver-id');
	console.log(listUserId);
	var userName = currentBlock.closest('tr').attr('data-username');
	$("#removeApproverListPopup").dialog({
		modal : true,
		maxWidth : 400,
		minHeight : 100,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});

	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$("#removeApproverListPopup").find('.approverInfoBlock').find('span:first-child').text(userName);
	$("#removeApproverListPopup").find('.approverInfoBlock').find('span:last-child').text(listType);
	$("#removeApproverListPopup").find('#approverListId').val(listUserId);
	$("#removeApproverListPopup").find('#approverListType').val(listType);
	/* $('.ui-dialog-title').text('Remove ' + listType); */
});

$(document).delegate('.removeApproverListPerson', 'click', function(e) {
	var userId = $("#removeApproverListPopup").find('#approverListId').val();
	var listType = $("#removeApproverListPopup").find('#approverListType').val();
	var eventId = $('.event_form').find('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type : "POST",
		url : getBuyerContextPath('removeTeamMemberfromList'),
		data : {
			eventId : eventId,
			listType : listType,
			userId : userId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			userList = approverListForEventDropdown(data);
			// $('.row[user-id="'+userId+'"]').closest('.width100.usersListTable').html(userList);
			// console.log()
			$('#appUsersList tr[approver-id="' + userId + '"]').remove();
			$('#TeamMemberList').html(userList).trigger("chosen:updated");
			// $('#TeamMemberList').html(userList).trigger("chosen:updated");

			// if ($('.row[approver-id="' + userId + '"]').siblings().length ==
			// 0) {
			// userList = approverListForEvent('', listType);
			// $('.row[approver-id="' + userId +
			// '"]').closest('.width100.usersListTable').html(userList);
			// }

			// $('.row[approver-id="' + userId + '"]').remove();
			$("#removeApproverListPopup").dialog('close');
			// reorderApproverList();
			if ($('#eventTeamMembersList').length > 0) {
				$('#eventTeamMembersList').DataTable().ajax.reload();
			}
			updateUserList('', $("#TeamMemberList"), 'NORMAL_USER');
		},
		error : function(e) {
			console.log("Error");
		},
		complete : function() {
			$('#loading').hide();
//			window.location.href = window.location.href;
		}
	});
});

$(document).delegate('.upbutton', 'click', function(e) {
	e.preventDefault();
	var hook = $(this).closest('.row').prev('.row');
	if (hook.length) {
		var elementToMove = $(this).closest('.row').detach();
		hook.before(elementToMove);
	}
	reorderApproverList();
});

$(document).delegate('.downbutton', 'click', function(e) {
	e.preventDefault();
	var hook = $(this).closest('.row').next('.row');
	if (hook.length) {
		var elementToMove = $(this).closest('.row').detach();
		hook.after(elementToMove);
	}
	reorderApproverList();
});

function reorderApproverList() {
	var approverList = [];
	$('.approverList.remarksBlock.usersListTable .row').each(function() {
		approverList.push($(this).attr('approver-id'));
	});
	var eventId = $('.event_form').find('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var listType = 'Approver';
	$.ajax({
		type : "POST",
		url : getBuyerContextPath('reorderApprovers'),
		data : {
			eventId : eventId,
			listType : listType,
			approverList : approverList
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			// xhr.setRequestHeader("Content-Type", "application/json");
		},
		success : function(data) {
			userList = approverListForEvent(data, listType);
			$('.approverList.remarksBlock.usersListTable').html(userList);
		},
		error : function(e) {
			console.log("Error");
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

function userListForEventDropdown(data, listType) {
	return "";
	var userList = '<option value="">Select Editor</option>';
	$.each(data, function(i, user) {
		userList += '<option value="' + user.id + '">' + user.name + '</option>';
	});
	return userList;
}

function userListForEvent(data) {
	var userList = '';

	$.each(data, function(i, user) {

		// console.log(user);
		// userList += '<tr data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
		// userList += '<td class="width_50_fix "></td>';
		// userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
		// userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
		// if (user.teamMemberType == "Editor")
		// userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon glyphicon-pencil " aria-hidden="true
		// "></i> </a>';
		// else if (user.teamMemberType == "Viewer")
		// userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true
		// "></i> </a>';
		// else if (user.teamMemberType == "Associate_Owner")
		// userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-user-plus" aria-hidden="true "></i> </a>';
		// userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
		// if (user.teamMemberType == "Editor")
		// userList += ' checked="checked" ';
		// userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Editor" type="checkbox"
		// value="Editor">&nbsp;Editor</a> </li>';
		// userList += '<li><a href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' +
		// user.user.id + '-Viewer" ';
		//		
		// if (user.teamMemberType == "Associate_Owner")
		// userList += ' checked="checked" ';
		// userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" type="checkbox"
		// value="Associate_Owner">&nbsp;Associate_Owner</a> </li>';
		// userList += '<li><a href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' +
		// user.user.id + '-Associate_Owner" ';
		//		
		// if (user.teamMemberType == "Viewer")
		// userList += ' checked="checked" ';
		// userList += 'type="checkbox" value="Viewer">&nbsp;Viewer</a> </li>';
		// userList += '</ul></li></ul></div></div></td><td>'
		// userList += '<div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList">Delete</a> </div></td></tr>'
		//
		//			

		userList += '<tr  data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
		userList += '<td class="width_50_fix "></td>';
		userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
		userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
		if (user.teamMemberType == "Editor")
			userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Editor"> <i class="glyphicon glyphicon-pencil " aria-hidden="true"></i> </a>';
		else if (user.teamMemberType == "Viewer")
			userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Viewer"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true"></i> </a>';
		else if (user.teamMemberType == "Associate_Owner") userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Associate Owner"> <i class="fa fa-user-plus" aria-hidden="true"></i> </a>';
		userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
		if (user.teamMemberType == "Editor") userList += ' checked="checked"  ';
		userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Editor" type="checkbox" value="Editor">&nbsp;Editor</a> </li>';
		userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" ';
		if (user.teamMemberType == "Viewer") userList += ' checked="checked"  ';
		userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" type="checkbox" value="Viewer">&nbsp;Viewer</a> </li>';
		userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" ';
		if (user.teamMemberType == "Associate_Owner") userList += ' checked="checked"  ';
		userList += 'type="checkbox" value="Associate_Owner">&nbsp;Associate Owner</a> </li>';
		userList += '</ul></li></ul></div></div></td><td>'
		userList += '<div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList"  title="Delete"></a> </div></td></tr>'

	});
	if (userList == '') {
		// userList += '<div class="row" user-id=""><div
		// class="col-md-12"><p>Add ' + listType + '</p></div></div>';
	}
	return userList;
}

/*
 * function viewerListForEventDropdown(data, listType) { var userList = '<option value="">Select Viewer</option>'; $.each(data, function(i, user) {
 * userList += '<option value="' + user.id + '">' + user.name + '</option>'; }); return userList; }
 * 
 * function viewerListForEvent(data, listType) { var userList = ''; $.each(data, function(i, user) { userList += '<div class="row" viewer-id="' +
 * user.id + '">'; if (listType == 'Approver') { userList += '<div class="col-md-1 pad0"><a href="" class="upbutton"><i class="fa fa-level-up"
 * aria-hidden="true"></i></a>'; userList += '<a href="" class="downbutton"><i class="fa fa-level-down" aria-hidden="true"></i></a></div>';
 * userList += '<div class="col-md-9 pad0"><p>' + user.name + '</p></div>'; } else { userList += '<div class="col-md-10"><p>' + user.name + '</p></div>'; }
 * userList += '<div class="col-md-2">'; userList += '<a href="" class="removeViewersList" list-type="' + listType + '"><i class="fa
 * fa-times-circle"></i></a></div></div>'; }); if (userList == '') { userList += '<div class="row" viewer-id=""><div class="col-md-12"><p>Add ' +
 * listType + '</p></div></div>'; } return userList; }
 */
function approverListForEventDropdown(data, listType) {

	var userList = '<option value="">Select Team Member</option>';
	$.each(data, function(i, user) {
		userList += '<option value="' + user.id + '">' + user.name + '</option>';
	});
	return userList;
}

function approverListForEvent(data, listType) {
	var userList = '';
	$.each(data, function(i, user) {
		userList += '<div class="row" approver-id="' + user.id + '">';
		if (listType == 'Approver') {
			userList += '<div class="col-md-1 pad0"><a href=""  data-placement="right" data-toggle="tooltip" data-original-title="Up" class="upbutton"><i class="glyph-icon icon-caret-up" aria-hidden="true"></i></a></div>';
			userList += '<div class="col-md-8 pad0"><p>' + user.name + '</p></div>';
			userList += '<div class="col-md-1 pad0"><a href="" data-placement="bottom" data-toggle="tooltip" data-original-title="Down"  class="downbutton"><i class="glyph-icon icon-caret-down" aria-hidden="true"></i></a></div>';
		} else {
			userList += '<div class="col-md-10"><p>' + user.name + '</p></div>';
		}
		userList += '<div class="col-md-1">';
		userList += '<a href="" class="removeApproversList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div></div>';
	});
	if (userList == '') {
		userList += '<div class="row" approver-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>';
	}
	return userList;
}

function disableWizard(publicPrivate) {
	if (publicPrivate == 'PUBLIC') {
		$('#form-wizard-2 .tb_3').hide();
	} else {
		$('#form-wizard-2 .tb_3').show();
	}
	renumberWizard();
}

function eventDescriptionWizard(radioName, status) {
	var dataItems = [];
	if (radioName == 'documentReq') {
		dataItems = [ '2' ];
	} else if (radioName == 'meetingReq') {
		dataItems = [ '4' ];
	} else if (radioName == 'questionnaires') {
		dataItems = [ '5' ];
	} else if (radioName == 'billOfQuantity') {
		dataItems = [ '6' ];
	} else if(radioName == 'scheduleOfRate') {
		dataItems = [ '7' ];
	}
	$.each(dataItems, function(i, item) {
		if (status == 1) {
			$('#form-wizard-2').find('li:nth-child(' + item + ')').show();
		} else {
			$('#form-wizard-2').find('li:nth-child(' + item + ')').hide();
		}
	});
	renumberWizard();
}

function vertiCaleALignDeleteAddress() {
	var marginTop = ($('.phisicalArressBlock').height() - $('#deleteDeliveryAddress').height()) / 2;
	$('#deleteDeliveryAddress').css('margin-top', marginTop);
}
jQuery(document).ready(function() {

	jQuery(".auction-spt-radio").change(function() {
		jQuery(".ac-time-togle").toggle();

	});

	$('#idAutoDisqualify').click(function() {
		if ($(this).is(':checked')) {
			$(".showHideTextBox").show();
		} else {
			$(".showHideTextBox").hide();
		}
	});
	idTypeExtenSion();
	// for hide div on select on timeExtensionType
	$('#idTypeExtension').on('change', function() {
		idTypeExtenSion();
	});

});

function idTypeExtenSion() {
	if ($('#idTypeExtension').val() == 'AUTOMATIC') {
		$(".hideDiv").show();
	} else {
		$(".hideDiv").hide();
	}
}
// manage event visibility

$('.visibilitySearch').change(function() {
	var status = $('#demo-form1').find('#status').val();
	/*
	 * if (status != undefined && status != '' && status === 'DRAFT') { $("#demo-form1").ajaxSubmit({ url : getBuyerContextPath('autoSaveDraft'), type :
	 * 'post' }) }
	 */
	var eventVisbility = $('.visibilitySearch').val();
	var eventId = $("#id").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getBuyerContextPath('manageEventVisibility');
	var visibilityData = {
		'eventId' : eventId,
		'eventVisbility' : eventVisbility,
	};
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		data : visibilityData,
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data, textStatus, request) {
			var info = request.getResponseHeader('success');
			$('select[name="' + eventVisbility + '"][value="' + eventVisbility + '"]').prop('selected', false).parent().removeClass('selected');
			$('select[name="' + eventVisbility + '"][value="' + eventVisbility + '"]').prop('selected', true).parent().addClass('selected');
			$('#loading').hide();

		},
		error : function(request, textStatus, errorThrown) {
			console.log(request.responseText);

			if ($("#idEventVisibility").val() == "PUBLIC") {
				$("#idEventVisibility").val("PRIVATE");

			}/*
				 * else { $("#idEventVisibility").val("PUBLIC"); }
				 */
			$("#idEventVisibility").trigger("chosen:updated");

			if (request.responseText > 0) {
				$('#myModalDeleteRelatedItems1').find('#selectedrelated').val(eventVisbility);
				$('#myModalDeleteRelatedItems1').modal('show');
			}/*
				 * else { $('input[type="radio"][name="' + radioname + '"][value="1"]').prop('checked', false).parent().removeClass('chosen');
				 * $('input[type="radio"][name="' + radioname + '"][value="0"]').prop('checked', true).parent().addClass('chosen'); }
				 */
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});
$(document).on("click", "#idConfirmDeleteRelatedItems1", function(e) {
	var eventVisbility = $('#myModalDeleteRelatedItems1').find('#selectedrelated').val();
	$('#myModalDeleteRelatedItems1').modal('hide');
	// $('input[type="radio"][name="' + radioname +
	// '"][value="1"]').prop('checked', false).parent().removeClass('checked');
	// $('input[type="radio"][name="' + radioname +
	// '"][value="0"]').prop('checked', true).parent().addClass('checked');
	if (eventVisbility == "PUBLIC") {
		$("#idEventVisibility").val("PUBLIC");
		$("#idEventVisibility").trigger("chosen:updated");
	}
	eventDescriptionWizard(eventVisbility, $('select[name="' + eventVisbility + '"]:selected').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getBuyerContextPath('deleteEventVisibilityMeetings'),
		data : {
			'eventVisbility' : eventVisbility,
			'eventId' : $('#id').val()
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			console.log(data);
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

// summary edit Approval pop up
$(document).delegate('.editApprovalPopupButton', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$("#editApprovalPopup").dialog({
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
	$('#idReminderSettings').hide();
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	// $('.ui-dialog-title').text('Add Contact Person');
	// $('#addEditContactPopup').find('a.addContactPerson').text('Add Contact
	// Person');
});

function onchangeDateOrTime(dataDtype) {

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#addReminder').isValid()) {
		return false;
	}
	var attrReminder = dataDtype;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var currentBlock = $('.rfaIdAddReminder[data-dtype="' + attrReminder + '"]');
	var reminderDate = $.trim(currentBlock.closest('.row').find('.for-clander-view').val());
	var reminderTime = $.trim(currentBlock.closest('.row').find('.for-timepicker-view').val());
	var reminderId = $.trim($('#reminderId').val());
	var reminderData = {
		'eventDate' : reminderDate,
		'eventTime' : reminderTime,
		'reminderType' : attrReminder,
		'eventId' : $('#id').val(),
		'reminderId' : reminderId
	};
	if (!reminderTime || !reminderDate) {
		return false;
	}
	$.ajax({
		url : getBuyerContextPath('manageReminderOnStartEndDateChange'),
		data : reminderData,
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var table = '';
			$.each(data, function(i, item) {
				table += '<div class="row" id="' + item.id + '">' + '<input type="hidden" name="reminderDate" value="' + item.reminderDate + '">' + '<div class="col-md-10"><p><b>Reminder Date:</b>' + item.reminderDate + '</p>'
						+ '</div><div class="col-md-2"><a href="" class="deleteReminder" data-remtype="' + attrReminder + '" reminderId=""><i class="fa fa-times-circle"></i></a></div></div>';
			});
			if (table == '') {
				table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
			}
			currentBlock.closest('.row').next().find('.reminderList.marginDisable').html(table);
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
			var error = request.getResponseHeader('error');
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

$('.startDateAuction, .endDateAuction').datepicker().on('changeDate', function(e) {
	e.preventDefault();
	onchangeDateOrTime($(this).attr('data-dtype'));
});

/*
 * $(function() { "use strict";
 * 
 * $('#deliveryDate').bsdatepicker({ format : 'dd/mm/yyyy', onRender : function(date) { if(date.valueOf() < $.now()){ return 'disabled' ; } }
 * 
 * }).on('changeDate', function(e) { $(this).blur(); $(this).bsdatepicker('hide'); });
 * 
 * });
 */

var nowTemp = new Date();
var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

$('#deliveryDate').bsdatepicker({
	format : 'dd/mm/yyyy',
	minDate : now,
	onRender : function(date) {
		return date.valueOf() < now.valueOf() ? 'disabled' : '';
	}

}).on('changeDate', function(e) {

	/*
	 * var status = $('#demo-form1').find('#status').val(); if (status != undefined && status != '' && status === 'DRAFT') {
	 * $("#demo-form1").ajaxSubmit({ url : getBuyerContextPath('autoSaveDraft'), type : 'post' }) }
	 */
	$(this).blur();
	$(this).bsdatepicker('hide');
});

$('.timepicker-example').timepicker({
	disableFocus : true,
	explicitMode : false
}).on('hide.timepicker', function(e) {
	e.preventDefault();
	$(this).blur();
});

if ($('.endTimeAuction,.startTimeAuction').length) {
	$('.endTimeAuction,.startTimeAuction').timepicker({
		disableFocus : true,
		explicitMode : false
	}).on('hide.timepicker', function(e) {
		e.preventDefault();
		$(this).blur();
		onchangeDateOrTime($(this).attr('data-dtype'));
	});
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

// summary edit Suspension Approval pop up
$(document).delegate('.editSuspApprvPopupButton', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$("#editSuspApprvlPopup").dialog({
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
	$('#idReminderSettings').hide();
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
});