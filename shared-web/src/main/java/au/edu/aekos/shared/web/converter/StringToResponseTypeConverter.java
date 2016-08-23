package au.edu.aekos.shared.web.converter;


import org.springframework.core.convert.converter.Converter;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class StringToResponseTypeConverter implements Converter<String,ResponseType> {

	@Override
	public ResponseType convert(String source) {
		return ResponseType.valueOf(ResponseType.class, source);
	}
	
}