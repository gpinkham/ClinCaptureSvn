<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>

<jsp:include page="include/home-page-with-charts-header.jsp"/>

<jsp:include page="include/sideAlert.jsp"/>
	<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
        <b>
        	<fmt:message key="instructions" bundle="${restext}"/>
        </b>
        <div class="sidebar_tab_content">
            <fmt:message key="design_implement_coding" bundle="${restext}"/>
        </div>
    </td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
        	<img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10">
        </a>
        <b>
        	<fmt:message key="instructions" bundle="${restext}"/>
        </b>
    </td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>

<h1> 
    <span class="first_level_header" style="line-height:5px;">
        <fmt:message key="welcome_to" bundle="${restext}"/>
            <c:choose>
                <c:when test='${study.parentStudyId > 0}'>
                    <c:out value='${study.parentStudyName}'/>                
                </c:when>
                <c:otherwise>
                    <c:out value='${study.name}'/>
                </c:otherwise>
        </c:choose>
    </span>
</h1>

<c:if test="${!empty dispayWidgetsLayout}">
	<table class="widgets_container configure">
		<tr>
			<td id="layout1" class="droptrue ui-sortable column1" unselectable="on">
				<span class="filler">filler</span>
				<c:forEach var="widget" items="${dispayWidgetsLayout}">
					<c:if test="${widget.ordinal ne 0 and widget.ordinal%2 ne 0 and not widget.twoColumnWidget}">
						<div class="widget" id="${widget.widgetId}">
							<a onclick="javascript:removeWidget(this)" href="#"><img src="../images/remove_widget.png" class="remove" title="remove"/></a>
							<c:catch var="e">
								<c:import url="widgets/${widget.widgetName}" />
							</c:catch>
							<c:if test="${!empty e}">
								<div class="widget_error_message">
									<fmt:message key="widget_error_message_cannot_load_widget" bundle="${restext}">
										<fmt:param>
											<c:out value="${widget.widgetName}" />
										</fmt:param>
									</fmt:message>
								</div>
							</c:if>
						</div>
					</c:if>
				</c:forEach>
			</td>
			<td id="layout2" class="droptrue ui-sortable column2" unselectable="on">
				<span class="filler">filler</span>
				<c:forEach var="widget" items="${dispayWidgetsLayout}">
					<c:if test="${widget.ordinal ne 0 and widget.ordinal%2 eq 0 and not widget.twoColumnWidget}">
						<div class="widget" id="${widget.widgetId}">
							<a onclick="javascript:removeWidget(this)" href="#"><img src="../images/remove_widget.png" class="remove" title="remove"/></a>
							<c:catch var="e">
								<c:import url="widgets/${widget.widgetName}" />
							</c:catch>
							<c:if test="${!empty e}">
								<div class="widget_error_message">
									<fmt:message key="widget_error_message_cannot_load_widget" bundle="${restext}">
										<fmt:param>
											<c:out value="${widget.widgetName}" />
										</fmt:param>
									</fmt:message>
								</div>
							</c:if>
						</div>
					</c:if>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td colspan="2" id="layout_tc" class="droptrue_tc ui-sortable column_tc" unselectable="on">
				<c:forEach var="widget" items="${dispayWidgetsLayout}">
					<c:if test="${widget.ordinal ne 0 and widget.twoColumnWidget}">
						<div class="widget_big" id="${widget.widgetId}">
							<a onclick="javascript:removeWidget(this)" href="#"><img src="../images/remove_widget.png" class="remove" title="remove"/></a>
							<c:catch var="e">
								<c:import url="widgets/${widget.widgetName}" />
							</c:catch>
							<c:if test="${!empty e}">
								<div class="widget_error_message">
									<fmt:message key="widget_error_message_cannot_load_widget" bundle="${restext}">
										<fmt:param>
											<c:out value="${widget.widgetName}" />
										</fmt:param>
									</fmt:message>
								</div>
							</c:if>
						</div>
					</c:if>
				</c:forEach>
				<span class="filler">filler</span>
			</td>
		</tr>
	</table>
</c:if>

<c:if test="${!empty dispayWidgetsLayout}">
<div class="toolbar_wrapper">
	<h2 align="center"><fmt:message key="available_widgets" bundle="${resword}"/></h2>
	<p class="note"><fmt:message key="drag_n_drop_instruction" bundle="${restext}"/></p>
	<div id="scroll-container">
		<h2 align="center"><fmt:message key="one_column_widgets" bundle="${resword}"/></h2>
		<div id="toolbar" class="droptrue ui-sortable">
			<c:forEach var="widget" items="${dispayWidgetsLayout}">
				<c:if test="${widget.ordinal eq 0 and not widget.twoColumnWidget}">
					<div class="widget" id="${widget.widgetId}">
						<a onclick="javascript:removeWidget(this)" href="#"><img src="../images/remove_widget.png" class="remove" title="Remove"/></a>
						<c:catch var="e">
							<c:import url="widgets/${widget.widgetName}" />
						</c:catch>
						<c:if test="${!empty e}">
							<div class="widget_error_message">
								<fmt:message key="widget_error_message_cannot_load_widget" bundle="${restext}">
									<fmt:param>
										<c:out value="${widget.widgetName}" />
									</fmt:param>
								</fmt:message>
							</div>
						</c:if>
					</div>
				</c:if>
			</c:forEach>
		</div>
		<h2 align="center"><fmt:message key="two_column_widgets" bundle="${resword}"/></h2>
		<div id="toolbar_tc" class="droptrue_tc ui-sortable_tc">
			<c:forEach var="widget" items="${dispayWidgetsLayout}">
				<c:if test="${widget.ordinal eq 0 and widget.twoColumnWidget}">
					<div class="widget_big" id="${widget.widgetId}">
						<a onclick="javascript:removeWidget(this)" href="#"><img src="../images/remove_widget.png" class="remove" title="Remove"/></a>
						<c:catch var="e">
							<c:import url="widgets/${widget.widgetName}" />
						</c:catch>
						<c:if test="${!empty e}">
							<div class="widget_error_message">
								<fmt:message key="widget_error_message_cannot_load_widget" bundle="${restext}">
									<fmt:param>
										<c:out value="${widget.widgetName}" />
									</fmt:param>
								</fmt:message>
							</div>
						</c:if>
					</div>
				</c:if>
			</c:forEach>
			<span class="filler">filler</span>
		</div>
	</div>
	<a onclick="javascript:toolbarToggle(this)" href="#" class="show_hide_link">
		<div id="show_hide">
			<span class="show-message" active="false" style="display: none;"><fmt:message bundle="${resword}" key="show"/></span>
			<span class="hide-message" active="true"><fmt:message bundle="${resword}" key="hide"/></span>
		</div>
	</a>
</div>

</c:if>
<form action="saveHomePage" id="actionForSave">
	<input type="hidden" id="postOrder1" name="postOrder1" class="order" value="" title= "order in column 1"/>
	<input type="hidden" id="postOrder2" name="postOrder2" class="order" value="" title= "order in column 2"/>
	<input type="hidden" id="unusedWidgets" name="unusedWidgets" class="order" value="" title= "list of unused widgets"/>
	<input type="hidden" id="bigWidgets" name="bigWidgets" class="order" value="" title= "order of big widgets"/>
	<input type="hidden" id="userId" name="userId" value="${userBean.id}"/>
	<input type="hidden" id="studyId" name="studyId" value="${study.id}"/>
</form>

<form action="${defaultURL}" id="goToHomePage"></form>
	
<br>
	<table>
	<tr>
		<td>
			<input type="button" name="BTN_Back_Smart" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
		</td>
		<td>
			<input type="button" name="Submit" id="SubminAndContinue" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick="javascript: saveLayoutAndExit()"/>
		</td>
	</tr>
	</table> 
<br>

<script>
	$(document).ready(function($) {
		launchCustomizePage();
	});
</script>

<jsp:include page="include/footer.jsp"/>
