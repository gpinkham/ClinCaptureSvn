/*
 *  Web Forms 2.0 Repetition Model Cross-browser Implementation <http://code.google.com/p/repetitionmodel/>
 *  Version: 0.8.2.2 (2007-03-28)
 *  Copyright: 2007, Weston Ruter <http://weston.ruter.net/>
 *  License: http://creativecommons.org/licenses/LGPL/2.1/
 *
 *  The comments contained in this code are largely quotations from the
 *  WebForms 2.0 specification: <http://whatwg.org/specs/web-forms/current-work/#repeatingFormControls>
 *
 *  Usage: <script type="text/javascript" src="repetition-model.js"></script>
 */
var addButtonClicked = false;
var firstRepeatingNumber=2;
var RepeatingBlockid="";
var global_addButtonClicked=false;
var pressedRemoveButton;

_getElementsByClassName = function(node, search) {
	var d = node, elements, pattern, i, results = [];
	if (d.querySelectorAll) { // IE8
		return d.querySelectorAll("." + search);
	}
	if (d.evaluate) { // IE6, IE7
		pattern = ".//*[contains(concat(' ', @class, ' '), ' " + search + " ')]";
		elements = d.evaluate(pattern, d, null, 0, null);
		while ((i = elements.iterateNext())) {
			results.push(i);
		}
	} else {
		elements = d.getElementsByTagName("*");
		pattern = new RegExp("(^|\\s)" + search + "(\\s|$)");
		for (i = 0; i < elements.length; i++) {
			if ( pattern.test(elements[i].className) ) {
				results.push(elements[i]);
			}
		}
	}
	return results;
}

addOverlay = function() {
	removeOverlay();
	var div = document.createElement("div");
	div.setAttribute("id", "pageOverlay");
	div.setAttribute("style", "width: " + parseInt(document.body.scrollWidth) + "px; height: " + parseInt(document.body.scrollHeight) + "px;");
	document.body.appendChild(div);
}

removeOverlay = function() {
	var overlay = document.getElementById('pageOverlay');
	if (overlay != null) {
		document.body.removeChild(overlay);
	}
}

replaceRowPrefix = function(node, rowPrefix, newRowPrefix) {
	if (node.attributes != undefined) {
		for (var i = 0; i < node.attributes.length; i++) {
			var attr = node.attributes[i];
			var attrValue = node[attr.name] || node.getAttribute(attr.name);
			if (attrValue != undefined && attrValue.toString().indexOf(rowPrefix) >= 0) {
				if (typeof attrValue != 'function') {
					node.setAttribute(attr.name, attrValue.toString().replace(new RegExp(rowPrefix, "g"), newRowPrefix));
				}
			}
		}
	}
	for (i = 0; i < node.children.length; i++) {
		replaceRowPrefix(node.children[i], rowPrefix, newRowPrefix);
	}
	// fix calendar buttons
	$("a[id^=anchor" + rowPrefix + "]").each(function() {
		try {
			var _html = $(this).get(0).outerHTML;
			$(this).get(0).outerHTML = _html.replace(new RegExp(rowPrefix, "g"), newRowPrefix);
		} catch (e) {}
	});
}

processRemoveButtons = function() {
	var rbtnElement = _getElementsByClassName(document.body, "button_remove");
	for (var i = 0; i < rbtnElement.length; i++) {
		if (rbtnElement[i].getAttribute("rel") == "false") {
			rbtnElement[i].style.visibility = "visible";
		}
	}
}

removeButtonClick = function(btn, block) {
	try {
		var deletedRowIndex = undefined;
		var rowPrefix = btn.parentNode.children[0].getAttribute("name").replace('.newRow','');
		if (rowPrefix.indexOf("_manual") >= 0) {
			deletedRowIndex = parseInt(rowPrefix.replace(/.*_manual/g, ""));
		}
		if (deletedRowIndex != undefined && rowPrefix != undefined && block != undefined) {
			addOverlay();
			var timestamp = new Date().getTime();
			$.get("ShuffleDiscrepancyNotes?rp=" + rowPrefix + "&t=" + timestamp, function(data) {
				for (var i = 0; i < block.parentNode.children.length; i++) {
					var tr = block.parentNode.children[i];
					if (tr.nodeName.toUpperCase() == "TR") {
						var repeatTemplate = tr.getAttribute("repeat-template");
						if (repeatTemplate == undefined ) {
							break;
						}
						var rButtons = _getElementsByClassName(tr, "button_remove");
						if (rButtons.length == 1) {
							var rowPrefix = rButtons[0].parentNode.children[0].getAttribute("name").replace('.newRow','');
							if (rowPrefix.indexOf("_manual") >= 0 ) {
								var rowIndex = parseInt(rowPrefix.replace(/.*_manual/g, ""));
								if (rowIndex > deletedRowIndex) {
									var newRowPrefix = rowPrefix.replace("_manual" + rowIndex, "_manual" + (rowIndex - 1));
									replaceRowPrefix(tr, rowPrefix, newRowPrefix);
								}
							} else
							if (parseInt(rowPrefix.replace(repeatTemplate + "_", "")) > 0) {
								break;
							}
						}
					}
				}
				if(block) {
					block.removeRepetitionBlock();
				}
				removeOverlay();
			});
		} else
		if(block) {
			block.removeRepetitionBlock();
		}
	} catch(e) {
		removeOverlay();
	}
}
//clinovo - end

if (!window.RepetitionElement || (
	document.implementation && document.implementation.hasFeature &&
	!document.implementation.hasFeature("WebForms", "2.0")
	)) {
	var RepetitionElement = {
		REPETITION_NONE:0,
		REPETITION_TEMPLATE:1,
		REPETITION_BLOCK:2,

		_initialized:false,
		_repetitionTemplates:[],

		_init_document : function(){ //called when the document is loaded

			processRemoveButtons();

			if(RepetitionElement._initialized)
				return;
			RepetitionElement._initialized = true;

			//RepetitionElement interface must be implemented by all elements.
			if(window.Element && Element.prototype){
				Element.prototype.REPETITION_NONE     = RepetitionElement.REPETITION_NONE;
				Element.prototype.REPETITION_TEMPLATE = RepetitionElement.REPETITION_TEMPLATE;
				Element.prototype.REPETITION_BLOCK    = RepetitionElement.REPETITION_BLOCK;

				Element.prototype.repetitionType      = RepetitionElement.REPETITION_NONE;
				Element.prototype.repetitionIndex     = 0;
				Element.prototype.repetitionTemplate  = null; /*readonly*/
				Element.prototype.repetitionBlocks    = null; /*readonly*/

				Element.prototype.repeatStart = 1;
				Element.prototype.repeatMin   = 0;
				Element.prototype.repeatMax   = Infinity;

				Element.prototype.addRepetitionBlock        = RepetitionElement.addRepetitionBlock;
				Element.prototype.addRepetitionBlockByIndex = RepetitionElement.addRepetitionBlockByIndex;
				Element.prototype.removeRepetitionBlock     = RepetitionElement.removeRepetitionBlock;
			}

			RepetitionElement._init_repetitionBlocks(); //initialize any repetition block

			RepetitionElement._init_repetitionTemplates();
			RepetitionElement._init_addButtons();
			RepetitionElement._init_removeButtons();

			RepetitionElement.__updateAddButtons();

			initCrfMoreInfo();
		},

		/*##############################################################################################
		 # REPETITION TEMPLATE
		 ##############################################################################################*/
		_repetitionTemplate_constructor : function(){
			if(this._initialized)
				return;
			this._initialized = true; //SAFARI needs this to be here for some reason...

			this.style.display = 'none'; //This should preferrably be specified via a stylesheet
			this.repetitionType = RepetitionElement.REPETITION_TEMPLATE;
			this.repetitionIndex = 0;
			this.repetitionTemplate = null; //IMPLEMENT GETTER
			this.repetitionBlocks = []; //IMPLEMENT GETTER
			var _attr;
			this.repeatStart = /^\d+$/.test(_attr = this.getAttribute('repeat-start')) ? parseInt(_attr) : 1;
			this.repeatMin   = /^\d+$/.test(_attr = this.getAttribute('repeat-min'))   ? parseInt(_attr) : 0;
			this.repeatMax   = /^\d+$/.test(_attr = this.getAttribute('repeat-max'))   ? parseInt(_attr) : Infinity;

			if(!this.addRepetitionBlock) this.addRepetitionBlock = function(refNode, index){
				return RepetitionElement.addRepetitionBlock.apply(this, [refNode, index]); //wrapper to save memory
			};
			if(!this.addRepetitionBlockByIndex)
				this.addRepetitionBlockByIndex = this.addRepetitionBlock/*ByIndex*/; //one method implements both algorithms

			//On the HTMLFormElement, the templateElements attribute contains the list of form controls associated
			//   with this form that form part of repetition templates. It is defined in more detail in the section
			//   on the repetition model. (Image controls are part of this array, when appropriate.) The controls
			//   in the elements and templateElements lists must be in document order.
			var form = this;
			while(form = form.parentNode){
				if(form.nodeName.toLowerCase() == 'form')
					break;
			}
			var _templateElements;
			if(form && (_templateElements = RepetitionElement.__getElementsByTagNames.apply(this, ['button','input','select','textarea','isindex','a'])).length){
				//_templateElements = cssQuery("button,input,select,textarea,isindex", this); //IMAGE???, fieldset not included

				//INCORRECT IMPLEMENTATION: this should append the new elements onto the form.templateElements array and then sort them in document order?
				form.templateElements = _templateElements;

				//Controls in the templateElements attribute cannot be successful; controls inside repetition templates can never be submitted.
				//   Therefore disable all elements in the template; however, due to the issue below, the original disabled state must be stored in the field's class attribute as "disabled"
				//   this storing of the original disabled state will enable the elements in cloned blocks to be disabled as originally coded in the template
				//ISSUE: inputs retain disabled (but not defaultDisabled) attribue after returning to page from back button or reload
				//   see http://weblogs.mozillazine.org/gerv/archives/2006/10/firefox_reload_behaviour.html
				// As a workaround... this implementation requires that authors, in addition to supplying a DISABLED attribute (for Opera), to include a class name "disabled"
				for(var el, i = 0; el = form.templateElements[i]; i++)
					el.disabled = true;

				//sort elements by document order (code from PPK: http://www.quirksmode.org/dom/getElementsByTagNames.html)
				if (form.templateElements[0].sourceIndex){ //Internet Explorer
					form.templateElements.sort(function (a,b) {
						return a.sourceIndex - b.sourceIndex;
					});
				}
				else if (form.templateElements[0].compareDocumentPosition){ //Gecko/W3C
					form.templateElements.sort(function (a,b) {
						return 3 - (a.compareDocumentPosition(b) & 6);
					});
				}

				//IMPLEMENTATION DEFICIENCY: unable to remove form.templateElements from form.elements
			}

			//Repetition blocks without a repeat-template attribute are associated with their first following sibling
			//   that is a repetition template, if there is one.
			var sibling = this;
			while(sibling = sibling.previousSibling){
				if(sibling.repetitionType == RepetitionElement.REPETITION_BLOCK && !sibling.getAttribute('repeat-template')){
					sibling.repetitionTemplate = this;
					sibling.setAttribute('repeat-template', this.id);
					this.repetitionBlocks.unshift(sibling);
				}
			}

			//the UA must invoke the template's replication behaviour as many times as the repeat-start attribute
			//   on the same element specifies (just once, if the attribute is missing or has an invalid value).
			//   Then, while the number of repetition blocks associated with the repetition template is less than
			//   the template's repeat-min attribute, the template's replication behaviour must be further invoked.
			//   (Invoking the template's replication behaviour means calling its addRepetitionBlock() method).
			for(var i = 0; i < Math.max(this.repeatStart, this.repeatMin); i++)
				this.addRepetitionBlock();

			RepetitionElement._repetitionTemplates.push(this);
			this._initialized = true;
		},

		_init_repetitionTemplates : function(parentNode){
			//UAs must iterate through every node in the document, depth first, looking for templates so that their
			//   initial repetition blocks can be created.
			//var repetitionTemplates = cssQuery("*[repeat=template]", parentNode);
			var repetitionTemplates = RepetitionElement.__getElementsByNameAndAttribute.apply((parentNode || document.body), ['*', 'repeat', 'template']);
			for(var i = 0, rt; i < repetitionTemplates.length; i++)
				RepetitionElement._repetitionTemplate_constructor.apply(repetitionTemplates[i]);
		},


		/*##############################################################################################
		 # REPETITION BLOCK
		 ##############################################################################################*/

		_repetitionBlock_constructor : function(){
			//a boolean value signaling that the Add button has been clicked
			var addButtonClicked;
			this.addButtonClicked = false;
			if(this._initialized)
				return;

			this.style.display = ''; //This should preferrably be specified via a stylesheet
			this.repetitionType = RepetitionElement.REPETITION_BLOCK;
			var _attr;
			this.repetitionIndex = /^\d+$/.test(_attr = this.getAttribute('repeat')) ? parseInt(_attr) : 0;
			this.repetitionBlocks = null;

			//find this block's repetition template
			this.repetitionTemplate = null; //IMPLEMENT GETTER
			var node;

			if((node = document.getElementById(this.getAttribute('repeat-template'))) &&
				node.repetitionType == RepetitionElement.REPETITION_TEMPLATE)
			{
				// mantis issue 4597 -- Start
				//alert(node.childNodes[1].childNodes[0].childNodes[1].innerHTML);
				if(RepeatingBlockid != node.id){
					firstRepeatingNumber=2;
					RepeatingBlockid = node.id;
				}

				if(document.getElementById("repeatCaption")){
					node.childNodes[1].childNodes[0].childNodes[1].innerHTML = "<strong>Repeat: "+(firstRepeatingNumber++)+"<strong>";
				}
				// mantis issue 4597 -- End
				this.repetitionTemplate = node;
			}
			else {
				node = this;
				while(node = node.nextSibling){
					if(node.repetitionType == RepetitionElement.REPETITION_TEMPLATE){
						this.repetitionTemplate = node;
						break;
					}
				}
			}

			if(!this.removeRepetitionBlock) this.removeRepetitionBlock = function(){
				return RepetitionElement.removeRepetitionBlock.apply(this); //wrapper to save memory
			};
			this._initialized = true;
		},

		_init_repetitionBlocks : function(parentNode){
			//var repetitionBlocks = cssQuery('*[repeat]:not([repeat="template"])', parentNode); //:not([repeat="template"])
			var repetitionBlocks = RepetitionElement.__getElementsByNameAndAttribute.apply((parentNode || document.body), ['*', 'repeat', 'template', true]);
			for(var i = 0; i < repetitionBlocks.length; i++)
				RepetitionElement._repetitionBlock_constructor.apply(repetitionBlocks[i]);
		},

		/*##############################################################################################
		 # ADD BUTTON
		 ##############################################################################################*/
		_addButton_constructor : function(){
			if(this._initialized)
				return;
			this.htmlTemplate = RepetitionElement.__getHtmlTemplate(this); //IMPLEMENT GETTER
			//user agents must automatically disable add buttons (irrespective of the value of the disabled
			//   DOM attribute [NOT IMPLEMENTED]) when the buttons are not in a repetition block that has an
			//   associated template and their template attribute is either not specified or does not have
			//   an ID that points to a repetition template...
			var rb;
			this.disabled = !(  ((rb = RepetitionElement.__getRepetitionBlock(this)) && rb.repetitionTemplate)
			||
			this.htmlTemplate
			);

			if(this.addEventListener)
				this.addEventListener('click', RepetitionElement._addButton_click, false);
			else if(this.attachEvent)
				this.attachEvent('onclick', RepetitionElement._addButton_click);
			else this.onclick = RepetitionElement._addButton_click;

			this._initialized = true;
		},

		_init_addButtons : function(parentNode){
			//var addButtons = cssQuery("button[type=add]", parentNode);
			var addButtons = RepetitionElement.__getElementsByNameAndAttribute.apply((parentNode || document.body), ['button', 'stype', 'add']);
			for(var i = 0; i < addButtons.length; i++)
				RepetitionElement._addButton_constructor.apply(addButtons[i]);
		},

		_addButton_click : function(e){
			addButtonClicked = true;
			if(e && e.preventDefault)
				e.preventDefault(); //Firefox thinks default of custom repetition buttons is submit

			//If the event is canceled (btn.returnValue === false, set within onclick handler), then the default action will not occur.
			var btn;
			if(e && e.target)
				btn = e.target;
			else if(window.event)
				btn = window.event.srcElement;
			else if(String(this.nodeName).toLowerCase() == 'button')
				btn = this;

			//QUESTION: spec says that this event is not cancelable! Does Opera not follow the spec here?

			//Terminate if an onclick handler was called beforehand and returned a false value passed via the button's returnValue property
			if(!btn.returnValue && typeof(btn.returnValue) != 'undefined'){
				btn.returnValue = undefined;
				return false;
			}

			//Ensure that a user-supplied onclick handler is fired before the repetition behavior is executed
			//  and terminate if this onclick handler returns false
			if((btn.onclick || btn._onclick) && btn.hasAttribute && !btn.hasAttribute("onclick")){ //NOTE: MSIE fires this afterwards???
				if(btn.onclick)
					btn._onclick = btn.onclick;
				btn.returnValue = btn._onclick(e);
				btn.onclick = null; //prevent the onclick handler from firing afterwards (would fire after movement action)
				if(!btn.returnValue && typeof(btn.returnValue) != 'undefined'){
					btn.returnValue = undefined;
					return false;
				}
			}
			//ISSUE: How do we ensure that the MSIE and DOM Level 2 Event handlers are executed beforehand, and how do we get their return values?

			var rt;
			//If an add button with a template attribute is activated, and its template attribute gives the ID
			//   of an element in the document that is a repetition template as defined above, then that
			//   template's replication behaviour is invoked. (Specifically, in scripting-aware environments,
			//   the template's addRepetitionBlock() method is called with a null argument.) In the case of
			//   duplicate IDs, the behaviour should be the same as with getElementById().
			if(btn.htmlTemplate)
				rt = btn.htmlTemplate;
			else {
				//If an add button without a template attribute is activated, and it has an ancestor that is a
				//   repetition block that is not an orphan repetition block, then the repetition template associated
				//   with that repetition block has its template replication behaviour invoked with the respective
				//   repetition block as its argument. (Specifically, in scripting-aware environments, the template's
				//   addRepetitionBlock() method is called with a reference to the DOM Element node that represents
				//   the repetition block.)
				var block = RepetitionElement.__getRepetitionBlock(btn);
				if(block && block.repetitionTemplate)
					rt = block.repetitionTemplate;
			}
			if(rt) {
				//THIS CODE  ADDED RE: TASK 1868,RELATED TO METHOD clearInputElementValues() IN
				//GLOBAL_FUNCTIONS_JAVASCRIPT.JS FILE
				global_addButtonClicked=true;   //added for functions in OpenClinica 2.2
				rt.addRepetitionBlock();
				if(btn.disabled){ changeBtnDisabledState(btn,'button_add',
					'button_add_disabled',false); }     //added for functions in OpenClinica 2.2
			}
			else
				btn.disabled = true; //NOTE: THIS IS NOT A VALID IMPLEMENTATION
			return true;
		},

		/*##############################################################################################
		 # REMOVE BUTTON
		 ##############################################################################################*/

		_removeButton_constructor : function(){
			if(this._initialized)
				return;
			this.htmlTemplate = RepetitionElement.__getHtmlTemplate(this); //IMPLEMENT GETTER

			if(this.addEventListener)
				this.addEventListener('click', RepetitionElement._removeButton_click, false);
			else if(this.attachEvent)
				this.attachEvent('onclick', RepetitionElement._removeButton_click);
			else this.onclick = RepetitionElement._removeButton_click;

			this._initialized = true;
		},

		_init_removeButtons : function(parentNode){
			//var removeButtons = cssQuery("button[type=remove]", parentNode);
			var removeButtons = RepetitionElement.__getElementsByNameAndAttribute.apply((parentNode || document.body), ['button', 'stype', 'remove']);
			for(var i = 0; i < removeButtons.length; i++)
				RepetitionElement._removeButton_constructor.apply(removeButtons[i]);
		},

		_removeButton_click : function(e){
			if(e && e.preventDefault)
				e.preventDefault(); //Firefox thinks default of custom repetition buttons is submit

			//user agents must automatically disable remove buttons when they are not in a repetition
			//   block. [NOT IMPLEMENTED:] This automatic disabling does not affect the DOM disabled  attribute. It is an intrinsic property of these buttons.
			this.disabled = !RepetitionElement.__getRepetitionBlock(this);

			//If the event is canceled (btn.returnValue === false, set within onclick handler), then the default action will not occur.
			var btn;
			if(e && e.target)
				btn = e.target;
			else if(window.event)
				btn = window.event.srcElement;
			else if(String(this.nodeName).toLowerCase() == 'button')
				btn = this;

			//QUESTION: spec says that this event is not cancelable! Does Opera not follow the spec here?

			//Terminate if an onclick handler was called beforehand and returned a false value passed via the button's returnValue property
			if(!btn.returnValue && typeof(btn.returnValue) != 'undefined'){
				btn.returnValue = undefined;
				return false;
			}

			//Ensure that a user-supplied onclick handler is fired before the repetition behavior is executed
			//  and terminate if this onclick handler returns false
			if((btn.onclick || btn._onclick) && btn.hasAttribute && !btn.hasAttribute("onclick")){ //NOTE: MSIE fires this afterwards???
				if(btn.onclick)
					btn._onclick = btn.onclick;
				btn.returnValue = btn._onclick(e);
				btn.onclick = null; //prevent the onclick handler from firing afterwards (would fire after movement action)
				if(!btn.returnValue && typeof(btn.returnValue) != 'undefined'){
					btn.returnValue = undefined;
					return false;
				}
			}
			//ISSUE: How do we ensure that the MSIE and DOM Level 2 Event handlers are executed beforehand, and how do we get their return values?

			var block = RepetitionElement.__getRepetitionBlock(btn);

			//clinovo - ticket #54
			removeButtonClick(btn, block);

			return false;
		},

		/*##############################################################################################
		 # AddRepetitionBlock algorithm
		 ##############################################################################################*/

		//Element addRepetitionBlock(in Node refNode);
		addRepetitionBlock : function(refNode, index){ //addRepetitionBlockByIndex functionalty enabled if @index defined
			if(refNode && !refNode.nodeType)
				throw Error("Exception: WRONG_ARGUMENTS_ERR");

			if(this.repetitionType != RepetitionElement.REPETITION_TEMPLATE)
			//throw DOMException("NOT_SUPPORTED_ERR");
				throw Error("DOMException: NOT_SUPPORTED_ERR");

			//1. If the template has no parent node or its parent node is not an element, then the method must abort
			//   the steps and do nothing.
			if(this.parentNode == null)
				return;

			//[furthermore, if this template is the child of another template (not the child of an instance, a block) return false]
			var node = this;
			while(node = node.parentNode){
				if(node.repetitionType == RepetitionElement.REPETITION_TEMPLATE)
					return false;
			}

			//2. The template examines its preceding siblings, up to the start of the parent element. For each sibling
			//   that is a repetition block whose associated template is this template, if the repetition block's index
			//   is greater than or equal to the template's index, then the template's index is increased to the repetition
			//   block's index plus one. The total number of repetition blocks associated with this template that were
			//   found is used in the next step.
			//QUESTION: Why not just use this.repetitionBlocks.length????????????
			var sibling = this.previousSibling;
			var currentBlockCount = 0;
			while(sibling != null){
				if(sibling.repetitionType == RepetitionElement.REPETITION_BLOCK &&
					sibling.repetitionTemplate == this)
				{
					//Old Note: sibling.getAttribute('repeat') is used instead of sibling.repetitionIndex because appearantly
					//      the sibling is not yet bound to the document and so the getters are not available
					//this.repetitionIndex = Math.max(this.repetitionIndex, parseInt(sibling.getAttribute('repeat'))+1);
					this.repetitionIndex = Math.max(this.repetitionIndex, sibling.repetitionIndex+1);
					currentBlockCount++;
				}
				sibling = sibling.previousSibling;
			}

			//3. If the repetition template has a repeat-max attribute and that attribute's value is less than or equal
			//   to the number of repetition blocks associated with this template that were found in the previous step,
			//   the UA must stop at this step, returning a null value.
			if(this.repeatMax <= currentBlockCount)
				return null;

			//4. If this algorithm was invoked via the addRepetitionBlockByIndex()  method, and the value of the method's
			//   index argument is greater than the template's index, then the template's index is set to the value of the
			//   method's index argument.
			if(typeof index != 'undefined' && index > this.repetitionIndex)
				this.repetitionIndex = index;

			//(the following steps are out of order to facilitate a custom cloneNode to cope for MSIE and Gecko issues)

			//9. If the new repetition block has an ID attribute (that is, an attribute specifying an ID, regardless
			//   of the attribute's namespace or name), then that attribute's value is used as the template name in
			//   the following steps. Otherwise, the template has no name. (If there is more than one ID attribute,
			//   the "first" one in terms of node order is used. [DOM3CORE])
			//   [Since this step was moved here, it uses 'this' and not 'block', which hasn't been created yet]
			//NOTE: hasAttribute throws error in IE
			//var IDAttr = block.getAttributeNode('id') ? block.getAttributeNode('id') : block.getAttributeNode('name'); //DETECT ID TYPE For others?
			var IDAttrName = this.getAttribute('id') ? 'id' : this.getAttribute('name') ? 'name' : ''; //NOTE: hasAttribute not implemented in MSIE
			var IDAttrValue = this.getAttribute(IDAttrName);

			//10. If the template has a name (see the previous step), and that name contains either an opening square
			//    bracket (U+005B, "[") a modifier letter half triangular colon (U+02D1), a closing square bracket
			//    (U+005D, "]") or a middle dot (U+00B7), then the template's name is ignored for the purposes of
			//    the next step.
			var ignoreName = /\u005B|\u02D1|\u005D|\u00B7/.test(IDAttrValue); //VALID LOGIC?
			var boolProcessAttr = IDAttrValue && !ignoreName;

			//5. A clone of the template is made. The resulting element is the new repetition block element.
			//   [Note that the DOM cloneNode method is not invoked in this implementation due to MSIE
			//   errors, such as not being able to modify the name attribute of an existing node and strange Gecko behavior
			//   regarding the inconsistant correspondence of an input node's value attribute and value property.
			//   Instead of invoking the native cloneNode method, each element is copied manually when it is iterated over.]
			//	 [Note: step 11 of the the specification's algorithm has been merged into step 5. See note at step 11 below]
			//(11). If the template has a name and it is not being ignored (see the previous two steps), then, for every
			//      attribute on the new element, and for every attribute in every descendant of the new element: if the
			//      attribute starts with a zero-width non-breaking space character (U+FEFF) then that character is
			//      removed from the attribute; otherwise, any occurrences of a string consisting of an opening square
			//      bracket (U+005B, "[") or a modifier letter half triangular colon (U+02D1), the template's name,
			//      and a closing square bracket (U+005D, "]") or a middle dot (U+00B7), are replaced by the new
			//      repetition block's index. This is performed regardless of the types, names, or namespaces of attributes,
			//      and is done to all descendants, even those inside nested forms, nested repetition templates, and so forth.
			var block;

			//Function that processes an attribute value as defined in step 11
			var replaceValue = this.repetitionIndex;
			var reTemplateName = new RegExp("(\\[|\u02D1)" + IDAttrValue + "(\\]|\u00B7)", 'g'); //new RegExp('(\\u005B|\\u02D1)' + IDAttrValue + '(\\u005D|\\u00B7)', 'g');
			function _processAttr(attrVal){
				if(!attrVal)
					return attrVal;
				attrVal = attrVal.toString();
				if(attrVal.indexOf("\uFEFF") === 0)
					return attrVal.replace(/^\uFEFF/, '');
				return attrVal.replace(reTemplateName, replaceValue);
			}


			var _customAttrs = { //FOR MSIE BUG: it cannot perceive the attributes that were actually specified
				'type':1,'template':1,'repeat':1,'repeat-template':1,'repeat-min':1,
				'repeat-max':1,'repeat-start':1,'value':1,'class':1,'required':1,
				'pattern':1,'form':1,'autocomplete':1,'autofocus':1,'inputmode':1
			};
			var _skippedAttrs = {
				'name':1,  //due to MSIE bug, set via RepetitionElement.__createElementWithName
				'class':1, //due to MSIE bug, set below (see http://www.alistapart.com/articles/jslogging)
				'for':1,   //due to preceived MSIE bug, set below
				'style':1,  //inline styles require special handling
				onadd:1,onremove:1,onmove:1, //don't copy Repetition old model event attributes not methods
				onmoved:1,onadded:1,onremoved:1, //deprecated

				//for MSIE, properties (or methods) == attributes
				addRepetitionBlock:1,addRepetitionBlockByIndex:1,
				removeRepetitionBlock:1, repetitionBlocks:1,
				_initialized:1
			};

			//BROWSER BUG: _cloneNode used with Gecko because Gecko starts to have irratic behavior with a cloned
			//  input's value attribute and value property; furthermore, various MSIE bugs prevent its ise of cloneNode

			//tabindex will hold the value of the tabindex of the last input element of the last row.
			var tabindex;
			function _cloneNode(node){
				var clone, attr;

				if (typeof(node.name) != "undefined" && node.name.length > 0 && node.getAttribute('tabindex') != null){
					tabindex = node.getAttribute('tabindex');
				}

				if (node.nodeType == 1) {
					if (node.nodeName != undefined && node.nodeName == "") {
						return node.cloneNode(true);
					}
					clone = node.name ? RepetitionElement.__createElementWithName(node.nodeName, (boolProcessAttr ? _processAttr(node.name) : node.name))
						: document.createElement(node.nodeName);

					for (var i = 0; attr = node.attributes[i]; i++){
						// Set default options for single select.
						if (isOptionNode(node) && isOptionSelected(node)) {
							clone.selected = true;
						}

						if((attr.specified || _customAttrs[attr.name]) && !_skippedAttrs[attr.name]){
							//clone and process an event handler property (attribute)
							if((attr.name.indexOf("on") === 0) && (typeof node[attr.name] == 'function')){
								var funcBody = _processAttr(node[attr.name].toString().match(/{((?:.|\n)+)}/)[1]);
								funcBody = _processAttr(funcBody);
								clone[attr.name] = new Function('event', funcBody);
							}
							//clone and process other attributes
							else {
								var attrValue = node.getAttribute(attr.name);
								attrValue = (boolProcessAttr ? _processAttr(attrValue) : attrValue);
								clone.setAttribute(attr.name, attrValue);
							}
						}
					}

					if( node.className){
						var _className = (boolProcessAttr ? _processAttr(node.className) : node.className);
						if (clone.getAttributeNode('class')){
							for(i = 0; i < clone.attributes.length; i++) {
								if(clone.attributes[i].name == 'class')
									clone.attributes[i].value = _className;
							}
						}
						else clone.setAttribute('class', _className);
					}

					//Restore the template's elements to the originally coded disabled state (indicated by 'disabled' class name)
					// All elements within the repetition template are disabled to prevent them from being successful.
					if(!/\bdisabled\b/.test(node.className))
						clone.disabled = false;

					//Process the inline style
					if(node.style){
						clone.style.cssText = _processAttr(node.style.cssText);
					}

					//label's 'for' attribute, set here due to MSIE bug
					if(node.nodeName.toLowerCase() == 'label' && node.htmlFor)
						clone.htmlFor = (boolProcessAttr ? _processAttr(node.htmlFor) : node.htmlFor);

					for(i = 0; el = node.childNodes[i]; i++) {
						clone.appendChild(_cloneNode(el));
					}

					if (node.nodeName == "SELECT" || node.nodeName == "select") {
						$(clone).val(node.value);
					}
				}
				else clone = node.cloneNode(true);
				return clone;
			}

			// We are using this function to update the tabindex of the last row so that the tabs traverses left to right rather
			// than top to bottom.
			function _updateTabindex(node) {
				var i, el;
				if (typeof(node.name) != "undefined" && node.name.length > 0 && node.getAttribute('tabindex') != null){
					node.setAttribute('tabindex', parseInt(tabindex));
				}
				for (i = 0; el = node.childNodes[i]; i++) {
					_updateTabindex(el);
				}
			}

			block = _cloneNode(this);
			block._initialized = false;

			//6. If this algorithm was invoked via the addRepetitionBlockByIndex()  method, the new repetition block
			//   element's index is set to the method's index argument. Otherwise, the new repetition block element's
			//   index is set to the template's index. [Note: if called by addRepetitionBlockByIndex() then the
			//   template's repetitionIndex has already been set to the index argument. Redundant algorithm step.]
			//block.repetitionIndex = this.repetitionIndex; //this is set in the constructor for the repetitionBlock
			//7. If the new repetition block element is in the http://www.w3.org/1999/xhtml namespace, then the
			//   repeat attribute in no namespace on the cloned element has its value changed to the new block's
			//   index. Otherwise, the repeat attribute in the http://www.w3.org/1999/xhtml namespace has its value
			//   changed to the new block's index.
			block.setAttribute('repeat', this.repetitionIndex); //when inserted into DOM, constructor sets block.repetitionIndex

			//8. If the new repetition block element is in the http://www.w3.org/1999/xhtml namespace, then any
			//   repeat-min, repeat-max, or repeat-start attributes in no namespace are removed from the element.
			//   Otherwise, any repeat-min, repeat-max, or repeat-start attributes in the http://www.w3.org/1999/xhtml
			//   namespace are removed instead.

			block.removeAttribute('repeat-min');
			block.removeAttribute('repeat-max');
			block.removeAttribute('repeat-start');

			//(steps 9 and 10 moved to before step 5 (operates on this repetition template, and not on cloned block))

			//12. If the template has a name (see the earlier steps): If the new repetition block element is in the
			//    http://www.w3.org/1999/xhtml namespace, then the repeat-template attribute in no namespace on the
			//    cloned element has its value set to the template's name. Otherwise, the repeat-template attribute
			//    in the http://www.w3.org/1999/xhtml namespace has its value set to the template's name. (This
			//    happens even if the name was ignored for the purposes of the previous step.)
			if(IDAttrName){
				block.setAttribute('repeat-template', IDAttrValue); //block.setAttributeNS(null, 'repeat-template', IDAttr.nodeValue);
				//13. The attribute from which the template's name was derived, if any, and even if it was ignored, is
				//    removed from the new repetition block element. (See the previous four steps.)
				block.removeAttribute(IDAttrName);
			}

			//14. If the first argument to the method was null, then the template once again crawls through its
			//    previous siblings, this time stopping at the first node (possibly the template itself) whose
			//    previous sibling is a repetition block (regardless of what that block's template is) or the first
			//    node that has no previous sibling, whichever comes first. The new element is the inserted into the
			//    parent of the template, immediately before that node. Mutation events are fired if appropriate.
			if(!refNode){
				var refNode = this;
				while(refNode.previousSibling && refNode.previousSibling.repetitionType != RepetitionElement.REPETITION_BLOCK)
					refNode = refNode.previousSibling;
				this.parentNode.insertBefore(block, refNode);
				this.repetitionBlocks.push(block);
			}
			//15. Otherwise, the new element is inserted into the parent of the node that was passed to the method
			//    as the first argument, immediately after that node (before the node's following sibling, if any).
			//    Mutation events are fired if appropriate.
			else {
				refNode.parentNode.insertBefore(block, refNode.nextSibling);
				this.repetitionBlocks.push(block);

				//sort elements by document order (code from PPK: http://www.quirksmode.org/dom/getElementsByTagNames.html)
				if (this.repetitionBlocks[0].sourceIndex){ //Internet Explorer
					this.repetitionBlocks.sort(function (a,b) {
						return a.sourceIndex - b.sourceIndex;
					});
				}
				else if (this.repetitionBlocks[0].compareDocumentPosition){ //Gecko/W3C
					this.repetitionBlocks.sort(function (a,b) {
						return 3 - (a.compareDocumentPosition(b) & 6);
					});
				}
			}

			//16. The template's index is increased by one.
			if (addButtonClicked) {
				this.repetitionIndex++;
			}

			//[apply constructors to the new repetition block, and to the new remove buttons, add buttons, etc]
			RepetitionElement._repetitionBlock_constructor.apply(block);
			RepetitionElement._init_repetitionTemplates(block);

			//CLINOVO - start
			if (addButtonClicked) {
				addButtonClicked = false;
				if (block != undefined) {
					var dnaElement = _getElementsByClassName(block, "dnLink");
					if (dnaElement.length > 0) {
						for (var i = 0; i < dnaElement.length; i++) {
							dnaElement[i].setAttribute("class", "hidden");
							dnaElement[i].setAttribute("className", "hidden");
						}
						var rbtnElement = _getElementsByClassName(block, "button_remove");
						for (var i = 0; i < rbtnElement.length; i++) {
							rbtnElement[i].style.visibility = "visible";
						}
						var ieElement = _getElementsByClassName(block, "aka_input_error");
						for (var i = 0; i < ieElement.length; i++) {
							ieElement[i].setAttribute("class", "");
							ieElement[i].setAttribute("className", "");
						}
						var eeElement = _getElementsByClassName(block, "aka_exclaim_error");
						for (var i = 0; i < eeElement.length; i++) {
							eeElement[i].style.display = "none";
						}
					}
				}
			} else {
				var inputElements = block.getElementsByTagName("input");
				for (var i = 0; i < inputElements.length; i++) {
					if (inputElements[i].type == "radio" && inputElements[i].value == "on") {
						inputElements[i].value = inputElements[i].getAttribute("rel");
					}
				}
			}
			//CLINOVO - end

			RepetitionElement._init_addButtons(block);
			RepetitionElement._init_removeButtons(block);

			//In addition, user agents must automatically disable add buttons (irrespective of the value of the
			//   disabled DOM attribute) when the buttons are not in a repetition block that has an associated
			//   template and their template attribute is either not specified or does not have an ID that points
			//   to a repetition template, and, when the repetition template's repeat-max attribute is less than
			//   or equal to the number of repetition blocks that are associated with that template and that have
			//   the same parent. This automatic disabling does not affect the DOM disabled attribute. It is an
			//   intrinsic property of these buttons.
			if(RepetitionElement._initialized){ //if buttons not yet initialized, will initially be called by _init_document
				RepetitionElement.__updateAddButtons(this);
			}

			//17. An added event with no namespace, which bubbles but is not cancelable and has no default action,
			//    must be fired on the repetition template using the RepetitionEvent interface, with the repetition
			//    block's DOM node as the context information in the element  attribute.
			var addEvt;

			try {
				if(document.createEvent)
					addEvt = document.createEvent("UIEvents"); //document.createEvent("RepetitionEvent")
				else if(document.createEventObject)
					addEvt = document.createEventObject();
				RepetitionEvent._upgradeEvent.apply(addEvt);
				addEvt.initRepetitionEvent("added", true /*canBubble*/, false /*cancelable*/, block);
				if(this.dispatchEvent)
					this.dispatchEvent(addEvt);
			}
			catch(err){
				addEvt = new Object();
				RepetitionEvent._upgradeEvent.apply(addEvt);
				addEvt.initRepetitionEvent("added", true /*canBubble*/, false /*cancelable*/, block);
			}

			//Add support for event handler set with HTML attribute
			var onaddAttr = this.getAttribute('onadd') || /* deprecated */ this.getAttribute('onadded');
			if(onaddAttr && (!this.onadd || typeof this.onadd != 'function')) //in MSIE, attribute == property
				this.onadd = new Function('event', onaddAttr);

			//Dispatch events for the old event model (extension to spec)
			if(this.onadd)
				this.onadd(addEvt);
			else if(this.onadded) //deprecated
				this.onadded(addEvt);

			//Clear the new row's input values, so that the elements in the new rows
			//do not display any values
			if(global_addButtonClicked) {
				clearInputElementValues(block);  //added for functions in OpenClinica 2.2
				changeDNoteIcon(block);
			}

			//18. The return value is the newly cloned element.
			return block;
		},
		//Element addRepetitionBlockByIndex(in Node refNode, in long index);
		addRepetitionBlockByIndex : function(refNode, index){
			RepetitionElement.addRepetitionBlock.apply(this, [refNode, index])
		},

		/*##############################################################################################
		 # RemoveRepetitionBlock algorithm
		 ##############################################################################################*/

		removeRepetitionBlock : function(){
			if(this.repetitionType != RepetitionElement.REPETITION_BLOCK)
				throw Error("DOMException: NOT_SUPPORTED_ERR");

			//1. The node is removed from its parent, if it has one. Mutation events are fired if appropriate.
			//   (This occurs even if the repetition block is an orphan repetition block.)
			var parentNode = this.parentNode; //save for __updateMoveButtons
			var block = parentNode.removeChild(this);

			//The following loop used to appear within step #3 below;
			//  this caused problems because the program state was incorrect when onremove was called (repetitionBlocks was not modified)
			if(this.repetitionTemplate != null){
				for(var i = 0; i < this.repetitionTemplate.repetitionBlocks.length; i++){
					if(this.repetitionTemplate.repetitionBlocks[i] == this){
						this.repetitionTemplate.repetitionBlocks.splice(i,1);
						break;
					}
				}
			}

			//2. If the repetition block is not an orphan, a removed event with no namespace, which bubbles but
			//   is not cancelable and has no default action, must be fired on the element's repetition template,
			//   using the RepetitionEvent interface, with the repetition block's DOM node as the context information
			//   in the element attribute.
			if(this.repetitionTemplate != null){
				var removeEvt;
				try {
					if(document.createEvent)
						removeEvt = document.createEvent("UIEvents"); //document.createEvent("RepetitionEvent")
					else if(document.createEventObject)
						removeEvt = document.createEventObject();
					RepetitionEvent._upgradeEvent.apply(removeEvt);
					removeEvt.initRepetitionEvent("removed", true /*canBubble*/, false /*cancelable*/, this);
					if(this.repetitionTemplate.dispatchEvent)
						this.repetitionTemplate.dispatchEvent(removeEvt);
				}
				catch(err){
					removeEvt = new Object();
					RepetitionEvent._upgradeEvent.apply(removeEvt);
					removeEvt.initRepetitionEvent("removed", true /*canBubble*/, false /*cancelable*/, this);
				}

				//Add support for event handler set with HTML attribute
				var onremoveAttr = this.repetitionTemplate.getAttribute('onremove')
					|| /* deprecated */ this.repetitionTemplate.getAttribute('onremoved');
				if(onremoveAttr && (!this.repetitionTemplate.onremove || typeof this.repetitionTemplate.onremove != 'function')) //in MSIE, attribute == property
					this.repetitionTemplate.onremove = new Function('event', onremoveAttr);

				//Dispatch events for the old event model (extension to spec)
				if(this.repetitionTemplate.onremove)
					this.repetitionTemplate.onremove(removeEvt);
				else if(this.repetitionTemplate.onremoved) //deprecated
					this.repetitionTemplate.onremoved(removeEvt);
			}

			//3. If the repetition block is not an orphan, then while the remaining number of repetition blocks
			//   associated with the original element's repetition template and with the same parent as the template
			//   is less than the template's repeat-min attribute and less than its repeat-max attribute, the
			//   template's replication behaviour is invoked (specifically, its addRepetitionBlock() method is called).
			if(this.repetitionTemplate != null){
				if(this.repetitionTemplate.repetitionBlocks.length < this.repetitionTemplate.repeatMin
					&& this.repetitionTemplate.repetitionBlocks.length < this.repetitionTemplate.repeatMax)
				{
					this.repetitionTemplate.addRepetitionBlock();
				}

				//enable add buttons
				if(this.repetitionTemplate.repetitionBlocks.length < this.repetitionTemplate.repeatMax){
					var addBtns = RepetitionElement.__getElementsByNameAndAttribute.apply(document.body, ['button', 'stype', 'add']);
					for(i = 0; i < addBtns.length; i++){
						if(addBtns[i].htmlTemplate == this.repetitionTemplate)
							addBtns[i].disabled = false;
					}
				}
			}
		},

		/*##############################################################################################
		 # other helper functions (not made into methods)
		 ##############################################################################################*/
		__getRepetitionBlock : function(node){
			while(node = node.parentNode){
				if(node.repetitionType == RepetitionElement.REPETITION_BLOCK){
					return node;
				}
			}
			return null;
		},

		__getHtmlTemplate : function(button){
			var attr = button.getAttribute('template');
			var node;
			if(attr && (node = document.getElementById(attr)) && node.repetitionType == RepetitionElement.REPETITION_TEMPLATE)
				return node;
			return null;
		},

		__updateAddButtons : function(rt){
			//In addition, user agents must automatically disable add buttons (irrespective of the value of the
			//   disabled DOM attribute) when the buttons are not in a repetition block that has an associated
			//   template and their template attribute is either not specified or does not have an ID that points
			//   to a repetition template, and, when the repetition template's repeat-max attribute is less than
			//   or equal to the number of repetition blocks that are associated with that template and that have
			//   the same parent. This automatic disabling does not affect the DOM disabled attribute. It is an
			//   intrinsic property of these buttons.

			var repetitionTemplates = rt ? [rt] : RepetitionElement._repetitionTemplates;

			//var btns = cssQuery("button[type=add]");
			var btns = RepetitionElement.__getElementsByNameAndAttribute.apply(document.body, ['button', 'type', 'add']);
			for(var i = 0; i < btns.length; i++){
				for(var t, j = 0; t = repetitionTemplates[j]; j++){
					if(btns[i].htmlTemplate == t && t.repetitionBlocks.length >= t.repeatMax){
						btns[i].disabled = true;
						//line 1217, changeBtnDisabledState() METHOD DEFINED OUTSIDE OF THIS LIBRARY, IN
						//global_functions_javascript.js
						changeBtnDisabledState(btns[i],'button_add','button_add_disabled',false) //part of OPENCLINICA 2.2 functionality
					}
				}
			}
		},

		/*##############################################################################################
		 # Generic DOM query functions
		 ##############################################################################################*/

		__getElementsByProperty : function(propName, propValue){
			var els = [];
			var all = document.body.getElementsByTagName('*');
			for(i = 0; i < all.length; i++){
				if(all[i][propName] == propValue)
					els.push(all[i]);
			}
			return els;
		},

		__getElementsByTagNames : function(/* ... */){
			//IMPLEMENT XPATH
			var results = [];
			for(var i = 0; i < arguments.length; i++){
				var elements = this.getElementsByTagName(arguments[i]);
				for(var j = 0; j < elements.length; j++){
					results.push(elements[j]);
				}
			}
			return results;
			//sort with document order?
		},

		__getElementsByNameAndAttribute : function(elName, attrName, attrValue, isNotEqual){
			//IMPLEMENT XPATH
			var results = [];
			var all = this.getElementsByTagName(elName);
			for(var i = 0; i < all.length; i++){
				var thisAttrNode = all[i].getAttributeNode(attrName);
				var thisAttrValue = all[i].getAttribute(attrName); //MSIE needs getAttribute here for custom button types to be read
				if(thisAttrNode && (typeof attrValue == 'undefined' || (isNotEqual ? thisAttrValue != attrValue : thisAttrValue == attrValue) )){
					results.push(all[i]);
				}
			}
			return results;
		},

		//The following function enables MSIE to create elements with the name attribute set, per MSDN:
		//   The NAME attribute cannot be set at run time on elements dynamically created with the
		//   createElement method. To create an element with a name attribute, include the attribute
		//   and value when using the createElement method.
		__createElementWithName : function(type, name){
			throw Error("__createElementWithName not yet created. Browser-specific code defined immediately below.");
		}
	};
	(function(){
		try {
			var el = document.createElement('<div name="foo">');
			if(el.tagName.toLowerCase() == 'div' || el.name != 'foo'){
				throw 'create element error';
			}
			RepetitionElement.__createElementWithName = function(tag, name){
				return document.createElement('<'+tag+' name="'+name+'"></'+tag+'>');
			};
		}
		catch(err){
			RepetitionElement.__createElementWithName = function(tag, name){
				var el = document.createElement(tag);
				el.setAttribute('name', name);
				//el.name = name;
				return el;
			};
		}
	})();



	/*##############################################################################################
	 # RepetitionEvent
	 ##############################################################################################*/

	var RepetitionEvent = {};

	//the following takes a UIEvent and adds the required properties for a RepetitionEvent
	RepetitionEvent._upgradeEvent = function(){
		this.initRepetitionEvent = RepetitionEvent.initRepetitionEvent;
		this.initRepetitionEventNS = RepetitionEvent.initRepetitionEventNS;
	};
	RepetitionEvent.initRepetitionEvent = function(typeArg, canBubbleArg, cancelableArg, elementArg){
		if(this.initEvent)
			this.initEvent(typeArg, canBubbleArg, cancelableArg);
		else {
			this.type = typeArg;

			if(!this.preventDefault)
				this.preventDefault = function(){
					this.returnValue = false;
				};
			if(!this.stopPropagation)
				this.stopPropagation = function(){
					this.cancelBubble = true;
				};
		}
		this.element = elementArg;
		this.relatedNode = elementArg; //for Opera (deprecated?)
	};
	RepetitionEvent.initRepetitionEventNS = function(namespaceURIArg, typeArg, canBubbleArg, cancelableArg, elementArg){
		throw Error("NOT IMPLEMENTED: RepetitionEvent.initRepetitionEventNS");
	};


	/*##############################################################################################
	 # Initializing the Repetition Model in the document
	 ##############################################################################################*/

	$(window).load(function() {
		RepetitionElement._init_document();
		giveFirstElementFocus();
	});
} //End If(!window.RepetitionElement...

//Extend the WebForms 2.0 Repetition Model to allow for the old event model
else if(document.addEventListener &&
	(typeof(RepetitionElement.oldEventModelEnabled) == 'undefined' || RepetitionElement.oldEventModelEnabled)
){
	RepetitionElement.oldEventModelEnabled = true;

	//added event----------------
	document.addEventListener("added", function(evt){
		if(!RepetitionElement.oldEventModelEnabled)
			return;
		if(!evt.element && evt.relatedNode) //Opera uses evt.relatedNode instead of evt.element as the specification dictates
			evt.element = evt.relatedNode;
		if(!evt.element || !evt.element.repetitionTemplate)
			return;

		var rt = evt.element.repetitionTemplate;

		//Add support for event handler set with HTML attribute
		var onaddAttr = rt.getAttribute('onadd') || /* deprecated */ rt.getAttribute('onadded');
		if(onaddAttr && (!rt.onadd || typeof rt.onadd != 'function')) //in MSIE, attribute == property
			rt.onadd = new Function('event', onaddAttr);

		if(evt.element.repetitionTemplate.onadd)
			evt.element.repetitionTemplate.onadd(evt);
		else if(evt.element.repetitionTemplate.onadded) //deprecated
			evt.element.repetitionTemplate.onadded(evt);
	}, false);

	//removed event----------------
	document.addEventListener("removed", function(evt){
		if(!RepetitionElement.oldEventModelEnabled)
			return;
		if(!evt.element && evt.relatedNode) //Opera uses evt.relatedNode instead of evt.element as the specification dictates
			evt.element = evt.relatedNode;
		if(!evt.element || !evt.element.repetitionTemplate)
			return;

		var rt = evt.element.repetitionTemplate;

		//Add support for event handler set with HTML attribute
		var onremoveAttr = rt.getAttribute('onremove') || /* deprecated */ rt.getAttribute('onremoved');
		if(onremoveAttr && (!rt.onremove || typeof rt.onremove != 'function')) //in MSIE, attribute == property
			rt.onremove = new Function('event', onremoveAttr);

		if(evt.element.repetitionTemplate.onremove)
			evt.element.repetitionTemplate.onremove(evt);
		else if(evt.element.repetitionTemplate.onremoved) //deprecated
			evt.element.repetitionTemplate.onremoved(evt);
	}, false);

	//moved event----------------
	document.addEventListener("moved", function(evt){
		if(!RepetitionElement.oldEventModelEnabled)
			return;
		if(!evt.element && evt.relatedNode) //Opera uses evt.relatedNode instead of evt.element as the specification dictates
			evt.element = evt.relatedNode;
		if(!evt.element || !evt.element.repetitionTemplate)
			return;

		var rt = evt.element.repetitionTemplate;

		//Add support for event handler set with HTML attribute
		var onmoveAttr = rt.getAttribute('onmove') || /* deprecated */ rt.getAttribute('onmoved');
		if(onmoveAttr && (!rt.onmove || typeof rt.onmove != 'function')) //in MSIE, attribute == property
			rt.onmove = new Function('event', onmoveAttr);

		if(evt.element.repetitionTemplate.onmove)
			evt.element.repetitionTemplate.onmove(evt);
		else if(evt.element.repetitionTemplate.onmoved) //deprecated
			evt.element.repetitionTemplate.onmoved(evt);
	}, false);
}

//*** END REPETITION MODEL IMPLEMENTATION CODE ***************************************************************

if (!Array.prototype.some)
{	//http://www.dustindiaz.com/basement/sugar-arrays.html
	Array.prototype.some = function(fn, thisObj) {
		var scope = thisObj || window;
		for ( var i=0, j=this.length; i < j; ++i ) {
			if ( fn.call(scope, this[i], i, this) ) {
				return true;
			}
		}
		return false;
	};
}

function isOptionNode(node) {
	return node.tagName && ((node.tagName.indexOf("OPTION") != -1) || (node.tagName.indexOf("option") != -1));
}

function isOptionSelected(node) {
	return node.outerHTML.indexOf("SELECTED") != -1 || node.outerHTML.indexOf("selected") != -1;
}
