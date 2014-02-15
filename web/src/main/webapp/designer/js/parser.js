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

function Parser() {

	this.rule = Object.create(null);

	this.rule.targets = [];
	this.rule.actions = [];	
}

/* ===========================================================================
 * Validates what the next droppable should be given a droppable with a type
 *
 * Arguments [currentElement]:
 * => currentElement - The current droppable
 *
 * Return the type for the expected next element.
 * ========================================================================== */
Parser.prototype.validateNext = function(currentElement) {

	var nextElement = "INVALID";

	// The second group positionally identifies an element
	var className = currentElement.attr('class').split(/\s+/)[1];

	switch (className) {

		case "cal":
		case "comp":
			nextElement = "DATA"
			break;
		case "data":
			nextElement = "COMPUTE";
			break;
		case "group":
			nextElement = this.determineNext(currentElement);
			break;
	}

	return nextElement;
}

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

		return "ANY"

		// RPAREN
	} else {

		return "EVAL"
	}
}

/* ==============================================================================
 * This function creates the next valid droppable surface based off the existing
 * surface.
 *
 * Arguments [params]:
 * => element - The current droppable
 * => ui - The draggable that has been dropped
 * ============================================================================ */
Parser.prototype.createNextDroppable = function(params) {

	var __NEXT__ = this.validateNext(params.element);

	if (__NEXT__ === "ANY") {

		var RPAREN = createRPARENDiv();
		var dataPredicate = createStartExpressionDroppable();

		dataPredicate.text("Group or Data");

		params.element.after(dataPredicate);

		dataPredicate.after(RPAREN);

		createPopover(RPAREN);

		createPopover(dataPredicate);

	} else if (params.element.is("input")) {

		if (!this.isAlreadyAddedTarget(params.ui.draggable.text())) {

			this.rule.targets.push(params.ui.draggable.text());

			var newInput = params.element.clone();
			createDroppable({
				element: newInput,
				accept: "div[id='items'] td"
			})

			// create a new input 
			if (!params.element.val()) {

				params.element.after(newInput);
				createDroppable({
					element: newInput,
					accept: "div[id='items'] td"
				})
			} 

			newInput.focus();
			params.element.val(params.ui.draggable.text());
		}

	} else {

		if (params.element.is(".comp")) {

			var dataPredicate = createStartExpressionDroppable();

			// Avoid creating unnecessary evaluation/data/crf item/group boxes
			if (params.element.next().size() == 0 || params.element.next().is(".pull-right") || params.element.next().is(".group")) {

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

			var droppable = createStartExpressionDroppable();
			params.element.after(droppable);

			createPopover(droppable);

		} else {

			if (!params.element.next().is(".comp")) {

				var droppable = createSymbolDroppable();
				params.element.after(droppable);

				createPopover(droppable);
			}
		}
	}
}

/* ======================================================
 * Determines if the element is of type 'text' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'text'
 * ====================================================== */
Parser.prototype.isText = function(element) {

	return element.attr("id") === "text"
}

/* ======================================================
 * Determines if the element is of type 'date' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'date'
 * ====================================================== */
Parser.prototype.isDate = function(element) {

	return element.attr("id") === "date"
}

/* ======================================================
 * Determines if the element is of type 'empty' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'empty'
 * ====================================================== */
Parser.prototype.isEmpty = function(element) {

	return element.attr("id") === "empty"
}

/* ======================================================
 * Determines if the element is of type 'number' on the UI
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'number'
 * ====================================================== */
Parser.prototype.isNumber = function(element) {

	return element.attr("id") === "number"
}

/* ==============================================================================
 * Determines if the drop surface is of type that accepts conditional draggables
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element has id === 'evalSurface'
 * ============================================================================ */
Parser.prototype.isConditionalSurface = function(element) {

	return element.attr("id") === "evalSurface"
}

/* ====================================================
 * Determines if the element is a CRF item
 *
 * Arguments [element]:
 * => element - the element to check on
 *
 * Returns true if the element is <td> and a draggable
 * ==================================================== */
Parser.prototype.isCRFItem = function(element) {

	return element.prop("tagName").toLowerCase() === "td" && element.is(".ui-draggable")
}

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

	var expression = [];
	var dottedBorders = $(".dotted-border");

	for (var x = 0; x < dottedBorders.size(); x++) {

		var exprItem = $(dottedBorders[x]).text();

		if (this.isOp(exprItem)) {

			if (this.isConditionalOp(exprItem)) {
				exprItem = exprItem.toLowerCase();
			} else {
				exprItem = this.getOp(exprItem)
			}
		}

		var item = this.findItem($(dottedBorders[x]).text());

		if (item) {

			// Add form oid and group oid
			exprItem = item.formOid + "." + item.group + "." + item.oid;
		}

		expression.push(exprItem);
	}

	this.rule.expression = expression;

	if (this.isValid(expression).valid) {

		var tt = []
		for (var x = 0; x < this.rule.targets.length; x++) {

			var obj = this.findItem(this.rule.targets[x])
			var itemOid = obj !== undefined ? obj : $(".target").val();

			tt.push(itemOid.oid)
		}

		this.rule.targets = tt;
	} 
}

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

		valid = false,
		message = "A rule is supposed to fire an action. Please select the action(s) to take if the rule evaluates as intended."
	}

	if (!$("#ide").is(":checked") && !$("#ae").is(":checked") && !$("#dde").is(":checked") && !$("#dataImport").is(":checked")) {

		valid = false,
		message = "Please specify when the rule should be run"
	}

	if ($("input[name=ruleInvoke]:checked").length == 0) {

		valid = false,
		message = "A rule is supposed to evaluate to true or false. Please specify"
	}

	if (this.rule.targets.length === 0) {

		valid = false,
		message = "Please specify a rule target"
	}

	if ($("#ruleName").val().length == 0) {

		valid = false,
		message = "Please specify the rule description"
	}

	for (var x = 0; x < expression.length; x++) {

		if (expression[x] === "Group or Data" || expression[x] === "Compare or Calculate" || expression[x] === "Evaluate") {

			valid = false,
			message = "The expression is invalid. Please fill in or delete unused boxes in the expression."
		}
	}

	if ($("#chkEmail").is(":checked")) {

		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

		if (!re.test($("#toField").val().trim())) {

			valid = false,
			message = "The email address is invalid. Check the email and try again."
		}
	}

	return {

		valid: valid,
		message: message
	}
}

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
	ops.push(unescape(JSON.parse('"\u2260"')))

	// gte
	ops.push(unescape(JSON.parse('"\u2264"')))

	// lte
	ops.push(unescape(JSON.parse('"\u2265"')))

	// not contains
	ops.push(unescape(JSON.parse('"\u2209"')))

	// contains
	ops.push(unescape(JSON.parse('"\u220B"')))

	// divide
	ops.push(unescape(JSON.parse('"\u00F7"')))

	// multiply
	ops.push(unescape(JSON.parse('"\u0078"')))

	var dOps = ['eq', 'ne', 'lt', 'gt', 'lte', 'gte', 'nct', 'ct', 'and', 'or', 'not'];

	return ops.indexOf(predicate) > -1 || dOps.indexOf(predicate) > -1;
}

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
	ops.push(unescape(JSON.parse('"\u00F7"')))

	return ops.indexOf(predicate) > -1;
}

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
}

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
}

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
			return unescape(JSON.parse('"\u2265"'));
		} else if (predicate === 'gte') {
			return unescape(JSON.parse('"\u2264"'));
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
}

/* =====================================================================
 * Checks if the targets exists among the added targets
 *
 * Arguments [target]:
 * => target - the target to check
 *
 * Returns true if target is in added array of targets, false otherwise
 * =================================================================== */
Parser.prototype.isAlreadyAddedTarget = function(target) {

	return this.rule.targets.indexOf(target) > -1;
}

/* ===========================================================================
 * Finds a CRF item from the original data returns from CC given an item name
 *
 * Arguments [itemName]:
 * => itemName - the itemName of the crf item to extract from a study
 *
 * Returns the returned CRF item
 * ========================================================================= */
Parser.prototype.findItem = function(itemName) {

	var storedStudies = JSON.parse(sessionStorage.getItem("studies"));

	for (var x in storedStudies) {

		var study = storedStudies[x];

		for (var e in study.events) {

			var event = study.events[e];

			for (var c in event.crfs) {

				var crf = event.crfs[c];

				for (var v in crf.versions) {

					var ver = crf.versions[v];

					for (var i in ver.items) {

						var itm = ver.items[i];

						if (itm.name === itemName) {

							itm.formOid = crf.oid;

							return itm;
						}
					}
				}
			}
		}
	}
}

/* ==========================================================================
 * Finds a CRF item from the original data returns from CC given an item oid
 *
 * Arguments [itemOID]:
 * => itemOID - the itemOID of the crf item to extract from a study
 *
 * Returns the returned CRF item
 * ======================================================================== */
Parser.prototype.findItemName = function(itemOID) {

	var storedStudies = JSON.parse(sessionStorage.getItem("studies"));

	for (var x in storedStudies) {

		var study = storedStudies[x];

		for (var e in study.events) {

			var event = study.events[e];

			for (var c in event.crfs) {

				var crf = event.crfs[c];

				for (var v in crf.versions) {

					var ver = crf.versions[v];

					for (var i in ver.items) {

						var itm = ver.items[i];

						if (itm.oid === itemOID) {

							itm.formOid = crf.oid;

							return itm;
						}
					}
				}
			}
		}
	}
}

Parser.prototype.setName = function(name) {

	if (name && name.length > 0) {

		this.rule.name = name;

		$("#ruleName").val(this.rule.name);
	}
}

Parser.prototype.getName = function() {
	return this.rule.name;
}

Parser.prototype.setTargets = function(targets) {

	if (targets.length > 0) {

		var currInput = $(".target");
		for (var x = 0; x < targets.length; x++) {

			var input = $(".target").clone();

			createDroppable({
				element: input,
				accept: "div[id='items'] td"
			})

			input.val(this.findItemName(targets[x]).name);
			input.css('font-weight', 'bold');

			if (x === 0) {

				$(".target").before(input);

			} else {

				currInput.after(input);
				currInput = input;
			}

			this.rule.targets.push(this.findItemName(targets[x]).name);
		}
	}
}

Parser.prototype.getTargets = function() {
	return this.rule.targets;
}

Parser.prototype.setEvaluatesTo = function(evaluates) {

	if (evaluates) {

		this.rule.evaluatesTo = true;
		$("#evaluateTrue").prop("checked", true);

	} else {

		this.rule.evaluatesTo = false;
		$("#evaluateFalse").prop("checked", true);

	}
}

Parser.prototype.getEvaluatesTo = function() {
	return this.rule.evaluatesTo;
}

Parser.prototype.setInitialDataEntryExecute = function(execute) {

	if (execute) {

		this.rule.ide = true;
		$("#ide").prop("checked", execute);
	} else {

		this.rule.ide = false;
		$("#ide").prop("checked", execute);
	}
}

Parser.prototype.getInitialDataEntryExecute = function() {
	return this.rule.ide ? this.rule.ide : false;
}

Parser.prototype.setDoubleDataEntryExecute = function(execute) {

	if (execute) {

		this.rule.dde = true;
		$("#dde").prop("checked", execute);
	} else {

		this.rule.dde = false;
		$("#dde").prop("checked", execute);
	}
}

Parser.prototype.getDoubleDataEntryExecute = function() {
	return this.rule.dde ? this.rule.dde : false;
}

Parser.prototype.setAdministrativeEditingExecute = function(execute) {

	if (execute) {

		this.rule.ae = true;
		$("#ae").prop("checked", execute);
	} else {

		this.rule.ae = false;
		$("#ae").prop("checked", execute);
	}
}

Parser.prototype.getAdministrativeEditingExecute = function() {
	return this.rule.ae ? this.rule.ae : false;
}

Parser.prototype.setDataImportExecute = function(execute) {

	if (execute) {

		this.rule.di = true;
		$("#dataImport").prop("checked", execute);
	} else {

		this.rule.di = false;
		$("#dataImport").prop("checked", execute);
	}
}

Parser.prototype.getDataImportExecute = function() {
	return this.rule.di ? this.rule.di : false;
}

Parser.prototype.getDiscrepancyAction = function() {

	if (this.rule.actions.length > 0) {

		for (var x = 0; x < this.rule.actions.length; x++) {

			var action = this.rule.actions[x];

			if (action.type === "discrepancy") {

				return action;
			}
		}
	}
}

Parser.prototype.setDiscrepancyAction = function(params) {

	if (params) {

		var action = Object.create(null);

		// function to toggle display
		action.render = function(visible) {

			if (visible) {

				$("#message").show();
				$("#actionMessages").show();

				$("#discrepancyText").show();
				$("#discrepancyText").find("textarea").val(action.message);

				$("#chkDiscrepancyText").prop("checked", params.selected);

			} else {

				// Update UI
				$("#message").hide();
				$("#discrepancyText").hide();

				$("#chkDiscrepancyText").prop("checked", params.selected);

				if ($("#actionMessages").find("div:visible").length === 0) {

					$("#actionMessages").hide();
				}
			}
		}

		if (params.selected) {

			if (this.getActions().length > 0 && this.getDiscrepancyAction()) {
				action = this.getDiscrepancyAction();
			} else {

				this.rule.actions.push(action);
			}
			
			action.type = "discrepancy";
			action.message = params.message;
			
			action.render(params.selected);

		} else {

			// Delete saved reference
			var act = this.getDiscrepancyAction();
			
			if ($.inArray(act, this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(act, this.rule.actions), 1);
			}

			action.render(params.selected);
		}
	}
}

Parser.prototype.getEmailAction = function() {

	if (this.rule.actions.length > 0) {

		for (var x = 0; x < this.rule.actions.length; x++) {

			var action = this.rule.actions[x];

			if (action.type === "email") {

				return action;
			}
		}
	}
}

Parser.prototype.setEmailAction = function(params) {

	if (params) {

		var action = Object.create(null);

		// function to toggle display
		action.render = function(visible) {

			if (visible) {

				$("#actionMessages").show();

				// Action controls
				$("#email").show();
				$("#emailTo").show();

				$("#body").show();
				$("#toField").show();

				$("#toField").val(action.to);
				$("#body").val(action.body);

				$("#chkEmail").prop("checked", params.selected);

			} else {

				$("#email").hide();
				$("#emailTo").hide();

				$("#body").val("");
				$("#toField").val("");

				$("#chkEmail").prop("checked", params.selected);

				if ($("#actionMessages").find("div:visible").length === 0) {

					$("#actionMessages").hide();
				}
			}
		}

		if (params.selected) {

			if (this.getActions().length > 0 && this.getEmailAction()) {
				action = this.getEmailAction();
			} else {

				this.rule.actions.push(action);
			}

			action.type = "email";

			action.to = params.to;
			action.body = params.message;

			action.render(params.selected);

		} else {

			// Delete saved reference
			var act = this.getEmailAction();
			if ($.inArray(act, this.rule.actions) > -1) {
				this.rule.actions.splice($.inArray(act, this.rule.actions), 1);
			}

			action.render(params.selected);

		}
	}
}

Parser.prototype.setExpression = function(expression) {

	if (expression instanceof Array) {

		this.rule.expression = expression;

		var currDroppable = $("#groupSurface");

		for (var e = 0; e < expression.length; e++) {

			if (e === 0) {

				$("#groupSurface").text(expression[e]);

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
					droppable.text(predicate);

					currDroppable.after(droppable);

					currDroppable = droppable;
				}
			}

			currDroppable.removeClass("init");
			currDroppable.addClass("bordered");
			currDroppable.css('font-weight', 'bold');
 			
		}
	} else if (typeof expression === "string") {

		var rawExpression = [];

		// The regex skips quoted strings in expression
		var expr = expression.split(/\s+(?!\w+")/);

		for (var x = 0; x < expr.length; x++) {

			if (expr[x].indexOf(".") !== -1) {

				var itemOID = new RegExp("\.([^\.]+)$").exec(expr[x])[1]

				var itm = this.findItemName(itemOID);

				rawExpression.push(itm.name);

			} else {

				if (this.isOp(expr[x])) {
					rawExpression.push(this.getLocalOp(expr[x]));
				} else {
					rawExpression.push(expr[x]);
				}
			}
		}

		this.setExpression(rawExpression);
	}
}

Parser.prototype.setActions = function(actions) {

	if (actions.length > 0) {

		for (var x = 0; x < actions.length; x++) {

			var action = actions[x];

			if (action.type.toLowerCase() === "discrepancy") {

				this.setDiscrepancyAction({
					selected: true,
					message: action.message
				})

			} else if (action.type.toLowerCase() === "email") {

				this.setEmailAction({

					selected: true,
					to: action.to,
					message: action.body
				})
			}
		}
	}
}

Parser.prototype.getActions = function() {
	return this.rule.actions;
}

Parser.prototype.getRule = function() {

	this.createRule();

	if (this.isValid(this.rule.expression).valid) {

		var rule = Object.create(null);

		rule.expression = this.rule.expression.join().replace(/\,/g, " ");

		rule.name = this.getName();
		rule.targets = this.getTargets();
		rule.evaluatesTo = this.getEvaluatesTo();
		
		// Evocation
		rule.di = this.getDataImportExecute();
		rule.dde = this.getDoubleDataEntryExecute();
		rule.ide = this.getInitialDataEntryExecute();
		rule.ae = this.getAdministrativeEditingExecute();

		rule.actions = this.getActions();

		rule.submission = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

		return rule;

	} else {

		// Ensure one alert is displayed
		if ($(".alert").size() == 0) {

			$("#designSurface").find(".panel-body").prepend(createAlert(this.isValid(this.rule.expression).message));
		}

		return false;
	}
}

Parser.prototype.render = function(rule) {

	this.setExpression(rule.expression);

	// properties
	this.setName(rule.name);
	this.setTargets(rule.targets);
	this.setEvaluatesTo(rule.evaluatesTo);

	// executions
	this.setDataImportExecute(rule.di);
	this.setDoubleDataEntryExecute(rule.dde);
	this.setInitialDataEntryExecute(rule.ide);
	this.setAdministrativeEditingExecute(rule.ae);

	// Actions
	parser.setActions(rule.actions);
}

/* ========================================================================
 * Fetch studies from CC. The studies come with events/crf and items added.
 * ====================================================================== */
Parser.prototype.fetchStudies = function() {
	
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$.ajax({

		type: "POST",

		url: c + "/studies?action=fetch",

		success: function(studies) {

			// FF can return a string
			if (typeof(studies) === "string") {

				studies = JSON.parse(studies)
			}

			sessionStorage.setItem("studies", JSON.stringify(studies));
			loadStudies(studies);

		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

/* ========================================================================
 * Fetch a specific rule from CC for editing in the designer. The rule is
 * passed as an id on the url with a parameter action=editing
 * ====================================================================== */
Parser.prototype.fetchRuleForEditing = function() {

	sessionStorage.setItem("edit", true);
	sessionStorage.setItem("id", this.getParameterValue("rId"));
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$.ajax({

		type: "POST",

		url: c + "/studies?action=edit&id=" + this.getParameterValue("id") + "&rId=" + this.getParameterValue("rId"),

		success: function(response) {

			var rule = null;

			// FF can return a string
			if (typeof(response) === "string") {

				rule = JSON.parse(response)
			}

			parser.render(rule);

		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

/* =================================================================
 * Validates the designed rule using the CC Test Rule servlet
 *
 * Arguments [params]:
 * => expression - the expression in text format
 * => targets - the rule targets
 * => evaluateTo - What the rule should evaluate to
 * ============================================================= */
Parser.prototype.validate = function(rule) {

	var rule = parser.getRule();

	if (rule) {

		$.ajax({

			type: "POST",

			data: {

				rs: true,
				rule: rule.expression,
				target: rule.targets[0],
				testRuleActions: rule.evaluateTo
			},

			url: rule.submission + "/TestRule?action=validate&study=" + selectedStudy,

			success: function(response) {

				sessionStorage.setItem("validation", response);
				parser.displayValidationResults(rule)
			},

			error: function(response) {

				handleErrorResponse({

					response: response
				})
			}
		})
	}
}

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

		url: rule.submission + "/studies?action=validate",

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
			})
		}
	})
}

Parser.prototype.getParameter = function(name) {

	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))|| null;
}

Parser.prototype.getParameterValue = function(name) {

	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");

	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);
	if (results == null) {
		return "";
	} else {
		return results[1];
	}
}