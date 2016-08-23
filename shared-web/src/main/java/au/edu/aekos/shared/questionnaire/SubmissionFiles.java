package au.edu.aekos.shared.questionnaire;

import java.util.ArrayList;
import java.util.List;

public class SubmissionFiles {

	private List<SubmissionFileModel> submissionFileList = new ArrayList<SubmissionFileModel>();

	public List<SubmissionFileModel> getSubmissionFileList() {
		return submissionFileList;
	}

	public void setSubmissionFileList(List<SubmissionFileModel> submissionFileList) {
		this.submissionFileList = submissionFileList;
	}

	public int size() {
		return submissionFileList.size();
	}

	public SubmissionFileModel get(int i) {
		return submissionFileList.get(i);
	}
}
