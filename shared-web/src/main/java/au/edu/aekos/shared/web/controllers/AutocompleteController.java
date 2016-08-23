package au.edu.aekos.shared.web.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.service.quest.RAMDirectoryVocabService;
import au.edu.aekos.shared.service.quest.TraitValue;


/**
 * Use an in memory lucene index to facilitate quick search response.
 * 
 * 
 * @author btill
 */


@Controller
public class AutocompleteController {
	
	@Autowired
	private RAMDirectoryVocabService autoService;
	
	@RequestMapping("/questionnaire/vocab/autocomplete/{trait}")
	public void performQuery(@RequestParam String term, @PathVariable String trait, HttpServletResponse response) throws IOException{
		response.setContentType("application/json");
		String traitName = trait.replaceAll("_", " ");
		List<TraitValue> searchResultList = autoService.performSearch(term, traitName, true);
		StringBuffer sb = new StringBuffer("[");
        boolean notFirst = false;
		for(TraitValue tv : searchResultList ){
			if(notFirst){
				sb.append(",");
			}else{
				notFirst = true;
			}
			sb.append("{");
			sb.append("\"label\" :\"");
			if(StringUtils.hasLength(tv.getDisplayString())){
			    sb.append(tv.getDisplayString());
			}
			sb.append("\",\"value\":\"").append(tv.getTraitValue()).append("\"}");
		}
		sb.append("]");
		response.getWriter().print(sb.toString());
		response.flushBuffer();
	}
}
