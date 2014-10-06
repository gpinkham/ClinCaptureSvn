<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="coding_progress_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${cpPreviousYearExists}">
					<input type="button" name="BTN_Back" id="previous" value="Previous" class="button_medium" onClick="javascript: initCodingProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${cpNextYearExists}">
					<input type="button" name="BTN_Forvard" id="next" value="Next" class="button_medium" onClick="javascript: initCodingProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="coding_progress_form">
<c:forEach items="${cpDataRows}" var="row" varStatus="rowIndex">
	<input type="hidden" value="${row.key}" 
		<c:forEach items="${row.value}" var="status">
			stat-${fn:replace(status.key," ", "_")}="${status.value}"
		</c:forEach>
	/>
</c:forEach>

	<input type="text" value="${cpYear}" id="cpYear" />
	<input type="text" class="currentColor" value="${newThemeColor}">
	<input type="text" id="cpActivateLegend" value="${cpActivateLegend}">
</form>
<input type="hidden" value="${study.id}" id="cpStudyId">

<script>applyThemeForChart();</script>