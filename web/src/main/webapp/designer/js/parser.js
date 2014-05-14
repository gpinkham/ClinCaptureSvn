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
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i].trim();
        if (c.indexOf(name)==0) return c.substring(name.length,c.length);
    }
    return "";
}

function Parser() {
	this.rule = Object.create(null);
	this.rule.targets = [];
	this.rule.actions = [];
	this.rule.copied = false;
	this.rule.editing = false;
}

Parser.prototype.getStudy = function() {
	if (this.rule) {
		return this.rule.study;
	}
};

Parser.prototype.setStudy = function(study) {
	this.rule.study = study;
};

Parser.prototype.getCopy = function() {
	return this.rule.copied;
};

Parser.prototype.setCopy = function(copied) {
	this.rule.copied = copied;
};

Parser.prototype.getEditing = function() {
	return this.rule.editing;
};

Parser.prototype.setEditing = function(editing) {
	this.rule.editing = editing;
};

Parser.prototype.getRuleSet = function() {
	return this.rule.ruleSet;
};

Parser.prototype.setRuleSet = function(ruleSet) {
	this.rule.ruleSet = ruleSet;
};

/* ========================================================================
 * Fetch studies from CC. The studies come with events/crf and items added.
 * ====================================================================== */
Parser.prototype.fetchStudies = function() {
	// Notification
	$("body").append(createLoader());
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	$.ajax({
		type: "POST",
		url: c + "/rules?action=fetch",
		success: function(studies) {
			// FF can return a string
			if (typeof(studies) === "string") {
				studies = JSON.parse(studies);
			}
			sessionStorage.setItem("studies", JSON.stringify(studies));
			loadStudies(studies);
			// If editing a rule
			if (parser.getParameter("action") === "edit") {
				parser.fetchRuleForEditing();
			}
			// A back action from validation page
			if (sessionStorage.getItem("status") && sessionStorage.getItem("status") === "load") {
				parser.render(JSON.parse(sessionStorage.getItem("rule")));
				sessionStorage.removeItem("status");
			}
			// Successful save - load previous context
			if (sessionStorage.getItem("context")) {
				parser.render(JSON.parse(sessionStorage.getItem("context")));
				sessionStorage.removeItem("context");
			}
			removeLoader();
			// Wait till items will be added to DOM structure and update click events for them;
			setTimeout(updateOnClickActions,400);
		},
		error: function(response) {
			handleErrorResponse({
				response: response
			});
		}
	});
};

/* ========================================================================
 * Fetch a specific rule from CC for editing in the designer. The rule is
 * passed as an id on the url with a parameter action=editing
 * ====================================================================== */
Parser.prototype.fetchRuleForEditing = function() {
	editing = true;
	$("body").append(createLoader());
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$.ajax({
		type: "POST",
		url: c + "/rules?action=edit&id=" + this.getParameterValue("id") + "&rId=" + this.getParameterValue("rId"),
		success: function(response) {
			var rule = null;
			// FF can return a string
			if (typeof(response) === "string") {
				rule = JSON.parse(response);
			}
			rule.editing = true;
			rule.ruleSet = parser.getParameterValue("rId");
			rule.study = parseInt(parser.getParameterValue("study"));
			parser.render(rule);
		},
		error: function(response) {
			handleErrorResponse({
				response: response
			});
		}
	});
};

/* ===========================================================================
 * For start groups and conditinal drop surface, this functions determines what
 * should come next.
 *
 * Arguments [currentElement]:
 * => currentElement - The current droppable
 *
 * Return the type for the expected next element.
 * ========================================================================== */
Parser.prototype.determineNext = function(currentElement) {
	// LPAREN
	if (currentElement.text() == "(") {
		return "ANY";
		// RPAREN
	} else {
		return "EVAL";
	}
};

/* ==============================================================================
 * This function creates the next valid droppable surface based off the existing
 * surface.
 *
 * Arguments [params]:
 * => element - The current droppable
 * => ui - The draggable that has been dropped
 * ============================================================================ */
Parser.prototype.createNextDroppable = function(params) {
	var __NEXT__ = this.determineNext(params.element);
	// Selected item
	var selectedItem = this.findItemBySelectedProperties({
		study: this.extractStudy(this.getStudy()),
		identifier: params.ui.draggable.attr("item-name"),
		crf: $("div[id=crfs]").find(".selected").find("td[oid]").attr("oid"),
		evt: $("div[id=events]").find(".selected").find("td[oid]").attr("oid"),
		version: $("div[id=versions]").find(".selected").find("td[oid]").attr("oid")
	});
	var path = null;
	if (selectedItem) {
		path = selectedItem.crfOid + "." + selectedItem.group + "." + selectedItem.oid;
	}
	if (__NEXT__ === "ANY") {
		var RPAREN = createRPARENDiv();
		var dataPredicate = createStartExpressionDroppable();
		dataPredicate.text("Group or Data");
		if (params.element.next().length === 0) {
			params.element.after(dataPredicate);
			dataPredicate.after(RPAREN);
			createPopover(RPAREN);
			createPopover(dataPredicate);
		}
		// Enable showing selected item td
		params.element.click(function() {
			showCRFItem(this);
		});
	} else if (params.element.is(".target")) {
		if (!this.isAddedTarget(selectedItem)) {
			if (params.existingValue) {
				for (var x = 0; x < this.rule.targets.length; x++) {
					var t = this.rule.targets[x];
					if (t.name === params.existingValue) {
						this.rule.targets.splice(x, 1);
					}
				}
			}
			var target = Object.create(null);
			target.oid = selectedItem.oid;
			target.name = selectedItem.name;
			target.crf = selectedItem.crfOid;
			target.group = selectedItem.group;
			target.evt = selectedItem.eventOid;
			target.version = selectedItem.crfVersionOid;
			this.rule.targets.push(target);
			// If this is an edit - changing an eventified target
			if (params.element.parent().find(".versionify").is(":checked")) {
				target.versionify = true;
			}
			//Reset UI
			var div = params.element.parent().clone();
			div.find(".target").val("");
			div.find(".target").removeClass("bordered");
			// Re-bind Event handlers
			createDroppable({
				accept: "div[id='items'] td",
				element: div.find(".target")
			});
			// create a new input
			if (!params.element.val()) {
				$('.opt:last').after(div);
				div.after($('.opt:last').clone());
			}
			div.find(".target").focus();
		}
		params.element.removeClass("bordered");
		params.element.val(params.ui.draggable.attr("item-name"));
	} else if (params.element.is(".dest")) {
		if (!this.isAddedShowHideTarget(params.ui.draggable.attr("item-name"))) {
			if (params.existingValue) {
				var itemExpression = params.element.attr("crf-oid") + "." + params.element.attr("group-oid") + "." + params.element.attr("item-oid");
				var index = this.getShowHideAction().destinations.indexOf(itemExpression);
				if (index > -1) {
					this.getShowHideAction().destinations.splice(index, 1);
				}
			}
			this.getShowHideAction().destinations.push(path);
			var div = params.element.parent().clone();
			div.find("input").val("");
			div.find("input").removeClass("bordered");
			// create a new input
			if (!params.element.val()) {
				params.element.parent().after(div);
				createDroppable({
					element: div.find(".dest"),
					accept: "div[id='items'] td"
				});
			}
			params.element.val(params.ui.draggable.attr("item-name"));
		}
		params.element.removeClass("bordered");
	} else if (params.element.is(".item")) {
		if (!this.isAddedInsertTarget(params.ui.draggable.attr("item-name"))) {
			if (params.existingValue) {
				for (var x = 0; x < this.getInsertAction().destinations.length; x++) {
					var dest = this.getInsertAction().destinations[x];
					if (dest.oid === this.constructCRFPath(params.existingValue)) {
						this.getInsertAction().destinations.splice(x, 1);
					}
				}
			}
			params.element.removeClass("bordered");
			// Destination
			var dest = null;
			if (this.getInsertActionDestination(params.element.parents(".row").attr("id"))) {
 				dest = this.getInsertActionDestination(params.element.parents(".row").attr("id"));
 				dest.oid = path;
 			} else {
 				dest = Object.create(null);
 				dest.oid = path;
 				dest.id = params.element.parents(".row").attr("id");
 				this.getInsertAction().destinations.push(dest);
 			}
 			if (dest.value && dest.value.length > 0) {
				this.addNewInsertActionInputs();
			}
 			params.element.val(params.ui.draggable.attr("item-name"));
			params.element.parent().siblings(".col-md-4").find(".value").focus();
		}
		params.element.removeClass("bordered");
	} else if (params.element.is(".value")) {
		var destination = null;
		if (this.getInsertAction().destinations.length > 0) {
			for (var x = 0; x < this.getInsertAction().destinations.length; x++) {
				destination = this.getInsertAction().destinations[x];
				if (destination.id === params.element.parents(".row").attr("id")) {
					break;
				}
			}
		} else {
			destination = Object.create(null);
			destination.id = params.element.parents(".row").attr("id");
			this.getInsertAction().destinations.push(destination);
		}
		if (destination) {
			if (!params.existingValue) {
				var index = this.getInsertAction().destinations.indexOf(destination);
				if (index > -1) {
					this.getInsertAction().destinations.splice(index, 1);
				}
				if (destination.oid && destination.oid.length > 0) {
					this.addNewInsertActionInputs();
				}
				this.getInsertAction().destinations.push(destination);
			}

			if (params.ui.draggable.is("td.group")) {
				destination.item = true;
				destination.value = selectedItem.oid;
				params.element.val(params.ui.draggable.attr("item-name"));
			} else {
				destination.item = false;
				if (this.isText(params.ui.draggable)) {
					params.element.focus();
				} else if (this.isDate(params.ui.draggable)) {
					params.element.attr("type", "date");
					params.element.val($(this).text());
					$.fn.datepicker.defaults.format = getCookie('bootstrapDateFormat');
					params.element.data({
						date: new Date(params.element.val())
					}).datepicker('update').children("input").val(new Date(params.element.val()));
					params.element.datepicker({
						orientation: 'bottom left'
					}).on("hide", function() {
						if ($(this).val()) {
							params.element.val($(this).val());
						} else {
							params.element.val("Select Date");
						}
					});
				} else if (this.isNumber(params.ui.draggable)) {
					params.element.attr("type", "number");
					params.element.blur(function() {
						if ($(this).val() && /[0-9]|\./.test($(this).val())) {
							params.element.text($(this).val());
							params.element.removeClass("invalid");
						} else {
							params.element.val();
							params.element.select();
							params.element.addClass("invalid");
						}
					});
				} else if (parser.isEmpty(params.ui.draggable)) {
					params.element.removeAttr("type");
					params.element.val('""');
				} else {
					destination.value = params.element.val();
				}
			}
			params.element.focus();
		}
		params.element.removeClass("bordered");
	} else {
		if (params.element.is(".comp")) {
			var dataPredicate = createStartExpressionDroppable();
			// Avoid creating unnecessary evaluation/data/crf item/group boxes
			if (params.element.next().size() === 0 || params.element.next().is(".pull-right") || params.element.next().is(".group")) {
				if (params.element.next().is(".group") && params.element.next().text() === ")") {
					params.element.after(dataPredicate);
				} else if (params.element.next().length === 0) {
					params.element.after(dataPredicate);
				}
			} else if (params.element.next().is(".eval")) {
				params.element.after(dataPredicate);
			}
			createPopover(dataPredicate);
		} else if (params.element.is(".eval")) {
			if (!params.element.next().is(".dotted-border")) {
				var droppable = createStartExpressionDroppable();
				params.element.after(droppable);
				createPopover(droppable);
			}
		} else {
			if (!params.element.next().is(".comp")) {
				if (!params.element.next().is(".dotted-border")) {
					var droppable = createSymbolDroppable();
					params.element.after(droppable);
					createPopover(droppable);
				}
			}
		}
	}
	// Update onClick actions for newly added elements;
	updateOnClickActions();
};

Parser.prototype.getInsertActionDestination = function(id) {
	for (var x = 0; x < this.getInsertAction().destinations.length; x++) {
		var dest = this.getInsertAction().destinations[x];
		if (dest.id == id) {
			return dest;
		}
	}
};

/* ======================================================
 * Determines if the element is of type 'text' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'text'
 * ====================================================== */
Parser.prototype.isText = function(element) {
	return element.is('.text');
};

/* ======================================================
 * Determines if the element is of type 'date' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'date'
 * ====================================================== */
Parser.prototype.isDate = function(element) {
	return element.is('.date');
};

/* ======================================================
 * Determines if the element is of type 'empty' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'empty'
 * ====================================================== */
Parser.prototype.isEmpty = function(element) {
	return element.is('.empty');
};

/* ======================================================
 * Determines if the element is of type 'number' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'number'
 * ====================================================== */
Parser.prototype.isNumber = function(element) {
	return element.is('.number');
};

/* =============================================================
 * Determines if the element is of type 'current-date' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'currentDate'
 * ====================================================== */
Parser.prototype.isCurrentDate = function(element) {
	return element.is('.current-date');
};

Parser.prototype.getItemDuplicates = function(itemName) {
	var duplicates = [];
	var storedStudies = JSON.parse(sessionStorage.getItem("studies"));
	storedStudies.forEach(function(study) {
		study.events.forEach(function(evt) {
			evt.crfs.forEach(function(crf) {
				crf.versions.forEach(function(version) {
					version.items.forEach(function(item) {
						if (item.name == itemName) {
							item.crfOid = crf.oid;
							item.eventOid = evt.oid;
							item.crfVersionOid = version.oid;
							duplicates.push(item);
						}
					});
				});
			});
		});
	});
	return duplicates;
};

Parser.prototype.isDuplicated = function(params) {
	var duped = this.getItemDuplicates(params.name).map(function(x) {
		return x[params.type];
	}).every(function(element, index, array){
		return array[0] === array[index];
	});
	return duped;
};

Parser.prototype.isRepeatItem = function(itemName) {
	var item = this.getItem(itemName);
	if (item.repeat) {
		return true;
	}
	return false;
};

Parser.prototype.getRuleCRFItems = function() {
	var items = [];
	var itemHolders = $(".dotted-border, .target, .item, .value, .dest");
	for (var x = 0; x < itemHolders.size(); x++) {
		if ($(itemHolders[x]).text().length > 0) {
			var item = this.findItem($(itemHolders[x]).text());
			if (item && typeof(item) !== "function") {
				var pred = Object.create(null);
				pred.holder = $(itemHolders[x]);
				pred.itemName = $(itemHolders[x]).text();
				items.push(pred);
			}
		}
	}
	return items;
};

/* ==============================================================================
 * Creates a rule based on what the user has dropped on the drop surfaces and the
 * entered details.
 *
 * Note that this function also validates the completeness of what the user has
 * designed.
 *
 * Returns:
 * - validity of the rule
 * - the rule targets
 * - expression in text format (an array really)
 * - The evaluate to predicate
 * ============================================================================ */
Parser.prototype.createRule = function() {
	this.quantify = function(element, index, array) {
		if (array.length > 1) {
			for (var x = 0; x < array.length; x++) {
				if (array[x] !== array[0]) {
					return false;
				}
			}
		}
		return true;
	};
	var study = this.extractStudy(this.getStudy());
	var expressionPredicates = $(".dotted-border:not(:empty), .target:not(:empty)");
	var oids = expressionPredicates.toArray().map(function(x) {
		if ($(x).is(".group") || $(x).is(".target")) {
			var tar = parser.getItem($(x).text());
			if (tar && typeof(tar) !== "function") {
				return tar.eventOid;
			}
		}
	}).filter(function(x) {
		if (x) {
			return x;
		}
	});
	var expression = [];
	var quantified = oids.every(this.quantify);
	$(".dotted-border").each(function(index) {
		var pred = $($(".dotted-border")[index]).text();
		if (parser.isOp(pred)) {
			pred = parser.isConditionalOp(pred) ? pred = pred.toLowerCase() : pred = parser.getOp(pred);
		}
		var item = parser.getItem(pred);
		if (item) {
			pred = parser.constructExpressionPath({
				quantified: quantified,
				ele: $(".dotted-border")[index]
			});
		}
        // check dates
		if (parser.isDateValue(pred)) {
			var date = Date.parse(pred);
			if (date != null) {
				pred = date.toString("yyyy-MM-dd");
			}
		}
		expression.push(pred);

	});
	parser.rule.expression = expression;
	if (parser.isValid(expression).valid) {
		for (var x = 0; x < parser.rule.targets.length; x++) {
			var target = parser.rule.targets[x];
			if (target.linefy) {
				target.name = this.constructRepeatItemPath(target);
			} else if (target.versionify) {
				target.name = this.constructCRFVersionPath(target);
			} else {
				var formPath = target.crf + "." + target.group + "." + target.oid;
				target.name = target.eventify ? (target.evt + "." + formPath) : formPath;
			}
		}
	}
};

Parser.prototype.constructExpressionPath = function(params) {
	if (params.quantified) {
		return  $(params.ele).attr("crf-oid") + "." + $(params.ele).attr("group-oid") + "." + $(params.ele).attr("item-oid");
	} else {
		return $(params.ele).attr("event-oid") + "." + $(params.ele).attr("crf-oid") + "." + $(params.ele).attr("group-oid") + "." + $(params.ele).attr("item-oid");
	}
};

Parser.prototype.getRule = function() {
	this.createRule();
	if (this.isValid(this.rule.expression).valid) {
		var rule = Object.create(null);

		rule.name = this.getName();
		rule.study = this.rule.study;
		rule.copied = this.getCopy();
		rule.editing = this.getEditing();
		if (rule.editing) {
			rule.ruleSet = this.getRuleSet();
		}
		rule.targets = this.getTargets();
		rule.actions = this.getActions();
		// Evocation
		rule.di = this.getDataImportExecute();
		rule.dde = this.getDoubleDataEntryExecute();
		rule.ide = this.getInitialDataEntryExecute();
		rule.ae = this.getAdministrativeEditingExecute();
		
		rule.evaluates = this.getEvaluates();
		rule.expression = this.rule.expression.join().replace(/\,/g, " ");
		rule.submission = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
		return rule;
	} else {
		// Ensure one alert is displayed
		if ($(".alert").size() == 0) {
			$(".targettable").find(".panel-body").prepend(createAlert(this.isValid(this.rule.expression).message));
		}
		return false;
	}
};

Parser.prototype.render = function(rule) {
	this.rule.targets = [];
	this.setStudy(rule.study);
	// properties
	if (rule.context) {
		this.setActions({
			context: true,
			actions: rule.actions
		});
	} else {
		this.setName(rule.name);
		this.setCopy(rule.copied);
		this.setEditing(rule.editing);
		this.setRuleSet(rule.ruleSet);
		this.setExpression(rule.expression);
		// Actions
		this.setActions({
			context: false,
			actions: rule.actions
		});
	}
	this.setTargets(rule.targets);
	// Select target item
	parser.selectTarget();
	// Don't select evalutes for show/hide
	if (rule.actions[0].type != 'showHide') {
		this.setEvaluates(rule.evaluates);
	}
	// executions
	this.setDataImportExecute(rule.di);
	this.setDoubleDataEntryExecute(rule.dde);
	this.setInitialDataEntryExecute(rule.ide);
	this.setAdministrativeEditingExecute(rule.ae);
	// Show the delete expression button
	$("#deleteButton").removeClass("hidden");
};

/* ===========================================================================================
 * Determines if what has been designed by the user is valid according to the following rules:
 *
 * - The are not empty drop surface (by empty we mean not edited/dropped on by the user)
 * - A valid rule name has been entered (not empty)
 * - At least one valid rule target has been specified
 * - The user has specified what the rule evaluates to (true or false)
 * - At least one rule invocation target has been specified
 * - At least one action has been specified
 *
 * Arguments [element]:
 * => expression - The rule expression as text
 
 * Returns:
 * - validity of the rule
 * - message if the validation failed
 * ========================================================================================== */
Parser.prototype.isValid = function(expression) {

	var valid = true;
	var message = "";
	if (this.rule.actions.length === 0) {
		valid = false;
		message = "A rule is supposed to fire an action. Please select the action(s) to take if the rule evaluates as intended.";
	}

	if (!$("#ide").is(":checked") && !$("#ae").is(":checked") && !$("#dde").is(":checked") && !$("#dataimport").is(":checked")) {
		valid = false;
		message = "Please specify when the rule should be run";
	}

	if (!$("input[name=ruleInvoke]").is(':checked') && this.getActions()[0].type != 'showHide') {
		valid = false;
		message = "A rule is supposed to evaluate to true or false. Please specify";
	}

	if (this.rule.targets.length === 0) {
		valid = false;
		message = "Please specify a rule target";
	}

	if ($("#ruleName").val().length == 0) {
		valid = false;
		message = "Please specify the rule description";
	}

	if ($("input[action=discrepancy]").is(":checked")) {
		if ($(".discrepancy-properties").find("textarea").val().length <= 0) {
			valid = false;
			message = "A discrepancy action was selected but no discrepancy text has been specified.";
			$(".discrepancy-properties").find("textarea").focus();
		}
	}

	if ($("input[action=email]").is(":checked")) {
		if ($(".email-properties").find("textarea").val().length <= 0) {
			valid = false;
			message = "Please provide a valid email message";
			$(".email-properties").find("textarea").focus();
		}
	}

	if ($("input[action=email]").is(":checked")) {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (!re.test($(".to").val().trim())) {
			valid = false;
			message = "The email address is invalid. Check the email and try again.";
			$(".to").focus();
		}
	}

	if ($("input[action=insert]").is(":checked")) {
		if (this.getInsertAction() && this.getInsertAction().destinations.length < 1) {
			valid = false;
			message = "An insert action was selected but the items to insert values into are not specified.";	
		}
	}

	if ($("input[name='ruleInvoke']").is(":checked")) {
		if (this.getShowHideAction() && this.getShowHideAction().destinations.length < 1) {
			valid = false;
			message = "An show/hide action was selected but the items to show/hide are not specified.";	
		}
	}

	for (var x = 0; x < expression.length; x++) {
		if (expression[x] === "Group or Data" || expression[x] === "Compare or Calculate" || expression[x] === "Condition") {
			var index = expression.indexOf(expression[x]);
			expression.splice(index, 1);
		}
	}

	// It should not be empty
	if (expression.length < 3) {
		valid = false;
		message = "Cannot validate an empty or incomplete expressions.";
	}

	if ($(".invalid").size() > 0) {
		valid = false;
		message = "A rule should only contain valid items in the selected study but the current rule contains invalid items that do not exist in the selected study. Fix the items to proceed";	
	}

	return {
		valid: valid,
		message: message
	};
};

/* ==============================================================================
 * Determines if the a given predicate is an operator according to the CC spec.
 *
 * Arguments [predicate]:
 * => predicate - the predicate to check on
 *
 * Returns true if the predicate is among the allowed math symbols in CC
 * ============================================================================ */
Parser.prototype.isOp = function(predicate) {
	var ops = ['=', '<', '>', '+', 'AND', 'OR', 'NOT'];
	// not equals
	ops.push(unescape(JSON.parse('"\u2260"')));
	// gte
	ops.push(unescape(JSON.parse('"\u2264"')));
	// lte
	ops.push(unescape(JSON.parse('"\u2265"')));
	// not contains
	ops.push(unescape(JSON.parse('"\u2209"')));
	// contains
	ops.push(unescape(JSON.parse('"\u220B"')));
	// divide
	ops.push(unescape(JSON.parse('"\u00F7"')));
	// multiply
	ops.push(unescape(JSON.parse('"\u0078"')));

	var dOps = ['eq', 'ne', 'lt', 'gt', 'lte', 'gte', 'nct', 'ct', 'and', 'or', 'not'];
	return ops.indexOf(predicate) > -1 || dOps.indexOf(predicate) > -1;
};

/* ===========================================================
 * Determines if the a given predicate is an allowed operator
 *
 * Arguments [predicate]:
 * => predicate - the predicate to check on
 *
 * Returns true if the predicate is a math symbols
 * =========================================================== */
Parser.prototype.isAllowedOp = function(predicate) {
	var ops = ['+', '-'];
	// Divide
	ops.push(unescape(JSON.parse('"\u00F7"')));
	return ops.indexOf(predicate) > -1;
};

/* ==============================================================
 * Determines if the a given predicate is a conditional operator
 *
 * Arguments [predicate]:
 * => predicate - the predicate to check on
 *
 * Returns true if the predicate is a 'AND' or 'OR'
 * =========================================================== */
Parser.prototype.isConditionalOp = function(predicate) {
	var ops = ['AND', 'OR'];
	return ops.indexOf(predicate) > -1;
};

/* =====================================================================
 * Converts between design operator representation to CC representation
 *
 * Arguments [predicate]:
 * => predicate - the predicate to convert
 *
 * Returns a CC compartible version of the operator
 * =================================================================== */
Parser.prototype.getOp = function(predicate) {
	if (this.isAllowedOp(predicate)) {
		return predicate;
	} else {
		if (predicate === '=') {
			return "eq";
		} else if (predicate === unescape(JSON.parse('"\u2260"'))) {
			return "ne";
		} else if (predicate === '<') {
			return "lt";
		} else if (predicate === '>') {
			return "gt";
		} else if (predicate === unescape(JSON.parse('"\u2264"'))) {
			return "lte";
		} else if (predicate === unescape(JSON.parse('"\u2265"'))) {
			return "gte";
		} else if (predicate === unescape(JSON.parse('"\u2209"'))) {
			return "nct";
		} else if (predicate === unescape(JSON.parse('"\u220B"'))) {
			return "ct";
		} else if (predicate === unescape(JSON.parse('"\u0078"'))) {
			return "*";
		}
	}
};

Parser.prototype.getLocalOp = function(predicate) {
	if (this.isAllowedOp(predicate)) {
		return predicate;
	} else {
		if (predicate === 'eq') {
			return "=";
		} else if (predicate === 'ne') {
			return unescape(JSON.parse('"\u2260"'));
		} else if (predicate === 'lt') {
			return "<";
		} else if (predicate === 'gt') {
			return ">";
		} else if (predicate === 'lte') {
			return unescape(JSON.parse('"\u2264"'));
		} else if (predicate === 'gte') {
			return unescape(JSON.parse('"\u2265"'));
		} else if (predicate === 'nct') {
			return unescape(JSON.parse('"\u2209"'));
		} else if (predicate === "ct") {
			return unescape(JSON.parse('"\u220B"'));
		} else if (predicate === "*") {
			return "x";
		} else if (predicate === "and") {
			return "AND";
		} else if (predicate === "or") {
			return "OR";
		} 
	}
	return false;
};

/* =====================================================================
 * Checks if the targets exists among the added targets
 *
 * Arguments [target]:
 * => target - the target to check
 *
 * Returns true if target is in added array of targets, false otherwise
 * =================================================================== */
Parser.prototype.isAddedTarget = function(target) {
	for (var x = 0; x < this.rule.targets.length; x++) {
		var tar = this.rule.targets[x];
		if (tar.name === target.name && tar.eventOid === target.eventOid && 
			tar.crfOid === target.crfOid && tar.crfVersionOid === target.crfVersionOid) {
			return true;
		}
	}
};

Parser.prototype.isAddedInsertTarget = function(target) {
	for (var x = 0; x < this.getInsertAction().destinations.length; x++) {
		if (this.getInsertAction().destinations[x].oid === this.constructCRFPath(target)) {
			return true;
		}
	}
};

Parser.prototype.isAddedShowHideTarget = function(target) {
	return this.getShowHideAction() && this.getShowHideAction().destinations.indexOf(this.constructCRFPath(target)) > -1;
};

/* ===========================================================================
 * Finds a CRF item from the original data returns from CC given an item name
 *
 * Arguments [itemName]:
 * => itemName - the itemName of the crf item to extract from a study
 *
 * Returns the returned CRF item
 * ========================================================================= */
Parser.prototype.findItem = function(identifier) {
	var item = null;
	var study = this.extractStudy(this.getStudy());
	study.events.forEach(function(evt) {
		evt.crfs.forEach(function(crf) {
			crf.versions.forEach(function(ver) {
				ver.items.forEach(function(itm) {
					if (itm.oid === identifier || itm.name == identifier) {
						itm.crfOid = crf.oid;
						itm.eventOid = evt.oid;
						itm.crfVersionOid = ver.oid;
						item = itm;
						return;
					}
				});
			});
		});
	});
	return item;
};

Parser.prototype.findStudyItem = function(params) {
	for (var e in params.study.events) {
		var evt = params.study.events[e];
		if (params.evt) {
			if (evt.oid === params.evt) {
				for (var c in evt.crfs) {
					var crf = evt.crfs[c];
					for (var v in crf.versions) {
						var ver = crf.versions[v];
						for (var i in ver.items) {
							var itm = ver.items[i];
							if (itm.name == params.name || itm.oid == params.name) {
								itm.crfOid = crf.oid;
								itm.eventOid = evt.oid;
								itm.crfVersionOid = ver.oid;
								return itm;
							}
						}
					}
				}
			}
		} else {
			var selectedEvent = $("div[id=events]").find(".selected").find("td[oid]").attr("oid");
			if (evt.oid === selectedEvent) {
				for (var c in evt.crfs) {
					var crf = evt.crfs[c];
					for (var v in crf.versions) {
						var ver = crf.versions[v];
						for (var i in ver.items) {
							var itm = ver.items[i];
							if (itm.name == params.name || itm.oid == params.name) {
								itm.crfOid = crf.oid;
								itm.crfVersionOid = ver.oid;
								itm.eventOid = selectedEvent;
								return itm;
							}
						}
					}
				}
			}
		}
	}
};

Parser.prototype.findItemBySelectedProperties = function(params) {
	var item = null;
	var study = this.extractStudy(this.getStudy());
	study.events.forEach(function(evt) {
		if (evt.oid == params.evt) {
			evt.crfs.forEach(function(crf) {
				if (crf.oid == params.crf) {
					crf.versions.forEach(function(ver) {
						if (ver.oid == params.version) {
							ver.items.forEach(function(itm) {
								if (itm.oid === params.identifier || itm.name == params.identifier) {
									itm.crfOid = crf.oid;
									itm.eventOid = evt.oid;
									itm.crfVersionOid = ver.oid;
									item = itm;
									return;
								}
							});
						}
					});
				}
			});
		}
	});
	return item;
};

Parser.prototype.getVersionCRF = function(params) {
	var cf = null;
	params.study.events.forEach(function(evt) {
		evt.crfs.forEach(function(crf) {
			crf.versions.forEach(function(ver) {
				if (ver.oid == params.ver) {
					cf = crf;
				}
			});
		});
	});
	return cf;
};

Parser.prototype.setName = function(name) {
	if (name && name.length > 0) {
		this.rule.name = name;
		$("#ruleName").val(this.rule.name);
	}
};

Parser.prototype.getName = function() {
	return this.rule.name;
};

Parser.prototype.setEvaluates = function(evaluates) {
	if (evaluates) {
		this.rule.evaluates = true;
		$("#evaluateTrue").prop("checked", true);
	} else {
		this.rule.evaluates = false;
		$("#evaluateFalse").prop("checked", true);
	}
};

Parser.prototype.getEvaluates = function() {
	return this.rule.evaluates;
};

Parser.prototype.setInitialDataEntryExecute = function(execute) {
	if (execute) {
		this.rule.ide = true;
		$("#ide").prop("checked", execute);
	} else {
		this.rule.ide = false;
		$("#ide").prop("checked", execute);
	}
};

Parser.prototype.getInitialDataEntryExecute = function() {
	return this.rule.ide ? this.rule.ide : false;
};

Parser.prototype.setDoubleDataEntryExecute = function(execute) {
	if (execute) {
		this.rule.dde = true;
		$("#dde").prop("checked", execute);
	} else {
		this.rule.dde = false;
		$("#dde").prop("checked", execute);
	}
};

Parser.prototype.getDoubleDataEntryExecute = function() {
	return this.rule.dde ? this.rule.dde : false;
};

Parser.prototype.setAdministrativeEditingExecute = function(execute) {
	if (execute) {
		this.rule.ae = true;
		$("#ae").prop("checked", execute);
	} else {
		this.rule.ae = false;
		$("#ae").prop("checked", execute);
	}
};

Parser.prototype.getAdministrativeEditingExecute = function() {
	return this.rule.ae ? this.rule.ae : false;
};

Parser.prototype.setDataImportExecute = function(execute) {
	if (execute) {
		this.rule.di = true;
		$("#dataimport").prop("checked", execute);
	} else {
		this.rule.di = false;
		$("#dataimport").prop("checked", execute);
	}
};

Parser.prototype.getDataImportExecute = function() {
	return this.rule.di ? this.rule.di : false;
};

Parser.prototype.setActions = function(params) {
	if (params.actions.length > 0) {
		for (var x = 0; x < params.actions.length; x++) {
			var action = params.actions[x];
			if (action.type.toLowerCase() === "discrepancy") {
				this.setDiscrepancyAction({
					selected: true,
					message: action.message,
					context: params.context
				});
			} else if (action.type.toLowerCase() === "email") {
				this.setEmailAction({
					selected: true,
					to: action.to,
					message: action.body,
					context: params.context
				});
			} else if (action.type.toLowerCase() === "insert") {
				this.setInsertAction({
					edit: true,
					action: action,
					selected: true,
					context: params.context
				});
			} else if (action.type === "showHide") {
				this.setShowHideAction({
					action: action,
					hide: action.hide,
					show: action.show,
					context: params.context
				});
			} 
		}
	}
};

Parser.prototype.getActions = function() {
	return this.rule.actions;
};

Parser.prototype.getDiscrepancyAction = function() {
	if (this.rule.actions.length > 0) {
		for (var x = 0; x < this.rule.actions.length; x++) {
			var action = this.rule.actions[x];
			if (action.type === "discrepancy") {
				return action;
			}
		}
	}
};

Parser.prototype.setDiscrepancyAction = function(params) {
	if (params) {
		var action = Object.create(null);
		// function to toggle display
		action.render = function(visible) {
			if (visible) {
				$("#message").show();
				$(".dotted-border-lg").show();
				$(".discrepancy-properties").show();
				$("input[action=discrepancy]").prop("checked", params.selected);
				if (!params.context) {
					$(".discrepancy-properties").find("textarea").val(action.message);
				}
			} else {
				$("#message").hide();
				$(".discrepancy-properties").hide();
				$("input[action=discrepancy]").prop("checked", params.selected);
				if ($(".dotted-border-lg").find("div:visible").length === 0) {
					$(".dotted-border-lg").hide();
				}
			}
		};
		if (params.selected) {
			if (this.getActions().length > 0 && this.getDiscrepancyAction()) {
				action = this.getDiscrepancyAction();
			} else {
				this.rule.actions.push(action);
			}
			action.type = "discrepancy";
			action.message = params.message;
		} else {
			if ($.inArray(this.getDiscrepancyAction(), this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(this.getDiscrepancyAction(), this.rule.actions), 1);
			}
		}
		action.render(params.selected);
	}
};

Parser.prototype.getEmailAction = function() {
	if (this.rule.actions.length > 0) {
		for (var x = 0; x < this.rule.actions.length; x++) {
			var action = this.rule.actions[x];
			if (action.type === "email") {
				return action;
			}
		}
	}
};

Parser.prototype.setEmailAction = function(params) {
	if (params) {
		var action = Object.create(null);
		// function to toggle display
		action.render = function(visible) {
			if (visible) {
				$(".dotted-border-lg").show();
				// Action controls
				$(".email-properties").show();
				$(".to").show();
				$(".body").show();
				$("input[action=email]").prop("checked", params.selected);
				if (!params.context) {
					$(".to").val(action.to);
					$(".body").val(action.body);
				}
			} else {
				$(".email-properties").hide();
				$(".to").val("");
				$(".body").val("");
				$("input[action=email]").prop("checked", params.selected);
				if ($(".dotted-border-lg").find("div:visible").length === 0) {
					$(".dotted-border-lg").hide();
				}
			}
		};
		if (params.selected && !params.context) {
			if (this.getActions().length > 0 && this.getEmailAction()) {
				action = this.getEmailAction();
			} else {
				this.rule.actions.push(action);
			}
			action.type = "email";
			action.to = params.to;
			action.body = params.message;
		} else {
			if ($.inArray(this.getEmailAction(), this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(this.getEmailAction(), this.rule.actions), 1);
			}
		}
		action.render(params.selected);
	}
};

Parser.prototype.getInsertAction = function() {
	if (this.rule.actions.length > 0) {
		for (var x = 0; x < this.rule.actions.length; x++) {
			var action = this.rule.actions[x];
			if (action.type === "insert") {
				return action;
			}
		}
	}
};

Parser.prototype.setInsertAction = function(params) {
	if (params) {
		var action = Object.create(null);
		// function to toggle display
		action.render = function(visible) {
			if (visible) {
				$(".dotted-border-lg").show();
				$(".insert-properties").show();
				$("input[action=insert]").prop("checked", params.selected);
			} else {
				// Update UI
				$(".insert-properties").hide();
				$("input[action=insert]").prop("checked", params.selected);
				if ($(".dotted-border-lg").find("div:visible").length === 0) {
					$(".dotted-border-lg").hide();
				}
				var div = $(".insert-properties").find(".row").first().clone();
				div.find(".item").val("");
				div.find(".value").val("");
				div.find(".value").attr("type", "text");
				div.find(".value").removeClass("invalid");
				$(".insert-properties").find(".row").remove();
				createDroppable({
					element: div.find(".item"),
					accept: "div[id='items'] td"
				});
				createDroppable({
					element: div.find(".value"),
					accept: "div[id='data'] p, div[id='items'] td"
				});
				$(".insert-properties").append(div);
			}
		};
		if (params.selected) {
			if (this.getActions().length > 0 && this.getInsertAction()) {
				action = this.getInsertAction();
			} else {
				this.rule.actions.push(action);
			}
			if (params.edit) {
				action.message = "";
				action.type = "insert";
				action.destinations = params.action.destinations;
				// Add action targets
				if (!params.context) {
					this.setDestinations(action.destinations);
				}
			} else {
				action.message = "";
				action.type = "insert";
				action.destinations = [];
			}
		} else {
			if ($.inArray(this.getInsertAction(), this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(this.getInsertAction(), this.rule.actions), 1);
			}
		}
		action.render(params.selected);
	}
};

Parser.prototype.getShowHideAction = function() {
	if (this.rule.actions.length > 0) {
		for (var x = 0; x < this.rule.actions.length; x++) {
			var action = this.rule.actions[x];
			if (action.type === "showHide") {
				return action;
			}
		}
	}
};

Parser.prototype.setShowHideActionMessage = function(message) {
	this.getShowHideAction().message = message;	
};

Parser.prototype.setShowHideAction = function(params) {
	if (params) {
		var action = Object.create(null);
		// function to toggle display
		action.render = function(visible) {
			if (visible.show || visible.hide) {
				$(".dotted-border-lg").show();
				$(".show-hide-properties").show();
				if (!params.context) {
					$(".show-hide-properties").find("textarea").val(action.message);
					$(".show-hide-properties").find("textarea").focus();
				}
				if (visible.hide) {
					$(".show-hide-properties").find("textarea").hide();
					$(".show-hide-properties").find("textarea").val("");
					$(".show-hide-properties").find(".control-label").first().hide();
				} else {
					$(".show-hide-properties").find("textarea").show();
					$(".show-hide-properties").find(".control-label").show();
				}
			} else {
				// Update UI
				$(".show-hide-properties").hide();
				if ($(".dotted-border-lg").find("div:visible").length === 0) {
					$(".dotted-border-lg").hide();
				}
				$(".show-hide-properties").find("textarea").val("");
				var div = $(".show-hide-properties").find(".input-group").first().clone();
				div.find(".dest").val("");
				div.find(".dest").attr("type", "text");
				div.find(".dest").removeClass("invalid");
				$(".show-hide-properties").find(".input-group").remove();
				createDroppable({
					element: div.find(".dest"),
					accept: "div[id='items'] td"
				});

				$(".show-hide-properties").find(".col-md-6").append(div);
			}
			$("input[action=show]").prop("checked", visible.show);
			$("input[action=hide]").prop("checked", visible.hide);
		};
		if (params.hide || params.show) {
			if (this.getActions().length > 0 && this.getShowHideAction()) {
				action = this.getShowHideAction();
				action.type = "showHide";
				action.hide = params.hide;
				action.show = params.show;
				action.message = action.message;
				action.destinations = action.destinations;
			} else {
				action.message = "";
				action.type = "showHide";
				action.destinations = [];
				action.hide = params.hide;
				action.show = params.show;
				this.rule.actions.push(action);
			}
			if (params.action && !params.context) {
				action.type = "showHide";
				action.hide = params.hide;
				action.show = params.show;
				action.message = params.action.message;
				action.destinations = params.action.destinations;
				// Add action targets
				this.setShowHideDestinations(action.destinations);
			}
		} else {
			if ($.inArray(this.getShowHideAction(), this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(this.getShowHideAction(), this.rule.actions), 1);
			}
		}
		action.render(params);
	}
};

Parser.prototype.setDestinationValue = function(params) {
	var act = this.getInsertAction();
	var row = $(".row[id=" + params.id + "]");
	if (act) {
		if (act.destinations.length > 0) {
			for (var x = 0; x < act.destinations.length; x++) {
				var dest = act.destinations[x];
				if (parseInt(dest.id) === parseInt(params.id) && !dest.item) {
					dest.item = false;
					dest.value = params.value;
					if (!row.closest(".row").next().is(".row")) {
						this.addNewInsertActionInputs();
					}
					row.find(".bordered").removeClass("bordered");
				}
			}
		} else {
			var dest = Object.create(null);
			dest.id = params.id;
			dest.value = params.value;
			act.destinations.push(dest);
		}
		act.render(act);
	}
};

Parser.prototype.addNewInsertActionInputs = function() {

	var div = $(".insert-properties").find(".row").first().clone();
	div.attr("id", $(".insert-properties").find(".row").size() + 1);
	div.find("label").hide();

	var input = div.find(".item");
	input.val("");
	input.removeClass("bordered");

	var inputVal = div.find(".value");
	inputVal.val("");
	inputVal.removeClass("bordered");
	inputVal.removeAttr("type");
	inputVal.blur(function() {
		parser.setDestinationValue({
			value: $(this).val(),
			id: $(this).parents(".row").attr("id")
		});
	});
	createDroppable({
		element: input,
		accept: "div[id='items'] td"
	});
	createDroppable({
		element: inputVal,
		accept: "div[id='data'] p, div[id='items'] td"
	});

	$(".insert-properties").append(div);
};

Parser.prototype.setDestinations = function(dests) {
	if (dests.length > 0) {
		// Remove labels
		$(".insert-properties").find("label").hide();
		for (var x = 0; x < dests.length; x++) {
			var dest = dests[x];
			var div = $(".insert-properties").find(".row").first().clone();
			var item = this.getItem(dest.oid);
			var input = div.find(".item");
			div.attr("id", dest.id);
			input.attr("item-oid", item.oid);
			input.attr("group-oid", item.group);
			input.attr("crf-oid", item.crfOid);
			input.attr("event-oid", item.eventOid);
			input.attr("version-oid", item.crfVersionOid);
			input.attr("study-oid", this.extractStudy(this.getStudy()).oid);
			input.val(item.name);
			input.css('font-weight', 'bold');

			var inputVal = div.find(".value");
			inputVal.css('font-weight', 'bold');
			if (this.findItem(dest.value)) {
				var item = this.getItem(dest.value);
				inputVal.attr("item-oid", item.oid);
				inputVal.attr("group-oid", item.group);
				inputVal.attr("crf-oid", item.crfOid);
				inputVal.attr("event-oid", item.eventOid);
				inputVal.attr("version-oid", item.crfVersionOid);
				inputVal.attr("study-oid", this.extractStudy(this.getStudy()).oid);
			}
			inputVal.val(this.findItem(dest.value) ? this.findItem(dest.value).name : dest.value);
			inputVal.blur(function() {
				parser.setDestinationValue({
					value: $(this).val(),
					id: $(this).parents(".row").attr("id")
				});
			});
			createDroppable({
				element: input,
				accept: "div[id='data'] p, div[id='items'] td"
			});
			createDroppable({
				element: inputVal,
				accept: "div[id='data'] p, div[id='items'] td"
			});
			if (x === 0) {
				div.find("label").show();
				$(".insert-properties").find(".row").before(div);
			} else {
				div.find("label").hide();
				$(".insert-properties").find(".row").last().before(div);
			}
		}
	}
};

Parser.prototype.setShowHideDestinations = function(dests) {
	if (dests.length > 0) {
		var div = $(".space-left-neg > .input-group").first();
		for (var x = 0; x < dests.length; x++) {
			var cloned = div.clone();
			createDroppable({
				accept: "div[id='items'] td",
				element: cloned.find(".dest")
			});
			var item = this.getItem(dests[x]);
			cloned.find("input").val(item.name);
			cloned.find(".dest").attr("item-oid", item.oid);
			cloned.find(".dest").attr("group-oid", item.group);
			cloned.find(".dest").attr("crf-oid", item.crfOid);
			cloned.find(".dest").attr("event-oid", item.eventOid);
			cloned.find(".dest").attr("version-oid", item.crfVersionOid);
			cloned.find(".dest").attr("study-oid", this.extractStudy(this.getStudy()).oid);
			cloned.find("input").css('font-weight', 'bold');
			x === 0 ? div.before(cloned) : $(".space-left-neg > .input-group").last().before(cloned);
		}
	}
};

Parser.prototype.setInsertActionMessage = function(message) {
	this.getInsertAction().message = message;
};

Parser.prototype.setExpression = function(expression) {
	if (expression instanceof Array) {
		this.rule.expression = expression;
		var currDroppable = $(".dotted-border");
		for (var e = 0; e < expression.length; e++) {
			var itm = this.getItem(expression[e]);
			if (e === 0) {
				if (itm) {
					var preds = expression[e].split(".");
					if (preds.length == 4) {
						$(".dotted-border").attr("event-oid", preds[3]);
						$(".dotted-border").attr("crf-oid", preds[preds.length - 3]);
						$(".dotted-border").attr("item-oid", preds[preds.length - 1]);
						$(".dotted-border").attr("group-oid", preds[preds.length - 2]);
					} else if (preds.length == 3) {
						$(".dotted-border").attr("crf-oid", preds[0]);
						$(".dotted-border").attr("item-oid", preds[2]);
						$(".dotted-border").attr("group-oid", preds[1]);
						$(".dotted-border").attr("event-oid", itm.eventOid);
					} else {
						$(".dotted-border").attr("item-oid", itm.oid);
						$(".dotted-border").attr("crf-oid", itm.crfOid);
						$(".dotted-border").attr("group-oid", itm.group);
						$(".dotted-border").attr("event-oid", itm.eventOid);
					}
					$(".dotted-border").attr("version-oid", itm.crfVersionOid);
					$(".dotted-border").attr("study-oid", this.extractStudy(this.getStudy()).oid);
					$(".dotted-border").text(itm.name);
				} else {
					$(".dotted-border").text(expression[e]);
				}
			} else {
				var predicate = expression[e];
				if (parser.isConditionalOp(predicate.toUpperCase())) {
					var droppable = createConditionDroppable();
					droppable.text(predicate);
					currDroppable.after(droppable);
					currDroppable = droppable;
				} else if (parser.isOp(predicate)) {
					var droppable = createSymbolDroppable();
					droppable.text(predicate);
					currDroppable.after(droppable);
					currDroppable = droppable;
				} else {
					var droppable = createStartExpressionDroppable();
					if (itm) {
						var itm = this.getItem(expression[e]);
						var preds = expression[e].split(".");
						if (preds.length == 4) {
							droppable.attr("event-oid", preds[3]);
							droppable.attr("crf-oid", preds[preds.length - 3]);
							droppable.attr("item-oid", preds[preds.length - 1]);
							droppable.attr("group-oid", preds[preds.length - 2]);
						} else if (preds.length == 3) {
							droppable.attr("crf-oid", preds[0]);
							droppable.attr("item-oid", preds[2]);
							droppable.attr("group-oid", preds[1]);
							droppable.attr("event-oid", itm.eventOid);
						} else {
							droppable.attr("item-oid", itm.oid);
							droppable.attr("group-oid", itm.group);
							droppable.attr("crf-oid", itm.crfOid);
							droppable.attr("event-oid", itm.eventOid);
						} 
						droppable.attr("version-oid", itm.crfVersionOid);
						droppable.attr("study-oid", this.extractStudy(this.getStudy()).oid);
						droppable.text(itm.name);
					} else {
						if (this.isDateValue(expression[e], true)) {
							var value = expression[e];
							var date = Date.parse(value);
							if (date != null) {
								value = date.toString(getCookie('ccDateFormat'));
							}
							droppable.text(value);
						} else {
							droppable.text(expression[e]);	
						}
					}
					currDroppable.after(droppable);
					currDroppable = droppable;
				}
			}
			currDroppable.addClass("bordered");
			currDroppable.removeClass("init");
			currDroppable.css('font-weight', 'bold');
		}
	} else if (typeof expression === "string") {
		var rawExpression = [];
		// The regex skips quoted strings in expression
		var expr = expression.split(/(\()|(?=\))|\s+(?!\w+(\s+\w+)*?")/g).filter(function(x) {
			if (x) {
				return x;
			}
		});
		for (var x = 0; x < expr.length; x++) {
			var itm = this.getItem(expr[x]);
			if (itm) {
				rawExpression.push(expr[x]);
			} else {
				var pred = this.isOp(expr[x]) ? this.getLocalOp(expr[x]) : expr[x];
				rawExpression.push(pred);
			}
		}
		this.setExpression(rawExpression);
	}
	// Make them sortable
	if (!$(".sortable").is(".ui-sortable")) {
		createSortable();
	}
};

Parser.prototype.setTargets = function(targets) {
	if (targets.length > 0) {
		var targetDiv = $(".parent-target");
		for (var x = 0; x < targets.length; x++) {
			var tar = this.extractTarget({
				target: targets[x],
				name: targets[x].name
			});
			var div = targetDiv.clone();
			div.find(".target").val("");
			createDroppable({
				element: div.find(".target"),
				accept: "div[id='items'] td"
			});
			div.find(".target").val(tar.name);
			// Item attributes
			div.find(".target").css('font-weight', 'bold');
			div.find(".target").attr("item-oid", tar.oid);
			div.find(".target").attr("group-oid", tar.group);
			div.find(".target").attr("crf-oid", tar.crf);
			div.find(".target").attr("event-oid", tar.evt);
			div.find(".target").attr("version-oid", tar.version);
			div.find(".target").attr("study-oid", this.extractStudy(this.getStudy()).oid);
			createToolTip({
				element: div.find(".eventify"),
				title: "Click to bind the target to only the event."
			});
			createToolTip({
				element: div.find(".versionify"),
				title: "Click to bind the target to the crf version."
			});
			createToolTip({
				element: div.find(".linefy"),
				title: "Specify the line number in the repeating group to which the rule will apply"
			});
			x === 0 ? targetDiv.before(div) : $(".parent-target").last().before(div);
			div.after($('.opt:last').clone());
			// Eventify
			var eventDuplex = this.isDuplicated({
				type: "eventOid",
				name: tar.name
			});
			if (!eventDuplex) {
				tar.eventify = false;
				div.find(".eventify").parent().removeClass("hidden");
				if (targets[x].eventify) {
					tar.eventify = true;
					div.find(".eventify").parent().removeClass("hidden");
					div.find(".eventify").prop("checked", tar.eventify);
				}
			}
			// Versionify
			var versionDuplex = this.isDuplicated({
				name: tar.name,
				type: "crfVersionOid"
			});
			if (!versionDuplex) {
				tar.versionify = false;
				div.find(".versionify").parent().removeClass("hidden");
				if (targets[x].versionify) {
					tar.versionify = true;
					div.find(".versionify").parent().removeClass("hidden");
					div.find(".versionify").prop("checked", tar.versionify);
				}
			}
			// linefy
			if (targets[x].linefy) {
				tar.linefy = true;
				tar.line = targets[x].line;
				div.find(".linefy").removeClass("hidden");
				div.find(".linefy").val(tar.line);
				div.find(".linefy").siblings(".target").css("width", "89%");
			}
			this.rule.targets.push(tar);
		}
	}
};

Parser.prototype.extractTarget = function(params) {
	var target = Object.create(null);
	var tt = this.getItem(params.name);
	target.oid = tt.oid;
	target.name = tt.name;
	target.crf = params.target.crf;
	target.group = params.target.group;
	target.evt = params.target.eventify ? params.target.evt : tt.eventOid;
	target.version = params.target.versionify ? params.target.version : tt.crfVersionOid;
	if (params.target.versionify) {
		target.crf = this.getVersionCRF({
			ver: target.version,
			study: this.extractStudy(this.getStudy())
		}).oid;
	}	
	return target;
};

Parser.prototype.getTargets = function() {
	return this.rule.targets;
};

Parser.prototype.selectTarget = function() {
	var study = this.extractStudy(this.rule.study);
	if (this.rule.targets) {
		var row = $("td[oid=" + study.oid + "]");
		if (row) {
			$("tr.selected").each(function() {
				$(this).removeClass("selected");
			});
			row.parent().click();
			// Event
			this.recursiveSelect({
				click: true,
				type: "events",
				candidate: this.rule.targets[0].evt
			});
			// CRF
			this.recursiveSelect({
				type: "crfs",
				click: true,
				candidate: this.rule.targets[0].crf
			});
			// CRF version
			this.recursiveSelect({
				click: true,
				type: "versions",
				candidate: this.rule.targets[0].version
			});
			// Item
			this.recursiveSelect({
				click: false,
				type: "items",
				candidate: this.rule.targets[0].oid
			});
		}
	}
};

Parser.prototype.deleteTarget = function(target) {
	if ($(target).siblings("input").is(".target")) {
		for (var x = 0; x < this.rule.targets.length; x++) {
			var index = this.rule.targets.indexOf(this.rule.targets[x]);
			if (index > -1 && this.rule.targets[x].name === $(target).siblings("input").val()) {
				this.rule.targets.splice(index, 1);
				$(target).parent().next().remove();
				$(target).parent().remove();
				break;
			}
		}
	} else if ($(target).siblings("input").is(".value")) {
		var act = this.getInsertAction();
		var oid = this.constructCRFPath($(target).closest(".row").find(".item").val());
		for (var x = 0; x < act.destinations.length; x++) {
			var dest = act.destinations[x];
			if (dest.oid === oid) {
				act.destinations.splice(x, 1);
				if ($(".insert-properties").find(".row").size() === 1) { 
					$(target).closest(".row").find(".item").val("");
					$(target).closest(".row").find(".value").val("");
				} else {
					$(target).closest(".row").remove();
				}
				if ($(".insert-properties").find("label:visible").size() === 0) {
					$(".insert-properties").find(".row:first").find("label").map(function() {
						$(this).show();
					});
				}
			}
		}
	} else {
		if (this.getShowHideAction() && this.getShowHideAction().destinations.length > 0) {
			var itemExpression = $(target).siblings(".dest").attr("crf-oid") + "." + $(target).siblings(".dest").attr("group-oid") + "." + $(target).siblings(".dest").attr("item-oid");
			var index = this.getShowHideAction().destinations.indexOf(itemExpression);
			if (index > -1) {
				this.getShowHideAction().destinations.splice(index, 1);
				$(target).parent().remove();
			}	
		}
	}
};

/* =================================================================
 * Validates the designed rule using the CC Test Rule servlet
 *
 * Arguments [params]:
 * => expression - the expression in text format
 * => targets - the rule targets
 * => evaluateTo - What the rule should evaluate to
 * ============================================================= */
Parser.prototype.validate = function() {
	var rule = this.getRule();
	$("body").append(createLoader());
	if (rule) {
		$.ajax({
			type: "POST",
			data: {
				rs: true,
				rule: rule.expression,
				target: rule.targets[0].name,
				testRuleActions: rule.evaluateTo
			},
			url: rule.study ? rule.submission + "/TestRule?action=validate&study=" + rule.study : rule.submission + "/TestRule?action=validate",
			success: function(response) {
				rule.validation = response;
				parser.displayValidationResults(rule);
				removeLoader();
			},
			error: function(response) {
				handleErrorResponse({
					response: response
				});
			}
		});
	}
	removeLoader();
};

/* =================================================================
 * Displays the validation results from testing a rule in CC.
 *
 * Note that this function loads the validation page to display the 
 * results.
 *
 * Arguments [params]:
 * => expression - the expression in text format
 * => targets - the rule targets
 * => evaluateTo - What the rule should evaluate to
 * ============================================================= */
Parser.prototype.displayValidationResults = function(rule) {
	$.ajax({
		type: "POST",
		data: {
			action: "save",
			rule: JSON.stringify(rule)
		},
		url: rule.submission + "/rules?action=validate",
		success: function(response) {
			if (response) {
				// To be used in validation
				rule.xml = response;
				sessionStorage.setItem("rule", JSON.stringify(rule));
				// launch validation window
				window.open("validation.html", '_self');
			}
		},
		error: function(response) {
			handleErrorResponse({
				response: response
			});
		}
	});
};

Parser.prototype.getParameter = function(name) {
	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))|| null;
};

Parser.prototype.getParameterValue = function(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
	var results = regex.exec(window.location.href);
	if (results == null) {
		return "";
	} else {
		return results[1];
	}
};

Parser.prototype.extractEventNameFromExpression = function(predicate) {
	return new RegExp("^(\\w+)(?=\.)").exec(predicate)[1];
};

Parser.prototype.extractItemOIDFromExpression = function(predicate) {
	return new RegExp("\.([^\.]+)$").exec(predicate)[1];
};

Parser.prototype.extractStudy = function(id) {
	var studies = JSON.parse(sessionStorage.getItem("studies"));
	for (var x = 0; x < studies.length; x++) {
		if (studies[x].id === id) {
			return studies[x];
		}
	}
};

Parser.prototype.constructCRFPath = function(itemName) {
	var item = this.getItem(itemName);
	return item.crfOid + "." + item.group + "." + item.oid;
};

Parser.prototype.constructCRFVersionPath = function(target) {
	return target.versionify ? target.version + "." + target.group + "." + target.oid : this.constructCRFPath(target.name);
};

Parser.prototype.constructEventPath = function(itemName) {
	var item = this.getItem(itemName);
	return item.eventOid + "." + this.constructCRFPath(itemName);
};

Parser.prototype.constructRepeatItemPath = function(target) {
	// event oid?
	var name = target.eventify ? target.evt : "";
	// version oid?
	if (target.versionify) {
		name = name.length > 0 ? name + "." + target.version : target.version;
	} else {
		name = name.length > 0 ? name + "." + target.crf : target.crf;
	}
	return name + "." + target.group + "[" + target.line + "]" + "." + target.oid;
};

Parser.prototype.eventify = function(targetEvent) {
	var targetName = $(targetEvent).parent().siblings(".target").val();
	for (var x = 0; x < this.rule.targets.length; x++) {
		var tar = this.rule.targets[x];
		if (tar.name === targetName) {
			tar.eventify = $(targetEvent).is(":checked");
			break;
		}
	}
};

Parser.prototype.isEventified = function(expression) {
	return expression.slice(0, "SE_".length) == "SE_";
};

Parser.prototype.versionify = function(targetEvent) {
	var targetName = $(targetEvent).parent().siblings(".target").val();
	for (var x = 0; x < this.rule.targets.length; x++) {
		var tar = this.rule.targets[x];
		if (tar.name === targetName) {
			tar.versionify = $(targetEvent).is(":checked");
			break;
		}
	}
};

Parser.prototype.linefy = function(targetEvent) {
	var targetName = $(targetEvent).siblings(".target").val();
	for (var x = 0; x < this.rule.targets.length; x++) {
		var tar = this.rule.targets[x];
		if (tar.name === targetName) {
			if ($(targetEvent).val().length > 0) {
				tar.linefy = true;
				tar.line = $(targetEvent).val();
			} else {
				tar.linefy = false;
			}
			break;
		}
	}
};
// This function is a bit involved - consider refactoring
Parser.prototype.recursiveSelect = function(params) {
	var next = $("div[id=" + params.type + "]").find("a:contains(" + unescape(JSON.parse('"\u00BB\u00BB"')) + ")");
	if ($("td[oid=" + params.candidate + "]").length > 0) {
		$("td[oid=" + params.candidate + "]").parent().addClass("selected");
	} else {
		if (next.length > 0) {
			next[0].click();
			if ($("td[oid=" + params.candidate + "]").length == 0) {
				if (next[0]) {
					next[0].click();
				}
			} else {
				$("td[oid=" + params.candidate + "]").parent().addClass("selected");
			}
		}
	}
	if (params.click) {
		$("td[oid=" + params.candidate + "]").parent().trigger('click', [{x:false}]);
	}
};

Parser.prototype.getItem = function(expression) {
	if (expression.indexOf(".") == -1) {
		return this.findItem(expression);
	} else if (this.isEventified(expression)) {
		return this.findStudyItem({
			study: this.extractStudy(this.rule.study),
			name: this.extractItemOIDFromExpression(expression),
			evt: this.extractEventNameFromExpression(expression)
		});
	} else {
		return this.findItem(this.extractItemOIDFromExpression(expression));
	}
};

Parser.prototype.resetActions = function(target) {
	function isShowHideTarget(item) {
		return $(item).attr("action") == 'show' || $(item).attr("action") == 'hide';
	}
	this.rule.actions = [];
	$(".dotted-border-lg").find("input, textarea").each(function() {
		if (!isShowHideTarget(target)) {
			$(this).val("");
			$(".space-left-neg > .input-group").not(":first").remove();
		}
	});
	$(".dotted-border-lg").hide();
	$(".dotted-border-lg").children().hide();
	$("input[name='action']").not(target).attr("previous-state", false);
};

Parser.prototype.isDateValue = function(val, expressionFormat) {
    var date = Date.parse(val);
    return date != null && date.toString(expressionFormat ? "yyyy-MM-dd" : getCookie('ccDateFormat')) == val;
};