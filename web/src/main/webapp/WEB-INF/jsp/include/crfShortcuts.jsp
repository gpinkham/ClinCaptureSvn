<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />
<c:if test="${crfShortcutsAnalyzer ne null}">
    <table id="" border="0" cellpadding="0" cellspacing="0" class="${param.wrapperClass}">
        <tr>
            <td classname="${param.className}" class="">
                <c:set var="tdPercentWidth" value="16.7%"/>
                <c:set var="crfShortcutsSpan" value="${6}"/>
                <c:set var="crfShortcutsWidth" value="${110}"/>
                <c:if test="${crfShortcutsAnalyzer.totalResolutionProposed == 0}">
                    <c:set var="tdPercentWidth" value="20%"/>
                    <c:set var="crfShortcutsSpan" value="${crfShortcutsSpan - 1}"/>
                </c:if>
                <span class="hidden" id="crfShortcutsSpan">${crfShortcutsSpan}</span>
                <span class="hidden" id="crfShortcutsWidth">${crfShortcutsWidth}</span>
                <span class="hidden" id="crfShortcutsAllowSdvWithOpenQueries">${study.studyParameterConfig.allowSdvWithOpenQueries}</span>
                <span class="hidden" id="userIsAbleToSDVItems">${crfShortcutsAnalyzer.userIsAbleToSDVItems}</span>
                <table id="crfShortcutsTable" border="0" cellspacing="0" cellpadding="0" style="cursor: default;position: relative;" class="notSelectable hidden">
                    <tr>
                        <td>
                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR"><div class="tablebox_center">
                                <table id="crfShortcutsSubTable" border="0" cellspacing="0" cellpadding="0" width="${crfShortcutsSpan * crfShortcutsWidth}px">
                                    <tr>
                                        <td colspan="${crfShortcutsSpan}" valign="top" class="table_cell_left_header" style="padding-left: 6px; padding-right: 6px;">
                                            <b><fmt:message key="crf_shortcuts_header" bundle="${resword}"/>:</b>
                                            <a onclick="processPushpin();" type="image" id="pushpin" class="ui-icon ui-icon-bullet" title="<fmt:message key="unlock" bundle="${resword}"/>" unlocktitle="<fmt:message key="unlock" bundle="${resword}"/>" locktitle="<fmt:message key="lock" bundle="${resword}"/>">&nbsp;</a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#CC0000;text-align: center;"><fmt:message key="openDn" bundle="${resword}"/></td>
                                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#D4A718;text-align: center;"><fmt:message key="updatedDn" bundle="${resword}"/></td>
                                        <c:if test="${crfShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="resolved" bundle="${resword}"/></td></c:if>
                                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#7CB98F;text-align: center;"><fmt:message key="closedDn" bundle="${resword}"/></td>
                                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="annotations" bundle="${resword}"/></td>
                                        <td valign="top" width="${tdPercentWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;"> <fmt:message key="itemsToSDV" bundle="${resword}"/></td>
                                    </tr>
                                    <tr>
                                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#CC0000;"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalNew}" nextdnlink="${crfShortcutsAnalyzer.nextNewDnLink}" onclick="highlightFieldForCRFShortcutAnchor(0, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalNew">&nbsp;${crfShortcutsAnalyzer.totalNew}&nbsp;</div></a></td>
                                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#D4A718;"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalUpdated}" nextdnlink="${crfShortcutsAnalyzer.nextUpdatedDnLink}" onclick="highlightFieldForCRFShortcutAnchor(1, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalUpdated">&nbsp;${crfShortcutsAnalyzer.totalUpdated}&nbsp;</div></a></td>
                                        <c:if test="${crfShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black;"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalResolutionProposed}" nextdnlink="${crfShortcutsAnalyzer.nextResolutionProposedLink}" onclick="highlightFieldForCRFShortcutAnchor(2, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalResolutionProposed">&nbsp;${crfShortcutsAnalyzer.totalResolutionProposed}&nbsp;</div></a></td></c:if>
                                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#7CB98F;"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalClosed}" nextdnlink="${crfShortcutsAnalyzer.nextClosedDnLink}" onclick="highlightFieldForCRFShortcutAnchor(3, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalClosed">&nbsp;${crfShortcutsAnalyzer.totalClosed}&nbsp;</div></a></td>
                                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalAnnotations}" nextdnlink="${crfShortcutsAnalyzer.nextAnnotationLink}" onclick="highlightFieldForCRFShortcutAnchor(4, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalAnnotations">&nbsp;${crfShortcutsAnalyzer.totalAnnotations}&nbsp;</div></a></td>
                                        <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black"><a class="crfShortcut" sectiontotal="${crfShortcutsAnalyzer.sectionTotalItemsToSDV}" nextdnlink="${crfShortcutsAnalyzer.nextItemToSDVLink}" onclick="highlightFieldForCRFShortcutAnchor(5, this);"><div style="width: 100%; text-align: center;" id="crfShortcutTotalItemsToSDV">&nbsp;${crfShortcutsAnalyzer.totalItemsToSDV}&nbsp;</div></a></td>
                                    </tr>
                                </table>
                            </div></div></div></div></div></div></div></div></div>
                            <c:if test="${crfShortcutsAnalyzer eq null || (crfShortcutsAnalyzer.totalNew == 0 && crfShortcutsAnalyzer.totalUpdated == 0 && crfShortcutsAnalyzer.totalResolutionProposed == 0 && crfShortcutsAnalyzer.totalClosed == 0 && crfShortcutsAnalyzer.totalAnnotations == 0 && crfShortcutsAnalyzer.totalItemsToSDV == 0)}">
                                <script>
                                    $("#crfShortcutsTable").addClass("hidden");
                                </script>
                            </c:if>
                        </td></tr>
                </table>
            </td>
        </tr>
    </table>
</c:if>
<script>
    adjustCrfShortcutsTable();
    var crfShortcutInterval;
    var crfShortcutFunction = function() {
        try {
            var end = document.location.href.length;
            var start = document.location.href.indexOf("#");
            if (start > 0) {
                var crfShortcutId = document.location.href.substring(start, end);
                if (crfShortcutId == "#" || $(crfShortcutId).length == 0) {
                    clearInterval(crfShortcutInterval);
                } else {
                    var positionTop = parseInt($(crfShortcutId).position().top);
                    var browserClientHeight = getBrowserClientHeight();
                    if (positionTop >= 0) {
                        if (positionTop > browserClientHeight) {
                            document.location.reload(true);
                        }
                        highlightFirstFieldForCRFShortcutAnchors(crfShortcutId.replace("#", ""));
                        clearInterval(crfShortcutInterval);
                    }
                }
            } else {
                clearInterval(crfShortcutInterval);
            }
        } catch (e) {
            console.log("Error: " + e);
        }
    }
    $(document).ready(function () {
        crfShortcutInterval = setInterval(crfShortcutFunction, 1);
    });
    window.updateCRFHeader = function(field, itemId, rowCount, resolutionStatusId) {
        gfAddOverlay();
        var parametersHolder = {
            contextPath: "<%=request.getContextPath()%>",
            servletPath: document.location.pathname.replace("<%=request.getContextPath()%>", ""),
            restfulUrl: document.location.pathname.toString().indexOf("/ClinicalData/html/view") >= 0,
            tabId: "<%=request.getAttribute("tabId") == null ? request.getParameter("tabId") : request.getAttribute("tabId")%>",
            sectionId: "<%=request.getAttribute("sectionId") == null ? request.getParameter("sectionId") : request.getAttribute("sectionId")%>",
            itemId: parseInt(itemId),
            field: field,
            rowCount: rowCount,
            resolutionStatusId: parseInt(resolutionStatusId),
            eventCRFId: parseInt("${eventCRF.id}"),
            eventDefinitionCRFId: parseInt("<%=request.getParameter("eventDefinitionCRFId") == null ? request.getAttribute("eventDefinitionCRFId") : request.getParameter("eventDefinitionCRFId")%>"),
            studyEventId: parseInt("<%=request.getParameter("studyEventId") == null ? request.getAttribute("studyEventId") : request.getParameter("studyEventId")%>"),
            subjectId: parseInt("<%=request.getParameter("subjectId") == null ? request.getAttribute("subjectId") : request.getParameter("subjectId")%>"),
            action: "<%=request.getParameter("action") == null ? request.getAttribute("action") : request.getParameter("action")%>",
            exitTo: "<%=request.getParameter("exitTo") == null ? request.getAttribute("exitTo") : request.getParameter("exitTo")%>",
            crfVersionId: parseInt("<%=request.getParameter("crfVersionId") == null ? request.getAttribute("crfVersionId") : request.getParameter("crfVersionId")%>")
        }
        if (parametersHolder.exitTo.toLowerCase() == "null") {
            parametersHolder.exitTo = "";
        }
        resetHighlightedFieldsForCRFShortcutAnchors();
        updateCRFHeaderFunction(parametersHolder);
    };
</script>