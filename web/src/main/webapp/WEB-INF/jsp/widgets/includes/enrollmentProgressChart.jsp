<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="enrollment_progress_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${epPreviousYearExists}">
					<input type="button" name="BTN_Back" id="previous" value="Previous" class="button_medium" onClick="javascript: initEnrollmentProgress('back');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${epNextYearExists}">
					<input type="button" name="BTN_Forvard" id="next" value="Next" class="button_medium" onClick="javascript: initEnrollmentProgress('next');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<form class="hidden" id="enrollment_progress">
<c:forEach items="${epDataRows}" var="row" varStatus="rowIndex">
	<input type="hidden" value="${row.key}" 
		<c:forEach items="${row.value}" var="status">
			stat-${status.key.name}="${status.value}"
		</c:forEach>
	/>
</c:forEach>

	<input type="text" value="${epYear}" id="epYear" />
	<input type="text" class="currentColor" value="${newThemeColor}">
</form>
<input type="hidden" value="${study.id}" id="epStudyId">
