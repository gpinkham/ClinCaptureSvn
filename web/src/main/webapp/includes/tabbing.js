var tabbingElement;
var tabbingPrevElement;
var tabbingElementIndex = 0;
var tabbingMode = "leftToRight";

var repeatingTable;
var repeatingTableRows = 0;
var repeatingTableColumns = 0;
var repeatingTableCurrentRow = 0;
var repeatingTableStartIndex = 0;
var repeatingTableCurrentColumn = 0;

var focusFirstElementWasCalled;

function focusFirstElement() {
    if (focusFirstElementWasCalled == undefined) {
        focusFirstElementWasCalled = true;
        tabbingElement = undefined;
        tabbingElementIndex = 0;
        tabToNextElement();
    }
}

function focusElement(element) {
    var type = element.attr("type")
    if (type != undefined && type == "radio") {
        var name = element.attr("name");
        var inputs = $("input[name=" + name + "]:checked");
        if (inputs.length == 0) {
            inputs = $("input[name=" + name + "]");
        }
        element = $(inputs.get(0));
    }
    if (tabbingElement == undefined || tabbingElement.get(0) != element.get(0)) {
        if (element.attr("readonly") || element.attr("disabled") || !element.is(":visible")) {
            tabToNextElement();
        } else {
            tabbingElement = element;
            element.focus();
        }
    } else {
        tabToNextElement();
    }
}

function tabToNextElement() {
    var namesArray = [];
    var tabbedElements = $("[tabbed]");
    $.each(tabbedElements, function() {
        var elementName= this.name;
        if ($.inArray(elementName, namesArray) < 0){
            namesArray.push(elementName);
            if (tabbingPrevElement != undefined && tabbingPrevElement.attr("name") == elementName) {
                tabbingElement = undefined;
                tabbingPrevElement = undefined;
                tabbingElementIndex = namesArray.length - 1;
            }
        }
    });
    if (tabbingElement == undefined || namesArray.length > 1) {
        tabbingElementIndex = tabbingElementIndex >= namesArray.length ? 0 : tabbingElementIndex;
        var tabbedElement = $("[name=" + namesArray[tabbingElementIndex] + "]:first");
        tabbingElementIndex++;
        if (tabbedElement != undefined && $(tabbedElement).parents("tr[repeat=template]").length != 0) {
            tabToNextElement();
        } else {
            if (tabbedElement != undefined) {
                var parentTr = $(tabbedElement).parents("tr[repeat-template]");
                var rowNum = parentTr.length == 1 ? $(parentTr.get(0)).attr("repeat") : "";
                if (rowNum == "" || tabbingMode == "leftToRight") {
                    focusElement($(tabbedElement));
                } else if (rowNum != "" && tabbingMode == "topToBottom") {
                    tabbingElementIndex--;
                    repeatingTable = $(tabbedElement).parents("table.repeatingGroupTable:first");
                    repeatingTableRows = parseInt($(tabbedElement).parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow").not("[repeat=template]").length);
                    repeatingTableColumns = parseInt($(tabbedElement).parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow:first td.itemHolderClass").length);
                    repeatingTableCurrentRow = $(tabbedElement).parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow").not("[repeat=template]").index($(tabbedElement).parents("tr.repeatingTableRow:first")) + 1;
                    repeatingTableCurrentColumn = $(tabbedElement).parents("tr.repeatingTableRow:first").find("td.itemHolderClass").index($(tabbedElement).parents("td.itemHolderClass:first")) + 1;
                    if (repeatingTableCurrentRow == 1 && repeatingTableCurrentColumn == 1) {
                        repeatingTableStartIndex = tabbingElementIndex;
                    }
                    tabbingElementIndex = tabbingElementIndex == (repeatingTableStartIndex + (repeatingTableRows * repeatingTableColumns) - 1) ? ++tabbingElementIndex : (repeatingTableCurrentRow == repeatingTableRows ? repeatingTableStartIndex + repeatingTableCurrentColumn : (repeatingTableStartIndex + repeatingTableCurrentColumn - 1 + (repeatingTableCurrentRow * repeatingTableColumns)));
                    focusElement($(tabbedElement));
                }
            }
        }
    }
}

function initCustomTabbing() {
    $(document).bind("keydown", function (e) {
        var keyCode = e.keyCode || e.which;
        if(e.keyCode == 9) {
            e.preventDefault();
            tabToNextElement();
        }
    });
    $(document).mouseup(function() {
        var found = false;
        var namesArray = [];
        var focusedElement = $(':focus');
        var tabbedElements = $("[tabbed]");
        $.each(tabbedElements, function() {
            var elementName= this.name;
            if ($.inArray(elementName, namesArray) < 0){
                namesArray.push(elementName);
            }
            if (elementName == focusedElement.attr("name")) {
                found = true;
                tabbingPrevElement = undefined;
                tabbingElement = focusedElement;
                tabbingElementIndex = namesArray.length - 1;
                if (tabbingMode == "topToBottom" && focusedElement.parents("table.repeatingGroupTable:first").length != 0) {
                    repeatingTable = focusedElement.parents("table.repeatingGroupTable:first");
                    repeatingTableRows = parseInt(focusedElement.parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow").not("[repeat=template]").length);
                    repeatingTableColumns = parseInt(focusedElement.parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow:first td.itemHolderClass").length);
                    repeatingTableCurrentRow = focusedElement.parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow").not("[repeat=template]").index(focusedElement.parents("tr.repeatingTableRow:first")) + 1;
                    repeatingTableCurrentColumn = focusedElement.parents("tr.repeatingTableRow:first").find("td.itemHolderClass").index(focusedElement.parents("td.itemHolderClass:first")) + 1;
                    repeatingTableStartIndex = $.inArray(focusedElement.parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow:first td.itemHolderClass:first select,input,textarea").not("[type=hidden]").get(0).name, namesArray);
                    tabbingElementIndex = tabbingElementIndex == (repeatingTableStartIndex + (repeatingTableRows * repeatingTableColumns) - 1) ? ++tabbingElementIndex : (repeatingTableCurrentRow == repeatingTableRows ? repeatingTableStartIndex + repeatingTableCurrentColumn : (repeatingTableStartIndex + repeatingTableCurrentColumn - 1 + (repeatingTableCurrentRow * repeatingTableColumns)));
                } else {
                    tabbingElementIndex++;
                }
            }
        });
        if (!found || focusedElement.attr("stype") == "remove" || focusedElement.attr("stype") == "add") {
            tabbingPrevElement = tabbingElement;
            if (focusedElement.attr("stype") == "remove" && repeatingTableCurrentRow == focusedElement.parents("table.repeatingGroupTable:first").find("tr.repeatingTableRow").not("[repeat=template]").index(focusedElement.parents("tr.repeatingTableRow:first")) + 1) {
                tabbingPrevElement = $("[name=" + namesArray[tabbingElementIndex] + "]:first");
            }
        }
    });
}
