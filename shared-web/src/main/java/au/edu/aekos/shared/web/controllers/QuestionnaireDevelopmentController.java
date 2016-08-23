package au.edu.aekos.shared.web.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;
import au.edu.aekos.shared.questionnaire.PageAnswersModel;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.questionnaire.jaxb.QuestionnaireConfig;
import au.edu.aekos.shared.service.quest.ControlledVocabularyNotFoundException;
import au.edu.aekos.shared.service.quest.ControlledVocabularyService;
import au.edu.aekos.shared.service.quest.QuestionnaireConfigService;
import au.edu.aekos.shared.service.quest.TraitValue;
import au.edu.aekos.shared.service.security.SecurityService;
import au.org.aekos.shared.questionnaire.QuestionnaireConfigValidationException;



@Controller
public class QuestionnaireDevelopmentController {

	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private QuestionnaireConfigService questionnaireConfigService;
	
	@Autowired
	private ControlledVocabularyService controlledVocabularyService;
	
	@RequestMapping(value = "/dev/uploadQuestionnaire", method = RequestMethod.GET)
	public String viewUploadQuestionnairePage(){
		if( securityService.notHasRole(SharedRole.ROLE_DEVELOPER) ){
			return "home";
		}
		
		return "dev/uploadQuestionnaire";
	}
	
	@RequestMapping(value = "/dev/uploadQuestionnaire", method = RequestMethod.POST)
	public String processUploadQuestionnaireForm(@RequestParam("file") CommonsMultipartFile file, Model model) throws JAXBException, IOException, QuestionnaireConfigValidationException, ControlledVocabularyNotFoundException{
		
		String str = file.getOriginalFilename();
		model.addAttribute("configFileName", str);
		FileItem item = file.getFileItem();
		
		JAXBContext context = JAXBContext.newInstance(QuestionnaireConfig.class);
	    Unmarshaller un = context.createUnmarshaller();
	    Object value = un.unmarshal(item.getInputStream()); 
        QuestionnaireConfig config = ( QuestionnaireConfig ) value;
        questionnaireConfigService.validateConfig(config, false);
        //OK - if we get to here - use the config to make a single paged questionnaire - for testing purposes - 
        //I think we`ll be able to manage a validation step to see if not nulls etc and validation work.
        DisplayQuestionnaire dq = new DisplayQuestionnaire(config);
        model.addAttribute("quest", dq);
        
        
        
        //QuestionnairePage page = quest.getPages().get(index);
		//model.addAttribute("page", page);
		model.addAttribute("answers",new PageAnswersModel());
		//model.addAttribute("controlledVocab", 
        Map<String, List<TraitValue>> controlledVocabs = new HashMap<String, List<TraitValue>>();
		for(QuestionnairePage qp : dq.getPages()){		
			Map<String, List<TraitValue>> cvMap =  controlledVocabularyService.prepareControlledVocabListsForPage(qp) ;
			for(Entry<String, List<TraitValue>> entry :  cvMap.entrySet()){
				controlledVocabs.put(entry.getKey(), entry.getValue());
			}
		}
		model.addAttribute("controlledVocab", controlledVocabs );
        
		
		return "dev/displayQuestionnaire";
	}
}
