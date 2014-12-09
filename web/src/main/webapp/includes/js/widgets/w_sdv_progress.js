$(window).load(function() {
	initSdvProgress("init"); 
});

/**
 * Initialize SDV Progress widget This function will be used for three types of
 * actions:
 * 1. Init - will be run when user opens home page 
 * 2. Next - will upload data for next year (if exists) 
 * 3. Back - will upload data for previous year (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden
 * 
 * @param action - type if action that which will be executed 
 * @returns sdvProgressChart.jsp (append it to .sdv_progress_container)
 */
function initSdvProgress(action) {
	var url = getCurrentUrl();
	var displayedYear = parseInt($(".sdv_progress #sdvProgressYear").val());
	if (action === "init") 
		displayedYear = 0;
	else if (action === "next") 
		displayedYear += 1;
	else if (action === "back") 
		displayedYear -= 1;

	var data = {
		action: action,
		sdvProgressYear: displayedYear
	};
	$.ajax({
		type : "POST",
		url : url + "initSdvProgressWidget",
		data : data,
		success : function(html) {
			$(".sdv_progress .sdv_progress_container").html(html);
			displayedYear = $(".sdv_progress #sdvProgressYear").val();
			var data = getSdvWidgetData();
			var options = new getVerticalBarOptions();
			options.hAxis.title = displayedYear;
			var sdvProgressChart = new google.visualization.ColumnChart(document.getElementById('sdv_progress_chart'));
			sdvProgressChart.draw(data, options);
			setButtonsColor(".sdv_progress");
			var element = document.getElementById('toolbar');
			if (!element) {
				function selectHandler() {
					var statuses = [];
					statuses.push(($("#sdv_progress .status_available_for_sdv_jmesa_filter").val()).replace(/\s/g, '+'));
					statuses.push(($("#sdv_progress .status_sdved_jmesa_filter").val()).replace(/\s/g, '+'));
					var selectedItem = sdvProgressChart.getSelection()[0];
					var currentYear = new Date().getFullYear();
					var currentMonth = new Date().getMonth();
					if (selectedItem && selectedItem.row == currentMonth && currentYear == displayedYear) {
						var sdvStep = (selectedItem.column == "3" || selectedItem.column == "4") ? statuses[0] : statuses[1];
						var redirectPrefix = "pages/viewAllSubjectSDVtmp?studyId=";
						var redirectSufix = "&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15";
						var studyId = $("input[id=sdvWStudyId]").val();
						window.location.href = redirectPrefix + studyId + redirectSufix + "&sdv_f_sdvStatus=" + sdvStep;
					} else 
						sdvProgressChart.setSelection([]);
				}
				google.visualization.events
						.addListener(sdvProgressChart, 'select',
						selectHandler);
			} else
				$(".widget input[type=button], .widget_big input[type=button]").remove();
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
* Get values for SDV Progress Widget from sdvMonthForm
*/
function getSdvWidgetData() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Month');
	data.addColumn('number', $("#sdv_progress .sdved").val());
	data.addColumn({
		type : 'number',
		role : 'annotation'
	});
	data.addColumn('number', $("#sdv_progress .available_for_sdv").val());
	data.addColumn({
		type : 'number',
		role : 'annotation'
	});
	$("#sdvMonthForm input").each(
			function() {
				var currentValue = parseInt($(this).val());
				var currentMonth = $(this).attr("month");
				var available = parseInt($(this).attr("available"));
				data.addRow([ currentMonth, currentValue, currentValue,
						available, available ]);
			});
	return data;
}
