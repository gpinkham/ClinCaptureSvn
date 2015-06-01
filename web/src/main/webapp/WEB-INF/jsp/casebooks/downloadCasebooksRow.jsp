<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope="request" id="currRow" class="com.clinovo.entity.DownloadCasebooksRow"/>
<tr>
    <td class="table_header_column" align="center">
        <b>
            <c:out value="${currRow.bean.studySubjectLabel}"/>
        </b>
    </td>
    <td class="table_header_column" align="center">
        <b>
            <c:out value="${currRow.bean.studyName}"/>
        </b>
    </td>
    <td class="table_header_column" align="center">
        <table width=100%>
            <tr>
                <td align="center"><a href="<c:out value="${currRow.bean.downloadLink}"/>"><fmt:message key="download" bundle="${resword}"/></a></td>
                <td align="center"><a href="<c:out value="${currRow.bean.deleteLink}"/>"><fmt:message key="delete" bundle="${resword}"/></a></td>
            </tr>
        </table>
    </td>
</tr>