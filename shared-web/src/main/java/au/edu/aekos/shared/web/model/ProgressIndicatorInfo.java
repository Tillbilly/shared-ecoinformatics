package au.edu.aekos.shared.web.model;

import au.edu.aekos.shared.questionnaire.DisplayQuestionnaire;

public class ProgressIndicatorInfo {
	private final int currentPageNum;
	private final int totalPageCount;

	ProgressIndicatorInfo(int currentPage, int totalPages) {
		this.currentPageNum = currentPage;
		this.totalPageCount = totalPages;
	}

	public int getCurrentPageNum() {
		return currentPageNum;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public static ProgressIndicatorInfo newInstance(DisplayQuestionnaire source) {
		return new ProgressIndicatorInfo(source.getCurrentPageNumber(), source.getPages().size());
	}
}
