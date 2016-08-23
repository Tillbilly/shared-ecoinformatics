<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>View Application Config</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="View Application Config" />
		<h2 class="centeredContent">Properties</h2>
		<div class="uploadedFilesContainer">
			<table class="centeredBlock">
				<c:forEach items="${props}" var="p">
					<tr>
						<td>${p.key}</td>
						<td>${p.value}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>