package au.edu.aekos.shared.data.entity;

import java.util.Arrays;
import java.util.List;

public enum SubmissionLinkType {
	HAS_NEW_VERSION("has a newer version", "is a new version of"),
	IS_NEW_VERSION_OF("is a new version of", "has a newer version"),
	LINKED("is linked to", "is linked to"),
	RELATED("is related to", "is related to");

	private final String normalTitle;
	private final String inverseTitle;
	
	private SubmissionLinkType(String normalTitle, String inverseTitle) {
		this.normalTitle = normalTitle;
		this.inverseTitle = inverseTitle;
	}
	
	public String getNormalTitle() {
		return normalTitle;
	}

	public String getInverseTitle() {
		return inverseTitle;
	}

	/**
	 * @return	link types that are allowed to be created (as some types 
	 * may be supported for legacy reasons but not allowed for new links)
	 */
	public static List<SubmissionLinkType> getCreatableLinkTypes() {
		// If the rules get more complex, we can move to a Spring approach
		return Arrays.asList(IS_NEW_VERSION_OF, HAS_NEW_VERSION, RELATED);
	}
}
