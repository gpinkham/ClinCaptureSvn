$(window).load(function() {
	initEnrollmentProgress("init");
});

/**
 * Initialize Enrollment Progress widget. This function will be used for three types of
 * actions:
 * 1. Init - will be run when user opens home page
 * 2. Next - will upload data for next year (if exists)
 * 3. Back - will upload data for previous year (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden.
 *
 * @returns enrollmentProgressChart.jsp (append it to .enrollment_progress_container).
 */
function initEnrollmentProgress(action) {
	var url = getCurrentUrl();
	var displayedYear = parseInt($("#epYear").val());
	if (action === "init") 
		displayedYear = 0;
	else if (action === "next")
		displayedYear += 1;
	else if (action === "back")
		displayedYear -= 1;

	$.ajax({
		type : "POST",
		url : url + "initEnrollmentProgressWidget",
		data : {
			currentYear : displayedYear
		},
		success : function(html) {
			$(".enrollment_progress .enrollment_progress_container").html(html);
			setButtonsColor(".enrollment_progress");
			var data = getEnrollmentProgressWidgetData();
			displayedYear = $(".enrollment_progress #epYear").val();
			var options = new getVerticalBarOptions();
			options.colors = ['#868686', '#32a656', '#ff0000', '#7fd0ff'];
			options.hAxis.title = displayedYear;
			var enrollmentProgressChart = new google.visualization.ColumnChart(document.getElementById('enrollment_progress_chart'));
			enrollmentProgressChart.draw(data, options);
			var element = document.getElementById('toolbar');
			if (!element) {
				function selectHandler() {
					var selectedItem = enrollmentProgressChart.getSelection()[0];
					var currentYear = new Date().getFullYear();
					var currentMonth = new Date().getMonth();
					if (selectedItem && selectedItem.row == currentMonth && currentYear == displayedYear) {
						var arr = [];
						// locked, signed, removed, available.
						$("#enrollment_progress .status").each(function(){
							arr.push($(this).val());
						});
						var statusNumber = (selectedItem.column % 2 == 0 ? selectedItem.column - 2 : selectedItem.column - 1) / 2;
						var currentStatus = arr[statusNumber];
						var redirectPrefix = "ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_f_studySubject.status=";
						window.location.href = redirectPrefix + currentStatus;
					} else 
						enrollmentProgressChart.setSelection([]);
				}
				google.visualization.events.addListener(enrollmentProgressChart, 'select',
						selectHandler);
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
 * Get values for Enrollment Progress Widget from enrollment_progress form
 */
function getEnrollmentProgressWidgetData() {
	var regexp = /^stat\-(.+)$/;
	var i;
	// Set the order of statuses in chart
	var arr = [];
	// locked, signed, removed, available.
	$("#enrollment_progress .status").each(function(){
		arr.push($(this).val());
	});
	var data = new google.visualization.DataTable();
	var arrLength = arr.length;
	data.addColumn('string', 'Month');
	for (i = 0; i < arrLength; i++) {
		data.addColumn('number', capitalizeFirstLetter(arr[i]));
		data.addColumn({
			type : 'number',
			role : 'annotation'
		});
	}
	$("form#enrollment_progress input[type=hidden]").each(function() {
		var currentValues = getNodeAttributes($(this), regexp);
		var currentMonth = $(this).val();
		var row = [];
		row.push(currentMonth);
		for (i = 0; i < arrLength; i++) {
			row.push(parseInt(currentValues[arr[i]]));
			row.push(parseInt(currentValues[arr[i]]));
		}
		data.addRow(row);
	});
	return data;
}
