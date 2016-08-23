<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Map Tool</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:mapScripts />
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/maplayers-google.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/featurelist.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/mapInit.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-compressed.js?version=${sharedVersion}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/mapconfig/proj4js-projections.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
		$(function() {
			var openerData = opener.shared.geoFeatureSetPopupCallback();
			
			$("#clearFeatures").click(function(){
				$("#coords").empty();
				for(var x=0; x < map.layers.length; x++){
					var curLayer = map.layers[x];
					if(curLayer instanceof OpenLayers.Layer.Vector && curLayer.name != "OpenLayers.Handler.Point"){
						curLayer.destroyFeatures();
					}
				}
			});
			
			$("#saveGeometry").click(function(){
				var jsonStr = shared.buildFeatureListGeometryJsonString(openerData.questionId);
				opener.updateGeometryFeatureSet(jsonStr);
				window.close();
			});
			
			$('input[name=type]:checked').click();
		});
	</script>
	<style type="text/css">
		.mapPopup #map, .mapPopup #coordList {
			bottom: 146px;
		}
	
		.mapPopup #toolSelector {
			height: 146px;
			left: 0;
			right: 0;
			bottom: 0;
			border-top: 2px solid black;
			overflow-y: auto;
		}
		
		body.mapPopup  {
			min-height: 628px;
		}
		
		div.olControlMousePosition {
		    top : 0px;
		}
	</style>
</head>
<body class="mapPopup">
	<div id="header" class="centeredContent">
		<h2>Map Tool</h2>
		<div>Please enter all your geometric information and give each piece of data a name in the left hand panel.</div>
	</div>
	<div id="map"></div>
	<div id="coordList">
		<div id="coordListHeader" class="centeredContent">
			<h3 class="noMargin">Entered data</h3>
		    <button id="clearFeatures">Clear</button>
		    <button id="saveGeometry">Save and Close</button><br />
		    <button id="closeWindow">Close without Saving</button>
		</div>
	    <div id="coords" class="userFeatureList"></div>
	</div>
	<div id="toolSelector" class="uploadedFilesContainer">
		<table>
			<thead>
				<tr>
					<th>Tool</th>
					<th>Description</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td class="toolSelectionCell">
		                <input type="radio" name="type" value="point" id="pointToggle" onclick="toggleControl(this);" checked="checked" />
		                <label for="pointToggle">Draw Point</label>
					</td>
					<td>With the point drawing control active, click on the map to add a point.</td>
				</tr>
				<tr>
					<td class="toolSelectionCell">
		                <input type="radio" name="type" value="polygon" id="polygonToggle" onclick="toggleControl(this);" />
		                <label for="polygonToggle">Draw Polygon</label>
					</td>
					<td>With the polygon drawing control active, click on the map to add the points that make up your
        				polygon. Double-click to finish drawing.<br />
        				Hold down the <span class="focusText">shift</span> key while drawing to activate freehand mode. While in freehand mode, hold 
        				the mouse down and a point will be added with every mouse movement.</td>
				</tr>
				<tr>
					<td class="toolSelectionCell">
		                <input type="radio" name="type" value="box" id="boxToggle" onclick="toggleControl(this);" />
		                <label for="boxToggle">Draw Box</label>
					</td>
					<td>With the box drawing control active, click in the map and drag the mouse to get a rectangle. Release
        				the mouse to finish.</td>
				</tr>
				<!--  Disabled because the drag navigation is disabled in OpenLayers
				<tr>
					<td>
		                <input type="radio" name="type" value="none" id="noneToggle" onclick="toggleControl(this);" />
		                <label for="noneToggle">Navigate</label>
					</td>
					<td>Navigate around the map by dragging without any drawing occurring.</td>
				</tr>
				<tr>
					<td>
		                <input type="checkbox" name="allow-pan" value="allow-pan" id="allowPanCheckbox" checked="checked" onclick="allowPan(this);" />
		                <label for="allowPanCheckbox">Allow Pan While Drawing</label>
					</td>
					<td>(Write desc)</td>
				</tr>
				<tr><td colspan="2">With any drawing control active, paning the map can still be achieved. Drag the map as usual for that.</td></tr>
				-->
			</tbody>
		</table>
	</div>
</body>
</html>