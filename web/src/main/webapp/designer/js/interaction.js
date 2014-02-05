var selectedStudy = 0;
var parser = new Parser();

$(function() {

	// Get url component
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true");
	// Create tooltips for the controls to aid in navigation
	$("#deleteButton").tooltip({

		container: "body",
		placement: "top",
		trigger: "hover focus",
		title: "Press to clear the current rule expression"
	})

	$("input.ui-droppable").tooltip({
		
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Apply the rule to the selected CRF item(s). Drag and drop CRF item(s) to target the rule.  Multiple targets can be selected"
	})

	$("#ruleName").tooltip({
		
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Enter a name for the new rule"
	})

	createToolTip({
		
		title: "Start grouping",
		element: $("#leftParentheses")
	})

	createToolTip({
		
		title: "End grouping",
		element: $("#rightParentheses")
	})

	createToolTip({
		
		title: "And...",
		element: $("#and")
	})	

	createToolTip({
		
		title: "Or...",
		element: $("#or")
	})

	createToolTip({
		
		title: "Not",
		element: $("#not")
	})

	createToolTip({
		
		title: "Equal",
		element: $("#equal")
	})

	createToolTip({
		
		title: "Not equal",
		element: $("#notEqual")
	})

	createToolTip({
		
		title: "Less than",
		element: $("#lessThan")
	})

	createToolTip({
		
		title: "Greater than",
		element: $("#greaterThan")
	})

	createToolTip({
		
		title: "Less than or equal to",
		element: $("#lessThanOrEqual")
	})

	createToolTip({
		
		title: "Greater than or equal to",
		element: $("#greaterThanOrEqual")
	})

	createToolTip({
		
		title: "Contain",
		element: $("#contain")
	})

	createToolTip({
		
		title: "Does not contain",
		element: $("#notContain")
	})

	createToolTip({
		
		title: "Plus",
		element: $("#plus")
	})

	createToolTip({
		
		title: "Minus",
		element: $("#minus")
	})

	createToolTip({
		
		title: "Divide by",
		element: $("#divide")
	})

	createToolTip({
		
		title: "Multiplied by",
		element: $("#multiply")
	})

	createToolTip({
		
		element: $("#number"),
		title: "Integer and float number field"
	})

	createToolTip({
		
		title: "Null field",
		element: $("#empty")
	})

	createToolTip({
		
		title: "Date field",
		element: $("#date")
	})

	createToolTip({
		
		title: "Text field",
		element: $("#text")
	})

	createToolTip({
		
		title: "Select a study",
		element: $("#studiesLink")
	})

	createToolTip({
		
		element: $("#eventsLink"),
		title: "Select a study event"
	})

	createToolTip({
		
		element: $("#crfsLink"),
		title: "Select an event CRF"
	})

	createToolTip({
		
		element: $("#itemsLink"),
		title: "Select a CRF item"
	})

	createToolTip({
		
		element: $("#groupSurface"),
		title: "Drag and drop an item from the Group tool box, the data toolbox or a CRF item."
	})	

	// End of tool tip creation

	// Hide action messages parameter divs
	$("div[id='actionMessages']").hide();
	$("div[id='actionMessages'] > *").hide();

	// Create draggables
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
			target: $(".eval"),
		});
	})

	$("div[id='compare']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".comp"),
		});
	})

	$("div[id='calculate']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".comp"),
		});
	})

	$("div[id='data']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".group"),
		});
	})

	// End of creating draggables

	// Create droppables
	createDroppable({
		element: $("#groupSurface"),
		accept: "#leftParentheses, div[id='data'] p, div[id='items'] td"
	})

	createDroppable({
		element: $(".target"),
		accept: "div[id='items'] td"
	})

	// End of creating droppables

	$('div[id="variables"] a').click(function(e) {

		currentPageIndex = 0;
		
		e.preventDefault();
		$(this).tab('show');
	})

	$("a[href=#items]").click(function() {

		var selected = $("table").find(".selected").length

		var data = JSON.parse(sessionStorage.getItem("studies"));

		// Only study
		if (selected === 1) {

			var study = data[$("table").find(".selected").attr("id")]
			var studyEvent = study.events[Object.keys(study.events)[0]]
			var crf = studyEvent.crfs[Object.keys(studyEvent.crfs)[0]]

			createBreadCrumb({

				crf: crf.name,
				study: study.name,
				event: studyEvent.name

			})
		}
	})

	$("#chkDiscrepancyText").change(function() {

		toggleActionControls({
			control: this,
			element: $("#discrepancyText")
		})

		$("#discrepancyText").find("textarea").focus();
	})

	$("#chkEmail").change(function() {

		toggleActionControls({
			control: this,
			element: $("#emailTo")
		})

		$("#emailTo").find("input").focus();

		toggleActionControls({
			control: this,
			element: $("#email")
		})
	})

	$("#chkData").change(function() {

		toggleActionControls({
			control: this,
			element: $("#ruleData")
		})

		$("#ruleData").find("textarea").focus();
	})

	$("input[name=tItem]").change(function() {

		toggleActionControls({
			control: this,
			element: $("#parameters")
		})

		$("#parameters").find("textarea").focus();
	})

	$("#deleteButton").click(function() {

		if ($("#designSurface").find(".panel-body").children().size() > 2) {

			bootbox.confirm("Are you sure you want to clear the entire expression?", function(result) {

				if (result) {

					resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));

					$(".modal-backdrop").remove();
				}
			});
		}
	})

	$("#toField").blur(function() {

		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

		if (!re.test($(this).val().trim())) {

			$(this).parent().addClass("has-error");
		} else {

			$(this).parent().removeClass("has-error");
		}
	})

	$("a[id='test']").click(function() {

		var vRule = parser.createRule();

		if (vRule && vRule.valid) {

			validate({

				targets: vRule.targets,
				expression: vRule.expression.replace(/\,/g, " "),
				evaluateTo: $("input[name='ruleInvoke']:checked").parent().text().trim()
			});
		}
	})

	$("#validate").click(function() {

		var vRule = parser.createRule();
		if (vRule && vRule.valid) {

			validate({

				targets: vRule.targets,
				expression: vRule.expression.replace(/\,/g, " "),
				evaluateTo: $("input[name='ruleInvoke']:checked").parent().text().trim()

			})
		}
	})

	createPopover($("#groupSurface"));
	fetchStudies();

	// If a rule already exists in a session, display it
	$(document).ready(function() {

		if (sessionStorage.getItem("status")) {

			// Persist these to be used in validation.js
			var name = sessionStorage.getItem("name");
			var actions = sessionStorage.getItem("actions");
			var targets = JSON.parse(sessionStorage.getItem("targets"));
			var properties = JSON.parse(sessionStorage.getItem("properties"));
			var expression = JSON.parse(sessionStorage.getItem("oExpression"));

			// Rule name
			$("#ruleName").val(name);

			var currInput = $(".target");

			// Targets
			for (var t = 0; t < targets.length; t++) {

				var crfItem = parser.findItemName(targets[t]);

				if (t > 0) {

					var input = $(".target").clone();

					createDroppable({
						element: input,
						accept: "div[id='items'] td"
					})

					input.val(crfItem.item.name);


					currInput.after(input);

					currInput = input;

				} else {

					$(".target").val(crfItem.item.name);
				}

				parser.targets.push(crfItem.item.name);
			}

			var newInput = $(".target").last().clone();

			createDroppable({
				element: newInput,
				accept: "div[id='items'] td"
			})

			newInput.val("");
			$(".target").last().after(newInput);

			// Rule execution
			if (properties.doubleDataEntry) {
				$("#dde").prop("checked", true);
			}

			if (properties.initialDataEntry) {
				$("#ide").prop("checked", true);
			}

			if (properties.importDataEntry) {
				$("#dataImport").prop("checked", true);
			}

			if (properties.administrativeDataEntry) {
				$("#ae").prop("checked", true);
			}

			// Rule evaluate to
			if (properties.evaluateTo) {

				$("#evaluateTrue").prop("checked", true);

			} else {

				$("#evaluateFalse").prop("checked", true);
			}

			// Parameters
			$("#message").show();
			$("#actionMessages").show();

			// Actions
			if (properties.to) {

				$("#email").show();
				$("#emailTo").show();

				$("#body").val(properties.body);
				$("#toField").val(properties.to);

				$("#chkEmail").prop("checked", true);
			}

			if (properties.discrepancyText) {

				$("#discrepancyText").show();
				$("#discrepancyText").find("textarea").val(properties.discrepancyText);

				$("#chkDiscrepancyText").prop("checked", true);
			}

			var currDroppable = $("#groupSurface");
			for (var e = 0; e < expression.length; e++) {

				if (e === 0) {

					$("#groupSurface").text(expression[e]);

				} else {

					var predicate = expression[e];

					if (parser.isOp(predicate)) {

						var droppable = createSymbolDroppable();
						droppable.text(predicate);

						currDroppable.after(droppable);

						currDroppable = droppable;

					} else if (parser.isConditionalOp(predicate)) {

						var droppable = createConditionDroppable();
						droppable.text(predicate);

						currDroppable.after(droppable);

						currDroppable = droppable;

					} else {

						var droppable = createStartExpressionDroppable();
						droppable.text(predicate);

						currDroppable.after(droppable);

						currDroppable = droppable;
					}
				}
			}
		}

		sessionStorage.removeItem("status");

	})

	$('body').click(function(e) {

		$(".modal").remove();
		$(".popover").remove();
	});
})

/* =============================================================================
 * Determines the parameter boxes to show depending on the selected action.
 *
 * Argument Object [params] parameters:
 * - control - the action control that has been select
 * - element - the corresponding action element to display if control is selected
 * ============================================================================ */
function toggleActionControls(params) {

	$("#message").show();
	if ($(params.control).is(":checked")) {

		if ($("#actionMessages").is(":visible")) {

			params.element.show();

			$("#actionMessages > .space-top-x").removeClass("space-top-x");
			$("#actionMessages > div").filter(":visible:first").addClass("space-top-x");

		} else {

			$("#actionMessages").show();
			$("#actionMessages > span").show();

			// show element
			params.element.show();

			$("#actionMessages > div").filter(":visible:first").addClass("space-top-x");
		}
	} else {

		$(params.element).hide();

		if (!$("div[id='actionMessages'] > div").is(":visible")) {

			$("#actionMessages").hide();

		} else {

			$("#actionMessages > div").filter(":visible:first").addClass("space-top-x");
		}
	}
}

Array.prototype.chunk = function(arr) {

    if (!this.length) {
        return [];
    }

    return [this.slice(0, arr)].concat(this.slice(arr).chunk(arr));
};