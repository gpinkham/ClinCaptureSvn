<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
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
