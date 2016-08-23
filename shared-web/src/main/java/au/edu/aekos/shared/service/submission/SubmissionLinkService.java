package au.edu.aekos.shared.service.submission;

import au.edu.aekos.shared.data.entity.SubmissionLinkType;

public interface SubmissionLinkService {
	void linkSubmissions(Long sourceSubmissionId, Long targetSubmissionId, SubmissionLinkType linkType, String description);
	void unlinkSubmissions(Long sourceSubmissionId, Long targetSubmissionId);
}
