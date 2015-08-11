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
    <td class="table_header_column" align="center" style="width: 80px;">
		<a href="<c:out value="${currRow.bean.downloadLink}"/>">
			<img name="bt_Download1" src="images/bt_Download.gif" border="0" align="left" hspace="6"
				 alt="<fmt:message key="download" bundle="${resword}"/>"
				 title="<fmt:message key="download" bundle="${resword}"/>"/>
		</a>
		<a href="<c:out value="${currRow.bean.deleteLink}"/>">
			<img name="bt_Delete1" src="images/bt_Delete.gif" border="0"
				 alt="<fmt:message key="delete" bundle="${resword}"/>"
				 title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"/>
		</a>
	</td>
</tr>