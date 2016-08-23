package au.edu.aekos.shared.service.submission;

import java.util.Comparator;

import au.edu.aekos.shared.data.entity.Submission;

public class SubmissionDateDescComparator implements Comparator<Submission> {

	@Override
	public int compare(Submission sub1, Submission sub2) {
		return sub2.getSubmissionDate().compareTo(sub1.getSubmissionDate());
	}

}
