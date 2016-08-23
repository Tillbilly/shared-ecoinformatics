<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<q:sharedDoctype />
<html>
<head>
    <q:sharedHeadTitle>Reset Password</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/hmac-sha512.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/script/enc-base64-min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/validateConfirmationField.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
		$(function(){
		   $("#resetPasswordForm").submit(function(){
			   //Check both password fields are not null 
			   //and are equal before submitting
			   var password = $("#passwordInput").val();
			   var verifyPassword = $("#verifyPasswordInput").val();
			   if( password != null && password != '' && password == verifyPassword ){
				   var hash = CryptoJS.HmacSHA512(password, '${username}');
				   var hashStr = hash.toString(CryptoJS.enc.Base64);
				   $("#passwordHash").val(hashStr);
				   $("#resetPasswordForm").submit();
			   }else if(password != null && password != '' && password != verifyPassword){
				   alert("Entered passwords do not match");
			   }
			   $("#passwordInput").val(null);
		   });
		});
		
		var shared = shared || {};
		
		shared.validatePasswordsMatch = function() {
			var isPasswordValid = shared.validateConfirmationField('#passwordInput', '#verifyPasswordInput', '#confirmPasswordMessage', 'Passwords');
			var submitButton = $('#submit');
			if (isPasswordValid) {
				submitButton.prop('disabled', false);
				return;
			}
			submitButton.prop('disabled', true);
		}
	</script>
	<style type="text/css">
		.twoColLeft {
			width: 40%;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Password Reset" />
		<div>
			<form:errors cssClass="formErrorSpan" />
		</div>
		<form:form commandName="resetPasswordModel" id="resetPasswordForm" method="post" action="${pageContext.request.contextPath}/reg/resetPassword/${username}/${token}" class="centeredBlock">
		    <form:hidden path="username" />
		    <form:hidden path="token" />
		    <form:hidden id="passwordHash" path="password" />
			<div class="twoColLeft">Username:</div>
			<div class="twoColRight">
				<input type="text" disabled="disabled" value="${username}" />
			</div>
			<div class="twoColLeft">Enter new password:</div>
			<div class="twoColRight">
				<input id="passwordInput" type="password" onkeyup="shared.validatePasswordsMatch(); return false;" />
			</div>
			<div class="twoColLeft">Re-enter new password:</div>
			<div class="twoColRight">
				<input id="verifyPasswordInput" type="password" onkeyup="shared.validatePasswordsMatch(); return false;" />
				<span id="confirmPasswordMessage"></span>
			</div>
			<div class="twoColLeft">Confirm current email address:</div>
			<div class="twoColRight">
				<form:input path="emailAddress" />
				<form:errors path="emailAddress" cssClass="formErrorSpan" />
			</div>
			<div class="centeredContent">
				<a href="${pageContext.request.contextPath}">Cancel</a>
				<input name="submit" id="submit" type="submit" value="Submit" disabled="disabled" />
			</div>
		</form:form>
	</div>
</body>
</html>