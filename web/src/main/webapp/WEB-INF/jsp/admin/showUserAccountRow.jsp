<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<c:set var="totalRoles" value="${fn:length(currRow.bean.roles)}"/>

<tr valign="top" bgcolor="#F5F5F5">
	<td class="table_cell_left">
		<c:choose>
			<c:when test='${currRow.bean.status.deleted}'>
				<font color='gray'><c:out value="${currRow.bean.name}" /></font>
			</c:when>
			<c:otherwise>
				<c:out value="${currRow.bean.name}" />
			</c:otherwise>
		</c:choose>
	</td>
    <td class="table_cell">
        <c:choose>
            <c:when test="${currRow.bean.sysAdmin}">
                <fmt:message key="administrator" bundle="${resword}"/>
            </c:when>
            <c:otherwise>
                <fmt:message key="user" bundle="${resword}"/>
            </c:otherwise>
        </c:choose>
    </td>
	<td class="table_cell"><c:out value="${currRow.bean.firstName}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.lastName}" /></td>
	<td class="table_cell"><c:out value="${currRow.bean.status.name}" /></td>
	
	<%-- ACTIONS --%>
	<td class="table_cell">
	 <table border="0" cellpadding="0" cellspacing="0">
	 <tr class="innerTable">
		<c:choose>
		<c:when test='${(currRow.bean.techAdmin && !(userBean.techAdmin))}'>
			</c:when>
			<c:otherwise>
				<c:choose>
				<c:when test='${currRow.bean.status.deleted}'>
                    <c:set var="confirmQuestion">
					  <fmt:message key="are_you_sure_you_want_to_restore" bundle="${resword}">
					    <fmt:param value="${currRow.bean.name}"/>
					  </fmt:message>
					</c:set>
					
					<c:set var="onClick" value="setAccessedObjectWithMultipleRows(this); return confirmDialog({ message: '${confirmQuestion}', height: 150, width: 500, aLink: this });"/>
					<%-- <img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a>
					<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a>
					<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a> --%>
					<td><a href="DeleteUser?action=4&userId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />"
					onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
				    onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
				    data-cc-userId="${currRow.bean.id}"
				    data-cc-rowCount="${totalRoles}">	<img name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
				   	</td>
			</c:when>
			<c:otherwise>
				<td><a href="ViewUserAccount?userId=<c:out value="${currRow.bean.id}"/>"
					onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
					onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
					onclick="setAccessedObjectWithMultipleRows(this)"><img name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a></td>
				<td><a href="pages/EditUserAccount?userId=<c:out value="${currRow.bean.id}"/>"
					onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
					onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
					data-cc-userId="${currRow.bean.id}"
					data-cc-rowCount="${totalRoles}"
					onclick="setAccessedObjectWithMultipleRows(this)"><img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit" bundle="${resword}"/>" title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a></td>
			    <c:if test="${currRow.bean.name ne 'root'}">
                    <td><a href="SetUserRole?action=confirm&userId=<c:out value="${currRow.bean.id}"/>"
                      onMouseDown="javascript:setImage('bt_SetRole1','images/bt_SetRole_d.gif');"
                      onMouseUp="javascript:setImage('bt_SetRole1','images/bt_SetRole.gif');"
                      onclick="setAccessedObjectWithMultipleRows(this)"><img
                      name="bt_SetRole1" src="images/bt_SetRole.gif" border="0" alt="<fmt:message key="set_role" bundle="${resword}"/>" title="<fmt:message key="set_role" bundle="${resword}"/>" align="left" hspace="6"></a>
                    </td>
                </c:if>
				<c:set var="confirmQuestion">
				 <fmt:message key="are_you_sure_you_want_to_remove" bundle="${resword}">
				   <fmt:param value="${currRow.bean.name}"/>
				 </fmt:message>
				</c:set> 
				
				<c:set var="onClick" value="setAccessedObjectWithMultipleRows(this); return confirmDialog({ message: '${confirmQuestion}', height: 150, width: 500, aLink: this });"/>
                <c:choose>
                    <c:when test="${currRow.bean.name eq 'root'}"></c:when>
                    <c:when test="${currRow.bean.id eq userBean.id}"></c:when>
                    <c:otherwise>
                        <td><a href="DeleteUser?action=3&userId=<c:out value="${currRow.bean.id}"/>" onClick="<c:out value="${onClick}" />"
                               onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                               onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');">
                            <img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
                            </td>
                    </c:otherwise>
                </c:choose>
				<c:if test='${currRow.bean.status.locked}'>
				<td><a href="UnLockUser?userId=<c:out value="${currRow.bean.id}"/>"
                    onMouseDown="javascript:setImage('bt_Unlock1','images/bt_Unlock.gif');"
                    onMouseUp="javascript:setImage('bt_Unlock1','images/bt_Unlock.gif');"
                    onclick="setAccessedObjectWithMultipleRows(this)"><img name="bt_Unlock1" src="images/bt_Unlock.gif" border="0" alt="<fmt:message key="unlock" bundle="${resword}"/>" title="<fmt:message key="unlock" bundle="${resword}"/>" align="left" hspace="6"></a>
                </td>
                </c:if>
			</c:otherwise>
			</c:choose>			
			
			</c:otherwise>
		</c:choose>	
		</tr>
		</table>
	</td>
</tr>
<c:choose>
	<c:when test="${empty currRow.bean.roles}">
		<tr valign="top">
			<td class="table_cell_left">&nbsp;</td>
            <td class="table_cell_left">&nbsp;</td>
			<td class="table_cell" colspan="3"><i><fmt:message key="no_roles_assigned" bundle="${resword}"/></i></td>
			<td class="table_cell">&nbsp;</td>
		</tr>
	</c:when>
	<c:otherwise>
		<c:forEach var="sur" items="${currRow.bean.roles}">
			<c:choose>
				<c:when test='${sur.studyName != ""}'>
					<c:set var="study" value="${sur.studyName}" />
				</c:when>
				<c:otherwise>
					<c:set var="study" value="Study ${sur.studyId}" />				
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test='${sur.status.deleted}'>
					<c:set var="actionName" >
						<fmt:message key="restore" bundle="${resword}"/>
					</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="actionName">
						<fmt:message key="remove" bundle="${resword}"/>
					</c:set>
				</c:otherwise>
			</c:choose>
			<c:set var="confirmQuestion"> 
			<fmt:message key="are_you_want_to_the_role_for" bundle="${restext}">
				<fmt:param value="${actionName}"/>
				<fmt:param value="${sur.role.description}"/>
				<fmt:param value="${study}"/>
			</fmt:message>
			</c:set>
			<c:set var="onClick" value="setAccessedObjectWithMultipleRows(this); return confirmDialog({ message: '${confirmQuestion}', height: 150, width: 500, aLink: this });"/>
			<tr valign="top">
				<td class="table_cell_left">&nbsp;</td>
                <td class="table_cell_left">&nbsp;</td>
				<td class="table_cell" colspan="3" >
					<c:if test='${sur.status.deleted}'>
						<font color='gray'>
					</c:if>
                    <c:if test="${sur.studyId > 0}">
                        <c:choose>
                            <c:when test='${sur.studyName != ""}'><c:out value="${sur.studyName}" /></c:when>
                            <c:otherwise>Study <c:out value="${sur.studyId}" /></c:otherwise>
                        </c:choose>
                        -
                    </c:if>
					<fmt:message key="${roleMap[sur.role.id] }" bundle="${resterm}"></fmt:message>

					<c:if test='${sur.status.deleted}'>
						</font>
					</c:if>
				</td>
				<td class="table_cell">
					<c:if test='${!sur.status.deleted}'>
                        <c:choose>
                            <c:when test="${sur.role.id eq 1}">
                                <img name="spaceIcon" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
                                <img name="spaceIcon" src="images/bt_Transparent.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
                            </c:when>
                            <c:otherwise>
                                <img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
                                <a href="EditStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userName=<c:out value="${currRow.bean.name}"/>"
                                   onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
                                   onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
                                   data-cc-userId="${currRow.bean.id}_${sur.studyId}"
                                   onclick="setAccessedObjectWithMultipleRows(this);"><img name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit" bundle="${resword}"/>" title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a>
                            </c:otherwise>
                        </c:choose>
					</c:if>
					<c:if test="${not currRow.bean.status.deleted and not ((currRow.bean.name eq userBean.name) and (sur.studyId eq studyId || sur.studyId eq parentStudyId))}">
					<div><c:out value="${currentStudy.id}"/></div>
						<c:choose>
							<c:when test='${sur.status.deleted}'>
								<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
								<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
								<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
								<a href="DeleteStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userId=<c:out value="${currRow.bean.id}"/>&action=4" onClick="<c:out value="${onClick}" />"
									onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
						    		onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');">
						   			<img name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
							</c:when>
							<c:otherwise>
                        		<c:if test="${(totalRoles - userRolesRemovedCountMap[currRow.bean.name]) > 1 and userBean.sysAdmin and not (sur.role.id eq 1)}">
                        			<img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
                        			<a href="DeleteStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userId=<c:out value="${currRow.bean.id}"/>&action=6" onClick="<c:out value="${onClick}" />"
                        				onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                           				onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');">
                            			<img name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
                        		</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${(sur.status.deleted or (totalRoles - userRolesRemovedCountMap[currRow.bean.name]) > 1) and userBean.sysAdmin and not (sur.role.id eq 1)}">
					
							<c:set var="actionName">
								<fmt:message key="delete" bundle="${resword}"/>
							</c:set>
							<c:set var="confirmQuestion"> 
								<fmt:message key="are_you_want_to_the_role_for" bundle="${restext}">
									<fmt:param value="${actionName}"/>
									<fmt:param value="${sur.role.description}"/>
									<fmt:param value="${study}"/>
								</fmt:message>
							</c:set>
							<c:set var="onClick" value="setAccessedObjectWithMultipleRows(this); return confirmDialog({ message: '${confirmQuestion}', height: 150, width: 500, aLink: this });"/>
					
                        	<a href="DeleteStudyUserRole?studyId=<c:out value="${sur.studyId}" />&userId=<c:out value="${currRow.bean.id}"/>&action=3" onClick="<c:out value="${onClick}" />"
                        		onMouseDown="javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');"
                           		onMouseUp="javascript:setImage('bt_Delete1','images/bt_Delete.gif');">
                            	<img name="bt_Delete1" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"></a>
                   
                    	</c:if>
                    </c:if>
				</td>
			</tr>
		</c:forEach>
	</c:otherwise>
</c:choose>
