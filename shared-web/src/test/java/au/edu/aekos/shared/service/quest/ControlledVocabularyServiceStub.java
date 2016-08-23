package au.edu.aekos.shared.service.quest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.edu.aekos.shared.data.entity.CustomVocabulary;
import au.edu.aekos.shared.data.entity.Submission;
import au.edu.aekos.shared.data.entity.SubmissionAnswer;
import au.edu.aekos.shared.questionnaire.QuestionnairePage;
import au.edu.aekos.shared.web.json.dynatree.DynatreeNode;

public class ControlledVocabularyServiceStub implements ControlledVocabularyService {

	public static final String ORG_JAMES_COOK_UNIVERSITY = "james cook university";
	public static final String ORG_UNIVERSITY_OF_ADELAIDE = "university of adelaide";
	private static final Map<TraitDisplayKey, String> traitDisplayTexts = new HashMap<TraitDisplayKey, String>();
	static {
		traitDisplayTexts.put(new TraitDisplayKey("organisation", ORG_JAMES_COOK_UNIVERSITY), "James Cook University");
		traitDisplayTexts.put(new TraitDisplayKey("organisation", ORG_UNIVERSITY_OF_ADELAIDE), "University of Adelaide");
	}
	private static final Logger logger = Logger.getLogger(ControlledVocabularyServiceStub.class);

	public ControlledVocabularyServiceStub() {
	}

	@Override
	public List<TraitValue> getTraitValueList(String traitName, Boolean isCustom, boolean sortAlpha) {
		return null;
	}

	@Override
	public Map<String, List<TraitValue>> prepareControlledVocabListsForPage(QuestionnairePage page)
			throws ControlledVocabularyNotFoundException {
		return null;
	}

	@Override
	public String getTraitDisplayText(String traitName, String traitValue) {
		logger.info(String.format("Requesting trait display text for %s(%s).", traitValue, traitName));
		return traitDisplayTexts.get(new TraitDisplayKey(traitName, traitValue));
	}

	@Override
	public List<DynatreeNode> getDynatreeNodeVocabRepresentation(String traitName) {
		return null;
	}

	@Override
	public void loadCustomVocabularyFile(File file) throws IOException, SQLException { }

	@Override
	public void loadCustomVocabularyFromInputStream(InputStream is) throws IOException, SQLException { }

	@Override
	public CustomVocabulary buildCustomVocabFromFileLine(String line) {
		return new ControlledVocabularyServiceImpl().buildCustomVocabFromFileLine(line);
	}

	@Override
	public List<String> getListOfAvailableTraits() {
		return null;
	}

	@Override
	public List<String> getListOfAvailableCustomTraits() {
		return null;
	}

	@Override
	public boolean traitListContainsValue(String traitName, boolean isCustom, String value) {
		return false;
	}

	@Override
	public String getDisplayStringForTraitValue(String traitName, boolean isCustom, String value) {
		return null;
	}

	@Override
	public Map<Submission, List<SubmissionAnswer>> getSubmissionAnswersContainingVocabValueAnswer(String vocabValue, String vocabularyName) {
		return null;
	}

	@Override
	public TraitValue getCustomTraitValue(String vocabularyName, String vocabValue) {
		return null;
	}

	@Override
	public TraitValue getTraitValue(String vocabularyName, String vocabValue) {
		return null;
	}

	@Override
	public void removeCustomVocabValue(String vocabularyName, String vocabValue) { }

	@Override
	public void addCustomVocabValue(String vocabularyName, TraitValue value) { }

	@Override
	public void addSuggestedValuesToVocab(String vocabularyName, List<TraitValue> traitList) { }

	@Override
	public boolean checkCustomVocabValueExists(String vocabularyName, TraitValue value) {
		return false;
	}

	@Override
	public void updateCustomVocabValue(String vocabularyName, String originalValue, TraitValue value) { }

	@Override
	public void streamCustomVocabulariesToOutputStream(OutputStream outputStream) { }

	@Override
	public void addSuggestedAekosVocabValue(String vocabularyName, TraitValue value) { }

	@Override
	public List<TraitValue> getTraitValueList(String traitName, Boolean isCustom) {
		return null;
	}
	
	private static class TraitDisplayKey {
		private final String traitName;
		private final String traitValue;
		public TraitDisplayKey(String traitName, String traitValue) {
			this.traitName = traitName;
			this.traitValue = traitValue;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((traitName == null) ? 0 : traitName.hashCode());
			result = prime * result + ((traitValue == null) ? 0 : traitValue.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TraitDisplayKey other = (TraitDisplayKey) obj;
			if (traitName == null) {
				if (other.traitName != null)
					return false;
			} else if (!traitName.equals(other.traitName))
				return false;
			if (traitValue == null) {
				if (other.traitValue != null)
					return false;
			} else if (!traitValue.equals(other.traitValue))
				return false;
			return true;
		}
	}
}
