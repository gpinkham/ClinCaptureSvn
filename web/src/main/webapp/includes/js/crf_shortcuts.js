var ignoreResize = false;
var crfShortcutsBoxState = 0;
var currentHighlightedShortcutAnchor;
var currentHighlightedShortcutAnchorInd;
var currentHighlightedShortcutAnchorCounter;
var crfShortcutAnchors = ["newDn_", "updatedDn_", "resolutionProposedDn_", "closedDn_", "annotationDn_", "itemToSDV_"];

// ITEM LEVEL SDV

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
                var crfShortcutTotalItemsToSDV = $("#crfShortcutTotalItemsToSDV");
                crfShortcutTotalItemsToSDV.text(" " + jsonData.totalItemsToSDV + " ");
                crfShortcutTotalItemsToSDV.parent("a.crfShortcut").attr("sectiontotal", jsonData.totalSectionItemsToSDV);
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
                    var holderData = "<a id=\"itemToSDV_" + (n + 1) + "\" rel=\"" + itemData.itemId + "\" alt=\"" + itemData.rowCount + "\">";
                    holder.append(holderData);
                }
                adjustCrfShortcutsTable();
                gfRemoveOverlay();
            }
        });
    } catch (e) {
        gfRemoveOverlay();
    }
}

// CRF SHORTCUTS

function clearRepeatTemplate() {
    $(document).ready(function () {
        $("table.aka_form_table tr[repeat=template] a.sdvItemLink").remove();
        $("table.aka_form_table tr[repeat=template] div[id^=crfShortcutAnchors_]").remove();
    });
}

$(window).scroll(function() {
    if (crfShortcutsBoxState == 1) {
        enableDNBoxFeatures();
    }
});

$(window).resize(function() {
    if (crfShortcutsBoxState == 1 && !ignoreResize) {
        unpin();
    }
});

function crfShortcutsIgnoreResize(value) {
    ignoreResize = value;
}

function enableDNBoxFeatures() {
    var crfShortcutsTable = $("#crfShortcutsTable");
    if (crfShortcutsBoxState == 1 && crfShortcutsTable.length > 0 && crfShortcutsTable.css("position") == "relative") {
        crfShortcutsTable.parents("td:first").css("height", crfShortcutsTable.outerHeight() + "px");
        crfShortcutsTable.css("top", crfShortcutsTable.position().top + "px");
        crfShortcutsTable.css("position", "fixed");
        crfShortcutsTable.draggable({
            containment: "window",
            scroll: false,
            start: function (event, ui) {
                crfShortcutsTable.css("position", "absolute");
            }, drag: function (event, ui) {
                if (ui.offset != undefined && ui.position != undefined && ui.offset.top != ui.position.top) ui.position.top = ui.offset.top;
                if (ui.offset != undefined && ui.position != undefined && ui.offset.left != ui.position.left) ui.position.left = ui.offset.left;
            }, stop: function (event, ui) {
                crfShortcutsTable.css("position", "fixed");
                crfShortcutsTable.css("top", (parseInt(crfShortcutsTable.css("top")) - $(window).scrollTop()) + 'px');
                crfShortcutsTable.css("left", (parseInt(crfShortcutsTable.css("left")) - $(window).scrollLeft()) + 'px');
            }
        });
    }
}

function resetCrfShortcutsTable() {
    var crfShortcutsTable = $("#crfShortcutsTable");
    if (crfShortcutsTable.length > 0) {
        crfShortcutsTable.css("top", "");
        crfShortcutsTable.css("left", "");
        crfShortcutsTable.css("position", "relative");
        crfShortcutsTable.draggable("destroy");
    }
}

function unpin() {
    crfShortcutsBoxState = 0;
    resetCrfShortcutsTable();
    var element = $("#pushpin");
    if (element.length == 1) {
        element.removeClass("ui-icon-pin-w");
        element.addClass("ui-icon-bullet");
        element.attr("title", element.attr("unlocktitle"));
    }
}

function pin() {
    crfShortcutsBoxState = 1;
    enableDNBoxFeatures();
    var element = $("#pushpin");
    if (element.length == 1) {
        element.removeClass("ui-icon-bullet");
        element.addClass("ui-icon-pin-w");
        element.attr("title", element.attr("locktitle"));
    }
}

function processPushpin() {
    if ($("#pushpin").hasClass("ui-icon-pin-w")) {
        unpin();
    } else {
        pin();
    }
    return false;
}

function adjustCrfShortcutsTopPosition() {
    var crfShortcutsTable = $("#crfShortcutsTable");
    if (crfShortcutsTable.length == 1 && crfShortcutsTable.css("position") == "fixed") {
        var crfInfoBoxOpen = $('#CRF_infobox_open');
        var p = crfInfoBoxOpen.css('display') == 'none' ? -1 : 1;
        crfShortcutsTable.css("top", crfShortcutsTable.position().top + p * crfInfoBoxOpen.outerHeight() + "px");
    }
}

function adjustCrfShortcutsTable() {
    var selector1;
    var selector2;
    var crfShortcutsSpan = parseInt($("#crfShortcutsSpan").text());
    var crfShortcutsWidth = parseInt($("#crfShortcutsWidth").text());
    var allowSdvWithOpenQueries = $("#crfShortcutsAllowSdvWithOpenQueries").text() == "yes";

    var crfShortcutTotalNew = $("#crfShortcutTotalNew");
    var crfShortcutTotalUpdated = $("#crfShortcutTotalUpdated");
    var crfShortcutTotalResolutionProposed = $("#crfShortcutTotalResolutionProposed");
    var crfShortcutTotalClosed = $("#crfShortcutTotalClosed");
    var crfShortcutTotalAnnotations = $("#crfShortcutTotalAnnotations");

    var itemsToSDVAreHidden = false;
    var userIsAbleToSDVItems = ($("#userIsAbleToSDVItems").text() == "true");
    var hasDNs = !(parseInt(crfShortcutTotalNew.text()) == 0 && parseInt(crfShortcutTotalUpdated.text()) == 0 && (crfShortcutTotalResolutionProposed.length == 0 || parseInt(crfShortcutTotalResolutionProposed.text()) == 0) && parseInt(crfShortcutTotalClosed.text()) == 0 && parseInt(crfShortcutTotalAnnotations.text()) == 0);
    var hasOutstandingDNs = !(parseInt(crfShortcutTotalNew.text()) == 0 && parseInt(crfShortcutTotalUpdated.text()) == 0 && (crfShortcutTotalResolutionProposed.length == 0 || parseInt(crfShortcutTotalResolutionProposed.text()) == 0));
    var hasItemsToSDV = parseInt($("#crfShortcutTotalItemsToSDV").text()) > 0;

    if (!hasItemsToSDV || (!allowSdvWithOpenQueries && hasOutstandingDNs) || !userIsAbleToSDVItems) {
        if ((!allowSdvWithOpenQueries && hasOutstandingDNs) || !userIsAbleToSDVItems) {
            $("a.sdvItemLink").addClass("hidden");
        } else {
            $("a.sdvItemLink").removeClass("hidden");
        }
        itemsToSDVAreHidden = true;
        selector1 = "#crfShortcutsSubTable tr:eq(1) td:eq(" + (crfShortcutsSpan - 1) + ")";
        selector2 = "#crfShortcutsSubTable tr:eq(2) td:eq(" + (crfShortcutsSpan - 1) + ")";
        $(selector1).addClass("hidden");
        $(selector2).addClass("hidden");
        crfShortcutsSpan--;
    } else {
        $("a.sdvItemLink").removeClass("hidden");
        selector1 = "#crfShortcutsSubTable tr:eq(1) td:eq(" + (crfShortcutsSpan - 1) + ")";
        selector2 = "#crfShortcutsSubTable tr:eq(2) td:eq(" + (crfShortcutsSpan - 1) + ")";
        $(selector1).removeClass("hidden");
        $(selector2).removeClass("hidden");
    }

    var endWidth = crfShortcutsSpan * crfShortcutsWidth;
    var crfShortcutsSubTable = $("#crfShortcutsSubTable");
    crfShortcutsSubTable.attr("width", endWidth + "px");
    selector1 = "#crfShortcutsSubTable tr:eq(0) td";
    selector2 = "#crfShortcutsSubTable tr:eq(1) td";
    $(selector1).attr("colspan", crfShortcutsSpan);
    $(selector2).attr("width", ((crfShortcutsWidth / endWidth) * 100) + "%");

    var crfShortcutsTable = $("#crfShortcutsTable");
    var parentTD = crfShortcutsTable.parents("td:first");

    if (!hasDNs && itemsToSDVAreHidden) {
        crfShortcutsTable.addClass("hidden");
        parentTD.removeClass(parentTD.attr("classname"));
    } else if (crfShortcutsTable.hasClass("hidden")) {
        crfShortcutsTable.removeClass("hidden");
        parentTD.addClass(parentTD.attr("classname"));
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
    var highlightedShortcutAnchor = $("#" + currentHighlightedShortcutAnchor);
    var newHighlightedShortcutAnchor = $("#" + newCurrentHighlightedShortcutAnchor);
    if (currentHighlightedShortcutAnchor != undefined && currentHighlightedShortcutAnchor != newCurrentHighlightedShortcutAnchor && highlightedShortcutAnchor.parent()[0] == newHighlightedShortcutAnchor.parent()[0]) {
        if (currentHighlightedShortcutAnchor.replace(/_.*/g,"") == newCurrentHighlightedShortcutAnchor.replace(/_.*/g,"") && highlightedShortcutAnchor.parent()[0] == newHighlightedShortcutAnchor.parent()[0]) {
            highlightFieldForCRFShortcutAnchor(ind, currentElement);
            return;
        }
        delay = 100;
    }
    if (newHighlightedShortcutAnchor.parents(".itemHolderClass:first").parents("tr:first").css("display") == "none" || newHighlightedShortcutAnchor.parents("div[id^=crfShortcutAnchors_]:first").parents("td:first").css("display") == "none") {
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

                var crfShortcutTotalNew = $("#crfShortcutTotalNew");
                var crfShortcutTotalUpdated = $("#crfShortcutTotalUpdated");
                var crfShortcutTotalResolutionProposed = $("#crfShortcutTotalResolutionProposed");
                var crfShortcutTotalClosed = $("#crfShortcutTotalClosed");
                var crfShortcutTotalAnnotations = $("#crfShortcutTotalAnnotations");

                crfShortcutTotalNew.text(" " + jsonObject.totalNew + " ");
                crfShortcutTotalUpdated.text(" " + jsonObject.totalUpdated + " ");
                crfShortcutTotalResolutionProposed.text(" " + jsonObject.totalResolutionProposed + " ");
                crfShortcutTotalClosed.text(" " + jsonObject.totalClosed + " ");
                crfShortcutTotalAnnotations.text(" " + jsonObject.totalAnnotations + " ");

                crfShortcutTotalNew.parent().attr("sectiontotal", jsonObject.sectionTotalNew);
                crfShortcutTotalNew.parent().attr("nextdnlink", jsonObject.nextNewDnLink);

                crfShortcutTotalUpdated.parent().attr("sectiontotal", jsonObject.sectionTotalUpdated);
                crfShortcutTotalUpdated.parent().attr("nextdnlink", jsonObject.nextUpdatedDnLink);

                crfShortcutTotalResolutionProposed.parent().attr("sectiontotal", jsonObject.sectionTotalResolutionProposed);
                crfShortcutTotalResolutionProposed.parent().attr("nextdnlink", jsonObject.nextResolutionProposedDnLink);

                crfShortcutTotalClosed.parent().attr("sectiontotal", jsonObject.sectionTotalClosed);
                crfShortcutTotalClosed.parent().attr("nextdnlink", jsonObject.nextClosedDnLink);

                crfShortcutTotalAnnotations.parent().attr("sectiontotal", jsonObject.sectionTotalAnnotations);
                crfShortcutTotalAnnotations.parent().attr("nextdnlink", jsonObject.nextAnnotationDnLink);

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