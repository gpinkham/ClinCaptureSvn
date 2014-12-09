$(window).load(function() {
	initSubjectStatusCount();
});

/**
 * Initialize Subject Status Count widget.
 * 
 * @returns subjectStatusCountChart.jsp (append it to #subject_status_count_container)
 */
function initSubjectStatusCount() {
	var url = getCurrentUrl();
	$.ajax({
		type : "POST",
		url : url + "initSubjectStatusCount",
		data : {},
		success : function(html) {
			$("#subject_status_count_container").html(html);
			var totalSubjectsCount = 0;
			$("form#subjects_status_count input").each(function() {
				totalSubjectsCount += parseInt($(this).val());
			});
			if (totalSubjectsCount != 0 ) {
				var data = getSubjectStatusWidgetData();
				var options = new getPieOptions();
				options.colors = [ '#7fd0ff', '#32a656','#ff0000', '#868686' ];
				var subjectStatusChart = new google.visualization.PieChart(document.getElementById('subject_status_count_chart'));
				subjectStatusChart.draw(data, options);
				var element = document.getElementById('toolbar');
				if (!element) {
					function selectHandler() {
						var selectedItem = subjectStatusChart.getSelection()[0];
							if (selectedItem) {
							var statusName = trim(data.getValue(selectedItem.row, 0)
									.toString().split('-')[1]).toLowerCase();
							window.location.href = "ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_f_studySubject.status="
									+ statusName;
						}
					}
					google.visualization.events.addListener(subjectStatusChart, 'select', selectHandler);
				}
			} else {
				$("#subject_status_count_container").attr("height","50px");
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Get values for Subject Status Count Widget from .subjects_status_count form.
 * 
 * @returns google DataTable.
 */
function getSubjectStatusWidgetData() {
	var data = new google.visualization.DataTable();
	var statuses = [];
	$("#subjects_status_count .status").each(function(){
		statuses.push($(this).val());
	});
	data.addColumn('string', 'Statuses');
	data.addColumn('number', 'Count');
	$("form#subjects_status_count input").each(function(index) {
		data.addRow([ " - " + statuses[index], parseInt($(this).val()) ]);
	});
	return data;
}
