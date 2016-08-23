package au.edu.aekos.shared.web.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import au.edu.aekos.shared.data.dao.CustomVocabularyDao;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.TraitValue;

@Controller
public class VocabularyManagementController {
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@Autowired
	private CustomVocabularyDao customVocabDao;
	
	@RequestMapping("vocabManagement/vocabularyManagement")
	public String viewVocabularyManagementMenu(Model model){
		
		//Get list of custom vocabularies
		List<String> customTraits = controlledVocabularyService.getListOfAvailableCustomTraits();
		model.addAttribute("customVocabNames", customTraits);
		return "vocabManagement/vocabularyManagement";
	}
	
	@RequestMapping("vocabManagement/viewVocabulary")
	public String viewVocabulary(@RequestParam(required=true) String vocabularyName, Model model){
		List<TraitValue> traitValueList = controlledVocabularyService.getTraitValueList(vocabularyName, true, true);
		model.addAttribute("vocabularyName", vocabularyName);
		model.addAttribute("traitValueList", traitValueList);
		model.addAttribute("newVocabValue", new TraitValue());
		return "vocabManagement/viewVocabulary";
	}
	
	@RequestMapping("vocabManagement/removeVocabularyValue")
	public String removeVocabularyValue(@RequestParam(required=true) String vocabularyName, @RequestParam(required=true) String vocabValue, Model model){
		Map<Submission, List<SubmissionAnswer>> submissionAnswerMap = controlledVocabularyService.getSubmissionAnswersContainingVocabValueAnswer(vocabValue, vocabularyName);
		if(submissionAnswerMap.size() > 0){
			Integer numSubmissions = submissionAnswerMap.size();
			String message = " Can't remove vocabulary value '" + vocabValue +"' for vocab '" + vocabularyName +"<br/>";
			message += "The vocabulary value is used in " + numSubmissions.toString() + " submissions.";
			model.addAttribute("message", message);
			return viewVocabulary(vocabularyName, model);
		}
		controlledVocabularyService.removeCustomVocabValue(vocabularyName, vocabValue);
		String message = "Vocab Value '"+vocabValue+"' for " + vocabularyName + " deleted";
		model.addAttribute("message", message);
		return viewVocabulary(vocabularyName, model);
	}

	
	@RequestMapping(value = "vocabManagement/addVocabValue/{vocabularyName}", method = RequestMethod.POST )
	public String addVocabValue(@PathVariable String vocabularyName, @ModelAttribute("newVocabValue") TraitValue newVocabValue, Model model){
		String message = null;
		if(controlledVocabularyService.checkCustomVocabValueExists(vocabularyName, newVocabValue)){
			message = "Vocab Value '"+newVocabValue.getTraitValue()+"' for " + vocabularyName + " already exists";
		}else{
			controlledVocabularyService.addCustomVocabValue(vocabularyName, newVocabValue);
			message = "Vocab Value '"+newVocabValue.getTraitValue()+"' for " + vocabularyName + " added";
		}
		model.addAttribute("message", message);
		return viewVocabulary(vocabularyName, model);
	}
	
	@RequestMapping(value="vocabManagement/addNewVocabularyFromFile", method = RequestMethod.POST)
	public String addNewVocabularyFromFile( @RequestParam("vocabFile") CommonsMultipartFile file, Model model) throws IOException, SQLException{
		//customVocabularyDao.deleteAllEntries();
		
		controlledVocabularyService.loadCustomVocabularyFromInputStream( file.getInputStream() );
		
	    return viewVocabularyManagementMenu(model);
	}
	
	@RequestMapping(value="vocabManagement/replaceVocabularies", method = RequestMethod.POST)
	public String replaceVocabularies( @RequestParam("vocabFile") CommonsMultipartFile file, Model model) throws IOException, SQLException{
		customVocabDao.deleteAllEntries();
		controlledVocabularyService.loadCustomVocabularyFromInputStream( file.getInputStream() );
	    return  viewVocabularyManagementMenu(model);
	}
	
	@RequestMapping("vocabManagement/downloadCustomVocabularies")
	public void downloadCustomVocabularies( HttpServletResponse response) throws IOException{
		response.setContentType("text/csv");
		response.addHeader("content-disposition", "inline; filename=customVocabs.csv");
		controlledVocabularyService.streamCustomVocabulariesToOutputStream(response.getOutputStream());
		response.flushBuffer();
	}
}
