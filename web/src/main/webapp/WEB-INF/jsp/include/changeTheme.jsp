<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    $(window).load(function() {
        deleteHideStuff();
    });
    var theme = eval('${newThemeColor}Theme');
    theme.applyCommonStyles();
    theme.applyCustomStyles();
</script>



