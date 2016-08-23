package au.edu.aekos.shared.web.converter;


import org.springframework.core.convert.converter.Converter;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class ResponseTypeToStringConverter implements Converter<ResponseType,String> {
	@Override
	public String convert(ResponseType source) {
		return source.name();
	}
	
}