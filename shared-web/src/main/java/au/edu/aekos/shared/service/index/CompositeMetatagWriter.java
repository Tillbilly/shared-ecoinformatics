package au.edu.aekos.shared.service.index;

import java.util.List;

public interface CompositeMetatagWriter {

	/**
	 * Some writers need configured metatags.  This method is used by the Validator to ensure 
	 * a new questionnaire config contains the metatags required by the Writer
	 * @return
	 */
	List<String> getRequiredMetatags();
}
