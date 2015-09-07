<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:if test="${!empty pageMessages}">
	<div class="alert">
		<c:forEach var="message" items="${pageMessages}">
			<c:out value="${message}" escapeXml="false"/>
			<br><br>
		</c:forEach>
	</div>
</c:if>
<c:if test="${!empty formMessages or !empty warningMessages}">
	<div id="errorMessagesContainer" style="word-wrap: break-word !important; width:600px;margin-bottom: 20px;"
		 class="aka_err_message">
		<ul>
			<c:forEach var="message" items="${warningMessages}">
				<c:forEach items="${message.value}" var="value">
					<li style="color: #868686">
                            <span style="text-decoration: underline"><strong>
								<label onclick="getFocused('<c:out value="${message.key}"/>');"><c:out
										value="${value}"/></label>
							</strong></span>
					</li>
				</c:forEach>
			</c:forEach>
		</ul>
		<ul>
			<c:forEach var="formMsg" items="${formMessages}">
				<c:choose>
					<c:when test="${Hardrules}">
						<li style="color: #ff0000">
					</c:when>
					<c:otherwise>
						<li style="color: #E46E16">
					</c:otherwise>
				</c:choose>
				<span style="text-decoration: underline"><strong><label for="<c:out value="${formMsg.key}" />"><c:out
						value="${formMsg.value}"/></label></strong></span></li>
			</c:forEach>
		</ul>
	</div>
</c:if>