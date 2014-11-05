/* Customize page */
var scrollTrigger = true;

/**
 * This function is used to show/hide toolbar when user click on "Show/Hide"
 * button.
 *
 * @param e -
 *            an element on which user will click.
 */
function toolbarToggle(e) {
	var activeTab = $("span[active=true]");
	var inActiveTab = $("span[active=false]");
	if (activeTab.attr("class") == "hide-message") {
		$(e).parent().animate({
			right : "-430px"
		}, 500);
	} else {
		$(e).parent().animate({
			right : "0px"
		}, 500);
	}
	inActiveTab.attr("active","true").css("display","inline");
	activeTab.attr("active","false").css("display","none");
}

/**
 * This function is used to add .trim method if it's not exists (For example in
 * some old versions of IE).
 */
function trim(string) {
	return string.replace(/^\s+|\s+$/g, '');
}

/**
 * This function is container for all functions that should be launched when
 * user opens "Customize Home Page" page.
 */
function launchCustomizePage() {
	setScrollHandlers(scrollTrigger);
	resizeTcInToolbar();
	initializeDnD();
	$("#toolbar, #layout1, #layout2,#layout_tc").disableSelection();
	$(".widget .chart_wrapper a").attr("href", "#");
	$(".widget input[type=button], .widget_big input[type=button]").remove();
	updatePostOrder();
}

/**
 * Initialize Drag'n'Drop on "Customize Home Page" page.
 */
function initializeDnD() {
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
			$(ui.item).find(".tc_content").css("display", "none");
			$(ui.item).find(".description").css("display", "block");
			$(ui.helper).find(".tc_content").css("display", "none");
			$(ui.helper).find(".description").css("display", "block");
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

/**
* This function adds auto scrolling for the toolbar area in the Customize page.
* When user set mouse over two columns widget - toolbar area will be scrolled
* to TC widgets drop zone.
*/
function setScrollHandlers(scrollTrigger) {
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

/**
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

/**
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

/**
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

/**
 * This function will be called after each change of widgets positions. It gets
 * order of widgets in each column in the form of arrays, and puts this arrays
 * into hidden inputs.
 */
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

/**
 * This function will be called when user will click on the "Remove" icon for
 * any widget. It will remove widget from layout and put it into the toolbar
 * area.
 */
function removeWidget(element) {
	var itemToRemove = $(element).parent();
	if (itemToRemove.attr("class") == "widget") 
		$("#toolbar").prepend(itemToRemove);
	else {
		$("#toolbar_tc").prepend(itemToRemove);
		itemToRemove.css("width", "380px");
		itemToRemove.find(".tc_content").css("display", "none");
		itemToRemove.find(".description").css("display", "block");
	}
	updatePostOrder();
}

/**
 * This function is used to send ajax request to WidgetsLayoutController, where
 * positions of widgets will be saved into the database. 
 * 
 * If there was no errors in user's submission - user will be redirected to the Home Page.
 * 
 * If there was some errors inside Controller (for example user can manually
 * change structure of the page using some browser's tools, and incorrect data
 * will goes into controller) - error will be shown in log.
 */
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


/* Supporting functions */
/**
* Calculate limit of caption for table (it should be divisible by four).
* 
* @param total - the maximum value that will be displayed in the chart
*/
function countCaptionLimit(total) {
	if (total < 4) 
		total = 4;
	else if (total % 2 == 0) 
		(total / 2) % 2 == 0 ? total : total = total + 2;
	else
		(total + 1) % 4 == 0 ? total = total + 1 : total = total + 3;

	return total;
}

/**
* Set captions for table
*
* @param selector - jQuery selector for line marks
* @param maxValue - caption limit
*/
function setCaption(selector, maxValue) {
	var counter = 0;
	$(selector).each(function() {
		var stack = this;
		switch (counter) {
			case 0:
				$(stack).html(0);
				break;
			case 1:
				$(this).html(maxValue / 4);
				break;
			case 2:
				$(this).html(maxValue / 2);
				break;
			case 3:
				$(this).html(maxValue / 4 * 3);
				break;
			case 4:
				$(this).html(maxValue);
				break;
		}
		counter++;
	});
}

/**
 * This function will set values for widget's legend
 *
 * @param displayType - value format that will be shown in pop-up
 * @param selector - parent's selector for widget
 * @param values - array of values to put into pop-ups
 */
function setDataToLegend(displayType, selector, values, names) {
	var total = 0;
	var valuesLength = values.length;
	for ( var i = 0; i < valuesLength; i++) {
		total += isNaN(parseInt(values[i])) ? 0 : parseInt(values[i]);
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
					var displayedValue = 0;
					if (percent != 0){
						displayedValue = values[index] / percent % 1 === 0 ? values[index]
						/ percent : (values[index] / percent).toFixed(2);
					}
					$(this).html(displayedValue + "%");
				});
	};
}

/**
 * This function if used to set size of stacks in the small horizontal bars widget.
 * 
 * @param <String> selector by which stack should be found by jQuery search engine (for example "#events_completion_container .stacked_bar .stack").
 * @param <List<Integer>> values - list of values, for each stack.
 * @param <Integer> captionLimit - length of whole row.
 */
function setStacksLengths(selector, values, captionLimit) {
	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	$(selector).each(function (index) {
		var stackWidth = parseInt(values[index], 10) * unitSize;
		$(this).animate({width: stackWidth}, 500);
		$(this).find(".pop-up").css(
			"margin-left", ((parseInt(values[index]) / 2) * unitSize) - (values[index].toString().length * 7 / 2))
			.html(parseInt(values[index]));
		if (values[index].toString().length * 15 < stackWidth) {
			$(this).find('.pop-up').removeClass('pop-up').addClass('pop-up-visible');
			if ($(this).hasClass('not_scheduled')) 
				$(this).find('.pop-up-visible').css('color', '#4D4D4D');
		}
	});
}

/**
 * This function if used to get size of row in the small horizontal bars widget.
 *
 * @param <String> selector by which stack should be found by jQuery search engine (for example "#events_completion_container .stacked_bar .stack").
 * @param <Integer> total - value of the row.
 * @param <Integer> captionLimit - length of whole row.
 * @return length of the row in pixels.
 */
function getRowLength(selector, total, captionLimit) {

	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	var rowWidth = parseInt(total, 10) * unitSize;
	return rowWidth;
}

/**
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

/**
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
	this.fontName = '"Tahoma"';
	this.tooltip = {
		textStyle : {
			color : '#4D4D4D',
			fontName : '"Tahoma"',
			fontSize : 11,
			showColorCode : false
		}
	};
}

/**
 * Get all required options for Vertical bar chart
 */
function getVerticalBarOptions() {
	this.hAxis = {
		title : "",
		slantedText: "false",
		maxTextLines: "auto",
		maxAlternation: 1,
		minTextSpacing: 5,
		allowContainerBoundaryTextCufoff : true,
		titleTextStyle : {
			italic : false,
			bold : true
		}
	};
	this.vAxis = {
		minValue : 0,
		viewWindow : {
			min : 0
		},
		maxValue: 10, 
		format: '0'
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
			fontName : '"Tahoma"',
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
	this.fontName = '"Tahoma"';
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

/**
 * This function will update color of buttons inside widget depending on color theme
 *
 * @param selector - widget's wrapper class
 */
function setButtonsColor(selector) {
	var color = $(selector + " .currentColor").val();
	if (color === "green") 
		$(selector + " input[type=button]").attr("class", "button_medium_green");
	else if (color === "violet")
		$(selector + " input[type=button]").attr("class", "button_medium_violet");
}

/**
 * This function is used to check if current url contains word "pages", and add
 * it if not.
 * 
 * @returns <String> prefix that will be used for ajax requests.
 */
function getCurrentUrl() {
	var urlTemp = new RegExp("^.*(pages)")
			.exec(window.location.href.toString());
	var url = "";
	if (urlTemp == null) 
		url = "pages/";
	else
		url = "";

	return url;
}

/**
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

/**
 * Get Cookie by name
 * 
 * @param c_name <String> name of cookie to find;
 * @return <String> value of cookie;
 */
function getCookie(c_name) {
	if (document.cookie.length > 0) {
		var c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) {
			c_start = c_start + c_name.length + 1;
			var c_end = document.cookie.indexOf(";", c_start);
			if (c_end == -1)
				c_end = document.cookie.length;
			return  decodeURI(document.cookie.substring(c_start, c_end));
		}
	}
	return "";
}

/**
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

/**
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
	if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6')
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#AA62C6');

	// If color theme = green
	if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894')
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#75b894');
}

/**
 * This function returns all attributes of DOM node:
 * 
 * @param node - jQuery object, which attributes will be taken. 
 * @param regexp - regular expression, by which object will be searched.
 */
function getNodeAttributes(node, regexp) {
	var attributes = {};
	$.each(node.get(0).attributes, function(index, attr) {
		if (regexp.test(attr.nodeName)) {
			var key = attr.nodeName.match(regexp)[1];
			attributes[key] = attr.value;
		}
	});
	return attributes;
}

/**
 * This method will capitalize the first letter in the string.
 */ 
function capitalizeFirstLetter(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
};


/**
 * This method is used to find maximum length of row.
 *
 * @param rows - an array of objects, from which values will be taken.
 */
function getMaxRowLengths(rows) {
	var max = 0;
	rows.each(function(entry) {
		var total = 0;
		var stacks = $(this).find("li");
		stacks.each(function(index) {
			var currentValue = $(this).find(".hidden").html();
			if (currentValue < 0)
				currentValue = 0;
			if (currentValue)
				total += parseInt(currentValue);
		});
		max = total > max ? total : max;
	});
	return max;
}

/**
 * This function is used to move such elements as 'Expected total enrollment'
 * to the right of row in the horizontal bar widgets.
 *
 * @param selector - jQ selector of the element which should be moved.
 * @param length - length in px, on which element will be moved to the right.
 */
function moveElementsToTheRight(selector,length) {

	var offset = parseInt(length, 10) + 2;
	$(selector).css("padding-left", offset + "px");
}
/* /Supporting functions */
