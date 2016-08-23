<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>AAF SHaRED User Registration</title>
	<q:sharedCommonIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/hmac-sha512.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/enc-base64-min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/validateConfirmationField.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
		
		$(function(){
			
		});

	
	</script>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="AAF User Registration" />
		<h3>Welcome ${registration.username}. You have logged in via AAF. Please enter some additional details:</h3>
		<form:form id="registrationForm" commandName="registration" action="${pageContext.request.contextPath}/secure/processAafRegistration">
	    	<form:hidden path="username"/>
	    	<p id="regoOptionalNote">The following fields are optional, but are used to pre-fill submission metadata:</p>
	    	<div class="twoColLeft">Full name:</div>
		    <div class="twoColRight"><form:input path="fullName" cssErrorClass="formInputError" /><form:errors path="fullName" cssClass="formErrorSpan"/></div>
	    	<div class="twoColLeft">Organisation:</div>
	    	<div class="twoColRight"><form:select path="organisation" cssErrorClass="formInputError" >
		                <form:option value="" />
		                <form:options items="${orgList}" itemLabel="displayString" itemValue="traitValue"/>
		            </form:select>
		    <form:errors path="organisation" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Org (not listed):</div>
		    <div class="twoColRight"><form:input path="organisationOther" cssErrorClass="formInputError" /><form:errors path="organisationOther" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Postal Address:</div>
		    <div class="twoColRight"><form:textarea path="postalAddress" cssErrorClass="formInputError" /><form:errors path="postalAddress" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Phone:</div>
		    <div class="twoColRight"><form:input path="phoneNumber" cssErrorClass="formInputError" /><form:errors path="phoneNumber" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Website:</div>
		    <div class="twoColRight"><form:input path="website" cssErrorClass="formInputError" /><form:errors path="website" cssClass="formErrorSpan"/></div>
		    <div class="centeredContent">
		    	<a href="/shared-web">Cancel</a>
		    	<input id="submitButton" name="submit" type="submit" value="Submit" />
	    	</div>
		</form:form>
	</div>
</body>
</html>