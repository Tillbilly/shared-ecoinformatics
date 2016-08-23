package au.org.aekos.shared.api.model.dataset;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.solr.common.SolrDocument;

public class DateGridCellMapper extends GridCellMapper {
	private static final String DATE_FORMAT = "dd/MM/yyyy";
	
	public DateGridCellMapper(String label, String indexField) {
		super(label, indexField);
	}

	@Override
	public SharedGridField map(SolrDocument doc) {
		Date dateValue = (Date) doc.getFirstValue(indexField);
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(dateValue);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date roundedDownDate = c.getTime();
		String roundedDownDateString = new SimpleDateFormat(DATE_FORMAT).format(roundedDownDate);
		return new SharedGridField(label, roundedDownDateString);
	}
}