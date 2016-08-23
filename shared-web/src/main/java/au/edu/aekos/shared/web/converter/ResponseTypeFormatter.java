package au.edu.aekos.shared.web.converter;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import au.edu.aekos.shared.questionnaire.jaxb.ResponseType;

public class ResponseTypeFormatter implements Formatter<ResponseType> {

	@Override
	public String print(ResponseType object, Locale locale) {
		return object.name();
	}

	@Override
	public ResponseType parse(String text, Locale locale) throws ParseException {
		return ResponseType.valueOf(text);
	}

}
