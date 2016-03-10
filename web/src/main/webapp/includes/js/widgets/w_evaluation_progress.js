$(window).load(function() {
	initEvaluationProgress("init");
});

/**
 * Initialize Evaluation Progress widget. This function will be used for three types of
 * actions:
 * 1. Init - will be run when user opens home page
 * 2. Next - will upload data for next year (if exists)
 * 3. Back - will upload data for previous year (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden.
 *
 * @returns evaluationProgressChart.jsp (append it to .coding_progress_container).
 */
function initEvaluationProgress(action) {
	var url = getCurrentUrl();
	var displayedYear = parseInt($("#evaluationProgressYear").val());
	if (action === "init")
		displayedYear = 0;
	else if (action === "next")
		displayedYear += 1;
	else if (action === "back")
		displayedYear -= 1;

	$.ajax({
		type : "POST",
		url : url + "initEvaluationProgressWidget",
		data : {
			evaluationProgressYear : displayedYear
		},
		success : function(html) {
			$(".evaluation_progress .evaluation_progress_container").html(html);
			setButtonsColor(".evaluation_progress");
			var data = getEvaluationProgressWidgetData();
			displayedYear = $("#evaluationProgressYear").val();
			var options = new getVerticalBarOptions();
			options.hAxis.title = displayedYear;
			var evaluationProgressChart = new google.visualization.ColumnChart(document.getElementById('evaluation_progress_chart'));
			evaluationProgressChart.draw(data, options);
			var element = document.getElementById('toolbar');
			var activate = $("#evaluationProgressActivateLegend").val();
			if (!element && activate == "true") {
				function selectHandler() {
					var selectedItem = evaluationProgressChart.getSelection()[0];
					var currentYear = new Date().getFullYear();
					var currentMonth = new Date().getMonth();
					if (selectedItem && selectedItem.row == currentMonth && currentYear == displayedYear) {
						var arr = getEvaluationStatuses();
						var statusNumber = (selectedItem.column % 2 == 0 ? selectedItem.column - 2 : selectedItem.column - 1) / 2;
						var currentStatus = arr[statusNumber].replace(/ /g, "+");
						var redirectPrefix= "pages/crfEvaluation?maxRows=15&showMoreLink=false&crfEvaluationTable_tr_=true&crfEvaluationTable_p_=1&crfEvaluationTable_mr_=15&crfEvaluationTable_f_evaluation_status=";
						window.location.href = redirectPrefix + currentStatus ;
					} else
						evaluationProgressChart.setSelection([]);
				}
				google.visualization.events.addListener(evaluationProgressChart, 'select',
					selectHandler);
			} else if (!element && isSponsor()) {
				function onClickHandler() {
					showNoAccessDialogSponsor();
					evaluationProgressChart.setSelection([]);
				}

				google.visualization.events.addListener(evaluationProgressChart, 'select', onClickHandler);
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Get values for Evaluation Progress Widget from enrollment_progress form
 */
function getEvaluationProgressWidgetData() {
	var regexp = /^stat\-(.+)$/;
	var i;
	// Set the order of statuses in chart
	var arr = getEvaluationStatuses();
	var data = new google.visualization.DataTable();
	var arrLength = arr.length;
	data.addColumn('string', 'Month');
	for (i = 0; i < arrLength; i++) {
		data.addColumn('number', arr[i]);
		data.addColumn({
			type : 'number',
			role : 'annotation'
		});
	}
	$("form#evaluation_progress_form input[type=hidden]").each(function() {
		var currentValues = getNodeAttributes($(this), regexp);
		var currentMonth = $(this).val();
		var row = [];
		row.push(currentMonth);
		for (i = 0; i < arrLength; i++) {
			row.push(parseInt(currentValues[arr[i].replace(/ /g, "_").toLowerCase()]));
			row.push(parseInt(currentValues[arr[i].replace(/ /g, "_").toLowerCase()]));
		}
		data.addRow(row);
	});
	return data;
}

/**
 * Gets an array of evaluation statuses
 */
function getEvaluationStatuses() {
	var evalStatusList = $("#evaluationStatusList").val();
	return evalStatusList.split(";");
}