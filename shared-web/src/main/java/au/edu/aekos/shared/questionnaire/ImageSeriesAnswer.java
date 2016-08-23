package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.List;

public class ImageSeriesAnswer {

	private String questionId;
	
	private List<ImageAnswer> imageAnswerList = new ArrayList<ImageAnswer>();

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public List<ImageAnswer> getImageAnswerList() {
		return imageAnswerList;
	}

	public void setImageAnswerList(List<ImageAnswer> imageAnswerList) {
		this.imageAnswerList = imageAnswerList;
	}
	
	
	
}
