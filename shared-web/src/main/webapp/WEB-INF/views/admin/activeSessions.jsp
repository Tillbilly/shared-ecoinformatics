<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Active User Sessions</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Show Active User Sessions" />
		<div>
			<h2 class="centeredContent">Active User Sessions</h2>
			<p class="centeredContent">Current Time <fmt:formatDate value="${currentDate}" pattern="HH:mm:ss dd-MM-yyyy z" /></p>
			<table class="sharedTable">
				<thead>
					<tr>
						<th>Username</th>
						<th>Last Request</th>
						<th>Time until timeout</th>
					</tr>
				</thead>
				<c:forEach items="${activeSessionList}" var="activeSesh">
					<tr>
						<td>${activeSesh.username}</td>
						<td><fmt:formatDate value="${activeSesh.lastRequest}" pattern="HH:mm:ss dd-MM-yyyy z" /></td>
						<td>${activeSesh.minutesUntilTimeout} minute(s) (<fmt:formatDate value="${activeSesh.timeoutTime}" pattern="HH:mm:ss dd-MM-yyyy z" />)</td>
				    </tr>
				</c:forEach>	
			</table>
		</div>
	</div>
</body>
</html>