$('document').ready(function() {
	
		var table = $('#datatable-example').DataTable();
		var table2 = $('#datatable-example1').DataTable();
		// Setup - add a text input to each footer cell
		jQuery("#selectSession").change(function() {
			  
			var regDate= $('#selectSession').val();
		    var regCountry=$('#regCountry').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			if (regCountry == null || regCountry == undefined || regCountry == "") {
				regCountry = "dummy"
			}
			table.destroy();
			
			table2.destroy();
			
			table = $('#datatable-example').DataTable({
			"processing" : true,
			"serverSide" : false,
			"pageLength" : 10,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
//				$('div[id=idGlobalError]').hide();
				$('#loading').show();
				return true;
			},
			"drawCallback" : function(d) {
				
				var table = $('#datatable-example').DataTable();
				// in case your overlay needs to be put away automatically you can put it here
				
				d.page = (table != undefined && table.page.info() != undefined) ? table.page.info().page : 0;
				d.size = (table != undefined && table.page.info() != undefined) ? table.page.info().length : 10;
					$('#loading').hide();
			},
			"serverSide" : false,
			"pageLength" : 10,
			"searching" : false,
			"ajax" : { 
			"url" : getContextPath() + '/owner/searchRegisteredBuyers/'+ regDate +'/'+ regCountry,
			"type" : "POST",
			"data" : function(d) {
			},
			"beforeSend" : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			} },
			"order" : [],
			"columns" : [
			             { "mData" : "companyName" }, 
			             { "mData" : "registrationCompleteDate" }, 
			             { "mData" : "registrationCountryName" },
			             { "mData" : "companyRegistrationNumber" } 
			             ] 
			});
			
			table2 = $('#datatable-example1').DataTable({
				"processing" : true,
				"deferRender" : true,
				"preDrawCallback" : function(settings) {
//					$('div[id=idGlobalError]').hide();
					$('#loading').show();
					return true;
				},
				"drawCallback" : function() {
					// in case your overlay needs to be put away automatically you can put it here
						$('#loading').hide();
				},
				"serverSide" : false,
				"pageLength" : 10,
				"searching" : false,
				"ajax" : { 
				"url" : getContextPath() + '/owner/searchRegisteredSuppliers/'+ regDate +'/' + regCountry,
				"type" : "POST",
				"data" : function(d) {
					var table = $('#datatable-example1').DataTable();
					// in case your overlay needs to be put away automatically you can put it here
					
					d.page = (table != undefined && table.page.info() != undefined) ? table.page.info().page : 0;
					d.size = (table != undefined && table.page.info() != undefined) ? table.page.info().length : 10;
				},
				"beforeSend" : function(xhr) {
					//$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						//$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						//$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				} },
				"order" : [],
				"columns" : [
				             { "mData" : "companyName" }, 
				             { "mData" : "registrationCompleteDate" }, 
				             { "mData" : "countryName" },
				             { "mData" : "companyRegistrationNumber" } 
				             ] 
				});
			});
		$('#selectSession').trigger('change');
			
	});


$('.searchOwnerMetric').click(function(event){
	event.preventDefault();
	$("#idGlobalInfo").hide();
	$("#idGlobalError").hide();
	$("#idGlobalWarn").hide();
	$("#idEventInfo").hide();
	
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/ownerMetric",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			xtrimg = '<img src="'+getContextPath()+'/resources/assets/images/pop_icon.png" width="12" height="13">';
			if(data.revenueGenerated > 0){data.revenueGenerated = data.revenueGenerated+xtrimg;}
			if(data.conversionRate > 0){data.conversionRate = data.conversionRate+xtrimg;}
			if(data.newBuyer > 0){data.newBuyer = data.newBuyer+xtrimg;}
			if(data.totalBuyer > 0){data.totalBuyer = data.totalBuyer+xtrimg;}
			if(data.averageTimePerWeek > 0){data.averageTimePerWeek = data.averageTimePerWeek.toFixed(2)+xtrimg;}
			if(data.suspendedBuyers > 0){data.suspendedBuyers = data.suspendedBuyers+xtrimg;}
			if(data.totalSavings > 0){data.totalSavings = data.totalSavings+xtrimg;}
			/*if(data.averageSavings > 0){data.averageSavings = data.averageSavings+xtrimg;}*/
			$('#trail_pros').html(data.trailInProgress);
			$('#active_buy').html(data.activeBuyers);
			$('#fail_pay').html(data.failedPaymentTransaction);
			$('#revenueGenerated').html(data.revenueGenerated);
			$('#conversionRate').html(data.conversionRate);
			$('#newBuyer').html(data.newBuyer);
			$('#totalBuyers').html(data.totalBuyer);
			$('#eve_cancel').html(data.canceledEvents);
			$('#tot_eve').html(data.totalEvents);
			$('#averageTime').html(data.averageTimePerWeek);
			$('#eve_cat').html(data.eventPerCategory.toFixed(2));
			$('#tot_supp').html(data.totalSuppliers);
			$('#tot_pr').html(data.totalPr);
			$('#tot_po').html(data.totalPo);
			$('#suspendedBuyers').html(data.suspendedBuyers);
			$('#auto_ext').html(data.autoExtention);
			$('#manu_ext').html(data.manualExtention);
			$('#totalSave').html(data.totalSavings);
			$('#averageSave').html(data.averageSavings);
			
		},
		error : function(request, textStatus, errorThrown) {
			if (request.getResponseHeader('error')) {
				$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
				$("#idGlobalError").show();
			}
			if (request.getResponseHeader('info')) {
				$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
				$("#idGlobalInfo").show();
			}
		}
	})
});

$('#newBuyer').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showBuyerPlan",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			console.log("data :" +data);
			$('#newBuyerPlan').html("");
			$('#newBuyerPlnCount').html("");
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				$('#newBuyerPlan').html($('#newBuyerPlan').html() + '<div class="mpm_row">' +( key != 'null' ? key : "") + '</div>');
				$('#newBuyerPlnCount').html($('#newBuyerPlnCount').html() + '<div class="mpr_row">' + value + '</div>');
			});
		},error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},complete : function() {
			$('#loading').hide();
		}
	});
});


$('#totalBuyers').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showTotalBuyerPlan",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#totalBuyerPlan').html("");
			$('#totalBuyerCount').html("");
			console.log("data"+data);
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				$('#totalBuyerPlan').html($('#totalBuyerPlan').html() + '<div class="mpm_row">' +(key != 'null' ? key : "")+ '</div>');
				$('#totalBuyerCount').html($('#totalBuyerCount').html() + '<div class="mpr_row">' + value + '</div>');
			});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$('#suspendedBuyers').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showSuspendedBuyerPlan",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data,textStatus, request) {
			$('#planType').html('');
			$('#planCount').html('');
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				$('#planType').html($('#planType').html() + '<div class="mpm_row">' +(key != 'null' ? key : "" )+ '</div>');
				$('#planCount').html($('#planCount').html() + '<div class="mpr_row">' + value + '</div>');
			});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});


$('#revenueGenerated').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showRevenuePlan",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#revenuePlan').html('');
			$('#revenueCount').html('');
			$.each(data, function (key, value) {
			console.log(key + "  : " + value);
			console.log(key + "  : " + value);
			$('#revenuePlan').html($('#revenuePlan').html() + '<div class="mpm_row">' +(key != 'null' ? key : "")+ '</div>');
			$('#revenueCount').html($('#revenueCount').html() + '<div class="mpr_row">' + value + '</div>');
		});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});


$('#conversionRate').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showConversionPlan",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#conversionPlan').html('');
			$('#conversionCont').html('');
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				console.log(key + "  : " + value);
				$('#conversionPlan').html($('#conversionPlan').html() + '<div class="mpm_row">' +(key != 'null' ? key : "" )+ '</div>');
				$('#conversionCont').html($('#conversionCont').html() + '<div class="mpr_row">' + value + '</div>');
			});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});


$('#averageTime').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showPerWeekEvent",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#avgPerEvent').html('');
			$('#avgPerWeekCount').html('');
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				console.log(key + "  : " + value);
				$('#avgPerEvent').html($('#avgPerEvent').html() + '<div class="mpm_row">' + (key != 'null' ? key : "") + '</div>');
				$('#avgPerWeekCount').html($('#avgPerWeekCount').html() + '<div class="mpr_row">' + value + '</div>');
			});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});



$('#totalSave').click(function() {
	var startDate= $('#startDate').val();
	var endDate= $('#endDate').val();
	var country= $('#selectCountry').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	$.ajax({
		type : "POST",
		url : getContextPath()+ "/owner/showTotalAuctionSaving",
		data : {startDate:startDate,endDate:endDate,country:country},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#auctionTotalName').html('');
			$('#auctionTotalSaving').html('');
			$.each(data, function (key, value) {
				console.log(key + "  : " + value);
				console.log(key + "  : " + value);
				$('#auctionTotalName').html($('#auctionTotalName').html() + '<div class="mpr_row">' +(key != 'null' ? key : "" )+ '</div>');
				$('#auctionTotalSaving').html($('#auctionTotalSaving').html() + '<div class="mpm_row">' + value + '</div>');
			});
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});


function render_chart(selector){
	
		$("#"+selector_id).html("");
		console.log(selector_id);
				switch(selector_id){
					case "morris-bar-weekly":
						weekBar();
					break;
					case "morris-bar-monthly" :
						monthBar();
					break;
					case "morris-qtr" :
						quaterBar();
					break;
					case "morris-bar-yearly" :
						anualBar();
					break;
					default :
					break;
				}
	
}	


$('.ph_tabs li a').on('shown.bs.tab', function (e) {
		
				parent_id = $('.ph_tabs li.active a').attr("href");
				selector = $(parent_id);
				selector_id = selector.find(".graph").attr("id");
				render_chart(selector_id);		
});

	
$('#statSel').change(function(){
				parent_id = $('.ph_tabs li.active a').attr("href");
				selector = $(parent_id);
				selector_id = selector.find(".graph").attr("id");	
				render_chart(selector_id);
});


//$('#weekBar').click(function() {
function weekBar(){	
	 var regCountry=$('#regCountry').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			//url : getContextPath()+ "/owner/showRevenueGeneratedWeek",
			
			url : getContextPath()+ "/owner/show"+$('#statSel :selected').text()+"GeneratedWeek",
			data : {country:regCountry},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#graph_loader').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#graph_loader').hide();
			},
			success : function(data) {
			gData = [];
			gLabels = "";
			
			$.each(data, function (key, value) {
					actual_data = Object.keys(value).toString();	
					actual_value = value[actual_data];
					
					//	console.log(value[actual_data]);
					
					//dummy = key+10;
					splt_array = actual_data.split('-');
					item = {}
					item['y'] = splt_array[0].trim();
					item['a'] = actual_value;//dummy;								
					gLabels =splt_array[1]+",";
						gData.push(item); 
				});												
				gLabels = gLabels.replace(/,+$/,'');				
				label = "Series" ; 
				
					Morris.Bar({ element : 'morris-bar-weekly',
										data :gData,
										xkey : 'y',
										ykeys : [ 'a' ],
										labels : [ $('#statSel :selected').text() ],
										stacked : true,
										hideHover : 'auto',
										resize : true, //defaulted to true
										gridLineColor : '#eeeeee',
										barColors : [ '#06ccb3' ] 
								});
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				
			},
			complete : function() {
				$('#graph_loader').hide();
			}
		});
		
}

//$('#monthBar').click(function() {
function monthBar(){
	 var regCountry=$('#regCountry').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			//url : getContextPath()+ "/owner/showRevenueGeneratedMonth",
			url : getContextPath()+ "/owner/show"+$('#statSel :selected').text()+"GeneratedMonth",
			
			data : {country:regCountry},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#graph_loader').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#graph_loader').hide();
			},
			success : function(data) {
				gData = [];
			gLabels = "";
			
			$.each(data, function (key, value) {
					
					actual_data = Object.keys(value).toString();	
					actual_value = value[actual_data];					
					//dummy = key+10;
					splt_array = actual_data.split('-');
					item = {}
					item['y'] = splt_array[0].trim();
					item['a'] = actual_value;//dummy;								
					gLabels =splt_array[1]+",";
						gData.push(item); 
				});												
				gLabels = gLabels.replace(/,+$/,'');				
				label = "Series" ; 
				
					Morris.Bar({ element : 'morris-bar-monthly',
										data :gData,
										xkey : 'y',
										ykeys : [ 'a' ],
										labels : [ $('#statSel :selected').text() ],
										stacked : true,
										hideHover : 'auto',
										resize : true, //defaulted to true
										gridLineColor : '#eeeeee',
										barColors : [ '#06ccb3' ] 
								});
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				
			},
			complete : function() {
				$('#graph_loader').hide();
			}
		});
}


//$('#quaterBar').click(function() {
function quaterBar(){
	 var regCountry=$('#regCountry').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			//url : getContextPath()+ "/owner/showRevenueGeneratedQuarter",
			url : getContextPath()+ "/owner/show"+$('#statSel :selected').text()+"GeneratedQuarter",
			data : {country:regCountry},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#graph_loader').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#graph_loader').hide();
			},
			success : function(data) {
				
			gData = [];
			gLabels = "";
			
			$.each(data, function (key, val) {
				
					actual_data = Object.keys(val).toString();	
					splt_array = actual_data.split('-');
					item = {}
					item['y'] = val['label']; 
					item['a'] = val['value'];						
					gLabels =val['label']+",";
						gData.push(item); 
				});												
				gLabels = gLabels.replace(/,+$/,'');				
				label = "Series" ; 
								
				Morris.Bar({ element : "morris-qtr",
										data :gData,
										xkey : 'y',
										ykeys : [ 'a' ],
										labels : [ $('#statSel :selected').text() ],
										stacked : true,
										hideHover : 'auto',
										resize : true, //defaulted to true
										gridLineColor : '#eeeeee',
										barColors : [ '#06ccb3' ] 
				});
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#graph_loader').hide();
			}
		});
		
}
//);


//$('#anualBar').click(function() {
function anualBar(){
	 var regCountry=$('#regCountry').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			//url : getContextPath()+ "/owner/showRevenueGeneratedYear",
			url : getContextPath()+ "/owner/show"+$('#statSel :selected').text()+"GeneratedYear",
			data : {country:regCountry},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#graph_loader').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#graph_loader').hide();
			},
			success : function(data) {
			gData = [];
			gLabels = "";
			$.each(data, function (key, val) {
					actual_data = Object.keys(val).toString();	
					//dummy = key+10;
					item = {}
					console.log("Lable : " + val.label + " Value 1 : "+ val.valuePojo.value1 +" Value 2 : " + val.valuePojo.value2);
					item['x'] = val.label; 
					item['y'] = val.valuePojo.value1; 
					item['z'] = val.valuePojo.value2;						
					gLabels =val['label']+",";
					gData.push(item); 
				});	
				Morris.Bar({ element : "morris-bar-yearly",
										data :gData,
										xkey : 'x',
										ykeys : [ "y","z" ],
										labels : [ 'First Half',"Second Half" ],
										stacked : true,
										hideHover : 'auto',
										resize : true,
				});
				
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				 
			},
			complete : function() {
				$('#graph_loader').hide();
			}
		});
		
}
