//Default WMS Map Layer config for SHaRED feature creator map
//The order of layers defined here is the order the layers are rendered i.e. the first layer here becomes the base layer on the map

var layerMap = {
	 layer1 : new OpenLayers.Layer.Google("Google Physical", {type: google.maps.MapTypeId.TERRAIN} ),
     layer2 : new OpenLayers.Layer.Google("Google Streets", {numZoomLevels: 20}),
     layer3 : new OpenLayers.Layer.Google("Google Hybrid",{type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20}),
     layer4 : new OpenLayers.Layer.Google( "Google Satellite", {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22} )
};

function addConfigLayersToMap( map){
	for(key in layerMap) {
        var layer = layerMap[key];
        map.addLayer(layer);   
	}
};