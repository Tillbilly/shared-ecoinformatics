<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Edit Access Denied</q:sharedHeadTitle>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shared-web.css"></link>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Submission Review" />
		<div class="msgBoxSpacing errorBox centeredContent">
			<h3>Edit Access Denied</h3>
			<p>User <span class="emphasisedText">${username}</span> does not have permission to modify submission <span class="emphasisedText">${submissionId}</span></p>
			<p>If you believe this is an error, please contact support</p>
		    <div>
		    	<a href="${pageContext.request.contextPath}/">
					<button id="homeButton">Home</button>
				</a>
		    </div>
		</div>
	</div>
</body>
</html>	