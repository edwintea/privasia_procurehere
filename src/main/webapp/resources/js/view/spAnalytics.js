$(document).ready(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	getBUnitLisByDate(header, token, null, null);
	getProcCatListByDate(header, token, null, null)

	getTopHighPerformanceSuppier(header, token, 'pie', null, null);
	getTopLowPerformanceSuppier(header, token, 'pie', null, null);
 
 	$("#resetDate").click(function(e) {
		e.preventDefault();
		$('#datepickerAnalytics').data('daterangepicker').setStartDate(new Date());
		$('#datepickerAnalytics').data('daterangepicker').setEndDate(new Date());
 		$("#datepickerAnalytics").val('');
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
	
		getBUnitLisByDate(header, token, null, null);
		getProcCatListByDate(header, token, null, null)
	
		getTopHighPerformanceSuppier(header, token, 'pie', null, null);
		getTopLowPerformanceSuppier(header, token, 'pie', null, null);

	});
	
	$('#datepickerAnalytics').on('apply.daterangepicker', function(e, picker) {
		console.log("apply click");
		e.preventDefault();
		$('.error-range.text-danger').remove();

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];

		getBUnitLisByDate(header, token, startDate, endDate);
		getProcCatListByDate(header, token, startDate, endDate)


		getTopHighPerformanceSuppier(header, token, 'pie', startDate, endDate);
		getTopLowPerformanceSuppier(header, token, 'pie', startDate, endDate);
	});

	$('#highPerformanceSuppBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		if (!$(this).hasClass('icon-active')) {
			getTopHighPerformanceSuppier(header, token, 'bar', startDate, endDate)
		}
	});

	$('#highPerformanceSuppPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		if (!$(this).hasClass('icon-active')) {
			getTopHighPerformanceSuppier(header, token, 'pie', startDate, endDate);
		}
	});

	$('#highPerformanceSuppByBUPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);
		var buId = $('#idBusinessUnit').val();

		if (!$(this).hasClass('icon-active')) {
			getTophighPerformanceSuppByBUForCurrentYear(header, token, 'pie', startDate, endDate, buId);
		}
	});

	$('#highPerformanceSuppByBUBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		var buId = $('#idBusinessUnit').val();

		if (!$(this).hasClass('icon-active')) {
			getTophighPerformanceSuppByBUForCurrentYear(header, token, 'bar', startDate, endDate, buId);
		}
	});

	$('#idBusinessUnit').on('change', function() {
		console.log("change >>>");
		var buId = this.value;

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		getTophighPerformanceSuppByBUForCurrentYear(header, token, 'pie', startDate, endDate, buId);
	});

	$('#topHighPerformanceSuppByCatPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		var pcId = $('#idPcList').val();

		if (!$(this).hasClass('icon-active')) {
			getTopHighPerformanceSuppByProcCatForCurrentYear(header, token, 'pie', startDate, endDate, pcId);
		}
	});

	$('#topHighPerformanceSuppByCatBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];

		var pcId = $('#idPcList').val();

		if (!$(this).hasClass('icon-active')) {
			getTopHighPerformanceSuppByProcCatForCurrentYear(header, token, 'bar', startDate, endDate, pcId);
		}
	});

	$('#idPcList').on('change', function() {
		console.log("change >>>");
		var pcId = this.value;

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		getTopHighPerformanceSuppByProcCatForCurrentYear(header, token, 'pie', startDate, endDate, pcId);
	});

	$('#lowPerformanceSuppBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerformanceSuppier(header, token, 'bar', startDate, endDate);
		}
	});

	$('#lowPerformanceSuppPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerformanceSuppier(header, token, 'pie', startDate, endDate);
		}
	});

	$('#lowPerformanceSuppByBUPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);
		var buId = $('#idLowPermBusinessUnit').val();

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerformanceSuppByBU(header, token, 'pie', startDate, endDate, buId);
		}
	});

	$('#lowPerformanceSuppByBUBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		var buId = $('#idLowPermBusinessUnit').val();

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerformanceSuppByBU(header, token, 'bar', startDate, endDate, buId);
		}
	});

	$('#idLowPermBusinessUnit').on('change', function() {
		console.log("change >>>");
		var buId = this.value;

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		getTopLowPerformanceSuppByBU(header, token, 'pie', startDate, endDate, buId);

	});

	$('#lowPerfSuppByProcCatPieIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);
		var pcId = $('#idLowPerfPcList').val();

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerfSuppByProcCat(header, token, 'pie', startDate, endDate, pcId);
		}
	});

	$('#lowPerfSuppByProcCatBarIconId').click(function() {
		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1]);

		var pcId = $('#idLowPerfPcList').val();

		if (!$(this).hasClass('icon-active')) {
			getTopLowPerfSuppByProcCat(header, token, 'bar', startDate, endDate, pcId);
		}
	});

	$('#idLowPerfPcList').on('change', function() {
		console.log("change >>>");
		var pcId = this.value;

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1]);

		var startDate = myArr[0];
		var endDate = myArr[1];

		getTopLowPerfSuppByProcCat(header, token, 'pie', startDate, endDate, pcId);

	});


});

function getTopHighPerformanceSuppier(header, token, type, startDate, endDate) {

	var rfsCategoryBarChart_ = echarts.init(document.getElementById('highPerformanceSuppBarChart'));
	rfsCategoryBarChart_.showLoading();
	rfsCategoryBarChart_.clear();
	var url = getContextPath() + '/spAnalytics/getTopHighPerformanceSuppier';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfData = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfData = totalOfData + element.score;
				console.log(">>>>>> " + totalOfData);
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					supplier: (item.name ? item.name : ''),
					score: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString(undefined, {maximumFractionDigit:0}) + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSupplier',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfsCatVolPieheaders = ["RANK", "SUPPLIER", "SCore", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSupplier', rfsCatVolPieheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 25,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSupplier',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfsCatVolheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSupplier', rfsCatVolheaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#rfsCategoryByVolumeDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td class="" >' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#rfsCategoryByVolumeDataTable').append(html);
			}

			//			$('.rfsCategoryByVolume-heading').html('Total Volume: ' + data.recordsTotal);

			if (type == 'pie') {
				$('#highPerformanceSuppPieIconId').addClass('icon-active');
				$('#highPerformanceSuppBarIconId').removeClass('icon-active');
			} else {
				$('#highPerformanceSuppBarIconId').addClass('icon-active');
				$('#highPerformanceSuppPieIconId').removeClass('icon-active');
			}
			rfsCategoryBarChart_.hideLoading();
			rfsCategoryBarChart_.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			rfsCategoryBarChart_.hideLoading();
		}
	});
}

function genData(response) {
	var nameList = [];
	for (var index = 0; index < response.length; index++) {
		//		console.log(" >>>>>> "+response[index].name);
		nameList.push(response[index].name);
	}
	var legendData = [];
	var seriesData = [];
	var selected = {};
	for (var index = 0; index < response.length; index++) {
		legendData.push(response[index].name);
		seriesData.push({ name: response[index].name, value: response[index].value });
		selected[response[index].name] = index < 10;
	}
	return {
		legendData: legendData,
		seriesData: seriesData,
		selected: selected
	};
}

function exportCSVFile(items, title, headers) {

	// Convert Object to JSON
	var jsonObject = JSON.stringify(items);

	var csv = this.convertToCSV(jsonObject);
	fileTitle = title;
	var exportedFilenmae = fileTitle + '.csv' || 'export.csv';

	var blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
	if (navigator.msSaveBlob) { // IE 10+
		navigator.msSaveBlob(blob, exportedFilenmae);
	} else {
		var link = document.createElement("a");
		if (link.download !== undefined) { // feature detection
			// Browsers that support HTML5 download attribute
			var url = URL.createObjectURL(blob);
			link.setAttribute("href", url);
			link.setAttribute("download", exportedFilenmae);
			link.style.visibility = 'hidden';
			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
		}
	}
}

function convertToCSV(objArray) {
	var array = typeof objArray != 'object' ? JSON.parse(objArray) : objArray;
	var str = '';

	for (var i = 0; i < array.length; i++) {
		var line = '';
		var pos = 0;
		for (var index in array[i]) {
			if (line != '') line += ','

			if (pos == 1) {
				line += "\"";
			}

			line += array[i][index];

			if (pos == 1) {
				line += "\"";
			}
			pos++;
		}
		str += line + '\r\n';
	}
	return str;
}

function getTophighPerformanceSuppByBUForCurrentYear(header, token, type, startDate, endDate, buId) {

	var rfsVolumeByBusinessUnitBarChart = echarts.init(document.getElementById('highPerformanceSuppByBUBarChart'));
	rfsVolumeByBusinessUnitBarChart.showLoading();
	rfsVolumeByBusinessUnitBarChart.clear();

	var url = '';
	url = getContextPath() + '/spAnalytics/getTopHighPerformanceSuppierByBUnit';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
			'buId': buId
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfData = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfData = totalOfData + element.score;
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					supplier: (item.name ? item.name : ''),
					score: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSupplierByBusinessUnit',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAMAAADDpiTIAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAAztAAAM7QFl1QBJAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAwBQTFRF////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACyO34QAAAP90Uk5TAAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+6wjZNQAAHJlJREFUeNrtnXdgFcX2xzeF0AkBpPNAmoAFNRp9NjqSpzQBKQKKUkSKWBCQpsgTFBUBlS5IUxGRHkAxCKIIglEELKg0iRDpIaTe81P86aPce+fs7szNbOb7/Td3z9k955MtZ87MWJYCFb132qqkIz4yTseT3qpnma6ILivTyWDtqG92/lvsJMOV/YjB6a+ykSB6ydj810tB9v/UU4bmv1cmcn9OvvZG5r8TMv+30m83MP+xaUj8PzpWy7j8l96PtJ+nX8qYBsBsJP0CbS1sVv7r5iDnF2p5hFEArEHGL9Zkk/Ifh3xfqkEGATAW6fZTDuhgDgC7kW5/5YA7TMl/TSTbfzmgtiEA3IdcBygHlDUDgIFIdQB9aUY5YDwyHUgrjCgHvINEB9QUEwBI8HPhvcqaoatFBAw2AIDVfq7blI/gssJyQEcAYDQAlFEPABgNAB2vDQCMBoD2lgUARgNA2woDAKMBoJURAMBoAGgqADAbABoCAMwGwNcJABgNAGXUBwBGA0DH6wAAowGgveUAgNEA0LYiAMBoAGhVJAAwGgCaBgDMBoCeBgBmA+C7DwAYDQBlNAAARgNAx68EAEYDQPvKAQCjAaDtRQCA0QBQQiQAMBoAmg4AzAaAhgIAswGgzgDAbAAyGgIAowGgE1cCAKMBoP3lAYDRANBXRQGA0QDQ6kgAYDQANAMAmA0ADQMAZgNAXQCA2QBkNgIARgNAJ64CAEYDkDfKAQDAhZKKAgCjAaA1kQDAaABoJgAwGwAaAQDMBoC6AgCzAfB6OQAAuNXJqwGA0QDQgQoAwGgAvF0OAAAStDYSABgNAL0JAMwGgEYCALMBoPsBgNkAZDYGAEYDQCevAQBGA+DVcgAAkKaviwEAowGgtfkAgNEA0CwAYDYA9AwAMBsA6gYAzAYgswkAMBoA75UDAIBkHawIAIwGgL4pBgCMBoA+zAcAjAaAZgMAswGgZwGA2QDQgwDAbACymgIAowGgU3UBgNEA0K+VAIDRANCOaABgNAD0UT4AYDQA9BYAMBsAGgUAzAaAHgIAZgOQdScAMBoAOnUtADAaAA+UAwCAWmlfDgAAirUuHwAwGgCaAwDMBoCeAwBmA0DdAYDZAGQ1AwBGA0CnrwMARgNAh/4FAIwGgL6NBgBGA0AfRwEAowGguQDAbABoNAAwGwDqAQDMBiArHgAYDYCe5QAAEELpWA4AAKHUzuIAwGgAKDEKABgNAM0LAwBGA0D9AIDZABwvBQCMBoCmAACzAcgpAQCMBoDqAQCzAegDADRRmdwB4A0AoInCTuYKAEsAgC76OFcAWA0AdNFYAGA2AC0AgNkAhCUCAKMBsKqcBgBGA2D1AABmA2C1TQEARgNglV4MAIwGwLJqdnwp8TtF+tVPeBMAgDnqgDsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACA0QCUavnI6Flrduw9T2f9nGHKXkiS/G1Jdfb8H+xYM2v0Iy1LhSL7NZ7ckEOQlspeP6CK2uznf2IXwqy3vuqTT1n6w7vsQ4D1108dw9Tkv+lXCK43tK2JgvRHTUdgvaPpUbLzf9kGRNVL2nCZ3Pxfsxcx9Zb2XiP18Z+KiHpNqU3l5b/OScTTezpZR1b+S+xBNL2oPSXk5D9yHWLpTa2LlALARETSq5okI/83+hBIr8p3owQAEhFH7yrRff7vQhS9rLtcDwDtQBC9rB3hLgHoihh6W11dArAWIfS21rrLf3QmQuhtZRaT3o2oQKe/XTVlyP1t4u+4oXblUoVjKtWJa9jyvl5PPvPS1CW7MuS7O/XTltXzJz3Tv3P8TdVLFK1QK65R6659h4x5bU7ib3mPgPauAHhb9ekdXj68xbUxwU4holr8o69/tF9WNeK7Nx+qFaRjpsSt3V9J2JuXSh/z3eQ/3wmFZ5b6ybh2ldmnUui6Actdjkmd3Ti2Oa9xtnBsz0XHgtr6djBD492GaAvDyUKBjeNu6sFXKcv+nlca2D+xyJuHrjvr9Hto4M32umQibh7xaVbgOxfn8yryuMsodWM4mS0ycqWbPgAlyc/ZNMj5SGWBRrPSbHvMWljP2Ttwq8mB3gru4By/0F2gfKUZrXrCm7SbBsEHFKQ/6WG33Uoxj+625TH52fIuxkJbr/Q7D+JVzsHd3IVqM8NFc6GVB1yE+mnZ2U+bfbOUEcp6H7FdbmzvtlG+4gg/DXH7OEeWdfc2OZThYq7QytMuLv01uenfPSBGVpPKOO47sJTWuPCmlyJwA+fAba7iVZcxVUf8YvyaiwtfLDP9WyU2qTEje+huWXNijl5i+3nOcaPdBGw/w0ErsZnFkif9Ov3vbyOzSzWGNT9xrrQbTpyfggLnuFvchOwNhoMFYjOrdQBgX7cIqW3qDPApuYU8f8P82Od8yUQcdRG0/4jtFzztCQCOPppf8jwVRpva/BIS/W3042CY4lLqmQJi8/eQFwB4v4z0iWrCLoXDrWW6K+avIrRd8XD6Uob5dzwAwOF28icqlhbW/cpL9dfSr5PLOWfq/EOwO6M+nqo/AAtULFvRXuB0U4xcf2/49fI459CtjsuA5cTG25HuABxqaanQ1OBeVxaS7M//zJhPOYc+6zR0XzKMv6c7AJvKKsm/9UNQr6siJburFmBMg3N1NzmN3UjGoGWa5gDMjFKT/4pBvW4vIttf7wCeHuaUkFIcBi9WbJvXrpNrAGQ/ailS0FfrfeWk+1sSwBWr326es+j9yljnZbHWABxtrCr/1qwgbk9dKd1dZKB6exbnXfM+Z+GbJrZc9KzOAHxXTVn+rX0ueyhs6raAzjg916WcrarXXGy5E2kMwA/l1OW/mqKRj0B6LqC3JZzDNztqXyvk/MmkAwB7KqjLv9U9SP1fRdXhi8BpKsw4fISTAK5klCfT9QXg50oK8x9sBKy1AnclgtzD2zKOv8FJBBkfGF1IWwD2VVGZfys5cN1Bhbt7g9U5GceHHXYQwopiu8u1BeBAVaX5rxPY820q/M0McqknOZWOt+yH8Cux1eIZugKQepXS/Ft9AnpersTfAZdj9k6mVz0ntspu2A05AO3V5t96P6Dn6+yaCitaodplgsJx7aAXO4PzEpFtO4ZxYqurdAXgZafOqt7WqseQV+YkbN2b8nPShhULprw4rEvspWXdsIBNNpvtOGs3Zu3+k3+93p3al5S4eOa4wXf57VsYEPRqUzidTpvsxvA3cRkwJlNTAD521PtVo+eCQ/4HRfetHt/z3+f/k17rvgZ05ZgApaT9i4c0ibH3n1af4W+o3SC+Kbb5IOkJwAH7sz6qPjTvoMDqqWX9a//988cD/egEcxD4niRBDeOtluc1Y+U/E/zXExker7cbxdYyUxNSANLjbHoo2OdHpumDs1ufe+VeEegHb7Ac1v6Q4ev0223+pqmRiHjGoE1Yss0oisczS2bpCcBQe/ajh9j6Rv590o1W5Ck3L+RWd+6j88x7bc49zF6U8L5mvWkvioyU9SAtAdhpaxZW2bH253vvej3gv00h2U/jfU/98T6QJPrVWIbbdvYusq/Y4odaAuC73c6jf7LTmd7+9RHD5zCbNs9Mri9s6vyB4bd4li234kLqZdlaAjDDhumH00muBop9xmWRAnHqXra229jBiB7pCMAR/lSMwvOl50G8AkD+71Xkn0YwrnewHYNjxPY+1hKAzmy7tXfKz4N4ILi9kvxTEuOC69oxeIvQXJkcHQFYzzbbScEGJEckFk9titP7dJBvLkW8+swjpCMA3FVY8r+hIgti/KKzFQHwJOOiZ/DNvSW2tl5HADZyv/03K8mCuInyWkX5p8845Ue+OfFUunI5OgJwJ7P0p2j/uReFnluqAsDHmIhYjD12w1jasx9pCMBWnsV8KxVlQTxZW926148wLjxRYj1jo44AMCcBLlCVhP5C1w2VAcCpQQ3kGhsgNFXBpyEA3/A2LO6nLAniseBqynxnlRRfOXvNTfE3xQDSEICOLHvXpStLwgNC51EZuejcsvbzTO0WW/pMQwBOsNaAKfKDshTQ4Fx8/NAyxsVPlfUyW8mnIQC8UYBp6vLPWbnzNmXOGQP43I8Q8RK0j5OGAHAao6zYHIUAvMs4gS+Ueb+XcftjPYGOiTvqNmsIAKctxgr7TGH+6RPOCPQxVd7fYXhnrW4rXt2/MmkIwAscY11U5p81LG/Fq7oHnWa8A7Fu3eKX6YE6AnA1w1bUQaUAnGZdUPcsRe4Za9LW5nxQilcc2KohAN9wbKnegK4w64oaHFXjndHIbf0iY0irKmkIwHCOra8UA8AcjKy2XYn33xkLUzFGQZ+Q21oSKgA4u2fUV5x/eoV5TeFd9qpw31Ds+W6xlSuERrZrCEA6pwq0RDUAP7GvKv/jCp4DjFX5CwnroD8KbVQnDQHYwLBUMks1AGRjUnL0mDTZ3jmreq1xfxd7WkcARjMsPaQ8//YmpVSYKbs/iLEXzgD3z5EkHQHg7ASSoB6ALfaurc7MM1Ldi4v4Vk3RiIpwXs0VpCEAWYxCeEwI9iD22V0hPLrvtxLd72F43OO2nD1cRwC+0KAIcE7P2r/A2+bKG6BmbE81KbgFcV/9Dh0B4Hx/zQwFAKlO1iYs+cT3oeMvPqiBbGFfSW3SEYAeDEO7QgEATXd0kWENF0p5QDFmdBUM+vEhXn7+GS0BaCC2Uzw023FnO10nuMzT+yS4ryF2FHRuyiA1/0fKAWAsadeUQqOVjq80vHmCa0jFCQzeEynE19le3qoBSGNUQEaGCABOQTbwKMFLLguEjLfhYI2pPwuPHqUlAN8y7LwbKgCSCri4WqvAA1tcfYcy7oVBPgTFqw19pyUAHzDsbA4VADTPcqcb3VSs+rkaERQucVOXtARgHMNOcsgAoCEuCbAaOO8cTBRbD9waKl7i5r96AtCTcWv1hQ4AXyu3BFhtnVYGssVrFBQLOCgmnl70o54AMCaF1qAQKrWuawIiex1y5vshse2A8/qES9xcR3oCcKvYTKNQAkD7SrsmwCo03NE0IsZnaMBqvrCvcqymADD+4VqHFADaJmOv4rpOBorSxVO7A+0j+KvwyJ80BaC62Ezn0AJAe+tIIKDARAeexV3d4QHmJswSHXgDaQoAYwSmZ4gBoBONJBBgNbP/8fKe2GqA7V47iI57UVcAxHc967FQA0CZUnaPK7XU9huouBDlf5HXHOFI4F5dAWAsDz+UQq/nwmQg8ITdD1jxOhmVnZWRHe9BrBqAdIaZ0bkAAL0nZQe5HjYnkzGW+PJb0B0lOuplXQH4nWFmUG4AQEc6yCCgs73m0WPiCSITnXxMh+3XFYADDDPdKXe0TMb2lW3sdYs0ERr0Nz/kuOhB+m/SFYDfGGZa5RIAdLKnhDeBu2ytaj5ZaK+IH6IWiQ56VVsATjHM3E65psTq7gloYmdSS7J4oVc/63yK2urCDmoLQDbDTJ3cA4AypvzLNQG2vmLEpXE/s3tE5+hmdRvVn4GMiYGlKTeVObWySwDC7azOLt4279Ka3i5HL46aACBe0sCK9FHuIjCtijsCyqfwnYk7u8IvsTZedMQhjQHgvGkfp1xW5vTLXRFwtw1f4v1L3774kGaCA+qRxgAwmqFFU6JCoewVrSJdRGKCjRqk0Fi3i444W1BwwOs6A3A9w85m0kHJY6o5jkR+fil+p9BYxYuOWCN6AvymMwAtGHYWkR7yreuY32EobKxyLF7m46Idcx4X/LwB6QxAf4ad/qSNjo6v5SgUBY+wXYj7UsdfeIBoRsgUrQHg7BZ+Nemkj9vlcxAL/p6D4q0T4m1V0yOOaA3A+ww7YSlaEUDJo+1Xh2JOsc0LjV+4WpBooeXGpDUA2ziG3iPNlLMsPtxmMMaxjT8qtJV4/s9FmwRN0xsAzniwvX3OQqSfB9trHy7Pbg0Qr1t8wRwPQQ9r5O96A0CMFWKcrWygfpxgclk70fiSfXsRknXXeb8WrQ13J2kOwA0cS8laEkCpo4qqeAYI18woed6PRf3AM3UHoA/H0tukqY70ZEcjnm00wU5fmGA6Ub5jugPwFsdST9JWyy9jRqMIuy8gI1pkaxa7bhRPugPwHcdSyVR9CfgtnhkO/o4X94lM/a85XLTp8WztAfAV55h6RV8AyNeLFw7+/GxhceR/q70sDv7DqOPaA8BaKdSqkKExAYwv9z/VjG3vjGh8L/wEcyDgbtIfgGEsW9N1BoC1+6udinZrka1/Fo6OC/67OR4AIIFlq0aOzgCkcdoayvHtzRXZGvn3vSL4uET+kx4AgLNr3h96R+tbwCeMBvJ8fHPHReNNf6+c93Hwn0nY8jwEO4a0ZRm7VmsAWBdxgm9OtHBK9P/fEAVzwuZ7AoC5PGurtAZgqdzWtqkiWzs4b9AFTnkCgGO8brvrtf4QyCghtbXtN9Fg4197CWcHL0XLWFslFPsGMhfo7O31Z8AKG+ZuF9h64Nyvtqt/bwoFABOZ9uboDMBj4vO3s2CEqNe/JiNyBVM9AsDBCJ69Ql9rDABjxUs7K8nuFRk7N8wffNfptuQRAOgepsHqJ/QFYLb49A/YsRcrMLb8zx8F3+ZmoWcAWM+12MKnLQAviVsbbe1991+BtT/niAafR1b4jGcAYG0ffU7PawuAuL/d3iTX3QJrf+6lOifoL9qTdwCYxjUZsUhXAITle7vLddcObq1wlmih5fc9BMCZGDYBM/TMP2O1/2b2LIp2stxGFHRFyyJpHgKAnuRbfUFLABhrvXezZ1HUMP8aHQ06ANGRvARAcmG+2X46lgS7i8/b7sa9goUpOtGyoH9f4ikAaKQNu7E/qkniUuevzQcY7cF2u7MEpaXL6algfy6W7i0AUu302BedpyL/Z/OXGPKrwzeAJoy3F7tz9DYKDCbfEuyvspbYDhUA4vGvC9T4UzWP8aiuSU4OfZ1xyvXsGs0RzPnpEBXsr8u8BkC2zUXam2ySDcAzfxlutNJ289FCzrIB9tfq6+Ui9tEZXgOAlts13/Qz+7fqYPv5/LOHabk+620x8Cproqj93UXXuIj9/eQ5AFiLhVyoOz+3dUvd0K9CmcB/zji/FbdM73XcVX5TeJODYu3HPrO489iv9CAAhx3s1nPTiI28CnvKsj7nXjMPsd+5Luv5IeM2mv5CNO9Mnax53sVx6GMyPQiA/YfAX58EzSfuDv52kTS5aw1xU8ZoP021cX3nfBds/Gn3SPZSEU52bv7Acei7kRcBoJ5OPVXsNv/zPZe0QPsObpg1vNPNF3QdPxfQeaAvuehGQz7wt9buqU3P29hi7gonwU8rHLLQ6wFAag3LjaLK123csd+oSeOGD3iwXbNbahWw0yaXGTTYxS6Pbdqxz4gJ81Z98fna92dPGtG6qr2VxCc4in4bh5EomeVNAGhzpKVYlQO5/lyp20rO6nLzHbqTuMVCaAGg6aoBsAJt8j5WqdepzqJ/MsqZu7WeBYA5U9CFPgrgOF6l06pOb8nOzqpUtncBcPHpw1OAlVqyi6p0utxp+J3dEXuRhwHIbKgWgADD5FtV+nReljsS4cTfOi8DQCeuUgpALf9exyl0WcHFKg31HPgrne1pAOjXK1UCEO5/tsTdCl26mdU4wYE/qVOocgEAOnarSgL8DiTnFFfncLib+O934HC91wGgNJX/j5P8edyuzl9bd3MZbrTtsGyO5wGgrAfUJcRvmXy8MnexLqdnjLHtsS95HwAK3u/mSn5XmmilytvlB10m4HvbLjfkCQDo7WhFKcnnZ5DXV1KRsysOus6A3Vfi8r68AQD9couipGy71Nc3ilxdc9h9Bobb9Pko5REAKHt4uJKs+FlxbpKa/N90TEIGvrLpdFOeAYDok0oq0uLnM7mtkvz3ktOab2/Pwkq+PAQAHe+vYHz45kv9lFaQ/iLzJaXgCVtuH6O8BADRbvmjdIUuqZTuUpD/q3fLSsEmW3435zEAiFbVkp2bnRe7mCw9/YVfkNaVSb5yNhxXpjwHAGVNkPyRNveSQVfZY8HtDsjMQW8bnp/MgwAQpU2tIzM9j1/aeTO+qszi34dyc/ChDd9b8iQAf2jtf8KkJai+v9GgJfUlWb9jtewcZJVgO7+c8ioARN/3KSIlQZEtE/w7SOpW0LXxsHgFs1bpfrb/QXkYAKKzS7rGuM3QFS8E2Us7bWXfam6MV3/2Z1Khpewz2JanAfhDmWt6Ov9mD7+2n/j/84cJzQo4sl6i1yZSpLPce181yusA/Pm0/mTknfZvBPlvH7KKu8xk2qp+dewVoKLqj96cTerUjnkeQ0wA4Nyn8fdzHonlbuJdMq7j8xvtVmXTv54/pHkVzotnhfjBCWdIrb6ZwtN+UwD468a47f2X+zW/pljAF7JK9R96/t1tbpaXPb15xoDGVUv4b82Nrt24+4TEo5S3pTEAf+vY9rVLFsyYMGb4E70f7Dto1CtT532w9rOv9ySny3Nx+sCOT1fMe230wB49+j81csyrU+cn/pBKRsgDAEAAAAIAkH4ALEL4vK9FLgCYiPB5XxNdADAI4fO+BrkAoAvC5325ma7fCOHzvhq5AKAWwud9uWnICz+M+HldR1zNzZiJAHpdb7rqvmiBAHpdLV0BUPAMIuhtpRVy14C1BCH0tpa57MBriRB6W21cAmB9ihh6WZtdt0nfgiB6WXe478JfjCia+wZwrg8/C3H0qrKlTMh7GoH0qkZaUrQAkfSmFkqajVlwK2LpRW0rZElShUOIpveUXFHefPnrkxFPz+X/epkrMlTajoh6S9slr89WCA3CntKiQpZkhY3yIaxekW9UmCVfdRMQWW8ooa6lRg22ILj6a0sDS53aJGQgwjorI6GNpVbF2r9zEnHWUyff6VDMCoGi4lr1HjV9xfrz5G9R7V3rIUnyt3H5sfN/sGL6qN6t4qKsXJO/acQdLEiSOvgJ72qtzhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQOIAAAQAIAAAAQAIAEAAAAIAEACAAAAEACAAAAEACABAAAACABAAgAAABAAgAAABAAgAQAAAAgAQAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABA4gAAZDAAS8dCkrTUkwBASgUAAAAAAAAAAABooQQkJNRK0AqAd5GQUOtdrQAYj4SEWuO1AmAgEhJqDdQKgM5ISKjVWSsAaiIhoVZNvarVu5GR0Gq3ZsMVY5GS0GqsZgDEISWhVZxuI5ZrkJNQao12Q9Z1c5CV0Cmnrn5NC7ORltBptoZdK6X3Iy+h0v7SOvYtxaYhM6FRWqyenWudkJrQqJOuvYu9MpEc9crspW/3ar0U5Ee1Uurp3L9cZSMypFYbq2jewt5iJ5KkTjtb6D+JIaLLynRkSoXSV3aJ8MZElqL3TluVdMSHlMmS70jSqmn3FlWRq/8DuRPhbREQOwkAAAAASUVORK5CYII=',
								onclick: function() {
									var rfsVolBUheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSupplierByBusinessUnit', rfsVolBUheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 35,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSupplierByBusinessUnit',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var barHeaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSupplierByBusinessUnit', barHeaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score (%)',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#rfsVolumeByBusinessUnitDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td >' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#rfsVolumeByBusinessUnitDataTable').append(html);
			}

			//			$('.rfsVolumeByBU-heading').html('Total Volume: ' + data.recordsTotal);

			if (type == 'pie') {
				$('#highPerformanceSuppByBUPieIconId').addClass('icon-active');
				$('#highPerformanceSuppByBUBarIconId').removeClass('icon-active');
			} else {
				$('#highPerformanceSuppByBUBarIconId').addClass('icon-active');
				$('#highPerformanceSuppByBUPieIconId').removeClass('icon-active');
			}
			rfsVolumeByBusinessUnitBarChart.hideLoading();
			rfsVolumeByBusinessUnitBarChart.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			rfsVolumeByBusinessUnitBarChart.hideLoading();
		}
	});
}

function getTopHighPerformanceSuppByProcCatForCurrentYear(header, token, type, startDate, endDate, pcId) {

	var rfxVolumeByCategoryBarChart = echarts.init(document.getElementById('topHighPerformanceSuppByCatBarChart'));
	rfxVolumeByCategoryBarChart.showLoading();
	rfxVolumeByCategoryBarChart.clear();

	var url = '';
	url = getContextPath() + '/spAnalytics/getTopHighPerformanceSuppByProcCat';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
			'pcId': pcId
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfDataRfxVolByCat = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfDataRfxVolByCat = totalOfDataRfxVolByCat + element.score;
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					category: (item.name ? item.name : ''),
					volume: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSuppliersByCategory',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfxVolByCatheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSuppliersByCategory', rfxVolByCatheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 30,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'HighPerformanceSuppliersByCategory',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfxVolByCatBARheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'HighPerformanceSuppliersByCategory', rfxVolByCatBARheaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score (%)',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#rfxVolumeByCategoryDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td>' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#rfxVolumeByCategoryDataTable').append(html);
			}

			//			$('.rfxVolByCat-head').html('Total Volume: ' + totalOfDataRfxVolByCat);

			if (type == 'pie') {
				$('#topHighPerformanceSuppByCatPieIconId').addClass('icon-active');
				$('#topHighPerformanceSuppByCatBarIconId').removeClass('icon-active');
			} else {
				$('#topHighPerformanceSuppByCatBarIconId').addClass('icon-active');
				$('#topHighPerformanceSuppByCatPieIconId').removeClass('icon-active');
			}
			rfxVolumeByCategoryBarChart.hideLoading();
			rfxVolumeByCategoryBarChart.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			rfxVolumeByCategoryBarChart.hideLoading();
		}
	});
}


function getTopLowPerformanceSuppier(header, token, type, startDate, endDate) {

	var lowPerformanceBarChart = echarts.init(document.getElementById('lowPerformanceSuppBarChart'));
	lowPerformanceBarChart.showLoading();
	lowPerformanceBarChart.clear();
	var url = getContextPath() + '/spAnalytics/getTopLowPerformanceSuppier';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfData = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfData = totalOfData + element.score;
				console.log(">>>>>> " + totalOfData);
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					supplier: (item.name ? item.name : ''),
					score: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplier',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfsCatVolPieheaders = ["RANK", "SUPPLIER", "SCore", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplier', rfsCatVolPieheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 25,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplier',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAMAAADDpiTIAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAAztAAAM7QFl1QBJAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAwBQTFRF////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACyO34QAAAP90Uk5TAAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+6wjZNQAAHJlJREFUeNrtnXdgFcX2xzeF0AkBpPNAmoAFNRp9NjqSpzQBKQKKUkSKWBCQpsgTFBUBlS5IUxGRHkAxCKIIglEELKg0iRDpIaTe81P86aPce+fs7szNbOb7/Td3z9k955MtZ87MWJYCFb132qqkIz4yTseT3qpnma6ILivTyWDtqG92/lvsJMOV/YjB6a+ykSB6ydj810tB9v/UU4bmv1cmcn9OvvZG5r8TMv+30m83MP+xaUj8PzpWy7j8l96PtJ+nX8qYBsBsJP0CbS1sVv7r5iDnF2p5hFEArEHGL9Zkk/Ifh3xfqkEGATAW6fZTDuhgDgC7kW5/5YA7TMl/TSTbfzmgtiEA3IdcBygHlDUDgIFIdQB9aUY5YDwyHUgrjCgHvINEB9QUEwBI8HPhvcqaoatFBAw2AIDVfq7blI/gssJyQEcAYDQAlFEPABgNAB2vDQCMBoD2lgUARgNA2woDAKMBoJURAMBoAGgqADAbABoCAMwGwNcJABgNAGXUBwBGA0DH6wAAowGgveUAgNEA0LYiAMBoAGhVJAAwGgCaBgDMBoCeBgBmA+C7DwAYDQBlNAAARgNAx68EAEYDQPvKAQCjAaDtRQCA0QBQQiQAMBoAmg4AzAaAhgIAswGgzgDAbAAyGgIAowGgE1cCAKMBoP3lAYDRANBXRQGA0QDQ6kgAYDQANAMAmA0ADQMAZgNAXQCA2QBkNgIARgNAJ64CAEYDkDfKAQDAhZKKAgCjAaA1kQDAaABoJgAwGwAaAQDMBoC6AgCzAfB6OQAAuNXJqwGA0QDQgQoAwGgAvF0OAAAStDYSABgNAL0JAMwGgEYCALMBoPsBgNkAZDYGAEYDQCevAQBGA+DVcgAAkKaviwEAowGgtfkAgNEA0CwAYDYA9AwAMBsA6gYAzAYgswkAMBoA75UDAIBkHawIAIwGgL4pBgCMBoA+zAcAjAaAZgMAswGgZwGA2QDQgwDAbACymgIAowGgU3UBgNEA0K+VAIDRANCOaABgNAD0UT4AYDQA9BYAMBsAGgUAzAaAHgIAZgOQdScAMBoAOnUtADAaAA+UAwCAWmlfDgAAirUuHwAwGgCaAwDMBoCeAwBmA0DdAYDZAGQ1AwBGA0CnrwMARgNAh/4FAIwGgL6NBgBGA0AfRwEAowGguQDAbABoNAAwGwDqAQDMBiArHgAYDYCe5QAAEELpWA4AAKHUzuIAwGgAKDEKABgNAM0LAwBGA0D9AIDZABwvBQCMBoCmAACzAcgpAQCMBoDqAQCzAegDADRRmdwB4A0AoInCTuYKAEsAgC76OFcAWA0AdNFYAGA2AC0AgNkAhCUCAKMBsKqcBgBGA2D1AABmA2C1TQEARgNglV4MAIwGwLJqdnwp8TtF+tVPeBMAgDnqgDsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACA0QCUavnI6Flrduw9T2f9nGHKXkiS/G1Jdfb8H+xYM2v0Iy1LhSL7NZ7ckEOQlspeP6CK2uznf2IXwqy3vuqTT1n6w7vsQ4D1108dw9Tkv+lXCK43tK2JgvRHTUdgvaPpUbLzf9kGRNVL2nCZ3Pxfsxcx9Zb2XiP18Z+KiHpNqU3l5b/OScTTezpZR1b+S+xBNL2oPSXk5D9yHWLpTa2LlALARETSq5okI/83+hBIr8p3owQAEhFH7yrRff7vQhS9rLtcDwDtQBC9rB3hLgHoihh6W11dArAWIfS21rrLf3QmQuhtZRaT3o2oQKe/XTVlyP1t4u+4oXblUoVjKtWJa9jyvl5PPvPS1CW7MuS7O/XTltXzJz3Tv3P8TdVLFK1QK65R6659h4x5bU7ib3mPgPauAHhb9ekdXj68xbUxwU4holr8o69/tF9WNeK7Nx+qFaRjpsSt3V9J2JuXSh/z3eQ/3wmFZ5b6ybh2ldmnUui6Actdjkmd3Ti2Oa9xtnBsz0XHgtr6djBD492GaAvDyUKBjeNu6sFXKcv+nlca2D+xyJuHrjvr9Hto4M32umQibh7xaVbgOxfn8yryuMsodWM4mS0ycqWbPgAlyc/ZNMj5SGWBRrPSbHvMWljP2Ttwq8mB3gru4By/0F2gfKUZrXrCm7SbBsEHFKQ/6WG33Uoxj+625TH52fIuxkJbr/Q7D+JVzsHd3IVqM8NFc6GVB1yE+mnZ2U+bfbOUEcp6H7FdbmzvtlG+4gg/DXH7OEeWdfc2OZThYq7QytMuLv01uenfPSBGVpPKOO47sJTWuPCmlyJwA+fAba7iVZcxVUf8YvyaiwtfLDP9WyU2qTEje+huWXNijl5i+3nOcaPdBGw/w0ErsZnFkif9Ov3vbyOzSzWGNT9xrrQbTpyfggLnuFvchOwNhoMFYjOrdQBgX7cIqW3qDPApuYU8f8P82Od8yUQcdRG0/4jtFzztCQCOPppf8jwVRpva/BIS/W3042CY4lLqmQJi8/eQFwB4v4z0iWrCLoXDrWW6K+avIrRd8XD6Uob5dzwAwOF28icqlhbW/cpL9dfSr5PLOWfq/EOwO6M+nqo/AAtULFvRXuB0U4xcf2/49fI459CtjsuA5cTG25HuABxqaanQ1OBeVxaS7M//zJhPOYc+6zR0XzKMv6c7AJvKKsm/9UNQr6siJburFmBMg3N1NzmN3UjGoGWa5gDMjFKT/4pBvW4vIttf7wCeHuaUkFIcBi9WbJvXrpNrAGQ/ailS0FfrfeWk+1sSwBWr326es+j9yljnZbHWABxtrCr/1qwgbk9dKd1dZKB6exbnXfM+Z+GbJrZc9KzOAHxXTVn+rX0ueyhs6raAzjg916WcrarXXGy5E2kMwA/l1OW/mqKRj0B6LqC3JZzDNztqXyvk/MmkAwB7KqjLv9U9SP1fRdXhi8BpKsw4fISTAK5klCfT9QXg50oK8x9sBKy1AnclgtzD2zKOv8FJBBkfGF1IWwD2VVGZfys5cN1Bhbt7g9U5GceHHXYQwopiu8u1BeBAVaX5rxPY820q/M0McqknOZWOt+yH8Cux1eIZugKQepXS/Ft9AnpersTfAZdj9k6mVz0ntspu2A05AO3V5t96P6Dn6+yaCitaodplgsJx7aAXO4PzEpFtO4ZxYqurdAXgZafOqt7WqseQV+YkbN2b8nPShhULprw4rEvspWXdsIBNNpvtOGs3Zu3+k3+93p3al5S4eOa4wXf57VsYEPRqUzidTpvsxvA3cRkwJlNTAD521PtVo+eCQ/4HRfetHt/z3+f/k17rvgZ05ZgApaT9i4c0ibH3n1af4W+o3SC+Kbb5IOkJwAH7sz6qPjTvoMDqqWX9a//988cD/egEcxD4niRBDeOtluc1Y+U/E/zXExker7cbxdYyUxNSANLjbHoo2OdHpumDs1ufe+VeEegHb7Ac1v6Q4ev0223+pqmRiHjGoE1Yss0oisczS2bpCcBQe/ajh9j6Rv590o1W5Ck3L+RWd+6j88x7bc49zF6U8L5mvWkvioyU9SAtAdhpaxZW2bH253vvej3gv00h2U/jfU/98T6QJPrVWIbbdvYusq/Y4odaAuC73c6jf7LTmd7+9RHD5zCbNs9Mri9s6vyB4bd4li234kLqZdlaAjDDhumH00muBop9xmWRAnHqXra229jBiB7pCMAR/lSMwvOl50G8AkD+71Xkn0YwrnewHYNjxPY+1hKAzmy7tXfKz4N4ILi9kvxTEuOC69oxeIvQXJkcHQFYzzbbScEGJEckFk9titP7dJBvLkW8+swjpCMA3FVY8r+hIgti/KKzFQHwJOOiZ/DNvSW2tl5HADZyv/03K8mCuInyWkX5p8845Ue+OfFUunI5OgJwJ7P0p2j/uReFnluqAsDHmIhYjD12w1jasx9pCMBWnsV8KxVlQTxZW926148wLjxRYj1jo44AMCcBLlCVhP5C1w2VAcCpQQ3kGhsgNFXBpyEA3/A2LO6nLAniseBqynxnlRRfOXvNTfE3xQDSEICOLHvXpStLwgNC51EZuejcsvbzTO0WW/pMQwBOsNaAKfKDshTQ4Fx8/NAyxsVPlfUyW8mnIQC8UYBp6vLPWbnzNmXOGQP43I8Q8RK0j5OGAHAao6zYHIUAvMs4gS+Ueb+XcftjPYGOiTvqNmsIAKctxgr7TGH+6RPOCPQxVd7fYXhnrW4rXt2/MmkIwAscY11U5p81LG/Fq7oHnWa8A7Fu3eKX6YE6AnA1w1bUQaUAnGZdUPcsRe4Za9LW5nxQilcc2KohAN9wbKnegK4w64oaHFXjndHIbf0iY0irKmkIwHCOra8UA8AcjKy2XYn33xkLUzFGQZ+Q21oSKgA4u2fUV5x/eoV5TeFd9qpw31Ds+W6xlSuERrZrCEA6pwq0RDUAP7GvKv/jCp4DjFX5CwnroD8KbVQnDQHYwLBUMks1AGRjUnL0mDTZ3jmreq1xfxd7WkcARjMsPaQ8//YmpVSYKbs/iLEXzgD3z5EkHQHg7ASSoB6ALfaurc7MM1Ldi4v4Vk3RiIpwXs0VpCEAWYxCeEwI9iD22V0hPLrvtxLd72F43OO2nD1cRwC+0KAIcE7P2r/A2+bKG6BmbE81KbgFcV/9Dh0B4Hx/zQwFAKlO1iYs+cT3oeMvPqiBbGFfSW3SEYAeDEO7QgEATXd0kWENF0p5QDFmdBUM+vEhXn7+GS0BaCC2Uzw023FnO10nuMzT+yS4ryF2FHRuyiA1/0fKAWAsadeUQqOVjq80vHmCa0jFCQzeEynE19le3qoBSGNUQEaGCABOQTbwKMFLLguEjLfhYI2pPwuPHqUlAN8y7LwbKgCSCri4WqvAA1tcfYcy7oVBPgTFqw19pyUAHzDsbA4VADTPcqcb3VSs+rkaERQucVOXtARgHMNOcsgAoCEuCbAaOO8cTBRbD9waKl7i5r96AtCTcWv1hQ4AXyu3BFhtnVYGssVrFBQLOCgmnl70o54AMCaF1qAQKrWuawIiex1y5vshse2A8/qES9xcR3oCcKvYTKNQAkD7SrsmwCo03NE0IsZnaMBqvrCvcqymADD+4VqHFADaJmOv4rpOBorSxVO7A+0j+KvwyJ80BaC62Ezn0AJAe+tIIKDARAeexV3d4QHmJswSHXgDaQoAYwSmZ4gBoBONJBBgNbP/8fKe2GqA7V47iI57UVcAxHc967FQA0CZUnaPK7XU9huouBDlf5HXHOFI4F5dAWAsDz+UQq/nwmQg8ITdD1jxOhmVnZWRHe9BrBqAdIaZ0bkAAL0nZQe5HjYnkzGW+PJb0B0lOuplXQH4nWFmUG4AQEc6yCCgs73m0WPiCSITnXxMh+3XFYADDDPdKXe0TMb2lW3sdYs0ERr0Nz/kuOhB+m/SFYDfGGZa5RIAdLKnhDeBu2ytaj5ZaK+IH6IWiQ56VVsATjHM3E65psTq7gloYmdSS7J4oVc/63yK2urCDmoLQDbDTJ3cA4AypvzLNQG2vmLEpXE/s3tE5+hmdRvVn4GMiYGlKTeVObWySwDC7azOLt4279Ka3i5HL46aACBe0sCK9FHuIjCtijsCyqfwnYk7u8IvsTZedMQhjQHgvGkfp1xW5vTLXRFwtw1f4v1L3774kGaCA+qRxgAwmqFFU6JCoewVrSJdRGKCjRqk0Fi3i444W1BwwOs6A3A9w85m0kHJY6o5jkR+fil+p9BYxYuOWCN6AvymMwAtGHYWkR7yreuY32EobKxyLF7m46Idcx4X/LwB6QxAf4ad/qSNjo6v5SgUBY+wXYj7UsdfeIBoRsgUrQHg7BZ+Nemkj9vlcxAL/p6D4q0T4m1V0yOOaA3A+ww7YSlaEUDJo+1Xh2JOsc0LjV+4WpBooeXGpDUA2ziG3iPNlLMsPtxmMMaxjT8qtJV4/s9FmwRN0xsAzniwvX3OQqSfB9trHy7Pbg0Qr1t8wRwPQQ9r5O96A0CMFWKcrWygfpxgclk70fiSfXsRknXXeb8WrQ13J2kOwA0cS8laEkCpo4qqeAYI18woed6PRf3AM3UHoA/H0tukqY70ZEcjnm00wU5fmGA6Ub5jugPwFsdST9JWyy9jRqMIuy8gI1pkaxa7bhRPugPwHcdSyVR9CfgtnhkO/o4X94lM/a85XLTp8WztAfAV55h6RV8AyNeLFw7+/GxhceR/q70sDv7DqOPaA8BaKdSqkKExAYwv9z/VjG3vjGh8L/wEcyDgbtIfgGEsW9N1BoC1+6udinZrka1/Fo6OC/67OR4AIIFlq0aOzgCkcdoayvHtzRXZGvn3vSL4uET+kx4AgLNr3h96R+tbwCeMBvJ8fHPHReNNf6+c93Hwn0nY8jwEO4a0ZRm7VmsAWBdxgm9OtHBK9P/fEAVzwuZ7AoC5PGurtAZgqdzWtqkiWzs4b9AFTnkCgGO8brvrtf4QyCghtbXtN9Fg4197CWcHL0XLWFslFPsGMhfo7O31Z8AKG+ZuF9h64Nyvtqt/bwoFABOZ9uboDMBj4vO3s2CEqNe/JiNyBVM9AsDBCJ69Ql9rDABjxUs7K8nuFRk7N8wffNfptuQRAOgepsHqJ/QFYLb49A/YsRcrMLb8zx8F3+ZmoWcAWM+12MKnLQAviVsbbe1991+BtT/niAafR1b4jGcAYG0ffU7PawuAuL/d3iTX3QJrf+6lOifoL9qTdwCYxjUZsUhXAITle7vLddcObq1wlmih5fc9BMCZGDYBM/TMP2O1/2b2LIp2stxGFHRFyyJpHgKAnuRbfUFLABhrvXezZ1HUMP8aHQ06ANGRvARAcmG+2X46lgS7i8/b7sa9goUpOtGyoH9f4ikAaKQNu7E/qkniUuevzQcY7cF2u7MEpaXL6algfy6W7i0AUu302BedpyL/Z/OXGPKrwzeAJoy3F7tz9DYKDCbfEuyvspbYDhUA4vGvC9T4UzWP8aiuSU4OfZ1xyvXsGs0RzPnpEBXsr8u8BkC2zUXam2ySDcAzfxlutNJ289FCzrIB9tfq6+Ui9tEZXgOAlts13/Qz+7fqYPv5/LOHabk+620x8Cproqj93UXXuIj9/eQ5AFiLhVyoOz+3dUvd0K9CmcB/zji/FbdM73XcVX5TeJODYu3HPrO489iv9CAAhx3s1nPTiI28CnvKsj7nXjMPsd+5Luv5IeM2mv5CNO9Mnax53sVx6GMyPQiA/YfAX58EzSfuDv52kTS5aw1xU8ZoP021cX3nfBds/Gn3SPZSEU52bv7Acei7kRcBoJ5OPVXsNv/zPZe0QPsObpg1vNPNF3QdPxfQeaAvuehGQz7wt9buqU3P29hi7gonwU8rHLLQ6wFAag3LjaLK123csd+oSeOGD3iwXbNbahWw0yaXGTTYxS6Pbdqxz4gJ81Z98fna92dPGtG6qr2VxCc4in4bh5EomeVNAGhzpKVYlQO5/lyp20rO6nLzHbqTuMVCaAGg6aoBsAJt8j5WqdepzqJ/MsqZu7WeBYA5U9CFPgrgOF6l06pOb8nOzqpUtncBcPHpw1OAlVqyi6p0utxp+J3dEXuRhwHIbKgWgADD5FtV+nReljsS4cTfOi8DQCeuUgpALf9exyl0WcHFKg31HPgrne1pAOjXK1UCEO5/tsTdCl26mdU4wYE/qVOocgEAOnarSgL8DiTnFFfncLib+O934HC91wGgNJX/j5P8edyuzl9bd3MZbrTtsGyO5wGgrAfUJcRvmXy8MnexLqdnjLHtsS95HwAK3u/mSn5XmmilytvlB10m4HvbLjfkCQDo7WhFKcnnZ5DXV1KRsysOus6A3Vfi8r68AQD9couipGy71Nc3ilxdc9h9Bobb9Pko5REAKHt4uJKs+FlxbpKa/N90TEIGvrLpdFOeAYDok0oq0uLnM7mtkvz3ktOab2/Pwkq+PAQAHe+vYHz45kv9lFaQ/iLzJaXgCVtuH6O8BADRbvmjdIUuqZTuUpD/q3fLSsEmW3435zEAiFbVkp2bnRe7mCw9/YVfkNaVSb5yNhxXpjwHAGVNkPyRNveSQVfZY8HtDsjMQW8bnp/MgwAQpU2tIzM9j1/aeTO+qszi34dyc/ChDd9b8iQAf2jtf8KkJai+v9GgJfUlWb9jtewcZJVgO7+c8ioARN/3KSIlQZEtE/w7SOpW0LXxsHgFs1bpfrb/QXkYAKKzS7rGuM3QFS8E2Us7bWXfam6MV3/2Z1Khpewz2JanAfhDmWt6Ov9mD7+2n/j/84cJzQo4sl6i1yZSpLPce181yusA/Pm0/mTknfZvBPlvH7KKu8xk2qp+dewVoKLqj96cTerUjnkeQ0wA4Nyn8fdzHonlbuJdMq7j8xvtVmXTv54/pHkVzotnhfjBCWdIrb6ZwtN+UwD468a47f2X+zW/pljAF7JK9R96/t1tbpaXPb15xoDGVUv4b82Nrt24+4TEo5S3pTEAf+vY9rVLFsyYMGb4E70f7Dto1CtT532w9rOv9ySny3Nx+sCOT1fMe230wB49+j81csyrU+cn/pBKRsgDAEAAAAIAkH4ALEL4vK9FLgCYiPB5XxNdADAI4fO+BrkAoAvC5325ma7fCOHzvhq5AKAWwud9uWnICz+M+HldR1zNzZiJAHpdb7rqvmiBAHpdLV0BUPAMIuhtpRVy14C1BCH0tpa57MBriRB6W21cAmB9ihh6WZtdt0nfgiB6WXe478JfjCia+wZwrg8/C3H0qrKlTMh7GoH0qkZaUrQAkfSmFkqajVlwK2LpRW0rZElShUOIpveUXFHefPnrkxFPz+X/epkrMlTajoh6S9slr89WCA3CntKiQpZkhY3yIaxekW9UmCVfdRMQWW8ooa6lRg22ILj6a0sDS53aJGQgwjorI6GNpVbF2r9zEnHWUyff6VDMCoGi4lr1HjV9xfrz5G9R7V3rIUnyt3H5sfN/sGL6qN6t4qKsXJO/acQdLEiSOvgJ72qtzhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQOIAAAQAIAAAAQAIAEAAAAIAEACAAAAEACAAAAEACABAAAACABAAgAAABAAgAAABAAgAQAAAAgAQAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABA4gAAZDAAS8dCkrTUkwBASgUAAAAAAAAAAABooQQkJNRK0AqAd5GQUOtdrQAYj4SEWuO1AmAgEhJqDdQKgM5ISKjVWSsAaiIhoVZNvarVu5GR0Gq3ZsMVY5GS0GqsZgDEISWhVZxuI5ZrkJNQao12Q9Z1c5CV0Cmnrn5NC7ORltBptoZdK6X3Iy+h0v7SOvYtxaYhM6FRWqyenWudkJrQqJOuvYu9MpEc9crspW/3ar0U5Ee1Uurp3L9cZSMypFYbq2jewt5iJ5KkTjtb6D+JIaLLynRkSoXSV3aJ8MZElqL3TluVdMSHlMmS70jSqmn3FlWRq/8DuRPhbREQOwkAAAAASUVORK5CYII=',
								onclick: function() {
									var rfsCatVolheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplier', rfsCatVolheaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#lowPerformanceSuppDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td>' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#lowPerformanceSuppDataTable').append(html);
			}

			//			$('.rfsCategoryByVolume-heading').html('Total Volume: ' + data.recordsTotal);

			if (type == 'pie') {
				$('#lowPerformanceSuppPieIconId').addClass('icon-active');
				$('#lowPerformanceSuppBarIconId').removeClass('icon-active');
			} else {
				$('#lowPerformanceSuppBarIconId').addClass('icon-active');
				$('#lowPerformanceSuppPieIconId').removeClass('icon-active');
			}
			lowPerformanceBarChart.hideLoading();
			lowPerformanceBarChart.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			lowPerformanceBarChart.hideLoading();
		}
	});
}

function getTopLowPerformanceSuppByBU(header, token, type, startDate, endDate, buId) {

	var lowPermByBUBarChart = echarts.init(document.getElementById('lowPerformanceSuppByBUBarChart'));
	lowPermByBUBarChart.showLoading();
	lowPermByBUBarChart.clear();

	var url = '';
	url = getContextPath() + '/spAnalytics/getTopLowPerformanceSuppByBU';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
			'buId': buId
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfData = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfData = totalOfData + element.score;
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					supplier: (item.name ? item.name : ''),
					score: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplierByBusinessUnit',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAMAAADDpiTIAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAAztAAAM7QFl1QBJAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAwBQTFRF////AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACyO34QAAAP90Uk5TAAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+6wjZNQAAHJlJREFUeNrtnXdgFcX2xzeF0AkBpPNAmoAFNRp9NjqSpzQBKQKKUkSKWBCQpsgTFBUBlS5IUxGRHkAxCKIIglEELKg0iRDpIaTe81P86aPce+fs7szNbOb7/Td3z9k955MtZ87MWJYCFb132qqkIz4yTseT3qpnma6ILivTyWDtqG92/lvsJMOV/YjB6a+ykSB6ydj810tB9v/UU4bmv1cmcn9OvvZG5r8TMv+30m83MP+xaUj8PzpWy7j8l96PtJ+nX8qYBsBsJP0CbS1sVv7r5iDnF2p5hFEArEHGL9Zkk/Ifh3xfqkEGATAW6fZTDuhgDgC7kW5/5YA7TMl/TSTbfzmgtiEA3IdcBygHlDUDgIFIdQB9aUY5YDwyHUgrjCgHvINEB9QUEwBI8HPhvcqaoatFBAw2AIDVfq7blI/gssJyQEcAYDQAlFEPABgNAB2vDQCMBoD2lgUARgNA2woDAKMBoJURAMBoAGgqADAbABoCAMwGwNcJABgNAGXUBwBGA0DH6wAAowGgveUAgNEA0LYiAMBoAGhVJAAwGgCaBgDMBoCeBgBmA+C7DwAYDQBlNAAARgNAx68EAEYDQPvKAQCjAaDtRQCA0QBQQiQAMBoAmg4AzAaAhgIAswGgzgDAbAAyGgIAowGgE1cCAKMBoP3lAYDRANBXRQGA0QDQ6kgAYDQANAMAmA0ADQMAZgNAXQCA2QBkNgIARgNAJ64CAEYDkDfKAQDAhZKKAgCjAaA1kQDAaABoJgAwGwAaAQDMBoC6AgCzAfB6OQAAuNXJqwGA0QDQgQoAwGgAvF0OAAAStDYSABgNAL0JAMwGgEYCALMBoPsBgNkAZDYGAEYDQCevAQBGA+DVcgAAkKaviwEAowGgtfkAgNEA0CwAYDYA9AwAMBsA6gYAzAYgswkAMBoA75UDAIBkHawIAIwGgL4pBgCMBoA+zAcAjAaAZgMAswGgZwGA2QDQgwDAbACymgIAowGgU3UBgNEA0K+VAIDRANCOaABgNAD0UT4AYDQA9BYAMBsAGgUAzAaAHgIAZgOQdScAMBoAOnUtADAaAA+UAwCAWmlfDgAAirUuHwAwGgCaAwDMBoCeAwBmA0DdAYDZAGQ1AwBGA0CnrwMARgNAh/4FAIwGgL6NBgBGA0AfRwEAowGguQDAbABoNAAwGwDqAQDMBiArHgAYDYCe5QAAEELpWA4AAKHUzuIAwGgAKDEKABgNAM0LAwBGA0D9AIDZABwvBQCMBoCmAACzAcgpAQCMBoDqAQCzAegDADRRmdwB4A0AoInCTuYKAEsAgC76OFcAWA0AdNFYAGA2AC0AgNkAhCUCAKMBsKqcBgBGA2D1AABmA2C1TQEARgNglV4MAIwGwLJqdnwp8TtF+tVPeBMAgDnqgDsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACA0QCUavnI6Flrduw9T2f9nGHKXkiS/G1Jdfb8H+xYM2v0Iy1LhSL7NZ7ckEOQlspeP6CK2uznf2IXwqy3vuqTT1n6w7vsQ4D1108dw9Tkv+lXCK43tK2JgvRHTUdgvaPpUbLzf9kGRNVL2nCZ3Pxfsxcx9Zb2XiP18Z+KiHpNqU3l5b/OScTTezpZR1b+S+xBNL2oPSXk5D9yHWLpTa2LlALARETSq5okI/83+hBIr8p3owQAEhFH7yrRff7vQhS9rLtcDwDtQBC9rB3hLgHoihh6W11dArAWIfS21rrLf3QmQuhtZRaT3o2oQKe/XTVlyP1t4u+4oXblUoVjKtWJa9jyvl5PPvPS1CW7MuS7O/XTltXzJz3Tv3P8TdVLFK1QK65R6659h4x5bU7ib3mPgPauAHhb9ekdXj68xbUxwU4holr8o69/tF9WNeK7Nx+qFaRjpsSt3V9J2JuXSh/z3eQ/3wmFZ5b6ybh2ldmnUui6Actdjkmd3Ti2Oa9xtnBsz0XHgtr6djBD492GaAvDyUKBjeNu6sFXKcv+nlca2D+xyJuHrjvr9Hto4M32umQibh7xaVbgOxfn8yryuMsodWM4mS0ycqWbPgAlyc/ZNMj5SGWBRrPSbHvMWljP2Ttwq8mB3gru4By/0F2gfKUZrXrCm7SbBsEHFKQ/6WG33Uoxj+625TH52fIuxkJbr/Q7D+JVzsHd3IVqM8NFc6GVB1yE+mnZ2U+bfbOUEcp6H7FdbmzvtlG+4gg/DXH7OEeWdfc2OZThYq7QytMuLv01uenfPSBGVpPKOO47sJTWuPCmlyJwA+fAba7iVZcxVUf8YvyaiwtfLDP9WyU2qTEje+huWXNijl5i+3nOcaPdBGw/w0ErsZnFkif9Ov3vbyOzSzWGNT9xrrQbTpyfggLnuFvchOwNhoMFYjOrdQBgX7cIqW3qDPApuYU8f8P82Od8yUQcdRG0/4jtFzztCQCOPppf8jwVRpva/BIS/W3042CY4lLqmQJi8/eQFwB4v4z0iWrCLoXDrWW6K+avIrRd8XD6Uob5dzwAwOF28icqlhbW/cpL9dfSr5PLOWfq/EOwO6M+nqo/AAtULFvRXuB0U4xcf2/49fI459CtjsuA5cTG25HuABxqaanQ1OBeVxaS7M//zJhPOYc+6zR0XzKMv6c7AJvKKsm/9UNQr6siJburFmBMg3N1NzmN3UjGoGWa5gDMjFKT/4pBvW4vIttf7wCeHuaUkFIcBi9WbJvXrpNrAGQ/ailS0FfrfeWk+1sSwBWr326es+j9yljnZbHWABxtrCr/1qwgbk9dKd1dZKB6exbnXfM+Z+GbJrZc9KzOAHxXTVn+rX0ueyhs6raAzjg916WcrarXXGy5E2kMwA/l1OW/mqKRj0B6LqC3JZzDNztqXyvk/MmkAwB7KqjLv9U9SP1fRdXhi8BpKsw4fISTAK5klCfT9QXg50oK8x9sBKy1AnclgtzD2zKOv8FJBBkfGF1IWwD2VVGZfys5cN1Bhbt7g9U5GceHHXYQwopiu8u1BeBAVaX5rxPY820q/M0McqknOZWOt+yH8Cux1eIZugKQepXS/Ft9AnpersTfAZdj9k6mVz0ntspu2A05AO3V5t96P6Dn6+yaCitaodplgsJx7aAXO4PzEpFtO4ZxYqurdAXgZafOqt7WqseQV+YkbN2b8nPShhULprw4rEvspWXdsIBNNpvtOGs3Zu3+k3+93p3al5S4eOa4wXf57VsYEPRqUzidTpvsxvA3cRkwJlNTAD521PtVo+eCQ/4HRfetHt/z3+f/k17rvgZ05ZgApaT9i4c0ibH3n1af4W+o3SC+Kbb5IOkJwAH7sz6qPjTvoMDqqWX9a//988cD/egEcxD4niRBDeOtluc1Y+U/E/zXExker7cbxdYyUxNSANLjbHoo2OdHpumDs1ufe+VeEegHb7Ac1v6Q4ev0223+pqmRiHjGoE1Yss0oisczS2bpCcBQe/ajh9j6Rv590o1W5Ck3L+RWd+6j88x7bc49zF6U8L5mvWkvioyU9SAtAdhpaxZW2bH253vvej3gv00h2U/jfU/98T6QJPrVWIbbdvYusq/Y4odaAuC73c6jf7LTmd7+9RHD5zCbNs9Mri9s6vyB4bd4li234kLqZdlaAjDDhumH00muBop9xmWRAnHqXra229jBiB7pCMAR/lSMwvOl50G8AkD+71Xkn0YwrnewHYNjxPY+1hKAzmy7tXfKz4N4ILi9kvxTEuOC69oxeIvQXJkcHQFYzzbbScEGJEckFk9titP7dJBvLkW8+swjpCMA3FVY8r+hIgti/KKzFQHwJOOiZ/DNvSW2tl5HADZyv/03K8mCuInyWkX5p8845Ue+OfFUunI5OgJwJ7P0p2j/uReFnluqAsDHmIhYjD12w1jasx9pCMBWnsV8KxVlQTxZW926148wLjxRYj1jo44AMCcBLlCVhP5C1w2VAcCpQQ3kGhsgNFXBpyEA3/A2LO6nLAniseBqynxnlRRfOXvNTfE3xQDSEICOLHvXpStLwgNC51EZuejcsvbzTO0WW/pMQwBOsNaAKfKDshTQ4Fx8/NAyxsVPlfUyW8mnIQC8UYBp6vLPWbnzNmXOGQP43I8Q8RK0j5OGAHAao6zYHIUAvMs4gS+Ueb+XcftjPYGOiTvqNmsIAKctxgr7TGH+6RPOCPQxVd7fYXhnrW4rXt2/MmkIwAscY11U5p81LG/Fq7oHnWa8A7Fu3eKX6YE6AnA1w1bUQaUAnGZdUPcsRe4Za9LW5nxQilcc2KohAN9wbKnegK4w64oaHFXjndHIbf0iY0irKmkIwHCOra8UA8AcjKy2XYn33xkLUzFGQZ+Q21oSKgA4u2fUV5x/eoV5TeFd9qpw31Ds+W6xlSuERrZrCEA6pwq0RDUAP7GvKv/jCp4DjFX5CwnroD8KbVQnDQHYwLBUMks1AGRjUnL0mDTZ3jmreq1xfxd7WkcARjMsPaQ8//YmpVSYKbs/iLEXzgD3z5EkHQHg7ASSoB6ALfaurc7MM1Ldi4v4Vk3RiIpwXs0VpCEAWYxCeEwI9iD22V0hPLrvtxLd72F43OO2nD1cRwC+0KAIcE7P2r/A2+bKG6BmbE81KbgFcV/9Dh0B4Hx/zQwFAKlO1iYs+cT3oeMvPqiBbGFfSW3SEYAeDEO7QgEATXd0kWENF0p5QDFmdBUM+vEhXn7+GS0BaCC2Uzw023FnO10nuMzT+yS4ryF2FHRuyiA1/0fKAWAsadeUQqOVjq80vHmCa0jFCQzeEynE19le3qoBSGNUQEaGCABOQTbwKMFLLguEjLfhYI2pPwuPHqUlAN8y7LwbKgCSCri4WqvAA1tcfYcy7oVBPgTFqw19pyUAHzDsbA4VADTPcqcb3VSs+rkaERQucVOXtARgHMNOcsgAoCEuCbAaOO8cTBRbD9waKl7i5r96AtCTcWv1hQ4AXyu3BFhtnVYGssVrFBQLOCgmnl70o54AMCaF1qAQKrWuawIiex1y5vshse2A8/qES9xcR3oCcKvYTKNQAkD7SrsmwCo03NE0IsZnaMBqvrCvcqymADD+4VqHFADaJmOv4rpOBorSxVO7A+0j+KvwyJ80BaC62Ezn0AJAe+tIIKDARAeexV3d4QHmJswSHXgDaQoAYwSmZ4gBoBONJBBgNbP/8fKe2GqA7V47iI57UVcAxHc967FQA0CZUnaPK7XU9huouBDlf5HXHOFI4F5dAWAsDz+UQq/nwmQg8ITdD1jxOhmVnZWRHe9BrBqAdIaZ0bkAAL0nZQe5HjYnkzGW+PJb0B0lOuplXQH4nWFmUG4AQEc6yCCgs73m0WPiCSITnXxMh+3XFYADDDPdKXe0TMb2lW3sdYs0ERr0Nz/kuOhB+m/SFYDfGGZa5RIAdLKnhDeBu2ytaj5ZaK+IH6IWiQ56VVsATjHM3E65psTq7gloYmdSS7J4oVc/63yK2urCDmoLQDbDTJ3cA4AypvzLNQG2vmLEpXE/s3tE5+hmdRvVn4GMiYGlKTeVObWySwDC7azOLt4279Ka3i5HL46aACBe0sCK9FHuIjCtijsCyqfwnYk7u8IvsTZedMQhjQHgvGkfp1xW5vTLXRFwtw1f4v1L3774kGaCA+qRxgAwmqFFU6JCoewVrSJdRGKCjRqk0Fi3i444W1BwwOs6A3A9w85m0kHJY6o5jkR+fil+p9BYxYuOWCN6AvymMwAtGHYWkR7yreuY32EobKxyLF7m46Idcx4X/LwB6QxAf4ad/qSNjo6v5SgUBY+wXYj7UsdfeIBoRsgUrQHg7BZ+Nemkj9vlcxAL/p6D4q0T4m1V0yOOaA3A+ww7YSlaEUDJo+1Xh2JOsc0LjV+4WpBooeXGpDUA2ziG3iPNlLMsPtxmMMaxjT8qtJV4/s9FmwRN0xsAzniwvX3OQqSfB9trHy7Pbg0Qr1t8wRwPQQ9r5O96A0CMFWKcrWygfpxgclk70fiSfXsRknXXeb8WrQ13J2kOwA0cS8laEkCpo4qqeAYI18woed6PRf3AM3UHoA/H0tukqY70ZEcjnm00wU5fmGA6Ub5jugPwFsdST9JWyy9jRqMIuy8gI1pkaxa7bhRPugPwHcdSyVR9CfgtnhkO/o4X94lM/a85XLTp8WztAfAV55h6RV8AyNeLFw7+/GxhceR/q70sDv7DqOPaA8BaKdSqkKExAYwv9z/VjG3vjGh8L/wEcyDgbtIfgGEsW9N1BoC1+6udinZrka1/Fo6OC/67OR4AIIFlq0aOzgCkcdoayvHtzRXZGvn3vSL4uET+kx4AgLNr3h96R+tbwCeMBvJ8fHPHReNNf6+c93Hwn0nY8jwEO4a0ZRm7VmsAWBdxgm9OtHBK9P/fEAVzwuZ7AoC5PGurtAZgqdzWtqkiWzs4b9AFTnkCgGO8brvrtf4QyCghtbXtN9Fg4197CWcHL0XLWFslFPsGMhfo7O31Z8AKG+ZuF9h64Nyvtqt/bwoFABOZ9uboDMBj4vO3s2CEqNe/JiNyBVM9AsDBCJ69Ql9rDABjxUs7K8nuFRk7N8wffNfptuQRAOgepsHqJ/QFYLb49A/YsRcrMLb8zx8F3+ZmoWcAWM+12MKnLQAviVsbbe1991+BtT/niAafR1b4jGcAYG0ffU7PawuAuL/d3iTX3QJrf+6lOifoL9qTdwCYxjUZsUhXAITle7vLddcObq1wlmih5fc9BMCZGDYBM/TMP2O1/2b2LIp2stxGFHRFyyJpHgKAnuRbfUFLABhrvXezZ1HUMP8aHQ06ANGRvARAcmG+2X46lgS7i8/b7sa9goUpOtGyoH9f4ikAaKQNu7E/qkniUuevzQcY7cF2u7MEpaXL6algfy6W7i0AUu302BedpyL/Z/OXGPKrwzeAJoy3F7tz9DYKDCbfEuyvspbYDhUA4vGvC9T4UzWP8aiuSU4OfZ1xyvXsGs0RzPnpEBXsr8u8BkC2zUXam2ySDcAzfxlutNJ289FCzrIB9tfq6+Ui9tEZXgOAlts13/Qz+7fqYPv5/LOHabk+620x8Cproqj93UXXuIj9/eQ5AFiLhVyoOz+3dUvd0K9CmcB/zji/FbdM73XcVX5TeJODYu3HPrO489iv9CAAhx3s1nPTiI28CnvKsj7nXjMPsd+5Luv5IeM2mv5CNO9Mnax53sVx6GMyPQiA/YfAX58EzSfuDv52kTS5aw1xU8ZoP021cX3nfBds/Gn3SPZSEU52bv7Acei7kRcBoJ5OPVXsNv/zPZe0QPsObpg1vNPNF3QdPxfQeaAvuehGQz7wt9buqU3P29hi7gonwU8rHLLQ6wFAag3LjaLK123csd+oSeOGD3iwXbNbahWw0yaXGTTYxS6Pbdqxz4gJ81Z98fna92dPGtG6qr2VxCc4in4bh5EomeVNAGhzpKVYlQO5/lyp20rO6nLzHbqTuMVCaAGg6aoBsAJt8j5WqdepzqJ/MsqZu7WeBYA5U9CFPgrgOF6l06pOb8nOzqpUtncBcPHpw1OAlVqyi6p0utxp+J3dEXuRhwHIbKgWgADD5FtV+nReljsS4cTfOi8DQCeuUgpALf9exyl0WcHFKg31HPgrne1pAOjXK1UCEO5/tsTdCl26mdU4wYE/qVOocgEAOnarSgL8DiTnFFfncLib+O934HC91wGgNJX/j5P8edyuzl9bd3MZbrTtsGyO5wGgrAfUJcRvmXy8MnexLqdnjLHtsS95HwAK3u/mSn5XmmilytvlB10m4HvbLjfkCQDo7WhFKcnnZ5DXV1KRsysOus6A3Vfi8r68AQD9couipGy71Nc3ilxdc9h9Bobb9Pko5REAKHt4uJKs+FlxbpKa/N90TEIGvrLpdFOeAYDok0oq0uLnM7mtkvz3ktOab2/Pwkq+PAQAHe+vYHz45kv9lFaQ/iLzJaXgCVtuH6O8BADRbvmjdIUuqZTuUpD/q3fLSsEmW3435zEAiFbVkp2bnRe7mCw9/YVfkNaVSb5yNhxXpjwHAGVNkPyRNveSQVfZY8HtDsjMQW8bnp/MgwAQpU2tIzM9j1/aeTO+qszi34dyc/ChDd9b8iQAf2jtf8KkJai+v9GgJfUlWb9jtewcZJVgO7+c8ioARN/3KSIlQZEtE/w7SOpW0LXxsHgFs1bpfrb/QXkYAKKzS7rGuM3QFS8E2Us7bWXfam6MV3/2Z1Khpewz2JanAfhDmWt6Ov9mD7+2n/j/84cJzQo4sl6i1yZSpLPce181yusA/Pm0/mTknfZvBPlvH7KKu8xk2qp+dewVoKLqj96cTerUjnkeQ0wA4Nyn8fdzHonlbuJdMq7j8xvtVmXTv54/pHkVzotnhfjBCWdIrb6ZwtN+UwD468a47f2X+zW/pljAF7JK9R96/t1tbpaXPb15xoDGVUv4b82Nrt24+4TEo5S3pTEAf+vY9rVLFsyYMGb4E70f7Dto1CtT532w9rOv9ySny3Nx+sCOT1fMe230wB49+j81csyrU+cn/pBKRsgDAEAAAAIAkH4ALEL4vK9FLgCYiPB5XxNdADAI4fO+BrkAoAvC5325ma7fCOHzvhq5AKAWwud9uWnICz+M+HldR1zNzZiJAHpdb7rqvmiBAHpdLV0BUPAMIuhtpRVy14C1BCH0tpa57MBriRB6W21cAmB9ihh6WZtdt0nfgiB6WXe478JfjCia+wZwrg8/C3H0qrKlTMh7GoH0qkZaUrQAkfSmFkqajVlwK2LpRW0rZElShUOIpveUXFHefPnrkxFPz+X/epkrMlTajoh6S9slr89WCA3CntKiQpZkhY3yIaxekW9UmCVfdRMQWW8ooa6lRg22ILj6a0sDS53aJGQgwjorI6GNpVbF2r9zEnHWUyff6VDMCoGi4lr1HjV9xfrz5G9R7V3rIUnyt3H5sfN/sGL6qN6t4qKsXJO/acQdLEiSOvgJ72qtzhAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQOIAAAQAIAAAAQAIAEAAAAIAEACAAAAEACAAAAEACABAAAACABAAgAAABAAgAAABAAgAQAAAAgAQAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABA4gAAZDAAS8dCkrTUkwBASgUAAAAAAAAAAABooQQkJNRK0AqAd5GQUOtdrQAYj4SEWuO1AmAgEhJqDdQKgM5ISKjVWSsAaiIhoVZNvarVu5GR0Gq3ZsMVY5GS0GqsZgDEISWhVZxuI5ZrkJNQao12Q9Z1c5CV0Cmnrn5NC7ORltBptoZdK6X3Iy+h0v7SOvYtxaYhM6FRWqyenWudkJrQqJOuvYu9MpEc9crspW/3ar0U5Ee1Uurp3L9cZSMypFYbq2jewt5iJ5KkTjtb6D+JIaLLynRkSoXSV3aJ8MZElqL3TluVdMSHlMmS70jSqmn3FlWRq/8DuRPhbREQOwkAAAAASUVORK5CYII=',
								onclick: function() {
									var rfsVolBUheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplierByBusinessUnit', rfsVolBUheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 35,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplierByBusinessUnit',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var barHeaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplierByBusinessUnit', barHeaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score (%)',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#lowPerformanceSuppByBUDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td>' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#lowPerformanceSuppByBUDataTable').append(html);
			}

			//			$('.rfsVolumeByBU-heading').html('Total Volume: ' + data.recordsTotal);

			if (type == 'pie') {
				$('#lowPerformanceSuppByBUPieIconId').addClass('icon-active');
				$('#lowPerformanceSuppByBUBarIconId').removeClass('icon-active');
			} else {
				$('#lowPerformanceSuppByBUBarIconId').addClass('icon-active');
				$('#lowPerformanceSuppByBUPieIconId').removeClass('icon-active');
			}
			lowPermByBUBarChart.hideLoading();
			lowPermByBUBarChart.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			lowPermByBUBarChart.hideLoading();
		}
	});
}

function getTopLowPerfSuppByProcCat(header, token, type, startDate, endDate, pcId) {

	var lowPermByProcCatBarChart = echarts.init(document.getElementById('lowPerfSuppByProcCatBarChart'));
	lowPermByProcCatBarChart.showLoading();
	lowPermByProcCatBarChart.clear();

	var url = '';
	url = getContextPath() + '/spAnalytics/getTopLowPerfSuppByProcCat';

	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
			'pcId': pcId
		},
		type: 'POST',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {
			var totalOfData = 0;
			var downloadData = [];
			var rank = 1;
			for (var index = 0; index < genData(data.data).seriesData.length; index++) {
				var element = genData(data.data).seriesData[index];
				totalOfData = totalOfData + element.score;
			}

			downloadData.push({
				rank: 'RANK',
				supplier: 'SUPPLIER',
				score: 'OVERALL SCORE (%)',
				rating: 'RATING',
			});

			data.data.forEach(function(item) {
				downloadData.push({
					rank: rank++,
					supplier: (item.name ? item.name : ''),
					score: (item.value ? item.value : 0),
					rating: (item.rating ? item.rating : 0),
				});
			});

			if (type == 'pie') {
				var response = genData(data.data);
				var option = {
					color: ['#308dcc', '#A389D4', '#0ed4cb', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) : ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						type: 'category',
						show: false,
					},
					yAxis: {
						type: 'value',
						show: false,
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplierByCategory',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var rfsVolBUheaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplierByCategory', rfsVolBUheaders);
								}
							}
						},
						itemSize: 25,
						right: 0,
						itemGap: 10
					},
					legend: {
						type: 'scroll',
						orient: 'horizontal',
						bottom: 20,
						align: 'auto',
						verticalAlign: 'bottom',
						data: response.legendData,
						selected: response.selected
					},
					series: [
						{
							type: 'pie',
							radius: '55%',
							center: ['50%', '50%'],
							data: response.seriesData,
							emphasis: {
								itemStyle: {
									shadowBlur: 10,
									shadowOffsetX: 0,
									shadowColor: 'rgba(0, 0, 0, 0.5)'
								}
							},
							label: {
								show: true
							},
						}
					]
				};

			} else {
				var colours = ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'];
				var option = {
					tooltip: {
						trigger: 'item',
						formatter: function(params) {
							return 'Supplier : ' + params.name + ' <br/> Overall Score (%) :  ' + Number(params.value).toLocaleString() + ' (%)'
						}
					},
					xAxis: {
						boundaryGap: false,
						type: 'category',
						show: true,
						data: [],
						axisLabel: {
							rotate: 35,
						},
						axisTick: {
							alignWithLabel: true
						},
						name: '',
						nameLocation: 'middle',
					},
					yAxis: {
						type: 'value',
						show: true,
						name: 'Overall Score (%)',
						nameLocation: 'middle',
						nameRotate: 90,
						nameTextStyle: { padding: [15, 0, 50, 0] }
					},
					grid: {
						containLabel: true
					},
					toolbox: {
						feature: {
							saveAsImage: {
								name: 'LowPerformanceSupplierByCategory',
								title: 'Save Image',
								icon: 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTM5jWRgMAAAAVdEVYdENyZWF0aW9uIFRpbWUAMi8xNy8wOCCcqlgAAAQRdEVYdFhNTDpjb20uYWRvYmUueG1wADw/eHBhY2tldCBiZWdpbj0iICAgIiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDQuMS1jMDM0IDQ2LjI3Mjk3NiwgU2F0IEphbiAyNyAyMDA3IDIyOjExOjQxICAgICAgICAiPgogICA8cmRmOlJERiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiPgogICAgICA8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIgogICAgICAgICAgICB4bWxuczp4YXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iPgogICAgICAgICA8eGFwOkNyZWF0b3JUb29sPkFkb2JlIEZpcmV3b3JrcyBDUzM8L3hhcDpDcmVhdG9yVG9vbD4KICAgICAgICAgPHhhcDpDcmVhdGVEYXRlPjIwMDgtMDItMTdUMDI6MzY6NDVaPC94YXA6Q3JlYXRlRGF0ZT4KICAgICAgICAgPHhhcDpNb2RpZnlEYXRlPjIwMDgtMDMtMjRUMTk6MDA6NDJaPC94YXA6TW9kaWZ5RGF0ZT4KICAgICAgPC9yZGY6RGVzY3JpcHRpb24+CiAgICAgIDxyZGY6RGVzY3JpcHRpb24gcmRmOmFib3V0PSIiCiAgICAgICAgICAgIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyI+CiAgICAgICAgIDxkYzpmb3JtYXQ+aW1hZ2UvcG5nPC9kYzpmb3JtYXQ+CiAgICAgIDwvcmRmOkRlc2NyaXB0aW9uPgogICA8L3JkZjpSREY+CjwveDp4bXBtZXRhPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDUdUmQAAAE7SURBVDiNpZM7TsNAEIb/8WPNwwJKh5qGBiNxAiqa+AjQ5iCufQBaOILTUHEBIyEaGmoIVAkyEHshQ7HedSzbUiSvtNqZ0fzfzmhniZkxZFmD1AAcbRCRCQYpdZY1i9gk6cqdrkQAuD1n5H/K9m3g8p4683oBH7/AolT2vujLWgMEKaUAxtp/l8C8AizJ5OjWpgAiACDdy2hq8dVZs/XFd1XBTvPWmwfC23hFDQARIUiJL05qSP6lTn+3Ft89EWYR17p1gC7z9FjFlp8qtrWn/MdnMi/RC9CQwyPGaq5i1gHj9YU6n7EToCHbvrJ/8uYMbATQEKAtbgAqP2qpN1jMnOo5GGVZdu26LjzPgxACQgh4ngdmhpSytcMwnJgKkiRhKSWKokBZluYsSzVJjuPAtm2ztR/HMdHQ7/wPj7WgYLMWxPQAAAAASUVORK5CYII=',
							},
							myTool1: {
								show: true,
								title: 'Download Report',
								icon: 'image://data:image/png;base64,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',
								onclick: function() {
									var barHeaders = ["RANK", "SUPPLIER", "SCORE", "RATING"];
									exportCSVFile(downloadData, 'LowPerformanceSupplierByCategory', barHeaders);
								}
							}
						},
						itemSize: 25
					},
					series: [{
						name: 'Overall Score (%)',
						type: type != 'mixed' ? type : 'bar',
						barMaxWidth: 45,
						data: [],
						label: { normal: { show: false, position: 'top', color: 'black' } },
						smooth: true,
						areaStyle: {
							color: '#308dcc',
						}
					}]
				}
				for (var index = 0; index < data.data.length; index++) {
					option.xAxis.data.push(data.data[index].name);
					option.series[0].data.push({ value: data.data[index].value, itemStyle: { color: colours[index] } });
				}
				type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
			}

			var total = 0
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				total = total + element.value;
			}

			$('#lowPerfSuppByProcCatDataTable').html('');
			for (var index = 0; index < data.data.length; index++) {
				var element = data.data[index];
				var html = '';
				html = '<tr><td>' + Number(index + 1) + '</td><td >' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString(undefined, { maximumFractionDigits: 0 })) + '</td><td>' + (Number(element.rating).toLocaleString()) + '</td></tr>'
				$('#lowPerfSuppByProcCatDataTable').append(html);
			}

			//			$('.rfsVolumeByBU-heading').html('Total Volume: ' + data.recordsTotal);

			if (type == 'pie') {
				$('#lowPerfSuppByProcCatPieIconId').addClass('icon-active');
				$('#lowPerfSuppByProcCatBarIconId').removeClass('icon-active');
			} else {
				$('#lowPerfSuppByProcCatBarIconId').addClass('icon-active');
				$('#lowPerfSuppByProcCatPieIconId').removeClass('icon-active');
			}
			lowPermByProcCatBarChart.hideLoading();
			lowPermByProcCatBarChart.setOption(option, true);
		},
		error: function(error) {
			console.log(error);
			lowPermByProcCatBarChart.hideLoading();
		}
	});
}



function getBUnitLisByDate(header, token, startDate, endDate) {
	var url = '';
	url = getContextPath() + '/spAnalytics/getBusinessUnitByDate';
	console.log("********* ");
	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
		},
		type: 'GET',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {

			console.log(">>>>>>>>>>>>>>>>>>");
			var html = '';
			$('#idLowPermBusinessUnit').val('');
			$('#idBusinessUnit').val('');
			$.each(data, function(i, item) {
				console.log("Bu : " + item.id);
				html += '<option value="' + item.id + '">' + item.displayName + '</option>';
			});
			$('#idBusinessUnit').html(html);
			$("#idBusinessUnit").trigger("chosen:updated");

			$('#idLowPermBusinessUnit').html(html);
			$("#idLowPermBusinessUnit").trigger("chosen:updated");

			var buId = $('#idBusinessUnit').val();
			var bUnitId = $('#idLowPermBusinessUnit').val();
	
			getTophighPerformanceSuppByBUForCurrentYear(header, token, 'pie', startDate, endDate, buId);
			getTopLowPerformanceSuppByBU(header, token, 'pie', startDate, endDate, bUnitId)

		},
		error: function(error) {
			console.log(error);
		}
	});
}


function getProcCatListByDate(header, token, startDate, endDate) {
	var url = '';
	url = getContextPath() + '/spAnalytics/getprocureherementCategoryListByDate';
	console.log("********* ");
	$.ajax({
		url: url,
		data: {
			'startDate': startDate,
			'endDate': endDate,
		},
		type: 'GET',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success: function(data) {

			console.log(">>>>>>>>>>>>>>>>>>");
			var html = '';
			$('#idPcList').val('');
			$('#idLowPerfPcList').val('');
			$.each(data, function(i, item) {
				html += '<option value="' + item.id + '">' + item.procurementCategories + '</option>';
			});
			$('#idPcList').html(html);
			$("#idPcList").trigger("chosen:updated");

			$('#idLowPerfPcList').html(html);
			$("#idLowPerfPcList").trigger("chosen:updated");

			var pcId = $('#idPcList').val();
			var proccId = $('#idLowPerfPcList').val();
			getTopHighPerformanceSuppByProcCatForCurrentYear(header, token, 'pie', startDate, endDate, pcId);
			getTopLowPerfSuppByProcCat(header, token, 'pie', startDate, endDate, proccId);

		},
		error: function(error) {
			console.log(error);
		}
	});
}

