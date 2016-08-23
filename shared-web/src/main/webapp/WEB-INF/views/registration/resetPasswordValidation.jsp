<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Password Reset Confirmation</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
</head>
<body class="sharedBodyBg">
	<div class="main-content centeredContent">
		<q:pageHeader includeHomeButton="false" titleText="Password Reset Successful" />
		<div class="infoBox msgBoxSpacing">
			<p>Your password has been reset successfully.</p>
			<a href="${pageContext.request.contextPath}">
				<button>Login</button>
			</a>
		</div>
	</div>
</body>
</html>