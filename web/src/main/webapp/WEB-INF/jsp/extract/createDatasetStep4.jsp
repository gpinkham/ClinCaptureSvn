<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:include page="../include/extract-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<c:if test="${newDataset.id>0}">
			<div class="sidebar_tab_content">
				<p><fmt:message key="enter_dataset_properties_be_descriptive" bundle="${restext}"/>
					<fmt:message key="name_description_required" bundle="${restext}"/></p>
				<p><fmt:message key="copy_dataset_by_change_name" bundle="${restext}"/></p>
			</div>
		</c:if>
	</td>
</tr>

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<c:set var="dsName" value="${newDataset.name}" />
<c:set var="dsDesc" value="${newDataset.description}" />
<c:set var="itemStatusId" value="${newDataset.datasetItemStatus.id}"/>
<c:set var="dsStatusId" value="${0}" />
<c:set var="mdvOID" value="${newDataset.odmMetaDataVersionOid}"/>
<c:set var="mdvName" value="${newDataset.odmMetaDataVersionName}"/>
<c:set var="mdvPrevStudy" value="${newDataset.odmPriorStudyOid}"/>
<c:set var="mdvPrevOID" value="${newDataset.odmPriorMetaDataVersionOid}"/>

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "dsName"}'>
		<c:set var="dsName" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsDesc"}'>
		<c:set var="dsDesc" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "dsStatusId"}'>
		<c:set var="dsStatusId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "mdvOID"}'>
		<c:set var="mdvOID" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvName"}'>
		<c:set var="mdvName" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevStudy"}'>
		<c:set var="mdvPrevStudy" value="${presetValue.value}"/>
	</c:if>
	<c:if test='${presetValue.key == "mdvPrevOID"}'>
		<c:set var="mdvPrevOID" value="${presetValue.value}"/>
	</c:if>
</c:forEach>

<c:choose>
	<c:when test="${newDataset.id>0}">
		<h1>
			<span class="first_level_header">
				<fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="specify_dataset_properties" bundle="${resword}"/>
				<a href="javascript:openDocWindow('help/4_7_editDataset_Help.html#step3')">
				<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>"
				title="<fmt:message key="help" bundle="${resword}"/>"></a>
				: <c:out value="${newDataset.name}"/>
			</span>
		</h1>
	</c:when>
	<c:otherwise>
		<h1>
			<span class="first_level_header">
				<fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="specify_dataset_properties" bundle="${resword}"/>
				<a href="javascript:openDocWindow('help/4_2_createDataset_Help.html#step4')">
				<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>"
				title="<fmt:message key="help" bundle="${resword}"/>"></a>
			</span>
		</h1>
	</c:otherwise>
</c:choose>

<c:if test="${newDataset.id<=0}">
	<fmt:message key="enter_dataset_properties_be_descriptive" bundle="${restext}"/> <fmt:message key="name_description_required" bundle="${restext}"/>
</c:if>

<form id="datasetForm" action="CreateDataset" method="post">

	<input type="hidden" name="action" value="specifysubmit"/>

	<table>

		<tr>
			<td><fmt:message key="name" bundle="${resword}"/>:</td>
			<td>
				<input type="text" name="dsName" size="30" value="<c:out value='${dsName}' />"/>
				<span class="asterisk">*</span><br>
				<jsp:include page="../showMessage.jsp">
					<jsp:param name="key" value="dsName"/>
				</jsp:include>
			</td>
		</tr>

		<tr>
			<td><fmt:message key="description" bundle="${resword}"/>:</td>
			<td><textarea name="dsDesc" cols="40" rows="4"><c:out value="${dsDesc}"/></textarea>
				<span class="asterisk">*</span><br>
				<jsp:include page="../showMessage.jsp">
					<jsp:param name="key" value="dsDesc"/>
				</jsp:include>
			</td>
		</tr>

		<tr>
			<td><fmt:message key="item_status" bundle="${resword}"/>:</td>
			<td>
				<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B">
				<div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
					<div class="tablebox_center" align="center">
						<table border="0" cellpadding="0" cellspacing="0" width="100%">
							<c:choose>
								<c:when test='${itemStatusId eq null or itemStatusId eq "1"}'>

									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1" checked></td>
										<td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2"></td>
										<td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3"></td>
										<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
								</c:when>
								<c:when test='${itemStatusId eq "2"}'>

									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1"></td>
										<td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2" checked></td>
										<td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3"></td>
										<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
								</c:when>
								<c:when test='${itemStatusId eq "3"}'>

									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="1"></td>
										<td class="table_cell"><fmt:message key="completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="2"></td>
										<td class="table_cell"><fmt:message key="non_completed_items" bundle="${resterm}"/></td></tr>
									<tr><td class="table_cell"><input type="radio" name="itemStatus" value="3" checked></td>
										<td class="table_cell"><fmt:message key="completed_and_non_completed_items" bundle="${resterm}"/></td></tr>
								</c:when>
							</c:choose>
						</table>
					</div>
				</div></div></div></div></div></div></div></div>
			</td>
		</tr>

		<tr>
			<td colspan="2">
				<br><br><br><br>
				 <fmt:message key="long_note1" bundle="${restext}"/>
				 <fmt:message key="long_note2" bundle="${restext}"/>
				 <fmt:message key="long_note3" bundle="${restext}"/>
				 <fmt:message key="long_note4" bundle="${restext}"/>
				 <fmt:message key="long_note5" bundle="${restext}"/>
				 <fmt:message key="long_note6" bundle="${restext}"/>
				<br><br>
			</td>
			<br><br>
		</tr>

		<tr>
			<td><fmt:message key="metadataversion_ODM_ID" bundle="${resword}"/>:          </td>
			<td>
				<input type="text" name="mdvOID" size="25" value="<c:out value='${mdvOID}' />"/>
				<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvOID"/></jsp:include>
			</td>
		</tr>

		<tr>
			<td><fmt:message key="metadataversion_name" bundle="${resword}"/>:            </td>
			<td>
				<input type="text" name="mdvName" size="25" value="<c:out value='${mdvName}' />"/>
				<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvName"/></jsp:include>
			</td>
		</tr>

		<tr>
			<td><fmt:message key="previous_study_ODM_ID" bundle="${resword}"/>:            </td>
			<td>
				<input type="text" name="mdvPrevStudy" size="25" value="<c:out value='${mdvPrevStudy}' />"/>
				<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevStudy"/></jsp:include>
			</td>
		</tr>

		<tr>
			<td><fmt:message key="previous_metadataversion_ODM_ID" bundle="${resword}"/>:   </td>
			<td>
				<input type="text" name="mdvPrevOID" size="25" value="<c:out value='${mdvPrevOID}' />"/>
				<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="mdvPrevOID"/></jsp:include>
			</td>
		</tr>

		<tr>
			<td class="text" colspan="2" align="left">
				<input type="hidden" name="dsStatus" value="1"/>

				<table>
					<tr>
						<td colspan="3" align="left">
							<input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50"
								onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_beginsubmit');"/>
						</td>
						<td>
							<input type="submit" id="btnSubmit" name="btnSubmit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium medium_continue"
								onclick=" return checkMetaDataVersionODMID($.trim($('[name=mdvOID]').val()),
								'<fmt:message key="datasetConfirmIncludeDescriptorsDisabled" bundle="${restext}"/>',
								document.forms['datasetForm'], ${study.parentStudyId eq 0});" />
						</td>
						<td>
							<input type="button" onclick="confirmCancel('ViewDatasets');" name="cancel"
								   value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium medium_cancel"/>
						</td>
					</tr>
				</table>

			</td>
		</tr>

	</table>

</form>

<br><br>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
