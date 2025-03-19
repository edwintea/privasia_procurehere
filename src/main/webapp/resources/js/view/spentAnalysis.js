$(document).ready(function () {
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    $('#month').val((new Date).getMonth()).trigger("chosen:updated");
    $('#year').val((new Date).getFullYear()).trigger("chosen:updated");

    $('.tablinks-overall').toggleClass('active');
    $('#yearSpend').html($('#year').val());
    $('#yearVolume').html($('#year').val());

    getDataForCurrentYear(header, token, 'line', 'overall');
    getValueForCurrentYear(header, token, 'line', 'overall');
    getDataForLastFiveYears(header, token, 'line', 'overall');
    getPODataBasedOnStatus(header, token, 'overall');
    getPODataBasedOnCategory(header, token, 'overall', 'pie');
    getPOValueBasedOnCategory(header, token, 'overall', 'pie');
    getPoDataForInternalAndExternalCo(header, token, 'line', 'overall');
    getPoVolumeForInternalAndExternalCo(header, token, 'line', 'overall');
    getTopSupplierDataForCurrentYear(header, token, 'pie', 'overall');
    getTopSupplierVolumeForCurrentYear(header, token, 'pie', 'overall');

    $('#updateDashboard').click(function () {
        $('.tab').find('.active').click();
    });

    $('#categoryVolumePieId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'overall', 'pie');
        }
    });

    $('#categoryVolumePieId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'subsidiary', 'pie');
        }
    });

    $('#categoryVolumePieId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'nonsubsidiary', 'pie');
        }
    });

    $('#categoryValuePieId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'subsidiary', 'pie');
        }
    });

    $('#categoryValuePieId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'overall', 'pie');
        }
    });

    $('#categoryValuePieId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'nonsubsidiary', 'pie');
        }
    });

    $('#categoryVolumeTreeId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'overall', 'tree');
        }
    });

    $('#categoryVolumeTreeId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'subsidiary', 'tree');
        }
    });

    $('#categoryVolumeTreeId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPODataBasedOnCategory(header, token, 'nonsubsidiary', 'tree');
        }
    });

    $('#categoryValueTreeId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'subsidiary', 'tree');
        }
    });

    $('#categoryValueTreeId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'overall', 'tree');
        }
    });

    $('#categoryValueTreeId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPOValueBasedOnCategory(header, token, 'nonsubsidiary', 'tree');
        }
    });

    $('#volumeLineIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'line', 'overall');
        }
    });

    $('#volumeLineIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'line', 'subsidiary');
        }
    });

    $('#volumeLineIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'line', 'nonsubsidiary');
        }
    });

    $('#volumeBarIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'bar', 'overall');
        }
    });

    $('#volumeBarIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'bar', 'subsidiary');
        }
    });

    $('#volumeBarIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForCurrentYear(header, token, 'bar', 'nonsubsidiary');
        }
    });

    $('#spendLineIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'line', 'overall');
        }
    });

    $('#spendLineIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'line', 'subsidiary');
        }
    });

    $('#spendLineIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'line', 'nonsubsidiary');
        }
    });

    $('#spendBarIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'bar', 'overall');
        }
    });

    $('#spendBarIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'bar', 'subsidiary');
        }
    });

    $('#spendBarIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getValueForCurrentYear(header, token, 'bar', 'nonsubsidiary');
        }
    });

    $('#previousYearsLineIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'line', 'overall');
        }
    });

    $('#previousYearsLineIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'line', 'subsidiary');
        }
    });

    $('#previousYearsLineIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'line', 'nonsubsidiary');
        }
    });

    $('#previousYearsBarIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'bar', 'overall');
        }
    });

    $('#previousYearsBarIconId_subsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'bar', 'subsidiary');
        }
    });

    $('#previousYearsBarIconId_nonsubsidiary').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getDataForLastFiveYears(header, token, 'bar', 'nonsubsidiary');
        }
    });

    $('#lineChartForSpentSubsidiaryId').click(function () {
        if (!$(this).hasClass('icon-active')) {
            var chart = echarts.init(document.getElementById('lineChartForSpentSubsidiary'));
            var options = chart.getOption();

            for (var index = 0; index < options.series.length; index++) {
                var element = options.series[index];
                element.type = 'line';
            }

            options.xAxis.boundaryGap = false;
            chart.setOption(options);
            $('#barChartForSpentSubsidiaryId').removeClass('icon-active');
            $('#lineChartForSpentSubsidiaryId').addClass('icon-active');
        }

    });

    $('#barChartForSpentSubsidiaryId').click(function () {
        if (!$(this).hasClass('icon-active')) {
            var chart = echarts.init(document.getElementById('lineChartForSpentSubsidiary'));
            var options = chart.getOption();

            for (var index = 0; index < options.series.length; index++) {
                var element = options.series[index];
                element.type = 'bar';
            }
            options.xAxis.boundaryGap = true;
            chart.setOption(options);
            $('#barChartForSpentSubsidiaryId').addClass('icon-active');
            $('#lineChartForSpentSubsidiaryId').removeClass('icon-active');
        }
    });

    $('#lineChartForVolumeSubsidiaryId').click(function () {
        if (!$(this).hasClass('icon-active')) {
            var chart = echarts.init(document.getElementById('lineChartForVolumeSubsidiary'));
            var options = chart.getOption();

            for (var index = 0; index < options.series.length; index++) {
                var element = options.series[index];
                element.type = 'line';
            }
            options.xAxis.boundaryGap = false;
            chart.setOption(options);
            $('#lineChartForVolumeSubsidiaryId').addClass('icon-active');
            $('#barChartForVolumeSubsidiaryId').removeClass('icon-active');
        }
    });

    $('#barChartForVolumeSubsidiaryId').click(function () {
        if (!$(this).hasClass('icon-active')) {
            var chart = echarts.init(document.getElementById('lineChartForVolumeSubsidiary'));
            var options = chart.getOption();

            for (var index = 0; index < options.series.length; index++) {
                var element = options.series[index];
                element.type = 'bar';
            }
            options.xAxis.boundaryGap = true;
            chart.setOption(options);
            $('#barChartForVolumeSubsidiaryId').addClass('icon-active');
            $('#lineChartForVolumeSubsidiaryId').removeClass('icon-active');
        }
    });

    $('#lineChartForSpentSubsidiaryId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPoDataForInternalAndExternalCo(header, token, 'line', 'overall');
        }
    });

    $('#barChartForSpentSubsidiaryId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPoDataForInternalAndExternalCo(header, token, 'bar', 'overall');
        }
    });

    $('#barChartForBudgetVolumeId_budget').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getBudgetValue(header, token, 'bar', 'budget', $('#costCenter').val(), $('#businessUnit').val());
        }
    });

    $('#lineChartForBudgetValueId_budget').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getBudgetValue(header, token, 'line', 'budget', $('#costCenter').val(), $('#businessUnit').val());
        }
    });

    $('#lineChartForVolumeSubsidiaryId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPoVolumeForInternalAndExternalCo(header, token, 'line', 'overall');
        }
    });

    $('#barChartForVolumeSubsidiaryId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getPoVolumeForInternalAndExternalCo(header, token, 'bar', 'overall');
        }
    });

    $('#selectAllOptionsForValue_overall').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_overall'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForValue_overall')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#selectAllOptionsForValue_subsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_subsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForValue_subsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#selectAllOptionsForValue_nonsubsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_nonsubsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForValue_nonsubsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForValue_overall').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_overall'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForValue_overall')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForValue_subsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_subsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForValue_subsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForValue_nonsubsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChartForPoValue_nonsubsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForValue_nonsubsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('#selectAllOptionsForVolume_overall').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_overall'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForVolume_overall')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#selectAllOptionsForVolume_subsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_subsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForVolume_subsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#selectAllOptionsForVolume_nonsubsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_nonsubsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#unselectAllOptionsForVolume_nonsubsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = true;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForVolume_overall').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_overall'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForVolume_overall')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForVolume_subsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_subsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForVolume_subsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('#unselectAllOptionsForVolume_nonsubsidiary').change(function () {
        var chart = echarts.init(document.getElementById('pieChart_nonsubsidiary'));
        var options = chart.getOption();
        if (this.checked) {
            $('#selectAllOptionsForVolume_nonsubsidiary')[0].checked = false;
            for (var index = 0; index < options.legend.length; index++) {
                var element = options.legend[index];
                for (var index_2 = 0; index_2 < Object.keys(element.selected).length; index_2++) {
                    const element_2 = Object.keys(element.selected)[index_2];
                    element.selected[element_2] = false;
                }
            }
            chart.setOption(options);
        }
    });

    $('.tablinks-overall').click(function () {

        $('.tablinks-overall').addClass('active');
        $('.tablinks-subsidiary').removeClass('active');
        $('.tablinks-nonsubsidiary').removeClass('active');
        $('.tablinks-budget').removeClass('active');

        $('#subsidiaryTab').addClass('hidden');
        $('#nonsubsidiaryTab').addClass('hidden');
        $('#subsidiaryTab').removeClass('show');
        $('#nonsubsidiaryTab').removeClass('show');
        $('#overallTab').removeClass('hidden');
        $('#overallTab').addClass('show');
        $('#budgetTab').removeClass('show');
        $('#budgetTab').addClass('hidden');

        getDataForCurrentYear(header, token, 'line', 'overall');
        getValueForCurrentYear(header, token, 'line', 'overall');
        getDataForLastFiveYears(header, token, 'line', 'overall');
        getPODataBasedOnStatus(header, token, 'overall');
        getPODataBasedOnCategory(header, token, 'overall', 'pie');
        getPOValueBasedOnCategory(header, token, 'overall', 'pie');
        getPoDataForInternalAndExternalCo(header, token, 'line', 'overall');
        getPoVolumeForInternalAndExternalCo(header, token, 'line', 'overall');
        getTopSupplierDataForCurrentYear(header, token, 'pie', 'overall');
        getTopSupplierVolumeForCurrentYear(header, token, 'pie', 'overall');

    });

    $('.tablinks-subsidiary').click(function () {


        $('.tablinks-subsidiary').addClass('active');
        $('.tablinks-overall').removeClass('active');
        $('.tablinks-nonsubsidiary').removeClass('active');
        $('.tablinks-budget').removeClass('active');

        $('#overallTab').addClass('hidden');
        $('#nonsubsidiaryTab').addClass('hidden');
        $('#overallTab').removeClass('show');
        $('#nonsubsidiaryTab').removeClass('show');
        $('#subsidiaryTab').removeClass('hidden');
        $('#subsidiaryTab').addClass('show');
        $('#budgetTab').removeClass('show');
        $('#budgetTab').addClass('hidden');

        getDataForCurrentYear(header, token, 'line', 'subsidiary');
        getValueForCurrentYear(header, token, 'line', 'subsidiary');
        getDataForLastFiveYears(header, token, 'line', 'subsidiary');
        getPODataBasedOnStatus(header, token, 'subsidiary');
        getPODataBasedOnCategory(header, token, 'subsidiary', 'pie');
        getPOValueBasedOnCategory(header, token, 'subsidiary', 'pie');
    });

    $('.tablinks-nonsubsidiary').click(function () {
        $('.tablinks-nonsubsidiary').addClass('active');
        $('.tablinks-overall').removeClass('active');
        $('.tablinks-subsidiary').removeClass('active');
        $('.tablinks-budget').removeClass('active');

        $('#overallTab').addClass('hidden');
        $('#subsidiaryTab').addClass('hidden');
        $('#overallTab').removeClass('show');
        $('#subsidiaryTab').removeClass('show');
        $('#nonsubsidiaryTab').removeClass('hidden');
        $('#nonsubsidiaryTab').addClass('show');
        $('#budgetTab').removeClass('show');
        $('#budgetTab').addClass('hidden');

        getDataForCurrentYear(header, token, 'line', 'nonsubsidiary');
        getValueForCurrentYear(header, token, 'line', 'nonsubsidiary');
        getDataForLastFiveYears(header, token, 'line', 'nonsubsidiary');
        getPODataBasedOnStatus(header, token, 'nonsubsidiary');
        getPODataBasedOnCategory(header, token, 'nonsubsidiary', 'pie');
        getPOValueBasedOnCategory(header, token, 'nonsubsidiary', 'pie');
    });

    $('#supplierVolumeBarIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getTopSupplierVolumeForCurrentYear(header, token, 'bar', 'overall');
        }
    });

    $('#supplierVolumePieIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getTopSupplierVolumeForCurrentYear(header, token, 'pie', 'overall');
        }
    });

    $('#supplierValueBarIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getTopSupplierDataForCurrentYear(header, token, 'bar', 'overall');
        }
    });

    $('#supplierValuePieIconId_overall').click(function () {
        if (!$(this).hasClass('icon-active')) {
            getTopSupplierDataForCurrentYear(header, token, 'pie', 'overall');
        }
    });

    $('.tablinks-budget').click(function () {

        $('.tablinks-budget').addClass('active');
        $('.tablinks-overall').removeClass('active');
        $('.tablinks-subsidiary').removeClass('active');
        $('.tablinks-nonsubsidiary').removeClass('active');

        $('#subsidiaryTab').addClass('hidden');
        $('#nonsubsidiaryTab').addClass('hidden');
        $('#subsidiaryTab').removeClass('show');
        $('#nonsubsidiaryTab').removeClass('show');
        $('#overallTab').removeClass('show');
        $('#overallTab').addClass('hidden');
        $('#budgetTab').addClass('show')
        $('#budgetTab').removeClass('hidden')

        getBudgetValue(header, token, 'line', 'budget', $('#costCenter').val(), $('#businessUnit').val());

    });

    $('#updateGraph').click(function () {
        getBudgetValue(header, token, 'line', 'budget', $('#costCenter').val(), $('#businessUnit').val());
    });

});

function getDataForCurrentYear(header, token, type, tab) {
    var barChart = echarts.init(document.getElementById('barChart_' + tab));
    barChart.showLoading();
    barChart.clear();

    var url = '';

    tab == 'overall' ? url = getContextPath() + '/buyer/getPoDataByMonth/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoDataForSubsidiaryByMonth/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoDataForNonSubsidiaryByMonth/' + $('#month').val() + '/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            // $('#loading').show();
        },
        success: function (data) {
            var option = {
                color: ['#308dcc', '#000', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {},
                xAxis: {
                    boundaryGap: false,
                    name: 'Month',
                    nameLocation: 'middle',
                    nameTextStyle: { padding: [15, 0, 5, 0] },
                    data: [],
                    axisLabel: {
                        // formatter: function (value) {
                        //     return value.substring(0, 10) + '...';
                        // }
                        rotate: 20,
                        fontSize: 10
                    }
                },
                yAxis: {
                    name: 'PO Volume ( in numbers )',
                    nameLocation: 'middle',
                    nameRotate: 90,
                    nameTextStyle: { padding: [15, 0, 15, 0] },
                },
                series: [{
                    name: 'PO volume',
                    type: type != 'mixed' ? type : 'bar',
                    data: [],
                    label: { normal: { show: false, position: 'top', color: 'black' } },
                    smooth: true,
                    areaStyle: {
                        color: '#308dcc',
                    }
                }]
            }

            if (type == 'mixed') {
                option.series.push({
                    type: 'line',
                    data: [],
                    label: { normal: { show: true, position: 'top', color: 'black' } },
                    smooth: true,
                })
            }

            for (var index = 0; index < data.length; index++) {
                option.xAxis.data.push(data[index].name);
                option.series[0].data.push(data[index].value);
            }

            if (type == 'mixed') {
                for (var index = 0; index < data.length; index++) {
                    option.series[1].data.push(data[index].value);
                }
            }

            var total = 0
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                total = total + element.value;
            }

            $('#totalVolumeId_' + tab).html('Total Volume : ' + total);
            $('#crrYearVolumeId').html(total);
            $('#yearVolume').html($('#year').val());
            $('#tableSpan_' + tab).html($('#year').val());

            $('#volumeByMonth_' + tab).html('&nbsp;&nbsp;From ' + (data[0] ? data[0].startDate : 'January ' + $('#year').val()) + ' to ' + (data[data.length - 1] ? data[data.length - 1].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));

            $('#poVolumeDataTable_' + tab).html('');
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                var html = '';
                html = '<tr><td>' + element.name + '</td><td>' + element.value + '</td><td>' + (((element.value / total) * 100).toFixed(2) != "NaN" ? ((element.value / total) * 100).toFixed(2) : 0) + ' %' + '</td></tr>'
                $('#poVolumeDataTable_' + tab).append(html);
            }

            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            // type == 'mixed' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            barChart.hideLoading();
            barChart.setOption(option, true);

            if (type == 'line') {
                $('#volumeLineIconId_' + tab).addClass('icon-active');
                $('#volumeBarIconId_' + tab).removeClass('icon-active');
            } else {
                $('#volumeBarIconId_' + tab).addClass('icon-active');
                $('#volumeLineIconId_' + tab).removeClass('icon-active');
            }

            // $('#loading').hide();
        },
        error: function (error) {
            console.log(error);
            barChart.hideLoading();
            // $('#loading').hide();
        }
    });
}

function getValueForCurrentYear(header, token, type, tab) {
    var lineChartForValue = echarts.init(document.getElementById('lineChartForValue_' + tab));
    lineChartForValue.showLoading();
    lineChartForValue.clear();

    var url = '';

    tab == 'overall' ? url = getContextPath() + '/buyer/getPoValueByMonth/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoValueForSubsidiaryByMonth/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoValueForNonSubsidiaryByMonth/' + $('#month').val() + '/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            // $('#loading').show();
        },
        success: function (data) {
            var totalOfData = 0;
            for (var index = 0; index < genData(data).seriesData.length; index++) {
                var element = genData(data).seriesData[index];
                totalOfData = totalOfData + element.value;
            }
            var option = {
                color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {
                    trigger: 'item',
                    formatter: function (params) {
                        return 'PO Spend <br/>' + params.name + ': RM ' + Number(params.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                    }
                },
                xAxis: {
                    boundaryGap: false,
                    data: [],
                    name: 'Month',
                    nameLocation: 'middle',
                    nameTextStyle: { padding: [15, 0, 5, 0] },
                    axisLabel: {
                        rotate: 20,
                        fontSize: 10
                    }
                },
                yAxis: {
                    name: 'PO Value ( in RM )',
                    nameLocation: 'middle',
                    nameRotate: 90,
                    nameTextStyle: { padding: [0, 0, 40, 0] },
                },
                series: [{
                    name: 'PO spent',
                    type: type,
                    barMaxWidth: 45,
                    data: [],
                    smooth: true,
                    label: { normal: { show: false, position: 'top', color: 'black' } },
                    areaStyle: {
                        color: '#308dcc',
                    }
                }]
            }

            for (var index = 0; index < data.length; index++) {
                option.xAxis.data.push(data[index].name);
                option.series[0].data.push(data[index].value);
            }

            var total = 0

            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                total = total + element.value;
            }

            $('#totalValueId_' + tab).html('Total Value : RM ' + Number(total).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#crrYearSpendId').html('RM ' + Number(total).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#yearSpend').html($('#year').val());
            $('#tableValueSpan_' + tab).html($('#year').val());

            $('#valueByMonth_' + tab).html('&nbsp;&nbsp;From ' + (data[0] ? data[0].startDate : 'January ' + $('#year').val()) + ' to ' + (data[data.length - 1] ? data[data.length - 1].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));

            $('#poValueDataTable_' + tab).html('');
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                var html = '';
                html = '<tr><td>' + element.name + '</td><td>' + ('RM ' + Number(element.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })) + '</td><td>' + (((element.value / total) * 100).toFixed(2) != "NaN" ? ((element.value / total) * 100).toFixed(2) : 0) + ' %' + '</td></tr>'
                $('#poValueDataTable_' + tab).append(html);
            }

            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;

            lineChartForValue.hideLoading();
            lineChartForValue.setOption(option, true);

            if (type == 'line') {
                $('#spendLineIconId_' + tab).addClass('icon-active');
                $('#spendBarIconId_' + tab).removeClass('icon-active');
            } else {
                $('#spendBarIconId_' + tab).addClass('icon-active');
                $('#spendLineIconId_' + tab).removeClass('icon-active');
            }

            // $('#loading').hide();
        },
        error: function (error) {
            console.log(error);
            lineChartForValue.hideLoading();
            // $('#loading').hide();
        }
    });
}

function getDataForLastFiveYears(header, token, type, tab) {
    var lineChart = echarts.init(document.getElementById('lineChart_' + tab));
    lineChart.showLoading();
    lineChart.clear();

    var url = '';

    tab == 'overall' ? url = getContextPath() + '/buyer/getPoDataForPreviousYears/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoDataForSubsidiaryPreviousYears/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoDataForNonSubsidiaryPreviousYears/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            // $('#loading').show();
        },
        success: function (data) {
            var option = {
                color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: []
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: [],
                    name: 'Months',
                    nameLocation: 'middle',
                    nameTextStyle: { padding: [15, 0, 5, 0] }
                },
                yAxis: {
                    type: 'value',
                    name: 'Volume ( in numbers )',
                    nameLocation: 'middle',
                    nameRotate: 90,
                    nameTextStyle: { padding: [15, 0, 15, 0] }
                },
                series: []
            };

            for (var index = 0; index < data[Object.keys(data)[0]].length; index++) {
                option.legend.data.push(data[Object.keys(data)[0]][index].name);
                option.xAxis.data.push(data[Object.keys(data)[0]][index].name);
            }


            for (var index = 0; index < Object.keys(data).length; index++) {
                var seriesData = { name: '', type: type, barMaxWidth: 45, smooth: true, data: [], areaStyle: {}, label: { normal: { show: false, position: 'top', color: 'black' } } }
                seriesData.name = Object.keys(data)[index];
                for (var index_2 = 0; index_2 < data[Object.keys(data)[index]].length; index_2++) {
                    seriesData.data.push(data[Object.keys(data)[index]][index_2].value)
                }
                option.series.push(seriesData)
            }

            $('#valueForPrevious_' + tab).html('&nbsp;&nbsp;From ' + ($('#year').val() - 4) + ' to ' + $('#year').val());

            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;

            if (type == 'line') {
                $('#previousYearsLineIconId_' + tab).addClass('icon-active');
                $('#previousYearsBarIconId_' + tab).removeClass('icon-active');
            } else {
                $('#previousYearsBarIconId_' + tab).addClass('icon-active');
                $('#previousYearsLineIconId_' + tab).removeClass('icon-active');
            }

            lineChart.hideLoading();
            lineChart.setOption(option, true);

            // $('#loading').hide();
        },
        error: function (error) {
            console.log(error);
            lineChart.hideLoading();
            // $('#loading').hide();
        }
    });
}

function getPODataBasedOnStatus(header, token, tab) {
    var url = '';
    tab == 'overall' ? url = getContextPath() + '/buyer/getPoDataBasedOnStatus/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoDataForSubsidiaryBasedOnStatus/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoDataForNonsubsidiaryBasedOnStatus/' + $('#month').val() + '/' + $('#year').val() : null;
    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
            // $('#loading').show();
        },
        success: function (data) {
            data.find(function (e) { return e.name == "ACCEPTED" }) == undefined ? data.push({ name: "ACCEPTED", value: 0, count: 0 }) : ''
            data.find(function (e) { return e.name == "CANCELLED" }) == undefined ? data.push({ name: "CANCELLED", value: 0, count: 0 }) : ''
            data.find(function (e) { return e.name == "DECLINED" }) == undefined ? data.push({ name: "DECLINED", value: 0, count: 0 }) : ''
            data.find(function (e) { return e.name == "ORDERED" }) == undefined ? data.push({ name: "ORDERED", value: 0, count: 0 }) : ''
            data.find(function (e) { return e.name == "READY" }) == undefined ? data.push({ name: "READY", value: 0, count: 0 }) : ''
            var option = {
                color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {
                    show: false,
                },
                legend: {
                    show: false,
                },
                animation: false,
                series: [
                    {
                        name: 'PO STATUS',
                        type: 'pie',
                        radius: ['77%', '80%'],
                        avoidLabelOverlap: false,
                        label: {
                            show: true,
                            position: 'center',
                            fontSize: '20',
                            formatter: '',
                            color: ''
                        },
                        labelLine: {
                            show: false
                        },
                        color: [],
                        data: []
                    }
                ]
            };
            // Calculate total
            var totalPos = 0;
            for (var index = 0; index < data.length; index++) {
                totalPos = totalPos + data[index].value;
            }
            $('#poCountId').html(totalPos);
            // Setting as 1 so that graph is shown correctly
            if (totalPos == 0) {
                totalPos = 1;
            }
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                if (element.name === 'READY') {
                    option.series[0].label.formatter = element.value.toString();
                    option.series[0].label.color = 'white';
                    option.series[0].data = [{ name: element.name, value: element.value }, { name: 'Total', value: totalPos }];
                    option.series[0].color = ['white', '#59d68e'];
                    // pieChartForReady.setOption(option, true);
                    $('#readyStatusId').html(element.value);
                } else if (element.name === 'ORDERED') {
                    option.series[0].label.formatter = element.value.toString();
                    option.series[0].label.color = 'white';
                    option.series[0].data = [{ name: element.name, value: element.value }, { name: 'Total', value: totalPos }];
                    option.series[0].color = ['white', '#5dace2'];
                    // pieChartForOrdered.setOption(option, true);
                    $('#orderedStatusId').html(element.value);
                } else if (element.name === 'ACCEPTED') {
                    option.series[0].label.formatter = element.value.toString();
                    option.series[0].label.color = 'white';
                    option.series[0].data = [{ name: element.name, value: element.value }, { name: 'Total', value: totalPos }];
                    option.series[0].color = ['white', '#c8cbe9'];
                    // pieChartForAccepted.setOption(option, true);
                    $('#acceptedStatusId').html(element.value);
                } else if (element.name === 'DECLINED') {
                    option.series[0].label.formatter = element.value.toString();
                    option.series[0].label.color = 'white';
                    option.series[0].data = [{ name: element.name, value: element.value }, { name: 'Total', value: totalPos }];
                    option.series[0].color = ['white', '#f4c22bab'];
                    // pieChartForDeclined.setOption(option, true);
                } else if (element.name === 'CANCELLED') {
                    option.series[0].label.formatter = element.value.toString();
                    option.series[0].label.color = 'white';
                    option.series[0].data = [{ name: element.name, value: element.value }, { name: 'Total', value: totalPos }];
                    option.series[0].color = ['white', '#eb9752'];
                    // pieChartForCancelled.setOption(option, true);
                    $('#cancelledStatusId').html(element.value);
                }
            }
        },
        error: function (error) {
            console.log(error);
        }
    });
}

function getPODataBasedOnCategory(header, token, tab, type) {
    var pieChart = echarts.init(document.getElementById('pieChart_' + tab));
    pieChart.showLoading();
    pieChart.clear();
    var url = '';
    tab == 'overall' ? url = getContextPath() + '/buyer/getPoDataBasedOnCategory/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoDataBasedOnCategoryForSubsidiary/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoDataBasedOnCategoryForNonsubsidiary/' + $('#month').val() + '/' + $('#year').val() : null;
    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            var totalOfData = 0;
            for (var index = 0; index < genData(response).seriesData.length; index++) {
                var element = genData(response).seriesData[index];
                totalOfData = totalOfData + element.value;
            }
            if (type == 'pie') {
                var data = genData(response);
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Category : ' + params.name + ' <br/> Volume : ' + Number(params.value).toLocaleString() + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                        }
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: data.legendData,
                        selected: data.selected
                    },
                    series: [
                        {
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
                            data: data.seriesData,
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
            } else if (type == 'tree') {
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    legend: {
                        data: [],
                        selectedMode: 'single',
                        top: 55,
                        itemGap: 5,
                        borderRadius: 5
                    },
                    tooltip: {
                    },
                    series: [{
                        type: 'treemap',
                        nodeClick: false,
                        roam: false,
                        tooltip: {
                            formatter: function (params) {
                                return 'Category : ' + params.name + ' <br/> Volume : ' + Number(params.value).toLocaleString() + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                            }
                        },
                        data: [],
                        zoomToNodeRatio: 0,
                        breadcrumb: {
                            show: false
                        }
                    }]
                }
                for (var index = 0; index < response.length; index++) {
                    var element = response[index];
                    option.series[0].data.push(element);
                }

                for (var index = 0; index < response.length; index++) {
                    var element = response[index];
                    option.legend.data.push(element.value);
                }

                for (var index = 0; index < option.series[0].data.length; index++) {
                    var element = option.series[0].data[index];
                    var label = {
                        show: true,
                        position: ['1%', '1%'],
                        formatter: element.name
                    }
                    element['label'] = label
                }
            }

            var total = 0;
            for (var index = 0; index < response.length; index++) {
                var element = response[index];
                total = total + element.value;
            }
            $('#totalVolumeCategoryId_' + tab).html('Total Volume : ' + Number(total).toLocaleString());
            $('#categoryVolume').html(Number(total).toLocaleString());
            $('#volumeByCategory_' + tab).html('&nbsp;&nbsp;From ' + (response[0] ? response[0].startDate : 'January ' + $('#year').val()) + ' to ' + (response[0] ? response[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));
            $('#totalVolumeCategoryTableId_' + tab).html('');
            for (var index = 0; index < response.length; index++) {
                var element = response[index];
                var html = '';
                html = '<tr><td>' + Number(index + 1) + '</td><td class="overflow-table">' + element.name + '</td><td>' + element.value + '</td><td>' + ((element.value / total) * 100).toFixed(2) + ' %' + '</td></tr>'
                $('#totalVolumeCategoryTableId_' + tab).append(html);
            }
            if (type == 'tree') {
                $('#categoryVolumeTreeId_' + tab).addClass('icon-active');
                $('#categoryVolumePieId_' + tab).removeClass('icon-active');
                $('#includeExcludeVolume_' + tab).children().addClass('hidden');
            } else {
                $('#categoryVolumePieId_' + tab).addClass('icon-active');
                $('#categoryVolumeTreeId_' + tab).removeClass('icon-active');
                $('#includeExcludeVolume_' + tab).children().removeClass('hidden');
            }
            pieChart.setOption(option, true);
            pieChart.hideLoading();
        },
        error: function (error) {
            console.log(error);
            pieChart.hideLoading();
        }
    });
}

function getPOValueBasedOnCategory(header, token, tab, type) {
    var pieChartForPoValue = echarts.init(document.getElementById('pieChartForPoValue_' + tab));
    pieChartForPoValue.clear();
    pieChartForPoValue.showLoading();
    var url = '';
    tab == 'overall' ? url = getContextPath() + '/buyer/getPoValueBasedOnCategory/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getPoValueBasedOnCategoryForSubsidiary/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getPoValueBasedOnCategoryForNonSubsidiary/' + $('#month').val() + '/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (response) {
            var totalOfData = 0;
            for (var index = 0; index < genData(response).seriesData.length; index++) {
                var element = genData(response).seriesData[index];
                totalOfData = totalOfData + element.value;
            }
            if (type == 'pie') {
                var data = genData(response);
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Category : ' + params.name + ' <br/> Value : RM ' + Number(params.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                        }
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: data.legendData,
                        selected: data.selected
                    },
                    series: [
                        {
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
                            data: data.seriesData,
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
            } else if (type == 'tree') {
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    legend: {
                        data: [],
                        selectedMode: 'single',
                        top: 55,
                        itemGap: 5,
                        borderRadius: 5
                    },
                    tooltip: {
                    },
                    series: [{
                        type: 'treemap',
                        nodeClick: false,
                        roam: false,
                        tooltip: {
                            formatter: function (params) {
                                return 'Category : ' + params.name + ' <br/> Value : RM ' + Number(params.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                            }

                        },
                        data: [],
                        zoomToNodeRatio: 0,
                        breadcrumb: {
                            show: false
                        }
                    }]
                }
                for (var index = 0; index < response.length; index++) {
                    var element = response[index];
                    option.series[0].data.push(element);
                }

                for (var index = 0; index < response.length; index++) {
                    var element = response[index];
                    option.legend.data.push(element.value);
                }

                for (var index = 0; index < option.series[0].data.length; index++) {
                    var element = option.series[0].data[index];
                    var label = {
                        show: true,
                        position: ['1%', '1%'],
                        formatter: element.name
                    }
                    element['label'] = label

                }
            }
            var total = 0;
            for (var index = 0; index < response.length; index++) {
                var element = response[index];
                total = total + element.value;
            }
            $('#totalValueCategoryId_' + tab).html('Total Value : RM ' + Number(total).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#categorySpend').html('RM ' + Number(total).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#valueByCategory_' + tab).html('&nbsp;&nbsp;From ' + (response[0] ? response[0].startDate : 'January ' + $('#year').val()) + ' to ' + (response[0] ? response[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));
            $('#totalValueCategoryTableId_' + tab).html('');
            for (var index = 0; index < response.length; index++) {
                var element = response[index];
                var html = '';
                html = '<tr><td>' + Number(index + 1) + '</td><td class="overflow-table">' + element.name + '</td><td>' + ('RM ' + Number(element.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })) + '</td><td>' + ((element.value / total) * 100).toFixed(2) + ' %' + '</td></tr>'
                $('#totalValueCategoryTableId_' + tab).append(html);
            }
            if (type == 'tree') {
                $('#categoryValueTreeId_' + tab).addClass('icon-active');
                $('#includeExcludeValue_' + tab).children().addClass('hidden');
                $('#categoryValuePieId_' + tab).removeClass('icon-active');
            } else {
                $('#categoryValuePieId_' + tab).addClass('icon-active');
                $('#categoryValueTreeId_' + tab).removeClass('icon-active');
                $('#includeExcludeValue_' + tab).children().removeClass('hidden');
            }
            pieChartForPoValue.setOption(option, true);
            pieChartForPoValue.hideLoading();
        },
        error: function (error) {
            console.log(error);
            pieChartForPoValue.hideLoading();
        }
    });
}

function getPoDataForInternalAndExternalCo(header, token, type, tab) {
    var lineChartForSpentSubsidiary = echarts.init(document.getElementById('lineChartForSpentSubsidiary_' + tab));
    lineChartForSpentSubsidiary.showLoading();
    lineChartForSpentSubsidiary.clear();
    $.ajax({
        url: getContextPath() + '/buyer/getPoDataForInternalAndExternalCo/' + $('#month').val() + '/' + $('#year').val(),
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (internalExternalCoData) {
            var option = {
                color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {
                    trigger: 'axis',
                    formatter: function (params) {
                        var paramZero = ((params[0].value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
                        var paramOne = ((params[1].value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
                        return params[0].name + ' <br/>Inter-co: RM ' + paramZero + ' <br/> External: RM ' + paramOne;
                    }
                },
                legend: {
                    data: ['Inter-co', 'External']
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    axisLabel: {
                        rotate: 20,
                        fontSize: 10
                    },
                    data: [],
                    name: 'Business unit',
                    nameLocation: 'middle',
                    nameTextStyle: { padding: [30, 0, 0, 0] }
                },
                yAxis: {
                    type: 'value',
                    name: 'PO Value ( in RM )',
                    nameLocation: 'middle',
                    nameRotate: 90,
                    nameTextStyle: { padding: [0, 0, 40, 0] }
                },
                series: [
                    {
                        name: 'Inter-co',
                        type: type,
                        barMaxWidth: 45,
                        data: [],
                        smooth: true,
                        label: { normal: { show: false, position: 'top', color: 'black' } },
                        areaStyle: {}
                    },
                    {
                        name: 'External',
                        type: type,
                        barMaxWidth: 45,
                        data: [],
                        smooth: true,
                        label: { normal: { show: false, position: 'top', color: 'black' } },
                        areaStyle: {}
                    },
                ]
            };

            for (var index = 0; index < internalExternalCoData.length; index++) {
                var element = internalExternalCoData[index];
                if (option.xAxis.data.find(function (e) { return e == element.displayName }) == undefined) {
                    option.xAxis.data.push(element.displayName);
                }
            }

            for (var index = 0; index < option.xAxis.data.length; index++) {
                var element = option.xAxis.data[index];
                var dataArray = internalExternalCoData.filter(function (e) { return (e.displayName == element && (e.name != null || e.name != undefined)) });
                if (dataArray.length == 2) {
                    var data = dataArray.find(function (e) { return e.name == 'EXTERNAL' });
                    if (data) {
                        option.series[1].data.push(data.value);
                    }
                    data = dataArray.find(function (e) { return e.name == 'INTERNAL' });
                    if (data) {
                        option.series[0].data.push(data.value);
                    }
                } else if (dataArray.length == 1) {
                    var data = dataArray.find(function (e) { return e.name == 'EXTERNAL' });
                    if (data) {
                        option.series[1].data.push(data.value);
                    } else {
                        option.series[1].data.push(0);
                    }
                    data = dataArray.find(function (e) { return e.name == 'INTERNAL' });
                    if (data) {
                        option.series[0].data.push(data.value);
                    } else {
                        option.series[0].data.push(0);
                    }
                }
            }

            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            $('#interAndExternalValue_' + tab).html('&nbsp;&nbsp;From ' + (internalExternalCoData[0] ? internalExternalCoData[0].startDate : 'January ' + $('#year').val()) + ' to ' + (internalExternalCoData[0] ? internalExternalCoData[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));
            if (type == 'line') {
                $('#lineChartForSpentSubsidiaryId_' + tab).addClass('icon-active');
                $('#barChartForSpentSubsidiaryId_' + tab).removeClass('icon-active');
            } else {
                $('#barChartForSpentSubsidiaryId_' + tab).addClass('icon-active');
                $('#lineChartForSpentSubsidiaryId_' + tab).removeClass('icon-active');
            }
            lineChartForSpentSubsidiary.setOption(option, true);
            var total_interco = 0;
            for (var index = 0; index < option.series[0].data.length; index++) {
                var element = option.series[0].data[index];
                total_interco = total_interco + element;
            }
            var total_external = 0;
            for (var index = 0; index < option.series[1].data.length; index++) {
                var element = option.series[1].data[index];
                total_external = total_external + element;
            }
            $('#totalIntercoId').html('RM ' + total_interco.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#totalExternalId').html('RM ' + total_external.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            $('#totalIntercoPercentId').html(((total_interco / (total_external + total_interco) * 100).toFixed(2)) == "NaN" ? 0 : ((total_interco / (total_external + total_interco) * 100).toFixed(2)));
            $('#totalExternalPercentId').html(((total_external / (total_external + total_interco) * 100).toFixed(2)) == "NaN" ? 0 : ((total_external / (total_external + total_interco) * 100).toFixed(2)));
            $('#totalInterExternalSpendId').html('RM ' + (total_interco + total_external).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }));
            lineChartForSpentSubsidiary.hideLoading();
        },
        error: function (error) {
            console.log(error);
            lineChartForSpentSubsidiary.hideLoading();
        }
    });
}

function getPoVolumeForInternalAndExternalCo(header, token, type, tab) {
    var lineChartForVolumeSubsidiary = echarts.init(document.getElementById('lineChartForVolumeSubsidiary_' + tab));
    lineChartForVolumeSubsidiary.showLoading();
    lineChartForVolumeSubsidiary.clear();
    $.ajax({
        url: getContextPath() + '/buyer/getPoCountForInternalAndExternalCo/' + $('#month').val() + '/' + $('#year').val(),
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (internalExternalCoVolume) {

            var option = {
                color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: ['Inter-co', 'External']
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    axisLabel: {
                        rotate: 20,
                        fontSize: 10
                    },
                    data: [],
                    name: 'Business Unit',
                    nameLocation: 'middle',
                    nameTextStyle: { padding: [30, 0, 0, 0] }
                },
                yAxis: {
                    type: 'value',
                    name: 'PO Volume ( in numbers )',
                    nameLocation: 'middle',
                    nameRotate: 90,
                    nameTextStyle: { padding: [15, 0, 15, 0] }
                },
                series: [
                    {
                        name: 'Inter-co',
                        type: type,
                        barMaxWidth: 45,
                        data: [],
                        smooth: true,
                        label: { normal: { show: false, position: 'top', color: 'black' } },
                        areaStyle: {}
                    },
                    {
                        name: 'External',
                        type: type,
                        barMaxWidth: 45,
                        data: [],
                        smooth: true,
                        label: { normal: { show: false, position: 'top', color: 'black' } },
                        areaStyle: {}
                    },
                ]
            };

            for (var index = 0; index < internalExternalCoVolume.length; index++) {
                var element = internalExternalCoVolume[index];
                if (option.xAxis.data.find(function (e) { return e == element.displayName }) == undefined) {
                    option.xAxis.data.push(element.displayName);
                }
            }

            for (var index = 0; index < option.xAxis.data.length; index++) {
                var element = option.xAxis.data[index];

                var dataArray = internalExternalCoVolume.filter(function (e) { return e.displayName == element && (e.name != null || e.name != undefined) });
                if (dataArray.length == 2) {
                    var data = dataArray.find(function (e) { return e.name == 'EXTERNAL' });
                    if (data) {
                        option.series[1].data.push(data.value);
                    }
                    data = dataArray.find(function (e) { return e.name == 'INTERNAL' });
                    if (data) {
                        option.series[0].data.push(data.value);
                    }
                } else if (dataArray.length == 1) {
                    var data = dataArray.find(function (e) { return e.name == 'EXTERNAL' });
                    if (data) {
                        option.series[1].data.push(data.value);
                    } else {
                        option.series[1].data.push(0);
                    }
                    data = dataArray.find(function (e) { return e.name == 'INTERNAL' });
                    if (data) {
                        option.series[0].data.push(data.value);
                    } else {
                        option.series[0].data.push(0);
                    }
                }
            }

            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            $('#interAndExternalVolume_' + tab).html('&nbsp;&nbsp;From ' + (internalExternalCoVolume[0] ? internalExternalCoVolume[0].startDate : 'January ' + $('#year').val()) + ' to ' + (internalExternalCoVolume[0] ? internalExternalCoVolume[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));
            if (type == 'line') {
                $('#lineChartForVolumeSubsidiaryId_' + tab).addClass('icon-active');
                $('#barChartForVolumeSubsidiaryId_' + tab).removeClass('icon-active');
            } else {
                $('#barChartForVolumeSubsidiaryId_' + tab).addClass('icon-active');
                $('#lineChartForVolumeSubsidiaryId_' + tab).removeClass('icon-active');
            }
            var total_interco = 0;
            for (var index = 0; index < option.series[0].data.length; index++) {
                var element = option.series[0].data[index];
                total_interco = total_interco + element;
            }
            var total_external = 0;
            for (var index = 0; index < option.series[1].data.length; index++) {
                var element = option.series[1].data[index];
                total_external = total_external + element;
            }
            $('#totalIntercoCountId').html(total_interco.toLocaleString());
            $('#totalExternalCountId').html(total_external.toLocaleString());
            $('#totalIntercoCountPercentId').html(((total_interco / (total_external + total_interco) * 100).toFixed(2)) == "NaN" ? 0 : ((total_interco / (total_external + total_interco) * 100).toFixed(2)));
            $('#totalExternalCountPercentId').html(((total_external / (total_external + total_interco) * 100).toFixed(2)) == "NaN" ? 0 : ((total_external / (total_external + total_interco) * 100).toFixed(2)));
            $('#totalInterExternalCountId').html((total_interco + total_external).toLocaleString());
            lineChartForVolumeSubsidiary.setOption(option, true);
            lineChartForVolumeSubsidiary.hideLoading();
        },
        error: function (error) {
            console.log(error);
            lineChartForVolumeSubsidiary.hideLoading();
        }
    });
}

function getBudgetValue(header, token, type, tab, costCenter, businessUnit) {

    var lineChartForBudgetVolume = echarts.init(document.getElementById('lineChartForBudgetVolume_' + tab));
    lineChartForBudgetVolume.showLoading();
    lineChartForBudgetVolume.clear();
    $.ajax({
        url: getContextPath() + '/buyer/getBudgetValue/' + (costCenter ? costCenter : null) + '/' + (businessUnit ? businessUnit : null) + '/' + $('#month').val() + '/' + $('#year').val(),
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (budgetData) {
            var option = {
                color: ['#3b5998', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                tooltip: {},
                xAxis: {
                    boundaryGap: false,
                    axisLabel: {
                        rotate: 20,
                        fontSize: 10
                    },
                    data: []
                },
                yAxis: {},
                series: [{
                    type: type,
                    barMaxWidth: 45,
                    data: [],
                    smooth: true,
                    label: { normal: { show: false, position: 'top', color: 'black' } },
                    areaStyle: {
                        color: '#3b5998',
                    }
                }]
            }
            type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            for (var index = 0; index < budgetData.length; index++) {
                var element = budgetData[index];
                option.xAxis.data.push(element.businessUnit + ' - ' + element.costCenter);
            }
            for (var index = 0; index < budgetData.length; index++) {
                var element = budgetData[index];
                option.series[0].data.push(element.value);
            }
            if (type == 'line') {
                $('#lineChartForBudgetValueId_' + tab).addClass('icon-active');
                $('#barChartForBudgetVolumeId_' + tab).removeClass('icon-active');
            } else {
                $('#barChartForBudgetVolumeId_' + tab).addClass('icon-active');
                $('#lineChartForBudgetValueId_' + tab).removeClass('icon-active');
            }
            if ($('#costCenter').val() != null && $('#costCenter').val().length > 0 &&
                $('#businessUnit').val() != null && $('#businessUnit').val().length > 0) {
                var additionalInfo = {
                    silent: true,
                    lineStyle: {
                        color: 'black'
                    },
                    label: {
                        show: true,
                        position: 'middle',
                        formatter: 'Total budget Amount : {c}'
                    },
                    data: [{
                        yAxis: 0
                    }]
                }
                option.series[0]['markLine'] = additionalInfo;
                option.series[0].markLine.data[0].yAxis = budgetData[0] ? budgetData[0].totabBudgetAmount : 0;
            }
            lineChartForBudgetVolume.setOption(option, true);
            lineChartForBudgetVolume.hideLoading();
        },
        error: function (error) {
            console.log(error);
            lineChartForBudgetVolume.hideLoading();
        }
    });
}

function getTopSupplierVolumeForCurrentYear(header, token, type, tab) {
    var supplierBarChart_ = echarts.init(document.getElementById('supplierBarChart_' + tab));
    supplierBarChart_.showLoading();
    supplierBarChart_.clear();
    var url = '';
    tab == 'overall' ? url = getContextPath() + '/buyer/getTopSuppliersByVolume/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getTopSuppliersByVolume/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getTopSuppliersByVolume/' + $('#month').val() + '/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            var totalOfData = 0;
            for (var index = 0; index < genData(data).seriesData.length; index++) {
                var element = genData(data).seriesData[index];
                totalOfData = totalOfData + element.value;
            }
            if (type == 'pie') {
                var response = genData(data);
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbd', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Supplier : ' + params.name + ' <br/> Volume : ' + Number(params.value).toLocaleString() + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
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
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: response.legendData,
                        selected: response.selected
                    },
                    series: [
                        {
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
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
                var option = {
                    color: ['#308dcc', '#000', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Supplier : ' + params.name + ' <br/> Volume :  ' + Number(params.value).toLocaleString() + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                        }
                    },
                    xAxis: {
                        boundaryGap: false,
                        type: 'category',
                        show: true,
                        data: [],
                        name: 'Suppliers',
                        nameLocation: 'middle',
                        nameTextStyle: { padding: [15, 0, 5, 0] }
                    },
                    yAxis: {
                        type: 'value',
                        show: true,
                        name: 'PO Volume ( in numbers )',
                        nameLocation: 'middle',
                        nameRotate: 90,
                        nameTextStyle: { padding: [15, 0, 15, 0] }
                    },
                    series: [{
                        name: 'PO volume',
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
                for (var index = 0; index < data.length; index++) {
                    option.xAxis.data.push(data[index].name);
                    option.series[0].data.push(data[index].value);
                }
                type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            }

            var total = 0
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                total = total + element.value;
            }

            $('#supplierVolumeDataTable_' + tab).html('');
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                var html = '';
                html = '<tr><td>' + Number(index + 1) + '</td><td class="overflow-table">' + (element.name) + '</td><td>' + (Number(element.value).toLocaleString()) + '</td><td>' + ((element.value / total) * 100).toFixed(2) + ' %' + '</td></tr>'
                $('#supplierVolumeDataTable_' + tab).append(html);
            }

            $('#topSuppliersByVolume_' + tab).html('&nbsp;&nbsp;From ' + (data[0] ? data[0].startDate : 'January ' + $('#year').val()) + ' to ' + (data[0] ? data[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));

            if (type == 'pie') {
                $('#supplierVolumePieIconId_' + tab).addClass('icon-active');
                $('#supplierVolumeBarIconId_' + tab).removeClass('icon-active');
            } else {
                $('#supplierVolumeBarIconId_' + tab).addClass('icon-active');
                $('#supplierVolumePieIconId_' + tab).removeClass('icon-active');
            }
            supplierBarChart_.hideLoading();
            supplierBarChart_.setOption(option, true);
        },
        error: function (error) {
            console.log(error);
            supplierBarChart_.hideLoading();
        }
    });
}

function getTopSupplierDataForCurrentYear(header, token, type, tab) {
    var supplierBarChart_ = echarts.init(document.getElementById('supplierValuebarChart_' + tab));
    supplierBarChart_.showLoading();
    supplierBarChart_.clear();
    var url = '';
    tab == 'overall' ? url = getContextPath() + '/buyer/getTopSuppliersByData/' + $('#month').val() + '/' + $('#year').val() :
        tab == 'subsidiary' ? url = getContextPath() + '/buyer/getTopSuppliersByData/' + $('#month').val() + '/' + $('#year').val() :
            tab == 'nonsubsidiary' ? url = getContextPath() + '/buyer/getTopSuppliersByData/' + $('#month').val() + '/' + $('#year').val() : null;

    $.ajax({
        url: url,
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            var totalOfData = 0;
            for (var index = 0; index < genData(data).seriesData.length; index++) {
                var element = genData(data).seriesData[index];
                totalOfData = totalOfData + element.value;
            }

            if (type == 'pie') {
                var response = genData(data);
                var option = {
                    color: ['#308dcc', '#A389D4', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Supplier : ' + params.name + ' <br/> Value : RM ' + Number(params.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                        }
                    },
                    xAxis: {
                        type: 'category',
                        show: false,
                    },
                    yAxis: {
                        type: 'value',
                        show: false
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        right: 10,
                        top: 20,
                        bottom: 20,
                        data: response.legendData,
                        selected: response.selected
                    },
                    series: [
                        {
                            type: 'pie',
                            radius: '55%',
                            center: ['40%', '50%'],
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
                var option = {
                    color: ['#308dcc', '#000', '#f4c22b', '#e67e22', '#a0ea5e', '#1dc4e9', '#1de9e0b8', '#e91d1db8', '#e91dbdb8', '#1d2de9'],
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return 'Supplier : ' + params.name + ' <br/> Value : RM ' + Number(params.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' (' + Number((params.value / totalOfData) * 100).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + ' %)'
                        }
                    },
                    xAxis: {
                        type: 'category',
                        show: true,
                        boundaryGap: false,
                        data: [],
                        name: 'Suppliers',
                        nameLocation: 'middle',
                        nameTextStyle: { padding: [15, 0, 5, 0] }
                    },
                    yAxis: {
                        type: 'value',
                        show: true,
                        name: 'PO Value ( in RM )',
                        nameLocation: 'middle',
                        nameRotate: 90,
                        nameTextStyle: { padding: [0, 0, 40, 0] }
                    },
                    series: [{
                        name: 'PO volume',
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

                for (var index = 0; index < data.length; index++) {
                    option.xAxis.data.push(data[index].name);
                    option.series[0].data.push(data[index].value);
                }

                type == 'bar' ? option.xAxis.boundaryGap = true : option.xAxis.boundaryGap = false;
            }

            var total = 0
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                total = total + element.value;
            }


            $('#supplierValueDataTable_' + tab).html('');
            for (var index = 0; index < data.length; index++) {
                var element = data[index];
                var html = '';
                html = '<tr><td>' + Number(index + 1) + '</td><td class="overflow-table">' + element.name + '</td><td>' + ('RM ' + Number(element.value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })) + '</td><td>' + ((element.value / total) * 100).toFixed(2) + ' %' + '</td></tr>'
                $('#supplierValueDataTable_' + tab).append(html);
            }

            supplierBarChart_.hideLoading();
            supplierBarChart_.setOption(option, true);
            $('#topSuppliersByValue_' + tab).html('&nbsp;&nbsp;From ' + (data[0] ? data[0].startDate : 'January ' + $('#year').val()) + ' to ' + (data[0] ? data[0].endDate : ($('#month option:selected').html() + ' ' + $('#year').val())));
            if (type == 'pie') {
                $('#supplierValuePieIconId_' + tab).addClass('icon-active');
                $('#supplierValueBarIconId_' + tab).removeClass('icon-active');
            } else {
                $('#supplierValueBarIconId_' + tab).addClass('icon-active');
                $('#supplierValuePieIconId_' + tab).removeClass('icon-active');
            }
        },
        error: function (error) {
            console.log(error);
            supplierBarChart_.hideLoading();
        }
    });
}

function genData(response) {
    var nameList = [];
    for (var index = 0; index < response.length; index++) {
        nameList.push(response[index].name);
    }
    var legendData = [];
    var seriesData = [];
    var selected = {};
    for (var index = 0; index < response.length; index++) {
        legendData.push(response[index].name);
        seriesData.push({ name: response[index].name, value: response[index].value });
        selected[response[index].name] = index < 6;
    }
    return {
        legendData: legendData,
        seriesData: seriesData,
        selected: selected
    };

}
