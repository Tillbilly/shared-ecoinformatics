package au.edu.aekos.shared.web.model;

import java.util.ArrayList;
import java.util.List;

public class AnswerReviewHistoryModel {
	
	private String questionText;
	private List<AnswerReviewHistoryRow> reviewHistoryRows = new ArrayList<AnswerReviewHistoryRow>();
	
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public List<AnswerReviewHistoryRow> getReviewHistoryRows() {
		return reviewHistoryRows;
	}
	public void setReviewHistoryRows(List<AnswerReviewHistoryRow> reviewHistoryRow) {
		this.reviewHistoryRows = reviewHistoryRow;
	}
}
