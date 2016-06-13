<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
</head>
<body>
<script>
    if (window.opener) {
        window.opener.location.reload(true);
    }
    window.close();
</script>
</body>
</html>
