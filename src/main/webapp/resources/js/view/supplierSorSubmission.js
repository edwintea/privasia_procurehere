var decimalLimit = $('#eventDecimal').val();
var item_level_order = '';
var item_order = '';

var totalPage = 500;
var visiblePage = 5;
var isRateLarge = false;

$(document).delegate('.viewSupplierBillOfQuantity', 'click', function(e) {
    e.preventDefault();

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    var rfteventId = $(this).attr('rftevent-id');
    var eventBqId = $(this).attr('eventBq-id');
    var remarks = $("#supplierBqList #remarks").val();
    var bqStatus = $(this).attr('bq-status');
    console.log("bqStatus ---"+bqStatus);

    var ajaxUrl = getContextPath() + '/supplier/viewSorDetails/' + getEventType() + '/' + eventBqId;
    $.ajax({
        url : ajaxUrl,
        type : "POST",
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader(header, token);
        },
        success : function(data) {
            var html = supplierSearchBqList(data, rfteventId, eventBqId, bqStatus);
            $(".doc-fir-inner1").hide();
            $('.bqlistDetails').html(html);
            if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
                if(allowBqChanges){
                    allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
                    disableFormFields(allowedFields);
                    $('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
                    $('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
                }else{
                    disableFormFields(allowedFields);
                }
            }
            $('.bqlistDetails').find('.custom-select').uniform();
            $('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
            $('.bqlistDetails').find(".selector").find('span').css('border', 'none');

            console.log("===================1===================");
            var bqPageLength = $('#bqPageLength').val();

            // setting value for select Page Length on user preferences
            if (bqPageLength === undefined || bqPageLength === '') {
                bqPageLength = 50;
            }
            var totalBqItemCount = data.totalBqItemCount;
            console.log("totalBqItemCount :" + totalBqItemCount + "== bqPageLength :" + bqPageLength + "===totalBqItemCount/bqPageLength :" + totalBqItemCount / bqPageLength);
            totalPage = Math.ceil(totalBqItemCount / bqPageLength);

            if (totalPage == 0) {
                totalPage = 1;
            }

            if (totalPage < 5) {
                visiblePage = totalPage;
            }

            $('#pagination').twbsPagination({
                totalPages : totalPage,
                visiblePages : visiblePage
            });

            console.log(" ==" + bqPageLength);
            $("#selectPageLen").val(bqPageLength).trigger("chosen:updated");

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
        }
    });
});
$(document).delegate('#resetButton', 'click', function(e) {
    e.preventDefault();
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");
    item_level_order = '';
    $("#bqItemSearch").val("");
    $("#chooseSection").val('').trigger("chosen:updated");
    $('#pagination').find('li.active').removeClass(".active");
    var pageLen = "50";
    pageLen = $('#selectPageLen option:selected').val();
    var pageLength = parseInt(pageLen);
    var eventType = getEventType();

    // reset pagination to 1
    var pagination = jQuery('.pagination').data('twbsPagination');
    var pageToShow = 1;
    pagination.show(pageToShow);

    var searchVal = "";
    var choosenVal = "";
    var selectPageNo = 1;
    var pageNo = parseInt(selectPageNo);

    var eventBqId = $('#supplierBqList #eventBqId').val();
    var rfteventId = $('#supplierBqList #rfteventId').val();

    $.ajax({
        type : "POST",
        url : getContextPath() + '/supplier/getEventSorForResetValue',
        data : {
            'rfteventId' : rfteventId,
            'eventBqId' : eventBqId,
            'eventType' : eventType,
            'searchVal' : searchVal,
            'pageNo' : pageNo,
            'pageLength' : pageLength,
        },
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader(header, token);
            xhr.setRequestHeader("Accept", "application/json");
        },
        success : function(data) {
            // console.log(data);
            var html = searchRenderSorItemGrid(data);
            $(".doc-fir-inner1").hide();
            $('#bqItemList').html(html);
            if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
                if(allowBqChanges){
                    allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
                    disableFormFields(allowedFields);
                    $('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
                    $('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
                }else{
                    disableFormFields(allowedFields);
                }
            }
            $('.bqlistDetails').find('.custom-select').uniform();
            $('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
            $('.bqlistDetails').find(".selector").find('span').css('border', 'none');

            // set pagination according to result
            visiblePage = 5;
            totalPage = Math.ceil(data.totalBqItemCount / pageLength);

            if (totalPage == 0) {
                totalPage = 1;
            }

            if (totalPage < 5) {
                visiblePage = totalPage;
            }
            var opts = {
                totalPages : 500,
                visiblePages : 5
            };

            $('.pagination').twbsPagination('destroy');
            $('.pagination').twbsPagination($.extend(opts, {
                totalPages : totalPage,
                visiblePages : visiblePage
            }));
            var pagination = jQuery('.pagination').data('twbsPagination');
            pagination.show(pageNo);

            // Building Level filter drop down
            var filterTable = '<option value="">&nbsp;</option>';
            $.each(data.levelOrderList, function(i, item) {
                filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li class="active-result">' + item.level +'.'
                // + item.order + '</li>';
            });
            $('#chooseSection').html(filterTable).trigger("chosen:updated");

        },
        error : function(request, textStatus, errorThrown) {
            console.log("ERROR : " + request.getResponseHeader('error'));
            if (request.getResponseHeader('error')) {
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
                $('div[id=idGlobalError]').show();
            }
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });
});
$(document).delegate('.page-link', 'click', function(e) {
    e.preventDefault();
    var searchVal = $('#bqItemSearch').val();
    var choosenSection = ''; // $('#chooseSection option:selected').text();
    item_level_order = '';
    var pageNo = parseInt($('#pagination').find('li.active').text());
    var pageLen = "50";
    if ($('#selectPageLen option:selected').text()) {
        pageLen = $('#selectPageLen').val();
    }
    var pageLength = parseInt(pageLen);
    // console.log("searchVal :"+searchVal + " choosenSection : "+choosenSection +" page :"+pageNo + " pageLength : "+pageLength) ;
    console.log("================2======================");
    $("#chooseSection").val('').trigger("chosen:updated");
    var isChooseSection = false;
    searchFilterSorItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
});
$(document).ready(function() {

    var bqPageLength = $('#bqPageLength').val();
    if (bqPageLength === undefined || bqPageLength === '') {
        bqPageLength = 50;
    }
    console.log(" bqPageLength :" + bqPageLength);
    $("#selectPageLen").val(bqPageLength).trigger("chosen:updated");

    $(document).delegate('input[name="bqItemSearch"]', 'keyup', function(e) {
        e.preventDefault();
        console.log(" ===bqItemSearch=== ");
        var pageLen = "50";
        pageLen = $('#selectPageLen option:selected').val();
        var pageLength = parseInt(pageLen);
        var selectPageNo = $('#pagination').find('li.active').text();

        var pagination = jQuery('.pagination').data('twbsPagination');
        var pageToShow = 1;
        pagination.show(pageToShow);
        var pageNo = parseInt(pageToShow);
        // var pageNo = parseInt(selectPageNo);

        if ($(this).val() == "") {
            var searchVal = $(this).val();
            var choosenSection = ''; // $('#chooseSection option:selected').val();
            var pageLen = "50";
            pageLen = $('#selectPageLen option:selected').val();
            var pageLength = parseInt(pageLen);
            var selectPageNo = $('#pagination').find('li.active').text();

            var pagination = jQuery('.pagination').data('twbsPagination');

            // var pageNo = parseInt(selectPageNo);

            // New requirement for filter fetch selected filter value page records.
            if (choosenSection) {
                var selectedIndex = $('#chooseSection option:selected').index();
                var pageToShow = selectedIndex / pageLength;
                var pageNo = parseInt(pageToShow);
                if (pageToShow % 1 !== 0) {
                    pageNo = parseInt(pageToShow) + 1;
                }
                if (pageNo == 0) {
                    pageNo = 1;
                }
                pagination.show(pageNo);
                choosenSection = "";
            } else {
                var pageToShow = 1;
                pagination.show(pageToShow);
                var pageNo = parseInt(pageToShow);
            }
            console.log(" pageNo  : " + pageNo);
            var isChooseSection = false;
            searchFilterSorItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);

        }
        if (this.value.length > 2) {
            var searchVal = $(this).val();
            var choosenSection = ''; // $('#chooseSection option:selected').text();
            console.log(" pageLength  :" + pageLength + " searchVal :" + searchVal + "pageNo :" + pageNo + " choosenSection  : ");
            var isChooseSection = false;
            searchFilterSorItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
        }
    });

    $(document).delegate('#selectPageLen', 'change', function(e) {
        var pageLen = "50";
        pageLen = $('#selectPageLen option:selected').val();
        var searchVal = $('#bqItemSearch').val();
        var pageLength = parseInt(pageLen);
        var selectPageNo = $('#pagination').find('li.active').text();
        var choosenSection = $('#chooseSection option:selected').text();
        var pageNo = parseInt(selectPageNo);

        // New requirement for filter fetch selected filter value page records.
        if (choosenSection) {
            var pagination = jQuery('.pagination').data('twbsPagination');
            var selectedIndex = $('#chooseSection option:selected').index();
            var pageToShow = selectedIndex / pageLength;
            var pageNo = parseInt(pageToShow);
            if (pageToShow % 1 !== 0) {
                pageNo = parseInt(pageToShow) + 1;
            }
            if (pageNo == 0) {
                pageNo = 1;
            }
            pagination.show(pageNo);
            choosenSection = "";
        } else {
            item_level_order = '';
        }

        var isChooseSection = false;
         console.log(" pageLength : "+pageLen + "searchVal :"+searchVal) ;
        searchFilterSorItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
    });

    $(document).delegate('#chooseSection', 'change', function(e) {
        if ($('#chooseSection option:selected').text()) {
            var choosenVal = $('#chooseSection option:selected').val();
        }
        var chooseSection = choosenVal;
        var searchVal = $('#bqItemSearch').val();
        var pageLen = "50";
        pageLen = $('#selectPageLen').val();
        var pageLength = parseInt(pageLen);
        var selectPageNo = $('#pagination').find('li.active').text();

        var pagination = jQuery('.pagination').data('twbsPagination');

        var selectedIndex = $('#chooseSection option:selected').index();
        console.log("selectedIndex :" + selectedIndex);

        item_order = choosenVal.split(".")[1];
        item_level_order = chooseSection.replace(".", "_");
        item_level_order = "item_" + item_level_order;
        if (!$('#chooseSection option:selected').text()) {
            item_level_order = '';
        }
        console.log("item_level_order :" + item_level_order);

        // New requirement for filter fetch selected filter value page records.
        var pageToShow = selectedIndex / pageLength;

        var pageNo = parseInt(pageToShow);
        if (pageToShow % 1 !== 0) {
            console.log("pageToShow :" + pageToShow);
            pageNo = parseInt(pageToShow) + 1;
        }
        if (pageNo == 0) {
            pageNo = 1;
        }
        pagination.show(pageNo);
        console.log(" pageNo  : " + pageNo);
        chooseSection = "";

        var isChooseSection = true;
        var pageNo = parseInt(selectPageNo);
         console.log(" pageLength : "+pageLength + " searchVal :"+searchVal +" pageNo :"+pageNo +" choosenVal :"+choosenVal);
        searchFilterSorItem(searchVal, pageNo, pageLength, chooseSection, isChooseSection);
    });
});
function ReplaceNumberWithCommas(yourNumber) {
    // Seperates the components of the number
    var n = yourNumber.toString().split(".");
    // Comma-fies the first part
    n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    // Combines the two sections
    return n.join(".");
}

function supplierSearchBqList(item, rfteventId, eventBqId, bqStatus) {
    var decimalLimit = $('#eventDecimal').val();
    var countNewData = 1;
    var html = '';
    var buyerSubmit = false;
    var remarks = "";
    $.each(item.supplierSorItemList, function(i, data) {
        console.log(" item.supplierSorItemList :");
        // if (data.supplierBq.buyerSubmited != null && data.supplierBq.buyerSubmited == true) {
        // 	buyerSubmit = true;
        // }
        if (i > 0) {
            // return false;
        } else {
            var eventCurr = $('#eventCurrency').val();
            html += '<h3>' + data.supplierBq.name.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;') + '</h3>';

            html += '<div class="column_button_bar">';
            html += '<div class="pull-left">';

            html += '<div aria-label="Page navigation">';
            html += '<ul class="pagination" id="pagination"></ul>';
            html += '</div>';

            html += '<div class="col-md-12 col-sm-12 col-xs-12">';
            html += '<label class="marg-right-10">';
            html += ' Jump to item';
            html += '</label>';
            html += '<select path="" class="choosen-select" name="" data-parsley-id="0644" id="chooseSection" style ="background: white; width: 49px;height: 35px;">';
            html += '<option value=""></option>';
            $.each(item.levelOrderList, function(i, data) {
                html += '<option  value="' + data.level + '.' + data.order + '">' + data.level + '.' + data.order + '</option>';
                if (data.children != undefined && data.children != null) {
                    $.each(data.children, function(i, child) {
                        html += '<option value="">' + child.level + '.' + child.order + '</option>';
                    });
                }
            });
            html += '</select>';
            html += '</div>';
            html += '</div>';

            html += '<div class="pull-right">';
            html += '<div class="col-md-12 col-sm-12 col-xs-12">';
            html += '<label>';
            html += '<select class="disablesearch choosen-select" name="" data-parsley-id="0644" id="selectPageLen" style="background: white; width: 59px;height: 36px;">';
            html += '<option value="10">10</option>';
            html += '<option value="30">30</option>';
            html += '<option value="50">50</option>';
            html += '<option value="100">100</option>';
            html += '<option value="9999">ALL</option>';
            html += '</select>';
            html += ' records per page';
            html += '</label>';
            html += '</div>';
            html += '</div>';

            html += '<div class="right_button">';
            html += '<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="resetButton">Reset</button>';
            html += '</div>';
            html += '<div class="pull-right" style="padding-right: 12px;">';
            html += '<div class="row" style="width: 306px;">';
            html += '<input name="bqItemSearch" type="text" id="bqItemSearch" placeholder="Search sor by item name & description.." class="form-control" />';
            html += '</div>';
            html += '</div>';
            html += '</div>';
            html += '</div>';

            html += '<form id="supplierBqList">';
            html += '<div class="main_table_wrapper ph_table_border payment height-full1 mega">';
            html += '<table cellpadding="0" cellspacing="0" border="0" width="100%" class="ph_table font-set table" style="top: 0px; ">';
            html += '<thead><tr><th class="width_50 width_50_fix">No</th>' +
                    '<th class="width_300_fix width_300"> Item Name </th>';
            html += '<th class="text-center width_100_fix width_100">UOM</th>';
            html += '<th class="text-center width_100_fix width_100">Rate</th>';
            if (data.supplierBq.field1ToShowSupplier) {
                html += '<th class="width_200 width_200_fix text-left align-center"> ' + (data.supplierBq.field1Label != undefined ? data.supplierBq.field1Label : "") + ' </th>';
            }
            html += '</tr></thead></table>';
        }
    });
    html += '<table class="deta ph_table table bq-table"><tbody id="bqItemList">';
    html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
    html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';

    $.each(item.supplierSorItemList, function(i, data) {
        var totalAmount = '';
        if (data.order == 0) {
            if (data.totalAmount != undefined) {
                totalAmount = data.totalAmount;
            }
            html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" data-item="' + data.id + '" class=" item_' + data.level + '_' + data.order + '"><td class="width_50 width_50_fix"><span>' + data.level + '.'
                + data.order + '</span>&nbsp; </td>';
            html += '<td class="width_300 width_300_fix"><span>' + data.itemName.replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&#39;') + '</span>';
            html += data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "";
            html += '<div id="demo-' + data.id + '" class="collapse table-contant">' + data.itemDescription + '</div>' + '</td>';
            html += '<td class="text-center width_100 width_100_fix"></td>';
            html += '<td class="text-center width_100_fix width_100">';
            html += '<span>&nbsp;</span></td>';
            if (data.supplierBq.field1ToShowSupplier) {
                html += '<td class="width_200 width_200_fix align-center"></td>';
            }
            html += '</tr>';
            html += '<tr id="demo-' + data.id + '" class="collapse" style=" background: #fff;">';
            html += '<td  style=" background: #fff;">';
            html += '</td>';
            html += '<td colspan="7" style=" background: #fff;">';
            html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
            html += '</td>';
            html += '</tr>';
        } else {
            html += renderSearchChildBqHtml(data);
        }
        remarks = data.supplierBq.remark;
    });

    var eventCurr = $('#eventCurrency').val();
    html += '</tbody></table></div>';
    // Dont show additional tax if auction type is without tax
    html += '</div>';
    html += '<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">';

    html += '<div><label>Remark</label></div>'
    html += '<textarea rows="3"  cols="42" name="remarks" id="remarks" placeholder ="Write Your Remarks" maxlength="3000" minlength="0" >' + (remarks != undefined ? remarks : '') + '</textarea>';
    html += '</p></div></div>';
    html += '<div class="row">';
    html += '<div class="col-md-12 col-xs-12 col-sm-12">';
    html += '<button class="btn btn-black ph_btn_midium back_to_BQ hvr-pop hvr-rectangle-out1">Back to Schedule of Rate</button>';
    if (buyerSubmit == false || revisedSubmissionMode == true) {
        html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out pull-right complete-event '
            + '">Complete</button>';
    } else if (allowedFields == '') {
        html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out pull-right complete-event">Confirm</button>';
    }
    if(bqStatus != 'COMPLETED'){
        html += '<button class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1 pull-right skipvalidation saveDraftSupplierBqButton">Save Draft</button>';
    }
    html += '</div></div></form>';
    return html;
}

function renderSearchChildBqHtml(data) {
    var decimalLimit = $('#eventDecimal').val();
    var html = '';

    // Main row
    html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important; background: #eef7fc;" class="sub_item item_' + data.level + '_' + data.order + '" data-item="' + data.id + '">';
    html += '<input type="hidden" name="itemName" id="" value="' + data.itemName.replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;') + '" />';
    html += '<td class="width_50 width_50_fix">' + data.level + '.' + data.order + ' &nbsp;</td>';
    html += '<td class="width_300 width_300_fix">';
    html += data.itemName.replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
    html += (data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "");
    html += '</td>';
    html += '<td class="text-center width_100 width_100_fix">';
    if (data.uom) {
        html += data.uom.uom;
    }
    html += '</td>';
    html += '<td class="width_200 width_200_fix align-center">';
    html += '<input data-id="' + data.id + '" data-pos="2" data-validation="required" class="validate form-control text-right" type="text" name="totalAmount" onkeypress="return isNumberKey(event)" onchange="validateInput(this)" min="0" max="9999999999" value="';    html += (data.totalAmount != undefined ? data.totalAmount.toFixed(2) : "") + '" />';
    html += '</td>';

    if (data.supplierBq && data.supplierBq.field1ToShowSupplier) {
        var isBuyer = (data.supplierBq.field1FilledBy == 'BUYER' ? true : false);
        html += '<td class="width_200 width_200_fix align-center">';
        if (!isBuyer) {
            html += '<input type="text" field1-id="' + data.id + '" data-pos="5" name="field1" data-validation="' +
                ((data.supplierBq.field1Required != undefined && data.supplierBq.field1Required) ? "required" : "") +
                '" class="extra-column form-control " style="text-align:left;" value="' +
                (data.field1 != undefined ? data.field1 : "") + '" onchange="handleInputChange(this)">';
        } else {
            html += data.field1 != undefined ? data.field1 : "";
            html += '<input type="hidden" name="field1" value="' + (data.field1 != undefined ? data.field1 : "") + '">';
        }
        html += '</td>';
    }
    html += '</tr>';

    // Description row
    html += '<tr id="demo-' + data.id + '" class="collapse" style="background: #eef7fc;">';
    html += '<td style="background: #eef7fc; border-bottom: 0 !important;"></td>';
    html += '<td colspan="' + (data.supplierBq && data.supplierBq.field1ToShowSupplier ? '4' : '3') + '" style="background: #eef7fc; border-bottom: 0 !important; padding-left: 10px;">';
    html += '<p style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;">' + data.itemDescription + '</p>';
    html += '</td>';
    html += '</tr>';

    return html;
}


function handleInputChange(inputElement) {
    var $inputElement = $(inputElement);
    var inputValue = $inputElement.val();
    var field1Id = $inputElement.attr('field1-id');
    addInputInExtra($inputElement, true, field1Id);
}


function addInputInExtra(block, showSuccess, itemId) {
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();

    var totalAmountForValidation = $('#amountAfterTax').text();
    var rfteventId = $('#supplierBqList #rfteventId').val();
    var ajaxUrl = getContextPath() + '/supplier/updateSupplierSORItemExtraFieldDetails/' + getEventType()+'/'+rfteventId;
    var remarks = $("#supplierBqList #remarks").val();

    var currentBlock = block;
    var dataRow = {};
    block.closest('tr').find('input, select').each(function() {
        dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
    });
    dataRow['eventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
    dataRow['id'] = itemId;

    // console.log(JSON.stringify(dataRow));
    $.ajax({
        url : ajaxUrl,
        type : "POST",
        data : JSON.stringify(dataRow),
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader(header, token);
        },
        success : function(data, textStatus, request) {

            $('.saveSupplierBq').prop("disabled", false);
            if (currentBlock.closest('tr').next('tr').hasClass('in')) {
                currentBlock.closest('tr').next('tr').remove();
            }

            if (request.getResponseHeader('error')) {
                // $('#newDiv').clear();
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            } else {
                if (showSuccess) {
                    var success = request.getResponseHeader('success');
                    $.jGrowl(success, {
                        sticky : false,
                        position : 'top-right',
                        theme : 'bg-green'
                    });
                }

            }
        },
        error : function(request, textStatus, errorThrown) {
            if (request.responseText != null) {
                try {
                    var data = JSON.parse(request.responseText);
                    // html = renderChildBqHtml(data);
                    var ItemId = currentBlock.closest('tr').attr('data-item');
                    currentBlock.closest('tr').replaceWith(html);
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find('.custom-select').uniform();
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").find('span').css('border', 'none');
                } catch (e) {
                    // TODO: handle exception
                }
            }
            if (request.getResponseHeader('error')) {
                // $('#newDiv').clear();
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            }
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });
}

function searchFilterSorItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection) {
    var eventBqId = $('#supplierBqList #eventBqId').val();
    var rfteventId = $('#supplierBqList #rfteventId').val();
    var eventType = getEventType();

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $.ajax({
        type : "POST",
        url : getContextPath() + '/supplier/getSorItemForSearchFilterForSupplier',
        data : {
            'rfteventId' : rfteventId,
            'eventBqId' : eventBqId,
            'eventType' : eventType,
            'searchVal' : searchVal,
            'pageNo' : pageNo,
            'pageLength' : pageLength,
            'choosenSection' : choosenSection,
        },
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader(header, token);
            xhr.setRequestHeader("Accept", "application/json");
        },
        success : function(data) {
            var html = searchRenderSorItemGrid(data);
            $(".doc-fir-inner1").hide();
            $('#bqItemList').html(html);
            if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
                if(allowBqChanges){
                    allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
                    disableFormFields(allowedFields);
                    $('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
                    $('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
                }else{
                    disableFormFields(allowedFields);
                }
            }
            $('.bqlistDetails').find('.custom-select').uniform();
            $('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
            $('.bqlistDetails').find(".selector").find('span').css('border', 'none');

            if (item_level_order.length) {
                $('.' + item_level_order).each(function() {
                    $.each(this.cells, function() {
                        $(this).animate({
                            backgroundColor : "#ffff00"
                        }, 2000);
                    });
                });

                setTimeout(function() {
                    $('.' + item_level_order).each(function() {
                        $.each(this.cells, function() {
                            if (item_order == '0') {
                                $(this).animate({
                                    backgroundColor : "#ffffff"
                                }, 2000);
                            } else {
                                $(this).animate({
                                    backgroundColor : "#eef7fc"
                                }, 2000);
                            }
                        });
                    });
                }, 4000);
            }

            // set pagination according to result
            visiblePage = 5;
            totalPage = Math.ceil(data.totalBqItemCount / pageLength);

            if (totalPage == 0) {
                totalPage = 1;
            }

            if (totalPage < 5) {
                visiblePage = totalPage;
            }
            var opts = {
                totalPages : 500,
                visiblePages : 5
            };

            $('.pagination').twbsPagination('destroy');
            $('.pagination').twbsPagination($.extend(opts, {
                totalPages : totalPage,
                visiblePages : visiblePage
            }));
            var pagination = jQuery('.pagination').data('twbsPagination');
            pagination.show(pageNo);

            // Building Level filter drop down
            if (!isChooseSection) {
                var filterTable = '<option value="">&nbsp;</option>';
                $.each(data.levelOrderList, function(i, item) {
                    filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li class="active-result">' + item.level
                    // +'.' + item.order + '</li>';
                });
                $('#chooseSection').html(filterTable).trigger("chosen:updated");
            }

        },
        error : function(request, textStatus, errorThrown) {
            console.log("error");
            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
            $('div[id=idGlobalError]').show();
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });
}
function searchRenderSorItemGrid(item) {
    var decimalLimit = $('#eventDecimal').val();
    var countNewData = 1;
    var buyerSubmit = false;

    var rfteventId = $('#supplierBqList #rfteventId').val();
    var eventBqId = $('#supplierBqList #eventBqId').val();

    var html = '';
    html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
    html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';
    $.each(item.supplierSorItemList, function(i, data) {
        var totalAmount = '';
        if (data.order == 0) {
            if (data.totalAmount != undefined) {
                totalAmount = data.totalAmount;
            }
        var totalAmount = '';
        html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" data-item="' + data.id + '" class=" item_' + data.level + '_' + data.order + '"><td class="width_50 width_50_fix"><span>' + data.level + '.'
            + data.order + '</span>&nbsp; </td>';
        html += '<td class="width_300 width_300_fix"><span>' + data.itemName + '</span>';
        html += data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "";
        html += '<div id="demo-' + data.id + '" class="collapse table-contant">' + data.itemDescription + '</div>' + '</td>';
        html += '<td class="text-center width_100 width_100_fix"></td>';
        html += '<td class="text-center width_100_fix width_100">';
        html += '<span>&nbsp;</span></td>';
            if (data.supplierBq.field1ToShowSupplier) {
                html += '<td class="width_200 width_200_fix align-center"></td>';
            }
        html += '</tr>';

            html += '<tr id="demo-' + data.id + '" class="collapse"  style=" background: #fff;">';
            html += '<td  style=" background: #fff;">';
            html += '</td>';
            html += '<td colspan="7" style=" background: #fff;;">';
            html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
            html += '</td>';
            html += '</tr>';

        } else {
            html += renderSearchChildBqHtml(data);
        }
    });
    return html;
}

$(document).delegate('#supplierBqList table.bq-table tr input, #supplierBqList table.bq-table tr select', 'change', function(e) {
    e.preventDefault();

    var itemId = $(this).data('id'); // Get the data-id
    console.log("Item ID: " + itemId);

    console.log("====Extra column=============" + $(this).hasClass('extra-column'));
    if ($(this).hasClass('extra-column')) {
        return;
    }

    if (!$('#supplierBqList').isValid()) {
        doCompute($(this), false, itemId);
    } else {
        doCompute($(this), true, itemId);
    }
});

function doCompute(block, showSuccess, itemId) {

    if(isRateLarge) {
        return;
    }

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $('div[id=idGlobalError]').hide();
    $('div[id=idGlobalSuccess]').hide();

    var totalAmountForValidation = $('#amountAfterTax').text();
    var rfteventId = $('#supplierBqList #rfteventId').val();
    var ajaxUrl = getContextPath() + '/supplier/updateSupplierSORItemDetails/' + getEventType()+'/'+rfteventId;
    var remarks = $("#supplierBqList #remarks").val();

    var currentBlock = block;
    var dataRow = {};
    block.closest('tr').find('input, select').each(function() {
        dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
    });
    dataRow['eventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
    dataRow['id'] = itemId;

    // console.log(JSON.stringify(dataRow));
    $.ajax({
        url : ajaxUrl,
        type : "POST",
        data : JSON.stringify(dataRow),
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader(header, token);
        },
        success : function(data, textStatus, request) {

            $('.saveSupplierBq').prop("disabled", false);
            if (currentBlock.closest('tr').next('tr').hasClass('in')) {
                currentBlock.closest('tr').next('tr').remove();
            }

            if (request.getResponseHeader('error')) {
                // $('#newDiv').clear();
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            } else {
                if (showSuccess) {
                    var success = request.getResponseHeader('success');
                    $.jGrowl(success, {
                        sticky : false,
                        position : 'top-right',
                        theme : 'bg-green'
                    });
                }

            }
        },
        error : function(request, textStatus, errorThrown) {
            if (request.responseText != null) {
                try {
                    var data = JSON.parse(request.responseText);
                    // html = renderChildBqHtml(data);
                    var ItemId = currentBlock.closest('tr').attr('data-item');
                    currentBlock.closest('tr').replaceWith(html);
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find('.custom-select').uniform();
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
                    $('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").find('span').css('border', 'none');
                } catch (e) {
                    // TODO: handle exception
                }
            }
            if (request.getResponseHeader('error')) {
                // $('#newDiv').clear();
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            }
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });
}


$(document).delegate(".saveDraftSupplierBqButton", "click", function(e) {
    e.preventDefault();

    // if (!$('#supplierBqList').isValid()) {
    //     return;
    // }

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $('div[id=idGlobalSuccess]').hide();
    $('div[id=idGlobalError]').hide();

    var rfteventId = $('#supplierBqList #rfteventId').val();
    var remarks = $("#supplierBqList #remarks").val();

    var eventBqId = $('#supplierBqList #eventBqId').val();
    var ajaxUrl = getContextPath() + '/supplier/saveDraftSupplierSor/' + getEventType() + '/' +rfteventId+'/'+eventBqId;

    $.ajax({
        url : ajaxUrl,
        type : "POST",
        data: JSON.stringify({
            remarks: remarks
        }),
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader(header, token);
        },
        success : function(data, textStatus, request) {
            if (request.getResponseHeader('success')) {
                var success = "Schedule of Rate saved successfully";
                $.jGrowl(success, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-green'
                });
            }
        },
        error : function(request, textStatus, errorThrown) {
            console.log("ERROR : " + request.getResponseHeader('error'));
            if (request.getResponseHeader('error')) {
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            }
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });

});

function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode != 46 && charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}

function validateInput(input) {
    // Remove non-numeric characters except decimal point
    isRateLarge = false;

    if(input.value == 0) {
        input.value = '0.00'
    } else {
        input.value = input.value.replace(/[^0-9.]/g, '');
        input.value = input.value.replace(/^0+/, '');
    }

    // Ensure only one decimal point
    var parts = input.value.split('.');
    if (parts.length > 2) {
        parts = [parts[0], parts.slice(1).join('')];
        input.value = parts.join('.');
    }

    // Convert input to a number and fix to 2 decimal places
    let value = parseFloat(input.value);
    if (!isNaN(value)) {
        input.value = value.toFixed(2);
    }

    // Check if the value is less than the min value
    if (input.value !== "" && parseFloat(input.value) < parseFloat(input.min)) {
        input.value = parseFloat(input.min).toFixed(2);
    }

    // Check if the value is greater than the max value
    if (input.value !== "" && parseFloat(input.value) > parseFloat(input.max)) {
        var error = "Rate is too large";
        $.jGrowl(error, {
            sticky: false,
            position: 'top-right',
            theme: 'bg-red'
        });
        isRateLarge = true;
        input.value = "0.00";
    }
}
$(document).delegate(".complete-event", "click", function(e) {
    e.preventDefault();
    if (!$('#supplierBqList').isValid()) {
        return;
    }

    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $('div[id=idGlobalSuccess]').hide();
    $('div[id=idGlobalError]').hide();

    var rfteventId = $('#supplierBqList #rfteventId').val();
    var remarks = $("#supplierBqList #remarks").val();

    var eventBqId = $('#supplierBqList #eventBqId').val();
    var ajaxUrl = getContextPath() + '/supplier/saveCompleteSupplierSor/' + getEventType() + '/' +rfteventId+'/'+eventBqId ;

    $.ajax({
        url : ajaxUrl,
        type : "POST",
        data: JSON.stringify({
            remarks: remarks
        }),
        beforeSend : function(xhr) {
            $('#loading').show();
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.setRequestHeader(header, token);
        },
        success : function(data, textStatus, request) {
            if (request.getResponseHeader('success')) {
                var success = "Schedule of Rate saved successfully";
                $.jGrowl(success, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-green'
                });
                window.location.href = getContextPath() + '/supplier/viewSorList/' + getEventType() + '/' + rfteventId;
            }
        },
        error : function(request, textStatus, errorThrown) {
            console.log("ERROR : " + request.getResponseHeader('error'));
            if (request.getResponseHeader('error')) {
                $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
                $('div[id=idGlobalError]').show();
                var error = request.getResponseHeader('error');
                $.jGrowl(error, {
                    sticky : false,
                    position : 'top-right',
                    theme : 'bg-red'
                });
            }
            $('#loading').hide();
        },
        complete : function() {
            $('#loading').hide();
        }
    });

});

