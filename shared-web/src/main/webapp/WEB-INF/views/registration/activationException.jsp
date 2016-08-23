<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Activation Exception</q:sharedHeadTitle>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shared-web.css"></link>
</head>
<body class="sharedBodyBg">
	<q:pageHeader includeHomeButton="true" titleText="Activation Exception" />
	<div class="centeredContent msgBoxSpacing errorBox">
		<p>${e.message}</p>
		<p class="centeredContent">
			<a href="${pageContext.request.contextPath}"><button>Home</button></a>
		</p>
	</div>
</body>
</html>