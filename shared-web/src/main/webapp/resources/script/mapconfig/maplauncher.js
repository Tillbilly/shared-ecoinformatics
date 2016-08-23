var shared = shared || {};

function initGeoFeatureSetMapLaunch(pageContext){
	function launchGeoFeatureSetMapPopup(){
		var clickedButton = $(this);
		shared.geoFeatureSetPopupCallback = function() {
			var $questDiv = clickedButton.closest("div.questionDiv");
			var questionId = $questDiv.attr("id");
			questionId = questionId.replace(/Q/,'');
			
			var geometryJson = $questDiv.find("input.geometryInput").first().val();
			if( geometryJson == null || geometryJson == ''){
				geometryJson = '{ "questionId" : "'+ questionId + '", "srs":"EPSG:4283", "features" : [ ] }'; 
			}
			var result = $.parseJSON(geometryJson);
			result.readOnly = false;
			return result;
		}
		
		var url = pageContext + "/mapGeometryPicker";
		var popup = window.open(url, "Map", "location=0,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=1000,height=630");
		if (typeof popup == "undefined") {
			alert(shared.popupBlockedMessage);
			return;
		}
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		shared.openPopUps.push(popup);
	}
	
	$(".viewFeatureSetMapButton").each(function(index, element) {
		$(element).click(function() {
			shared.executeBasedOnActiveSession({
				active: function() { launchGeoFeatureSetMapPopup.call(element) },
				inactive: shared.reloadPage
			})
		});
	});
	
	$("input.geometryInput").each(function(){
		var jsonStr = $(this).val();
		if(jsonStr != null && jsonStr != ''){
			updateGeometryFeatureSet(jsonStr);
		}
	});
};

function updateGeometryFeatureSet(geometryJsonString){
	console.log(geometryJsonString);
	try{
		var gfsObj = $.parseJSON(geometryJsonString);
		if(gfsObj.questionId == null || gfsObj.questionId == ''){
			return;
		}
	    var qID = gfsObj.questionId;
	    var divId = "Q" + qID;
		var divIdSelector = "#" + divId.replace(/\./g,'\\.');
		var $geoFSDiv = $(divIdSelector + " div.geoFeatureSet").first();
		$geoFSDiv.find(".geometryInput").val(geometryJsonString).trigger('change');
		$geoFSDiv.find("div.featureList").empty();
		
		if(gfsObj.features != null && gfsObj.features.length > 0 ){
			for(var x = 0; x < gfsObj.features.length; x++ ){
				
				var featureDesc= gfsObj.features[x].description == null ? '' : gfsObj.features[x].description;
				
                var htmlString = "<div class='featureDetails'> " +
					                "<span class='featureId'>" + gfsObj.features[x].id + "</span> " + 
					                "<span class='featureDescription'>"+ featureDesc +"</span>" +
					                "<span class='featureWktGeom' style='display:none;'>" + gfsObj.features[x].geometry + "</span> " + 
                                 "</div>";
                $geoFSDiv.find("div.featureList").append(htmlString);
			}
		}else{
			$geoFSDiv.find(".geometryInput").val('');
		}
	}catch(err){
		console.warn(err);
	}
};