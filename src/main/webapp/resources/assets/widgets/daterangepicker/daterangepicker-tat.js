/* Daterangepicker bootstrap */

$(function() {
	"use strict";
	$('#daterangepicker-example').daterangepicker();

});

$(function() {
	"use strict";

	var locale = {
		"applyLabel" : "Apply",
		"cancelLabel" : "Cancel",
		"fromLabel" : "From",
		"toLabel" : "To",
		daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
		monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
	};
	if (languageCode === "ms") {
		locale = {
			"applyLabel" : "Pilih",
			"cancelLabel" : "Batal",
			"fromLabel" : "Dari",
			"toLabel" : "Untuk",
			daysOfWeek : [ 'A', 'I', 'S', 'R', 'K', 'J', 'S' ],
			monthNames : [ 'Januari', 'Februari', 'Mac', 'April', 'Mei', 'Jun', 'Julai', 'Ogos', 'September', 'Oktober', 'November', 'Disember' ],
		};
	}

	$('#daterangepicker-time, .for-clander-view').keypress(function() {
		return false;
	});
	$('#daterangepicker-time-single').daterangepicker({
		singleDatePicker : true,
		timePicker : true,
		minDate : new Date(),
		startDate : new Date(),
		timePickerIncrement : 15,
		format : 'DD/MM/YYYY',
		locale : locale,
	}, function(start, end, label) {
		console.log("New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')");
	});
	$('#daterangepicker-time').daterangepicker({
		autoApply : true,
		showDropdowns : true,
		timePicker : true,
		minDate : new Date(),
		timePickerIncrement : 1,
		format : 'DD/MM/YYYY',
		locale : locale,
	});

	$('.daterangepickerTime').daterangepicker({
		autoApply : true,
		showDropdowns : true,
		timePicker : true,
		minDate : new Date(),
		timePickerIncrement : 1,
		format : 'DD/MM/YYYY',
		locale : locale,
	});

	$('#daterangepicker-time').on('show.daterangepicker', function(ev, picker) {
		$('.range_inputs').find('[type="text"]').addClass('disabled');
		if ($('#daterangepicker-time').attr('data-startdate') == 'disable') {
			$('.calendar.left').addClass('disabled');
		}
	});

	$('#daterangepicker-time, #daterangepicker-time-single').on('apply.daterangepicker', function(ev, picker) {
		$(this).blur();
	});
	$('#daterangepicker-date').not('.pull-right').daterangepicker({
		format : 'DD/MM/YYYY',
		locale : locale,
	});
	// var start = moment().subtract(29, 'days');
	// var end = moment();
	$('#daterangepicker-date.pull-right').daterangepicker({
		format : 'DD/MM/YYYY',
		maxDate : new Date(),
		autoApply : true,
		locale : locale,
	});
	$('#daterangepicker-date').on('apply.daterangepicker', function(ev, picker) {
		$(this).blur();
	});

	// Single Date picker with time
	$('#datepicker-time, .for-clander-view').keypress(function() {
		return false;
	});
	$('#datepicker-time').daterangepicker({
		singleDatePicker : true,
		autoApply : true,
		showDropdowns : true,
		timePicker : true,
		minDate : new Date(),
		timePickerIncrement : 1,
		format : 'DD/MM/YYYY',
		locale : locale,
	});
	
	$('#datepicker-time').on('apply.daterangepicker', function(ev, picker) {
		$(this).blur();
	});
	
	$('#datepicker-date-time-nodisable').daterangepicker({
		format : 'DD/MM/YYYY',
		startDate : moment().subtract(90, 'days'),
		endDate: new Date(),
		locale : locale,
	});
	

});

$(function() {
	"use strict";

	var locale = {
			"applyLabel" : "Apply",
			"cancelLabel" : "Cancel",
			"fromLabel" : "From",
			"toLabel" : "To",
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
		};
		if (languageCode === "ms") {
			locale = {
				"applyLabel" : "Pilih",
				"cancelLabel" : "Batal",
				"fromLabel" : "Dari",
				"toLabel" : "Untuk",
				daysOfWeek : [ 'A', 'I', 'S', 'R', 'K', 'J', 'S' ],
				monthNames : [ 'Januari', 'Februari', 'Mac', 'April', 'Mei', 'Jun', 'Julai', 'Ogos', 'September', 'Oktober', 'November', 'Disember' ],
			};
		}
	$('#daterangepicker-custom').daterangepicker({
		startDate : moment().subtract('days', 90),
		endDate : moment(),
		minDate : '01/01/2012',
		maxDate : '12/31/2014',
		dateLimit : {
			days : 90
		},
		showDropdowns : true,
		showWeekNumbers : true,
		timePicker : false,
		timePickerIncrement : 1,
		timePicker12Hour : true,
		ranges : {
			'Today' : [ moment(), moment() ],
			'Yesterday' : [ moment().subtract('days', 1), moment().subtract('days', 1) ],
			'Last 7 Days' : [ moment().subtract('days', 6), moment() ],
			'Last 30 Days' : [ moment().subtract('days', 29), moment() ],
			'This Month' : [ moment().startOf('month'), moment().endOf('month') ],
			'Last Month' : [ moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month') ]
		},
		opens : 'left',
		buttonClasses : [ 'btn btn-default' ],
		applyClass : 'small bg-green',
		cancelClass : 'small ui-state-default',
		format : 'DD/MM/YYYY',
		separator : ' to ',
		locale : locale,
		/*locale : {
			applyLabel : 'Apply',
			fromLabel : 'From',
			toLabel : 'To',
			customRangeLabel : 'Custom Range',
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
			firstDay : 1
		}*/
	}, function(start, end) {
		console.log("Callback has been called!");
		$('#daterangepicker-custom span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	});
	$('#daterangepicker-custom span').html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));
});

$(function() {
	"use strict";
		var locale = {
			"applyLabel" : "Apply",
			"cancelLabel" : "Cancel",
			"fromLabel" : "From",
			"toLabel" : "To",
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
		};
		if (languageCode === "ms") {
			locale = {
				"applyLabel" : "Pilih",
				"cancelLabel" : "Batal",
				"fromLabel" : "Dari",
				"toLabel" : "Untuk",
				daysOfWeek : [ 'A', 'I', 'S', 'R', 'K', 'J', 'S' ],
				monthNames : [ 'Januari', 'Februari', 'Mac', 'April', 'Mei', 'Jun', 'Julai', 'Ogos', 'September', 'Oktober', 'November', 'Disember' ],
			};
		}
	$('#daterangepicker-custom-2').daterangepicker({
		startDate : moment().subtract('days', 90),
		endDate : moment(),
		minDate : '01/01/2012',
		maxDate : '12/31/2014',
		dateLimit : {
			days : 90
		},
		showDropdowns : true,
		showWeekNumbers : true,
		timePicker : false,
		timePickerIncrement : 1,
		timePicker12Hour : true,
		ranges : {
			'Today' : [ moment(), moment() ],
			'Yesterday' : [ moment().subtract('days', 1), moment().subtract('days', 1) ],
			'Last 7 Days' : [ moment().subtract('days', 6), moment() ],
			'Last 30 Days' : [ moment().subtract('days', 29), moment() ],
			'This Month' : [ moment().startOf('month'), moment().endOf('month') ],
			'Last Month' : [ moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month') ]
		},
		opens : 'left',
		buttonClasses : [ 'btn btn-default' ],
		applyClass : 'small bg-green',
		cancelClass : 'small ui-state-default',
		format : 'DD/MM/YYYY',
		separator : ' to ',
		locale : {
			applyLabel : 'Apply',
			fromLabel : 'From',
			toLabel : 'To',
			customRangeLabel : 'Custom Range',
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
			firstDay : 1
		}
	}, function(start, end) {
		console.log("Callback has been called!");
		$('#daterangepicker-custom-2 span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	});
	$('#daterangepicker-custom-2 span').html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));
});

$(function() {
	"use strict";
	var locale = {
			"applyLabel" : "Apply",
			"cancelLabel" : "Cancel",
			"fromLabel" : "From",
			"toLabel" : "To",
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
		};
		if (languageCode === "ms") {
			locale = {
				"applyLabel" : "Pilih",
				"cancelLabel" : "Batal",
				"fromLabel" : "Dari",
				"toLabel" : "Untuk",
				daysOfWeek : [ 'A', 'I', 'S', 'R', 'K', 'J', 'S' ],
				monthNames : [ 'Januari', 'Februari', 'Mac', 'April', 'Mei', 'Jun', 'Julai', 'Ogos', 'September', 'Oktober', 'November', 'Disember' ],
			};
		}
	
	$('#daterangepicker-custom-1').daterangepicker({
		startDate : moment().subtract('days', 90),
		endDate : moment(),
		minDate : '01/01/2012',
		maxDate : '12/31/2014',
		dateLimit : {
			days : 90
		},
		showDropdowns : true,
		showWeekNumbers : true,
		timePicker : false,
		timePickerIncrement : 1,
		timePicker12Hour : true,
		ranges : {
			'Today' : [ moment(), moment() ],
			'Yesterday' : [ moment().subtract('days', 1), moment().subtract('days', 1) ],
			'Last 7 Days' : [ moment().subtract('days', 6), moment() ],
			'Last 30 Days' : [ moment().subtract('days', 29), moment() ],
			'This Month' : [ moment().startOf('month'), moment().endOf('month') ],
			'Last Month' : [ moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month') ]
		},
		opens : 'left',
		buttonClasses : [ 'btn btn-default' ],
		applyClass : 'small bg-green',
		cancelClass : 'small ui-state-default',
		format : 'MM/DD/YYYY',
		separator : ' to ',
		locale : locale,
		/*locale : {
			applyLabel : 'Apply',
			fromLabel : 'From',
			toLabel : 'To',
			customRangeLabel : 'Custom Range',
			daysOfWeek : [ 'Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa' ],
			monthNames : [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ],
			firstDay : 1
		}*/
	}, function(start, end) {
		console.log("Callback has been called!");
		$('#daterangepicker-custom-1 span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
	});
	$('#daterangepicker-custom-1 span').html(moment().subtract('days', 29).format('MMMM D, YYYY') + ' - ' + moment().format('MMMM D, YYYY'));
});
