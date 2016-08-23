package au.edu.aekos.shared.web.controllers.integration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import au.edu.aekos.shared.system.SharedStatsGenerator;
import au.edu.aekos.shared.system.SharedStatsResult;

@Controller
public class StatisticsController {

	@Autowired
	private List<SharedStatsGenerator> statsGenerators;
	
	@RequestMapping("admin/statistics")
	public String viewStatistics(Model model){
		List<SharedStatsResult> stats = new ArrayList<SharedStatsResult>();
		for (SharedStatsGenerator currGenerator : statsGenerators) {
			stats.add(currGenerator.generateStat());
		}
		model.addAttribute("stats", stats);
		return "admin/statistics";
	}

	public void setStatsGenerators(List<SharedStatsGenerator> statsGenerators) {
		this.statsGenerators = statsGenerators;
	}
}
