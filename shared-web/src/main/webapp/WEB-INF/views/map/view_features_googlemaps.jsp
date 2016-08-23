<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>View Features</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:mapScripts />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplayers-google.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/featurelist.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/mapInit.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-compressed.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-projections.js"></script>
</head>
<body class="mapPopup">
	<div id="header" class="centeredContent">
		<h2>View Features</h2>
		<div>The following are the features entered by the submitter.</div>
	</div>
	<div id="map"></div>
	<div id="coordList">
		<div id="coordListHeader" class="centeredContent">
			<h3 class="noMargin">Entered data</h3>
		    <button id="closeWindow">Close</button>
		</div>
	    <div id="coords" class="userFeatureList"></div>
	</div>
</body>
</html>