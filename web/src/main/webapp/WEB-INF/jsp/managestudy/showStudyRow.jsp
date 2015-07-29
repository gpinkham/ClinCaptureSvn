<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.DisplayStudyRow" />
<c:choose>
  <c:when test="${currRow.bean.parent.status.available}">
    <c:set var="className" value="aka_green_highlight"/>
  </c:when>
  <c:when test="${currRow.bean.parent.status.deleted || currRow.bean.parent.status.locked}">
    <c:set var="className" value="aka_red_highlight"/>
  </c:when>
  <c:otherwise>
		<c:set var="className" value=""/>
	</c:otherwise>
</c:choose>
<tr valign="top" bgcolor="#F5F5F5">
      <td class="table_cell_left"><b><c:out value="${currRow.bean.parent.name}"/></b></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.identifier}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.oid}"/></td>
      <td class="table_cell"><c:out value="${currRow.bean.parent.principalInvestigator}"/></td>  
      <td class="table_cell"><c:out value="${currRow.bean.parent.facilityName}"/>&nbsp;</td> 
      <td class="table_cell">
		  <cc-fmt:formatDate value="${currRow.bean.parent.createdDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
      <td class="table_cell <c:out value='${className}'/>"><c:out value="${currRow.bean.parent.status.name}"/></td> 
      <td class="table_cell">
       <table border="0" cellpadding="0" cellspacing="0">
	    <tr class="innerTable">
	      <td><a href="ViewStudy?id=<c:out value="${currRow.bean.parent.id}"/>&viewFull=yes"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			data-cc-studyId="${currRow.bean.parent.id}"
			onclick="setAccessedObjected(this);"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		  </td>
          <c:choose>
          <c:when test="${!currRow.bean.parent.status.deleted}">
          <td>
            <c:choose>
                <c:when test="${(study.parentStudyId > 0 && study.parentStudyId == currRow.bean.parent.id) || (study.id == currRow.bean.parent.id)}">
                    <a href="#" onclick="alertDialog({message: '<fmt:message key="you_are_trying_to_remove_the_current_study" bundle="${resword}"/>', height: 150, width: 500 }); setAccessedObjected(this);"/>
                    <img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6">
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="RemoveStudy?action=confirm&id=<c:out value="${currRow.bean.parent.id}"/>"
                       onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                       onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                       onclick="setAccessedObjected(this);">
                        <img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6">
                    </a>
                </c:otherwise>
            </c:choose>
		 </td>
          </c:when>
         <c:otherwise>
          <td><a href="RestoreStudy?action=confirm&id=<c:out value="${currRow.bean.parent.id}"/>"
			onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
			onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
			onclick="setAccessedObjected(this);"><img 
			name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
		 </td>          
         </c:otherwise>
         </c:choose>
	    </tr>
	   </table>       
     </td>   
   </tr>
   <c:forEach var="child" items="${currRow.bean.children}">
     <%-- color-coded statuses...--%>
   <c:choose>
	  <c:when test="${child.status.available}">
	    <c:set var="className" value="aka_green_highlight"/>
	  </c:when>
	  <c:when test="${child.status.deleted || child.status.locked}">
	    <c:set var="className" value="aka_red_highlight"/>
	  </c:when>
	  <c:otherwise>
			<c:set var="className" value=""/>
		</c:otherwise>
	</c:choose>
    <tr>
      <td class="table_cell_left"><div class="homebox_bullets"><c:out value="${child.name}"/></div></td>
      <td class="table_cell"><c:out value="${child.identifier}"/></td>
      <td class="table_cell"><c:out value="${child.oid}"/></td>            
      <td class="table_cell"><c:out value="${child.principalInvestigator}"/></td>  
      <td class="table_cell"><c:out value="${child.facilityName}"/>&nbsp;</td> 
      <td class="table_cell">
		  <cc-fmt:formatDate value="${child.createdDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
      <td class="table_cell <c:out value='${className}'/>"><c:out value="${child.status.name}"/></td>
      <td class="table_cell">
        <table border="0" cellpadding="0" cellspacing="0">
	    <tr class="innerTable">
	     <td>
	      <a href="ViewSite?id=<c:out value="${child.id}"/>"
			onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
			onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
			data-cc-studyId="${currRow.bean.parent.id}_${child.id}"
			onclick="setAccessedObjected(this);"><img 
		    name="bt_View1" src="images/bt_View.gif" border="0"  alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
		  </td>
          <td>&nbsp;</td>  
        </tr>
	   </table>
      </td>
     </tr>
     </c:forEach>
