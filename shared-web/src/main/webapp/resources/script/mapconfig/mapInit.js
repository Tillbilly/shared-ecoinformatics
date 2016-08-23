var map, drawControls, pointLayer, polygonLayer, boxLayer, highlightCtrlListSelect;
OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2';

$(function(){
	var openerData = opener.shared.geoFeatureSetPopupCallback();
	
	var controlArray = [ new OpenLayers.Control.MousePosition({'numDigits':3, 'displayProjection':new OpenLayers.Projection("EPSG:4326")}),
	                     new OpenLayers.Control.LayerSwitcher({'ascending':false}),
	                     new OpenLayers.Control.PanZoomBar(),
	                     new OpenLayers.Control.Navigation ( {dragPanOptions: {enableKinetic: true}})  ];

	var options = {
		    projection: "EPSG:900913",
		    maxExtent: new OpenLayers.Bounds(112.5, -45.0, 155.0, -9.0).transform("EPSG:4283", "EPSG:900913"),
		    center: new OpenLayers.LonLat(133.0,-25.5).transform("EPSG:4283", "EPSG:900913"),
		    numZoomLevels : 15,
		    controls : controlArray,
		    zoom : 13
		};
	
	map = new OpenLayers.Map( 'map' , options);
	addConfigLayersToMap(map);
    
    pointLayer = new OpenLayers.Layer.Vector("Point Layer");
    polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");
    boxLayer = new OpenLayers.Layer.Vector("Box layer");
    
    var vectorLayerArray = [ pointLayer, polygonLayer, boxLayer];
    map.addLayers(vectorLayerArray);
    
    drawControls = {
            point: new OpenLayers.Control.DrawFeature(pointLayer,
                OpenLayers.Handler.Point),
            polygon: new OpenLayers.Control.DrawFeature(polygonLayer,
                OpenLayers.Handler.Polygon),
            box: new OpenLayers.Control.DrawFeature(boxLayer,
                OpenLayers.Handler.RegularPolygon, {
                    handlerOptions: {
                        sides: 4,
                        irregular: true
                    }
                }
            )
        };

    drawControls.point.events.register('featureadded', 
    		                           drawControls.point, 
    		                           function(event){ 
    	                                   shared.addFeatureToFeatureList(event.feature, '', "EPSG:900913");
    	                               } );
    drawControls.polygon.events.register('featureadded', 
            drawControls.polygon, 
            function(event){ 
                shared.addFeatureToFeatureList(event.feature,'', "EPSG:900913");
            } );
    drawControls.box.events.register('featureadded', 
            drawControls.box, 
            function(event){ 
                shared.addFeatureToFeatureList(event.feature, '', "EPSG:900913");
            } );
    
    
    for(var key in drawControls) {
        map.addControl(drawControls[key]);
    }
    
    var highlightCtrlHover = new OpenLayers.Control.SelectFeature(vectorLayerArray, {
        hover: true,
        highlightOnly: true,
        renderIntent: "temporary",
        eventListeners: {
            featurehighlighted: function(event){
            	var selector = "#div_" + event.feature.id;
            	selector = selector.replace(/\./g,'_');
            	$(selector).addClass("hoveredMapFeature");
            },
            featureunhighlighted: function(event){
            	var selector = "#div_" + event.feature.id;
            	selector = selector.replace(/\./g,'_');
            	$(selector).removeClass("hoveredMapFeature");
            }
        }
    });

    highlightCtrlListSelect = new OpenLayers.Control.SelectFeature(vectorLayerArray,
        { hover: false,
          highlightOnly: true,
          renderIntent: "temporary"
        }
    );

    map.addControl(highlightCtrlHover);
    map.addControl(highlightCtrlListSelect); 

    highlightCtrlHover.activate();
    highlightCtrlListSelect.activate();
    
    map.setCenter(new OpenLayers.LonLat(133.0,-25.5).transform("EPSG:4283", "EPSG:900913"), 4);
    $("#noneToggle").attr("checked","checked");
    
    map.render("map");
    
    //See if we need to render anything already
    if( openerData != null && typeof openerData != "undefined"){
    	try{
    		var geomSRSCode = openerData.srs;
    		for( var x = 0; x < openerData.features.length; x++ ){
    			var jsonFeature = openerData.features[x];
    			if(jsonFeature.geometry.indexOf('POLY') != -1 ){
    				var vectorPolyFeature = new OpenLayers.Feature.Vector(OpenLayers.Geometry.fromWKT(jsonFeature.geometry));
    				vectorPolyFeature.id = jsonFeature.id;
    				vectorPolyFeature.layer = polygonLayer;
    				if (openerData.readOnly) {
         				shared.addFeatureToFeatureListViewOnly(vectorPolyFeature, jsonFeature.description);
    				} else {
    					shared.addFeatureToFeatureList(vectorPolyFeature, jsonFeature.description, geomSRSCode);
    				}
    				shared.transformToGoogleProjection(geomSRSCode, vectorPolyFeature);
    				vectorPolyFeature.id = jsonFeature.id;
    				var featureArray1 = [ vectorPolyFeature ];
    				polygonLayer.addFeatures( featureArray1 );
    			}else{
    				var vectorPointFeature = new OpenLayers.Feature.Vector(OpenLayers.Geometry.fromWKT(jsonFeature.geometry));
    				vectorPointFeature.id = jsonFeature.id;
    				vectorPointFeature.layer = pointLayer;
    				if (openerData.readOnly) {
         				shared.addFeatureToFeatureListViewOnly(vectorPointFeature, jsonFeature.description);
    				} else {
    					shared.addFeatureToFeatureList(vectorPointFeature, jsonFeature.description, geomSRSCode);
    				}
    				shared.transformToGoogleProjection(geomSRSCode, vectorPointFeature);
    				vectorPointFeature.id = jsonFeature.id;
    				var featureArray = [ vectorPointFeature ];
    				pointLayer.addFeatures( featureArray );
    			}
    		}
    	}catch(err){
    		console.warn(err);
    	}
    }
    
	$("#closeWindow").click(function(){
		window.close();
	});
});

function toggleControl(element) {
    for(key in drawControls) {
        var control = drawControls[key];
        if(element.value == key && element.checked) {
            control.activate();
        } else {
            control.deactivate();
        }
    }
}

function allowPan(element) {
    var stop = !element.checked;
    for(var key in drawControls) {
        drawControls[key].handler.stopDown = stop;
        drawControls[key].handler.stopUp = stop;
    }
}