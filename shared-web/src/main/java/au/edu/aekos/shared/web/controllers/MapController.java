package au.edu.aekos.shared.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MapController {
	
	@RequestMapping(method=RequestMethod.GET ,value="/mapGeometryPicker")
	public String mapGeometryPicker(){
		return "map/pick_googlemaps";
	}
	
	@RequestMapping(method=RequestMethod.GET ,value="/viewFeatures")
	public String viewFeatures(){
		return "map/view_features_googlemaps";
	}

}
