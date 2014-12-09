$(window).load(function() {
	initNdsPerCrf("init");
});

/**
 * Initialize NDs per CRF widget This function will be used for three types of
 * actions:
 * 1. Init - will be run when user opens home page 
 * 2. Next - will upload data for next crfs (if exists) 
 * 3. Back - will upload data for previous crfs (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden
 * 
 * @param action - type if action that which will be executed 
 * @returns ndsPerCrfChart.jsp 
 */
function initNdsPerCrf(action){
	var start = $("#nds_per_crf_start").val();
	var url = getCurrentUrl();
	start = action == "init" ? 0 : start;
	var data = {
		action : action,
		start : start
	};
	$.ajax({
		type : "POST",
		url : url + "initNdsPerCrfWidget",
		data : data,
		success : function(html) {
			$(".nds_per_crf .nds_per_crf_container").html(html);
			setButtonsColor(".nds_per_crf");
			var data = getNdsPerCrfWidgetData();
			var options = new getVerticalBarOptions();
			options.bar.groupWidth = "45%";
			options.colors = ['#32A656','#E5A200','#FF0000','#9AB6D4'];
			options.height = 200;
			options.chartArea.height = "60%";
			var ndsPerCrfChart = new google.visualization.ColumnChart(document.getElementById('nds_per_crf_chart'));
			ndsPerCrfChart.draw(data, options);
			var element = document.getElementById('toolbar');
			if (!element) {
				function selectHandler() {
					var statuses = [];
					$("#nds_per_crf_form .status").each(function(){
						statuses.push(($(this).val()).replace(/\s/g, '+'));
					});
					var selectedItem = ndsPerCrfChart.getSelection()[0];
					var crfName = $("#nds_per_crf_form input[type=text]:nth-child(" + (selectedItem.row+1) + ")").val().replace(/\s/g, '+');
					var statusNumber = (selectedItem.column % 2 == 0 ? selectedItem.column - 2 : selectedItem.column - 1) / 2;
					var ndStatus = statuses[statusNumber];
					var prefix = "ViewNotes?module=submit&maxRows=15&showMoreLink=true&listNotes_tr_=true&listNotes_p_=1&listNotes_mr_=15";
					var sufix = "&listNotes_f_discrepancyNoteBean.resolutionStatus=";
					var redirect = "";
					if ( selectedItem.row == 0) 
						redirect = prefix + "&listNotes_f_crfName=" + crfName + sufix + ndStatus;
					else if (!selectedItem.row) 
						redirect = prefix + sufix + ndStatus;
					else 
						redirect = prefix + "&listNotes_f_crfName=" + crfName + sufix + ndStatus;

					window.location.href = redirect;
				}
				google.visualization.events
						.addListener(ndsPerCrfChart, 'select',
						selectHandler);
			} else 
				$(".widget input[type=button], .widget_big input[type=button]").remove();
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
* Get values for NDs per CRF Widget from nds_per_crf_form.
*/
function getNdsPerCrfWidgetData() {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'CRF Name');
	// Closed, Updated, New, Not Applicable
	$("#nds_per_crf_form .legend").each(function(){
		data.addColumn('number', $(this).val());
		data.addColumn({
			type : 'number',
			role : 'annotation'
		});
});
	var counter = 0;
	$("#nds_per_crf_form input[type=text]").each(
			function() {
				counter++;
				if (counter < 8) {
					var crfName = $(this).val().replace(/-|\//g,' ');
					var newNds = parseInt($(this).attr("new"));
					var updatedNds = parseInt($(this).attr("updated"));
					var closedNds = parseInt($(this).attr("closed"));
					var notApplicableNds = parseInt($(this).attr("not_applicable"));
					var displayedName = "";
					var element = document.getElementById('toolbar');
					if (!element) {
						var words = crfName.split(" ");
						$.each(words, function(index) {
							var word = this;
							if (this.length > 14) 
								word = this.substring(0,7) + "...";

							displayedName += word + " ";
						});
					} else 
						displayedName = crfName.substring(0,7) + "...";

					data.addRow([displayedName, newNds, newNds, updatedNds,updatedNds, closedNds,closedNds,
							notApplicableNds, notApplicableNds ]);
				}
			});
	return data;
}
