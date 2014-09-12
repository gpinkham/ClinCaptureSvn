$(window).load(function() {
	initStudyProgress();
});

/**
 * Initialize Study Progress widget.
 * 
 * @returns studyProgressChart.jsp (append it to .study_progress_container)
 */
function initStudyProgress() {
	var url = getCurrentUrl();
	$.ajax({
		type : "POST",
		url : url + "initStudyProgress",
		data : {},
		success : function(html) {
			$(".study_progress #study_progress_container").html(html);
			var totalCount = 0;
			$("form[id=study_progress] input").each(function(index) {
				totalCount += parseInt($(this).val());
			});
			var element = document.getElementById('toolbar');	
			if (totalCount != 0) {
				var spData = getStudyProgressWidgetData();
				var options = new getPieOptions();
				options.colors = [ '#12d2ff', '#ffc700',
						'#8ac819', '#029f32', '#9439c4', '#ff6301',
						'#ff0000', '#868686' ];
				var subjectStatusChart = new google.visualization.PieChart(
						document.getElementById('study_progress_chart'));
				subjectStatusChart.draw(spData, options);
				if (!element) {
					var startEndDates = getEventsFilterDates();
					var startDate = startEndDates[0];
					var endDate = startEndDates[1];
					function selectHandler() {
						var selectedItem = subjectStatusChart
								.getSelection()[0];
						if (selectedItem) {
							window.location.href = "ViewStudyEvents?startDate="
									+ startDate
									+ "&endDate="
									+ endDate
									+ "&statusId="
									+ spData.getValue(selectedItem.row,
											2).toString()
									+ "&refreshPage=1";
						}
					}
					google.visualization.events
							.addListener(subjectStatusChart, 'select',
							selectHandler);
				}
			} else 
				$("#study_progress_container").attr("height", "50px");
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
* Get values for Study Progress Widget from studyProgressFrom
*/
function getStudyProgressWidgetData() {
	var spData = new google.visualization.DataTable();
	var statusIds = [ 1, 3, 9, 8, 4, 6, 5, 7 ];
	var statusNames = [ 'Scheduled', 'Data Entry Started', 'SDV-ed', 'Signed',
			'Completed', 'Skipped', 'Stopped', 'Locked' ];
	spData.addColumn('string', 'Statuses');
	spData.addColumn('number', 'Count');
	spData.addColumn('number', 'StatusId');
	$("form[id=study_progress] input").each(
			function(index) {
				if (statusIds[index] != undefined) {
					spData.addRow([ " - " + statusNames[index],
							parseInt($(this).val()), statusIds[index] ]);
				}
			});
	return spData;
}
