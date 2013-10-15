<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<c:if test="${discrepancyShortcutsAnalyzer ne null && discrepancyShortcutsAnalyzer.hasNotes}">
  <c:set var="dnShortcutsSpan" value="${5}"/>
  <c:set var="dnShortcutsWidth" value="100px"/>
  <c:if test="${discrepancyShortcutsAnalyzer.totalResolutionProposed == 0}">
    <c:set var="dnShortcutsWidth" value="80px"/>
    <c:set var="dnShortcutsSpan" value="${dnShortcutsSpan - 1}"/>
  </c:if>
  <table id="dnShortcutsTable" border="0" cellspacing="0" cellpadding="0" width="" style="padding-top: 20px;"><tr><td>
    <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR"><div class="tablebox_center">
      <table border="0" cellspacing="0" cellpadding="0" width="">
        <tr>
          <td colspan="${dnShortcutsSpan}" valign="top" class="table_cell_left_header" style="padding-left: 6px; padding-right: 6px;"><b>Discrepancy Notes on this CRF:</b></td>
        </tr>
        <tr>
          <td valign="top" width="${dnShortcutsWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#CC0000;text-align: center;" width="20%"><fmt:message key="open" bundle="${resword}"/></td>
          <td valign="top" width="${dnShortcutsWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#D4A718;text-align: center;" width="20%"><fmt:message key="updated" bundle="${resword}"/></td>
          <c:if test="${discrepancyShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" width="${dnShortcutsWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;" width="20%"> <fmt:message key="resolved" bundle="${resword}"/></td></c:if>
          <td valign="top" width="${dnShortcutsWidth}" align="center" class="table_cell_left" style="white-space: nowrap;border-right:1px solid #E6E6E6;color:#7CB98F;text-align: center;" width="20%"><fmt:message key="closed" bundle="${resword}"/></td>
          <td valign="top" width="${dnShortcutsWidth}" align="center" class="table_cell_left"  style="white-space: nowrap;border-right:1px solid #E6E6E6;color:black;text-align: center;" width="20%"> <fmt:message key="annotations" bundle="${resword}"/></td>
        </tr>
        <tr>
          <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#CC0000;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstNewDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstNewDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalNew">&nbsp;${discrepancyShortcutsAnalyzer.totalNew}&nbsp;</div></a></td>
          <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#D4A718;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstUpdatedDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstUpdatedDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalUpdated">&nbsp;${discrepancyShortcutsAnalyzer.totalUpdated}&nbsp;</div></a></td>
          <c:if test="${discrepancyShortcutsAnalyzer.totalResolutionProposed != 0}"><td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:black;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstResolutionProposedLink}" onclick="highlightFieldForDNShortcutAnchor('firstResolutionProposed');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalResolutionProposed">&nbsp;${discrepancyShortcutsAnalyzer.totalResolutionProposed}&nbsp;</div></a></td></c:if>
          <td valign="top" align="center" class="table_cell_left" style="border-right:1px solid #E6E6E6;color:#7CB98F;" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstClosedDnLink}" onclick="highlightFieldForDNShortcutAnchor('firstClosedDn');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalClosed">&nbsp;${discrepancyShortcutsAnalyzer.totalClosed}&nbsp;</div></a></td>
          <td valign="top" align="center" class="table_cell_left"  style="border-right:1px solid #E6E6E6;color:black" width="20%"><a class="dnShortcut" href="${discrepancyShortcutsAnalyzer.firstAnnotationLink}" onclick="highlightFieldForDNShortcutAnchor('firstAnnotation');"><div style="width: 100%; text-align: center;" id="dnShortcutTotalAnnotations">&nbsp;${discrepancyShortcutsAnalyzer.totalAnnotations}&nbsp;</div></a></td>
        </tr>
      </table>
    </div></div></div></div></div></div></div></div></div>
    <c:if test="${discrepancyShortcutsAnalyzer.totalNew == 0 && discrepancyShortcutsAnalyzer.totalUpdated == 0 && discrepancyShortcutsAnalyzer.totalResolutionProposed == 0 && discrepancyShortcutsAnalyzer.totalClosed == 0 && discrepancyShortcutsAnalyzer.totalAnnotations == 0}">
      <script>
        $("#dnShortcutsTable").addClass("hidden");
      </script>
    </c:if>
  </td></tr></table>
</c:if>
