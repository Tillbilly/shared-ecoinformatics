package au.edu.aekos.shared.data.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class SubmissionDataTypeTest {

	/**
	 * Can we tell when something is a species file?
	 */
	@Test
	public void testIsSpeciesFile01() {
		assertTrue(SubmissionDataType.SPECIES_LIST.isSpeciesFile());
	}

	/**
	 * Can we tell when something is NOT a species file?
	 */
	@Test
	public void testIsSpeciesFile02() {
		List<SubmissionDataType> nonSpeciesFileTypes = getNonSpeciesListTypes();
		for (SubmissionDataType currType : nonSpeciesFileTypes) {
			assertFalse(currType.isSpeciesFile());
		}
	}

	/**
	 * Gets all the types are aren't a species list in a way the test newly added enums automatically 
	 * 
	 * @return
	 */
	private List<SubmissionDataType> getNonSpeciesListTypes() {
		List<SubmissionDataType> result = new LinkedList<SubmissionDataType>();
		for (SubmissionDataType currType : SubmissionDataType.values()) {
			if (currType.equals(SubmissionDataType.SPECIES_LIST)) {
				continue;
			}
			result.add(currType);
		}
		return result;
	}
}
