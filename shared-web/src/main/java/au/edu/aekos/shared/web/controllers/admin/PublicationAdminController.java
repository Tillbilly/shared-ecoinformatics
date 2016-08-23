package au.edu.aekos.shared.web.controllers.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import au.edu.aekos.shared.data.entity.PublicationLog;
import au.edu.aekos.shared.data.entity.SharedRole;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionPublicationStatus;
import au.edu.aekos.shared.service.index.SolrIndexService;
import au.edu.aekos.shared.service.publication.SubmissionPublicationService;
import au.edu.aekos.shared.service.rifcs.RifcsService;
import au.edu.aekos.shared.service.security.SecurityService;


@Controller
public class PublicationAdminController {

	@Autowired
	private SubmissionPublicationService submissionPublicationService;
	
	@Autowired
	private SolrIndexService solrIndexService;
	
	@Autowired
	private RifcsService rifcsService;
	
	@Autowired
	private SecurityService securityService;
	
	@RequestMapping("/admin/publicationConsole")
	public String viewPublicationConsole(Model model){
		//Get list of failed submission publications
		List<Submission> subList = submissionPublicationService.getListOfPublicationFailedSubmissions();
		Map<Long, PublicationLog> publicationLogMap = submissionPublicationService.getLatestPublicationLogEntriesForSubmissionList(subList);
		model.addAttribute("failedSubmissionList", subList);
		model.addAttribute("publicationLogMap", publicationLogMap);
		return "admin/publicationConsole";
	}
	
	@RequestMapping("/admin/republishSubmission")
	public String republishSubmission(Model model, @RequestParam(required=true) Long submissionId){
		if(securityService.hasRole(SharedRole.ROLE_ADMIN)){
			SubmissionPublicationStatus status = submissionPublicationService.publishSubmission(submissionId);
			model.addAttribute("republishSubmissionId", submissionId);
			model.addAttribute("republishStatus", status);
		}
		return viewPublicationConsole(model);
	}
	
	@RequestMapping("admin/reindexSubmissions")
	public String reindexSubmissions(Model model){
		if(securityService.hasRole(SharedRole.ROLE_ADMIN)){
		    solrIndexService.reindexAllPublishedSubmissions();
		}
		return viewPublicationConsole(model);
	}
	
	@RequestMapping("admin/regenerateRifcs")
	public String regenerateRifcsRecords(Model model){
		if(securityService.hasRole(SharedRole.ROLE_ADMIN)){
			int rifcsRecordsGenerated = rifcsService.regenerateRifcsForAllPublishedSubmissions();
			model.addAttribute("rifcsRecordsGenerated", rifcsRecordsGenerated);
		}
		return viewPublicationConsole(model);
	}
}
