/* Customize page */
var scrollTrigger = true;

function toolbarToogle(e) {
	if ($(e).children().text() == "Hide") {
		$(e).parent().animate({
			right : "-430px"
		}, 500);
		$(e).children().html("Show");
	} else {
		$(e).parent().animate({
			right : "0px"
		}, 500);
		$(e).children().html("Hide");
	}
}

if (!Array.prototype.forEach) {
	Array.prototype.forEach = function(fn, scope) {
		for ( var i = 0, len = this.length; i < len; ++i) {
			fn.call(scope, this[i], i, this);
		}
	};
}

if (typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	};
}

function launchCustomizePage() {

	setScrollHendlers(scrollTrigger);
	resizeTcInToolbar();
	inicializeDnD();
	$("#toolbar, #layout1, #layout2,#layout_tc").disableSelection();
	$(".widget .chart_wrapper a").attr("href", "#");
	$(".widget input[type=button], .widget_big input[type=button]").remove();
	updatePostOrder();
}

function inicializeDnD() {

	$(".droptrue").sortable({
		connectWith : '.droptrue',
		opacity : 0.6,
		items : ".widget",
		revert : true,
		update : updatePostOrder,
		start : function(e, ui) {
			highlight("oc");
			scrollTrigger = false;
		},
		stop : function(e, ui) {
			disableHighlight();
			scrollTrigger = true;
		}
	});

	$(".droptrue_tc").sortable({
		connectWith : '.droptrue_tc',
		opacity : 0.6,
		update : updatePostOrder,
		helper : 'clone',
		items : '.widget_big',
		cursorAt : {
			left : 80,
			top : 20
		},
		revert : true,
		start : function(e, ui) {
		
			ui.helper.animate({
				width : 380,
				height : "auto"
			}, 300);

			highlight("tc");
			$(this).find(".tc_content").css("display", "none");
			$(this).find(".description").css("display", "block");
			scrollTrigger = false;
		},
		receive : function(e, ui) {

			if ($(this).attr("id") == "layout_tc") {
				ui.item.animate({
					width : 778
				}, 300);
				$(this).find(".tc_content").css("display", "block");
				$(this).find(".description").css("display", "none");
			} else {
				ui.item.css("width", "380");
				$(this).find(".tc_content").css("display", "none");
				$(this).find(".description").css("display", "block");
			};
		},
		stop : function(e, ui) {

			disableHighlight();
			scrollTrigger = true;
			if ($(this).attr("id") == "layout_tc") {
				$(this).find(".tc_content").css("display", "block");
				$(this).find(".description").css("display", "none");
			}
		},
		placeholer : 'ui-placeholder-big'
	});
}

/*
* This function adds auto scrolling for the toolbar area in the Customize page.
* When user set mouse over two columns widget - toolbar area will be scrolled
* to TC widgets drop zone.
*/
function setScrollHendlers(scrollTrigger) {
	$(".widget_big").mouseenter(function() {
		if (scrollTrigger) {
			if ($(this).parent().attr("id") == "layout_tc") {
				scrollTrigger = false;
				$('#scroll-container').animate({
					scrollTop : $('#toolbar_tc').offset().top
				}, {
					duration : 'slow',
					complete : function() {
						scrollTrigger = true;
					},
					fail : function() {
						scrollTrigger = true;
					}
				});
			}
		}
	});

	$(".widget").mouseenter(
			function() {
				if (scrollTrigger) {
					if ($(this).parent().attr("id") == "layout1"
							|| $(this).parent().attr("id") == "layout2") {
						scrollTrigger = false;
						$('#scroll-container').animate({
							scrollTop : 0
						}, {
							duration : 'slow',
							complete : function() {
								scrollTrigger = true;
							},
							fail : function() {
								scrollTrigger = true;
							}
						});
					}
				}
			});
}

/*
* Change size of all Two-columns widgets in toolbar,
* hide content and show description
*/
function resizeTcInToolbar() {

	$("#toolbar_tc .widget_big").css("width", "380px");

	$("#toolbar_tc .widget_big").each(function() {
		$(this).find(".tc_content").css("display", "none");
		$(this).find(".description").css("display", "block");
	});
}

/*
* Highlight areas where widget can be dropped.
* This function will be activated on drag action.
*/
function highlight(target) {

	if (target === "oc") {
		$("#layout1, #layout2, #toolbar").each(function() {
			$(this).animate({
				backgroundColor : '#ededed'
			}, 300);
			if ($(this).height() < 40) {
				$(this).css("height", "60px");
			}
		});
	} else {
		$("#toolbar_tc, #layout_tc").each(function() {
			$(this).animate({
				backgroundColor : '#ededed'
			}, 300);
			$(this).css("height", "60px");
		});
	}
}

/*
* Disable highlighting of areas where widget can be dropped.
* This function will be activated when user drops widget.
*/
function disableHighlight() {
	$("#layout1, #layout2, #toolbar, #toolbar_tc, #layout_tc").each(function() {
		$(this).animate({
			backgroundColor : 'white'
		}, 300);
		$(this).css("height", "");
	});
}

function updatePostOrder() {

	var arr = [];
	$(".column1 div.widget").each(function() {
		arr.push($(this).attr('id'));
	});
	$('#postOrder1').val(arr.join(','));

	var arr2 = [];
	$(".column2 div.widget").each(function() {
		arr2.push($(this).attr('id'));
	});
	$('#postOrder2').val(arr2.join(','));

	var arr3 = [];
	$("#scroll-container div.widget, #scroll-container div.widget_big").each(
			function() {
				arr3.push($(this).attr('id'));
			});
	$('#unusedWidgets').val(arr3.join(','));

	var arr4 = [];
	$("#layout_tc div.widget_big").each(function() {
		arr4.push($(this).attr('id'));
	});
	$('#bigWidgets').val(arr4.join(','));
}

function removeWidget(elemet) {

	var itemToRemove = $(elemet).parent();

	if (itemToRemove.attr("class") == "widget") {
		$("#toolbar").prepend(itemToRemove);
	} else {
		$("#toolbar_tc").prepend(itemToRemove);
		itemToRemove.css("width", "380px");
		itemToRemove.find(".tc_content").css("display", "none");
		itemToRemove.find(".description").css("display", "block");
	}
	updatePostOrder();
}

function saveLayoutAndExit() {
	$.ajax({
		type : "POST",
		url : "saveHomePage",
		data : {
			orderInColumn1 : $("#postOrder1").val(),
			orderInColumn2 : $("#postOrder2").val(),
			unusedWidgets : $("#unusedWidgets").val(),
			bigWidgets : $("#bigWidgets").val(),
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function() {
			$("form[id=goToHomePage]").submit();
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}
/* /Customize page */


/* Initialization of widgets */

/*
 * Initialize NDs Assigned To Me widget.
 */
function initNdsAssignedToMeWidget() {
	var url = getCurentUrl();

	$.ajax({
		type : "POST",
		url : url + "initNdsAssignedToMeWidget",
		data : {
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function(html) {
			var array = html.split(',');
			var newNds = parseInt(array[0]);
			var updatedNds = parseInt(array[1]);
			var resolutionProposedDns = parseInt(array[2]);
			var closedNds = parseInt(array[3]);

			if (resolutionProposedDns != 0) {
				$(".dns_assigned_to_me td.optional").css("display",
						"table-cell");

				$(".dns_assigned_to_me a.optional").css("display",
						"inline-block");
			}

			var totalNds = newNds + updatedNds + closedNds
					+ resolutionProposedDns;
			captionLimit = countCaptionLimit(totalNds);

			var captionSelector = ".dns_assigned_to_me .captions td";
			setCaption(captionSelector, captionLimit);
			
			var valuesSecelctor = ".dns_assigned_to_me .stack";
			setStacksLenghts(valuesSecelctor, array, captionLimit);
			
			var element = document.getElementById('toolbar');

			if (!element) {
				setDataToLegend("percents",".dns_assigned_to_me",array);
				activateNDsWidgetLegend();
			} else {
				$(".dns_assigned_to_me .pop-up-visible").css('display', 'none');
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
 * Function for adding onClick action for "NDs assigned to me" widget's legend
 * and bars.
 */
function activateNDsWidgetLegend() {

	var urlPrefix = "ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=";
	var currentUser = $("form#ndsWidgetForm input#cUser").val();
	var urlSufix = "&listNotes_f_discrepancyNoteBean.resolutionStatus=";
	var Statuses = [ "New", "Updated", "Closed", "Resolution Proposed" ];

	$(".dns_assigned_to_me .pop-up-visible").css('display', 'table');

	$(".dns_assigned_to_me .stacked_bar a").each(
			function(index) {
				$(this).attr("href",
						urlPrefix + currentUser + urlSufix + Statuses[index]);
			});

	$(".dns_assigned_to_me .signs td").each(
			function(index) {
				$(this).click(
						function() {
							window.location.href = urlPrefix + currentUser
									+ urlSufix + Statuses[index];
						});

				$(this).css("cursor", "pointer");
			});

	$(".dns_assigned_to_me .signs td").mouseover(function() {
		$(this).find(".popup_legend_min").css({display : "block","margin-top":"-20px"});
	});

	$(".dns_assigned_to_me .signs td").mouseout(function() {
		$(this).find(".popup_legend_min").css({display : "none"});
	});
}

/*
 * Initialize Events Completion widget.
 * 
 * @returns eventsCompletionChart.jsp (append it to #events_completion_container)
 */
function initEventsCompletionWidget(action) {
	var url = getCurentUrl();
	var lastElement = $(".events_completion input#ec_last_element").val();

	$(".events_completion .pop-up-visible").css('display', 'none');
	$(".events_completion .pop-up").css('display', 'none');

	if (lastElement == undefined) {
		lastElement = 0;
	}

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

            $(".pop-up").css('display', 'none');

			if (hasNext == "true") {
				$(".events_completion input#next").css("display", "block");
			} else {
				$(".events_completion input#next").css("display", "none");
			}

			if (hasPrevious == "true") {
				$(".events_completion input#previous").css("display", "block");
			} else {
				$(".events_completion input#previous").css("display", "none");
			}
            $(".events_completion #events_completion_container").show(500, function () {
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
					setStacksLenghts(selector, values, total);
				}
			});

			var element = document.getElementById('toolbar');

			if (element) {
				$(".events_completion .chart_wrapper a").attr("href", "#");
			} else {
				activateEventCompletionLegend();
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
 * Function for activation legend for "Events Completion Widget"
 */
function getEventsCompletionLegendValues() {

	var element = document.getElementById('toolbar');
	var statusNames = [ 'Scheduled', 'Data Entry Started', 'Completed',
			'Signed', 'Locked', 'Skipped', 'Stopped', 'Source Data Verified', 'Not Scheduled'];

	if (!element) {
		var url = getCurentUrl();

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

/*
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

	$(".events_completion .signs td").each(
			function(index) {
				$(this).click(
						function() {
							window.location.href = urlPrefix + startDate
									+ "&endDate=" + endDate + "&statusId="
									+ statuses[index] + "&submitted=1";
						});

				$(this).css("cursor", "pointer");
			});
}

/*
 * Initialize Subject Status Count widget.
 * 
 * @returns subjectStatusCountChart.jsp (append it to #subject_status_count_container)
 */
function initSubjectStatusCount() {
	var url = getCurentUrl();

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
						var statusName = data.getValue(selectedItem.row, 0)
								.toString().split('-')[1].trim().toLowerCase();
						
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


/*
 * Get values for Subject Status Count Widget from .subjects_status_count form
 */
function getSubjectStatusWidgetData() {

	var data = new google.visualization.DataTable();
	var statuses = [ 'Available', 'Signed', 'Removed', 'Locked' ];

	data.addColumn('string', 'Statuses');
	data.addColumn('number', 'Count');

	$("form#subjects_status_count input").each(function(index) {
		data.addRow([ " - " + statuses[index], parseInt($(this).val()) ]);
	});

	return data;
}

/*
 * Initialize SDV Progress widget This function will be used for three tipes of
 * actions:
 * 1. Init - will be run when user opens home page 
 * 2. Next - will upload data for next year (if exists) 
 * 3. Back - will upload data for previous year (if exists)
 * 
 * Depending on returned data - "Next" or "Previous" buttons will be hidden
 * 
 * @param action - type if action that which will be executed 
 * @returns sdvProgressChart.jsp (append it to .sdv_progress_container)
 */
function initSdvProgress(action) {
	var url = getCurentUrl();
	var displayedYear = parseInt($(".sdv_progress #sdvProgressYear").val());
	
	if (action === "init") {
		displayedYear = 0;
	} else if (action === "next") {
		displayedYear += 1;
	} else if (action === "back") {
		displayedYear -= 1;
	}

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
			setSDVButtonsColor();

			var element = document.getElementById('toolbar');
			if (!element) {

				function selectHandler() {

					var selectedItem = sdvProgressChart
						.getSelection()[0];
					
					var currentYear = new Date().getFullYear();
					var currentMonth = new Date().getMonth();

					if (selectedItem && selectedItem.row == currentMonth && currentYear == displayedYear) {

						var sdvStep = (selectedItem.column == "3" || selectedItem.column == "4") ? "not+done" : "complete";
						var redirectPrefix = "pages/viewAllSubjectSDVtmp?studyId=";
						var redirectSufix = "&showMoreLink=true&sdv_tr_=true&sdv_p_=1&sdv_mr_=15";
						var studyId = $("input[id=sdvWStudyId]").val();
						
						window.location.href = redirectPrefix + studyId + redirectSufix + "&sdv_f_sdvStatus=" + sdvStep;
					} else {
						sdvProgressChart.setSelection([]);
					}
				}

				google.visualization.events
						.addListener(sdvProgressChart, 'select',
						selectHandler);
			} else {
				$(".widget input[type=button], .widget_big input[type=button]").remove();
			}
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
	data.addColumn('number', 'SDV-ed event CRFs');
	data.addColumn({
		type : 'number',
		role : 'annotation'
	});
	data.addColumn('number', 'Available for SDV');
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

/*
 * Initialize Study Progress widget.
 * 
 * @returns studyProgressChart.jsp (append it to .study_progress_container)
 */
function initStudyProgress() {
	var url = getCurentUrl();

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

				var spData = getStudyProgresWidgetData();
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
									+ "&submitted=1";
						}
					}

					google.visualization.events
							.addListener(subjectStatusChart, 'select',
							selectHandler);
				}
			} else {
				$("#study_progress_container").attr("height", "50px");
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
* Get values for SDV Progress Widget from sdvMonthForm
*/
function getStudyProgresWidgetData() {
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
/* /Initialization of widgets */

/* Supporting functions */


/* 
* Calculate limit of caption for table
* 
* @param total - the maximum value that will be displayed in the chart
*/

/* Supporting functions */
function countCaptionLimit(total) {
	if (total < 4) {
		total = 4;
	} else if (total % 2 == 0) {
		(total / 2) % 2 == 0 ? total : total = total + 2;
	} else {
		(total + 1) % 4 == 0 ? total = total + 1 : total = total + 3;
	}
	return total;
}

/*
* Set captions for table
*
* @param selector - jQuery selector for line marks
* @param maxValue - caption limit
*/
function setCaption(selector, maxValue) {
	var counter = 0;
	$(selector).each(function() {
		if (counter == 0) {
			$(this).html(0);
		} else if (counter == 1) {
			$(this).html(maxValue / 4);
		} else if (counter == 2) {
			$(this).html(maxValue / 2);
		} else if (counter == 3) {
			$(this).html(maxValue / 4 * 3);
		} else if (counter == 4) {
			$(this).html(maxValue);
		}

		counter++;
	});
}


/*
 * This function will set values for widget's legend
 *
 * @param displayType - value format that will be shown in pop-up
 * @param selector - parent's selector for widget
 * @param values - array of values to put into pop-ups
 */
function setDataToLegend(displayType, selector, values, names) {

	var total = 0;
	for ( var i = 0; i < values.length; i++) {
		total += parseInt(values[i]) === "NaN" ? 0 : parseInt(values[i]);
	}

	var percent = total / 100;

	if (displayType === "both") {

		$(selector)
				.find("div[class^=popup_legend_] p")
				.each(
						function(index) {

							var displayedValue = values[index] / percent % 1 === 0 ? values[index]
									/ percent
									: (values[index] / percent).toFixed(2);
							$(this).html(names[index] + "<br><b>" + values[index] +" (" + displayedValue + "%)</b>");
						});
	} else if (displayType === "percents") {

		$(selector)
		.find("div[class^=popup_legend_]")
		.each(
				function(index) {
					var displayedValue = values[index] / percent % 1 === 0 ? values[index]
					/ percent
					: (values[index] / percent).toFixed(2);
					$(this).html(displayedValue + "%");
				});
	}
}

function setStacksLenghts(selector, values, captionLimit) {
	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	$(selector).each(
			function(index) {
				var stackWidth = parseInt(values[index], 10) * unitSize;
				$(this).animate({
					width : stackWidth
				}, 500);

                $(this).find(".pop-up").css("margin-left",
                    ((parseInt(values[index]) / 2) * unitSize) - (values[index].toString().length * 7 / 2)).html(parseInt(values[index]));

                if (values[index].toString().length * 15 < stackWidth) {
                    $(this).find('.pop-up').removeClass('pop-up').addClass('pop-up-visible');
                    if ($(this).hasClass('not_scheduled')) {
                        $(this).find('.pop-up-visible').css('color', '#4D4D4D');
                    }
                }
			});
}

/*
* On "View All Events in study" page user should enter two dates for filter
* 
* @returns <Array<Date, Date>>[0] - start date for filter (current date - 10 years)
* @returns <Array<Date, Date>>[1] - end date for filter (current date)
*/
function getEventsFilterDates() {

	var d = new Date();
	var months = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
			"Sep", "Oct", "Nov", "Dec" ];

	var curr_date = d.getDate();
	var curr_month = months[parseInt(d.getMonth())];
	var curr_year = d.getFullYear();
	var startYear = parseInt(curr_year) - 10;
	var endDate = (('' + curr_date).length < 2 ? '0' : '') + curr_date + "-"
			+ curr_month + "-" + curr_year;

	var startDate = (('' + curr_date).length < 2 ? '0' : '') + curr_date + "-"
			+ curr_month + "-" + startYear;

	return [ startDate, endDate ];
}

/*
 * Get all required options for Pie chart
 */
function getPieOptions() {

	this.width = 450;
	this.height = 200;
	this.legend = {
		position : 'right',
		alignment : 'center',
		textStyle : {
			color : '#4D4D4D'
		}
	};
	this.pieSliceText = 'percents';
	this.pieStartAngle = 0;
	this.chartArea = {
		left : 10,
		top : 20,
		right : 20,
		width : "85%",
		height : "85%"
	};
	this.colors = [ '#8ac819' ];
	this.fontSize = 11;
	this.fontName = 'Tahoma';
	this.tooltip = {
		textStyle : {
			color : '#4D4D4D',
			fontName : 'Tahoma',
			fontSize : 11,
			showColorCode : false
		}
	};
}

/*
 * Get all required options for Vertical bar chart
 */
function getVerticalBarOptions() {

	this.hAxis = {
		title : "",
		allowContainerBoundaryTextCufoff : false,
		titleTextStyle : {
			italic : false,
			bold : true
		}
	};

	this.chartArea = {
		left : 30,
		top : 30,
		right : 40,
		width : "77%",
		height : "65%"
	};
	this.annotations = {
		textStyle : {
			fontName : 'Tahoma',
			fontSize : 10,
			bold : false,
			italic : false,
			color : '#4D4D4D',
			auraColor : '#ffffff',
			opacity : 1
		}
	};
	this.width = 790;
	this.height = 200;
	this.legend = {
		position : 'right',
		alignment : 'center',
		maxLines : 2,
		textStyle : {
			color : '#4D4D4D'
		}
	};

	this.fontSize = 11;
	this.fontName = 'Tahoma';
	this.bar = {
		groupWidth : '70%'
	};

	this.animation = {
		duration : 1000,
		easing : 'out'
	};

	this.colors = [ '#8ac819', '#ffc700' ];
	this.isStacked = true;
}


/*
 * This function will update color of buttons inside SDV widget depending on color theme
 */
function setSDVButtonsColor() {
	
	var color = $("form[id=sdv_progress] #currentColor").val();
	
	if (color === "green") {
		$(".sdv_progress input[type=button]").attr("class", "button_medium_green");
	} else if (color === "violet") {
		$(".sdv_progress input[type=button]").attr("class", "button_medium_violet");
	}
}

function getCurentUrl() {

	var urlTemp = new RegExp("^.*(pages)")
			.exec(window.location.href.toString());
	var url = "";

	if (urlTemp == null) {
		url = "pages/";
	} else {
		url = "";
	}
	return url;
}

/*
 * Create new cookie
 * 
 * @param c_name <String> name of cookie that will be created;
 * @param value <String> value of cookie that will be created;
 * @param exdays <String> time of cookie life in days;
 */
function setCookie(c_name, value, exdays) {

	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = encodeURI(value)
			+ ((exdays == null) ? "" : ("; expires=" + exdate.toUTCString()));
	document.cookie = c_name + "=" + c_value;
}

/*
 * Get Cookie by name
 * 
 * @param c_name <String> name of cookie to find;
 * @return <String> value of cookie;
 */
function getCookie(c_name) {

	if (document.cookie.length > 0) {
		c_start = document.cookie.indexOf(c_name + "=");

		if (c_start != -1) {
			c_start = c_start + c_name.length + 1;
			c_end = document.cookie.indexOf(";", c_start);

			if (c_end == -1) {
				c_end = document.cookie.length;
			}

			return  decodeURI(document.cookie.substring(c_start, c_end));
		}
	}

	return "";
}

/*
 * Check if cookies are enabled in browser
 */
function checkCookiesDialog() {

	setCookie("testCookie", "test");
	var result = getCookie("testCookie");

	if (result != "test") {
		$(".widget>div>div").remove();
		$(".widget>div>table").remove();
		$(".widget_big>div>div").remove();
		displayCookiesErrorMessage();
	}
}

/*
 * This function shows the dialog window with a warning message inside.
 */
function displayCookiesErrorMessage() {

	// Check if dialog was already shown before
	if ($("#confirmation").length == 0) {
		$("body")
				.append(
						"<div id=\"confirmation\" style=\"display: none;\">"
								+ "<div style=\"clear: both; text-align: justify;\">"
								+ "Cookies are disabled in your browser, some of widgets will not be shown. Please enable cookies."
								+ "</div>" + "</div>");

		$("#confirmation").dialog({
			autoOpen : false,
			modal : true,
			resizable : false,
			height : 80,
			minHeight : 80,
			width : 450
		});
	}

	$("#confirmation").dialog("open");

	// Set color scheme of the dialog
	var color = $('*').find('a').css('color');

	// If color theme = violet
	if (color == 'rgb(170, 98, 198)' || color == '#AA62C6'
			|| color == '#aa62c6') {
		$('.ui-dialog .ui-dialog-titlebar').find('span')
				.css('color', '#AA62C6');
	}

	// If color theme = green
	if (color == 'rgb(117, 184, 148)' || color == '#75b894'
			|| color == '#75B894') {
		$('.ui-dialog .ui-dialog-titlebar').find('span')
				.css('color', '#75b894');
	}
}
/* /Supporting functions */