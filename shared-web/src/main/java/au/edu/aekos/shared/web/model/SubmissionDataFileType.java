package au.edu.aekos.shared.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.aekos.shared.data.entity.SubmissionDataType;


/**
 * Consider merging this functionality into {@link SubmissionDataType} if it doesn't break Hibernate.
 */
public enum SubmissionDataFileType {
	DATA("Data"),
	RELATED_DOC("Associated/Supplementary Material"),
	SITE_FILE("Study Location File"),
	SPECIES_LIST("Species List");
	
	public static final String NO_TITLE = "(no title)";
	private static final Logger logger = LoggerFactory.getLogger(SubmissionDataFileType.class);
	private final String title;

	private SubmissionDataFileType(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * @param enumCode	The {@link SubmissionDataFileType} value to get the title for
	 * @return			The title if it exists, otherwise a default value
	 */
	public static String getTitleForCode(String enumCode) {
		try {
			return valueOf(enumCode).getTitle();
		} catch (IllegalArgumentException e) {
			logger.warn("Unable to find a title for code: " + enumCode);
			return SubmissionDataFileType.NO_TITLE;
		}
	}
}
