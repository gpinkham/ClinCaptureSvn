<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.StudyRow" />
<c:set var="userRoleId" value="${param.userRoleId}" />

<c:choose>
  <c:when test="${currRow.bean.status.available}">
    <c:set var="className" value="aka_green_highlight"/>
  </c:when>
  <c:when test="${currRow.bean.status.deleted || currRow.bean.status.locked}">
    <c:set var="className" value="aka_red_highlight"/>
  </c:when>
  <c:otherwise>
		<c:set var="className" value=""/>
	</c:otherwise>
</c:choose>

<tr valign="top">   
      <td class="table_cell_left"><c:out value="${currRow.bean.name}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.identifier}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.oid}"/></td>           
      <td class="table_cell"><c:out value="${currRow.bean.principalInvestigator}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.facilityName}"/>&nbsp;</td>         
      <td class="table_cell">
          <cc-fmt:formatDate value="${currRow.bean.createdDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
      </td>
      <td class="table_cell <c:out value='${className}'/>"><c:out value="${currRow.bean.status.name}"/></td>
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
		<tr class="innerTable">
		 <td><a href="ViewSite?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			data-cc-siteId="${currRow.bean.id}"
			onclick="setAccessedObjected(this);"><img 
			name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		</td>
	  <c:if test="${readOnly != 'true' && userRoleId ne 10}">	       
      <c:choose>
       <c:when test="${!currRow.bean.status.deleted and !currRow.bean.status.locked and (currRow.bean.origin == 'gui' || userBean.name == 'root')}">
          <c:if test="${!study.status.locked}">
          <td><a href="InitUpdateSubStudy?id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
			onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
			onclick="setAccessedObjected(this);"><img
			name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit" bundle="${resword}"/>" title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a>
		  </td>
          <td>
              <c:choose>
                  <c:when test="${currRow.bean.id ne study.id}">
                      <a href="RemoveSite?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
                         onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                         onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                         onclick="setAccessedObjected(this);"><img
                         name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
                  </c:when>
                  <c:otherwise>&nbsp;</c:otherwise>
              </c:choose>
		  </td>
         </c:if>
       </c:when>
       <c:otherwise>
        <c:if test="${currRow.bean.id ne study.id and !currRow.bean.status.locked and !(study.status.deleted || study.status.locked)}">
		<td><img name="bt_Transparent" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
        <td><a href="RestoreSite?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
			onclick="setAccessedObjected(this);"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
		 </td>
         </c:if>  
       </c:otherwise>
      </c:choose>
      </c:if>
      <c:if test="${currRow.bean.id ne study.id and (userRoleId eq 1 or userRoleId eq 2)}">
          <c:choose>
              <c:when test="${!currRow.bean.status.deleted and currRow.bean.showUnlockEventsButton}">
                  <td><img name="bt_Transparent" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
                  <td><img name="bt_Transparent" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
                  <td>
                      <a href="LockSite?id=${currRow.bean.id}&action=unlock" onclick="setAccessedObjected(this);"><img src="images/bt__Unlock.png" border="0" align="left" alt="<fmt:message key="unlockSiteStudySubjects" bundle="${resword}"/>" title="<fmt:message key="unlockSiteStudySubjects" bundle="${resword}"/>" hspace="4"/></a>
                  </td>
              </c:when>
              <c:when test="${!currRow.bean.status.deleted and currRow.bean.showLockEventsButton}">
                  <td>
                      <a href="LockSite?id=${currRow.bean.id}&action=lock" onclick="setAccessedObjected(this);"><img src="images/bt__Lock.png" border="0" align="left" alt="<fmt:message key="lockSiteStudySubjects" bundle="${resword}"/>" title="<fmt:message key="lockSiteStudySubjects" bundle="${resword}"/>" hspace="4"/></a>
                  </td>
              </c:when>
              <c:otherwise>
                  <td><img name="bt_Transparent" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
              </c:otherwise>
          </c:choose>
      </c:if>
      </tr>
      </table>
      </td>
   </tr>