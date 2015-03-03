<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="annotations" class="java.lang.String" />

<c:set var="curCategory" value="" />

<c:forEach var="se" items="${allSections}" >

	<c:set var="displayItemNum" value="${0}" />
	<c:set var="numOfTr" value="0"/>
	<c:set var="newSection" value="true" />

	<c:forEach var="displayItem" items="${se.items}" varStatus="itemStatus">
		<c:if test="${newSection}">
			<div style="width:100%">
			<!-- These DIVs define shaded box borders -->
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="tablebox_center">
		
			<!-- Table Contents -->
				<table border="0" cellpadding="0" cellspacing="0" width="100%">  
					<tr>
						<td class="table_header_row_left" style="background-color: #ECECEC;">
							<table border="0" cellpadding="0" cellspacing="0" width="100%">
								<tr>
									<td width="5%" nowrap><b><fmt:message key="section" bundle="${resword}"/>:</b></td>
									<td width="75%" style="padding-left: 12px;">
										<b>
										<c:if test="${se.section.parent.active}">
											<ui:escape content="${se.section.parent.title}" tagNames="script"/> &gt;
										</c:if>
										<ui:escape content="${se.section.title}" tagNames="script"/>
										</b>
									</td>
									<c:if test="${not empty displayItem.metadata.pageNumberLabel}">
										<td rowspan="2" width="20%" align="right" valign="top">
											<fmt:message key="page" bundle="${resword}"/>: <b><c:out value="${displayItem.metadata.pageNumberLabel}" /></b>
										</td>
									</c:if>
								</tr>
								<c:if test='${se.section.subtitle != ""}'>
			   						<tr>
										<td width="5%" nowrap><b><fmt:message key="subtitle" bundle="${resword}"/>:</b></td>
										<td width="75%" style="padding-left: 12px;"> <ui:escape content="${se.section.subtitle}" tagNames="script"/> </td>
									</tr>
								</c:if>
								<c:if test='${se.section.instructions != ""}'>
									<tr>
										<td width="5%" nowrap><b><fmt:message key="instructions" bundle="${resword}"/>:</b></td>
										<td width="75%" style="padding-left: 12px;"> <ui:escape content="${se.section.instructions}" tagNames="script"/> </td>
									</tr>
								</c:if>
							</table>
						</td>
					</tr>
		
					<c:set var="newSection" value="false" />
		</c:if>
	
		<%-- SHOW THE PARENT FIRST --%>
		<c:if test="${displayItem.metadata.parentId == 0}">
	
	  		<c:if test="${!empty displayItem.metadata.header}">
	     		<tr style="background-color: #ECECEC;">
		    		<td class="table_cell_left"><ui:escape content="${displayItem.metadata.header}" tagNames="script"/></td>
	      		</tr>
	  		</c:if>
	  		
	  		<c:if test="${!empty displayItem.metadata.subHeader}">
	   			<tr style="background-color: #ECECEC;">
		  			<td class="table_cell_left"><ui:escape content="${displayItem.metadata.subHeader}" tagNames="script"/></td>
	   			</tr>
	  		</c:if>

			<!--ACCORDING TO COLUMN NUMBER, ARRANGE QUESTIONS IN THE SAME LINE-->

			<c:if test="${displayItem.metadata.columnNumber <=1}">
				<c:set var="numOfTr" value="${numOfTr+1}"/>
	    			<tr>
	    				<td class="table_cell">
	      					<table border="0" width="100%">
             					 <tr>
                					<td valign="top">
			</c:if>
	  			

			<c:if test="${displayItem.metadata.columnNumber >1}">
      			<td valign="top">
    		</c:if>
    
			<table border="0">
				<tr>
					<td valign="top"><c:out value="${displayItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
					<td valign="top"><ui:escape content="${displayItem.metadata.leftItemText}" tagNames="script"/></td>
					<td valign="top">
						<%-- display the HTML input tag --%>
						<c:set var="displayItem" scope="request" value="${displayItem}" />
						<c:import url="../submit/showItemInputPrint.jsp" />
						<br /><c:import url="../showMessage.jsp"><c:param name="key" value="input${displayItem.item.id}" /></c:import>
					</td>
					<c:if test='${displayItem.item.units != ""}'>
					<td valign="top">
						<c:out value="(${displayItem.item.units})" />
					</td>
					</c:if>
					<td valign="top"><ui:escape content="${displayItem.metadata.rightItemText}" tagNames="script"/></td>
				</tr>
			</table>
		
			</td>

			<c:if test="${displayItem.numChildren > 0}">
				<tr>
					<%-- NOW SHOW THE CHILDREN --%>

					<td class="table_cell">
						<table border="0">
							<c:set var="notFirstRow" value="${0}" />
							<c:forEach var="childItem" items="${displayItem.children}">

								<c:set var="currColumn" value="${childItem.metadata.columnNumber}" />
								<c:if test="${currColumn == 1}">
									<c:if test="${notFirstRow != 0}">
										</tr>
									</c:if>
									<tr>
									<c:set var="notFirstRow" value="${1}" />
									<%-- indentation --%>
		          					<td valign="top">&nbsp;</td>
								</c:if>
								<%--
									this for loop "fills in" columns left blank
									e.g., if the first childItem has column number 2, and the next one has column number 5,
									then we need to insert one blank column before the first childItem, and two blank columns between the second and third children
								--%>
								<c:forEach begin="${currColumn}" end="${childItem.metadata.columnNumber}">
									<td valign="top">&nbsp;</td>
								</c:forEach>

								<td valign="top">
									<table border="0">
										<tr>
											<td valign="top"><c:out value="${childItem.metadata.questionNumberLabel}" escapeXml="false"/></td>
											<td valign="top"><ui:escape content="${displayItem.metadata.leftItemText}" tagNames="script"/></td>
											<td valign="top">
												<%-- display the HTML input tag --%>
												<c:set var="displayItem" scope="request" value="${childItem}" />
												<c:import url="../submit/showItemInputPrint.jsp" />
												<br /><c:import url="../showMessage.jsp"><c:param name="key" value="input${childItem.item.id}" /></c:import>
											</td>
								<c:if test='${childItem.item.units != ""}'>
									<td valign="top"> <c:out value="(${childItem.item.units})" /> </td>
								</c:if>
								<td valign="top"> <ui:escape content="${displayItem.metadata.rightItemText}" tagNames="script"/> </td>
								</tr>
								</table>
								</td>
							</c:forEach>
						</tr>
					</table>
				</td>
			</tr>
			</c:if>
	
			<c:if test="${itemStatus.last or ((se.items)[itemStatus.index + 1].metadata.columnNumber <=1)}">
	  			 </tr>
       				</table>
      					 </td>
	   						</tr>
			</c:if>
		
			<c:if test="${itemStatus.last or (displayItem.metadata.pageNumberLabel ne (se.items)[itemStatus.index + 1].metadata.pageNumberLabel)}">
				</table>
				<!-- End Table Contents -->

				</div>
				</div></div></div></div></div></div></div></div>
				</div>
		
				<c:set var="newSection" value="true" />
			</c:if>	
		
		</c:if>
		<c:set var="displayItemNum" value="${displayItemNum + 1}" />
	</c:forEach>

</c:forEach>
