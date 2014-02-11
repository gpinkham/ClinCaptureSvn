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

		if (!params.element.next().is(".group")) {

			params.element.after(dataPredicate);

			// Check for existing data
			dataPredicate.after(RPAREN);

			createPopover(RPAREN);
		}

		createPopover(dataPredicate);

	} else if (params.element.is("input")) {

		if (!this.isAlreadyAddedTarget(params.ui.draggable.text())) {

			this.rule.targets.push(params.ui.draggable.text());

			var newInput = params.element.clone();

			// create a new input 
			if (params.element.val()) {

				if (params.element.siblings("input").length == 0) {

					createDroppable({
						element: newInput,
						accept: "div[id='items'] td"
					})
				}
			} else {

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

	if (!$("#chkDiscrepancyText").is(":checked") && !$("#chkEmail").is(":checked") && !$("#chkData").is(":checked") && $("input[name=tItem]:checked").length == 0) {

		valid = false,
		message = "A rule is supposed to fire an action. Please the action to take if the rule evaluates correctly"
	}

	if (!$("#ide").is(":checked") && !$("#ae").is(":checked") && !$("#dde").is(":checked") && !$("#dataImport").is(":checked")) {

		valid = false,
		message = "Please specify when the rule should be run"
	}

	if ($("input[name=ruleInvoke]:checked").length == 0) {

		valid = false,
		message = "A rule is supposed to evaluate to true or false. Please specify"
	}

	if ($(".target").val().length == 0) {

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

	var item = Object.create(null);
	var storedStudies = JSON.parse(sessionStorage.getItem("studies"));

	for (var x in storedStudies) {

		var study = storedStudies[x];

		for (var e in study.events) {

			var event = study.events[e];

			for (var c in event.crfs) {

				var crf = event.crfs[c];

				for (var i in crf.items) {

					var item = crf.items[i];

					if (item.name === itemName) {

						item.item = item;
						item.formOid = crf.oid;

						return item;
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

	var item = Object.create(null);
	var storedStudies = JSON.parse(sessionStorage.getItem("studies"));

	for (var x in storedStudies) {

		var study = storedStudies[x];

		for (var e in study.events) {

			var event = study.events[e];

			for (var c in event.crfs) {

				var crf = event.crfs[c];

				for (var i in crf.items) {

					var item = crf.items[i];

					if (item.oid === itemOID) {

						item.item = item;
						item.formOid = crf.oid;

						return item;
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

			var crfItem = this.findItemName(targets[x]);

			if (x > 0) {

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

			this.rule.targets.push(crfItem.item.name);
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

		if (params.selected) {

			var action = Object.create(null);

			if (this.getActions().length > 0 && this.getDiscrepancyAction()) {
				action = this.getDiscrepancyAction();
			} else {

				this.rule.actions.push(action);
			}
			
			action.type = "discrepancy";
			action.message = params.message;

			// function to show
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
				}
			}
			
			action.render(params.selected);

		} else {

			// Delete saved reference
			var act = this.getDiscrepancyAction();
			delete act;
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

		if (params.selected) {

			var action = Object.create(null);

			if (this.getActions().length > 0 && this.getEmailAction()) {
				action = this.getEmailAction();
			} else {

				this.rule.actions.push(action);
			}

			action.type = "email";

			action.to = params.to;
			action.body = params.message;

			// Update UI
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

					// Update UI
					$("#email").hide();
					$("#emailTo").hide();

					$("#body").val("");
					$("#toField").val("");

					$("#chkEmail").prop("checked", params.selected);
				}
			}

			action.render(params.selected);

		} else {

			// Delete saved reference
			var act = this.getEmailAction();
			delete act;
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
	this.setEvaluatesTo(rule.evaluateTo);

	// executions
	this.setDataImportExecute(rule.di);
	this.setInitialDataEntryExecute(rule.ide);
	this.setDoubleDataEntryExecute(rule.dde);
	this.setAdministrativeEditingExecute(rule.ae);

	// Actions
	parser.setActions(rule.actions);
}