<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:if test="${discrepancyShortcutsAnalyzer ne null && discrepancyShortcutsAnalyzer.hasNotes}">
    <table id="dnShortcutsTable" border="0" cellspacing="1" cellpadding="0" width="100%">
        <tr>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#CC0000;" width="20%"><fmt:message key="open" bundle="${resword}"/></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#D4A718;" width="20%"><fmt:message key="updated" bundle="${resword}"/></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black;" width="20%"> <fmt:message key="resolved" bundle="${resword}"/></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#7CB98F;" width="20%"><fmt:message key="closed" bundle="${resword}"/></td>
            <td valign="top" align="center" class="table_cell_left"  style="border-right:1px solid #E6E6E6;color:black" width="20%"> <fmt:message key="not_applicable" bundle="${resword}"/></td>
        </tr>
        <tr>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#CC0000;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstNewDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstNewDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalNew">&nbsp;${discrepancyShortcutsAnalyzer.totalNew}&nbsp;</div></a></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#D4A718;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstUpdatedDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstUpdatedDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalUpdated">&nbsp;${discrepancyShortcutsAnalyzer.totalUpdated}&nbsp;</div></a></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstResolutionProposedLink}" onclick="highlightFieldForDNShortcutAnchor('firstResolutionProposed');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalResolutionProposed">&nbsp;${discrepancyShortcutsAnalyzer.totalResolutionProposed}&nbsp;</div></a></td>
            <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#7CB98F;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstClosedDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstClosedDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalClosed">&nbsp;${discrepancyShortcutsAnalyzer.totalClosed}&nbsp;</div></a></td>
            <td valign="top" align="center" class="table_cell_left"  style="border-right:1px solid #E6E6E6;color:black" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstAnnotationLink}" onclick="highlightFieldForDNShortcutAnchor('firstAnnotation');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalAnnotations">&nbsp;${discrepancyShortcutsAnalyzer.totalAnnotations}&nbsp;</div></a></td>
        </tr>
    </table>
</c:if>
