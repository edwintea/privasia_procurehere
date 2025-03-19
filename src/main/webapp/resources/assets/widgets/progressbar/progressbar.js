/* Progress bars */

function progress(percent, element) {
	var progressBarWidth = percent * element.width() / 100;
	// element.find('.progressbar-value').width(progressBarWidth);
	element.find('.progressbar-value').animate({
		width : progressBarWidth
	}, 100);
	element.find('.progress-label').html(percent + '%');
}

$(document).on('ready', function() {

	$('.progressbar').each(function() {
		var bar = $(this);
		var max = $(this).attr('data-value');

		progress(max, bar);
	});

});

$(function() {

	$('#header-right, .updateEasyPieChart, .complete-user-profile, #progress-dropdown, .progress-box').hover(function() {

		$('.progressbar').each(function() {
			var bar = $(this);
			var max = $(this).attr('data-value');

			progress(max, bar);
		});

	});

});

/* MAIL BOX REPLY */

$(document).on('ready', function() {

	$('.progressbar1').each(function() {
		var bar = $(this);
		var max = $(this).attr('data-value');

		progress(max, bar);
	});

});

$(function() {

	$('#header-right, .updateEasyPieChart, .complete-user-profile, #progress-dropdown, .progress-box').hover(function() {

		$('.progressbar1').each(function() {
			var bar = $(this);
			var max = $(this).attr('data-value');

			progress(max, bar);
		});

	});

});