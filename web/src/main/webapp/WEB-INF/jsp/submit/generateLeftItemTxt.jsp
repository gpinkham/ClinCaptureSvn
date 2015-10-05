<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<%@ page import="org.akaza.openclinica.logic.score.*, java.util.HashMap, java.util.ArrayList, org.akaza.openclinica.bean.submit.ResponseOptionBean, org.akaza.openclinica.bean.submit.ItemBean" %>

<c:set var="linkText" value="${param.linkText}"/>

<c:out value="${linkText}" escapeXml="false"/>
