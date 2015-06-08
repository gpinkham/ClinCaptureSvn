$(window).load(function() {
	initEventsCompletionWidget("init");
});

/**
 * Initialize Events Completion widget.
 * 
 * @returns eventsCompletionChart.jsp (append it to #events_completion_container)
 */
function initEventsCompletionWidget(action) {
	var url = getCurrentUrl();
	var lastElement = $(".events_completion input#ec_last_element").val();
	$(".events_completion .pop-up-visible").css('display', 'none');
	$(".events_completion .pop-up").css('display', 'none');
	if (lastElement == undefined) 
		lastElement = 0;

	$(".events_completion #events_completion_container").hide(500);
	$.ajax({
		type : "POST",
		url : url + "initEventsCompletionWidget",
		data : {
			lastElement : lastElement,
			studyId : $("#studyId").val(),
			action : action
		},
		success : function(html) {
			$("#events_completion_container").html(html);
			var hasNext = $("#ec_has_next").val();
			var hasPrevious = $("#ec_has_previous").val();
			if (hasNext == "true") 
				$(".events_completion input#next").css("display", "block");
			else
				$(".events_completion input#next").css("display", "none");

			if (hasPrevious == "true")
				$(".events_completion input#previous").css("display", "block");
			else
				$(".events_completion input#previous").css("display", "none");

			$("#events_completion_container").show(500, function () {
				$(".events_completion .pop-up").css('display', '');
			});
			var stack = $("#events_completion_container .stacked_bar");
			stack.each(function(entry) {
				var values = new Array();
				var total = 0;
				var stack = $(this).find("li");
				stack.each(function(index) {
					var currentValue = $(this).find(".hidden").html();
					if (currentValue < 0) {
						currentValue = 0;
					}
					if (currentValue) {
						values[index] = parseInt(currentValue);
						total += parseInt(currentValue);
					}
				});
				if (parseInt(total) != 0) {
					var currentBarClass = $(this).attr("barnumber");
					var selector = "#events_completion_container .stacked_bar[barnumber="
							+ (currentBarClass) + "] .stack";
					setStacksLengths(selector, values, total);
				}
			});
			var element = document.getElementById('toolbar');
			if (!element) {
				activateEventCompletionLegend();
				getEventsCompletionLegendValues();
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Function for activation legend for "Events Completion Widget"
 */
function getEventsCompletionLegendValues() {
	var element = document.getElementById('toolbar');
	var statusNames = [];
	$("#events_completion_form .pop_up_status").each(function(){
		statusNames.push($(this).val());
	});
	if (!element) {
		var url = getCurrentUrl();
		$.ajax({
			type : "POST",
			url : url + "getEventsCompletionLegendValues",
			data : {
				studyId : $("#studyId").val()
			},
			success : function(html) {
				var resArray =  html.replace(/\s+|[\[\]]+/g,'').split(',');
				setDataToLegend("both", ".events_completion", resArray, statusNames);
				$(".events_completion .signs td").mouseover(function() {
					$(this).find(".popup_legend_medium").css({display : "block","margin-top":"-39px"});
				});
				$(".events_completion .signs td").mouseout(function() {
					$(this).find(".popup_legend_medium").css({display : "none"});
				});
			},
			error : function(e) {
				console.log("Error:" + e);
			}
		});
	}
}

/**
 * Function for adding onClick action for "Events Completion" widget's legend
 * and bars.
 */
function activateEventCompletionLegend() {
	var startEndDates = getEventsFilterDates();
	var startDate = startEndDates[0];
	var endDate = startEndDates[1];
	var urlPrefix = "ViewStudyEvents?startDate=";
	var statuses = [ 1, 3, 4, 8, 7, 6, 5, 9, 0 ];
	$(".events_completion .pop-up-visible").css('display', '');
	$(".events_completion .signs td").each(function(index) {
		$(this).click(function() {
			window.location.href = urlPrefix + startDate
					+ "&endDate=" + endDate + "&statusId="
					+ statuses[index] + "&refreshPage=1";
		});
		$(this).css("cursor", "pointer");
	});

	var statNames = [];
	$("#events_completion_form .status").each(function(){
		statNames.push($(this).val());
	});
	$(".events_completion .stacked_bar a").each(function() {
		var urlS = "ListEventsForSubjects?defId=";
		var eventId = $(this).attr("def-id");
		var urlM = "&listEventsForSubject_p_=1&listEventsForSubject_mr_=15&listEventsForSubject_f_event.status=";
		var status = statNames[parseInt($(this).attr("status-id"))].toLowerCase().replace(/\s/g,'+');
		var url = urlS + eventId + urlM + status;
		$(this).attr("href", url);
	});
}
