<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>View Study Location File</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<q:mapScripts />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-compressed.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-projections.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplayers-google.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/renderSiteFileMap.js"></script>
	<script> 
        $(function() { 
        	var data = jQuery.parseJSON('${siteFileJson}');
        	$("div.siteMap").show();
            $("div.map").empty();
            drawSitePointsOnMap(data.sites, data.coordSys);
        	$("#backButton").click(function(){
            	window.close();
            });
        });
    </script>
    <style type="text/css">
		.mapPopup #map {
			right: 0;
		}
		
		body.mapPopup  {
			min-height: 0;
			min-width: 0;
		}
	</style>
</head>
<body class="mapPopup">
	<div id="header" class="centeredContent">
		<h2>View Study Location File</h2>
		<button id="backButton">Close Window</button>
	</div>
	<div id="map"></div>
</body>
</html>	