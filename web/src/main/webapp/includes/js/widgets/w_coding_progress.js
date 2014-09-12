$(window).load(function() {
	initCodingProgress("init");
});

/**
 * Initialize Coding Progress widget. This function will be used for three types of
 * actions:
 * 1. Init - will be run when user opens home page
 * 2. Next - will upload data for next year (if exists)
 * 3. Back - will upload data for previous year (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden.
 *
 * @returns codingProgressChart.jsp (append it to .coding_progress_container).
 */
function initCodingProgress(action) {
	var url = getCurrentUrl();
	var displayedYear = parseInt($("#cpYear").val());
	if (action === "init") 
		displayedYear = 0;
	else if (action === "next") 
		displayedYear += 1;
	else if (action === "back") 
		displayedYear -= 1;

	$.ajax({
		type : "POST",
		url : url + "initCodingProgressWidget",
		data : {
			codingProgressYear : displayedYear
		},
		success : function(html) {
			$(".coding_progress .coding_progress_container").html(html);
			setButtonsColor(".coding_progress");
			var data = getCodingProgressWidgetData();
			displayedYear = $(".coding_progress #cpYear").val();
			var options = new getVerticalBarOptions();
			options.hAxis.title = displayedYear;
			var codingProgressChart = new google.visualization.ColumnChart(document.getElementById('coding_progress_chart'));
			codingProgressChart.draw(data, options);
			var element = document.getElementById('toolbar');
			var activate = $("#cpActivateLegend").val();
			if (!element && activate == "true") {
				function selectHandler() {
					var selectedItem = codingProgressChart.getSelection()[0];
					var currentYear = new Date().getFullYear();
					var currentMonth = new Date().getMonth();
					if (selectedItem && selectedItem.row == currentMonth && currentYear == displayedYear) {
						var arr = ["Coded","Not+Coded"];
						var statusNumber = (selectedItem.column % 2 == 0 ? selectedItem.column - 2 : selectedItem.column - 1) / 2;
						var currentStatus = arr[statusNumber];
						var redirectPrefix = "pages/codedItems?maxRows=15&showContext=false&showMoreLink=false&codedItemsId_tr_=true&codedItemsId_p_=1&codedItemsId_mr_=15&codedItemsId_f_itemDataValue=&codedItemsId_f_status=";
						window.location.href = redirectPrefix + currentStatus;
					} else 
						codingProgressChart.setSelection([]);
				}
				google.visualization.events.addListener(codingProgressChart, 'select',
						selectHandler);
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Get values for Coding Progress Widget from enrollment_progress form
 */
function getCodingProgressWidgetData() {
	var regexp = /^stat\-(.+)$/;
	var i;
	// Set the order of statuses in chart
	var arr = ["coded_items", "items_to_be_coded"];
	var data = new google.visualization.DataTable();
	var arrLength = arr.length;
	data.addColumn('string', 'Month');
	for (i = 0; i < arrLength; i++) {
		data.addColumn('number', capitalizeFirstLetter(arr[i]).replace(/_/g," "));
		data.addColumn({
			type : 'number',
			role : 'annotation'
		});
	}
	$("form#coding_progress_form input[type=hidden]").each(function() {
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
