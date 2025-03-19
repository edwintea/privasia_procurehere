var browserOnline = true;


function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
};

$(document).ready(function() {

	window.addEventListener('offline', function() {
		console.log('Browser offline...');
		browserOnline = false;
	});
	window.addEventListener('online', function() {
		console.log('Browser back online....');
		browserOnline = true;
	});

	/*
	 * alert($(window).height()); alert($('header').height()); alert($('#footer').height());
	 */
	body_sizer();
	$('[data-toggle="tooltip"]').tooltip();
	$("div[id='#fixed-sidebar']").on('click', function() {

		if ($(this).hasClass("switch-on")) {
			var windowHeight = $(window).height();
			var headerHeight = $('#page-header').height();
			var contentHeight = windowHeight - headerHeight;

			$('#page-sidebar').css('height', contentHeight);
			$('.scroll-sidebar').css('height', contentHeight);

			$('.scroll-sidebar').slimscroll({
				height : '100%',
				color : 'rgba(155, 164, 169, 0.68)',
				size : '6px'
			});

			var headerBg = $('#page-header').attr('class');
			$('#header-logo').addClass(headerBg);

		} else {
			var windowHeight = $(document).height();
			var headerHeight = $('#page-header').height();
			var contentHeight = windowHeight - headerHeight;

			$('#page-sidebar').css('height', contentHeight);
			$('.scroll-sidebar').css('height', contentHeight);

			$(".scroll-sidebar").slimScroll({
				destroy : true
			});

			$('#header-logo').removeClass('bg-gradient-9');

		}

	});

});

$(window).on('resize', function() {
	body_sizer();
});

function jgrowlActive(messege, mtype, life, sticky) {
	var theme = 'bg-blue-alt';
	var header = '';
	if (mtype.toLowerCase() == 'e') {
		theme = 'bg-red';
		header = '<i class="glyph-icon icon-times"></i> Error';
	} else if (mtype.toLowerCase() == 's') {
		theme = 'bg-green';
		header = '<i class="glyph-icon icon-check"></i> Success';
	} else if (mtype.toLowerCase() == 'i') {
		theme = 'bg-blue-alt';
		header = '<i class="glyph-icon icon-info"></i> Info';
	}
	$.jGrowl(messege, {
		sticky : sticky,
		header : header,
		life : life,
		position : 'top-right',
		theme : theme
	});
}

function showMessage(mtype, message) {
	var theme = 'bg-blue-alt';
	var header = '';
	if (mtype.toLowerCase() == 'error') {
		theme = 'bg-red';
		header = '<i class="glyph-icon icon-times"></i> ERROR';
	} else if (mtype.toLowerCase() == 'success') {
		theme = 'bg-green';
		header = '<i class="glyph-icon icon-check"></i> SUCCESS';
	} else if (mtype.toLowerCase() == 'info') {
		theme = 'bg-blue-alt';
		header = '<i class="glyph-icon icon-info"></i> INFO';
	} else if (mtype.toLowerCase() == 'warn') {
		theme = 'bg-yellow-alt';
		header = '<i class="glyph-icon icon-warning"></i> WARN';
	}
	$.jGrowl(message, {
		sticky : false,
		header : header,
		life : 5000,
		position : 'top-right',
		theme : theme
	});
}

function body_sizer() {
	var windowHeight = $(window).height();
	var docHeight = $(document).height();
	var headerHeight = $('header').height();
	var contentHeight = windowHeight - headerHeight - 51;
	/*
	 * alert(windowHeight); alert(headerHeight); alert(contentHeight); alert(docHeight);
	 */
	var siderBarHgt = docHeight - headerHeight - 51;
	if (siderBarHgt < contentHeight) {
		siderBarHgt = contentHeight;
	}
	$('#page-content').css('min-height', contentHeight);
	// $('.scroll-sidebar').css({'max-height':siderBarHgt,'overflow':'auto'});
	/* $('.scroll-sidebar').css({'max-height':siderBarHgt}); */
	var tmpW = $(window).width();// patch for small to large navigation

	if (tmpW >= "768") {
		$("#page-sidebar").removeClass("collapse");
	}

	// console.log();
	/*
	 * if ($('body').hasClass('fixed-sidebar')) {
	 * 
	 * var windowHeight = $(window).height(); //var headerHeight = $('#page-header').height();" var headerHeight = $('header').height(); var
	 * contentHeight = windowHeight - headerHeight; alert(windowHeight); alert(headerHeight); alert(contentHeight);
	 * 
	 * //$('#page-sidebar').css('height', contentHeight); //$('.scroll-sidebar').css('height', contentHeight); $('#page-content').css('min-height',
	 * contentHeight); } else {
	 * 
	 * var windowHeight = $(document).height(); //var headerHeight = $('#page-header').height(); var headerHeight = $('header').height(); var
	 * contentHeight = windowHeight - headerHeight; // $('#page-sidebar').css('height', contentHeight); // $('.scroll-sidebar').css('height',
	 * contentHeight); $('#page-content').css('min-height', contentHeight); }
	 */

};

function pageTransitions() {

	var transitions = ['.pt-page-moveFromLeft', 'pt-page-moveFromRight', 'pt-page-moveFromTop', 'pt-page-moveFromBottom', 'pt-page-fade', 'pt-page-moveFromLeftFade', 'pt-page-moveFromRightFade', 'pt-page-moveFromTopFade',
		'pt-page-moveFromBottomFade', 'pt-page-scaleUp', 'pt-page-scaleUpCenter', 'pt-page-flipInLeft', 'pt-page-flipInRight', 'pt-page-flipInBottom', 'pt-page-flipInTop', 'pt-page-rotatePullRight', 'pt-page-rotatePullLeft',
		'pt-page-rotatePullTop', 'pt-page-rotatePullBottom', 'pt-page-rotateUnfoldLeft', 'pt-page-rotateUnfoldRight', 'pt-page-rotateUnfoldTop', 'pt-page-rotateUnfoldBottom'];
	for (var i in transitions) {
		var transition_name = transitions[i];
		if ($('.add-transition').hasClass(transition_name)) {

			$('.add-transition').addClass(transition_name + '-init page-transition');

			setTimeout(function() {
				$('.add-transition').removeClass(transition_name + ' ' + transition_name + '-init page-transition');
			}, 1200);
			return;
		}
	}

};

$(document).ready(function() {

	pageTransitions();

	// ADD SLIDEDOWN ANIMATION TO DROPDOWN //
	$('.dropdown').on('show.bs.dropdown', function(e) {
		$(this).find('.dropdown-menu').first().stop(true, true).slideDown();
	});

	// ADD SLIDEUP ANIMATION TO DROPDOWN //
	$('.dropdown').on('hide.bs.dropdown', function(e) {
		$(this).find('.dropdown-menu').first().stop(true, true).slideUp();
	});

	/* Sidebar menu */
	$(function() {

		$('#sidebar-menu').superclick({
			animation: {
				height: 'show'
			},
			animationOut: {
				height: 'hide'
			},
			speedOut: '600'
		});

		// automatically open the current path
		var path = window.location.pathname.split('/');
		path = path[path.length - 1];
		// console.log(path);
		if (path !== undefined) {
			$("#sidebar-menu").find("a[href$='" + path + "']").addClass('sfActive');
			$("#sidebar-menu").find("a[href$='" + path + "']").parents().eq(3).superclick('show');
		}
		var viewName = $('#page-content').attr('view-name');
		if (viewName !== undefined) {
			$("#sidebar-menu").find("li[view-name='" + viewName + "']").children('a').addClass('sfActive');
			$("#sidebar-menu").find("li[view-name='" + viewName + "']").children('a').parents().eq(3).superclick('show');
		}

		if (localStorage.getItem("isOpneSidebar") != undefined && localStorage.getItem("isOpneSidebar") == 'true') {
			$('body').toggleClass('closed-sidebar');
			$('.glyph-icon', '#close-sidebar').toggleClass('icon-angle-left').toggleClass('icon-angle-right').toggleClass('user_mail1');
			$('.user_box .user-pic img').toggleClass('profile-img-minimize');
			var act = $('.glyph-icon').hasClass("icon-angle-right");
			if (act) {
				$('#close-sidebar').attr('title', "Open sidebar");
				$("#sidebar-menu").find("li").children('a').removeClass('sfActive');
				$("#sidebar-menu").find('.sidebar-submenu').hide();
			} else {
				$('#close-sidebar').attr('title', "Close sidebar");
			}
		}

	});

	/* Colapse sidebar */
	$(function() {

		$('#close-sidebar').click(function() {
			$('body').toggleClass('closed-sidebar');
			$('.glyph-icon', this).toggleClass('icon-angle-left').toggleClass('icon-angle-right').toggleClass('user_mail1');
			$('.user_box .user-pic img').toggleClass('profile-img-minimize');
			var act = $('.glyph-icon').hasClass("icon-angle-right");
			if (act) {
				localStorage.setItem("isOpneSidebar", 'true');
				$(this).attr('title', "Open sidebar");
				$("#sidebar-menu").find("li").children('a').removeClass('sfActive');
			} else {
				localStorage.setItem("isOpneSidebar", 'false');
				$(this).attr('title', "Close sidebar");
				var path = window.location.pathname.split('/');
				path = path[path.length - 1];
				// console.log(path);
				if (path !== undefined) {
					$("#sidebar-menu").find("a[href$='" + path + "']").addClass('sfActive');
					$("#sidebar-menu").find("a[href$='" + path + "']").parents().eq(3).superclick('show');
				}
				var viewName = $('#page-content').attr('view-name');
				if (viewName !== undefined) {
					$("#sidebar-menu").find("li[view-name='" + viewName + "']").children('a').addClass('sfActive');
					$("#sidebar-menu").find("li[view-name='" + viewName + "']").children('a').parents().eq(3).superclick('show');
				}
			}

		});

		$("#dashnav-btn").click(function(event) {
			event.preventDefault();
			$(".mobile_search_block").slideDown();
		});

		$(".mobile_search_block .close").click(function(event) {
			event.preventDefault();
			$(".mobile_search_block").slideUp();
		});

		$('a.sf-with-ul').hover(function() {
			$(this).find('.sidebar-submenu').show();
		}, function() {
			$(this).find('.sidebar-submenu').hide();
		});

	});

	/* Sidebar scroll */

	$('.scrollable-slim-box').perfectScrollbar();
	$('.event_box,.overview_box .box_top').matchHeight();

	$('.ph_tabs a').click(function(e) {
		e.preventDefault()
		$(this).tab('show')
	});

	/*
	 * // Binding next button on first step $(".open1").click(function (event) { event.preventDefault(); $(".tab-pane.active , .form-wizard
	 * .active").removeClass("active"); $("#step-1 , .tb_1").addClass("active");
	 * 
	 * }); // Binding next button on second step $(".open2").click(function (event) { event.preventDefault(); $(".tab-pane.active , .form-wizard
	 * .active").removeClass("active"); $("#step-2 , .tb_2").addClass("active");
	 * 
	 * }); // Binding back button on second step $(".open3").click(function (event) { event.preventDefault(); $(".tab-pane.active , .form-wizard
	 * .active").removeClass("active"); $("#step-3 , .tb_3").addClass("active"); }); // Binding back button on third step $(".open4").click(function
	 * (event) { event.preventDefault(); $(".tab-pane.active , .form-wizard .active").removeClass("active"); $("#step-4 , .tb_4").addClass("active");
	 * });
	 * 
	 * $(".open5").click(function (event) { event.preventDefault(); $(".tab-pane.active , .form-wizard .active").removeClass("active"); $("#step-5 ,
	 * .tb_5").addClass("active"); });
	 * 
	 * 
	 * $('[data-toggle="tooltip"]').tooltip();
	 * 
	 * $('.scroll_box_inner, .last_step_table').perfectScrollbar();
	 * 
	 * 
	 * 
	 * $('.scrollable-slim-box_vertical').perfectScrollbar({ suppressScrollX : true });
	 * 
	 * $('.scrollable-slim-box_horizontal').perfectScrollbar({ suppressScrollY : true });
	 */

	$(document).on('click', '.s1_view_desc', function(event) {
		event.preventDefault();
		$(this).closest('.item_detail').next().slideToggle();
		$(this).text(function(i, text) {
			return (text.trim()) === "View Description" ? "Close Description" : "View Description";
		})
	});

	$(document).on('click', '.s2_view_desc', function(event) {
		event.preventDefault();
		$(this).closest('tr').next('tr').slideToggle();
		$(this).text(function(i, text) {
			return (text.trim()) === "View Description" ? "Close Description" : "View Description";
		})
	});

	$(document).on('click', '.s3_view_desc', function(event) {
		event.preventDefault();
		$(this).text(function(i, text) {
			return (text.trim()) === "View Description" ? "Close Description" : "View Description";
		})
	});

	$(window).on("load resize", function(e) {
		getTableWidth();
		getCustomeTableWidth();
	});

	/* =============== Envelop Drag Drop Question ========= */

});

function getTableWidth() {
	var phTable = $('.ph_table_border .ph_table');
	var width = phTable.outerWidth();
	var mainTable = $('.main_table_wrapper');
	mainTable.find('.table_title_wrapper , .table_data_wrapper , .table_data_footer').css('width', width);
}

function getCustomeTableWidth() {
	var cusTable2 = $('.sa_enlp_table_block .table');
	var width = cusTable2.outerWidth();
	var mainTable = $('.sa_enlp_table_block');
	mainTable.find('.sa_cutom_tbl_warpWidth').css('width', width);
}

// function disableFormFields(allowedFields = '', disableAnchers = '') { replaced 12/7 ie old raise error and function call only one in app with
// single param
function disableFormFields(allowedFields) {

	if (typeof allowedFields == undefined || $.trim(allowedFields) == '') {
		$('#page-content').find('input[type!="hidden"], button, select, textarea').attr('readonly', 'readonly').addClass('disabled');
		$('#page-content').find('.fileinput').addClass('disabled');
		$('#page-content').find('select.chosen-select').css('opacity', '0').siblings('div.chosen-container').addClass('disabled');
		$('#page-content').find('select.chosen-select').parent('div').addClass('disabled');
		$('#page-content').find('input.bootstrap-datepicker').parent('div').addClass('disabled');
		$('#page-content').find('a').addClass('disabled');
		$('#page-content').find('.for-clander-view').parent('div').addClass('disabled');
		$('#page-content').find('ol.sortable').removeClass('sortable');
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').parents('span, div.checker, label').attr('readonly', 'readonly').addClass('disabled');
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').click(function(event) {
			event.stopPropagation();
			event.preventDefault();
			return false;
		});
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').change(function(event) {
			event.stopPropagation();
			event.preventDefault();
			return false;
		});
		$.uniform.update(':not(.remindMeTime)');
	} else {
		$('#page-content').find('input[type!="hidden"], button, select, textarea').not(allowedFields).attr('readonly', 'readonly').addClass('disabled');
		$('#page-content').find('.fileinput').not(allowedFields).addClass('disabled');
		$('#page-content').find('select.chosen-select').not(allowedFields).css('opacity', '0').siblings('div.chosen-container').addClass('disabled');
		$('#page-content').find('select.chosen-select').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('select.chosen-select').not(allowedFields).parent('span').addClass('disabled');
		$('#page-content').find('input.bootstrap-datepicker').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('.for-clander-view').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('input.bootstrap-timepicker').not(allowedFields).parent('div').addClass('disabled');
		$('#page-content').find('ol.sortable').not(allowedFields).removeClass('sortable');
		$('#page-content').find('a').not(allowedFields).addClass('disabled');
		$('#page-content').find('a').not(allowedFields).attr("href", "#");
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').not(allowedFields).parents('span, div.checker, label').attr('readonly', 'readonly').addClass('disabled');
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').not(allowedFields).attr('readonly', 'readonly');
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').not(allowedFields).click(function(event) {
			event.stopPropagation();
			event.preventDefault();
			return false;
		});
		$('#page-content').find('input[type="checkbox"], input[type="radio"]').change(function(event) {
			event.stopPropagation();
			event.preventDefault();
			return false;
		});

		$.uniform.update(':not(.remindMeTime)');
	}
}
$(document).ready(function(e) {
	$('.searchRadio').click(function(e) {
		// e.preventDefault();
		$('.search-panel span#search_concept').text($(this).data("text"));
	});
	$('.search_mob_disply.dropdown > a').click(function(event) {
		event.preventDefault();
		$('.search_web_disply').slideToggle().toggleClass('active');
		$('.search_mob_disply').toggleClass('active');
	});
	$(document).on('click', function(event) {
		if ($(event.target).closest('.search_web_disply').length == 0 && $(event.target).closest('.search_mob_disply').length == 0 && $('.search_web_disply').hasClass('active')) {
			$('.search_web_disply').slideUp().removeClass('active');
			$('.search_mob_disply').removeClass('active');
		}
	});
	/*
	 * $(".Invited-Supplier-List").click(function () { $("#timer-accord").toggleClass("small-accordin-tab"); });
	 */

    $(document).delegate('.user-list-approval.chosen-select', "chosen:hiding_dropdown", function(e) {
        updateUserForPoApprovalList('', $(this), 'ALL_USER');
    });

	$(document).delegate('.user-list-all.chosen-select', "chosen:hiding_dropdown", function(e) {
		updateUserList('', $(this), 'ALL_USER');
	});

	$(document).delegate('.user-list-normal.chosen-select', "chosen:hiding_dropdown", function(e) {
		updateUserList('', $(this), 'NORMAL_USER');
	});

	$(document).delegate('.user-list-normal-rfx-template.chosen-select', "chosen:hiding_dropdown", function(e) {
		updateRfxTemplateUserList('', $(this), 'NORMAL_USER', $('#templateId').val());
	});

	$(document).delegate('.user-list-normal-pr-template.chosen-select', "chosen:hiding_dropdown", function(e) {
		prTemplateUpdateUserList('', $(this), 'NORMAL_USER', $('#templateId').val());
	});

	$(document).delegate('.option_list_all.chosen-select', "chosen:hiding_dropdown", function(e) {
	console.log("####################");
		reloadCqItemOptionList('', $(this));
	});
	
	$(document).delegate('.user-list-normal-sourcing-template.chosen-select', "chosen:hiding_dropdown", function(e) {
		sourcingTemplateUpdateUserList('', $(this), 'NORMAL_USER', $('#templateId').val());
	});
		
	$(document).delegate('.user-list-normal-sp-template.chosen-select', "chosen:hiding_dropdown", function(e) {
		spTemplateUpdateUserList('', $(this), 'NORMAL_USER', $('#templateId').val());
	});

	/*
	 * $(document).delegate('.user-list-normal ~ .chosen-container input', "focusout", function(event) { event.preventDefault();
	 * event.stopImmediatePropagation(); var selectElement = $(this).parents().eq(3).closest('div').find('select'); console.log('Focus Out....',
	 * event); // updateUserList('',selectElement, 'ALL_USER'); });
	 */

	$(document).delegate(".user-list-normal ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			updateUserList(userName, selectElement, 'NORMAL_USER');
			// $(this).val(userName);
		}
	}, 500));

	$(document).delegate(".user-list-normal-rfx-template ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			updateRfxTemplateUserList(userName, selectElement, 'NORMAL_USER', $('#templateId').val());
			// $(this).val(userName);
		}
	}, 500));

	$(document).delegate(".user-list-normal-pr-template ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			prTemplateUpdateUserList(userName, selectElement, 'NORMAL_USER', $('#templateId').val());
			// $(this).val(userName); 
		}
	}, 500));


	$(document).delegate(".user-list-all ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			updateUserList(userName, selectElement, 'ALL_USER');
			// $(this).val(userName);
		}
	}, 500));

	$(document).delegate(".user-list-all-approval ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
    		var selectElement = $(this).parents().eq(3).closest('div').find('select');
    		// ignore arrow keys
    		switch (e.keyCode) {
    			case 17: // CTRL
    				return false;
    				break;
    			case 18: // ALT
    				return false;
    				break;
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
    		var userName = $.trim(this.value);

    		if (userName.length > 2 || e.keyCode == 8) {
    			updateUserForPoApprovalList(userName, selectElement, 'ALL_USER');
    			// $(this).val(userName);
    		}
    	}, 500));
	
	$(document).delegate(".option_list_all ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
	console.log("@@@@@@@@@@@@@@@@@@@@@@@@@");
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			reloadCqItemOptionList(userName, selectElement);
			// $(this).val(userName);
		}
	}, 500));
	
		$(document).delegate(".user-list-normal-sourcing-template ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			sourcingTemplateUpdateUserList(userName, selectElement, 'NORMAL_USER', $('#templateId').val());
			// $(this).val(userName); 
		}
	}, 500));
	
	$(document).delegate(".user-list-normal-sp-template ~ .chosen-container input", "keyup", keyDebounceDelay(function(e) {
		var selectElement = $(this).parents().eq(3).closest('div').find('select');
		// ignore arrow keys
		switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
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
		var userName = $.trim(this.value);

		if (userName.length > 2 || e.keyCode == 8) {
			spTemplateUpdateUserList(userName, selectElement, 'NORMAL_USER', $('#templateId').val());
		}
	}, 500));

});

function keyDebounceDelay(callback, ms) {
	var timer = 0;
	return function() {
		var context = this, args = arguments;
		clearTimeout(timer);
		timer = setTimeout(function() {
			callback.apply(context, args);
		}, ms || 0);
	};
}

function updateUserList(userName, selectElement, userType) {
	var data = {
		'search': userName,
	};
	doAjaxCallForUser(selectElement, userType, 'searchUserName', data);
}

function updateUserForPoApprovalList(userName, selectElement, userType) {
	var data = {
		'search': userName,
	};
	doAjaxCallForUserApproval(selectElement, userType, 'searchUserNameForPoApproval', data);
}

function updateRfxTemplateUserList(userName, selectElement, userType, templateId) {
	var data = {
		'search': userName,
		'templateId': templateId
	};
	console.log("Rfx");
	doAjaxCallForUser(selectElement, userType, 'searchUserNameForRfxTemplate', data);
}

function prTemplateUpdateUserList(userName, selectElement, userType, templateId) {
	var data = {
		'search': userName,
		'templateId': templateId
	};
	console.log("PR");
	doAjaxCallForUser(selectElement, userType, 'searchPrTemplateUserName', data);
}

function sourcingTemplateUpdateUserList(userName, selectElement, userType, templateId) {
	var data = {
		'search': userName,
		'templateId': templateId
	};
	console.log("SOURCING Templ");
	doAjaxCallForUser(selectElement, userType, 'searchSourcingTemplateUserName', data);
}

function spTemplateUpdateUserList(userName, selectElement, userType, templateId) {
	var data = {
		'search': userName,
		'templateId': templateId
	};
	console.log("SP Templ");
	doAjaxCallForUser(selectElement, userType, 'searchSPTemplateUserName', data);
}

function doAjaxCallForUser(selectElement, userType, apiName, data) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/admin/' + apiName + '/' + userType,
		data: data,
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';

			if (data != '' && data != null && data.length > 0) {
				if (selectElement.attr('multiple') !== undefined) {
					console.log('Clearing non selected items...');
					selectElement.find('option').not(':selected').remove();
				} else {
					if (selectElement.find('option:first').val() === '') {
						console.log('Clearing all except first...');

						selectElement.find('option').each(function() {
							if (this.value == '' || this.value === selectElement.val()) {
								console.log(' Not Removing ', this);
							} else {
								this.remove();
							}
						});

						// selectElement.find('option:not(:first)').remove();
						// selectElement.find('option').not(':selected').remove();
					} else {
						console.log('Clearing all items...');
						selectElement.find('option').not(':selected').remove();
					}
				}
				$.each(data, function(key, value) {

					var selectedIds = selectElement.attr("selected-id");
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.name + '</option>';
					} else if (value.id == '-1') {
						html += '<option value="-1" disabled>' + value.name + '</option>';
					} else {
						if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
							var found = false;
							if (selectedIds !== undefined) {
								$('[' + selectedIds + ']').each(function(index) {
									if ($(this).attr(selectedIds) === value.id) {
										// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
										found = true;
										return false;
									}
								});
							}
							if (!found) {
								html += '<option value="' + value.id + '" data-name="' + value.name + '">' + value.name + '</option>';
							}
						}
					}
				});
			}

			selectElement.append(html);
			selectElement.trigger("chosen:updated")
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
			$('#loading').hide();
		}
	});


}

function doAjaxCallForUserApproval(selectElement, userType, apiName, data) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	let url=getContextPath() + '/admin/' + apiName + '/ALL_USER';
	console.log(data);

	$.ajax({
		url: getContextPath() + '/admin/' + apiName + '/' + userType,
		data: data,
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';

			if (data != '' && data != null && data.length > 0) {
				if (selectElement.attr('multiple') !== undefined) {
					console.log('Clearing non selected items...');
					selectElement.find('option').not(':selected').remove();
				} else {
					if (selectElement.find('option:first').val() === '') {
						console.log('Clearing all except first...');

						selectElement.find('option').each(function() {
							if (this.value == '' || this.value === selectElement.val()) {
								console.log(' Not Removing ', this);
							} else {
								this.remove();
							}
						});

						// selectElement.find('option:not(:first)').remove();
						// selectElement.find('option').not(':selected').remove();
					} else {
						console.log('Clearing all items...');
						selectElement.find('option').not(':selected').remove();
					}
				}
				$.each(data, function(key, value) {

					var selectedIds = selectElement.attr("selected-id");
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.name + '</option>';
					} else if (value.id == '-1') {
						html += '<option value="-1" disabled>' + value.name + '</option>';
					} else {
						if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
							var found = false;
							if (selectedIds !== undefined) {
								$('[' + selectedIds + ']').each(function(index) {
									if ($(this).attr(selectedIds) === value.id) {
										// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
										found = true;
										return false;
									}
								});
							}
							if (!found) {
								html += '<option value="' + value.id + '" data-name="' + value.name + '">' + value.name + '</option>';
							}
						}
					}
				});
			}

			selectElement.append(html);
			selectElement.trigger("chosen:updated")
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
			$('#loading').hide();
		}
	});


}

function reloadCqItemOptionList(value, selectElement) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var cqItemId = $(selectElement).attr("data-cqItemId");
		console.log("***************************** value "+value);
		console.log("***************************** selectElement "+selectElement);
		console.log("cqItemId ..."+cqItemId);
		
		$.ajax({
			url : getContextPath() + '/buyer/searchCqItemOptionFromList',
			data : {
				'cqItemOption' : value,
				'cqItemId' : cqItemId
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
			var html = '';
			console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> success");
			
			if (data != '' && data != null && data.length > 0) {
				if (selectElement.attr('multiple') !== undefined) {
					console.log('Clearing non selected items...');
					selectElement.find('option').not(':selected').remove();
				} else {
					if (selectElement.find('option:first').val() === '') {
						console.log('Clearing all except first...');

						selectElement.find('option').each(function() {
							if (this.value == '' || this.value === selectElement.val()) {
								console.log(' Not Removing ', this);
							} else {
								this.remove();
							}
						});

						// selectElement.find('option:not(:first)').remove();
						// selectElement.find('option').not(':selected').remove();
					} else {
						console.log('Clearing all items...');
						selectElement.find('option').not(':selected').remove();
					}
				}
				$.each(data, function(key, value) {

					var selectedIds = selectElement.attr("selected-id");
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.value + '</option>';
					} else if (value.id == '-1') {
						html += '<option value="-1" disabled>' + value.value + '</option>';
					} else {
						if (selectElement.find('option[value=' + value.order + ']:selected').val() === undefined) {
							var found = false;
							if (selectedIds !== undefined) {
								$('[' + selectedIds + ']').each(function(index) {
									if ($(this).attr(selectedIds) === value.order) {
										// html += '<option value="' + value.order + '" data-name="'+value.value+'" disabled>' + value.value + '</option>';
										found = true;
										return false;
									}
								});
							}
							if (!found) {
								html += '<option value="' + value.order + '" data-name="' + value.value + '">' + value.value + '</option>';
							}
						}
					}
				});
			}

			selectElement.append(html);
			selectElement.trigger("chosen:updated")
			},
			error : function(error) {
				$('#loading').hide();
				console.log("##########33333######## error");
				console.log(error);
			},
			complete : function(){
				console.log("!!!!!!!!!!!!!!!!!!!!! complete");
				$('#loading').hide();
			}
		});
	}
