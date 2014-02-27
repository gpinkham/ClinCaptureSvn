<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="chart_wrapper" align="left">
	<c:forEach items="${eventCompletionRows}" var="row" varStatus="status">
		<div class="bar_name">${row.rowName}</div>
		<ul id="stacked_bar" class="bar${status.count}">
			<c:forEach items="${row.rowValues}" var="statuses">
			<c:if test="${statuses.value!=0}">
				<a href="#">
					<li id="stack" class="${statuses.key}">
						<div id="pop-up"></div>
						<div class="hidden" id="value">${statuses.value}</div>
					</li>
				</a>
			</c:if>
			</c:forEach>		
		</ul>		
	</c:forEach>
</div>

<form id="events_completion_form">
	<input type="text" id="has_next" value="${eventCompletionHasNext}" /> 
	<input type="text" id="has_previous" value="${eventCompletionHasPrevious}" />
	<input type="text" id="last_element" value="${eventCompletionLastElement}" />
</form>