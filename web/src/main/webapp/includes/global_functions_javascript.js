var currentPopupUid;
var subjectMatrixPopupStick;
var popupInterval;
var firstFormState;
var dnShortcutAnchors = ["firstNewDn", "firstUpdatedDn", "firstResolutionProposed", "firstClosedDn", "firstAnnotation"];
var dnShortcutLinks = ["dnShortcutTotalNew", "dnShortcutTotalUpdated", "dnShortcutTotalResolutionProposed", "dnShortcutTotalClosed", "dnShortcutTotalAnnotations"];
var dnFlagImages = ["icon_Note.gif", "icon_flagYellow.gif", "icon_flagBlack.gif", "icon_flagGreen.gif", "icon_flagWhite.gif"];

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

function checkGoBackEntryStatus(strImageName, Message) {
    closing = false;        
    objImage = MM_findObj(strImageName);

    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        return confirmBack(Message);
    } else {
      history.go(-1);
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
function confirmGoTo(Message, Adress){
    var confirm1 = confirm(Message);
    if(confirm1){
    	window.location.href=(Adress);
    }
}
function confirmBack(Message){
    var confirm1 = confirm(Message);
    if(confirm1){
       history.go(-1);
    }
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
    //alert(objImage.src);
    if (objImage != null && objImage.src.indexOf('images/icon_UnsavedData.gif')>0) {
        return confirmBackSmart(Message, servletURL, defaultURL);
    } else {
        goBackSmart(servletURL, defaultURL);
    }
    return true;
}

function confirmBackSmart(Message, servletURL, defaultURL){
    var confirm1 = confirm(Message);
    if(confirm1){
        goBackSmart(servletURL, defaultURL);
    }
}

function datasetConfirmBack(message, formId, address, action) {
  var newFormState = $("#" + formId).serialize();
  var confirm1 = $("#" + formId).length != 0
      && newFormState != firstFormState ? confirm(message) : true;
  if (confirm1) {
    $("#" + formId)[0].setAttribute("action", address);
    $("#" + formId + " input[name=action]").val(action);
    $("#" + formId + " input[id=btnSubmit]")[0].setAttribute("onclick", "");
    $("#" + formId + " input[id=btnSubmit]").click();
  }
}

function formWithStateGoBackSmart(message, servletURL, defaultURL) {
  var newFormState = $("#formWithStateFlag").parent("form").serialize();
  var confirm1 = newFormState != firstFormState || $("#formWithStateFlag").val().toLowerCase() == "changed" ? confirm(message) : true;
  if (confirm1) {
    goBackSmart(servletURL, defaultURL);
  }
  return true;
}

function formWithStateCancelSmart(message, servletURL, defaultURL) {
  var newFormState = $("#formWithStateFlag").parent("form").serialize();
  var confirm1 = newFormState != firstFormState || $("#formWithStateFlag").val().toLowerCase() == "changed" ? confirm(message) : true;
  if (confirm1) {
    goBackSmart(servletURL, defaultURL);
  }
  return true;
}

function formWithStateConfirmGoTo(message, address) {
  var newFormState = $("#formWithStateFlag").parent("form").serialize();
  var confirm1 = newFormState != firstFormState || $("#formWithStateFlag").val().toLowerCase() == "changed" ? confirm(message) : true;
  if (confirm1) {
    window.location.href = address;
  }
  return true;
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
    
    var theadRows = thead.getElementsByTagName('tr')

	
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
    }
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
}

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
}

var markCRFCompleteOk = function(checkboxObjectName) {
    $("#confirmation").dialog("close");
    var checkboxObjects = document.getElementsByName(checkboxObjectName);
    if(checkboxObjects[0]){
        checkboxObjects[0].checked=true;
    }
    if(checkboxObjects[1]){
        checkboxObjects[1].checked=true;
    }
}

var markCRFCompleteCancel = function(checkboxObjectName) {
    $("#confirmation").dialog("close");
    var checkboxObjects = document.getElementsByName(checkboxObjectName);
    if(checkboxObjects[0]){
        checkboxObjects[0].checked=false;
    }
    if(checkboxObjects[1]){
        checkboxObjects[1].checked=false;
    }
}

var shouldShowDialog = function() {
    var result = true;
    var value = getCookie("ignoreMarkCRFCompleteMSG");
    if (value == "yes") {
        result = false;
    }
    return result;
}

/* Only display the confirm dialogue box if the checkbox was checked
 when the user clicked it; then uncheck the checkbox if the user chooses "cancel"
 in the confirm dialogue. */
function displayMessageFromCheckbox(checkboxObject, dde){
    if(checkboxObject != null && checkboxObject.checked){

        if ($("#confirmation").length == 0) {
            $("body").append(
                "<div id=\"confirmation\" style=\"display: none;\" title=\"Mark CRF Complete\">" +
                    (dde == undefined ? "<div style=\"clear: both; text-align: justify;\">Marking this CRF complete will finalize data entry. You will be allowed to edit the data later but this data entry stage is completed. If Double Data Entry is required, you or another user may need to complete this CRF again before it is verified as complete. Are you sure you want to mark this CRF complete?</div>"
                                      : "<div style=\"clear: both; text-align: justify;\">Marking this CRF complete will prepare it for Double Data Entry, where another user will enter data and then be able to finally complete this CRF.</div>") +
                    "<div style=\"clear: both; padding: 6px;\"><input type=\"checkbox\" id=\"ignoreMarkCRFCompleteMSG\"/> Do not show this message anymore.</div>" +
                    "<div style=\"clear: both;\">" +
                        "<input type=\"button\" value=\"OK\" class=\"button_medium\" onclick=\"markCRFCompleteOk('" + checkboxObject.name + "');\" style=\"float: left;\">" +
                        "<input type=\"button\" value=\"Cancel\" class=\"button_medium\" onclick=\"markCRFCompleteCancel('" + checkboxObject.name + "');\" style=\"float: left; margin-left: 6px;\">" +
                    "</div>" +
                "</div>");

            $("#confirmation").dialog({
                autoOpen : false,
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

	
	var color = $('*').find('a').css('color');
	if (color == 'rgb(170, 98, 198)' || color == '#AA62C6' || color == '#aa62c6') {
		$('input.button_medium').css('background-image', 'url(images/violet/button_medium_BG.gif)');
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#AA62C6');
	}
	if (color == 'rgb(117, 184, 148)' || color == '#75b894' || color == '#75B894') {
		$('input.button_medium').css('background-image', 'url(images/green/button_medium_BG.gif)');
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#75b894');
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
var updatePeriods = new Array("month","date","weekday","hour","request")

// Invoked to display rotated HTML content in a Web page. The period
// argument should be an element of the updatePeriods array.

function displayRotatedContent(period) {
    var updatePeriod = -1
    for(var i=0;i<content.length;++i) {
        if(period.toLowerCase() == updatePeriods[i].toLowerCase()) {
            updatePeriod = i
            break
        }
    }
    var s = selectHTML(updatePeriod)
    document.write(s)
}

function selectHTML(updatePeriod) {
    var n = 0
    var max = content.length
    var d = new Date()
    switch(updatePeriod) {
        case 0: // Month (0 - 11)
            n = d.getMonth()
            break
        case 1: // Date (1 - 31 scaled to 0 - 30)
            n = d.getDate() - 1
            break
        case 2: // Weekday (0 - 6)
            n = d.getDay()
            break
        case 3: // Hour (0 - 23)
            n = d.getHours()
            break
        case 4: // Request (Default)
        default:
            n = selectRandom(max)
    }
    n %= max
    return content[n]
}

// Select a random integer that is between 0 (inclusive) and max (exclusive)
function selectRandom(max) {
    var r = Math.random()
    r *= max
    r = parseInt(r)
    if(isNaN(r)) r = 0
    else r %= max
    return r
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
        alert("You are only allowed to choose up to a maximum of fifty (50) variables.  You have picked "+number+".  Please go back to the form and remove some of your selections.  For Data Dumps of more than 50 variables, please contact your Project Administrator or DBA.");
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

//-------------------------------------------------------------------------
// Function: setImageInParentWin
//
// Sets/changes the source file of an image in a parent window
//     Must be called from within a popup window that was opened by the parent window
//-------------------------------------------------------------------------

function setImageInParentWin(strParentWinImageName,strParentWinImageFullPath, resolutionStatusId) {
    var objImage;
    if (window.opener && !window.opener.closed) {
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

function showPopupForEvents(obj, e) {
  if (!subjectMatrixPopupStick) {
    clearInterval(popupInterval);
    var coords = analyzeEvent(e);
    popupInterval = setInterval(function() {
      currentPopupUid = obj;
      processPopupForEvents(coords.top, coords.left);
    }, 1000);
  }
}

function justShowPopupForEvents(obj, e) {
  clearInterval(popupInterval);
  var coords = analyzeEvent(e);
  setTimeout(function() {
    currentPopupUid = obj;
    processPopupForEvents(coords.top, coords.left);
  }, 200);
}

function closePopupForEvents() {
  clearInterval(popupInterval);
  subjectMatrixPopupStick = undefined;
}

var popupForEventsMouseDown = function(ev) {
  var element =  ev.target || ev.srcElement;
  if (ev.target.nodeName.toLowerCase() != "html") {
    if (currentPopupUid != undefined && jQuery(ev.target).closest("#" + currentPopupUid).length == 0) {
      jQuery(".calendar").remove();
      jQuery("#" + currentPopupUid).css("visibility", "hidden");
      currentPopupUid = undefined;
      subjectMatrixPopupStick = undefined;
      jQuery("*").unbind("mousedown", popupForEventsMouseDown);
    } else {
      subjectMatrixPopupStick = true;
    }
  }
}

function processPopupForEvents(top, left) {
  $("div[id^=Lock_]").css("display", "none");
  $("div[id^=Event_]").css("display", "none");
  $("div[id^=S_Lock_]").css("display", "none");
  $("div[id^=S_Event_]").css("display", "none");
  $("#" + currentPopupUid + " tr[id^=Menu_off_]").css("display", "none");
  $("#" + currentPopupUid + " tr[id^=Menu_on_]").css("display", "");
  $("#" + currentPopupUid + " tr[id^=S_Menu_off_]").css("display", "none");
  $("#" + currentPopupUid + " tr[id^=S_Menu_on_]").css("display", "");
  var objLayer = $("#" + currentPopupUid);
  if (objLayer.length > 0) {
    objLayer.css("display", "");
    objLayer.css("visibility", "visible");
    objLayer.css("z-index", "9999");
    objLayer.css("left", left);
    objLayer.css("top", top);
    jQuery("*").unbind("mousedown", popupForEventsMouseDown).bind("mousedown", popupForEventsMouseDown);
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
    if (tempX < 0){tempX = 0}
    if (tempY < 0){tempY = 0}

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

    if (tempX < 0){tempX = 0}
    if (tempY < 0){tempY = 0}

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
    if (tempX < 0){tempX = 0}
    if (tempY < 0){tempY = 0}

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
        alert('Your browser is not IE 5 or higher, or Firefox or Safari or Opera');
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
            checkboxObject.checked=false; alert('Your password did not match. Please try again.');
        }
    }
}

function requestSignatureFromCheckbox(password, checkbox){
	checkboxObject = checkbox;
	if (password==null || password==''){
		alert('Your password did not match. Please try again.');
		checkbox.checked=false;
		return;
	}
	if(checkbox != null && checkbox.checked){
		sendRequest("GET", "MatchPassword?password=" + password);
	}
}

function numberGroupRows(){
	alert("test");
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
		if(parentobj==document.getElementsByTagName('body')[0]){break}
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

var hideCurrentPopup = function(ev) {
    var element =  ev.target || ev.srcElement;
    if (ev.target.nodeName.toLowerCase() != "html") {
        if (currentPopupUid != undefined && jQuery(ev.target).closest("#Event_" + currentPopupUid).length == 0) {
            jQuery(".calendar").remove();
            jQuery("#Event_" + currentPopupUid).css("visibility", "hidden");
            jQuery("#" + currentPopupUid).html("");
            jQuery("#" + currentPopupUid).html("");
            currentPopupUid = undefined;
            subjectMatrixPopupStick = undefined;
            jQuery("*").unbind("mousedown", hideCurrentPopup);
        } else {
            subjectMatrixPopupStick = true;
        }
    }
}

function getSchedulePage(statusBoxId) {
    var element = jQuery('#eventScheduleWrapper_' + statusBoxId);
    if (element.length == 1) {
        jQuery('#eventScheduleWrapper_' + statusBoxId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
        jQuery('#eventScheduleWrapper_' + statusBoxId).css("height", "18px");
        jQuery.ajax({
            url: element.attr("rel"),
            type: "GET",
            data: {},
            cache: false,
            success: function (data) {
                jQuery(".calendar").remove();
                jQuery('#eventScheduleWrapper_' + statusBoxId).css("height", "auto");
                jQuery('#eventScheduleWrapper_' + statusBoxId).html(data);
                jQuery("img[class='showCalendar']").each(function() {
                    var jsCode = jQuery(this).attr("rel");
                    eval(jsCode);
                });
                jQuery("#addSubjectForm").remove();
                jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
            }
        });
    }
}

function adjustCrfListTable(studyEventId) {
  var wereRemoved = 0;
  var popupTotalColumns = 8;

  var hideCol = function(num) {
    $(".crfListTable tr").find("td:last").find("img:eq(" + (num - wereRemoved) + ")").remove();
    popupTotalColumns--;
    wereRemoved++;
  }

  if ($("#crfListWrapper_" + studyEventId + " #hideCol1").val() == 'true') hideCol(0);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol2").val() == 'true') hideCol(1);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol3").val() == 'true') hideCol(2);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol4").val() == 'true') hideCol(3);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol5").val() == 'true') hideCol(4);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol6").val() == 'true') hideCol(5);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol7").val() == 'true') hideCol(6);
  if ($("#crfListWrapper_" + studyEventId + " #hideCol8").val() == 'true') hideCol(7);

  $("#popupTotalColumns").val(popupTotalColumns);

  var crfActionIconWidth = 33;
  var crfActionsMaxIconsCount = parseInt($("#popupTotalColumns").val());
  crfActionsMaxIconsCount = (crfActionsMaxIconsCount < 2 ? 2 : crfActionsMaxIconsCount);
  jQuery('#crfListWrapper_' + studyEventId + ' .crfListTableActions').attr("style", "width: " + (crfActionIconWidth * crfActionsMaxIconsCount) + "px;");

  $(".crfListTable tr").find("td:first").each(function() {
    $(this).attr("style", "border-left : none !important;" + $(this).attr("style"));
  });
}

function getCRFList(studyEventId) {
    jQuery('#crfListWrapper_' + studyEventId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
    jQuery('#crfListWrapper_' + studyEventId).css("height", "18px");
    jQuery.ajax({
        url: "CRFListForStudyEvent",
        type: "GET",
        data: {eventId: studyEventId},
        cache: false,
        success: function (data) {
            jQuery('#crfListWrapper_' + studyEventId).css("height", "auto");
            jQuery('#crfListWrapper_' + studyEventId).html(data);
            jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
            adjustCrfListTable(studyEventId);
        }
    });
}

function closePopup() {
    clearInterval(popupInterval);
    subjectMatrixPopupStick = undefined;
}

function canShowPopup(studyEventId) {
    return !subjectMatrixPopupStick;
}

function showPopup(studyEventId, statusBoxId, statusBoxNum, event) {
    clearInterval(popupInterval);
    var coords = analyzeEvent(event);
    popupInterval = setInterval(function() {
        clearInterval(popupInterval);
        hideAllTooltips(studyEventId, statusBoxId, statusBoxNum, coords.top, coords.left);
    }, 1000);
}

function justShowPopup(studyEventId, statusBoxId, statusBoxNum, event) {
    clearInterval(popupInterval);
    var coords = analyzeEvent(event);
    setTimeout(function() {
        hideAllTooltips(studyEventId, statusBoxId, statusBoxNum, coords.top, coords.left);
    }, 200);
}

function getScheduledEventContent(studyEventId, statusBoxId, statusBoxNum, top, left, localDivId, localDivRel) {
    jQuery('#eventScheduleWrapper_' + statusBoxId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
    jQuery('#eventScheduleWrapper_' + statusBoxId).css("height", "18px");
    jQuery.ajax({
        url: "CreateNewStudyEvent",
        type: "GET",
        data: {returnScheduledEvenContent: localDivId, popupQueryStudySubjectId: localDivRel},
        cache: false,
        success: function (data) {
            var localDiv = jQuery("#" + localDivId);
            localDiv.html(data);
            //IE8 bug
            if (localDiv.html() == "") {
                localDiv.get(0).innerHTML = data;
            }
            if (localDiv[0].repeatingEvent && parseInt(localDiv[0].totalEvents) > 1) {
                localDiv.attr("style", localDiv.attr("style").replace("width: 608px;", "width: 658px;"));
            }
            hideAllTooltips(studyEventId, statusBoxId, statusBoxNum, top, left);
        }
    });
}

function hideAllTooltips(studyEventId, statusBoxId, statusBoxNum, top, left) {
    try {
        jQuery(".calendar").remove();
        jQuery("table#findSubjects div[id^='Event_']").css("visibility", "hidden");
        jQuery("table#findSubjects div[id^='crfListWrapper_']").html("");
        jQuery("table#findSubjects div[id^='eventScheduleWrapper_']").html("");

        var objHolder = document.getElementById("Event_" + statusBoxId);
        if (objHolder == undefined || top == undefined || left == undefined) return;
        objHolder.style.top  = top;
        objHolder.style.left = left;
        objHolder.style.display = 'block';
        objHolder.style.visibility = 'visible';

        jQuery('#crfListWrapper_' + studyEventId).html("<div align=\"center\"><img src=\"images/ajax-loader-blue.gif\"/></div>");
        jQuery('#crfListWrapper_' + studyEventId).css("height", "18px");

        if (statusBoxId != undefined) {
            currentPopupUid = statusBoxId;

            var eventDiv = jQuery("#Event_" + statusBoxId);
            if (eventDiv.length > 0) {
                if (eventDiv[0].repeatingEvent && parseInt(eventDiv[0].totalEvents) > 1) {
                    statusBoxNum = eventDiv[0].totalEvents;
                }
                if (eventDiv[0].getScheduledContent) {
                    eventDiv[0].getScheduledContent = undefined;
                    getScheduledEventContent(studyEventId, statusBoxId, statusBoxNum, top, left, eventDiv.attr("id"), eventDiv.attr("rel"));
                    return;
                }
            }

            if (statusBoxNum != undefined) {
                EnableScrollArrows2(statusBoxId, statusBoxNum);
                jQuery("*").unbind("mousedown", hideCurrentPopup).bind("mousedown", hideCurrentPopup);
            } else {
                if (document.getElementById('Menu_on_' + statusBoxId) != undefined) document.getElementById('Menu_on_' + statusBoxId).style.display = "";
                if (document.getElementById('Menu_off_' + statusBoxId) != undefined) document.getElementById('Menu_off_' + statusBoxId).style.display = "none";
                if (studyEventId != '') {
                    getCRFList(studyEventId);
                } else {
                    getSchedulePage(statusBoxId);
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

function createNewEvent(event) {
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
            params['popupQueryStudySubjectId'] = jQuery(element).closest("div[id^='Event_']").attr("rel");
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
                                    var aElement = eventDiv.next();
                                    var eventId = eventDef.eventIds[0];

                                    eventDiv[0].totalEvents = eventDef.totalEvents;
                                    eventDiv[0].repeatingEvent = eventDef.repeatingEvent;
                                    eventDiv[0].getScheduledContent = true;

                                    var newMOFuncBody = jQuery.trim(aElement.attr("onmouseover").toString().match(/{((?:.|\n)+)}/)[1]).toString();
                                    newMOFuncBody = newMOFuncBody.replace(/canShowPopup\(''\)/g, "canShowPopup('" + eventId + "')").replace(/canShowPopup\(\"\"\)/g, "canShowPopup(\"" + eventId + "\")");
                                    newMOFuncBody = newMOFuncBody.replace(/showPopup\('',/g, "showPopup('" + eventId + "',").replace(/showPopup\(\"\",/g, "showPopup(\"" + eventId + "\",");
                                    aElement.attr("onmouseover", "");
                                    var confEvent = function(element, funcBody) {
                                        element.unbind("mouseover").bind("mouseover", function(event){
                                            eval(funcBody);
                                        });
                                    }
                                    confEvent(aElement, newMOFuncBody);

                                    var newCLFuncBody = jQuery.trim(aElement.attr("onclick").toString().match(/{((?:.|\n)+)}/)[1]).toString();
                                    newCLFuncBody = newCLFuncBody.replace(/justShowPopup\('',/g, "justShowPopup('" + eventId + "',").replace(/justShowPopup\(\"\",/g, "justShowPopup(\"" + eventId + "\",");
                                    aElement.attr("onclick", "");
                                    var confEvent = function(element, funcBody) {
                                        element.unbind("click").bind("click", function(event){
                                            eval(funcBody);
                                        });
                                    }
                                    confEvent(aElement, newCLFuncBody);

                                    if (aElement.find("img").attr("src").indexOf("icon_NotStarted.gif") > 0) {
                                        aElement.find("img").attr("src", "images/icon_Scheduled.gif");
                                        aElement.find("img").attr("style", "position: relative;");
                                    }
                                    eventDiv.css("visibility", "hidden");
                                    if (eventDef.repeatingEvent && parseInt(eventDef.totalEvents) > 1) {
                                        aElement.html(aElement.find("img")[0].outerHTML + "<span class=\"re_indicator\">x" + eventDef.totalEvents + "</span>");
                                    }
                                }
                            }
                        }
                        gfRemoveOverlay();
                        if (result.pageMessages.length > 0) {
                            jQuery("#sidebar_Alerts_open").css("sidebar_Alerts_closed", "none");
                            jQuery("#sidebar_Alerts_open").css("display", "all");
                            jQuery("#sidebar_Alerts_open div.sidebar_tab_content").html("");
                            jQuery("#sidebar_Alerts_open div.sidebar_tab_content").get(0).innerHTML = "";
                            for (pageMessage in result.pageMessages) {
                                jQuery("#sidebar_Alerts_open div.sidebar_tab_content").append("<i><div class=\"alert\">" + pageMessage + "<br><br></div></i>");
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
}

var radioButtonOnMouseUp = function(event) {
    crfRadioButtonChecked = false;
    var ev = event || window.event;
    var element =  ev.target || ev.srcElement;
    if (element != undefined && element.checked) {
        crfRadioButtonChecked = true;
    }
}

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
}

/* =======================================================================
 * Disable the Randomization result field and the randomization date field
 * They should be read-only
 ========================================================================= */
$(function() {
	
	var dateInput = $("#Rand_Date").find(":input")
	var resultInput = $("#Rand_Result").find(":input")

	$(dateInput).attr("readonly", "readonly");
	$(resultInput).attr("readonly", "readonly");
})

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

      disableRandomizeCRFButtons(true)

    var crf = $("input:hidden[name='crfId']").val();
    
    var eligibility = null;

    // Check if the subject eligibility is defined
    if($("#Rand_Eligibility :radio").size() > 0) {

        // Check if selection has been done
        if($("input[type=radio]:checked", "#Rand_Eligibility").length > 0) {
            
            eligibility = $("input[type=radio]:checked", "#Rand_Eligibility").val();

        } 
    }

   var strataLevels = []
    $.each($("div[id^=Rand_StrataData]").find(":selected"), function(index, element) {

        var strata = {

            // ordering
            StratificationID: index + 1,
            Level: $("input[eleid^='"+ $(this).parents("div").attr("id") +"']").attr($(this).text())
        }

        strataLevels.push(strata)
    })

    var trialId = null

    // Check if the trial Id is defined
    if($("#Rand_TrialIDs :select").size() > 0) {

        // Check if selection has been done
        if($("#Rand_TrialIDs :select").find(":selected") !== undefined) {

            var opt3 = $("#Rand_TrialIDs :select").find(":selected").text();
           
           trialId = $("input:hidden[eleid='requiredParam3']").attr(opt3)

        } else {
            
            alert($("input:hidden[name='requiredParam3Missing']").val());

            return false;
        }
    } else {
        
        trialId = $("input[eleid='randomize']").attr("trialId")
    }
    
    var subject = $("input:hidden[name='subjectLabel']").val()

    $.ajax({

        type:"POST",
        url: "randomize",
        data: {

            crf: crf,
            subject: subject,
            trialId: trialId,
            eligibility: eligibility,
            strataLevel: JSON.stringify(strataLevels)
        },


        success: function(data) {
            
            if(data.match(/UnknownHostException/)) {

                disableRandomizeCRFButtons(false)
                alert("The randomization service is not available. Consult your system administrator")

            } else if(data.match(/Invalid Site/)) {

                disableRandomizeCRFButtons(false)
                alert("The Site Id configured is invalid. Please contact your system administrator")

            } else if(data.match(/Invalid Trial/)) {

                disableRandomizeCRFButtons(false)
                alert("The Trial Id configured is invalid. Please contact your system administrator")

            } else if(data.match(/Invalid Strata/)) {

                disableRandomizeCRFButtons(false)
                alert("The Stratification level missing. Please contact your system administrator")

            } else if(data.match(/^\</)) {

                disableRandomizeCRFButtons(false)
                alert("An error occurred during the randomization call. Please contact your system administrator")

            } else if(data.match(/Site is not auth/)) {

                disableRandomizeCRFButtons(false)
                alert("The Site configured is not authorized to randomize subjects . Please contact your system administrator")

            } else if(data.match(/Exception/)) {

                disableRandomizeCRFButtons(false)
                var exceptionPattern = new RegExp("^.*:(.*)")
                alert(exceptionPattern.exec(data)[1])

            } else {
                
                var result = JSON.parse(data)
                
                var dateInput = $("#Rand_Date").find(":input")
                var resultInput = $("#Rand_Result").find(":input")

                $(dateInput).attr("readonly", "");
                $(resultInput).attr("readonly", "");

                $(dateInput).val(result.date).change();
                $(resultInput).val(result.result).change();

                $(dateInput).attr("readonly", "readonly");
                $(resultInput).attr("readonly", "readonly");

                $("input[type='submit']").removeAttr("disabled")
            }
        }
    })
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
      firstFormState = $("#formWithStateFlag").parent("form").serialize();
    }
});

disableRandomizeCRFButtons = function(flag) {

    if (flag) {

        $("input[type='submit']").attr("disabled", "disabled")
        $("input[eleid='randomize']").attr("disabled", "disabled")

    } else {

        $("input[type='submit']").removeAttr("disabled")
        $("input[eleid='randomize']").removeAttr("disabled")
    }
}

codeItem = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0]
    var study = new RegExp("study=(\\d+)").exec(window.location.href.toString())[1]

    $.ajax({

        type: "POST",
        url: url + "/codeItem",
        data: {

            study: study,
            item: $(item).attr("itemid"),
            verbatimTerm: $.trim($(item).parent().siblings("td").find("input").val()),
            dictionary: $(item).parent().siblings("td").find("select option:selected").val()
        },

        success: function(data) {

            $("#tablepaging").parent().html('');
            $("div[id=" + ($(item).attr("itemid")) + "]").html(data);

            // The item is created in codeItem.jsp
            if ($("#autoCode").size() === 1) {

                updateCodingUX($(item).parents("td").siblings("td").find("div.wrapText").parent());
                $(item).parents('td').find("a[name='deleteTerm'][itemid=" + $(item).attr("itemid").toLowerCase() + "]").css("visibility", "visible");

                $("#autoCode").remove();
            }
        },
        error: function(e) {
            console.log("Error:" + e);
        }
    })
}

saveCodedItem = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0]

    $.ajax({

        type: "POST",
        url: url + "/saveCodedItem",
        data: {

            item: $(item).children('div').attr("id"),
            code: $.trim($(item).children('div').text()),
            dictionary: $(item).parents('tr').find("select option:selected").val()
        },

        success: function(data) {

            updateCodingUX(item)

            console.log("Medical coding executed successfully")
        },
        error: function(e) {
            console.log("Error:" + e);
        }
    })
}

uncodeCodeItem = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0]
    var confirmText = "Uncoding this item will reset the coded item and its status. Do you want to proceed?";

    if(confirm(confirmText)) {
        $.ajax({

            type: "POST",
            url: url + "/uncodeCodedItem",
            data: {

                item: $(item).attr("itemid")
            },

            success: function(data) {

                /* get theme color and choose icon */
                var codeItemButtonSrc = "../images/code_blue.png";
                var color = $('*').find('a').css('color').toLowerCase();
                if (color == 'rgb(170, 98, 198)' || color == '#aa62c6') {
                    codeItemButtonSrc = "../images/violet/code_violet.png";
                }
                if (color == 'rgb(117, 184, 148)' || color == '#75b894') {
                    codeItemButtonSrc = "../images/green/code_green.png";
                }

                /* change completed code icon to available */
                $(item).siblings("a[name='Code'][itemid=" + $(item).attr("itemid") + "]").children('img').attr('src', codeItemButtonSrc)
                /* change code item version */
                var versionNumber = parseInt($.trim($(item).parent('td').siblings("td").find("div[name='codedItemVersion']").text()));
                $(item).parents('td').siblings("td").find("div[name='codedItemVersion']").text(versionNumber + 1);
                /* change status from completed to available */
                $(item).parent().siblings("td").filter(function () {
                    return $(this).text() == 'Completed'; }).text("To be Coded");
                /* change input box value from coded term to verbatim term */
                $(item).parent().siblings("td").find("input:first").val($(item).parent().siblings("td:first").text()).attr("disabled", false);
                /* hide unCode icon */
                $(item).css("visibility", "hidden");
                /* increase 'To be coded' value & decrease 'Coded' value */
                var tdCoded = parseInt($("table.summaryTable tr td[name='tdCoded']").text());
                var tdToBeCoded = parseInt($("table.summaryTable tr td[name='tdToBeCoded']").text());
                $("table.summaryTable tr td[name='tdCoded'] a").text(tdCoded - 1);
                $("table.summaryTable tr td[name='tdToBeCoded'] a").text(tdToBeCoded + 1);
                /*clean up coding resuts*/
                $("#tablepaging").parent().html('');

                console.log("Medical uncoding executed successfully");
            },
            error: function(e) {
                console.log("Error:" + e);
            }
        })
    }
    return false;
}

function codeAndAlias(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0]
    var study = new RegExp("study=(\\d+)").exec(window.location.href.toString())[1]

    $.ajax({

        type: "POST",
        url: url + "/codeAndAlias",
        data: {

            study: study,
            item: $(item).children('div').attr("id"),
            code: $.trim($(item).children('div').text()),
            dictionary: $(item).parents('tr').find("select option:selected").val()
        },

        success: function(data) {

            updateCodingUX(item)
            $("a[name='deleteTerm'][term=" + $(item).parents('tr').find("a[name='deleteTerm'][itemid=" + $(item).children('div').attr("id") + "]").attr("term").toLowerCase() + "]").each(function () {
                $(this).children('img').css("visibility", "visible");
            });

            console.log("Medical coding executed successfully")
        },
        error: function(e) {
            console.log("Error:" + e);
        }
    })
}

function initCrfMoreInfo() {
  if (window.expandCrfInfo != undefined && window.expandCrfInfo == 'true' && $('#CRF_infobox_open').css('display') == 'none') {
    $('#CRF_infobox_closed').css('display', '');
    $('#CRF_infobox_open').css('display', '');
    $('img[id=moreInfoExpandedImg]').css('display', 'none');
    $('img[id=moreInfoCollapsedImg]').css('display', '');
  } else {
    $('#CRF_infobox_open').css('display', 'none');
    $('img[id=moreInfoExpandedImg]').css('display', '');
    $('img[id=moreInfoCollapsedImg]').css('display', 'none');
  }
}

function processCrfMoreInfo() {
  var displayValue = $('#CRF_infobox_open').css('display');
  $('#CRF_infobox_open').css('display', displayValue == 'none' ? '' : 'none');
  if (displayValue == 'none') {
    $('img[id=moreInfoExpandedImg]').css('display', 'none');
    $('img[id=moreInfoCollapsedImg]').css('display', '');
  } else {
    $('img[id=moreInfoExpandedImg]').css('display', '');
    $('img[id=moreInfoCollapsedImg]').css('display', 'none');
  }
}

function Pager(tableName, itemsPerPage) {
    this.tableName = tableName;
    this.itemsPerPage = itemsPerPage;
    this.currentPage = 1;
    this.pages = 0;
    this.inited = false;

    this.showRecords = function (from, to) {

        var rows = document.getElementById(tableName).rows;
        var rowLength = rows.length;
        var tableObj = document.getElementById(tableName);

        for (var i = 0; i < rows.length; i++) {
            if (i < from || i > to)
                rows[i].style.display = 'none';
            else
                rows[i].style.display = '';
        }

    }

    this.showPage = function (pageNumber) {

        if (!this.inited) {
            alert("not inited");
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

    }

    this.prev = function () {

        if (this.currentPage > 1)
            this.showPage(this.currentPage - 1);

    }

    this.next = function () {

        if (this.currentPage < this.pages) {
            this.showPage(this.currentPage + 1);
        }

    }

    this.init = function () {

        var rows = document.getElementById(tableName).rows;
        var records = (rows.length - 1);
        this.pages = Math.ceil(records / itemsPerPage);
        this.inited = true;

    }

    this.showPageNav = function (pagerName, positionId) {

        if (!this.inited) {
            alert("not inited");
            return;
        }

        var element = document.getElementById(positionId);
        var pagerHtml = '<span onclick="' + pagerName + '.prev();" class="pg-normal pointed-span"> « Prev </span> ';

        for (var page = 1; page <= this.pages; page++) {
            pagerHtml += '<span id="pg' + page + '" class="pg-normal pointed-span" onclick="' + pagerName + '.showPage(' + page + ');">' + page + '</span> ';
        }

        pagerHtml += '<span onclick="' + pagerName + '.next();" class="pg-normal pointed-span"> Next »</span>';
        element.innerHTML = pagerHtml;

    }
}

updateCodingUX = function(item) {

    /* update code item version */
    var versionNumber = parseInt($.trim($(item).parents('td').siblings("td").find("div[name='codedItemVersion']").text()));

    $(item).parents('td').siblings("td").find("div[name='codedItemVersion']").text(versionNumber + 1);
    /* update code icon from available to code completed */
    $(item).parents('td').find("a[name='Code'][itemid=" + $(item).children('div').attr("id") + "]").children('img').attr('src', '../images/code_confirm.png');
    /* update status from Available to Completed */
    $(item).parents('td').siblings("td").filter(function() {
        return $(this).text() == 'To be Coded' || $(this).text() == 'Available';
    }).text("Completed");
    /* update coding box from verbatim term to coded term */
    $(item).parents("div[id=" + $(item).children('div').attr("id") + "]").siblings("input").val($.trim($(item).children('div').text())).attr('disabled', true);
    /* display unCode icon */
    $(item).parents('td').find("a[name='unCode'][itemid=" + $(item).children('div').attr("id") + "]").css("visibility", "visible");
    /* increase 'Coded' value & decrease 'To Be Coded' value */
    var tdCoded = parseInt($("table.summaryTable tr td[name='tdCoded']").text());
    var tdToBeCoded = parseInt($("table.summaryTable tr td[name='tdToBeCoded']").text());

    $("table.summaryTable tr td[name='tdCoded'] a").text(tdCoded + 1);
    $("table.summaryTable tr td[name='tdToBeCoded'] a").text(tdToBeCoded - 1);
}

deleteTerm = function(item) {

    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0]

    $.ajax({

        type: "POST",
        url: url + "/deleteTerm",
        data: {

            item: $(item).attr("itemid"),
            code: $.trim($(item).parent().siblings("td").find("input:first").val()),
            dictionary: $(item).parents('tr').find("select option:selected").val()
        },

        success: function(data) {

            $("a[term=" + $(item).attr("term").toLowerCase() + "]").each(function () {

                $(this).children('img').css("visibility", "hidden");

            });

            console.log("Term successfully deleted")
        },
        error: function(e) {

            console.log("Error:" + e);
        }
    })
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

function resetHighlightedFieldsForDNShortcutAnchors(idToHighlight) {
  var bgc = "";
  var commonParent = undefined;
  for (var i = 0; i < dnShortcutAnchors.length; i++) {
    var id = dnShortcutAnchors[i];
    var inputHolderElement = $("#itemHolderId_" + $("#" + id).attr("alt") + "input" + $("#" + id).attr("rel"));
    var inputElement = inputHolderElement.find("input[id*=" + $("#" + id).attr("alt") + "input" + $("#" + id).attr("rel") + "]");
    inputElement = inputElement.length == 0 ? inputHolderElement.find("select[id*=" + $("#" + id).attr("alt") + "input" + $("#" + id).attr("rel") + "]") : inputElement;
    inputElement = inputElement.length == 0 ? inputHolderElement.find("textarea[id*=" + $("#" + id).attr("alt") + "input" + $("#" + id).attr("rel") + "]") : inputElement;
    if (inputElement.attr("type") != undefined && (inputElement.attr("type").toLowerCase() == "radio" || inputElement.attr("type").toLowerCase() == "checkbox")) {
      inputElement = inputElement.parent();
    }
    bgc = id == idToHighlight? "yellow" : "";
    if (commonParent == undefined || (bgc == "" && inputElement.parent()[0] != commonParent[0])) {
      inputElement.css("background-color", bgc);
    }
    if (bgc == "yellow" && commonParent == undefined) {
      commonParent = inputElement.parent();
    }
  }
}

function highlightFieldForDNShortcutAnchor(idToHighlight) {
  resetHighlightedFieldsForDNShortcutAnchors(idToHighlight);
}

function getDNShortcutAnchorId(resolutionStatusId) {
  var result = "";
  switch (resolutionStatusId) {
    case 1 : {
      result = dnShortcutAnchors[0];
      break;
    }
    case 2 : {
      result = dnShortcutAnchors[1];
      break;
    }
    case 3 : {
      result = dnShortcutAnchors[2];
      break;
    }
    case 4 : {
      result = dnShortcutAnchors[3];
      break;
    }
    case 5 : {
      result = dnShortcutAnchors[4];
      break;
    }
  }
  return result;
}

function updateCRFHeaderFunction(parametersHolder) {
	try {
		jQuery.ajax({
	    url : parametersHolder.contextPath + "/UpdateCRFHeader",
		  type : "GET",
			data : parametersHolder,
			cache : false,
			success : function(data) {
				if (data.indexOf("dnShortcutsTable") >= 0) {
          $('#dnShortcutsTable').removeClass("hidden");
					$('#dnShortcutsTable')[0].outerHTML = $.trim(data);
          for (var i = 0; i < dnShortcutLinks.length; i++) {
            if (parseInt($.trim($("#" + dnShortcutLinks[i]).text())) == 0) {
              var inputHolderElement = $("#itemHolderId_" + $("#" + dnShortcutAnchors[i]).attr("alt") + "input" + $("#" + dnShortcutAnchors[i]).attr("rel"));
              var inputElement = inputHolderElement.find("input[id*=" + $("#" + dnShortcutAnchors[i]).attr("alt") + "input" + $("#" + dnShortcutAnchors[i]).attr("rel") + "]");
              inputElement = inputElement.length == 0 ? inputHolderElement.find("select[id*=" + $("#" + dnShortcutAnchors[i]).attr("alt") + "input" + $("#" + dnShortcutAnchors[i]).attr("rel") + "]") : inputElement;
              inputElement = inputElement.length == 0 ? inputHolderElement.find("textarea[id*=" + $("#" + dnShortcutAnchors[i]).attr("alt") + "input" + $("#" + dnShortcutAnchors[i]).attr("rel") + "]") : inputElement;
              if (inputElement.attr("type") != undefined && (inputElement.attr("type").toLowerCase() == "radio" || inputElement.attr("type").toLowerCase() == "checkbox")) {
                inputElement =  inputElement.parent();
              }
              inputElement.css("background", "");
              $("#" + dnShortcutAnchors[i]).remove();
            }
          }
				}
			}
    });
	} catch (e) {
		//
	}
}

function addMissedDNShortcutAnchors() {
  var regexp = new RegExp("flag_.*input\d*");
  $("img[id^=flag]").filter(function () { return regexp.test($(this).attr("id")) && $(this).attr("id").indexOf("_[") < 0; }).each(function(){
    var v = $(this).attr("id").match(/_(\d*)input\d*/) || $(this).attr("id").match(/_manual(\d*)input\d*/);
    for (var i = 0; i < dnShortcutAnchors.length; i++) {
      if ($(this).attr("src").toLowerCase().indexOf(dnFlagImages[i].toLowerCase()) >= 0 && $("#" + dnShortcutAnchors[i]).length == 0) {
        $(this).closest(".itemHolderClass").prepend( "<a id=\"" + dnShortcutAnchors[i] + "\" rel=\"" + $(this).attr("id").replace(/flag_.*input/, "") + "\" alt=\"" + (v != undefined && v.length == 2 ? v[1] : "") + "\"></a>" );
      }
    }
  });
}

function addDNShortcutAnchorsForItem(parametersHolder) {
  try {
    jQuery.ajax({
      url : parametersHolder.contextPath + "/UpdateDNShortcutAnchors",
      type : "GET",
      data : parametersHolder,
      cache : false,
      success : function(data) {
        if (data.indexOf("id=") >= 0) {
          var inputHolderElement = $("#itemHolderId_" + parametersHolder.rowCount + "input" + parametersHolder.itemId);
          inputHolderElement.prepend(data);
        }
        addMissedDNShortcutAnchors();
      }
    });
  } catch (e) {
    //
  }
}

function removeAllDNShortcutAnchorsForItem(parametersHolder) {
  var inputHolderElement = $("#itemHolderId_" + parametersHolder.rowCount + "input" + parametersHolder.itemId);
  for (var i = 0; i < dnShortcutAnchors.length; i++) {
    inputHolderElement.find("#" + dnShortcutAnchors[i]).remove();
  }
}

function removeAnchorIfItsBelowThanCurrent(id, parametersHolder) {
  if ($("#" + id).length == 1) {
    var prevAnchorHolder = $("#" + id).closest(".itemHolderClass");
    var newAnchorHolder = $("#itemHolderId_" + parametersHolder.rowCount + "input" + parametersHolder.itemId);
    var prevAnchorHolderRowCount = parseInt(prevAnchorHolder.attr("id").replace(/itemHolderId_/, "").replace(/input.*/, ""));
    var newAnchorHolderRowCount = parseInt(parametersHolder.rowCount);
    var prevAnchorHolderItemId = parseInt(prevAnchorHolder.attr("id").replace(/itemHolderId_.*input/, ""));
    var newAnchorHolderItemId = parseInt(parametersHolder.itemId);
    if ((isNaN(prevAnchorHolderRowCount) || isNaN(newAnchorHolderRowCount)) && prevAnchorHolderItemId > newAnchorHolderItemId) {
      $("#" + id).remove();
    } else
    if (!isNaN(prevAnchorHolderRowCount) && !isNaN(newAnchorHolderRowCount) && newAnchorHolderRowCount < prevAnchorHolderRowCount) {
      $("#" + id).remove();
    } else
    if (!isNaN(prevAnchorHolderRowCount) && !isNaN(newAnchorHolderRowCount) && newAnchorHolderRowCount == prevAnchorHolderRowCount && prevAnchorHolderItemId > newAnchorHolderItemId) {
      $("#" + id).remove();
    }
  }
}

function addDNShortcutAnchor(parametersHolder) {
  var id = getDNShortcutAnchorId(parametersHolder.resolutionStatusId)
  if (id != "") {
    resetHighlightedFieldsForDNShortcutAnchors();
    removeAnchorIfItsBelowThanCurrent(id, parametersHolder);
    removeAllDNShortcutAnchorsForItem(parametersHolder);
    addDNShortcutAnchorsForItem(parametersHolder);
    updateCRFHeaderFunction(parametersHolder);
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
		
function setParameterForDNWithPath(field, parameterName, value, contextPath) {
	$.ajax({
		url: contextPath+'/ChangeParametersForDNote',
		type: 'GET',
		data: 'field='+field+'&parameterName='+parameterName+'&value='+value,
		dataType: 'text'
	});
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
