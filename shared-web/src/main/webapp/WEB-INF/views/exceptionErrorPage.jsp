<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>SHaRED Application Exception</title>
</head>
<body>
    <h2>An error has occured!</h2>
    <c:if test="${ not empty exception}">
        ${exception.message} 
    </c:if>
</body>
</html>