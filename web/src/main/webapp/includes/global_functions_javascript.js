var currentPopupUid;
var subjectMatrixPopupStick;
var popupInterval;
var firstFormState;
var crfShortcutsBoxState = 0;
var currentHighlightedShortcutAnchor;
var currentHighlightedShortcutAnchorInd;
var currentHighlightedShortcutAnchorCounter;
var crfShortcutAnchors = ["newDn_", "updatedDn_", "resolutionProposedDn_", "closedDn_", "annotationDn_", "itemToSDV_"];
var rowHighlightTypes = {NORMAL: 0, ROWSPAN: 1, MULTIPLE: 2};

var pageTitle_SMForAllOfEvents = "/ListStudySubjects";
var pageTitle_SMForSelectedEvent = "/ListEventsForSubjects";

var windowScrollLeft = 0;
var crfShortcutsTableDefTop = 174;
var crfShortcutsTableDefLeft = 78;

var autotabbingCurrentElementName = "";

function selectAllChecks(formObj,value){
    if(formObj) {
        var allChecks = formObj.getElementsByTagName("input");
        for(var i = 0; i < allChecks.length; i++){
            if(allChecks[i] && allChecks[i].getAttribute &&
               allChecks[i].getAttribute("type") &&
               allChecks[i].getAttribute("type").indexOf("checkbox") != -1  ) {

                allChecks[i].checked = value;
            }
        }
    } 
}

if (!Array.prototype.forEach) {
	Array.prototype.forEach = function(fn, scope) {
		for ( var i = 0, len = this.length; i < len; ++i) {
			fn.call(scope, this[i], i, this);
		}
	};
}

function checkGoBackEntryStatus(strImageName, Message, submit) {
    closing = false;        
    objImage = MM_findObj(strImageName);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
		return confirmSubmit({
			message : Message,
			height : 150,
			width : 500,
			submit : submit
		});
    } else {
		$(submit).submit();
    }
    return true;
}

function checkGoToEntryStatus(strImageName, Message, Adress) {
    closing = false;        
    objImage = MM_findObj(strImageName);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        return confirmGoTo(Message, Adress);
    } else {
        window.location.href=(Adress);
    }
    return true;
}
function confirmGoTo(Message, Address){
	confirmDialog({ message: Message, height: 150, width: 500, redirectLink: Address });
}
function confirmBack(Message){
	return confirmSubmit({ message: Message, height: 150, width: 500, goBack: true });
}

function changeIcon(){
	setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');
}

function goBackSmart(servletURL, defaultURL) {
    jQuery.ajax({
        url:servletURL,
        type: 'GET',
        dataType: 'text',
        success: function(responseURL){
			if (responseURL.search("DOCTYPE") === -1 ) {
				window.location.href = responseURL;
			} else {
				//window.history.go(-1);
				window.location.href = defaultURL;
            }
        }
    });
}

function checkGoBackSmartEntryStatus(strImageName, Message, servletURL, defaultURL) {
    closing = false;
    objImage = MM_findObj(strImageName);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        return confirmBackSmart(Message, servletURL, defaultURL, undefined);
    } else {
        goBackSmart(servletURL, defaultURL);
    }
    return true;
}

function confirmBackSmart(Message, servletURL, defaultURL){
	
	$("<div id='confirmDialog' title='Confirm Action'>" +
	        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
	        Message +
	        "</div></div>").appendTo("body");
	
    $("#confirmDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: 150,
        width: 500,
        buttons:{ 
        	'Yes': function() {
            	$("#confirmDialog").remove();
            	goBackSmart(servletURL, defaultURL);
        	},
            'No': function() { 
            	$("#confirmDialog").remove(); 
            }
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	cancelButtonValue: "No", 
            	okButtonValue: "Yes", 
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
}

function datasetConfirmBack(message, formId, address, action) {
	var newFormState = $("#" + formId).serialize();
	var confirm1 = $("#" + formId).length != 0
    && newFormState != firstFormState;
	
	if(!confirm1){
		confirmBackSubmitForm(formId, address, action);
		return true;
	}		
	
	$("<div id='confirmDialog' title='Confirm Action'>" +
	        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
	        message +
	        "</div></div>").appendTo("body");
	
    $("#confirmDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: 150,
        width: 500,
        buttons:{ 
        	'Yes': function() {
        		confirmBackSubmitForm(formId, address, action);
        	},
            'No': function() { 
            	$("#confirmDialog").remove(); 
            }
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	cancelButtonValue: "No", 
            	okButtonValue: "Yes", 
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
}

function checkMetaDataVersionODMID(metaDataVersionODMID, messageToDisplay, formToSubmit, isCurrentScopeStudy) {

	if (!metaDataVersionODMID && isCurrentScopeStudy) {

		return confirmSubmit({
			message : messageToDisplay,
			height : 180,
			width : 500,
			form : formToSubmit
		});
	} else {

		formToSubmit.submit();
	}
}

function confirmBackSubmitForm(formId, address, action) {
	$("#" + formId)[0].setAttribute("action", address);
    $("#" + formId + " input[name=action]").val(action);
    $("#" + formId + " input[id=btnSubmit]")[0].setAttribute("onclick", "");
    $("#" + formId + " input[id=btnSubmit]").click();
}

function submitFormWithState() {
    if ($("#formWithStateFlag").length > 0) {
        $("#formWithStateFlag").parent("form").submit();
    }
}

function formWithStateGoBackSmart(message, servletURL, defaultURL) {
  var newFormState = $("#formWithStateFlag").parent("form").find("input[type=checkbox],input[type=radio],input[type=text],input[type=password],input[type=file],textarea,select").serialize();
  var confirm1 = newFormState != firstFormState || $("#formWithStateFlag").val().toLowerCase() != "";
  if (confirm1) {
	  confirmBackSmart(message, servletURL, defaultURL);
  }
  else {
	  goBackSmart(servletURL, defaultURL);
  }
  return true;
}

function formWithStateConfirmGoTo(message, address) {
  var newFormState = $("#formWithStateFlag").parent("form").find("input[type=checkbox],input[type=radio],input[type=text],input[type=password],input[type=file],textarea,select").serialize();
  var confirm1 = newFormState != firstFormState || $("#formWithStateFlag").val().toLowerCase() != "";
  if (confirm1) {
	  confirmDialog({ message: message, height: 150, width: 500, redirectLink: address });
  }
  else {
	  window.location.href = address;
    }
    return true;
}

function initFieldCopying(item, target) {

	$(target).val($(item).val());
	$(item).blur(function() {
		$(target).val($(item).val());
	});
}

function hideCols(tableId,columnNumArray,showTable){
    var theStyle;
    if (showTable) {
        theStyle = '';
        if(document.getElementById('showMore') && document.getElementById('hide')){
            document.getElementById('showMore').style.display='none';
            document.getElementById('hide').style.display='';
            document.getElementById('showMoreLink').value="false";
        }
    }
    else  {
        theStyle = 'none';
        if(document.getElementById('showMore') && document.getElementById('hide')){
            document.getElementById('showMore').style.display='';
            document.getElementById('hide').style.display='none';
            document.getElementById('showMoreLink').value="true";
        }
    }
    
    var tbl  = document.getElementById(tableId);
    var tbodys = tbl.getElementsByTagName('tbody');

    var _tbody = tbodys[0];
    for(var i = 0; i < tbodys.length;i++){
        if(tbodys[i].className &&
           tbodys[i].className.indexOf('tbody') != -1) {
            _tbody=tbodys[i];
        }
    }
    
    var rows = _tbody.getElementsByTagName('tr');
    
    var theads = tbl.getElementsByTagName('thead');
    
    var thead = theads[0];
    
    var theadRows = thead.getElementsByTagName('tr');

	
    for (var th=0; th<theadRows.length;th++) {
		
		if(theadRows[th].className &&
		  (theadRows[th].className.indexOf('filter') != -1 ||
			theadRows[th].className.indexOf('header') != -1)) {
			var headCels = theadRows[th].getElementsByTagName('td');
			for(var k=0; k < headCels.length; k++){
				for(var x=0; x<columnNumArray.length;x++)  {

					if(columnNumArray[x] == k){
						
						headCels[k].style.display=theStyle;
					}
				}
			}
		}
    }
    
    for (var row=0; row<rows.length;row++) {
        var cels = rows[row].getElementsByTagName('td');

        var tableRowIds = rows[row].getAttribute('id');
        if ( tableRowIds != null && tableRowIds.match(tableId + "_row") != null ){
        	for(var j=0; j < cels.length; j++){
        		for(var x=0; x<columnNumArray.length;x++)  {
        			if(columnNumArray[x]  == j){
        				cels[j].style.display=theStyle;
        			}
        		}
        	}
        }
    }

    highlightLastAccessedObject();
}

function toggleName(str){

    if(str.innerHTML == 'Show'){
        str.innerHTML='Hide';
    }  else {
        str.innerHTML='Show';
    }
}
function changeOpenDivButton(imgObject) {

    if(imgObject && imgObject.src.indexOf('sidebar_collapse.gif') != -1){

        $(imgObject.parentNode.nextSibling).hide();
        imgObject.src='../images/sidebar_expand.gif';
    } else if (imgObject)

    {imgObject.src='../images/sidebar_collapse.gif';
        $(imgObject.parentNode.nextSibling).show();}
}
function myCancel(objButtonText) {

    var cancelButton=document.getElementById('cancel');

    if ( cancelButton != null) {
        if(confirm(objButtonText)) {
            window.location.href="ListStudy";
            return true;
        } else {
            return false;
        }
    }
    return true;

}

/*
 This method is called as part of a window.onload handler. It places the focus
 on the first element in a CRF's data-entry form. SEE THE REPETITION-MODEL.JS FILE,
 BEGINNING LINE 1466 FOR WHERE THIS METHOD IS CALLED. The
 REPETITION-MODEL.JS file gets a handle to the window.load event handler,
 so the actual JSP page's body.onload handler cannot be called.
 */
function giveFirstElementFocus(){

    var element = document.getElementById("hasPopUp");
    if (element != null) {
        var hasPopUp = element.value;
        if (hasPopUp == 1) {
            return;
        }
    }
    var frm = document.getElementById("mainForm");
    if(frm == null){
        return;
    }
    var selects = frm.getElementsByTagName("SELECT");
    var textareas = frm.getElementsByTagName("TEXTAREA");

    var firstField = document.getElementById("formFirstField");
    var fieldId;
    if(firstField){
        fieldId = firstField.value;
    }
    if(selects) {
        for(var i = 0; i <=selects.length;i++) {
            if(selects[i] &&
               selects[i].id && selects[i].id.indexOf(fieldId) != -1) {
                selects[i].focus();
                return;
            }

        }
    }
    var inputs = frm.getElementsByTagName("INPUT");

    if(inputs){
        for(var j = 0; j <=inputs.length;j++) {
            if(inputs[j] &&
               inputs[j].id &&
               (inputs[j].getAttribute("type")) &&
               (inputs[j].getAttribute("type").indexOf("hidden") == -1)){

                if(inputs[j].id.indexOf(fieldId) != -1) {
                    inputs[j].focus();
                    return;
                }
            }

        }
    }

    if(textareas){
        for(var h = 0; h <=textareas.length;h++) {
            if(textareas[h] &&
               textareas[h].id && textareas[h].id.indexOf(fieldId) != -1) {
                textareas[h].focus();
                return;
            }

        }
    }

}

/**
 * Display a sequence of three tabs (implemented as TD elements) in a JSP view;
 * by selecting only the tabs or TD elements that have a certain class name.
 * @param tabNumber The numbered position of the currently selected tab, like 3
 * @param totalNumberOfTabs  The total number of tabs or TD elements in the collection.
 * @param tabClassName The name of the class that specifies the tab.
 */
function selectTabs(tabNumber,totalNumberOfTabs,tabClassName) {

    if((! tabNumber) || tabNumber < 1){
        tabNumber = 1;
    }
    if((! tabClassName) || tabClassName == ""){
        tabClassName = "crfHeaderTabs";
    }
    if(totalNumberOfTabs == null || totalNumberOfTabs == 0) return;
    var param = 'TD.'+tabClassName;
    //in terms of array element selection, set tabNumber to tabNumber - 1
    tabNumber = tabNumber - 1;
    var firstTabSelected = tabNumber == 0; //the first three tabs should be selected
    //the last three tabs should be selected
    var lastTabSelected = (tabNumber == (totalNumberOfTabs - 1));
    //fetches all TD elements with a class name of tabClassName
    var allTabs = prt$$(param);
    var tdCount = allTabs.length;
    //If there are not more than two tabs, just one or two, then all we have to do is
    //select one or two TD elements, and not worry about deselecting others
    var moreThanTwoTabs = (tdCount > 2);

    if(moreThanTwoTabs && firstTabSelected) {
        //"true" means select the first three of the collection
        selectThreeTabs(allTabs,true);
        return;
    }

    if(moreThanTwoTabs && lastTabSelected) {
        //"false" means select the last three of the collection
        selectThreeTabs(allTabs,false);
        return;
    }

    //if there are just 1 to 2 tabs, select the chosen one, and make sure the
    //other tab is displayed
    if(! moreThanTwoTabs) {
        for(var i = 0; i < tdCount; i++) {
            allTabs[i].style.display = "";

        }
        return;
    }
    //select the TD with position tabNumber, and any sibling TDs
    //before and after it, if tab numbers > 2

    if(moreThanTwoTabs) {
        if(! allTabs[tabNumber]){ return;}

        allTabs[tabNumber].style.display = "";
      //cycle through all the tabs; the first sibling of the selected tab
        //will have number tabNumber - 1, the next sibling with have
        //tabNumber + 1; all others should be display = "none"
        
    	if(tabNumber==2)
    	{
    		 for(var i = 0; i < tdCount; i++) {
    	
    	   if(i == tabNumber)  continue;  //already displayed

                if(i == tabNumber - 2 || i == tabNumber - 1){ //prev sibling or next sibling

                 continue;

                } else {
                    allTabs[i].style.display = "none";

                }
            }
    	}
    	else{
            for(var i = 0; i < tdCount; i++) {
                if(i == tabNumber)  continue;  //already displayed

                if(i == tabNumber - 1 || i == tabNumber + 1){ //prev sibling or next sibling

                    allTabs[i].style.display = "";

                } else {
                    allTabs[i].style.display = "none";

                }
            }
    	}
    }   
}

/*
 true in the second parameter means select the first three tabs; otherwise select the last three
 */
function selectThreeTabs(arrayOfTDElements,firstOrLastThree){
    if(firstOrLastThree){
        for(var i = 0; i < arrayOfTDElements.length; i++){
            if(i >= 0 && i <= 2) {

                arrayOfTDElements[i].style.display="";

            } else {

                arrayOfTDElements[i].style.display="none";

            }
        }
    }//true is select first three
    else {  //select the last three elements
        for(var j = arrayOfTDElements.length-1; j >= 0; j--){
            if(j <= arrayOfTDElements.length-1 && j >= arrayOfTDElements.length-3) {

                arrayOfTDElements[j].style.display="";

            } else {

                arrayOfTDElements[j].style.display="none";

            }

        }
    }
}

function setNewIconInParentWin(idOfImageElement, imageLocation){

    var imgObject = window.opener.document.getElementById(idOfImageElement);
    
    if(imgObject) {
        imgObject.src = imageLocation;
    }
    
    imgObject.title = 'Refresh or re-enter this form to view any new discrepancy notes.';
    imgObject.parentNode.onclick=function(){
        return false;
    };
}

function showSummaryBox(divObject, parentLinkObj, showText, hideText) {

    var sumBox = document.getElementById(divObject);
    var pathname = window.location.pathname;
    var color = $('*').find('a').css('color');

    if (sumBox && sumBox.style.display == "none") {

        sumBox.style.display = "block";

        if (pathname.indexOf("pages") > 0) {

            if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6') {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/violet/bt_Collapse.gif" border="0"> ' + hideText;
            } else if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894') {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/green/bt_Collapse.gif" border="0"> ' + hideText;
            } else {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/bt_Collapse.gif" border="0"> ' + hideText;
            }

        } else {

            if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6') {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/violet/bt_Collapse.gif" border="0"> ' + hideText;
            } else if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894') {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/green/bt_Collapse.gif" border="0"> ' + hideText;
            } else {
                parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/bt_Collapse.gif" border="0"> ' + hideText;
            }
        }

    } else {

        if (sumBox) {

            sumBox.style.display = "none";

            if (pathname.indexOf("pages") > 0) {

                if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6') {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/violet/bt_Expand.gif" border="0"> ' + showText;
                } else if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894') {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/green/bt_Expand.gif" border="0"> ' + showText;
                } else {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="../images/bt_Expand.gif" border="0"> ' + showText;
                }

            } else {
                
                if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6') {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/violet/bt_Expand.gif" border="0"> ' + showText;
                } else if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894') {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/green/bt_Expand.gif" border="0"> ' + showText;
                } else {
                    parentLinkObj.innerHTML = '<img name="ExpandGroup1" src="images/bt_Expand.gif" border="0"> ' + showText;
                }
            }

        }
    }
}

function getSib(theSibling){
    var sib;
    do {
        sib  = theSibling.previousSibling;
        if(sib.nodeType != 1){
            theSibling = sib;
        }
    } while(! (sib.nodeType == 1))

    return sib;
}

/* Return true, if the browser used is IE6 or IE7. */
function detectIEWindows(userAgentString) {
    return ((userAgentString.indexOf("MSIE") != -1) &&
            (userAgentString.indexOf("Windows") != -1) &&
            (userAgentString.indexOf("compatible") != -1));
}

/* Return true, if the browser used is Firefox on Windows. */
function detectFirefoxWindows(userAgentString) {
    return /Firefox[\/\s](\d+\.\d+)/.test(userAgentString);
}

/*change a button to a new CSS class if the button is in a disabled state.
 THIS METHOD IS USED BY LINES 306 AND 1221 in the file repetition-model.js*/
function changeBtnDisabledState(buttonObj,cssOnStateClass,
                                cssDisabledClass,onState){
    if(buttonObj == null || buttonObj == undefined) return;
    if(cssOnStateClass == null || cssOnStateClass == undefined) return;
    if(cssDisabledClass == null || cssDisabledClass == undefined) return;

    if(buttonObj && buttonObj.removeClassName && buttonObj.addClassName &&
       buttonObj.disabled && ! onState)  {
        buttonObj.removeClassName(cssOnStateClass);
        buttonObj.addClassName(cssDisabledClass);
    }
    if(buttonObj && buttonObj.removeClassName && buttonObj.addClassName && (! buttonObj.disabled) && onState)  {
        buttonObj.removeClassName(cssDisabledClass);
        buttonObj.addClassName(cssOnStateClass);
    }
}

/*A METHOD CALLED BY THE WEB 2.0 FORMS JS LIBRARY, LINE 942.
 The method clears all the values in a new row added by this library.  All of the
 inputs values are set to empty Strings, or checked inputs are unchecked. */
function clearInputElementValues(trElement) {
    if(! trElement)  { return; }

    var tdElements = trElement.getElementsByTagName('td');


    //variables representing all inputs, selects, textareas, and options
    //in the new row
    var inputs;
    var selects;
    var textareas;
    var options;
    var myDiv;
    var myId="";
    if(tdElements){
        for(var i = 0; i < tdElements.length; i++) {
            if(tdElements[i]) {
			    var rp=-1;
			    var rm=-1;
			    var myDivEls="";
                inputs = tdElements[i].getElementsByTagName('input');
                selects= tdElements[i].getElementsByTagName('select');

                textareas = tdElements[i].getElementsByTagName('textarea');
                //for file datatype, please reference to showGroupItemInput.jsp 
                myDiv = tdElements[i].getElementsByTagName('div');
                if(myDiv) {
	                //for file datatype, which only have one <div> with id as "div+inputname"
	                if(myDiv[0] && myDiv[0].getAttribute("id").startsWith("div")) {
		            	myId = myDiv[0].getAttribute("id").substring(3);
		            	myDivEls = myDiv[0].getElementsByTagName('a');
		            	if(myDivEls.length<=0) {
			            	myDivEls = myDiv[0].getElementsByTagName('del');
		            	}
	           		}
            	}
                if(inputs) {
                    for(var j = 0; j < inputs.length; j++){
                        if(inputs[j]){
                            if(inputs[j].getAttribute("type") &&
                               (inputs[j].getAttribute("type").indexOf("checkbox") != -1 ||
                                inputs[j].getAttribute("type").indexOf("radio") != 1)){
                                inputs[j].removeAttribute("checked");
                                inputs[j].checked=false;
                            }
                            if(inputs[j].getAttribute("type") &&
                               inputs[j].getAttribute("type").indexOf("text") != -1) {
                                inputs[j].setAttribute("value","");
                            }
                            //remove two buttons, Replace, Remove, for File datatype.if(inputs[j].getAttribute("type") &&
                           if(inputs[j].getAttribute("type") &&
                               inputs[j].getAttribute("type").indexOf("button") != -1 &&
                               inputs[j].getAttribute("id") == "rp"+myId) {
	                               rp = j;
                           }
                           if(inputs[j].getAttribute("type") &&
                               inputs[j].getAttribute("type").indexOf("button") != -1 &&
                               inputs[j].getAttribute("id") == "rm"+myId) {
	                               rm = j;
                           }
                           if(inputs[j].getAttribute("type") &&
                               inputs[j].getAttribute("type").indexOf("hidden") != -1 &&
                               inputs[j].getAttribute("id") == "hidft"+myId) {
		                           inputs[j].setAttribute("id", "ft"+myId);
		                           try {
			                           inputs[j].setAttribute("type", "text");
	                               } catch (e) {
		                               var newElement = null;
		                               var nameStr = inputs[j].getAttribute("name");
		                               try {
			                           		newElement = document.createElement("<input type=\"text\" id=\"ft" + myId 
			                           		+ "\" name=\"" + nameStr + "\" disabled=\"disabled\">");
		                               }catch(e){}
		                               inputs[j].parentNode.replaceChild(newElement,inputs[j]);
	                           	   }
                           }
                           if(inputs[j].getAttribute("type") &&
                               inputs[j].getAttribute("type").indexOf("hidden") != -1 &&
                               inputs[j].getAttribute("id") == "hidup"+myId) {
	                               inputs[j].setAttribute("id", "up"+myId);
	                               try {
	                               		inputs[j].setAttribute("type", "button");
                               	   } catch (e) {
	                               	   var newElement = null;
	                               	   var nameStr = inputs[j].getAttribute("name");
	                               	   var valueStr = inputs[j].getAttribute("value");
		                               try {
			                           		newElement = document.createElement("<input type=\"button\" id=\"up\"" + myId 
			                           		+ "\" name=\"" + nameStr + "\" value=\"" + valueStr + "\">");
			                           		newElement.onclick = inputs[j].onclick;
		                               }catch(e){}
		                               inputs[j].parentNode.replaceChild(newElement,inputs[j]);
                               	   }
                           }
                        }
                    }
                }
                
                if(rp>=0) {
                	tdElements[i].removeChild(inputs[rm]);
                	tdElements[i].removeChild(inputs[rp]);
                	if(myDivEls[0]) {
	                	myDiv[0].removeChild(myDivEls[0]);
                	}
            	}
                /* select element behavior removed for 2791: */

                if(selects) {
                    for(var h = 0; h < selects.length; h++){
                        if(selects[h]){
                            options = selects[h].getElementsByTagName("option");
                            if(options){
                                if(! detectIEWindows(navigator.userAgent)){
                                    for(var k = 0; k < options.length; k++){
                                        if(options[k]) {
                                            options[k].selected=false;
                                        }

                                    }
                                }
                                
                                if(detectIEWindows(navigator.userAgent)){
                                    selects[h].selectedIndex=0;
                                }
                            }
                        }
                    }

                }

                if(textareas) {
                    for(var m = 0; m < textareas.length; m++){
                        if(textareas[m]) {
                            textareas[m].innerHTML="";
                        }
                    }
                }
            }
        }
    }
}
function changeDNoteIcon(trElement) {

    if(! trElement)  { return; }
    var tdElements = trElement.getElementsByTagName('td');

    var hrefElements;

    if(tdElements) {
        for(var i =0; i < tdElements.length; i++)  {
            hrefElements = tdElements[i].getElementsByTagName('a');
            if(hrefElements) {
                for(var j =0; j < hrefElements.length; j++)  {
                    if(hrefElements[j].childNodes){
                        for(var h = 0; h < hrefElements[j].childNodes.length; h++){
                            checkImgIcon(hrefElements[j].childNodes[h]);
                        }
                    }
                }
            }
        }
    }
}

function checkImgIcon(imgObject) {

    if(! imgObject) {
        return;
    }
    
    if(imgObject.src && (imgObject.src.indexOf("images/icon_Note.gif") != -1)) {

        imgObject.src = "images/icon_noNote.gif";
    }
}

function submitWithNewParam(formElement,paramName,paramValue) {
    if(formElement == null || formElement == undefined) { return; }
    var hiddenElement = document.createElement("input");
    hiddenElement.setAttribute("type","hidden");
    hiddenElement.setAttribute("name",paramName);
    hiddenElement.setAttribute("value",paramValue);
    formElement.appendChild(hiddenElement);
    formElement.submit();

}

/* show or hide using prototype */
function show(objId){
    $(objId).show();
}
function hide(objId){
    $(objId).hide();
}
/* Taking care of IE6 bug vis a vis the repetition model JavaScript library. If
 a radio button is clicked, it's sibling radios are unchecked.  The radioObject parameter is the
 radio input element DOM object;  the configuration refers to the Strings "vertical"  or
 "horizontal".  If the radio buttons have a horizontal configuration, then they are each locate din a different
 TD tag, and the JavaScript has to iterate the DOM differently in order to uncheck the right radio button.
 */
function unCheckSiblings(radioObject,
                         configuration){
    var allSibs;
    if(configuration == null || configuration == undefined) { return;}
    if(radioObject == null || radioObject == undefined) { return;}

    if(configuration.indexOf('horizontal') == -1)  {

        var nextSib = radioObject.nextSibling;
        var preSib = radioObject.previousSibling;
        do{
            unCheckObject(nextSib);
            if(nextSib) {
                nextSib = nextSib.nextSibling;
            }
        }  while(nextSib)

        do{
            unCheckObject(preSib);
            if(preSib) {
                preSib = preSib.previousSibling;
            }
        }  while(preSib)

    } else {
        var name = radioObject.getAttribute("name");
        //Get radio elements in adjacent TD cells that have the same name
        //then uncheck them
        var allTDs = $(radioObject).up().siblings();
        var _elements;
        if(allTDs)  {
            for(var j = 0; j < allTDs.length; j++){
                if(allTDs[j])   {
                    if($(allTDs[j]).childElements)  {
                        _elements=$(allTDs[j]).childElements();
                    } else {
                        continue;
                    }
                    if(_elements)  {
                        for(var k = 0; k < _elements.length; k++){
                            if(_elements[k] && _elements[k].tagName.indexOf("INPUT") != -1 &&
                               _elements[k].getAttribute('type').indexOf('radio') != -1 &&
                               _elements[k].getAttribute('name') &&
                               _elements[k].getAttribute('name').indexOf(name) != -1){
                                _elements[k].checked=false;
                            }
                        }
                    }
                } 
            }
        }
    }

}

function unCheckObject(radioObject) {
    if(radioObject && radioObject.tagName &&
       radioObject.tagName.indexOf("INPUT") != -1 &&
       radioObject.getAttribute('type').indexOf('radio') != -1){
        radioObject.checked=false;
    }

}
function isCheckedRadioOrCheckbox(inputObject){
    if(inputObject == null || inputObject == undefined)  { return false; }
    var typ=inputObject.getAttribute('type');
    if(typ != null && (typ.indexOf('checkbox') != -1 ||
                       typ.indexOf('radio') != -1)){

        return inputObject.checked;

    }

    return false;
}

var setCookie = function(c_name, value, exdays) {
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var c_value = escape(value) + (exdays == null ? "" : "; expires="+exdate.toUTCString());
    document.cookie = c_name + "=" + c_value;
};

var getCookie = function(c_name) {
    var i, x, y, ARRcookies = document.cookie.split(";");
    for (i = 0; i < ARRcookies.length; i++) {
        x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
        y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
        x  = x.replace(/^\s+|\s+$/g,"");
        if (x == c_name) {
            return unescape(y);
        }
    }
};

var markCRFCompleteOk = function(checkboxObjectName) {
    $("#confirmation").dialog("close");
    var checkboxObjects = document.getElementsByName(checkboxObjectName);
    if(checkboxObjects[0]){
        checkboxObjects[0].checked=true;
    }
    if(checkboxObjects[1]){
        checkboxObjects[1].checked=true;
    }
};

var markCRFCompleteCancel = function(checkboxObjectName) {
    $("#confirmation").dialog("close");
    var checkboxObjects = document.getElementsByName(checkboxObjectName);
    if(checkboxObjects[0]){
        checkboxObjects[0].checked=false;
    }
    if(checkboxObjects[1]){
        checkboxObjects[1].checked=false;
    }
};

var shouldShowDialog = function() {
    var result = true;
    var value = getCookie("ignoreMarkCRFCompleteMSG");
    if (value == "yes") {
        result = false;
    }
    return result;
};

/* Only display the confirm dialogue box if the checkbox was checked
 when the user clicked it; then uncheck the checkbox if the user chooses "cancel"
 in the confirm dialogue. */
function displayMessageFromCheckbox(checkboxObject, dde){
    if(checkboxObject != null && checkboxObject.checked){

        if ($("#confirmation").length == 0) {
            $("body").append(
                "<div id=\"confirmation\" title=\"Mark CRF Complete\">" +
                    (dde == undefined ? "<div style=\"clear: both; text-align: justify;\">Marking this CRF complete will finalize data entry. You will be allowed to edit the data later but this data entry stage is completed. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?</div>"
                                      : "<div style=\"clear: both; text-align: justify;\">Marking this CRF complete will prepare it for Double Data Entry, where another user will enter data and then be able to finally complete this CRF.</div>") +
                    "<div style=\"clear: both; padding: 6px;\"><input type=\"checkbox\" id=\"ignoreMarkCRFCompleteMSG\"/> Do not show this message anymore.</div>" +
                    "<div style=\"clear: both;\">" +
                        "<input type=\"button\" value=\"Yes\" class=\"button_medium\" onclick=\"markCRFCompleteOk('" + checkboxObject.name + "');\" style=\"float: left;\">" +
                        "<input type=\"button\" value=\"No\" class=\"button_medium\" onclick=\"markCRFCompleteCancel('" + checkboxObject.name + "');\" style=\"float: left; margin-left: 6px;\">" +
                    "</div>" +
                "</div>");

            $("#confirmation").dialog({
                autoOpen : false,
                closeOnEscape: false,
                modal : true,
                height: (dde == undefined ? 180 : 150),
                width: 450}
            );

            $("#confirmation #ignoreMarkCRFCompleteMSG").unbind("change").bind("change", function() {
                setCookie("ignoreMarkCRFCompleteMSG", $(this).attr("checked") ? "yes" : "no", 1000);
            });
        }

        if (shouldShowDialog()) {
            $("input[name=" + checkboxObject.name + "]").attr('checked', false);
            $("#confirmation #ignoreMarkCRFCompleteMSG").attr('checked', false);
            $("#confirmation").dialog("open");
        } else {
            markCRFCompleteOk(checkboxObject.name);
        }

    } else
    if(checkboxObject != null && !checkboxObject.checked){
    	var checkboxObjects = document.getElementsByName(checkboxObject.name);
        if(checkboxObjects[0]){
            checkboxObjects[0].checked=false;
        }
        if(checkboxObjects[1]){
            checkboxObjects[1].checked=false;
        }
    }

    if (theme.name != 'blue') {
		$('input.button_medium').css('background-image', 'url(images/' + theme.name + '/button_medium_BG.gif)');
        $('.ui-dialog .ui-dialog-titlebar').find('span').css('color', theme.mainColor);
        $('.ui-dialog-titlebar').css('border', '1px Solid ' + theme.mainColor);
	}
}

function popUp(strFileName, strTarget) {
    window.open(strFileName, strTarget, 'menubar=yes,toolbar=no,scrollbars=yes,resizable,width=700,height=450,screenX=0,screenY=0');
}

function newImage(arg) {
    if (document.images) {
        rslt = new Image();
        rslt.src = arg;
        return rslt;
    }
}

function changeImages() {
    if (document.images && (preloadFlag == true)) {
        for (var i=0; i<changeImages.arguments.length; i+=2) {
            document[changeImages.arguments[i]].src = changeImages.arguments[i+1];
        }
    }
}

var preloadFlag = false;
function preloadImages() {
    if (document.images) {
        bt_GO_h = newImage("/images/bt_GO_d.gif");
        preloadFlag = true;

    }
}

function MM_jumpMenu(targ,selObj,restore){ //v3.0
    eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
    if (restore) selObj.selectedIndex=0;
}

/* Specifies the period of time between updates:
 month - once a month
 date - once per every day of the month (repeats the next month)
 weekday - once per every day of the week (repeats the next week)
 hour - once per hour (repeats the next day)
 request - once per browser request (default)
 */
var updatePeriods = new Array("month","date","weekday","hour","request");

// Invoked to display rotated HTML content in a Web page. The period
// argument should be an element of the updatePeriods array.

function displayRotatedContent(period) {
    var updatePeriod = -1;
    for(var i=0;i<content.length;++i) {
        if(period.toLowerCase() == updatePeriods[i].toLowerCase()) {
            updatePeriod = i;
            break;
        }
    }
    var s = selectHTML(updatePeriod);
    document.write(s);
}

function selectHTML(updatePeriod) {
    var n = 0;
    var max = content.length;
    var d = new Date();
    switch(updatePeriod) {
        case 0: // Month (0 - 11)
            n = d.getMonth();
            break;
        case 1: // Date (1 - 31 scaled to 0 - 30)
            n = d.getDate() - 1;
            break;
        case 2: // Weekday (0 - 6)
            n = d.getDay();
            break;
        case 3: // Hour (0 - 23)
            n = d.getHours();
            break;
        case 4: // Request (Default)
        default:
            n = selectRandom(max);
    }
    n %= max;
    return content[n];
}

// Select a random integer that is between 0 (inclusive) and max (exclusive)
function selectRandom(max) {
    var r = Math.random();
    r *= max;
    r = parseInt(r);
    if(isNaN(r)) r = 0;
    else r %= max;
    return r;
}

function confirmSaveAndContinue () {

    var yesno = confirm("Your data will now be saved to the database. This may take a minute or two; \nplease be patient and do not attempt to reload or make changes to the page. \nClick 'OK' to continue or 'Cancel' to return to the page without saving.","");
    return yesno;
}

function disableAllButtons (theform) {

    if (document.all || document.getElementById) {
        for (i = 0; i < theform.length; i++) {
            var tempobj = theform.elements[i];
            if (tempobj.type.toLowerCase() == "submit" || tempobj.type.toLowerCase() == "reset") {
                tempobj.disabled = true;
            }
        }
    }

    return true;
}

function submitFormDataConfirm (theform) {

    if (confirmSaveAndContinue()) {
        return disableAllButtons(theform);
    } else {
        return false;
    }
}

function submitFormReportCheck (theformlist) {
    var number = 0;
    for (i = 0; i < theformlist.length; i++) {
        if (theformlist[i].selected) number++;
    }
    //if (isNaN(number)) number = 0;
    if (number > 50) {
        alertDialog({ message: "You are only allowed to choose up to a maximum of fifty (50) variables.  You have picked "+number+".  Please go back to the form and remove some of your selections.  For Data Dumps of more than 50 variables, please contact your Project Administrator or DBA.", height: 150, width: 550 });
        return false;
    } else {
        return true;
    }
}


//---------------------added by jxu,10-15-2004------------------------

//-------------------------------------------------------------------------
// Function: setfocus
//
// Set the focus to the first form element.
//-------------------------------------------------------------------------

function setFocus() {

    var finished = false;
    var index = 0;
    if (document.forms[0] != null)
    {
        while ( finished == false )
        {
            if (document.forms[0].elements[index].type != 'hidden')
            {
                document.forms[0].elements[index].focus();
                finished = true;
            }

            index++;
        }
    }
}

//----------------------------------------------------
function trimString (str) {
    str = this != window? this : str;
    return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
}


//-------------------------------------------------------------------------
// Function: getQueryVariable
//
// returns the value of a key/value pair from the page's URL 'GET' parameters
//-------------------------------------------------------------------------

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return unescape(pair[1]);
        }
    }
    return '';
}

//-------------------------------------------------------------------------
// Function: getName
//
// returns the 'id' attribute of a DOM element by its ID
//-------------------------------------------------------------------------

function getName(spanId) {

    obj = getRef(spanId);
    str = obj.getAttribute('id');
    str = trimString(str);
    return str;

}

//-------------------------------------------------------------------------
// Function: getContent
//
// returns the html/text content of a DOM element by its ID
//-------------------------------------------------------------------------

function getContent(spanId) {
    obj = getRef(spanId);
    str = obj.innerHTML;
    return str;
}

//-------------------------------------------------------------------------
// Function: openDNoteWindow
//
// Pops up a new browser window for discrepancy notes, including the validation error message text if applicable
//-------------------------------------------------------------------------

function getDNViewLink(e) {
    var newURL = undefined;
    var imgElement = undefined;
    if (e != undefined) {
        var evt = e || window.event;
        imgElement = evt.target || evt.srcElement;
    }
    if (imgElement != undefined && imgElement.src != undefined && imgElement.parentNode != undefined && imgElement.src.toString().indexOf("icon_noNote.gif") < 0 &&
        imgElement.parentNode.children.length == 2 && imgElement.parentNode.children[1] != undefined && imgElement.parentNode.children[1].nodeName.toUpperCase() == "INPUT" &&
        imgElement.parentNode.children[1].value != undefined && imgElement.parentNode.children[1].value.toString().indexOf("ViewDiscrepancyNote") >= 0) {
        newURL = imgElement.parentNode.children[1].value;
    }
    return newURL;
}

function openDNoteWindow(inURL, spanID, strErrMsg, e) {

    var viewDNLink = getDNViewLink(e);

    // when user has removed then row from RG then we have to show other DN link - the right link in the 'rel' attribute (if we change it dynamically
    // then for some reasons we have the problems with repeating-model.js in the IE7)
    if (viewDNLink != undefined) {
        newURL = viewDNLink;
    } else {
        if (spanID) {
            //strErrMsg = getContent(spanID);
            // add the error message to the URL
            // encode it using 'escape'
            if (inURL.match(/\?/)) {
                if (inURL.match(/\?$/)) {
                    newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
                } else {
                    newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
                }
            } else {
                newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
            }
        } else {
            newURL = inURL;
        }
    }
    openNewWindow(newURL,
            'dnote_win',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'dnote');

}

//-----------------------------------------------------------
//--------for adding new note
function openDNWindow(inURL, spanID, strErrMsg, e) {

    var viewDNLink = getDNViewLink(e);

    // when user has created the DN then we have to open DN view link!
    if (viewDNLink != undefined) {
        return openDNoteWindow(viewDNLink, spanID, strErrMsg);
    }

    if (spanID) {
        //strErrMsg = getContent(spanID);
        // add the error message to the URL
        // encode it using 'escape'
        if (inURL.match(/\?/)) {
            if (inURL.match(/\?$/)) {
                newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
            } else {
                newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
            }
        } else {
            newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
        }
    } else {
        newURL = inURL;
    }
    openNewWindow(newURL,
            'dnote_win',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'dn');

}

//--------------------------------------
//--pop up a window which is smaller 
//------------------------------------------
function openDSNoteWindow(inURL, spanID, e) {

    var viewDNLink = getDNViewLink(e);

    // when user has created the DN then we have to open DN view link!
    if (viewDNLink != undefined) {
        return openDNoteWindow(viewDNLink, spanID);
    }

    if (spanID) {
        strErrMsg = getContent(spanID);
        // add the error message to the URL
        // encode it using 'escape'
        if (inURL.match(/\?/)) {
            if (inURL.match(/\?$/)) {
                newURL = inURL + 'strErrMsg=' + escape(strErrMsg);
            } else {
                newURL = inURL + '&strErrMsg=' + escape(strErrMsg);
            }
        } else {
            newURL = inURL + '?strErrMsg=' + escape(strErrMsg);
        }
    } else {
        newURL = inURL;
    }
    openNewWindow(newURL,
            'dnote_win',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'dsnote');

}


function openVNoteWindow(inURL) {

    openNewWindow(inURL,
            'def_win',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'dnote');

}


//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openDefWindow(inURL) {

    openNewWindow(inURL,
            'def_win',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'small');

}

//-------------------------------------------------------------------------
// Function: openNctEntryWindow
//
// Pops up a new browser window containing the NCT Entry screen
//-------------------------------------------------------------------------

function openNctEntryWindow(inURL) {

    openNewWindow(inURL,
            '_blank',
            'directories=no,location=no,menubar=no,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'medium');

}

//-------------------------------------------------------------------------
// Function: openDocWindow
//
// Pops up a new browser window containing a document, such as the 
// PRS Reference Guide.
//-------------------------------------------------------------------------

function openDocWindow(inURL) {

    openNewWindow(inURL,
    		'',
            'directories=no,location=no,menubar=yes,scrollbars=yes,toolbar=no,status=no,resizable=yes',
            'medium');
    //Name changed to blank because it shows error on IE7 and IE8. Mantis issue: 5553.
}

//-------------------------------------------------------------------------
// Function: openNewWindow
//
// Pops up a new browser window containing the definitions page, and scrolls
//     to the correct spot
//-------------------------------------------------------------------------

function openNewWindow(inURL, name, features, windowSize) {

    // Add check for browser capability
    var old_browser = true;
    if (window.screen != null) old_browser = false;
    /*
     Detect Internet Explorer, for the sake of printing CRFs.
     */

    if(inURL && inURL.indexOf("Print") != -1) {
        if(detectIEWindows(navigator.userAgent)) {
            if (inURL.indexOf("?") == -1) {
                inURL = inURL+"?ie=y";
            } else {
                inURL = inURL+"&ie=y";
            }
        }
    }

    if (features == "") {
        features = "toolbar=yes,directories=yes,location=1,status=yes,menubar=yes,scrollbars=yes,resizable=yes";
    }

    var height=250;
    var width=350;
    var screenHeight = 480;
    var screenWidth = 640;

    if(windowSize == 'small')
    {
        height = 150;
        width = 200;
    }
    if(windowSize == 'medium')
    {
        height = 300;
        width = 500;
    }
    if(windowSize == 'dnote')
    {
        height = 350;
        width = 450;
    }
    if(windowSize == 'dsnote')
    {
        height = 350;
        width = 450;
    }
    if(windowSize == 'dn')
    {
        height = 350;
        width = 450;
    }
    if (windowSize == 'print') {
        height = 700;
        width = 900;
    }



    if (window.screen != null)
    {
        screenHeight = window.screen.height;
        screenWidth = window.screen.width;
    }

    if (screenWidth > 640)
    {
        width = width + (screenWidth - 640)*.50;
    }

    if(screenHeight > 480)
    {
        height = height + (screenHeight - 480)*.50;
    }

    features += ",width=" + width + ",height=" + height;

    var docView = window.open (inURL, name, features);
    docView.focus();
}

//-------------------------------------------------------------------------
// Function: MM_findObjInParentWin
//
// Finds the specified object in the parent window if it exists
//     Must be called from within a popup window opened by a parent window
//-------------------------------------------------------------------------

function MM_findObjInParentWin(strParentWinImageName) { //v4.0
    var objImage;

    if (window.opener && !window.opener.closed) {
        objImage = MM_findObj(strParentWinImageName, window.opener.document);
    }

    return objImage;
}

function refreshSdvPageAfterItemSDV() {
    try {
        if (window.opener && !window.opener.closed && window.opener.location.href.indexOf("viewAllSubjectSDVtmp") > 0) {
            window.opener.location.reload();
        }
    } catch (e) {
        console.log(e.message)
    }
}

function refreshSdvPage() {
    try {
        if (window.opener.opener && !window.opener.opener.closed && window.opener.opener.location.href.indexOf("viewAllSubjectSDVtmp") > 0) {
            window.opener.opener.location.reload();
        }
    } catch (e) {
        console.log(e.message)
    }
}

//-------------------------------------------------------------------------
// Function: setImageInParentWin
//
// Sets/changes the source file of an image in a parent window
//     Must be called from within a popup window that was opened by the parent window
//-------------------------------------------------------------------------

function setImageInParentWin(strParentWinImageName,strParentWinImageFullPath, resolutionStatusId) {
    var objImage;
    if (window.opener && !window.opener.closed) {
        refreshSdvPage();
        objImage = MM_findObjInParentWin(strParentWinImageName);
        if (objImage != null) {
            objImage.src = strParentWinImageFullPath;
        }
        if (window.opener.updateCRFHeader != undefined) {
            var v = strParentWinImageName.match(/_(\d*)input\d*/) || strParentWinImageName.match(/_manual(\d*)input\d*/);
            window.opener.updateCRFHeader(strParentWinImageName.replace(/flag_/, ""), strParentWinImageName.replace(/flag_.*input/, ""), (v != undefined && v.length == 2 ? v[1] : ""), resolutionStatusId);
        }
    }
}

// new functions for View Subjects status menus 9-13-06
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
    if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
        document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
    else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_preloadImages() { //v3.0
    var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
        var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
            if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.0
    var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
    if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
    for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
    if(!x && document.getElementById) x=document.getElementById(n); return x;
}

function setImage(strImageName, strImageFullPath) {
    var objImage;

    objImage = MM_findObj(strImageName);
    if (objImage != null) { objImage.src = strImageFullPath; }
}

function setImageWithTitle(strImageName, strImageFullPath, strTitle) {
    var objImage;

    objImage = MM_findObj(strImageName);
    if (objImage != null) { objImage.src = strImageFullPath; objImage.title = strTitle;}
}

function leftnavExpand(strLeftNavRowElementName){

    var objLeftNavRowElement;

    objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
    if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
        objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
    }
}

function leftnavExpand_ext(strLeftNavRowElementName, isHeader, themeColor){
	var objLeftNavRowElement;
	var color = "";
	if (themeColor == "green" || themeColor == "violet") { color = "/" + themeColor; }
	objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
	if (objLeftNavRowElement != null) {
		if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
		objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
		if (isHeader) {
			objExCl = MM_findObj("excl_"+strLeftNavRowElementName);
			if(objLeftNavRowElement.display == "none"){
				objExCl.src = "images"+color+"/bt_Expand.gif";
			}else{
				objExCl.src = "images"+color+"/bt_Collapse.gif";
			}	
		}
	}
}

function layersShowOrHide() {
  var arrayArgs = layersShowOrHide.arguments;
  var objLayer;
  var strShowOrHide = arrayArgs[0];
  var i;

  for (i=1;i<=arrayArgs.length-1;i++) {
    if ((objLayer=MM_findObj(arrayArgs[i]))!=null) {
      // for IE and NS compatibility
      if (objLayer.style) { objLayer = objLayer.style; }
      objLayer.visibility = strShowOrHide;
    }
  }
}

/* 
 Functions that swaps images.  These functions were generated by Dreamweaver, but are
 not used by e-guana.
 */
function MM_swapImage() { //v3.0
    var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
        if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}
function MM_swapImgRestore() { //v3.0
    var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

var isDOM = (document.getElementById ? true : false);
var isIE4 = ((document.all && !isDOM) ? true : false);
var isNS4 = (document.layers ? true : false);
function getRef(id) {
    if (isDOM) return document.getElementById(id);
    if (isIE4) return document.all[id];
    if (isNS4) return document.layers[id];
}

function getSty(id) {
    return (isNS4 ? getRef(id) : getRef(id).style);
}

function gotopage(){

    if(document.jumpform.category.options[document.jumpform.category.selectedIndex].value != "") {
        document.location.href = document.jumpform.category.options[document.jumpform.category.selectedIndex].value;
    }
}


// new functions for revised View Subjects screen 9-13-06


function getObject( obj ) {

    // step 1
    if ( document.getElementById ) {
        obj = document.getElementById( obj );

        // step 2
    } else if ( document.all ) {
        obj = document.all.item( obj );

        //step 3
    } else {
        obj = null;
    }

    //step 4
    return obj;
}

function LockObject( obj, e ) {

    // step 1
    var tempX = 0;
    var tempY = 0;
    var offsetx = -17;
    var offsety = -15;
    var objHolder = obj;

    // step 2
    obj = getObject( obj );
    if (obj==null) return;

    // step 3
    if (!e) var e = window.event;
    if (e.pageX || e.pageY) 	{
        tempX = e.pageX;
        tempY = e.pageY;
    }
    else if (e.clientX || e.clientY) 	{
        tempX = e.clientX + document.body.scrollLeft
                + document.documentElement.scrollLeft;
        tempY = e.clientY + document.body.scrollTop
                + document.documentElement.scrollTop;
    }

    // step 4
    if (tempX < 0){tempX = 0;}
    if (tempY < 0){tempY = 0;}

    // step 5
    obj.style.top  = (tempY + offsety) + 'px';
    obj.style.left = (tempX + offsetx) + 'px';

    // step 6
    displayObject( objHolder, true );
}


function analyzeEvent(e) {
    var result = {};
    result.top = undefined;
    result.left = undefined;
    var tempX = 0;
    var tempY = 0;
    var offsetx = -2;
    var offsety = 10;

    if (!e) var e = window.event;
    if (!e) return result;
    if (e.pageX || e.pageY) 	{
        tempX = e.pageX;
        tempY = e.pageY;
    }
    else if (e.clientX || e.clientY) 	{
        tempX = e.clientX + document.body.scrollLeft
            + document.documentElement.scrollLeft;
        tempY = e.clientY + document.body.scrollTop
            + document.documentElement.scrollTop;
    }

    if (tempX < 0){tempX = 0;}
    if (tempY < 0){tempY = 0;}

    result.top  = (tempY + offsety) + 'px';
    result.left = (tempX + offsetx) + 'px';

    return result;
}

function moveObject( obj, e ) {

    // step 1
    var tempX = 0;
    var tempY = 0;
    var offsetx = -2;
    var offsety = 10;
    var objHolder = obj;

    // step 2
    obj = getObject( obj );
    if (obj==null) return;

    // step 3
    if (!e) var e = window.event;
    if (!e) return;
    if (e.pageX || e.pageY) 	{
        tempX = e.pageX;
        tempY = e.pageY;
    }
    else if (e.clientX || e.clientY) 	{
        tempX = e.clientX + document.body.scrollLeft
                + document.documentElement.scrollLeft;
        tempY = e.clientY + document.body.scrollTop
                + document.documentElement.scrollTop;
    }
    // step 4
    if (tempX < 0){tempX = 0;}
    if (tempY < 0){tempY = 0;}

    // step 5
    obj.style.top  = (tempY + offsety) + 'px';
    obj.style.left = (tempX + offsetx) + 'px';

    // step 6
    displayObject( objHolder, true );
}

function displayObject( obj, show ) {

    // step 1
    obj = getObject( obj );
    if (obj==null) return;

    // step 2
    obj.style.display = show ? 'block' : 'none';
    obj.style.visibility = show ? 'visible' : 'hidden';
}

function createRequestObject(){
    var req;

    if(window.XMLHttpRequest){
        //For Firefox, Safari, Opera
        req = new XMLHttpRequest();
    }else if(window.ActiveXObject){
        //For IE 5+
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }else{
        //Error for an old browser
        alertDialog({ message: 'Your browser is not IE 5 or higher, or Firefox or Safari or Opera', height: 150, width: 500 });
    }

    return req;
}

//Make the XMLHttpRequest Object
var http = createRequestObject();

var checkboxObject;
function sendRequest(method, url){
    if(method == 'get' || method == 'GET'){
        http.open(method,url);
        http.onreadystatechange = handleResponse;
        http.send(null);
    }
}
function handleResponse(){
    if(http.readyState == 4 && http.status == 200){
        var response = http.responseText;
        if(response == null || response != 'true') {
            checkboxObject.checked=false; 
            alertDialog({ message: 'Your password did not match. Please try again.', height: 150, width: 400 });
        }
    }
}

function requestSignatureFromCheckbox(password, checkbox){
	checkboxObject = checkbox;
	if (password==null || password==''){
		alertDialog({ message: 'Your password did not match. Please try again.', height: 150, width: 400 });
		checkbox.checked=false;
		return;
	}
	if(checkbox != null && checkbox.checked){
		sendRequest("GET", "MatchPassword?password=" + password);
	}
}

function numberGroupRows(){
	var allGroupDivs = prt$$("div.tableDiv");
	var allTrTags;
	var rowCounter;

	for(var i = 0; i < allGroupDivs.length; i++){

		allTrTags =  allGroupDivs[i].getElementsByTagName("tr");

		for(var j=0; j < allTrTags.length;j++) {

			if(allTrTags[j]) {
				rowCounter=allTrTags[j].getAttribute("repeat");

				if(rowCounter && rowCounter.indexOf("template") == -1)  {
					rowCounter++;
					allTrTags[j].innerHTML=rowCounter+
					allTrTags[j].innerHTML;
					rowCounter=0;//reset
				}
			}
		}


	}
}

/**
 * Refresh source page of the current page when "isRefresh" is "true" and source url match "pattern"
 */
function refreshSource(isRefresh, pattern) {
	if(isRefresh=="true") {
		if(window.opener && !window.opener.closed) {
			var v = window.opener.location.href;
			if(v.indexOf(pattern)>0) {
				window.opener.location.href = v;
			}
		}
	}	
}

function findPos(navElement) {
	var subnavElement='sub'+navElement;
	var parentobj = document.getElementById(navElement);
	var obj = document.getElementById(subnavElement);
	var posX = parentobj.offsetLeft;var posY = parentobj.offsetTop;
	var offsetx = -14;
	var offsety = (parentobj.offsetHeight);
	while(parentobj.offsetParent)
	{
		posX=posX+parentobj.offsetParent.offsetLeft;
		posY=posY+parentobj.offsetParent.offsetTop;
		if(parentobj==document.getElementsByTagName('body')[0]){break;}
		else
		{
			parentobj=parentobj.offsetParent;
		}
	}
	obj.style.top  = (posY + offsety) + 'px';
	obj.style.left = (posX + offsetx) + 'px';
	displayObject( subnavElement, true );
}

function setNav(thisNavItem)
{
	hideSubnavs();
	layersShowOrHide('visible', 'sub' + thisNavItem);
	layersShowOrHide('visible', 'nav_hide');
	findPos(thisNavItem);
}

function hideSubnavs()
{
	var navItem = new Array('nav_Tasks');
	for(i=0;i<navItem.length;i++) 
	{
		layersShowOrHide('hidden', 'sub' + navItem[i]);
	}
	layersShowOrHide('hidden', 'nav_hide');
}

var hideContentForCurrentPopup = function() {
    jQuery(".calendar").remove();
    var eventDiv = jQuery("div[id^='Event_" + currentPopupUid + "']");
    eventDiv.css("visibility", "hidden");
    if (eventDiv.find(".popupShadow").length == 1) {
        eventDiv.html(eventDiv.find(".popupShadow").html());
    }
    jQuery("div[id^='crfListWrapper_" + currentPopupUid + "']").html("");
    jQuery("div[id^='eventScheduleWrapper_" + currentPopupUid + "']").html("");
    currentPopupUid = undefined;
    subjectMatrixPopupStick = undefined;
};

var hideCurrentPopup = function (ev) {
    var element = ev.target || ev.srcElement;
    if (ev.target.nodeName.toLowerCase() != "html") {
        if ((currentPopupUid != undefined && jQuery(ev.target).closest("#Event_" + currentPopupUid).length == 0)
            && jQuery(ev.target).parents('#ui-datepicker-div').length == 0) {
            hideContentForCurrentPopup();
            jQuery("*").unbind("mousedown", hideCurrentPopup);
        } else {
            subjectMatrixPopupStick = true;
        }
    }
};

function getSchedulePage(params, targetElement) {
    var element = jQuery('#eventScheduleWrapper_' + params.statusBoxId);
    if (element.length == 1) {
        jQuery('#eventScheduleWrapper_' + params.statusBoxId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
        jQuery('#eventScheduleWrapper_' + params.statusBoxId).css("height", "18px");
        
        jQuery.ajax({
            url: element.attr("rel"),
            type: "GET",
            data: {page: params.page},
            cache: false,
            success: function (data) {
                jQuery(".calendar").remove();
                jQuery('#eventScheduleWrapper_' + params.statusBoxId).css("height", "auto");
                jQuery('#eventScheduleWrapper_' + params.statusBoxId).html(data);
                jQuery("img[class='showCalendar']").each(function() {
                    var jsCode = jQuery(this).attr("rel");
                    eval(jsCode);
                });
                jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
                setAccessedObjected(targetElement);
            }
        });
       
    }
}

function adjustCrfListTable(studyEventId, statusBoxId) {
  var wereRemoved = 0;
  var popupTotalColumns = 8;

  var hideCol = function(num) {
    $(".crfListTable tr").find("td:last").find("img:eq(" + (num - wereRemoved) + ")").remove();
    popupTotalColumns--;
    wereRemoved++;
  };

  if ($("#crfListWrapper_" + statusBoxId + " #hideCol1").val() == 'true') hideCol(0);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol2").val() == 'true') hideCol(1);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol3").val() == 'true') hideCol(2);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol4").val() == 'true') hideCol(3);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol5").val() == 'true') hideCol(4);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol6").val() == 'true') hideCol(5);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol7").val() == 'true') hideCol(6);
  if ($("#crfListWrapper_" + statusBoxId + " #hideCol8").val() == 'true') hideCol(7);

  $("#popupTotalColumns").val(popupTotalColumns);

  var crfActionIconWidth = 33;
  var crfActionsMaxIconsCount = parseInt($("#popupTotalColumns").val());
  crfActionsMaxIconsCount = (crfActionsMaxIconsCount < 2 ? 2 : crfActionsMaxIconsCount);
  jQuery('#crfListWrapper_' + statusBoxId + ' .crfListTableActions').attr("style", "width: " + (crfActionIconWidth * crfActionsMaxIconsCount) + "px;");

  $(".crfListTable tr").find("td:first").each(function() {
    $(this).attr("style", "border-left : none !important;" + $(this).attr("style"));
  });
}

function getCRFList(params, targetElement) {
    jQuery('#crfListWrapper_' + params.statusBoxId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
    jQuery('#crfListWrapper_' + params.statusBoxId).css("height", "18px");
    jQuery.ajax({
        url: "CRFListForStudyEvent",
        type: "GET",
        data: {studyEventId: params.studyEventId, eventCRFId: params.eventCRFId,
        	eventDefintionCRFId: params.eventDefintionCRFId, page: params.page},
        cache: false,
        success: function (data) {
            jQuery('#crfListWrapper_' + params.statusBoxId).css("height", "auto");
            jQuery('#crfListWrapper_' + params.statusBoxId).html(data);
            jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
            adjustCrfListTable(params.studyEventId, params.statusBoxId);
            setAccessedObjected(targetElement);
        }
    });
}

function closePopup() {
    clearInterval(popupInterval);
    subjectMatrixPopupStick = undefined;
}

function canShowPopup() {
    return !subjectMatrixPopupStick;
}

function showPopup(params, event) {
    clearInterval(popupInterval);
    var coords = analyzeEvent(event);
    var targetElement = event.target ? event.target : event.srcElement;
    popupInterval = setInterval(function() {
        clearInterval(popupInterval);
        hideAllTooltips(params, coords.top, coords.left, targetElement);
    }, 1000);
}

function justShowPopup(params, event) {
    clearInterval(popupInterval);
    var coords = analyzeEvent(event);
    var targetElement = event.target ? event.target : event.srcElement;
    setTimeout(function() {
        hideAllTooltips(params, coords.top, coords.left, targetElement);
    }, 200);
}

function getScheduledEventContent(params, top, left, localDivId, localDivRel, targetElement) {
    jQuery('#eventScheduleWrapper_' + params.statusBoxId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
    jQuery('#eventScheduleWrapper_' + params.statusBoxId).css("height", "18px");
    
    var paramsForReqest = {};
    paramsForReqest["returnScheduledEvenContent"] = localDivId;
    paramsForReqest["popupQueryStudySubjectId"] = localDivRel;
    paramsForReqest["page"] = params.page;
    paramsForReqest["eventCRFId"] = params.eventCRFId;
    paramsForReqest["eventDefintionCRFId"] = params.eventDefintionCRFId;
    if (params.page == pageTitle_SMForSelectedEvent) {
    	paramsForReqest['selectedEventDefId'] = jQuery("#sedDropDown").val();    //determines event definition name, SM filtered for
    }
    
    jQuery.ajax({
        url: "CreateNewStudyEvent",
        type: "GET",
        data: paramsForReqest,
        cache: false,
        success: function (data) {
            var localDiv = jQuery("#" + localDivId);
            localDiv.get(0).innerHTML = data;
            if (localDiv[0].repeatingEvent && parseInt(localDiv[0].totalEvents) > 1) {
                localDiv.attr("style", localDiv.attr("style").replace("width: 608px;", "width: 658px;"));
            }
            setAccessedObjected(targetElement);
            hideAllTooltips(params, top, left, targetElement);
        }
    });
}

function hideAllTooltips(params, top, left, targetElement) {
    try {
        jQuery(".calendar").remove();
        hideContentForCurrentPopup();

        var objHolder = document.getElementById("Event_" + params.statusBoxId);
        if (objHolder == undefined || top == undefined || left == undefined) return;
        objHolder.style.top  = top;
        objHolder.style.left = left;
        objHolder.style.display = 'block';
        objHolder.style.visibility = 'visible';
        
        if (params.statusBoxId != undefined) {
            currentPopupUid = params.statusBoxId;

            var eventDiv = jQuery("#Event_" + params.statusBoxId);
            if (eventDiv.length > 0) {
                var prevHtml = eventDiv.html();
                eventDiv.html(jQuery("#popupShadowWrapper").html());
                eventDiv.find(".popupShadow").html(prevHtml);

                if (eventDiv[0].repeatingEvent && parseInt(eventDiv[0].totalEvents) > 1) {
                	params.statusBoxNum = eventDiv[0].totalEvents;
                }
                if (eventDiv[0].getScheduledContent) {
                    eventDiv[0].getScheduledContent = undefined;
                    getScheduledEventContent(params, top, left, eventDiv.attr("id"), eventDiv.attr("rel"), targetElement);
                    return;
                }
            }

            if (params.statusBoxNum != undefined) {
                EnableScrollArrows2(params.statusBoxId, params.statusBoxNum, targetElement);
                jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
            } else {
                if (document.getElementById('Menu_on_' + params.statusBoxId) != undefined) {
                	document.getElementById('Menu_on_' + params.statusBoxId).style.display = "";
                }	
                if (document.getElementById('Menu_off_' + params.statusBoxId) != undefined) {
                	document.getElementById('Menu_off_' + params.statusBoxId).style.display = "none";
                }	
                if (params.studyEventId != '') {
                    getCRFList(params, targetElement);
                } else {
                    getSchedulePage(params, targetElement);
                }
            }
        }
    } catch (e) {
        //
    }
}

function bodyWasLoaded() {
}

function gfAddOverlay() {
    gfRemoveOverlay();
    var div = document.createElement("div");
    div.setAttribute("id", "pageOverlay");
    div.setAttribute("style", "width: " + parseInt(document.body.scrollWidth) + "px; height: " + parseInt(document.body.scrollHeight) + "px;");
    document.body.appendChild(div);
}

function gfRemoveOverlay() {
    var overlay = document.getElementById('pageOverlay');
    if (overlay != null) {
        document.body.removeChild(overlay);
    }
}

function createNewEvent_ClosePopup(event) {
    closePopup();
    var ev = event || window.event;
    var element =  ev.target || ev.srcElement;
    if (element != undefined) {
        jQuery(element).closest("div[id^='Event_']").css("visibility", "hidden");
        jQuery(element).closest("div[id^='crfListWrapper_']").html("");
        jQuery(element).closest("div[id^='eventScheduleWrapper_']").html("");
    }
}

function createNewEvent(page, event) {
    closePopup();
    var ev = event || window.event;
    var element =  ev.target || ev.srcElement;
    if (element != undefined) {
        gfAddOverlay();
        var params = {};
        jQuery("#CreateNewStudyEvent").find("input").each(function(){
            if (jQuery(this).attr("name") != undefined && jQuery.trim(jQuery(this).attr("name")) != "") {
                params[jQuery(this).attr("name")] = jQuery(this).val();
            }
        });
        jQuery("#CreateNewStudyEvent").find("select").each(function(){
            if (jQuery(this).attr("name") != undefined && jQuery.trim(jQuery(this).attr("name")) != "") {
                params[jQuery(this).attr("name")] = jQuery(this).val();
            }
        });
        try {
            params['popupQuery'] = jQuery(element).closest("div[id^='Event_']").attr("id");
            params['popupQueryStudySubjectId'] = jQuery(element).closest("div[id^='Event_']").attr("rel");;
            params['page'] = page;    //determines page name, on which user perfoms scheduling
            if (page == pageTitle_SMForSelectedEvent) {
            	params['selectedEventDefId'] = jQuery("#sedDropDown").val();    //determines event definition name, SM filtered for
            }

            jQuery.post(
                "CreateNewStudyEvent",
                params,
                function(data) {
                    var result = eval("(" + data + ")");
                    if (result.errors != undefined) {
                        var eventDiv = jQuery(element).closest("div[id^='Event_']");
                        eventDiv.find("input[type='submit']").click();
                    } else {
                        jQuery("#button_unlockStudySubject_" + params['popupQueryStudySubjectId']).remove();
                        jQuery("#button_lockStudySubject_" + params['popupQueryStudySubjectId']).removeAttr("style");
                        jQuery("#button_signStudySubject_" + params['popupQueryStudySubjectId']).removeAttr("style");
                        
                        for (var h = 0; h < result.eventDefs.length; h++) {
                            var eventDef = result.eventDefs[h];
                            if (eventDef != undefined && eventDef.eventDivId != undefined) {
                                var eventDiv = jQuery("div[id^='" + eventDef.eventDivId + "']");
                                if (eventDiv.length > 0 && eventDef.eventIds.length > 0) {
                                    var aElement = eventDiv.parent().find("a:last");
                                    var eventId = eventDef.eventIds[0];
                     
                                    eventDiv[0].totalEvents = eventDef.totalEvents;
                                    eventDiv[0].repeatingEvent = eventDef.repeatingEvent;
                                    eventDiv[0].getScheduledContent = true;
                                    
                                    if(aElement.attr("onmouseover") != undefined && jQuery.trim(aElement.attr("onmouseover").toString()) != "") {
                                        var newMOFuncBody = jQuery.trim(aElement.attr("onmouseover").toString().match(/{((?:.|\n)+)}/)[1]).toString();
                                        newMOFuncBody = newMOFuncBody.replace(/'studyEventId':''/g, "'studyEventId':'" + eventId + "'")
                                            .replace(/\"studyEventId\":\"\"/g, "\"studyEventId\":\"" + eventId + "\"");
                                        aElement.attr("onmouseover", "");
                                        var confEvent = function (element, funcBody) {
                                            element.unbind("mouseover").bind("mouseover", function (event) {
                                                eval(funcBody);
                                            });
                                        };
                                        confEvent(aElement, newMOFuncBody);
                                    }

                                    if (aElement.attr("onclick") != undefined && jQuery.trim(aElement.attr("onclick").toString()) != "") {
                                        var newCLFuncBody = jQuery.trim(aElement.attr("onclick").toString().match(/{((?:.|\n)+)}/)[1]).toString();
                                        newCLFuncBody = newCLFuncBody.replace(/'studyEventId':''/g, "'studyEventId':'" + eventId + "'")
                                        		.replace(/\"studyEventId\":\"\"/g, "\"studyEventId\":\"" + eventId + "\"");
                                        aElement.attr("onclick", "");
                                        var confEvent = function(element, funcBody) {
                                            element.unbind("click").bind("click", function(event){
                                                eval(funcBody);
                                            });
                                        };
                                        confEvent(aElement, newCLFuncBody);
                                    }

                                    if (eventDef.popupToDisplayEntireEvent && aElement.find("img").attr("src").indexOf("icon_NotStarted.gif") > 0) {
                                        aElement.find("img").attr("src", "images/icon_Scheduled.gif");
                                        aElement.find("img").attr("style", "position: relative;");
                                    }

                                    if (eventDef.repeatingEvent && parseInt(eventDef.totalEvents) > 1) {
                                        aElement.html(aElement.find("img")[0].outerHTML + "<span class=\"re_indicator\">x" + eventDef.totalEvents + "</span>");
                                    }
                                }
                            }
                        }

                        $("div[id='Event_" + currentPopupUid + "']").css("visibility", "hidden");
                        gfRemoveOverlay();

                        if (result.pageMessages.length > 0) {
                            jQuery("#sidebar_Alerts_closed").attr("style", "display: none;");
                            jQuery("#sidebar_Alerts_open").attr("style", "display: block;");
                            jQuery("#sidebar_Alerts_open div.sidebar_tab_content").html("");
                            jQuery("#sidebar_Alerts_open div.sidebar_tab_content").get(0).innerHTML = "";
                            for (var mPos = 0; mPos < result.pageMessages.length; mPos++) {
                                jQuery("#sidebar_Alerts_open div.sidebar_tab_content").append("<i><div class=\"alert\">" + result.pageMessages[mPos] + "<br><br></div></i>");
                            }
                            jQuery("#sidebar_Alerts_open div.sidebar_tab_content").append("<br>");
                        }
                    }
                }
            );
        } catch (e) { gfRemoveOverlay(); }
    }
}

var crfRadioButtonChecked = false;
var radioButtonsWereBinded = false;

var radioButtonOnClick = function(event) {
    if (crfRadioButtonChecked) {
        var ev = event || window.event;
        var element =  ev.target || ev.srcElement;
        if (element != undefined) {
            element.checked = false;
            var func = element["onclick"];
            if (func != undefined && (typeof func == 'function') && func.toString().indexOf("radioControlShow") >= 0) {
                func();
            }
        }
    }
};

var radioButtonOnMouseUp = function(event) {
    crfRadioButtonChecked = false;
    var ev = event || window.event;
    var element =  ev.target || ev.srcElement;
    if (element != undefined && element.checked) {
        crfRadioButtonChecked = true;
    }
};

var bindRadioButtons = function() {
    if (radioButtonsWereBinded == false) {
        radioButtonsWereBinded = true;
        var mainForm = document.getElementById('mainForm');
        if (mainForm != undefined && mainForm.name == "crfForm") {
            var elementList = mainForm.getElementsByTagName('input');
            for (i = 0; i < elementList.length; i++) {
                if (elementList[i].type == 'radio') {
                    $(elementList[i]).bind("mouseup", function(event) {
                        radioButtonOnMouseUp(event);
                    });
                    $(elementList[i]).bind("click", function(event) {
                        radioButtonOnClick(event);
                    });
                }
            }
        }
    }
};

function fixFileInputsForFireFox() {
    $(".fileInputDivWrapper").parent().append("<span class=\"fileNameTail hidden\">...</span>");
    $(".fileInputDivWrapper input[type=file]").each(function() {
        $(this).change(function() {
            $(".fileNameTail").addClass("hidden");
            $(".fileNameTail").removeClass("visible");
            var tableWidth = parseInt($(".fileInputTableWrapper").width());
            var divWidth = parseInt($(".fileInputTableWrapper").parent().width());
            if ((tableWidth - 3) > divWidth) {
                $(".fileNameTail").removeClass("hidden");
                $(".fileNameTail").addClass("visible");
            }
        });
    });
}

/* =======================================================================
 * Disable the Randomization result field and the randomization date field
 * They should be read-only
 ========================================================================= */
$(function() {
    var dateInput = $("#Rand_Date").find(":input");
    var resultInput = $("#Rand_Result").find(":input");

    $(dateInput).attr("readonly", "readonly");
    $(resultInput).attr("readonly", "readonly");

    fixFileInputsForFireFox();
});

/* =======================================================================================
* Randomize the current subject for whom data entry is being executed on.
*
* This function takes no parameters but assumes the following (for a valid randomization): 
*
* - The current study is a site
* - A stratification level is provided
* - Corresponding details are configured in randomize.net
* - A valid Trial Id is either configured in the CRF or datainfo.properties (but not both)
*
* This function returns undefined.
========================================================================================== */
function randomizeSubject() {

	disableRandomizeCRFButtons(true);
	var crf = $("input:hidden[name='crfId']").val();
	var eventCrfId = $("input:hidden[name='eventCRFId']").val();
	var dateInputId = $("#Rand_Date").find(":input").attr("id").replace('input','');
	var resultInputId = $("#Rand_Result").find(":input").attr("id").replace('input','');
	var eligibility = null;
	// Check if the data entry step is IDE
	if($("form[id=mainForm]").attr("action") !== "InitialDataEntry") {
 		alertDialog({ message: $("input:hidden[name='randDataEntryStepMessage']").val(), height: 150, width: 500 });
		$("input[type='submit']").removeAttr("disabled");
		return false;
	}
	// Check if the subject eligibility is defined
	if($("#Rand_Eligibility :radio").size() > 0) {
        // Check if selection has been done
        if($("input[type=radio]:checked", "#Rand_Eligibility").length > 0) {
            eligibility = $("input[type=radio]:checked", "#Rand_Eligibility").val();
        }
    }
	var strataLevels = [];
	var strataItemIds = [];

    $.each($("div[id^=Rand_StrataData]").find(":selected"), function(index, element) {
        var strata = {
            // ordering
            StratificationID: index + 1,
            Level: $("input[eleid^='"+ $(this).parents("div").attr("id") +"']").attr($(this).text())
        };
        var strataItemId = $(this).parents("select").attr("id").replace('input','');
        strataItemIds.push(strataItemId);
        strataLevels.push(strata);
    });
    var trialId = null;
    var trialIdItemId = null;
    var trialIdItemValue = null;
    // Check if the trial Id is defined
    if($("#Rand_TrialIDs :select").size() > 0) {
        // Check if selection has been done
        if($("#Rand_TrialIDs :select").find(":selected") !== undefined) {
            var opt3 = $("#Rand_TrialIDs :select").find(":selected").text();
            trialIdItemId = $("#Rand_TrialIDs").find("select").attr("id").replace('input','');
            trialIdItemValue = $("#Rand_TrialIDs").find("select").val();
            trialId = $("input:hidden[eleid='requiredParam3']").attr(opt3);
        } else {
            alertDialog({ message: $("input:hidden[name='requiredParam3Missing']").val(), height: 150, width: 500 });
            $("input[type='submit']").removeAttr("disabled");
            return false;
        }
    } else {
        trialId = $("input[eleid='randomize']").attr("trialId");
	}
	var subject = $("input:hidden[name='subjectLabel']").val();

	if ($("input:hidden[name='assignRandomizationResultTo']").val() == "ssid"
		&& (subject == "" || subject == undefined)) {
		alert($("input[name=personIdMissing]").val());
		$("input[type='submit']").removeAttr("disabled");
		return false;
	}
	var subjectId = $("input[name='studySubjectId']").val();

    $.ajax({
        type:"POST",
        url: "randomize",
        data: {
            crf: crf,
            eventCrfId: eventCrfId,
            dateInputId: dateInputId,
            resultInputId: resultInputId,
            subject: subject,
	        subjectId: subjectId,
            trialId: trialId,
            trialIdItemId: trialIdItemId,
            trialIdItemValue: trialIdItemValue,
            eligibility: eligibility,
            strataItemIds: JSON.stringify(strataItemIds),
            strataLevel: JSON.stringify(strataLevels)
        },
        success: function(data) {
            if(data.match(/UnknownHostException/)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "The randomization service is not available. Consult your system administrator", height: 150, width: 500 });
            } else if(data.match(/Invalid Site/)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "The Site Id configured is invalid. Please contact your system administrator", height: 150, width: 500 });
            } else if(data.match(/Invalid Trial/)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "The Trial Id configured is invalid. Please contact your system administrator", height: 150, width: 500 });
            } else if(data.match(/Invalid Strata/)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "The Stratification level missing. Please contact your system administrator", height: 150, width: 500 });
            } else if(data.match(/^\</)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "An error occurred during the randomization call. Please contact your system administrator", height: 150, width: 500 });
            } else if(data.match(/Site is not auth/)) {
                disableRandomizeCRFButtons(false);
                alertDialog({ message: "The Site configured is not authorized to randomize subjects . Please contact your system administrator", height: 150, width: 500 });
            } else if(data.match(/Exception/)) {
                disableRandomizeCRFButtons(false);
                var exceptionPattern = new RegExp("^.*:(.*)");
                alertDialog({ message: exceptionPattern.exec(data)[1], height: 150, width: 500 });
            } else {
                var result = JSON.parse(data);
                var dateInput = $("#Rand_Date").find(":input");
                var resultInput = $("#Rand_Result").find(":input");
                $(dateInput).attr("readonly", "");
                $(resultInput).attr("readonly", "");
                $(dateInput).val(result.date).change();
                $(resultInput).val(result.result).change();
                $(dateInput).attr("readonly", "readonly");
                $(resultInput).attr("readonly", "readonly");
                $("input[type='submit']").removeAttr("disabled");
                var errorMessage = $("input[name=randomizationMessage]").val();
                alertDialog({ message: errorMessage, height: 150, width: 500 });
            }
        }
    });
}

/* =======================================================================
 * Check if opened CRF is Randomization CRF, and disable strata and
 * trialID items.
 ========================================================================= */
function checkRandomizationCRF() {

	var dateSize = parseInt($("div[id=Rand_Date] input").size(),10);
	var resultSize = parseInt($("div[id=Rand_Result] input").size(),10);

	if (dateSize > 0 && resultSize > 0) {

		var randResult = $("div#Rand_Result input").val();
		var randDate = $("div#Rand_Date input").val();

		if (randResult != "" && randDate != "") {
			$("div[id^=Rand_StrataData] select option:not(:selected), div[id^=Rand_TrialIDs] select option:not(:selected)").remove();
		}
	}
}

function urlParam(name){
    var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return results != undefined ? results[1] : undefined;
}

// when document is loaded
$(function() {
    // enable the ability to uncheck the radio buttons in the CRF's
    bindRadioButtons();
    if ($("#datasetForm").length > 0) {
        firstFormState = $("#datasetForm").serialize();
    } else
    if ($("#formWithStateFlag").length > 0) {
        firstFormState = $("#formWithStateFlag").parent("form").find("input[type=checkbox],input[type=radio],input[type=text],input[type=password],input[type=file],textarea,select").serialize();
        $("#formWithStateFlag").parent("form").find("input[type=checkbox],input[type=radio],input[type=text],input[type=password],input[type=file],textarea,select").change(function(){
            $("#formWithStateFlag").val("changed");
        });
    }
});

executeWhenDOMIsReady = function(code) {
    $(function() {
        eval(code);
    });
}

disableRandomizeCRFButtons = function(flag) {

    if (flag) {

        $("input[type='submit']").attr("disabled", "disabled");
        $("input[eleid='randomize']").attr("disabled", "disabled");

    } else {

        $("input[type='submit']").removeAttr("disabled");
        $("input[eleid='randomize']").removeAttr("disabled");
    }
};

function updateExpandCollapseCodedItemsInput(value) {

    if (value) {

        $("#showContext").val("false");

    } else {

        $("#showContext").val("true");
    }
}

function showHideCodedItemContext(item) {

    if ($("div[id=" + $(item).attr("itemid") + "]").parent("td").find("#tablepaging").css('display') == 'none') {

        $("div[id=" + $(item).attr("itemid") + "]").parent("td").find("#tablepaging").css('display', '');
    } else {

        $("div[id=" + $(item).attr("itemid") + "]").parent("td").find("#tablepaging").css('display', 'none');
    }
}

codeItem = function(item) {
	
	setAccessedObjected(item);
    var isLocked = $("a[name='goToEcrf'][itemid=" + $(item).attr("itemid") + "]").children('img').filter(function () {
        return $(this).attr('src').indexOf('icon_Locked_long.gif') > 0;
    });
    if ($(item).attr('block') == 'true' || isLocked.size() > 0) {

        if ($(item).parent().siblings("td").find("div[name='itemStatus']").text() == 'Coded') {

            showHideCodedItemContext(item);
            return;
        } else {
            var locked_ecrf_message = $("#locked_crf_message").val();
            alertDialog({ message: locked_ecrf_message, height: 150, width: 500 });
        }
        return;
    }

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];

    var ajaxRequest = $.ajax({

        type: "POST",
        url: url + "/codeItem",
        beforeSend: function(jqXHR, settings) { showMedicalCodingAlertBox(item, jqXHR);},

        data: {

            item: $(item).attr("itemid"),
            prefLabel: $.trim($(item).parent().siblings("td").find("input").val()),
            dictionary: $(item).parent().siblings("td").find("div[name='termDictionary']").text()
        },
        success: function(data) {

            hideMedicalCodingAlertBox(ajaxRequest);
            //delete old results
            $("#emptyResult").parent().html('');

            //display ajax response
            $("div[id=" + ($(item).attr("itemid")) + "]").html(data);

            //auto code actions
            if ($("div[id=" + ($(item).attr("itemid")) + "]").find('table').length > 0 && $("#autoCode").size() === 1) {

                //disable input field
                $(item).parent().siblings("td").find("input").attr('disabled', true);

                //set autocoding item name
                $(item).parent().siblings("td").find("input").val($("a[name='unCode'][itemid=" + ($(item).attr("itemid")) + "]").attr('pref'));

                //display uncode icon
                $("a[name='unCode'][itemid=" + ($(item).attr("itemid")) + "]").css("visibility", "visible");

                //update code icon url
                $("a[name='Code'][itemid=" + ($(item).attr("itemid")) + "]").children('img').attr('src', '../images/code_confirm.png');

                //add block for hyperlink
                $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").attr('block', 'true');

                //add term mark
                $("a[name='unCode'][itemid=" + $(item).attr("itemid") + "]").attr('term',  $.trim($(item).parents().siblings("td").find("div[name='itemDataValue']").text()).toLowerCase());

                var tdCoded = parseInt($("table.summaryTable tr td[name='tdCoded']").text());
                $("table.summaryTable tr td[name='tdCoded'] a").text(tdCoded + 1);

                if($(item).parent().siblings("td").find("div[name='itemStatus']").text() == 'Code not Found') {

                    var tdCodeNotFound = parseInt($("table.summaryTable tr td[name='tdCodeNotFound']").text());
                    $("table.summaryTable tr td[name='tdCodeNotFound'] a").text(tdCodeNotFound - 1);

                } else if ($(item).parent().siblings("td").find("div[name='itemStatus']").text() == 'Not Coded') {

                    var tdNotCoded = parseInt($("table.summaryTable tr td[name='tdNotCoded']").text());
                    $("table.summaryTable tr td[name='tdNotCoded'] a").text(tdNotCoded - 1);
                }

                //update goToEcrf icon if item is sdv or signed
                var subjectId = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='subjectId']").text();
                var eventName = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='eventName']").text();
                var crfName = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='crfName']").text();

                $("a[name='goToEcrf']").children('img').each(function () {

                    if(($(this).attr('src').indexOf('icon_Signed_long.gif') > 0 || $(this).attr('src').indexOf('icon_DoubleCheck_long.gif') > 0)
                        && $(this).parents().siblings("td").find("div[name='subjectId']").text() == subjectId
                        && $(this).parents().siblings("td").find("div[name='eventName']").text() == eventName
                        && $(this).parents().siblings("td").find("div[name='crfName']").text() == crfName) {

                        $(this).attr('src', '../images/icon_DEcomplete_long.gif');
                    }
                });

                //update coded item status
                $(item).parent().siblings("td").find("div[name='itemStatus']").text("Coded");

                $("div[id=" + $(item).attr("itemid") + "]").find('#tablepaging_result').attr('id', 'tablepaging');

                $("#autoCode").remove();

            } else if ($("#notCoded").size() === 1) {

                // Update counters for new code not found items only
                if($(item).parent().siblings("td").find("div[name='itemStatus']").text() !== 'Code not Found') {

                    var tdNotCoded = parseInt($("table.summaryTable tr td[name='tdNotCoded']").text());
                    var tdCodeNotFound = parseInt($("table.summaryTable tr td[name='tdCodeNotFound']").text());

                    $("table.summaryTable tr td[name='tdNotCoded'] a").text(tdNotCoded - 1);
                    $("table.summaryTable tr td[name='tdCodeNotFound'] a").text(tdCodeNotFound + 1);
                }

                $(item).parent().siblings("td").find("div[name='itemStatus']").text('Code not Found');

                $("#tdCodeNotFound").show();
                $("td[name='tdCodeNotFound']").show();
            }
        },
        error: function(e) {

            hideMedicalCodingAlertBox(ajaxRequest);

            //open alert box
            if($("#sidebar_Alerts_open").css("display") == 'none') {

                leftnavExpand('sidebar_Alerts_open');
                leftnavExpand('sidebar_Alerts_closed');
            }
            //append error to the alert box
            $("#sidebar_Alerts_open .sidebar_tab_content").html('')
                .append("<div class='alert'>Dictionary is not available. Please contact your system administrator.</div>");

            console.log("Error:" + e);
        }
    });
};

getMedicalCodingCategoryList = function(item) {

    var categoryList = $(item).closest('tbody').find('tr').map(function() {

        return $(this).find('td').map(function(index) {

            var returnValue = $(this).html() == '' || $(this).html().indexOf('INPUT') > 0 || $(this).html().indexOf('input') > 0 ? null : $(this).attr('id').length > 0 ? $(this).attr('id') : $.trim($(this).html());
            returnValue = returnValue != null && returnValue.indexOf("href") > 0 ? $.trim($(this).find("a").text()) : returnValue;

            return returnValue;

        }).get();

    }).get();

    categoryList = $.map(categoryList, function(value, i) {

        if(i % 2 == 0) {
            return value.replace(/\:/g, '');
        }

        return value;
    });

    return categoryList;
};

saveCodedItem = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];
    var categoryList = getMedicalCodingCategoryList(item).join("|");

    $.ajax({

        type: "POST",
        url: url + "/saveCodedItem",
        data: {

            categoryList: categoryList,
            item: $(item).parents('div').attr("id"),
            verbatimTerm: $.trim($(item).parents().siblings("td").find("div[name='itemDataValue']").text()),
            coderSearchTerm: $(item).parents().find("div[id=" + $(item).parents('div').attr("id") + "]").siblings("input").val()
        },

        success: function(data) {

            manualUpdateMedicalCodingUX(item);

            console.log("Medical coding executed successfully");
        },
        error: function(e) {

            if($("#sidebar_Alerts_open").css("display") == 'none') {

                leftnavExpand('sidebar_Alerts_open');
                leftnavExpand('sidebar_Alerts_closed');
            }
            //append error to the alert box
            $("#sidebar_Alerts_open .sidebar_tab_content").html('')
                .append("<div class='alert'>Dictionary is not available. Please contact your system administrator.</div>");
            console.log("Error:" + e);
        }
    });
};

uncodeCodeItem = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];

        $.ajax({

         type: "POST",
         url: url + "/uncodeCodedItem",
         data: {

            item: $(item).attr("itemid")
         },

         success: function(data) {

            var codeItemButtonSrc = "../images/code_blue.png";
            var color = $('*').find('a').css('color').toLowerCase();
            if (color == 'rgb(170, 98, 198)' || color == '#aa62c6') {
                codeItemButtonSrc = "../images/violet/code_violet.png";
            } else if (color == 'rgb(117, 184, 148)' || color == '#75b894') {
                codeItemButtonSrc = "../images/green/code_green.png";
            } else if (color == 'rgb(44, 108, 175)' || color == '#2c6caf') {
                codeItemButtonSrc = "../images/darkBlue/code_darkBlue.png";
            }

            $(item).parents().find("div[id=" + $(item).attr("itemid") + "]").siblings("input").attr('disabled', false);
            $(item).parents().find("div[id=" + $(item).attr("itemid") + "]").siblings("input").val($($(item).parent().siblings("td")[0]).children("div[name='itemDataValue']").text());

            //remove block from hyperlink
            $(item).siblings("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").attr('block', 'false');

            //change completed code icon to available
            $(item).siblings("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").children('img').attr('src', codeItemButtonSrc);

            //update coded item status
            $(item).parent().siblings("td").find("div[name='itemStatus']").text("Not Coded");

            //hide unCode icon
            $(item).css("visibility", "hidden");

            //update coded items counter
             var tdCoded = parseInt($("table.summaryTable tr td[name='tdCoded']").text());
             var tdNotCoded = parseInt($("table.summaryTable tr td[name='tdNotCoded']").text());
             $("table.summaryTable tr td[name='tdCoded'] a").text(tdCoded - 1);
             $("table.summaryTable tr td[name='tdNotCoded'] a").text(tdNotCoded + 1);

             //cleanup results
             $(item).parents().find("div[id=" + $(item).attr("itemid") + "]").find("#tablepaging").remove();

             //update goToEcrf icon if item is sdv or signed
             var subjectId = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='subjectId']").text();
             var eventName = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='eventName']").text();
             var crfName = $("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").parents().siblings("td").find("div[name='crfName']").text();

             $("a[name='goToEcrf']").children('img').each(function () {

                 if(($(this).attr('src').indexOf('icon_Signed_long.gif') > 0 || $(this).attr('src').indexOf('icon_DoubleCheck_long.gif') > 0)
                     && $(this).parents().siblings("td").find("div[name='subjectId']").text() == subjectId
                     && $(this).parents().siblings("td").find("div[name='eventName']").text() == eventName
                     && $(this).parents().siblings("td").find("div[name='crfName']").text() == crfName) {

                     $(this).attr('src', '../images/icon_DEcomplete_long.gif');
                 }
             });

             console.log("Medical uncoding executed successfully");
         },
         error: function(e) {

             console.log("Error:" + e);
         }
    });

    return false;
};

codeAndAlias = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];
    var categoryList = getMedicalCodingCategoryList(item).join("|");

    $.ajax({

        type: "POST",
        url: url + "/codeAndAlias",
        data: {

            categoryList: categoryList,
            item: $(item).parents('div').attr("id"),
            verbatimTerm: $.trim($(item).parents().siblings("td").find("div[name='itemDataValue']").text()),
            coderSearchTerm: $(item).parents().find("div[id=" + $(item).parents('div').attr("id") + "]").siblings("input").val()

        },

        success: function(data) {

            manualUpdateMedicalCodingUX(item);

            console.log("Medical coding executed successfully");
        },
        error: function(e) {

            if($("#sidebar_Alerts_open").css("display") == 'none') {

                leftnavExpand('sidebar_Alerts_open');
                leftnavExpand('sidebar_Alerts_closed');
            }
            //append error to the alert box
            $("#sidebar_Alerts_open .sidebar_tab_content").html('')
                .append("<div class='alert'>Dictionary is not available. Please contact your system administrator.</div>");
            console.log("Error:" + e);
        }
    });
};

function codedItemAutoUpdate() {
    $(document).ready(function () {

        setInterval(function () {

            var arr = new Array();

            $("td:contains('In Process')").each(

                function () {

                    if ($(this).siblings("td").find('div[id]').attr('id') !== undefined) {

                        arr.push($(this).siblings("td").find('div[id]').attr('id'));
                    }
                }
            );

            if (arr.length > 0) {

                arr = arr.toString();
                codedItemAutoUpdateAjax(arr);
            }
        }, 1000);
    });
}

function codedItemAutoUpdateAjax(arr) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];

    $.ajax({
        type: "POST",
        url: url + "/checkCodedItemsStatus",
        data: {
            arr: arr,
            showContext: $("#showContext").val()
        },
        success: function (html) {

            autoUpdateMedicalCodingUX(html);
        },
        error: function(e) {

            console.log("Error:" + e);
        }
    });

}

function autoUpdateMedicalCodingUX(itemsToUpdate) {

    var array = itemsToUpdate.split('separatorMark');

    $.each(array, function () {

        var contextBox = $.trim(this).replace('[', '').replace(']', '');

        if (contextBox.length > 0) {

            var idReg = /idToAppend=\"(\d*)\"/;
            var termReg = /termToAppend=\"([\w\s\d\(\)]+)\"?/;
            var prefReg = /prefToAppend=\"([\w\s\d\(\)]+)\"?/;

            var id = contextBox.match(idReg)[1];
            var term = undefined;
            var pref = undefined;

            try {

                term = contextBox.match(termReg)[1];
                pref = contextBox.match(prefReg)[1];
            } catch(e) {}

            //update code icon url
            $("a[name='Code'][itemid=" + id + "]").children('img').attr('src', '../images/code_confirm.png');

            //update goToEcrf icon if item is sdv or signed
            var subjectId = $("a[name='Code'][itemid=" + id + "]").parents().siblings("td").find("div[name='subjectId']").text();
            var eventName = $("a[name='Code'][itemid=" + id + "]").parents().siblings("td").find("div[name='eventName']").text();
            var crfName = $("a[name='Code'][itemid=" + id + "]").parents().siblings("td").find("div[name='crfName']").text();

            $("a[name='goToEcrf']").children('img').each(function () {
                if(($(this).attr('src').indexOf('icon_Signed_long.gif') > 0 || $(this).attr('src').indexOf('icon_DoubleCheck_long.gif') > 0)
                    && $(this).parents().siblings("td").find("div[name='subjectId']").text() == subjectId
                    && $(this).parents().siblings("td").find("div[name='eventName']").text() == eventName
                    && $(this).parents().siblings("td").find("div[name='crfName']").text() == crfName) {

                    $(this).attr('src', '../images/icon_DEcomplete_long.gif');
                }
            });

            //change coded item status
            $("div[id=" + id + "]").parent().siblings("td").find("div[name='itemStatus']").text('Coded');

            //display uncode icon
            $("a[name='unCode'][itemid=" + id + "]").css("visibility", "visible");

            //update code status counter
            var tdCoded = parseInt($("table.summaryTable tr td[name='tdCoded']").text());
            $("table.summaryTable tr td[name='tdCoded'] a").text(tdCoded + 1);

            //append context box
            if ($.trim($("div[id=" + id + "]").text()).length == 0) {

                $("div[id=" + id + "]").append(contextBox);
            }

            //add hidden marks
            if (term !== undefined && pref !== undefined) {

                $("a[name='unCode'][itemid=" + id + "]").attr('term', term);
                $("a[name='unCode'][itemid=" + id + "]").attr('pref', pref);

                var dictionary = $("div[id=" + id + "]").parent().siblings("td").find("div[name='termDictionary']").text();

                $("a[name='unCode']").filter(function () {
                    return $(this).parents().siblings("td").find("div[name='termDictionary']").text() == dictionary
                        && $(this).parents().siblings("td").find("div[name='itemDataValue']").text().toLowerCase() == term;}).attr('term', term).attr('pref', pref);
            }

            if ($("div:contains('In process')").size() == 0) {

                $('.ui-widget-overlay').remove();
            }
        }
    });
}

function manualUpdateMedicalCodingUX(item) {

    //add block to hyperlink
    $("a[name='Code'][itemid=" + $(item).parents('div').attr("id") + "]").attr('block', 'true');

    //block input field
    $(item).parents("div[id=" + $(item).parents('div').attr("id") + "]").siblings("input").attr('disabled', true);

    if ($(item).parents("div[id=" + $(item).parents('div').attr("id") + "]").parent().siblings("td").find("div[name='itemStatus']").text() == 'Code not Found') {

        var tdCodeNotFound = parseInt($("table.summaryTable tr td[name='tdCodeNotFound']").text());

        $("table.summaryTable tr td[name='tdCodeNotFound'] a").text(tdCodeNotFound - 1);

    } else if ($(item).parents("div[id=" + $(item).parents('div').attr("id") + "]").parent().siblings("td").find("div[name='itemStatus']").text() == 'Not Coded') {

        var tdNotCoded = parseInt($("table.summaryTable tr td[name='tdNotCoded']").text());

        $("table.summaryTable tr td[name='tdNotCoded'] a").text(tdNotCoded - 1);
    }

    $(item).parents("div[id=" + $(item).parents('div').attr("id") + "]").parent().siblings("td").find("div[name='itemStatus']").text('In Process');

    //hide code icon results
    $("div[id=" + $(item).parents('div').attr("id") + "]").html('');

    //block page
    $("<div class='ui-widget-overlay' style='width:" + $(document).width() + "px; height:" + $(document).height() + "px; z-index: 1005;' />").appendTo('body');

}

autoCode = function() {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];

    $.ajax({

        type: "POST",
        url: url + "/autoCode",

        success: function(data) {

            window.location.replace(url + "/codedItems");
        },
        error: function(e) {

            console.log("Error:" + e);
        }
    });
};

function initCrfMoreInfo() {
  var topVal = crfShortcutsTableDefTop;
  if (window.expandCrfInfo != undefined && window.expandCrfInfo == 'true' && $('#CRF_infobox_open').css('display') == 'none') {
    $('#CRF_infobox_closed').css('display', '');
    $('#CRF_infobox_open').css('display', '');
    $('img[id=moreInfoExpandedImg]').css('display', 'none');
    $('img[id=moreInfoCollapsedImg]').css('display', '');
    topVal += $("#CRF_infobox_open").outerHeight();
  } else {
    $('#CRF_infobox_open').css('display', 'none');
    $('img[id=moreInfoExpandedImg]').css('display', '');
    $('img[id=moreInfoCollapsedImg]').css('display', 'none');
  }
  $("#crfShortcutsTable").css("top", topVal + "px");
  adjustCrfShortcutsTable();
  $("table.aka_form_table tr[repeat=template] a.sdvItemLink").remove();
  $("table.aka_form_table tr[repeat=template] div[id^=crfShortcutAnchors_]").remove();
}

function processCrfMoreInfo() {
  var topVal = crfShortcutsTableDefTop;
  var displayValue = $('#CRF_infobox_open').css('display');
  $('#CRF_infobox_open').css('display', displayValue == 'none' ? '' : 'none');
  if (displayValue == 'none') {
    $('img[id=moreInfoExpandedImg]').css('display', 'none');
    $('img[id=moreInfoCollapsedImg]').css('display', '');
    topVal += $("#CRF_infobox_open").outerHeight();
  } else {
    $('img[id=moreInfoExpandedImg]').css('display', '');
    $('img[id=moreInfoCollapsedImg]').css('display', 'none');
  }
  $("#crfShortcutsTable").css("top", topVal + "px");
}

function Pager(tableName, itemsPerPage) {
    this.tableName = tableName;
    this.itemsPerPage = itemsPerPage;
    this.currentPage = 1;
    this.pages = 0;
    this.inited = false;

    this.showRecords = function (from, to) {

        var tableBodies = document.getElementById(tableName).tBodies;
        var rowLength = tableBodies.length;
        var tableObj = document.getElementById(tableName);

        for (var i = 0; i < tableBodies.length; i++) {
            if (i < from || i > to)
                tableBodies[i].style.display = 'none';
            else
                tableBodies[i].style.display = '';
        }

    };

    this.showPage = function (pageNumber) {

        if (!this.inited) {
            alertDialog({ message: "not inited", height: 150, width: 500 });
            return;
        }

        var oldPageAnchor = document.getElementById('pg' + this.currentPage);
        oldPageAnchor.className = 'pg-normal pointed-span';
        this.currentPage = pageNumber;
        var newPageAnchor = document.getElementById('pg' + this.currentPage);
        newPageAnchor.className = 'pg-selected pointed-span';
        var from = (pageNumber - 1) * itemsPerPage;
        var to = from + itemsPerPage - 1;
        this.showRecords(from, to);

    };

    this.prev = function () {

        if (this.currentPage > 1)
            this.showPage(this.currentPage - 1);

    };

    this.next = function () {

        if (this.currentPage < this.pages) {
            this.showPage(this.currentPage + 1);
        }

    };

    this.init = function () {

        var tableBodies = document.getElementById(tableName).tBodies;
        var records = tableBodies.length;
        this.pages = Math.ceil(records / itemsPerPage);
        this.inited = true;

    };

    this.showPageNav = function (pagerName, positionId) {

        if (!this.inited) {
        	alertDialog({ message: "not inited", height: 150, width: 500 });
            return;
        }

        var element = document.getElementById(positionId);
        var pagerHtml = '<span onclick="' + pagerName + '.prev();" class="pg-normal pointed-span"> « Prev </span> ';

        for (var page = 1; page <= this.pages; page++) {
            pagerHtml += '<span id="pg' + page + '" class="pg-normal pointed-span" onclick="' + pagerName + '.showPage(' + page + ');">' + page + '</span> ';
        }

        pagerHtml += '<span onclick="' + pagerName + '.next();" class="pg-normal pointed-span"> Next »</span>';
        element.innerHTML = pagerHtml;

    };
}

deleteTerm = function(item) {

	var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];

    $.ajax({

        type: "POST",
        url: url + "/deleteTerm",
        data: {

            item: $(item).attr("itemid"),
            code: $.trim($(item).parent().siblings("td").find("div[name='itemDataValue']").text()).toLowerCase()
        },

        success: function(data) {

            var dictionary = $(item).parent().siblings("td").find("div[name='termDictionary']").text();

            $("a[name='unCode'][term=" + $(item).attr("term").toLowerCase() + "][pref=" + $(item).attr("pref").toLowerCase() + "]").filter(function () {
                return $(this).parents().siblings("td").find("div[name='termDictionary']").text() == dictionary; }).attr('term', '').attr('pref', '');

            console.log("Term successfully deleted");
        },
        error: function(e) {

            console.log("Error:" + e);
        }
    });
};

codeItemFields = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];
    var term = $(item).closest('tbody').find('td').filter(function () {
        return $.trim($(this).attr('id')) == "EXT" || $.trim($(this).attr('id')) == "AEG";
    }).next().text();

    $("<div class='ui-widget-overlay' style='width:" + $(document).width() + "px; height:" + $(document).height() + "px; z-index: 1005;' />").appendTo('body');

    $.ajax({

        type: "POST",
        url: url + "/codeItemFields",
        data: {

            term: $.trim(term),
            codedItemUrl: $.trim($(item).closest('tbody').find('a').text())
        },

        success: function(data) {

            $("div[id=" + $(item).parents('div').attr("id") + "]").find('td').filter(function () {
                return $.trim($(this).text()) == $.trim($(item).closest('tbody').find('a').text());
            }).closest('tbody').find('input').css('visibility', 'visible');
            $("div[id=" + $(item).parents('div').attr("id") + "]").find('td').filter(function () {
                return $.trim($(this).text()) == $.trim($(item).closest('tbody').find('a').text());
            }).parent('tr').after(data);

            $(item).css('visibility', 'hidden');
            $('.ui-widget-overlay').remove();
        },
        error: function(e) {

            $('.ui-widget-overlay').remove();

            console.log("Error:" + e);
        }
    });
};

function showMedicalCodingAlertBox(item, ajaxResponse){

    if ($("#alertBox").length == 0) {

        $("<div id='alertBox' title='Medical coding process message'>" +
            "<div style='clear: both; margin-top: 2%; text-align: center;'>Retrieving medical codes. Please wait...</div>&nbsp;" +
            "<img style='display:block;margin:auto;' src='../images/ajax-loader-blue.gif'>" +
            "</div>").appendTo("body");

        $("#alertBox").dialog({
            autoOpen : true,
            modal : true,
            height: 150,
            width: 450,
            buttons: { 'Cancel': function() { hideMedicalCodingAlertBox(ajaxResponse); }},
            open: function(event, ui) {

            	openDialog({ 
            		dialogDiv: this, 
            		cancelButtonValue: "Cancel",
            		imagesFolderPath: determineImagesPath() 
    			});
            }
        });
    }
}

function hideMedicalCodingAlertBox(ajaxResponse) {

    if ($("#alertBox").length > 0) {

        ajaxResponse.abort();

        $("#alertBox").remove();
    }

}

function showMedicalCodingUncodeAlertBox(item) {
	
	setAccessedObjected(item);
    var isLocked = $("a[name='goToEcrf'][itemid=" + $(item).attr("itemid") + "]").children('img').filter(function () {
        return $(this).attr('src').indexOf('icon_Locked_long.gif') > 0;
    });
    if (isLocked.size() > 0) {
        var locked_ecrf_message = $("#locked_crf_message").val();
        alertDialog({ message: locked_ecrf_message, height: 150, width: 500 });
        return;
    }

    if ($("#alertBox").length == 0) {

        if($(item).attr("term").length > 0) {

            $("<div id='alertBox' title='Warning message'>" +
                "<div style='clear: both; margin-top: 2%; text-align: center;'>Check the item you want to delete:<br><br>" +
                "<input name='answer' type='radio' value='Code'>Code" +
                "<input name='answer' type='radio' value='Alias'>Alias" +
                "<input name='answer' type='radio' value='Both'>Both" +
                "</div></div>").appendTo("body");

            $("#alertBox").dialog({
                autoOpen : true,
                closeOnEscape: false,
                modal : true,
                height: 150,
                width: 500,
                buttons:{ 'Submit': function() {
	                    var answer = $('input[name=answer]:checked' ).val();
	                    if(answer == 'Code') {
	                        uncodeCodeItem(item);
	                    } else if(answer == 'Alias') {
	                        deleteTerm(item);
	                    } else if(answer == 'Both') {
	                        uncodeCodeItem(item);
	                        deleteTerm(item);
	                    }
	                    $("#alertBox").remove();
	                },
                    'Cancel': function() { 
                    	$("#alertBox").remove(); 
                	}
                },

                open: function(event, ui) {
                	
                	openDialog({ 
                		dialogDiv: this, 
                		cancelButtonValue: "Cancel", 
                		okButtonValue: "Submit", 
                		imagesFolderPath: determineImagesPath() 
        			});
                }
            });
        } else {

            $("<div id='alertBox' title='Warning message'>" +
                "<div style='clear: both; margin-top: 2%; text-align: center;'>Are you sure you want to delete this code?</div>&nbsp;" +
                "</div>").appendTo("body");

            $("#alertBox").dialog({
                autoOpen : true,
                closeOnEscape: false,
                modal : true,
                height: 150,
                width: 450,
                buttons: { 'Submit': function() { 
	                	uncodeCodeItem(item); $("#alertBox").remove(); 
                	}, 
                	'Cancel': function() { 
                		$("#alertBox").remove();
            		}},
                open: function(event, ui) {

                	openDialog({ 
                		dialogDiv: this, 
                		cancelButtonValue: "Cancel", 
                		okButtonValue: "Submit", 
                		imagesFolderPath: determineImagesPath() 
        			});
                }
            });
        }
    }
}

function getBrowserClientWidth() {
  var v = 0;
  if (self.innerWidth)
  {
    v = self.innerWidth;
  }
  else if (document.documentElement && document.documentElement.clientWidth)
  {
    v = document.documentElement.clientWidth;
  }
  else if (document.body)
  {
    v = document.body.clientWidth;
  }
  return v;
}

function getBrowserClientHeight() {
  var v = 0;
  if (self.innerHeight)
  {
    v = self.innerHeight;
  }
  else if (document.documentElement && document.documentElement.clientHeight)
  {
    v = document.documentElement.clientHeight;
  }
  else if (document.body)
  {
    v = document.body.clientHeight;
  }
  return v;
}

function enableDNBoxFeatures() {
    if (crfShortcutsBoxState == 1 && $("#crfShortcutsTable").length > 0 && $("#crfShortcutsTable").css("position") == "absolute") {
        $("#crfShortcutsTable").css("position", "fixed");
        $("#crfShortcutsTable").draggable({
            containment: "window",
            scroll: false,
            start: function (event, ui) {
                $("#crfShortcutsTable").css("position", "absolute");
            }, drag: function (event, ui) {
                if (ui.offset != undefined && ui.position != undefined && ui.offset.top != ui.position.top) ui.position.top = ui.offset.top;
                if (ui.offset != undefined && ui.position != undefined && ui.offset.left != ui.position.left) ui.position.left = ui.offset.left;
            }, stop: function (event, ui) {
                $("#crfShortcutsTable").css("position", "fixed");
                $("#crfShortcutsTable").css("top", (parseInt($("#crfShortcutsTable").css("top")) - $(window).scrollTop()) + 'px');
                $("#crfShortcutsTable").css("left", (parseInt($("#crfShortcutsTable").css("left")) - $(window).scrollLeft()) + 'px');
            }
        });
    }
}

function resetCrfShortcutsTable() {
    if ($("#crfShortcutsTable").length > 0) {
        var crfShortcutsTableTop = crfShortcutsTableDefTop;
        if ($('#CRF_infobox_open').css('display') != "none") {
            crfShortcutsTableTop += $("#CRF_infobox_open").outerHeight();
        }
        $("#crfShortcutsTable").css("position", "absolute");
        $("#crfShortcutsTable").css("top", crfShortcutsTableTop + 'px');
        $("#crfShortcutsTable").css("left", crfShortcutsTableDefLeft + 'px');
        $("#crfShortcutsTable").draggable("destroy");
    }
}

function processPushpin(element) {
    if (element.hasClass("ui-icon-pin-w")) {
        crfShortcutsBoxState = 0;
        resetCrfShortcutsTable();
        element.removeClass("ui-icon-pin-w");
        element.addClass("ui-icon-bullet");
        element.attr("title", element.attr("unlocktitle"));
    } else {
        crfShortcutsBoxState = 1;
        enableDNBoxFeatures();
        element.removeClass("ui-icon-bullet");
        element.addClass("ui-icon-pin-w");
        element.attr("title", element.attr("locktitle"));
    }
    return false;
}

$(window).scroll(function() {
    if (crfShortcutsBoxState == 1) {
        enableDNBoxFeatures();
    }
})

$(window).resize(function() {
    if (crfShortcutsBoxState == 1) {
        resetCrfShortcutsTable();
    }
})

function isElementOutViewport(element) {
    var result = false;
    if (element != undefined) {
        var additionalVal = 20;
        var jqWindow = $(window);
        var windowLeft = jqWindow.scrollLeft();
        var windowTop = jqWindow.scrollTop();
        var windowHeight = getBrowserClientHeight();
        var windowWidth = getBrowserClientWidth();

        var jqElement = $(element);
        var jqElementOffset = jqElement.offset();
        var elementTop = jqElementOffset.top;
        var elementLeft = jqElementOffset.left;
        var elementHeight = jqElement.outerHeight();
        var elementWidth = jqElement.outerWidth();
        var elementBottom = elementTop + elementHeight;
        var elementRight = elementLeft + elementWidth;

        result = elementLeft - additionalVal < windowLeft || elementTop - additionalVal < windowTop || elementRight + additionalVal > windowLeft + windowWidth || elementBottom + additionalVal > windowTop + windowHeight;
    }
    return result;
}

function adjustCrfShortcutsTable() {
    var crfShortcutsSpan = parseInt($("#crfShortcutsSpan").text());
    var crfShortcutsWidth = parseInt($("#crfShortcutsWidth").text());
    var allowSdvWithOpenQueries = $("#crfShortcutsAllowSdvWithOpenQueries").text() == "yes";

    var itemsToSDVAreHidden = false;
    var userIsAbleToSDVItems = ($("#userIsAbleToSDVItems").text() == "true");
    var hasDNs = !(parseInt($("#crfShortcutTotalNew").text()) == 0 && parseInt($("#crfShortcutTotalUpdated").text()) == 0 && ($("#crfShortcutTotalResolutionProposed").length == 0 || parseInt($("#crfShortcutTotalResolutionProposed").text()) == 0) && parseInt($("#crfShortcutTotalClosed").text()) == 0 && parseInt($("#crfShortcutTotalAnnotations").text()) == 0);
    var hasOutstandingDNs = !(parseInt($("#crfShortcutTotalNew").text()) == 0 && parseInt($("#crfShortcutTotalUpdated").text()) == 0 && ($("#crfShortcutTotalResolutionProposed").length == 0 || parseInt($("#crfShortcutTotalResolutionProposed").text()) == 0));
    var hasItemsToSDV = parseInt($("#crfShortcutTotalItemsToSDV").text()) > 0;

    if (!hasItemsToSDV || (!allowSdvWithOpenQueries && hasOutstandingDNs) || !userIsAbleToSDVItems) {
        if ((!allowSdvWithOpenQueries && hasOutstandingDNs) || !userIsAbleToSDVItems) {
            $("a.sdvItemLink").addClass("hidden");
        } else {
            $("a.sdvItemLink").removeClass("hidden");
        }
        itemsToSDVAreHidden = true;
        $("#crfShortcutsSubTable tr:eq(1) td:eq(" + (crfShortcutsSpan - 1) + ")").addClass("hidden");
        $("#crfShortcutsSubTable tr:eq(2) td:eq(" + (crfShortcutsSpan - 1) + ")").addClass("hidden");
        crfShortcutsSpan--;
    } else {
        $("a.sdvItemLink").removeClass("hidden");
        $("#crfShortcutsSubTable tr:eq(1) td:eq(" + (crfShortcutsSpan - 1) + ")").removeClass("hidden");
        $("#crfShortcutsSubTable tr:eq(2) td:eq(" + (crfShortcutsSpan - 1) + ")").removeClass("hidden");
    }

    var endWidth = crfShortcutsSpan * crfShortcutsWidth;
    $("#crfShortcutsSubTable").attr("width", endWidth + "px");
    $("#crfShortcutsSubTable tr:eq(0) td").attr("colspan", crfShortcutsSpan);
    $("#crfShortcutsSubTable tr:eq(1) td").attr("width", ((crfShortcutsWidth / endWidth) * 100) + "%");

    if (!hasDNs && itemsToSDVAreHidden) {
        $("#crfShortcutsTable").addClass("hidden");
        $("#crfSectionTabsTable").attr("style", "");
    } else if ($("#crfShortcutsTable").hasClass("hidden")) {
        $("#crfShortcutsTable").removeClass("hidden");
        $("#crfSectionTabsTable").attr("style", "padding-top: 80px;");
    }
}

function itemLevelUnSDV(contextPath, itemDataId, sectionId, eventDefinitionCrfId, sdvItemIconSrc, sdvImgSrc, sdvItemTitle, sdvCrfTitle) {
    sdvItem(contextPath, itemDataId, sectionId, eventDefinitionCrfId, sdvItemIconSrc, sdvImgSrc, sdvItemTitle, sdvCrfTitle, "unsdv");
}

function itemLevelSDV(contextPath, itemDataId, sectionId, eventDefinitionCrfId, sdvItemIconSrc, sdvImgSrc, sdvItemTitle, sdvCrfTitle) {
    sdvItem(contextPath, itemDataId, sectionId, eventDefinitionCrfId, sdvItemIconSrc, sdvImgSrc, sdvItemTitle, sdvCrfTitle, "sdv");
}

function sdvItem(contextPath, itemDataId, sectionId, eventDefinitionCrfId, sdvItemIconSrc, sdvImgSrc, sdvItemTitle, sdvCrfTitle, action) {
    gfAddOverlay();
    try {
        jQuery.ajax({
            url: contextPath + "/pages/sdvItem",
            type: "GET",
            data: {itemDataId: itemDataId, sectionId: sectionId, eventDefinitionCrfId: eventDefinitionCrfId, action: action},
            cache: false,
            success: function (data) {
                jsonData = eval("(" + data + ")");
                resetHighlightedFieldsForCRFShortcutAnchors();
                var sdvItem = $("#sdv_itemData_" + itemDataId);
                $("a[id^=itemToSDV_]").remove();
                sdvItem.attr("onclick", "");
                sdvItem.unbind("click");
                if (action == "sdv") {
                    sdvItem.click(function() {
                        eval($("#unSDVFunction_" + itemDataId).text().replace("onclick=\"","").replace("\"",""));
                    });
                } else {
                    sdvItem.click(function() {
                        eval($("#sdvFunction_" + itemDataId).text().replace("onclick=\"","").replace("\"",""));
                    });
                }
                sdvItem.attr("src", sdvItemIconSrc);
                sdvItem.mouseover(function () {
                    callTip(sdvItemTitle)
                });
                $("#crfShortcutTotalItemsToSDV").text(" " + jsonData.totalItemsToSDV + " ");
                $("#crfShortcutTotalItemsToSDV").parent("a.crfShortcut").attr("sectiontotal", jsonData.totalSectionItemsToSDV);
                if (jsonData.crf == "sdv" || jsonData.crf == "completed") {
                    var crfName = $("#crfNameId > img");
                    crfName.attr("src", sdvImgSrc);
                    crfName.attr("alt", sdvCrfTitle);
                    crfName.attr("title", sdvCrfTitle);
                    refreshSdvPageAfterItemSDV();
                }
                for (var n = 0; n < jsonData.itemDataItems.length; n++) {
                    var itemData = jsonData.itemDataItems[n];
                    var holder = $("#sdv_itemData_" + itemData.itemDataId).closest(".itemHolderClass").find("div[id^=crfShortcutAnchors_]");
                    var data = "<a id=\"itemToSDV_" + (n + 1) + "\" rel=\"" + itemData.itemId + "\" alt=\"" + itemData.rowCount + "\">";
                    holder.append(data);
                }
                adjustCrfShortcutsTable();
                gfRemoveOverlay();
            }
        });
    } catch (e) {
        gfRemoveOverlay();
    }
}

function highlightDn(id, color, delay) {
    var moreInfo = false;
    var element = $("#" + id);
    var inputId = "input" + element.attr("rel");
    if (element.attr("rel") == "interviewer" || element.attr("rel") == "interviewDate") {
        inputId = element.attr("rel");
        if (color == "yellow" && $("#CRF_infobox_open").css("display") == "none") {
            moreInfo = true;
        }
    }
    var inputHolderElement = $("#itemHolderId_" + element.attr("alt") + inputId);
    var inputElement = inputHolderElement.find("input[id*=" + element.attr("alt") + inputId + "]");
    inputElement = inputElement.length == 0 ? inputHolderElement.find("select[id*=" + element.attr("alt") + inputId + "]") : inputElement;
    inputElement = inputElement.length == 0 ? inputHolderElement.find("textarea[id*=" + element.attr("alt") + inputId + "]") : inputElement;
    if (inputElement.attr("type") != undefined && (inputElement.attr("type").toLowerCase() == "radio" || inputElement.attr("type").toLowerCase() == "checkbox")) {
        inputElement = inputElement.parent();
    }
    setTimeout(function () {
        inputElement.css("background-color", color);
        if (moreInfo) {
            processCrfMoreInfo();
        }
        if (color == "yellow" && isElementOutViewport(inputHolderElement.get(0))) {
            var newTop = inputHolderElement.offset().top - $("#crfShortcutsTable").outerHeight() - 20;
            var newLeft = inputHolderElement.offset().left - 20;
            $(window).scrollTop(newTop < 0 ? 0 : newTop);
            $(window).scrollLeft(newLeft < 0 ? 0 : newLeft);
        }
    }, delay == undefined ? 0 : parseInt(delay));
}

function resetHighlightedFieldsForCRFShortcutAnchors() {
    if (currentHighlightedShortcutAnchor != undefined) {
        highlightDn(currentHighlightedShortcutAnchor, "");
    }
    currentHighlightedShortcutAnchorCounter = 0;
    currentHighlightedShortcutAnchor = undefined;
    currentHighlightedShortcutAnchorInd = undefined;
}

function highlightFirstFieldForCRFShortcutAnchors(idToHighlight) {
    resetHighlightedFieldsForCRFShortcutAnchors();
    for (var i = 0; i < crfShortcutAnchors.length; i++) {
        if (idToHighlight.startsWith(crfShortcutAnchors[i])) {
            currentHighlightedShortcutAnchorInd = i;
            currentHighlightedShortcutAnchorCounter = parseInt(idToHighlight.replace(/.*_/g, ""));
        }
    }
    if (currentHighlightedShortcutAnchorInd != undefined) {
        currentHighlightedShortcutAnchor = idToHighlight;
        highlightDn(currentHighlightedShortcutAnchor, "yellow");
    }
}

function highlightFieldForCRFShortcutAnchor(ind, currentElement) {
    var delay = 0;
    enableDNBoxFeatures();
    var nextDnLink = $(currentElement).attr("nextdnlink");
    if (currentHighlightedShortcutAnchor != undefined) {
        highlightDn(currentHighlightedShortcutAnchor, "");
    }
    if (currentHighlightedShortcutAnchorInd == undefined || currentHighlightedShortcutAnchorInd != ind) {
        currentHighlightedShortcutAnchorCounter = 0;
    }
    currentHighlightedShortcutAnchorCounter++;
    var sectionTotal = parseInt($(currentElement).attr("sectiontotal"));
    if (currentHighlightedShortcutAnchorCounter > sectionTotal) {
        if (nextDnLink.startsWith("#")) {
            delay = 100;
            currentHighlightedShortcutAnchorCounter = 1;
        } else {
            location.href = nextDnLink;
            return;
        }
    }
    currentHighlightedShortcutAnchorInd = ind;
    var newCurrentHighlightedShortcutAnchor = crfShortcutAnchors[currentHighlightedShortcutAnchorInd] + currentHighlightedShortcutAnchorCounter;
    if (currentHighlightedShortcutAnchor != undefined && currentHighlightedShortcutAnchor != newCurrentHighlightedShortcutAnchor && $("#" + currentHighlightedShortcutAnchor).parent()[0] == $("#" + newCurrentHighlightedShortcutAnchor).parent()[0]) {
        if (currentHighlightedShortcutAnchor.replace(/_.*/g,"") == newCurrentHighlightedShortcutAnchor.replace(/_.*/g,"") && $("#" + currentHighlightedShortcutAnchor).parent()[0] == $("#" + newCurrentHighlightedShortcutAnchor).parent()[0]) {
            highlightFieldForCRFShortcutAnchor(ind, currentElement);
            return;
        }
        delay = 100;
    }
    if ($("#" + newCurrentHighlightedShortcutAnchor).parents(".itemHolderClass:first").parents("tr:first").css("display") == "none" || $("#" + newCurrentHighlightedShortcutAnchor).parents("div[id^=crfShortcutAnchors_]:first").parents("td:first").css("display") == "none") {
        highlightFieldForCRFShortcutAnchor(ind, currentElement);
        return;
    }
    currentHighlightedShortcutAnchor = newCurrentHighlightedShortcutAnchor;
    highlightDn(currentHighlightedShortcutAnchor, "yellow", delay);
}

function updateCRFHeaderFunction(parametersHolder) {
    try {
        parametersHolder.totalItems = 0;
        $("div[id^=crfShortcutAnchors_]").each(function() {
            var rowCount = $(this).attr("id").replace("crfShortcutAnchors_", "").replace(/item_.*/, "");
            var itemId = $(this).attr("id").replace(/crfShortcutAnchors_.*item_/, "");
            itemId = itemId == "interviewer" || itemId == "interviewDate" ? itemId : parseInt(itemId);
            var field = $(this).attr("field");
            if ($(this).parent().parent().attr("repeat") != "template") {
                parametersHolder.totalItems++;
                parametersHolder["rowCount_" + parametersHolder.totalItems] = rowCount;
                parametersHolder["itemId_" + parametersHolder.totalItems] = itemId;
                parametersHolder["field_" + parametersHolder.totalItems] = field;
            }
            $("#crfShortcutAnchors_" + rowCount + "item_" + itemId + " a[id^=newDn_]").remove();
            $("#crfShortcutAnchors_" + rowCount + "item_" + itemId + " a[id^=updatedDn_]").remove();
            $("#crfShortcutAnchors_" + rowCount + "item_" + itemId + " a[id^=closedDn_]").remove();
            $("#crfShortcutAnchors_" + rowCount + "item_" + itemId + " a[id^=annotationDn_]").remove();
            $("#crfShortcutAnchors_" + rowCount + "item_" + itemId + " a[id^=resolutionProposedDn_]").remove();
        });
        parametersHolder.sectionId = $("input[name=sectionId]").val();
        jQuery.ajax({
            url : parametersHolder.contextPath + "/UpdateCRFHeader",
            type : "POST",
            data : parametersHolder,
            cache : false,
            success : function(data) {
                var jsonObject = eval("(" + data + ")");

                $("#crfShortcutTotalNew").text(" " + jsonObject.totalNew + " ");
                $("#crfShortcutTotalUpdated").text(" " + jsonObject.totalUpdated + " ");
                $("#crfShortcutTotalResolutionProposed").text(" " + jsonObject.totalResolutionProposed + " ");
                $("#crfShortcutTotalClosed").text(" " + jsonObject.totalClosed + " ");
                $("#crfShortcutTotalAnnotations").text(" " + jsonObject.totalAnnotations + " ");

                $("#crfShortcutTotalNew").parent().attr("sectiontotal", jsonObject.sectionTotalNew);
                $("#crfShortcutTotalNew").parent().attr("nextdnlink", jsonObject.nextNewDnLink);

                $("#crfShortcutTotalUpdated").parent().attr("sectiontotal", jsonObject.sectionTotalUpdated);
                $("#crfShortcutTotalUpdated").parent().attr("nextdnlink", jsonObject.nextUpdatedDnLink);

                $("#crfShortcutTotalResolutionProposed").parent().attr("sectiontotal", jsonObject.sectionTotalResolutionProposed);
                $("#crfShortcutTotalResolutionProposed").parent().attr("nextdnlink", jsonObject.nextResolutionProposedDnLink);

                $("#crfShortcutTotalClosed").parent().attr("sectiontotal", jsonObject.sectionTotalClosed);
                $("#crfShortcutTotalClosed").parent().attr("nextdnlink", jsonObject.nextClosedDnLink);

                $("#crfShortcutTotalAnnotations").parent().attr("sectiontotal", jsonObject.sectionTotalAnnotations);
                $("#crfShortcutTotalAnnotations").parent().attr("nextdnlink", jsonObject.nextAnnotationDnLink);

                adjustCrfShortcutsTable();

                for (var n = 0; n < jsonObject.items.length; n++) {
                    var p;
                    var crfShortcutsData = "";
                    var item = jsonObject.items[n];
                    var inputHolderElement = $("#crfShortcutAnchors_" + item.rowCount + "item_" + (item.itemId == "interviewer" || item.itemId == "interviewDate" ? item.itemId : item.itemId));
                    for (p = 0; p < item.newDn.length; p++) {
                        crfShortcutsData += "<a id=\"" + item.newDn[p] + "\" rel=\"" + item.itemId + "\" alt=\"" + item.rowCount + "\"></a>";
                    }
                    for (p = 0; p < item.updatedDn.length; p++) {
                        crfShortcutsData += "<a id=\"" + item.updatedDn[p] + "\" rel=\"" + item.itemId + "\" alt=\"" + item.rowCount + "\"></a>";
                    }
                    for (p = 0; p < item.resolutionProposedDn.length; p++) {
                        crfShortcutsData += "<a id=\"" + item.resolutionProposedDn[p] + "\" rel=\"" + item.itemId + "\" alt=\"" + item.rowCount + "\"></a>";
                    }
                    for (p = 0; p < item.closedDn.length; p++) {
                        crfShortcutsData += "<a id=\"" + item.closedDn[p] + "\" rel=\"" + item.itemId + "\" alt=\"" + item.rowCount + "\"></a>";
                    }
                    for (p = 0; p < item.annotationDn.length; p++) {
                        crfShortcutsData += "<a id=\"" + item.annotationDn[p] + "\" rel=\"" + item.itemId + "\" alt=\"" + item.rowCount + "\"></a>";
                    }
                    inputHolderElement.prepend(crfShortcutsData);
                }

                gfRemoveOverlay();
            }
        });
    } catch (e) {
        console.log(e.message);
    }
}

function hideElement(elementName){
	setDisplayProperty(elementName, "none");
}
		
function showElement(elementName){
	setDisplayProperty(elementName, "");
}
		
function setDisplayProperty(elementName, propertyValue){
	var objElement;
	objElement = MM_findObj(elementName);
	if (objElement != null) {
		if (objElement.style) { objElement = objElement.style; }
	    	objElement.display = propertyValue;
		}
	}
	    
function setElements(typeId,user1,user2,id,filter1,nw,ud,rs,cl,na) {
	setStatusWithId(typeId,id,filter1,nw,ud,rs,cl,na);
	if(typeId == 3) {//query
		showElement(user1+id);
		showElement(user2+id);	
	} else {
		hideElement(user1+id);
		hideElement(user2+id);
	}
}
		
function setDisabledProperty(elementName, propertyValue){
	var objElement;
	objElement = document.getElementById(elementName);
	if (objElement != null) {
		objElement.disabled = propertyValue;
	}
}
		
function switchOnElement(elementName){
	setDisabledProperty(elementName, '');
}
		
function switchOffElement(elementName){
	setDisabledProperty(elementName, 'disabled');
}

function setParameterForDNWithPath(toOverwrite, field, parameterName, value, contextPath) {
    if (contextPath) {
        $.ajax({
            url: contextPath + '/ChangeParametersForDNote',
            type: 'POST',
            data: 'field=' + field + '&parameterName=' + parameterName + '&value=' + value + '&toOverwrite=' + toOverwrite,
            dataType: 'text'
        });
    }
}

function turnOffIsInRFCErrorParamOfDN(field) {
	setParameterForDN(field, "isInRFCError", "0");
}

function turnOnIsDataChangedParamOfDN(field) {
	setParameterForDN(field, "isDataChanged", "1");
}
	

function viewCrfByVersion(eventDefinitionCRFId, studySubjectId, crfVersionId, eventId, tabId,  exitTo) {
  $("body").append("<form id='viewSectionDataEntryForm' style='display: none;' method='GET' action='ViewSectionDataEntry'>" +
      "<input type='hidden' name='eventDefinitionCRFId' value='" + eventDefinitionCRFId + "'/>" +
      "<input type='hidden' name='studySubjectId' value='" + studySubjectId + "'/>" +
      "<input type='hidden' name='crfVersionId' value='" + crfVersionId + "'/>" +
      "<input type='hidden' name='eventId' value='" + eventId + "'/>" +
      "<input type='hidden' name='tabId' value='" + tabId + "'/>" +
      (exitTo != undefined ? "<input type='hidden' name='exitTo' value='" + exitTo + "'/>" : "") +
      "</form>");
  $("#viewSectionDataEntryForm").submit();
}

function upateEventDefinitionAddCRF(){
	$("input[type=hidden]").attr("value","addCrfs");
	document.forms["UpdateEventDefinition"].submit();
}

function isPhoneNumberValid(id, msg) {
  var valid = $("#" + id).val().replace(/\+{0,1}\d{1,}/, '') == '';
  if (!valid) {
    $("#spanAlert-phone").html(msg);
    if ($("#spanAlert-phone").siblings("br").length == 0) {
      $("#spanAlert-phone").parent().append("<br/><br/>");
    }
  }
  return valid;
}

function chooseHomePageVersion(){
	if($.browser.msie && parseFloat($.browser.version) < 8){
		$(".new_home_page").remove();
	} else {
		$(".old_home_page").remove();
	}
}

/* =======================================================================================
* Adding an ability to uncheck radiobuttons on all pages
* To make radiobutton uncheckable, you should add class "uncheckable_radio" to it
========================================================================================== */
$(document).ready(function() {
    $("input[type='radio'].uncheckable_radio").each(function() {
        $(this).attr('previousValue', $(this).attr('checked') == true ? 'checked' : '');
        $(this).click(function() {
            var previousValue = $(this).attr('previousValue');
            var name = $(this).attr('name');

            if (previousValue == 'checked') {
                $(this).removeAttr('checked');
                $(this).attr('previousValue', false);
            } else {
                $("input[name=" + name + "]:radio").attr('previousValue', false);
                $(this).attr('previousValue', 'checked');
            }
        });
    });
});


function showEmailField(element) {
	if ($(element).attr('previousValue') == 'checked') {
		$(element).parent().parent().find(".email_wrapper").css("display",
				"none");
		$(element).parent().parent().find(".email_wrapper input").val("");
	} else {
		$(element).parent().parent().find(".email_wrapper").css("display",
				"table-cell");
	}
}

function FieldChecker(selector, checkType) {
	this.selector = selector;
	this.checkType = checkType;
	var numberOfErrors = 0;
	this.errorsExists = false;

	this.check = function() {

		$(this.selector)
				.each(
						function(index) {

							if ($(this).parent().css("display") != "none") {
								var currentValue = $(this).val();
								var currentObject = $(this)

								switch (checkType) {
								case "email":
									var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
									var partsOfStr = currentValue.split(',');

									partsOfStr.forEach(function(entry) {

										var trimmedMail = entry.replace(/^\s+/, '').replace(/\s+$/, '');

										if (!re.test(trimmedMail)) {
											numberOfErrors++;
											currentObject.focus();
											currentObject.parent().parent().find(
													".alert").css("display",
													"block");
										} else {
											currentObject.parent().parent().find(
													".alert").css("display",
													"none");
										}
									});
									break;
								default:
									numberOfErrors = 0;

								}
							}
						});

		if (numberOfErrors != 0) {
			this.errorsExists = true;
		} else {
			this.errorsExists = false;
		}

		return this.errorsExists;
	}
}

/* =======================================================================================
* Using this function we can add dinamic validation to different number of the fields.
* Expected values:
* "expectedValues" - identifiers for regular expressions that need to be run for every selector;
* "selectors" - array of fields that will be checked;
* "formName" - name of form that will be submitted on success.
* 
* Item block need to have folowing srtructure, for correct errors displaying:
* <wrapper>
* 	<item-block-wrapper>
* 		...
* 		<item class="selector">
* 		...
* 	</item-clock-wrapper>
* 	<alert-block class="alert"> Error message </alert-block>
* </wrapper>
========================================================================================== */
function validateCustomFields(expectedValues, selectors, formToSubmit) {
	var errorsExists = false;

	selectors
			.forEach(function(entry, index) {
				var currentFieldChecker = new FieldChecker(entry,
						expectedValues[index]);
				var checkResult = currentFieldChecker.check();

				if (checkResult == true) {
					errorsExists = true;
				}
			});

	if (!errorsExists) {
		$(formToSubmit).submit();
	}
}

function alertDialog(params){
	
	$("<div id='alertDialog' title='Message Alert'>" +
        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
        params.message +
        "</div></div>").appendTo("body");
	
    $("#alertDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: params.height,
        width: params.width,
        buttons:{ 
        	'Ok': function() {
            	$("#alertDialog").remove();
        	}
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	okButtonValue: "Ok", 
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
}

function crfCompleteAuthorize(params){
	
	if(params.checkbox.checked == false){
		return true;
	}
	$("<div id='crfPasswordDialog' title='Authorization'>" +
	        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
	        params.message +
	        "</div>" +
	        "<div style='text-align:left; width:100%;'>" +
	        "<input align='left' type='password' name='password' id='passwordIdNew'/>" +
	        "</div>" +
	        "</div>").appendTo("body");
	
	$("#crfPasswordDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: params.height,
        width: params.width,
        buttons:{ 
        	'Ok': function() {
        		params.checkbox.checked = true;
        		requestSignatureFromCheckbox(document.getElementById('passwordIdNew').value, params.checkbox);
            	$("#crfPasswordDialog").remove();
        	}, 
        	'Cancel': function() {
        		$("#crfPasswordDialog").remove();
        	}
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	okButtonValue: "Ok", 
            	cancelButtonValue: "Cancel",
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
	
}

function confirmDialog(params){
	
	$("<div id='confirmDialog' title='Confirm Action'>" +
        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
        params.message +
        "</div></div>").appendTo("body");
	
    $("#confirmDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: params.height,
        width: params.width,
        buttons:{ 
        	'Yes': function() {
            	$("#confirmDialog").remove();
            	if(params.aLink){
            		var href = $(params.aLink).attr('href');
                	window.location.href = href;
            	} else if (params.redirectLink){
            		window.location.href = params.redirectLink;
            	} else if (params.checkbox) {
            		var isChecked = $(params.checkbox).is(':checked');
            		$(params.checkbox).attr("checked", !isChecked);
            	}            	
        	},
            'No': function() { 
            	$("#confirmDialog").remove(); 
            }
        },
        
        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	cancelButtonValue: "No", 
            	okButtonValue: "Yes", 
            	imagesFolderPath: determineImagesPath()
    		});

        	if(params.aLink && params.highlightRow) {
        		setAccessedObjected(params.aLink);
        	}
        }
    });
    return false;
}

function openDialog(params) {
	$(".ui-dialog-titlebar-close", $(params.dialogDiv).parent()).hide();
    $('.ui-dialog-titlebar').css('border', '1px Solid ' + theme.mainColor);
    $('.ui-widget-content').css('border', '0');
    
    if($.browser.msie){
    	$(params.dialogDiv).focus();
    }
    
    if(params.cancelButtonValue)
    	setButtonAttributes(params.cancelButtonValue);
    
    if(params.okButtonValue)
    	setButtonAttributes(params.okButtonValue);    
    
    setDialogTheme(params);
    
}

function setButtonAttributes(buttonValue){
	
	$('.ui-dialog-buttonpane').find('button:contains("' + buttonValue + '")')
    .removeAttr('class').addClass('button_medium').css('width', '120px').css('float', 'left').css('line-height', '0').css('padding','8px 0');
    $('.ui-dialog-buttonpane').find('button:contains("' + buttonValue + '")')
        .mouseover(function() {$(this).removeClass("ui-state-hover");})
        .focus(function () {$(this).removeClass("ui-state-focus");});
    $('.ui-dialog-buttonpane').find('button:contains("' + buttonValue + '")').blur();
}

function setDialogTheme(params) {
    if (theme.name != 'blue') {
        if (params.cancelButtonValue)
            $('.ui-dialog-buttonpane').find('button:contains("' + params.cancelButtonValue + '")').css('background-image', 'url(' + params.imagesFolderPath + theme.name + '/button_medium_BG.gif)');
        if (params.okButtonValue)
            $('.ui-dialog-buttonpane').find('button:contains("' + params.okButtonValue + '")').css('background-image', 'url(' + params.imagesFolderPath + theme.name + '/button_medium_BG.gif)');
    }
    $('.ui-dialog .ui-dialog-titlebar').find('span').css('color', theme.mainColor);
}

function confirmMarkAsComplete(params){
	
	$("<div id='confirmDialog' title='Confirm Action'>" +
        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
        params.message +
        "</div></div>").appendTo("body");
	
    $("#confirmDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: params.height,
        width: params.width,
        buttons:{ 
        	'Yes': function() {
            	$("#confirmDialog").remove();
            	document.getElementById(params.elementName).value=2;
                document.forms[1].action=params.formAction;
                document.forms[1].method="POST";
                document.forms[1].submit();
        	},
            'No': function() { 
            	$("#confirmDialog").remove(); 
            }
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	cancelButtonValue: "No", 
            	okButtonValue: "Yes", 
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
}

/*=======================================================================================
 * This function show confirmation dialog for actions.
 * 
 * @param <int> or <String> width - width of pop-up (required);
 * @param <int> or <String> height - height of pop-up (required);
 * @param <String> message - message that will be shown in pop-up (required);
 * @param <javascript object> form - form that will be submited if user select 'Yes' (optional);
 * @param <javascript object> submit - button that will be clicked if user select 'Yes' (optional);
 * @param <javascript object> disable - button or input that will be disabled if user select 'Yes' (optional);
 * @param <jquery object or objects> disableSet - set of buttons or inputs that will be disabled if user select 'Yes' (optional);
 * @param <String> submit - page to redirect if user will select 'Yes' (optional);
 * @param <boolean> goBack - if user will select 'Yes' history.go(-1) action will be performed (optional);
 * @return <boolean> false;
 =======================================================================================*/
function confirmSubmit(params){
	
	$("<div id='confirmDialog' title='Confirm Action'>" +
        "<div style='clear: both; margin-top: 2%; text-align: justify'>" +
        params.message +
        "</div></div>").appendTo("body");
	
    $("#confirmDialog").dialog({
        autoOpen : true,
        closeOnEscape: false,
        modal : true,
        height: params.height,
        width: params.width,
        buttons:{ 
        	'Yes': function() {
            	$("#confirmDialog").remove();
            	if(params.form) {
            		params.form.submit();
            	} else if(params.submit) {
            		params.submit.setAttribute("onClick", "");
            		params.submit.setAttribute("onclick", "");
            		params.submit.click();
            	} else if(params.pageName) {
            		window.location = params.pageName;
            	} else if(params.goBack) {
            		history.go(-1);
            	}

				if(params.disable){
					params.disable.style.color = "#e6e6e6";
					params.disable.disabled = true;
				}
				if(params.disableSet){
					params.disableSet.css("color","#e6e6e6");
					params.disableSet.attr("disabled","true");
				}
            		
        	},
            'No': function() { 
            	$("#confirmDialog").remove();
            }
        },

        open: function(event, ui) {

            openDialog({ 
            	dialogDiv: this, 
            	cancelButtonValue: "No", 
            	okButtonValue: "Yes", 
            	imagesFolderPath: determineImagesPath()
    		});
        }
    });
    return false;
}

function determineImagesPath() {
	
	var slashesInPath = window.location.pathname.split('/').length - 1;
	var imagesPath = "images/";
	
	for(var i = 2; i < slashesInPath; i++ ) {
		imagesPath = "../" + imagesPath;
	}		
	
	return imagesPath;
}

function checkFileUpload(fileUploadId, message){
    var fileUpload = document.getElementById(fileUploadId);
    //Does the user browse or select a file or not
    if (fileUpload.value =='' ){
    	alertDialog({ message: message, height: 150, width: 400 });
        return false;
    }
    return true;
}

function hideUnhideStudyParamRow(element) {
	var rowClass = $(element).attr('data-row-class');
	if ($(element).attr('data-cc-action') == 'show') {
		$("tr." + rowClass).show();
	} else {
		$("tr." + rowClass).hide();
	}
}

function setAccessedObjected(element) {
	
	if(isBrowserIE7OrLower()) {
		return;
	}
	
	var attrName = $("#accessAttributeName").val();
	var tr = getElementRow(element);
	var dataElement = $(tr).find("a["+attrName+"]");
	var idValue = $(dataElement).attr(attrName);
	var newHtml = "";
	setIdByAttributeName(attrName, idValue);
	clearHighlight();
	$(tr).find('td').each(function(){
		if($(this).is(":visible")) {
			$(this).addClass("borderHighlight");
		}		
	});	
	
	//Remove inherited border highlight from cell contents
	if($.browser.msie){
		removeInheritedHighlightForIE(tr[0]);
	} else {
		removeInheritedHighlight(tr);
	}
}

function getElementRow(element) {
	var tr = $(element).closest("tr");
	while ($(tr).hasClass("innerTable")) { 
	 	var table = $(tr).closest("table"); 
	 	tr = $(table).closest("tr"); 
	} 
	return tr;
}

function removeInheritedHighlight(tr) {
	
	$(tr).find('td').each(function() {
		removeHighlightFromCellDescendants(this);
	});
}

function removeInheritedHighlightForIE(row){
	
	for (var i = 0; i < row.cells.length; i++) {
		removeHighlightFromCellDescendants(row.cells[i]);
    }
}

function clearHighlight(withRowSpan) {
	
	$("td.borderHighlight").each(function(){
		$(this).removeClass("borderHighlight");
	});
	if(withRowSpan) {
		$("td.borderHighlightTop").each(function(){
			$(this).removeClass("borderHighlightTop");
		});
		$("td.borderHighlightBottom").each(function(){
			$(this).removeClass("borderHighlightBottom");
		});
	}
}

function highlightLastAccessedObject(rowHighlightType) {
	
	var attrName = $("#accessAttributeName").val();
	if(attrName){
		
		var dataElement = $("a[" + attrName + "='" + getIdByAttributeName(attrName) + "']");
		if(dataElement && $(dataElement).is("a")) {
			if(!rowHighlightType) {
				setAccessedObjected(dataElement);
			} else {
				if (rowHighlightType == rowHighlightTypes.ROWSPAN) {
					setAccessedObjectWithRowspans(dataElement);
				} else if (rowHighlightType == rowHighlightTypes.MULTIPLE) {
					setAccessedObjectWithMultipleRows(dataElement);
				}
			}			
		}
	}	
}

function getLastAccessedId(){
	
	var attrName = $("#accessAttributeName").val();
	return getIdByAttributeName(attrName);
}

function setLastAccessedId(id){
	
	var attrName = $("#accessAttributeName").val();
	setIdByAttributeName(attrName, id);
}

function getIdByAttributeName(attrName) {
	if(!isBrowserIE7OrLower()){
		return localStorage[attrName];
	}
}

function setIdByAttributeName(attrName, idValue) {
	if(!isBrowserIE7OrLower()){
		localStorage[attrName] = idValue;
	}
}

function clearLastAccessedObjects(){
	localStorage.removeItem("data-cc-subjectMatrixId");
	localStorage.removeItem("data-cc-ndId");
	localStorage.removeItem("data-cc-mcItemId");
	localStorage.removeItem("data-cc-studyAuditLogId");	
	localStorage.removeItem("data-cc-sdvCrfId");
	localStorage.removeItem("data-cc-sdvStudySubjectId");
	localStorage.removeItem("data-cc-ruleId");
    localStorage.removeItem("data-cc-groupId");
    localStorage.removeItem("data-cc-userId");
    localStorage.removeItem("data-cc-subjectId");
    localStorage.removeItem("data-cc-siteId");
    localStorage.removeItem("data-cc-studyId");
    localStorage.removeItem("data-cc-crfId");
    localStorage.removeItem("data-cc-exportJobId");
    localStorage.removeItem("data-cc-importJobId");
    localStorage.removeItem("data-cc-runningJobId");
    localStorage.removeItem("data-cc-auditUserId");
    localStorage.removeItem("data-cc-datasetId");
    localStorage.removeItem("data-cc-userInStudyId");
    localStorage.removeItem("data-cc-subjectStudyEventId");
    localStorage.removeItem("data-cc-eventDefinitionId");
    localStorage.removeItem("data-cc-eventDefinitionCrfId");
    localStorage.removeItem("data-cc-eventDefinitionReadonlyCrfId");
    localStorage.removeItem("data-cc-crfEvaluationId");
}

function removeHighlightFromCellDescendants(td){
	
	$(td).find(".borderHighlight").each(function(){	
		$(this).removeClass("borderHighlight");
	});
}

function isBrowserIE7OrLower(){
	if($.browser.msie  && parseInt($.browser.version, 10) < 8) {
		return true;
	}
	return false;
}

function setAccessedObjectWithRowspans(element) {
	if(isBrowserIE7OrLower()) {
		return;
	}
	//Get attribute name e.g. data-cc-crfId
	var attrName = $("#accessAttributeName").val();
	//Get row with element containing the attribute
	var tr = getElementRow(element);
	//Get the element containing the attribute
	var dataElement = $(tr).find("a["+attrName+"]");
	//Get attribute value
	var idValue = $(dataElement).attr(attrName);
	//Refine attribute value accordingly
	if(idValue.indexOf("_") > -1) {
		idValue = idValue.substring(0, idValue.indexOf("_"));
		//Working backwards: Get dataElement with attribute value idValue
		dataElement = $("a["+attrName+"='"+idValue+"']");
		//Get row containing datElement;
		tr = getElementRow(dataElement);
	}
	
	//Clear highlight with rowspan
	clearHighlight(true);
	setAccessedObjected(dataElement);
	//Refine highlights to cater for rowspans
	refineHighlightForRowspans(element, tr);
}

function refineHighlightForRowspans(element, row) {
	var rowspan = 1;
	//Highligh only top border for single rowspan cells and set maximum rowspan value
	$(row).find('td.borderHighlight').each(function() {
		if (parseInt($(this).attr("rowspan")) > rowspan) {
			rowspan = parseInt($(this).attr("rowspan"));
		}
		if($(this).is(":visible") && parseInt($(this).attr("rowspan")) == 1) {
			$(this).removeClass("borderHighlight");
			$(this).addClass("borderHighlightTop");
		}		
	});	
	
	//Get the last row spanned and highlight the bottom border of the cells
	var lastRow = getLastRowSpanned(row, rowspan);
	$(lastRow).children('td').each(function() {
		if($(this).is(":visible")) {
			$(this).addClass("borderHighlightBottom");
		}		
	});	
}

function getLastRowSpanned(firstRow, rowspan) {
	var nextRow = $(firstRow).next("tr");
	for(var i=2; i<rowspan; i++) {
		nextRow = $(nextRow).next("tr");
	}
	return nextRow;
}

function setAccessedObjectWithMultipleRows(element) {
	if(isBrowserIE7OrLower()) {
		return;
	}
	var rowCountAttr = "data-cc-rowCount";
	//Get attribute name e.g. data-cc-crfId
	var attrName = $("#accessAttributeName").val();
	//Get row with element containing the attribute
	var tr = getElementRow(element);
	//Get the element containing the attribute
	var dataElement = $(tr).find("a["+attrName+"]");
	//Get attribute value
	var idValue = $(dataElement).attr(attrName);
	//Refine attribute value accordingly
	if(idValue.indexOf("_") > -1) {
		idValue = idValue.substring(0, idValue.indexOf("_"));
		//Working backwards: Get dataElement with attribute value idValue
		dataElement = $("a["+attrName+"='"+idValue+"']");
		//Get row containing datElement;
		tr = getElementRow(dataElement);
	}	
	//Clear highlight with rowspan/multiple rows
	clearHighlight(true);
	
	//Get number of rows
	var rowNum = parseInt($(dataElement).attr(rowCountAttr));
	//Get last row in group
	var lastRow = getLastRowSpanned(tr, ++rowNum);
	//Highlight first and last rows
	highlightRow(tr, "borderHighlightTop");
	highlightRow(lastRow, "borderHighlightBottom");
	//Persist last accessed object
	setIdByAttributeName(attrName, idValue);
}

function highlightRow(row, cssClass) {
	$(row).children('td').each(function() {
		if($(this).is(":visible")) {
			$(this).addClass(cssClass);
		}		
	});	
}

function changeDefinitionOrdinal(params) {
    jQuery.ajax({
        url: params.context + '/pages/' + params.servlet,
        type: 'POST',
        data: { current: params.current, previous: params.previous },
        success: function () {
            location.reload();
        }
    });
}

//-------------------------------------------------------------------------
// Function: openDocWindow
//
// Pops up a new browser window containing a document, such as the
// PRS Reference Guide.
//-------------------------------------------------------------------------
function openDocWindow(inURL) {

    openNewWindow(inURL, '',
        'directories=no,location=no,menubar=yes,scrollbars=yes,toolbar=no,status=no,resizable=yes', 'medium');
}


//-------------------------------------------------------------------------
// Function: openPrintCRFWindow
//
//-------------------------------------------------------------------------
function openPrintCRFWindow(inURL) {
    inURL = encodeURI(inURL);
    openNewWindow(inURL, '',
        'directories=no,location=yes,menubar=yes,scrollbars=yes,toolbar=yes,status=yes,resizable=yes',
        'print');
}


function processPrintCRFRequest(url) {
    url = encodeURI(url);
    openPrintCRFWindow(url);
}


function checkOrUncheckAllByClass(className, check) {
	var selector = "input[type=checkbox][class=" + className + "]";	
	$(selector).each(function() {		
		this.checked = check;
	});
}

function focusNextIfExceedMaxLength(element, maxLength, value, event) {
    if (value.length >= maxLength) {
        var tabIndex = parseInt($(element).attr("tabindex"));
        var allElements = $("[tabindex=" + tabIndex + "]").not("[name*=]input]");
        var index = allElements.index($(element));
        $(element).trigger("change");
        $(element).trigger("blur");
        if (allElements.length > 1 && index < allElements.length - 1) {
            $(allElements.get(index + 1)).focus();
        } else {
            var nextElement = $("[tabindex=" + (tabIndex + 1) + "]:first");
            if (nextElement.length != 0) {
                nextElement.focus();
            } else {
                $("[tabindex]:visible:first").focus();
            }
        }
    }
}

function checkMaxLength(element, event) {
    try {
        var ev = event || window.event;
        var keyCode = String.fromCharCode(ev.which || ev.keyCode);
        if (keyCode.match(/\w/) || keyCode.match(/\d/)) {
            var type = $(element).attr("type");
            var tagName = $(element).get(0).nodeName;
            var dataType = $(element).attr("datatype");
            var maxLength = $(element).attr("maxlength");
            if (maxLength != "") {
                if (tagName.toLowerCase() == "input" && type.toLowerCase() == "text") {
                    focusNextIfExceedMaxLength(element, parseInt(maxLength), $(element).val(),event);
                } else if (tagName.toLowerCase() == "textarea") {
                    focusNextIfExceedMaxLength(element, parseInt(maxLength), $(element).text(), event);
                }
            }
        }
    } catch (e) {}
}

function initAutotabbing() {
    $(document).keydown(function(event) {
        var ev = event || window.event;
        var element = ev.target || ev.srcElement;
        if (element != undefined && $(element).attr("autotabbing") != undefined) {
            autotabbingCurrentElementName = $(element).attr("name");
        }
    });
    $(document).keyup(function(event) {
        var ev = event || window.event;
        var element = ev.target || ev.srcElement;
        if (element != undefined && $(element).attr("autotabbing") != undefined && autotabbingCurrentElementName == $(element).attr("name")) {
            checkMaxLength(element, event);
        }
    });
}
