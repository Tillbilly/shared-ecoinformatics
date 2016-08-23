<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>User Registration</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<spring:eval expression="@sharedWebProperties.getProperty('shared.system.email.address')" var="systemEmailAddress" />
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Registration Success" />
		<div class="infoBox msgBoxSpacing centeredContent">
		    <p>An activation email will be sent to <em>${emailAddress}</em> shortly.</p>
		    <p>It will be sent from <em>${systemEmailAddress}</em>, check that it doesn't get caught by your spam filter.</p>
		    <p>Click on the link in the email to activate your account.</p>
		    <p>You will then be able to log in and start using AEKOS SHaRED.</p>
		    <a href="${pageContext.request.contextPath}"><button>Home</button></a>
		</div>
	</div>
</body>
</html>