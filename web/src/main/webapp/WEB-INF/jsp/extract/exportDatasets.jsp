<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>

<jsp:include page="../include/extract-refresh-header.jsp"/>
<script type="text/javascript">
    var openDataExtractLink = true;
    function openDoc(inURL) {
        if(openDataExtractLink){
            openDataExtractLink = false;
            openDocWindow(inURL);
        }
    }
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="dataset" class="org.akaza.openclinica.bean.extract.DatasetBean"/>
<jsp:useBean scope="request" id="filelist" class="java.util.ArrayList"/>
<h1>
	<span class="first_level_header">
		<fmt:message key="extract_data" bundle="${resword}"/>: <c:out value="${dataset.name}"/> 
		<a href="javascript:openDocWindow('help/4_3_exportDatasets_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>


<a href="javascript:leftnavExpand('section1');">
	<img id="excl_section1" src="images/bt_Collapse.gif" border="0"> 
	<span class="table_title_Admin">
		<fmt:message key="dataset_details" bundle="${resword}"/>  
	</span>
</a>
<div id="section1" style="display: "><div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center" style="padding:0px 6px 8px 0px">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top" >
			<td class="table_header_column">
				<fmt:message key="dataset_name" bundle="${resword}"/>:
			</td>
			<td class="table_cell">
				<c:out value="${dataset.name}"/>
			</td>
		</tr>
		<tr valign="top" >
			<td class="table_header_column">
				<fmt:message key="dataset_description" bundle="${resword}"/>:
			</td>
			<td class="table_cell">
				<c:out value="${dataset.description}"/>
			</td>
		</tr>
		<tr valign="top">
			<td class="table_header_column">
				<fmt:message key="item_status" bundle="${resword}"/>:
			</td>
			<td class="table_cell"> 
				<c:out value="${dataset.datasetItemStatus.description}"/>
			</td>
		</tr>
	</table>
</div>

</div></div></div></div></div></div></div></div>
</div></div><br>

<a href="javascript:leftnavExpand('section2');">
	<img id="excl_section2" src="images/bt_Collapse.gif" border="0"> 
	<span class="table_title_Admin">
		<fmt:message key="extract_formats" bundle="${resword}"/>  
	</span>
</a>
<div id="section2" style="display: ">
	<form name="export_form" id="extract_format_form" action="pages/extract" method=GET>
		<input type="hidden" name="datasetId" value="<c:out value="${dataset.id}"/>"/>
		<table border="0" cellpadding="5" width="525" style="margin:2px 0px 2px 10px">
			<c:forEach var="extract" items="${extractProperties}">
				<tr valign="top">
					    <td style="padding:0px">
							<%-- use fn:startsWith(extract.filedescription, '&') here, for i18n --%>
							<input type="radio" name="id" value="<c:out value="${extract.id}"/>"  
								<c:choose><c:when test="${extract.id=='1'}">checked</c:when><c:otherwise></c:otherwise></c:choose>>
								<c:choose>
									<c:when test="${fn:startsWith(extract.filedescription, '&')==true}">
										<fmt:message key="${fn:substringAfter(extract.filedescription, '&')}" bundle="${restext}"/>&nbsp;
									</c:when>
									<c:otherwise>
										<c:out value="${extract.filedescription}"/>&nbsp;
									</c:otherwise>
								</c:choose>								
							</input>
						</td>
					</tr>
		    </c:forEach>
		</table>
	</form>
</div><br>

<%--<table border="0" cellpadding="5" width="525" style="margin:15px 10px"><tbody>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="1" checked>CDISC ODM XML 1.3 Full with ClinCapture extensions&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="2">CDISC ODM XML 1.3 Clinical Data with ClinCapture extensions&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="3">CDISC ODM XML 1.3 Clinical Data&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="4">CDISC ODM XML 1.2 Clinical Data with ClinCapture extensions&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="5">CDISC ODM XML 1.2 Clinical Data&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="6">View as HTML&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="7">Excel Spreadsheet&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="8">Tab-delimited Text&nbsp;
	</td></tr>
	<tr valign="top"><td style="padding:0px">			
		<input type="radio" name="id" value="9">SPSS data and syntax&nbsp;
	</td></tr>
</tbody></table>--%>

<a href="javascript:leftnavExpand('section3');">
	<img id="excl_section3" src="images/bt_Collapse.gif" border="0"> 
	<span class="table_title_Admin">
		<fmt:message key="extracted_files" bundle="${resword}"/>  
	</span>
</a>
<div id="section3" style="display: ">
	<c:import url="../include/showTable.jsp">
		<c:param name="rowURL" value="showArchivedDatasetFileRow.jsp" />
	</c:import>
</div>

<table>
	<tr>
		<td>
	        <input type="button" onclick="javascript:window.location.href='ViewDatasets'" name="exit" value="<fmt:message key="exit" bundle="${resword}"/>" class="button_medium"/>
	    </td>
	    <td>
	         <input class="button_medium" type="button" name="BTN_Submit" value="<fmt:message key="extract_data" bundle="${resword}"/>" onclick="javascript:document.forms['extract_format_form'].submit()"/>
	    </td>
	</tr>
</table>

<jsp:include page="../include/footer.jsp"/>
