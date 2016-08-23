package au.edu.aekos.shared.questionnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PageAnswersModel {
	
	private Integer pageNumber;

	private Map<String,String> selectedReusableGroupMap = new HashMap<String,String>();

	private Map<String, Answer> answers = new HashMap<String,Answer>();
	
	private Map<String, ImageAnswer> imageAnswerMap = new HashMap<String, ImageAnswer>();
    
	private Map<String, ImageSeriesAnswer> imageSeriesAnswerMap = new HashMap<String, ImageSeriesAnswer>();
	
	public Map<String, Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Map<String, Answer> answers) {
		this.answers = answers;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Map<String, ImageAnswer> getImageAnswerMap() {
		return imageAnswerMap;
	}

	public void setImageAnswerMap(Map<String, ImageAnswer> imageAnswerMap) {
		this.imageAnswerMap = imageAnswerMap;
	}

	public Map<String, ImageSeriesAnswer> getImageSeriesAnswerMap() {
		return imageSeriesAnswerMap;
	}

	public void setImageSeriesAnswerMap(
			Map<String, ImageSeriesAnswer> imageSeriesAnswerMap) {
		this.imageSeriesAnswerMap = imageSeriesAnswerMap;
	}
	
	public Map<String, String> getSelectedReusableGroupMap() {
		return selectedReusableGroupMap;
	}

	public void setSelectedReusableGroupMap(
			Map<String, String> selectedReusableGroupMap) {
		this.selectedReusableGroupMap = selectedReusableGroupMap;
	}

	@Override
	public String toString() {
		TreeMap<String, Answer> sortedAnswers = new TreeMap<String, Answer>(answers);
		return "pageNumber=" + pageNumber + ", answers=" + sortedAnswers.toString();
	}
}
