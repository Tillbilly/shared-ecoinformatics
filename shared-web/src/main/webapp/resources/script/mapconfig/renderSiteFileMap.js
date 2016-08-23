var map, drawControls, pointLayer,popup;

OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2';

function initialiseMapForSiteFile(){
	var controlArray = [ new OpenLayers.Control.MousePosition(),
	                     new OpenLayers.Control.LayerSwitcher({'ascending':false}),
	                     new OpenLayers.Control.PanZoomBar(),
	                     new OpenLayers.Control.OverviewMap(), // ];
	                     new OpenLayers.Control.Navigation ( {dragPanOptions: {enableKinetic: true}}) ];
	                    
	var options = {
		    projection: "EPSG:900913",
		    maxExtent: new OpenLayers.Bounds(100.0, -70.0, 200.0, -0.0).transform("EPSG:4283", "EPSG:900913"),
		    center: new OpenLayers.LonLat(133.0,-25.5).transform("EPSG:4283", "EPSG:900913"),
		    numZoomLevels : 15,
		    controls : controlArray,
		    zoom : 13
		};
	
	map = new OpenLayers.Map( 'map' , options);
	addConfigLayersToMap(map);
    pointLayer = new OpenLayers.Layer.Vector("Point Layer");
    var vectorLayerArray = [ pointLayer ];
    map.addLayers(vectorLayerArray);
	
    var selectControl = new OpenLayers.Control.SelectFeature(
    		pointLayer, {
    		    hover: true,
    		    onBeforeSelect: function(feature) {
    		    	
    		    	//siteId;
    		    	//comments;
    		        
    		    	//build the popup attribute display
    		    	var popupContent = "<div>";
    		    	
    		    	if(feature.attributes.siteId != null){
    		    		popupContent = popupContent + feature.attributes.siteId + "<br/>";
    		    	}
    		    	if(feature.attributes.comments != null){
    		    		popupContent = popupContent + feature.attributes.comments + "<br/>";
    		    	}
    		    	
    		    	popupContent = popupContent + "</div>";
    		    	
    		    	
    		    	
    		       // add code to create tooltip/popup
    		       popup = new OpenLayers.Popup.FramedCloud(
    		          "",
    		          feature.geometry.getBounds().getCenterLonLat(),
    		          new OpenLayers.Size(200,120),
    		          popupContent,
    		          null,
    		          false,
    		          null);
    		       feature.popup = popup;
    		       map.addPopup(popup);
    		       // return false to disable selection and redraw
    		       // or return true for default behaviour
    		       return true;
    		    },
    		    onUnselect: function(feature) {
    		       // remove tooltip
    		       map.removePopup(feature.popup);
    		       feature.popup.destroy();
    		       feature.popup=null;
    		    }
    		});
    map.addControl(selectControl);
    selectControl.activate();
	
}

function getEpsgCodeForZone(zone){
	var zoneStr = "" + zone;
	zoneStr = zoneStr.trim();
	return "EPSG:283" + zoneStr;
}



function drawSitePointsOnMap(siteArray, srs){
    var mapNeedsRendering = false;
	if(map == null ){
    	initialiseMapForSiteFile();
    	mapNeedsRendering = true;
    }else{
    	pointLayer.removeAllFeatures();
    }
    //Now lets draw some points
    //siteId;
    //xCoord;
    //yCoord;
    //zone;
    //comments;
    
   
    for(var x = 0; x < siteArray.length; x++ ){
    	var site = siteArray[x];
    	//construct wkt point
    	var wktPointStr = "POINT(" + site.xCoord + " " + site.yCoord + ")";
    	var attributes = { siteId : site.siteId ,
    			           comments: site.comments };
    	var geom = OpenLayers.Geometry.fromWKT(wktPointStr);
    	if("EPSG:900913" != srs){  //If we are'nt in the google projection we need to transform to 900913
    		var projStr = srs;
    		if("UTM" == srs){
    			//need to transform based on the zone specified in the site coord
    			projStr = getEpsgCodeForZone(site.zone);
    		}
    	    geom.transform(projStr, "EPSG:900913");
    	}
    	var vectorPointFeature = new OpenLayers.Feature.Vector(geom, attributes ) ;		
    	var featureArray = [ vectorPointFeature ];
    	pointLayer.addFeatures( featureArray );		
    }
    
    var dataExtent = pointLayer.getDataExtent();
    dataExtent = dataExtent.scale(1.1);
    
    if (mapNeedsRendering) {
    	map.render("map");
    }
    map.zoomToExtent(dataExtent);
};