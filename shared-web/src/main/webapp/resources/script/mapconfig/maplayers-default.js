//Default WMS Map Layer config for SHaRED feature creator map
//The order of layers defined here is the order the layers are rendered i.e. the first layer here becomes the base layer on the map

var layerMap = {
		
	 layer1 : new OpenLayers.Layer.WMS( "AEKOS Layers",
     		                            'http://portal.dev.aekos.org.au/geoserver/wms',
                                        {layers: 'AEKOS:dem_9st,AEKOS:npwsa_reserves,AEKOS:waterbodies_10m',
    			                         srs: 'EPSG:4283',
    			                         visibility: true} ) ,
    			                         
     layer2 : new OpenLayers.Layer.WMS( "250k Satellite",
     		'http://mapconnect.ga.gov.au/wmsconnector/com.esri.wms.Esrimap?ServiceName=GDA94_MapConnect_SDE_250kmap_WMS',
            {layers: 'LandsatMosaic,Cities',
    		 transparent: 'true',
    		 srs: 'EPSG:4283',
    		 visibility: true } ),
    		 
     layer3 : new OpenLayers.Layer.WMS( "Freeways",
    		 "http://wms-syd.terrapages.com:80/geoserver/wms?SERVICE=WMS",
    		 {layers: 'psma:freeways',
        		 transparent: 'true',
        		 srs: 'EPSG:4283',
        		 visibility: true }
    		 ),
     layer4 : new OpenLayers.Layer.WMS( "PSMA National Parks",
    		 "http://wms-syd.terrapages.com:80/geoserver/wms?SERVICE=WMS",
    		 {layers: 'psma:nationalparks',
        		 transparent: 'true',
        		 srs: 'EPSG:4283',
        		 visibility: true }
    		 ),
     layer5 : new OpenLayers.Layer.WMS( "Joined Roads",
    	    		 "http://wms-syd.terrapages.com:80/geoserver/wms?SERVICE=WMS",
    	    		 {layers: 'psma:joinedroads',
    	        		 transparent: 'true',
    	        		 srs: 'EPSG:4283',
    	        		 visibility: true }
    	    		 )		 

};

function addConfigLayersToMap( map){
	for(key in layerMap) {
        var layer = layerMap[key];
        map.addLayer(layer);   
	}
};