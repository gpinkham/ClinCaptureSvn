<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>


<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:set var="count" value="${param.eblRowCount}" />
<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.StudyEventRow" />   

   <input type="hidden" name="id<c:out value="${count}"/>" value="<c:out value="${currRow.bean.id}"/>">  
  
   <c:choose>
    <c:when test="${currRow.bean.scheduledDatePast}">
      <tr valign="top" bgcolor="#FFFF80">  
    </c:when>
    <c:otherwise>
    <tr valign="top">  
   </c:otherwise>   
   </c:choose>    
      <td class="table_cell_left" width="230px"><c:out value="${currRow.bean.studySubjectLabel}"/></td>

      <td class="table_cell">
		  <cc-fmt:formatDate value="${currRow.bean.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
      </td>

      <td class="table_cell"><c:out value="${currRow.bean.subjectEventStatus.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr>
		<td>
        <a href="EnterDataForStudyEvent?eventId=<c:out value="${currRow.bean.id}"/>"
		onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
		onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img 
		name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		
		</td>
		</tr>
		</table>
      </td>          
    <c:set var="count" value="${count+1}"/>
   </tr>
