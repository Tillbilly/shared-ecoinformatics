<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>SHaRED User Default Registration</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shared-web.css"></link>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<div class="centeredContent">
			<q:sharedSmallLogo />
			<h1>Default Shared User Registration</h1>
		</div>
		<h3>Please enter your details</h3>
		<div>
			<form:form commandName="registration" action="processRegistration">
			    <table class="centeredBlock">
				    <tr><td>Username:</td><td><form:input path="username" cssErrorClass="formInputError" /><form:errors path="username" cssClass="formErrorSpan"/> </td></tr>
				    <tr><td>Password:</td><td><form:password path="password" cssErrorClass="formInputError" /><form:errors path="password" cssClass="formErrorSpan"/> </td></tr>
				    <tr><td>Email Address:</td><td><form:input path="emailAddress" cssErrorClass="formInputError" /><form:errors path="emailAddress" cssClass="formErrorSpan"/></td></tr>
				    <tr><td></td><td style="text-align:right;"><input name="submit" type="submit" value="Submit" /></td></tr>
			    </table>
			</form:form>
		</div>
	</div>
</body>
</html>