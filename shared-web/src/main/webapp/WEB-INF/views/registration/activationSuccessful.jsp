<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>User Activation</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
</head>
<body class="sharedBodyBg">
	<div class="main-content centeredContent">
		<q:pageHeader includeHomeButton="true" titleText="User Activation" />
		<div class="msgBoxSpacing infoBox centeredContent">
			<h2>Your activation has been successful.</h2>
			<p>Please click <a href="${pageContext.request.contextPath}">here</a> to log in.</p>
		</div>
	</div>
</body>
</html>