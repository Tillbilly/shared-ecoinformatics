<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<title>SHaRED User Default Registration</title>
	<q:sharedCommonIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/disableEnableButton.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/hmac-sha512.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/enc-base64-min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/validateConfirmationField.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
		var shared = shared || {};
		shared.isConfirmPasswordMatching = false;
		shared.isConfirmEmailAddressMatching = false;
		shared.isEmailAddressValid = false;
	
		$(function(){
			shared.disableButton('#submitButton');
			
			$("#registrationForm").submit(function(){
				var enteredPassword = $("#passwd").val();
				var username = $("#username").val();
				if(enteredPassword && enteredPassword.length > 0 && username && username.length > 0){
					var hash = CryptoJS.HmacSHA512(enteredPassword, username);
					var hashStr = hash.toString(CryptoJS.enc.Base64);
					$("#passwd").val(hashStr);
				}
			});
			
			$('#passwd').qtip({
				show: 'focus',
				hide: 'blur',
				position : {
					my : 'left center',
					at : 'center right',
					target : false
				},
			});
			
			/* This is the fallback for browsers that don't support the onInput DOM event */
			$('#emailAddress').focus(function() {
				var refreshIntervalMs = 250;
				function checkEmailInput() {
					shared.validateEmail();
					shared.validateConfirmEmailMatches();
					setTimeout(checkEmailInput, refreshIntervalMs);
				}
				setTimeout(checkEmailInput, refreshIntervalMs); 
			});
		});

		shared.validateConfirmPasswordMatches = function() {
			shared.isConfirmPasswordMatching = shared.validateConfirmationField('#passwd', '#passwdConfirm', '#confirmPasswordMessage', 'Passwords');
			shared.updateSubmitButtonStatus();
		}
		
		shared.validateConfirmEmailMatches = function() {
			shared.isConfirmEmailAddressMatching = shared.validateConfirmationField('#emailAddress', '#emailAddressConfirm', '#confirmEmailMessage', 'Email Addresses');
			shared.updateSubmitButtonStatus();
		}
		
		shared.updateSubmitButtonStatus = function() {
			if (shared.isConfirmPasswordMatching && shared.isConfirmEmailAddressMatching && shared.isEmailAddressValid) {
				shared.enableButton('#submitButton');
				return;
			}
			shared.disableButton('#submitButton');
		}
		
		shared.validateEmail = function () {
			var validateHelper = function(email) { 
				// http://stackoverflow.com/a/46181/11236
	    	    var validEmailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    	    return validEmailRegex.test(email);
			}
			var emailAddressField = $('#emailAddress');
			var messageField = $('#validEmailMessage');
			shared.isEmailAddressValid = validateHelper(emailAddressField.val());
			if (!shared.isEmailAddressValid) {
				emailAddressField.addClass("badColour");
		        messageField.html("Invalid email address");
		        return;
			}
			emailAddressField.removeClass("badColour");
	        messageField.html("");
	        return;
		}
	</script>
	<style type="text/css">
		
		/**
		 * From: http://nicolasgallagher.com/micro-clearfix-hack/
		 *
		 * For modern browsers
		 * 1. The space content is one way to avoid an Opera bug when the
		 *    contenteditable attribute is included anywhere else in the document.
		 *    Otherwise it causes space to appear at the top and bottom of elements
		 *    that are clearfixed.
		 * 2. The use of `table` rather than `block` is only necessary if using
		 *    `:before` to contain the top-margins of child elements.
		 */
		.clearfix:before,
		.clearfix:after {
		    content: " "; /* 1 */
		    display: table; /* 2 */
		}
		
		.clearfix:after {
		    clear: both;
		}
		
		/**
		 * For IE 6/7 only
		 * Include this rule to trigger hasLayout and contain floats.
		 */
		.clearfix {
		    *zoom: 1;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="User Registration" />
		<h3>Please enter your details:</h3>
		<form:form id="registrationForm" commandName="registration" action="processRegistration">
	    	<div class="twoColLeft">Username:</div>
	    	<div class="twoColRight"><form:input id="username" path="username" cssErrorClass="formInputError" /><form:errors path="username" cssClass="formErrorSpan"/></div>
	    	
	    	<div class="twoColLeft">Password:</div>
	    	<div class="twoColRight">
	    		<form:password id="passwd" path="password" cssErrorClass="formInputError" oninput="shared.validateConfirmPasswordMatches(); return false;" title="Please choose a strong password, at least 8 characters" />
	    		<form:errors path="password" cssClass="formErrorSpan"/>
    		</div>
	    	<div class="twoColLeft regoConfirm">Confirm Password:</div>
	    	<div class="twoColRight regoConfirm">
	    		<input type="password" id="passwdConfirm" oninput="shared.validateConfirmPasswordMatches(); return false;" />
	    		<span id="confirmPasswordMessage"></span>
    		</div>
	    	
	    	<div class="twoColLeft">Email Address:</div>
	    	<div class="twoColRight">
	    		<form:input path="emailAddress" cssErrorClass="formInputError" oninput="shared.validateEmail(); shared.validateConfirmEmailMatches(); return false;"/>
	    		<form:errors path="emailAddress" cssClass="formErrorSpan"/>
	    		<span id="validEmailMessage" class="badColour"></span>
    		</div>
	    	<div class="twoColLeft regoConfirm">Confirm Email Address:</div>
	    	<div class="twoColRight regoConfirm">
	    		<input type="text" id="emailAddressConfirm" oninput="shared.validateConfirmEmailMatches(); return false;" />
	    		<span id="confirmEmailMessage"></span>
    		</div>
	    	
	    	<p id="regoOptionalNote">The following fields are optional, but are used to pre-fill submission metadata:</p>
   		    <div class="twoColLeft">Full Name:</div>
	    	<div class="twoColRight">
	    		<form:input path="fullName" cssErrorClass="formInputError" />
	    		<form:errors path="fullName" cssClass="formErrorSpan"/>
    		</div>
	    	<div class="twoColLeft">Organisation:</div>
	    	<div class="twoColRight"><form:select path="organisation" cssErrorClass="formInputError" >
		                <form:option value="" />
		                <form:options items="${orgList}" itemLabel="displayString" itemValue="traitValue"/>
		            </form:select>
		    <form:errors path="organisation" cssClass="formErrorSpan"/></div>
		    <div class="clearfix"></div>
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