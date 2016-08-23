<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>SHaRED Statistics</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Statistics" />
		<div class="uploadedFilesContainer">
			<table>
				<thead>
					<tr>
						<th>Description</th>
						<th>Stat</th>
					</tr>
				</thead>
			    <tbody>
			    	<c:forEach var="currStat" items="${stats}">
			    		<tr>
			    			<td>${currStat.text}</td>
			    			<td>${currStat.stat}</td>
			    		</tr>
			    	</c:forEach>
			    </tbody>
			</table>
		</div>
	</div>
</body>
</html>