<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<q:sharedDoctype />
<html>
<head>
    <q:sharedHeadTitle>Update Details</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/validateConfirmationField.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/hmac-sha512.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/script/enc-base64-min.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#updateDetailsForm").submit(function(){
				//Check if the email address has changed
				var origEmail = $("#originalEmailAddress").html();
				var newEmail  = $("#newEmailAddress").val();
				if(origEmail === newEmail){
					return true;
				}
				return confirm("If you change the email address, you will be logged out until you respond to the verification email");
			});
			
			$("#changePasswordForm").submit(function(){
				var username = $("#pwusername").val();
				$(".pwField").each(function(){
					var pwRaw = $(this).val();
					if(pwRaw && pwRaw.length > 0){
						var hash = CryptoJS.HmacSHA512(pwRaw, username);
						var hashStr = hash.toString(CryptoJS.enc.Base64);
						$(this).val(hashStr);
					}
				});
			});
			
			function updateOtherOrgVisibility() {
				var keyOfOtherValue = "OTHER";
				if(keyOfOtherValue == $(this).val()) {
					$('.otherOrg').slideDown();
					return;
				}
				$('.otherOrg').slideUp();
			}
			
			$('#organisation').on('change', updateOtherOrgVisibility).trigger('change');
		});
		
		var shared = shared || {};
		
		shared.validatePassword = function() {
			var isPasswordValid = shared.validateConfirmationField('#newPassword', '#newPassword2', '#confirmPasswordMessage', 'Passwords');
			var submitButton = $('#passwordSubmit');
			if (isPasswordValid) {
				submitButton.prop('disabled', false);
				return;
			}
			submitButton.prop('disabled', true);
		}
	</script>
	<style type="text/css">
		.clearHack {
			clear: both;
		}
	</style>
</head>
<body class="sharedBodyBg">
    <div class="main-content">
	    <q:pageHeader titleText="Update Details" includeHomeButton="true" />
		<div class="centeredContent">
			<h2 class="noMargin">User: <span class="focusText">${registration.username}</span></h2>
		</div>
	    <form:form id="updateDetailsForm" commandName="registration" action="${pageContext.request.contextPath}/useradmin/updateDetails">
	        <div id="originalEmailAddress" style="display:none;">${registration.emailAddress}</div>
	        <form:hidden path="username" />
	        
	        
	        <div class="twoColLeft">Email Address:</div>
	        <div class="twoColRight">
	            <c:choose>
	              <c:when test="${not registration.aafUser  }" >
		        	<form:input id="newEmailAddress" path="emailAddress" cssErrorClass="formInputError" />
		        	<form:errors path="emailAddress" cssClass="formErrorSpan"/>
	        	  </c:when>
	        	  <c:otherwise>
	        	    <form:hidden path="emailAddress" readonly="true"/>${registration.emailAddress}
	        	  </c:otherwise>
	        	</c:choose>
        	</div>
        	
		    <div class="twoColLeft">Full Name:</div>
		    <div class="twoColRight"><form:input path="fullName" cssErrorClass="formInputError" /><form:errors path="fullName" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Organisation:</div>
	        <div class="twoColRight">
	        		<form:select path="organisation" cssErrorClass="formInputError" >
	                <form:option value="OTHER">Other</form:option>
	                <form:options items="${orgList}" itemLabel="displayString" itemValue="traitValue" />
	            </form:select>
	    	<form:errors path="organisation" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft otherOrg">Org (not listed):</div>
		    <div class="twoColRight otherOrg">
		    	<form:input path="organisationOther" cssErrorClass="formInputError" />
		    	<form:errors path="organisationOther" cssClass="formErrorSpan"/>
	    	</div>
		    <div class="twoColLeft">Postal Address:</div>
		    <div class="twoColRight"><form:textarea path="postalAddress" cssErrorClass="formInputError" /><form:errors path="postalAddress" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Phone:</div>
		    <div class="twoColRight"><form:input path="phoneNumber" cssErrorClass="formInputError" /><form:errors path="phoneNumber" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Website:</div>
		    <div class="twoColRight"><form:input path="website" cssErrorClass="formInputError" /><form:errors path="website" cssClass="formErrorSpan"/></div>
		    <div class="twoColLeft">Update contact on submissions?</div>
		    <div class="twoColRight"><form:checkbox path="updateSubmissionsWithChangedDetails" /></div>
		    <div class="centeredContent clearHack">
		    	<a href="/shared-web">Cancel</a>
		    	<input name="submit" type="submit" value="Submit" />
	    	</div>
	    </form:form>
	    <c:choose>
		    <c:when test="${not registration.aafUser }" >
			    <h2 class="centeredContent">Change Password</h2>
			    <form:form id="changePasswordForm" commandName="changePassword" action="${pageContext.request.contextPath}/useradmin/changePassword" >
			        <form:hidden id="pwusername" path="username" />
					<div class="twoColLeft">Current Password:</div>
					<div class="twoColRight">
						<form:password path="currentPassword" cssClass="pwField" cssErrorClass="formInputError pwField" />
						<form:errors path="currentPassword" cssClass="formErrorSpan"/>
					</div>
					<div class="twoColLeft">New Password:</div>
					<div class="twoColRight">
						<form:password path="newPassword" cssClass="pwField" cssErrorClass="formInputError pwField" onkeyup="shared.validatePassword(); return false;" />
						<form:errors path="newPassword" cssClass="formErrorSpan"/>
					</div>
					<div class="twoColLeft">Retype New Password:</div>
					<div class="twoColRight">
						<form:password path="newPassword2" cssClass="pwField" cssErrorClass="formInputError pwField" onkeyup="shared.validatePassword(); return false;" />
						<form:errors path="newPassword2" cssClass="formErrorSpan"/>
						<span id="confirmPasswordMessage"></span>
					</div>
					<div class="centeredContent">
						<a href="/shared-web">Cancel</a>
						<input name="submit" id="passwordSubmit" type="submit" value="Submit" disabled="disabled" />
					</div>
			    </form:form>
		    </c:when>
		    <c:otherwise>
		       This user has logged in thru AAF.  Any password enquires need to be redirected to their home organisation system administrators.
	    	</c:otherwise>
	    </c:choose>
	    
    </div>
</body>
</html>