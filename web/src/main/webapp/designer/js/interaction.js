/* ===================================================================================================================================================================================================================================================================================================================================================================================================================================================================
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO‚ÄôS ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 * =================================================================================================================================================================================================================================================================================================================================================================================================================================================================== */

var editing = true;
var parser = new Parser();

$(function() {
	// Get url component
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true&restore=true");

	// ======================= Tool tip creation =======================
	$("#deleteButton").tooltip({
		container: "body",
		placement: "top",
		trigger: "hover focus",
		title: "Press to clear the current rule expression"
	});

	$(".target").tooltip({
		container: "body",
		placement: "auto",
		trigger: "hover focus",
		title: "Apply the rule to the selected CRF item(s). Drag and drop CRF item(s) to target the rule.  Multiple targets can be selected. To replace a target, drop another target on it."
	});

	$("#ruleName").tooltip({
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Enter a description for the new rule"
	});

	createToolTip({
		title: "Start grouping",
		element: $("#leftParentheses")
	});

	createToolTip({
		title: "End grouping",
		element: $("#rightParentheses")
	});

	createToolTip({
		title: "And...",
		element: $("#and")
	});

	createToolTip({
		title: "Or...",
		element: $("#or")
	});

	createToolTip({
		title: "Not",
		element: $("#not")
	});

	createToolTip({
		title: "Equal",
		element: $("#equal")
	});

	createToolTip({
		title: "Not equal",
		element: $("#notEqual")
	});

	createToolTip({
		title: "Less than",
		element: $("#lessThan")
	});

	createToolTip({
		title: "Greater than",
		element: $("#greaterThan")
	});

	createToolTip({
		title: "Less than or equal to",
		element: $("#lessThanOrEqual")
	});

	createToolTip({
		title: "Greater than or equal to",
		element: $("#greaterThanOrEqual")
	});

	createToolTip({
		title: "Contain",
		element: $("#contain")
	});

	createToolTip({
		title: "Does not contain",
		element: $("#notContain")
	});

	createToolTip({
		title: "Plus",
		element: $("#plus")
	});

	createToolTip({
		title: "Minus",
		element: $("#minus")
	});

	createToolTip({
		title: "Divide by",
		element: $("#divide")
	});

	createToolTip({
		title: "Multiplied by",
		element: $("#multiply")
	});

	createToolTip({
		element: $("#number"),
		title: "Integer and float number field"
	});

	createToolTip({
		title: "Null field",
		element: $("#empty")
	});

	createToolTip({
		title: "Date field",
		element: $("#date")
	});

	createToolTip({
		title: "Text field",
		element: $("#text")
	});

	createToolTip({
		title: "Current system date",
		element: $("#currentDate")
	});

	createToolTip({
		title: "Select a study",
		element: $("#studiesLink")
	});

	createToolTip({
		element: $("#eventsLink"),
		title: "Select a study event"
	});

	createToolTip({
		element: $("#crfsLink"),
		title: "Select an event CRF"
	});

	createToolTip({
		element: $("#versionsLink"),
		title: "Select a CRF Version"
	});

	createToolTip({
		element: $("#itemsLink"),
		title: "Select a CRF item"
	});

	createToolTip({
		element: $("#groupSurface"),
		title: "Drag and drop an item from the Group tool box, the data toolbox or a CRF item."
	});

	createToolTip({
		element: $(".item"),
		title: "Drag and drop a CRF item."
	});

	createToolTip({
		element: $(".eventify"),
		title: "Click to bind the target to only the event."
	});

	// ======================= End of tool tip creation =======================
	// Hide action messages parameter divs
	$("div[id='actionMessages']").hide();
	$("div[id='actionMessages'] > *").hide();

	// ======================= Draggables =======================
	createDraggable({
		target: $("#groupSurface"),
		element: $("#leftParentheses")
	});

	createDraggable({
		target: $("#secondGroupSurface"),
		element: $("#rightParentheses")
	});

	$("div[id='evaluate']").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".eval")
		});
	});

	$("div[id='compare']").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".comp")
		});
	});

	$("div[id='calculate']").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".comp")
		});
	});

	$("div[id='data']").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: ".group, .value"
		});
	});

	// ======================= End of creating draggables =======================

	// ======================= Droppables =======================
	createDroppable({
		element: $("#groupSurface"),
		accept: "#leftParentheses, div[id='data'] p, div[id='items'] td"
	});

	createDroppable({
		element: $(".target"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".item"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".dest"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".value"),
		accept: "div[id='data'] p, div[id='items'] td"
	});

	// ======================= End of creating droppables =======================

	// ============================= Event handlers =============================
	$("#ruleName").blur(function() {
		parser.setName($(this).val());
	})

	$("input[name='ruleInvoke']").change(function() {
		if ($("#evaluateTrue").is(":checked")) {
			parser.setEvaluatesTo(true);
		} else {
			parser.setEvaluatesTo(false);
		}
	});

	$("#ide").change(function() {
		parser.setInitialDataEntryExecute($(this).is(":checked"));
	});

	$("#ae").change(function() {
		parser.setAdministrativeEditingExecute($(this).is(":checked"));	
	});

	$("#dde").change(function() {
		parser.setDoubleDataEntryExecute($(this).is(":checked"));
	});

	$("#dataImport").change(function() {
		parser.setDataImportExecute($(this).is(":checked"));
	});

	$(".glyphicon-remove").click(function() {
		parser.deleteTarget(this);
	});

	// === Enable addition event OID to targets ===
	$(".eventify").change(function() {
		parser.eventify(this);
	});

	// === Discrepancy action ====
	$("#chkDiscrepancyText").change(function() {
		parser.setDiscrepancyAction({
			selected: $(this).is(":checked"),
			message: $("#discrepancyText").val()
		});

		$("#discrepancyText").find("textarea").focus();
	});

	$("#discrepancyText").find("textarea").blur(function() {
		parser.setDiscrepancyAction({
			message: $(this).val(),
			selected: $("#chkDiscrepancyText").is(":checked")
		});
	});

	// === Email action ====
	$("#chkEmail").change(function() {
		parser.setEmailAction({
			to: $("#toField").val(),
			message: $("#body").val(),
			selected: $(this).is(":checked")
		});

		$("#toField").focus();
	});

	$("#toField").blur(function() {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (!re.test($(this).val().trim())) {
			$(this).parent().addClass("has-error");
		} else {
			parser.setEmailAction({
				to: $("#toField").val(),
				message: $("#body").val(),
				selected: $("#chkEmail").is(":checked")
			});

			$(this).parent().removeClass("has-error");
		}
	});

	$("#body").blur(function() {
		parser.setEmailAction({
			to: $("#toField").val(),
			message: $("#body").val(),
			selected: $("#chkEmail").is(":checked")
		});
	});

	// === Insert action ====
	$("#chkData").change(function() {
		parser.setInsertAction({
			selected: $(this).is(":checked")
		});

		$("#insert").find("textarea").focus();
	});

	// Not used
	$("#insert").find("textarea").blur(function() {
		parser.setInsertActionMessage($(this).val());
	});

	$(".value").blur(function() {
		parser.setDestinationValue({
			value: $(this).val(),
			id: $(this).parents(".row").attr("id")
		});
	});

	// === Show/Hide action ====
	$("input[action=show]").change(function() {
		parser.setShowHideAction({
			show: true,
			hide: false
		});
	});

	$("input[action=show]").click(function() {
		if (this.getAttribute('checked')) {
			$(this).removeAttr('checked');
			parser.setShowHideAction(Object.create(null));
		} else {
			$(this).attr('checked', true);
		}
	});

	$("input[action=hide]").change(function() {
		parser.setShowHideAction({
			hide: true,
			show: false
		});
	});

	$("input[action=hide]").click(function() {
		if (this.getAttribute('checked')) {
			$(this).removeAttr('checked');
			parser.setShowHideAction(Object.create(null));
		} else {
			$(this).attr('checked', true);
		}
	});

	$("#dActionMessage").blur(function() {
		parser.setShowHideActionMessage($(this).val());
	});

	$("#deleteButton").click(function() {
		if ($("#designSurface").find(".panel-body").children().size() > 2) {
			bootbox.confirm("Are you sure you want to clear the entire expression?", function(result) {
				if (result) {
					resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));
				}
				$(".modal-backdrop").remove();
			});
		}
	});

	$('div[id="variables"] a').click(function(e) {
		currentPageIndex = 0;
		e.preventDefault();
		$(this).tab('show');
	});

	$("a[href=#items]").click(function() {
		var selected = $("table").find(".selected").length;
		var data = JSON.parse(sessionStorage.getItem("studies"));

		// Only study
		if (selected === 1) {
			var study = data[$("table").find(".selected").attr("id")]
			var studyEvent = study.events[Object.keys(study.events)[0]]
			if (studyEvent) {
				var crf = studyEvent.crfs[Object.keys(studyEvent.crfs)[0]]
				createBreadCrumb({
					crf: crf.name,
					study: study.name,
					event: studyEvent.name,
					version: crf.versions[0].name
				});

			} else {
				createBreadCrumb({
					study: study.name
				});
			}
		}
	})

	$("#validate").click(function() {
		parser.validate();
	});

	createPopover($("#groupSurface"));
	// If a rule already exists in a session, display it
	$(document).ready(function() {
		parser.fetchStudies();
		// If editing a rule
		if (parser.getParameter("action") === "edit") {
			parser.fetchRuleForEditing();
		}

		if (sessionStorage.getItem("status") && sessionStorage.getItem("status") === "load") {
			var rule = JSON.parse(sessionStorage.getItem("rule"));
			parser.render(rule);
		}
		sessionStorage.removeItem("status");
	});

	$('body').click(function(e) {
		$(".tops").remove();
		$(".popover").remove();
	});
});

Array.prototype.chunk = function(arr) {
    if (!this.length) {
        return [];
    }
    return [this.slice(0, arr)].concat(this.slice(arr).chunk(arr));
};