<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext" />
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />

<jsp:include page="../include/managestudy_top_pages.jsp" />

<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jspfunctions.js?r=${revisionNumber}'/>"></script>

<jsp:include page="../include/sideAlert.jsp" />
<link rel="stylesheet" href="/includes/styles.css?r=${revisionNumber}" type="text/css">
<link rel="stylesheet" href="<c:url value='/includes/jmesa/jmesa.css?r=${revisionNumber}'/>" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jmesa.js?r=${revisionNumber}'/>"></script>
<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery.jmesa.js?r=${revisionNumber}'/>"></script>

<script type="text/javascript">

  function selectAll(isSelect) {
    $("input[name='oids']").each(function() {
      this.checked = isSelect;
    });
  }

  function onInvokeAction(id) {
    createHiddenInputFieldsForLimitAndSubmit(id);
  }

  function sendSubjectOids() {
    var list = $("input[name=oids]:checked").map(function () {return $(this).attr("ssoid");}).get().join(",")
    var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];
    if($("#sidebar_Alerts_open").css("display") == 'none') {
      leftnavExpand('sidebar_Alerts_open');
      leftnavExpand('sidebar_Alerts_closed');
    }
    $("#sidebar_Alerts_open .sidebar_tab_content").html('')
            .append("<div class='alert'><fmt:message key="selected_casebooks_in_progress" bundle="${resword}"/></div>");
    $.ajax({
      type: "POST",
      url: url + "/generateCasebooks",
      data: {
        oids: list
      },
      success: function(data) {
        $("#sidebar_Alerts_open .sidebar_tab_content").html('');
      },
      error: function(e) {
        console.log("Error:" + e);
      }
    });
  }

</script>

<tr id="sidebar_Instructions_open">
  <td class="sidebar_tab">
    <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
      <img src="${pageContext.request.contextPath}/images/sidebar_collapse.gif" border="0" align="right" hspace="10">
    </a>
    <b><fmt:message key="instructions" bundle="${restext}" /></b>
    <div class="sidebar_tab_content">

      <fmt:message key="casebooks" bundle="${resword}" /> for ${studyName}
    </div>
  </td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
  <td class="sidebar_tab">
    <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
      <img src="${pageContext.request.contextPath}/images/sidebar_expand.gif" border="0" align="right" hspace="10">
    </a>
    <b><fmt:message key="instructions" bundle="${restext}" /></b>
  </td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<h1>
	<span class="first_level_header">
		<fmt:message key="casebooks" bundle="${resword}"/> for ${studyName}
	</span>
</h1>

<form action="${pageContext.request.contextPath}/pages/casebooks" style="clear:left; float:left;">
  ${crfEvaluationTable}
</form>

<br>

<div style="clear:left; float:left">
  <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
         value="<fmt:message key="back" bundle="${resword}"/>"
         class="button_medium"
         onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
  <input type="button" value="<fmt:message key="generate_casebooks" bundle="${resword}"/>"
         class="button_long" onClick="javascript: sendSubjectOids();"/>
  <input type="button" value="Download Casebooks"
         class="button_long" onClick="javascript:window.open('${pageContext.request.contextPath}/pages/downloadCasebooks','','location=0,status=0,scrollbars=1,width=650,height=600');"/>
</div>




<jsp:include page="../include/footer.jsp" />
