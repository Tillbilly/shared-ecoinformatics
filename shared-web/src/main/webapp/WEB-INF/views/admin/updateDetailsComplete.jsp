<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>

<q:sharedDoctype />
<html>
<head>
    <title>SHaRED User Details Change Confirmation</title>
	<q:sharedCommonIncludes />
	<c:if test="${emailAddressChanged}" >
		<script type="text/javascript">
			$(function(){
				setInterval(function(){
					window.location.href='${pageContext.request.contextPath}/j_spring_security_logout';
				}, 10000);
			});
		</script>
	</c:if>
	<style type="text/css">
		.detailTable {
			margin: 0 auto;
		}
		
		.emailAddress {
			font-weight: bold;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
	    <q:pageHeader titleText="Details Updated" includeHomeButton="false" />
		<div class="centeredContent">
			<h2 class="noMargin">User: <span class="focusText">${registration.username}</span></h2>
		</div>
	    <table class="detailTable">
	        <tr><td>Username:</td><td>${registration.username}</td></tr>
	        <tr><td>Full name:</td><td>${registration.fullName}</td></tr>
	        <tr><td>Organisation:</td><td>${registration.organisation == 'OTHER' ? registration.organisationOther : orgName}</td></tr>
	        <tr><td>Address:</td><td><p>${fn:replace(registration.postalAddress, newLineChar, '<br />')}</p></td></tr>
	        <tr><td>Email:</td><td>${registration.emailAddress}</td></tr>
	        <tr><td>Phone:</td><td>${registration.phoneNumber}</td></tr>
	    </table>
	    <div class="centeredContent infoBox msgBoxSpacing">
			<c:choose>
			    <c:when test="${emailAddressChanged}" >
			        <p>You have changed the registered email address for this user.</p>
			        <p>The system will now automatically log you out.</p>
			        <p>Please respond to the email sent to the new address<br /><span class="emailAddress">'${registration.emailAddress}'</span><br /> to log back in.</p>
			        <p>If the system does not automatically redirect you in 20 seconds, then click <a href="${pageContext.request.contextPath}/j_spring_security_logout">here</a>.</p>
			    </c:when>
			    <c:otherwise>
			        <p>Return to the <a href="${pageContext.request.contextPath}/">Main Menu</a>.</p>
			    </c:otherwise>
			</c:choose>
		</div>
	</div>
</body>
</html>