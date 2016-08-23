var shared = shared || {};
shared.pointIdIndex = 0;
shared.polyIdIndex = 0;

shared.incrementIndexCounter = function(oldId){
	if(oldId != null && oldId.indexOf("PLY_") > -1 ){
		this.polyIdIndex = parseInt( oldId.substring(4) );
	}else if(oldId != null && oldId.indexOf("PNT_") > -1 ){
		this.pointIdIndex = parseInt( oldId.substring(4) );
	}
}

shared.adjustToUserFriendlyFeatureId = function(feature){
	if(feature.id.indexOf("Open") == -1 ){
		this.incrementIndexCounter(feature.id);
		return;
	}
	var newId = '';
	var wktGeom = feature.geometry.toString();
	if( wktGeom.indexOf("POLY") >= 0 ){
		this.polyIdIndex = this.polyIdIndex + 1;
		newId = "PLY_" + this.polyIdIndex;
	}else{
		this.pointIdIndex = this.pointIdIndex + 1;
		newId = "PNT_" + this.pointIdIndex;
	}
	feature.id = newId;
}

shared.addFeatureToFeatureList = function(feature , featureDescription, srsCode){
	this.adjustToUserFriendlyFeatureId(feature);
	var featureId = feature.id;
	var divId = "div_" + featureId;
	divId = divId.replace(/\./g,'_');
	var layerName = feature.layer.name;
	var cloneGeom = feature.geometry.clone();
	if(srsCode != "EPSG:4283"){
	    cloneGeom.transform(srsCode,"EPSG:4283");
	    //lets limit the lon lats to 7 decimal places
	    var verticesArray = cloneGeom.getVertices();
	    for(var x = 0; x < verticesArray.length; x++){
	    	var vertex = verticesArray[x];
	    	vertex.x = vertex.x.toFixed(7);
	    	vertex.y = vertex.y.toFixed(7);
	    }
	}
	var wktGeom = cloneGeom.toString();
	var featureDesc = '';
	if(featureDescription != null ){
		featureDesc = featureDescription;
	}
	
	$(".userFeatureList").first().append("<div id='"+ divId +"' class='featureDetails' >" + 
			                                "<span class='featureId'>" + featureId + "</span> " + 
			                                "<span class='featureDescription'><input type='text' class='featureDescInput' /></span>" +
			                                "<span class='deleteFeatureSpan'><div class=\"deleteIcon\"></div></span>" +
			                                "<span class='featureWktGeom' style='display:none;'>" + wktGeom + "</span> " + 
			                                "<span class='layerName' style='display:none;'>" + layerName + "</span></div>" 
	                                         );
	
	$("#" + divId + " input.featureDescInput").val(featureDesc);
	
	this.assignMouseEventsToFeatureList();
};

shared.addFeatureToFeatureListViewOnly = function(feature , featureDescription){
	this.adjustToUserFriendlyFeatureId(feature);
	var featureId = feature.id;
	var divId = "div_" + featureId;
	divId = divId.replace(/\./g,'_');
	var layerName = feature.layer.name;
	var wktGeom = feature.geometry.toString();
	var featureDesc = '';
	if(featureDescription != null ){
		featureDesc = featureDescription;
	}
	
	$(".userFeatureList").first().append("<div id='"+ divId +"' class='featureDetails' >" + 
			                                "<span class='featureId'>" + featureId + "</span> " + 
			                                "<span class='featureDescription'><input type='text' readonly='readonly' class='featureDescInput' /></span>" +
			                                "<span class='featureWktGeom' style='display:none;'>" + wktGeom + "</span> " + 
			                                "<span class='layerName' style='display:none;'>" + layerName + "</span></div>" 
	                                         );
	
	$("#" + divId + " input.featureDescInput").val(featureDesc);
	
	this.assignMouseEventsToFeatureList();
};

shared.assignMouseEventsToFeatureList = function(){
	var highlightOn = function(){
		$(this).addClass("hoveredMapFeature");
		var featureId = $(this).find("span.featureId").first().html();
		var layerName = $(this).find("span.layerName").first().html();
		for( var x = 0 ; x < highlightCtrlListSelect.layers.length; x++ ){
			var curLayer = highlightCtrlListSelect.layers[x];
			if(layerName == curLayer.name){
			    var curFeature = curLayer.getFeatureById(featureId);
			    highlightCtrlListSelect.highlight(curFeature);
			    break;
			}
		}
		
	};
	var highlightOff = function() {
		$(this).removeClass("hoveredMapFeature");
		var featureId = $(this).find("span.featureId").first().html();
		var layerName = $(this).find("span.layerName").first().html();
		for( var x = 0 ; x < highlightCtrlListSelect.layers.length; x++ ){
			var curLayer = highlightCtrlListSelect.layers[x];
			if(layerName == curLayer.name){
			    var curFeature = curLayer.getFeatureById(featureId);
			    highlightCtrlListSelect.unhighlight(curFeature);
			    break;
			}
		}
	};
	$(".featureDetails").hover(highlightOn, highlightOff);
	$("div.featureDetails span.deleteFeatureSpan").click(function(){
		var $featDet = $(this).parent("div.featureDetails");
		var featureId = $featDet.find("span.featureId").first().html();
		var layerName = $featDet.find("span.layerName").first().html();
		for( var x = 0 ; x < highlightCtrlListSelect.layers.length; x++ ){
			var curLayer = highlightCtrlListSelect.layers[x];
			if(layerName == curLayer.name){
			    var curFeature = curLayer.getFeatureById(featureId);
			    var featuresToDestroy = [curFeature ];
			    curLayer.destroyFeatures(featuresToDestroy);
			    $featDet.remove();
			    break;
			}
		}
	
	});
};

shared.buildFeatureListGeometryJsonString = function(questId){
	var jsonStr = '{ "questionId" : "' + questId + '", "srs":"EPSG:4283", "features" : [ ';
	var featureDetailsArray = $("#coords div.featureDetails");
	if(featureDetailsArray.length > 0){
		for(var x = 0; x < featureDetailsArray.length; x++){
			var featId = $(featureDetailsArray[x]).find("span.featureId").first().html();
			var featDesc = $(featureDetailsArray[x]).find("input.featureDescInput").first().val();
			featDesc = featDesc == null ? '' : featDesc;
			var wktGeom = $(featureDetailsArray[x]).find("span.featureWktGeom").first().html();
			if(x > 0){
				jsonStr = jsonStr + ',';
			}
			jsonStr = jsonStr + '{"id":"' + featId +'","geometry":"' + wktGeom +'","description":"'+featDesc+'"}';
		}
	}
	
	jsonStr = jsonStr + '] }';
	return jsonStr;
};

//Transforms a features geometry to EPSG:900913 and fixes the precision to 2 d.p. ( about 1cm on the ground )
shared.transformToGoogleProjection = function(srsCode, feature){
	if("EPSG:900913" != srsCode){
		feature.geometry.transform(srsCode, "EPSG:900913");
		var vertices = feature.geometry.getVertices();
		for(var i = 0; i < vertices.length; i++ ){
			vertices[i].x = vertices[i].x.toFixed(2);
			vertices[i].y = vertices[i].y.toFixed(2);
		}
	}
};