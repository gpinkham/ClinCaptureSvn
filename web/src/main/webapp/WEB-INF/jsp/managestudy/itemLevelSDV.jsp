<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmr" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="resnotes"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/home-page-with-charts-header.jsp"/>
<jsp:include page="../include/sideAlert.jsp"/>
<jsp:include page="/includes/js/dialogs.js.jsp" />

<script type="text/JavaScript" language="JavaScript" src="../includes/js/pages/item_level_sdv.js"></script>

<tr id="sidebar_Instructions_open">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
				src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
			<fmt:message key="choose_crf_instruction_key" bundle="${resword}"/>
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img
				src="../images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<h1>
	<span class="first_level_header">
		<fmt:message bundle="${resword}" key="configure_items_for_sdv"/>. <br/>
		<fmt:message bundle="${resword}" key="event"/>: ${studyEventDefinition.name}. <fmt:message bundle="${resword}" key="CRF"/>: ${crf.name}.
	</span>
</h1>

<form>
	<input type="hidden" value="${edcId}" name="edc_id">
	<div>
		<fmt:message bundle="${resword}" key="choose_crf_version_to_configure"/>:
		<select name="crf_version" style="display: inline-block;">
			<c:forEach var="version" items="${versionsList}" varStatus="status">
				<option ${status.index == 0 ? 'selected' : ''} value="${version.id}">${version.name}</option>
			</c:forEach>
		</select>
	</div>

	<div class="sdv_tables_content">
	</div>


	<table style="margin-top:40px">
		<tr>
			<td>
				<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage"
					   value="<fmt:message key="back" bundle="${resword}"/>"
					   class="button_medium medium_back"
					   onClick="checkGoToEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '../UpdateEventDefinition?id=${edcId}');"/>


			</td>
			<td>
				<input type="button" name="submit_and_exit" value="<fmt:message key="continue" bundle="${resword}"/>"
					   class="button_medium medium_continue" onclick="submitItemLevelSDVAndExit();"/>
			</td>
			<td>
				<img src="../images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
			</td>
		</tr>
	</table>
</form>

<script>
	$(window).load(function() {
		var $versionsSelect = $("select[name=crf_version]");

		getItemsTableForCRFVersion($versionsSelect.val());
		setSuccessMessage("<fmt:message bundle="${resword}" key="configuration_saved_successfully_please_note"/>");

		$versionsSelect.change(function() {
			var $statusIcon = $("img[name=DataStatus_bottom]");
			if ($statusIcon && $statusIcon.attr("src").indexOf("icon_UnsavedData") > -1) {
				showChangedSDVConfigMessage($versionsSelect.val());
			} else {
				getItemsTableForCRFVersion($versionsSelect.val());
			}
		});
	});
</script>

<jsp:include page="../include/footer.jsp"/>