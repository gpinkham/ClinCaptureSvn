<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" style="position: relative; min-height: 100%;" >

<head>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <title>ClinCapture</title>
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="<c:url value='/includes/styles.css?r=${revisionNumber}'/>" type="text/css"/>
    <link rel="stylesheet" href="<c:url value='/includes/NewLoginStyles.css?r=${revisionNumber}'/>" type="text/css"/>
    <script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery-1.3.2.min.js'/>"></script>
    <script type="text/javascript" language="JavaScript" src="<c:url value='/includes/jmesa/jquery.blockUI.js?r=${revisionNumber}'/>"></script>
	<script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/global_functions_javascript.js?r=${revisionNumber}'/>"></script>
    <script type="text/JavaScript" language="JavaScript" src="<c:url value='/includes/theme.js?r=${revisionNumber}'/>"></script>
</head>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>


<body class="login_BG" onLoad="document.getElementById('username').focus();" style="margin-bottom: 170px;">
<div class="login_BG">
    <center>

        <!-- ClinCapture logo -->
        <%String ua = request.getHeader( "User-Agent" );
            String temp = "";
            String iev = "";
            if( ua != null && ua.indexOf( "MSIE" ) != -1 ) {
                temp = ua.substring(ua.indexOf( "MSIE" ),ua.length());
                iev = temp.substring(4, temp.indexOf(";"));
                iev = iev.trim();
            }
            if(iev.length() > 1 && Double.valueOf(iev)<7) {%>
        <div ID="OClogoIE6">&nbsp;</div>
        <%} else {%>
        <div ID="OClogo">&nbsp;</div>
        <%}%>
        <!-- end ClinCapture logo -->

        <span class='dbTitle'> <jsp:include page="../login-include/login-dbtitle.jsp"/> </span> <br><br><br>

        <table border="0" cellpadding="0" cellspacing="0" class="loginBoxes">
            <tr>
                <td class="loginBox_T">&nbsp;</td>
            </tr>
            <tr>
                <td class="loginBox" align="center">
                    <div ID="loginBox" align="center">
                        <!-- Login box contents -->
                        <div ID="login" align="left">
                            <form action="<c:url value='/j_spring_security_check'/>" method="post" autocomplete='off'>
                            	<input type="hidden" name="domain_name" value="" />
                                <input type="password" style="display:none"/>
                                <h1><fmt:message key="login" bundle="${resword}"/></h1>
                                <b><fmt:message key="user_name" bundle="${resword}"/></b>
                                <div class="formfieldM_BG">
                                    <input type="text" id="username" name="j_username" class="formfieldM">
                                </div>

                                <b><fmt:message key="password" bundle="${resword}"/></b>
                                <div class="formfieldM_BG">
                                    <input type="password" id="j_password" name="j_password"  class="formfieldM">
                                </div>
                                <input type="submit" name="submit" value="<fmt:message key='login_button' bundle='${resword}'/>" class="loginbutton" />
                                <div style="display:inline; position:absolute;"><a style="" href="#" id="requestPassword"> <fmt:message key="forgot_password" bundle="${resword}"/></a></div>
                            </form>
                            <br/><jsp:include page="../login-include/login-alertbox.jsp"/>
                        </div>
                        <!-- End Login box contents -->
                    </div>
                </td>
            </tr>
        </table>

    </center>
</div>

    <script type="text/javascript">
        jQuery(document).ready(function() {
        	
        	jQuery('input[name=domain_name]').val(document.location.host);

            jQuery('#requestPassword').click(function() {
                jQuery.blockUI({ message: jQuery('#requestPasswordForm'), css:{left: "200px", top:"180px" } });
            });

            jQuery('#cancel').click(function() {
                jQuery.unblockUI();
                return false;
            });
        });

    </script>

    <div id="requestPasswordForm" style="display:none;">
        <c:import url="requestPasswordPop.jsp">
        </c:import>
    </div>

    <!-- End Main Content Area -->
<jsp:include page="../login-include/login-footer.jsp"/>

</body>
</html>
